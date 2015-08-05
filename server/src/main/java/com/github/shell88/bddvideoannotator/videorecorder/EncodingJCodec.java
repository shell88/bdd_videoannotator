package com.github.shell88.bddvideoannotator.videorecorder;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

public class EncodingJCodec extends EncodingThread{
  
  private final int imagesPerSeconds;
  private Transform transform;
  private SeekableByteChannel ch;
  private ByteBuffer _out;
  private MP4Muxer muxer;
  private H264Encoder encoder;
  private ArrayList<ByteBuffer> spsList;
  private ArrayList<ByteBuffer> ppsList;
  private FramesMP4MuxerTrack outTrack;
  private Picture toEncode;
  
  public EncodingJCodec(File out, int imagesPerSeconds, Dimension screenBounds) throws IOException {
    super(screenBounds);
    this.imagesPerSeconds = imagesPerSeconds;
    this.ch = NIOUtils.writableFileChannel(out);
    // Transform to convert between RGB and YUV
    encoder = new H264Encoder();
    
   // transform = ColorUtil.getTransform(ColorSpace.RGB,  enco) new RgbToYuv420(0, 0);
    muxer = new MP4Muxer(ch, Brand.MP4);
    transform = ColorUtil.getTransform(ColorSpace.RGB, encoder.getSupportedColorSpaces()[0]);
    _out = ByteBuffer.allocate(1920 * 1080 * 6);
    
    spsList = new ArrayList<ByteBuffer>();
    ppsList = new ArrayList<ByteBuffer>();
    outTrack = muxer.addTrack(TrackType.VIDEO,  imagesPerSeconds);
  }

  

  public Picture fromBufferedImage(BufferedImage src) {
    Picture dst = Picture.create(src.getWidth(), src.getHeight(),
        ColorSpace.RGB);
    fromBufferedImage(src, dst);
    return dst;
  }

  public void fromBufferedImage(BufferedImage src, Picture dst) {
    int[] dstData = dst.getPlaneData(0);

    int off = 0;
    for (int i = 0; i < src.getHeight(); i++) {
      for (int j = 0; j < src.getWidth(); j++) {
        int rgb1 = src.getRGB(j, i);
        dstData[off++] = (rgb1 >> 16) & 0xff;
        dstData[off++] = (rgb1 >> 8) & 0xff;
        dstData[off++] = rgb1 & 0xff;
      }
    }
  }
  
  public void encodeNativeFrame(Picture pic) throws IOException {
    if (toEncode == null) {
        toEncode = Picture.create(pic.getWidth(), pic.getHeight(), ColorSpace.YUV420J);
    }
    
    // Perform conversion
     transform.transform(pic, toEncode);
    
    // Encode image into H.264 frame, the result is stored in '_out' buffer
    _out.clear();
    ByteBuffer result = encoder.encodeFrame(toEncode, _out);

    // Based on the frame above form correct MP4 packet
    spsList.clear();
    ppsList.clear();
    
    H264Utils.wipePS(result,  spsList,  ppsList);
    H264Utils.encodeMOVPacket(result);

    // Add packet to video track
    outTrack.addFrame(new MP4Packet(result, getCurrentFrameNumber(), imagesPerSeconds, 1,
        getCurrentFrameNumber(), true, null, getCurrentFrameNumber(), 0));

}

  public void finish() {

    // Push saved SPS/PPS to a special storage in MP4
    outTrack
        .addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList, 4));

    // Write MP4 header and finalize recording
    try {
      muxer.writeHeader();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    NIOUtils.closeQuietly(ch);
  }

  @Override
  public void encodeScreenShotData(ScreenShotData data) {
    // TODO Auto-generated method stub
    try {
      encodeNativeFrame(fromBufferedImage(data.image));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
}
