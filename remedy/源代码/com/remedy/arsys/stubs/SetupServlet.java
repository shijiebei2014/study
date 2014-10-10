package com.remedy.arsys.stubs;

import com.bmc.arsys.api.ProxyManager;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.IConfigObserver;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.prefetch.PrefetchTask;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.share.LogDataImpl;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.CacheManager;
import org.eclipse.birt.report.utility.ParameterAccessor;

public class SetupServlet extends GoatHttpServlet
{
  public static String baseURL = null;
  public static final int DEFAULT_MAX_CONNECTIONS_PER_SERVER = 75;
  private final Object syncObj = new Object();

  public void init(ServletConfig paramServletConfig)
    throws ServletException
  {
    super.init(paramServletConfig);
    String str1 = getInitParameter("type");
    if (str1 == null)
      throw new ServletException("Missing or bad type for ReportSetupServlet");
    Log.setLogData(LogDataImpl.getLogData());
    String str2 = "BMC Remedy Mid Tier version " + "8.1.00 201301251157".trim();
    Log localLog = Log.getRootLogger();
    localLog.log(Level.SEVERE, str2);
    MLog = Log.get(10);
    Configuration localConfiguration = Configuration.getInstance();
    localConfiguration.adjustServerListForHostName();
    localConfiguration.setInstallType(str1);
    localConfiguration.setServletContext(paramServletConfig.getServletContext());
    GoatCacheManager.setOverflowToDisk(localConfiguration.getOverflowToDisk());
    int i = localConfiguration.getIntProperty("arsystem.pooling_max_connections_per_server", 75);
    ProxyManager.setConnectionLimits(i);
    ProxyManager.setUseConnectionPooling(localConfiguration.getEnableUseConnectionPooling());
    ResourceService.getInstance().start(MLog);
    Map localMap = ParameterAccessor.initViewerProps(getServletContext(), null);
    synchronized (this.syncObj)
    {
      baseURL = (String)localMap.get("base_url");
      if ((baseURL != null) && (baseURL.length() == 0))
        baseURL = null;
    }
    Cache.initServerReapers();
    localConfiguration.addConfigObserver(new ConfigObserver(null));
    PrefetchTask.schedule(10L);
    Login.findAndFillPasswordFormNames();
  }

  public void destroy()
  {
    try
    {
      synchronized (CacheManager.class)
      {
        GoatCacheManager localGoatCacheManager = Cache.getCacheManager();
        Log localLog = Log.get(1);
        localLog.log(Level.INFO, "CACHE: SetupServlet:destroy: CacheManager shutdown");
        localGoatCacheManager.shutdownEx();
      }
      Thread.sleep(400L);
    }
    catch (ExceptionInInitializerError localExceptionInInitializerError)
    {
      localExceptionInInitializerError.printStackTrace();
    }
    catch (InterruptedException localInterruptedException)
    {
      localInterruptedException.printStackTrace();
    }
  }

  protected final void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    paramHttpServletResponse.sendError(404);
  }

  private static final class ConfigObserver
    implements IConfigObserver
  {
    public void notify(Object paramObject1, int paramInt, Object paramObject2)
    {
      switch (paramInt)
      {
      case 2:
        Cache.initServerReapers();
        break;
      case 3:
        Cache.removeServerReaper((String)paramObject2);
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.SetupServlet
 * JD-Core Version:    0.6.1
 */