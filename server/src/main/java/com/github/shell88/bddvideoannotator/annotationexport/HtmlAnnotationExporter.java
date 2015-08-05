package com.github.shell88.bddvideoannotator.annotationexport;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//FIXME: export on Ubuntu fails
public class HtmlAnnotationExporter extends AnnotationExporter {

  private final String replaceIdentifierScenarioName = "#scenarioName";
  private final String replaceIdentifierSteps = "#steps";
  private final String replaceIdentifierVideoRef = "#video";

  private final String[] assets = new String[] { "index.html",
      "angular.min.js", "bootstrap.min.css" };

  private List<StepAnnotation> steps;

  public HtmlAnnotationExporter() {
    steps = new ArrayList<StepAnnotation>();
  }

  @Override
  public void addStepAnnotation(StepAnnotation step) {
    steps.add(step);
  }

  @Override
  public void endOfCurrentScenario(String scenarioName, String pathToVideoFile,
      String checksum) throws Exception {

    copyAssetsToOutputDirectory(); 
   
    //Replacements
    File indexFileCopy = new File(getOutputDirectory(), "index.html");
    String content = FileUtils.readFileToString(indexFileCopy, "UTF-8");
    //TODO: implement with GSON
    //TODO: FileUtils in maven shade plugin
    content = content.replaceFirst(replaceIdentifierScenarioName, "'" + scenarioName + "'");
    
    content = content.replaceFirst(replaceIdentifierVideoRef, "'" 
        +  new File(pathToVideoFile).getName() + "'");

    String stepJsonString = "";
    for (StepAnnotation step : steps) {
      stepJsonString += "{ 'starttime': " + step.getMillisecondsFrom()
          + ", 'endtime': " + step.getMillisecondsTo() + ", 'text': '"
          + step.getSteptext() + "'" + "}";
    }
    content = content.replaceFirst(replaceIdentifierSteps, stepJsonString);
    FileUtils.writeStringToFile(indexFileCopy, content);
  }
  
  private void copyAssetsToOutputDirectory() {

    for (String assetName : assets) {
      InputStream assetStream = ClassLoader.getSystemResourceAsStream(assetName);
      if (assetStream == null) {
        // TODO use right exception type
        throw new RuntimeException("Could not find: " + assetName);
      }
      try {
        FileUtils.copyInputStreamToFile(assetStream,
            new File(this.getOutputDirectory(), assetName));
      } catch (IOException e) {
        throw new RuntimeException("Unable to write to report file item: ", e);
      }
    }
  }
  

}
