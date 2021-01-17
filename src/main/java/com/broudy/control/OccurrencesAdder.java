package com.broudy.control;

import com.broudy.entity.Occurrences;

/**
 * TODO provide a summary to OccurrencesAdder class!!!!!
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public interface OccurrencesAdder {

  void increaseCount(Occurrences occurrences);

  class LeftOccurrencesAdder implements OccurrencesAdder {
    @Override
    public void increaseCount(Occurrences occurrences) {
      occurrences.increaseLeftCountByOne();
    }
  }

  class RightOccurrencesAdder implements OccurrencesAdder {
    @Override
    public void increaseCount(Occurrences occurrences) {
      occurrences.increaseRightCountByOne();
    }
  }

}
