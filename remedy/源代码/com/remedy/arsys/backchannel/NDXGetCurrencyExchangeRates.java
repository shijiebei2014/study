package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetCurrencyExchangeRates extends NDXRequest
{
  protected String mServer;
  protected long mDate;

  NDXGetCurrencyExchangeRates(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetCurrencyExchangeRates");
    if (paramArrayList.size() != 2)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mDate = argToLong((String)paramArrayList.get(1));
    MLog.fine("mDate=" + this.mDate);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetCurrencyExchangeRates
 * JD-Core Version:    0.6.1
 */