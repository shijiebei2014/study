package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.plugincontainer.NoPermissionException;
import com.remedy.arsys.plugincontainer.Plugin;
import com.remedy.arsys.stubs.AuthenticationHelperServlet;
import com.remedy.arsys.stubs.GoatHttpServlet;
import com.remedy.arsys.stubs.RequestInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class PluginContainer extends AuthenticationHelperServlet
{
  private static final Log MLog = Log.get(13);

  private PluginContextImpl setupEnv(RequestInfo paramRequestInfo, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    String str1 = paramRequestInfo.getServer();
    ServerLogin localServerLogin;
    if (str1 != null)
      localServerLogin = SessionData.get().getServerLogin(str1);
    else
      localServerLogin = null;
    String[] arrayOfString = GoatHttpServlet.getI18nFriendlyPathElements(paramHttpServletRequest);
    String str2 = (arrayOfString != null) && (arrayOfString.length > 0) ? arrayOfString[0] : "";
    return new PluginContextImpl(paramHttpServletRequest, paramHttpServletResponse, getServletContext(), localServerLogin, str2, paramRequestInfo);
  }

  public void destroy()
  {
    PluginFactory.cleanup();
    super.destroy();
  }

  protected FormContext arrangeFormContext(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException
  {
    String[] arrayOfString = GoatHttpServlet.getI18nFriendlyPathElements(paramHttpServletRequest);
    String str = (String)paramHttpServletRequest.getAttribute("fbimageservlet_fwd");
    StringBuilder localStringBuilder = new StringBuilder();
    if ((str != null) && (str.equals("true")))
      localStringBuilder.append("../");
    else if (arrayOfString != null)
      for (int i = 0; i < arrayOfString.length; i++)
        localStringBuilder.append("../");
    return new PluginFormContext(localStringBuilder.toString(), paramHttpServletRequest.getContextPath() + "/", getServletContext().getRealPath("/"), null);
  }

  protected void processRequestInfo(RequestInfo paramRequestInfo, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    try
    {
      PluginContextImpl localPluginContextImpl = setupEnv(paramRequestInfo, paramHttpServletRequest, paramHttpServletResponse);
      postPluginInfo(localPluginContextImpl.getPlugin(), localPluginContextImpl);
    }
    catch (Exception localException1)
    {
      MLog.log(Level.SEVERE, "Exception while processing request", localException1);
      if (!paramHttpServletResponse.isCommitted())
      {
        paramHttpServletResponse.reset();
        paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
        paramHttpServletResponse.setHeader("Pragma", "no-cache");
        paramHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
        String str = localException1.getLocalizedMessage();
        Exception localException2;
        if (str == null)
          localException2 = (Exception)new GoatException(3600).initCause(localException1);
        generateErrorResponse(paramHttpServletRequest, paramHttpServletResponse, localException2);
      }
    }
  }

  protected abstract void postPluginInfo(Plugin paramPlugin, PluginContextImpl paramPluginContextImpl)
    throws ServletException, GoatException, IOException, NoPermissionException;

  protected abstract void generateErrorResponse(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, Exception paramException)
    throws IOException;

  private static class PluginFormContext extends FormContext
  {
    private PluginFormContext(String paramString1, String paramString2, String paramString3)
    {
      super(paramString1, paramString2, paramString3);
    }

    public void setApplication(String paramString)
    {
      this.mApplication = paramString;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PluginContainer
 * JD-Core Version:    0.6.1
 */