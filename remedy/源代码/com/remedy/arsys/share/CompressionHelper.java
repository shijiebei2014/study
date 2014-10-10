package com.remedy.arsys.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CompressionHelper
{
  public static void compressContents(String paramString, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, boolean paramBoolean)
    throws IOException
  {
    byte[] arrayOfByte1 = paramString.getBytes("UTF-8");
    String str1 = paramHttpServletRequest.getHeader("accept-encoding");
    String str2 = paramHttpServletRequest.getHeader("User-Agent");
    int i = 0;
    if ((!paramBoolean) && (str2.indexOf("MSIE 6.0") != -1))
      i = 1;
    if (arrayOfByte1.length < 512)
      i = 1;
    if ((str1 != null) && (str1.indexOf("gzip") != -1) && (i == 0))
    {
      paramHttpServletResponse.setHeader("Content-Encoding", "gzip");
      GZIPOutputStream local1 = null;
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(arrayOfByte1.length);
        local1 = new GZIPOutputStream(localByteArrayOutputStream)
        {
        };
        local1.write(arrayOfByte1);
        local1.finish();
        local1.flush();
        byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
        paramHttpServletResponse.setContentLength(arrayOfByte2.length);
        paramHttpServletResponse.getOutputStream().write(arrayOfByte2);
      }
      finally
      {
        if (local1 != null)
          local1.close();
      }
    }
    else if (arrayOfByte1.length < 512)
    {
      paramHttpServletResponse.setContentLength(arrayOfByte1.length);
      paramHttpServletResponse.getOutputStream().write(arrayOfByte1);
    }
    else
    {
      paramHttpServletResponse.sendError(505);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.CompressionHelper
 * JD-Core Version:    0.6.1
 */