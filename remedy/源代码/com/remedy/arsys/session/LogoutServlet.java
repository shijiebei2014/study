package com.remedy.arsys.session;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.stubs.GoatServlet;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class LogoutServlet extends GoatServlet
{
  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    CacheDirectiveController.setCacheControlCookie(paramHttpServletRequest, paramHttpServletResponse);
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    Params localParams = new Params(paramHttpServletRequest);
    String str1 = localParams.get("username");
    String str2 = localParams.get("pwd");
    String str3 = localParams.get("auth");
    if (str1 != null)
    {
      SessionData.dispose(str1, str2, str3);
    }
    else if (localHttpSession != null)
    {
      str4 = localHttpSession.getId();
      MLog.fine("LogoutServlet: session terminated id = " + str4);
      SessionData.get().dispose(true);
      Object localObject = localHttpSession.getAttribute("arsys.customlogout.pathelements");
      localHttpSession.invalidate();
      if (localObject != null)
        paramHttpServletRequest.getSession(true).setAttribute("arsys.customlogout.pathelements", localObject);
    }
    String str4 = getFinalLogoutURL(paramHttpServletRequest);
    MLog.fine("LogoutServlet: logout url = " + str4);
    int i = str4.lastIndexOf('/');
    str4 = str4.substring(0, i + 1) + URLEncoder.encode(str4.substring(i + 1), "UTF-8");
    paramHttpServletResponse.sendRedirect(Validator.sanitizeCRandLF(str4));
  }

  protected void postInternal(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    if (localHttpSession != null)
      synchronized (localHttpSession)
      {
        super.postInternal(paramHttpServletRequest, paramHttpServletResponse);
      }
    else
      super.postInternal(paramHttpServletRequest, paramHttpServletResponse);
  }

  protected void invalidSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    Params localParams = new Params(paramHttpServletRequest);
    String str1 = localParams.get("username");
    String str2 = localParams.get("pwd");
    String str3 = localParams.get("auth");
    if (str1 != null)
      SessionData.dispose(str1, str2, str3);
    paramHttpServletResponse.sendRedirect(getFinalLogoutURL(paramHttpServletRequest));
  }

  protected boolean redirectToLogin(HttpServletRequest paramHttpServletRequest)
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    Object localObject = localHttpSession.getAttribute("usercredentials");
    return localObject != null;
  }

  private static String getFinalLogoutURL(HttpServletRequest paramHttpServletRequest)
  {
    String str1 = paramHttpServletRequest.getParameter("goto");
    String str2 = null;
    if ((str1 != null) && (!str1.equals("null")))
    {
      String str3 = paramHttpServletRequest.getContextPath();
      if ((str1.startsWith("http")) || (str1.startsWith(str3)))
        str2 = str1;
      else
        str2 = str3 + str1;
    }
    else
    {
      str2 = getDefaultLogoutURL(paramHttpServletRequest);
    }
    return str2;
  }

  private static String getDefaultLogoutURL(HttpServletRequest paramHttpServletRequest)
  {
    String str = paramHttpServletRequest.getContextPath();
    return str + "/shared/logout.jsp";
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.LogoutServlet
 * JD-Core Version:    0.6.1
 */