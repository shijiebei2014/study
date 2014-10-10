package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSetEntryList extends EntryListBase
{
  protected String mCurrentServer;
  protected String mCurrentSchema;
  protected String mServer;
  protected String mSchema;
  protected String mCurEntryId;
  protected String mQualification;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;
  protected int mNoMatchOpt;
  protected int mMultiMatchOpt;
  protected boolean mLikeId;
  protected int[] mFields;
  protected String[] mFieldValues;
  protected int[] mFieldTypes;
  protected int mReqIdfield;
  protected boolean mReload;
  protected String[] mTDirtyData;

  NDXSetEntryList(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SetEntryList");
    if (paramArrayList.size() != 18)
      throw new GoatException("Wrong argument length, spoofed");
    this.mCurrentServer = ((String)paramArrayList.get(0));
    MLog.fine("mCurrentServer=" + this.mCurrentServer);
    this.mCurrentSchema = ((String)paramArrayList.get(1));
    MLog.fine("mCurrentSchema=" + this.mCurrentSchema);
    this.mServer = ((String)paramArrayList.get(2));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(3));
    MLog.fine("mSchema=" + this.mSchema);
    this.mCurEntryId = ((String)paramArrayList.get(4));
    MLog.fine("mCurEntryId=" + this.mCurEntryId);
    this.mQualification = ((String)paramArrayList.get(5));
    MLog.fine("mQualification=" + this.mQualification);
    this.mQualFieldIds = argToIntArray((String)paramArrayList.get(6));
    StringBuilder localStringBuilder = new StringBuilder("mQualFieldIds=");
    for (int i = 0; i < this.mQualFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldValues = argToStringArray((String)paramArrayList.get(7));
    localStringBuilder = new StringBuilder("mQualFieldValues=");
    for (i = 0; i < this.mQualFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldTypes = argToIntArray((String)paramArrayList.get(8));
    localStringBuilder = new StringBuilder("mQualFieldTypes=");
    for (i = 0; i < this.mQualFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mNoMatchOpt = argToInt((String)paramArrayList.get(9));
    MLog.fine("mNoMatchOpt=" + this.mNoMatchOpt);
    this.mMultiMatchOpt = argToInt((String)paramArrayList.get(10));
    MLog.fine("mMultiMatchOpt=" + this.mMultiMatchOpt);
    this.mLikeId = argToBoolean((String)paramArrayList.get(11));
    MLog.fine("mLikeId=" + this.mLikeId);
    this.mFields = argToIntArray((String)paramArrayList.get(12));
    localStringBuilder = new StringBuilder("mFields=");
    for (i = 0; i < this.mFields.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFields[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldValues = argToStringArray((String)paramArrayList.get(13));
    localStringBuilder = new StringBuilder("mFieldValues=");
    for (i = 0; i < this.mFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldTypes = argToIntArray((String)paramArrayList.get(14));
    localStringBuilder = new StringBuilder("mFieldTypes=");
    for (i = 0; i < this.mFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mReqIdfield = argToInt((String)paramArrayList.get(15));
    MLog.fine("mReqIdfield=" + this.mReqIdfield);
    this.mReload = argToBoolean((String)paramArrayList.get(16));
    MLog.fine("mReload=" + this.mReload);
    this.mTDirtyData = argToStringArray((String)paramArrayList.get(17));
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
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSetEntryList
 * JD-Core Version:    0.6.1
 */