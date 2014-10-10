package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;

public class DiaryField extends DataField
{
  private static final long serialVersionUID = 301469883566825105L;
  private boolean mShowURL;
  private static final GoatImageButton MExpandButton = new GoatImageButton("diary");
  private String mDefaultValue;

  public DiaryField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (localValue.getDataType().equals(DataType.DIARY))
      setMDefaultValue(localValue.toString());
    else if (!localValue.getDataType().equals(DataType.NULL))
      setMDefaultValue(localValue.getValue().toString());
    if (getMRows() <= 0L)
      setMRows(1L);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 5116)
      setMShowURL(propToBool(paramValue));
    else
      super.handleProperty(paramInt, paramValue);
  }

  public String getForCodePrefix()
  {
    return "arid";
  }

  public String getExpandBoxClassesString()
  {
    if (isMShowURL())
      return "diaryUrl";
    return "diary";
  }

  public GoatImageButton getExpandButton()
  {
    return MExpandButton;
  }

  public String getExpandBoxAltText()
  {
    if (getMAccess() == 3L)
      return getLocalizedDescriptionStringForWidget("Editor for {0}, empty and unavailable");
    return getLocalizedDescriptionStringForWidget("Editor for {0}, empty");
  }

  protected void setMDefaultValue(String paramString)
  {
    this.mDefaultValue = paramString;
  }

  public String getMDefaultValue()
  {
    return this.mDefaultValue;
  }

  public boolean isMShowURL()
  {
    return this.mShowURL;
  }

  public void setMShowURL(boolean paramBoolean)
  {
    this.mShowURL = paramBoolean;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.DiaryField
 * JD-Core Version:    0.6.1
 */