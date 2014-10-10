package com.remedy.arsys.stubs;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AuthenticationHelperServlet extends GoatHttpServlet
{
  private static final String ARSERVER = "server";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "pwd";
  private static final String AUTHENTICATION = "auth";
  private static final String NATIVE = "native";
  private static final String LOCALE = "locale";
  private static final String TIMEZONE = "usertimezone";
  private static final String APPNAME = "appname";
  private static final String FIELDID = "fieldid";
  private static final String WINDOWID = "windowID";
  private static final String FORMNAME = "schema";
  private static final String VIEWNAME = "view";
  private static final String USERDETAILS = "ud";
  private static final String[] SPECIAL_URL_PATTERNS = { "/plugins/feeddvm/params" };
  private static final String USER_SESSION_VALID = "USER_SESSION_VALID";
  private static final String PLUGIN_REQ_INFO = "PluginReqInfo";
  private static final Value AR_FB_MIDTIER_CTYPE = new Value(12);
  private static final String PASSWORD_ENCRYPTED = "encpwd";
  private static char[] cipher = { 'k', 's', 'z', 'h', 'x', 'b', 'p', 'j', 'v', 'c', 'g', 'f', 'q', 'n', 't', 'm' };

  private boolean isSpecialUrl(String paramString)
  {
    boolean bool = false;
    for (int i = 0; i < SPECIAL_URL_PATTERNS.length; i++)
      if (paramString.endsWith(SPECIAL_URL_PATTERNS[i]))
      {
        bool = true;
        break;
      }
    return bool;
  }

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    RequestInfo localRequestInfo = setupEnv(paramHttpServletRequest, paramHttpServletResponse);
    processRequestInfo(localRequestInfo, paramHttpServletRequest, paramHttpServletResponse);
    teardownEnv(paramHttpServletRequest);
  }

  private RequestInfo setupEnv(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    String str1 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("server"));
    if (str1 != null)
      str1 = Configuration.getInstance().getLongName(str1);
    String str2 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("username"));
    String str3 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("pwd"));
    String str4 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("auth"));
    String str5 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("appname"));
    String str6 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("fieldid"));
    String str7 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("windowID"));
    String str8 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("schema"));
    String str9 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("view"));
    if (str7 == null)
    {
      str7 = "0";
      MLog.warning("windowID was not received in request parameters, setting it to 0");
    }
    boolean bool = "1".equals(paramHttpServletRequest.getParameter("native"));
    String str10 = paramHttpServletRequest.getParameter("encpwd");
    if ((str10 != null) && (str10.equals("1")))
      str3 = unscramble(str3);
    if ((str10 != null) && (str10.equals("2")))
    {
      localObject1 = new RSAAuthenticator(str1);
      str3 = ((RSAAuthenticator)localObject1).decrypt(str3, str1);
    }
    Object localObject1 = paramHttpServletRequest.getHeader("X-Encrypted-Pwd");
    if (localObject1 != null)
    {
      localObject2 = new RSAAuthenticator(str1);
      str3 = ((RSAAuthenticator)localObject2).decrypt((String)localObject1, str1);
    }
    Object localObject2 = paramHttpServletRequest.getParameterMap();
    if ((Configuration.getInstance().getPluginSecurity()) && (Validator.URLParamHasXSSTag((Map)localObject2)))
    {
      paramHttpServletResponse.setStatus(400);
      throw new GoatException(9423);
    }
    Object localObject5;
    if ((paramHttpServletRequest.getParameter("ud") != null) && (paramHttpServletRequest.getParameter("ud").length() > 0) && (isSpecialUrl(paramHttpServletRequest.getRequestURL().toString())))
    {
      localObject3 = Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter("ud"));
      localObject4 = new RSAAuthenticator(str1);
      localObject3 = ((RSAAuthenticator)localObject4).decrypt((String)localObject3, str1);
      localObject5 = ((String)localObject3).split("\n");
      if (localObject5.length == 3)
      {
        str3 = localObject5[0];
        str2 = localObject5[1];
      }
    }
    Object localObject3 = paramHttpServletRequest.getSession(false);
    Object localObject4 = null;
    if ((localObject3 != null) && (paramHttpServletRequest.isRequestedSessionIdValid()))
    {
      localObject4 = (SessionData)((HttpSession)localObject3).getAttribute("com.remedy.arsys.stubs.sessionData");
      if (localObject4 != null)
        if ((str2 != null) && (str3 != null))
        {
          if ((!str2.equals(((SessionData)localObject4).getUserName())) || (!str3.equals(((SessionData)localObject4).getPassword())) || ((str4 != null) && (!str4.equals(((SessionData)localObject4).getAuthentication()))))
          {
            ((SessionData)localObject4).dispose(true);
            localObject4 = null;
            ((HttpSession)localObject3).invalidate();
          }
          else
          {
            paramHttpServletRequest.setAttribute("USER_SESSION_VALID", "true");
          }
        }
        else
        {
          str2 = ((SessionData)localObject4).getUserName();
          str3 = ((SessionData)localObject4).getPassword();
          str4 = ((SessionData)localObject4).getAuthentication();
          localObject5 = (PRI)((HttpSession)localObject3).getAttribute("PluginReqInfo");
          if (localObject5 != null)
          {
            str1 = ((PRI)localObject5).mDefServer;
            str5 = ((PRI)localObject5).mAppName;
            str6 = ((PRI)localObject5).mFieldID;
          }
        }
    }
    if (localObject4 == null)
      if ((str2 != null) && (str3 != null))
      {
        localObject3 = paramHttpServletRequest.getSession(true);
        if (bool)
        {
          localObject4 = new SessionData(str2, str3, str4, getLocaleStr(paramHttpServletRequest), getTimezoneStr(paramHttpServletRequest), "FB GUID", "FB SID", AR_FB_MIDTIER_CTYPE);
          ((HttpSession)localObject3).setMaxInactiveInterval(864000);
        }
        else
        {
          localObject4 = new SessionData(str2, str3, str4, getLocaleStr(paramHttpServletRequest), getTimezoneStr(paramHttpServletRequest), Login.generateGUID(paramHttpServletRequest, paramHttpServletResponse), ((HttpSession)localObject3).getId());
          int i = ((SessionData)localObject4).getPreferences().getSessionTimeoutInMinutes();
          if (i == -1)
            i = Configuration.getInstance().getSessionTimeoutInMinutes();
          if (i != -1)
            ((HttpSession)localObject3).setMaxInactiveInterval(i * 60);
        }
        ((HttpSession)localObject3).setAttribute("com.remedy.arsys.stubs.sessionData", localObject4);
        ((HttpSession)localObject3).setAttribute("PluginReqInfo", new PRI(str1, str5, str6, null));
      }
      else
      {
        throw new GoatException(9201);
      }
    SessionData.set((SessionData)localObject4);
    arrangeFormContext(paramHttpServletRequest, paramHttpServletResponse);
    return new RequestInfo(str1, str2, str3, str4, str5, str6, str7, str8, str9);
  }

  private void teardownEnv(HttpServletRequest paramHttpServletRequest)
  {
    FormContext.dispose();
    if ((paramHttpServletRequest.getParameter("ud") != null) && (paramHttpServletRequest.getParameter("ud").length() > 0) && (isSpecialUrl(paramHttpServletRequest.getRequestURL().toString())))
    {
      String str = (String)paramHttpServletRequest.getAttribute("USER_SESSION_VALID");
      if ((str == null) || (!str.trim().equalsIgnoreCase("true")))
        paramHttpServletRequest.getSession().invalidate();
    }
    SessionData.set(null);
  }

  private String getLocaleStr(HttpServletRequest paramHttpServletRequest)
  {
    String str = paramHttpServletRequest.getParameter("locale");
    if (str == null)
      str = paramHttpServletRequest.getLocale().toString();
    return str;
  }

  private String getTimezoneStr(HttpServletRequest paramHttpServletRequest)
  {
    return paramHttpServletRequest.getParameter("usertimezone");
  }

  protected abstract FormContext arrangeFormContext(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException;

  protected abstract void processRequestInfo(RequestInfo paramRequestInfo, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException;

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

  private static class PRI
    implements Serializable
  {
    private final String mDefServer;
    private final String mAppName;
    private final String mFieldID;

    private PRI(String paramString1, String paramString2, String paramString3)
    {
      this.mDefServer = paramString1;
      this.mAppName = paramString2;
      this.mFieldID = paramString3;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.AuthenticationHelperServlet
 * JD-Core Version:    0.6.1
 */