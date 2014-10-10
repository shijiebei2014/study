package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;

public class TimeField extends DataField
{
  private static final long serialVersionUID = -3274505287879430087L;
  private String mDefault;
  private boolean mHaveDefault;
  private static final GoatImageButton mExpandButton = new GoatImageButton("calendar");
  private int mDateTimePopup;

  public TimeField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (localValue.getDataType().equals(DataType.TIME))
    {
      setMDefault(((Timestamp)localValue.getValue()).getValue() + "");
      setMHaveDefault(true);
    }
    else if (localValue.getDataType().equals(DataType.KEYWORD))
    {
      setMDefault(localValue.toString());
      setMHaveDefault(true);
    }
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 144)
      setMDateTimePopup(propToInt(paramValue));
    else
      super.handleProperty(paramInt, paramValue);
  }

  public final int getDateTimePopup()
  {
    return getMDateTimePopup();
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

  private void setMDateTimePopup(int paramInt)
  {
    this.mDateTimePopup = paramInt;
  }

  public int getMDateTimePopup()
  {
    return this.mDateTimePopup;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.TimeField
 * JD-Core Version:    0.6.1
 */