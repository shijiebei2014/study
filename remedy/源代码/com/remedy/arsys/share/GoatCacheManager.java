package com.remedy.arsys.share;

import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.prefetch.PrefetchTask;
import com.remedy.arsys.prefetch.ViewInfoCollector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.distribution.CacheManagerPeerListener;
import net.sf.ehcache.event.CacheManagerEventListener;

public class GoatCacheManager extends CacheManager
{
  private static final Log cacheLog = Log.get(1);
  private static boolean blnOverflowToDisk = false;
  public static Object mutex = new Object();
  protected final Map<String, Cache> goatCaches = new HashMap();
  public static boolean inPrefetch = false;
  private static Hashtable<PrefetchTask, String> prefetchMap = new Hashtable();
  private static boolean dirtyCheckEnable = true;
  private static boolean forceSave = true;
  private static Object SaveObj = new Object();
  private static boolean shutDown = false;
  private static DiskCacheManager diskMgr;
  private static GoatCacheManager instance = new GoatCacheManager();
  private static Thread saveThread;
  private static Log MPerformanceLog = Log.get(8);
  private static long totaltime = 0L;
  private static int cnt = 0;
  private static boolean readyToSave = true;

  public static void SetPrefetchFlag(boolean paramBoolean)
  {
    inPrefetch = paramBoolean;
  }

  public synchronized void addCache(Cache paramCache)
    throws IllegalStateException, ObjectExistsException, CacheException
  {
    this.goatCaches.put(paramCache.getName(), paramCache);
    if (paramCache.getDiskCache() != null)
    {
      diskMgr.addCache(paramCache.getDiskCache());
      paramCache.getDiskCache().setStatisticsEnabled(com.remedy.arsys.config.Configuration.getInstance().getCacheEnableStatistics());
    }
    super.addCache(paramCache.getMemCache());
    paramCache.getMemCache().setStatisticsEnabled(com.remedy.arsys.config.Configuration.getInstance().getCacheEnableStatistics());
  }

  public static boolean getDiskPersistent()
  {
    return blnOverflowToDisk;
  }

  public static GoatCacheManager getInstance()
  {
    return instance;
  }

  public GoatCacheManager()
    throws CacheException
  {
    super(getMemConfig());
    diskMgr = new DiskCacheManager();
    saveThread = new SaveThread();
    saveThread.start();
  }

  public static void setOverflowToDisk(boolean paramBoolean)
  {
    blnOverflowToDisk = paramBoolean;
  }

  public synchronized String update()
  {
    return null;
  }

  private static net.sf.ehcache.config.Configuration getMemConfig()
  {
    CacheConfiguration localCacheConfiguration = new CacheConfiguration("defaultCache", 0);
    DiskStoreConfiguration localDiskStoreConfiguration = new DiskStoreConfiguration();
    String str1 = com.remedy.arsys.config.Configuration.getInstance().getMidTierCacheTempDir();
    String str2 = null;
    if ((str1 != null) && (str1.length() > 0))
      str2 = str1;
    else
      str2 = com.remedy.arsys.config.Configuration.getInstance().getRootPath() + File.separator + "cachetemp";
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
    localConfiguration.setName("memo");
    return localConfiguration;
  }

  public void shutdown()
  {
    try
    {
      try
      {
        super.shutdown();
      }
      finally
      {
        if (diskMgr != null)
          diskMgr.shutdown();
      }
    }
    catch (Exception localException)
    {
      System.out.println("Exception in Shutdown -");
      localException.printStackTrace();
    }
  }

  public void shutdownEx()
  {
    shutDown = true;
    synchronized (mutex)
    {
      Enumeration localEnumeration = prefetchMap.keys();
      while (localEnumeration.hasMoreElements())
      {
        PrefetchTask localPrefetchTask = (PrefetchTask)localEnumeration.nextElement();
        localPrefetchTask.stop();
      }
      try
      {
        Thread.sleep(500L);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
      finally
      {
        ViewInfoCollector.saveStatistics();
      }
      blnOverflowToDisk = false;
      ActiveLinkCache localActiveLinkCache = (ActiveLinkCache)getGoatCache("Active links");
      if (localActiveLinkCache != null)
        synchronized (localActiveLinkCache)
        {
          ActiveLinkCache.resetActiveLinkCount();
          localActiveLinkCache.save();
        }
      boolean bool = com.remedy.arsys.config.Configuration.getInstance().getOverflowToDiskTemp();
      if (!bool)
      {
        String[] arrayOfString = getCacheNames();
        for (int i = 0; i < arrayOfString.length; i++)
        {
          Cache localCache = getGoatCache(arrayOfString[i]);
          if (localCache != null)
            synchronized (localCache)
            {
              localCache.save();
            }
        }
      }
      shutdown();
    }
  }

  public synchronized void removalAll()
  {
    super.removalAll();
    diskMgr.removalAll();
  }

  public synchronized void removeCache(String paramString)
    throws IllegalStateException
  {
    try
    {
      super.removeCache(paramString);
    }
    finally
    {
      try
      {
        diskMgr.removeCache(paramString);
      }
      finally
      {
        if (this.goatCaches.containsKey(paramString))
          this.goatCaches.remove(paramString);
      }
    }
  }

  protected synchronized void removeCacheNoDispose(String paramString)
    throws IllegalStateException
  {
    if ((paramString == null) || (paramString.length() == 0))
      return;
    removeCache(paramString);
    if (getCacheManagerEventListener() != null)
      getCacheManagerEventListener().notifyCacheRemoved(paramString);
    CacheManagerPeerListener localCacheManagerPeerListener = getCachePeerListener(com.remedy.arsys.config.Configuration.getInstance().getCachePeerListenerScheme());
    if (localCacheManagerPeerListener != null)
      localCacheManagerPeerListener.notifyCacheRemoved(paramString);
  }

  public static void addPrefetch(PrefetchTask paramPrefetchTask)
  {
    synchronized (mutex)
    {
      if (prefetchMap == null)
        return;
      prefetchMap.put(paramPrefetchTask, "");
      if (shutDown)
        paramPrefetchTask.stop();
    }
  }

  public static void removePrefetch(PrefetchTask paramPrefetchTask)
  {
    synchronized (mutex)
    {
      if (prefetchMap == null)
        return;
      if (prefetchMap.containsKey(paramPrefetchTask))
        prefetchMap.remove(paramPrefetchTask);
    }
  }

  private static void createBackupDir()
  {
    String str1 = DiskCacheManager.getDiskConfig().getDiskStoreConfiguration().getPath();
    String str2 = str1 + File.separator + "backup";
    System.out.println(str2);
    File localFile = new File(str2);
    if ((localFile.exists()) && (!localFile.isDirectory()))
      try
      {
        throw new Exception("Store directory \"" + localFile.getCanonicalPath() + "\" exists and is not a directory.");
      }
      catch (IOException localIOException1)
      {
        localIOException1.printStackTrace();
      }
      catch (Exception localException1)
      {
        localException1.printStackTrace();
      }
    if ((!localFile.exists()) && (!localFile.mkdirs()))
      try
      {
        throw new Exception("Could not create cache directory \"" + localFile.getCanonicalPath() + "\".");
      }
      catch (IOException localIOException2)
      {
        localIOException2.printStackTrace();
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
  }

  public Cache getGoatCache(String paramString)
  {
    return (Cache)this.goatCaches.get(paramString);
  }

  public Set<String> getGoatCacheNames()
  {
    return this.goatCaches.keySet();
  }

  public boolean containsGoatCache(String paramString)
  {
    return this.goatCaches.containsKey(paramString);
  }

  public static void backup()
  {
    forceSave();
  }

  public static void printTotalTime()
  {
    MPerformanceLog.fine("Total disk save time = " + totaltime + " number of count = " + cnt);
  }

  public static void doSave()
  {
    doSave(false);
  }

  public static void doSave(boolean paramBoolean)
  {
    if (!getDiskPersistent())
      return;
    Date localDate1 = new Date();
    forceSave = false;
    GoatCacheManager localGoatCacheManager = getInstance();
    try
    {
      String[] arrayOfString = localGoatCacheManager.getCacheNames();
      for (int i = 0; i < arrayOfString.length; i++)
      {
        Cache localCache = localGoatCacheManager.getGoatCache(arrayOfString[i]);
        if (localCache != null)
          synchronized (localCache)
          {
            if (!paramBoolean)
              localCache.saveOnSizeChange();
            else
              localCache.save();
          }
      }
    }
    catch (Exception localException)
    {
      cacheLog.fine(localException.toString());
    }
    cnt += 1;
    Date localDate2 = new Date();
    totaltime += localDate2.getTime() - localDate1.getTime();
  }

  public static void forceSave()
  {
    if ((!readyToSave) || (!getDiskPersistent()) || (PrefetchTask.isPreloadTaskRunning()))
      return;
    synchronized (SaveObj)
    {
      SaveObj.notify();
    }
  }

  public static void deleteAll()
  {
    GoatCacheManager localGoatCacheManager = getInstance();
    String[] arrayOfString = localGoatCacheManager.getCacheNames();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      Cache localCache = localGoatCacheManager.getGoatCache(arrayOfString[i]);
      if (localCache != null)
        synchronized (localCache)
        {
          Ehcache localEhcache = localCache.getDiskCache();
          if (localEhcache != null)
            localEhcache.removeAll();
        }
    }
  }

  private static void copyFile(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    int i = paramString1.lastIndexOf("_");
    if (i != -1)
      return;
    String str = paramString1 + paramString4;
    System.out.println("file name= " + paramString2 + File.separator + str);
    File localFile1 = new File(paramString2 + File.separator + str);
    File localFile2 = new File(paramString3 + File.separator + str);
    FileReader localFileReader = null;
    FileWriter localFileWriter = null;
    try
    {
      localFileReader = new FileReader(localFile1);
      localFileWriter = new FileWriter(localFile2);
      int j;
      while ((j = localFileReader.read()) != -1)
        localFileWriter.write(j);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localFileNotFoundException.printStackTrace();
    }
    catch (IOException localIOException5)
    {
      localIOException5.printStackTrace();
    }
    finally
    {
      try
      {
        if (localFileReader != null)
          localFileReader.close();
      }
      catch (IOException localIOException8)
      {
        localIOException8.printStackTrace();
      }
      try
      {
        if (localFileWriter != null)
          localFileWriter.close();
      }
      catch (IOException localIOException9)
      {
        localIOException9.printStackTrace();
      }
    }
    System.out.println("END Backup");
  }

  private static void recoverFile(String paramString1, String paramString2, String paramString3)
  {
    copyFile(paramString1, paramString2, paramString3, ".data");
    copyFile(paramString1, paramString2, paramString3, ".index");
  }

  private static void backupFiles(String paramString1, String paramString2, String paramString3)
  {
    copyFile(paramString1, paramString2, paramString3, ".data");
    copyFile(paramString1, paramString2, paramString3, ".index");
  }

  public static void recover()
  {
    String str1 = DiskCacheManager.getDiskConfig().getDiskStoreConfiguration().getPath();
    String str2 = str1 + File.separator + "backup";
    String[] arrayOfString = diskMgr.getCacheNames();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      diskMgr.getCache(arrayOfString[i]).flush();
      recoverFile(arrayOfString[i], str2, str1);
    }
  }

  public static boolean getForceSaveFlag()
  {
    return forceSave;
  }

  public static void setForceSaveFlag()
  {
    if (blnOverflowToDisk)
      forceSave = true;
    else
      forceSave = false;
  }

  public static void setDirtyCheckEnable(boolean paramBoolean)
  {
    dirtyCheckEnable = paramBoolean;
  }

  public static boolean getDirtyCheckEnable()
  {
    return dirtyCheckEnable;
  }

  public static void printCacheStatus(String paramString)
  {
    String str1 = com.remedy.arsys.config.Configuration.getInstance().getProperty("arsystem.log_level");
    if ((str1 != null) && (!str1.equalsIgnoreCase("Fine")))
      return;
    cacheLog.fine("******************************************");
    cacheLog.fine("Beginning Cache status report for AR server: " + paramString);
    try
    {
      LinkedHashSet localLinkedHashSet = new LinkedHashSet();
      String[] arrayOfString1 = GoatCacheConstants.CACHE_NAMES;
      int i = arrayOfString1.length;
      for (int j = 0; j < i; j++)
        localLinkedHashSet.add(arrayOfString1[j]);
      localLinkedHashSet.add("Attachment");
      String[] arrayOfString2 = GoatCacheConstants.CACHE_NAME_PREFIXES;
      int k = arrayOfString2.length;
      Set localSet = instance.getGoatCacheNames();
      Object localObject = localSet.iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str2 = (String)((Iterator)localObject).next();
        for (int m = 0; m < k; m++)
          if (str2.startsWith(arrayOfString2[m]))
          {
            localLinkedHashSet.add(str2);
            break;
          }
      }
      localObject = (String[])localLinkedHashSet.toArray(new String[0]);
      long l = 0L;
      String str3 = null;
      for (int n = 0; localObject.length > n; n++)
      {
        l = 0L;
        str3 = null;
        if ("Attachment".equals(localObject[n]))
        {
          str3 = "Attachment";
          cacheLog.fine("CacheName: " + str3);
          l = AttachmentData.getInMemoryAttachmentDataCount();
          cacheLog.fine("keysCount: " + l);
        }
        else
        {
          Cache localCache = instance.getGoatCache(localObject[n]);
          if (localCache == null)
            continue;
          net.sf.ehcache.Cache localCache1 = localCache.getMemCache();
          str3 = localCache1.getName();
          cacheLog.fine("CacheName: " + str3);
          List localList = localCache1.getKeys();
          l = 0L;
          for (int i1 = 0; localList.size() > i1; i1++)
          {
            String str4 = (String)localList.get(i1);
            if ((str4 != null) && (str4.indexOf(paramString) != -1))
            {
              l += 1L;
              Element localElement = localCache1.getQuiet(str4);
              cacheLog.fine("keyName: " + str4 + ";  keySize: " + (localElement != null ? Long.valueOf(localElement.getSerializedSize()) : "N/A"));
            }
          }
          cacheLog.fine("keysCount: " + l);
        }
        cacheLog.fine("-----------------------------------------------------");
      }
    }
    catch (Exception localException)
    {
    }
    cacheLog.fine("Cache status report end for AR server: " + paramString);
    cacheLog.fine("******************************************");
  }

  public static String dumpHTMLStats()
  {
    HTMLWriter localHTMLWriter = new HTMLWriter();
    localHTMLWriter.openTag("tr").attr("class", "BaseTableHeader").attr("nowrap").endTag(false);
    localHTMLWriter.openTag("th").attr("class", "BaseTableHeader").attr("nowrap").endTag(false).cdata("Object name").closeTag("th", false);
    localHTMLWriter.openTag("th").attr("class", "BaseTableHeader").attr("nowrap").endTag(false).cdata("Object count").closeTag("th", false);
    if (com.remedy.arsys.config.Configuration.getInstance().getCacheEnableStatistics())
    {
      localHTMLWriter.openTag("th").attr("class", "BaseTableHeader").attr("nowrap").endTag(false).cdata("Hit count").closeTag("th", false);
      localHTMLWriter.openTag("th").attr("class", "BaseTableHeader").attr("nowrap").endTag(false).cdata("Miss count").closeTag("th", false);
    }
    localHTMLWriter.openTag("th").attr("class", "BaseTableHeader").attr("nowrap").endTag(false).cdata("Last flush").closeTag("th", false);
    localHTMLWriter.closeTag("tr");
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString1 = GoatCacheConstants.CACHE_NAMES;
    int i = arrayOfString1.length;
    for (int j = 0; j < i; j++)
    {
      Cache localCache1 = instance.getGoatCache(arrayOfString1[j]);
      if (localCache1 != null)
        localArrayList.add(arrayOfString1[j]);
    }
    String[] arrayOfString2 = GoatCacheConstants.CACHE_NAME_PREFIXES;
    int k = arrayOfString2.length;
    Set localSet = instance.getGoatCacheNames();
    Object localObject = localSet.iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      for (n = 0; n < k; n++)
        if (str.startsWith(arrayOfString2[n]))
        {
          localArrayList.add(str);
          break;
        }
    }
    if (localArrayList.size() == 0)
    {
      localHTMLWriter.openTag("tr").endTag(false);
      localHTMLWriter.openTag("td").append(" valign='top' align='center' colspan='5' class='Status BorderTopLeft'").endTag(false);
      localHTMLWriter.append("Empty Cache").closeTag("td").closeTag("tr");
      return localHTMLWriter.toString();
    }
    localObject = "BaseTableCell";
    int m = 0;
    localObject = convertHTMLStatsStyle((String)localObject);
    int n = localArrayList.size();
    for (int i1 = 0; i1 < n; i1++)
    {
      Cache localCache2 = instance.getGoatCache((String)localArrayList.get(i1));
      localHTMLWriter.openTag("tr").endTag(false);
      m = convertRowType(i1, n);
      localCache2.doDumpHTMLStats(localHTMLWriter, (String)localObject, m);
      localHTMLWriter.closeTag("tr");
    }
    return localHTMLWriter.toString();
  }

  private static int convertRowType(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (paramInt1 == 0)
      i = 0;
    else if (paramInt1 == paramInt2 - 1)
      i = 2;
    return i;
  }

  private static String convertHTMLStatsStyle(String paramString)
  {
    if (paramString.equals("BaseTableCell"))
      paramString = "BaseTableCellOdd";
    else
      paramString = "BaseTableCell";
    return paramString;
  }

  public static String printPreloadStatus(String paramString)
  {
    String str = "-";
    Map localMap = PrefetchTask.getPreloadStatus();
    int i = localMap.get(paramString) == null ? -1 : ((Integer)localMap.get(paramString)).intValue();
    if (i == PrefetchTask.PRELOAD_DISABLED)
    {
      str = "Disabled";
    }
    else if (i == PrefetchTask.PRELOAD_RUNNING)
    {
      HTMLWriter localHTMLWriter = new HTMLWriter();
      Integer localInteger1 = (Integer)PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.get(paramString);
      if (localInteger1 == null)
        localInteger1 = new Integer(0);
      Integer localInteger2 = (Integer)PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.get(paramString);
      if (localInteger2 == null)
        localInteger2 = new Integer(0);
      int j = 0;
      if ((localInteger1.intValue() == 0) || (localInteger2.intValue() == 0))
        j = 0;
      else
        j = localInteger1.intValue() * 100 / localInteger2.intValue();
      localHTMLWriter.openTag("div").attr("class", "dd").endTag(false);
      localHTMLWriter.openTag("div").attr("id", "progDIv").attr("class", "blue").attr("style", "width:" + j + "%;").endTag(false);
      localHTMLWriter.openTag("span").attr("class", "pre").endTag(false);
      localHTMLWriter.append(j + "%");
      localHTMLWriter.closeTag("span");
      localHTMLWriter.closeTag("div");
      localHTMLWriter.closeTag("div");
      str = localHTMLWriter.toString();
    }
    else if (i == PrefetchTask.PRELOAD_NOT_RUNNING)
    {
      str = "Not Running";
    }
    else if (i == PrefetchTask.PRELOAD_COMPLETED)
    {
      str = "Completed";
    }
    return str;
  }

  private final class SaveThread extends Thread
  {
    public SaveThread()
    {
      super();
      setDaemon(true);
      setPriority(2);
    }

    public final void run()
    {
      while (true)
      {
        try
        {
          synchronized (GoatCacheManager.SaveObj)
          {
            GoatCacheManager.SaveObj.wait();
          }
        }
        catch (InterruptedException localInterruptedException)
        {
        }
        GoatCacheManager.access$102(false);
        GoatCacheManager.doSave();
        GoatCacheManager.access$102(true);
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.GoatCacheManager
 * JD-Core Version:    0.6.1
 */