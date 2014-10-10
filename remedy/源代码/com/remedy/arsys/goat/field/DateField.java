package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;

public class DateField extends DataField
{
  private static final long serialVersionUID = 5411328448107369045L;
  private String mDefault;
  private boolean mHaveDefault;
  private static final GoatImageButton mExpandButton = new GoatImageButton("calendar");

  public DateField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (localValue.getDataType().equals(DataType.DATE))
    {
      setMDefault(((DateInfo)localValue.getValue()).getValue() + "");
      setMHaveDefault(true);
    }
    else if (localValue.getDataType().equals(DataType.KEYWORD))
    {
      setMDefault(localValue.toString());
      setMHaveDefault(true);
    }
  }

  public GoatImageButton getExpandButton()
  {
    if (FormContext.get().IsVoiceAccessibleUser())
      return null;
    return mExpandButton;
  }

  void setMDefault(String paramString)
  {
    this.mDefault = paramString;
  }

  public String getMDefault()
  {
    return this.mDefault;
  }

  void setMHaveDefault(boolean paramBoolean)
  {
    this.mHaveDefault = paramBoolean;
  }

  public boolean isMHaveDefault()
  {
    return this.mHaveDefault;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.DateField
 * JD-Core Version:    0.6.1
 */