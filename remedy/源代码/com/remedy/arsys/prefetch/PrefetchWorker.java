package com.remedy.arsys.prefetch;

public abstract interface PrefetchWorker
{
  public abstract void stop();

  public abstract void start(boolean paramBoolean);

  public static abstract interface Item
  {
    public abstract String getServer();

    public abstract String getSchema();

    public abstract String getViewname();

    public abstract String getUsername();

    public abstract String getLocale();

    public abstract String getTimezone();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.PrefetchWorker
 * JD-Core Version:    0.6.1
 */