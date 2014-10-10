package com.remedy.arsys.border;

import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public abstract class ImageEncoder
  implements ImageConsumer
{
  private static final ColorModel rgbModel = ColorModel.getRGBdefault();
  protected OutputStream out;
  private IOException iox;
  private ImageProducer producer;
  private int[] accumulator;
  private boolean accumulate = false;
  private boolean encoding;
  private boolean started = false;
  private int height = -1;
  private int hintflags = 0;
  private int width = -1;

  public ImageEncoder(Image paramImage, OutputStream paramOutputStream)
    throws IOException
  {
    this(paramImage.getSource(), paramOutputStream);
  }

  public ImageEncoder(ImageProducer paramImageProducer, OutputStream paramOutputStream)
    throws IOException
  {
    this.producer = paramImageProducer;
    this.out = paramOutputStream;
  }

  public void setColorModel(ColorModel paramColorModel)
  {
  }

  public void setDimensions(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }

  public void setHints(int paramInt)
  {
    this.hintflags = paramInt;
  }

  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    int[] arrayOfInt = new int[paramInt3];
    for (int i = 0; i < paramInt4; i++)
    {
      int j = paramInt5 + i * paramInt6;
      for (int k = 0; k < paramInt3; k++)
        arrayOfInt[k] = paramColorModel.getRGB(paramArrayOfByte[(j + k)] & 0xFF);
      try
      {
        encodePixelsWrapper(paramInt1, paramInt2 + i, paramInt3, 1, arrayOfInt, 0, paramInt3);
      }
      catch (IOException localIOException)
      {
        this.iox = localIOException;
        stop();
        return;
      }
    }
  }

  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    if (paramColorModel == rgbModel)
    {
      try
      {
        encodePixelsWrapper(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5, paramInt6);
      }
      catch (IOException localIOException1)
      {
        this.iox = localIOException1;
        stop();
        return;
      }
    }
    else
    {
      int[] arrayOfInt = new int[paramInt3];
      for (int i = 0; i < paramInt4; i++)
      {
        int j = paramInt5 + i * paramInt6;
        for (int k = 0; k < paramInt3; k++)
          arrayOfInt[k] = paramColorModel.getRGB(paramArrayOfInt[(j + k)]);
        try
        {
          encodePixelsWrapper(paramInt1, paramInt2 + i, paramInt3, 1, arrayOfInt, 0, paramInt3);
        }
        catch (IOException localIOException2)
        {
          this.iox = localIOException2;
          stop();
          return;
        }
      }
    }
  }

  public void setProperties(Hashtable<?, ?> paramHashtable)
  {
  }

  public synchronized void encode()
    throws IOException
  {
    this.encoding = true;
    this.iox = null;
    this.producer.startProduction(this);
    while (this.encoding)
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    if (this.iox != null)
      throw this.iox;
  }

  public void imageComplete(int paramInt)
  {
    this.producer.removeConsumer(this);
    if (paramInt == 4)
      this.iox = new IOException("image aborted");
    else
      try
      {
        encodeFinish();
        encodeDone();
      }
      catch (IOException localIOException)
      {
        this.iox = localIOException;
      }
    stop();
  }

  abstract void encodeDone()
    throws IOException;

  abstract void encodePixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
    throws IOException;

  abstract void encodeStart(int paramInt1, int paramInt2)
    throws IOException;

  private void encodeFinish()
    throws IOException
  {
    if (this.accumulate)
    {
      encodePixels(0, 0, this.width, this.height, this.accumulator, 0, this.width);
      this.accumulator = null;
      this.accumulate = false;
    }
  }

  private void encodePixelsWrapper(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
    throws IOException
  {
    if (!this.started)
    {
      this.started = true;
      encodeStart(this.width, this.height);
      if ((this.hintflags & 0x2) == 0)
      {
        this.accumulate = true;
        this.accumulator = new int[this.width * this.height];
      }
    }
    if (this.accumulate)
      for (int i = 0; i < paramInt4; i++)
        System.arraycopy(paramArrayOfInt, i * paramInt6 + paramInt5, this.accumulator, (paramInt2 + i) * this.width + paramInt1, paramInt3);
    else
      encodePixels(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5, paramInt6);
  }

  private synchronized void stop()
  {
    this.encoding = false;
    notifyAll();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.border.ImageEncoder
 * JD-Core Version:    0.6.1
 */