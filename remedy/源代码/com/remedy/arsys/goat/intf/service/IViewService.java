package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;

public abstract interface IViewService
{
  public abstract void preCache(FieldGraph paramFieldGraph)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IViewService
 * JD-Core Version:    0.6.1
 */