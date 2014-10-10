package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.quickreports.ARUserQuickReports;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class SaveQuickReportAgent extends NDXSaveQuickReport
{
  public SaveQuickReportAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    ARUserQuickReports localARUserQuickReports = new ARUserQuickReports(localSessionData.getUserName(), this.mSchema, this.mServer);
    String str;
    if (this.mDefinition)
    {
      str = localARUserQuickReports.retrieveDefinition(this.mServer, this.mName);
      if (str != null)
        str = JSWriter.escape(str);
      else
        str = "";
      append("this.result={a:\"").append(str + "\"}").append(";");
    }
    else if (this.mFieldID.length > 0)
    {
      str = localARUserQuickReports.getQBEMatchValue(this.mFieldID);
      append("this.result={a:{").append(str).append("}};");
    }
    else
    {
      boolean bool = false;
      if (localARUserQuickReports != null)
        bool = localARUserQuickReports.saveQuickReport(localSessionData, this.mName, this.mReportQual, this.mNewrecord, this.mDisable);
      append("this.result=").append(bool).append(";");
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SaveQuickReportAgent
 * JD-Core Version:    0.6.1
 */