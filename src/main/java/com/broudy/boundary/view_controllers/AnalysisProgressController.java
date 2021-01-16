package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.control.FileParser;
import com.broudy.control.FilesManager;
import com.broudy.control.NewAnalyzer;
import com.broudy.control.StageManager;
import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.AnalysisResults;
import com.broudy.entity.CorrelationArrays;
import com.broudy.entity.ParsedSequence;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.ReadyForParsing;
import com.broudy.entity.Results;
import com.broudy.entity.Sequence;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

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

  private final FileChooser fileChooser;
  private final ExecutorService taskExecutor;

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
    this.taskExecutor = Executors.newFixedThreadPool(4);
  }

  @FXML
  void initialize() {

    // startBTN.disableProperty().bind(inProgress);

    startBTN.textProperty().bind(
        Bindings.createStringBinding(() -> inProgress.getValue() ? "Cancel" : "Start", inProgress));

    startBTN.setOnAction(click -> {
      if (inProgress.get()) {
        taskExecutor.shutdownNow();
        inProgress.set(false);
        stageManager.switchScene(FXMLView.TARGET_SITE_SELECTION);
      } else {

        inProgress.set(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        updateLog("Parsing files...");
        // Prepare a mapping from file to a list of analysis parameters
        final List<ReadyForParsing> readyForParsingList = filesManager.getReadyForParsing();
        final HashMap<File, List<AnalysisParameters>> filesToAnalysisParametersMapping = new HashMap<>();
        List<AnalysisParameters> analysisParametersList;
        for (ReadyForParsing readyForParsing : readyForParsingList) {
          final File file = readyForParsing.getFile();
          if (!filesToAnalysisParametersMapping.containsKey(file)) {
            analysisParametersList = new ArrayList<>();
            filesToAnalysisParametersMapping.put(file, analysisParametersList);
          } else {
            analysisParametersList = filesToAnalysisParametersMapping.get(file);
          }
          analysisParametersList.add(readyForParsing.getAnalysisParameters());
        }

        final int numberOfParsers = filesToAnalysisParametersMapping.size();
        final AtomicInteger numberOfFinishedThreads = new AtomicInteger();

        for (Entry<File, List<AnalysisParameters>> readyForParsing : filesToAnalysisParametersMapping
            .entrySet()) {
          final FileParser fileParser = new FileParser(readyForParsing.getKey(),
              readyForParsing.getValue());
          fileParser.setOnSucceeded(finished -> {
            filesManager.getAnalysisInformationList()
                .addAll((List<AnalysisInformation>) finished.getSource().getValue());
            numberOfFinishedThreads.getAndIncrement();
            if (numberOfFinishedThreads.get() == numberOfParsers) {
              activateAnalyzers();
            }
          });
          taskExecutor.execute(fileParser);
        }
      }
    });
  }

  private void updateLog(String s) {
    logTA.appendText(s + "\n");
  }

  private void activateAnalyzers() {
    updateLog("\nStarting analyzing files...");

    // progressBar.setProgress(0);
    final List<AnalysisInformation> analysisInformationList = filesManager
        .getAnalysisInformationList();
    final int numberOfAnalysisFiles = analysisInformationList.size();
    final AtomicInteger numberOfFinishedThreads = new AtomicInteger();

    // final ExecutorService taskExecutor = Executors.newFixedThreadPool(4);
    for (AnalysisInformation analysisInformation : analysisInformationList) {

      final NewAnalyzer analyzer = new NewAnalyzer(analysisInformation);
      analyzer.setOnSucceeded(finished -> {
        filesManager.getAnalysisResults().add((AnalysisResults) finished.getSource().getValue());
        numberOfFinishedThreads.getAndIncrement();
        if (numberOfFinishedThreads.get() == numberOfAnalysisFiles) {
          taskExecutor.shutdown();
          filesManager.getAnalysisInformationList().clear();
          stageManager.switchScene(FXMLView.RESULTS_DOWNLOAD);
        }
      });
      progressBar.progressProperty().bind(analyzer.progressProperty());
      analyzer.messageProperty().addListener((observable, oldValue, newValue) -> {
        updateLog(newValue);
      });

      taskExecutor.execute(analyzer);

    }

  }

  private void saveFile3(ParsedSequence parsedSequence) {
    URL resource = getClass().getResource("/com/broudy/Excel/FILTER_500_TEMPLATE.xlsx");
    if (resource == null) {
      throw new IllegalArgumentException("file not found!");
    } else {

      // failed if files have whitespaces or special characters
      //return new File(resource.getFile());

      try {
        XSSFWorkbook workbook = (XSSFWorkbook) XSSFWorkbookFactory
            .create(new File(resource.toURI()));
        XSSFSheet firstSheet = workbook.getSheetAt(0);
        XSSFSheet secondSheet = workbook.getSheetAt(1);

        XSSFRow row = firstSheet.getRow(0);
        XSSFCell cell = row.getCell(1);
        cell.setCellValue(parsedSequence.getHeader());
        firstSheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));
        row = firstSheet.getRow(1);
        cell = row.getCell(1);
        cell.setCellValue(parsedSequence.getTargetSite());
        firstSheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 9));

        int firstSheetRowCount = 4, secondSheetRowCount = 1;
        final List<ProtonavPair> correlations = parsedSequence.getProtonavPairsCorrelations();
        correlations.sort(new Comparator<ProtonavPair>() {
          @Override
          public int compare(ProtonavPair o1, ProtonavPair o2) {
            return o1.getExtractedProtonav().getPattern()
                .compareToIgnoreCase(o2.getExtractedProtonav().getPattern());
          }
        });

        writeCorrelations(correlations, firstSheet, firstSheetRowCount, secondSheet,
            secondSheetRowCount);

        // int[] rowsCount = writeCorrelations(parsedSequence.getLeftSequence(), firstSheetRowCount,
        //     firstSheet, secondSheetRowCount, secondSheet);
        // writeCorrelations(parsedSequence.getRightSequence(), rowsCount[0], firstSheet, rowsCount[1],
        //     secondSheet);

        fileChooser.setInitialFileName(
            parsedSequence.getFileName().replaceFirst("[.][^.]+$", "") + "_" + parsedSequence
                .getTargetSite() + "_FILTER_500" + ".xlsx");
        File outputFile = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
        try (OutputStream fileOut = new FileOutputStream(outputFile)) {
          workbook.write(fileOut);
        } catch (Exception e) {

        }

      } catch (URISyntaxException | IOException e) {
        e.printStackTrace();
      }
    }


  }

  private void writeCorrelations(List<ProtonavPair> correlations, XSSFSheet firstSheet,
      int firstSheetRowCount, XSSFSheet secondSheet, int secondSheetRowCount) {

    XSSFRow row;
    XSSFCell cell;

    long ID;

    CorrelationArrays protonavCorrelations, palimentaryCorrelations;
    // int[] protonavLeftCorrelations, protonavRightCorrelations;
    // int[] palimentaryLeftCorrelations, palimentaryRightCorrelations;
    double[] protonavLeftCorrelations, protonavRightCorrelations;
    double[] palimentaryLeftCorrelations, palimentaryRightCorrelations;

    for (ProtonavPair pair : correlations) {

      row = firstSheet.createRow(firstSheetRowCount++);

      ID = pair.getID();
      cell = row.createCell(0);
      cell.setCellValue(ID);

      cell = row.createCell(1);
      cell.setCellValue("---");

      cell = row.createCell(2);
      cell.setCellValue(pair.getExtractedProtonav().getPattern());
      cell = row.createCell(3);
      cell.setCellValue(pair.getPalimentaryProtonav().getPattern());

      protonavCorrelations = pair.getExtractedProtonav().getCorrelationArrays();
      palimentaryCorrelations = pair.getPalimentaryProtonav().getCorrelationArrays();
      // protonavLeftCorrelations = protonavCorrelations.getCorrelationsOnLeft();
      // protonavRightCorrelations = protonavCorrelations.getCorrelationsOnRight();
      // palimentaryLeftCorrelations = palimentaryCorrelations.getCorrelationsOnLeft();
      // palimentaryRightCorrelations = palimentaryCorrelations.getCorrelationsOnRight();
      protonavLeftCorrelations = protonavCorrelations.getSmoothedCorrelationsOnLeft();
      protonavRightCorrelations = protonavCorrelations.getSmoothedCorrelationsOnRight();
      palimentaryLeftCorrelations = palimentaryCorrelations.getSmoothedCorrelationsOnLeft();
      palimentaryRightCorrelations = palimentaryCorrelations.getSmoothedCorrelationsOnRight();

      // for (int index = 1; index < 98; index++) {
      for (int index = 1; index < 498; index++) {
        row = secondSheet.createRow(secondSheetRowCount++);
        cell = row.createCell(0);
        cell.setCellValue(ID);
        cell = row.createCell(1);
        cell.setCellValue(index);
        cell = row.createCell(2);
        cell.setCellValue(protonavLeftCorrelations[index]);
        cell = row.createCell(3);
        cell.setCellValue(palimentaryLeftCorrelations[index]);
        cell = row.createCell(4);
        cell.setCellValue(protonavRightCorrelations[index]);
        cell = row.createCell(5);
        cell.setCellValue(palimentaryRightCorrelations[index]);
      }

    }

  }

  private void saveFile2(ParsedSequence parsedSequence) {
    XSSFWorkbook workbook = new XSSFWorkbook();
    XSSFSheet firstSheet = workbook.createSheet();
    firstSheet.setDefaultColumnWidth(15);
    XSSFSheet secondSheet = workbook.createSheet();
    secondSheet.setDefaultColumnWidth(15);

    int firstSheetRowCount = 0, secondSheetRowCount = 0;
    XSSFRow row = firstSheet.createRow(firstSheetRowCount++);

    XSSFCell cell = row.createCell(0);
    cell.setCellValue("Header:");

    cell = row.createCell(1);
    cell.setCellValue(parsedSequence.getHeader());
    firstSheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

    row = firstSheet.createRow(firstSheetRowCount++);
    cell = row.createCell(0);
    cell.setCellValue("Target Site:");

    cell = row.createCell(1);
    cell.setCellValue(parsedSequence.getTargetSite());
    firstSheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 9));

    // Insert empty row
    firstSheetRowCount++;

    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setWrapText(true); //Set wordwrap
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

    row = firstSheet.createRow(firstSheetRowCount++);
    cell = row.createCell(0);
    cell.setCellValue("ID");
    cell = row.createCell(1);
    cell.setCellStyle(cellStyle);
    cell.setCellValue("Generated From");
    cell = row.createCell(2);
    cell.setCellValue("Protonav");
    cell = row.createCell(3);
    cell.setCellValue("Palimentary");

    row = secondSheet.createRow(secondSheetRowCount++);
    cell = row.createCell(0);
    cell.setCellValue("ID");
    cell = row.createCell(1);
    cell.setCellValue("Indices");
    cell = row.createCell(2);
    cell.setCellStyle(cellStyle);
    cell.setCellValue("Protonav Left Correlations");
    cell = row.createCell(3);
    cell.setCellStyle(cellStyle);
    cell.setCellValue("Palimentary Left Correlations");
    cell = row.createCell(4);
    cell.setCellStyle(cellStyle);
    cell.setCellValue("Protonav Right Correlations");
    cell = row.createCell(5);
    cell.setCellStyle(cellStyle);
    cell.setCellValue("Palimentary Right Correlations");

    final List<ProtonavPair> correlations = parsedSequence.getProtonavPairsCorrelations();
    correlations.sort(new Comparator<ProtonavPair>() {
      @Override
      public int compare(ProtonavPair o1, ProtonavPair o2) {
        return o1.getExtractedProtonav().getPattern()
            .compareToIgnoreCase(o2.getExtractedProtonav().getPattern());
      }
    });

    int[] rowsCount = writeCorrelations(parsedSequence.getLeftSequence(), firstSheetRowCount,
        firstSheet, secondSheetRowCount, secondSheet);
    writeCorrelations(parsedSequence.getRightSequence(), rowsCount[0], firstSheet, rowsCount[1],
        secondSheet);

    // rowCount = 4;
    // row = sheet.createRow(rowCount++);
    // cell = row.createCell(8);
    // cell.setCellValue("Indices");
    // cell = row.createCell(9);
    // cell.setCellStyle(cellStyle);
    // cell.setCellValue("Protonav Left Occurrences");
    // cell = row.createCell(10);
    // cell.setCellStyle(cellStyle);
    // cell.setCellValue("Palimentary Left Occurrences");
    // cell = row.createCell(11);
    // cell.setCellStyle(cellStyle);
    // cell.setCellValue("Protonav Right Occurrences");
    // cell = row.createCell(12);
    // cell.setCellStyle(cellStyle);
    // cell.setCellValue("Palimentary Right Occurrences");

    fileChooser.setInitialFileName(parsedSequence.getHeader().substring(1) + ".xlsx");
    File outputFile = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
    try (OutputStream fileOut = new FileOutputStream(outputFile)) {
      workbook.write(fileOut);
    } catch (Exception e) {

    }

  }

  private int[] writeCorrelations(Sequence sequence, int firstSheetRowCount, XSSFSheet firstSheet,
      int secondSheetRowCount, XSSFSheet secondSheet) {
    final String currentSide = sequence.getSide().toString();
    final List<ProtonavPair> protonavPairs = sequence.getProtonavs();

    protonavPairs.sort(new Comparator<ProtonavPair>() {
      @Override
      public int compare(ProtonavPair o1, ProtonavPair o2) {
        return o1.getExtractedProtonav().getPattern()
            .compareToIgnoreCase(o2.getExtractedProtonav().getPattern());
      }
    });

    XSSFRow row;
    XSSFCell cell;

    long ID;

    CorrelationArrays protonavCorrelations, palimentaryCorrelations;
    // int[] protonavLeftCorrelations, protonavRightCorrelations;
    // int[] palimentaryLeftCorrelations, palimentaryRightCorrelations;
    double[] protonavLeftCorrelations, protonavRightCorrelations;
    double[] palimentaryLeftCorrelations, palimentaryRightCorrelations;

    for (ProtonavPair pair : protonavPairs) {

      row = firstSheet.createRow(firstSheetRowCount++);

      ID = pair.getID();
      cell = row.createCell(0);
      cell.setCellValue(ID);

      cell = row.createCell(1);
      cell.setCellValue(currentSide);

      cell = row.createCell(2);
      cell.setCellValue(pair.getExtractedProtonav().getPattern());
      cell = row.createCell(3);
      cell.setCellValue(pair.getPalimentaryProtonav().getPattern());

      protonavCorrelations = pair.getExtractedProtonav().getCorrelationArrays();
      palimentaryCorrelations = pair.getPalimentaryProtonav().getCorrelationArrays();
      // protonavLeftCorrelations = protonavCorrelations.getCorrelationsOnLeft();
      // protonavRightCorrelations = protonavCorrelations.getCorrelationsOnRight();
      // palimentaryLeftCorrelations = palimentaryCorrelations.getCorrelationsOnLeft();
      // palimentaryRightCorrelations = palimentaryCorrelations.getCorrelationsOnRight();
      protonavLeftCorrelations = protonavCorrelations.getSmoothedCorrelationsOnLeft();
      protonavRightCorrelations = protonavCorrelations.getSmoothedCorrelationsOnRight();
      palimentaryLeftCorrelations = palimentaryCorrelations.getSmoothedCorrelationsOnLeft();
      palimentaryRightCorrelations = palimentaryCorrelations.getSmoothedCorrelationsOnRight();

      for (int index = 1; index < 98; index++) {
        row = secondSheet.createRow(secondSheetRowCount++);
        cell = row.createCell(0);
        cell.setCellValue(ID);
        cell = row.createCell(1);
        cell.setCellValue(index);
        cell = row.createCell(2);
        cell.setCellValue(protonavLeftCorrelations[index]);
        cell = row.createCell(3);
        cell.setCellValue(palimentaryLeftCorrelations[index]);
        cell = row.createCell(4);
        cell.setCellValue(protonavRightCorrelations[index]);
        cell = row.createCell(5);
        cell.setCellValue(palimentaryRightCorrelations[index]);
      }

    }

    return new int[]{firstSheetRowCount, secondSheetRowCount};
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
      writeRow(pair.getExtractedProtonav(), row);

      row = sheet.createRow(rowCount++);
      cell = row.createCell(0);
      cell.setCellValue(pair.getID());
      cell = row.createCell(1);
      cell.setCellValue(currentSide);
      cell = row.createCell(2);
      cell.setCellValue("Palimentary");
      writeRow(pair.getPalimentaryProtonav(), row);
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
