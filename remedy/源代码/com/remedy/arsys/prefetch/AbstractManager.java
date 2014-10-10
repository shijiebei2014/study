package com.remedy.arsys.prefetch;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractManager
{
  protected static int MAX_PREFETCH_THREADS = 4;
  protected static int DAEMON_THREAD_PRIORITY = localConfiguration.getDaemonThreadPriority();
  protected static Log MPerformanceLog = Log.get(8);
  protected Iterator<PrefetchWorker.Item> mItemSet;
  protected HashMap<String, List<String>> svrformToALMap;
  protected ArrayList<WorkerThread> mWorkerThreads;
  protected volatile int mThreadCount = MAX_PREFETCH_THREADS;
  private final boolean mIsDaemon;
  private boolean mStopOnError = false;
  private AbstractManager mSequentialManager;
  private final ArrayList<String> mNAserverList;

  public AbstractManager(Set<PrefetchWorker.Item> paramSet, boolean paramBoolean)
  {
    this.mItemSet = paramSet.iterator();
    this.mIsDaemon = paramBoolean;
    if (paramSet != null)
      this.mThreadCount = Math.min(paramSet.size(), MAX_PREFETCH_THREADS);
    this.mWorkerThreads = new ArrayList(this.mThreadCount);
    List localList = Configuration.getInstance().getServers();
    this.mNAserverList = new ArrayList(localList.size());
  }

  public AbstractManager(Set<PrefetchWorker.Item> paramSet, boolean paramBoolean, HashMap<String, List<String>> paramHashMap)
  {
    this.mItemSet = paramSet.iterator();
    this.mIsDaemon = paramBoolean;
    this.svrformToALMap = paramHashMap;
    if (paramSet != null)
      this.mThreadCount = Math.min(paramSet.size(), MAX_PREFETCH_THREADS);
    this.mWorkerThreads = new ArrayList(this.mThreadCount);
    List localList = Configuration.getInstance().getServers();
    this.mNAserverList = new ArrayList(localList.size());
  }

  public AbstractManager(Set<PrefetchWorker.Item> paramSet, int paramInt, boolean paramBoolean)
  {
    this.mItemSet = paramSet.iterator();
    this.mIsDaemon = paramBoolean;
    this.mThreadCount = paramInt;
    if (paramSet != null)
      this.mThreadCount = Math.min(paramSet.size(), MAX_PREFETCH_THREADS);
    this.mWorkerThreads = new ArrayList(this.mThreadCount);
    List localList = Configuration.getInstance().getServers();
    this.mNAserverList = new ArrayList(localList.size());
  }

  public abstract PrefetchWorker createWorker(AbstractManager paramAbstractManager);

  public static PrefetchWorker.Item createItem(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return PrefetchWorkerImpl.create(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6);
  }

  public synchronized void addToNAserverList(String paramString)
  {
    if (!this.mNAserverList.contains(paramString))
      this.mNAserverList.add(paramString);
  }

  public synchronized PrefetchWorker.Item getNextItem()
  {
    while (this.mItemSet.hasNext())
    {
      PrefetchWorker.Item localItem = (PrefetchWorker.Item)this.mItemSet.next();
      if (!this.mNAserverList.contains(localItem.getServer()))
        return localItem;
    }
    return null;
  }

  public HashMap<String, List<String>> getSvrFormToALMap()
  {
    return this.svrformToALMap;
  }

  public void start(boolean paramBoolean)
  {
    this.mStopOnError = paramBoolean;
    for (int i = 0; i < this.mThreadCount; i++)
    {
      WorkerThread localWorkerThread = new WorkerThread(this);
      if (this.mIsDaemon)
        localWorkerThread.setPriority(DAEMON_THREAD_PRIORITY);
      this.mWorkerThreads.add(localWorkerThread);
      localWorkerThread.setStopAtFirstError(paramBoolean);
      localWorkerThread.start();
    }
  }

  public void stop()
  {
    for (int i = 0; i < this.mThreadCount; i++)
      ((WorkerThread)this.mWorkerThreads.get(i)).gracefulStop();
  }

  protected boolean threadDone()
  {
    synchronized (this)
    {
      this.mThreadCount -= 1;
    }
    if (this.mThreadCount == 0)
    {
      if (this.mSequentialManager != null)
        this.mSequentialManager.start(this.mStopOnError);
      return true;
    }
    return false;
  }

  public AbstractManager getSequentialManager()
  {
    return this.mSequentialManager;
  }

  public void setSequentialManager(AbstractManager paramAbstractManager)
  {
    this.mSequentialManager = paramAbstractManager;
  }

  protected int getThreadCount()
  {
    return this.mThreadCount;
  }

  static
  {
    Configuration localConfiguration = Configuration.getInstance();
    int i = localConfiguration.getMaxNumberPrefetchThreads();
    if ((i != MAX_PREFETCH_THREADS) && (MAX_PREFETCH_THREADS > 0) && (MAX_PREFETCH_THREADS < 100))
      MAX_PREFETCH_THREADS = i;
  }

  private class WorkerThread extends Thread
  {
    private final AbstractManager mManager;
    private final PrefetchWorker mWorker;
    private boolean mStopAtFirstError = false;

    WorkerThread(AbstractManager arg2)
    {
      AbstractManager localAbstractManager;
      this.mManager = localAbstractManager;
      this.mWorker = AbstractManager.this.createWorker(localAbstractManager);
    }

    public void run()
    {
      try
      {
        this.mWorker.start(this.mStopAtFirstError);
      }
      finally
      {
        this.mManager.threadDone();
      }
    }

    public void setStopAtFirstError(boolean paramBoolean)
    {
      this.mStopAtFirstError = paramBoolean;
    }

    public void gracefulStop()
    {
      this.mWorker.stop();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.AbstractManager
 * JD-Core Version:    0.6.1
 */