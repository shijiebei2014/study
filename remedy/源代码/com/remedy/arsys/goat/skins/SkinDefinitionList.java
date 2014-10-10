package com.remedy.arsys.goat.skins;

import com.remedy.arsys.share.Cache.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SkinDefinitionList
  implements Cache.Item
{
  private final ArrayList<String> skinDefList = new ArrayList();
  private final Map<String, Long> skinDefListTimeStamp = new HashMap();
  private final Map<String, ArrayList<String>> skinFormList = new HashMap();
  private String server;

  public String getServer()
  {
    return this.server;
  }

  public void setServer(String paramString)
  {
    this.server = paramString;
  }

  public int getSize()
  {
    if (this.skinDefList == null)
      return 0;
    return this.skinDefList.size();
  }

  public void addSkin(String paramString, long paramLong)
  {
    this.skinDefList.add(paramString);
    this.skinDefListTimeStamp.put(paramString, Long.valueOf(paramLong));
  }

  public void addForm(String paramString1, String paramString2)
  {
    ArrayList localArrayList = (ArrayList)this.skinFormList.get(paramString1);
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      this.skinFormList.put(paramString1, localArrayList);
    }
    if (!localArrayList.contains(paramString2))
      localArrayList.add(paramString2);
  }

  public ArrayList<String> getSkins()
  {
    return this.skinDefList;
  }

  public long getModifiedTime(String paramString)
  {
    Long localLong = (Long)this.skinDefListTimeStamp.get(paramString);
    if (localLong != null)
      return localLong.longValue();
    return 0L;
  }

  public ArrayList<String> getFormList(String paramString)
  {
    return (ArrayList)this.skinFormList.get(paramString);
  }

  public void addForm(String paramString, ArrayList<String> paramArrayList)
  {
    ArrayList localArrayList = (ArrayList)this.skinFormList.get(paramString);
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      this.skinFormList.put(paramString, localArrayList);
    }
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!localArrayList.contains(str))
        localArrayList.add(str);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.skins.SkinDefinitionList
 * JD-Core Version:    0.6.1
 */