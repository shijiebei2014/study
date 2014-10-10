package com.remedy.arsys.share;

import com.bmc.arsys.api.ARException;
import com.remedy.arsys.backchannel.TableEntryListBase.SchemaList;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatContainer;
import com.remedy.arsys.goat.GoatContainer.ContainerList;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatImage;
import com.remedy.arsys.goat.Guide;
import com.remedy.arsys.goat.cache.sync.relationshipManager;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.goat.permissions.Role;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.goat.sharedresource.ARSharedResource;
import com.remedy.arsys.goat.skins.Skin;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.prefetch.CacheFetchManager;
import com.remedy.arsys.prefetch.PrefetchManager;
import com.remedy.arsys.prefetch.PrefetchTask;
import com.remedy.arsys.prefetch.PrefetchWorker.Item;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

public class Cache
{
  private static Log mLog = Log.get(1);
  private static Log mPerformanceLog = Log.get(8);
  public static final int SENSITIVE_NONE = 0;
  public static final int SENSITIVE_DEFINITIONS = 1;
  public static final int SENSITIVE_PERMISSIONS = 2;
  public static final int SENSITIVE_DATA = 4;
  public static final int ISFIRSTROW = 0;
  public static final int ISMIDDLEROW = 1;
  public static final int ISLASTROW = 2;
  public static final int ISLEFTCOL = -1;
  public static final int ISMIDDLECOL = -2;
  public static final int ISRIGHTCOL = -3;
  public static final int DEFAULT_INITIAL_CAPACITY = 5000;
  protected static Log cacheLog = Log.get(1);
  public static ThreadLocal updateFormInMemoryTL = new ThreadLocal()
  {
    protected synchronized Object initialValue()
    {
      return null;
    }
  };
  protected static final Hashtable<String, Cache> MCaches = new Hashtable();
  protected String mName;
  protected int mSensitivity;
  protected net.sf.ehcache.Cache ehCache;
  private boolean mDisabled = false;
  protected String mLastReapTime;
  private static final Map<String, ServerReaper> MServerUpdateMap = new HashMap();

  public void resetEhcache(net.sf.ehcache.Cache paramCache)
  {
    this.ehCache = paramCache;
  }

  public net.sf.ehcache.Cache getMemCache()
  {
    return this.ehCache;
  }

  protected Cache()
  {
  }

  public static GoatCacheManager getCacheManager()
  {
    return GoatCacheManager.getInstance();
  }

  protected net.sf.ehcache.Cache createMemoryCache(String paramString)
  {
    Configuration localConfiguration = Configuration.getInstance();
    return createMemoryCache(paramString, true);
  }

  public static final int getMaxElementsInMemory(String paramString)
  {
    Configuration localConfiguration = Configuration.getInstance();
    int i = localConfiguration.getCacheReferenceMaxElementsInMemory();
    if (i < 0)
    {
      i = 5000;
    }
    else
    {
      double d = localConfiguration.getCacheReferenceMaxElementsInMemoryWeight(paramString);
      if (d < 0.0D)
        i = 5000;
      else
        i = (int)Math.round(i * d);
    }
    return i;
  }

  protected net.sf.ehcache.Cache createMemoryCache(String paramString, boolean paramBoolean)
  {
    assert (paramString != null);
    Configuration localConfiguration = Configuration.getInstance();
    boolean bool = paramBoolean ? localConfiguration.getOverflowToDiskTemp() : false;
    int i = getMaxElementsInMemory(paramString);
    net.sf.ehcache.Cache localCache = new net.sf.ehcache.Cache(paramString, i, localConfiguration.getMemoryStoreEvictionPolicy(), bool, null, true, 0L, 0L, false, localConfiguration.getDiskExpiryThreadIntervalSeconds(), null, null, localConfiguration.getMaxElementsOnDisk());
    return localCache;
  }

  protected net.sf.ehcache.Cache createCache(String paramString, int paramInt, boolean paramBoolean)
  {
    assert (paramString != null);
    net.sf.ehcache.Cache localCache = createMemoryCache(paramString, paramBoolean);
    cacheLog.log(Level.INFO, "CACHE:Cache is created with name= " + paramString);
    this.mName = paramString;
    this.mSensitivity = paramInt;
    return localCache;
  }

  protected net.sf.ehcache.Cache createCache(String paramString, int paramInt)
  {
    assert (paramString != null);
    net.sf.ehcache.Cache localCache = createMemoryCache(paramString);
    cacheLog.log(Level.INFO, "CACHE:Cache is created with name= " + paramString);
    this.mName = paramString;
    this.mSensitivity = paramInt;
    return localCache;
  }

  protected static net.sf.ehcache.Cache createDiskCache(String paramString)
  {
    assert (paramString != null);
    Configuration localConfiguration = Configuration.getInstance();
    net.sf.ehcache.Cache localCache = new net.sf.ehcache.Cache(paramString, localConfiguration.getDiskCacheMaxElementsInMemory(), localConfiguration.getMemoryStoreEvictionPolicy(), true, null, true, 0L, 0L, true, localConfiguration.getDiskExpiryThreadIntervalSeconds(), null, null, localConfiguration.getMaxElementsOnDisk());
    return localCache;
  }

  public Cache(String paramString, int paramInt)
  {
    this.ehCache = createCache(paramString, paramInt);
    synchronized (MCaches)
    {
      MCaches.put(paramString, this);
    }
  }

  public String getName()
  {
    return this.mName;
  }

  protected static Hashtable<String, Cache> getMCache()
  {
    return MCaches;
  }

  public void put(String paramString, Item paramItem)
  {
    GoatCacheManager.setForceSaveFlag();
    if (this.mDisabled)
      return;
    this.ehCache.put(new Element(paramString, paramItem));
  }

  public Item get(String paramString, Class paramClass)
  {
    try
    {
      return getHelper(paramString);
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public Item get(String paramString)
  {
    try
    {
      return getHelper(paramString);
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  private final Item getHelper(String paramString)
  {
    Element localElement = this.ehCache.get(paramString);
    Item localItem = null;
    if (localElement != null)
      localItem = (Item)localElement.getObjectValue();
    return localItem;
  }

  public Set<Item> getAll(String paramString, Class paramClass)
  {
    List localList = this.ehCache.getKeys();
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      String str = (String)localObject;
      Item localItem = getHelper(str);
      if ((paramString.equals(localItem.getServer())) && ((paramClass == null) || (paramClass.isAssignableFrom(localItem.getClass()))))
        localLinkedHashSet.add(localItem);
    }
    return localLinkedHashSet;
  }

  public List<String> getAllKeys(String paramString, Class paramClass)
  {
    List localList = this.ehCache.getKeys();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      String str = (String)localObject;
      Item localItem = getHelper(str);
      if ((paramString == null) || (paramString.equals(localItem.getServer())))
        localArrayList.add(str);
    }
    return localArrayList;
  }

  public int getKeysInMemorySize()
  {
    List localList = this.ehCache.getKeys();
    if (localList == null)
      return 0;
    return localList.size();
  }

  public void remove(String paramString)
  {
    this.ehCache.remove(paramString);
  }

  public void disable()
  {
    this.mDisabled = true;
  }

  public void enable()
  {
    this.mDisabled = false;
  }

  public boolean containsKey(String paramString)
  {
    return this.ehCache.isKeyInCache(paramString);
  }

  protected String typeToClassType(int paramInt1, int paramInt2)
  {
    String str = "";
    switch (paramInt1)
    {
    case 0:
      switch (paramInt2)
      {
      case -1:
        str = "BaseTableCellLeftTop";
        break;
      case -2:
        str = "BaseTableCellCenterTop";
        break;
      case -3:
        str = "BaseTableCellRightTop";
        break;
      default:
        str = "BaseTableCellCenterTop";
      }
      break;
    case 1:
      switch (paramInt2)
      {
      case -1:
        str = "BaseTableCellLeft";
        break;
      case -2:
        str = "BaseTableCellCenter";
        break;
      case -3:
        str = "BaseTableCellRight";
        break;
      default:
        str = "BaseTableCellCenter";
      }
      break;
    case 2:
      switch (paramInt2)
      {
      case -1:
        str = "BaseTableCellLeftBottom";
        break;
      case -2:
        str = "BaseTableCellCenterBottom";
        break;
      case -3:
        str = "BaseTableCellRightBottom";
      default:
        str = "BaseTableCellCenterBottom";
      }
      break;
    default:
      str = "BaseTableCellCenter";
    }
    return str;
  }

  protected void doDumpHTMLStats(HTMLWriter paramHTMLWriter, String paramString, int paramInt)
  {
    String str = paramString + " " + typeToClassType(paramInt, -1);
    paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).cdata(this.mName).closeTag("td", false);
    str = paramString + " " + typeToClassType(paramInt, -2);
    paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).append("" + this.ehCache.getSize()).closeTag("td", false);
    if (Configuration.getInstance().getCacheEnableStatistics())
    {
      paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).append("" + this.ehCache.getStatistics().getCacheHits()).closeTag("td", false);
      paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).append("" + this.ehCache.getStatistics().getCacheMisses()).closeTag("td", false);
    }
    str = paramString + " " + typeToClassType(paramInt, -3);
    paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).cdata(this.mLastReapTime == null ? "-" : this.mLastReapTime).closeTag("td", false);
  }

  protected void doReap(String paramString, int paramInt)
  {
    if ((this.mSensitivity & paramInt) == 0)
      return;
    String str1 = paramString == null ? "All Servers" : paramString;
    this.mLastReapTime = (new Date().toString() + " (server: " + str1 + ")");
    String str2 = null;
    if ((paramInt & 0x1) > 0)
      str2 = "definition";
    if ((paramInt & 0x2) > 0)
      if (str2 == null)
        str2 = "permissions";
      else
        str2 = str2 + ", permissions ";
    if ((paramInt & 0x4) > 0)
      if (str2 == null)
        str2 = "data";
      else
        str2 = str2 + ", data ";
    if (str2 != null)
    {
      mLog.warning("Reaping cache " + this.mName + " for " + str2 + " change on server " + str1);
      mPerformanceLog.warning("Reaping cache " + this.mName + " for " + str2 + " change on server " + str1);
    }
    this.ehCache.removeAll();
  }

  public static final void flush(boolean paramBoolean, String paramString)
  {
    if (paramBoolean)
      initServerReapers();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    synchronized (MServerUpdateMap)
    {
      localObject1 = MServerUpdateMap.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        localObject3 = (ServerReaper)((Map.Entry)localObject2).getValue();
        if (paramString != null)
        {
          if (paramString.equals(((ServerReaper)localObject3).getServer()))
          {
            ((ServerReaper)localObject3).wakeUp();
            break;
          }
        }
        else if (paramBoolean)
        {
          ((ServerReaper)localObject3).wakeUp();
        }
        else
        {
          ((ServerReaper)localObject3).stop();
          ((Iterator)localObject1).remove();
        }
      }
    }
    if (!paramBoolean)
      try
      {
        synchronized (MCaches)
        {
          localObject1 = new HashMap();
          localObject2 = MCaches.elements();
          while (((Enumeration)localObject2).hasMoreElements())
          {
            localObject3 = (Cache)((Enumeration)localObject2).nextElement();
            ((HashMap)localObject1).put(((Cache)localObject3).getName(), ((Cache)localObject3).getName());
            cacheLog.log(Level.INFO, "CACHE:doReap cache: name= " + ((Cache)localObject3).getName());
            ((Cache)localObject3).doReap(null, 7);
          }
          localObject3 = GoatCacheConstants.getMap();
          Object[] arrayOfObject = ((HashMap)localObject3).keySet().toArray();
          for (int i = 0; i < arrayOfObject.length; i++)
          {
            String str = (String)arrayOfObject[i];
            int j = ((Integer)((HashMap)localObject3).get(str)).intValue();
            if (((HashMap)localObject1).get(str) == null)
            {
              new Cache(str, j).doReap(null, 7);
              try
              {
                GoatCacheManager.getInstance().removeCache(str);
              }
              catch (IllegalStateException localIllegalStateException)
              {
              }
              MCaches.remove(str);
              cacheLog.log(Level.INFO, "CACHE:flush uncleaned cache: name= " + str);
            }
          }
        }
      }
      finally
      {
        initServerReapers();
      }
  }

  public static final void stopReaping()
  {
    synchronized (MServerUpdateMap)
    {
      Iterator localIterator = MServerUpdateMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ServerReaper localServerReaper = (ServerReaper)localEntry.getValue();
        localServerReaper.stop();
        localIterator.remove();
      }
    }
  }

  private static final void addKnownServers(List paramList)
  {
    Iterator localIterator;
    Object localObject1;
    String str;
    boolean bool;
    if ((Configuration.getInstance().getCacheUpdateNeeded()) && (Configuration.getInstance().getCacheUpdateInterval() > 0))
      synchronized (MServerUpdateMap)
      {
        localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = localIterator.next();
          str = (String)localObject1;
          if ((str != null) && (str.length() > 0))
          {
            if (!PrefetchTask.MAP_SERVER_PRELOAD_STATE.containsKey(str))
            {
              bool = Configuration.getInstance().getServerPreloadFlag(str);
              if (bool)
                PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(str, Integer.valueOf(PrefetchTask.PRELOAD_NOT_RUNNING));
              else
                PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(str, Integer.valueOf(PrefetchTask.PRELOAD_DISABLED));
              PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.put(str, Integer.valueOf(0));
              PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.put(str, Integer.valueOf(0));
            }
            if (!MServerUpdateMap.containsKey(str))
            {
              new ServerReaper(str, null);
              cacheLog.info("Created server reaper: " + str);
            }
          }
        }
      }
    else
      synchronized (MServerUpdateMap)
      {
        localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = localIterator.next();
          str = (String)localObject1;
          if ((str != null) && (str.length() > 0) && (!PrefetchTask.MAP_SERVER_PRELOAD_STATE.containsKey(str)))
          {
            bool = Configuration.getInstance().getServerPreloadFlag(str);
            if (bool)
              PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(str, Integer.valueOf(PrefetchTask.PRELOAD_NOT_RUNNING));
            else
              PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(str, Integer.valueOf(PrefetchTask.PRELOAD_DISABLED));
            PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.put(str, Integer.valueOf(0));
            PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.put(str, Integer.valueOf(0));
          }
        }
      }
  }

  public static final void initServerReapers()
  {
    initCacheClasses();
    Configuration localConfiguration = Configuration.getInstance();
    List localList = localConfiguration.getServers();
    addKnownServers(localList);
  }

  public static final void removeServerReaper(String paramString)
  {
    PrefetchTask.MAP_SERVER_PRELOAD_STATE.remove(paramString);
    PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.remove(paramString);
    PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.remove(paramString);
    synchronized (MServerUpdateMap)
    {
      ServerReaper localServerReaper = (ServerReaper)MServerUpdateMap.remove(paramString);
      if (localServerReaper != null)
        localServerReaper.stop();
      cacheLog.info("Removed server reaper: " + paramString);
    }
  }

  public static final void initCacheClasses()
  {
    try
    {
      Class.forName(Form.class.getName());
      Class.forName(ActiveLink.class.getName());
      Class.forName(FieldGraph.class.getName());
      Class.forName(GoatField.class.getName());
      Class.forName(GoatImage.class.getName());
      Class.forName(MessageTranslation.class.getName());
      Class.forName(Group.class.getName());
      Class.forName(Role.class.getName());
      Class.forName(FieldMapCache.class.getName());
      Class.forName(relationshipManager.class.getName());
      Class.forName(MiscCache.class.getName());
      Class.forName(GoatContainer.class.getName());
      Class.forName(Guide.class.getName());
      Class.forName(Menu.class.getName());
      Class.forName(ARUserPreferences.class.getName());
      Class.forName(ARUserSearches.class.getName());
      Class.forName(GoatContainer.ContainerList.class.getName());
      Class.forName(ARSharedResource.class.getName());
      Class.forName(TableEntryListBase.SchemaList.class.getName());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localClassNotFoundException.printStackTrace();
    }
  }

  public static final boolean fullFlushCache(String paramString)
  {
    try
    {
      ServerInfo localServerInfo = ServerInfo.get(paramString, true);
      return localServerInfo.getVersionAsNumber() < 70500;
    }
    catch (GoatException localGoatException)
    {
      cacheLog.log(Level.WARNING, "Failed to determine version for server " + paramString, localGoatException);
    }
    return true;
  }

  public void save()
  {
  }

  public void saveOnSizeChange()
  {
  }

  public Ehcache getDiskCache()
  {
    return null;
  }

  public static void BaseDataCacheVerification(String paramString, boolean paramBoolean)
  {
    int i = 0;
    synchronized (MCaches)
    {
      Enumeration localEnumeration = MCaches.elements();
      while (localEnumeration.hasMoreElements())
      {
        Cache localCache1 = (Cache)localEnumeration.nextElement();
        if ((localCache1 instanceof BaseDataCache))
          try
          {
            mPerformanceLog.fine("Validating the data for form " + ((BaseDataCache)localCache1).getFormName() + " located on server " + ((BaseDataCache)localCache1).getServer());
            ((BaseDataCache)localCache1).checkCache();
          }
          catch (ARException localARException)
          {
            mPerformanceLog.severe("Problem updating the data for form " + ((BaseDataCache)localCache1).getFormName() + " located on server " + ((BaseDataCache)localCache1).getServer());
          }
        else if (localCache1.getName().equals("SharedResourceList"))
        {
          if (ARSharedResource.checkTemplateCache(paramString))
            i = 1;
        }
        else if (localCache1.getName().equals("Skin Definitions"))
          Skin.checkSkinCache(paramString);
      }
      if ((paramBoolean) && (i != 0))
      {
        localEnumeration = MCaches.elements();
        while (localEnumeration.hasMoreElements())
        {
          int j = 3;
          Cache localCache2 = (Cache)localEnumeration.nextElement();
          localCache2.doReap(paramString, j);
        }
      }
    }
  }

  private static void refreshCache(String paramString, Map<String, Set<String>> paramMap)
  {
    cacheLog.info("Refreshing cache for server: " + paramString);
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    Set localSet1 = paramMap.entrySet();
    Object localObject = localSet1.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      String str1 = (String)localEntry.getKey();
      Set localSet2 = (Set)localEntry.getValue();
      Iterator localIterator = localSet2.iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        cacheLog.fine("Refreshing cache for server: " + paramString + ", form: " + str1 + ", view: " + str2);
        PrefetchWorker.Item localItem = PrefetchManager.createItem(paramString, str1, null, null, null, str2);
        localLinkedHashSet.add(localItem);
      }
    }
    if (!localLinkedHashSet.isEmpty())
    {
      localObject = new CacheFetchManager(localLinkedHashSet);
      ((CacheFetchManager)localObject).start(false);
    }
    cacheLog.info("Done refreshing cache for server: " + paramString);
  }

  public static class SetItem<T>
    implements Cache.Item
  {
    private static final long serialVersionUID = 4367808140172414679L;
    protected String server;
    protected Set<T> set;

    public SetItem(String paramString)
    {
      this.server = paramString;
      this.set = new LinkedHashSet();
    }

    public String getServer()
    {
      return this.server;
    }

    public int getSize()
    {
      return 0;
    }

    public void addSetItem(T paramT)
    {
      this.set.add(paramT);
    }

    public Set<T> getSet()
    {
      return this.set;
    }
  }

  public static class LongArrayItem
    implements Cache.Item
  {
    private static final long serialVersionUID = 7702534043671538360L;
    protected String server;
    protected long[] longArrayValue;

    public LongArrayItem(String paramString, long[] paramArrayOfLong)
    {
      this.server = paramString;
      this.longArrayValue = paramArrayOfLong;
    }

    public String getServer()
    {
      return this.server;
    }

    public int getSize()
    {
      return 8;
    }

    public long[] getLongArrayValue()
    {
      return this.longArrayValue;
    }
  }

  private static class ServerReaper
  {
    private final Object mSleepLock = new Object();
    private boolean mShouldSleep = true;
    private boolean mFinish;
    private String mServerName;
    private long[] mLastTimes;
    private boolean firstTime = true;

    private ServerReaper(String paramString)
    {
      assert (!Cache.MServerUpdateMap.containsKey(paramString));
      this.mServerName = paramString;
      this.mFinish = false;
      Thread local1 = new Thread()
      {
        // ERROR //
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore_1
          //   2: aload_1
          //   3: ifnonnull +89 -> 92
          //   6: new 10	com/remedy/arsys/share/Cache$ServerReaperSessionData
          //   9: dup
          //   10: aload_0
          //   11: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   14: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   17: invokespecial 11	com/remedy/arsys/share/Cache$ServerReaperSessionData:<init>	(Ljava/lang/String;)V
          //   20: astore_1
          //   21: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   24: new 3	java/lang/StringBuilder
          //   27: dup
          //   28: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   31: ldc 13
          //   33: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   36: aload_0
          //   37: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   40: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   43: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   46: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   49: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   52: aload_1
          //   53: invokestatic 15	com/remedy/arsys/stubs/SessionData:set	(Lcom/remedy/arsys/stubs/SessionData;)V
          //   56: new 16	com/remedy/arsys/goat/FormContext
          //   59: dup
          //   60: aconst_null
          //   61: ldc 17
          //   63: aconst_null
          //   64: invokestatic 18	com/remedy/arsys/config/Configuration:getInstance	()Lcom/remedy/arsys/config/Configuration;
          //   67: invokevirtual 19	com/remedy/arsys/config/Configuration:getRootPath	()Ljava/lang/String;
          //   70: invokespecial 20	com/remedy/arsys/goat/FormContext:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
          //   73: pop
          //   74: goto +18 -> 92
          //   77: astore_2
          //   78: aload_2
          //   79: invokevirtual 22	com/remedy/arsys/goat/GoatException:printStackTrace	()V
          //   82: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   85: aload_2
          //   86: invokevirtual 23	com/remedy/arsys/goat/GoatException:getMessage	()Ljava/lang/String;
          //   89: invokevirtual 24	com/remedy/arsys/log/Log:fine	(Ljava/lang/String;)V
          //   92: iconst_0
          //   93: istore_2
          //   94: aload_0
          //   95: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   98: invokestatic 25	com/remedy/arsys/share/Cache$ServerReaper:access$600	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/Object;
          //   101: dup
          //   102: astore_3
          //   103: monitorenter
          //   104: aload_0
          //   105: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   108: invokestatic 26	com/remedy/arsys/share/Cache$ServerReaper:access$700	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Z
          //   111: istore_2
          //   112: aload_3
          //   113: monitorexit
          //   114: goto +10 -> 124
          //   117: astore 4
          //   119: aload_3
          //   120: monitorexit
          //   121: aload 4
          //   123: athrow
          //   124: iload_2
          //   125: ifeq +37 -> 162
          //   128: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   131: new 3	java/lang/StringBuilder
          //   134: dup
          //   135: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   138: ldc 27
          //   140: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   143: aload_0
          //   144: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   147: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   150: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   153: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   156: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   159: goto +1623 -> 1782
          //   162: new 28	java/util/Date
          //   165: dup
          //   166: invokespecial 29	java/util/Date:<init>	()V
          //   169: invokevirtual 30	java/util/Date:getTime	()J
          //   172: lstore_3
          //   173: sipush 3600
          //   176: istore 5
          //   178: invokestatic 18	com/remedy/arsys/config/Configuration:getInstance	()Lcom/remedy/arsys/config/Configuration;
          //   181: invokevirtual 31	com/remedy/arsys/config/Configuration:getCacheUpdateInterval	()I
          //   184: istore 5
          //   186: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   189: new 3	java/lang/StringBuilder
          //   192: dup
          //   193: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   196: ldc 32
          //   198: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   201: aload_0
          //   202: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   205: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   208: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   211: ldc 33
          //   213: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   216: iload 5
          //   218: invokevirtual 34	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   221: ldc 35
          //   223: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   226: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   229: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   232: aload_0
          //   233: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   236: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   239: iconst_1
          //   240: invokestatic 36	com/remedy/arsys/share/ServerInfo:get	(Ljava/lang/String;Z)Lcom/remedy/arsys/share/ServerInfo;
          //   243: astore 6
          //   245: aload 6
          //   247: invokevirtual 37	com/remedy/arsys/share/ServerInfo:getCacheChangeTimes	()[J
          //   250: astore 7
          //   252: aload_0
          //   253: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   256: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   259: ifnonnull +125 -> 384
          //   262: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   265: new 3	java/lang/StringBuilder
          //   268: dup
          //   269: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   272: ldc 39
          //   274: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   277: aload_0
          //   278: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   281: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   284: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   287: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   290: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   293: aload_0
          //   294: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   297: iconst_3
          //   298: newarray long
          //   300: invokestatic 40	com/remedy/arsys/share/Cache$ServerReaper:access$802	(Lcom/remedy/arsys/share/Cache$ServerReaper;[J)[J
          //   303: pop
          //   304: aload_0
          //   305: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   308: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   311: iconst_0
          //   312: aload 7
          //   314: iconst_0
          //   315: laload
          //   316: lastore
          //   317: aload_0
          //   318: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   321: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   324: iconst_1
          //   325: aload 7
          //   327: iconst_1
          //   328: laload
          //   329: lastore
          //   330: aload_0
          //   331: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   334: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   337: iconst_2
          //   338: aload 7
          //   340: iconst_2
          //   341: laload
          //   342: lastore
          //   343: aload_0
          //   344: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   347: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   350: invokestatic 41	com/remedy/arsys/stubs/ServerLogin:getAdmin	(Ljava/lang/String;)Lcom/remedy/arsys/stubs/ServerLogin;
          //   353: astore 8
          //   355: new 42	com/remedy/arsys/goat/cache/sync/ServerSync
          //   358: dup
          //   359: aload_0
          //   360: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   363: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   366: aload 8
          //   368: invokestatic 43	com/remedy/arsys/share/GoatCacheManager:getInstance	()Lcom/remedy/arsys/share/GoatCacheManager;
          //   371: invokespecial 44	com/remedy/arsys/goat/cache/sync/ServerSync:<init>	(Ljava/lang/String;Lcom/bmc/arsys/api/ARServerUser;Lcom/remedy/arsys/share/GoatCacheManager;)V
          //   374: astore 9
          //   376: aload 9
          //   378: invokevirtual 45	com/remedy/arsys/goat/cache/sync/ServerSync:serverStartupSync	()V
          //   381: goto +843 -> 1224
          //   384: invokestatic 18	com/remedy/arsys/config/Configuration:getInstance	()Lcom/remedy/arsys/config/Configuration;
          //   387: aload_0
          //   388: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   391: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   394: invokevirtual 46	com/remedy/arsys/config/Configuration:getServerPreloadFlag	(Ljava/lang/String;)Z
          //   397: ifeq +47 -> 444
          //   400: aload_0
          //   401: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   404: invokestatic 47	com/remedy/arsys/share/Cache$ServerReaper:access$900	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Z
          //   407: ifeq +37 -> 444
          //   410: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   413: new 3	java/lang/StringBuilder
          //   416: dup
          //   417: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   420: ldc 48
          //   422: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   425: aload_0
          //   426: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   429: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   432: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   435: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   438: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   441: goto +783 -> 1224
          //   444: aload_0
          //   445: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   448: aload_0
          //   449: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   452: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   455: invokestatic 49	com/remedy/arsys/share/Cache$ServerReaper:access$1000	(Lcom/remedy/arsys/share/Cache$ServerReaper;Ljava/lang/String;)Z
          //   458: ifeq +14 -> 472
          //   461: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   464: ldc 50
          //   466: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   469: goto +755 -> 1224
          //   472: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   475: new 3	java/lang/StringBuilder
          //   478: dup
          //   479: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   482: ldc 51
          //   484: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   487: aload_0
          //   488: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   491: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   494: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   497: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   500: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   503: aload_0
          //   504: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   507: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   510: invokestatic 52	com/remedy/arsys/share/Cache:fullFlushCache	(Ljava/lang/String;)Z
          //   513: istore 8
          //   515: aconst_null
          //   516: astore 9
          //   518: iconst_0
          //   519: istore 10
          //   521: aload 7
          //   523: iconst_0
          //   524: laload
          //   525: aload_0
          //   526: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   529: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   532: iconst_0
          //   533: laload
          //   534: lcmp
          //   535: ifgt +20 -> 555
          //   538: aload 7
          //   540: iconst_1
          //   541: laload
          //   542: aload_0
          //   543: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   546: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   549: iconst_1
          //   550: laload
          //   551: lcmp
          //   552: ifle +9 -> 561
          //   555: iload 10
          //   557: iconst_2
          //   558: ior
          //   559: istore 10
          //   561: aload 7
          //   563: iconst_2
          //   564: laload
          //   565: aload_0
          //   566: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   569: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   572: iconst_2
          //   573: laload
          //   574: lcmp
          //   575: ifle +9 -> 584
          //   578: iload 10
          //   580: iconst_1
          //   581: ior
          //   582: istore 10
          //   584: iload 5
          //   586: ifle +543 -> 1129
          //   589: iload 10
          //   591: ifle +538 -> 1129
          //   594: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   597: new 3	java/lang/StringBuilder
          //   600: dup
          //   601: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   604: aload_0
          //   605: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   608: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   611: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   614: ldc 53
          //   616: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   619: aload 7
          //   621: iconst_0
          //   622: laload
          //   623: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   626: ldc 55
          //   628: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   631: aload 7
          //   633: iconst_1
          //   634: laload
          //   635: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   638: ldc 55
          //   640: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   643: aload 7
          //   645: iconst_2
          //   646: laload
          //   647: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   650: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   653: invokevirtual 24	com/remedy/arsys/log/Log:fine	(Ljava/lang/String;)V
          //   656: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   659: new 3	java/lang/StringBuilder
          //   662: dup
          //   663: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   666: aload_0
          //   667: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   670: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   673: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   676: ldc 56
          //   678: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   681: aload_0
          //   682: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   685: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   688: iconst_0
          //   689: laload
          //   690: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   693: ldc 55
          //   695: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   698: aload_0
          //   699: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   702: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   705: iconst_1
          //   706: laload
          //   707: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   710: ldc 55
          //   712: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   715: aload_0
          //   716: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   719: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   722: iconst_2
          //   723: laload
          //   724: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   727: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   730: invokevirtual 24	com/remedy/arsys/log/Log:fine	(Ljava/lang/String;)V
          //   733: aload 7
          //   735: iconst_2
          //   736: laload
          //   737: aload_0
          //   738: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   741: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   744: iconst_2
          //   745: laload
          //   746: lcmp
          //   747: ifle +123 -> 870
          //   750: invokestatic 57	com/remedy/arsys/share/Cache:access$1100	()Lcom/remedy/arsys/log/Log;
          //   753: new 3	java/lang/StringBuilder
          //   756: dup
          //   757: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   760: ldc 58
          //   762: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   765: aload_0
          //   766: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   769: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   772: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   775: ldc 59
          //   777: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   780: aload_0
          //   781: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   784: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   787: iconst_2
          //   788: laload
          //   789: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   792: ldc 60
          //   794: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   797: aload 7
          //   799: iconst_2
          //   800: laload
          //   801: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   804: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   807: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   810: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   813: new 3	java/lang/StringBuilder
          //   816: dup
          //   817: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   820: ldc 58
          //   822: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   825: aload_0
          //   826: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   829: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   832: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   835: ldc 59
          //   837: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   840: aload_0
          //   841: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   844: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   847: iconst_2
          //   848: laload
          //   849: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   852: ldc 60
          //   854: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   857: aload 7
          //   859: iconst_2
          //   860: laload
          //   861: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   864: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   867: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   870: iload 8
          //   872: ifeq +110 -> 982
          //   875: getstatic 61	com/remedy/arsys/share/Cache:cacheLog	Lcom/remedy/arsys/log/Log;
          //   878: new 3	java/lang/StringBuilder
          //   881: dup
          //   882: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   885: ldc 62
          //   887: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   890: aload_0
          //   891: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   894: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   897: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   900: ldc 63
          //   902: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   905: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   908: invokevirtual 64	com/remedy/arsys/log/Log:info	(Ljava/lang/String;)V
          //   911: getstatic 65	com/remedy/arsys/share/Cache:MCaches	Ljava/util/Hashtable;
          //   914: dup
          //   915: astore 11
          //   917: monitorenter
          //   918: getstatic 65	com/remedy/arsys/share/Cache:MCaches	Ljava/util/Hashtable;
          //   921: invokevirtual 66	java/util/Hashtable:elements	()Ljava/util/Enumeration;
          //   924: astore 12
          //   926: aload 12
          //   928: invokeinterface 67 1 0
          //   933: ifeq +32 -> 965
          //   936: aload 12
          //   938: invokeinterface 68 1 0
          //   943: checkcast 69	com/remedy/arsys/share/Cache
          //   946: astore 13
          //   948: aload 13
          //   950: aload_0
          //   951: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   954: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   957: iload 10
          //   959: invokevirtual 70	com/remedy/arsys/share/Cache:doReap	(Ljava/lang/String;I)V
          //   962: goto -36 -> 926
          //   965: aload 11
          //   967: monitorexit
          //   968: goto +11 -> 979
          //   971: astore 14
          //   973: aload 11
          //   975: monitorexit
          //   976: aload 14
          //   978: athrow
          //   979: goto +108 -> 1087
          //   982: getstatic 61	com/remedy/arsys/share/Cache:cacheLog	Lcom/remedy/arsys/log/Log;
          //   985: new 3	java/lang/StringBuilder
          //   988: dup
          //   989: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   992: ldc 71
          //   994: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   997: aload_0
          //   998: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1001: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1004: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1007: ldc 72
          //   1009: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1012: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1015: invokevirtual 64	com/remedy/arsys/log/Log:info	(Ljava/lang/String;)V
          //   1018: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1021: new 3	java/lang/StringBuilder
          //   1024: dup
          //   1025: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1028: ldc 73
          //   1030: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1033: aload_0
          //   1034: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1037: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1040: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1043: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1046: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   1049: aload_0
          //   1050: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1053: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1056: invokestatic 41	com/remedy/arsys/stubs/ServerLogin:getAdmin	(Ljava/lang/String;)Lcom/remedy/arsys/stubs/ServerLogin;
          //   1059: astore 11
          //   1061: new 42	com/remedy/arsys/goat/cache/sync/ServerSync
          //   1064: dup
          //   1065: aload_0
          //   1066: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1069: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1072: aload 11
          //   1074: invokestatic 43	com/remedy/arsys/share/GoatCacheManager:getInstance	()Lcom/remedy/arsys/share/GoatCacheManager;
          //   1077: invokespecial 44	com/remedy/arsys/goat/cache/sync/ServerSync:<init>	(Ljava/lang/String;Lcom/bmc/arsys/api/ARServerUser;Lcom/remedy/arsys/share/GoatCacheManager;)V
          //   1080: astore 9
          //   1082: aload 9
          //   1084: invokevirtual 74	com/remedy/arsys/goat/cache/sync/ServerSync:productionSync	()V
          //   1087: aload_0
          //   1088: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1091: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   1094: iconst_0
          //   1095: aload 7
          //   1097: iconst_0
          //   1098: laload
          //   1099: lastore
          //   1100: aload_0
          //   1101: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1104: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   1107: iconst_1
          //   1108: aload 7
          //   1110: iconst_1
          //   1111: laload
          //   1112: lastore
          //   1113: aload_0
          //   1114: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1117: invokestatic 38	com/remedy/arsys/share/Cache$ServerReaper:access$800	(Lcom/remedy/arsys/share/Cache$ServerReaper;)[J
          //   1120: iconst_2
          //   1121: aload 7
          //   1123: iconst_2
          //   1124: laload
          //   1125: lastore
          //   1126: goto +19 -> 1145
          //   1129: iload 8
          //   1131: ifeq +14 -> 1145
          //   1134: aload_0
          //   1135: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1138: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1141: iconst_1
          //   1142: invokestatic 75	com/remedy/arsys/share/Cache:BaseDataCacheVerification	(Ljava/lang/String;Z)V
          //   1145: iload 8
          //   1147: ifne +77 -> 1224
          //   1150: iload 5
          //   1152: ifle +72 -> 1224
          //   1155: aload_0
          //   1156: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1159: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1162: iconst_0
          //   1163: invokestatic 75	com/remedy/arsys/share/Cache:BaseDataCacheVerification	(Ljava/lang/String;Z)V
          //   1166: aload 9
          //   1168: ifnull +25 -> 1193
          //   1171: aload 9
          //   1173: invokevirtual 76	com/remedy/arsys/goat/cache/sync/ServerSync:getFormViewsToRemove	()Ljava/util/Map;
          //   1176: astore 11
          //   1178: aload_0
          //   1179: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1182: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1185: aload 11
          //   1187: invokestatic 77	com/remedy/arsys/share/Cache:access$1200	(Ljava/lang/String;Ljava/util/Map;)V
          //   1190: goto +34 -> 1224
          //   1193: getstatic 61	com/remedy/arsys/share/Cache:cacheLog	Lcom/remedy/arsys/log/Log;
          //   1196: new 3	java/lang/StringBuilder
          //   1199: dup
          //   1200: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1203: ldc 78
          //   1205: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1208: aload_0
          //   1209: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1212: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1215: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1218: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1221: invokevirtual 64	com/remedy/arsys/log/Log:info	(Ljava/lang/String;)V
          //   1224: jsr +200 -> 1424
          //   1227: goto +213 -> 1440
          //   1230: astore 6
          //   1232: aload 6
          //   1234: invokevirtual 22	com/remedy/arsys/goat/GoatException:printStackTrace	()V
          //   1237: invokestatic 57	com/remedy/arsys/share/Cache:access$1100	()Lcom/remedy/arsys/log/Log;
          //   1240: getstatic 79	java/util/logging/Level:WARNING	Ljava/util/logging/Level;
          //   1243: new 3	java/lang/StringBuilder
          //   1246: dup
          //   1247: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1250: ldc 80
          //   1252: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1255: aload_0
          //   1256: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1259: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1262: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1265: ldc 81
          //   1267: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1270: aload 6
          //   1272: invokevirtual 23	com/remedy/arsys/goat/GoatException:getMessage	()Ljava/lang/String;
          //   1275: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1278: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1281: aload 6
          //   1283: invokevirtual 82	com/remedy/arsys/log/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   1286: jsr +138 -> 1424
          //   1289: goto +151 -> 1440
          //   1292: astore 6
          //   1294: aload 6
          //   1296: invokevirtual 84	com/remedy/arsys/goat/cache/sync/SyncException:printStackTrace	()V
          //   1299: invokestatic 57	com/remedy/arsys/share/Cache:access$1100	()Lcom/remedy/arsys/log/Log;
          //   1302: getstatic 79	java/util/logging/Level:WARNING	Ljava/util/logging/Level;
          //   1305: new 3	java/lang/StringBuilder
          //   1308: dup
          //   1309: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1312: ldc 85
          //   1314: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1317: aload_0
          //   1318: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1321: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1324: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1327: ldc 81
          //   1329: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1332: aload 6
          //   1334: invokevirtual 86	com/remedy/arsys/goat/cache/sync/SyncException:getMessage	()Ljava/lang/String;
          //   1337: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1340: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1343: aload 6
          //   1345: invokevirtual 82	com/remedy/arsys/log/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   1348: jsr +76 -> 1424
          //   1351: goto +89 -> 1440
          //   1354: astore 6
          //   1356: aload 6
          //   1358: invokevirtual 88	java/lang/Exception:printStackTrace	()V
          //   1361: invokestatic 57	com/remedy/arsys/share/Cache:access$1100	()Lcom/remedy/arsys/log/Log;
          //   1364: getstatic 79	java/util/logging/Level:WARNING	Ljava/util/logging/Level;
          //   1367: new 3	java/lang/StringBuilder
          //   1370: dup
          //   1371: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1374: ldc 89
          //   1376: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1379: aload_0
          //   1380: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1383: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1386: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1389: ldc 81
          //   1391: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1394: aload 6
          //   1396: invokevirtual 90	java/lang/Exception:getMessage	()Ljava/lang/String;
          //   1399: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1402: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1405: aload 6
          //   1407: invokevirtual 82	com/remedy/arsys/log/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   1410: jsr +14 -> 1424
          //   1413: goto +369 -> 1782
          //   1416: astore 15
          //   1418: jsr +6 -> 1424
          //   1421: aload 15
          //   1423: athrow
          //   1424: astore 16
          //   1426: aload_0
          //   1427: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1430: iconst_0
          //   1431: invokestatic 91	com/remedy/arsys/share/Cache$ServerReaper:access$902	(Lcom/remedy/arsys/share/Cache$ServerReaper;Z)Z
          //   1434: pop
          //   1435: invokestatic 92	com/remedy/arsys/stubs/ServerLogin:threadDepartingGoatSpace	()V
          //   1438: ret 16
          //   1440: new 28	java/util/Date
          //   1443: dup
          //   1444: invokespecial 29	java/util/Date:<init>	()V
          //   1447: invokevirtual 30	java/util/Date:getTime	()J
          //   1450: lload_3
          //   1451: lsub
          //   1452: lstore 6
          //   1454: lload 6
          //   1456: lconst_0
          //   1457: lcmp
          //   1458: ifge +6 -> 1464
          //   1461: lconst_0
          //   1462: lstore 6
          //   1464: ldc2_w 93
          //   1467: lstore 8
          //   1469: iload 5
          //   1471: ifle +15 -> 1486
          //   1474: iload 5
          //   1476: sipush 1000
          //   1479: imul
          //   1480: i2l
          //   1481: lload 6
          //   1483: lsub
          //   1484: lstore 8
          //   1486: lload 8
          //   1488: ldc2_w 95
          //   1491: lcmp
          //   1492: ifge +8 -> 1500
          //   1495: ldc2_w 95
          //   1498: lstore 8
          //   1500: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1503: new 3	java/lang/StringBuilder
          //   1506: dup
          //   1507: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1510: ldc 97
          //   1512: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1515: aload_0
          //   1516: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1519: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1522: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1525: ldc 33
          //   1527: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1530: lload 8
          //   1532: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   1535: ldc 98
          //   1537: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1540: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1543: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   1546: aload_0
          //   1547: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1550: invokestatic 25	com/remedy/arsys/share/Cache$ServerReaper:access$600	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/Object;
          //   1553: dup
          //   1554: astore 10
          //   1556: monitorenter
          //   1557: aload_0
          //   1558: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1561: invokestatic 26	com/remedy/arsys/share/Cache$ServerReaper:access$700	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Z
          //   1564: ifeq +9 -> 1573
          //   1567: aload 10
          //   1569: monitorexit
          //   1570: goto +212 -> 1782
          //   1573: aload_0
          //   1574: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1577: invokestatic 99	com/remedy/arsys/share/Cache$ServerReaper:access$1300	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Z
          //   1580: ifeq +64 -> 1644
          //   1583: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1586: new 3	java/lang/StringBuilder
          //   1589: dup
          //   1590: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1593: ldc 100
          //   1595: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1598: aload_0
          //   1599: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1602: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1605: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1608: ldc 101
          //   1610: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1613: lload 8
          //   1615: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
          //   1618: ldc 98
          //   1620: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1623: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1626: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   1629: aload_0
          //   1630: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1633: invokestatic 25	com/remedy/arsys/share/Cache$ServerReaper:access$600	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/Object;
          //   1636: lload 8
          //   1638: invokevirtual 102	java/lang/Object:wait	(J)V
          //   1641: goto +39 -> 1680
          //   1644: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1647: new 3	java/lang/StringBuilder
          //   1650: dup
          //   1651: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1654: ldc 100
          //   1656: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1659: aload_0
          //   1660: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1663: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1666: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1669: ldc 103
          //   1671: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1674: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1677: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   1680: aload_0
          //   1681: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1684: iconst_1
          //   1685: invokestatic 104	com/remedy/arsys/share/Cache$ServerReaper:access$1302	(Lcom/remedy/arsys/share/Cache$ServerReaper;Z)Z
          //   1688: pop
          //   1689: goto +21 -> 1710
          //   1692: astore 11
          //   1694: aload 11
          //   1696: invokevirtual 106	java/lang/InterruptedException:printStackTrace	()V
          //   1699: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1702: aload 11
          //   1704: invokevirtual 107	java/lang/InterruptedException:getMessage	()Ljava/lang/String;
          //   1707: invokevirtual 24	com/remedy/arsys/log/Log:fine	(Ljava/lang/String;)V
          //   1710: aload 10
          //   1712: monitorexit
          //   1713: goto +11 -> 1724
          //   1716: astore 17
          //   1718: aload 10
          //   1720: monitorexit
          //   1721: aload 17
          //   1723: athrow
          //   1724: goto -1722 -> 2
          //   1727: astore_2
          //   1728: aload_2
          //   1729: invokevirtual 88	java/lang/Exception:printStackTrace	()V
          //   1732: invokestatic 57	com/remedy/arsys/share/Cache:access$1100	()Lcom/remedy/arsys/log/Log;
          //   1735: getstatic 79	java/util/logging/Level:WARNING	Ljava/util/logging/Level;
          //   1738: new 3	java/lang/StringBuilder
          //   1741: dup
          //   1742: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1745: ldc 108
          //   1747: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1750: aload_0
          //   1751: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1754: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1757: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1760: ldc 109
          //   1762: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1765: aload_2
          //   1766: invokevirtual 90	java/lang/Exception:getMessage	()Ljava/lang/String;
          //   1769: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1772: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1775: aload_2
          //   1776: invokevirtual 82	com/remedy/arsys/log/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   1779: goto +3 -> 1782
          //   1782: jsr +72 -> 1854
          //   1785: goto +245 -> 2030
          //   1788: astore_2
          //   1789: aload_2
          //   1790: invokevirtual 88	java/lang/Exception:printStackTrace	()V
          //   1793: invokestatic 57	com/remedy/arsys/share/Cache:access$1100	()Lcom/remedy/arsys/log/Log;
          //   1796: getstatic 79	java/util/logging/Level:WARNING	Ljava/util/logging/Level;
          //   1799: new 3	java/lang/StringBuilder
          //   1802: dup
          //   1803: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1806: ldc 110
          //   1808: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1811: aload_0
          //   1812: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1815: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1818: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1821: ldc 109
          //   1823: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1826: aload_2
          //   1827: invokevirtual 90	java/lang/Exception:getMessage	()Ljava/lang/String;
          //   1830: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1833: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1836: aload_2
          //   1837: invokevirtual 82	com/remedy/arsys/log/Log:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
          //   1840: jsr +14 -> 1854
          //   1843: goto +187 -> 2030
          //   1846: astore 18
          //   1848: jsr +6 -> 1854
          //   1851: aload 18
          //   1853: athrow
          //   1854: astore 19
          //   1856: invokestatic 111	com/remedy/arsys/share/Cache:access$300	()Ljava/util/Map;
          //   1859: dup
          //   1860: astore 20
          //   1862: monitorenter
          //   1863: invokestatic 111	com/remedy/arsys/share/Cache:access$300	()Ljava/util/Map;
          //   1866: aload_0
          //   1867: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1870: invokevirtual 112	com/remedy/arsys/share/Cache$ServerReaper:getServer	()Ljava/lang/String;
          //   1873: invokeinterface 113 2 0
          //   1878: pop
          //   1879: aload 20
          //   1881: monitorexit
          //   1882: goto +11 -> 1893
          //   1885: astore 21
          //   1887: aload 20
          //   1889: monitorexit
          //   1890: aload 21
          //   1892: athrow
          //   1893: jsr +14 -> 1907
          //   1896: goto +132 -> 2028
          //   1899: astore 22
          //   1901: jsr +6 -> 1907
          //   1904: aload 22
          //   1906: athrow
          //   1907: astore 23
          //   1909: aload_1
          //   1910: ifnull +46 -> 1956
          //   1913: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1916: new 3	java/lang/StringBuilder
          //   1919: dup
          //   1920: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1923: ldc 100
          //   1925: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1928: aload_0
          //   1929: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1932: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   1935: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1938: ldc 114
          //   1940: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   1943: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   1946: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   1949: aload_1
          //   1950: iconst_1
          //   1951: invokevirtual 115	com/remedy/arsys/stubs/SessionData:dispose	(Z)V
          //   1954: aconst_null
          //   1955: astore_1
          //   1956: jsr +14 -> 1970
          //   1959: goto +67 -> 2026
          //   1962: astore 24
          //   1964: jsr +6 -> 1970
          //   1967: aload 24
          //   1969: athrow
          //   1970: astore 25
          //   1972: aload_0
          //   1973: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   1976: invokestatic 26	com/remedy/arsys/share/Cache$ServerReaper:access$700	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Z
          //   1979: ifne +6 -> 1985
          //   1982: invokestatic 116	com/remedy/arsys/share/Cache:initServerReapers	()V
          //   1985: invokestatic 12	com/remedy/arsys/share/Cache:access$500	()Lcom/remedy/arsys/log/Log;
          //   1988: new 3	java/lang/StringBuilder
          //   1991: dup
          //   1992: invokespecial 4	java/lang/StringBuilder:<init>	()V
          //   1995: ldc 100
          //   1997: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   2000: aload_0
          //   2001: getfield 1	com/remedy/arsys/share/Cache$ServerReaper$1:this$0	Lcom/remedy/arsys/share/Cache$ServerReaper;
          //   2004: invokestatic 7	com/remedy/arsys/share/Cache$ServerReaper:access$400	(Lcom/remedy/arsys/share/Cache$ServerReaper;)Ljava/lang/String;
          //   2007: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   2010: ldc 117
          //   2012: invokevirtual 6	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   2015: invokevirtual 8	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   2018: invokevirtual 14	com/remedy/arsys/log/Log:warning	(Ljava/lang/String;)V
          //   2021: invokestatic 118	com/remedy/arsys/goat/FormContext:dispose	()V
          //   2024: ret 25
          //   2026: ret 23
          //   2028: ret 19
          //   2030: return
          //
          // Exception table:
          //   from	to	target	type
          //   6	74	77	com/remedy/arsys/goat/GoatException
          //   104	114	117	finally
          //   117	121	117	finally
          //   918	968	971	finally
          //   971	976	971	finally
          //   178	1224	1230	com/remedy/arsys/goat/GoatException
          //   178	1224	1292	com/remedy/arsys/goat/cache/sync/SyncException
          //   178	1224	1354	java/lang/Exception
          //   178	1227	1416	finally
          //   1230	1289	1416	finally
          //   1292	1351	1416	finally
          //   1354	1413	1416	finally
          //   1416	1421	1416	finally
          //   1573	1689	1692	java/lang/InterruptedException
          //   1557	1570	1716	finally
          //   1573	1713	1716	finally
          //   1716	1721	1716	finally
          //   2	159	1727	java/lang/Exception
          //   162	1413	1727	java/lang/Exception
          //   1416	1570	1727	java/lang/Exception
          //   1573	1724	1727	java/lang/Exception
          //   2	1782	1788	java/lang/Exception
          //   2	1785	1846	finally
          //   1788	1843	1846	finally
          //   1846	1851	1846	finally
          //   1863	1882	1885	finally
          //   1885	1890	1885	finally
          //   1856	1896	1899	finally
          //   1899	1904	1899	finally
          //   1909	1959	1962	finally
          //   1962	1967	1962	finally
        }
      };
      synchronized (Cache.MServerUpdateMap)
      {
        Cache.MServerUpdateMap.put(this.mServerName, this);
      }
      local1.setDaemon(true);
      local1.start();
    }

    private void stop()
    {
      synchronized (this.mSleepLock)
      {
        this.mFinish = true;
        wakeUp();
      }
    }

    private void wakeUp()
    {
      synchronized (this.mSleepLock)
      {
        this.mShouldSleep = false;
        this.mSleepLock.notifyAll();
      }
    }

    protected String getServer()
    {
      return this.mServerName;
    }

    private boolean checkIfPreloadinProgress(String paramString)
    {
      Cache.mPerformanceLog.info("Checking for " + this.mServerName + " if preload in progress");
      if (PrefetchTask.MAP_SERVER_PRELOAD_STATE.get(paramString) != null)
      {
        int i = ((Integer)PrefetchTask.MAP_SERVER_PRELOAD_STATE.get(paramString)).intValue();
        if (i == PrefetchTask.PRELOAD_RUNNING)
          return true;
      }
      return false;
    }
  }

  private static class ServerReaperSessionData extends SessionData
  {
    private static final String SESSION_ID = "fixed server reaper session id";

    ServerReaperSessionData(String paramString)
      throws GoatException
    {
      super(null, null, null, null, null, "fixed server reaper session id/" + paramString);
    }

    public ServerLogin getServerLogin(String paramString)
      throws GoatException
    {
      try
      {
        return ServerLogin.getAdmin(paramString);
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
      return null;
    }
  }

  public static abstract interface Item extends Serializable
  {
    public abstract int getSize();

    public abstract String getServer();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.Cache
 * JD-Core Version:    0.6.1
 */