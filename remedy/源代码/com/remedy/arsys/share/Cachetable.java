package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;

public class Cachetable extends Cache
{
  protected net.sf.ehcache.Cache diskCache;

  public Cachetable(String paramString)
  {
    this(paramString, 1, 1);
  }

  public Cachetable(String paramString, int paramInt1, int paramInt2)
  {
    this.ehCache = createCache(paramString, paramInt1);
    this.mSensitivity = paramInt1;
    synchronized (MCaches)
    {
      MCaches.put(paramString, this);
    }
    this.diskCache = createDiskCache(paramString);
    GoatCacheManager.getInstance().addCache(this);
  }

  public Cachetable(String paramString, int paramInt)
  {
    this(paramString, paramInt, 0);
  }

  public void put(String paramString, Cache.Item paramItem)
  {
    super.put(paramString, paramItem);
    if (GoatCacheManager.getDiskPersistent())
      this.diskCache.put(new Element(paramString, paramItem));
  }

  public Cache.Item get(String paramString, Class paramClass)
  {
    Cache.Item localItem = super.get(paramString, paramClass);
    if (localItem != null)
      return localItem;
    return getFromDiskHelper(paramString, paramClass);
  }

  private Cache.Item getFromDiskHelper(String paramString, Class paramClass)
  {
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

  public Set<Cache.Item> getAll(String paramString, Class paramClass)
  {
    Set localSet = super.getAll(paramString, paramClass);
    List localList = this.diskCache.getKeys();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      String str = (String)localObject;
      Cache.Item localItem = getFromDiskHelper(str, paramClass);
      if ((localItem != null) && (paramString.equals(localItem.getServer())))
        localSet.add(localItem);
    }
    return localSet;
  }

  public void remove(String paramString)
  {
    this.diskCache.remove(paramString);
    super.remove(paramString);
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
    if (bool)
      return true;
    return this.diskCache.isKeyInCache(paramString);
  }

  protected void doReap(String paramString, int paramInt)
  {
    if ((this.mSensitivity & paramInt) == 0)
      return;
    String str = paramString == null ? "All Servers" : paramString;
    this.mLastReapTime = (new Date().toString() + " (server: " + str + ")");
    super.doReap(paramString, paramInt);
    this.diskCache.removeAll();
  }

  public Ehcache getDiskCache()
  {
    return this.diskCache;
  }

  public void saveOnSizeChange()
  {
    long l = this.diskCache.getMemoryStoreSize();
    if (l > 0L)
      save();
  }

  public void save()
  {
    this.diskCache.flush();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.Cachetable
 * JD-Core Version:    0.6.1
 */