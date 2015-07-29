package com.github.shell88.bddvideoannotator.annotationexport;

import java.io.File;

/**
 * Parent class for all AnnotationExporters.
 * 
 * @author shell
 *
 */
public abstract class AnnotationExporter {
  /** File where EAF outputFile will be stored. */
  private File outputDirectory;
 

  /** 
   * @return {@link #outputDirectory}.
   */
  protected final File getOutputDirectory() {
    return this.outputDirectory;
  }

  
  public final void setOutputDirectory(File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }
  

  /**
   * Uses {@link #addTextualAnnotation(String, Long, Long, String)} to adds the step to
   * the designated annotation tiers.
   * 
   * @param step
   *          the StepAnnotatin to be added
   */
  public abstract void addStepAnnotation(StepAnnotation step);
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
   * Writes the AnnotationDocument to {@link #outputDirectory}.
   * 
   * @throws Exception
   *           - thrown when writing the file is not possible
   */
  public abstract void endOfCurrentScenario(String currentScenarioName) throws Exception;
}