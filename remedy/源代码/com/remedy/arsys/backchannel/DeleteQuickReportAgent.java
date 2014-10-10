package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.quickreports.ARUserQuickReports;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class DeleteQuickReportAgent extends NDXDeleteQuickReport
{
  public DeleteQuickReportAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    ARUserQuickReports localARUserQuickReports = new ARUserQuickReports(localSessionData.getUserName(), this.mSchema, this.mServer);
    boolean bool = false;
    if (localARUserQuickReports != null)
      bool = localARUserQuickReports.deleteQuickReport(localSessionData, this.mNames);
    append("this.result=").append(bool).append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.DeleteQuickReportAgent
 * JD-Core Version:    0.6.1
 */