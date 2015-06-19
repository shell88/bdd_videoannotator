package stepdef.helper.annotationfileparser;

import java.io.File;
import java.util.Collections;
import java.util.List;

public abstract class AnnotationFileParser {

  protected File annotationFile;

  public AnnotationFileParser(File annotationFile) {
    this.annotationFile = annotationFile;
  }

  /**
   * Parses steps from the annoationFile and returns a List sorted by the
   * aligned time of the steps.
   * 
   * @return Sorted List of steps
   * @throws Throwable
   *           IO Errors while parsing the file.
   */
  public List<ResultStep> parseSortedSteps() throws Throwable {
    List<ResultStep> steps = parseSteps();
    Collections.sort(steps);
    return steps;
  }

  /**
   * Parses the URL that references the corresponding Video file.
   * 
   * @return String the URL to the video reference file
   * @throws Throwable
   *           IO Errors while parsing the file.
   */
  public abstract String parseVideoReferenceFile() throws Throwable;
  
  protected abstract List<ResultStep> parseSteps() throws Throwable;

  /**
   * Parses the SHA1-Checksum.
   * 
   * @return SHA-1 checksum as specified in the annotation file
   * @throws Throwable
   *           IO Errors while parsing the file.
   */
  public abstract String parseSha1Checksum() throws Throwable;
}
