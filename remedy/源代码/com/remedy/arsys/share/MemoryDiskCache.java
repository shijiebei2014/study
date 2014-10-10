package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

public class MemoryDiskCache extends Cache
  implements Serializable
{
  private static final long serialVersionUID = 6719843549050928237L;
  private final transient net.sf.ehcache.Cache diskCache;
  private transient Integer referenceCount;
  private transient boolean isReaped = false;
  protected final transient ReentrantReadWriteLock readWriteLock;

  private MemoryDiskCache(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
      this.referenceCount = new Integer(1);
    else
      this.referenceCount = null;
    this.readWriteLock = new ReentrantReadWriteLock();
    boolean bool = paramBoolean1;
    this.ehCache = createCache(paramString, paramInt1, bool);
    this.mSensitivity = paramInt1;
    synchronized (MCaches)
    {
      MCaches.put(paramString, this);
    }
    if (paramBoolean1)
      this.diskCache = createDiskCache(paramString);
    else
      this.diskCache = null;
    this.mName = paramString;
    GoatCacheManager.getInstance().addCache(this);
  }

  public static final MemoryDiskCache getInstance(String paramString, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    MemoryDiskCache localMemoryDiskCache = null;
    if (GoatCacheManager.getInstance().containsGoatCache(paramString))
    {
      localMemoryDiskCache = (MemoryDiskCache)GoatCacheManager.getInstance().getGoatCache(paramString);
      if (localMemoryDiskCache != null)
        localMemoryDiskCache.increaseReferenceCount();
    }
    else
    {
      localMemoryDiskCache = new MemoryDiskCache(paramString, paramInt1, paramInt2, paramBoolean1, paramBoolean2);
    }
    return localMemoryDiskCache;
  }

  public static final MemoryDiskCache getInstance(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    return getInstance(paramString, 4, 0, paramBoolean1, paramBoolean2);
  }

  public void put(String paramString, Cache.Item paramItem)
  {
    super.put(paramString, paramItem);
    if ((this.diskCache != null) && (GoatCacheManager.getDiskPersistent()))
      this.diskCache.put(new Element(paramString, paramItem));
  }

  public Cache.Item get(String paramString, Class paramClass)
  {
    Cache.Item localItem = super.get(paramString, paramClass);
    if ((localItem != null) || (this.diskCache == null))
      return localItem;
    synchronized (paramString.intern())
    {
      Element localElement = this.diskCache.get(paramString);
      if ((localElement != null) && ((paramClass == null) || (paramClass.isAssignableFrom(localElement.getObjectValue().getClass()))))
      {
        put(paramString, (Cache.Item)localElement.getObjectValue());
        return (Cache.Item)localElement.getObjectValue();
      }
      return null;
    }
  }

  protected long getCacheSize()
  {
    return getMemCache().getSize();
  }

  protected void doDumpHTMLStats(HTMLWriter paramHTMLWriter, String paramString, int paramInt)
  {
    String str = paramString + " " + typeToClassType(paramInt, -1);
    paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).cdata(this.mName).closeTag("td", false);
    str = paramString + " " + typeToClassType(paramInt, -2);
    paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).append("" + getCacheSize()).closeTag("td", false);
    if (Configuration.getInstance().getCacheEnableStatistics())
    {
      paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).append("" + getCacheHits()).closeTag("td", false);
      paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).append("" + getCacheMisses()).closeTag("td", false);
    }
    str = paramString + " " + typeToClassType(paramInt, -3);
    paramHTMLWriter.openTag("td").attr("class", str).attr("nowrap").endTag(false).cdata(this.mLastReapTime == null ? "-" : this.mLastReapTime).closeTag("td", false);
  }

  protected long getCacheHits()
  {
    return getMemCache().getStatistics().getCacheHits();
  }

  protected long getCacheMisses()
  {
    return getMemCache().getStatistics().getCacheMisses();
  }

  public boolean containsKey(String paramString)
  {
    boolean bool = super.containsKey(paramString);
    if ((bool) || (this.diskCache == null))
      return bool;
    return this.diskCache.isKeyInCache(paramString);
  }

  protected void doReap(String paramString, int paramInt)
  {
    if ((this.mSensitivity & paramInt) == 0)
      return;
    try
    {
      String str = paramString == null ? "All Servers" : paramString;
      this.mLastReapTime = (new Date().toString() + " (server: " + str + ")");
      super.doReap(paramString, paramInt);
      if (this.diskCache != null)
        this.diskCache.removeAll();
    }
    finally
    {
      this.isReaped = true;
    }
  }

  public boolean isReaped()
  {
    return this.isReaped;
  }

  public void resetReaped()
  {
    this.isReaped = false;
  }

  public Ehcache getDiskCache()
  {
    return this.diskCache;
  }

  public void saveOnSizeChange()
  {
    long l = this.diskCache == null ? 0L : this.diskCache.getMemoryStoreSize();
    if (l > 0L)
      save();
  }

  public void save()
  {
    if (this.diskCache != null)
      this.diskCache.flush();
  }

  public void remove(String paramString)
  {
    getMemCache().remove(paramString);
    if ((this.diskCache != null) && (GoatCacheManager.getDiskPersistent()))
      this.diskCache.put(new Element(paramString, null));
  }

  public final ReentrantReadWriteLock getReadWriteLock()
  {
    return this.readWriteLock;
  }

  private final synchronized void increaseReferenceCount()
  {
    if (this.referenceCount != null)
      this.referenceCount = Integer.valueOf(this.referenceCount.intValue() + 1);
  }

  private final synchronized void decreaseReferenceCount()
  {
    if (this.referenceCount != null)
    {
      this.referenceCount = Integer.valueOf(this.referenceCount.intValue() - 1);
      if (this.referenceCount.intValue() <= 0)
      {
        synchronized (MCaches)
        {
          MCaches.remove(this.mName);
        }
        try
        {
          GoatCacheManager.getInstance().removeCache(this.mName);
        }
        catch (IllegalStateException localIllegalStateException)
        {
        }
      }
    }
  }

  public final void release()
  {
    decreaseReferenceCount();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.MemoryDiskCache
 * JD-Core Version:    0.6.1
 */