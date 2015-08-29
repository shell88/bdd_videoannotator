package com.github.shell88.bddvideoannotator.annotationfile.exporter;

import java.util.ArrayList;
import java.util.List;

public class ScenarioAnnotationsDto {

  private String featureText;
  private String scenarioText;
  private List<StepAnnotation> stepAnnotations = new ArrayList<StepAnnotation>();
  private String sha1ChecksumVideo;
  private String nameVideoFile;

  public List<StepAnnotation> getStepAnnotations() {
    return stepAnnotations;
  }

  public int getNumberOfStepAnnotations() {
    return stepAnnotations.size();
  }

  public boolean hasStepAnnotations() {
    return getNumberOfStepAnnotations() > 0;
  }

  public StepAnnotation getStepAnnotation(int index) {
    return stepAnnotations.get(index);
  }

  public void addStepAnnotation(StepAnnotation stepAnnotation) {
    this.stepAnnotations.add(stepAnnotation);
  }

  public String getSha1ChecksumVideo() {
    return sha1ChecksumVideo;
  }

  public void setSha1ChecksumVideo(String sha1ChecksumVideo) {
    this.sha1ChecksumVideo = sha1ChecksumVideo;
  }

  public String getFeatureText() {
    return featureText;
  }

  public void setFeatureText(String featureText) {
    this.featureText = featureText;
  }

  public String getScenarioText() {
    return scenarioText;
  }

  public void setScenarioText(String scenarioText) {
    this.scenarioText = scenarioText;
  }

  public String getNameVideoFile() {
    return nameVideoFile;
  }

  public void setNameVideoFile(String nameVideoFile) {
    this.nameVideoFile = nameVideoFile;
  }

}
