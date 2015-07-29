package com.github.shell88.bddvideoannotator.annotationexport;

import java.io.File;

import javax.xml.datatype.DatatypeConfigurationException;

public class ExporterFactory {

  
  public static AnnotationExporter createAnnotationExporter(SupportedAnnotationFileExtension extension) {

    switch (extension) {
      case EAF:
        try {
          return new EafAnnotationExporter();
        } catch (DatatypeConfigurationException e) {
          throw new IllegalArgumentException("Could not generate EAFExporter: "
              + e.getMessage());
        }
      default:
        throw new IllegalArgumentException("File extension not supported!");
    }

  }

}
