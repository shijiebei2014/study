package com.remedy.arsys.goat.intf.service;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.GoatException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract interface IFormFieldService
{
  public abstract Field[] getFields(String paramString1, String paramString2, Integer[] paramArrayOfInteger)
    throws GoatException;

  public abstract Field[] getFieldsAsAdmin(String paramString1, String paramString2, int paramInt)
    throws GoatException;

  public abstract Field getField(String paramString1, String paramString2, int paramInt);

  public abstract List<List<Integer>> categorizeLocalRemoteFields(String paramString1, String paramString2, List<Integer> paramList);

  public abstract Map<Integer, String> getHelpText(String paramString1, String paramString2, Set<Integer> paramSet)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IFormFieldService
 * JD-Core Version:    0.6.1
 */