package com.broudy.entity;

/**
 * Encapsulate probabilities
 * <p>
 * Created on the 22nd of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ProtonavProbabilities {

  private final double leftProbabilityBySingles, rightProbabilityBySingles;
  private double leftProbabilityByPairs, rightProbabilityByPairs;


  public ProtonavProbabilities(double leftProbabilityBySingles, double rightProbabilityBySingles) {
    this.leftProbabilityBySingles = leftProbabilityBySingles;
    this.rightProbabilityBySingles = rightProbabilityBySingles;
  }

  /**
   * Gets the leftProbabilityBySingles.
   *
   * @return leftProbabilityBySingles's value.
   */
  public double getLeftProbabilityBySingles() {
    return leftProbabilityBySingles;
  }

  /**
   * Gets the rightProbabilityBySingles.
   *
   * @return rightProbabilityBySingles's value.
   */
  public double getRightProbabilityBySingles() {
    return rightProbabilityBySingles;
  }

  /**
   * Gets the leftProbabilityByPairs.
   *
   * @return leftProbabilityByPairs's value.
   */
  public double getLeftProbabilityByPairs() {
    return leftProbabilityByPairs;
  }

  /**
   * Sets the leftProbabilityByPairs.
   *
   * @param leftProbabilityByPairs is the leftProbabilityByPairs's new value.
   */
  public void setLeftProbabilityByPairs(double leftProbabilityByPairs) {
    this.leftProbabilityByPairs = leftProbabilityByPairs;
  }

  /**
   * Gets the rightProbabilityByPairs.
   *
   * @return rightProbabilityByPairs's value.
   */
  public double getRightProbabilityByPairs() {
    return rightProbabilityByPairs;
  }

  /**
   * Sets the rightProbabilityByPairs.
   *
   * @param rightProbabilityByPairs is the rightProbabilityByPairs's new value.
   */
  public void setRightProbabilityByPairs(double rightProbabilityByPairs) {
    this.rightProbabilityByPairs = rightProbabilityByPairs;
  }
}
