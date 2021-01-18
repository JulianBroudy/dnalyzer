package com.broudy.control;

import java.util.regex.Pattern;
import junit.framework.TestCase;

public class PatternCounterTest extends TestCase {

  private PatternCounter patternCounter;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    patternCounter = new PatternCounter();
  }

  public void testCountOccurrences() {
    long expectedResult = 3;
    long actualResult = patternCounter.countOccurrences("C","CTCAC");
    assertEquals(expectedResult,actualResult);

    expectedResult = 1;
    actualResult = patternCounter.countOccurrences("A","CTCAC");
    assertEquals(expectedResult,actualResult);

    expectedResult = 1;
    actualResult = patternCounter.countOccurrences("CT","CTCAC");
    assertEquals(expectedResult,actualResult);
  }

}