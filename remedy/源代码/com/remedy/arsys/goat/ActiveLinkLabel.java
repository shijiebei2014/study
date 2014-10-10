package com.remedy.arsys.goat;

public class ActiveLinkLabel
  implements ActiveLinkInfo
{
  private static final long serialVersionUID = -8924576753002005772L;
  private String mName;

  public ActiveLinkLabel(String paramString)
  {
    this.mName = paramString;
  }

  public long getExecutionOrder()
  {
    return -1L;
  }

  public boolean hasGoto()
  {
    return true;
  }

  public boolean canTerminate()
  {
    return false;
  }

  public boolean isInterruptible()
  {
    return false;
  }

  public String getJSFunctionName()
  {
    return this.mName;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ActiveLinkLabel
 * JD-Core Version:    0.6.1
 */