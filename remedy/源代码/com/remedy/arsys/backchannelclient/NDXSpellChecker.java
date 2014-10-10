package com.remedy.arsys.backchannelclient;

public class NDXSpellChecker
{
  static String GetRequestString(String[] paramArrayOfString, String paramString)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramArrayOfString);
    str = str + Encoder.EncodeParam(paramString);
    return Encoder.EncodeParam("SpellChecker/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSpellChecker
 * JD-Core Version:    0.6.1
 */