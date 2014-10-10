package com.remedy.arsys.backchannelclient;

public class NDXMarkAlert
{
  static String GetRequestString(String paramString1, String paramString2, String[] paramArrayOfString, boolean paramBoolean)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    str = str + Encoder.EncodeParam(paramBoolean);
    return Encoder.EncodeParam("MarkAlert/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXMarkAlert
 * JD-Core Version:    0.6.1
 */