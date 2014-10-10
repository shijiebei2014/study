package com.remedy.arsys.share;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Entry;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;

public abstract class BaseMappingDataCache extends BaseDataCache
  implements Serializable
{
  protected static final transient String DEFAULT_VALUE = "$NULL$";
  private transient MemoryDiskCache dataMap;
  private transient boolean isRebuilding = false;
  private final String name;
  private final boolean useDiskCache;
  private final boolean trackReferences;

  public BaseMappingDataCache(String paramString1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, ServerLogin paramServerLogin, String paramString2)
  {
    super(paramString1, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramServerLogin, paramString2);
    this.name = paramString1;
    this.useDiskCache = paramBoolean2;
    this.trackReferences = paramBoolean4;
    String str = String.format("%sMapping", new Object[] { paramString1 });
    this.dataMap = MemoryDiskCache.getInstance(str, paramBoolean2, paramBoolean4);
  }

  protected void onCacheRefreshed(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      MPerformanceLog.fine("DataCache Mapping: Rebuilding map for cache " + this.mName + ".");
      Date localDate = new Date();
      rebuildMap();
      MPerformanceLog.fine("DataCache Mapping: Map rebuild for cache " + this.mName + " and took " + (new Date().getTime() - localDate.getTime()));
    }
  }

  public void rebuildMap()
  {
    ReentrantReadWriteLock.WriteLock localWriteLock = this.cache.getReadWriteLock().writeLock();
    localWriteLock.lock();
    try
    {
      removeCurrentMappings();
      List localList;
      if ((GoatCacheManager.getDiskPersistent()) && (super.getCache().getDiskCache() != null))
        localList = super.getCache().getDiskCache().getKeys();
      else
        localList = super.getCache().getMemCache().getKeys();
      if ((localList != null) && (localList.size() > 0))
        for (int i = 0; i < localList.size(); i++)
        {
          String str = (String)localList.get(i);
          if (str.indexOf(generateKey(null)) == 0)
            addEntryToMap(getEntry(str, false));
        }
    }
    finally
    {
      localWriteLock.unlock();
    }
  }

  protected void addMapping(String[] paramArrayOfString, Serializable paramSerializable)
  {
    String str = generateKey(paramArrayOfString);
    if (this.dataMap.get(str, null) == null)
      this.dataMap.put(str, new MappingItem(super.getServer(), paramSerializable));
  }

  protected void replaceMapping(String[] paramArrayOfString, Serializable paramSerializable)
  {
    String str = generateKey(paramArrayOfString);
    this.dataMap.put(str, new MappingItem(super.getServer(), paramSerializable));
  }

  public void checkCache()
    throws ARException
  {
    super.checkCache();
    if ((this.dataMap == null) && (getLastModifiedTimeStamp() > 0))
      rebuildMap();
  }

  protected Serializable getMapping(String[] paramArrayOfString)
  {
    ReentrantReadWriteLock.ReadLock localReadLock = this.cache.getReadWriteLock().readLock();
    localReadLock.lock();
    int i;
    try
    {
      if (this.dataMap == null)
        i = 1;
      else
        i = (this.dataMap.isReaped()) && (!this.isRebuilding) ? 1 : 0;
    }
    finally
    {
      localReadLock.unlock();
    }
    if (i != 0)
    {
      localObject2 = this.cache.getReadWriteLock().writeLock();
      ((ReentrantReadWriteLock.WriteLock)localObject2).lock();
      try
      {
        if (this.dataMap == null)
          i = 1;
        else
          i = (this.dataMap.isReaped()) && (!this.isRebuilding) ? 1 : 0;
        if (i != 0)
        {
          this.isRebuilding = true;
          setLastModifiedTimeStamp(0);
          this.loadAllEntries = true;
          checkCache();
          if (this.dataMap != null)
            this.dataMap.resetReaped();
        }
      }
      catch (ARException localARException)
      {
        localARException.printStackTrace();
      }
      finally
      {
        this.isRebuilding = false;
        ((ReentrantReadWriteLock.WriteLock)localObject2).unlock();
      }
    }
    Object localObject2 = null;
    if (this.dataMap != null)
    {
      String str = generateKey(paramArrayOfString);
      try
      {
        if (this.dataMap.containsKey(str))
          localObject2 = this.dataMap.get(str, null);
      }
      catch (Exception localException)
      {
      }
    }
    return localObject2 == null ? null : ((MappingItem)localObject2).getValue();
  }

  protected abstract void addEntryToMap(Entry paramEntry);

  protected String getValueForFieldAsString(int paramInt, Entry paramEntry)
  {
    String str = super.getValueForFieldAsString(paramInt, paramEntry);
    if ((str == null) || ("".equals(str)))
      return "$NULL$";
    return str;
  }

  protected final void removeCurrentMappings()
  {
    if (this.dataMap != null)
    {
      List localList;
      if ((GoatCacheManager.getDiskPersistent()) && (this.dataMap.getDiskCache() != null))
        localList = this.dataMap.getDiskCache().getKeys();
      else
        localList = this.dataMap.getMemCache().getKeys();
      for (int i = 0; (localList != null) && (i < localList.size()); i++)
      {
        String str = (String)localList.get(i);
        if (!isSpecialKey(str))
          if (str.indexOf(String.format("{%s}", new Object[] { getPrimaryKey() })) == 0)
            try
            {
              this.dataMap.remove(str);
            }
            catch (Exception localException)
            {
            }
      }
    }
  }

  protected void release()
  {
    if (this.dataMap != null)
      this.dataMap.release();
    super.release();
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    this.cache = MemoryDiskCache.getInstance(this.name, this.useDiskCache, this.trackReferences);
    String str = String.format("%sMapping", new Object[] { this.name });
    this.dataMap = MemoryDiskCache.getInstance(str, this.useDiskCache, this.trackReferences);
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    release();
  }

  public static class MappingItem
    implements Cache.Item
  {
    private static final long serialVersionUID = 4211188328969447906L;
    private String server = null;
    private Serializable value = null;

    public MappingItem()
    {
    }

    public MappingItem(String paramString, Serializable paramSerializable)
    {
      this.server = paramString;
      this.value = paramSerializable;
    }

    public Serializable getValue()
    {
      return this.value;
    }

    public void setValue(Serializable paramSerializable)
    {
      this.value = paramSerializable;
    }

    public String getServer()
    {
      return this.server;
    }

    public int getSize()
    {
      return 0;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.BaseMappingDataCache
 * JD-Core Version:    0.6.1
 */