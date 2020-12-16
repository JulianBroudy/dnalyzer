package com.broudy.entity;

import java.util.HashMap;

/**
 * A class that holds probabilities by single nucleotides and nucleotide pairs.
 * <p>
 * Created on the 16th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class NucleotideProbabilities {

  private double[] probabilitiesOfSingles;
  private HashMap<String, Double> probabilitiesOfPairs;

  public NucleotideProbabilities(String inThisSequence) {
    probabilitiesOfPairs = ProbabilitiesCalculator.calculateProbabilitiesOfPairs(inThisSequence);
    probabilitiesOfSingles = ProbabilitiesCalculator
        .calculateProbabilitiesOfSingles(inThisSequence);
  }

  /**
   * Gets the nucleotidesCount.
   *
   * @return nucleotidesCount's value.
   */
  public double[] getProbabilitiesOfSingles() {
    return probabilitiesOfSingles;
  }

  /**
   * Sets the nucleotidesCount.
   *
   * @param probabilitiesOfSingles is the nucleotidesCount's new value.
   */
  public void setProbabilitiesOfSingles(double[] probabilitiesOfSingles) {
    this.probabilitiesOfSingles = probabilitiesOfSingles;
  }

  /**
   * Gets the pairsProbabilities.
   *
   * @return pairsProbabilities's value.
   */
  public HashMap<String, Double> getProbabilitiesOfPairs() {
    return probabilitiesOfPairs;
  }

  /**
   * Sets the pairsProbabilities.
   *
   * @param probabilitiesOfPairs is the pairsProbabilities's new value.
   */
  public void setProbabilitiesOfPairs(HashMap<String, Double> probabilitiesOfPairs) {
    this.probabilitiesOfPairs = probabilitiesOfPairs;
  }

  /**
   * Helper class to encapsulate the process of calculating the probabilities of a sequence.
   */
  static class ProbabilitiesCalculator {

    public static double[] calculateProbabilitiesOfSingles(String inThisSequence) {
      long[] count = new long[26];

      final char[] sequence = inThisSequence.toCharArray();
      for (char ch : sequence) {
        count[ch - 'A']++;
      }

      double totalCount = 0;
      for (long occurrences : count) {
        totalCount += occurrences;
      }

      double[] probabilities = new double[26];
      for (int i = 0; i < 26; i++) {
        probabilities[i] = count[i] == 0 ? 1 : count[i] / totalCount;
      }
      return probabilities;
    }

    static HashMap<String, Double> calculateProbabilitiesOfPairs(String sequence) {
      final HashMap<String, Double> probabilitiesOfPairs = generateNucleotidePairs();

      final PatternCounter patternCounter = new PatternCounter();
      long totalCount = 0;
      long occurrences;
      for (String pair : probabilitiesOfPairs.keySet()) {
        occurrences = patternCounter.countOccurrences(pair, sequence);
        probabilitiesOfPairs.put(pair, (occurrences == 0) ? 1 : (double) occurrences);
        totalCount += occurrences;
      }
      for (String pair : probabilitiesOfPairs.keySet()) {
        probabilitiesOfPairs.put(pair, probabilitiesOfPairs.get(pair) / totalCount);
      }
      return probabilitiesOfPairs;
    }

    static private HashMap<String, Double> generateNucleotidePairs() {
      final HashMap<String, Double> pairingsProbabilities = new HashMap<>();

      // final char[] nucleotides = new char[]{'A', 'C', 'G', 'T', 'N', 'W', 'S', 'Y', 'R'};
      final char[] nucleotides = new char[]{'A', 'C', 'G', 'T'};
      final StringBuilder stringBuilder = new StringBuilder();
      for (char firstNucleotide : nucleotides) {
        for (char secondNucleotide : nucleotides) {
          pairingsProbabilities
              .put(stringBuilder.append(firstNucleotide).append(secondNucleotide).toString(),
                  (double) 0);
          stringBuilder.delete(0, 2);
        }
      }
      return pairingsProbabilities;
    }

  }


}
