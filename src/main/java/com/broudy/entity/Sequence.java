package com.broudy.entity;

/**
 * This class represents a DNA sequence including its nucleotides' probabilities.
 * <p>
 * Created on the 16th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Sequence {

  private final String sequence;
  private final NucleotideProbabilities nucleotideProbabilities;

  public Sequence(String sequence) {
    this.sequence = sequence;
    this.nucleotideProbabilities = new NucleotideProbabilities(sequence);
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
   * Gets the nucleotideProbabilities.
   *
   * @return nucleotideProbabilities's value.
   */
  public NucleotideProbabilities getNucleotideProbabilities() {
    return nucleotideProbabilities;
  }
}
