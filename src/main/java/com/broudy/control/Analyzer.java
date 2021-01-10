package com.broudy.control;

import com.broudy.control.CorrelationArrayGetter.LeftCorrelationArraysGetter;
import com.broudy.control.CorrelationArrayGetter.RightCorrelationArraysGetter;
import com.broudy.control.ResultsFilter.MoreOnLeftFilter;
import com.broudy.control.ResultsFilter.MoreOnRightFilter;
import com.broudy.entity.CorrelationArrays;
import com.broudy.entity.ParsedSequence;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.ProtonavProbabilities;
import com.broudy.entity.Results;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.concurrent.Task;

/**
 * TODO provide a summary to Analyzer class!!!!!
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Analyzer extends Task<ParsedSequence> {

  private List<String> possiblePatterns = new ArrayList<>();
  private final HashMap<String, Integer> protonavPairsIDs = new HashMap<>();
  private final ParsedSequence parsedSequence;
  private double[] probabilitiesOfSinglesOnLeft;
  private double[] probabilitiesOfSinglesOnRight;
  private HashMap<String, Double> probabilitiesByPairsOnLeft;
  private HashMap<String, Double> probabilitiesByPairsOnRight;
  private double totalNumberOfNucleotidesOnLeft;
  private double totalNumberOfNucleotidesOnRight;
  private double totalNumberOfPairsOnLeft;
  private double totalNumberOfPairsOnRight;
  private long PAIRS_COUNT;
  private HashSet<String> generatedPatterns = new HashSet<>();


  public Analyzer(ParsedSequence parsedSequence) {
    this.parsedSequence = parsedSequence;
    this.PAIRS_COUNT = 0;
  }

  // The main function that recursively prints
  // all repeated permutations of the given string.
  // It uses data[] to store all permutations one by one
  private void allLexicographicRecur(String str, char[] data, int last, int index) {
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

  // This function sorts input string, allocate memory
  // for data(needed for allLexicographicRecur()) and calls
  // allLexicographicRecur() for printing all permutations
  private void allLexicographic(String str, int length) {

    // Create a temp array that will be used by
    // allLexicographicRecur()
    char[] data = new char[length];
    char[] temp = str.toCharArray();

    // Sort the input string so that we get all
    // output strings in lexicographically sorted order
    Arrays.sort(temp);
    str = new String(temp);

    // Now print all permutaions
    allLexicographicRecur(str, data, length - 1, 0);
  }

  @Override
  protected ParsedSequence call() throws Exception {

    ProtonavPair.setPairsCount(0);

    // return analyzeOccurrenceProbabilities();

    return analyzeCorrelations();


  }

  private void generateProtonavPairIds(int min, int max) {

    int ID = 1;

    final HashSet<String> done = new HashSet<>();

    for (int len = min; len <= max; len++) {
      allLexicographic("ACGT", len);
    }

    Comparator<String> lenThenAlphaComparator = (o1, o2) -> {
      if (o1.length() != o2.length()) {
        return o1.length() - o2.length(); //overflow impossible since lengths are non-negative
      }
      return o1.compareTo(o2);
    };

    possiblePatterns.sort(lenThenAlphaComparator);

    String palimentary;
    for (String pattern : possiblePatterns) {
      if (!done.contains(pattern)) {
        palimentary = getPalindromicComplementaryPattern(pattern);
        done.add(pattern);
        done.add(palimentary);
        protonavPairsIDs.put(pattern, ID);
        protonavPairsIDs.put(palimentary, ID++);
      }
    }
  }

  private ParsedSequence analyzeCorrelations() {

    final int FILTER_SIZE = 100;
    final int MIN_LEN = 1;
    final int MAX_LEN = 3;

    generateProtonavPairIds(MIN_LEN, MAX_LEN);

    final int WINDOW = 100000;

    HashMap<String, CorrelationArrays> patternsCorrelationArrays = new HashMap<>();

    String sequenceString = parsedSequence.getLeftSequence().getSequence();
    int from = sequenceString.length() - WINDOW - FILTER_SIZE;
    String sequenceUnderTest = sequenceString.substring(from);

    final List<ProtonavPair> protonavPairsCorrelations = parsedSequence
        .getProtonavPairsCorrelations();

    parsedSequence.getLeftSequence().getProtonavs().addAll(
        getCorrelations(sequenceUnderTest, WINDOW, FILTER_SIZE, MIN_LEN, MAX_LEN,
            patternsCorrelationArrays, new LeftCorrelationArraysGetter()));
    protonavPairsCorrelations.addAll(parsedSequence.getLeftSequence().getProtonavs());

    sequenceString = parsedSequence.getRightSequence().getSequence();
    sequenceUnderTest = sequenceString.substring(0, WINDOW + FILTER_SIZE);

    parsedSequence.getRightSequence().getProtonavs().addAll(
        getCorrelations(sequenceUnderTest, WINDOW, FILTER_SIZE, MIN_LEN, MAX_LEN,
            patternsCorrelationArrays, new RightCorrelationArraysGetter()));

    protonavPairsCorrelations.addAll(parsedSequence.getRightSequence().getProtonavs());

    // Update smoothed correlations
    int[] leftCorrelationArray, rightCorrelationArray;
    double[] leftSmoothedCorrelationArray, rightSmoothedCorrelationArray;
    final int len = FILTER_SIZE - 1;
    for (CorrelationArrays correlationArrays : patternsCorrelationArrays.values()) {
      leftCorrelationArray = correlationArrays.getCorrelationsOnLeft();
      rightCorrelationArray = correlationArrays.getCorrelationsOnRight();
      leftSmoothedCorrelationArray = correlationArrays.getSmoothedCorrelationsOnLeft();
      rightSmoothedCorrelationArray = correlationArrays.getSmoothedCorrelationsOnRight();
      for (int index = 2; index < len; index++) {
        leftSmoothedCorrelationArray[index] =
            (double) (leftCorrelationArray[index - 1] + leftCorrelationArray[index]
                + leftCorrelationArray[index + 1]) / 3;
        rightSmoothedCorrelationArray[index] =
            (double) (rightCorrelationArray[index - 1] + rightCorrelationArray[index]
                + rightCorrelationArray[index + 1]) / 3;
      }

    }

    return parsedSequence;
  }

  private List<ProtonavPair> getCorrelations(String sequenceUnderTest, int WINDOW, int FILTER_SIZE,
      int MIN_LEN, int MAX_LEN, HashMap<String, CorrelationArrays> patternsCorrelationArrays,
      CorrelationArrayGetter correlationsGetter) {
    final List<ProtonavPair> protonavPairs = new ArrayList<>();

    final PatternCounter patternCounter = new PatternCounter();

    int progress = 0;
    final int totalProgress = WINDOW * 2;
    updateProgress(progress++, totalProgress);

    CorrelationArrays correlationArrays;
    for (int i = 0; i < WINDOW; i++) {

      final String searchArea = sequenceUnderTest.substring(i, i + FILTER_SIZE);

      for (int len = MIN_LEN; len <= MAX_LEN; len++) {

        final String pattern = sequenceUnderTest.substring(i, i + len);

        if (patternsCorrelationArrays.containsKey(pattern)) {
          correlationArrays = patternsCorrelationArrays.get(pattern);
        } else {
          final Protonav patternProtonav = new Protonav(pattern);
          correlationArrays = patternProtonav.getCorrelationArrays();
          patternsCorrelationArrays.put(pattern, correlationArrays);

          final String palimentary = getPalindromicComplementaryPattern(pattern);
          final Protonav palimentaryProtonav = new Protonav(palimentary);
          patternsCorrelationArrays.put(palimentary, palimentaryProtonav.getCorrelationArrays());

          protonavPairs.add(new ProtonavPair(protonavPairsIDs.get(pattern), patternProtonav,
              palimentaryProtonav));
        }
        patternCounter.updateCorrelationArray(pattern, searchArea,
            correlationsGetter.getCorrelationArray(correlationArrays));
        updateProgress(progress++, totalProgress);
      }
    }

    return protonavPairs;
  }

  private String getPalindromicComplementaryPattern(String pattern) {
    final StringBuilder palimentary = new StringBuilder();
    final char[] patternCharArray = pattern.toCharArray();
    for (char nucleotide : patternCharArray) {
      palimentary.append(getPalindromicComplementaryNucleotide(nucleotide));
    }
    return palimentary.reverse().toString();
  }

  private ParsedSequence analyzeOccurrenceProbabilities() {

    updateMessage("Setting up...");
    probabilitiesOfSinglesOnLeft = parsedSequence.getLeftSequence().getNucleotideProbabilities()
        .getProbabilitiesOfSingles();
    probabilitiesOfSinglesOnRight = parsedSequence.getRightSequence().getNucleotideProbabilities()
        .getProbabilitiesOfSingles();
    probabilitiesByPairsOnLeft = parsedSequence.getLeftSequence().getNucleotideProbabilities()
        .getProbabilitiesOfPairs();
    probabilitiesByPairsOnRight = parsedSequence.getRightSequence().getNucleotideProbabilities()
        .getProbabilitiesOfPairs();
    totalNumberOfNucleotidesOnLeft = parsedSequence.getLeftSequence().getNucleotideProbabilities()
        .getTotalNumberOfNucleotides();
    totalNumberOfNucleotidesOnRight = parsedSequence.getRightSequence().getNucleotideProbabilities()
        .getTotalNumberOfNucleotides();
    totalNumberOfPairsOnLeft = totalNumberOfNucleotidesOnLeft - 1;
    totalNumberOfPairsOnRight = totalNumberOfNucleotidesOnRight - 1;

    updateMessage("Extracting possible patterns from left sequence...");
    //  Get patterns from left sequence
    List<String> simplePatterns = extractSimplePatterns(
        parsedSequence.getLeftSequence().getSequence(), parsedSequence.getMinPatternLength(),
        parsedSequence.getMaxPatternLength());

    updateMessage("Generating protonav pairs...");
    // Generate protonav pairs
    List<ProtonavPair> protonavPairs = generateProtonavPairs(simplePatterns);

    updateMessage("Counting generated protonavs in left & right sequences...");
    ResultsFilter moreOnLeftFilter = new MoreOnLeftFilter();
    ResultsFilter moreOnRightFilter = new MoreOnRightFilter();
    //  Count
    protonavPairs = countOccurrencesAndFilterProtonavs(protonavPairs, moreOnLeftFilter,
        moreOnRightFilter);

    parsedSequence.getLeftSequence().getProtonavs().addAll(protonavPairs);

    updateMessage("Extracting possible patterns from right sequence...");
    //  Get patterns from left sequence
    simplePatterns = extractSimplePatterns(parsedSequence.getRightSequence().getSequence(),
        parsedSequence.getMinPatternLength(), parsedSequence.getMaxPatternLength());

    updateMessage("Generating protonav pairs...");
    // Generate protonav pairs
    protonavPairs = generateProtonavPairs(simplePatterns);

    updateMessage("Counting generated protonavs in left & right sequences...");
    //  Count on left side
    protonavPairs = countOccurrencesAndFilterProtonavs(protonavPairs, moreOnRightFilter,
        moreOnLeftFilter);
    parsedSequence.getRightSequence().getProtonavs().addAll(protonavPairs);

    updateMessage("Done");
    return parsedSequence;
  }

  private List<ProtonavPair> countOccurrencesAndFilterProtonavs(List<ProtonavPair> protonavPairs,
      ResultsFilter protonavFilter, ResultsFilter palimentaryFilter) {

    long progress = 0;
    long totalProgress = protonavPairs.size() * 4;

    final String leftSequence = parsedSequence.getLeftSequence().getSequence();
    final String rightSequence = parsedSequence.getRightSequence().getSequence();
    PatternCounter patternCounter = new PatternCounter();

    List<ProtonavPair> filteredPairs = new ArrayList<>();

    long leftCount, rightCount;
    double probabilityOnLeft, probabilityOnRight;

    Protonav protonav, palimentary;
    ProtonavProbabilities protonavProbabilities, palimentaryProbabilities;

    for (ProtonavPair protonavPair : protonavPairs) {

      protonav = protonavPair.getProtonav();
      palimentary = protonavPair.getPalimentary();
      protonavProbabilities = protonav.getProbabilities();
      palimentaryProbabilities = palimentary.getProbabilities();

      leftCount = patternCounter.countOccurrences(protonav.getPattern(), leftSequence);
      probabilityOnLeft = (leftCount / totalNumberOfNucleotidesOnLeft) / protonavProbabilities
          .getLeftProbabilityBySingles();
      updateProgress(progress++, totalProgress);

      rightCount = patternCounter.countOccurrences(protonav.getPattern(), rightSequence);
      probabilityOnRight = (rightCount / totalNumberOfNucleotidesOnRight) / protonavProbabilities
          .getRightProbabilityBySingles();
      updateProgress(progress++, totalProgress);

      if (protonavFilter.isGood(probabilityOnLeft, probabilityOnRight)) {
        final Results protonavResults = new Results();
        protonavResults.setLeftCount(leftCount);
        protonavResults.setRightCount(rightCount);
        protonavResults.setLeftProbabilityBySingles(probabilityOnLeft);
        protonavResults.setRightProbabilityBySingles(probabilityOnRight);
        protonavResults.setLeftProbabilityByPairs(
            (leftCount / totalNumberOfPairsOnLeft) / protonavProbabilities
                .getLeftProbabilityByPairs());
        protonavResults.setRightProbabilityByPairs(
            (leftCount / totalNumberOfPairsOnRight) / protonavProbabilities
                .getRightProbabilityByPairs());
        protonav.setResults(protonavResults);

        leftCount = patternCounter.countOccurrences(palimentary.getPattern(), leftSequence);
        probabilityOnLeft = (leftCount / totalNumberOfNucleotidesOnLeft) / palimentaryProbabilities
            .getLeftProbabilityBySingles();
        updateProgress(progress++, totalProgress);

        rightCount = patternCounter.countOccurrences(palimentary.getPattern(), rightSequence);
        probabilityOnRight =
            (rightCount / totalNumberOfNucleotidesOnRight) / palimentaryProbabilities
                .getRightProbabilityBySingles();
        updateProgress(progress++, totalProgress);

        if (palimentaryFilter.isGood(probabilityOnLeft, probabilityOnRight)) {
          final Results palimentaryResults = new Results();
          palimentaryResults.setLeftCount(leftCount);
          palimentaryResults.setRightCount(rightCount);
          palimentaryResults.setLeftProbabilityBySingles(probabilityOnLeft);
          palimentaryResults.setRightProbabilityBySingles(probabilityOnRight);
          palimentaryResults.setLeftProbabilityByPairs(
              (leftCount / totalNumberOfPairsOnLeft) / palimentaryProbabilities
                  .getLeftProbabilityByPairs());
          palimentaryResults.setRightProbabilityByPairs(
              (leftCount / totalNumberOfPairsOnRight) / palimentaryProbabilities
                  .getRightProbabilityByPairs());
          palimentary.setResults(palimentaryResults);
          filteredPairs.add(protonavPair);
        }
      } else {
        updateProgress(progress++, totalProgress);
        updateProgress(progress++, totalProgress);
      }
    }

    return filteredPairs;
  }

  private List<ProtonavPair> generateProtonavPairs(List<String> simplePatterns) {
    final List<ProtonavPair> protonavPairs = new ArrayList<>();

    final List<String> newPatterns = new ArrayList<>(simplePatterns);
    newPatterns.removeAll(generatedPatterns);

    double patternLeftProbabilityBySingles;
    double patternRightProbabilityBySingles;
    double palimentaryLeftProbabilityBySingles;
    double palimentaryRightProbabilityBySingles;

    char palimentaryNucleotide;
    String palimentaryPattern;

    final StringBuilder palimentaryBuilder = new StringBuilder();
    char[] patternCharArray;
    for (String pattern : newPatterns) {

      patternLeftProbabilityBySingles = 1;
      patternRightProbabilityBySingles = 1;
      palimentaryLeftProbabilityBySingles = 1;
      palimentaryRightProbabilityBySingles = 1;

      patternCharArray = pattern.toCharArray();
      for (char nucleotide : patternCharArray) {
        palimentaryNucleotide = getPalindromicComplementaryNucleotide(nucleotide);
        palimentaryBuilder.append(palimentaryNucleotide);

        patternLeftProbabilityBySingles *= probabilitiesOfSinglesOnLeft[nucleotide - 'A'];
        patternRightProbabilityBySingles *= probabilitiesOfSinglesOnRight[nucleotide - 'A'];
        palimentaryLeftProbabilityBySingles *= probabilitiesOfSinglesOnLeft[palimentaryNucleotide
            - 'A'];
        palimentaryRightProbabilityBySingles *= probabilitiesOfSinglesOnRight[palimentaryNucleotide
            - 'A'];
      }

      final ProtonavProbabilities protonavProbabilities = new ProtonavProbabilities(
          patternLeftProbabilityBySingles, patternRightProbabilityBySingles);
      setProbabilitiesByPairs(pattern, protonavProbabilities);

      palimentaryPattern = palimentaryBuilder.reverse().toString();
      final ProtonavProbabilities palimentaryProbabilities = new ProtonavProbabilities(
          palimentaryLeftProbabilityBySingles, palimentaryRightProbabilityBySingles);
      setProbabilitiesByPairs(palimentaryPattern, palimentaryProbabilities);

      final ProtonavPair newProtonavPair = new ProtonavPair(PAIRS_COUNT++,
          new Protonav(pattern, protonavProbabilities),
          new Protonav(palimentaryPattern, palimentaryProbabilities));
      protonavPairs.add(newProtonavPair);

      generatedPatterns.add(pattern);
      generatedPatterns.add(palimentaryPattern);
      palimentaryBuilder.delete(0, palimentaryBuilder.length());
    }

    return protonavPairs;
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

  private char getPalindromicComplementaryNucleotide(char nucleotide) {
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

  private List<String> extractSimplePatterns(String sequence, int minLength, int maxLength) {

    final HashSet<String> simplePatterns = new HashSet<>();

    final int length = sequence.length();
    final int firstLoopLength = length - maxLength + 1;

    long totalProgress = 0;
    for (int i = minLength; i <= maxLength; i++) {
      totalProgress += firstLoopLength - i + 1;
    }
    long progress = 0;
    updateProgress(progress++, totalProgress);

    for (int index = 0; index < firstLoopLength; index++) {
      for (int patternLength = minLength; patternLength < maxLength; patternLength++) {
        simplePatterns.add(sequence.substring(index, index + patternLength));
        updateProgress(progress++, totalProgress);
      }
    }

    for (int index = firstLoopLength; index < length; index++) {
      for (int patternLength = minLength; patternLength < maxLength; patternLength++) {
        updateProgress(progress++, totalProgress);
        if (patternLength + index > length) {
          continue;
        }
        simplePatterns.add(sequence.substring(index, index + patternLength));
      }

    }

    final List<String> sortedPatterns = new ArrayList<>(simplePatterns);
    Collections.sort(sortedPatterns);

    return sortedPatterns;
  }

  // private void countOccurrencesForRightOf(String sequence, HashSet<ProtonavPair> allPatterns) {
  //
  //   long progress = 0;
  //   long totalProgress = allPatterns.size() * 2;
  //   updateProgress(progress++, totalProgress);
  //
  //   for (ProtonavPair pair : allPatterns) {
  //     pair.getProtonav().getOccurrences().increaseRightCount(
  //         countOccurrences(preparePatternForCount(pair.getProtonav().getPattern()), sequence));
  //     updateProgress(progress++, totalProgress);
  //     pair.getPalimentary().getOccurrences().increaseRightCount(
  //         countOccurrences(preparePatternForCount(pair.getPalimentary().getPattern()), sequence));
  //     updateProgress(progress++, totalProgress);
  //   }
  // }
  //
  // private void countOccurrencesForLeftOf(String sequence, HashSet<ProtonavPair> allPatterns) {
  //   long progress = 0;
  //   long totalProgress = allPatterns.size() * 2;
  //   updateProgress(progress++, totalProgress);
  //
  //   for (ProtonavPair pair : allPatterns) {
  //     pair.getProtonav().getOccurrences().increaseLeftCount(
  //         countOccurrences(preparePatternForCount(pair.getProtonav().getPattern()), sequence));
  //     updateProgress(progress++, totalProgress);
  //     pair.getPalimentary().getOccurrences().increaseLeftCount(
  //         countOccurrences(preparePatternForCount(pair.getPalimentary().getPattern()), sequence));
  //     updateProgress(progress++, totalProgress);
  //   }
  // }


  private HashSet<ProtonavPair> generateWildPatterns(Set<String> simplePatterns) {
    HashSet<ProtonavPair> protonavPairs = new HashSet<>();

    final HashSet<String> generatedPatterns = new HashSet<>();
    // final String regexStart = "(?=(";
    final String regexStart = "";
    // final String regexGap = "[ACGT]";
    final String regexGap = "N";
    // final String regexEnd = "))";
    final String regexEnd = "";

    // For each simple pattern:
    for (String candidate : simplePatterns) {
      final int len = candidate.length();
      // Get all permutations of pattern's length - 2 because 1st and last nucleotide must stay put.
      List<char[]> permutations = booleanPermutationsWithMaxSetBits(len - 2, Math.floorDiv(len, 2));

      // final HashMap<String, String> protonavPairs = new HashMap<>();
      for (char[] permutation : permutations) {
        final StringBuilder protonavBuilder = new StringBuilder("");
        protonavBuilder
            .append(candidate.charAt(0));        // Because first nucleotide must stay put.
        for (int nucleotide = 1; nucleotide < len - 1; nucleotide++) {
          if (permutation[nucleotide - 1] == '1') {
            protonavBuilder.append(regexGap);
          } else {
            protonavBuilder.append(candidate.charAt(nucleotide));
          }
        }
        protonavBuilder
            .append(candidate.charAt(len - 1));   // Because last nucleotide must stay put.
        final String protonav = protonavBuilder.toString();
        if (!generatedPatterns.contains(protonav)) {
          generatedPatterns.add(protonav);

          double protonavProbability = 1;
          final StringBuilder palimentaryBuilder = new StringBuilder("");
          double palimentaryProbability = 1;

          final StringBuilder pairBuilder = new StringBuilder();
          double protonavPairProbability = 1;
          double palimentaryPairProbability = 1;
          char previousNucleotide = 'N';

          final char[] protonavArray = protonav.toCharArray();
          for (char ch : protonavArray) {
            // protonavProbability *= (nucleotidesProbabilities[ch - 'A']);
            //TODO previous line needs to change to left and right probabilities
            final char nucleotide;
            switch (ch) {
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
              case 'W':
                nucleotide = 'S';
                break;
              case 'S':
                nucleotide = 'W';
                break;
              case 'R':
                nucleotide = 'Y';
                break;
              case 'Y':
                nucleotide = 'R';
                break;
              case 'B':
                nucleotide = 'V';
                break;
              case 'V':
                nucleotide = 'B';
                break;
              case 'D':
                nucleotide = 'H';
                break;
              case 'H':
                nucleotide = 'D';
                break;
              default:
                nucleotide = 'N';
            }
            palimentaryBuilder.append(nucleotide);
            // palimentaryProbability *= nucleotidesProbabilities[nucleotide - 'A'];

            // protonavPairProbability *= pairingsProbabilities
            //     .getOrDefault(pairBuilder.append(previousNucleotide).append(ch).toString(),
            //         (double) 1);
            // palimentaryPairProbability *= pairingsProbabilities
            //     .getOrDefault(pairBuilder.reverse().toString(), (double) 1);

            previousNucleotide = ch;

            pairBuilder.delete(0, 2);
          }
          // final ProtonavPair newPair = new ProtonavPair(
          //     new Protonav(protonav, protonavProbability, protonavPairProbability),
          //     new Protonav(palimentaryBuilder.reverse().toString(), palimentaryProbability,
          //         palimentaryPairProbability));
          // protonavPairs.add(newPair);
        }
      }
    }
    return protonavPairs;
  }

  private HashMap<String, List<String>> generateInterruptedPatterns(Set<String> simplePatterns) {
    HashMap<String, List<String>> candidatesWithRegexes = new HashMap<>();
    // final String regexStart = "(?=(";
    final String regexStart = "";
    // final String regexGap = "[ACGT]{1,10}?";
    final String regexGap = "N";
    // final String regexEnd = "))";
    final String regexEnd = "";

    for (String candidate : simplePatterns) {
      final List<String> candidateRegexes = new ArrayList<>();
      final int len = candidate.length();
      List<char[]> permutations = booleanPermutations(len - 1);
      int count = 0;
      for (char[] permutation : permutations) {
        count++;
        List<String> substrings = new ArrayList<>();
        int from = 0;
        for (int to = 1; to <= len; to++) {
          while (to < len && permutation[to - 1] == '0') {
            to++;
          }
          substrings.add(candidate.substring(from, to));
          from = to;
        }
        candidateRegexes
            .add(String.format("%S%S%S", regexStart, String.join(regexGap, substrings), regexEnd));
      }
      candidatesWithRegexes.put(candidate, candidateRegexes);
    }
    // System.out.println("\n\n\nCandidates with Regexes:");
    // System.out.println(candidatesWithRegexes);
    return candidatesWithRegexes;
  }

  /**
   * Generates possible combinations such that the number of set digits is smaller than maxSetBits.
   *
   * @param n is the length of each combination.
   * @param maxSetBits is the maximal number of 1's in each combination.
   *
   * @return a list of char arrays representing the combinations.
   */
  private List<char[]> booleanPermutationsWithMaxSetBits(int n, int maxSetBits) {
    List<char[]> list = new ArrayList<>();
    BigInteger permutation = BigInteger.ZERO;
    BigDecimal numberOfPermutations = BigDecimal.valueOf(Math.pow(2, n));
    while (permutation.compareTo(numberOfPermutations.toBigInteger()) < 0) {
      StringBuilder binaryCode = new StringBuilder(permutation.toString(2));
      while (binaryCode.length() < n) {
        binaryCode.insert(0, "0");
      }
      list.add(binaryCode.toString().toCharArray());
      do {
        permutation = permutation.add(BigInteger.ONE);
      } while (permutation.bitCount() > maxSetBits);
    }
    return list;
  }


  private List<char[]> booleanPermutations(int n) {
    List<char[]> list = new ArrayList<>();
    BigInteger permutation = BigInteger.ZERO;
    BigDecimal numberOfPermutations = BigDecimal.valueOf(Math.pow(2, n));
    while (permutation.compareTo(numberOfPermutations.toBigInteger()) < 0) {
      StringBuilder binaryCode = new StringBuilder(permutation.toString(2));
      while (binaryCode.length() < n) {
        binaryCode.insert(0, "0");
      }
      // System.out.println(binaryCode.toString());
      list.add(binaryCode.toString().toCharArray());
      permutation = permutation.add(BigInteger.ONE);
    }
    return list;
  }

}
