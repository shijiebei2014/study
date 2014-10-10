package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.GoatException;
import java.util.List;

public abstract interface IActiveLinkService
{
  public abstract ActiveLink get(String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException;

  public abstract List<ActiveLink> get(String paramString1, String paramString2, String[] paramArrayOfString, String paramString3, String paramString4)
    throws GoatException;

  public abstract List<ActiveLink> getAsAdmin(String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IActiveLinkService
 * JD-Core Version:    0.6.1
 */