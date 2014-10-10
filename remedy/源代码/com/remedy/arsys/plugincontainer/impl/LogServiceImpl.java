package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.plugincontainer.LogService;

public class LogServiceImpl
  implements LogService
{
  private String mModuleName = null;
  private String mAppendStr = null;
  private static final Log MLog = Log.get(13);

  public LogServiceImpl(String paramString)
  {
    this.mModuleName = paramString;
    this.mAppendStr = ("DV Module: " + this.mModuleName + " - ");
  }

  public void severe(String paramString)
  {
    MLog.severe(this.mAppendStr + paramString);
  }

  public void fine(String paramString)
  {
    MLog.fine(this.mAppendStr + paramString);
  }

  public void warning(String paramString)
  {
    MLog.warning(this.mAppendStr + paramString);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.LogServiceImpl
 * JD-Core Version:    0.6.1
 */