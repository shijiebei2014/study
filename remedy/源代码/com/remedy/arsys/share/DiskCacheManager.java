package com.remedy.arsys.share;

import java.io.File;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;

public class DiskCacheManager extends CacheManager
{
  public DiskCacheManager()
    throws CacheException
  {
    super(getDiskConfig());
  }

  protected static net.sf.ehcache.config.Configuration getDiskConfig()
  {
    CacheConfiguration localCacheConfiguration = new CacheConfiguration("diskcache", 0);
    DiskStoreConfiguration localDiskStoreConfiguration = new DiskStoreConfiguration();
    String str1 = com.remedy.arsys.config.Configuration.getInstance().getMidTierCacheDir();
    String str2 = null;
    if ((str1 != null) && (str1.length() > 0))
      str2 = com.remedy.arsys.config.Configuration.getInstance().getMidTierCacheDir();
    else
      str2 = com.remedy.arsys.config.Configuration.getInstance().getRootPath() + File.separator + "cache";
    localDiskStoreConfiguration.setPath(str2);
    net.sf.ehcache.config.Configuration localConfiguration = new net.sf.ehcache.config.Configuration();
    localConfiguration.setDefaultCacheConfiguration(localCacheConfiguration);
    localConfiguration.addDiskStore(localDiskStoreConfiguration);
    if (com.remedy.arsys.config.Configuration.getInstance().getBooleanProperty("arsystem.enableCacheMonitor", false))
    {
      FactoryConfiguration localFactoryConfiguration = new FactoryConfiguration();
      localFactoryConfiguration.setClass("org.terracotta.ehcachedx.monitor.probe.ProbePeerListenerFactory");
      localFactoryConfiguration.setProperties("monitorAddress=localhost, monitorPort=9889, memoryMeasurement=true");
      localConfiguration.addCacheManagerPeerListenerFactory(localFactoryConfiguration);
    }
    return localConfiguration;
  }

  public void shutdown()
  {
    try
    {
      super.shutdown();
    }
    catch (IllegalStateException localIllegalStateException)
    {
    }
  }

  public void shutdownEx()
  {
    flush();
  }

  protected void flush()
  {
    String[] arrayOfString = getCacheNames();
    for (int i = 0; i < arrayOfString.length; i++)
      Cache localCache = getCache(arrayOfString[i]);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.DiskCacheManager
 * JD-Core Version:    0.6.1
 */