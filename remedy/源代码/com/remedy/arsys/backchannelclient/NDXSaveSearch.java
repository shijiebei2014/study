package com.remedy.arsys.backchannelclient;

public class NDXSaveSearch
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, int[] paramArrayOfInt, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramArrayOfInt);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    str = str + Encoder.EncodeParam(paramBoolean1);
    str = str + Encoder.EncodeParam(paramBoolean2);
    str = str + Encoder.EncodeParam(paramInt1);
    str = str + Encoder.EncodeParam(paramInt2);
    return Encoder.EncodeParam("SaveSearch/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSaveSearch
 * JD-Core Version:    0.6.1
 */