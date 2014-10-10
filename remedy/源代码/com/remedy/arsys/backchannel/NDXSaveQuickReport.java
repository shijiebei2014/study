package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSaveQuickReport extends NDXRequest
{
  protected String mName;
  protected String mSchema;
  protected String mServer;
  protected String mReportQual;
  protected boolean mNewrecord;
  protected boolean mDefinition;
  protected boolean mDisable;
  protected String[] mFieldID;

  NDXSaveQuickReport(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SaveQuickReport");
    if (paramArrayList.size() != 8)
      throw new GoatException("Wrong argument length, spoofed");
    this.mName = ((String)paramArrayList.get(0));
    MLog.fine("mName=" + this.mName);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mServer = ((String)paramArrayList.get(2));
    MLog.fine("mServer=" + this.mServer);
    this.mReportQual = ((String)paramArrayList.get(3));
    MLog.fine("mReportQual=" + this.mReportQual);
    this.mNewrecord = argToBoolean((String)paramArrayList.get(4));
    MLog.fine("mNewrecord=" + this.mNewrecord);
    this.mDefinition = argToBoolean((String)paramArrayList.get(5));
    MLog.fine("mDefinition=" + this.mDefinition);
    this.mDisable = argToBoolean((String)paramArrayList.get(6));
    MLog.fine("mDisable=" + this.mDisable);
    this.mFieldID = argToStringArray((String)paramArrayList.get(7));
    StringBuilder localStringBuilder = new StringBuilder("mFieldID=");
    for (int i = 0; i < this.mFieldID.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mFieldID[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSaveQuickReport
 * JD-Core Version:    0.6.1
 */