package com.remedy.arsys.backchannelclient;

public class NDXGetARUserPreference
{
  static String GetRequestString(int paramInt, String paramString)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramInt);
    str = str + Encoder.EncodeParam(paramString);
    return Encoder.EncodeParam("GetARUserPreference/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetARUserPreference
 * JD-Core Version:    0.6.1
 */