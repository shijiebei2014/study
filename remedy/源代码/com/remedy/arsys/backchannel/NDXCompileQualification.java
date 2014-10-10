package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXCompileQualification extends NDXRequest
{
  protected String mServer;
  protected String mSchema;
  protected String mVui;
  protected String mQualification;

  NDXCompileQualification(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> CompileQualification");
    if (paramArrayList.size() != 4)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(1));
    MLog.fine("mSchema=" + this.mSchema);
    this.mVui = ((String)paramArrayList.get(2));
    MLog.fine("mVui=" + this.mVui);
    this.mQualification = ((String)paramArrayList.get(3));
    MLog.fine("mQualification=" + this.mQualification);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXCompileQualification
 * JD-Core Version:    0.6.1
 */