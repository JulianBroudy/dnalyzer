package com.broudy.entity;

/**
 * A class that encapsulates all of the information needed for analysis.
 * <p>
 * Created on the 15th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class AnalysisInformation {

  private final FileMetadata metadata;

  private final Sequence leftSequence;
  private final Sequence rightSequence;

  private final AnalysisParameters analysisParameters;

  public AnalysisInformation(FileMetadata metadata, Sequence leftSequence, Sequence rightSequence,
      AnalysisParameters analysisParameters) {
    this.metadata = metadata;
    this.leftSequence = leftSequence;
    this.rightSequence = rightSequence;
    this.analysisParameters = analysisParameters;
  }

  /**
   * Gets the metadata.
   *
   * @return metadata's value.
   */
  public FileMetadata getMetadata() {
    return metadata;
  }

  /**
   * Gets the leftSequence.
   *
   * @return leftSequence's value.
   */
  public Sequence getLeftSequence() {
    return leftSequence;
  }

  /**
   * Gets the rightSequence.
   *
   * @return rightSequence's value.
   */
  public Sequence getRightSequence() {
    return rightSequence;
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
