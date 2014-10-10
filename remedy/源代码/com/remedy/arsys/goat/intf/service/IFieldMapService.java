package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;

public abstract interface IFieldMapService
{
  public abstract CachedFieldMap getFieldMap(Form paramForm)
    throws GoatException;

  public abstract CachedFieldMap getFieldMap(Form paramForm, boolean paramBoolean)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IFieldMapService
 * JD-Core Version:    0.6.1
 */