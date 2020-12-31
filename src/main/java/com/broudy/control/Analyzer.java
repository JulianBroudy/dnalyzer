package com.broudy.control;

import com.broudy.control.ResultsFilter.MoreOnLeftFilter;
import com.broudy.control.ResultsFilter.MoreOnRightFilter;
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

  @Override
  protected ParsedSequence call() throws Exception {

    return analyzeOccurrenceProbabilities();

    /*final int WINDOW = 500;
    final int MIN_LEN = 2;
    final int MAX_LEN = 3;

    HashMap<String, int[]> patternsCorrelations = new HashMap<>();

    HashSet<String> patterns = new HashSet<>();

    final String sequence = parsedSequence.getLeftSequence().getSequence();

    final int from = sequence.length()-10000-WINDOW;
    final int to = sequence.length() - WINDOW;
    final String sequenceUnderTest = sequence.substring(from);

    final PatternCounter patternCounter = new PatternCounter();

    for (int i = 0; i < 10000; i++) {

      final String searchArea = sequenceUnderTest.substring(i, i + WINDOW);

      for (int len = MIN_LEN; len <= MAX_LEN; len++) {

        final String pattern = sequenceUnderTest.substring(i, i + len);
        final int[] array;
        if (patterns.contains(pattern)) {
          array = patternsCorrelations.get(pattern);
        } else {
          array = new int[500];
          patterns.add(pattern);
          patternsCorrelations.put(pattern, array);
        }
        patternCounter.updateCorrelationArray(pattern, searchArea, array);
      }
    }

    patternsCorrelations.forEach((key, value) -> System.out.println(key + ":\n" + Arrays.toString(value)+"\n"));

    return parsedSequence;*/

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
