package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatApplicationContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.support.BrowserType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ARServerUserBean
{
  public String calculateIframeURLForLogin(HttpServletRequest paramHttpServletRequest, String paramString1, HttpSession paramHttpSession, String paramString2)
  {
    String str1 = null;
    try
    {
      String[] arrayOfString = GoatHttpServlet.getI18nFriendlyPathElements(paramString1, paramHttpServletRequest.getContextPath());
      if ((arrayOfString != null) && (arrayOfString.length > 2))
      {
        paramHttpSession.setAttribute("arsys.customlogout.pathelements", arrayOfString);
        ServerLogin localServerLogin = ServerLogin.getAdmin(arrayOfString[1], paramString2);
        GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(localServerLogin, arrayOfString[2]);
        BrowserType localBrowserType = BrowserType.getTypeFromRequest(paramHttpServletRequest);
        String str2 = GoatHttpServlet.getL10NFileName("login.html", paramHttpServletRequest);
        Globule localGlobule = localGoatApplicationContainer.getResourceFile(localBrowserType, "public/" + str2);
        if (localGlobule == null)
        {
          localGlobule = localGoatApplicationContainer.getResourceFile(localBrowserType, "public/login.html");
          str2 = "login.html";
        }
        String str3 = "../pubapps/" + arrayOfString[1] + "/" + arrayOfString[2] + "/resources/";
        if (localGlobule != null)
          str1 = str3 + str2;
      }
    }
    catch (GoatException localGoatException)
    {
    }
    return str1;
  }

  public String calculateIframeURLForLogout(HttpServletRequest paramHttpServletRequest, HttpSession paramHttpSession, String paramString)
  {
    String str1 = null;
    try
    {
      String[] arrayOfString = (String[])paramHttpSession.getAttribute("arsys.customlogout.pathelements");
      if ((arrayOfString != null) && (arrayOfString.length > 2))
      {
        ServerLogin localServerLogin = ServerLogin.getAdmin(arrayOfString[1], paramString);
        GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(localServerLogin, arrayOfString[2]);
        BrowserType localBrowserType = BrowserType.getTypeFromRequest(paramHttpServletRequest);
        String str2 = GoatHttpServlet.getL10NFileName("logout.html", paramHttpServletRequest);
        Globule localGlobule = localGoatApplicationContainer.getResourceFile(localBrowserType, "public/" + str2);
        if (localGlobule == null)
        {
          localGlobule = localGoatApplicationContainer.getResourceFile(localBrowserType, "public/logout.html");
          str2 = "logout.html";
        }
        String str3 = "../pubapps/" + arrayOfString[1] + "/" + arrayOfString[2] + "/resources/";
        if (localGlobule != null)
          str1 = str3 + str2;
      }
    }
    catch (GoatException localGoatException)
    {
    }
    finally
    {
      if (paramHttpSession != null)
        paramHttpSession.invalidate();
    }
    return str1;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ARServerUserBean
 * JD-Core Version:    0.6.1
 */