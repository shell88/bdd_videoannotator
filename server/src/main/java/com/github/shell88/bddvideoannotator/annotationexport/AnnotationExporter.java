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
   * Writes the AnnotationDocument to {@link #outputDirectory}.
   * TODO: Kapseln in MetaData-Objekt + featureName (nicht mehr wie drei argumente laut clean code)
   * 
   * @param currentScenarioName Name of the current ended Scenario
   * 
   * @param pathToVideoFile
   *          FilePath to be set as video reference.
   * @param checksum
   *          The sha1-checksum to store in the annotation file
   * 
   * @throws Exception
   *           - thrown when writing the file is not possible
   */
  public abstract void endOfCurrentScenario(String currentScenarioName,
      String pathToVideoFile, String checksumVideo) throws Exception;
}