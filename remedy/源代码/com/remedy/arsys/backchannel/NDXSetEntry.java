package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSetEntry extends EntryListBase
{
  protected String mServer;
  protected String mSchema;
  protected String mScreenName;
  protected String[] mEntryIds;
  protected long mTimestamp;
  protected int[] mFields;
  protected String[] mFieldValues;
  protected int[] mFieldTypes;
  protected boolean mReload;
  protected String[] mTDirtyData;

  NDXSetEntry(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SetEntry");
    if (paramArrayList.size() != 10)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mScreenName = ((String)paramArrayList.get(2));
    MLog.fine("mScreenName=" + this.mScreenName);
    this.mEntryIds = argToStringArray((String)paramArrayList.get(3));
    StringBuilder localStringBuilder = new StringBuilder("mEntryIds=");
    for (int i = 0; i < this.mEntryIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mEntryIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mTimestamp = argToLong((String)paramArrayList.get(4));
    MLog.fine("mTimestamp=" + this.mTimestamp);
    this.mFields = argToIntArray((String)paramArrayList.get(5));
    localStringBuilder = new StringBuilder("mFields=");
    for (i = 0; i < this.mFields.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFields[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldValues = argToStringArray((String)paramArrayList.get(6));
    localStringBuilder = new StringBuilder("mFieldValues=");
    for (i = 0; i < this.mFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldTypes = argToIntArray((String)paramArrayList.get(7));
    localStringBuilder = new StringBuilder("mFieldTypes=");
    for (i = 0; i < this.mFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mReload = argToBoolean((String)paramArrayList.get(8));
    MLog.fine("mReload=" + this.mReload);
    this.mTDirtyData = argToStringArray((String)paramArrayList.get(9));
    localStringBuilder = new StringBuilder("mTDirtyData=");
    for (i = 0; i < this.mTDirtyData.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mTDirtyData[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSetEntry
 * JD-Core Version:    0.6.1
 */