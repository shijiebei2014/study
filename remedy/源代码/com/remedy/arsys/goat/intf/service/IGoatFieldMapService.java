package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.GoatFieldMap;
import java.util.Map;

public abstract interface IGoatFieldMapService
{
  public abstract GoatFieldMap getGoatFieldMap(Form paramForm, Form.ViewInfo paramViewInfo, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException;

  public abstract GoatFieldMap getGoatFieldMap(Form paramForm, Form.ViewInfo paramViewInfo, boolean paramBoolean)
    throws GoatException;

  public abstract GoatFieldMap getGoatFieldMap(Form paramForm, Form.ViewInfo paramViewInfo, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException;

  public abstract boolean initHelpText(Form paramForm, Map paramMap)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IGoatFieldMapService
 * JD-Core Version:    0.6.1
 */