package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.control.FileParser;
import com.broudy.control.FilesManager;
import com.broudy.control.Analyzer;
import com.broudy.control.StageManager;
import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.AnalysisResults;
import com.broudy.entity.ReadyForParsing;
import java.io.File;
import java.util.ArrayList;
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

/**
 * Controller class for the analysis screen.
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

    startBTN.textProperty().bind(
        Bindings.createStringBinding(() -> inProgress.getValue() ? "Cancel" : "Start", inProgress));

    startBTN.setOnAction(click -> {
      if (inProgress.get()) {
        taskExecutor.shutdownNow();
        inProgress.set(false);
        filesManager.clearFiles();
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

    final List<AnalysisInformation> analysisInformationList = filesManager
        .getAnalysisInformationList();
    final int numberOfAnalysisFiles = analysisInformationList.size();
    final AtomicInteger numberOfFinishedThreads = new AtomicInteger();

    for (AnalysisInformation analysisInformation : analysisInformationList) {

      final Analyzer analyzer = new Analyzer(analysisInformation);
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


}
