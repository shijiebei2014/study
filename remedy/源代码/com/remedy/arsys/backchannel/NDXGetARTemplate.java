package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetARTemplate extends NDXRequest
{
  protected String mServer;
  protected String mTemplate;
  protected String mContextPath;
  protected String mCacheid;
  protected String mForm;
  protected String mVui;

  NDXGetARTemplate(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetARTemplate");
    if (paramArrayList.size() != 6)
      throw new GoatException("Wrong argument length, spoofed");
    this.mServer = ((String)paramArrayList.get(0));
    MLog.fine("mServer=" + this.mServer);
    this.mTemplate = ((String)paramArrayList.get(1));
    MLog.fine("mTemplate=" + this.mTemplate);
    this.mContextPath = ((String)paramArrayList.get(2));
    MLog.fine("mContextPath=" + this.mContextPath);
    this.mCacheid = ((String)paramArrayList.get(3));
    MLog.fine("mCacheid=" + this.mCacheid);
    this.mForm = ((String)paramArrayList.get(4));
    MLog.fine("mForm=" + this.mForm);
    this.mVui = ((String)paramArrayList.get(5));
    MLog.fine("mVui=" + this.mVui);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetARTemplate
 * JD-Core Version:    0.6.1
 */