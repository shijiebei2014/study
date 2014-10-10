package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.quickreports.ARUserQuickReports;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class ModifyQuickReportAgent extends NDXModifyQuickReport
{
  public ModifyQuickReportAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    ARUserQuickReports localARUserQuickReports = new ARUserQuickReports(localSessionData.getUserName(), this.mSchema, this.mServer);
    int i = this.mDisable.length;
    boolean[] arrayOfBoolean = new boolean[i];
    for (int j = 0; j < i; j++)
      arrayOfBoolean[j] = (this.mDisable[j] == 1 ? 1 : false);
    j = 0;
    boolean bool;
    if (localARUserQuickReports != null)
      bool = localARUserQuickReports.updateExistingRecord(localSessionData, this.mName, arrayOfBoolean, "");
    append("this.result=").append(bool).append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.ModifyQuickReportAgent
 * JD-Core Version:    0.6.1
 */