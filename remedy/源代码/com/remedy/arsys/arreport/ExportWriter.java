package com.remedy.arsys.arreport;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.reporting.common.ReportException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class ExportWriter
{
  private final int mDestination;
  private final HttpServletResponse mRes;
  private final String mCharSet;
  private final String mOutFilename;
  private final String mContentFilename;
  private final int mExportType;
  private final boolean mWriteZip;
  private final boolean mIsRFCCompat;
  private final String mRFCLocale;
  private ZipOutputStream mOutZip;
  private ServletOutputStream mServletOut;
  private OutputStreamWriter mOutCharTrans;

  private String makeContentName()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.mContentFilename.length() > 0)
    {
      Object localObject = "";
      String str = "";
      try
      {
        str = URLEncoder.encode(this.mContentFilename, "UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
      }
      if (str.length() > 0)
      {
        if (this.mDestination == 1)
          localObject = str;
        else if (this.mIsRFCCompat)
          localObject = "utf-8'" + this.mRFCLocale + "'" + str;
        else
          localObject = str;
        localStringBuilder.append(";filename");
        if (this.mIsRFCCompat)
          localStringBuilder.append("*");
        localStringBuilder.append("=").append((String)localObject);
      }
    }
    return localStringBuilder.toString();
  }

  private String contentString()
  {
    if (this.mWriteZip)
      return "application/x_gzip";
    String str;
    switch (this.mExportType)
    {
    case 4:
      str = "text/csv";
      break;
    case 5:
      str = "text/plain";
      break;
    case 6:
      str = "text/xml";
      break;
    default:
      str = "text/plain";
    }
    return str;
  }

  private void setContentType()
  {
    this.mRes.setContentType(contentString() + ";charset=" + this.mCharSet);
  }

  private void setContentDisposition()
  {
    String str1 = makeContentName();
    String str2 = this.mDestination == 1 ? "inline" : "attachment";
    this.mRes.setHeader("Content-Disposition", str2 + str1);
  }

  private void setupForWriting()
    throws ReportException
  {
    try
    {
      this.mServletOut = this.mRes.getOutputStream();
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9257, localIOException.toString());
    }
  }

  ExportWriter(HttpServletResponse paramHttpServletResponse, int paramInt1, String paramString1, String[] paramArrayOfString, int paramInt2, boolean paramBoolean, String paramString2)
  {
    assert (paramInt1 != 0);
    assert (paramString1 != null);
    assert (paramString1.length() > 0);
    this.mDestination = paramInt1;
    this.mRes = paramHttpServletResponse;
    this.mCharSet = paramString1;
    this.mOutFilename = paramArrayOfString[1];
    this.mContentFilename = paramArrayOfString[0];
    assert (this.mOutFilename.length() > 0);
    assert (this.mContentFilename.length() > 0);
    this.mWriteZip = (!this.mOutFilename.equals(this.mContentFilename));
    this.mExportType = paramInt2;
    this.mIsRFCCompat = paramBoolean;
    this.mRFCLocale = paramString2;
    try
    {
      setContentType();
      setContentDisposition();
      setupForWriting();
    }
    catch (ReportException localReportException)
    {
      Log localLog = Log.get(0);
      localLog.log(Level.SEVERE, "Exception tryng to create OutputStream", localReportException);
    }
  }

  public int getDestType()
  {
    return this.mDestination;
  }

  public HttpServletResponse getServletResponse()
  {
    return this.mRes;
  }

  public String getCharSet()
  {
    return this.mCharSet;
  }

  public String getFilename()
  {
    return this.mOutFilename;
  }

  private String getNCharset()
  {
    CharsetsExport localCharsetsExport = CharsetsExport.getInstance();
    String str = localCharsetsExport.getNIO(this.mCharSet);
    assert (str.length() > 0);
    return this.mCharSet;
  }

  private void writeBOMIfRequired(OutputStream paramOutputStream)
    throws ReportException
  {
    byte[] arrayOfByte = { -17, -69, -65 };
    try
    {
      if (CharsetsExport.BOMRequired(this.mCharSet))
        paramOutputStream.write(arrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }

  public void openExportFile()
    throws ReportException
  {
    assert (this.mServletOut != null);
    try
    {
      String str1;
      if (this.mWriteZip)
      {
        this.mOutZip = new ZipOutputStream(this.mServletOut);
        String str2 = URLEncoder.encode(this.mOutFilename, "UTF-8");
        this.mOutZip.putNextEntry(new ZipEntry(str2));
        writeBOMIfRequired(this.mOutZip);
        str1 = getNCharset();
        this.mOutCharTrans = new OutputStreamWriter(this.mOutZip, str1);
      }
      else
      {
        writeBOMIfRequired(this.mServletOut);
        str1 = getNCharset();
        this.mOutCharTrans = new OutputStreamWriter(this.mServletOut, str1);
      }
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }

  public void closeExportFile()
    throws ReportException
  {
    try
    {
      this.mOutCharTrans.flush();
      if (this.mWriteZip)
        this.mOutZip.closeEntry();
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }

  public void print(String paramString)
    throws ReportException
  {
    try
    {
      this.mOutCharTrans.write(paramString, 0, paramString.length());
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }

  public void openFile(String paramString)
    throws ReportException
  {
    try
    {
      if (this.mWriteZip)
        this.mOutZip.putNextEntry(new ZipEntry(paramString));
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }

  public void closeFile()
    throws ReportException
  {
    try
    {
      if (this.mWriteZip)
        this.mOutZip.closeEntry();
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }

  public OutputStream getZipStream()
  {
    assert (this.mWriteZip == true);
    return this.mOutZip;
  }

  public void flush()
    throws ReportException
  {
    try
    {
      if (this.mWriteZip)
        this.mOutZip.close();
      this.mServletOut.flush();
      this.mServletOut.close();
      if (this.mOutCharTrans != null)
        this.mOutCharTrans.close();
    }
    catch (IOException localIOException)
    {
      throw new ReportException(9301, localIOException.toString());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ExportWriter
 * JD-Core Version:    0.6.1
 */