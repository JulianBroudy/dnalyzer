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
  private Protonav protonav;
  private Protonav palimentary;

  public ProtonavPair() {
    this.ID = PAIRS_COUNT++;
  }

  public ProtonavPair(Protonav protonav, Protonav palimentary) {
    this.protonav = protonav;
    this.palimentary = palimentary;
    this.ID = PAIRS_COUNT++;
  }
  public ProtonavPair(long ID, Protonav protonav, Protonav palimentary) {
    this.protonav = protonav;
    this.palimentary = palimentary;
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
  public Protonav getProtonav() {
    return protonav;
  }

  /**
   * Gets the palimentary.
   *
   * @return palimentary's value.
   */
  public Protonav getPalimentary() {
    return palimentary;
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
