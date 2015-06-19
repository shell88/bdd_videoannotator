package com.github.shell88.bddvideoannotator.annotationexport;

import com.github.shell88.bddvideoannotator.service.Helper;
import com.github.shell88.bddvideoannotator.service.StepAnnotation;

import java.io.File;

/**
 * Parent class for all AnnotationExporters.
 * 
 * @author shell
 *
 */
public abstract class AnnotationExporter {
  /** File where EAF outputFile will be stored. */
  private File outputFile;
  /** Contains identifiers for all exported tiers in the output file. */
  private final String[] tiers = new String[] { "Steps" };

  /**
   * Initializes a new AnnotationFileExporter.
   * @param output {@link #outputFile}
   */
  public AnnotationExporter(final File output) {
    this.outputFile = output;
  }

  /** 
   * @return {@link #outputFile}.
   */
  protected final File getOutputFile() {
    return this.outputFile;
  }

  /**
   * @return {@link #tiers}.
   */
  protected final String[] getTiers() {
    return this.tiers;
  }

  /**
   * Uses {@link #addAnnotation(String, Long, Long, String)} to adds the step to
   * the designated annotation tiers.
   * 
   * @param step
   *          the StepAnnotatin to be added
   */
  public void addStepAnnotation(StepAnnotation step) {
    String annotationText = step.getSteptext()
        + Helper.stringifyDatatable(step.getDataTables()) + " "
        + step.getStepResult().toString();
    addAnnotation("Steps", step.getMillisecondsFrom(),
        step.getMillisecondsTo(), annotationText);
  }

  /**
   * Adds the reference to the corresponding video file to the
   * annotation-Document.
   * 
   * @param pathToVideoFile
   *          FilePath to be set as video reference.
   * @param checksum
   *          The sha1-checksum to store in the annotation file
   */
  public abstract void setVideoReferenceFile(String pathToVideoFile,
      String checksum);

  /**
   * Adds a textual annotation to the document.
   * 
   * @param tierIdentifier
   *          identifies the tier that the annotation should be added
   * @param millisFrom
   *          Starting time alignment of the annotation
   * @param millisTo
   *          Ending time alignment of the annotation
   * @param text
   *          text to be added as annotation.
   */
  protected abstract void addAnnotation(String tierIdentifier,
       Long millisFrom,  Long millisTo, String text);

  /**
   * Writes the AnnotationDocument to {@link #outputFile}.
   * 
   * @throws Exception
   *           - thrown when writing the file is not possible
   */
  public abstract void writeOutputFile() throws Exception;
}