package com.broudy.control;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to extract possible patterns from DNA sequences.
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class PatternCounter {


  public long prepareAndCountOccurrences(String pattern, String sequence) {
    return countOccurrences(preparePatternForCount(pattern), sequence);
  }

  public long countOccurrences(String pattern, String sequence) {
    final Pattern p = Pattern.compile(pattern);
    final Matcher m = p.matcher(sequence);
    long numberOfOccurrences = 0;
    while (m.find()) {
      final int groupCount = m.groupCount();
      for (int i = 0; i <= groupCount; i++) {
        if (!m.group(i).isEmpty()) {
          numberOfOccurrences++;
        }
      }
    }
    return numberOfOccurrences;
  }

  private String preparePatternForCount(String pattern) {
    final StringBuilder patternBuilder = new StringBuilder("");

    for (char ch : pattern.toCharArray()) {
      switch (ch) {
        case 'N':
          patternBuilder.append("[ACGT]");
          break;
        case 'W':
          patternBuilder.append("[AT]");
          break;
        case 'S':
          patternBuilder.append("[CG]");
          break;
        case 'R':
          patternBuilder.append("[AG]");
          break;
        case 'Y':
          patternBuilder.append("[CT]");
          break;
        case 'B':
          patternBuilder.append("[^A]");
          break;
        case 'D':
          patternBuilder.append("[^C]");
          break;
        case 'H':
          patternBuilder.append("[^G]");
          break;
        case 'V':
          patternBuilder.append("[^T]");
          break;
        default:
          patternBuilder.append(ch);
      }
    }

    return patternBuilder.toString();
  }

  public long updateCorrelationArray(String pattern, String sequence, int[] array) {
    final Pattern p = Pattern.compile(pattern);
    final Matcher m = p.matcher(sequence);
    long numberOfOccurrences = 0;
    while (m.find()) {
      final int groupCount = m.groupCount();
      for (int i = 0; i <= groupCount; i++) {
        if (!m.group(i).isEmpty()) {
          array[m.start()]++;
        }
      }
    }
    return numberOfOccurrences;
  }


}
