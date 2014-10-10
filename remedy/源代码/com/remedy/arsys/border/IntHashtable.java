package com.remedy.arsys.border;

import java.util.Dictionary;
import java.util.Enumeration;

public class IntHashtable extends Dictionary<Object, Object>
  implements Cloneable
{
  private IntHashtableEntry[] table;
  private float loadFactor;
  private int count;
  private int threshold;

  public IntHashtable(int paramInt, float paramFloat)
  {
    if ((paramInt <= 0) || (paramFloat <= 0.0D))
      throw new IllegalArgumentException();
    this.loadFactor = paramFloat;
    this.table = new IntHashtableEntry[paramInt];
    this.threshold = (int)(paramInt * paramFloat);
  }

  public IntHashtable(int paramInt)
  {
    this(paramInt, 0.75F);
  }

  public IntHashtable()
  {
    this(101, 0.75F);
  }

  public boolean isEmpty()
  {
    return this.count == 0;
  }

  public synchronized void clear()
  {
    IntHashtableEntry[] arrayOfIntHashtableEntry = this.table;
    int i = arrayOfIntHashtableEntry.length;
    while (true)
    {
      i--;
      if (i < 0)
        break;
      arrayOfIntHashtableEntry[i] = null;
    }
    this.count = 0;
  }

  public synchronized Object clone()
  {
    try
    {
      IntHashtable localIntHashtable = (IntHashtable)super.clone();
      localIntHashtable.table = new IntHashtableEntry[this.table.length];
      int i = this.table.length;
      while (i-- > 0)
        localIntHashtable.table[i] = (this.table[i] != null ? (IntHashtableEntry)this.table[i].clone() : null);
      return localIntHashtable;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    throw new InternalError();
  }

  public synchronized boolean contains(Object paramObject)
  {
    if (paramObject == null)
      throw new NullPointerException();
    IntHashtableEntry[] arrayOfIntHashtableEntry = this.table;
    int i = arrayOfIntHashtableEntry.length;
    while (i-- > 0)
      for (IntHashtableEntry localIntHashtableEntry = arrayOfIntHashtableEntry[i]; localIntHashtableEntry != null; localIntHashtableEntry = localIntHashtableEntry.next)
        if (localIntHashtableEntry.value.equals(paramObject))
          return true;
    return false;
  }

  public synchronized boolean containsKey(int paramInt)
  {
    IntHashtableEntry[] arrayOfIntHashtableEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfIntHashtableEntry.length;
    for (IntHashtableEntry localIntHashtableEntry = arrayOfIntHashtableEntry[j]; localIntHashtableEntry != null; localIntHashtableEntry = localIntHashtableEntry.next)
      if ((localIntHashtableEntry.hash == i) && (localIntHashtableEntry.key == paramInt))
        return true;
    return false;
  }

  public synchronized Enumeration<Object> elements()
  {
    return new IntHashtableEnumerator(this.table, false);
  }

  public synchronized Object get(int paramInt)
  {
    IntHashtableEntry[] arrayOfIntHashtableEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfIntHashtableEntry.length;
    for (IntHashtableEntry localIntHashtableEntry = arrayOfIntHashtableEntry[j]; localIntHashtableEntry != null; localIntHashtableEntry = localIntHashtableEntry.next)
      if ((localIntHashtableEntry.hash == i) && (localIntHashtableEntry.key == paramInt))
        return localIntHashtableEntry.value;
    return null;
  }

  public Object get(Object paramObject)
  {
    if (!(paramObject instanceof Integer))
      throw new InternalError("key is not an Integer");
    Integer localInteger = (Integer)paramObject;
    int i = localInteger.intValue();
    return get(i);
  }

  public synchronized Enumeration<Object> keys()
  {
    return new IntHashtableEnumerator(this.table, true);
  }

  public synchronized Object put(int paramInt, Object paramObject)
  {
    if (paramObject == null)
      throw new NullPointerException();
    IntHashtableEntry[] arrayOfIntHashtableEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfIntHashtableEntry.length;
    for (IntHashtableEntry localIntHashtableEntry = arrayOfIntHashtableEntry[j]; localIntHashtableEntry != null; localIntHashtableEntry = localIntHashtableEntry.next)
      if ((localIntHashtableEntry.hash == i) && (localIntHashtableEntry.key == paramInt))
      {
        Object localObject = localIntHashtableEntry.value;
        localIntHashtableEntry.value = paramObject;
        return localObject;
      }
    if (this.count >= this.threshold)
    {
      rehash();
      return put(paramInt, paramObject);
    }
    localIntHashtableEntry = new IntHashtableEntry();
    localIntHashtableEntry.hash = i;
    localIntHashtableEntry.key = paramInt;
    localIntHashtableEntry.value = paramObject;
    localIntHashtableEntry.next = arrayOfIntHashtableEntry[j];
    arrayOfIntHashtableEntry[j] = localIntHashtableEntry;
    this.count += 1;
    return null;
  }

  public Object put(Object paramObject1, Object paramObject2)
  {
    if (!(paramObject1 instanceof Integer))
      throw new InternalError("key is not an Integer");
    Integer localInteger = (Integer)paramObject1;
    int i = localInteger.intValue();
    return put(i, paramObject2);
  }

  public synchronized Object remove(int paramInt)
  {
    IntHashtableEntry[] arrayOfIntHashtableEntry = this.table;
    int i = paramInt;
    int j = (i & 0x7FFFFFFF) % arrayOfIntHashtableEntry.length;
    IntHashtableEntry localIntHashtableEntry1 = arrayOfIntHashtableEntry[j];
    IntHashtableEntry localIntHashtableEntry2 = null;
    while (localIntHashtableEntry1 != null)
    {
      if ((localIntHashtableEntry1.hash == i) && (localIntHashtableEntry1.key == paramInt))
      {
        if (localIntHashtableEntry2 != null)
          localIntHashtableEntry2.next = localIntHashtableEntry1.next;
        else
          arrayOfIntHashtableEntry[j] = localIntHashtableEntry1.next;
        this.count -= 1;
        return localIntHashtableEntry1.value;
      }
      localIntHashtableEntry2 = localIntHashtableEntry1;
      localIntHashtableEntry1 = localIntHashtableEntry1.next;
    }
    return null;
  }

  public Object remove(Object paramObject)
  {
    if (!(paramObject instanceof Integer))
      throw new InternalError("key is not an Integer");
    Integer localInteger = (Integer)paramObject;
    int i = localInteger.intValue();
    return remove(i);
  }

  public int size()
  {
    return this.count;
  }

  public synchronized String toString()
  {
    int i = size() - 1;
    StringBuilder localStringBuilder = new StringBuilder();
    Enumeration localEnumeration1 = keys();
    Enumeration localEnumeration2 = elements();
    localStringBuilder.append("{");
    for (int j = 0; j <= i; j++)
    {
      String str1 = localEnumeration1.nextElement().toString();
      String str2 = localEnumeration2.nextElement().toString();
      localStringBuilder.append(str1 + "=" + str2);
      if (j < i)
        localStringBuilder.append(", ");
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }

  protected void rehash()
  {
    int i = this.table.length;
    IntHashtableEntry[] arrayOfIntHashtableEntry1 = this.table;
    int j = i * 2 + 1;
    IntHashtableEntry[] arrayOfIntHashtableEntry2 = new IntHashtableEntry[j];
    this.threshold = (int)(j * this.loadFactor);
    this.table = arrayOfIntHashtableEntry2;
    int k = i;
    while (k-- > 0)
    {
      IntHashtableEntry localIntHashtableEntry1 = arrayOfIntHashtableEntry1[k];
      while (localIntHashtableEntry1 != null)
      {
        IntHashtableEntry localIntHashtableEntry2 = localIntHashtableEntry1;
        localIntHashtableEntry1 = localIntHashtableEntry1.next;
        int m = (localIntHashtableEntry2.hash & 0x7FFFFFFF) % j;
        localIntHashtableEntry2.next = arrayOfIntHashtableEntry2[m];
        arrayOfIntHashtableEntry2[m] = localIntHashtableEntry2;
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.border.IntHashtable
 * JD-Core Version:    0.6.1
 */