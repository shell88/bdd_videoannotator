package com.github.shell88.bddvideoannotator.videorecorder;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.HeightKey;
import static org.monte.media.VideoFormatKeys.QualityKey;
import static org.monte.media.VideoFormatKeys.WidthKey;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

/**
 * Adapter for the MonteVideoRecorderLibrary.
 * 
 * @author Hell
 */

public class MonteVideoRecorderAdapter extends ScreenRecorder implements
    VideoRecorder {
  /** The default number of Frames per second for screen capturing. */
  private static final Rational DEFAULT_FRAME_RATE_SCREEN_CAPTURE = Rational
      .valueOf(15);
  /** The default number of bits per pixel for screen capturing. */
  private static final int DEFAULT_DEPTH_KEY_SCREEN_CAPTURE = 16;

  /** The default interval between key framews for scree capturing. */
  private static final int DEFAULT_KEY_FRAME_INTERVAL_SCREEN_CAPTURE = 15 * 60;

  /** The default number of Frames per second for mouse capturing. */
  private static final Rational DEFAULT_FRAME_RATE_MOUSE_CAPTURE = Rational
      .valueOf(30);

  /**
   * @param outputFile - outputfolder for the video file, the filename will be set when
   *          the video is started.
   * @param dim        - Capturing area for the video 
   * @throws IOException
   *           - thrown if video file cannot be written to the output-file
   * @throws AWTException
   *           - thrown if an error with the capturing area occurs
   */

  public MonteVideoRecorderAdapter(File outputFile, Dimension dim)
      throws IOException, AWTException {

    super(GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice().getDefaultConfiguration(),
        new Rectangle(dim),
        // the file format                      //TODO: MIME_MP4 testing => for HTML-5 reports
        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
        // the output format for screen capture
        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,
            ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, CompressorNameKey,
            ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, WidthKey, dim.width,
            HeightKey, dim.height, DepthKey, DEFAULT_DEPTH_KEY_SCREEN_CAPTURE,
            FrameRateKey, DEFAULT_FRAME_RATE_SCREEN_CAPTURE, QualityKey, 1.0f,
            KeyFrameIntervalKey, DEFAULT_KEY_FRAME_INTERVAL_SCREEN_CAPTURE),
        // the output format for mouse capture
        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey,
            ScreenRecorder.ENCODING_WHITE_CURSOR, FrameRateKey,
            DEFAULT_FRAME_RATE_MOUSE_CAPTURE),
        // audio output format
        null, outputFile

    );

  }

  /**
   * Write file to the specified output file, not to users home video directory.
   * 
   * @param fileFormat
   *          fileFormat for the video output-file
   */

  @Override
  protected File createMovieFile(Format fileFormat)
      throws IOException {
    return this.movieFolder;
  }

  @Override
  public void startVideoRecording() throws Exception {
    super.start();
  }

  @Override
  public void stopVideoRecording() throws Exception {
    super.stop();
  }

  @Override
  public long getStartTimestamp() {
    return super.getStartTime();
  }

  @Override
  public String getPathToOutputFile() {
    return super.getCreatedMovieFiles().get(0).getPath();
  }

}
