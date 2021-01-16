package com.broudy.boundary.view_controllers;

import com.broudy.control.FilesManager;
import com.broudy.control.StageManager;
import com.broudy.entity.AnalysisResults;
import java.io.File;
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

/**
 * TODO provide a summary to ResultsDownloadController class!!!!!
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

    downloadSelectedBTN.disableProperty()
        .bind(resultsLV.getSelectionModel().selectedItemProperty().isNull());

    downloadSelectedBTN.setOnAction(click -> {
      final File fileDir = directoryChooser.showDialog(stageManager.getPrimaryStage());
      downloadReportFor(resultsLV.getSelectionModel().getSelectedItem(),fileDir);
    });

    downloadAllBTN.setOnAction(click -> {
      final File fileDir = directoryChooser.showDialog(stageManager.getPrimaryStage());
      final List<AnalysisResults> analysisResultsList = filesManager.getAnalysisResults();
      for (AnalysisResults analysisResults : analysisResultsList) {
        downloadReportFor(analysisResults, fileDir);
      }
    });

  }

  private void downloadReportFor(AnalysisResults analysisResults, File fileDir) {
    System.out.println(fileDir);
    System.out.println(fileDir.toPath());
  }


}
