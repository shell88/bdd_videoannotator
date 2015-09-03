package com.github.shell88.bddvideoannotator.annotationfile.converter;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.Helper;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.HtmlAnnotationExporter;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.ScenarioAnnotationsDto;
import com.github.shell88.bddvideoannotator.annotationfile.parser.AnnotationFileParserFactory;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Converts AnnotationFiles to a HTMLReport using
 * {@link FfmpegCommandLineH264Encoder} and {@link HtmlAnnotationExporter}.
 * 
 * @author Hell
 * 
 */

public class HtmlConverter {

  private File scanDir;
  private File targetDir;

  /**
   * @param scanDir
   *          - Directory to search for AnnoationFiles.
   * @param targetDir
   *          - TargetDirectory where to store the HTMLReport.
   */
  public HtmlConverter(String scanDir, String targetDir){
      this(new File(scanDir),new File(targetDir));
  }
  
  /**
   * @param scanDir
   *          - Directory to search for AnnoationFiles.
   * @param targetDir
   *          - TargetDirectory where to store the HTMLReport.
   */
  public HtmlConverter(File scanDir, File targetDir) {
    if (!scanDir.exists()) {
      throw new IllegalArgumentException(scanDir.getAbsolutePath()
          + " not found!");
    }

    if (!targetDir.exists()) {
      targetDir.mkdir();
    }

    if (!targetDir.canWrite()) {
      throw new IllegalArgumentException(targetDir.getAbsolutePath()
          + " not writable!");
    }

    this.scanDir = scanDir;
    this.targetDir = targetDir;

  }

  /**
   * Converts the annotationFiles from {@link #scanDir} to a HTML-Report.
   * 
   * @throws Throwable
   *           - IOErrors
   */
  public void convert() throws Throwable {
 
    System.out.println("----Start converting2html");
    File[] annotationFiles = Helper.getAnnotationFilesInDirectory(scanDir);

    BlockingQueue<Runnable> encodingQueue = new ArrayBlockingQueue<Runnable>(
        annotationFiles.length);
    ThreadPoolExecutor encodingThreadPool = new ThreadPoolExecutor(4, 4, 30,
        TimeUnit.SECONDS, encodingQueue);

    HtmlAnnotationExporter annotationExporterHtml = new HtmlAnnotationExporter(
        targetDir);

    List<Future<Object>> encodingTasks = new ArrayList<Future<Object>>(
        annotationFiles.length);

    ScenarioAnnotationsDto dto;

    File videoInputFile;
    File videoOutputFile;

    for (File annotationFile : annotationFiles) {
      System.out.println("Parsing : " + annotationFile.getName());
      dto = AnnotationFileParserFactory.getFileParser(annotationFile).parse();
      
      videoInputFile = new File(annotationFile.getParentFile(),
          dto.getNameVideoFile());
        
      if (!videoInputFile.exists()) {
        throw new FileNotFoundException(
            "Could not find videoReferenceFile for " + annotationFile);
      }

      if (!dto.getSha1ChecksumVideo().equals(
          Helper.calcSha1Checksum(videoInputFile))) {
        throw new IllegalArgumentException("Parsed SHA-1 Checksum in "
            + annotationFile.getAbsolutePath()
            + " doesn´t match to refered VideoFile " + videoInputFile.getName());
      }
      System.out.println("Encoding: " + videoInputFile.getName());
      videoOutputFile = new File(targetDir,
          FilenameUtils.removeExtension(videoInputFile.getName()) + ".mp4");
      encodingTasks.add(encodingThreadPool
          .submit(new FfmpegCommandLineH264Encoder(videoInputFile,
              videoOutputFile)));

      
      dto.setNameVideoFile(videoOutputFile.getName());

      annotationExporterHtml.write(dto);
     
    }

    encodingThreadPool.shutdown();

    for (Future<Object> encodingTask : encodingTasks) {
      encodingTask.get();
    }

    encodingThreadPool.awaitTermination(15, TimeUnit.MINUTES);
    System.out.println("----End of converting2html");
  }

  public static void main(String args[]) throws Throwable {
    if (args.length != 2) {
      throw new IllegalArgumentException(
          "Misconfiguration: <scanDirectory> <targetDirectory");
    }
    new HtmlConverter(
        new File(args[0]), 
        new File(args[1])
        ).convert();
  }

}
