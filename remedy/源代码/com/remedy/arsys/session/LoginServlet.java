package com.remedy.arsys.session;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.StatusInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.Configuration.ServerInformation;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatServerMessage;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.stubs.GoatHttpServlet;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class LoginServlet extends GoatHttpServlet
{
  private static final String LOGIN_PARAM_MAP = "loginParamMap";
  private static final int AUTHENTICATION_FAILED = 1;
  private static final int AUTHENTICATION_NO_USER = 2;
  private static final int AUTHENTICATION_BAD_PASSWORD = 4;
  private static final int AUTHENTICATION_FAILURE = 8;
  private static final int AUTHENTICATION_OK = 16;
  private static final int AUTHENTICATION_FAILED_ADMIN_ONLY_MODE = 32;
  private static final int CONNECTION_ARSERVER_NOT_REACHABLE = 64;
  private static final int MCRCBase = -306674912;
  private static final int[] MCRCTable = new int[256];
  private static Log MLog = Log.get(2);
  private final Authenticator mAuthenticator = new DefaultAuthenticator();

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    try
    {
      paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
      doThePost(paramHttpServletRequest, paramHttpServletResponse);
    }
    finally
    {
      SessionData.set(null);
      ServerLogin.threadDepartingGoatSpace();
    }
  }

  public static final int computeCRC32(byte[] paramArrayOfByte)
  {
    int i = -1;
    for (int j = 0; j < paramArrayOfByte.length; j++)
      i = i >>> 8 ^ MCRCTable[((i ^ paramArrayOfByte[j]) & 0xFF)];
    return i;
  }

  public void doThePost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException, GoatException
  {
    MLog.fine("LoginServlet: url=" + paramHttpServletRequest.getRequestURL() + "?" + paramHttpServletRequest.getQueryString());
    MLog.fine("LoginServlet: locale=" + paramHttpServletRequest.getLocale());
    reconcileIPOverrideParams(paramHttpServletRequest);
    Params localParams = new Params(paramHttpServletRequest);
    String str1 = localParams.get("tzind");
    String str2 = localParams.get("goto");
    Object localObject1 = null;
    if ((str2 != null) && (str2.length() > 0))
    {
      str2 = Validator.StripOffScriptTag(str2);
      localObject1 = resolveGotoURL(paramHttpServletRequest, str2);
    }
    String str3 = null;
    if ((localObject1 == null) || (((String)localObject1).length() == 0))
    {
      str3 = getTargetURL(paramHttpServletRequest);
      if ((str3 == null) || (str3.equals("null")) || (str3.length() == 0))
        localObject1 = paramHttpServletRequest.getContextPath() + "/home";
      else
        localObject1 = str3;
    }
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    UserCredentials localUserCredentials = null;
    Object localObject2;
    try
    {
      if (localHttpSession != null)
        localUserCredentials = (UserCredentials)localHttpSession.getAttribute("usercredentials");
      if (localUserCredentials == null)
        localUserCredentials = this.mAuthenticator.getAuthenticatedCredentials(paramHttpServletRequest, paramHttpServletResponse);
      if (localUserCredentials == null)
        return;
      String str4 = localUserCredentials.getUser();
      if ((str4 == null) || (str4.length() == 0))
        throw new GoatException(9295);
      str6 = localParams.get("ipoverride");
      localObject2 = null;
      if (str1 == null)
        localObject2 = doLogin(paramHttpServletRequest, localUserCredentials, str6, SessionData.getLocale(paramHttpServletRequest));
      HashMap localHashMap = null;
      if (localHttpSession != null)
        localHashMap = (HashMap)localHttpSession.getAttribute("servercacheids");
      if (str1 == null)
        cleanStaleSessions(localHttpSession);
      String str7 = localParams.get("timezone");
      if ((str7 != null) && (str7.equals("")))
        str7 = localUserCredentials.getTimezone();
      localObject3 = localParams.get("render_sel");
      if ((localObject3 == null) || (((String)localObject3).equals("")))
        localObject3 = "html";
      if (str1 == null)
      {
        localHttpSession = createAndInitNewHttpSession(paramHttpServletRequest, localUserCredentials, str7, (GoatServerMessage[])localObject2, localHashMap, (String)localObject3);
        localObject4 = paramHttpServletRequest.getCookies();
        if ((localObject4 != null) && (localObject4.length > 0))
          for (int i = 0; i < localObject4.length; i++)
            if ((localObject4[i].getName().equals("GF")) || (localObject4[i].getName().equals("FC")))
            {
              localObject4[i].setMaxAge(0);
              localObject4[i].setPath("/");
              paramHttpServletResponse.addCookie(localObject4[i]);
            }
      }
      else if (localHttpSession != null)
      {
        localHttpSession.setAttribute("usertimezone", str7);
      }
      if ((str7 == null) && (str1 == null))
      {
        paramHttpServletRequest.setAttribute("goto_url", Validator.URLParamsEscape(Validator.StripOffScriptTag((String)localObject1)));
        localObject4 = paramHttpServletRequest.getRequestDispatcher("/shared/customTimezone.jsp");
        if (localObject4 != null)
          ((RequestDispatcher)localObject4).forward(paramHttpServletRequest, paramHttpServletResponse);
        return;
      }
    }
    catch (GoatException localGoatException)
    {
      Object localObject4;
      str6 = SessionData.getLocale(paramHttpServletRequest);
      localObject2 = localGoatException.getStatusInfo(str6);
      long l = 0L;
      Object localObject3 = ((List)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (StatusInfo)((Iterator)localObject3).next();
        l = ((StatusInfo)localObject4).getMessageNum();
        MLog.fine("LoginServlet: Caught status " + l);
        if (l == 9093L)
        {
          String str8 = prepareIPOverrideMessage((StatusInfo)localObject4);
          prepareIPOverrideOption(localParams, localHttpSession, str8.toString());
          RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher("/shared/login.jsp");
          if (localRequestDispatcher != null)
            localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
          return;
        }
      }
      if (localHttpSession != null)
        localHttpSession.removeAttribute("com.remedy.arsys.stubs.sessionData");
      if ((str3 != null) && (str3.length() > 0))
      {
        DefaultAuthenticator.redirectToLogin(paramHttpServletRequest, paramHttpServletResponse, Validator.sanitizeCRandLF(str3), localGoatException);
        return;
      }
      throw localGoatException;
    }
    String str5 = paramHttpServletRequest.getSession().getId();
    String str6 = paramHttpServletRequest.getContextPath();
    MLog.info("Context path is" + str6);
    if (str5 != null)
      paramHttpServletResponse.setHeader("SET-COOKIE", "JSESSIONID=" + str5 + "; Path=" + str6 + ";" + "HttpOnly" + ";");
    if ((localUserCredentials != null) && (localUserCredentials.getUser() != null))
    {
      localObject2 = str5 + localUserCredentials.getUser() + Configuration.getInstance().getAuthServer();
      Cookie localCookie = new Cookie("MJUID", "");
      localCookie.setValue("" + computeCRC32(((String)localObject2).getBytes()));
      localCookie.setPath(str6);
      localCookie.setVersion(0);
      paramHttpServletResponse.addCookie(localCookie);
    }
    MLog.info("User " + localUserCredentials.getUser() + " on IPAddress " + paramHttpServletRequest.getRemoteAddr());
    CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
    if ((((String)localObject1).equals(paramHttpServletRequest.getContextPath())) || (((String)localObject1).equals(paramHttpServletRequest.getContextPath() + "/")) || (((String)localObject1).equals(paramHttpServletRequest.getContextPath() + "/home")))
    {
      localObject2 = paramHttpServletRequest.getRequestDispatcher("/home");
      if (localObject2 != null)
        ((RequestDispatcher)localObject2).forward(paramHttpServletRequest, paramHttpServletResponse);
      return;
    }
    paramHttpServletResponse.sendRedirect(Validator.sanitizeCRandLF((String)localObject1));
  }

  public static GoatServerMessage[] doLogin(HttpServletRequest paramHttpServletRequest, UserCredentials paramUserCredentials, String paramString1, String paramString2)
    throws GoatException
  {
    if (paramUserCredentials == null)
      return null;
    boolean bool = (paramString1 != null) && (paramString1.equals("1"));
    StatusInfo[] arrayOfStatusInfo = null;
    ArrayList localArrayList = new ArrayList();
    Set localSet = getAuthenPrefHomepageServers();
    AuthenticationResult localAuthenticationResult = null;
    int i = 0;
    if (localSet.size() > 0)
    {
      localObject = localSet.iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = (String)((Iterator)localObject).next();
        if (str != null)
        {
          localAuthenticationResult = authenticate(paramHttpServletRequest, str, paramUserCredentials, bool);
          i |= localAuthenticationResult.getStatus();
          arrayOfStatusInfo = localAuthenticationResult.getMessages();
          if ((arrayOfStatusInfo != null) && (arrayOfStatusInfo.length > 0))
            for (int k = 0; k < arrayOfStatusInfo.length; k++)
              localArrayList.add(arrayOfStatusInfo[k]);
          if ((i & 0x10) != 0)
          {
            i = 16;
            break;
          }
        }
      }
    }
    MLog.fine("LoginServlet: Authentication status: " + i);
    resolveAuthenticationException(i);
    Object localObject = null;
    if (localArrayList.size() > 0)
    {
      localObject = new GoatServerMessage[localArrayList.size()];
      for (int j = 0; j < localArrayList.size(); j++)
        localObject[j] = new GoatServerMessage((StatusInfo)localArrayList.get(j), paramString2);
      return localObject;
    }
    return null;
  }

  private static void resolveAuthenticationException(int paramInt)
    throws GoatException
  {
    if ((paramInt & 0x4) == 4)
      throw new GoatException(9382);
    if ((paramInt & 0x2) == 2)
      throw new GoatException(9381);
    if ((paramInt & 0x8) == 8)
      throw new GoatException(9388);
    if ((paramInt & 0x1) == 1)
      throw new GoatException(9388);
    if ((paramInt & 0x20) == 32)
      throw new GoatException(9425);
    if ((paramInt & 0x40) == 64)
      throw new GoatException(9505);
  }

  private static Set getAuthenPrefHomepageServers()
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    Configuration localConfiguration = Configuration.getInstance();
    String str = localConfiguration.getAuthServer();
    if (str != null)
    {
      localLinkedHashSet.add(str);
    }
    else
    {
      localLinkedHashSet.addAll(localConfiguration.getPreferenceServers());
      localLinkedHashSet.add(localConfiguration.getHomePageServer());
      localLinkedHashSet.addAll(localConfiguration.getServers());
    }
    return localLinkedHashSet;
  }

  private static AuthenticationResult authenticate(HttpServletRequest paramHttpServletRequest, String paramString, UserCredentials paramUserCredentials, boolean paramBoolean)
    throws GoatException
  {
    String str = Configuration.getInstance().getLongName(paramString);
    MLog.fine("LoginServlet: Authenticating " + paramUserCredentials.getUser() + " against server " + str);
    StatusInfo[] arrayOfStatusInfo1 = null;
    Configuration.ServerInformation localServerInformation = ServerLogin.getServerInformation(str);
    LoginARServerUser localLoginARServerUser = new LoginARServerUser(null);
    localLoginARServerUser.setUser(paramUserCredentials.getUser());
    localLoginARServerUser.setPassword(paramUserCredentials.getPassword());
    if (paramUserCredentials.getAuthentication() != null)
      localLoginARServerUser.setAuthentication(paramUserCredentials.getAuthentication());
    localLoginARServerUser.setServer(str.toLowerCase());
    try
    {
      localLoginARServerUser.setClientType(9);
      localLoginARServerUser.setPort(localServerInformation.getPort());
      localLoginARServerUser.login();
      long l1 = localLoginARServerUser.getCacheId();
      setServerCacheID(paramHttpServletRequest, str, l1);
      arrayOfStatusInfo1 = (StatusInfo[])localLoginARServerUser.getLastStatus().toArray(new StatusInfo[0]);
    }
    catch (ARException localARException)
    {
      MLog.log(Level.FINE, "LoginServlet: Authentication exception:  Server: " + str + " , ", localARException);
      StatusInfo[] arrayOfStatusInfo2 = (StatusInfo[])localARException.getLastStatus().toArray(new StatusInfo[0]);
      for (int i = 0; i < arrayOfStatusInfo2.length; i++)
      {
        long l2 = arrayOfStatusInfo2[i].getMessageNum();
        if (l2 == 329L)
          return new AuthenticationResult(4);
        if (l2 == 612L)
          return new AuthenticationResult(2);
        if (l2 == 623L)
          return new AuthenticationResult(8);
        if (l2 == 394L)
          return new AuthenticationResult(32);
        if ((l2 >= 90L) && (l2 <= 94L))
          return new AuthenticationResult(64);
      }
      return new AuthenticationResult(1);
    }
    return new AuthenticationResult(16, arrayOfStatusInfo1);
  }

  private static void setServerCacheID(HttpServletRequest paramHttpServletRequest, String paramString, long paramLong)
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    HashMap localHashMap = (HashMap)localHttpSession.getAttribute("servercacheids");
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      localHttpSession.setAttribute("servercacheids", localHashMap);
    }
    localHashMap.put(paramString, Long.valueOf(paramLong));
  }

  private static HttpSession createAndInitNewHttpSession(HttpServletRequest paramHttpServletRequest, UserCredentials paramUserCredentials, String paramString1, GoatServerMessage[] paramArrayOfGoatServerMessage, HashMap paramHashMap, String paramString2)
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    localHttpSession.setAttribute("usercredentials", paramUserCredentials);
    localHttpSession.setAttribute("usertimezone", paramString1);
    if (paramArrayOfGoatServerMessage != null)
      localHttpSession.setAttribute("arsystem.authentication.messages", paramArrayOfGoatServerMessage);
    if (paramHashMap != null)
      localHttpSession.setAttribute("servercacheids", paramHashMap);
    localHttpSession.setAttribute("rendering_engine", paramString2);
    return localHttpSession;
  }

  private static void cleanStaleSessions(HttpSession paramHttpSession)
  {
    if (paramHttpSession != null)
    {
      SessionData localSessionData = (SessionData)paramHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
      if (localSessionData != null)
        localSessionData.dispose(true);
      paramHttpSession.invalidate();
    }
  }

  private static String getTargetURL(HttpServletRequest paramHttpServletRequest)
  {
    String str = null;
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    if (localHttpSession != null)
    {
      str = (String)localHttpSession.getAttribute("returnBack");
      MLog.fine("LoginServlet: Target URL attribute = " + str);
    }
    if ((str == null) && (Configuration.getInstance().getAllowReturnBackURL()))
      str = paramHttpServletRequest.getParameter("returnBack");
    return str;
  }

  private static String resolveGotoURL(HttpServletRequest paramHttpServletRequest, String paramString)
  {
    String str1 = null;
    String str2 = paramHttpServletRequest.getContextPath();
    if (paramString.startsWith(str2))
      str1 = paramString;
    else
      try
      {
        URL localURL = new URL(paramString);
        String str3 = localURL.getProtocol();
        if ((str3 != null) && (str3.length() > 0))
          str1 = paramString;
        else
          str1 = str2 + paramString;
      }
      catch (MalformedURLException localMalformedURLException)
      {
        str1 = str2 + paramString;
      }
    return str1;
  }

  private static void reconcileIPOverrideParams(HttpServletRequest paramHttpServletRequest)
  {
    Params localParams = new Params(paramHttpServletRequest);
    String str = localParams.get("ipoverride");
    if ((str != null) && (str.equals("1")))
    {
      HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
      if (localHttpSession != null)
      {
        localParams.setMap((Map)localHttpSession.getAttribute("loginParamMap"));
        localHttpSession.removeAttribute("arsys_multi_ip_override_msg");
      }
    }
  }

  private static String prepareIPOverrideMessage(StatusInfo paramStatusInfo)
  {
    long l = paramStatusInfo.getMessageNum();
    StringBuilder localStringBuilder = new StringBuilder(100);
    localStringBuilder.append("ARERR ");
    localStringBuilder.append("[" + l + "] ");
    localStringBuilder.append(paramStatusInfo.getMessageText());
    return localStringBuilder.toString();
  }

  private static void prepareIPOverrideOption(Params paramParams, HttpSession paramHttpSession, String paramString)
  {
    Map localMap = Collections.synchronizedMap(new HashMap(11));
    localMap.put("username", paramParams.get("username"));
    localMap.put("pwd", paramParams.get("pwd"));
    String str = paramParams.get("timezone");
    if ((str != null) && (str.equals("")))
      str = paramHttpSession == null ? null : (String)paramHttpSession.getAttribute("usertimezone");
    localMap.put("timezone", str);
    localMap.put("auth", paramParams.get("auth"));
    localMap.put("encpwd", paramParams.get("encpwd"));
    localMap.put("goto", paramParams.get("goto"));
    if (paramHttpSession != null)
    {
      paramHttpSession.setAttribute("loginParamMap", localMap);
      paramHttpSession.setAttribute("arsys_multi_ip_override_msg", paramString);
    }
  }

  static
  {
    for (int i = 0; i < 256; i++)
    {
      int j = i;
      for (int k = 0; k < 8; k++)
        j = (j & 0x1) == 1 ? j >>> 1 ^ 0xEDB88320 : j >>> 1;
      MCRCTable[i] = j;
    }
  }

  private static class LoginARServerUser extends ARServerUser
  {
    protected void finalize()
      throws Throwable
    {
      setCacheId(0L);
      super.finalize();
    }
  }

  private static class AuthenticationResult
  {
    private final int status;
    private final StatusInfo[] messages;

    AuthenticationResult(int paramInt)
    {
      this(paramInt, null);
    }

    AuthenticationResult(int paramInt, StatusInfo[] paramArrayOfStatusInfo)
    {
      this.status = paramInt;
      this.messages = paramArrayOfStatusInfo;
    }

    int getStatus()
    {
      return this.status;
    }

    StatusInfo[] getMessages()
    {
      return this.messages;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.LoginServlet
 * JD-Core Version:    0.6.1
 */