package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetEntry extends EntryListBase
{
  protected String mServer;
  protected String mSchema;
  protected String mAppName;
  protected String mScreenName;
  protected String mEntryId;
  protected int[] mFields;
  protected boolean mDeferLargeDiary;

  NDXGetEntry(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetEntry");
    if (paramArrayList.size() != 7)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mAppName = ((String)paramArrayList.get(2));
    MLog.fine("mAppName=" + this.mAppName);
    this.mScreenName = ((String)paramArrayList.get(3));
    MLog.fine("mScreenName=" + this.mScreenName);
    this.mEntryId = ((String)paramArrayList.get(4));
    MLog.fine("mEntryId=" + this.mEntryId);
    this.mFields = argToIntArray((String)paramArrayList.get(5));
    StringBuilder localStringBuilder = new StringBuilder("mFields=");
    for (int i = 0; i < this.mFields.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFields[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mDeferLargeDiary = argToBoolean((String)paramArrayList.get(6));
    MLog.fine("mDeferLargeDiary=" + this.mDeferLargeDiary);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetEntry
 * JD-Core Version:    0.6.1
 */