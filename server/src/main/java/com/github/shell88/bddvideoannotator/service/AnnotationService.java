package com.github.shell88.bddvideoannotator.service;

import com.github.shell88.bddvideoannotator.annotationexport.AnnotationExporter;
import com.github.shell88.bddvideoannotator.annotationexport.EafAnnotationExporter;
import com.github.shell88.bddvideoannotator.annotationexport.ExporterFactory;
import com.github.shell88.bddvideoannotator.annotationexport.StepAnnotation;
import com.github.shell88.bddvideoannotator.annotationexport.StepResult;
import com.github.shell88.bddvideoannotator.annotationexport.SupportedAnnotationFileExtension;
import com.github.shell88.bddvideoannotator.videorecorder.MonteVideoRecorderAdapter;
import com.github.shell88.bddvideoannotator.videorecorder.VideoRecorder;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;

/**
 * Main Class for starting the soap-based annotation service.
 * @author Hell
 */

@WebService(name = "AnnotationService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class AnnotationService {
  
  
  /**List of buffered step_annotations. */
  private ArrayList<StepAnnotation> stepAnnotations;
  /** The systemTimestamp when the scenario was started using
   * {@link #startScenario(String)}. */
  private Long scenarioStartTimestamp; 
  /** The position of the last added stepResult.*/
  private int resultPos;
  /** Represents the end timestamp of the last result
   *  {@link #resultPos}.*/
  private Long currentEndTimestamp;
  /** Description of the currentScenario
    {@link #currentScenarioName}.*/
  private String currentScenarioName = "";
  /** Path to the videoOutputFile for referencing in the annotationFile. */
  private String videoOutputFile = "";
  /** Videorecorder used for recording the screencast.*/
  private VideoRecorder videoRecorder;
  /** Capturing Area for the Screencast.  */
  private int videoHeight;
  /** Capturing Area for the Screencast.  */
  private int videoWidth;
  /** Directory where to store the video and annotation outputFile.*/
  private File outputDirectory;
  /** Singleton Annotation Exporter. */
  private AnnotationExporter annotationExporter;

  /**
   * Necessary for generating java client. DO NOT USE.
   */
  public AnnotationService() {
  }
  
  /**Initalizes a new annotation-Service.
   * @param path                {@link #outputDirectory}
   * @param capturingWidth      {@link #videoWidth}
   * @param capturingHeight     {@link #videoHeight}
   */
  public AnnotationService( String path,  String capturingWidth, String capturingHeight) {
    
    if (capturingWidth.equalsIgnoreCase("full")) {
      videoWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    } else {
      videoWidth = Integer.parseInt(capturingWidth);
      if ( videoWidth < 0
          || videoWidth > (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
        throw new IllegalArgumentException("Video width has illegal dimension: " + videoWidth);
      }
    }

    if (capturingHeight.equalsIgnoreCase("full")) {
      videoHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    } else {
      videoHeight = Integer.parseInt(capturingHeight);
      if ( videoHeight < 0 
          || videoHeight > (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
        throw new IllegalArgumentException("Video height has illegal dimension: " + videoHeight);  
      }
    }
    
    changeOutputDirectory(path);
    //TODO: usage of changeOutputDirectory? => remove if not necessary

  }
  
  private AnnotationExporter getAnnotationExporter() {
    if (this.annotationExporter == null) {
      this.annotationExporter = ExporterFactory.createAnnotationExporter(
          SupportedAnnotationFileExtension.EAF, this.outputDirectory, new String[] { "Steps" });
    }
    return this.annotationExporter;
  }
  

  /**
   * Starts a new annotation file/video file. For each Scenario an own
   * annotation file will be generated. Also Scenario Outlines will be collected
   * to one Annotation File.
   * 
   * @param scenarioName
   *          The description for the started scenario. It will be used for
   *          naming the output annotation file/video file.
   */

  @WebMethod(operationName = "startScenario")
  public void startScenario(
                        @WebParam(name = "scenarioName")
                         String scenarioName) {

    this.currentScenarioName = scenarioName;
    if (stepAnnotations == null) {
      this.stepAnnotations = new ArrayList<StepAnnotation>();
      resultPos = 0;
      currentEndTimestamp = scenarioStartTimestamp = System.currentTimeMillis();
    }
    startVideoRecording();
  }

  /**
   * Stops the current Scenario and writes the appropriate output files.
   */


  public void stopScenario() {
    this.stopVideoRecording();

    for (int i = resultPos; stepAnnotations != null
        && i < this.stepAnnotations.size(); i++) {
      this.stepAnnotations.get(i).setMillisecondsFrom(currentEndTimestamp);
    }
    
    this.writeAnnotationFile();
  }

  /**
   * Writes the buffered Annotations to the annotation output file and stops
   * video recording.
   */
  private void writeAnnotationFile() {
    if (stepAnnotations == null) {
      return;
    }
    
    //TODO: Collect also FeatureName => new SOAP-Method

    try {

      for (int i = 0; stepAnnotations != null && i < stepAnnotations.size(); i++) {
        getAnnotationExporter().addStepAnnotation(stepAnnotations.get(i));
      }
      
      if (videoOutputFile != "") {
        String checksum = Helper.calcSha1Checksum(videoOutputFile);
        getAnnotationExporter().setVideoReferenceFile(videoOutputFile, checksum);
      } else {
        getAnnotationExporter().setVideoReferenceFile(" ", " ");
      }
      getAnnotationExporter().endOfCurrentScenario(this.currentScenarioName);
      
    } catch ( Exception e ) {
      throw new WebServiceException( "Could not write Annotation-Outputfile: " + e.getMessage());
      
    } finally {
      stepAnnotations = null;
    }

  }

  /**
   * Can be used to change the outputDirectory for the annotation files/video
   * files at runtime.
   * @param path
   *          The new target outputDirectory
   */

  @WebMethod(operationName = "changeOutputDirectory")
  public void changeOutputDirectory(
      @WebParam(name = "path")  String path) {
  
    File changedOutputDirectory = new File(path);

    if (changedOutputDirectory.isFile()) {
      throw new IllegalArgumentException(changedOutputDirectory.toString() + " is a file!");
    }

    if (!changedOutputDirectory.exists()) {
      if (!changedOutputDirectory.mkdirs()) {
        throw new IllegalArgumentException("Could not create OutputDirectory: "
            + changedOutputDirectory.toString());
      }
    }
    
    this.outputDirectory = changedOutputDirectory;
    this.getAnnotationExporter().setOutputDirectory(outputDirectory);
  }

  /**
   * Starts a screencast.
   */
  private void startVideoRecording() {

    if (videoRecorder != null) {
      return;
    }
    
    String prefix = "screencast";
    if (this.currentScenarioName != "") {
      prefix = this.currentScenarioName;
    }

    File outputfile = Helper.createNewOutputFile(outputDirectory,
        prefix, "avi");
    Dimension dim = new Dimension(videoWidth, videoHeight);
    try {
      videoRecorder = new MonteVideoRecorderAdapter(outputfile, dim);
      videoRecorder.startVideoRecording();
    } catch (Exception e) {
      throw new WebServiceException("Could not start videorecording: " + e.getMessage());
    }


  }

  /**
   * Stops a screencast.
   */
  private void stopVideoRecording() {
    if (videoRecorder == null) { 
      return; 
    }
    try {
      videoRecorder.stopVideoRecording();
      videoOutputFile = videoRecorder.getPathToOutputFile();
    } catch (Exception e) {
      throw new WebServiceException("Could not stop videorecording: " + e.getMessage());
    } finally {
      videoRecorder = null;
    }
  }

  /**
   * Adds a steptext to a buffer. For each step a result will be sent later. See
   * also {@link #addResultToBufferStep(StepResult)}.
   * @param steptext
   *          The gherkin text for the step
   * @param datatable
   *          Optional input data for the step
   */

  @WebMethod(operationName = "addStepToBuffer")
  public void addStepToBuffer(
      @WebParam(name = "steptext")  String steptext,
      @WebParam(name = "datatable")  String[][] datatable) {
    if (stepAnnotations == null) {
      // Scenario not started => return => no adding necessary
      return;
    }
    
    StepAnnotation stepAnnot = new StepAnnotation();
    stepAnnot.setSteptext(steptext);
    stepAnnot.setDataTables(datatable);
    
    /*
     * If JVM terminates unexpected, result "Error" will be written by the
     * shutdown hook of the adapter
     */
    stepAnnot.setStepResult(StepResult.ERROR);
    stepAnnot.setDurationMillis(0L);
    stepAnnot.setMillisecondsFrom(0L);
    stepAnnotations.add(stepAnnot);

  }

  /**
   * Adds a result to a bufferd step text
   * {@link #addStepToBuffer(String, String[][])}. The Mapping will occur in
   * order of the incoming result. So the first result will be mapped to the
   * first step in the buffer, the second result to the second step and so on.
   * For the duration of the step, the amount of time between the last result or
   * the start of the scenario will be set.
   * 
   * @param result
   *          the result of the step
   */
  @WebMethod(operationName = "addResultToBufferStep")
  public void addResultToBufferStep(
      @WebParam(name = "result")  StepResult result) {

    if (stepAnnotations == null || stepAnnotations.get(resultPos) == null) {
      return;
    }
    
    Long endTimestamp = System.currentTimeMillis();
    StepAnnotation annotation = stepAnnotations.get(resultPos);
    annotation.setMillisecondsFrom(currentEndTimestamp
        - this.scenarioStartTimestamp);

    if (result != null) {
      annotation.setStepResult(result);
    }
    annotation.setDurationMillis(endTimestamp - currentEndTimestamp);
    this.currentEndTimestamp = endTimestamp;
    this.resultPos++;
  }

  /**
   * Adds a step with its reported result to the buffer. Uses
   * {@link #addStepToBuffer(String, String[][])} and
   * {@link #addResultToBufferStep(StepResult)}
   * 
   * @param steptext
   *          The gherkin text for the step
   * @param datatable
   *          optional input data for the step
   * @param result
   *          the result of the step
   */

  @WebMethod(operationName = "addStepWithResult")
  public void addStepWithResult(
      @WebParam(name = "steptext")  String steptext,
      @WebParam(name = "datatable")  String[][] datatable,
      @WebParam(name = "result")  StepResult result) {

    if (stepAnnotations == null) {
      return;
    }

    StepAnnotation stepAnnot = new StepAnnotation();
    stepAnnot.setSteptext(steptext);
    stepAnnot.setDataTables(datatable);
    stepAnnotations.add(stepAnnot);
    addResultToBufferStep(result);

  }

  /**
   * Starts the server process with the given arguments. 
   * @param config
   *          Configuration-Array that need the following parameters: 0
   *          Publishing address for the SOAP-service 1 Output-Directory where
   *          to store the annotation-files/video-files 2 Video-with for the
   *          capturing area 3 Video-height for the capturing area If the
   *          capturing area is invalid, no video recording will be started.
   */

  public static void main( String[] config) {
    if (config.length < 4) {
      throw new IllegalArgumentException(
        "Misconfiguration, parameters to set: "
        + "<publish_adress> <outputDirectory>, <video_width>, <video_height>");
    }
      
    final AnnotationService service = new AnnotationService(config[1], config[2], config[3]);
    final Endpoint endpoint = Endpoint.publish(config[0], service);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        endpoint.stop();
        /*
         * Unfortunately it is not possible to call stopScenario() here as
         * MonteMediaLibrary will start and endless loop. Probably it is due to
         * the fact that MonteMediaLibrary will access the currentThread when it
         * stops the video recording. As a workaround, the Server will be start
         * in an own JVM and stopScenario() will be called in a shutdown hook
         * outside from the adapter.
         */
      }
    });
  }
}
