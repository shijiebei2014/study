package com.remedy.arsys.backchannelclient;

public class NDXGetCurrencyExchangeRates
{
  static String GetRequestString(String paramString, long paramLong)
  {
    String str = "";
    str = str + Encoder.EncodeParam(paramString);
    str = str + Encoder.EncodeParam(paramLong);
    return Encoder.EncodeParam("GetCurrencyExchangeRates/" + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.NDXGetCurrencyExchangeRates
 * JD-Core Version:    0.6.1
 */