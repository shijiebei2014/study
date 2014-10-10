package com.remedy.arsys.backchannelclient;

public class NDXGetARTemplate
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramString6);
    return Encoder.EncodeParam("GetARTemplate/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetARTemplate
 * JD-Core Version:    0.6.1
 */