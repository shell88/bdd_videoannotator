package com.github.shell88.bddvideoannotator.annotationexport;

import java.io.File;
import java.io.IOException;

public class HtmlAnnotationExport extends AnnotationExporter {


  public HtmlAnnotationExport(File output, String[] tiers) throws IOException {
    super(output);
    
    System.out.println(output.isDirectory());
    

  }


  @Override
  public void setVideoReferenceFile(String pathToVideoFile, String checksum) {

    // TODO Auto-generated method stub

  }






  @Override
  public void addStepAnnotation(StepAnnotation step) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void endOfCurrentScenario(String scenarioName) throws Exception {
    // TODO Auto-generated method stub
    
  }

}
