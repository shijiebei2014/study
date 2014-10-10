package com.remedy.arsys.arwebreport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

public class FilterServletOutputStream extends ServletOutputStream
{
  private DataOutputStream stream;

  public FilterServletOutputStream(OutputStream paramOutputStream)
  {
    this.stream = new DataOutputStream(paramOutputStream);
  }

  public void write(int paramInt)
    throws IOException
  {
    this.stream.write(paramInt);
  }

  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    this.stream.write(paramArrayOfByte);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    this.stream.write(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.FilterServletOutputStream
 * JD-Core Version:    0.6.1
 */