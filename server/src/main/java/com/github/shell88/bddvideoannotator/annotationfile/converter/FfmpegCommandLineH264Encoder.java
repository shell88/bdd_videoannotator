package com.github.shell88.bddvideoannotator.annotationfile.converter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Converts a Video to H264 Format by using ffmepg on the command line.
 * 
 * @author Hell
 *
 */

public class FfmpegCommandLineH264Encoder implements Callable<Object> {

  private ProcessBuilder processBuilder;

  /**
   * @param inputFile
   *          - Video input file.
   * @param outputFile
   *          - target outputFile.
   */
  public FfmpegCommandLineH264Encoder(File inputFile, File outputFile) {
    String pathInputFile = Paths.get(inputFile.toURI()).toString();
    String pathOutputFileName = outputFile.getAbsolutePath();
    processBuilder = new ProcessBuilder("ffmpeg", "-i", pathInputFile,
        "-vcodec", "libx264", "-pix_fmt", "yuv420p",  pathOutputFileName);
  }

  public String getCommand() {
    return this.processBuilder.command().toString();
  }

  @Override
  public Object call() throws Exception {
    try {
      Process process = processBuilder.start();
      awaitTermination(process);
    } catch (IOException e) {
      throw new EncodingException("Could not start ffmpegEncodingProcess: "
          + e.getMessage() + getCommand());
    }
    return null;
  }

  private void awaitTermination(Process process) throws EncodingException {

    int returnValue;
    String errorsStreamContent;
    try {
      errorsStreamContent = waitWhileStreamIsClosed(process.getErrorStream());
      returnValue = process.waitFor();
      if (returnValue != 0) {
        throw new EncodingException("Return Value: " + returnValue + " "
            + "of encoding process != 0, Contents of ErrorStream: \n"
            + errorsStreamContent);
      }
    } catch (IOException e) {
      throw new EncodingException("Could not close ErrorStream of subprocess "
          + e.getMessage());
    } catch (InterruptedException e) {
      throw new EncodingException("Could not Close Error Stream "
          + e.getMessage());
    }

  }

  // we have to consume error stream
  // as otherwise exitCode will not be returned
  private String waitWhileStreamIsClosed(InputStream in) throws IOException {
    StringBuilder output = new StringBuilder();
    int character;
    while ((character = in.read()) != -1) {
      output.append((char) character);
    }
    in.close();
    return output.toString();

  }

}
