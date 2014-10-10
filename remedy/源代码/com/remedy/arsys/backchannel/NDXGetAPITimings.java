package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetAPITimings extends NDXRequest
{
  protected boolean mFlag;

  NDXGetAPITimings(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetAPITimings");
    if (paramArrayList.size() != 1)
      throw new GoatException("Wrong argument length, spoofed");
    this.mFlag = argToBoolean((String)paramArrayList.get(0));
    MLog.fine("mFlag=" + this.mFlag);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetAPITimings
 * JD-Core Version:    0.6.1
 */