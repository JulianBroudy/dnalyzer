package com.broudy.entity;

/**
 * This class represents a Protonav that includes the pattern, occurrences and the probability of
 * this pattern.
 * <p>
 * Created on the 11th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Protonav {

  private final String pattern;
  private final double probabilityBySingles;
  private final double probabilityByPairs;
  private final Occurrences occurrences;

  public Protonav(String pattern, double probabilityBySingles, double probabilityByPairs) {
    this.pattern = pattern;
    this.probabilityBySingles = probabilityBySingles;
    this.probabilityByPairs = probabilityByPairs;
    this.occurrences = new Occurrences();
  }

  /**
   * Gets the pattern.
   *
   * @return pattern's value.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Gets the probability.
   *
   * @return probability's value.
   */
  public double getProbabilityBySingles() {
    return probabilityBySingles;
  }

  /**
   * Gets the pairsProbability.
   *
   * @return pairsProbability's value.
   */
  public double getProbabilityByPairs() {
    return probabilityByPairs;
  }

  /**
   * Gets the occurrences.
   *
   * @return occurrences's value.
   */
  public Occurrences getOccurrences() {
    return occurrences;
  }
}
