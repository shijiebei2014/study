package com.remedy.arsys.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class MultiHashPool
{
  private Map mMap = new HashMap();

  public void addItem(Object paramObject1, Object paramObject2)
  {
    LinkedList localLinkedList = (LinkedList)this.mMap.get(paramObject1);
    if (localLinkedList == null)
    {
      localLinkedList = new LinkedList();
      this.mMap.put(paramObject1, localLinkedList);
    }
    localLinkedList.add(paramObject2);
    assert (localLinkedList.size() > 0);
  }

  public ArrayList removeAll()
  {
    Iterator localIterator = this.mMap.values().iterator();
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      LinkedList localLinkedList = (LinkedList)localIterator.next();
      ListIterator localListIterator = localLinkedList.listIterator(0);
      while (localListIterator.hasNext())
      {
        Object localObject = localListIterator.next();
        localArrayList.add(localObject);
      }
      localIterator.remove();
    }
    assert (this.mMap.values().size() == 0);
    this.mMap = new HashMap();
    return localArrayList;
  }

  public Iterator getIterator(Object paramObject)
  {
    LinkedList localLinkedList = (LinkedList)this.mMap.get(paramObject);
    if (localLinkedList == null)
      return null;
    assert (localLinkedList.size() > 0);
    return localLinkedList.listIterator(0);
  }

  public Object iterate(Object paramObject, boolean paramBoolean, IteratorCB paramIteratorCB)
  {
    Iterator localIterator = getIterator(paramObject);
    if (localIterator == null)
      return null;
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (paramIteratorCB.callback(localObject))
      {
        if (paramBoolean)
          localIterator.remove();
        itemsChanged(paramObject);
        return localObject;
      }
    }
    return null;
  }

  public Iterator getKeySet()
  {
    Set localSet = this.mMap.keySet();
    if (localSet.isEmpty())
      return null;
    return localSet.iterator();
  }

  protected void itemsChanged(Object paramObject)
  {
    LinkedList localLinkedList = (LinkedList)this.mMap.get(paramObject);
    if (localLinkedList == null)
      return;
    if (localLinkedList.size() == 0)
      this.mMap.remove(paramObject);
  }

  public static abstract interface IteratorCB
  {
    public abstract boolean callback(Object paramObject);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.MultiHashPool
 * JD-Core Version:    0.6.1
 */