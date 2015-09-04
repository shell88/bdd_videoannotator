package com.github.shell88.bddvideoannotator.annotationfile.exporter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.github.shell88.bddvideoannotator.annotationfile.converter.GsonEscapeStringSerializer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Exports Annotations to a HTML-Report. The Codec of the video file must be
 * supported by the browser that you want to view with it, see also
 * {@link com.github.shell88.bddvideoannotator.annotationfile.converter.HtmlConverter}
 * Can be used to export multiple Scenarios to a single index.html.
 * @author Hell
 *
 */

public class HtmlAnnotationExporter extends AnnotationExporter {

  private Gson gson;
  private final String htmlResourcesFolder = "htmlconverter";
  private final String fileNameTargetHtml = "index.html";
  private boolean assetsCopied = false;
  private JsonArray bufferedScenarios;

  /**
   * @param outputDir - Directory where to store the htmlReport.
   */
  
  public HtmlAnnotationExporter(File outputDir) {
    super(outputDir);
    GsonBuilder builder = new GsonBuilder();
    builder.setPrettyPrinting();
    builder.registerTypeAdapter(String.class, new GsonEscapeStringSerializer());
    this.gson = builder.create();

    bufferedScenarios = new JsonArray();
  }
  

  @Override
  public void write(ScenarioAnnotationsDto exportable) throws IOException {
    if (!assetsCopied) {
      copyAssetsToOutputDirectory();
    }

    JsonObject scenario = scenarioToJsonObject(exportable.getFeatureText(),
        exportable.getScenarioText(), exportable.getStepAnnotations(),
        exportable.getNameVideoFile());

    // Every Scenario for the output-Index.html is buffered here
    // => each write command will write all Scenarios so that
    // index.html is consistent at any point in time
    bufferedScenarios.add(scenario);
    writeScenarioJsonObjectsToHtmlTemplate(bufferedScenarios);

  }

  private void copyAssetsToOutputDirectory() throws IOException {
   
    URL htmlResourceFolderUrl = ClassLoader
        .getSystemResource(htmlResourcesFolder);

    if (htmlResourceFolderUrl == null) {
      throw new IOException("Cannot find html resource folder "
          + htmlResourcesFolder);
    }
    
    if (htmlResourceFolderUrl.getProtocol().equals("jar")) {
      //Needed so that converter can also used from the standalone server jar
      String jarPath = htmlResourceFolderUrl.getPath().substring(5,
          htmlResourceFolderUrl.getPath().indexOf("!")); //strip out only the JAR file
      JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
      Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
      JarEntry entry;
      while (entries.hasMoreElements()) {
        entry = entries.nextElement();
        if (!entry.isDirectory() && entry.getName().startsWith(htmlResourcesFolder)) {
          InputStream in = jar.getInputStream(entry);
          FileUtils.copyInputStreamToFile(in, new File(getOutputDirectory(), entry.getName()
              .replaceFirst(htmlResourcesFolder, "")));
        }
      }
      jar.close();
      assetsCopied = true;
    } else {
      File htmlResource;
      try {
        htmlResource = new File(htmlResourceFolderUrl.toURI());
        if (htmlResource.list().length == 0) {
          throw new IOException(this.htmlResourcesFolder + " is empty!");
        }
        FileUtils.copyDirectory(htmlResource, this.getOutputDirectory());
      } catch (URISyntaxException e) {
        throw new IOException("Cannot read htmlResourceFolder: " + e.getMessage());
      }
      assetsCopied = true;
    }

  }

  private JsonObject scenarioToJsonObject(String featureText,
      String scenarioName, List<StepAnnotation> steps, String nameVideoFile) {
    JsonElement stepsAsJson = gson.toJsonTree(steps);
    JsonObject scenarioJson = new JsonObject();
    scenarioJson.addProperty("featuretext", featureText);
    scenarioJson.addProperty("scenarioname", scenarioName);
    scenarioJson.addProperty("video", nameVideoFile);
    scenarioJson.add("steps", stepsAsJson);
    return scenarioJson;
  }

  private void writeScenarioJsonObjectsToHtmlTemplate(JsonArray arrayScenarios)
      throws IOException {
    File indexFileCopy = new File(this.getOutputDirectory(), fileNameTargetHtml);
    String content = FileUtils.readFileToString(indexFileCopy, "UTF-8");
    content = content.replaceFirst("scenarios=[^;]*",
        "scenarios=" + gson.toJson(arrayScenarios));
    FileUtils.writeStringToFile(indexFileCopy, content);
  }

}
