package com.remedy.arsys.backchannelclient;

public class NDXLoadQueryWidget
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    return Encoder.EncodeParam("LoadQueryWidget/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXLoadQueryWidget
 * JD-Core Version:    0.6.1
 */