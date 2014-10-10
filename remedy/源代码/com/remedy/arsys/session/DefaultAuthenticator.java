package com.remedy.arsys.session;

import com.bmc.arsys.api.StatusInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DefaultAuthenticator
  implements Authenticator
{
  private static final int MAX_LOGIN_LENGTH = 255;
  private static final int MAX_PW_LENGTH = 31;
  private static final String TIMEZONE = "ardev.login.timezone";
  private static Log MLog = Log.get(2);
  private static char[] cipher = { 'k', 's', 'z', 'h', 'x', 'b', 'p', 'j', 'v', 'c', 'g', 'f', 'q', 'n', 't', 'm' };

  public void init(Map paramMap)
  {
  }

  public void destroy()
  {
  }

  public UserCredentials getAuthenticatedCredentials(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    UserCredentials localUserCredentials1 = null;
    Params localParams = new Params(paramHttpServletRequest);
    String str1 = localParams.get("username");
    if (str1 != null)
      str1 = str1.trim();
    String str2 = localParams.get("pwd");
    String str3 = localParams.get("encpwd");
    String str4 = null;
    if ((str3 != null) && (str3.equals("1")))
      str4 = unscramble(str2);
    else
      str4 = str2;
    if (str4 == null)
      str4 = "";
    if ((str1 != null) && (str1.length() > 0) && (str1.length() < 255) && (str4.length() < 31))
      localUserCredentials1 = new UserCredentials(str1, str4, localParams.get("auth"));
    if (localUserCredentials1 != null)
    {
      MLog.fine("DefaultAuthenticator: Using credentials from login page - " + localUserCredentials1.getUser());
      return localUserCredentials1;
    }
    Configuration localConfiguration = Configuration.getInstance();
    UserCredentials localUserCredentials2 = null;
    String str5 = localConfiguration.getLoginUser();
    if (str5 != null)
    {
      localUserCredentials2 = new UserCredentials(str5, localConfiguration.getLoginPassword(), localConfiguration.getLoginAuth());
      MLog.fine("DefaultAuthenticator: Using developer login - " + localUserCredentials2.getUser());
      localHttpSession.setAttribute("usertimezone", localConfiguration.getProperty("ardev.login.timezone"));
      return localUserCredentials2;
    }
    MLog.fine("DefaultAuthenticator: Credentials requested");
    String str6 = paramHttpServletRequest.getRequestURI();
    Object localObject;
    if (paramHttpServletRequest.getQueryString() != null)
    {
      localObject = paramHttpServletRequest.getQueryString();
      if (((String)localObject).toLowerCase().indexOf("<script>") == -1)
        str6 = str6 + "?" + (String)localObject;
    }
    else
    {
      localObject = (Map)localHttpSession.getAttribute("arsystem.vfservlet.orgreqmap");
      if (localObject != null)
      {
        String str7 = "";
        if ((String)((Map)localObject).get("server") != null)
          str7 = str7 + "server=" + (String)((Map)localObject).get("server");
        if ((String)((Map)localObject).get("form") != null)
          str7 = str7 + "&form=" + (String)((Map)localObject).get("form");
        if ((String)((Map)localObject).get("username") != null)
          str7 = str7 + "&username=" + (String)((Map)localObject).get("username");
        if ((String)((Map)localObject).get("pwd") != null)
          str7 = str7 + "&pwd=" + (String)((Map)localObject).get("pwd");
        if ((String)((Map)localObject).get("app") != null)
          str7 = str7 + "&app=" + (String)((Map)localObject).get("app");
        if ((String)((Map)localObject).get("qual") != null)
          str7 = str7 + "&qual=" + (String)((Map)localObject).get("qual");
        if ((String)((Map)localObject).get("mode") != null)
          str7 = str7 + "&mode=" + (String)((Map)localObject).get("mode");
        if ((String)((Map)localObject).get("eid") != null)
          str7 = str7 + "&eid=" + (String)((Map)localObject).get("eid");
        if ((String)((Map)localObject).get("view") != null)
          str7 = str7 + "&view=" + (String)((Map)localObject).get("view");
        if ((String)((Map)localObject).get("auth") != null)
          str7 = str7 + "&auth=" + (String)((Map)localObject).get("auth");
        if (str7.length() > 0)
          str6 = str6 + "?" + str7;
      }
    }
    redirectToLogin(paramHttpServletRequest, paramHttpServletResponse, str6, null);
    return null;
  }

  public static void redirectToLogin(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, String paramString, GoatException paramGoatException)
    throws IOException
  {
    MLog.fine("DefaultAuthenticator.redirectToLogin: url=" + paramString);
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    localHttpSession.setAttribute("arsys_login_name", "");
    localHttpSession.removeAttribute("arsys_login_user_error");
    localHttpSession.removeAttribute("arsys_login_password_error");
    if (paramGoatException == null)
    {
      localHttpSession.setAttribute("arsys_login_msg", " ");
    }
    else
    {
      str1 = SessionData.getLocale(paramHttpServletRequest);
      embedErrorMessagesInHttpSession(localHttpSession, paramGoatException, str1);
    }
    String str1 = paramHttpServletRequest.getContextPath() + "/shared/login.jsp";
    String str2 = Validator.sanitizeCRandLF(paramString);
    str1 = str1 + "?" + str2;
    localHttpSession.setAttribute("returnBack", str2);
    CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
    paramHttpServletResponse.sendRedirect(str1);
  }

  private static void embedErrorMessagesInHttpSession(HttpSession paramHttpSession, GoatException paramGoatException, String paramString)
  {
    String str = paramGoatException.toString(paramString);
    str = HTMLWriter.escape(str);
    str = str.replaceAll("\n", "<br>");
    paramHttpSession.setAttribute("arsys_login_msg", str);
    List localList = paramGoatException.getStatusInfo(paramString);
    Object localObject = localList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      StatusInfo localStatusInfo = (StatusInfo)((Iterator)localObject).next();
      long l = localStatusInfo.getMessageNum();
      if ((l == 329L) || (l == 9382L))
        paramHttpSession.setAttribute("arsys_login_password_error", "");
      else if ((l == 612L) || (l == 615L) || (l == 9381L))
        paramHttpSession.setAttribute("arsys_login_user_error", "");
    }
    localObject = (SessionData)paramHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
    if (localObject != null)
      paramHttpSession.setAttribute("arsys_login_name", ((SessionData)localObject).getUserName());
  }

  private static String unscramble(String paramString)
  {
    if (paramString == null)
      paramString = "";
    int i = paramString.length();
    assert (i % 2 == 0);
    byte[] arrayOfByte = new byte[i];
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    for (int j = 0; j + 1 < i; j += 2)
    {
      int m = -1;
      int n = -1;
      try
      {
        i2 = paramString.charAt(j);
        i3 = paramString.charAt(j + 1);
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        MLog.warning(localIndexOutOfBoundsException.getLocalizedMessage());
        return null;
      }
      for (int k = 0; k < 16; k++)
      {
        if (i2 == cipher[k])
          m = k;
        if (i3 == cipher[k])
          n = k;
      }
      assert ((m != -1) && (n != -1));
      arrayOfByte[(i1++)] = (byte)(m * 16 + n);
    }
    try
    {
      String str = new String(arrayOfByte, 0, i1, "UTF-8");
      return URLDecoder.decode(str, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      MLog.severe(localUnsupportedEncodingException.toString());
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.DefaultAuthenticator
 * JD-Core Version:    0.6.1
 */