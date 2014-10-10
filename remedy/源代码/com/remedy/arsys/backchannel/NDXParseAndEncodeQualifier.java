package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXParseAndEncodeQualifier extends NDXRequest
{
  protected String mServer;
  protected String mForm;
  protected String mView;
  protected String mQualifier;

  NDXParseAndEncodeQualifier(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> ParseAndEncodeQualifier");
    if (paramArrayList.size() != 4)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mForm = ((String)paramArrayList.get(1));
    MLog.fine("mForm=" + this.mForm);
    this.mView = ((String)paramArrayList.get(2));
    MLog.fine("mView=" + this.mView);
    this.mQualifier = ((String)paramArrayList.get(3));
    MLog.fine("mQualifier=" + this.mQualifier);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXParseAndEncodeQualifier
 * JD-Core Version:    0.6.1
 */