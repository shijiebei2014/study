package com.remedy.arsys.session;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.Configuration.ServerInformation;
import com.remedy.arsys.goat.ForcedLoginException;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatServerMessage;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.SchemaKeyFactory;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.dgc.VMID;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.axis.session.Session;

public class Login
{
  public static final String LOGIN_URL = "/shared/login.jsp";
  public static final String LOGOUT_URL = "/shared/logout.jsp";
  public static final String LOGGEDOUT_URL = "/shared/loggedout.jsp";
  public static final String ERROR_URL = "/shared/error.jsp";
  public static final String HOME_URL = "/home";
  public static final String WAIT_JSP = "/shared/wait.jsp";
  public static final String JS_RESOURCES = "/resources/javascript/8.1.00 201301251157/";
  public static final String VIEWFORM_URL = "/shared/view_form.jsp";
  public static final String FLEX_ENABLEMENT_COOKIE = "fe";
  private static final String DEFAULT_GUID_COOKIE_NAME = "Default mid-tier GUID";
  private static final String CUSTOM_AUTHENTICATOR = "arsystem.authenticator";
  private static final String DEFAULT_AUTHENTICATOR = "com.remedy.arsys.session.DefaultAuthenticator";
  private static final String AUTHENTICATOR_PROPERTIES_FILE = "arsystem.authenticator.config.file";
  private static final String IP_RESTRICTION_GUID = "G";
  private static final String SESSION_TIMEOUT_COOKIE = "st";
  private static final String LICENSE_RELEASE_COOKIE = "lt";
  public static final String CUSTOM_TIMEZONE_URL = "/shared/customTimezone.jsp";
  private static Map UserPWChangeSet = Collections.synchronizedMap(new HashMap());
  private static Authenticator DefaultAuthenticator;
  private static Authenticator CustomAuthenticator;
  private static final Log MLog = Log.get(2);
  private static final Pattern IP_ADDR_PATTERN = Pattern.compile("([0-9]+).([0-9]+).([0-9]+).([0-9]+)");
  private static String UserFormName = "";
  private static String UserPWChangeFormName = "";
  private static final int[] USER_FORM_IDS = { 101, 102, 104 };
  private static final int[] USER_PW_CHANGE_FORM_IDS = { 101, 102, 125 };
  private static final int LOGIN_FID = 101;
  private static final int PW_CHANGE_FID = 124;
  private static final ArithmeticOrRelationalOperand FID = new ArithmeticOrRelationalOperand(101);
  private static ArithmeticOrRelationalOperand LOGIN = new ArithmeticOrRelationalOperand(new Value());
  private static StringBuilder BASE_PW_CHANGE_URI_STR = new StringBuilder("/servlet/ViewFormServlet?server=&mode=Submit&form=");
  private static StringBuilder BASE_PW_CHANGE_URI;
  private static final String URI_ROUTING_JS = "this.redirectPassword(\"{0}\",\"{1}\",{2});";

  public static String generateGUID(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    String str1 = null;
    int i = 86400;
    Cookie[] arrayOfCookie = paramHttpServletRequest.getCookies();
    String str2;
    if (arrayOfCookie != null)
      for (int j = 0; j < arrayOfCookie.length; j++)
      {
        str2 = arrayOfCookie[j].getName();
        if ((str2 != null) && (str2.equals("G")))
        {
          str1 = arrayOfCookie[j].getValue();
          break;
        }
      }
    if ((str1 == null) || (str1.length() <= 0))
    {
      str1 = new VMID().toString();
      if ((str1 != null) && (str1.length() > 0))
      {
        Cookie localCookie = new Cookie("G", str1);
        localCookie.setMaxAge(i);
        localCookie.setPath(paramHttpServletRequest.getContextPath());
        str2 = paramHttpServletRequest.getServerName();
        int k = str2.indexOf('.');
        if (k != -1)
        {
          Matcher localMatcher = IP_ADDR_PATTERN.matcher(str2);
          if ((!localMatcher.matches()) && (str2.indexOf('.', k + 1) != -1))
            try
            {
              String str3 = InetAddress.getLocalHost().getHostName();
              if ((str3 != null) && (str3.equalsIgnoreCase(str2.substring(0, k))))
                localCookie.setDomain(str2.substring(k));
              else
                MLog.fine("Login: URL doesn't contain local hostname. Not setting domain cookie.");
            }
            catch (UnknownHostException localUnknownHostException)
            {
              MLog.fine(localUnknownHostException.getMessage());
            }
        }
        paramHttpServletResponse.addCookie(localCookie);
      }
    }
    if ((str1 == null) || (str1.length() <= 0))
      str1 = "Default mid-tier GUID";
    return str1;
  }

  public static ServerLogin initWebService(Session paramSession, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, int paramInt)
    throws GoatException
  {
    boolean bool = false;
    SessionData localSessionData = null;
    try
    {
      if (paramSession != null)
        localSessionData = (SessionData)paramSession.get("com.remedy.arsys.stubs.sessionData");
      if ((localSessionData != null) && (paramString3 != null) && (paramString4 != null))
      {
        bool = isNewUser(localSessionData, paramString3, paramString4);
        if (bool)
        {
          localSessionData.dispose(true);
          localSessionData = null;
        }
      }
      if (localSessionData == null)
      {
        localSessionData = new SessionData(paramString3, paramString4, paramString7, paramString5, paramString6, null, paramString1, new Value(34));
        if (paramSession != null)
        {
          paramSession.setTimeout(300);
          paramSession.set("com.remedy.arsys.stubs.sessionData", localSessionData);
        }
      }
      SessionData.set(localSessionData);
      MLog.fine("Login: Axis Session is good !!!");
      return localSessionData.getServerLogin(paramString2);
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Login: " + localGoatException.toString(), localGoatException);
      if (paramSession != null)
        paramSession.remove("com.remedy.arsys.stubs.sessionData");
      throw localGoatException;
    }
  }

  public static boolean establishSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, ServletException, IOException
  {
    MLog.fine("Login: establishing Session");
    HttpSession localHttpSession1 = paramHttpServletRequest.getSession(true);
    UserCredentials localUserCredentials = (UserCredentials)localHttpSession1.getAttribute("usercredentials");
    int i = 0;
    if (localUserCredentials == null)
    {
      localUserCredentials = CustomAuthenticator.getAuthenticatedCredentials(paramHttpServletRequest, paramHttpServletResponse);
      if (localUserCredentials != null)
        i = 1;
    }
    if (localUserCredentials == null)
      return false;
    if (localUserCredentials.getUser() == null)
    {
      MLog.fine("Login: Custom authenticator failed. Trying default authenticator");
      localUserCredentials = DefaultAuthenticator.getAuthenticatedCredentials(paramHttpServletRequest, paramHttpServletResponse);
      if (localUserCredentials == null)
        return false;
    }
    if (i != 0)
    {
      localObject = localUserCredentials.getTimezone();
      if ((localObject != null) && (!((String)localObject).equals("")))
      {
        HttpSession localHttpSession2 = paramHttpServletRequest.getSession(true);
        if (localHttpSession2 != null)
          localHttpSession2.setAttribute("usertimezone", localObject);
        MLog.fine("Login: MT setting the timezone:" + (String)localObject);
      }
    }
    Object localObject = establishSession(paramHttpServletRequest, paramHttpServletResponse, localUserCredentials, false);
    return localObject != null;
  }

  public static HttpSession establishSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, UserCredentials paramUserCredentials, boolean paramBoolean)
    throws GoatException, ServletException, IOException
  {
    MLog.fine("Login: creating session");
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    if ((localHttpSession != null) && (!hasMJHIDCookie(paramHttpServletRequest)))
      setMJUIDCookie(paramHttpServletRequest, paramHttpServletResponse, paramUserCredentials);
    String str1 = (String)localHttpSession.getAttribute("usertimezone");
    String str2 = (String)localHttpSession.getAttribute("rendering_engine");
    boolean bool = false;
    if ((str2 != null) && (str2.equals("flex")))
      bool = true;
    if ((paramBoolean) && ((str1 == null) || (str1.equals(""))))
    {
      str1 = paramHttpServletRequest.getParameter("usertimezone");
      if ((str1 == null) || (str1.equals("")))
      {
        Params localParams = new Params(paramHttpServletRequest);
        Object localObject1 = (Map)localHttpSession.getAttribute("arsystem.vfservlet.orgreqmap");
        localParams.setMap((Map)localObject1);
        if (localObject1 == null)
        {
          localObject1 = new HashMap();
          if (paramHttpServletRequest.getParameterMap() != null)
          {
            localObject2 = paramHttpServletRequest.getParameterMap().keySet();
            if (localObject2 != null)
            {
              localObject3 = ((Set)localObject2).iterator();
              while (((Iterator)localObject3).hasNext())
              {
                localObject4 = (String)((Iterator)localObject3).next();
                ((Map)localObject1).put(localObject4, Validator.sanitizeCRandLF(paramHttpServletRequest.getParameter((String)localObject4)));
              }
            }
          }
          localHttpSession.setAttribute("arsystem.vfservlet.orgreqmap", localObject1);
        }
        Object localObject2 = new StringBuffer();
        ((StringBuffer)localObject2).append(paramHttpServletRequest.getRequestURI());
        ((StringBuffer)localObject2).append("?");
        ((StringBuffer)localObject2).append(paramHttpServletRequest.getQueryString());
        Object localObject3 = Validator.sanitizeCRandLF(((StringBuffer)localObject2).toString());
        if ((localObject3 != null) && (((String)localObject3).toLowerCase().indexOf("<script>") != -1))
          localObject3 = "";
        paramHttpServletRequest.setAttribute("returnBack", localObject3);
        Object localObject4 = paramHttpServletRequest.getRequestDispatcher("/shared/view_form.jsp");
        if (localObject4 != null)
          ((RequestDispatcher)localObject4).forward(paramHttpServletRequest, paramHttpServletResponse);
        return null;
      }
      if (str1.equals("use_server"))
        str1 = "";
    }
    return initSessions(paramHttpServletRequest, paramHttpServletResponse, paramUserCredentials, str1, bool);
  }

  private static HttpSession initSessions(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, UserCredentials paramUserCredentials, String paramString, boolean paramBoolean)
    throws GoatException
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    SessionData localSessionData = null;
    String str1 = null;
    String str2 = null;
    if (localHttpSession != null)
    {
      str2 = (String)localHttpSession.getAttribute("returnBack");
      localSessionData = resolveStaleSessions(localHttpSession, paramUserCredentials);
    }
    if ((localHttpSession == null) || (localSessionData == null))
    {
      localHttpSession = paramHttpServletRequest.getSession(true);
      str1 = localHttpSession.getId();
      if (isLoginRedo(localHttpSession))
        localSessionData = createSessionDataForLoginRedo(paramHttpServletRequest, paramHttpServletResponse, paramUserCredentials, paramString);
      if (localSessionData == null)
        localSessionData = new SessionData(paramUserCredentials.getUser(), paramUserCredentials.getPassword(), paramUserCredentials.getAuthentication(), paramHttpServletRequest.getLocale().toString(), paramString, generateGUID(paramHttpServletRequest, paramHttpServletResponse), str1);
      HashMap localHashMap = (HashMap)localHttpSession.getAttribute("servercacheids");
      localSessionData.setServerCacheIds(localHashMap);
      localHttpSession.removeAttribute("servercacheids");
      localHttpSession.setAttribute("com.remedy.arsys.stubs.sessionData", localSessionData);
      int i = -1;
      if ((localSessionData != null) && (localSessionData.getPreferences() != null))
        i = localSessionData.getPreferences().getSessionTimeoutInMinutes();
      if (i == -1)
        i = Configuration.getInstance().getSessionTimeoutInMinutes();
      if (i != -1)
      {
        localHttpSession.setMaxInactiveInterval(i * 60);
        setSessionTimeoutCookie(paramHttpServletResponse, i * 60);
      }
      setLicenseTimeoutCookie(paramHttpServletResponse, Configuration.getInstance().getIntProperty("arsystem.licenserelease_timeout", 60));
      if (str2 != null)
        localHttpSession.setAttribute("returnBack", str2);
    }
    localSessionData.SetEnableFlex(paramBoolean);
    SessionData.set(localSessionData);
    MLog.fine("Login: SessionId=" + str1);
    return localHttpSession;
  }

  private static SessionData createSessionDataForLoginRedo(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, UserCredentials paramUserCredentials, String paramString)
    throws GoatException
  {
    SessionData localSessionData = null;
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    String str1 = localHttpSession.getId();
    String str2 = paramHttpServletRequest.getParameter("server");
    ARServerUser localARServerUser = new ARServerUser(paramUserCredentials.getUser(), paramUserCredentials.getPassword(), paramHttpServletRequest.getLocale().toString(), str2);
    if (str2 != null)
    {
      Configuration.ServerInformation localServerInformation = ServerLogin.getServerInformation(str2);
      localARServerUser.setPort(localServerInformation.getPort());
      try
      {
        localARServerUser.login();
        localSessionData = new SessionData(paramUserCredentials.getUser(), paramUserCredentials.getPassword(), paramUserCredentials.getAuthentication(), paramHttpServletRequest.getLocale().toString(), paramString, generateGUID(paramHttpServletRequest, paramHttpServletResponse), str1);
        localHttpSession.setAttribute("arsystem.redo_login", "false");
      }
      catch (ARException localARException)
      {
        localSessionData = null;
        localHttpSession.setAttribute("arsystem.redo_login", "true");
      }
    }
    else
    {
      localSessionData = null;
    }
    return localSessionData;
  }

  private static boolean isLoginRedo(HttpSession paramHttpSession)
  {
    String str = (String)paramHttpSession.getAttribute("arsystem.redo_login");
    return (str != null) && (str.equalsIgnoreCase("true"));
  }

  private static SessionData resolveStaleSessions(HttpSession paramHttpSession, UserCredentials paramUserCredentials)
  {
    SessionData localSessionData = (SessionData)paramHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
    boolean bool = isNewUser(localSessionData, paramUserCredentials.getUser(), paramUserCredentials.getPassword());
    if ((bool) || ((localSessionData != null) && (localSessionData.isLoggedOut())))
    {
      localSessionData.dispose(true);
      localSessionData = null;
      paramHttpSession.invalidate();
    }
    return localSessionData;
  }

  private static boolean isNewUser(SessionData paramSessionData, String paramString1, String paramString2)
  {
    return (paramSessionData != null) && (paramString1 != null) && (paramString2 != null) && ((!paramString1.equals(paramSessionData.getUserName())) || (!paramString2.equals(paramSessionData.getPassword())));
  }

  private static void createAndInitAuthenticators()
  {
    String str = Configuration.getInstance().getProperty("arsystem.authenticator");
    if ((str != null) && (str.length() > 0))
      try
      {
        CustomAuthenticator = (Authenticator)Class.forName(str).newInstance();
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        MLog.log(Level.SEVERE, "Login: Failed to locate class: " + str + " Please make sure it's in the classpath.\n", localClassNotFoundException);
        localClassNotFoundException.printStackTrace();
      }
      catch (InstantiationException localInstantiationException)
      {
        MLog.log(Level.SEVERE, "Login: The class " + str + " was found but cannot be instantiated for unknown reasons.\n", localInstantiationException);
        localInstantiationException.printStackTrace();
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        MLog.log(Level.SEVERE, "Login: The constructor of class " + str + " is not accessible.", localIllegalAccessException);
        localIllegalAccessException.printStackTrace();
      }
      catch (SecurityException localSecurityException)
      {
        MLog.log(Level.SEVERE, "Login: The class " + str + " was found but cannot be instantiated for security reasons. Please fix this.", localSecurityException);
        localSecurityException.printStackTrace();
      }
      catch (ClassCastException localClassCastException)
      {
        MLog.log(Level.SEVERE, "Login: The class " + str + " is not of type com.remedy.arsys.Authenticator. Please fix this.", localClassCastException);
        localClassCastException.printStackTrace();
      }
      catch (Exception localException)
      {
        MLog.log(Level.SEVERE, "Login: Unknown error.\n", localException);
        localException.printStackTrace();
      }
    Properties localProperties = loadAuthenticatorConfigFile();
    if (CustomAuthenticator != null)
      CustomAuthenticator.init(localProperties);
    if ((str != null) && (str.compareTo("com.remedy.arsys.session.DefaultAuthenticator") == 0))
      DefaultAuthenticator = CustomAuthenticator;
    else
      DefaultAuthenticator = new DefaultAuthenticator();
  }

  private static Properties loadAuthenticatorConfigFile()
  {
    Properties localProperties = new Properties();
    Configuration localConfiguration = Configuration.getInstance();
    String str = localConfiguration.getProperty("arsystem.authenticator.config.file", "");
    if (str.length() > 0)
    {
      InputStream localInputStream = null;
      try
      {
        localInputStream = localConfiguration.getClass().getClassLoader().getResourceAsStream(str);
        localProperties.load(localInputStream);
        MLog.log(Level.INFO, "Login: Loaded authenticator properties file " + str);
      }
      catch (IOException localIOException1)
      {
        MLog.log(Level.SEVERE, "Login: Failed to load authenticator properties file " + str, localIOException1);
        localIOException1.printStackTrace();
      }
      finally
      {
        try
        {
          if (localInputStream != null)
            localInputStream.close();
        }
        catch (IOException localIOException2)
        {
        }
      }
    }
    return localProperties;
  }

  public static GoatServerMessage[] getEmbeddedServerMessages(HttpServletRequest paramHttpServletRequest)
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    GoatServerMessage[] arrayOfGoatServerMessage = (GoatServerMessage[])localHttpSession.getAttribute("arsystem.authentication.messages");
    if (arrayOfGoatServerMessage != null)
      localHttpSession.removeAttribute("arsystem.authentication.messages");
    return arrayOfGoatServerMessage;
  }

  public static String formatServerMessages(GoatServerMessage[] paramArrayOfGoatServerMessage)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramArrayOfGoatServerMessage != null)
    {
      for (int i = 0; i < paramArrayOfGoatServerMessage.length; i++)
        localStringBuilder.append(paramArrayOfGoatServerMessage[i].toString());
      return localStringBuilder.toString();
    }
    return null;
  }

  public static boolean isUserForm(String paramString1, String paramString2)
  {
    if (paramString2.equalsIgnoreCase(Configuration.getInstance().getAuthServer()))
      return UserFormName.equals(paramString1);
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(paramString2);
      String str = SchemaKeyFactory.getInstance().getSchemaKey(localServerLogin, USER_FORM_IDS);
      if (str == null)
        return false;
      if (str.equals(paramString1))
        return true;
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Login: Failed to load an admin login");
      localGoatException.printStackTrace();
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Login: Failed to query User form name.");
      localARException.printStackTrace();
    }
    return false;
  }

  public static boolean isUserPasswordChangeForm(String paramString)
  {
    return UserPWChangeFormName.equals(paramString);
  }

  public static boolean isUserPasswordChangeForm(String paramString1, String paramString2)
  {
    String str = (String)UserPWChangeSet.get(paramString1);
    if (str == null)
    {
      str = getPWChangeFormName(paramString1);
      UserPWChangeSet.put(paramString1, str);
    }
    return str.equals(paramString2);
  }

  public static boolean isPasswordChangeRequired(UserCredentials paramUserCredentials)
  {
    if (Configuration.getInstance().isNewAuthenticationServer())
      findAndFillPasswordFormNames();
    if (UserPWChangeFormName.equals(""))
      return false;
    String str = paramUserCredentials.getUser();
    return getForcedChangePasswordFlag(str);
  }

  public static boolean getForcedChangePasswordFlag(String paramString)
  {
    ServerLogin localServerLogin = null;
    try
    {
      String str = Configuration.getInstance().getAuthServer();
      if (str == null)
        return false;
      localServerLogin = ServerLogin.getAdmin(str);
      localServerLogin.impersonateUser(null);
      localServerLogin.impersonateUser(paramString);
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Login: Failed to load an admin login");
      localGoatException.printStackTrace();
      return false;
    }
    catch (ARException localARException1)
    {
      MLog.log(Level.SEVERE, "Login: Failed to impersonate user");
      localARException1.printStackTrace();
      return false;
    }
    int[] arrayOfInt = { 124 };
    LOGIN.setValue(new Value(paramString));
    QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(1, FID, LOGIN));
    try
    {
      List localList = localServerLogin.getListEntryObjects(UserFormName, localQualifierInfo, 0, 1, null, arrayOfInt, false, null);
      if (localList.size() == 0)
      {
        boolean bool1 = false;
        return bool1;
      }
      Entry localEntry = (Entry)localList.get(0);
      Value localValue = (Value)localEntry.get(Integer.valueOf(124));
      if ((localValue == null) || (localValue.getValue() == null))
      {
        bool2 = false;
        return bool2;
      }
      boolean bool2 = true;
      return bool2;
    }
    catch (ARException localARException2)
    {
      MLog.log(Level.SEVERE, "Login: Failed to query PW change.");
      localARException2.printStackTrace();
    }
    finally
    {
      if (localServerLogin != null)
        try
        {
          localServerLogin.impersonateUser(null);
        }
        catch (ARException localARException3)
        {
          MLog.log(Level.SEVERE, "Login: Failed to reset impersonated user.");
        }
    }
    return false;
  }

  public static String getForcePasswordChangeURI(HttpServletRequest paramHttpServletRequest, String paramString)
  {
    String str = paramHttpServletRequest.getContextPath();
    StringBuilder localStringBuilder = new StringBuilder(str).append(BASE_PW_CHANGE_URI);
    int i = localStringBuilder.indexOf("&mode=");
    localStringBuilder.insert(i, paramString);
    return localStringBuilder.toString();
  }

  private static String[] getPasswordChangeStatusMsg(List<StatusInfo> paramList)
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = "";
    arrayOfString[1] = "";
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramList != null)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        StatusInfo localStatusInfo = (StatusInfo)localIterator.next();
        if (i == 0)
        {
          arrayOfString[1] = Long.toString(localStatusInfo.getMessageNum());
          i = 1;
        }
        if (localStringBuffer.length() > 0)
          localStringBuffer.append(" ");
        if (localStatusInfo.getMessageText().length() > 0)
          localStringBuffer.append(localStatusInfo.getMessageText());
        if (localStatusInfo.getAppendedText().length() > 0)
        {
          if (localStatusInfo.getMessageText().length() > 0)
            localStringBuffer.append(" ");
          localStringBuffer.append(localStatusInfo.getAppendedText());
        }
      }
      arrayOfString[0] = localStringBuffer.toString();
    }
    return arrayOfString;
  }

  private static String getAndConvert2JSForcePasswordChangeTargetURI(String paramString, List<StatusInfo> paramList)
  {
    String[] arrayOfString = getPasswordChangeStatusMsg(paramList);
    Object[] arrayOfObject = { paramString, JSWriter.escape(arrayOfString[0], false), arrayOfString[1] };
    String str = MessageFormat.format("this.redirectPassword(\"{0}\",\"{1}\",{2});", arrayOfObject);
    return str;
  }

  public static String handleForcedLoginException(ForcedLoginException paramForcedLoginException)
  {
    ForcedLoginException localForcedLoginException = paramForcedLoginException;
    SessionData localSessionData = SessionData.get();
    localSessionData.changePassword(localForcedLoginException.getNewPassword());
    localSessionData.setPasswordChangeRequired(false);
    MLog.log(Level.SEVERE, "Changed password for user: " + paramForcedLoginException.getUser());
    String str = localSessionData.getPasswordChangeTargetURL();
    if (str == null)
      return "";
    localSessionData.setPasswordChangeTargetURL(null);
    return getAndConvert2JSForcePasswordChangeTargetURI(str, localForcedLoginException.getStatus());
  }

  public static synchronized void findAndFillPasswordFormNames()
  {
    if (!Configuration.getInstance().isNewAuthenticationServer())
      return;
    String str = Configuration.getInstance().getAuthServer();
    if (str == null)
    {
      UserPWChangeFormName = "";
      UserFormName = "";
      return;
    }
    UserFormName = getUserFormName(str);
    UserPWChangeFormName = getPWChangeFormName(str);
    BASE_PW_CHANGE_URI = new StringBuilder(BASE_PW_CHANGE_URI_STR);
    BASE_PW_CHANGE_URI.append(UserPWChangeFormName);
    Configuration.getInstance().setNewAuthenticationServer(false);
  }

  private static String getPWChangeFormName(String paramString)
  {
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(paramString);
      String str = SchemaKeyFactory.getInstance().getSchemaKey(localServerLogin, USER_PW_CHANGE_FORM_IDS);
      return str == null ? "" : str;
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Login: Failed to load an admin login");
      localGoatException.printStackTrace();
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Login: Cannot access Password/User form name");
      localARException.printStackTrace();
    }
    return "";
  }

  private static String getUserFormName(String paramString)
  {
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(paramString);
      String str = SchemaKeyFactory.getInstance().getSchemaKey(localServerLogin, USER_FORM_IDS);
      return str == null ? "" : str;
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Login: Failed to load an admin login");
      localGoatException.printStackTrace();
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Login: Cannot access Password/User form name");
      localARException.printStackTrace();
    }
    return "";
  }

  private static void setSessionTimeoutCookie(HttpServletResponse paramHttpServletResponse, int paramInt)
  {
    Cookie localCookie = new Cookie("st", "" + paramInt);
    localCookie.setMaxAge(-1);
    localCookie.setPath("/");
    paramHttpServletResponse.addCookie(localCookie);
  }

  private static void setLicenseTimeoutCookie(HttpServletResponse paramHttpServletResponse, int paramInt)
  {
    Cookie localCookie = new Cookie("lt", "" + paramInt);
    localCookie.setMaxAge(-1);
    localCookie.setPath("/");
    paramHttpServletResponse.addCookie(localCookie);
  }

  private static void setMJUIDCookie(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, UserCredentials paramUserCredentials)
  {
    if (paramUserCredentials == null)
      return;
    String str1 = paramUserCredentials.getUser();
    if ((str1 == null) || (str1.length() == 0))
      return;
    String str2 = paramHttpServletRequest.getSession().getId();
    String str3 = paramHttpServletRequest.getContextPath();
    MLog.info("Context path is" + str3);
    if (str2 != null)
      paramHttpServletResponse.setHeader("SET-COOKIE", "JSESSIONID=" + str2 + "; Path=" + str3 + ";" + "HttpOnly" + ";");
    if ((paramUserCredentials != null) && (paramUserCredentials.getUser() != null))
    {
      String str4 = str2 + paramUserCredentials.getUser() + Configuration.getInstance().getAuthServer();
      Cookie localCookie = new Cookie("MJUID", "");
      localCookie.setValue("" + LoginServlet.computeCRC32(str4.getBytes()));
      localCookie.setPath(str3);
      localCookie.setVersion(0);
      paramHttpServletResponse.addCookie(localCookie);
    }
  }

  private static boolean hasMJHIDCookie(HttpServletRequest paramHttpServletRequest)
  {
    Cookie[] arrayOfCookie = paramHttpServletRequest.getCookies();
    if (arrayOfCookie != null)
      for (int i = 0; i < arrayOfCookie.length; i++)
      {
        Cookie localCookie = arrayOfCookie[i];
        if ((localCookie != null) && (localCookie.getValue() != null) && (localCookie.getValue().length() > 0) && (localCookie.getName().equals("MJUID")))
          return true;
      }
    return false;
  }

  static
  {
    createAndInitAuthenticators();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.Login
 * JD-Core Version:    0.6.1
 */