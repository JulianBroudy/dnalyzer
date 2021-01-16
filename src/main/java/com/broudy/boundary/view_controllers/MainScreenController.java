package com.broudy.boundary.view_controllers;

import com.broudy.boundary.FXMLView;
import com.broudy.boundary.RenderingsStyler;
import com.broudy.control.FilesManager;
import com.broudy.control.StageManager;
import com.broudy.entity.AnalysisParameters;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainScreenController {

  private static final Logger LOGGER = LogManager.getLogger(MainScreenController.class);
  private final FileChooser fileChooser;
  private final StageManager stageManager;
  private final FilesManager filesManager;


  @FXML
  private HBox contentHB;

  @FXML
  private ListView<File> databaseLV;

  @FXML
  private Button toggleDBConnectionBTN;

  @FXML
  private ListView<File> uploadedLV;

  @FXML
  private Button uploadBTN;

  @FXML
  private ListView<File> selectedLV;

  @FXML
  private Button nextBTN;

  public MainScreenController() {
    stageManager = StageManager.getStageManager();
    filesManager = FilesManager.getFilesManager();
    fileChooser = new FileChooser();
  }

  @FXML
  void initialize() {
    assert contentHB
        != null : "fx:id=\"contentHB\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";
    assert databaseLV
        != null : "fx:id=\"databaseLV\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";
    assert toggleDBConnectionBTN
        != null : "fx:id=\"toggleDBConnectionBTN\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";
    assert uploadedLV
        != null : "fx:id=\"uploadedLV\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";
    assert uploadBTN
        != null : "fx:id=\"uploadBTN\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";
    assert selectedLV
        != null : "fx:id=\"selectedLV\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";
    assert nextBTN
        != null : "fx:id=\"nextBTN\" was not injected: check your FXML file 'MainScreenVIEW.fxml'.";

    databaseLV.setPlaceholder(new Label("Empty"));
    uploadedLV.setPlaceholder(new Label("Drag & drop"));
    selectedLV.setPlaceholder(new Label("None selected"));

    uploadedLV.setCellFactory(RenderingsStyler::callUploadedLV);
    selectedLV.setCellFactory(RenderingsStyler::callUploadedLV);

    // uploadedLV.setCellFactory(file-> new ListCell<>(){
    //         @Override
    //         protected void updateItem(File item, boolean empty) {
    //           super.updateItem(item, empty);
    //           setText(empty ? "" : item.getName());
    //         }
    // });
    // uploadedLV.setCellFactory(new Callback<>() {
    //   @Override
    //   public ListCell<File> call(ListView<File> file) {
    //     return new ListCell<>() {
    //       @Override
    //       protected void updateItem(File item, boolean empty) {
    //         super.updateItem(item, empty);
    //         setText(empty ? "" : item.getName());
    //       }
    //     };
    //   }
    // });
    initializeBindings();
    initializeEventHandlers();
    // initializeEventListeners();

  }


  private void initializeBindings() {

    databaseLV.itemsProperty().bindBidirectional(filesManager.databaseDNAFilesProperty());
    uploadedLV.itemsProperty().bindBidirectional(filesManager.uploadedDNAFilesProperty());
    selectedLV.itemsProperty().bindBidirectional(filesManager.selectedDNAFilesProperty());

    nextBTN.disableProperty().bind(Bindings.isEmpty(selectedLV.getItems()));

  }

  private void initializeEventHandlers() {

    // Accept files on drag
    uploadedLV.setOnDragOver(dragEvent -> {
      if (dragEvent.getDragboard().hasFiles()) {
        dragEvent.acceptTransferModes(TransferMode.ANY);
      }
    });

    // Handle dropping files
    uploadedLV.setOnDragDropped(dragDroppedEvent -> {
      uploadedLV.getItems().addAll(dragDroppedEvent.getDragboard().getFiles());
      //  TODO Disable uploading duplicates?
    });

    // Handle uploading from device
    uploadBTN.setOnAction(click -> {
      final List<File> filesToUpload = fileChooser
          .showOpenMultipleDialog(stageManager.getPrimaryStage());
      if (filesToUpload != null) {
        uploadedLV.getItems().addAll(filesToUpload);
      }
    });

    // Handle selection of uploaded file
    setDoubleClickHandlerBetween(uploadedLV, selectedLV);
    // Handle removal of uploaded file from "Selected Files"
    setDoubleClickHandlerBetween(selectedLV, uploadedLV);
    setDoubleClickHandlerBetween(databaseLV, selectedLV);
    // TODO handle removal of database files from selected files

    nextBTN.setOnAction(click -> stageManager.switchScene(FXMLView.TARGET_SITE_SELECTION));

  }

  // -----------------------------------------------------------------------------------------------
  // Helper Functions

  private void setDoubleClickHandlerBetween(ListView<File> thisOne, ListView<File> thatOne) {
    thisOne.setOnMouseClicked(click -> {
      if (click.getClickCount() == 2) {
        moveSelectedFileFromTo(thisOne, thatOne);
      }
    });
  }

  private void moveSelectedFileFromTo(ListView<File> from, ListView<File> to) {
    final File selectedFile = from.getSelectionModel().getSelectedItem();
    if(selectedFile!=null) {
      to.getItems().add(selectedFile);
      from.getItems().remove(selectedFile);
    }
  }


}
