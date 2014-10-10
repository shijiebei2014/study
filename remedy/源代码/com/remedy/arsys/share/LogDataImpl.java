package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.LogData;
import com.remedy.arsys.stubs.SessionData;

public class LogDataImpl
  implements LogData
{
  private static final Configuration cfg = Configuration.getInstance();
  private static LogData mLogData = new LogDataImpl();

  public static LogData getLogData()
  {
    return mLogData;
  }

  public String getUserName()
  {
    SessionData localSessionData = SessionData.tryGet();
    return localSessionData != null ? localSessionData.getUserName() : null;
  }

  public String getFilePath()
  {
    return cfg.getProperty("arsystem.log_filepath");
  }

  public String getFileName()
  {
    return cfg.getProperty("arsystem.log_filename");
  }

  public String getFileSize()
  {
    return cfg.getProperty("arsystem.log_filesize");
  }

  public String getCategory()
  {
    return cfg.getProperty("arsystem.log_category");
  }

  public String getLevel()
  {
    return cfg.getProperty("arsystem.log_level");
  }

  public String getViewer()
  {
    return cfg.getProperty("arsystem.log_viewer");
  }

  public String getFormat()
  {
    return cfg.getProperty("arsystem.log_format");
  }

  public String getLogUser()
  {
    return cfg.getProperty("arsystem.log_user");
  }

  public int getNumBackups()
  {
    return cfg.getIntProperty("arsystem.log_backups", 10);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.LogDataImpl
 * JD-Core Version:    0.6.1
 */