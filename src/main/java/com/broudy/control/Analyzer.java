package com.broudy.control;

import com.broudy.control.CorrelationArrayGetter.LeftCorrelationArraysGetter;
import com.broudy.control.CorrelationArrayGetter.RightCorrelationArraysGetter;
import com.broudy.control.OccurrencesAdder.LeftOccurrencesAdder;
import com.broudy.control.OccurrencesAdder.RightOccurrencesAdder;
import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.AnalysisResults;
import com.broudy.entity.AutocorrelationArrays;
import com.broudy.entity.Occurrences;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.ProtonavProbabilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.concurrent.Task;

/**
 * This class controls the analysis flow.
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Analyzer extends Task<AnalysisResults> {

  public final AnalysisInformation analysisInformation;
  public final AnalysisParameters analysisParameters;
  public final List<String> possiblePatterns;
  public final List<ProtonavPair> protonavPairs;
  public final HashMap<String, Protonav> patternToProtonavMapping;
  public final HashMap<String, AutocorrelationArrays> patternToCorrelationArraysMapping;
  public final HashMap<String, Occurrences> patternToOccurrencesMapping;

  private long progress, totalProgress;


  public double[] probabilitiesOfSinglesOnLeft;
  public double[] probabilitiesOfSinglesOnRight;
  public HashMap<String, Double> probabilitiesByPairsOnLeft;
  public HashMap<String, Double> probabilitiesByPairsOnRight;
  public double totalNumberOfNucleotidesOnLeft;
  public double totalNumberOfNucleotidesOnRight;


  public Analyzer(AnalysisInformation analysisInformation) {
    this.analysisInformation = analysisInformation;
    this.analysisParameters = analysisInformation.getAnalysisParameters();
    this.possiblePatterns = new ArrayList<>();
    this.protonavPairs = new ArrayList<>();
    this.patternToProtonavMapping = new HashMap<>();
    this.patternToCorrelationArraysMapping = new HashMap<>();
    this.patternToOccurrencesMapping = new HashMap<>();
    this.progress = 0;
  }

  @Override
  protected AnalysisResults call() throws Exception {

    prepareProtonavPairs();

    final int windowSize = analysisParameters.getWindowSize();
    final int padding = analysisParameters.getPadding();

    totalProgress = padding * 2 + analysisInformation.getLeftSequence().getSequence().length()
        + analysisInformation.getRightSequence().getSequence().length();
    updateProgress(progress++, totalProgress);

    String sequenceString = analysisInformation.getLeftSequence().getSequence();
    int from = sequenceString.length() - padding - windowSize;
    String sequenceUnderTest = sequenceString.substring(from);

    updateMessage("Computing correlations...");
    computeAutocorrelations(sequenceUnderTest, new LeftCorrelationArraysGetter());

    sequenceString = analysisInformation.getRightSequence().getSequence();
    sequenceUnderTest = sequenceString.substring(0, padding + windowSize);

    computeAutocorrelations(sequenceUnderTest, new RightCorrelationArraysGetter());

    updateMessage("Calculating smoothed correlations...");
    smoothCorrelationArrays(windowSize);

    updateMessage("Counting occurrences...");
    countOccurrences(analysisInformation.getLeftSequence().getSequence(),
        new LeftOccurrencesAdder());
    countOccurrences(analysisInformation.getRightSequence().getSequence(),
        new RightOccurrencesAdder());

    return new AnalysisResults(analysisInformation, protonavPairs, getFilteredProtonavPairList());
  }

  public void countOccurrences(String sequence, OccurrencesAdder occurrencesAdder) {

    final int min = analysisParameters.getMinPatternLength();
    final int max = analysisParameters.getMaxPatternLength();
    final int length = sequence.length() - max;
    for (int i = 0; i < length; i++) {
      for (int patternLength = min; patternLength <= max; patternLength++) {
        final String pattern = sequence.substring(i, i + patternLength);
        occurrencesAdder.increaseCount(patternToOccurrencesMapping.get(pattern));
      }
      updateProgress(progress++, totalProgress);
    }
  }

  private List<ProtonavPair> getFilteredProtonavPairList() {

    updateMessage("Filtering protonav pairs...");

    final List<ProtonavPair> protonavsFilteredByProbabilisticOccurrences = new ArrayList<>();

    final double totalNumberOfPairsOnLeft = totalNumberOfNucleotidesOnLeft - 1;
    final double totalNumberOfPairsOnRight = totalNumberOfNucleotidesOnRight - 1;

    long leftCount, rightCount;
    double probabilityOnLeft, probabilityOnRight;
    ProtonavProbabilities extractedProtonavProbabilities, palimentaryProtonavProbabilities;
    ProtonavProbabilities extractedProtonavResultingProbabilities, palimentaryProtonavResultingProbabilities;
    Protonav extractedProtonav, palimentaryProtonav;
    for (ProtonavPair protonavPair : protonavPairs) {
      extractedProtonav = protonavPair.getExtractedProtonav();
      palimentaryProtonav = protonavPair.getPalimentaryProtonav();
      extractedProtonavProbabilities = extractedProtonav.getProbabilities();
      palimentaryProtonavProbabilities = palimentaryProtonav.getProbabilities();

      leftCount = extractedProtonav.getOccurrences().getLeftCount();
      rightCount = extractedProtonav.getOccurrences().getRightCount();
      probabilityOnLeft =
          (leftCount / totalNumberOfNucleotidesOnLeft) / extractedProtonavProbabilities
              .getLeftProbabilityBySingles();
      probabilityOnRight =
          (rightCount / totalNumberOfNucleotidesOnRight) / extractedProtonavProbabilities
              .getRightProbabilityBySingles();

      if ((int) probabilityOnLeft != (int) probabilityOnRight
          && probabilityOnLeft > probabilityOnRight) {
        extractedProtonavResultingProbabilities = new ProtonavProbabilities(probabilityOnLeft,
            probabilityOnRight);
        extractedProtonavResultingProbabilities.setLeftProbabilityByPairs(
            (leftCount / totalNumberOfPairsOnLeft) / extractedProtonavProbabilities
                .getLeftProbabilityByPairs());
        extractedProtonavResultingProbabilities.setRightProbabilityByPairs(
            (rightCount / totalNumberOfPairsOnRight) / extractedProtonavProbabilities
                .getRightProbabilityByPairs());
        extractedProtonav.setResultingProbabilities(extractedProtonavResultingProbabilities);

        leftCount = palimentaryProtonav.getOccurrences().getLeftCount();
        rightCount = palimentaryProtonav.getOccurrences().getRightCount();
        probabilityOnLeft =
            (leftCount / totalNumberOfNucleotidesOnLeft) / palimentaryProtonavProbabilities
                .getLeftProbabilityBySingles();
        probabilityOnRight =
            (rightCount / totalNumberOfNucleotidesOnRight) / palimentaryProtonavProbabilities
                .getRightProbabilityBySingles();

        if ((int) probabilityOnLeft != (int) probabilityOnRight
            && probabilityOnRight > probabilityOnLeft) {
          palimentaryProtonavResultingProbabilities = new ProtonavProbabilities(probabilityOnLeft,
              probabilityOnRight);
          palimentaryProtonavResultingProbabilities.setLeftProbabilityByPairs(
              (leftCount / totalNumberOfPairsOnLeft) / palimentaryProtonavProbabilities
                  .getLeftProbabilityByPairs());
          palimentaryProtonavResultingProbabilities.setRightProbabilityByPairs(
              (rightCount / totalNumberOfPairsOnRight) / palimentaryProtonavProbabilities
                  .getRightProbabilityByPairs());
          palimentaryProtonav.setResultingProbabilities(palimentaryProtonavResultingProbabilities);
          protonavsFilteredByProbabilisticOccurrences.add(protonavPair);
        }
      }
    }
    updateMessage("Finished :)");
    return protonavsFilteredByProbabilisticOccurrences;
  }

  /**
   * Fills in the smoothedCorrelationsArrays based by averaging every 3 consecutive cells into the
   * smoothed one: smoothedArray[i] = (old[i-1] + old[i] + old[i+1]) / 3
   */
  public void smoothCorrelationArrays(int windowSize) {
    int[] leftCorrelationArray, rightCorrelationArray;
    double[] leftSmoothedCorrelationArray, rightSmoothedCorrelationArray;
    final int len = windowSize - 1;
    for (AutocorrelationArrays autocorrelationArrays : patternToCorrelationArraysMapping.values()) {
      leftCorrelationArray = autocorrelationArrays.getCorrelationsOnLeft();
      rightCorrelationArray = autocorrelationArrays.getCorrelationsOnRight();
      leftSmoothedCorrelationArray = autocorrelationArrays.getSmoothedCorrelationsOnLeft();
      rightSmoothedCorrelationArray = autocorrelationArrays.getSmoothedCorrelationsOnRight();
      for (int index = 2; index < len; index++) {
        leftSmoothedCorrelationArray[index - 1] =
            (double) (leftCorrelationArray[index - 1] + leftCorrelationArray[index]
                + leftCorrelationArray[index + 1]) / 3;
        rightSmoothedCorrelationArray[index - 1] =
            (double) (rightCorrelationArray[index - 1] + rightCorrelationArray[index]
                + rightCorrelationArray[index + 1]) / 3;
      }
    }
  }

  public void computeAutocorrelations(String sequenceUnderTest,
      CorrelationArrayGetter correlationsGetter) {

    final int windowSize = analysisParameters.getWindowSize();
    final int padding = analysisParameters.getPadding();
    final int min = analysisParameters.getMinPatternLength();
    final int max = analysisParameters.getMaxPatternLength();

    final PatternCounter patternCounter = new PatternCounter();

    for (int i = 0; i < padding; i++) {
      final String searchArea = sequenceUnderTest.substring(i, i + windowSize);
      for (int length = min; length <= max; length++) {
        final String pattern = sequenceUnderTest.substring(i, i + length);
        patternCounter.updateCorrelationArray(pattern, searchArea,
            correlationsGetter.getCorrelationArray(patternToCorrelationArraysMapping.get(pattern)));
      }
      updateProgress(progress++, totalProgress);
    }

  }

  public void prepareProtonavPairs() {
    updateMessage("Preparing possible protonav pairs...");
    final int min = analysisParameters.getMinPatternLength();
    final int max = analysisParameters.getMaxPatternLength();
    for (int len = min; len <= max; len++) {
      allLexicographic("ACGT", len);
    }

    possiblePatterns.sort((o1, o2) -> {
      if (o1.length() != o2.length()) {
        return o1.length() - o2.length(); //overflow impossible since lengths are non-negative
      }
      return o1.compareTo(o2);
    });

    //
    probabilitiesOfSinglesOnLeft = analysisInformation.getLeftSequence()
        .getNucleotideProbabilities().getProbabilitiesOfSingles();
    probabilitiesOfSinglesOnRight = analysisInformation.getRightSequence()
        .getNucleotideProbabilities().getProbabilitiesOfSingles();
    totalNumberOfNucleotidesOnLeft = analysisInformation.getLeftSequence()
        .getNucleotideProbabilities().getTotalNumberOfNucleotides();
    totalNumberOfNucleotidesOnRight = analysisInformation.getRightSequence()
        .getNucleotideProbabilities().getTotalNumberOfNucleotides();
    probabilitiesByPairsOnLeft = analysisInformation.getLeftSequence().getNucleotideProbabilities()
        .getProbabilitiesOfPairs();
    probabilitiesByPairsOnRight = analysisInformation.getRightSequence()
        .getNucleotideProbabilities().getProbabilitiesOfPairs();

    final int windowSize = analysisParameters.getWindowSize();

    int ID = 1;
    for (String pattern : possiblePatterns) {
      if (!patternToProtonavMapping.containsKey(pattern)) {
        protonavPairs.add(generateProtonavPair(pattern, ID++, windowSize));
      }
    }

  }

  // HELPER METHODS:

  public ProtonavPair generateProtonavPair(String extractedPattern, int ID, int windowSize) {

    final StringBuilder palimentaryBuilder = new StringBuilder();
    final char[] patternCharArray = extractedPattern.toCharArray();

    double protonavLeftProbabilityBySingles = 1;
    double protonavRightProbabilityBySingles = 1;
    double palimentaryLeftProbabilityBySingles = 1;
    double palimentaryRightProbabilityBySingles = 1;

    char palimentaryNucleotide;

    for (char nucleotide : patternCharArray) {
      palimentaryNucleotide = getComplementaryNucleotide(nucleotide);
      palimentaryBuilder.append(palimentaryNucleotide);

      protonavLeftProbabilityBySingles *= probabilitiesOfSinglesOnLeft[nucleotide - 'A'];
      protonavRightProbabilityBySingles *= probabilitiesOfSinglesOnRight[nucleotide - 'A'];
      palimentaryLeftProbabilityBySingles *= probabilitiesOfSinglesOnLeft[palimentaryNucleotide
          - 'A'];
      palimentaryRightProbabilityBySingles *= probabilitiesOfSinglesOnRight[palimentaryNucleotide
          - 'A'];
    }

    final ProtonavProbabilities extractedProtonavProbabilities = new ProtonavProbabilities(
        protonavLeftProbabilityBySingles, protonavRightProbabilityBySingles);
    final String palimentaryPattern = palimentaryBuilder.reverse().toString();
    final ProtonavProbabilities palimentaryProbabilities = new ProtonavProbabilities(
        palimentaryLeftProbabilityBySingles, palimentaryRightProbabilityBySingles);

    if (extractedPattern.length() > 1) {
      setProbabilitiesByPairs(extractedPattern, extractedProtonavProbabilities);
      setProbabilitiesByPairs(palimentaryPattern, palimentaryProbabilities);
    }

    final AutocorrelationArrays extractedProtonavAutocorrelationArrays = new AutocorrelationArrays(windowSize);
    final AutocorrelationArrays palimentaryProtonavAutocorrelationArrays = new AutocorrelationArrays(
        windowSize);

    final Protonav extractedProtonav = new Protonav(extractedPattern,
        extractedProtonavProbabilities, extractedProtonavAutocorrelationArrays);
    final Protonav palimentaryProtonav = new Protonav(palimentaryPattern, palimentaryProbabilities,
        palimentaryProtonavAutocorrelationArrays);

    patternToProtonavMapping.put(extractedPattern, extractedProtonav);
    patternToProtonavMapping.put(palimentaryPattern, palimentaryProtonav);
    patternToOccurrencesMapping.put(extractedPattern, extractedProtonav.getOccurrences());
    patternToOccurrencesMapping.put(palimentaryPattern, palimentaryProtonav.getOccurrences());
    patternToCorrelationArraysMapping.put(extractedPattern, extractedProtonavAutocorrelationArrays);
    patternToCorrelationArraysMapping.put(palimentaryPattern,
        palimentaryProtonavAutocorrelationArrays);

    return new ProtonavPair(ID, extractedProtonav, palimentaryProtonav);
  }

  /**
   * Returns the complementary nucleotide.
   *
   * @return A->T, T->A, C->G, G->C
   */
  public char getComplementaryNucleotide(char nucleotide) {
    switch (nucleotide) {
      case 'A':
        nucleotide = 'T';
        break;
      case 'T':
        nucleotide = 'A';
        break;
      case 'C':
        nucleotide = 'G';
        break;
      case 'G':
        nucleotide = 'C';
        break;
    }
    return nucleotide;
  }

  private void setProbabilitiesByPairs(String pattern, ProtonavProbabilities probabilities) {

    double probabilityOnLeft = probabilitiesByPairsOnLeft
        .getOrDefault(pattern.substring(0, 2), (double) 1);
    double probabilityOnRight = probabilitiesByPairsOnRight
        .getOrDefault(pattern.substring(0, 2), (double) 1);

    double probabilityOfSingleOnLeft, probabilityOfSingleOnRight;
    final char[] patternCharArray = pattern.toCharArray();
    final int len = patternCharArray.length - 1;

    int probabilityIndex;
    String pair;

    for (int index = 1; index < len; index++) {
      pair = pattern.substring(index, index + 2);
      probabilityIndex = patternCharArray[index] - 'A';
      probabilityOfSingleOnLeft = probabilitiesOfSinglesOnLeft[probabilityIndex];
      probabilityOnLeft *=
          (probabilitiesByPairsOnLeft.getOrDefault(pair, (double) 1) + probabilityOfSingleOnLeft)
              / probabilityOfSingleOnLeft;
      probabilityOfSingleOnRight = probabilitiesOfSinglesOnRight[probabilityIndex];
      probabilityOnRight *=
          (probabilitiesByPairsOnRight.getOrDefault(pair, (double) 1) + probabilityOfSingleOnRight)
              / probabilityOfSingleOnRight;
    }

    probabilities.setLeftProbabilityByPairs(probabilityOnLeft);
    probabilities.setRightProbabilityByPairs(probabilityOnRight);
  }


  /**
   * Recursively store all permutations one by one.
   */
  public void allLexicographicRecur(String str, char[] data, int last, int index) {
    int length = str.length();

    // One by one fix all characters at the given index
    // and recur for the subsequent indexes
    for (int i = 0; i < length; i++) {

      // Fix the ith character at index and if
      // this is not the last index then
      // recursively call for higher indexes
      data[index] = str.charAt(i);

      // If this is the last index then print
      // the string stored in data[]
      if (index == last) {
        possiblePatterns.add(new String(data));
      } else {
        allLexicographicRecur(str, data, last, index + 1);
      }
    }
  }

  /**
   * Sort string and allocate memory for allLexicographicRecur().
   *
   * @param str is the string from which to generate patterns.
   * @param length is the length of each pattern.
   */
  public void allLexicographic(String str, int length) {

    // Create a temp array that will be used by
    // allLexicographicRecur()
    char[] data = new char[length];
    char[] temp = str.toCharArray();

    // Sort the input string so that we get all
    // output strings in lexicographically sorted order
    Arrays.sort(temp);
    str = new String(temp);

    // Now print all permutations
    allLexicographicRecur(str, data, length - 1, 0);
  }

}
