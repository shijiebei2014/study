package com.remedy.arsys.goat;

import java.io.Serializable;

public abstract interface ActiveLinkInfo extends Serializable
{
  public abstract long getExecutionOrder();

  public abstract boolean hasGoto();

  public abstract boolean canTerminate();

  public abstract boolean isInterruptible();

  public abstract String getJSFunctionName();
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ActiveLinkInfo
 * JD-Core Version:    0.6.1
 */