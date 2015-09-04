package com.github.shell88.bddvideoannotator.annotationfile.exporter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Helper {

  /** Ensures that no instance of Helper will be generated. */
  private Helper() {

  }
  
  /**
   * Converts a datatable to a flat string representation. Each line will be
   * seperated by "||". Cells are seperated by a "|". Example: Line1Value1,
   * Line1Value2 Line2Value1, Line2Value2 will be formatted as follows:
   * ||Line1Value1|Line1Value2||Line2Value1|Line2Value2||
   * 
   * @param datatable
   *          the datatable to stringify
   * @return the stringified datatable
   */
  public static String stringifyDatatable( List<String[]> datatable) {
    String stringified = "";

    if (datatable != null && datatable.size() > 0) {
      ArrayList<String> rowsStringified = new ArrayList<String>(1);
      for (String[] row : datatable) {
        rowsStringified.add(StringUtils.join(row, "|"));
      }

      stringified += "||" + StringUtils.join(rowsStringified, "||") + "||";
    }

    return stringified;
  }
  
  
  /**
   * Returns all AnnotationFiles in a directory based on the supported
   * FileExtensions specified in {@link SupportedAnnotationFileExtension}
   * 
   * @param directory
   *          - directory to search for annotation files
   * @return - Array of AnnotationFiles in the specified directory
   * @throws IOException When reading the directory fails.
   */
  public static File[] getAnnotationFilesInDirectory(File directory)
      throws IOException {
    FilenameFilter eafFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        for (SupportedAnnotationFileExtension extension : SupportedAnnotationFileExtension
            .values()) {
          if (name.toLowerCase().endsWith(extension.toString().toLowerCase())) {
            return true;
          }
        }
        return false;
      }
    };

    return directory.listFiles(eafFilter);
  }
  
  
  /**
   * Calculates the SHA-1 checksum for a file.
   * 
   * @param path
   *          - Path to the file
   * @return - SHA-1 Checksum of the file
   * @throws Exception
   *           - FileNotFound or Exception wile calculating the checksum
   */
  public static String calcSha1Checksum(String path) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA1");
    FileInputStream fis = new FileInputStream(path);

    byte[] dataBytes = new byte[1024];

    int nread = 0;

    while ((nread = fis.read(dataBytes)) != -1) {
      md.update(dataBytes, 0, nread);
    }

    byte[] mdbytes = md.digest();

    // convert the byte to hex format
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < mdbytes.length; i++) {
      sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
    }

    fis.close();
    return sb.toString();
  }
  
  
  public static String calcSha1Checksum(File videoFile) throws Exception {
    return calcSha1Checksum(videoFile.getAbsolutePath());
  }

  /**
   * Checks which file names in the given outputDirectory are already reserved
   * and returns an empty File.
   * 
   * @param outputDirectory
   *          - outputDirectory for the new file
   * @param prefix
   *          - Prefix for the filename
   * @param fileExtension
   *          - file_extension for the new file
   * @return empty File
   */
  public static synchronized File createNewOutputFile(
       File outputDirectory,  String prefix,
       String fileExtension) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    Date date = new Date();
    String outputFilename = prefix + dateFormat.format(date) + "."
        + fileExtension;
    File ftemp = new File(outputDirectory, outputFilename);
    int num = 0;
    while (ftemp.exists()) {
      num++;
      outputFilename = prefix + dateFormat.format(date) + "_" + num + "."
          + fileExtension;
      ftemp = new File(outputDirectory, outputFilename);
    }

    return new File(outputDirectory, outputFilename);
  }

  
}
