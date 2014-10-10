package com.remedy.arsys.stubs;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.JavaScriptGenerationException;
import com.remedy.arsys.goat.intf.service.IRequestService;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.share.JSWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class GoatServlet extends GoatHttpServlet
{
  protected static final ThreadLocal<Boolean> SESSION_DATA_ACQUIRED = new ThreadLocal();
  protected static IRequestService requestServiceDefault;
  protected static Map<String, IRequestService> REQUEST_SERVICE_TYPE_MAP;
  protected static String REQUEST_SERVICE_TYPE_DEFAULT;

  public static String setRequestServiceTypeDefault(String paramString)
  {
    REQUEST_SERVICE_TYPE_DEFAULT = paramString;
    if (REQUEST_SERVICE_TYPE_MAP != null)
      requestServiceDefault = (IRequestService)REQUEST_SERVICE_TYPE_MAP.get(REQUEST_SERVICE_TYPE_DEFAULT);
    return REQUEST_SERVICE_TYPE_DEFAULT;
  }

  public static final Map<String, IRequestService> setRequestServiceMap(Map<String, IRequestService> paramMap)
  {
    REQUEST_SERVICE_TYPE_MAP = paramMap;
    if (REQUEST_SERVICE_TYPE_DEFAULT != null)
      requestServiceDefault = (IRequestService)REQUEST_SERVICE_TYPE_MAP.get(REQUEST_SERVICE_TYPE_DEFAULT);
    return REQUEST_SERVICE_TYPE_MAP;
  }

  protected abstract void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException, ServletException;

  protected void postInternal(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    try
    {
      if (Configuration.getInstance().getEnableResponseHostIP())
        paramHttpServletResponse.addHeader("ARRESPONSEHOSTIP", InetAddress.getLocalHost().getHostAddress());
      paramHttpServletRequest.setCharacterEncoding("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    MLog.fine("GoatServlet: url=" + paramHttpServletRequest.getRequestURL() + (paramHttpServletRequest.getQueryString() != null ? "?" + paramHttpServletRequest.getQueryString() : ""));
    String str = paramHttpServletRequest.getHeader("cookie");
    MLog.fine("cookie=" + (str != null ? str : "none"));
    SessionData localSessionData;
    try
    {
      if (!setupSessionData(paramHttpServletRequest))
        if (redirectToLogin(paramHttpServletRequest))
        {
          boolean bool = Login.establishSession(paramHttpServletRequest, paramHttpServletResponse);
          if (!bool)
            return;
        }
        else
        {
          invalidSession(paramHttpServletRequest, paramHttpServletResponse);
          return;
        }
      FormContext localFormContext = new FormContext(null, "../../../../", paramHttpServletRequest.getContextPath() + "/", getServletContext().getRealPath("/"));
      IRequestService localIRequestService = resolveRequestService(paramHttpServletRequest);
      localFormContext.setRequestService(localIRequestService);
      try
      {
        doRequest(paramHttpServletRequest, paramHttpServletResponse);
      }
      finally
      {
        FormContext.dispose();
      }
    }
    catch (JavaScriptGenerationException localJavaScriptGenerationException)
    {
      MLog.log(Level.WARNING, "Caught JavaScriptGenerationException", localJavaScriptGenerationException);
      errorScript(paramHttpServletRequest, paramHttpServletResponse, localJavaScriptGenerationException);
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.WARNING, "Caught GoatException ", localGoatException);
      errorPage(paramHttpServletRequest, paramHttpServletResponse, localGoatException);
    }
    catch (IOException localIOException)
    {
      MLog.log(Level.WARNING, "Caught IOException ", localIOException);
      throw localIOException;
    }
    catch (Error localError)
    {
      MLog.log(Level.FINE, "Caught Error", localError);
      localError.printStackTrace(System.err);
      throw localError;
    }
    catch (RuntimeException localRuntimeException)
    {
      MLog.log(Level.SEVERE, "Caught RuntimeException", localRuntimeException);
      localRuntimeException.printStackTrace(System.err);
      throw localRuntimeException;
    }
    finally
    {
    }
    ret;
  }

  protected static final IRequestService resolveRequestService(HttpServletRequest paramHttpServletRequest)
  {
    IRequestService localIRequestService = null;
    String str1 = paramHttpServletRequest.getParameter("format");
    String str2 = paramHttpServletRequest.getRequestURL().toString();
    if ((!str2.endsWith(".js")) && (!str2.endsWith("help.html")))
    {
      if ((str1 != null) && (str1.equalsIgnoreCase("flex")))
        str1 = "html";
      HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
      if (localHttpSession != null)
      {
        SessionData localSessionData = (SessionData)localHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
        if ((localSessionData != null) && (localSessionData.isFlexEnabled()))
          str1 = "flex";
      }
    }
    if (str1 != null)
      localIRequestService = (IRequestService)REQUEST_SERVICE_TYPE_MAP.get(str1);
    if (localIRequestService == null)
      localIRequestService = requestServiceDefault;
    return localIRequestService;
  }

  protected boolean redirectToLogin(HttpServletRequest paramHttpServletRequest)
  {
    return false;
  }

  protected void errorPage(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, GoatException paramGoatException)
    throws ServletException, IOException
  {
    redirectToErrorPage(paramHttpServletRequest, paramHttpServletResponse, paramGoatException);
  }

  protected void invalidSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    throw new GoatException(9201);
  }

  protected void acquireSessionData(SessionData paramSessionData)
  {
  }

  protected void releaseSessionData(SessionData paramSessionData)
  {
  }

  protected boolean setupSessionData(HttpServletRequest paramHttpServletRequest)
    throws GoatException
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    if ((localHttpSession != null) && (!localHttpSession.isNew()) && (paramHttpServletRequest.isRequestedSessionIdValid()))
    {
      SessionData localSessionData = (SessionData)localHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
      if (localSessionData != null)
      {
        acquireSessionData(localSessionData);
        if (!localSessionData.isLoggedOut())
        {
          MLog.fine("GoatServlet: SessionID=" + localSessionData.getID());
          SessionData.set(localSessionData);
          return true;
        }
        localHttpSession.invalidate();
      }
      MLog.fine("GoatServlet: No SessionData");
    }
    else
    {
      MLog.fine("GoatServlet: No session or new session");
    }
    return false;
  }

  protected static boolean staticSetupSessionData(HttpServletRequest paramHttpServletRequest)
    throws GoatException
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    if ((localHttpSession != null) && (!localHttpSession.isNew()) && (paramHttpServletRequest.isRequestedSessionIdValid()))
    {
      SessionData localSessionData = (SessionData)localHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
      if (localSessionData != null)
      {
        synchronized (localSessionData)
        {
          if (!localSessionData.isLoggedOut())
          {
            localSessionData.incrFormRequestCount();
            SESSION_DATA_ACQUIRED.set(Boolean.valueOf(true));
          }
        }
        if (!localSessionData.isLoggedOut())
        {
          MLog.fine("GoatServlet: SessionID=" + localSessionData.getID());
          SessionData.set(localSessionData);
          return true;
        }
        localHttpSession.invalidate();
      }
      MLog.fine("GoatServlet: No SessionData");
    }
    else
    {
      MLog.fine("GoatServlet: No session or new session");
    }
    return false;
  }

  public static void teardownSessionData()
  {
    SessionData.set(null);
    SESSION_DATA_ACQUIRED.set(null);
    ServerLogin.threadDepartingGoatSpace();
  }

  private static void errorScript(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, GoatException paramGoatException)
    throws IOException
  {
    try
    {
      paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
      paramHttpServletResponse.setHeader("Pragma", "no-cache");
      paramHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
      paramHttpServletResponse.setContentType("text/plain; charset=UTF-8");
      JSWriter localJSWriter = new JSWriter();
      localJSWriter.statement("var jsPageException = " + JSWriter.escapeString(paramGoatException.toString()));
      localJSWriter.statement("LogAndAlert(jsPageException);");
      paramHttpServletResponse.getWriter().print(localJSWriter);
    }
    catch (IOException localIOException)
    {
      MLog.log(Level.SEVERE, "Caught IO exception sending script error", localIOException);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.GoatServlet
 * JD-Core Version:    0.6.1
 */