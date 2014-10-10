package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetEntryList extends EntryListBase
{
  protected String mCurrentServer;
  protected String mCurrentSchema;
  protected String mCurrentVui;
  protected String mServer;
  protected String mSchema;
  protected String mAppName;
  protected String mQualification;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;
  protected boolean[] mQualFieldExterns;
  protected boolean mCompile;
  protected int mNoMatchOpt;
  protected int mMultiMatchOpt;
  protected int[] mFields;

  NDXGetEntryList(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetEntryList");
    if (paramArrayList.size() != 15)
      throw new GoatException("Wrong argument length, spoofed");
    this.mCurrentServer = ((String)paramArrayList.get(0));
    MLog.fine("mCurrentServer=" + this.mCurrentServer);
    this.mCurrentSchema = ((String)paramArrayList.get(1));
    MLog.fine("mCurrentSchema=" + this.mCurrentSchema);
    this.mCurrentVui = ((String)paramArrayList.get(2));
    MLog.fine("mCurrentVui=" + this.mCurrentVui);
    this.mServer = ((String)paramArrayList.get(3));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(4));
    MLog.fine("mSchema=" + this.mSchema);
    this.mAppName = ((String)paramArrayList.get(5));
    MLog.fine("mAppName=" + this.mAppName);
    this.mQualification = ((String)paramArrayList.get(6));
    MLog.fine("mQualification=" + this.mQualification);
    this.mQualFieldIds = argToIntArray((String)paramArrayList.get(7));
    StringBuilder localStringBuilder = new StringBuilder("mQualFieldIds=");
    for (int i = 0; i < this.mQualFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldValues = argToStringArray((String)paramArrayList.get(8));
    localStringBuilder = new StringBuilder("mQualFieldValues=");
    for (i = 0; i < this.mQualFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldTypes = argToIntArray((String)paramArrayList.get(9));
    localStringBuilder = new StringBuilder("mQualFieldTypes=");
    for (i = 0; i < this.mQualFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldExterns = argToBooleanArray((String)paramArrayList.get(10));
    localStringBuilder = new StringBuilder("mQualFieldExterns=");
    for (i = 0; i < this.mQualFieldExterns.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldExterns[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mCompile = argToBoolean((String)paramArrayList.get(11));
    MLog.fine("mCompile=" + this.mCompile);
    this.mNoMatchOpt = argToInt((String)paramArrayList.get(12));
    MLog.fine("mNoMatchOpt=" + this.mNoMatchOpt);
    this.mMultiMatchOpt = argToInt((String)paramArrayList.get(13));
    MLog.fine("mMultiMatchOpt=" + this.mMultiMatchOpt);
    this.mFields = argToIntArray((String)paramArrayList.get(14));
    localStringBuilder = new StringBuilder("mFields=");
    for (i = 0; i < this.mFields.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFields[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetEntryList
 * JD-Core Version:    0.6.1
 */