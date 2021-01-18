package com.broudy.boundary;

import com.broudy.entity.AnalysisResults;
import com.broudy.entity.ReadyForParsing;
import java.io.File;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * A class that provides functions for manipulating GUI.
 * <p>
 * Created on the 14th of September, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class RenderingsStyler {


  public static ListCell<File> callUploadedLV(ListView<File> fileListView) {
    return new ListCell<>() {
      @Override
      protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? "" : item.getName());
      }
    };
  }

  public static ListCell<ReadyForParsing> callReadyForParsingLV(
      ListView<ReadyForParsing> fileListView) {
    return new ListCell<>() {
      @Override
      protected void updateItem(ReadyForParsing item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? "" : item.getFile().getName());
      }
    };
  }

  public static ListCell<AnalysisResults> callAnalysisResultsLV(
      ListView<AnalysisResults> analysisResultsListView) {
    return new ListCell<>() {
      @Override
      protected void updateItem(AnalysisResults item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? "" : item.getAnalysisInformation().getMetadata().getFileName().concat(" - ")
            .concat(item.getAnalysisInformation().getMetadata().getTargetSite()));
      }
    };
  }

  public static void allowNumericalOnly(TextField textField) {
    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d*")) {
        textField.setText(newValue.replaceAll("[^\\d]", ""));
      }
    });
  }

}
