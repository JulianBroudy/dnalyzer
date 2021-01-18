package com.broudy.entity;

import java.util.List;

/**
 * A class to encapsulate analysis results.
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class AnalysisResults {

  private final AnalysisInformation analysisInformation;
  private final List<ProtonavPair> protonavPairs;
  private final List<ProtonavPair> filteredProtonavPairs;


  public AnalysisResults(AnalysisInformation analysisInformation, List<ProtonavPair> protonavPairs,
      List<ProtonavPair> filteredProtonavPairs) {
    this.analysisInformation = analysisInformation;
    this.protonavPairs = protonavPairs;
    this.filteredProtonavPairs = filteredProtonavPairs;
  }

  /**
   * Gets the analysisInformation.
   *
   * @return analysisInformation's value.
   */
  public AnalysisInformation getAnalysisInformation() {
    return analysisInformation;
  }

  /**
   * Gets the protonavPairs.
   *
   * @return protonavPairs's value.
   */
  public List<ProtonavPair> getProtonavPairs() {
    return protonavPairs;
  }

  /**
   * Gets the filteredProtonavPairs.
   *
   * @return filteredProtonavPairs's value.
   */
  public List<ProtonavPair> getFilteredProtonavPairs() {
    return filteredProtonavPairs;
  }
}
