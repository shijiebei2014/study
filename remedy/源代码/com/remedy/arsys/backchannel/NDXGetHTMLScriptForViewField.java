package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetHTMLScriptForViewField extends NDXRequest
{
  protected String mServer;
  protected String mAppName;
  protected String mForm;
  protected String mView;
  protected boolean mJsget;
  protected String mCacheid;

  NDXGetHTMLScriptForViewField(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetHTMLScriptForViewField");
    if (paramArrayList.size() != 6)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mAppName = ((String)paramArrayList.get(1));
    MLog.fine("mAppName=" + this.mAppName);
    this.mForm = ((String)paramArrayList.get(2));
    MLog.fine("mForm=" + this.mForm);
    this.mView = ((String)paramArrayList.get(3));
    MLog.fine("mView=" + this.mView);
    this.mJsget = argToBoolean((String)paramArrayList.get(4));
    MLog.fine("mJsget=" + this.mJsget);
    this.mCacheid = ((String)paramArrayList.get(5));
    MLog.fine("mCacheid=" + this.mCacheid);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetHTMLScriptForViewField
 * JD-Core Version:    0.6.1
 */