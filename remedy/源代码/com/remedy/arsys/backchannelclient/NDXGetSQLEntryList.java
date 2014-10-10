package com.remedy.arsys.backchannelclient;

public class NDXGetSQLEntryList
{
  static String GetRequestString(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, String[] paramArrayOfString1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, String[] paramArrayOfString2, int[] paramArrayOfInt4, boolean paramBoolean)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramInt1);
    str = str + Encoder.EncodeParam(paramInt2);
    str = str + Encoder.EncodeParam(paramInt3);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString1);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramArrayOfInt3);
    str = str + Encoder.EncodeParam(paramArrayOfString2);
    str = str + Encoder.EncodeParam(paramArrayOfInt4);
    str = str + Encoder.EncodeParam(paramBoolean);
    return Encoder.EncodeParam("GetSQLEntryList/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetSQLEntryList
 * JD-Core Version:    0.6.1
 */