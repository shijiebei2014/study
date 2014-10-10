package com.remedy.arsys.prefetch;

import java.util.Set;

public class ViewBuildingManager extends AbstractManager
{
  private int mCurrentCount = 0;
  private int mCurrentCeiling = -1;

  public ViewBuildingManager(Set<PrefetchWorker.Item> paramSet)
  {
    super(paramSet, 1, true);
    this.mCurrentCeiling = ViewInfoCollector.getStatCeilingCount();
  }

  public ViewBuildingManager(Set<PrefetchWorker.Item> paramSet, int paramInt)
  {
    super(paramSet, 1, true);
    this.mCurrentCeiling = Math.max(ViewInfoCollector.getStatCeilingCount(), paramInt);
  }

  public PrefetchWorker createWorker(AbstractManager paramAbstractManager)
  {
    return new PrefetchWorkerImpl(paramAbstractManager, true);
  }

  public synchronized PrefetchWorker.Item getNextItem()
  {
    if (this.mCurrentCount >= this.mCurrentCeiling)
      return null;
    this.mCurrentCount += 1;
    return super.getNextItem();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.ViewBuildingManager
 * JD-Core Version:    0.6.1
 */