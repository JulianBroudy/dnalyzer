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
  private final AutocorrelationArrays autocorrelationArrays;
  private ProtonavProbabilities resultingProbabilities;

  public Protonav(String pattern, ProtonavProbabilities probabilities,
      AutocorrelationArrays autocorrelationArrays) {
    this.pattern = pattern;
    this.probabilities = probabilities;
    this.occurrences = new Occurrences();
    this.autocorrelationArrays = autocorrelationArrays;
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
   * Gets the correlationArrays.
   *
   * @return correlationArrays's value.
   */
  public AutocorrelationArrays getAutocorrelationArrays() {
    return autocorrelationArrays;
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
   * @param resultingProbabilities is the resultingProbabilities' new value.
   */
  public void setResultingProbabilities(ProtonavProbabilities resultingProbabilities) {
    this.resultingProbabilities = resultingProbabilities;
  }

}
