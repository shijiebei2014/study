package com.remedy.arsys.backchannelclient;

public class NDXSetOverride
{
  static String GetRequestString(boolean paramBoolean)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramBoolean);
    return Encoder.EncodeParam("SetOverride/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSetOverride
 * JD-Core Version:    0.6.1
 */