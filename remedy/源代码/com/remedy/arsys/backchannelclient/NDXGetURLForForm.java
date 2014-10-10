package com.remedy.arsys.backchannelclient;

public class NDXGetURLForForm
{
  static String GetRequestString(boolean paramBoolean1, String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean2, String paramString5, String paramString6, boolean paramBoolean3)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramBoolean1);
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramBoolean2);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramString6);
    str = str + Encoder.EncodeParam(paramBoolean3);
    return Encoder.EncodeParam("GetURLForForm/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetURLForForm
 * JD-Core Version:    0.6.1
 */