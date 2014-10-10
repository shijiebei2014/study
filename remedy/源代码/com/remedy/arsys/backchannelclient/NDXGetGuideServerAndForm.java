package com.remedy.arsys.backchannelclient;

public class NDXGetGuideServerAndForm
{
  static String GetRequestString(String paramString1, String paramString2)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    return Encoder.EncodeParam("GetGuideServerAndForm/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetGuideServerAndForm
 * JD-Core Version:    0.6.1
 */