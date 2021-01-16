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
  private final CorrelationArrays correlationArrays;
  private ProtonavProbabilities resultingProbabilities;
  private Results results;

  public Protonav(String pattern, ProtonavProbabilities probabilities,
      CorrelationArrays correlationArrays) {
    this.pattern = pattern;
    this.probabilities = probabilities;
    this.occurrences = new Occurrences();
    this.correlationArrays = correlationArrays;
    this.results = new Results();
  }
  //
  // public Protonav(String pattern) {
  //   this.pattern = pattern;
  //   this.probabilities = null;
  //   this.occurrences = new Occurrences();
  //   this.correlationArrays = new CorrelationArrays();
  //   this.results = new Results();
  // }


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
   * Gets the correlationArrays.
   *
   * @return correlationArrays's value.
   */
  public CorrelationArrays getCorrelationArrays() {
    return correlationArrays;
  }

  /**
   * Gets the resultingProbabilities.
   *
   * @return resultingProbabilities's value.
   */
  public ProtonavProbabilities getResultingProbabilities() {
    return resultingProbabilities;
  }

  /**
   * Sets the resultingProbabilities.
   *
   * @param resultingProbabilities is the resultingProbabilities's new value.
   */
  public void setResultingProbabilities(ProtonavProbabilities resultingProbabilities) {
    this.resultingProbabilities = resultingProbabilities;
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
