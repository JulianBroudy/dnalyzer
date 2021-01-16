package com.broudy.entity;

/**
 * A simple class to improve encapsulation.
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class FileMetadata {

  private final String fileName;
  private final String header;
  private final String targetSite;

  public FileMetadata(String fileName, String header, String targetSite) {
    this.fileName = fileName;
    this.header = header;
    this.targetSite = targetSite;
  }

  /**
   * Gets the fileName.
   *
   * @return fileName's value.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets the header.
   *
   * @return header's value.
   */
  public String getHeader() {
    return header;
  }

  /**
   * Gets the targetSite.
   *
   * @return targetSite's value.
   */
  public String getTargetSite() {
    return targetSite;
  }
}
