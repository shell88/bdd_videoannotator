package com.github.shell88.bddvideoannotator.annotationfile.parser;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.SupportedAnnotationFileExtension;

import java.io.File;


/**
 * Factory for different annotationFile-Formats.
 * @author Hell
 *
 */

public class AnnotationFileParserFactory {

  /**
   * @param annotationFile The annotation file to be parsed.
   * @return               the corresponding outputFile parser.
   * @throws Throwable     If FileExtension is not supported.
   */
  
  public static AnnotationFileParser getFileParser(File annotationFile)
      throws Throwable {

    if (!annotationFile.exists() || !annotationFile.isFile()) {
      throw new Exception("Problem reading File: "
          + annotationFile.getAbsolutePath());
    }

    if (annotationFile
        .getName()
        .toLowerCase()
        .endsWith(SupportedAnnotationFileExtension.EAF.toString().toLowerCase())) {
      return new EafAnnotationFileParser(annotationFile);
    }

    throw new Exception("File extension not supported!");

  }

}
