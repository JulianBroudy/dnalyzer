package com.broudy.boundary;

import com.broudy.entity.SequenceToBeParsed;
import java.io.File;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * TODO provide a summary to RenderingsStyler class!!!!!
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


  public static ListCell<SequenceToBeParsed> callMatchedLV(ListView<SequenceToBeParsed> fileListView) {
    return new ListCell<>() {
      @Override
      protected void updateItem(SequenceToBeParsed item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? "" : item.getDnaSequence().getName());
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
