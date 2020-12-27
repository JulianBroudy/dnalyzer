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
  private final Occurrences occurrences;
  private final ProtonavProbabilities probabilities;
  private Results results;

  public Protonav(String pattern, ProtonavProbabilities probabilities) {
    this.pattern = pattern;
    this.probabilities = probabilities;
    this.occurrences = new Occurrences();
    this.results = new Results();
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
   * Gets the occurrences.
   *
   * @return occurrences's value.
   */
  public Occurrences getOccurrences() {
    return occurrences;
  }

  /**
   * Gets the probabilities.
   *
   * @return probabilities's value.
   */
  public ProtonavProbabilities getProbabilities() {
    return probabilities;
  }

  /**
   * Gets the results.
   *
   * @return results's value.
   */
  public Results getResults() {
    return results;
  }

  /**
   * Sets the results.
   *
   * @param results is the results's new value.
   */
  public void setResults(Results results) {
    this.results = results;
  }
}
