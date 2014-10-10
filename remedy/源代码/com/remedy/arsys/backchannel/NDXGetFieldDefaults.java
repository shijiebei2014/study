package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetFieldDefaults extends NDXRequest
{
  protected String mServer;
  protected String mForm;
  protected int[] mFieldIds;

  NDXGetFieldDefaults(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetFieldDefaults");
    if (paramArrayList.size() != 3)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mForm = ((String)paramArrayList.get(1));
    MLog.fine("mForm=" + this.mForm);
    this.mFieldIds = argToIntArray((String)paramArrayList.get(2));
    StringBuilder localStringBuilder = new StringBuilder("mFieldIds=");
    for (int i = 0; i < this.mFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetFieldDefaults
 * JD-Core Version:    0.6.1
 */