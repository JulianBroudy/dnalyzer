package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.boundary.RenderingsStyler;
import com.broudy.control.FilesManager;
import com.broudy.control.StageManager;
import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.AnalysisResults;
import com.broudy.entity.AutocorrelationArrays;
import com.broudy.entity.FileMetadata;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.ProtonavProbabilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Controller class for reports downloading screen.
 * <p>
 * Created on the 16th of January, 2021.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class ResultsDownloadController {

  private static final Logger LOGGER = LogManager.getLogger(ResultsDownloadController.class);
  private final FilesManager filesManager;
  private final BooleanProperty inProgress;
  private final StageManager stageManager;
  private final FileChooser fileChooser;
  private final DirectoryChooser directoryChooser;


  @FXML
  private ListView<AnalysisResults> resultsLV;

  @FXML
  private Button downloadSelectedBTN;

  @FXML
  private Button downloadAllBTN;

  @FXML
  private Button anotherSetBTN;


  public ResultsDownloadController() {
    stageManager = StageManager.getStageManager();
    filesManager = FilesManager.getFilesManager();
    inProgress = new SimpleBooleanProperty(false);
    fileChooser = new FileChooser();
    fileChooser.getExtensionFilters()
        .addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
    directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("DNAnalyzer Results");
  }


  @FXML
  void initialize() {

    resultsLV.itemsProperty().bindBidirectional(filesManager.analysisResultsProperty());
    resultsLV.setCellFactory(RenderingsStyler::callAnalysisResultsLV);
    downloadSelectedBTN.disableProperty()
        .bind(resultsLV.getSelectionModel().selectedItemProperty().isNull());

    downloadSelectedBTN.setOnAction(click -> {
      final File fileDir = directoryChooser.showDialog(stageManager.getPrimaryStage());
      downloadReportFor(resultsLV.getSelectionModel().getSelectedItem(), fileDir);
    });

    downloadAllBTN.setOnAction(click -> {
      final File fileDir = directoryChooser.showDialog(stageManager.getPrimaryStage());
      final List<AnalysisResults> analysisResultsList = filesManager.getAnalysisResults();
      for (AnalysisResults analysisResults : analysisResultsList) {
        downloadReportFor(analysisResults, fileDir);
      }
    });

    anotherSetBTN.setOnAction(click -> {
      filesManager.clearFiles();
      stageManager.switchScene(FXMLView.MAIN_SCREEN);
    });

  }

  private void downloadReportFor(AnalysisResults analysisResults, File fileDir) {
    System.out.println(fileDir);
    System.out.println(fileDir.toPath());
    AnalysisInformation info = analysisResults.getAnalysisInformation();
    FileMetadata metadata = info.getMetadata();
    AnalysisParameters parameters = info.getAnalysisParameters();
    final int windowSize = parameters.getWindowSize();
    URL resource = getClass()
        .getResource("/com/broudy/Excel/FILTER_" + windowSize + "_TEMPLATE.xlsm");
    if (resource == null) {
      throw new IllegalArgumentException("file not found!");
    } else {

      XSSFWorkbook workbook = null;
      try {
        workbook = new XSSFWorkbook(OPCPackage.open(new File(resource.toURI())));

      } catch (IOException e) {
        e.printStackTrace();
      } catch (InvalidFormatException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
      XSSFSheet firstSheet = workbook.getSheetAt(0);
      XSSFSheet secondSheet = workbook.getSheetAt(1);
      XSSFSheet fourthSheet = workbook.getSheetAt(3);

      XSSFRow row = firstSheet.getRow(0);
      XSSFCell cell = row.getCell(1);
      cell.setCellValue(metadata.getHeader());
      firstSheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));
      row = firstSheet.getRow(1);
      cell = row.getCell(1);
      cell.setCellValue(metadata.getTargetSite());
      firstSheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 9));

      int firstSheetRowCount = 4, secondSheetRowCount = 1;
      final List<ProtonavPair> correlations = analysisResults.getProtonavPairs();
      correlations.sort((o1, o2) -> (int) (o1.getID() - o2.getID()));

      writeCorrelations(correlations, firstSheet, firstSheetRowCount, secondSheet,
          secondSheetRowCount, parameters.getWindowSize() - 2);
      writeOccurrences(fourthSheet, analysisResults.getFilteredProtonavPairs());

      row = firstSheet.getRow(3);
      cell = row.getCell(10);
      cell.setCellValue(correlations.get(correlations.size() - 1).getID());

      row = firstSheet.getRow(9);
      cell = row.createCell(5);
      cell.setCellFormula("setChartAxis(\"Sheet0\",\"Chart 1\",\"Max\",Sheet2!$K$2)");
      cell = row.createCell(6);
      cell.setCellFormula("setChartAxis(\"Sheet0\",\"Chart 2\",\"Max\",Sheet2!$K$2)");

      row = firstSheet.getRow(10);
      cell = row.createCell(5);
      cell.setCellFormula("setChartAxis(\"Sheet0\",\"Chart 1\",\"Min\",Sheet2!$K$3)");
      cell = row.createCell(6);
      cell.setCellFormula("setChartAxis(\"Sheet0\",\"Chart 2\",\"Min\",Sheet2!$K$3)");

      File outputFile = new File(
          fileDir.getPath() + "\\" + metadata.getFileName() + "_" + metadata.getTargetSite()
              + "_FILTER_" + windowSize + ".xlsm");
      try (OutputStream fileOut = new FileOutputStream(outputFile)) {
        workbook.write(fileOut);
      } catch (Exception e) {
        LOGGER.error(e);
        System.out.println("error");
      }


    }
  }


  private void writeCorrelations(List<ProtonavPair> correlations, XSSFSheet firstSheet,
      int firstSheetRowCount, XSSFSheet secondSheet, int secondSheetRowCount, int maxIndex) {

    XSSFRow row;
    XSSFCell cell;

    long ID;

    AutocorrelationArrays protonavCorrelations, palimentaryCorrelations;
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

      protonavCorrelations = pair.getExtractedProtonav().getAutocorrelationArrays();
      palimentaryCorrelations = pair.getPalimentaryProtonav().getAutocorrelationArrays();
      protonavLeftCorrelations = protonavCorrelations.getSmoothedCorrelationsOnLeft();
      protonavRightCorrelations = protonavCorrelations.getSmoothedCorrelationsOnRight();
      palimentaryLeftCorrelations = palimentaryCorrelations.getSmoothedCorrelationsOnLeft();
      palimentaryRightCorrelations = palimentaryCorrelations.getSmoothedCorrelationsOnRight();

      // for (int index = 1; index < 98; index++) {
      for (int index = 1; index < maxIndex; index++) {
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


  private void writeOccurrences(XSSFSheet sheet, List<ProtonavPair> filteredProtonavPairs) {
    final List<ProtonavPair> sortedPairs = filteredProtonavPairs;
    sortedPairs.sort((o1, o2) -> (int) (o1.getID() - o2.getID()));

    XSSFRow row;
    int rowCount = 1;

    for (ProtonavPair pair : sortedPairs) {
      row = sheet.createRow(rowCount++);
      writeOccurrences(row, pair.getID(), pair.getExtractedProtonav());
      row = sheet.createRow(rowCount++);
      writeOccurrences(row, pair.getID(), pair.getPalimentaryProtonav());
    }
  }

  private void writeOccurrences(XSSFRow row, long ID, Protonav protonav) {

    XSSFCell cell = row.createCell(0);
    cell.setCellValue(ID);

    cell = row.createCell(1);
    cell.setCellValue(protonav.getPattern());

    final ProtonavProbabilities resultingProbabilities = protonav.getResultingProbabilities();

    cell = row.createCell(2);
    cell.setCellValue(protonav.getOccurrences().getLeftCount());

    cell = row.createCell(3);
    cell.setCellValue(protonav.getOccurrences().getRightCount());

    cell = row.createCell(4);
    cell.setCellValue(resultingProbabilities.getLeftProbabilityBySingles());

    cell = row.createCell(5);
    cell.setCellValue(resultingProbabilities.getRightProbabilityBySingles());

    cell = row.createCell(6);
    cell.setCellValue(resultingProbabilities.getLeftProbabilityByPairs());

    cell = row.createCell(7);
    cell.setCellValue(resultingProbabilities.getRightProbabilityByPairs());

  }


}
