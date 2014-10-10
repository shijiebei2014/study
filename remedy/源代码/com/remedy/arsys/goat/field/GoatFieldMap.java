package com.remedy.arsys.goat.field;

import com.remedy.arsys.share.Cache.Item;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GoatFieldMap<K, V>
  implements Map<K, V>, Cache.Item
{
  private static final long serialVersionUID = -3425830948830221775L;
  private Map<K, V> map = Collections.synchronizedMap(new HashMap());

  public int getSize()
  {
    return 1;
  }

  public String getServer()
  {
    return "";
  }

  public void clear()
  {
    this.map.clear();
  }

  public boolean containsKey(Object paramObject)
  {
    return this.map.containsKey(paramObject);
  }

  public boolean containsValue(Object paramObject)
  {
    return this.map.containsValue(paramObject);
  }

  public Set entrySet()
  {
    return this.map.entrySet();
  }

  public boolean isEmpty()
  {
    return this.map.isEmpty();
  }

  public Set keySet()
  {
    return this.map.keySet();
  }

  public int size()
  {
    return this.map.size();
  }

  public Collection values()
  {
    return this.map.values();
  }

  public V get(Object paramObject)
  {
    return this.map.get(paramObject);
  }

  public V put(K paramK, V paramV)
  {
    return this.map.put(paramK, paramV);
  }

  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    this.map.putAll(paramMap);
  }

  public V remove(Object paramObject)
  {
    return this.map.remove(paramObject);
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    synchronized (this.map)
    {
      paramObjectOutputStream.writeObject(this.map);
    }
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.map = ((Map)paramObjectInputStream.readObject());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.GoatFieldMap
 * JD-Core Version:    0.6.1
 */