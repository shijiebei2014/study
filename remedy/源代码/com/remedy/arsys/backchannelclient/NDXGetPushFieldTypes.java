package com.remedy.arsys.backchannelclient;

public class NDXGetPushFieldTypes
{
  static String GetRequestString(String paramString1, String paramString2, int[] paramArrayOfInt)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramArrayOfInt);
    return Encoder.EncodeParam("GetPushFieldTypes/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetPushFieldTypes
 * JD-Core Version:    0.6.1
 */