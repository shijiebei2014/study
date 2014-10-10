package com.remedy.arsys.backchannelclient;

public class NDXSaveTableSettings
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, int paramInt1, int[] paramArrayOfInt, long[] paramArrayOfLong, String paramString4, int paramInt2)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramInt1);
    str = str + Encoder.EncodeParam(paramArrayOfInt);
    str = str + Encoder.EncodeParam(paramArrayOfLong);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramInt2);
    return Encoder.EncodeParam("SaveTableSettings/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSaveTableSettings
 * JD-Core Version:    0.6.1
 */