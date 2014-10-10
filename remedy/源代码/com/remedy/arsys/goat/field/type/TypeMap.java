package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DisplayInstanceMap;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.Value;

public class TypeMap
{
  private DataType mType;
  private long mMinId;
  private long mMaxId;
  private Integer mProp;
  private int mPropVal;
  private String mName;
  private boolean mQBE;
  private int mOperandType;
  private int mKeyWdValue;
  private String mKeyWdClassName;
  private static TypeMap[] MMap = { new TypeMap(DataType.ATTACHMENT, -1L, -1L, -1, -1, "Attachment", false, 4, -1, null), new TypeMap(DataType.ATTACHMENT_POOL, -1L, -1L, -1, -1, "AttachmentPool", false, 0, -1, null), new TypeMap(DataType.CHAR, 15L, 15L, -1, -1, "StatusHistory", false, 4, -1, null), new TypeMap(DataType.CHAR, 1005L, 1005L, -1, -1, "SearchBar", false, 4, -1, null), new TypeMap(DataType.CHAR, 104L, 104L, -1, -1, "Group", true, 4, -1, null), new TypeMap(DataType.CHAR, 112L, 112L, -1, -1, "Group", true, 4, -1, null), new TypeMap(DataType.CHAR, 60000L, 60999L, -1, -1, "Group", true, 4, -1, null), new TypeMap(DataType.CHAR, 2000L, 2199L, -1, -1, "Group", true, 4, -1, null), new TypeMap(DataType.CHAR, -1L, -1L, -1, -1, "Char", true, 4, -1, null), new TypeMap(DataType.COLUMN, -1L, -1L, -1, -1, "Column", false, 0, -1, null), new TypeMap(DataType.CONTROL, -1L, -1L, 2, 1, "Control", false, 0, -1, null), new TypeMap(DataType.CONTROL, -1L, -1L, 2, 512, "NavBarItem", false, 0, -1, null), new TypeMap(DataType.CONTROL, -1L, -1L, 2, 128, "HorzNavBar", false, 0, -1, null), new TypeMap(DataType.CONTROL, -1L, -1L, 2, 256, "VertNavBar", false, 0, -1, null), new TypeMap(DataType.CONTROL, -1L, -1L, 2, 2, "MenuItem", false, 0, -1, null), new TypeMap(DataType.CONTROL, -1L, -1L, -1, -1, "Control", false, 0, -1, null), new TypeMap(DataType.CURRENCY, -1L, -1L, -1, -1, "Currency", true, 10, -1, null), new TypeMap(DataType.DATE, -1L, -1L, -1, -1, "Date", true, 8, -1, null), new TypeMap(DataType.DECIMAL, -1L, -1L, -1, -1, "Decimal", true, 6, -1, null), new TypeMap(DataType.DIARY, -1L, -1L, -1, -1, "Diary", true, 4, -1, null), new TypeMap(DataType.DISPLAY, -1L, -1L, -1, -1, "Display", false, 4, -1, null), new TypeMap(DataType.ENUM, -1L, -1L, -1, -1, "Enum", true, 3, -1, null), new TypeMap(DataType.INTEGER, -1L, -1L, -1, -1, "Integer", true, 2, -1, null), new TypeMap(DataType.PAGE, -1L, -1L, -1, -1, "Page", false, 0, -1, null), new TypeMap(DataType.PAGE_HOLDER, -1L, -1L, -1, -1, "PageHolder", false, 4, -1, null), new TypeMap(DataType.REAL, -1L, -1L, -1, -1, "Real", true, 5, -1, null), new TypeMap(DataType.TABLE, 706L, 706L, -1, -1, "AlertList", false, 2, -1, null), new TypeMap(DataType.TABLE, -1L, -1L, -1, -1, "Table", false, 2, -1, null), new TypeMap(DataType.TIME, -1L, -1L, -1, -1, "Time", true, 7, -1, null), new TypeMap(DataType.TIME_OF_DAY, -1L, -1L, -1, -1, "TOD", true, 9, -1, null), new TypeMap(DataType.TRIM, -1L, -1L, -1, -1, "Trim", false, 0, -1, null), new TypeMap(DataType.VIEW, 1575L, 1575L, -1, -1, "AppList", false, 4, -1, null), new TypeMap(DataType.VIEW, -1L, -1L, -1, -1, "View", false, 4, -1, null), new TypeMap(null, 99L, 99L, -1, -1, "Integer", false, 2, -1, null), new TypeMap(DataType.KEYWORD, -1L, -1L, -1, -1, "DefaultKW", false, 14, 0, "Keyword"), new TypeMap(DataType.KEYWORD, -1L, -1L, -1, -1, "Keyword", false, 14, -1, null), new TypeMap(DataType.NULL, -1L, -1L, -1, -1, "Null", false, 1, -1, null) };
  private static int[] MRevMap = { 4, 4, 3, 6, 2, 2, 5, 3, 6, 10, 8, 13, 7, 7, 9, 14, 10, 12 };

  private TypeMap(DataType paramDataType, long paramLong1, long paramLong2, int paramInt1, int paramInt2, String paramString1, boolean paramBoolean, int paramInt3, int paramInt4, String paramString2)
  {
    this.mType = paramDataType;
    this.mName = paramString1;
    this.mMinId = paramLong1;
    this.mMaxId = paramLong2;
    this.mProp = new Integer(paramInt1);
    this.mPropVal = paramInt2;
    this.mQBE = paramBoolean;
    this.mOperandType = paramInt3;
    this.mKeyWdValue = paramInt4;
    this.mKeyWdClassName = paramString2;
  }

  private static boolean isFieldWithinRange(TypeMap paramTypeMap, int paramInt)
  {
    return (paramTypeMap.mMaxId == -1L) || ((paramInt >= paramTypeMap.mMinId) && (paramInt <= paramTypeMap.mMaxId));
  }

  public static String mapDataType(DataType paramDataType, int paramInt)
  {
    return mapDataType(paramDataType.toInt(), paramInt);
  }

  public static String mapDataType(int paramInt1, int paramInt2)
  {
    int i = paramInt2 != 0 ? paramInt2 : -1;
    for (int j = 0; j < MMap.length; j++)
      if ((MMap[j].mType != null) && (MMap[j].mType.toInt() == paramInt1) && (isFieldWithinRange(MMap[j], i)))
        return MMap[j].mName;
    return null;
  }

  public static String mapDataType(Value paramValue, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < MMap.length; i++)
      if ((MMap[i].mType != null) && (MMap[i].mType.toInt() == paramInt1) && (isFieldWithinRange(MMap[i], paramInt2)))
      {
        if (paramValue.getDataType().equals(DataType.KEYWORD))
        {
          if (MMap[i].mKeyWdValue == ((Keyword)paramValue.getValue()).toInt())
            return MMap[i].mName;
          return MMap[i].mKeyWdClassName;
        }
        return MMap[i].mName;
      }
    return null;
  }

  public static String mapDataType(String paramString, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < MMap.length; i++)
      if ((MMap[i].mType != null) && (MMap[i].mType.toInt() == paramInt1) && (isFieldWithinRange(MMap[i], paramInt2)))
      {
        if (paramInt1 == DataType.KEYWORD.toInt())
        {
          if (Keyword.toKeyword(paramString).equals(Keyword.AR_KEYWORD_DEFAULT))
            return MMap[i].mName;
          return MMap[i].mKeyWdClassName;
        }
        return MMap[i].mName;
      }
    return null;
  }

  public static String mapDataType(DataType paramDataType, Field paramField, int paramInt)
  {
    int i = paramField != null ? paramField.getFieldID() : -1;
    for (int j = 0; j < MMap.length; j++)
      if (equalDataType(MMap[j].mType, paramDataType))
      {
        Value localValue = null;
        if (paramField != null)
          localValue = paramField.getDisplayInstance().getProperty(paramInt, MMap[j].mProp);
        if ((localValue != null) && (localValue.getValue() != null) && (MMap[j].mProp.intValue() != -1))
          try
          {
            int k = Integer.parseInt(localValue.getValue().toString());
            if (k == MMap[j].mPropVal)
              return MMap[j].mName;
          }
          catch (NumberFormatException localNumberFormatException)
          {
          }
        else if (isFieldWithinRange(MMap[j], i))
          return MMap[j].mName;
      }
    return null;
  }

  private static boolean equalDataType(DataType paramDataType1, DataType paramDataType2)
  {
    if ((paramDataType1 != null) && (paramDataType2 != null))
      return paramDataType1.toInt() == paramDataType2.toInt();
    return paramDataType1 == paramDataType2;
  }

  public static String mapOperandDataType(int paramInt1, int paramInt2)
  {
    int i = paramInt2 != 0 ? paramInt2 : -1;
    for (int j = 0; j < MMap.length; j++)
      if ((MMap[j].mOperandType == paramInt1) && (isFieldWithinRange(MMap[j], i)))
        return MMap[j].mName;
    return null;
  }

  public static boolean isValidForQBE(DataType paramDataType, int paramInt)
  {
    int i = paramInt != 0 ? paramInt : -1;
    for (int j = 0; j < MMap.length; j++)
      if ((equalDataType(MMap[j].mType, paramDataType)) && (isFieldWithinRange(MMap[j], i)))
        return MMap[j].mQBE;
    return false;
  }

  public static int getOperandDataType(DataType paramDataType)
  {
    for (int i = 0; i < MMap.length; i++)
      if (equalDataType(MMap[i].mType, paramDataType))
        return MMap[i].mOperandType;
    return 0;
  }

  public static int getARDataType(int paramInt)
  {
    for (int i = 0; i < MRevMap.length; i += 2)
      if (MRevMap[i] == paramInt)
        return MRevMap[(i + 1)];
    return 0;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.TypeMap
 * JD-Core Version:    0.6.1
 */