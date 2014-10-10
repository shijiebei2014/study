package com.remedy.arsys.arwebreport;

import com.bmc.arsys.arreporting.ILogListener;
import com.remedy.arsys.log.Log;
import java.util.logging.Level;

public class BIRTLogListener
  implements ILogListener
{
  private Log mLog;

  public BIRTLogListener(Log paramLog)
  {
    this.mLog = paramLog;
  }

  public void logMessage(int paramInt, String paramString)
  {
    Level localLevel = Level.FINE;
    if (paramInt == 1)
      localLevel = Level.WARNING;
    else if (paramInt == 2)
      localLevel = Level.SEVERE;
    this.mLog.log(localLevel, paramString);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.BIRTLogListener
 * JD-Core Version:    0.6.1
 */