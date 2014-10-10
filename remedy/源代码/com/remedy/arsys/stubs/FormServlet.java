package com.remedy.arsys.stubs;

import com.bmc.arsys.api.ARServerUser;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatApplicationContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.cache.sync.ServerSync;
import com.remedy.arsys.goat.cache.sync.SyncException;
import com.remedy.arsys.goat.intf.service.IRequestService;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.UrlHelper;
import com.remedy.arsys.share.WebWriter;
import com.remedy.arsys.support.BrowserType;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FormServlet extends GoatServlet
{
  private static final long serialVersionUID = 528632188139612848L;
  private boolean mApp;

  public void init()
    throws ServletException
  {
    String str = getInitParameter("type");
    if (str.equals("form"))
      this.mApp = false;
    else if (str.equals("app"))
      this.mApp = true;
    else
      throw new ServletException("Missing or bad type (" + str + ") for FormServlet");
  }

  protected final void acquireSessionData(SessionData paramSessionData)
  {
    synchronized (paramSessionData)
    {
      if (!paramSessionData.isLoggedOut())
      {
        paramSessionData.incrFormRequestCount();
        SESSION_DATA_ACQUIRED.set(Boolean.valueOf(true));
      }
    }
  }

  protected final void releaseSessionData(SessionData paramSessionData)
  {
    Boolean localBoolean = (Boolean)SESSION_DATA_ACQUIRED.get();
    SESSION_DATA_ACQUIRED.set(null);
    if ((localBoolean != null) && (localBoolean.booleanValue()))
      paramSessionData.decrFormRequestCount();
  }

  protected boolean redirectToLogin(HttpServletRequest paramHttpServletRequest)
  {
    String[] arrayOfString;
    try
    {
      arrayOfString = getI18nFriendlyPathElements(paramHttpServletRequest);
    }
    catch (GoatException localGoatException)
    {
      return true;
    }
    if (arrayOfString != null)
      for (int i = arrayOfString.length - 1; i >= 0; i--)
        if ((arrayOfString[i].equalsIgnoreCase("form.js")) || (arrayOfString[i].equalsIgnoreCase("uds.js")) || (arrayOfString[i].equalsIgnoreCase("udd.js")) || (arrayOfString[i].equalsIgnoreCase("AppList.html")))
          return false;
    return true;
  }

  protected void errorScript(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, GoatException paramGoatException)
    throws IOException
  {
    String[] arrayOfString = null;
    try
    {
      arrayOfString = getI18nFriendlyPathElements(paramHttpServletRequest);
    }
    catch (GoatException localGoatException)
    {
    }
    if (arrayOfString != null)
      for (int i = arrayOfString.length - 1; i >= 0; i--)
        if (arrayOfString[i].equalsIgnoreCase("AppList.html"))
        {
          String str = paramGoatException.toString();
          paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
          PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
          localPrintWriter.print("<html><head><script>var Msg=");
          localPrintWriter.print(WebWriter.escapeString(str));
          localPrintWriter.println(";</script></head><body></body></html>");
          return;
        }
    try
    {
      paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
      paramHttpServletResponse.setHeader("Pragma", "no-cache");
      paramHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
      paramHttpServletResponse.setContentType("text/plain; charset=UTF-8");
      JSWriter localJSWriter = new JSWriter();
      localJSWriter.statement("var jsPageException = " + JSWriter.escapeString(paramGoatException.toString()));
      localJSWriter.statement("if(typeof LogAndAlert==\"function\")LogAndAlert(jsPageException);");
      paramHttpServletResponse.getWriter().print(localJSWriter);
    }
    catch (IOException localIOException)
    {
      MLog.log(Level.SEVERE, "Caught IO exception sending script error", localIOException);
    }
  }

  protected void invalidSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    errorScript(paramHttpServletRequest, paramHttpServletResponse, new GoatException(9201));
  }

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException, ServletException
  {
    MLog.fine("FormServlet: URI=" + paramHttpServletRequest.getRequestURI());
    UrlHelper localUrlHelper = new UrlHelper(paramHttpServletRequest, this.mApp);
    String str = localUrlHelper.getForm();
    Object localObject2;
    if ((str != null) && (!Login.isUserPasswordChangeForm(str)) && (SessionData.get().isPasswordChangeRequired()))
    {
      localObject1 = paramHttpServletRequest.getRequestURI().replaceFirst(paramHttpServletRequest.getContextPath(), "../../../..");
      localObject2 = paramHttpServletRequest.getQueryString();
      if (localObject2 != null)
        ((String)localObject1).concat("?").concat((String)localObject2);
      SessionData.get().setPasswordChangeTargetURL((String)localObject1);
      CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
      paramHttpServletResponse.sendRedirect(Login.getForcePasswordChangeURI(paramHttpServletRequest, Configuration.getInstance().getAuthServer()));
      return;
    }
    MLog.fine(localUrlHelper.toString());
    if (Configuration.getInstance().getEnableResponseHostIP())
      paramHttpServletResponse.addHeader("ARRESPONSEHOSTIP", InetAddress.getLocalHost().getHostAddress());
    Object localObject1 = FormContext.get();
    if (((str == null) && (localUrlHelper.getFile() == null)) || (localUrlHelper.getSize() < 2))
    {
      MLog.fine("redirect: " + ((FormContext)localObject1).getObjectListURL(localUrlHelper.getServer(), localUrlHelper.getApp()));
      CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
      paramHttpServletResponse.sendRedirect(((FormContext)localObject1).getObjectListURL(localUrlHelper.getServer(), localUrlHelper.getApp()));
      return;
    }
    Object localObject3;
    Object localObject4;
    Object localObject5;
    if (localUrlHelper.getApp() != null)
    {
      ((FormContext)localObject1).setApplication(localUrlHelper.getApp());
      localObject3 = SessionData.get().getServerLogin(localUrlHelper.getServer());
      localObject2 = GoatApplicationContainer.get((ServerLogin)localObject3, localUrlHelper.getApp());
      if (localUrlHelper.getAppResource() != null)
      {
        localObject4 = BrowserType.getTypeFromRequest(paramHttpServletRequest);
        localObject5 = ((GoatApplicationContainer)localObject2).getResourceFile((BrowserType)localObject4, localUrlHelper.getAppResource());
        if (localObject5 != null)
        {
          ((Globule)localObject5).transmit(paramHttpServletRequest, paramHttpServletResponse);
          return;
        }
        paramHttpServletResponse.sendError(404);
        return;
      }
    }
    Object localObject6;
    if (localUrlHelper.getFile() == null)
    {
      localObject2 = Form.get(localUrlHelper.getServer(), localUrlHelper.getForm());
      localObject3 = ((Form)localObject2).getViewInfoByInference(localUrlHelper.getView(), false, false);
      MLog.fine("No view specified - using " + ((Form.ViewInfo)localObject3).getLabel());
      localObject4 = ((FormContext)localObject1).getContextURL() + ((FormContext)localObject1).getFormURL(localUrlHelper.getOriginalServer(), localUrlHelper.getForm(), ((Form.ViewInfo)localObject3).getLabel());
      localObject5 = paramHttpServletRequest.getQueryString();
      localObject6 = UrlHelper.newUrlForCacheID((String)localObject4, (String)localObject5, (Form)localObject2, (Form.ViewInfo)localObject3);
      if (localObject6 == null)
        localObject6 = (String)localObject4 + "?" + (String)localObject5;
      if (SessionData.get().isFlexEnabled())
        localObject6 = (String)localObject6 + "&" + "format" + "=" + "flex";
      CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
      paramHttpServletResponse.sendRedirect(Validator.sanitizeCRandLF((String)localObject6));
      return;
    }
    if (localUrlHelper.getFile().length() == 0)
    {
      int i = Configuration.getInstance().getCacheUpdateInterval();
      if ((Configuration.getInstance().getCacheUpdateNeeded()) && (i == 0))
        try
        {
          localObject3 = Form.get(localUrlHelper.getServer(), localUrlHelper.getForm());
          localObject4 = ((Form)localObject3).getViewInfoByInference(localUrlHelper.getView(), false, false);
          localObject5 = GoatCacheManager.getInstance();
          localObject6 = ServerLogin.getAdmin(localUrlHelper.getServer());
          ServerSync localServerSync = new ServerSync(localUrlHelper.getServer(), (ARServerUser)localObject6, (GoatCacheManager)localObject5);
          localServerSync.developmentSync((Form.ViewInfo)localObject4);
        }
        catch (SyncException localSyncException)
        {
          localSyncException.printStackTrace();
        }
    }
    IRequestService localIRequestService = resolveRequestService(paramHttpServletRequest);
    localIRequestService.requestDispatch(paramHttpServletRequest, paramHttpServletResponse, localUrlHelper);
  }

  public static String HexString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      localStringBuilder.append(Integer.toHexString(paramString.charAt(i)));
      localStringBuilder.append(" ");
    }
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.FormServlet
 * JD-Core Version:    0.6.1
 */