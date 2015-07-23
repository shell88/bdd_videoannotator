package com.github.shell88.bddvideoannotator.javaadapters;

import com.github.shell88.bddvideoannotator.stubjava.AnnotationService;
import com.github.shell88.bddvideoannotator.stubjava.AnnotationServiceService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Properties;
import javax.xml.ws.BindingProvider;


/**
 * Reads the Properties-File, starts the java-based server Process and
 * establishes a SOAP-connection to it.
 * 
 * @author Hell
 * 
 */

public class ServerConnector {
  private AnnotationService serverClient = null;
  private Process serverProcess = null;
  private String publishAddress;
  private String videoWidth;
  private String videoHeight;
  private String outputDirectory;

  /**
   * Initializes ServerConnector with Properties from properties_file.
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
    return getServerClient();

  }

  /**
   * @return - Singleton SOAP-Client to the annotation server.
   */
  public synchronized AnnotationService getServerClient() {
    if (serverClient != null) {
      return serverClient;
    }

    URL connectionUrl;
    try {
      connectionUrl = new URL(getWsdlLocation());
    } catch (MalformedURLException e) {
      throw new ServerConnectorException("Problem with WSDL-Location "
          + getWsdlLocation(), e);
    }

    int sleepMilliseconds = 100;
    
    for (int retries = 0; retries < 30; retries++) {
      
      try {
        if (serverProcess.getErrorStream().available() > 0) {
          break;
        }
      } catch (IOException e2) {
          //nothing to do here
      }

      try {
        serverClient = new AnnotationServiceService(connectionUrl)
            .getAnnotationServicePort();
        BindingProvider bindingProvider = (BindingProvider) serverClient;
        bindingProvider.getRequestContext().put(
            BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getPublishingAddress());
        return serverClient;
      } catch (javax.xml.ws.WebServiceException e) {
        try {
          Thread.sleep(sleepMilliseconds);
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
   * 
   * @return true if serverProcess was terminated successfully
   */

  public synchronized boolean stopServerProcess() {

    if (serverClient != null) {
      try {
        serverClient.stopScenario();
      } catch (javax.xml.ws.WebServiceException e) {
        System.err.println("Could not stopScenario: " + e.getMessage());
      }
    }

    serverClient = null;
    if (serverProcess == null) {
      return true;
    }

    serverProcess.destroy();

    return isProcessTerminated(2);

  }

  /**
   * Compatibility to JRE Version 1.7 Can be replaced with process.isAlive() in
   * JDK/JRE Version 1.8
   * 
   * @param waitSeconds
   *          timeout
   * @return true if the process has exited successfully
   */
  private boolean isProcessTerminated(int waitSeconds) {
    int sleepMilliseconds = 10;
    int repetitions = waitSeconds * 1000 / sleepMilliseconds;
    do {
      try {
        serverProcess.exitValue();
        return true;
      } catch (IllegalThreadStateException ex) {
        try {
          Thread.sleep(sleepMilliseconds);
        } catch (InterruptedException e) {
          return false;
        } finally {
          --repetitions;
        }
      }
    } while (repetitions > 0);
    return false;
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
   * 
   * @param stream
   *          the input stream.
   * @return the string containing the contents of the stream.
   */

  public static String convertStreamtoString(InputStream stream) {
    StringBuilder content = new StringBuilder();
    String tempRead;
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    try {
      while (reader.ready() && (tempRead = reader.readLine()) != null) {
        content.append(tempRead);
      }
      reader.close();
      return content.toString();
    } catch (IOException e) {
      return "";
    }
  }

}
