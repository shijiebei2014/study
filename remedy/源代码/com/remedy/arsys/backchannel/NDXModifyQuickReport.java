package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXModifyQuickReport extends NDXRequest
{
  protected String mServer;
  protected String mSchema;
  protected String[] mName;
  protected int[] mDisable;

  NDXModifyQuickReport(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> ModifyQuickReport");
    if (paramArrayList.size() != 4)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mName = argToStringArray((String)paramArrayList.get(2));
    StringBuilder localStringBuilder = new StringBuilder("mName=");
    for (int i = 0; i < this.mName.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mName[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mDisable = argToIntArray((String)paramArrayList.get(3));
    localStringBuilder = new StringBuilder("mDisable=");
    for (i = 0; i < this.mDisable.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mDisable[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXModifyQuickReport
 * JD-Core Version:    0.6.1
 */