package com.remedy.arsys.share;

import java.util.Hashtable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class MiscCache extends Cache
{
  private String mPrefix = "";
  private static Cache baseCache = new Cache("Sysdata", 1);
  private static net.sf.ehcache.Cache diskCache = Cache.createDiskCache("Sysdata");
  private static boolean isRegistered = false;

  public MiscCache(String paramString)
  {
    this.mPrefix = paramString;
    MCaches.put("Sysdata", this);
    this.ehCache = baseCache.getMemCache();
    this.mSensitivity = 1;
    this.mName = baseCache.getName();
    registerCacheIfNeeded(this);
  }

  public static final synchronized void registerCacheIfNeeded(Cache paramCache)
  {
    if (isRegistered)
      return;
    GoatCacheManager.getInstance().addCache(paramCache);
    isRegistered = true;
  }

  public void put(String paramString, Cache.Item paramItem)
  {
    String str = hashCode(paramString);
    baseCache.put(str, paramItem);
    if (GoatCacheManager.getDiskPersistent())
      diskCache.put(new Element(str, paramItem));
  }

  public boolean containsKey(String paramString)
  {
    String str = hashCode(paramString);
    boolean bool = baseCache.containsKey(str);
    if (bool)
      return true;
    return diskCache.isKeyInCache(str);
  }

  public void remove(String paramString)
  {
    String str = hashCode(paramString);
    baseCache.remove(str);
    diskCache.remove(str);
  }

  public String getPrefix()
  {
    return this.mPrefix;
  }

  private String hashCode(String paramString)
  {
    return getPrefix() + paramString;
  }

  public Cache.Item get(String paramString, Class paramClass)
  {
    String str = hashCode(paramString);
    Cache.Item localItem = baseCache.get(str, paramClass);
    if (localItem != null)
      return localItem;
    Element localElement = diskCache.get(str);
    if ((localElement != null) && ((paramClass == null) || (paramClass.isAssignableFrom(localElement.getObjectValue().getClass()))))
    {
      baseCache.put(str, (Cache.Item)localElement.getObjectValue());
      return (Cache.Item)localElement.getObjectValue();
    }
    return null;
  }

  protected void doReap(String paramString, int paramInt)
  {
    if ((this.mSensitivity & paramInt) == 0)
      return;
    baseCache.doReap(paramString, paramInt);
    diskCache.removeAll();
  }

  protected void doDumpHTMLStats(HTMLWriter paramHTMLWriter, String paramString, int paramInt)
  {
    baseCache.doDumpHTMLStats(paramHTMLWriter, paramString, paramInt);
  }

  public Ehcache getDiskCache()
  {
    return diskCache;
  }

  public void saveOnSizeChange()
  {
    long l = diskCache.getMemoryStoreSize();
    if (l > 0L)
      save();
  }

  public void save()
  {
    diskCache.flush();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.MiscCache
 * JD-Core Version:    0.6.1
 */