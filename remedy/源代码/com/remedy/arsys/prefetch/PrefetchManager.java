package com.remedy.arsys.prefetch;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.GoatCacheManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;

public class PrefetchManager extends AbstractManager
{
  private PrefetchTask mParentTask;
  private long mStartTime = 0L;
  private boolean mIsLoadActiveLink = true;

  public PrefetchManager(PrefetchTask paramPrefetchTask, Set<PrefetchWorker.Item> paramSet)
  {
    super(roundRobin(paramSet), false);
    this.mParentTask = paramPrefetchTask;
  }

  public void start(boolean paramBoolean)
  {
    this.mStartTime = System.currentTimeMillis();
    MPerformanceLog.log(Level.INFO, "START prefetch; start time = " + this.mStartTime);
    super.start(paramBoolean);
  }

  public boolean threadDone()
  {
    if (!super.threadDone())
      return false;
    long l = System.currentTimeMillis();
    MPerformanceLog.log(Level.INFO, "END prefetch; end time = " + l + "; TOTAL(ms) = " + (l - this.mStartTime));
    GoatCacheManager.setDirtyCheckEnable(true);
    if (this.mParentTask != null)
      GoatCacheManager.removePrefetch(this.mParentTask);
    GoatCacheManager.printTotalTime();
    GoatCacheManager.doSave(true);
    return true;
  }

  public static boolean isPrefetchFileValid()
  {
    return ViewInfoCollector.isNewPrefetchFile();
  }

  protected static Set<PrefetchWorker.Item> roundRobin(Set<PrefetchWorker.Item> paramSet)
  {
    assert (paramSet.size() > 0);
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = paramSet.iterator();
    PrefetchWorker.Item localItem = null;
    HashSet localHashSet1 = null;
    while (localIterator1.hasNext())
    {
      localItem = (PrefetchWorker.Item)localIterator1.next();
      if (localItem.getServer() != null)
      {
        localHashSet1 = (HashSet)localHashMap.get(localItem.getServer());
        if (localHashSet1 == null)
        {
          localHashSet1 = new HashSet();
          localHashMap.put(localItem.getServer(), localHashSet1);
        }
        localHashSet1.add(localItem);
      }
    }
    int i = 0;
    HashSet localHashSet2 = null;
    int j = localHashMap.size();
    if (j == 1)
      return paramSet;
    Iterator localIterator2 = localHashMap.keySet().iterator();
    ArrayList localArrayList = new ArrayList(j);
    for (int k = 0; k < j; k++)
    {
      localHashSet2 = (HashSet)localHashMap.get(localIterator2.next());
      i = Math.max(i, localHashSet2.size());
      localArrayList.add(k, localHashSet2.iterator());
    }
    for (k = 0; k < i; k++)
      for (int m = 0; m < j; m++)
        if (((Iterator)localArrayList.get(m)).hasNext())
        {
          localItem = (PrefetchWorker.Item)((Iterator)localArrayList.get(m)).next();
          localLinkedHashSet.add(localItem);
        }
    return localLinkedHashSet;
  }

  public PrefetchWorker createWorker(AbstractManager paramAbstractManager)
  {
    return new PrefetchWorkerImpl(paramAbstractManager, false, this.mIsLoadActiveLink);
  }

  public void setLoadActiveLink(boolean paramBoolean)
  {
    this.mIsLoadActiveLink = paramBoolean;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.PrefetchManager
 * JD-Core Version:    0.6.1
 */