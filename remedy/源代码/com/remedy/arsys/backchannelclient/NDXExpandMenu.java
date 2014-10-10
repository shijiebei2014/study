package com.remedy.arsys.backchannelclient;

public class NDXExpandMenu
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, int[] paramArrayOfInt1, String[] paramArrayOfString1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, String[] paramArrayOfString2, int[] paramArrayOfInt4, int[] paramArrayOfInt5, String[] paramArrayOfString3, int[] paramArrayOfInt6)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramString6);
    str = str + Encoder.EncodeParam(paramString7);
    str = str + Encoder.EncodeParam(paramString8);
    str = str + Encoder.EncodeParam(paramString9);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString1);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramArrayOfInt3);
    str = str + Encoder.EncodeParam(paramArrayOfString2);
    str = str + Encoder.EncodeParam(paramArrayOfInt4);
    str = str + Encoder.EncodeParam(paramArrayOfInt5);
    str = str + Encoder.EncodeParam(paramArrayOfString3);
    str = str + Encoder.EncodeParam(paramArrayOfInt6);
    return Encoder.EncodeParam("ExpandMenu/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXExpandMenu
 * JD-Core Version:    0.6.1
 */