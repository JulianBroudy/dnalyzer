package com.broudy.entity;

import java.math.BigDecimal;

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
  private final double probability;
  private  double pairsProbability;
  private final Occurrences occurrences;

  public Protonav(String pattern, double probability,double pairsProbability) {
    this.pattern = pattern;
    this.probability = probability;
    this.pairsProbability = pairsProbability;
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
  public double getProbability() {
    return probability;
  }

  /**
   * Gets the pairsProbability.
   *
   * @return pairsProbability's value.
   */
  public double getPairsProbability() {
    return pairsProbability;
  }

  /**
   * Sets the pairsProbability.
   *
   * @param pairsProbability is the pairsProbability's new value.
   */
  public void setPairsProbability(double pairsProbability) {
    this.pairsProbability = pairsProbability;
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
