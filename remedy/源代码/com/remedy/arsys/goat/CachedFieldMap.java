package com.remedy.arsys.goat;

import com.bmc.arsys.api.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CachedFieldMap
  implements Map
{
  private CachedFieldMapInternal cachedFieldMapInternal;

  public CachedFieldMap(CachedFieldMapInternal paramCachedFieldMapInternal)
  {
    this.cachedFieldMapInternal = paramCachedFieldMapInternal;
  }

  public int[] getFieldIDs()
    throws GoatException
  {
    return this.cachedFieldMapInternal.getFieldIDs();
  }

  public Field getFieldByDBName(String paramString)
  {
    return this.cachedFieldMapInternal.getFieldByDBName(paramString);
  }

  public void clear()
  {
    this.cachedFieldMapInternal.clear();
  }

  public boolean containsKey(Object paramObject)
  {
    return this.cachedFieldMapInternal.containsKey(paramObject);
  }

  public boolean containsValue(Object paramObject)
  {
    return this.cachedFieldMapInternal.containsValue(paramObject);
  }

  public Set entrySet()
  {
    return this.cachedFieldMapInternal.entrySet();
  }

  public Object get(Object paramObject)
  {
    return this.cachedFieldMapInternal.get((Integer)paramObject);
  }

  public boolean isEmpty()
  {
    return this.cachedFieldMapInternal.isEmpty();
  }

  public Set keySet()
  {
    return this.cachedFieldMapInternal.keySet();
  }

  public Object put(Object paramObject1, Object paramObject2)
  {
    return this.cachedFieldMapInternal.put(paramObject1, paramObject2);
  }

  public void putAll(Map paramMap)
  {
    Set localSet = paramMap.entrySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      Map.Entry localEntry = (Map.Entry)localObject;
      put(localEntry.getKey(), localEntry.getValue());
    }
  }

  public Object remove(Object paramObject)
  {
    return this.cachedFieldMapInternal.remove(paramObject);
  }

  public int size()
  {
    return this.cachedFieldMapInternal.size();
  }

  public Collection values()
  {
    return this.cachedFieldMapInternal.values();
  }

  public String getServer()
  {
    return this.cachedFieldMapInternal.getServer();
  }

  public String getForm()
  {
    return this.cachedFieldMapInternal.getForm();
  }

  public CachedFieldMapInternal getCachedFieldMapInternal()
  {
    return this.cachedFieldMapInternal;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CachedFieldMap
 * JD-Core Version:    0.6.1
 */