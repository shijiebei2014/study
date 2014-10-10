package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import java.util.Map;

public abstract interface IGoatFieldService
{
  public abstract Map get(Form.ViewInfo paramViewInfo)
    throws GoatException;

  public abstract Map get(Form.ViewInfo paramViewInfo, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IGoatFieldService
 * JD-Core Version:    0.6.1
 */