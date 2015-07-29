package com.github.shell88.bddvideoannotator.service;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper-Class that contains static utilities.
 * 
 * @author Hell
 */

public  class Helper {

  /** Ensures that no instance of Helper will be generated. */
  private Helper() {

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
    try {
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
        sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
            .substring(1));
      }

      fis.close();
      return sb.toString();

    } catch (FileNotFoundException e) {
        return "";
    }
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
  public static String stringifyDatatable( String[][] datatable) {
    String stringified = "";

    if (datatable != null && datatable.length > 0) {
      ArrayList<String> rowsStringified = new ArrayList<String>(1);
      for (String[] row : datatable) {
        rowsStringified.add(StringUtils.join(row, "|"));
      }

      stringified += "||" + StringUtils.join(rowsStringified, "||") + "||";
    }

    return stringified;
  }
}