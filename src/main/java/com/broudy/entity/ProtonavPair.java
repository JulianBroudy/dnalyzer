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

  private final long ID;
  private Protonav extractedProtonav;
  private Protonav palimentaryProtonav;

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
