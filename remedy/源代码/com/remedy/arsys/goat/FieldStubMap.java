package com.remedy.arsys.goat;

import com.bmc.arsys.api.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class FieldStubMap extends HashMap<String, Field>
{
  private static final long serialVersionUID = -5292379331914897797L;
  protected String server;
  protected String formName;
  protected Map<String, Integer> fieldMap;

  public FieldStubMap(String paramString1, String paramString2)
  {
    this.server = paramString1;
    this.formName = paramString2;
    this.fieldMap = new HashMap();
  }

  public void clear()
  {
    this.fieldMap.clear();
  }

  public boolean containsKey(Object paramObject)
  {
    return this.fieldMap.containsKey(paramObject);
  }

  public boolean containsValue(Object paramObject)
  {
    if (!(paramObject instanceof Field))
      return false;
    Field localField = (Field)paramObject;
    int i = localField.getFieldID();
    Collection localCollection = this.fieldMap.values();
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      if (localInteger.intValue() == i)
        return true;
    }
    return false;
  }

  public Set<Map.Entry<String, Field>> entrySet()
  {
    try
    {
      HashSet localHashSet = new HashSet();
      if (this.fieldMap.isEmpty())
        return localHashSet;
      Form localForm = Form.get(this.server, this.formName);
      CachedFieldMap localCachedFieldMap = localForm.getCachedFieldMap();
      Set localSet = this.fieldMap.entrySet();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        int i = ((Integer)localEntry.getValue()).intValue();
        Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(i));
        localHashSet.add(new Entry((String)localEntry.getKey(), localField));
      }
      return localHashSet;
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
    }
    return new HashSet();
  }

  public Field get(Object paramObject)
  {
    try
    {
      Integer localInteger = (Integer)this.fieldMap.get(paramObject);
      if (localInteger == null)
        return null;
      Form localForm = Form.get(this.server, this.formName);
      CachedFieldMap localCachedFieldMap = localForm.getCachedFieldMap();
      return (Field)localCachedFieldMap.get(localInteger);
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
    }
    return null;
  }

  public boolean isEmpty()
  {
    return this.fieldMap.isEmpty();
  }

  public Set<String> keySet()
  {
    return this.fieldMap.keySet();
  }

  public Field put(String paramString, Field paramField)
  {
    Field localField = get(paramString);
    this.fieldMap.put(paramString, Integer.valueOf(paramField.getFieldID()));
    return localField;
  }

  public void putAll(Map<? extends String, ? extends Field> paramMap)
  {
    Set localSet = paramMap.entrySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      Map.Entry localEntry = (Map.Entry)localObject;
      this.fieldMap.put(localEntry.getKey(), Integer.valueOf(((Field)localEntry.getValue()).getFieldID()));
    }
  }

  public Field remove(Object paramObject)
  {
    Field localField = get(paramObject);
    this.fieldMap.remove(paramObject);
    return localField;
  }

  public int size()
  {
    return this.fieldMap.size();
  }

  public Collection<Field> values()
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      if (this.fieldMap.isEmpty())
        return localArrayList;
      Form localForm = Form.get(this.server, this.formName);
      CachedFieldMap localCachedFieldMap = localForm.getCachedFieldMap();
      Collection localCollection = this.fieldMap.values();
      Iterator localIterator = localCollection.iterator();
      while (localIterator.hasNext())
      {
        Integer localInteger = (Integer)localIterator.next();
        Field localField = (Field)localCachedFieldMap.get(localInteger);
        localArrayList.add(localField);
      }
      return localArrayList;
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
    }
    return new ArrayList();
  }

  public static final class Entry
    implements Map.Entry<String, Field>
  {
    protected String key;
    protected Field value;

    public Entry(String paramString, Field paramField)
    {
      this.key = paramString;
      this.value = paramField;
    }

    public String getKey()
    {
      return this.key;
    }

    public Field getValue()
    {
      return this.value;
    }

    public Field setValue(Field paramField)
    {
      Field localField = this.value;
      this.value = paramField;
      return localField;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.FieldStubMap
 * JD-Core Version:    0.6.1
 */