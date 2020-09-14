package com.broudy.control;

import com.broudy.boundary.FXMLView;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to StageManager class!!!!!
 * <p>
 * Created on the 31th of March, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class StageManager {

  private static final Logger LOGGER = LogManager.getLogger(StageManager.class);

  private static StageManager stageManager = null;
  private static boolean showingAlert = false;
  private Stage primaryStage;

  private StageManager(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  public static StageManager getStageManager() {
    Objects.requireNonNull(StageManager.stageManager,
        "StageManager must be initialized with primaryStage first!");
    return StageManager.stageManager;
  }

  public static StageManager initializeManager(Stage primaryStage) {
    StageManager.stageManager = new StageManager(primaryStage);
    return StageManager.stageManager;
  }

  /**
   * Gets the primaryStage.
   *
   * @return current primaryStage.
   */
  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public void loadNewScene(final FXMLView fxmlView) {
    LOGGER.traceEntry("loading new scene: {}", () -> fxmlView);
    Parent fxmlViewRootNode = loadViewNodeHierarchy(fxmlView);
    show(fxmlViewRootNode);
  }

  /**
   * This method loads the passed {@see FXMLView} after all its dependencies if any are resolved by
   * the {@see boundary.EnhancedFXMLLoader} and returns the top-most node of the FXML's hierarchy.
   *
   * @param fxmlView the {@see FXMLView} to be loaded.
   *
   * @return the root node {@see Parent} of the loaded {@see FXMLView}.
   */
  private Parent loadViewNodeHierarchy(FXMLView fxmlView) {
    Parent rootNode = null;
    FXMLLoader fxmlLoader = new FXMLLoader();
    try {
      fxmlLoader.setLocation(getClass().getResource(fxmlView.getPath()));
      rootNode = fxmlLoader.load();
      Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
    } catch (Exception exception) {
      exception.printStackTrace();
      LOGGER.error(exception.getMessage());
    }
    return LOGGER.traceExit(rootNode);
  }

  private void show(final Parent rootNode) {
    Scene scene = prepareScene(rootNode);
    Platform.runLater(() -> {
      primaryStage.setScene(scene);
      primaryStage.sizeToScene();
      primaryStage.centerOnScreen();

      LOGGER.debug("#show StageManager: " + this);
      LOGGER.debug("#show PrimaryStage: " + primaryStage);
      try {
        primaryStage.show();
      } catch (Exception exception) {
        LOGGER.error(exception);
      }
    });
  }

  public void lockMinWindowSize() {
    Platform.runLater(() -> {
      primaryStage.setMinWidth(primaryStage.getWidth());
      primaryStage.setMinHeight(primaryStage.getHeight());
    });
  }

  private Scene prepareScene(Parent rootNode) {
    Scene scene = new Scene(rootNode);
    scene.setRoot(rootNode);
    scene.setFill(Color.TRANSPARENT);
    return scene;
  }

  public void setMinWindowSize(double width, double height) {
    primaryStage.widthProperty().addListener((o, oldValue, newValue) -> {
      if (newValue.doubleValue() < width) {
        primaryStage.setResizable(false);
        primaryStage.setWidth(width);
        if (!showingAlert) {
          showingAlert = true;
          showMinimizationAlertFor("width");
        }
        primaryStage.setResizable(true);
      }
    });
    primaryStage.heightProperty().addListener((o, oldValue, newValue) -> {
      if (newValue.doubleValue() < height) {
        primaryStage.setResizable(false);
        primaryStage.setHeight(height);
        if (!showingAlert) {
          showingAlert = true;
          showMinimizationAlertFor("height");
        }
        primaryStage.setResizable(true);
      }
    });

  }

  private void showMinimizationAlertFor(String axis) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Resizing Error...");
    alert.setHeaderText("Oops!");
    alert.setContentText("A smaller " + axis
        + " than this and you will break me!\nYou wouldn't want that would you?");
    alert.showAndWait().ifPresent((btnType) -> {
      // feedbackLbl.setText("Thats all from " + BLOCKING_WARNING_ALERT);
      // clearDialogOptionSelections();
      showingAlert = false;
    });

  }


}
