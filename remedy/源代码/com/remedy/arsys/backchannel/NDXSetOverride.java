package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSetOverride extends NDXRequest
{
  protected boolean mOverride;

  NDXSetOverride(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SetOverride");
    if (paramArrayList.size() != 1)
      throw new GoatException("Wrong argument length, spoofed");
    this.mOverride = argToBoolean((String)paramArrayList.get(0));
    MLog.fine("mOverride=" + this.mOverride);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSetOverride
 * JD-Core Version:    0.6.1
 */