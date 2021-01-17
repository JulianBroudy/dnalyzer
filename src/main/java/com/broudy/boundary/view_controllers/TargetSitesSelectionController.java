package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.boundary.RenderingsStyler;
import com.broudy.control.FilesManager;
import com.broudy.control.StageManager;
import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.ReadyForParsing;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TargetSitesSelectionController {

  private static final Logger LOGGER = LogManager.getLogger(TargetSitesSelectionController.class);
  private final FilesManager filesManager;
  private StageManager stageManager;

  @FXML
  private ComboBox<File> selectedCB;

  @FXML
  private CheckBox yesCyclicCB;

  @FXML
  private CheckBox noCyclicCB;

  @FXML
  private TextField startIndexTF;

  @FXML
  private TextField endIndexTF;

  @FXML
  private TextField minPatternLenTF;

  @FXML
  private TextField maxPatternLenTF;

  @FXML
  private ComboBox<Integer> windowSizeCB;
  // @FXML
  // private TextField windowTF;

  @FXML
  private TextField paddingTF;

  @FXML
  private Button addBTN;

  @FXML
  private ListView<ReadyForParsing> readyForParsingLV;

  @FXML
  private Button removeBTN;

  @FXML
  private Button nextBTN;

  public TargetSitesSelectionController() {
    stageManager = StageManager.getStageManager();
    filesManager = FilesManager.getFilesManager();
  }

  @FXML
  void initialize() {

    selectedCB.setCellFactory(RenderingsStyler::callUploadedLV);
    readyForParsingLV.setCellFactory(RenderingsStyler::callReadyForParsingLV);

    RenderingsStyler.allowNumericalOnly(startIndexTF);
    RenderingsStyler.allowNumericalOnly(endIndexTF);
    RenderingsStyler.allowNumericalOnly(minPatternLenTF);
    RenderingsStyler.allowNumericalOnly(maxPatternLenTF);
    RenderingsStyler.allowNumericalOnly(paddingTF);
    windowSizeCB.getItems().addAll(100, 500);
    windowSizeCB.getSelectionModel().select(Integer.valueOf(500));

    initializeBindings();
    initializeEventHandlers();

    filesManager.getAnalysisInformationList().clear();
  }


  private void initializeBindings() {
    selectedCB.itemsProperty().bindBidirectional(filesManager.selectedDNAFilesProperty());

    yesCyclicCB.disableProperty().bind(selectedCB.valueProperty().isNull());
    noCyclicCB.disableProperty().bind(yesCyclicCB.disableProperty());

    startIndexTF.disableProperty()
        .bind(yesCyclicCB.selectedProperty().not().and(noCyclicCB.selectedProperty().not()));
    endIndexTF.disableProperty().bind(startIndexTF.disableProperty());
    addBTN.disableProperty()
        .bind(startIndexTF.textProperty().isEmpty().or(endIndexTF.textProperty().isEmpty()));

    readyForParsingLV.itemsProperty().bindBidirectional(filesManager.readyForParsingProperty());
    removeBTN.disableProperty()
        .bind(readyForParsingLV.getSelectionModel().selectedItemProperty().isNull());

    nextBTN.disableProperty().bind(filesManager.readyForParsingProperty().emptyProperty());
  }

  private void initializeEventHandlers() {

    yesCyclicCB.setOnAction(action -> {
      if (yesCyclicCB.isSelected()) {
        noCyclicCB.setSelected(false);
      }
    });
    noCyclicCB.setOnAction(action -> {
      if (noCyclicCB.isSelected()) {
        yesCyclicCB.setSelected(false);
      }
    });

    addBTN.setOnAction(click -> {
      final File selectedFile = selectedCB.getValue();
      final AnalysisParameters analysisParameters = new AnalysisParameters(yesCyclicCB.isSelected(),
          Integer.parseInt(startIndexTF.getText()), Integer.parseInt(endIndexTF.getText()),
          minPatternLenTF.getText().isEmpty() ? 1 : Integer.parseInt(minPatternLenTF.getText()),
          maxPatternLenTF.getText().isEmpty() ? 3 : Integer.parseInt(maxPatternLenTF.getText()),
          windowSizeCB.getValue(),
          // windowTF.getText().isEmpty() ? 500 : Integer.parseInt(windowTF.getText()),
          paddingTF.getText().isEmpty() ? 100000 : Integer.parseInt(paddingTF.getText()));
      final ReadyForParsing readyForParsing = new ReadyForParsing(selectedFile, analysisParameters);
      filesManager.getReadyForParsing().add(readyForParsing);
    });

    removeBTN.setOnAction(click -> filesManager.getReadyForParsing()
        .remove(readyForParsingLV.getSelectionModel().getSelectedItem()));

    nextBTN.setOnAction(click -> stageManager.switchScene(FXMLView.ANALYSIS_PROGRESS));

  }


}