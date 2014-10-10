package com.remedy.arsys.backchannelclient;

public class NDXGetAPITimings
{
  static String GetRequestString(boolean paramBoolean)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramBoolean);
    return Encoder.EncodeParam("GetAPITimings/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetAPITimings
 * JD-Core Version:    0.6.1
 */