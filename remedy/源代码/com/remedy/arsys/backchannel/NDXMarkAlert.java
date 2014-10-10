package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXMarkAlert extends EntryListBase
{
  protected String mServer;
  protected String mSchema;
  protected String[] mIds;
  protected boolean mRead;

  NDXMarkAlert(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> MarkAlert");
    if (paramArrayList.size() != 4)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mIds = argToStringArray((String)paramArrayList.get(2));
    StringBuilder localStringBuilder = new StringBuilder("mIds=");
    for (int i = 0; i < this.mIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mRead = argToBoolean((String)paramArrayList.get(3));
    MLog.fine("mRead=" + this.mRead);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXMarkAlert
 * JD-Core Version:    0.6.1
 */