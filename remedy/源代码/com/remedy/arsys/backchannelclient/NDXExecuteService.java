package com.remedy.arsys.backchannelclient;

public class NDXExecuteService
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, int[] paramArrayOfInt3, String paramString5)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramArrayOfInt3);
    str = str + Encoder.EncodeParam(paramString5);
    return Encoder.EncodeParam("ExecuteService/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXExecuteService
 * JD-Core Version:    0.6.1
 */