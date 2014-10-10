package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXLoadQueryWidget extends NDXRequest
{
  protected String mGuid;
  protected String mQual;
  protected String mServer;
  protected String mLocale;

  NDXLoadQueryWidget(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> LoadQueryWidget");
    if (paramArrayList.size() != 4)
      throw new GoatException("Wrong argument length, spoofed");
    this.mGuid = ((String)paramArrayList.get(0));
    MLog.fine("mGuid=" + this.mGuid);
    this.mQual = ((String)paramArrayList.get(1));
    MLog.fine("mQual=" + this.mQual);
    this.mServer = ((String)paramArrayList.get(2));
    MLog.fine("mServer=" + this.mServer);
    this.mLocale = ((String)paramArrayList.get(3));
    MLog.fine("mLocale=" + this.mLocale);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXLoadQueryWidget
 * JD-Core Version:    0.6.1
 */