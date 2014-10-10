package com.remedy.arsys.goat.skins;

import com.bmc.arsys.api.Value;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class SkinDefinitionMap
{
  private LinkedHashMap<String, Skin> applicableSkins;

  public void addSkin(String paramString, Skin paramSkin)
  {
    if (this.applicableSkins == null)
      this.applicableSkins = new LinkedHashMap();
    this.applicableSkins.put(paramString, paramSkin);
  }

  public int getSize()
  {
    if (this.applicableSkins != null)
      return this.applicableSkins.size();
    return 0;
  }

  public LinkedHashMap<String, Skin> getApplicableSkins()
  {
    return this.applicableSkins;
  }

  public Value getProperty(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if ((this.applicableSkins == null) || (this.applicableSkins.size() == 0))
      return null;
    int i = this.applicableSkins.size();
    Value localValue = null;
    ArrayList localArrayList = new ArrayList(this.applicableSkins.values());
    for (int j = 0; j < i; j++)
    {
      Skin localSkin = (Skin)localArrayList.get(j);
      if (localSkin != null)
      {
        localValue = localSkin.getPropertyvalue(paramString1, paramString2, paramString3, paramInt);
        if (localValue != null)
          return localValue;
      }
    }
    return localValue;
  }

  public Skin getSkin(String paramString)
  {
    if (this.applicableSkins != null)
      return (Skin)this.applicableSkins.get(paramString);
    return null;
  }

  public Value getProperty(String paramString1, String paramString2, short paramShort)
  {
    if ((this.applicableSkins == null) || (this.applicableSkins.size() == 0))
      return null;
    Value localValue = null;
    Collection localCollection = this.applicableSkins.values();
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      Skin localSkin = (Skin)localIterator.next();
      if (localSkin != null)
      {
        localValue = localSkin.getPropertyvalue(paramString1, paramString2, paramShort);
        if (localValue != null)
          return localValue;
      }
    }
    return localValue;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.skins.SkinDefinitionMap
 * JD-Core Version:    0.6.1
 */