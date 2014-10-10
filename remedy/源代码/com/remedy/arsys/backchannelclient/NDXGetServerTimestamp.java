package com.remedy.arsys.backchannelclient;

public class NDXGetServerTimestamp
{
  static String GetRequestString(String paramString)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString);
    return Encoder.EncodeParam("GetServerTimestamp/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetServerTimestamp
 * JD-Core Version:    0.6.1
 */