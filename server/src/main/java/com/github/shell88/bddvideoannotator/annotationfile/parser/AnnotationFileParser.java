package com.github.shell88.bddvideoannotator.annotationfile.parser;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.ScenarioAnnotationsDto;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.StepAnnotation;

import java.io.File;
import java.util.Collections;
import java.util.List;

public abstract class AnnotationFileParser {

  protected File annotationFile;

  public AnnotationFileParser(File annotationFile) {
    this.annotationFile = annotationFile;
  }

  /**
   * 
   * @return            - ScenarioAnnotationsDTO from the annotationFile. 
   * @throws Throwable  - IOErrors.
   */
  public ScenarioAnnotationsDto parse() throws Throwable {
    ScenarioAnnotationsDto dto = new ScenarioAnnotationsDto();
    dto.setFeatureText(parseFeatureText());
    dto.setScenarioText(parseScenarioText());
    dto.setSha1ChecksumVideo(parseSha1Checksum());
    dto.setNameVideoFile(parseVideoReferenceFile());

    List<StepAnnotation> stepAnnotations = parseSortedSteps();
    for (StepAnnotation stepAnnotation : stepAnnotations) {
      dto.addStepAnnotation(stepAnnotation);
    }
    return dto;
  }
  
  /**
   * Parses steps from the annoationFile and returns a List sorted by the
   * aligned time of the steps.
   * 
   * @return Sorted List of steps
   * @throws Throwable
   *           IO Errors while parsing the file.
   */
  protected List<StepAnnotation> parseSortedSteps() throws Throwable {
    List<StepAnnotation> steps = parseSteps();
    Collections.sort(steps);
    return steps;
  }

  protected abstract String parseVideoReferenceFile() throws Throwable;
  
  protected abstract String parseFeatureText() throws Throwable;
  
  protected abstract String parseScenarioText() throws Throwable;
  
  protected abstract List<StepAnnotation> parseSteps() throws Throwable;
  
  protected abstract String parseSha1Checksum() throws Throwable;
  
  
}
