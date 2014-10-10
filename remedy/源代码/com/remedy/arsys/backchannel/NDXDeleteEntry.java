package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXDeleteEntry extends NDXRequest
{
  protected String mServer;
  protected String mForm;
  protected String[] mEntries;

  NDXDeleteEntry(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> DeleteEntry");
    if (paramArrayList.size() != 3)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mForm = ((String)paramArrayList.get(1));
    MLog.fine("mForm=" + this.mForm);
    this.mEntries = argToStringArray((String)paramArrayList.get(2));
    StringBuilder localStringBuilder = new StringBuilder("mEntries=");
    for (int i = 0; i < this.mEntries.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mEntries[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXDeleteEntry
 * JD-Core Version:    0.6.1
 */