package com.remedy.arsys.prefetch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CacheFetchManager
{
  private PrefetchManager mPrefetchManager;

  public CacheFetchManager(Set<PrefetchWorker.Item> paramSet)
  {
    Set localSet1 = ViewInfoCollector.getViewUsageSet();
    Set localSet2 = buildViewSet(localSet1, paramSet);
    this.mPrefetchManager = new PrefetchManager(null, paramSet);
    if (!localSet2.isEmpty())
      this.mPrefetchManager.setSequentialManager(new ViewBuildingManager(localSet2, localSet2.size()));
  }

  public void start(boolean paramBoolean)
  {
    this.mPrefetchManager.start(paramBoolean);
  }

  private static Set<PrefetchWorker.Item> buildViewSet(Set<PrefetchWorker.Item> paramSet1, Set<PrefetchWorker.Item> paramSet2)
  {
    HashMap localHashMap = new HashMap();
    HashSet localHashSet = new HashSet();
    String str = null;
    PrefetchWorker.Item localItem = null;
    Iterator localIterator = paramSet1.iterator();
    while (localIterator.hasNext())
    {
      localItem = (PrefetchWorker.Item)localIterator.next();
      if (localItem.getUsername() != null)
      {
        str = getKey(localItem);
        addUserToList(localHashMap, str, localItem.getUsername());
      }
    }
    localIterator = paramSet2.iterator();
    while (localIterator.hasNext())
    {
      localItem = (PrefetchWorker.Item)localIterator.next();
      if (localItem.getViewname() != null)
      {
        str = getKey(localItem);
        createAndAddItemsWithView(localHashMap, str, localItem, localHashSet);
      }
    }
    return localHashSet;
  }

  private static void addUserToList(HashMap<String, ArrayList<String>> paramHashMap, String paramString1, String paramString2)
  {
    ArrayList localArrayList = (ArrayList)paramHashMap.get(paramString1);
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      paramHashMap.put(paramString1, localArrayList);
    }
    localArrayList.add(paramString2);
  }

  private static void createAndAddItemsWithView(HashMap<String, ArrayList<String>> paramHashMap, String paramString, PrefetchWorker.Item paramItem, Set<PrefetchWorker.Item> paramSet)
  {
    ArrayList localArrayList = (ArrayList)paramHashMap.get(paramString);
    String str = null;
    if (localArrayList != null)
      for (int i = 0; i < localArrayList.size(); i++)
      {
        str = (String)localArrayList.get(i);
        paramSet.add(PrefetchManager.createItem(paramItem.getServer(), paramItem.getSchema(), str, paramItem.getLocale(), paramItem.getTimezone(), paramItem.getViewname()));
      }
  }

  private static String getKey(PrefetchWorker.Item paramItem)
  {
    return (paramItem.getViewname() + paramItem.getSchema() + paramItem.getServer()).intern();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.CacheFetchManager
 * JD-Core Version:    0.6.1
 */