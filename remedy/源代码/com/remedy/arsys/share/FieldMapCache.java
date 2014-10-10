package com.remedy.arsys.share;

import java.util.ArrayList;
import java.util.Hashtable;
import net.sf.ehcache.Ehcache;

public class FieldMapCache extends Cache
{
  private String mPrefix = "";
  private static ArrayList<String> prefixList = new ArrayList();
  private static Cachetable cache = new Cachetable("Field maps", 1);

  public FieldMapCache(String paramString)
  {
    this.mPrefix = paramString;
    prefixList.add(paramString);
    MCaches.put("Field maps", this);
    this.mSensitivity = 1;
    this.mName = cache.getName();
  }

  public void put(String paramString, Cache.Item paramItem)
  {
    String str = hashCode(paramString);
    cache.put(str, paramItem);
  }

  public Cache.Item get(String paramString, Class paramClass)
  {
    String str = hashCode(paramString);
    return cache.get(str, paramClass);
  }

  public boolean containsKey(String paramString)
  {
    String str = hashCode(paramString);
    return cache.containsKey(str);
  }

  private String hashCode(String paramString)
  {
    return getPrefix() + paramString;
  }

  public String getPrefix()
  {
    return this.mPrefix;
  }

  protected void doDumpHTMLStats(HTMLWriter paramHTMLWriter, String paramString, int paramInt)
  {
    cache.doDumpHTMLStats(paramHTMLWriter, paramString, paramInt);
  }

  protected void doReap(String paramString, int paramInt)
  {
    cache.doReap(paramString, paramInt);
  }

  public Ehcache getDiskCache()
  {
    return cache.getDiskCache();
  }

  public void saveOnSizeChange()
  {
    cache.saveOnSizeChange();
  }

  public void save()
  {
    cache.save();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.FieldMapCache
 * JD-Core Version:    0.6.1
 */