package com.remedy.arsys.goat.intf.service;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.GoatField;

public abstract interface IGoatFieldFactory
{
  public abstract GoatField create(Form paramForm, int paramInt, Field paramField)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IGoatFieldFactory
 * JD-Core Version:    0.6.1
 */