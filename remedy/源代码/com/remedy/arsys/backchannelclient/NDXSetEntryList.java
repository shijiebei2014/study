package com.remedy.arsys.backchannelclient;

public class NDXSetEntryList
{
  static String GetRequestString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int[] paramArrayOfInt1, String[] paramArrayOfString1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, boolean paramBoolean1, int[] paramArrayOfInt3, String[] paramArrayOfString2, int[] paramArrayOfInt4, int paramInt3, boolean paramBoolean2, String[] paramArrayOfString3)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString1);
    str = str + Encoder.EncodeParam(paramString2);
    str = str + Encoder.EncodeParam(paramString3);
    str = str + Encoder.EncodeParam(paramString4);
    str = str + Encoder.EncodeParam(paramString5);
    str = str + Encoder.EncodeParam(paramString6);
    str = str + Encoder.EncodeParam(paramArrayOfInt1);
    str = str + Encoder.EncodeParam(paramArrayOfString1);
    str = str + Encoder.EncodeParam(paramArrayOfInt2);
    str = str + Encoder.EncodeParam(paramInt1);
    str = str + Encoder.EncodeParam(paramInt2);
    str = str + Encoder.EncodeParam(paramBoolean1);
    str = str + Encoder.EncodeParam(paramArrayOfInt3);
    str = str + Encoder.EncodeParam(paramArrayOfString2);
    str = str + Encoder.EncodeParam(paramArrayOfInt4);
    str = str + Encoder.EncodeParam(paramInt3);
    str = str + Encoder.EncodeParam(paramBoolean2);
    str = str + Encoder.EncodeParam(paramArrayOfString3);
    return Encoder.EncodeParam("SetEntryList/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXSetEntryList
 * JD-Core Version:    0.6.1
 */