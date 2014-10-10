package com.remedy.arsys.backchannelclient;

public class NDXSaveQuickReport
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String[] paramArrayOfString)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramBoolean1);
    str = str + Encoder.EncodeParam(paramBoolean2);
    str = str + Encoder.EncodeParam(paramBoolean3);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    return Encoder.EncodeParam("SaveQuickReport/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSaveQuickReport
 * JD-Core Version:    0.6.1
 */