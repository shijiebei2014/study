package com.remedy.arsys.ws.services.security;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

class WSSRequestWrapper extends HttpServletRequestWrapper
{
  private InputStream is;

  public WSSRequestWrapper(HttpServletRequest paramHttpServletRequest)
    throws IOException
  {
    super(paramHttpServletRequest);
    this.is = paramHttpServletRequest.getInputStream();
  }

  InputStream getRequestStream()
  {
    return this.is;
  }

  void setRequestStream(InputStream paramInputStream)
  {
    this.is = paramInputStream;
  }

  public ServletInputStream getInputStream()
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i;
    while ((i = this.is.read()) != -1)
      localByteArrayOutputStream.write((byte)i);
    localByteArrayOutputStream.flush();
    return new CustomServerInputStream(localByteArrayOutputStream);
  }

  public BufferedReader getReader()
    throws IOException
  {
    String str = getCharacterEncoding();
    return new BufferedReader(new InputStreamReader(getInputStream(), str));
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.WSSRequestWrapper
 * JD-Core Version:    0.6.1
 */