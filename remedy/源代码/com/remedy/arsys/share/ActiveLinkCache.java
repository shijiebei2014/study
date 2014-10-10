package com.remedy.arsys.share;

import com.remedy.arsys.prefetch.PrefetchTask;

public class ActiveLinkCache extends Cachetable
{
  private static int count = 200;
  private static final int bulkSize = 200;

  public ActiveLinkCache(String paramString)
  {
    super(paramString);
  }

  public void put(String paramString, Cache.Item paramItem)
  {
    count += 1;
    super.put(paramString, paramItem);
  }

  public ActiveLinkCache(String paramString, int paramInt1, int paramInt2)
  {
    super(paramString, paramInt1, paramInt2);
  }

  public void saveOnSizeChange()
  {
    save();
  }

  public void save()
  {
    if (PrefetchTask.isPreloadTaskRunning())
      return;
    if (count >= 200)
    {
      super.save();
      count = 0;
    }
  }

  public void forceSave()
  {
    super.save();
    count = 0;
  }

  public static void resetActiveLinkCount()
  {
    count = 200;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ActiveLinkCache
 * JD-Core Version:    0.6.1
 */