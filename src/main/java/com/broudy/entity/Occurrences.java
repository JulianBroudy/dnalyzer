package com.broudy.entity;

/**
 * TODO provide a summary to OccurrenceCount class!!!!!
 * <p>
 * Created on the 25th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Occurrences {

  private long leftCount;
  private long rightCount;

  public Occurrences() {
    this.leftCount = 0;
    this.rightCount = 0;
  }

  /**
   * Gets the leftCount.
   *
   * @return leftCount's value.
   */
  public long getLeftCount() {
    return leftCount;
  }

  /**
   * Gets the rightCount.
   *
   * @return rightCount's value.
   */
  public long getRightCount() {
    return rightCount;
  }

  public void increaseLeftCountByOne() {
    leftCount += 1;
  }

  public void increaseRightCountByOne() {
    rightCount += 1;
  }

  public void increaseLeftCount(long by) {
    leftCount += by;
  }

  public void increaseRightCount(long by) {
    rightCount += by;
  }

  @Override
  public String toString() {
    return leftCount + ",\t" + rightCount;
  }
}
