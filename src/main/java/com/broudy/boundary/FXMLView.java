package com.broudy.boundary;

/**
 * Enum class to encapsulate all the FXML logic and centralize calls from StageManager.
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
      return "Main Screen";
    }
  },
  TARGET_SITE_SELECTION {
    @Override
    public String getPath() {
      return "/com/broudy/FXMLs/TargetSitesSelectionVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Target Site Selector";
    }
  },
  ANALYSIS_PROGRESS {
    @Override
    public String getPath() {
      return "/com/broudy/FXMLs/AnalysisProgressVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Analysis Progress";
    }
  },
  RESULTS_DOWNLOAD {
    @Override
    public String getPath() {
      return "/com/broudy/FXMLs/ResultsDownloadVIEW.fxml";
    }

    @Override
    public String getTitle() {
      return "Results Download";
    }
  };


  abstract public String getPath();

  public String getTitle() {
    return "";
  }


  }
