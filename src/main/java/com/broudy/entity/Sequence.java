package com.broudy.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a DNA sequence including its nucleotides' probabilities.
 * <p>
 * Created on the 16th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Sequence {

  private final SequenceSide side;
  private final String sequence;
  private final List<Protonav> protonavs;
  private final NucleotideProbabilities nucleotideProbabilities;

  public Sequence(SequenceSide side, String sequence) {
    this.side = side;
    this.sequence = sequence;
    this.protonavs = new ArrayList<>();
    this.nucleotideProbabilities = new NucleotideProbabilities(sequence);
  }

  /**
   * Gets the side.
   *
   * @return side's value.
   */
  public SequenceSide getSide() {
    return side;
  }

  /**
   * Gets the sequence.
   *
   * @return sequence's value.
   */
  public String getSequence() {
    return sequence;
  }

  /**
   * Gets the protonavs.
   *
   * @return protonavs's value.
   */
  public List<Protonav> getProtonavs() {
    return protonavs;
  }

  /**
   * Gets the nucleotideProbabilities.
   *
   * @return nucleotideProbabilities's value.
   */
  public NucleotideProbabilities getNucleotideProbabilities() {
    return nucleotideProbabilities;
  }

  enum SequenceSide{
    LEFT,RIGHT
  }
}
