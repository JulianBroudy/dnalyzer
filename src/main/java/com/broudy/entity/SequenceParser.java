package com.broudy.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to SequenceParser class!!!!!
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class SequenceParser extends Task<ParsedSequence> {

  private static final Logger LOGGER = LogManager.getLogger(SequenceParser.class);

  private final Sequence sequenceToBeParsed;

  public SequenceParser(Sequence sequenceToBeParsed) {
    this.sequenceToBeParsed = sequenceToBeParsed;
  }

  @Override
  protected ParsedSequence call() throws Exception {
    final File file = sequenceToBeParsed.getDnaSequence();
    String header = "No Header";
    final StringBuilder sequence = new StringBuilder("");
    final long totalNumberOfBytes = Files.size(Paths.get(file.getPath())) + 2;
    long numberOfReadBytes = 0;
    updateProgress(numberOfReadBytes, totalNumberOfBytes);
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

      String aLine = bufferedReader.readLine();
      if (aLine != null) {
        if (aLine.startsWith(">") || aLine.startsWith(";")) {
          header = aLine;
          numberOfReadBytes += aLine.getBytes().length;
        } else {
          header = "No Header";
          sequence.append(aLine);
          numberOfReadBytes += aLine.getBytes().length;
          updateProgress(numberOfReadBytes, totalNumberOfBytes);
        }
        while ((aLine = bufferedReader.readLine()) != null) {
          sequence.append(aLine);
          numberOfReadBytes += aLine.getBytes().length;
          updateProgress(numberOfReadBytes, totalNumberOfBytes);
        }

      }
      updateMessage(header);
      updateProgress(numberOfReadBytes, totalNumberOfBytes);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    updateTitle("Finishing up...");
    final ParsedSequence parsedSequence;

    if (sequenceToBeParsed.isCyclic()) {
      final int lLength = sequenceToBeParsed.getStartIndex() - 1;
      final int rLength = sequence.length() - sequenceToBeParsed.getEndIndex();
      System.out.println("String len: " + sequence.length());
      final int lengthWithoutTarget = lLength + rLength;
      final int halfTheSum = lengthWithoutTarget / 2;
      System.out.println(
          "lLength: " + lLength + "\trLength: " + rLength + "\twithout: " + lengthWithoutTarget
              + "\thalfSum: " + halfTheSum);
      if (lLength > rLength) {
        parsedSequence = new ParsedSequence(header,
            sequence.substring((lLength - halfTheSum), sequenceToBeParsed.getStartIndex() - 1),
            sequence.substring(sequenceToBeParsed.getStartIndex() - 1,
                sequenceToBeParsed.getEndIndex()),
            sequence.substring(sequenceToBeParsed.getEndIndex())
                .concat(sequence.substring(0, lLength - halfTheSum)));
      } else {
        parsedSequence = new ParsedSequence(header,
            sequence.substring(sequence.length() - (halfTheSum - lLength))
                .concat(sequence.substring(0, sequenceToBeParsed.getStartIndex() - 1)), sequence
            .substring(sequenceToBeParsed.getStartIndex() - 1, sequenceToBeParsed.getEndIndex()),
            sequence.substring(sequenceToBeParsed.getEndIndex(),
                sequence.length() - (halfTheSum - lLength)));
      }
    } else {
      parsedSequence = new ParsedSequence(header,
          sequence.substring(0, sequenceToBeParsed.getStartIndex() - 1), sequence
          .substring(sequenceToBeParsed.getStartIndex() - 1, sequenceToBeParsed.getEndIndex()),
          sequence.substring(sequenceToBeParsed.getEndIndex() + 1));
    }
    System.out.println("Target Site:\t" + parsedSequence.getTargetSite());
    // System.out.println(
    //     "Start: " + sequenceToBeParsed.getStartIndex() + "\tEnd: " + sequenceToBeParsed
    //         .getEndIndex());
    System.out.println("Before: "+parsedSequence.getSequenceBeforeTargetSite().length()
    +"\tAfter: "+parsedSequence.getSequenceAfterTargetSite().length());
    updateProgress(1, 1);
    updateTitle("Done");
    return LOGGER.traceExit("Parsed sequence: {}", parsedSequence);
  }
}
