package com.remedy.arsys.border;

import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class GifEncoder extends ImageEncoder
{
  static final int EOF = -1;
  static final int BITS = 12;
  static final int HSIZE = 5003;
  IntHashtable colorHash;
  byte[] accum = new byte[256];
  int[] codetab = new int[5003];
  int[] htab = new int[5003];
  int[] masks = { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
  int[][] rgbPixels;
  boolean Interlace;
  boolean clear_flg = false;
  int ClearCode;
  int CountDown;
  int EOFCode;
  int Height;
  int Pass = 0;
  int Width;
  int a_count;
  int cur_accum = 0;
  int cur_bits = 0;
  int curx;
  int cury;
  int free_ent = 0;
  int g_init_bits;
  int height;
  int hsize = 5003;
  int maxbits = 12;
  int maxcode;
  int maxmaxcode = 4096;
  int n_bits;
  int width;
  private boolean interlace = false;

  public GifEncoder(Image paramImage, OutputStream paramOutputStream)
    throws IOException
  {
    super(paramImage, paramOutputStream);
  }

  public GifEncoder(Image paramImage, OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException
  {
    super(paramImage, paramOutputStream);
    this.interlace = paramBoolean;
  }

  public GifEncoder(ImageProducer paramImageProducer, OutputStream paramOutputStream)
    throws IOException
  {
    super(paramImageProducer, paramOutputStream);
  }

  public GifEncoder(ImageProducer paramImageProducer, OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException
  {
    super(paramImageProducer, paramOutputStream);
    this.interlace = paramBoolean;
  }

  byte GetPixel(int paramInt1, int paramInt2)
    throws IOException
  {
    GifEncoderHashitem localGifEncoderHashitem = (GifEncoderHashitem)this.colorHash.get(this.rgbPixels[paramInt2][paramInt1]);
    if (localGifEncoderHashitem == null)
      throw new IOException("color not found");
    return (byte)localGifEncoderHashitem.index;
  }

  final int MAXCODE(int paramInt)
  {
    return (1 << paramInt) - 1;
  }

  void Putbyte(byte paramByte, OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(paramByte);
  }

  void char_init()
  {
    this.a_count = 0;
  }

  void char_out(byte paramByte, OutputStream paramOutputStream)
    throws IOException
  {
    this.accum[(this.a_count++)] = paramByte;
    if (this.a_count >= 254)
      flush_char(paramOutputStream);
  }

  void cl_block(OutputStream paramOutputStream)
    throws IOException
  {
    cl_hash(this.hsize);
    this.free_ent = (this.ClearCode + 2);
    this.clear_flg = true;
    output(this.ClearCode, paramOutputStream);
  }

  void cl_hash(int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
      this.htab[i] = -1;
  }

  void compress(int paramInt, OutputStream paramOutputStream)
    throws IOException
  {
    this.g_init_bits = paramInt;
    this.clear_flg = false;
    this.n_bits = this.g_init_bits;
    this.maxcode = MAXCODE(this.n_bits);
    this.ClearCode = (1 << paramInt - 1);
    this.EOFCode = (this.ClearCode + 1);
    this.free_ent = (this.ClearCode + 2);
    char_init();
    int m = GIFNextPixel();
    int i2 = 0;
    int i = this.hsize;
    while (i < 65536)
    {
      i2++;
      i *= 2;
    }
    i2 = 8 - i2;
    int i1 = this.hsize;
    cl_hash(i1);
    output(this.ClearCode, paramOutputStream);
    int k;
    while ((k = GIFNextPixel()) != -1)
    {
      i = (k << this.maxbits) + m;
      int j = k << i2 ^ m;
      if (this.htab[j] == i)
      {
        m = this.codetab[j];
      }
      else
      {
        if (this.htab[j] >= 0)
        {
          int n = i1 - j;
          if (j == 0)
            n = 1;
          do
          {
            if (j -= n < 0)
              j += i1;
            if (this.htab[j] == i)
            {
              m = this.codetab[j];
              break;
            }
          }
          while (this.htab[j] >= 0);
        }
        output(m, paramOutputStream);
        m = k;
        if (this.free_ent < this.maxmaxcode)
        {
          this.codetab[j] = (this.free_ent++);
          this.htab[j] = i;
        }
        else
        {
          cl_block(paramOutputStream);
        }
      }
    }
    output(m, paramOutputStream);
    output(this.EOFCode, paramOutputStream);
  }

  void encodeDone()
    throws IOException
  {
    int i = -1;
    int j = -1;
    this.colorHash = new IntHashtable();
    int k = 0;
    for (int m = 0; m < this.height; m++)
      for (n = 0; n < this.width; n++)
      {
        int i1 = this.rgbPixels[m][n];
        boolean bool = i1 >>> 24 < 128;
        if (bool)
          if (i < 0)
          {
            i = k;
            j = i1;
          }
          else if (i1 != j)
          {
            int tmp104_103 = j;
            i1 = tmp104_103;
            this.rgbPixels[m][n] = tmp104_103;
          }
        localObject = (GifEncoderHashitem)this.colorHash.get(i1);
        if (localObject == null)
        {
          if (k >= 256)
            throw new IOException("too many colors for a GIF");
          localObject = new GifEncoderHashitem(i1, 1, k, bool);
          k++;
          this.colorHash.put(i1, localObject);
        }
        else
        {
          localObject.count += 1;
        }
      }
    if (k <= 2)
      m = 1;
    else if (k <= 4)
      m = 2;
    else if (k <= 16)
      m = 4;
    else
      m = 8;
    int n = 1 << m;
    byte[] arrayOfByte1 = new byte[n];
    byte[] arrayOfByte2 = new byte[n];
    Object localObject = new byte[n];
    Enumeration localEnumeration = this.colorHash.elements();
    while (localEnumeration.hasMoreElements())
    {
      GifEncoderHashitem localGifEncoderHashitem = (GifEncoderHashitem)localEnumeration.nextElement();
      arrayOfByte1[localGifEncoderHashitem.index] = (byte)(localGifEncoderHashitem.rgb >> 16 & 0xFF);
      arrayOfByte2[localGifEncoderHashitem.index] = (byte)(localGifEncoderHashitem.rgb >> 8 & 0xFF);
      localObject[localGifEncoderHashitem.index] = (byte)(localGifEncoderHashitem.rgb & 0xFF);
    }
    GIFEncode(this.out, this.width, this.height, this.interlace, (byte)0, i, m, arrayOfByte1, arrayOfByte2, (byte[])localObject);
  }

  void encodePixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
    throws IOException
  {
    for (int i = 0; i < paramInt4; i++)
      System.arraycopy(paramArrayOfInt, i * paramInt6 + paramInt5, this.rgbPixels[(paramInt2 + i)], paramInt1, paramInt3);
  }

  void encodeStart(int paramInt1, int paramInt2)
    throws IOException
  {
    this.width = paramInt1;
    this.height = paramInt2;
    this.rgbPixels = new int[paramInt2][paramInt1];
  }

  static void writeString(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    byte[] arrayOfByte = paramString.getBytes();
    paramOutputStream.write(arrayOfByte);
  }

  void BumpPixel()
  {
    this.curx += 1;
    if (this.curx == this.Width)
    {
      this.curx = 0;
      if (!this.Interlace)
        this.cury += 1;
      else
        switch (this.Pass)
        {
        case 0:
          this.cury += 8;
          if (this.cury >= this.Height)
          {
            this.Pass += 1;
            this.cury = 4;
          }
          break;
        case 1:
          this.cury += 8;
          if (this.cury >= this.Height)
          {
            this.Pass += 1;
            this.cury = 2;
          }
          break;
        case 2:
          this.cury += 4;
          if (this.cury >= this.Height)
          {
            this.Pass += 1;
            this.cury = 1;
          }
          break;
        case 3:
          this.cury += 2;
        }
    }
  }

  void GIFEncode(OutputStream paramOutputStream, int paramInt1, int paramInt2, boolean paramBoolean, byte paramByte, int paramInt3, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws IOException
  {
    this.Width = paramInt1;
    this.Height = paramInt2;
    this.Interlace = paramBoolean;
    int k = 1 << paramInt4;
    int j;
    int i = j = 0;
    this.CountDown = (paramInt1 * paramInt2);
    this.Pass = 0;
    int m;
    if (paramInt4 <= 1)
      m = 2;
    else
      m = paramInt4;
    this.curx = 0;
    this.cury = 0;
    writeString(paramOutputStream, "GIF89a");
    Putword(paramInt1, paramOutputStream);
    Putword(paramInt2, paramOutputStream);
    byte b = -128;
    b = (byte)(b | 0x70);
    b = (byte)(b | (byte)(paramInt4 - 1));
    Putbyte(b, paramOutputStream);
    Putbyte(paramByte, paramOutputStream);
    Putbyte((byte)0, paramOutputStream);
    for (int n = 0; n < k; n++)
    {
      Putbyte(paramArrayOfByte1[n], paramOutputStream);
      Putbyte(paramArrayOfByte2[n], paramOutputStream);
      Putbyte(paramArrayOfByte3[n], paramOutputStream);
    }
    if (paramInt3 != -1)
    {
      Putbyte((byte)33, paramOutputStream);
      Putbyte((byte)-7, paramOutputStream);
      Putbyte((byte)4, paramOutputStream);
      Putbyte((byte)1, paramOutputStream);
      Putbyte((byte)0, paramOutputStream);
      Putbyte((byte)0, paramOutputStream);
      Putbyte((byte)paramInt3, paramOutputStream);
      Putbyte((byte)0, paramOutputStream);
    }
    Putbyte((byte)44, paramOutputStream);
    Putword(i, paramOutputStream);
    Putword(j, paramOutputStream);
    Putword(paramInt1, paramOutputStream);
    Putword(paramInt2, paramOutputStream);
    if (paramBoolean)
      Putbyte((byte)64, paramOutputStream);
    else
      Putbyte((byte)0, paramOutputStream);
    Putbyte((byte)m, paramOutputStream);
    compress(m + 1, paramOutputStream);
    Putbyte((byte)0, paramOutputStream);
    Putbyte((byte)59, paramOutputStream);
  }

  int GIFNextPixel()
    throws IOException
  {
    if (this.CountDown == 0)
      return -1;
    this.CountDown -= 1;
    int i = GetPixel(this.curx, this.cury);
    BumpPixel();
    return i & 0xFF;
  }

  void Putword(int paramInt, OutputStream paramOutputStream)
    throws IOException
  {
    Putbyte((byte)(paramInt & 0xFF), paramOutputStream);
    Putbyte((byte)(paramInt >> 8 & 0xFF), paramOutputStream);
  }

  void flush_char(OutputStream paramOutputStream)
    throws IOException
  {
    if (this.a_count > 0)
    {
      paramOutputStream.write(this.a_count);
      paramOutputStream.write(this.accum, 0, this.a_count);
      this.a_count = 0;
    }
  }

  void output(int paramInt, OutputStream paramOutputStream)
    throws IOException
  {
    this.cur_accum &= this.masks[this.cur_bits];
    if (this.cur_bits > 0)
      this.cur_accum |= paramInt << this.cur_bits;
    else
      this.cur_accum = paramInt;
    for (this.cur_bits += this.n_bits; this.cur_bits >= 8; this.cur_bits -= 8)
    {
      char_out((byte)(this.cur_accum & 0xFF), paramOutputStream);
      this.cur_accum >>= 8;
    }
    if ((this.free_ent > this.maxcode) || (this.clear_flg))
      if (this.clear_flg)
      {
        this.maxcode = MAXCODE(this.n_bits = this.g_init_bits);
        this.clear_flg = false;
      }
      else
      {
        this.n_bits += 1;
        if (this.n_bits == this.maxbits)
          this.maxcode = this.maxmaxcode;
        else
          this.maxcode = MAXCODE(this.n_bits);
      }
    if (paramInt == this.EOFCode)
    {
      while (this.cur_bits > 0)
      {
        char_out((byte)(this.cur_accum & 0xFF), paramOutputStream);
        this.cur_accum >>= 8;
        this.cur_bits -= 8;
      }
      flush_char(paramOutputStream);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.border.GifEncoder
 * JD-Core Version:    0.6.1
 */