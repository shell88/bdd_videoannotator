package com.github.shell88.bddvideoannotator.javaadapters;

import com.github.shell88.bddvideoannotator.stubjava.AnnotationService;
import com.github.shell88.bddvideoannotator.stubjava.AnnotationServiceService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.BindingProvider;


/**
 * Reads the Properties-File, starts the java-based server Process and
 * establishes a SOAP-connection to it.
 * 
 * @author Hell
 *
 */

public class ServerConnector {
  /** Client to the annotationServer. */
  private AnnotationService javaClient = null;
  /** Process of the started annotationServer. */
  private Process serverProcess = null;
  /** Address where annotationServer is published. */
  private String publishAddress;
  /** Configuration of the VideoWith from PropertiesFile. */
  private String videoWidth;
  /** Configuration of the VideoHeight from PropertiesFile. */
  private String videoHeight;
  /** Configuration of the OutputDirectory from PropertiesFile. */
  private String outputDirectory;

  /**
      Initializes ServerConnector with Properties from properties_file.
   */
  public ServerConnector() {
    Properties properties = loadPropertiesFromConfigFile();
    Integer port = Integer.parseInt(properties.getProperty("publish_port"));
    this.publishAddress = "http://localhost:" + port + "/bdd_videoannotator";
    videoWidth = properties.getProperty("video_width");
    videoHeight = properties.getProperty("video_height");
    outputDirectory = properties.getProperty("output_directory");
  }

  /**
   * Loads properties from either adapter_config.propertes (custom_setting,
   * preferred file when found on the classpath) or the
   * default_config.properties (always included in the server JAR-Package).
   * 
   * @return loaded Properties
   */
  public Properties loadPropertiesFromConfigFile() {
    InputStream instream = ClassLoader
        .getSystemResourceAsStream("adapter_config.properties");
    if (instream == null) {
      instream = ClassLoader
          .getSystemResourceAsStream("default_config.properties");
    }
    Properties properties = new Properties();
    try {
      properties.load(instream);
      instream.close();
    } catch (IOException e) {
      throw new ServerConnectorException("Could not read Properties-File", e);
    }
    return properties;
  }

  /**
   * Starts the server in a separate JVM-Process. A shutdown hook will be
   * registered so that the JVM Process will be securely terminated when the
   * adapter ends.
   * 
   * @return A java SOAP client to the server
   */

  public synchronized AnnotationService startServerProcess() {
    if (serverProcess == null) {
      ProcessBuilder serverProcessBuilder = new ProcessBuilder(
          getCommandForServerStart());
      try {
        serverProcess = serverProcessBuilder.start();
      } catch (IOException e1) {
        throw new ServerConnectorException("Could not start ServerProcess", e1);
      }

      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          try {
            stopServerProcess();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

    }
    return getJavaClient();

  }

  /**
   * @return - Singleton SOAP-Client to the annotation server.
   */
  public synchronized AnnotationService getJavaClient() {
    if (javaClient != null) {
      return javaClient;
    }
    
    URL connectionUrl;
    try {
      connectionUrl = new URL(getWsdlLocation());
    } catch (MalformedURLException e) {
      throw new ServerConnectorException("Problem with WSDL-Location "
          + getWsdlLocation(), e);
    }
            
    for (int retries = 0; retries < 3; retries++) {
      try {
        javaClient = new AnnotationServiceService(connectionUrl)
            .getAnnotationServicePort();
        BindingProvider bindingProvider = (BindingProvider) javaClient;
        bindingProvider.getRequestContext().put(
            BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getPublishingAddress());
        return javaClient;
      } catch (javax.xml.ws.WebServiceException e) {
        System.out.println("Trying to connect again, waiting 100 ms");
        try {
          Thread.sleep(100);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }

      }
    }
    
    throw new ServerConnectorException(
          "Could not connect to server. Error Stream of Server: "
           + convertStreamtoString(serverProcess.getErrorStream()));

  }



  /**
   * Used to stop the server Process within a shutdown-hook.
   * @return boolean to check if the server process is still working
   */

  public synchronized boolean stopServerProcess() {
    if (javaClient != null) {
      javaClient.stopScenario();
    }
    javaClient = null;
    if (serverProcess == null) {
      return false;
    }
    serverProcess.destroyForcibly();

    try {
      serverProcess.waitFor(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new ServerConnectorException(
          "Could not safely destroy serverProcess", e);
    }

    return serverProcess.isAlive();
  }

  /**
   * @return The address where the server will be published.
   */
  public String getPublishingAddress() {
    return publishAddress;
  }

  /**
   * @return The adress where the wsdl-file will be published.
   */

  public String getWsdlLocation() {
    return publishAddress + "?wsdl";
  }

  /**
   * @return string Array containing all arguments to start the server process.
   */
  public String[] getCommandForServerStart() {
    /*
     * Uses non shaded (not standalone) server because dependencies for
     * Java-based BDD-Frameworks can be resolved using maven
     */
    
    return new String[] {
        "java",
        "-cp",
        getCurrentClasspath(),
        com.github.shell88.bddvideoannotator.service.AnnotationService.class
            .getCanonicalName(), publishAddress, outputDirectory, videoWidth,
        videoHeight };
        
  }

  /**
   * @return command-Line classpath from the current SystemClassLoader.
   * */
  private String getCurrentClasspath() {

    URL[] loadedUrls = ((URLClassLoader) (ClassLoader.getSystemClassLoader()))
        .getURLs();

    StringBuffer buffer = new StringBuffer();

    String pathTemp;
    for (URL url : loadedUrls) {
      try {
        pathTemp = Paths.get(url.toURI()).toFile().getAbsolutePath();
      } catch (URISyntaxException e) {
        throw new ServerConnectorException(
            "Could not read classpath for starting serverProcess", e);
      }
      buffer.append(pathTemp);
      buffer.append(System.getProperty("path.separator"));
    }

    return buffer.toString();

  }
  
  /**
   * Converts an InputStream to a single String.
   * @param stream the input stream.
   * @return   the string containing the contents of the stream.
   */
  
  public static String convertStreamtoString(InputStream stream) {
    Scanner scanner = new Scanner(stream);
    scanner.useDelimiter("\\A");
    String content = scanner.hasNext() ? scanner.next() : "";
    scanner.close();
    return content;
  }

}
