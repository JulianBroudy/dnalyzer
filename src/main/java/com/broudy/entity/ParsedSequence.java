package com.broudy.entity;

import java.util.HashMap;
import java.util.HashSet;

/**
 * TODO provide a summary to ParsedSequence class!!!!!
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ParsedSequence {

  private final String header;
  private final String sequenceBeforeTargetSite;
  private final String targetSite;
  private final String sequenceAfterTargetSite;
  private double[] nucleotidesProbabilities;
  private HashMap<String, Double> pairsProbabilities;
  private HashSet<ProtonavPair> results;


  public ParsedSequence(String header, String sequenceBeforeTargetSite, String targetSite,
      String sequenceAfterTargetSite) {
    this.header = header;
    this.sequenceBeforeTargetSite = sequenceBeforeTargetSite;
    this.targetSite = targetSite;
    this.sequenceAfterTargetSite = sequenceAfterTargetSite;
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
   * Gets the sequenceBeforeTargetSite.
   *
   * @return sequenceBeforeTargetSite's value.
   */
  public String getSequenceBeforeTargetSite() {
    return sequenceBeforeTargetSite;
  }

  /**
   * Gets the targetSite.
   *
   * @return targetSite's value.
   */
  public String getTargetSite() {
    return targetSite;
  }

  /**
   * Gets the sequenceAfterTargetSite.
   *
   * @return sequenceAfterTargetSite's value.
   */
  public String getSequenceAfterTargetSite() {
    return sequenceAfterTargetSite;
  }

  /**
   * Gets the nucleotidesCount.
   *
   * @return nucleotidesCount's value.
   */
  public double[] getNucleotidesProbabilities() {
    return nucleotidesProbabilities;
  }

  /**
   * Sets the nucleotidesCount.
   *
   * @param nucleotidesProbabilities is the nucleotidesCount's new value.
   */
  public void setNucleotidesProbabilities(double[] nucleotidesProbabilities) {
    this.nucleotidesProbabilities = nucleotidesProbabilities;
  }

  /**
   * Sets the pairsProbabilities.
   *
   * @param pairsProbabilities is the pairsProbabilities's new value.
   */
  public void setPairsProbabilities(HashMap<String, Double> pairsProbabilities) {
    this.pairsProbabilities = pairsProbabilities;
  }

  /**
   * Gets the pairsProbabilities.
   *
   * @return pairsProbabilities's value.
   */
  public HashMap<String, Double> getPairsProbabilities() {
    return pairsProbabilities;
  }

  /**
   * Gets the results.
   *
   * @return results's value.
   */
  public HashSet<ProtonavPair> getResults() {
    return results;
  }

  /**
   * Sets the results.
   *
   * @param results is the results's new value.
   */
  public void setResults(HashSet<ProtonavPair> results) {
    this.results = results;
  }

  @Override
  public String toString() {
    return "ParsedSequence:\n" + "left: "+ sequenceBeforeTargetSite + "\ntarget: "+ targetSite
        + "\nright: "+ sequenceAfterTargetSite + "\n\n";
  }


  public String toString2() {
    return "ParsedSequence:\t" + header + "\n" + sequenceBeforeTargetSite + targetSite
        + sequenceAfterTargetSite + "\n\n";
  }

}
