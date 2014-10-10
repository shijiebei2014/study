package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.io.OutputStream;
import java.util.ArrayList;

abstract class NDXGetQBETableEntryList extends TableEntryListBase
{
  protected String mTableServer;
  protected String mTableSchema;
  protected String mTableVuiName;
  protected int mTableFieldId;
  protected String mServer;
  protected String mSchema;
  protected String mAppName;
  protected long mStartRow;
  protected long mNumRows;
  protected long[] mSortOrder;
  protected String mOwQual;
  protected int[] mOwFieldIds;
  protected String[] mOwFieldValues;
  protected int[] mOwFieldTypes;
  protected int[] mFieldIds;
  protected String[] mFieldValues;
  protected int[] mFieldTypes;
  protected String mEncodedQual;
  protected int mVuiId;
  protected String mEntryToLoad;
  protected String mScreenName;
  protected String mSearchLabel;
  protected int[] mSearchFieldIds;
  protected String[] mSearchFieldVals;
  protected int[] mIRId;
  protected int[] mCCId;

  NDXGetQBETableEntryList(String paramString)
  {
    super(paramString);
  }

  NDXGetQBETableEntryList(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  NDXGetQBETableEntryList()
  {
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetQBETableEntryList");
    if (paramArrayList.size() != 26)
      throw new GoatException("Wrong argument length, spoofed");
    this.mTableServer = ((String)paramArrayList.get(0));
    MLog.fine("mTableServer=" + this.mTableServer);
    this.mTableSchema = ((String)paramArrayList.get(1));
    MLog.fine("mTableSchema=" + this.mTableSchema);
    this.mTableVuiName = ((String)paramArrayList.get(2));
    MLog.fine("mTableVuiName=" + this.mTableVuiName);
    this.mTableFieldId = argToInt((String)paramArrayList.get(3));
    MLog.fine("mTableFieldId=" + this.mTableFieldId);
    this.mServer = ((String)paramArrayList.get(4));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(5));
    MLog.fine("mSchema=" + this.mSchema);
    this.mAppName = ((String)paramArrayList.get(6));
    MLog.fine("mAppName=" + this.mAppName);
    this.mStartRow = argToLong((String)paramArrayList.get(7));
    MLog.fine("mStartRow=" + this.mStartRow);
    this.mNumRows = argToLong((String)paramArrayList.get(8));
    MLog.fine("mNumRows=" + this.mNumRows);
    this.mSortOrder = argToLongArray((String)paramArrayList.get(9));
    StringBuilder localStringBuilder = new StringBuilder("mSortOrder=");
    for (int i = 0; i < this.mSortOrder.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mSortOrder[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mOwQual = ((String)paramArrayList.get(10));
    MLog.fine("mOwQual=" + this.mOwQual);
    this.mOwFieldIds = argToIntArray((String)paramArrayList.get(11));
    localStringBuilder = new StringBuilder("mOwFieldIds=");
    for (i = 0; i < this.mOwFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mOwFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mOwFieldValues = argToStringArray((String)paramArrayList.get(12));
    localStringBuilder = new StringBuilder("mOwFieldValues=");
    for (i = 0; i < this.mOwFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mOwFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mOwFieldTypes = argToIntArray((String)paramArrayList.get(13));
    localStringBuilder = new StringBuilder("mOwFieldTypes=");
    for (i = 0; i < this.mOwFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mOwFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldIds = argToIntArray((String)paramArrayList.get(14));
    localStringBuilder = new StringBuilder("mFieldIds=");
    for (i = 0; i < this.mFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldValues = argToStringArray((String)paramArrayList.get(15));
    localStringBuilder = new StringBuilder("mFieldValues=");
    for (i = 0; i < this.mFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldTypes = argToIntArray((String)paramArrayList.get(16));
    localStringBuilder = new StringBuilder("mFieldTypes=");
    for (i = 0; i < this.mFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mEncodedQual = ((String)paramArrayList.get(17));
    MLog.fine("mEncodedQual=" + this.mEncodedQual);
    this.mVuiId = argToInt((String)paramArrayList.get(18));
    MLog.fine("mVuiId=" + this.mVuiId);
    this.mEntryToLoad = ((String)paramArrayList.get(19));
    MLog.fine("mEntryToLoad=" + this.mEntryToLoad);
    this.mScreenName = ((String)paramArrayList.get(20));
    MLog.fine("mScreenName=" + this.mScreenName);
    this.mSearchLabel = ((String)paramArrayList.get(21));
    MLog.fine("mSearchLabel=" + this.mSearchLabel);
    this.mSearchFieldIds = argToIntArray((String)paramArrayList.get(22));
    localStringBuilder = new StringBuilder("mSearchFieldIds=");
    for (i = 0; i < this.mSearchFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mSearchFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mSearchFieldVals = argToStringArray((String)paramArrayList.get(23));
    localStringBuilder = new StringBuilder("mSearchFieldVals=");
    for (i = 0; i < this.mSearchFieldVals.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mSearchFieldVals[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mIRId = argToIntArray((String)paramArrayList.get(24));
    localStringBuilder = new StringBuilder("mIRId=");
    for (i = 0; i < this.mIRId.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mIRId[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mCCId = argToIntArray((String)paramArrayList.get(25));
    localStringBuilder = new StringBuilder("mCCId=");
    for (i = 0; i < this.mCCId.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mCCId[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetQBETableEntryList
 * JD-Core Version:    0.6.1
 */