package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.api.ARServerUser;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.plugincontainer.ARPluginContext;
import com.remedy.arsys.plugincontainer.ARPluginContextEX;
import com.remedy.arsys.plugincontainer.AuthenticationException;
import com.remedy.arsys.plugincontainer.DefinitionService;
import com.remedy.arsys.plugincontainer.IDVMSkinService;
import com.remedy.arsys.plugincontainer.IEncryptionService;
import com.remedy.arsys.plugincontainer.LocaleService;
import com.remedy.arsys.plugincontainer.LogService;
import com.remedy.arsys.plugincontainer.PageService;
import com.remedy.arsys.plugincontainer.Plugin;
import com.remedy.arsys.plugincontainer.PluginContextEX;
import com.remedy.arsys.stubs.RequestInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PluginContextImpl
  implements PluginContextEX, ARPluginContext, ARPluginContextEX
{
  private HttpServletRequest mHttpRequest;
  private HttpServletResponse mHttpResponse;
  private ServletContext mServletContext;
  private String mServer;
  private PageService mPageService;
  private ServerLogin mLoginContext;
  private DefinitionService mDefinitionService;
  private LocaleServiceImpl mLocaleService;
  private LogServiceImpl mLogService;
  private DVMSkinServiceImpl mSkinService;
  private EncryptionServiceImpl mEncService;
  private String mPluginName;
  private Plugin mPlugin;
  private boolean mIsPrivileged = true;

  PluginContextImpl(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, ServletContext paramServletContext, ServerLogin paramServerLogin, String paramString, RequestInfo paramRequestInfo)
    throws GoatException, IOException
  {
    this.mHttpRequest = paramHttpServletRequest;
    this.mHttpResponse = paramHttpServletResponse;
    this.mServletContext = paramServletContext;
    this.mLoginContext = paramServerLogin;
    this.mPluginName = paramString;
    this.mPlugin = PluginFactory.getInstance(this.mPluginName);
    if (this.mLoginContext != null)
    {
      this.mServer = this.mLoginContext.getServer();
      this.mPageService = new PageServiceImpl(this.mHttpRequest, this.mServer, this.mPluginName, paramRequestInfo.getFieldId(), paramRequestInfo.getAppname(), paramRequestInfo.getWindowID());
      this.mDefinitionService = new DefinitionServiceImpl(this);
      this.mLocaleService = new LocaleServiceImpl(this.mServer, this.mPluginName, paramHttpServletRequest.getParameterMap());
      this.mLogService = new LogServiceImpl(this.mPluginName);
      if (paramRequestInfo.getFormName() != null)
        this.mSkinService = new DVMSkinServiceImpl(this, paramRequestInfo);
      this.mEncService = new EncryptionServiceImpl(this.mLoginContext);
    }
  }

  Plugin getPlugin()
  {
    return this.mPlugin;
  }

  String getServer()
  {
    return this.mServer;
  }

  void resetResponse()
  {
    this.mHttpResponse = null;
  }

  public String getPluginName()
  {
    return this.mPluginName;
  }

  public HttpServletRequest getRequest()
  {
    return this.mHttpRequest;
  }

  public HttpServletResponse getResponse()
  {
    return this.mHttpResponse;
  }

  public ServletContext getServletContext()
  {
    return this.mServletContext;
  }

  public PageService getPageService()
  {
    return this.mPageService;
  }

  public DefinitionService getDefinitionService()
  {
    return this.mDefinitionService;
  }

  public LocaleService getLocaleService()
  {
    return this.mLocaleService;
  }

  public ARServerUser getServerUser()
    throws AuthenticationException
  {
    ServerLogin localServerLogin = this.mLoginContext;
    try
    {
      return ARConversionHelper.convertTo70(localServerLogin);
    }
    catch (GoatException localGoatException)
    {
      throw new AuthenticationException(localGoatException.toString(), localGoatException);
    }
  }

  public ARServerUser getServerUser(String paramString)
    throws AuthenticationException
  {
    try
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString);
      return ARConversionHelper.convertTo70(localServerLogin);
    }
    catch (GoatException localGoatException)
    {
      throw new AuthenticationException(localGoatException.toString(), localGoatException);
    }
  }

  public ARServerUser getAdminServerUser()
    throws AuthenticationException
  {
    return getAdminServerUser(this.mServer);
  }

  public ARServerUser getAdminServerUser(String paramString)
    throws AuthenticationException
  {
    if (this.mIsPrivileged)
      try
      {
        ServerLogin localServerLogin = ServerLogin.getAdmin(paramString);
        return ARConversionHelper.convertTo70(localServerLogin);
      }
      catch (GoatException localGoatException1)
      {
        throw new AuthenticationException(localGoatException1.toString(), localGoatException1);
      }
    GoatException localGoatException2 = new GoatException(9403, this.mPluginName, this.mServer);
    throw new AuthenticationException(localGoatException2.getLocalizedMessage());
  }

  public Object getAdminServerUserObject()
    throws AuthenticationException
  {
    return getAdminServerUserObject(this.mServer);
  }

  public Object getAdminServerUserObject(String paramString)
    throws AuthenticationException
  {
    if (this.mIsPrivileged)
      try
      {
        return ServerLogin.getAdmin(paramString);
      }
      catch (GoatException localGoatException1)
      {
        throw new AuthenticationException(localGoatException1.toString(), localGoatException1);
      }
    GoatException localGoatException2 = new GoatException(9403, this.mPluginName, this.mServer);
    throw new AuthenticationException(localGoatException2.getLocalizedMessage());
  }

  public Object getServerUserObject()
    throws AuthenticationException
  {
    return this.mLoginContext;
  }

  public Object getServerUserObject(String paramString)
    throws AuthenticationException
  {
    try
    {
      return SessionData.get().getServerLogin(paramString);
    }
    catch (GoatException localGoatException)
    {
      throw new AuthenticationException(localGoatException.toString(), localGoatException);
    }
  }

  public LogService getLogService()
  {
    return this.mLogService;
  }

  public IDVMSkinService getSkinService()
  {
    return this.mSkinService;
  }

  public IEncryptionService getEncService()
  {
    return this.mEncService;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PluginContextImpl
 * JD-Core Version:    0.6.1
 */