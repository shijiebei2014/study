package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXExecuteService extends NDXRequest
{
  protected String mServer;
  protected String mForm;
  protected String mSourceForm;
  protected String mRequestidStr;
  protected int[] mInIds;
  protected String[] mInValues;
  protected int[] mInTypes;
  protected int[] mOutRefs;
  protected String mArrName;

  NDXExecuteService(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> ExecuteService");
    if (paramArrayList.size() != 9)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mForm = ((String)paramArrayList.get(1));
    MLog.fine("mForm=" + this.mForm);
    this.mSourceForm = ((String)paramArrayList.get(2));
    MLog.fine("mSourceForm=" + this.mSourceForm);
    this.mRequestidStr = ((String)paramArrayList.get(3));
    MLog.fine("mRequestidStr=" + this.mRequestidStr);
    this.mInIds = argToIntArray((String)paramArrayList.get(4));
    StringBuilder localStringBuilder = new StringBuilder("mInIds=");
    for (int i = 0; i < this.mInIds.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mInIds[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mInValues = argToStringArray((String)paramArrayList.get(5));
    localStringBuilder = new StringBuilder("mInValues=");
    for (i = 0; i < this.mInValues.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mInValues[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mInTypes = argToIntArray((String)paramArrayList.get(6));
    localStringBuilder = new StringBuilder("mInTypes=");
    for (i = 0; i < this.mInTypes.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mInTypes[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mOutRefs = argToIntArray((String)paramArrayList.get(7));
    localStringBuilder = new StringBuilder("mOutRefs=");
    for (i = 0; i < this.mOutRefs.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mOutRefs[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mArrName = ((String)paramArrayList.get(8));
    MLog.fine("mArrName=" + this.mArrName);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXExecuteService
 * JD-Core Version:    0.6.1
 */