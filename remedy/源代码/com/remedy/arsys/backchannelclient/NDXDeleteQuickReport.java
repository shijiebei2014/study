package com.remedy.arsys.backchannelclient;

public class NDXDeleteQuickReport
{
  static String GetRequestString(String paramString1, String paramString2, String[] paramArrayOfString)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    return Encoder.EncodeParam("DeleteQuickReport/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXDeleteQuickReport
 * JD-Core Version:    0.6.1
 */