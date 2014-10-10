package com.remedy.arsys.backchannelclient;

public class NDXServerRunProcess
{
  static String GetRequestString(String paramString1, String paramString2, int paramInt1, long paramLong, int paramInt2, int[] paramArrayOfInt1, String[] paramArrayOfString1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, String[] paramArrayOfString2, int[] paramArrayOfInt4)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramInt1);
    str = str + Encoder.EncodeParam(paramLong);
    str = str + Encoder.EncodeParam(paramInt2);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString1);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramArrayOfInt3);
    str = str + Encoder.EncodeParam(paramArrayOfString2);
    str = str + Encoder.EncodeParam(paramArrayOfInt4);
    return Encoder.EncodeParam("ServerRunProcess/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXServerRunProcess
 * JD-Core Version:    0.6.1
 */