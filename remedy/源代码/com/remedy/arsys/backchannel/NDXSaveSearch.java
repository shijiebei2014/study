package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSaveSearch extends NDXRequest
{
  protected String mName;
  protected String mServer;
  protected String mSchema;
  protected int[] mFieldIds;
  protected String[] mFieldValues;
  protected boolean mNewrecord;
  protected boolean mChangeState;
  protected int mDisable;
  protected int mType;

  NDXSaveSearch(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SaveSearch");
    if (paramArrayList.size() != 9)
      throw new GoatException("Wrong argument length, spoofed");
    this.mName = ((String)paramArrayList.get(0));
    MLog.fine("mName=" + this.mName);
    this.mServer = ((String)paramArrayList.get(1));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(2));
    MLog.fine("mSchema=" + this.mSchema);
    this.mFieldIds = argToIntArray((String)paramArrayList.get(3));
    StringBuilder localStringBuilder = new StringBuilder("mFieldIds=");
    for (int i = 0; i < this.mFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldValues = argToStringArray((String)paramArrayList.get(4));
    localStringBuilder = new StringBuilder("mFieldValues=");
    for (i = 0; i < this.mFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mNewrecord = argToBoolean((String)paramArrayList.get(5));
    MLog.fine("mNewrecord=" + this.mNewrecord);
    this.mChangeState = argToBoolean((String)paramArrayList.get(6));
    MLog.fine("mChangeState=" + this.mChangeState);
    this.mDisable = argToInt((String)paramArrayList.get(7));
    MLog.fine("mDisable=" + this.mDisable);
    this.mType = argToInt((String)paramArrayList.get(8));
    MLog.fine("mType=" + this.mType);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSaveSearch
 * JD-Core Version:    0.6.1
 */