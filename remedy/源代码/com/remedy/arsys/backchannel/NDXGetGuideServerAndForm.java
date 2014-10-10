package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetGuideServerAndForm extends NDXRequest
{
  protected String mGuideServer;
  protected String mGuideName;

  NDXGetGuideServerAndForm(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetGuideServerAndForm");
    if (paramArrayList.size() != 2)
      throw new GoatException("Wrong argument length, spoofed");
    this.mGuideServer = ((String)paramArrayList.get(0));
    MLog.fine("mGuideServer=" + this.mGuideServer);
    this.mGuideName = ((String)paramArrayList.get(1));
    MLog.fine("mGuideName=" + this.mGuideName);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetGuideServerAndForm
 * JD-Core Version:    0.6.1
 */