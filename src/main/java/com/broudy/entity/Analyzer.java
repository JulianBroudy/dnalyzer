package com.broudy.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  public Analyzer(ParsedSequence parsedSequence) {
    this.parsedSequence = parsedSequence;
  }

  @Override
  protected ParsedSequence call() throws Exception {

    // Analyze left side
    updateProgress(0, 2);

    nucleotidesProbabilities = getNucleotideProbabilities();
    parsedSequence.setNucleotidesProbabilities(nucleotidesProbabilities);
    


    // 1. Get regexes for left side patterns
    final HashSet<String> simplePatterns = extractSimplePatterns(
        parsedSequence.getSequenceBeforeTargetSite());
    updateProgress(1, 2);

    final HashSet<ProtonavPair> protonavPairs = generateWildPatterns(simplePatterns);
    updateProgress(2, 2);

    // final HashSet<ProtonavPair> protonavPairs = generateProtonavPairs(simplePatterns);

    // 2. Generate palimentary (palindromic and complementary) of right side
    // StringBuilder reversedSequence = new StringBuilder(
    //     parsedSequence.getSequenceBeforeTargetSite());
    // reversedSequence.reverse();
    // final String rightPalimentary = reversedSequence.toString().replace('A', 'T').replace('T', 'A')
    //     .replace('G', 'C').replace('C', 'G');

    // 3. Count occurrences in both sides
    countOccurrencesForLeftOf(parsedSequence.getSequenceBeforeTargetSite(), protonavPairs);
    countOccurrencesForRightOf(parsedSequence.getSequenceAfterTargetSite(), protonavPairs);

    parsedSequence.setResults(protonavPairs);


    // Analyze right side
    return parsedSequence;
  }

  private double[]
  getNucleotideProbabilities() {
    long[] count = new long[26];

    char[] sequence = parsedSequence.getSequenceBeforeTargetSite().toCharArray();
    for (char ch : sequence) {
      count[ch - 'A']++;
    }
    sequence = parsedSequence.getSequenceAfterTargetSite().toCharArray();
    for (char ch : sequence) {
      count[ch - 'A']++;
    }
    double totalCount = parsedSequence.getSequenceBeforeTargetSite().length() + parsedSequence
        .getSequenceAfterTargetSite().length();
    double[] probabilities = new double[26];
    // BigDecimal occurrences;
    for (int i = 0; i < 26; i++) {
      if (count[i] == 0) {
        probabilities[i] = 1;
      } else {
        probabilities[i] = count[i] / totalCount;
      }
      // occurrences = BigDecimal.valueOf(count[i]);
      // probabilities[i] = occurrences.divide(BigDecimal.valueOf(totalCount), RoundingMode.);
    }
    // for (int i = 0; i < 26; i++) {
    //   // if(probabilities[i].equals(BigDecimal.ZERO)){
    //   //   probabilities[i] = BigDecimal.ONE;
    //   // }
    // }

    return probabilities;
  }


  private void countOccurrencesForRightOf(String sequence, HashSet<ProtonavPair> allPatterns) {

    long progress = 0;
    long totalProgress = allPatterns.size() * 2;
    updateProgress(progress++, totalProgress);

    for (ProtonavPair pair : allPatterns) {
      pair.getProtonav().getOccurrences()
          .increaseRightCount(countOccurrences(pair.getProtonav(), sequence));
      updateProgress(progress++, totalProgress);
      pair.getPalimentary().getOccurrences()
          .increaseRightCount(countOccurrences(pair.getPalimentary(), sequence));
      updateProgress(progress++, totalProgress);
    }
  }

  private void countOccurrencesForLeftOf(String sequence, HashSet<ProtonavPair> allPatterns) {
    long progress = 0;
    long totalProgress = allPatterns.size() * 2;
    updateProgress(progress++, totalProgress);

    for (ProtonavPair pair : allPatterns) {
      pair.getProtonav().getOccurrences()
          .increaseLeftCount(countOccurrences(pair.getProtonav(), sequence));
      updateProgress(progress++, totalProgress);
      pair.getPalimentary().getOccurrences()
          .increaseLeftCount(countOccurrences(pair.getPalimentary(), sequence));
      updateProgress(progress++, totalProgress);
    }
  }

  private long countOccurrences(Protonav protonav, String sequence) {
    final Pattern p = Pattern
        .compile(Objects.requireNonNull(preparePatternForCount(protonav.getPattern())));
    final Matcher m = p.matcher(sequence);
    long numberOfOccurrences = 0;
    while (m.find()) {
      final int groupCount = m.groupCount();
      for (int i = 0; i <= groupCount; i++) {
        if (!m.group(i).isEmpty()) {
          numberOfOccurrences++;
        }
      }
    }
    return numberOfOccurrences;
  }

  private String preparePatternForCount(String pattern) {
    final StringBuilder patternBuilder = new StringBuilder("");

    for (char ch : pattern.toCharArray()) {
      switch (ch) {
        case 'N':
          patternBuilder.append("[ACGT]");
          break;
        case 'W':
          patternBuilder.append("[AT]");
          break;
        case 'S':
          patternBuilder.append("[CG]");
          break;
        case 'R':
          patternBuilder.append("[AG]");
          break;
        case 'Y':
          patternBuilder.append("[CT]");
          break;
        case 'B':
          patternBuilder.append("[^A]");
          break;
        case 'D':
          patternBuilder.append("[^C]");
          break;
        case 'H':
          patternBuilder.append("[^G]");
          break;
        case 'V':
          patternBuilder.append("[^T]");
          break;
        default:
          patternBuilder.append(ch);
      }
    }

    return patternBuilder.toString();
  }


  private HashMap<String, HashMap<String, Occurrences>> analyzeSequence(String sequence) {

    // simplePatterns.entrySet().stream().sorted(Map.Entry.comparingByValue())
    //     .forEach(System.out::println);
    // final HashMap<String, HashMap<String, Occurrences>> wildPatterns = generateWildPatterns(
    //     simplePatterns.keySet());

    return null;
  }


  private HashSet<String> extractSimplePatterns(String sequence) {
    return extractSimplePatterns(sequence, 3, 7);
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

    // for (int patternLength = minLength; patternLength < maxLength; patternLength++) {
    //   for (int i = firstLoopLength; i < sequenceLength - patternLength; i++) {
    //     simplePatterns.add(sequence.substring(i, i + patternLength));
    //   }
    // }
    return simplePatterns;
  }

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
          double palimentaryProbability = 1;

          final char[] reversedProtonav = protonavBuilder.reverse().toString().toCharArray();
          final StringBuilder palimentaryBuilder = new StringBuilder("");
          for (char ch : reversedProtonav) {
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
          }
          final ProtonavPair newPair = new ProtonavPair(new Protonav(protonav, protonavProbability),
              new Protonav(palimentaryBuilder.toString(), palimentaryProbability));
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
