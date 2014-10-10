package com.remedy.arsys.session;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.GoatServlet;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.BrowserType;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LicenseReleaseServlet extends GoatServlet
{
  private static String LICENSE_RELEASE_KEY = "lr";
  private static Map<String, Timer> MReleaseList = Collections.synchronizedMap(new HashMap());

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException, ServletException
  {
    Object localObject1 = paramHttpServletRequest.getSession(true);
    String str1 = paramHttpServletRequest.getParameter(LICENSE_RELEASE_KEY);
    String str2 = ((HttpSession)localObject1).getId();
    if (null == str1)
    {
      if (!MReleaseList.containsKey(str2))
        synchronized (localObject1)
        {
          ((HttpSession)localObject1).setAttribute(LICENSE_RELEASE_KEY, Boolean.TRUE);
        }
      else
        removeTimer(str2);
    }
    else
    {
      removeTimer(str2);
      ??? = null;
      synchronized (localObject1)
      {
        ??? = (Boolean)((HttpSession)localObject1).getAttribute(LICENSE_RELEASE_KEY);
        if (??? != null)
          ((HttpSession)localObject1).removeAttribute(LICENSE_RELEASE_KEY);
      }
      setTimer(str2, (HttpSession)localObject1);
    }
    paramHttpServletResponse.setHeader("Cache-Control", "no-store");
    paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    paramHttpServletResponse.setHeader("Pragma", "no-cache");
    paramHttpServletResponse.setDateHeader("Expires", 0L);
    localObject1 = BrowserType.getTypeFromRequest(paramHttpServletRequest);
    if (localObject1 == BrowserType.MOZ)
      paramHttpServletResponse.setContentType("text/plain;charset=UTF-8");
  }

  protected static final void removeTimer(String paramString)
  {
    Timer localTimer = (Timer)MReleaseList.remove(paramString);
    if (localTimer != null)
    {
      localTimer.cancel();
      localTimer.purge();
    }
  }

  protected static final void setTimer(String paramString, HttpSession paramHttpSession)
  {
    int i = Configuration.getInstance().getIntProperty("arsystem.licenserelease_timeout", 60);
    Timer localTimer = new Timer(true);
    localTimer.schedule(new LicenseReleaseTask(paramString, paramHttpSession, new Date().getTime()), i * 1000);
    MReleaseList.put(paramString, localTimer);
  }

  protected void postInternal(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    if (Configuration.getInstance().getEnableResponseHostIP())
      paramHttpServletResponse.addHeader("ARRESPONSEHOSTIP", InetAddress.getLocalHost().getHostAddress());
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    if ((localHttpSession != null) && (paramHttpServletRequest.isRequestedSessionIdValid()))
    {
      SessionData localSessionData = (SessionData)localHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
      if ((localSessionData != null) && (!localSessionData.isLoggedOut()))
        super.postInternal(paramHttpServletRequest, paramHttpServletResponse);
    }
  }

  private static class LicenseReleaseTask extends TimerTask
  {
    private String mID;
    private HttpSession mSession;
    private long expTime = 0L;

    LicenseReleaseTask(String paramString, HttpSession paramHttpSession, long paramLong)
    {
      this.mID = paramString;
      this.mSession = paramHttpSession;
      this.expTime = paramLong;
    }

    long getTime()
    {
      return this.expTime;
    }

    public void run()
    {
      SessionData localSessionData = null;
      try
      {
        localSessionData = (SessionData)this.mSession.getAttribute("com.remedy.arsys.stubs.sessionData");
      }
      catch (IllegalStateException localIllegalStateException)
      {
      }
      if ((localSessionData != null) && (getTime() + 1000L >= this.mSession.getLastAccessedTime()))
        synchronized (localSessionData)
        {
          if (!localSessionData.hasFormRequests())
          {
            localSessionData.dispose(true);
            this.mSession.invalidate();
          }
          else
          {
            LicenseReleaseServlet.removeTimer(this.mID);
            LicenseReleaseServlet.setTimer(this.mID, this.mSession);
            return;
          }
        }
      LicenseReleaseServlet.removeTimer(this.mID);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.LicenseReleaseServlet
 * JD-Core Version:    0.6.1
 */