package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.boundary.RenderingsStyler;
import com.broudy.control.FilesManager;
import com.broudy.control.StageManager;
import com.broudy.entity.SequenceToBeParsed;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TargetSitesSelectionController {

  private static final Logger LOGGER = LogManager.getLogger(TargetSitesSelectionController.class);
  private final FileChooser fileChooser;
  private final FilesManager filesManager;
  private StageManager stageManager;
  @FXML
  private Label descriptionLBL;

  @FXML
  private HBox contentHB;

  @FXML
  private ComboBox<File> unmatchedCB;

  @FXML
  private TextField startIndexTF;

  @FXML
  private TextField endIndexTF;

  @FXML
  private Button setBTN;

  @FXML
  private Button uploadTargetSiteBTN;

  @FXML
  private CheckBox yesCB;

  @FXML
  private CheckBox noCB;

  @FXML
  private ListView<SequenceToBeParsed> matchedLV;

  @FXML
  private Button unmatchBTN;

  @FXML
  private Button nextBTN;

  public TargetSitesSelectionController() {
    stageManager = StageManager.getStageManager();
    filesManager = FilesManager.getFilesManager();
    fileChooser = new FileChooser();
  }

  @FXML
  void initialize() {

    unmatchedCB.setCellFactory(RenderingsStyler::callUploadedLV);
    matchedLV.setCellFactory(RenderingsStyler::callMatchedLV);
    RenderingsStyler.allowNumericalOnly(startIndexTF);
    RenderingsStyler.allowNumericalOnly(endIndexTF);

    initializeBindings();
    initializeEventHandlers();
  }


  private void initializeBindings() {
    unmatchedCB.itemsProperty().bindBidirectional(filesManager.selectedDNAFilesProperty());

    yesCB.disableProperty().bind(unmatchedCB.valueProperty().isNull());
    noCB.disableProperty().bind(yesCB.disableProperty());

    startIndexTF.disableProperty()
        .bind(yesCB.selectedProperty().not().and(noCB.selectedProperty().not()));
    endIndexTF.disableProperty().bind(startIndexTF.disableProperty());
    setBTN.disableProperty()
        .bind(startIndexTF.textProperty().isEmpty().or(endIndexTF.textProperty().isEmpty()));

    matchedLV.itemsProperty().bindBidirectional(filesManager.matchedSequencesProperty());
    unmatchBTN.disableProperty()
        .bind(matchedLV.getSelectionModel().selectedItemProperty().isNull());
  }

  private void initializeEventHandlers() {

    yesCB.setOnAction(action ->{
      if(yesCB.isSelected()){
        noCB.setSelected(false);
      }
    });
    noCB.setOnAction(action ->{
      if(noCB.isSelected()){
        yesCB.setSelected(false);
      }
    });

    setBTN.setOnAction(click -> {
      //TODO check if both indices are actually inclusive
      //TODO check for start bigger than end and ask about complementary strand
      /*if (Integer.parseInt(startIndexTF.getText())>Integer.parseInt(endIndexTF.getText())){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Something seems off...");
        alert.setHeaderText("Oops!");
        alert.setContentText("Start index must be smaller than end index...");
        alert.showAndWait();
      }else{

      }*/

      final SequenceToBeParsed matchedSequence = new SequenceToBeParsed(unmatchedCB.getValue(), startIndexTF.getText(),
          endIndexTF.getText(), yesCB.isSelected());
      filesManager.getMatchedSequences().add(matchedSequence);

    });

    unmatchBTN.setOnAction(click -> filesManager.getMatchedSequences()
        .remove(matchedLV.getSelectionModel().getSelectedItem()));

    nextBTN.setOnAction(click -> stageManager.switchScene(FXMLView.ANALYSIS_PROGRESS));

  }


}