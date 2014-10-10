package com.remedy.arsys.arwebreport;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper
{
  private ByteArrayOutputStream output = new ByteArrayOutputStream();
  private int contentLength;
  private String contentType;
  private int statusCode;

  public ResponseWrapper(HttpServletResponse paramHttpServletResponse)
  {
    super(paramHttpServletResponse);
  }

  public byte[] getData()
  {
    return this.output.toByteArray();
  }

  public ServletOutputStream getOutputStream()
  {
    return new FilterServletOutputStream(this.output);
  }

  public PrintWriter getWriter()
  {
    return new PrintWriter(getOutputStream(), true);
  }

  public void setContentLength(int paramInt)
  {
    this.contentLength = paramInt;
    super.setContentLength(paramInt);
  }

  public int getContentLength()
  {
    return this.contentLength;
  }

  public void setContentType(String paramString)
  {
    this.contentType = paramString;
    super.setContentType(paramString);
  }

  public String getContentType()
  {
    return this.contentType;
  }

  public void setStatus(int paramInt)
  {
    this.statusCode = paramInt;
    super.setStatus(paramInt);
  }

  public int getStatus()
  {
    return this.statusCode;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.ResponseWrapper
 * JD-Core Version:    0.6.1
 */