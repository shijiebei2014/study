package com.remedy.arsys.automation;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.GoatHttpServlet;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutomationServlet extends GoatHttpServlet
{
  public static final String START = "start";
  public static final String STOP = "stop";

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    String str1 = paramHttpServletRequest.getParameter("cmd");
    if ("start".equals(str1))
    {
      String str2 = paramHttpServletRequest.getParameter("host");
      String str3 = paramHttpServletRequest.getParameter("port");
      String str4 = paramHttpServletRequest.getParameter("level");
      String str5 = paramHttpServletRequest.getParameter("category");
      int i = 0;
      Level localLevel = null;
      try
      {
        i = Integer.parseInt(str3);
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
      try
      {
        if (str4 != null)
          localLevel = Level.parse(str4);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
      }
      if (i != 0)
        Log.enableRemoteLogging(str2, i, localLevel, str5);
    }
    else if ("stop".equals(str1))
    {
      Log.disableRemoteLogging();
      System.out.println("Remote Logging Ended.");
    }
    paramHttpServletResponse.sendError(204);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.automation.AutomationServlet
 * JD-Core Version:    0.6.1
 */