package com.remedy.arsys.ws.services.security;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

class WSSResponseWrapper extends HttpServletResponseWrapper
{
  private ByteArrayOutputStream output = new ByteArrayOutputStream();

  WSSResponseWrapper(HttpServletResponse paramHttpServletResponse)
  {
    super(paramHttpServletResponse);
  }

  byte[] getResponseData()
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

  public byte[] getData()
  {
    return this.output.toByteArray();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.WSSResponseWrapper
 * JD-Core Version:    0.6.1
 */