package com.broudy.entity;

/**
 * A class for encapsulating all of the inputted parameters for an analysis.
 * <p>
 * Created on the 15th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class AnalysisParameters {

  private final boolean isCyclic;
  private final int targetSiteStartIndex, targetSiteEndIndex;
  private final int minPatternLength, maxPatternLength;
  private final int windowSize;
  private final int padding;

  public AnalysisParameters(boolean isCyclic, int targetSiteStartIndex, int targetSiteEndIndex,
      int minPatternLength, int maxPatternLength, int windowSize, int padding) {
    this.isCyclic = isCyclic;
    this.targetSiteStartIndex = targetSiteStartIndex;
    this.targetSiteEndIndex = targetSiteEndIndex;
    this.windowSize = windowSize;
    this.minPatternLength = minPatternLength;
    this.maxPatternLength = maxPatternLength;
    this.padding = padding;
  }

  /**
   * Gets the isCyclic.
   *
   * @return isCyclic's value.
   */
  public boolean isCyclic() {
    return isCyclic;
  }

  /**
   * Gets the startIndex.
   *
   * @return startIndex's value.
   */
  public int getTargetSiteStartIndex() {
    return targetSiteStartIndex;
  }

  /**
   * Gets the endIndex.
   *
   * @return endIndex's value.
   */
  public int getTargetSiteEndIndex() {
    return targetSiteEndIndex;
  }

  /**
   * Gets the minPatternLength.
   *
   * @return minPatternLength's value.
   */
  public int getMinPatternLength() {
    return minPatternLength;
  }

  /**
   * Gets the maxPatternLength.
   *
   * @return maxPatternLength's value.
   */
  public int getMaxPatternLength() {
    return maxPatternLength;
  }

  /**
   * Gets the filterSize.
   *
   * @return filterSize's value.
   */
  public int getWindowSize() {
    return windowSize;
  }

  /**
   * Gets the padding.
   *
   * @return padding's value.
   */
  public int getPadding() {
    return padding;
  }
}
