package com.broudy.entity;

import com.broudy.entity.Sequence.SequenceSide;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a parsed sequence file which contains the sequenceUnderTest header, target
 * site and left+right sequences.
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ParsedSequence {

  private final String fileName;
  private final String header;
  private final String targetSite;
  private final Sequence leftSequence;
  private final Sequence rightSequence;
  private final int minPatternLength, maxPatternLength;
  private final int filterSize;
  private final List<ProtonavPair> protonavPairsCorrelations;


  public ParsedSequence(String fileName, String header, String sequenceBeforeTargetSite, String targetSite,
      String sequenceAfterTargetSite, int minPatternLength, int maxPatternLength, int filterSize) {
    this.fileName = fileName;
    this.header = header;
    this.targetSite = targetSite;
    this.leftSequence = new Sequence(SequenceSide.LEFT, sequenceBeforeTargetSite);
    this.rightSequence = new Sequence(SequenceSide.RIGHT, sequenceAfterTargetSite);
    this.minPatternLength = minPatternLength;
    this.maxPatternLength = maxPatternLength;
    this.filterSize = filterSize;
    this.protonavPairsCorrelations = new ArrayList<>();
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
   * Gets the protonavPairsCorrelations.
   *
   * @return protonavPairsCorrelations's value.
   */
  public List<ProtonavPair> getProtonavPairsCorrelations() {
    return protonavPairsCorrelations;
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
