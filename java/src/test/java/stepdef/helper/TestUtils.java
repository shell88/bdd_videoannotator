package stepdef.helper;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Contains Static test-utilities.
 * @author Hell
 */

public class TestUtils {
  public static List<File> subTestDirectories = new ArrayList<File>();
  public static final File testOutputDirectory = new File("test_output");

  public static void cleanTestOutputFolders() throws IOException {
    FileUtils.deleteDirectory(testOutputDirectory);
  }

  /**
   * Compiles a java src-File.
   * WARNING: Requires java JDK.
   * 
   * @return error Messages from javac
   */
  public static List<String> compileJavaFile(File javaFile) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,
        null, null);

    Iterable<? extends JavaFileObject> compilationUnits = fileManager
        .getJavaFileObjectsFromStrings(Arrays.asList(javaFile.getPath()));

    List<String> options = new ArrayList<String>(2);
    // compiles to same folder as src (but to a own package)
    options.add("-d");
    options.add(javaFile.getParentFile().getPath());

    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    compiler.getTask(null, fileManager, diagnostics, options, null,
        compilationUnits).call();

    List<String> messages = new ArrayList<String>();
    for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
      messages.add(diagnostic.getKind() + ":\t Line ["
          + diagnostic.getLineNumber() + "] \t Position ["
          + diagnostic.getPosition() + "]\t"
          + diagnostic.getMessage(Locale.ROOT) + "\n");
    }
    try {
      fileManager.close();

    } catch (IOException e) {
      messages.add(e.getMessage());
    }
    return messages;
  }
  
  /**
   * @return A new subtest directory.
   */

  public static synchronized File getNewSubTestDirectory() {
    int nextIndex = subTestDirectories.size() + 1;
    File newSubTestDirectory = new File(testOutputDirectory, "subtest_"
        + nextIndex);
    boolean created = newSubTestDirectory.mkdirs();
    assertTrue(
        "Could not create Directory for SubTest: "
            + newSubTestDirectory.getPath(), created);
    subTestDirectories.add(newSubTestDirectory);
    return newSubTestDirectory;
  }

}
