package com.broudy.entity;

import java.math.BigInteger;

/**
 * TODO provide a summary to OccurrenceCount class!!!!!
 * <p>
 * Created on the 25th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class Occurrences {

  private BigInteger leftCount;
  private BigInteger rightCount;

  public Occurrences() {
    this.leftCount = BigInteger.ZERO;
    this.rightCount = BigInteger.ZERO;
  }

  /**
   * Gets the leftCount.
   *
   * @return leftCount's value.
   */
  public BigInteger getLeftCount() {
    return leftCount;
  }

  /**
   * Gets the rightCount.
   *
   * @return rightCount's value.
   */
  public BigInteger getRightCount() {
    return rightCount;
  }

  public void increaseLeftCountByOne(){
    leftCount = leftCount.add(BigInteger.ONE);
  }

  public void increaseRightCountByOne(){
    rightCount = rightCount.add(BigInteger.ONE);
  }

  public void increaseLeftCount(int by){
    leftCount = leftCount.add(BigInteger.valueOf(by));
  }

  public void increaseRightCount(int by){
    rightCount = rightCount.add(BigInteger.valueOf(by));
  }

  public void increaseLeftCount(long by){
    leftCount = leftCount.add(BigInteger.valueOf(by));
  }

  public void increaseRightCount(long by){
    rightCount = rightCount.add(BigInteger.valueOf(by));
  }

  @Override
  public String toString() {
    return leftCount + ",\t" + rightCount;
  }
}
