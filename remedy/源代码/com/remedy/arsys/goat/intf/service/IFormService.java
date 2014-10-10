package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;

public abstract interface IFormService
{
  public abstract Form getForm(String paramString1, String paramString2)
    throws GoatException;

  public abstract Form getForm(String paramString1, String paramString2, boolean paramBoolean)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IFormService
 * JD-Core Version:    0.6.1
 */