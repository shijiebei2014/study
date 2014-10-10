package com.remedy.arsys.backchannelclient;

public class NDXGetEntry
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int[] paramArrayOfInt, boolean paramBoolean)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramArrayOfInt);
    str = str + Encoder.EncodeParam(paramBoolean);
    return Encoder.EncodeParam("GetEntry/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetEntry
 * JD-Core Version:    0.6.1
 */