package com.remedy.arsys.support;

import BlowfishJ.BinConverter;
import BlowfishJ.BlowfishCBC;

public class PwdUtility
{
  private static final long CBCIV_START = 72623859790382856L;

  public static String decryptPswdUsingBlowfish(String paramString)
  {
    int i = paramString.length();
    if (i > 0)
    {
      byte[] arrayOfByte1 = new byte[5];
      for (int j = 0; j < arrayOfByte1.length; j++)
        arrayOfByte1[j] = (byte)(j + 1);
      BlowfishCBC localBlowfishCBC = new BlowfishCBC(arrayOfByte1, 0, arrayOfByte1.length, 72623859790382856L);
      int k = paramString.length() >> 1 & 0xFFFFFFF8;
      byte[] arrayOfByte2 = new byte[k];
      int m = BinConverter.binHexToBytes(paramString, arrayOfByte2, 0, 0, k);
      byte[] arrayOfByte3 = new byte[8];
      localBlowfishCBC.getCBCIV(arrayOfByte3, 0);
      localBlowfishCBC.decrypt(arrayOfByte2, 0, arrayOfByte2, 0, arrayOfByte2.length);
      String str = new String(arrayOfByte2);
      return str.trim();
    }
    return "";
  }

  public static String encryptPswdUsingBlowfish(String paramString)
  {
    if (paramString != null)
    {
      int i = paramString.length();
      if (i > 0)
      {
        byte[] arrayOfByte1 = new byte[5];
        for (int j = 0; j < arrayOfByte1.length; j++)
          arrayOfByte1[j] = (byte)(j + 1);
        BlowfishCBC localBlowfishCBC = new BlowfishCBC(arrayOfByte1, 0, arrayOfByte1.length, 72623859790382856L);
        byte[] arrayOfByte2 = paramString.getBytes();
        int k = i & 0x7;
        byte[] arrayOfByte3;
        if (k != 0)
        {
          arrayOfByte3 = new byte[(i & 0xFFFFFFF8) + 8];
          System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 0, i);
          for (int m = i; m < arrayOfByte3.length; m++)
            arrayOfByte3[m] = 0;
        }
        else
        {
          arrayOfByte3 = new byte[i];
          System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 0, i);
        }
        byte[] arrayOfByte4 = new byte[8];
        localBlowfishCBC.getCBCIV(arrayOfByte4, 0);
        localBlowfishCBC.encrypt(arrayOfByte3, 0, arrayOfByte3, 0, arrayOfByte3.length);
        return BinConverter.bytesToBinHex(arrayOfByte3);
      }
    }
    return "";
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.PwdUtility
 * JD-Core Version:    0.6.1
 */