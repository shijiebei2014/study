package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXExpandMenu extends EntryListBase
{
  protected String mName;
  protected String mServer;
  protected String mSchema;
  protected String mRServer;
  protected String mRSchema;
  protected String mQualification;
  protected String mFilter;
  protected String mAcstyle;
  protected String mArautocmb;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;
  protected int[] mFieldIds;
  protected String[] mFieldValues;
  protected int[] mFieldTypes;
  protected int[] mKeywordIds;
  protected String[] mKeywordVals;
  protected int[] mKeywordTypes;

  NDXExpandMenu(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> ExpandMenu");
    if (paramArrayList.size() != 18)
      throw new GoatException("Wrong argument length, spoofed");
    this.mName = ((String)paramArrayList.get(0));
    MLog.fine("mName=" + this.mName);
    this.mServer = ((String)paramArrayList.get(1));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(2));
    MLog.fine("mSchema=" + this.mSchema);
    this.mRServer = ((String)paramArrayList.get(3));
    MLog.fine("mRServer=" + this.mRServer);
    this.mRSchema = ((String)paramArrayList.get(4));
    MLog.fine("mRSchema=" + this.mRSchema);
    this.mQualification = ((String)paramArrayList.get(5));
    MLog.fine("mQualification=" + this.mQualification);
    this.mFilter = ((String)paramArrayList.get(6));
    MLog.fine("mFilter=" + this.mFilter);
    this.mAcstyle = ((String)paramArrayList.get(7));
    MLog.fine("mAcstyle=" + this.mAcstyle);
    this.mArautocmb = ((String)paramArrayList.get(8));
    MLog.fine("mArautocmb=" + this.mArautocmb);
    this.mQualFieldIds = argToIntArray((String)paramArrayList.get(9));
    StringBuilder localStringBuilder = new StringBuilder("mQualFieldIds=");
    for (int i = 0; i < this.mQualFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldValues = argToStringArray((String)paramArrayList.get(10));
    localStringBuilder = new StringBuilder("mQualFieldValues=");
    for (i = 0; i < this.mQualFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldTypes = argToIntArray((String)paramArrayList.get(11));
    localStringBuilder = new StringBuilder("mQualFieldTypes=");
    for (i = 0; i < this.mQualFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mFieldIds = argToIntArray((String)paramArrayList.get(12));
    localStringBuilder = new StringBuilder("mFieldIds=");
    for (i = 0; i < this.mFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldIds[i]);
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
    this.mKeywordIds = argToIntArray((String)paramArrayList.get(15));
    localStringBuilder = new StringBuilder("mKeywordIds=");
    for (i = 0; i < this.mKeywordIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mKeywordVals = argToStringArray((String)paramArrayList.get(16));
    localStringBuilder = new StringBuilder("mKeywordVals=");
    for (i = 0; i < this.mKeywordVals.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordVals[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mKeywordTypes = argToIntArray((String)paramArrayList.get(17));
    localStringBuilder = new StringBuilder("mKeywordTypes=");
    for (i = 0; i < this.mKeywordTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mKeywordTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXExpandMenu
 * JD-Core Version:    0.6.1
 */