package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetOpenWindowQueryVF extends NDXRequest
{
  protected String mServer;
  protected String mSchema;
  protected String mAppName;
  protected String mQualification;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;
  protected String mView;
  protected String mWinName;
  protected long[] mSortOrder;

  NDXGetOpenWindowQueryVF(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetOpenWindowQueryVF");
    if (paramArrayList.size() != 10)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mAppName = ((String)paramArrayList.get(2));
    MLog.fine("mAppName=" + this.mAppName);
    this.mQualification = ((String)paramArrayList.get(3));
    MLog.fine("mQualification=" + this.mQualification);
    this.mQualFieldIds = argToIntArray((String)paramArrayList.get(4));
    StringBuilder localStringBuilder = new StringBuilder("mQualFieldIds=");
    for (int i = 0; i < this.mQualFieldIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldValues = argToStringArray((String)paramArrayList.get(5));
    localStringBuilder = new StringBuilder("mQualFieldValues=");
    for (i = 0; i < this.mQualFieldValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldTypes = argToIntArray((String)paramArrayList.get(6));
    localStringBuilder = new StringBuilder("mQualFieldTypes=");
    for (i = 0; i < this.mQualFieldTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mView = ((String)paramArrayList.get(7));
    MLog.fine("mView=" + this.mView);
    this.mWinName = ((String)paramArrayList.get(8));
    MLog.fine("mWinName=" + this.mWinName);
    this.mSortOrder = argToLongArray((String)paramArrayList.get(9));
    localStringBuilder = new StringBuilder("mSortOrder=");
    for (i = 0; i < this.mSortOrder.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mSortOrder[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetOpenWindowQueryVF
 * JD-Core Version:    0.6.1
 */