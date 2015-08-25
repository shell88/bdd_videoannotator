package com.github.shell88.bddvideoannotator.annotationfile.exporter;

import java.io.File;
import java.io.IOException;

/**
 * Parent class for all AnnotationExporters.
 * 
 * @author shell
 *
 *
 *
 */

public abstract class AnnotationExporter {
  private File outputDirectory;

  /**
   * Initializes a new AnnotationFileExporter.
   * @param outputDirectory {@link #outputDirectory}
   */
  public AnnotationExporter(final File outputDirectory) {
    this.outputDirectory = outputDirectory;
  }

  /** 
   * @return {@link #outputDirectory}.
   */
  protected final File getOutputDirectory() {
    return this.outputDirectory;
  }
  
  /**
   * Exports an ScenarioAnnotations.
   * @param exportable   -  ScenarioAnnotation to write.
   * @throws IOException -  In case of write errors. 
   */
  public abstract void write(ScenarioAnnotationsDto exportable) throws IOException;
  
  /**
   * Uses {@link #addAnnotation(String, Long, Long, String)} to adds the step to
   * the designated annotation tiers.
   * 
   * @param step
   *          the StepAnnotation to be added
   */

}