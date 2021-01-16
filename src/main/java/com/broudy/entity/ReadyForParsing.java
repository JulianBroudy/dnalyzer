package com.broudy.entity;

import java.io.File;

/**
 * A helper class to pair each file with its analysis parameters in order to allow removing files
 * from the ready queue in the Target Site(s) screen.
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ReadyForParsing {

  private final File file;
  private final AnalysisParameters analysisParameters;

  public ReadyForParsing(File file, AnalysisParameters analysisParameters) {
    this.file = file;
    this.analysisParameters = analysisParameters;
  }

  /**
   * Gets the file.
   *
   * @return file's value.
   */
  public File getFile() {
    return file;
  }

  /**
   * Gets the analysisParameters.
   *
   * @return analysisParameters's value.
   */
  public AnalysisParameters getAnalysisParameters() {
    return analysisParameters;
  }
}
