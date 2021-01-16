package com.broudy.control;

import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.FileMetadata;
import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.Sequence;
import com.broudy.entity.Sequence.SequenceSide;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to FileParser class!!!!!
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class FileParser extends Task<List<AnalysisInformation>> {

  private static final Logger LOGGER = LogManager.getLogger(FileParser.class);

  private final File file;
  private final List<AnalysisParameters> analysisParametersList;

  public FileParser(File file, List<AnalysisParameters> analysisParametersList) {
    this.file = file;
    this.analysisParametersList = analysisParametersList;
  }

  @Override
  protected List<AnalysisInformation> call() throws Exception {
    final String fileName = file.getName().replaceFirst("[.][^.]+$", "");

    updateMessage("Reading file \"" + fileName + "\"...");

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
        } else {
          sequence.append(aLine);
        }
        updateMessage(header);
        numberOfReadBytes += aLine.getBytes().length;
        updateProgress(numberOfReadBytes, totalNumberOfBytes);

        while ((aLine = bufferedReader.readLine()) != null) {
          sequence.append(aLine);
          numberOfReadBytes += aLine.getBytes().length;
          updateProgress(numberOfReadBytes, totalNumberOfBytes);
        }
      }
      updateProgress(totalNumberOfBytes, totalNumberOfBytes);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    final List<AnalysisInformation> analysisInformationList = new ArrayList<>();

    updateMessage("Preparing files for analysis:");
    for (AnalysisParameters analysisParameters : analysisParametersList) {

      int leftSequenceLength = analysisParameters.getTargetSiteStartIndex() - 1;
      int rightSequenceLength = sequence.length() - analysisParameters.getTargetSiteEndIndex();
      int lengthExcludingTargetSite = leftSequenceLength + rightSequenceLength;
      int halfLength = lengthExcludingTargetSite / 2;

      final String targetSite = sequence
          .substring(leftSequenceLength, analysisParameters.getTargetSiteEndIndex());
      final FileMetadata metadata = new FileMetadata(fileName, header, targetSite);

      final String leftSequenceString, rightSequenceString;

      if (analysisParameters.isCyclic()) {
        if (leftSequenceLength > rightSequenceLength) {
          leftSequenceString = sequence
              .substring(leftSequenceLength - halfLength, leftSequenceLength);
          rightSequenceString = sequence.substring(analysisParameters.getTargetSiteEndIndex())
              .concat(sequence.substring(0, leftSequenceLength - halfLength));
        } else {
          leftSequenceString = sequence
              .substring(sequence.length() - (halfLength - leftSequenceLength))
              .concat(sequence.substring(0, leftSequenceLength));
          rightSequenceString = sequence.substring(analysisParameters.getTargetSiteEndIndex(),
              sequence.length() - (halfLength - leftSequenceLength));
        }
      } else {
        leftSequenceString = sequence.substring(0, leftSequenceLength);
        rightSequenceString = sequence.substring(analysisParameters.getTargetSiteEndIndex() + 1);
      }

      updateMessage("Calculating nucleotides' probabilities in DNA sequence...");
      final Sequence leftSequence, rightSequence;
      leftSequence = new Sequence(SequenceSide.LEFT, leftSequenceString);
      rightSequence = new Sequence(SequenceSide.RIGHT, rightSequenceString);

      analysisInformationList
          .add(new AnalysisInformation(metadata, leftSequence, rightSequence, analysisParameters));

    }

    return analysisInformationList;
  }


}
