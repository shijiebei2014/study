package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXCompileExternalQualification extends EntryListBase
{
  protected String mCurrentServer;
  protected String mCurrentSchema;
  protected String mCurrentVui;
  protected String mRemoteServer;
  protected String mRemoteSchema;
  protected String mQualification;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;

  NDXCompileExternalQualification(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> CompileExternalQualification");
    if (paramArrayList.size() != 9)
      throw new GoatException("Wrong argument length, spoofed");
    this.mCurrentServer = ((String)paramArrayList.get(0));
    MLog.fine("mCurrentServer=" + this.mCurrentServer);
    this.mCurrentSchema = ((String)paramArrayList.get(1));
    MLog.fine("mCurrentSchema=" + this.mCurrentSchema);
    this.mCurrentVui = ((String)paramArrayList.get(2));
    MLog.fine("mCurrentVui=" + this.mCurrentVui);
    this.mRemoteServer = ((String)paramArrayList.get(3));
    MLog.fine("mRemoteServer=" + this.mRemoteServer);
    this.mRemoteSchema = ((String)paramArrayList.get(4));
    MLog.fine("mRemoteSchema=" + this.mRemoteSchema);
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
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXCompileExternalQualification
 * JD-Core Version:    0.6.1
 */