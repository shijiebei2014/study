package com.remedy.arsys.share;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FormType;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Level;
import net.sf.ehcache.Ehcache;

public abstract class BaseDataCache extends Cache
  implements Serializable
{
  private static final transient Set<String> registeredSpecialKeys = new HashSet(5);
  private static final transient Map<String, String> systemForms = new HashMap(5);
  protected transient MemoryDiskCache cache = null;
  protected static transient Log logger = Log.get(2);
  private static final transient String LAST_MODIFIED_TIME_KEY = registerSpecialKey("LAST_MODIFIED_TIME_KEY");
  protected static transient Log MPerformanceLog = Log.get(8);
  private static final transient String ENTRY_COUNT_KEY = registerSpecialKey("ENTRY_COUNT");
  protected final String formName;
  private transient ServerLogin context;
  private final String server;
  protected boolean loadAllEntries;

  protected BaseDataCache(String paramString1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, ServerLogin paramServerLogin, String paramString2)
  {
    this.loadAllEntries = paramBoolean1;
    this.cache = MemoryDiskCache.getInstance(paramString1, paramBoolean2, paramBoolean4);
    this.formName = paramString2;
    this.context = paramServerLogin;
    this.server = this.context.getServer();
    this.mName = String.format("%sGlobal", new Object[] { paramString1 });
    if ((paramBoolean3) && (paramString1.equals("ActorViews")) && (this.formName != null))
      synchronized (MCaches)
      {
        MCaches.put(this.mName, this);
      }
  }

  protected QualifierInfo getLastModifiedQualification()
  {
    int i = getLastModifiedTimeStamp();
    QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(2, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 6), new ArithmeticOrRelationalOperand(new Value(i))));
    return new QualifierInfo(1, localQualifierInfo, getQualification());
  }

  protected abstract QualifierInfo getQualification();

  protected abstract List<SortInfo> getSortList();

  protected abstract int[] getRetrieveFieldIds();

  protected abstract void onCacheRefreshed(boolean paramBoolean);

  protected final int getLastModifiedTimeStamp()
  {
    IntegerItem localIntegerItem = (IntegerItem)get(LAST_MODIFIED_TIME_KEY);
    if (localIntegerItem != null)
      return localIntegerItem.getValue();
    return 0;
  }

  protected final int getEntryCount()
  {
    IntegerItem localIntegerItem = (IntegerItem)get(ENTRY_COUNT_KEY);
    if (localIntegerItem != null)
      return localIntegerItem.getValue();
    return 0;
  }

  protected final void setEntryCount(int paramInt)
  {
    put(ENTRY_COUNT_KEY, new IntegerItem(this.server, paramInt));
  }

  protected final void setLastModifiedTimeStamp(int paramInt)
  {
    put(LAST_MODIFIED_TIME_KEY, new IntegerItem(this.server, paramInt));
  }

  public void checkCache()
    throws ARException
  {
    ReentrantReadWriteLock.ReadLock localReadLock = this.cache.getReadWriteLock().readLock();
    localReadLock.lock();
    MPerformanceLog.fine("DataCache: Checking cache " + this.mName + " to see if there are new entries.");
    Date localDate = new Date();
    boolean bool1 = false;
    try
    {
      int i = getLastModifiedTimeStamp() > 0 ? 1 : this.loadAllEntries ? 0 : 0;
      if ((this.formName == null) || ("".equals(this.formName)) || (this.context == null))
      {
        logger.warning("DataCache: System form not defined for " + this.mName);
        return;
      }
      QualifierInfo localQualifierInfo = this.loadAllEntries ? getQualification() : getLastModifiedQualification();
      int j = getMaximumRetrieve();
      int k = 0;
      List localList1 = getSortList();
      int[] arrayOfInt1 = getRetrieveFieldIds();
      boolean bool2 = false;
      int[] arrayOfInt2 = new int[arrayOfInt1 == null ? 1 : arrayOfInt1.length + 1];
      arrayOfInt2[0] = 6;
      if (arrayOfInt1 != null)
      {
        int m = 1;
        for (int i2 : arrayOfInt1)
        {
          arrayOfInt2[m] = i2;
          m++;
        }
      }
      List localList2 = this.context.getListEntryObjects(this.formName, localQualifierInfo, k, j, localList1, arrayOfInt2, bool2, null);
      if ((localList2 != null) && (localList2.size() > 0))
      {
        MPerformanceLog.fine("DataCache: New Entries found for cache " + this.mName);
        bool1 = true;
        localReadLock.unlock();
        addEntries(localList2);
        localReadLock.lock();
      }
      if (this.loadAllEntries)
      {
        localReadLock.unlock();
        boolean bool3 = checkForDeletedEntries(localList2);
        if ((!bool1) && (bool3))
          bool1 = true;
        localReadLock.lock();
      }
      if (i != 0)
      {
        OutputInteger localOutputInteger = new OutputInteger();
        this.context.getListEntryObjects(this.formName, getQualification(), 0, 1, null, new int[] { 6 }, false, localOutputInteger);
        ??? = getEntryCount();
        if ((??? > 0) && (localOutputInteger.intValue() < ???))
        {
          MPerformanceLog.fine("DataCache: Deleted Entries found for cache " + this.mName);
          bool1 = true;
          localReadLock.unlock();
          try
          {
            List localList3 = this.context.getListEntryObjects(this.formName, getQualification(), 0, 0, null, new int[] { 6 }, false, null);
            checkForDeletedEntries(localList3);
          }
          finally
          {
            localReadLock.lock();
          }
        }
        else if ((??? > 0) && (localOutputInteger.intValue() > ???))
        {
          setLastModifiedTimeStamp(0);
          boolean bool4 = this.loadAllEntries;
          this.loadAllEntries = true;
          try
          {
            localReadLock.unlock();
            checkCache();
          }
          finally
          {
            this.loadAllEntries = bool4;
            localReadLock.lock();
          }
        }
      }
    }
    finally
    {
      localReadLock.unlock();
      MPerformanceLog.fine("DataCache: Finished checking cache " + this.mName + " to see if there are new entries and took " + (new Date().getTime() - localDate.getTime()));
    }
    onCacheRefreshed(bool1);
  }

  private boolean checkForDeletedEntries(Collection<Entry> paramCollection)
  {
    boolean bool = false;
    if (paramCollection != null)
    {
      ReentrantReadWriteLock.WriteLock localWriteLock = this.cache.getReadWriteLock().writeLock();
      localWriteLock.lock();
      try
      {
        HashSet localHashSet = new HashSet(paramCollection.size());
        if (paramCollection != null)
        {
          localObject1 = paramCollection.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            Entry localEntry = (Entry)((Iterator)localObject1).next();
            localHashSet.add(localEntry.getEntryId());
          }
        }
        Object localObject1 = null;
        if ((GoatCacheManager.getDiskPersistent()) && (this.cache.getDiskCache() != null))
          localObject1 = this.cache.getDiskCache().getKeys();
        else
          localObject1 = this.cache.getMemCache().getKeys();
        if (localObject1 != null)
          for (int i = 0; i < ((List)localObject1).size(); i++)
          {
            String str = (String)((List)localObject1).get(i);
            if ((!isSpecialKey(str)) && (isLocalKey(str)))
            {
              CacheEntryItem localCacheEntryItem = (CacheEntryItem)get(str, false);
              if ((localCacheEntryItem != null) && (!localHashSet.contains(localCacheEntryItem.getEntry().getEntryId())))
              {
                removeEntry(localCacheEntryItem.getEntry());
                bool = true;
              }
            }
          }
      }
      finally
      {
        localWriteLock.unlock();
      }
    }
    return bool;
  }

  protected String generateKey(String[] paramArrayOfString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append(getPrimaryKey());
    localStringBuilder.append("}");
    if (paramArrayOfString != null)
      for (String str : paramArrayOfString)
      {
        localStringBuilder.append("{");
        localStringBuilder.append(str);
        localStringBuilder.append("}");
      }
    return localStringBuilder.toString();
  }

  protected String getPrimaryKey()
  {
    return this.server;
  }

  protected static synchronized String findServerForm(ServerLogin paramServerLogin, int[] paramArrayOfInt)
  {
    String str = null;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append(paramServerLogin.getServer());
    localStringBuilder.append("}");
    for (int k : paramArrayOfInt)
    {
      localStringBuilder.append("{");
      localStringBuilder.append(k);
      localStringBuilder.append("}");
    }
    ??? = localStringBuilder.toString();
    if (systemForms.containsKey(???))
      return (String)systemForms.get(???);
    try
    {
      List localList;
      try
      {
        localList = paramServerLogin.getListForm(0L, FormType.ALL.toInt() | 0x400, null, paramArrayOfInt);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
      if ((localList != null) && (localList.size() != 0))
        str = (String)localList.get(0);
      systemForms.put(???, str);
    }
    catch (GoatException localGoatException)
    {
      logger.log(Level.WARNING, "Error loading system from server " + paramServerLogin.getServer(), localGoatException);
    }
    return str;
  }

  public final Cache.Item get(String paramString)
  {
    return get(paramString, true);
  }

  protected final Cache.Item get(String paramString, boolean paramBoolean)
  {
    ReentrantReadWriteLock.ReadLock localReadLock = this.cache.getReadWriteLock().readLock();
    localReadLock.lock();
    try
    {
      String str = null;
      if (paramBoolean)
        str = generateKey(new String[] { paramString });
      else
        str = paramString;
      Cache.Item localItem = this.cache.get(str, null);
      return localItem;
    }
    finally
    {
      localReadLock.unlock();
    }
  }

  public final Entry getEntry(String paramString)
  {
    return getEntry(paramString, true);
  }

  protected final Entry getEntry(String paramString, boolean paramBoolean)
  {
    Cache.Item localItem = get(paramString, paramBoolean);
    if ((localItem instanceof CacheEntryItem))
      return ((CacheEntryItem)localItem).getEntry();
    return null;
  }

  protected void addEntry(Entry paramEntry)
  {
    if (!containsKey(paramEntry.getEntryId()))
    {
      IntegerItem localIntegerItem1 = (IntegerItem)get(ENTRY_COUNT_KEY);
      if (localIntegerItem1 == null)
        localIntegerItem1 = new IntegerItem(this.server, 0);
      localIntegerItem1.setValue(localIntegerItem1.getValue() + 1);
      put(ENTRY_COUNT_KEY, localIntegerItem1);
    }
    if (paramEntry.get(Integer.valueOf(6)) != null)
    {
      int i = (int)((Timestamp)((Value)paramEntry.get(Integer.valueOf(6))).getValue()).getValue();
      IntegerItem localIntegerItem2 = (IntegerItem)get(LAST_MODIFIED_TIME_KEY);
      if (localIntegerItem2 == null)
        localIntegerItem2 = new IntegerItem(this.server, 0);
      if (i > localIntegerItem2.getValue())
      {
        localIntegerItem2.setValue(i);
        put(LAST_MODIFIED_TIME_KEY, localIntegerItem2);
      }
    }
    put(paramEntry.getEntryId(), new CacheEntryItem(this.context.getServer(), paramEntry));
  }

  protected void removeEntry(Entry paramEntry)
  {
    if (containsKey(paramEntry.getEntryId()))
    {
      IntegerItem localIntegerItem = (IntegerItem)get(ENTRY_COUNT_KEY);
      if (localIntegerItem == null)
        localIntegerItem = new IntegerItem(this.server, 0);
      else
        localIntegerItem.setValue(localIntegerItem.getValue() - 1);
      put(ENTRY_COUNT_KEY, localIntegerItem);
      remove(paramEntry.getEntryId());
    }
  }

  protected String getValueForFieldAsString(int paramInt, Entry paramEntry)
  {
    Object localObject = null;
    if (paramEntry != null)
    {
      Value localValue = (Value)paramEntry.get(Integer.valueOf(paramInt));
      if ((localValue != null) && (localValue.getValue() != null))
      {
        String str = localValue.getValue().toString();
        if (!"".equals(str))
          localObject = str;
      }
    }
    return localObject;
  }

  protected Value getValueForField(int paramInt, Entry paramEntry)
  {
    if (paramEntry != null)
      return (Value)paramEntry.get(Integer.valueOf(paramInt));
    return null;
  }

  public final void put(String paramString, Cache.Item paramItem)
  {
    put(paramString, paramItem, true);
  }

  protected final void put(String paramString, Cache.Item paramItem, boolean paramBoolean)
  {
    String str = paramBoolean ? generateKey(new String[] { paramString }) : paramString;
    this.cache.put(str, paramItem);
  }

  public final void remove(String paramString)
  {
    remove(paramString, true);
  }

  protected final void remove(String paramString, boolean paramBoolean)
  {
    String str = paramBoolean ? generateKey(new String[] { paramString }) : paramString;
    this.cache.remove(str);
  }

  public boolean containsKey(String paramString)
  {
    String str = generateKey(new String[] { paramString });
    return this.cache.containsKey(str);
  }

  protected final String getServer()
  {
    return this.server;
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if ((this.server != null) && (SessionData.get() != null))
      try
      {
        this.context = SessionData.get().getServerLogin(this.server);
      }
      catch (GoatException localGoatException)
      {
        logger.log(Level.SEVERE, "Unable to create a login context for server " + this.server);
      }
  }

  protected static String registerSpecialKey(String paramString)
  {
    registeredSpecialKeys.add(paramString);
    return paramString;
  }

  protected final boolean isSpecialKey(String paramString)
  {
    Iterator localIterator = registeredSpecialKeys.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.indexOf(str) >= 0)
        return true;
    }
    return false;
  }

  protected final boolean isLocalKey(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{");
    localStringBuilder.append(getPrimaryKey());
    localStringBuilder.append("}");
    return paramString.indexOf(localStringBuilder.toString()) == 0;
  }

  protected final MemoryDiskCache getCache()
  {
    return this.cache;
  }

  public final String getFormName()
  {
    return this.formName;
  }

  protected void release()
  {
    if (this.cache != null)
      this.cache.release();
  }

  protected void doDumpHTMLStats(HTMLWriter paramHTMLWriter, String paramString, int paramInt)
  {
  }

  protected void doReap(String paramString, int paramInt)
  {
  }

  public void addEntries(Collection<Entry> paramCollection)
  {
    ReentrantReadWriteLock.WriteLock localWriteLock = this.cache.getReadWriteLock().writeLock();
    localWriteLock.lock();
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        addEntry(localEntry);
      }
    }
    finally
    {
      localWriteLock.unlock();
    }
  }

  protected abstract int getMaximumRetrieve();

  public static class IntegerItem
    implements Cache.Item
  {
    private static final long serialVersionUID = 1797737654179635471L;
    private int value;
    private String server;

    public IntegerItem()
    {
    }

    public IntegerItem(String paramString, int paramInt)
    {
      this.value = paramInt;
      this.server = paramString;
    }

    public String getServer()
    {
      return this.server;
    }

    public int getSize()
    {
      return 32;
    }

    public int getValue()
    {
      return this.value;
    }

    public void setValue(int paramInt)
    {
      this.value = paramInt;
    }
  }

  public static class CacheEntryItem
    implements Cache.Item
  {
    private static final long serialVersionUID = 6215888563217303067L;
    private Entry entry;
    private String server;

    public CacheEntryItem()
    {
    }

    public CacheEntryItem(String paramString, Entry paramEntry)
    {
      this.server = paramString;
      this.entry = paramEntry;
    }

    public String getServer()
    {
      return this.server;
    }

    public int getSize()
    {
      return this.entry.size();
    }

    public Entry getEntry()
    {
      return this.entry;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.BaseDataCache
 * JD-Core Version:    0.6.1
 */