package com.remedy.arsys.backchannelclient;

public class NDXGetTableEntryList
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6, long paramLong1, long paramLong2, long[] paramArrayOfLong, String paramString7, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, boolean[] paramArrayOfBoolean, boolean paramBoolean, int[] paramArrayOfInt3, int[] paramArrayOfInt4)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramInt);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramString6);
    str = str + Encoder.EncodeParam(paramLong1);
    str = str + Encoder.EncodeParam(paramLong2);
    str = str + Encoder.EncodeParam(paramArrayOfLong);
    str = str + Encoder.EncodeParam(paramString7);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramArrayOfBoolean);
    str = str + Encoder.EncodeParam(paramBoolean);
    str = str + Encoder.EncodeParam(paramArrayOfInt3);
    str = str + Encoder.EncodeParam(paramArrayOfInt4);
    return Encoder.EncodeParam("GetTableEntryList/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetTableEntryList
 * JD-Core Version:    0.6.1
 */