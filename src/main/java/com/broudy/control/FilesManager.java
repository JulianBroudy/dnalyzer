package com.broudy.control;

import com.broudy.entity.AnalysisResults;
import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.ReadyForParsing;
import java.io.File;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Repository class to centralize all files and keep them synced over different screens.
 * <p>
 * Created on the 14th of September, 2020.
 *
 * @author <a href="https://github.com/JulianBroudy"><b>Julian Broudy</b></a>
 */
public class FilesManager {

  private static final Logger LOGGER = LogManager.getLogger(FilesManager.class);

  private static FilesManager filesManager = null;

  private final ListProperty<File> databaseDNAFiles;
  private final ListProperty<File> uploadedDNAFiles;
  private final ListProperty<File> selectedDNAFiles;
  private final ListProperty<ReadyForParsing> readyForParsing;
  private final ListProperty<AnalysisInformation> analysisInformationList;
  private final ListProperty<AnalysisResults> analysisResults;

  private FilesManager() {
    databaseDNAFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    uploadedDNAFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    selectedDNAFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    readyForParsing = new SimpleListProperty<>(FXCollections.observableArrayList());
    analysisInformationList = new SimpleListProperty<>(FXCollections.observableArrayList());
    analysisResults = new SimpleListProperty<>(FXCollections.observableArrayList());
  }

  public static FilesManager getFilesManager() {
    if (filesManager == null) {
      filesManager = new FilesManager();
    }
    return filesManager;
  }

  public ObservableList<File> getDatabaseDNAFiles() {
    return databaseDNAFiles.get();
  }

  public ListProperty<File> databaseDNAFilesProperty() {
    return databaseDNAFiles;
  }

  public void setDatabaseDNAFiles(ObservableList<File> databaseDNAFiles) {
    this.databaseDNAFiles.set(databaseDNAFiles);
  }

  public ObservableList<File> getUploadedDNAFiles() {
    return uploadedDNAFiles.get();
  }

  public ListProperty<File> uploadedDNAFilesProperty() {
    return uploadedDNAFiles;
  }

  public void setUploadedDNAFiles(ObservableList<File> uploadedDNAFiles) {
    this.uploadedDNAFiles.set(uploadedDNAFiles);
  }

  public ObservableList<File> getSelectedDNAFiles() {
    return selectedDNAFiles.get();
  }

  public ListProperty<File> selectedDNAFilesProperty() {
    return selectedDNAFiles;
  }

  public void setSelectedDNAFiles(ObservableList<File> selectedDNAFiles) {
    this.selectedDNAFiles.set(selectedDNAFiles);
  }

  public ObservableList<ReadyForParsing> getReadyForParsing() {
    return readyForParsing.get();
  }

  public ListProperty<ReadyForParsing> readyForParsingProperty() {
    return readyForParsing;
  }

  public void setReadyForParsing(ObservableList<ReadyForParsing> readyForParsing) {
    this.readyForParsing.set(readyForParsing);
  }

  public ObservableList<AnalysisInformation> getAnalysisInformationList() {
    return analysisInformationList.get();
  }

  public ListProperty<AnalysisInformation> analysisInformationListProperty() {
    return analysisInformationList;
  }

  public void setAnalysisInformationList(ObservableList<AnalysisInformation> analysisInformationList) {
    this.analysisInformationList.set(analysisInformationList);
  }

  public ObservableList<AnalysisResults> getAnalysisResults() {
    return analysisResults.get();
  }

  public ListProperty<AnalysisResults> analysisResultsProperty() {
    return analysisResults;
  }

  public void setAnalysisResults(ObservableList<AnalysisResults> analysisResults) {
    this.analysisResults.set(analysisResults);
  }

  public void clearFiles() {
    analysisResults.clear();
    analysisInformationList.clear();
    readyForParsing.clear();
  }
}
