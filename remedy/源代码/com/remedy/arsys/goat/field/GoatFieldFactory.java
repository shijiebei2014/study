package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.intf.service.IGoatFieldFactory;

public class GoatFieldFactory
  implements IGoatFieldFactory
{
  public GoatField create(Form paramForm, int paramInt, Field paramField)
    throws GoatException
  {
    assert ((paramField != null) && (paramForm != null));
    DataType localDataType = DataType.toDataType(paramField.getDataType());
    String str = TypeMap.mapDataType(localDataType, paramField, paramInt);
    if (str.equals("Column"))
      return new ColumnField(paramForm, paramField, paramInt);
    if (str.equals("Time"))
      return new TimeField(paramForm, paramField, paramInt);
    if (str.equals("TOD"))
      return new TODField(paramForm, paramField, paramInt);
    if (str.equals("AppList"))
      return new AppListField(paramForm, paramField, paramInt);
    if (str.equals("View"))
      return new ViewField(paramForm, paramField, paramInt);
    if (str.equals("Enum"))
      return new EnumField(paramForm, paramField, paramInt);
    if (str.equals("Page"))
      return new PageField(paramForm, paramField, paramInt);
    if (str.equals("Diary"))
      return new DiaryField(paramForm, paramField, paramInt);
    if (str.equals("VertNavBar"))
      return new VertNavBarField(paramForm, paramField, paramInt);
    if (str.equals("SearchBar"))
      return new SearchBarField(paramForm, paramField, paramInt);
    if (str.equals("Char"))
      return new CharField(paramForm, paramField, paramInt);
    if (str.equals("Display"))
      return new DisplayField(paramForm, paramField, paramInt);
    if (str.equals("StatusHistory"))
      return new StatusHistoryField(paramForm, paramField, paramInt);
    if (str.equals("NavBarItem"))
      return new NavBarItemField(paramForm, paramField, paramInt);
    if (str.equals("Attachment"))
      return new AttachmentField(paramForm, paramField, paramInt);
    if (str.equals("AttachmentPool"))
      return new AttachmentPoolField(paramForm, paramField, paramInt);
    if (str.equals("PageHolder"))
      return new PageHolderField(paramForm, paramField, paramInt);
    if (str.equals("Date"))
      return new DateField(paramForm, paramField, paramInt);
    if (str.equals("Decimal"))
      return new DecimalField(paramForm, paramField, paramInt);
    if (str.equals("Integer"))
      return new IntegerField(paramForm, paramField, paramInt);
    if (str.equals("Group"))
      return new GroupField(paramForm, paramField, paramInt);
    if (str.equals("Table"))
      return new TableField(paramForm, paramField, paramInt);
    if (str.equals("AlertList"))
      return new AlertListField(paramForm, paramField, paramInt);
    if (str.equals("Currency"))
      return new CurrencyField(paramForm, paramField, paramInt);
    if (str.equals("Trim"))
      return new TrimField(paramForm, paramField, paramInt);
    if (str.equals("Control"))
      return new ControlField(paramForm, paramField, paramInt);
    if (str.equals("HorzNavBar"))
      return new HorzNavBarField(paramForm, paramField, paramInt);
    if (str.equals("Real"))
      return new RealField(paramForm, paramField, paramInt);
    if (str.equals("MenuItem"))
      return new MenuItemField(paramForm, paramField, paramInt);
    throw new GoatException("Unsupported fieldtype - " + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.GoatFieldFactory
 * JD-Core Version:    0.6.1
 */