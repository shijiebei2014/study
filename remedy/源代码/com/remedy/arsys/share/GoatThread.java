package com.remedy.arsys.share;

import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.stubs.SessionData;

public class GoatThread extends Thread
{
  private FormContext formContext;
  private SessionData sessionData;

  public static void cloneThreadContext(GoatThread paramGoatThread)
  {
    paramGoatThread.setFormContext(FormContext.get());
    paramGoatThread.setSessionData(SessionData.get());
  }

  public SessionData getSessionData()
  {
    return this.sessionData;
  }

  public void setSessionData(SessionData paramSessionData)
  {
    this.sessionData = paramSessionData;
  }

  public FormContext getFormContext()
  {
    return this.formContext;
  }

  public void setFormContext(FormContext paramFormContext)
  {
    this.formContext = paramFormContext;
  }

  public void run()
  {
    FormContext.set(getFormContext());
    SessionData.set(getSessionData());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.GoatThread
 * JD-Core Version:    0.6.1
 */