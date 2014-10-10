package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetMenuDefinition extends NDXRequest
{
  protected String mName;
  protected String mServer;

  NDXGetMenuDefinition(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetMenuDefinition");
    if (paramArrayList.size() != 2)
      throw new GoatException("Wrong argument length, spoofed");
    this.mName = ((String)paramArrayList.get(0));
    MLog.fine("mName=" + this.mName);
    this.mServer = ((String)paramArrayList.get(1));
    MLog.fine("mServer=" + this.mServer);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetMenuDefinition
 * JD-Core Version:    0.6.1
 */