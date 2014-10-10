package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetARUserPreference extends NDXRequest
{
  protected int mFieldID;
  protected String mPrefKey;

  NDXGetARUserPreference(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetARUserPreference");
    if (paramArrayList.size() != 2)
      throw new GoatException("Wrong argument length, spoofed");
    this.mFieldID = argToInt((String)paramArrayList.get(0));
    MLog.fine("mFieldID=" + this.mFieldID);
    this.mPrefKey = ((String)paramArrayList.get(1));
    MLog.fine("mPrefKey=" + this.mPrefKey);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetARUserPreference
 * JD-Core Version:    0.6.1
 */