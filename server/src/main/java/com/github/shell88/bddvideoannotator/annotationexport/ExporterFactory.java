package com.github.shell88.bddvideoannotator.annotationexport;

import java.io.File;

import javax.xml.datatype.DatatypeConfigurationException;

public class ExporterFactory {

  //TODO: tiers should be an ENUM (supported Tiers) or specialized to the implementing Exporter
  // class EAF/HTML and so on, currently only used by EAFAnnotationExporter
  
  public static AnnotationExporter createAnnotationExporter(
      SupportedAnnotationFileExtension extension, File outputDirectory,
      String[] tiers) {

    switch (extension) {
      case EAF:
        try {
          return new EafAnnotationExporter(outputDirectory, tiers);
        } catch (DatatypeConfigurationException e) {
          throw new IllegalArgumentException("Could not generate EAFExporter: "
              + e.getMessage());
        }
      default:
        throw new IllegalArgumentException("File extension not supported!");
    }

  }

}
