package com.broudy.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO provide a summary to ProtonavPair class!!!!!
 * <p>
 * Created on the 09th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ProtonavPair {

  private static long PAIRS_COUNT = 0;

  private final long ID;
  private final Protonav protonav;
  private final Protonav palimentary;

  private long protonavProbability;
  private long palimentaryProbability;

  private final Occurrences protonavOccurrences;
  private final Occurrences palimentaryOccurrences;

  public ProtonavPair(String protonav, String palimentary) {
    this.protonav = protonav;
    this.palimentary = palimentary;
    this.protonavOccurrences = new Occurrences();
    this.palimentaryOccurrences = new Occurrences();
    this.ID = PAIRS_COUNT++;
  }

  /**
   * Gets the ID.
   *
   * @return ID's value.
   */
  public long getID() {
    return ID;
  }

  /**
   * Gets the protonav.
   *
   * @return protonav's value.
   */
  public String getProtonav() {
    return protonav;
  }

  /**
   * Gets the palimentary.
   *
   * @return palimentary's value.
   */
  public String getPalimentary() {
    return palimentary;
  }

  /**
   * Gets the protonavProbability.
   *
   * @return protonavProbability's value.
   */
  public long getProtonavProbability() {
    return protonavProbability;
  }

  /**
   * Sets the protonavProbability.
   *
   * @param protonavProbability is the protonavProbability's new value.
   */
  public void setProtonavProbability(long protonavProbability) {
    this.protonavProbability = protonavProbability;
  }

  /**
   * Gets the palimentaryProbability.
   *
   * @return palimentaryProbability's value.
   */
  public long getPalimentaryProbability() {
    return palimentaryProbability;
  }

  /**
   * Sets the palimentaryProbability.
   *
   * @param palimentaryProbability is the palimentaryProbability's new value.
   */
  public void setPalimentaryProbability(long palimentaryProbability) {
    this.palimentaryProbability = palimentaryProbability;
  }

  /**
   * Gets the protonavOccurrences.
   *
   * @return protonavOccurrences's value.
   */
  public Occurrences getProtonavOccurrences() {
    return protonavOccurrences;
  }

  /**
   * Gets the palimentaryOccurrences.
   *
   * @return palimentaryOccurrences's value.
   */
  public Occurrences getPalimentaryOccurrences() {
    return palimentaryOccurrences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProtonavPair that = (ProtonavPair) o;
    return protonav.equals(that.protonav);
  }

  @Override
  public int hashCode() {
    return Objects.hash(protonav);
  }
}
