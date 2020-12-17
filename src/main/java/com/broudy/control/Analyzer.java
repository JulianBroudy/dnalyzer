package com.broudy.control;

import com.broudy.entity.ParsedSequence;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.Sequence;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
  private double[] nucleotidesProbabilities;
  private HashMap<String, Double> pairingsProbabilities;

  public Analyzer(ParsedSequence parsedSequence) {
    this.parsedSequence = parsedSequence;
  }

  @Override
  protected ParsedSequence call() throws Exception {

    //  Get patterns from left sequence
    HashSet<String> simplePatterns = extractSimplePatterns(
        parsedSequence.getLeftSequence().getSequence(), parsedSequence.getMinPatternLength(),
        parsedSequence.getMaxPatternLength());

    // Generate protonav pairs
    List<ProtonavPair> protonavPairs = generateProtonavPairs(simplePatterns,
        parsedSequence.getLeftSequence());

    //  Count
    countOccurrences(protonavPairs);
    parsedSequence.getLeftSequence().getProtonavs().addAll(protonavPairs);

    //  Get patterns from left sequence
    simplePatterns = extractSimplePatterns(parsedSequence.getRightSequence().getSequence(),
        parsedSequence.getMinPatternLength(), parsedSequence.getMaxPatternLength());

    // Generate protonav pairs
    protonavPairs = generateProtonavPairs(simplePatterns, parsedSequence.getRightSequence());

    //  Count on left side
    countOccurrences(protonavPairs);
    parsedSequence.getRightSequence().getProtonavs().addAll(protonavPairs);

    return parsedSequence;
  }

  private void countOccurrences(List<ProtonavPair> protonavPairs) {

    long progress = 0;
    long totalProgress = protonavPairs.size() * 4;

    final String leftSequence = parsedSequence.getLeftSequence().getSequence();
    final String rightSequence = parsedSequence.getRightSequence().getSequence();
    PatternCounter patternCounter = new PatternCounter();

    for (ProtonavPair protonavPair : protonavPairs) {
      protonavPair.getProtonav().getOccurrences().increaseLeftCount(
          patternCounter.countOccurrences(protonavPair.getProtonav().getPattern(), leftSequence));
      updateProgress(progress++, totalProgress);
      protonavPair.getProtonav().getOccurrences().increaseRightCount(
          patternCounter.countOccurrences(protonavPair.getProtonav().getPattern(), rightSequence));
      updateProgress(progress++, totalProgress);

      protonavPair.getPalimentary().getOccurrences().increaseLeftCount(patternCounter
          .countOccurrences(protonavPair.getPalimentary().getPattern(), leftSequence));
      updateProgress(progress++, totalProgress);
      protonavPair.getPalimentary().getOccurrences().increaseRightCount(patternCounter
          .countOccurrences(protonavPair.getPalimentary().getPattern(), rightSequence));
      updateProgress(progress++, totalProgress);
    }

  }

  private List<ProtonavPair> generateProtonavPairs(HashSet<String> simplePatterns,
      Sequence sequence) {
    final List<ProtonavPair> protonavPairs = new ArrayList<>();

    final double[] probabilitiesOfSingles = sequence.getNucleotideProbabilities()
        .getProbabilitiesOfSingles();
    final HashMap<String, Double> probabilitiesByPairs = sequence.getNucleotideProbabilities()
        .getProbabilitiesOfPairs();

    double patternProbabilityBySingles;
    double palimentaryProbabilityBySingles;

    StringBuilder pairBuilder = new StringBuilder();
    double patternProbabilityByPairs;
    double palimentaryProbabilityByPairs;

    char previousNucleotide;
    char palimentaryNucleotide;

    final StringBuilder palimentaryBuilder = new StringBuilder();
    char[] patternCharArray;
    for (String pattern : simplePatterns) {

      patternProbabilityBySingles = 1;
      palimentaryProbabilityBySingles = 1;
      patternProbabilityByPairs = 1;
      palimentaryProbabilityByPairs = 1;

      previousNucleotide = '-';

      patternCharArray = pattern.toCharArray();
      for (char nucleotide : patternCharArray) {
        palimentaryNucleotide = getPalindromicComplementaryNucleotide(nucleotide);
        palimentaryBuilder.append(palimentaryNucleotide);

        patternProbabilityBySingles *= probabilitiesOfSingles[nucleotide - 'A'];
        palimentaryProbabilityBySingles *= probabilitiesOfSingles[palimentaryNucleotide - 'A'];

        patternProbabilityByPairs *= probabilitiesByPairs
            .getOrDefault(pairBuilder.append(previousNucleotide).append(nucleotide).toString(),
                (double) 1);
        palimentaryProbabilityByPairs *= probabilitiesByPairs
            .getOrDefault(pairBuilder.reverse().toString(), (double) 1);

        previousNucleotide = nucleotide;
        pairBuilder.delete(0, 2);
      }

      final ProtonavPair newProtonavPair = new ProtonavPair(
          new Protonav(pattern, patternProbabilityBySingles, patternProbabilityByPairs),
          new Protonav(palimentaryBuilder.reverse().toString(), palimentaryProbabilityBySingles,
              palimentaryProbabilityByPairs));
      protonavPairs.add(newProtonavPair);
      palimentaryBuilder.delete(0, palimentaryBuilder.length());
    }

    return protonavPairs;
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

  private HashSet<String> extractSimplePatterns(String sequence, int minLength, int maxLength) {

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

    return simplePatterns;
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
            protonavProbability *= (nucleotidesProbabilities[ch - 'A']);
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
            palimentaryProbability *= nucleotidesProbabilities[nucleotide - 'A'];

            protonavPairProbability *= pairingsProbabilities
                .getOrDefault(pairBuilder.append(previousNucleotide).append(ch).toString(),
                    (double) 1);
            palimentaryPairProbability *= pairingsProbabilities
                .getOrDefault(pairBuilder.reverse().toString(), (double) 1);

            previousNucleotide = ch;

            pairBuilder.delete(0, 2);
          }
          final ProtonavPair newPair = new ProtonavPair(
              new Protonav(protonav, protonavProbability, protonavPairProbability),
              new Protonav(palimentaryBuilder.reverse().toString(), palimentaryProbability,
                  palimentaryPairProbability));
          protonavPairs.add(newPair);
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
