package com.broudy.entity;

/**
 * TODO provide a summary to CorrelationArrays class!!!!!
 * <p>
 * Created on the 31th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class CorrelationArrays {

  private final int[] correlationsOnLeft;
  private final int[] correlationsOnRight;
  private final double[] smoothedCorrelationsOnLeft;
  private final double[] smoothedCorrelationsOnRight;

  public CorrelationArrays() {
    this.correlationsOnLeft = new int[500];
    this.correlationsOnRight= new int[500];
    this.smoothedCorrelationsOnLeft = new double[499];
    this.smoothedCorrelationsOnRight = new double[499];
    // this.correlationsOnLeft = new int[100];
    // this.correlationsOnRight= new int[100];
    // this.smoothedCorrelationsOnLeft = new double[99];
    // this.smoothedCorrelationsOnRight = new double[99];
  }

  /**
   * Gets the correlationsOnLeft.
   *
   * @return correlationsOnLeft's value.
   */
  public int[] getCorrelationsOnLeft() {
    return correlationsOnLeft;
  }

  /**
   * Gets the correlationsOnRight.
   *
   * @return correlationsOnRight's value.
   */
  public int[] getCorrelationsOnRight() {
    return correlationsOnRight;
  }

  /**
   * Gets the smoothedCorrelationsOnLeft.
   *
   * @return smoothedCorrelationsOnLeft's value.
   */
  public double[] getSmoothedCorrelationsOnLeft() {
    return smoothedCorrelationsOnLeft;
  }

  /**
   * Gets the smoothedCorrelationsOnRight.
   *
   * @return smoothedCorrelationsOnRight's value.
   */
  public double[] getSmoothedCorrelationsOnRight() {
    return smoothedCorrelationsOnRight;
  }

  public void increaseCorrelationsOnLeftAt(int index){
    correlationsOnLeft[index]++;
  }

  public void increaseCorrelationsOnRightAt(int index){
    correlationsOnRight[index]++;
  }

}
