package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.control.FilesManager;
import com.broudy.control.StageManager;
import com.broudy.entity.Analyzer;
import com.broudy.entity.ParsedSequence;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.Sequence;
import com.broudy.entity.SequenceParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    startBTN.disableProperty().bind(inProgress);

    startBTN.setOnAction(click -> {
      // Parse files
      final List<ParsedSequence> parsedSequences = new ArrayList<>();
      for (Sequence sequence : filesManager.getMatchedSequences()) {
        final SequenceParser parser = new SequenceParser(sequence);
        parser.setOnSucceeded(succeeded -> {
          parsedSequences.add((ParsedSequence) succeeded.getSource().getValue());
          final Analyzer analyzer = new Analyzer((ParsedSequence) succeeded.getSource().getValue());
          analyzer.setOnSucceeded(done -> {
            inProgress.set(false);
            saveFile((ParsedSequence) done.getSource().getValue());
            stageManager.switchScene(FXMLView.MAIN_SCREEN);
          });
          try {
            Thread thread = new Thread(analyzer);
            thread.setDaemon(true);
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(analyzer.progressProperty());
            thread.start();
          }catch (Exception e){
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
    });
  }

  private void saveFile(ParsedSequence parsedSequence) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet sheet = workbook.createSheet();

    // for (HashMap<String, Occurrences> patterns : wildPatterns.values()) {
    //   patterns.entrySet().stream().sorted(Map.Entry.comparingByKey())
    //       .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    // }

    HashSet<ProtonavPair> results = parsedSequence.getResults();
    int rowCount = 0;
    XSSFRow row = sheet.createRow(rowCount++);
    XSSFCell cell = row.createCell(0);
    cell.setCellValue("ID");
    cell = row.createCell(1);
    cell.setCellValue("Protonav");
    cell = row.createCell(2);
    cell.setCellValue("Left Occurrences");
    cell = row.createCell(3);
    cell.setCellValue("Right Occurrences");
    cell = row.createCell(4);
    cell.setCellValue("Left Probability By Nucleotide");
    cell = row.createCell(5);
    cell.setCellValue("Right Probability By Nucleotide");
    cell = row.createCell(6);
    cell.setCellValue("Left Probability By Pairs");
    cell = row.createCell(7);
    cell.setCellValue("Right Probability By Pairs");

    for (ProtonavPair pair : results) {
      row = sheet.createRow(rowCount++);
      cell = row.createCell(0);
      cell.setCellValue(pair.getID());
      writeRow(pair.getProtonav(), row);

      row = sheet.createRow(rowCount++);
      cell = row.createCell(0);
      cell.setCellValue(pair.getID());
      writeRow(pair.getPalimentary(), row);
    }

    fileChooser.setInitialFileName(parsedSequence.getHeader().substring(1) + ".xlsx");
    File outputFile = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
    try (OutputStream fileOut = new FileOutputStream(outputFile)) {
      workbook.write(fileOut);
    } catch (Exception e) {

    }

  }

  private void writeRow(Protonav protonav, XSSFRow row) {
    XSSFCell cell = row.createCell(1);
    cell.setCellValue(protonav.getPattern());

    cell = row.createCell(2);
    cell.setCellValue(protonav.getOccurrences().getLeftCount());

    cell = row.createCell(3);
    cell.setCellValue(protonav.getOccurrences().getRightCount());

    cell = row.createCell(4);
    cell.setCellValue(protonav.getOccurrences().getLeftCount()/protonav.getProbability());

    cell = row.createCell(5);
    cell.setCellValue(protonav.getOccurrences().getRightCount()/protonav.getProbability());

    cell = row.createCell(6);
    cell.setCellValue(protonav.getOccurrences().getLeftCount()/protonav.getPairsProbability());

    cell = row.createCell(7);
    cell.setCellValue(protonav.getOccurrences().getRightCount()/protonav.getPairsProbability());

  }


}
