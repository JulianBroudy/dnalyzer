package com.broudy.boundary;

/**
 * TODO provide a summary to FXMLView class!!!!!
 * <p>
 * Created on the 13th of September, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public enum FXMLView {

  MAIN_SCREEN {
    @Override
    public String getPath() {
      return "/com/broudy/FXMLs/MainScreenVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Primary View";
    }
  };


  abstract public String getPath();

  public String getTitle() {
    return "";
  }


  }
