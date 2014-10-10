package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.BMCMidTierPerf;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class ServiceLocator extends ContextLoaderListener
{
  private static ServiceLocator serviceLocator = new ServiceLocator();
  private static BeanFactory beanFactory;
  private static Map<String, Object> beanMap = new ConcurrentHashMap();

  public static final ServiceLocator getInstance()
  {
    return serviceLocator;
  }

  public Object getService(String paramString)
  {
    Object localObject = null;
    localObject = beanMap.get(paramString);
    if (localObject == null)
    {
      localObject = beanFactory.getBean(paramString);
      beanMap.put(paramString, localObject);
    }
    return localObject;
  }

  public void contextInitialized(ServletContextEvent paramServletContextEvent)
  {
    super.contextInitialized(paramServletContextEvent);
    ServletContext localServletContext = paramServletContextEvent.getServletContext();
    beanFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(localServletContext);
    if (Configuration.getInstance().getEnableMidtierPerfMBean())
      BMCMidTierPerf.registerBMCMidTierPerfMBean();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ServiceLocator
 * JD-Core Version:    0.6.1
 */