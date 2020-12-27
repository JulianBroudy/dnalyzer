package com.broudy.entity;

/**
 * TODO provide a summary to Results class!!!!!
 * <p>
 * Created on the 22nd of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Results {

  long leftCount, rightCount;
  double leftProbabilityBySingles, rightProbabilityBySingles;
  double leftProbabilityByPairs, rightProbabilityByPairs;

  public Results(double leftProbabilityBySingles, double rightProbabilityBySingles,
      double leftProbabilityByPairs, double rightProbabilityByPairs) {
    this.leftProbabilityBySingles = leftProbabilityBySingles;
    this.rightProbabilityBySingles = rightProbabilityBySingles;
    this.leftProbabilityByPairs = leftProbabilityByPairs;
    this.rightProbabilityByPairs = rightProbabilityByPairs;
  }

  public Results() {
  }

  /**
   * Gets the leftCount.
   *
   * @return leftCount's value.
   */
  public long getLeftCount() {
    return leftCount;
  }

  /**
   * Sets the leftCount.
   *
   * @param leftCount is the leftCount's new value.
   */
  public void setLeftCount(long leftCount) {
    this.leftCount = leftCount;
  }

  /**
   * Gets the rightCount.
   *
   * @return rightCount's value.
   */
  public long getRightCount() {
    return rightCount;
  }

  /**
   * Sets the rightCount.
   *
   * @param rightCount is the rightCount's new value.
   */
  public void setRightCount(long rightCount) {
    this.rightCount = rightCount;
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
   * Sets the leftProbabilityBySingles.
   *
   * @param leftProbabilityBySingles is the leftProbabilityBySingles's new value.
   */
  public void setLeftProbabilityBySingles(double leftProbabilityBySingles) {
    this.leftProbabilityBySingles = leftProbabilityBySingles;
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
   * Sets the rightProbabilityBySingles.
   *
   * @param rightProbabilityBySingles is the rightProbabilityBySingles's new value.
   */
  public void setRightProbabilityBySingles(double rightProbabilityBySingles) {
    this.rightProbabilityBySingles = rightProbabilityBySingles;
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
