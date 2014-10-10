package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.io.OutputStream;
import java.util.ArrayList;

abstract class NDXGetTableEntryList extends TableEntryListBase
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
  protected String mQualification;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;
  protected boolean[] mQualFieldExterns;
  protected boolean mCompile;
  protected int[] mIRId;
  protected int[] mCCId;

  NDXGetTableEntryList(String paramString)
  {
    super(paramString);
  }

  NDXGetTableEntryList(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetTableEntryList");
    if (paramArrayList.size() != 18)
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
    this.mQualification = ((String)paramArrayList.get(10));
    MLog.fine("mQualification=" + this.mQualification);
    this.mQualFieldIds = argToIntArray((String)paramArrayList.get(11));
    localStringBuilder = new StringBuilder("mQualFieldIds=");
    for (i = 0; i < this.mQualFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldValues = argToStringArray((String)paramArrayList.get(12));
    localStringBuilder = new StringBuilder("mQualFieldValues=");
    for (i = 0; i < this.mQualFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldTypes = argToIntArray((String)paramArrayList.get(13));
    localStringBuilder = new StringBuilder("mQualFieldTypes=");
    for (i = 0; i < this.mQualFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldExterns = argToBooleanArray((String)paramArrayList.get(14));
    localStringBuilder = new StringBuilder("mQualFieldExterns=");
    for (i = 0; i < this.mQualFieldExterns.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldExterns[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mCompile = argToBoolean((String)paramArrayList.get(15));
    MLog.fine("mCompile=" + this.mCompile);
    this.mIRId = argToIntArray((String)paramArrayList.get(16));
    localStringBuilder = new StringBuilder("mIRId=");
    for (i = 0; i < this.mIRId.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mIRId[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mCCId = argToIntArray((String)paramArrayList.get(17));
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
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetTableEntryList
 * JD-Core Version:    0.6.1
 */