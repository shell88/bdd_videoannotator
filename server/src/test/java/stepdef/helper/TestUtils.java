package stepdef.helper;

import static org.junit.Assert.assertTrue;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.Helper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Common used utilities for the use in stepdefinitions.
 * @author Hell
 */

public class TestUtils {
  public static List<File> subTestDirectories = new ArrayList<File>();
  public static final File parent_SubTestDirectories = new File("test_output");

  /**
   * Returns the latest annotation file in a directory.
   * 
   * @param directory directory to search for annotation files
   * @return Last modified annotation file or null if no annotation file
   *         exists in the directory
   * @throws IOException When reading the directory fails.
   */
  public static File getLatestAnnotationOutputFileInDirectory(File directory)
      throws IOException {
    File[] filesInDir = Helper.getAnnotationFilesInDirectory(directory);
    Arrays.sort(filesInDir, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
    if (filesInDir.length > 0) {
      return filesInDir[0];
    }
    return null;
  }

  

  /**
   * @param directory
   *          - directory to search for video files
   * @return - Returns all video files with the .avi extension in a directory
   * @throws IOException When reading the directory fails.
   */

  public static File[] getVideoFilesInDirectory(File directory)
      throws IOException {
    FilenameFilter eafFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".avi");
      }
    };

    return directory.listFiles(eafFilter);
  }

  /**
   * Removes all output-directories before the Testrun starts.
   * @throws IOException When cleaning the directory fails.
   */

  public static void cleanTestOutputFolders() throws IOException {
    FileUtils.deleteDirectory(parent_SubTestDirectories);
  }

  /**
   * Delivers a new output-Directory for a test where an annotation file/video
   * will be produced. The outputDirectory will be clean in @see
   * #cleanTestOutputFolders() at the start of a testrun.
   * 
   * @return File - empty outputDirectory
   */
  public static synchronized File getNewSubTestDirectory() {
    int nextIndex = subTestDirectories.size() + 1;
    File newSubTestDirectory = new File(parent_SubTestDirectories, "subtest_"
        + nextIndex);
    boolean created = newSubTestDirectory.mkdirs();
    assertTrue(
        "Could not create Directory for SubTest: "
            + newSubTestDirectory.getPath(), created);
    subTestDirectories.add(newSubTestDirectory);
    System.out.println("--OutputDirectory: " + newSubTestDirectory.getName());
    return newSubTestDirectory;
  }

}
