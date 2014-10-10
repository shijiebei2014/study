package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXServerRunProcess extends EntryListBase
{
  protected String mServer;
  protected String mAlName;
  protected int mActIdx;
  protected long mTs;
  protected int mFid;
  protected int[] mKeywordIds;
  protected String[] mKeywordVals;
  protected int[] mKeywordTypes;
  protected int[] mFieldIds;
  protected String[] mFieldVals;
  protected int[] mFieldTypes;

  NDXServerRunProcess(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> ServerRunProcess");
    if (paramArrayList.size() != 11)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mAlName = ((String)paramArrayList.get(1));
    MLog.fine("mAlName=" + this.mAlName);
    this.mActIdx = argToInt((String)paramArrayList.get(2));
    MLog.fine("mActIdx=" + this.mActIdx);
    this.mTs = argToLong((String)paramArrayList.get(3));
    MLog.fine("mTs=" + this.mTs);
    this.mFid = argToInt((String)paramArrayList.get(4));
    MLog.fine("mFid=" + this.mFid);
    this.mKeywordIds = argToIntArray((String)paramArrayList.get(5));
    StringBuilder localStringBuilder = new StringBuilder("mKeywordIds=");
    for (int i = 0; i < this.mKeywordIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mKeywordVals = argToStringArray((String)paramArrayList.get(6));
    localStringBuilder = new StringBuilder("mKeywordVals=");
    for (i = 0; i < this.mKeywordVals.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordVals[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mKeywordTypes = argToIntArray((String)paramArrayList.get(7));
    localStringBuilder = new StringBuilder("mKeywordTypes=");
    for (i = 0; i < this.mKeywordTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldIds = argToIntArray((String)paramArrayList.get(8));
    localStringBuilder = new StringBuilder("mFieldIds=");
    for (i = 0; i < this.mFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldVals = argToStringArray((String)paramArrayList.get(9));
    localStringBuilder = new StringBuilder("mFieldVals=");
    for (i = 0; i < this.mFieldVals.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldVals[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldTypes = argToIntArray((String)paramArrayList.get(10));
    localStringBuilder = new StringBuilder("mFieldTypes=");
    for (i = 0; i < this.mFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXServerRunProcess
 * JD-Core Version:    0.6.1
 */