package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSaveTableSettings extends NDXRequest
{
  protected String mServer;
  protected String mSchema;
  protected String mVui;
  protected int mTableId;
  protected int[] mColIds;
  protected long[] mColValues;
  protected String mSortOrder;
  protected int mInterval;

  NDXSaveTableSettings(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SaveTableSettings");
    if (paramArrayList.size() != 8)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mVui = ((String)paramArrayList.get(2));
    MLog.fine("mVui=" + this.mVui);
    this.mTableId = argToInt((String)paramArrayList.get(3));
    MLog.fine("mTableId=" + this.mTableId);
    this.mColIds = argToIntArray((String)paramArrayList.get(4));
    StringBuilder localStringBuilder = new StringBuilder("mColIds=");
    for (int i = 0; i < this.mColIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mColIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mColValues = argToLongArray((String)paramArrayList.get(5));
    localStringBuilder = new StringBuilder("mColValues=");
    for (i = 0; i < this.mColValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mColValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mSortOrder = ((String)paramArrayList.get(6));
    MLog.fine("mSortOrder=" + this.mSortOrder);
    this.mInterval = argToInt((String)paramArrayList.get(7));
    MLog.fine("mInterval=" + this.mInterval);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSaveTableSettings
 * JD-Core Version:    0.6.1
 */