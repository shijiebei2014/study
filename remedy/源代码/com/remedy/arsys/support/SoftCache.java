package com.remedy.arsys.support;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoftCache
  implements Cloneable
{
  private Map cache;
  private ReferenceQueue garbageCan = new ReferenceQueue();

  public SoftCache()
  {
    this.cache = new HashMap();
  }

  public SoftCache(int paramInt)
  {
    this.cache = new HashMap(paramInt);
  }

  public SoftCache(int paramInt, float paramFloat)
  {
    this.cache = new HashMap(paramInt, paramFloat);
  }

  public static SoftCache synchronizedCache(SoftCache paramSoftCache)
  {
    paramSoftCache.getClass();
    return new SynchronizedSoftCache(null);
  }

  public void put(Object paramObject1, Object paramObject2)
  {
    removeGarbage();
    EntryRef localEntryRef = new EntryRef(paramObject1, paramObject2, this.garbageCan, null);
    this.cache.put(paramObject1, localEntryRef);
  }

  public Object get(Object paramObject)
  {
    removeGarbage();
    EntryRef localEntryRef = (EntryRef)this.cache.get(paramObject);
    if (localEntryRef != null)
      return localEntryRef.get();
    return null;
  }

  public Set keySet()
  {
    removeGarbage();
    return this.cache.keySet();
  }

  public boolean containsKey(Object paramObject)
  {
    removeGarbage();
    return this.cache.containsKey(paramObject);
  }

  public boolean containsValue(Object paramObject)
  {
    removeGarbage();
    return this.cache.containsValue(paramObject);
  }

  public Object remove(Object paramObject)
  {
    removeGarbage();
    EntryRef localEntryRef = (EntryRef)this.cache.remove(paramObject);
    if (localEntryRef != null)
    {
      Object localObject = localEntryRef.get();
      localEntryRef.clear();
      return localObject;
    }
    return null;
  }

  public int size()
  {
    return this.cache.size();
  }

  public void clear()
  {
    this.cache.clear();
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return null;
  }

  private void removeGarbage()
  {
    EntryRef localEntryRef;
    while ((localEntryRef = (EntryRef)this.garbageCan.poll()) != null)
      this.cache.remove(localEntryRef.getKey());
  }

  private class SynchronizedSoftCache extends SoftCache
  {
    private Object lock = new Object();

    private SynchronizedSoftCache()
    {
    }

    public void clear()
    {
      synchronized (this.lock)
      {
        SoftCache.this.clear();
      }
    }

    public boolean containsKey(Object paramObject)
    {
      synchronized (this.lock)
      {
        return SoftCache.this.containsKey(paramObject);
      }
    }

    public boolean containsValue(Object paramObject)
    {
      synchronized (this.lock)
      {
        return SoftCache.this.containsValue(paramObject);
      }
    }

    public Object get(Object paramObject)
    {
      synchronized (this.lock)
      {
        return SoftCache.this.get(paramObject);
      }
    }

    public Set keySet()
    {
      synchronized (this.lock)
      {
        return SoftCache.this.keySet();
      }
    }

    public void put(Object paramObject1, Object paramObject2)
    {
      synchronized (this.lock)
      {
        SoftCache.this.put(paramObject1, paramObject2);
      }
    }

    public Object remove(Object paramObject)
    {
      synchronized (this.lock)
      {
        return SoftCache.this.remove(paramObject);
      }
    }

    public int size()
    {
      synchronized (this.lock)
      {
        return SoftCache.this.size();
      }
    }

    public Object clone()
    {
      synchronized (this.lock)
      {
        return SoftCache.this.clone();
      }
    }

    public boolean equals(Object paramObject)
    {
      synchronized (this.lock)
      {
        return SoftCache.this.equals(paramObject);
      }
    }

    public int hashCode()
    {
      synchronized (this.lock)
      {
        return SoftCache.this.hashCode();
      }
    }

    public String toString()
    {
      synchronized (this.lock)
      {
        return SoftCache.this.toString();
      }
    }
  }

  private class EntryRef extends SoftReference
  {
    private Object key;
    private int hashcode;

    private EntryRef(Object paramObject1, Object paramReferenceQueue, ReferenceQueue arg4)
    {
      super(localReferenceQueue);
      this.hashcode = (paramReferenceQueue != null ? paramReferenceQueue.hashCode() : 0);
      this.key = paramObject1;
    }

    private Object getKey()
    {
      return this.key;
    }

    public int hashCode()
    {
      return this.hashcode;
    }

    public boolean equals(Object paramObject)
    {
      Object localObject = get();
      if (localObject != null)
      {
        if ((paramObject instanceof EntryRef))
          return localObject.equals(((EntryRef)paramObject).get());
        return localObject.equals(paramObject);
      }
      if ((paramObject instanceof EntryRef))
        return null == ((EntryRef)paramObject).get();
      return null == paramObject;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.SoftCache
 * JD-Core Version:    0.6.1
 */