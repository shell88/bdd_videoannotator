package com.github.shell88.bddvideoannotator.videorecorder;

import static java.lang.Math.max;

import com.github.shell88.bddvideoannotator.service.Helper;


import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;



//TODO: Mauszeiger funktioniert noch nicht
//TODO: length of video images PerSeconds
public class HtmlJCodecMp4VideoRecorder implements VideoRecorder {

  private File outputFile;
  
  private ScheduledThreadPoolExecutor scheduledExecutor;
  private ScreenRecorderThread recorderThread;
  private EncodingThread encoderThread;
  
  private long startRecordingTimestamp; 
  private final int imagesPerSeconds = 5;
  
  
  public HtmlJCodecMp4VideoRecorder(File outputDirectory,
      String fileNamePrefix, Dimension capturingArea) throws IOException,
      AWTException {

    outputFile = Helper.createNewOutputFile(outputDirectory, fileNamePrefix,
        "mp4");
    encoderThread = new EncodingThread(outputFile, imagesPerSeconds);
    recorderThread = new ScreenRecorderThread(new Rectangle(capturingArea), encoderThread);
    scheduledExecutor = new ScheduledThreadPoolExecutor(2);
    
  }
  
  @Override
  public void startVideoRecording() throws Exception {
    double frameRateMillis = 1000 / imagesPerSeconds;
    int screenGrabRateMillis = max(1, (int) frameRateMillis);
    
    startRecordingTimestamp = System.currentTimeMillis();
    scheduledExecutor.scheduleAtFixedRate(recorderThread, screenGrabRateMillis,
        screenGrabRateMillis, TimeUnit.MILLISECONDS);
    encoderThread.start();
  }

  @Override
  public void stopVideoRecording() throws Exception {
    scheduledExecutor.shutdown();
    encoderThread.stopRunning();
    encoderThread.join();
  }

  
  @Override
  public long getStartTimestamp() {
    return startRecordingTimestamp;
  }

  @Override
  public String getPathToOutputFile() {
    // TODO Auto-generated method stub
    return this.outputFile.getAbsolutePath();
  }

}

interface ImageEncoder {
  public void encodeScreenshot(ScreenShotData data);
}

class EncodingThread extends Thread implements ImageEncoder{
  
  private boolean isRunning = false;
  private Queue<ScreenShotData> imageBuffer;
  
  private final int imagesPerSeconds;
  
  private SeekableByteChannel ch;
  private ByteBuffer _out;
  private int frameNo;
  private MP4Muxer muxer;
  private H264Encoder encoder;
  private RgbToYuv420 transform;
  private ArrayList<ByteBuffer> spsList;
  private ArrayList<ByteBuffer> ppsList;
  private FramesMP4MuxerTrack outTrack;
  private Picture toEncode;
  
  public EncodingThread(File out, int imagesPerSeconds) throws IOException {
    this.imagesPerSeconds = imagesPerSeconds;
    this.ch = NIOUtils.writableFileChannel(out);
    this.imageBuffer = new ConcurrentLinkedQueue<ScreenShotData>();
    // Transform to convert between RGB and YUV
    transform = new RgbToYuv420(0, 0);
    muxer = new MP4Muxer(ch, Brand.MP4);
    _out = ByteBuffer.allocate(1920 * 1080 * 6);
    encoder = new H264Encoder();
    spsList = new ArrayList<ByteBuffer>();
    ppsList = new ArrayList<ByteBuffer>();
    outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, imagesPerSeconds);
    
  }
  
  public void stopRunning() {
    this.isRunning = false;
  }
  
  public void encodeScreenshot(ScreenShotData data) {
    imageBuffer.add(data);
  }

  @Override
  public void run() {
    this.isRunning = true;
    ScreenShotData data;
    while (isRunning || imageBuffer.size() > 0) {
      data = imageBuffer.poll();
      if (data != null) {
        try {
          long startEncoding = System.currentTimeMillis(); 
          encodeImage(data.image);
          System.out.println("Encoding took: " + (System.currentTimeMillis() - startEncoding));
        } catch (IOException e) {
          isRunning = false;
          throw new RuntimeException("Could not encode image: "
              + e.getMessage());
        }

      }

    }
    try {
      finish();
    } catch (IOException e) {
      throw new RuntimeException("Could not finish video: " + e.getMessage());
    }
  }
  
  public void encodeImage(BufferedImage bi) throws IOException {
    encodeNativeFrame(AWTUtil.fromBufferedImage(bi));        
}

public void encodeNativeFrame(Picture pic) throws IOException {
    if (toEncode == null) {
        toEncode = Picture.create(pic.getWidth(), pic.getHeight(), ColorSpace.YUV420);
    }
    
    // Perform conversion
    transform.transform(pic, toEncode);
    
    // Encode image into H.264 frame, the result is stored in '_out' buffer
    _out.clear();
    ByteBuffer result = encoder.encodeFrame(_out, toEncode);

    // Based on the frame above form correct MP4 packet
    spsList.clear();
    ppsList.clear();
    H264Utils.encodeMOVPacket(result, spsList, ppsList);

    // Add packet to video track
    outTrack.addFrame(new MP4Packet(result, frameNo, imagesPerSeconds, 1,
        frameNo, true, null, frameNo, 0));

    frameNo++;
}

public void finish() throws IOException {
    // Push saved SPS/PPS to a special storage in MP4
    outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

    // Write MP4 header and finalize recording
    muxer.writeHeader();
    NIOUtils.closeQuietly(ch);
}
  
  
}

class ScreenShotData{
  public BufferedImage image;
  public long timestamp;
  public Point mousePointerLocation;
}

class ScreenRecorderThread extends Thread {

  private Rectangle screenBounds;
  private Robot robot;
  private ImageEncoder encoder;
  
  private int cntExecutions = 0;
  
  public ScreenRecorderThread(Rectangle screenBounds, ImageEncoder encoder)
      throws AWTException {
    this.screenBounds = screenBounds;
    this.encoder = encoder;
    robot = new Robot();
  }
   
  @Override
  public void run() {
    
    ScreenShotData data = new ScreenShotData();
    data.image = robot.createScreenCapture(screenBounds);
    data.timestamp = System.currentTimeMillis();
    data.mousePointerLocation = MouseInfo.getPointerInfo().getLocation();
    encoder.encodeScreenshot(data);
    cntExecutions++;
    System.out.println("CntExecutions: " + cntExecutions);
    
  }
  

}
