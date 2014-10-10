package com.remedy.arsys.backchannelclient;

public class NDXSetEntry
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString1, long paramLong, int[] paramArrayOfInt1, String[] paramArrayOfString2, int[] paramArrayOfInt2, boolean paramBoolean, String[] paramArrayOfString3)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramArrayOfString1);
    str = str + Encoder.EncodeParam(paramLong);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString2);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramBoolean);
    str = str + Encoder.EncodeParam(paramArrayOfString3);
    return Encoder.EncodeParam("SetEntry/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSetEntry
 * JD-Core Version:    0.6.1
 */