package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.stubs.ServerLogin;

public class GoatTypeFactory
{
  public static GoatType create(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    DataType localDataType = paramValue.getDataType();
    String str = TypeMap.mapDataType(localDataType, paramInt);
    if (str.equals("Time"))
      return new TimeType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("TOD"))
      return new TODType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("View"))
      return new ViewType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Enum"))
      return new EnumType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Diary"))
      return new DiaryType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Char"))
      return new CharType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Keyword"))
      return new KeywordType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("DefaultKW"))
      return new DefaultKWType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Display"))
      return new DisplayType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("StatusHistory"))
      return new StatusHistoryType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Attachment"))
      return new AttachmentType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Date"))
      return new DateType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Decimal"))
      return new DecimalType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Group"))
      return new GroupType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Integer"))
      return new IntegerType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Currency"))
      return new CurrencyType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Null"))
      return new NullType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    if (str.equals("Real"))
      return new RealType(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    throw new GoatException("Unsupported datatype - " + str);
  }

  public static GoatType create(Value paramValue, int paramInt)
    throws GoatException
  {
    int i = paramValue.getDataType().toInt();
    String str = TypeMap.mapDataType(paramValue, i, paramInt);
    if (str.equals("Time"))
      return new TimeType(paramValue, paramInt);
    if (str.equals("TOD"))
      return new TODType(paramValue, paramInt);
    if (str.equals("View"))
      return new ViewType(paramValue, paramInt);
    if (str.equals("Enum"))
      return new EnumType(paramValue, paramInt);
    if (str.equals("Diary"))
      return new DiaryType(paramValue, paramInt);
    if (str.equals("Char"))
      return new CharType(paramValue, paramInt);
    if (str.equals("Keyword"))
      return new KeywordType(paramValue, paramInt);
    if (str.equals("DefaultKW"))
      return new DefaultKWType(paramValue, paramInt);
    if (str.equals("Display"))
      return new DisplayType(paramValue, paramInt);
    if (str.equals("StatusHistory"))
      return new StatusHistoryType(paramValue, paramInt);
    if (str.equals("Attachment"))
      return new AttachmentType(paramValue, paramInt);
    if (str.equals("Date"))
      return new DateType(paramValue, paramInt);
    if (str.equals("Decimal"))
      return new DecimalType(paramValue, paramInt);
    if (str.equals("Group"))
      return new GroupType(paramValue, paramInt);
    if (str.equals("Integer"))
      return new IntegerType(paramValue, paramInt);
    if (str.equals("Currency"))
      return new CurrencyType(paramValue, paramInt);
    if (str.equals("Null"))
      return new NullType(paramValue, paramInt);
    if (str.equals("Real"))
      return new RealType(paramValue, paramInt);
    throw new GoatException("Unsupported datatype - " + str);
  }

  public static GoatType create(String paramString, int paramInt1, int paramInt2)
    throws GoatException
  {
    String str = TypeMap.mapDataType(paramString, paramInt1, paramInt2);
    if (str.equals("Time"))
      return new TimeType(paramString, paramInt2);
    if (str.equals("TOD"))
      return new TODType(paramString, paramInt2);
    if (str.equals("View"))
      return new ViewType(paramString, paramInt2);
    if (str.equals("Enum"))
      return new EnumType(paramString, paramInt2);
    if (str.equals("Diary"))
      return new DiaryType(paramString, paramInt2);
    if (str.equals("Char"))
      return new CharType(paramString, paramInt2);
    if (str.equals("Keyword"))
      return new KeywordType(paramString, paramInt2);
    if (str.equals("DefaultKW"))
      return new DefaultKWType(paramString, paramInt2);
    if (str.equals("Display"))
      return new DisplayType(paramString, paramInt2);
    if (str.equals("StatusHistory"))
      return new StatusHistoryType(paramString, paramInt2);
    if (str.equals("Attachment"))
      return new AttachmentType(paramString, paramInt2);
    if (str.equals("Date"))
      return new DateType(paramString, paramInt2);
    if (str.equals("Decimal"))
      return new DecimalType(paramString, paramInt2);
    if (str.equals("Group"))
      return new GroupType(paramString, paramInt2);
    if (str.equals("Integer"))
      return new IntegerType(paramString, paramInt2);
    if (str.equals("Currency"))
      return new CurrencyType(paramString, paramInt2);
    if (str.equals("Null"))
      return new NullType(paramString, paramInt2);
    if (str.equals("Real"))
      return new RealType(paramString, paramInt2);
    throw new GoatException("Unsupported datatype - " + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.GoatTypeFactory
 * JD-Core Version:    0.6.1
 */