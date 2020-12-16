package com.broudy.control;

import com.broudy.entity.ParsedSequence;
import com.broudy.entity.SequenceToBeParsed;
import java.io.File;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO provide a summary to FilesManager class!!!!!
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
  private final ListProperty<SequenceToBeParsed> matchedSequences;
  private final ListProperty<ParsedSequence> parsedSequences;

  private FilesManager() {
    databaseDNAFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    uploadedDNAFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    selectedDNAFiles = new SimpleListProperty<>(FXCollections.observableArrayList());
    matchedSequences = new SimpleListProperty<>(FXCollections.observableArrayList());
    parsedSequences = new SimpleListProperty<>(FXCollections.observableArrayList());
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

  public ObservableList<SequenceToBeParsed> getMatchedSequences() {
    return matchedSequences.get();
  }

  public ListProperty<SequenceToBeParsed> matchedSequencesProperty() {
    return matchedSequences;
  }

  public void setMatchedSequences(ObservableList<SequenceToBeParsed> matchedSequences) {
    this.matchedSequences.set(matchedSequences);
  }

  public ObservableList<ParsedSequence> getParsedSequences() {
    return parsedSequences.get();
  }

  public ListProperty<ParsedSequence> parsedSequencesProperty() {
    return parsedSequences;
  }

  public void setParsedSequences(ObservableList<ParsedSequence> parsedSequences) {
    this.parsedSequences.set(parsedSequences);
  }
}
