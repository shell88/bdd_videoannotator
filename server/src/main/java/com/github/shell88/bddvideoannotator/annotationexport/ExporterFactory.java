package com.github.shell88.bddvideoannotator.annotationexport;

import javax.xml.datatype.DatatypeConfigurationException;

public class ExporterFactory {

  public static AnnotationExporter createAnnotationExporter(
      SupportedAnnotationFileExtension extension) {

    switch (extension) {
    case EAF:
      try {
        return new EafAnnotationExporter();
      } catch (DatatypeConfigurationException e) {
        throw new IllegalArgumentException("Could not create EAFExporter: "
            + e.getMessage());
      }
    case HTML:
      return new HtmlAnnotationExporter();
    default:
      throw new IllegalArgumentException("File extension not supported!");
    }
  }

}
