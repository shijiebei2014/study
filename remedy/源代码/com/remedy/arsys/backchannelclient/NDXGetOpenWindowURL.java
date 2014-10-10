package com.remedy.arsys.backchannelclient;

public class NDXGetOpenWindowURL
{
  static String GetRequestString(boolean paramBoolean1, String paramString1, String paramString2, String paramString3, String paramString4, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, boolean paramBoolean2, boolean paramBoolean3, String paramString5, String paramString6, String paramString7, long[] paramArrayOfLong)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramBoolean1);
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramBoolean2);
    str = str + Encoder.EncodeParam(paramBoolean3);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramString6);
    str = str + Encoder.EncodeParam(paramString7);
    str = str + Encoder.EncodeParam(paramArrayOfLong);
    return Encoder.EncodeParam("GetOpenWindowURL/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetOpenWindowURL
 * JD-Core Version:    0.6.1
 */