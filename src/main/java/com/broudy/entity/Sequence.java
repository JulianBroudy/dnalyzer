package com.broudy.entity;

import java.io.File;

/**
 * TODO provide a summary to Sequence class!!!!!
 * <p>
 * Created on the 23rd of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Sequence {

  private final File dnaSequence;
  private File targetFile;
  private int startIndex, endIndex;

  public Sequence(File dnaSequence, int startIndex, int endIndex) {
    this.dnaSequence = dnaSequence;
    this.startIndex = startIndex;
    this.endIndex = endIndex;
  }

  public Sequence(File dnaSequence, String startIndex, String endIndex) {
    this(dnaSequence, Integer.parseInt(startIndex), Integer.parseInt(endIndex));
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
   * Gets the targetFile.
   *
   * @return targetFile's value.
   */
  public File getTargetFile() {
    return targetFile;
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
}



