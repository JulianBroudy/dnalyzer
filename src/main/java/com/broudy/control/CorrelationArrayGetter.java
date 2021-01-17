package com.broudy.control;

import com.broudy.entity.AutocorrelationArrays;

/**
 * A Command DP.
 * <p>
 * Created on the 31th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public interface CorrelationArrayGetter {

   int[] getCorrelationArray(AutocorrelationArrays autocorrelationArrays);

  class LeftCorrelationArraysGetter implements CorrelationArrayGetter{

    @Override
    public int[] getCorrelationArray(AutocorrelationArrays autocorrelationArrays) {
      return autocorrelationArrays.getCorrelationsOnLeft();
    }
  }

  class RightCorrelationArraysGetter implements CorrelationArrayGetter{

    @Override
    public int[] getCorrelationArray(AutocorrelationArrays autocorrelationArrays) {
      return autocorrelationArrays.getCorrelationsOnRight();
    }
  }

}
