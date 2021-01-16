package com.broudy.entity;

import java.io.File;

/**
 * TODO provide a summary to Sequence class!!!!!
 * <p>
 * Created on the 23rd of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SequenceToBeParsed {

  private final File dnaSequence;
  private int startIndex, endIndex;
  private boolean isCyclic;
  private final int minPatternLength, maxPatternLength;

  public SequenceToBeParsed(File dnaSequence, int startIndex, int endIndex, boolean isCyclic,
      int minPatternLength, int maxPatternLength) {
    this.dnaSequence = dnaSequence;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.isCyclic = isCyclic;
    this.minPatternLength = minPatternLength;
    this.maxPatternLength = maxPatternLength;
  }

  public SequenceToBeParsed(File dnaSequence, String startIndex, String endIndex, boolean isCyclic,
      int minPatternLength, int maxPatternLength) {
    this(dnaSequence, Integer.parseInt(startIndex), Integer.parseInt(endIndex), isCyclic,
        minPatternLength, maxPatternLength);
  }


  /**
   * Gets the file.
   *
   * @return file's value.
   */
  public File getDnaSequence() {
    return dnaSequence;
  }

  /**
   * Gets the startIndex.
   *
   * @return startIndex's value.
   */
  public int getStartIndex() {
    return startIndex;
  }

  /**
   * Gets the endIndex.
   *
   * @return endIndex's value.
   */
  public int getEndIndex() {
    return endIndex;
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
}



