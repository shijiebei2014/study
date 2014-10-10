package com.remedy.arsys.stubs;

import com.bmc.arsys.api.StatusInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class GoatHttpServlet extends HttpServlet
{
  protected static Log MLog;
  private static boolean EnableHttpTrace = false;
  private final Object syncObj = new Object();

  protected abstract void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException;

  public final void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    postInternal(paramHttpServletRequest, paramHttpServletResponse);
  }

  public final void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    postInternal(paramHttpServletRequest, paramHttpServletResponse);
  }

  protected void doTrace(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    synchronized (this.syncObj)
    {
      if (EnableHttpTrace)
        return;
      paramHttpServletResponse.setStatus(204);
    }
  }

  protected void postInternal(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    try
    {
      if (Configuration.getInstance().getEnableResponseHostIP())
        paramHttpServletResponse.addHeader("ARRESPONSEHOSTIP", InetAddress.getLocalHost().getHostAddress());
      try
      {
        paramHttpServletRequest.setCharacterEncoding("UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        if (!$assertionsDisabled)
          throw new AssertionError();
      }
      try
      {
        doRequest(paramHttpServletRequest, paramHttpServletResponse);
      }
      catch (GoatException localGoatException)
      {
        MLog.log(Level.FINE, "GoatHttpServlet: Caught GoatException ", localGoatException);
        redirectToErrorPage(paramHttpServletRequest, paramHttpServletResponse, localGoatException);
      }
    }
    finally
    {
      ServerLogin.threadDepartingGoatSpace();
    }
  }

  public static final String[] getI18nFriendlyPathElements(HttpServletRequest paramHttpServletRequest)
    throws GoatException
  {
    String str1 = paramHttpServletRequest.getRequestURI();
    String str2 = paramHttpServletRequest.getServletPath();
    return getI18nFriendlyPathElements(str1, str2);
  }

  public static void redirectToErrorPage(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, GoatException paramGoatException)
    throws ServletException, IOException
  {
    String str1 = SessionData.getLocale(paramHttpServletRequest);
    assert (str1 != null);
    try
    {
      List localList = paramGoatException.getStatusInfo(str1);
      HashMap localHashMap = null;
      Object localObject3;
      if (localList != null)
      {
        localObject1 = null;
        localHashMap = new HashMap(localList.size());
        localObject2 = localList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (StatusInfo)((Iterator)localObject2).next();
          localObject1 = new StringBuilder();
          long l = ((StatusInfo)localObject3).getMessageNum();
          if (l != 8914L)
          {
            if (l == 9093L)
            {
              String str2 = paramHttpServletRequest.getRequestURI();
              if (paramHttpServletRequest.getQueryString() != null)
                str2 = str2 + "?" + paramHttpServletRequest.getQueryString();
              paramHttpServletRequest.setAttribute("overrideURI", Validator.sanitizeCRandLF(str2));
              paramHttpServletRequest.getSession().setAttribute("overrideURI", Validator.sanitizeCRandLF(str2));
            }
            int i = ((StatusInfo)localObject3).getMessageType();
            ((StringBuilder)localObject1).append(i == 1 ? "ARWARN" : i == 2 ? "ARERR" : "");
            ((StringBuilder)localObject1).append("/");
            String str3 = ((StatusInfo)localObject3).getMessageText();
            if ((str3 != null) && (str3.length() > 0))
              ((StringBuilder)localObject1).append(str3);
            String str4 = ((StatusInfo)localObject3).getAppendedText();
            if ((str4 != null) && (str4.length() > 0))
              ((StringBuilder)localObject1).append(" : ").append(str4);
            localHashMap.put(Long.toString(l), HTMLWriter.escape(((StringBuilder)localObject1).toString()));
          }
        }
        paramHttpServletRequest.setAttribute("siMap", localHashMap);
      }
      Object localObject1 = "/shared/error.jsp";
      Object localObject2 = paramHttpServletRequest.getRequestDispatcher((String)localObject1);
      if (localObject2 != null)
      {
        if (errorInLogin(localList))
        {
          localObject3 = SessionData.tryGet();
          if ((localObject3 != null) && (((SessionData)localObject3).getAuthentication() != null) && (((SessionData)localObject3).getPassword() == null))
            paramHttpServletRequest.setAttribute("ISSSOENABLE", new Boolean(true));
          if (localObject3 != null)
            ((SessionData)localObject3).dispose(true);
          HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
          if (localHttpSession != null)
            localHttpSession.invalidate();
        }
        ((RequestDispatcher)localObject2).forward(paramHttpServletRequest, paramHttpServletResponse);
      }
    }
    catch (IOException localIOException)
    {
      MLog.severe(paramGoatException.toString(str1));
      MLog.log(Level.SEVERE, "GoatHttpServlet: IOException prevented Redirection ", localIOException);
    }
    catch (ServletException localServletException)
    {
      MLog.severe(paramGoatException.toString(str1));
      MLog.log(Level.SEVERE, "GoatHttpServlet: ServletException prevented Redirection ", localServletException);
    }
  }

  public static boolean browserIsRFC2184Compatible(HttpServletRequest paramHttpServletRequest)
  {
    String str = paramHttpServletRequest.getHeader("User-Agent");
    return (str != null) && (str.indexOf("MSIE") == -1) && (str.indexOf("AppleWebKit") == -1);
  }

  public static final String getL10NFileName(String paramString, HttpServletRequest paramHttpServletRequest)
  {
    if ((paramString == null) || (paramString.length() < 1))
      return null;
    String str = SessionData.getLocale(paramHttpServletRequest);
    int i = paramString.lastIndexOf('.');
    StringBuilder localStringBuilder = new StringBuilder(paramString.substring(0, i)).append('_').append(str).append(paramString.substring(i));
    return localStringBuilder.toString();
  }

  public static final String[] getI18nFriendlyPathElements(String paramString1, String paramString2)
    throws GoatException
  {
    int i = paramString1.indexOf(paramString2);
    if (i == -1)
      return null;
    if (i + paramString2.length() >= paramString1.length())
      return null;
    String str = paramString1.substring(i + paramString2.length());
    if (str.charAt(0) == '/')
    {
      if (str.length() < 2)
        return null;
      str = str.substring(1);
    }
    String[] arrayOfString = str.split("/", -1);
    if (arrayOfString != null)
      try
      {
        for (int j = 0; j < arrayOfString.length; j++)
          arrayOfString[j] = URLDecoder.decode(arrayOfString[j], "UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        if (!$assertionsDisabled)
          throw new AssertionError();
        return null;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new GoatException(9240);
      }
    return arrayOfString;
  }

  private static boolean errorInLogin(List<StatusInfo> paramList)
  {
    int i = 0;
    if (paramList != null)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        StatusInfo localStatusInfo = (StatusInfo)localIterator.next();
        i = (int)localStatusInfo.getMessageNum();
        if (((i >= 612) && (i <= 615)) || (i == 623) || (i == 9084) || (i == 9381) || (i == 9382))
          return true;
      }
    }
    return false;
  }

  static
  {
    String str = Configuration.getInstance().getProperty("arsystem.enableHttpTrace");
    boolean bool = Boolean.parseBoolean(str);
    if (bool)
      EnableHttpTrace = true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.GoatHttpServlet
 * JD-Core Version:    0.6.1
 */