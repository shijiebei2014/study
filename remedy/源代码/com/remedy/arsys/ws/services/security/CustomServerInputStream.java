package com.remedy.arsys.ws.services.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

class CustomServerInputStream extends ServletInputStream
{
  private InputStream in;

  public CustomServerInputStream(ByteArrayOutputStream paramByteArrayOutputStream)
    throws IOException
  {
    this.in = new ByteArrayInputStream(paramByteArrayOutputStream.toByteArray());
  }

  public int read()
    throws IOException
  {
    return this.in.read();
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return this.in.read(paramArrayOfByte, paramInt1, paramInt2);
  }

  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return this.in.read(paramArrayOfByte);
  }

  public void close()
    throws IOException
  {
    this.in.close();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.CustomServerInputStream
 * JD-Core Version:    0.6.1
 */