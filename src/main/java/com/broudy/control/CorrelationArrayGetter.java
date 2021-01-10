package com.broudy.control;

import com.broudy.entity.CorrelationArrays;

/**
 * A Command DP.
 * <p>
 * Created on the 31th of December, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public interface CorrelationArrayGetter {

   int[] getCorrelationArray(CorrelationArrays correlationArrays);

  class LeftCorrelationArraysGetter implements CorrelationArrayGetter{

    @Override
    public int[] getCorrelationArray(CorrelationArrays correlationArrays) {
      return correlationArrays.getCorrelationsOnLeft();
    }
  }

  class RightCorrelationArraysGetter implements CorrelationArrayGetter{

    @Override
    public int[] getCorrelationArray(CorrelationArrays correlationArrays) {
      return correlationArrays.getCorrelationsOnRight();
    }
  }

}
