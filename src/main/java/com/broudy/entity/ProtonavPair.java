package com.broudy.entity;

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
  private Protonav extractedProtonav;
  private Protonav palimentaryProtonav;

  public ProtonavPair() {
    this.ID = PAIRS_COUNT++;
  }

  /**
   * Sets the PAIRS_COUNT.
   *
   * @param PAIRS_COUNT is the PAIRS_COUNT's new value.
   */
  public static void setPairsCount(long pairsCount) {
    PAIRS_COUNT = pairsCount;
  }

  public ProtonavPair(Protonav extractedProtonav, Protonav palimentaryProtonav) {
    this.extractedProtonav = extractedProtonav;
    this.palimentaryProtonav = palimentaryProtonav;
    this.ID = PAIRS_COUNT++;
  }
  public ProtonavPair(long ID, Protonav extractedProtonav, Protonav palimentaryProtonav) {
    this.extractedProtonav = extractedProtonav;
    this.palimentaryProtonav = palimentaryProtonav;
    this.ID = ID;
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
  public Protonav getExtractedProtonav() {
    return extractedProtonav;
  }

  /**
   * Gets the palimentary.
   *
   * @return palimentary's value.
   */
  public Protonav getPalimentaryProtonav() {
    return palimentaryProtonav;
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
    return extractedProtonav.equals(that.extractedProtonav);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extractedProtonav);
  }
}
