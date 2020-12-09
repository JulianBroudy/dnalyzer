package com.broudy.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

  public Analyzer(ParsedSequence parsedSequence) {
    this.parsedSequence = parsedSequence;
  }

  @Override
  protected ParsedSequence call() throws Exception {

    // Analyze left side
    updateProgress(0, 2);

    // 1. Get regexes for left side patterns
    final HashMap<String, Integer> simplePatterns = extractSimplePatterns(
        parsedSequence.getSequenceBeforeTargetSite());
    updateProgress(1, 2);

    final HashMap<String, HashMap<String, Occurrences>> wildPatterns = generateWildPatterns(
        simplePatterns.keySet());
    updateProgress(2, 2);

    // 2. Generate palimentary (palindromic and complementary) of right side
    StringBuilder reversedSequence = new StringBuilder(
        parsedSequence.getSequenceBeforeTargetSite());
    reversedSequence.reverse();
    // final String rightPalimentary = reversedSequence.toString().replace('A', 'T').replace('T', 'A')
    //     .replace('G', 'C').replace('C', 'G');

    // 3. Count occurrences in both sides
    updateProgress(0, wildPatterns.size());
    countOccurrencesForLeftOf(parsedSequence.getSequenceBeforeTargetSite(), wildPatterns);

    updateProgress(0, wildPatterns.size());
    countOccurrencesForRightOf(parsedSequence.getSequenceAfterTargetSite(), wildPatterns);

    parsedSequence.setResults(wildPatterns);
    // for (HashMap<String, Occurrences> patterns : wildPatterns.values()) {
    //   patterns.entrySet().stream().sorted(Map.Entry.comparingByKey())
    //       .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    // }

    // Analyze right side

    return parsedSequence;
  }

  private void countOccurrencesForRightOf(String sequence,
      HashMap<String, HashMap<String, Occurrences>> allPatterns) {

    long progress = 0;
    updateProgress(progress++, allPatterns.size());
    // final HashSet<String> checkedPatterns = new HashSet<>();

    for (HashMap<String, Occurrences> patterns : allPatterns.values()) {
      updateProgress(progress++, allPatterns.size());
      for (Map.Entry<String, Occurrences> pattern : patterns.entrySet()) {
        // if (checkedPatterns.contains(pattern.getKey())) {
        //   continue;
        // }
        // checkedPatterns.add(pattern.getKey());
        final Pattern p = Pattern.compile(pattern.getKey().replace("-", "[ACGT]"));
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
        pattern.getValue().increaseRightCount(numberOfOccurrences);
      }
    }


  }

  private void countOccurrencesForLeftOf(String sequence,
      HashMap<String, HashMap<String, Occurrences>> allPatterns) {
    long progress = 0;
    updateProgress(progress++, allPatterns.size());
    final HashSet<String> checkedPatterns = new HashSet<>();

    for (HashMap<String, Occurrences> patterns : allPatterns.values()) {
      updateProgress(progress++, allPatterns.size());
      for (Map.Entry<String, Occurrences> pattern : patterns.entrySet()) {
        if (checkedPatterns.contains(pattern.getKey())) {
          continue;
        }
        checkedPatterns.add(pattern.getKey());
        final Pattern p = Pattern.compile(pattern.getKey().replace("-", "[ACGT]"));
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
        pattern.getValue().increaseLeftCount(numberOfOccurrences);
      }
    }


  }


  private HashMap<String, HashMap<String, Occurrences>> analyzeSequence(String sequence) {

    // simplePatterns.entrySet().stream().sorted(Map.Entry.comparingByValue())
    //     .forEach(System.out::println);
    // final HashMap<String, HashMap<String, Occurrences>> wildPatterns = generateWildPatterns(
    //     simplePatterns.keySet());

    return null;
  }


  private HashMap<String, Integer> extractSimplePatterns(String sequence) {
    final HashMap<String, Integer> simplePatterns = new HashMap<>();
    final int sequenceLength = sequence.length() + 1; // 1 is added here instead of in 2nd for loop
    String simplePattern;
    for (int patternLength = 3; patternLength < 7; patternLength++) {
      for (int i = 0; i < sequenceLength - patternLength; i++) {
        simplePattern = sequence.substring(i, i + patternLength);
        simplePatterns.putIfAbsent(simplePattern, 0);
        simplePatterns.replace(simplePattern, simplePatterns.getOrDefault(simplePattern, 0) + 1);
      }
    }
    return simplePatterns;
  }

  private HashMap<String, HashMap<String, Occurrences>> generateWildPatterns(
      Set<String> simplePatterns) {
    HashMap<String, HashMap<String, Occurrences>> wildPatterns = new HashMap<>();

    final HashSet<String> generatedPatterns = new HashSet<>();
    // final String regexStart = "(?=(";
    final String regexStart = "";
    // final String regexGap = "[ACGT]";
    final String regexGap = "-";
    // final String regexEnd = "))";
    final String regexEnd = "";

    // For each simple pattern:
    for (String candidate : simplePatterns) {
      final int len = candidate.length();
      // Get all permutations of pattern's length - 2 because 1st and last nucleotide must stay put.
      List<char[]> permutations = booleanPermutationsWithMaxSetBits(len - 2, Math.floorDiv(len, 2));

      final HashMap<String, Occurrences> wildPatternsOfCandidate = new HashMap<>();
      for (char[] permutation : permutations) {
        final StringBuilder newPattern = new StringBuilder("");
        newPattern.append(candidate.charAt(0));        // Because first nucleotide must stay put.
        for (int nucleotide = 1; nucleotide < len - 1; nucleotide++) {
          if (permutation[nucleotide - 1] == '1') {
            newPattern.append(regexGap);
          } else {
            newPattern.append(candidate.charAt(nucleotide));
          }
        }
        newPattern.append(candidate.charAt(len - 1));   // Because first nucleotide must stay put.
        if (!generatedPatterns.contains(newPattern.toString())) {
          wildPatternsOfCandidate.put(newPattern.toString(), new Occurrences());
          generatedPatterns.add(newPattern.toString());
        }
      }
      wildPatterns.put(candidate, wildPatternsOfCandidate);
    }
    // System.out.println("\n\n\nCandidates with Regexes:");
    // System.out.println(wildPatterns);
    return wildPatterns;
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
