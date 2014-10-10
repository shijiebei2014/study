package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Time;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;

public class TODField extends DataField
{
  private static final long serialVersionUID = -5778008194761243742L;
  private String mDefault;
  private boolean mHaveDefault;
  private static final GoatImageButton mExpandButton = new GoatImageButton("time");

  public TODField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (localValue.getDataType().equals(DataType.TIME_OF_DAY))
    {
      setMDefault(((Time)localValue.getValue()).getValue() + "");
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
 * Qualified Name:     com.remedy.arsys.goat.field.TODField
 * JD-Core Version:    0.6.1
 */