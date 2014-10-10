package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.ARServerLog;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;

public class GetAPITimingsAgent extends NDXRequest
{
  public GetAPITimingsAgent(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetAPITimings");
    if (paramArrayList.size() != 1)
      throw new GoatException("Wrong argument length, spoofed");
  }

  protected void process()
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    if ((localSessionData != null) && (localSessionData.getServerLog() != null))
    {
      String str = localSessionData.getServerLog().getLogMessage();
      append("this.result=").openObj().property("m", str).closeObj().append(";");
      localSessionData.getServerLog().clearMessage();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetAPITimingsAgent
 * JD-Core Version:    0.6.1
 */