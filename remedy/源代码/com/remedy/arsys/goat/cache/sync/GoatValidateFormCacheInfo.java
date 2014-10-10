package com.remedy.arsys.goat.cache.sync;

import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.ValidateFormCacheInfo;
import com.remedy.arsys.share.Cache.Item;
import java.util.List;

public class GoatValidateFormCacheInfo
  implements Cache.Item
{
  private static final long serialVersionUID = 2162969591518755539L;
  private ValidateFormCacheInfo vfcInfo;
  private Timestamp activelinkLastModified;
  private Timestamp menuLastChanged;
  private Timestamp guideLastChanged;
  private String mServer;
  private List<String> mMenuList;

  public GoatValidateFormCacheInfo(ValidateFormCacheInfo paramValidateFormCacheInfo, String paramString)
  {
    this.vfcInfo = paramValidateFormCacheInfo;
    this.mServer = paramString;
  }

  public Timestamp getActivelinkLastModified()
  {
    return this.activelinkLastModified;
  }

  public void setActivelinkLastModified(Timestamp paramTimestamp)
  {
    this.activelinkLastModified = paramTimestamp;
  }

  public Timestamp getMenuLastModified()
  {
    return this.menuLastChanged;
  }

  public void setMenuLastModified(Timestamp paramTimestamp)
  {
    this.menuLastChanged = paramTimestamp;
  }

  public Timestamp getGuideLastModified()
  {
    return this.guideLastChanged;
  }

  public void setGuideLastModified(Timestamp paramTimestamp)
  {
    this.guideLastChanged = paramTimestamp;
  }

  public void setMenuList(List<String> paramList)
  {
    this.mMenuList = paramList;
  }

  public List<String> getMenuList()
  {
    return this.mMenuList;
  }

  public Timestamp getFormLastModified()
  {
    try
    {
      return this.vfcInfo.getFormLastModified();
    }
    catch (Exception localException)
    {
    }
    return new Timestamp(0L);
  }

  public int getNumActLinkOnForm()
  {
    try
    {
      return this.vfcInfo.getNumActLinkOnForm();
    }
    catch (Exception localException)
    {
    }
    return 0;
  }

  public int getNumActLinkSince()
  {
    try
    {
      return this.vfcInfo.getNumActLinkSince();
    }
    catch (Exception localException)
    {
    }
    return 0;
  }

  public List<String> getMenuSinceList()
  {
    try
    {
      return this.vfcInfo.getMenuSinceList();
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public Timestamp getGroupsLastChanged()
  {
    try
    {
      return this.vfcInfo.getGroupsLastChanged();
    }
    catch (Exception localException)
    {
    }
    return new Timestamp(0L);
  }

  public Timestamp getUserLastChanged()
  {
    try
    {
      return this.vfcInfo.getUserLastChanged();
    }
    catch (Exception localException)
    {
    }
    return new Timestamp(0L);
  }

  public List<String> getGuideSinceList()
  {
    try
    {
      return this.vfcInfo.getGuideSinceList();
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  public void cleanUp()
  {
    this.vfcInfo = null;
    this.activelinkLastModified = null;
    this.menuLastChanged = null;
    this.guideLastChanged = null;
    this.mServer = null;
    if (this.mMenuList != null)
    {
      this.mMenuList.clear();
      this.mMenuList = null;
    }
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof GoatValidateFormCacheInfo))
      return false;
    if (this == paramObject)
      return true;
    GoatValidateFormCacheInfo localGoatValidateFormCacheInfo = (GoatValidateFormCacheInfo)paramObject;
    return (this.vfcInfo.equals(localGoatValidateFormCacheInfo.vfcInfo)) && (this.activelinkLastModified.equals(localGoatValidateFormCacheInfo.activelinkLastModified)) && (this.guideLastChanged.equals(localGoatValidateFormCacheInfo.guideLastChanged)) && (this.menuLastChanged.equals(localGoatValidateFormCacheInfo.menuLastChanged));
  }

  public int hashCode()
  {
    return this.vfcInfo.hashCode();
  }

  public String toString()
  {
    return this.vfcInfo.toString();
  }

  public String getServer()
  {
    return this.mServer;
  }

  public int getSize()
  {
    return 1;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.cache.sync.GoatValidateFormCacheInfo
 * JD-Core Version:    0.6.1
 */