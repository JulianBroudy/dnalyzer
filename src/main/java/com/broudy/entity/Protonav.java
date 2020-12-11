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
  private final long probability;
  private final Occurrences occurrences;

  public Protonav(String pattern, long probability) {
    this.pattern = pattern;
    this.probability = probability;
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
  public long getProbability() {
    return probability;
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
