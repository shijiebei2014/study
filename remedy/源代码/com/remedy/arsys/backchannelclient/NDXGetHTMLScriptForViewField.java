package com.remedy.arsys.backchannelclient;

public class NDXGetHTMLScriptForViewField
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean, String paramString5)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramBoolean);
    str = str + Encoder.EncodeParam(paramString5);
    return Encoder.EncodeParam("GetHTMLScriptForViewField/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetHTMLScriptForViewField
 * JD-Core Version:    0.6.1
 */