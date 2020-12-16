package com.broudy.entity;

import java.util.HashSet;

/**
 * This class represents a parsed sequence file which contains the sequenceUnderTest header, target
 * site and left+right sequences.
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ParsedSequence {

  private final String header;
  private final String targetSite;
  private final Sequence leftSequence;
  private final Sequence rightSequence;
  private HashSet<ProtonavPair> results;


  public ParsedSequence(String header, String sequenceBeforeTargetSite, String targetSite,
      String sequenceAfterTargetSite) {
    this.header = header;
    this.targetSite = targetSite;
    this.leftSequence = new Sequence(sequenceBeforeTargetSite);
    this.rightSequence = new Sequence(sequenceAfterTargetSite);
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
    return "ParsedSequence:\n" + "left: " + leftSequence + "\ntarget: " + targetSite + "\nright: "
        + rightSequence + "\n\n";
  }


  public String toString2() {
    return "ParsedSequence:\t" + header + "\n" + leftSequence + targetSite + rightSequence + "\n\n";
  }

}
