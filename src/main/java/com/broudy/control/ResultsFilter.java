package com.broudy.control;

/**
 * TODO provide a summary to Filter class!!!!!
 * <p>
 * Created on the 22nd of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public interface ResultsFilter {

  boolean isGood(double onLeft, double onRight);

  class MoreOnLeftFilter implements ResultsFilter {

    @Override
    public boolean isGood(double onLeft, double onRight) {
      return onLeft > onRight && (int) onLeft != (int) onRight;
    }

  }

  class MoreOnRightFilter implements ResultsFilter {

    @Override
    public boolean isGood(double onLeft, double onRight) {
      return onRight > onLeft && (int) onLeft != (int) onRight;
    }

  }


}
