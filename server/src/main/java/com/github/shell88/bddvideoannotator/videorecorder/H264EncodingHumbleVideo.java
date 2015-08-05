package com.github.shell88.bddvideoannotator.videorecorder;

import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//TODO: stop debugging messages from JNI-Encoding-Library
class H264EncodingHumbleVideo extends EncodingThread{

  private Muxer muxer;
  private Encoder encoder;
  private MediaPictureConverter converter;
  private MediaPicture targetPicture;
  private MediaPacket packet;
  
  public H264EncodingHumbleVideo(File out, int imagesPerSeconds, Dimension screenBounds) throws InterruptedException, IOException{
    super(screenBounds);
    final Rational framerate = Rational.make(1, imagesPerSeconds);
    muxer = Muxer.make(out.getAbsolutePath(),  null,  "mp4");
 
    Codec codec = Codec.findEncodingCodecByName("libx264");
    encoder = Encoder.make(codec);
    encoder.setWidth(screenBounds.width);
    encoder.setHeight(screenBounds.height);
    final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
    encoder.setPixelFormat(pixelformat);
    encoder.setTimeBase(framerate);
    
    targetPicture = MediaPicture.make(encoder.getWidth(),  encoder.getHeight(), pixelformat);
    targetPicture.setTimeBase(framerate);
    MuxerFormat format = muxer.getFormat();
    
    if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
      encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
    }
    /** Open the encoder. */
    encoder.open(null, null);
      
    /** Add this stream to the muxer. */
    muxer.addNewStream(encoder);
    
    /** And open the muxer for business. */
    muxer.open(null, null);
    packet = MediaPacket.make();
  }
  
  
  public static BufferedImage convertToType(BufferedImage sourceImage,
      int targetType) {
    BufferedImage image;

    // if the source image is already the target type, return the source image

    if (sourceImage.getType() == targetType) {
      image = sourceImage;
    } else {
      image = new BufferedImage(sourceImage.getWidth(),
          sourceImage.getHeight(), targetType);
      image.getGraphics().drawImage(sourceImage, 0, 0, null);
    }

    return image;
  }

  @Override
  public void encodeScreenShotData(ScreenShotData data) {
    BufferedImage converted = convertToType(data.image,
        BufferedImage.TYPE_3BYTE_BGR);
    if (converter == null) {
      converter = MediaPictureConverterFactory.createConverter(converted,
      targetPicture);
    }
    converter.toPicture(targetPicture, converted, getCurrentFrameNumber());
    
    do {
      encoder.encode(packet, targetPicture);

      if (packet.isComplete()) {
        muxer.write(packet, false);
      }
    } while (packet.isComplete());

  }

  @Override
  public void finish() {
    do {
      encoder.encode(packet, null);
      if (packet.isComplete()) {
        muxer.write(packet, false);
      }
    } while (packet.isComplete());
    muxer.close();
  }
}