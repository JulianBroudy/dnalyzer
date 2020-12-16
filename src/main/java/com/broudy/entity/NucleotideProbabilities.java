package com.broudy.entity;

import java.util.HashMap;

/**
 * A class that holds probabilities by single nucleotides and nucleotide pairs.
 * <p>
 * Created on the 16th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class NucleotideProbabilities {

  private double[] nucleotidesProbabilities;
  private HashMap<String, Double> pairsProbabilities;

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




}
