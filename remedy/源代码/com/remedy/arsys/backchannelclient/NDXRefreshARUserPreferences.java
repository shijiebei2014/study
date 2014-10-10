package com.remedy.arsys.backchannelclient;

public class NDXRefreshARUserPreferences
{
  static String GetRequestString(int paramInt)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramInt);
    return Encoder.EncodeParam("RefreshARUserPreferences/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXRefreshARUserPreferences
 * JD-Core Version:    0.6.1
 */