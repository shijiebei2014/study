package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXDeleteQuickReport extends NDXRequest
{
  protected String mServer;
  protected String mSchema;
  protected String[] mNames;

  NDXDeleteQuickReport(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> DeleteQuickReport");
    if (paramArrayList.size() != 3)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mNames = argToStringArray((String)paramArrayList.get(2));
    StringBuilder localStringBuilder = new StringBuilder("mNames=");
    for (int i = 0; i < this.mNames.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mNames[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXDeleteQuickReport
 * JD-Core Version:    0.6.1
 */