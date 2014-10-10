package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetSQLEntryList extends EntryListBase
{
  protected String mServer;
  protected String mActionName;
  protected int mActionIdx;
  protected int mNoMatchOpt;
  protected int mMultiMatchOpt;
  protected int[] mKeywordIds;
  protected String[] mKeywordValues;
  protected int[] mKeywordTypes;
  protected int[] mFieldIds;
  protected String[] mFieldValues;
  protected int[] mFieldTypes;
  protected boolean mIsAction;

  NDXGetSQLEntryList(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetSQLEntryList");
    if (paramArrayList.size() != 12)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mActionName = ((String)paramArrayList.get(1));
    MLog.fine("mActionName=" + this.mActionName);
    this.mActionIdx = argToInt((String)paramArrayList.get(2));
    MLog.fine("mActionIdx=" + this.mActionIdx);
    this.mNoMatchOpt = argToInt((String)paramArrayList.get(3));
    MLog.fine("mNoMatchOpt=" + this.mNoMatchOpt);
    this.mMultiMatchOpt = argToInt((String)paramArrayList.get(4));
    MLog.fine("mMultiMatchOpt=" + this.mMultiMatchOpt);
    this.mKeywordIds = argToIntArray((String)paramArrayList.get(5));
    StringBuilder localStringBuilder = new StringBuilder("mKeywordIds=");
    for (int i = 0; i < this.mKeywordIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mKeywordValues = argToStringArray((String)paramArrayList.get(6));
    localStringBuilder = new StringBuilder("mKeywordValues=");
    for (i = 0; i < this.mKeywordValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordValues[i]);
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
    this.mFieldValues = argToStringArray((String)paramArrayList.get(9));
    localStringBuilder = new StringBuilder("mFieldValues=");
    for (i = 0; i < this.mFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldValues[i]);
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
    this.mIsAction = argToBoolean((String)paramArrayList.get(11));
    MLog.fine("mIsAction=" + this.mIsAction);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetSQLEntryList
 * JD-Core Version:    0.6.1
 */