package com.remedy.arsys.goat;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.CacheDirectiveController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Globule
  implements Serializable, Cache.Item
{
  private static final long serialVersionUID = -4820102896119042131L;
  public static final int EXPIRY_INTERVAL_NO_CACHE = 0;
  public static final int EXPIRY_INTERVAL_ONE_SECOND = 1;
  public static final int EXPIRY_INTERVAL_FORMHTMLJS = 2;
  public static final int EXPIRY_INTERVAL_RESOURCE = 3;
  public static final int EXPIRY_INTERVAL_GOATIMAGE = 4;
  protected boolean mHTMLTemplate;
  protected String mDataStr;
  protected byte[] mData;
  protected byte[] mDecompressedCompressedData;
  protected boolean mCompressed;
  protected String mContentType;
  protected long mModifiedTime;
  protected int mOrigSize;
  protected transient long mHTTPCacheTime;
  private String mContentSignature;
  private static transient Log mPerformanceLog = Log.get(8);
  protected static final transient Log arLog = Log.get(11);
  protected int mExpiryType;

  public void assignHTTPCacheTime()
  {
    switch (this.mExpiryType)
    {
    case 0:
      this.mHTTPCacheTime = 0L;
      break;
    case 1:
      this.mHTTPCacheTime = 1L;
      break;
    case 2:
      this.mHTTPCacheTime = Configuration.getInstance().getFormHTMLJSExpiryInterval();
      break;
    case 3:
      this.mHTTPCacheTime = Configuration.getInstance().getResourceExpiryInterval();
      break;
    case 4:
      this.mHTTPCacheTime = 31536000L;
    }
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    assignHTTPCacheTime();
  }

  public Globule(String paramString1, String paramString2, long paramLong, int paramInt)
    throws GoatException
  {
    try
    {
      init(paramString1.getBytes("UTF-8"), paramString2, paramLong, paramInt);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
  }

  public Globule(byte[] paramArrayOfByte, String paramString, long paramLong)
    throws GoatException
  {
    init(paramArrayOfByte, paramString, paramLong, 0);
  }

  public Globule(byte[] paramArrayOfByte, String paramString, long paramLong, int paramInt)
    throws GoatException
  {
    init(paramArrayOfByte, paramString, paramLong, paramInt);
  }

  public Globule(String paramString1, String paramString2, long paramLong, int paramInt, boolean paramBoolean)
    throws GoatException
  {
    init(paramString1, paramString2, paramLong, paramInt, paramBoolean);
  }

  public void init(byte[] paramArrayOfByte, String paramString, long paramLong, int paramInt)
    throws GoatException
  {
    this.mContentType = paramString;
    this.mOrigSize = paramArrayOfByte.length;
    this.mModifiedTime = paramLong;
    this.mExpiryType = paramInt;
    assignHTTPCacheTime();
    this.mContentSignature = CacheDirectiveController.computeContentSignature(paramArrayOfByte);
    this.mCompressed = ((shouldCompress(paramString)) && (this.mOrigSize > 512));
    if (this.mCompressed)
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(this.mOrigSize);
        GZIPOutputStream local1 = null;
        try
        {
          local1 = new GZIPOutputStream(localByteArrayOutputStream)
          {
          };
          local1.write(paramArrayOfByte);
          local1.finish();
          local1.flush();
        }
        finally
        {
          if (local1 != null)
            local1.close();
        }
        this.mData = localByteArrayOutputStream.toByteArray();
        mPerformanceLog.fine("Globule: Compressed data type " + paramString + " - was " + this.mOrigSize + " bytes, now " + this.mData.length + " bytes");
      }
      catch (IOException localIOException)
      {
        if (!$assertionsDisabled)
          throw new AssertionError();
        throw new GoatException("IO exception compressing globule-should be impossible", localIOException);
      }
    else
      this.mData = paramArrayOfByte;
  }

  public void init(String paramString1, String paramString2, long paramLong, int paramInt, boolean paramBoolean)
    throws GoatException
  {
    this.mDataStr = paramString1;
    this.mContentType = paramString2;
    this.mModifiedTime = paramLong;
    this.mExpiryType = paramInt;
    assignHTTPCacheTime();
    this.mHTMLTemplate = paramBoolean;
    this.mCompressed = false;
  }

  public final long modified()
  {
    return this.mModifiedTime;
  }

  public final boolean isCompressed()
  {
    return this.mCompressed;
  }

  public final byte[] data()
  {
    return this.mData;
  }

  public final String contentType()
  {
    return this.mContentType;
  }

  public void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, boolean paramBoolean)
  {
    if ((paramBoolean) && (!CacheDirectiveController.needNewContent(paramHttpServletRequest, paramHttpServletResponse, this.mContentSignature)))
    {
      paramHttpServletResponse.setStatus(304);
      return;
    }
    transmit(paramHttpServletRequest, paramHttpServletResponse);
  }

  public void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    try
    {
      long l1;
      try
      {
        l1 = paramHttpServletRequest.getDateHeader("If-Modified-Since");
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Log.get(11).warning("HTTP request: Bad if modified since header " + paramHttpServletRequest.getHeader("If-Modified-Since"));
        l1 = -1L;
      }
      if (l1 != -1L)
      {
        long l2 = System.currentTimeMillis();
        long l3 = this.mModifiedTime / 1000L * 1000L;
        if ((l1 <= l2) && (l1 >= l3))
        {
          paramHttpServletResponse.addHeader("Cache-Control", "public" + (this.mHTTPCacheTime > 0L ? ",max-age=" + this.mHTTPCacheTime : ""));
          if (this.mHTTPCacheTime > 0L)
            paramHttpServletResponse.setDateHeader("Expires", System.currentTimeMillis() + this.mHTTPCacheTime * 1000L);
          paramHttpServletResponse.setStatus(304);
          return;
        }
      }
      paramHttpServletResponse.setContentType(this.mContentType);
      if (this.mHTTPCacheTime <= 0L)
      {
        paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
        paramHttpServletResponse.setHeader("Pragma", "no-cache");
      }
      else
      {
        paramHttpServletResponse.setDateHeader("Expires", System.currentTimeMillis() + this.mHTTPCacheTime * 1000L);
        paramHttpServletResponse.addHeader("Cache-Control", "public,max-age=" + this.mHTTPCacheTime);
      }
      paramHttpServletResponse.setDateHeader("Last-Modified", this.mModifiedTime);
      if (this.mCompressed)
      {
        String str = paramHttpServletRequest.getHeader("accept-encoding");
        if ((str != null) && (str.indexOf("gzip") != -1))
        {
          paramHttpServletResponse.setHeader("Content-Encoding", "gzip");
          paramHttpServletResponse.setContentLength(this.mData.length);
          paramHttpServletResponse.getOutputStream().write(this.mData);
        }
        else
        {
          paramHttpServletResponse.setContentLength(this.mOrigSize);
          if (this.mDecompressedCompressedData == null)
            mPerformanceLog.fine("Globule: Detected user agent not supporting compressed data");
          byte[] arrayOfByte = decompressedData();
          paramHttpServletResponse.getOutputStream().write(arrayOfByte);
        }
      }
      else
      {
        paramHttpServletResponse.setContentLength(this.mData.length);
        paramHttpServletResponse.getOutputStream().write(this.mData);
      }
    }
    catch (IOException localIOException)
    {
      Log.get(1).log(Level.SEVERE, "Globule: Error sending compressed data globule for form", localIOException);
    }
  }

  public void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, String[] paramArrayOfString)
  {
    if (!this.mHTMLTemplate)
    {
      transmit(paramHttpServletRequest, paramHttpServletResponse);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder(this.mDataStr);
    if (paramArrayOfString != null)
    {
      int i = 0;
      int j = 0;
      k = 0;
      int m = paramArrayOfString.length;
      while (k < m)
      {
        str1 = "{" + k + "}";
        i = localStringBuilder.indexOf(str1, i);
        if (i >= 0)
          j = i + str1.length();
        localStringBuilder = localStringBuilder.replace(i, j, paramArrayOfString[k]);
        i += paramArrayOfString[k].length();
        k++;
      }
    }
    String str1 = localStringBuilder.toString();
    byte[] arrayOfByte1 = null;
    try
    {
      arrayOfByte1 = str1.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
      localUnsupportedEncodingException.printStackTrace();
    }
    byte[] arrayOfByte2 = null;
    int k = arrayOfByte1.length;
    String str2 = paramHttpServletRequest.getHeader("accept-encoding");
    int n = (str2 != null) && (str2.indexOf("gzip") != -1) && (k > 512) ? 1 : 0;
    if (n != 0)
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(k);
        GZIPOutputStream local2 = null;
        try
        {
          local2 = new GZIPOutputStream(localByteArrayOutputStream)
          {
          };
          local2.write(arrayOfByte1);
          local2.finish();
          local2.flush();
        }
        finally
        {
          if (local2 != null)
            local2.close();
        }
        arrayOfByte2 = localByteArrayOutputStream.toByteArray();
        mPerformanceLog.fine("Globule: Compressed data type " + this.mContentType + " - was " + k + " bytes, now " + arrayOfByte2.length + " bytes");
      }
      catch (IOException localIOException1)
      {
        if (!$assertionsDisabled)
          throw new AssertionError();
        new GoatException("IO exception compressing globule-should be impossible", localIOException1).printStackTrace();
      }
    else
      arrayOfByte2 = arrayOfByte1;
    try
    {
      paramHttpServletResponse.setContentType(this.mContentType);
      if (this.mHTTPCacheTime <= 0L)
      {
        paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
        paramHttpServletResponse.setHeader("Pragma", "no-cache");
      }
      else
      {
        paramHttpServletResponse.setDateHeader("Expires", System.currentTimeMillis() + this.mHTTPCacheTime * 1000L);
        paramHttpServletResponse.addHeader("Cache-Control", "public,max-age=" + this.mHTTPCacheTime);
      }
      paramHttpServletResponse.setDateHeader("Last-Modified", this.mModifiedTime);
      if (n != 0)
      {
        paramHttpServletResponse.setHeader("Content-Encoding", "gzip");
        paramHttpServletResponse.setContentLength(arrayOfByte2.length);
        paramHttpServletResponse.getOutputStream().write(arrayOfByte2);
      }
      else if (k < 512)
      {
        paramHttpServletResponse.setContentLength(arrayOfByte2.length);
        paramHttpServletResponse.getOutputStream().write(arrayOfByte2);
      }
      else
      {
        paramHttpServletResponse.sendError(505);
      }
    }
    catch (IOException localIOException2)
    {
      Log.get(1).log(Level.SEVERE, "Globule: Error sending compressed data globule for form", localIOException2);
    }
  }

  public final byte[] decompressedData()
    throws IOException
  {
    if (!this.mCompressed)
      return this.mData;
    if (this.mDecompressedCompressedData != null)
      return this.mDecompressedCompressedData;
    this.mDecompressedCompressedData = new byte[this.mOrigSize];
    GZIPInputStream localGZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(this.mData));
    int i = 0;
    while (i < this.mOrigSize)
    {
      int j = localGZIPInputStream.read(this.mDecompressedCompressedData, i, this.mDecompressedCompressedData.length - i);
      assert (j != -1);
      i += j;
    }
    return this.mDecompressedCompressedData;
  }

  public void write(PrintWriter paramPrintWriter)
  {
    try
    {
      byte[] arrayOfByte = decompressedData();
      String str = new String(arrayOfByte, "UTF-8");
      paramPrintWriter.print(str);
    }
    catch (IOException localIOException)
    {
      arLog.log(Level.SEVERE, localIOException.toString());
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
  }

  public void write(OutputStream paramOutputStream)
  {
    try
    {
      paramOutputStream.write(decompressedData());
    }
    catch (IOException localIOException)
    {
      arLog.log(Level.SEVERE, localIOException.toString());
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
  }

  public int getSize()
  {
    return 1;
  }

  public String getServer()
  {
    return "";
  }

  private boolean shouldCompress(String paramString)
  {
    return (!paramString.startsWith("image")) && (paramString.indexOf("application/x-shockwave-flash") == -1);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Globule
 * JD-Core Version:    0.6.1
 */