package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.CurrencyRatios;
import com.remedy.arsys.goat.CurrencyRatios.CurrencyRatio;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class GetCurrencyExchangeRatesAgent extends NDXGetCurrencyExchangeRates
{
  public GetCurrencyExchangeRatesAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    CurrencyRatios.CurrencyRatio localCurrencyRatio = CurrencyRatios.GetCurrencyRatios(this.mServer, this.mDate);
    append("this.result={");
    if (localCurrencyRatio != null)
    {
      String[] arrayOfString = localCurrencyRatio.ratios.split("/");
      for (int i = 0; i < arrayOfString.length; i++)
        if (arrayOfString[i].length() > 6)
          append("\"").append(arrayOfString[i].substring(0, 6)).append("\":new BigDecimal(\"").append(arrayOfString[i].substring(6)).append("\"),");
      append("\"expires\":" + localCurrencyRatio.dateExpires / 1000L);
    }
    append("}");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetCurrencyExchangeRatesAgent
 * JD-Core Version:    0.6.1
 */