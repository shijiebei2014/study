package com.remedy.arsys.goat;

import com.bmc.arsys.api.EnumItem;
import com.bmc.arsys.api.SelectionFieldLimit;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumLimit
  implements Serializable
{
  private static final long serialVersionUID = 4993901911916631456L;
  private String[] mEnumLabels = new String[0];
  private SelectionFieldLimit mLimitStruct;
  public static final long NO_MATCH_LONG = 9223372036854775807L;

  public EnumLimit(SelectionFieldLimit paramSelectionFieldLimit)
  {
    this.mLimitStruct = paramSelectionFieldLimit;
  }

  public int getEnumStyle()
  {
    return this.mLimitStruct.getListStyle();
  }

  public int size()
  {
    return this.mLimitStruct.getValues().size();
  }

  public String valFromIndex(int paramInt)
  {
    String[] arrayOfString = getEnumValues();
    if ((paramInt < 0) || (paramInt > arrayOfString.length))
      return "";
    return arrayOfString[paramInt];
  }

  public int valToIndex(long paramLong)
  {
    int i = this.mLimitStruct.getListStyle();
    int j = -1;
    if (i == 1)
      return (int)paramLong;
    EnumItem[] arrayOfEnumItem = (EnumItem[])this.mLimitStruct.getValues().toArray(new EnumItem[0]);
    for (int k = 0; k < arrayOfEnumItem.length; k++)
      if (arrayOfEnumItem[k].getEnumItemNumber() == paramLong)
      {
        j = k;
        break;
      }
    return j;
  }

  public String valToString(long paramLong)
  {
    EnumItem[] arrayOfEnumItem = (EnumItem[])this.mLimitStruct.getValues().toArray(new EnumItem[0]);
    String str = "";
    for (int i = 0; i < arrayOfEnumItem.length; i++)
      if (arrayOfEnumItem[i].getEnumItemNumber() == paramLong)
      {
        str = arrayOfEnumItem[i].getEnumItemName();
        break;
      }
    return str;
  }

  public long stringToVal(String paramString)
  {
    EnumItem[] arrayOfEnumItem = (EnumItem[])this.mLimitStruct.getValues().toArray(new EnumItem[0]);
    String[] arrayOfString = new String[arrayOfEnumItem.length];
    long[] arrayOfLong = new long[arrayOfEnumItem.length];
    for (int i = 0; i < arrayOfEnumItem.length; i++)
    {
      arrayOfString[i] = arrayOfEnumItem[i].getEnumItemName();
      arrayOfLong[i] = arrayOfEnumItem[i].getEnumItemNumber();
    }
    for (i = 0; i < arrayOfString.length; i++)
      if (paramString.equals(arrayOfString[i]))
        return arrayOfLong[i];
    return 9223372036854775807L;
  }

  public boolean isValidValue(long paramLong)
  {
    EnumItem[] arrayOfEnumItem = (EnumItem[])this.mLimitStruct.getValues().toArray(new EnumItem[0]);
    long[] arrayOfLong = new long[arrayOfEnumItem.length];
    for (int i = 0; i < arrayOfEnumItem.length; i++)
      arrayOfLong[i] = arrayOfEnumItem[i].getEnumItemNumber();
    for (i = 0; i < arrayOfLong.length; i++)
      if (arrayOfLong[i] == paramLong)
        return true;
    return false;
  }

  public Map getEnumValueMap()
  {
    HashMap localHashMap = new HashMap();
    long[] arrayOfLong = getEnumIds();
    String[] arrayOfString = getEnumValues();
    for (int i = 0; i < arrayOfLong.length; i++)
      localHashMap.put(new Long(arrayOfLong[i]).toString(), arrayOfString[i]);
    return Collections.unmodifiableMap(localHashMap);
  }

  private long[] getEnumIds()
  {
    EnumItem[] arrayOfEnumItem = (EnumItem[])this.mLimitStruct.getValues().toArray(new EnumItem[0]);
    long[] arrayOfLong = new long[arrayOfEnumItem.length];
    for (int i = 0; i < arrayOfEnumItem.length; i++)
      arrayOfLong[i] = arrayOfEnumItem[i].getEnumItemNumber();
    return arrayOfLong;
  }

  private String[] getEnumValues()
  {
    EnumItem[] arrayOfEnumItem = (EnumItem[])this.mLimitStruct.getValues().toArray(new EnumItem[0]);
    String[] arrayOfString = new String[arrayOfEnumItem.length];
    for (int i = 0; i < arrayOfEnumItem.length; i++)
      arrayOfString[i] = arrayOfEnumItem[i].getEnumItemName();
    return arrayOfString;
  }

  public String[] getMEnumLabels()
  {
    return this.mEnumLabels;
  }

  public void setMEnumLabels(String[] paramArrayOfString)
  {
    this.mEnumLabels = new String[paramArrayOfString.length];
    this.mEnumLabels = paramArrayOfString;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.EnumLimit
 * JD-Core Version:    0.6.1
 */