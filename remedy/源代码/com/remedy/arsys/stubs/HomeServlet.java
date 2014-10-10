package com.remedy.arsys.stubs;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.share.ServerInfo;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomeServlet extends GoatServlet
{
  protected boolean redirectToLogin(HttpServletRequest paramHttpServletRequest)
  {
    return true;
  }

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    MLog.fine("HomeServlet: URI=" + paramHttpServletRequest.getRequestURI());
    if (paramHttpServletRequest.getPathInfo() != null)
    {
      paramHttpServletResponse.sendError(404);
      return;
    }
    ARUserPreferences localARUserPreferences = SessionData.get().getPreferences();
    String str1 = getHomeServer();
    if (str1 == null)
      throw new GoatException(9341);
    String str2 = Configuration.getInstance().getLongName(str1);
    String str3 = getHomeForm(str2);
    if (str3 == null)
      throw new GoatException(9343);
    Form localForm = Form.get(str2, str3);
    Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(null, false, false);
    FormContext localFormContext = FormContext.get();
    CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
    String str4 = localFormContext.getFormURL(str1, str3, localViewInfo.getLabel());
    if (str4 == null)
      str4 = "";
    paramHttpServletResponse.sendRedirect(localFormContext.getContextURL() + str4);
  }

  public static String getHomeServer()
  {
    String str = null;
    SessionData localSessionData = SessionData.tryGet();
    if (localSessionData != null)
      str = localSessionData.getPreferences().getHomePageServer();
    if (str == null)
      str = Configuration.getInstance().getHomePageServer();
    return str;
  }

  public static String getHomeForm(String paramString)
    throws GoatException
  {
    String str = null;
    SessionData localSessionData = SessionData.tryGet();
    if (localSessionData != null)
      str = localSessionData.getPreferences().getHomePageForm();
    if (str == null)
      str = ServerInfo.get(paramString).getHomepageSchema();
    return str;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.HomeServlet
 * JD-Core Version:    0.6.1
 */