package com.remedy.arsys.backchannelclient;

public class NDXGetBulkTableEntryList
{
  static String GetRequestString(String[] paramArrayOfString)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramArrayOfString);
    return Encoder.EncodeParam("GetBulkTableEntryList/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetBulkTableEntryList
 * JD-Core Version:    0.6.1
 */