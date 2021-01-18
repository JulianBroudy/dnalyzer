package com.broudy;

import com.broudy.boundary.FXMLView;
import com.broudy.control.StageManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

  private static Scene scene;
  private StageManager stageManager;

  static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFXML(fxml));
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
    return fxmlLoader.load();
  }

  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    StageManager.initializeManager(stage);
    stageManager = StageManager.getStageManager();
    stageManager.setMinWindowSize(750, 500);
    stageManager.loadNewScene(FXMLView.MAIN_SCREEN);
  }

}