package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.control.Analyzer;
import com.broudy.control.FilesManager;
import com.broudy.control.SequenceParser;
import com.broudy.control.StageManager;
import com.broudy.entity.ParsedSequence;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.Results;
import com.broudy.entity.Sequence;
import com.broudy.entity.SequenceToBeParsed;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * TODO provide a summary to AnalysisProgressController class!!!!!
 * <p>
 * Created on the 24th of November, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class AnalysisProgressController {

  private static final Logger LOGGER = LogManager.getLogger(AnalysisProgressController.class);
  private final FilesManager filesManager;
  private final BooleanProperty inProgress;
  private final StageManager stageManager;
  private FileChooser fileChooser;

  @FXML
  private ProgressBar progressBar;

  @FXML
  private TextArea logTA;

  @FXML
  private Button startBTN;


  public AnalysisProgressController() {
    stageManager = StageManager.getStageManager();
    filesManager = FilesManager.getFilesManager();
    inProgress = new SimpleBooleanProperty(false);
    fileChooser = new FileChooser();
    fileChooser.getExtensionFilters()
        .addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
  }

  @FXML
  void initialize() {

    // startBTN.disableProperty().bind(inProgress);

    startBTN.textProperty().bind(
        Bindings.createStringBinding(() -> inProgress.getValue() ? "Cancel" : "Start", inProgress));

    startBTN.setOnAction(click -> {
      if (inProgress.getValue()) {
        System.out.println("Cancelling...");
      } else {
        // Parse files
        final List<ParsedSequence> parsedSequences = new ArrayList<>();
        for (SequenceToBeParsed sequenceToBeParsed : filesManager.getMatchedSequences()) {
          final SequenceParser parser = new SequenceParser(sequenceToBeParsed);
          parser.setOnSucceeded(succeeded -> {
            parsedSequences.add((ParsedSequence) succeeded.getSource().getValue());
            final Analyzer analyzer = new Analyzer(
                (ParsedSequence) succeeded.getSource().getValue());
            analyzer.setOnSucceeded(done -> {
              inProgress.set(false);
              saveFile((ParsedSequence) done.getSource().getValue());
              stageManager.switchScene(FXMLView.MAIN_SCREEN);
            });

            analyzer.messageProperty().addListener((observable, oldValue, newValue) -> {
              logTA.appendText(newValue.concat("\n"));
            });

            try {
              Thread thread = new Thread(analyzer);
              thread.setDaemon(true);
              progressBar.progressProperty().unbind();
              progressBar.progressProperty().bind(analyzer.progressProperty());
              thread.start();
            } catch (Exception e) {
              System.out.println(e.getStackTrace());
            }
          });
          Thread thread = new Thread(parser);
          thread.setDaemon(true);
          progressBar.progressProperty().bind(parser.progressProperty());
          inProgress.set(true);
          thread.start();
        }
        // Analyze sequences
        // Show results
      }
    });
  }

  private void saveFile(ParsedSequence parsedSequence) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet();

    int rowCount = 0;
    XSSFRow row = sheet.createRow(rowCount++);

    XSSFCell cell = row.createCell(0);
    cell.setCellValue("Header:");

    cell = row.createCell(1);
    cell.setCellValue(parsedSequence.getHeader());
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

    row = sheet.createRow(rowCount++);
    cell = row.createCell(0);
    cell.setCellValue("Target Site:");

    cell = row.createCell(1);
    cell.setCellValue(parsedSequence.getTargetSite());
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 9));

    // Insert empty row
    rowCount++;
    row = sheet.createRow(rowCount++);

    cell = row.createCell(0);
    cell.setCellValue("ID");
    cell = row.createCell(1);
    cell.setCellValue("Generated From");
    cell = row.createCell(2);
    cell.setCellValue("Type");
    cell = row.createCell(3);
    cell.setCellValue("Pattern");
    cell = row.createCell(4);
    cell.setCellValue("Left Occurrences");
    cell = row.createCell(5);
    cell.setCellValue("Right Occurrences");
    cell = row.createCell(6);
    cell.setCellValue("Left Probability By Nucleotide");
    cell = row.createCell(7);
    cell.setCellValue("Right Probability By Nucleotide");
    cell = row.createCell(8);
    cell.setCellValue("Left Probability By Pairs");
    cell = row.createCell(9);
    cell.setCellValue("Right Probability By Pairs");

    rowCount = writeSequence(parsedSequence.getLeftSequence(), rowCount, sheet);
    writeSequence(parsedSequence.getRightSequence(), rowCount, sheet);

    fileChooser.setInitialFileName(parsedSequence.getHeader().substring(1) + ".xlsx");
    File outputFile = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
    try (OutputStream fileOut = new FileOutputStream(outputFile)) {
      workbook.write(fileOut);
    } catch (Exception e) {

    }

  }

  private int writeSequence(Sequence sequence, int rowCount, XSSFSheet sheet) {

    final String currentSide = sequence.getSide().toString();
    final List<ProtonavPair> protonavPairs = sequence.getProtonavs();

    XSSFRow row;
    XSSFCell cell;

    for (ProtonavPair pair : protonavPairs) {
      row = sheet.createRow(rowCount++);
      cell = row.createCell(0);
      cell.setCellValue(pair.getID());
      cell = row.createCell(1);
      cell.setCellValue(currentSide);
      cell = row.createCell(2);
      cell.setCellValue("Protonav");
      writeRow(pair.getProtonav(), row);

      row = sheet.createRow(rowCount++);
      cell = row.createCell(0);
      cell.setCellValue(pair.getID());
      cell = row.createCell(1);
      cell.setCellValue(currentSide);
      cell = row.createCell(2);
      cell.setCellValue("Palimentary");
      writeRow(pair.getPalimentary(), row);
    }

    return rowCount;
  }

  private void writeRow(Protonav protonav, XSSFRow row) {
    XSSFCell cell = row.createCell(3);
    cell.setCellValue(protonav.getPattern());

    final Results results = protonav.getResults();

    cell = row.createCell(4);
    cell.setCellValue(results.getLeftCount());

    cell = row.createCell(5);
    cell.setCellValue(results.getRightCount());

    cell = row.createCell(6);
    cell.setCellValue(results.getLeftProbabilityBySingles());

    cell = row.createCell(7);
    cell.setCellValue(results.getRightProbabilityBySingles());

    cell = row.createCell(8);
    cell.setCellValue(results.getLeftProbabilityByPairs());

    cell = row.createCell(9);
    cell.setCellValue(results.getRightProbabilityByPairs());

  }


}
