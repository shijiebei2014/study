package com.remedy.arsys.backchannelclient;

public class NDXSetARUserPreference
{
  static String GetRequestString(int paramInt, String paramString1, String paramString2)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramInt);
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    return Encoder.EncodeParam("SetARUserPreference/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSetARUserPreference
 * JD-Core Version:    0.6.1
 */