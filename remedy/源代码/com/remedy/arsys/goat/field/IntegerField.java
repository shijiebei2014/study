package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.IntegerFieldLimit;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.log.Log;

public class IntegerField extends DataField
{
  private static final long serialVersionUID = -2584800847078409356L;
  private long mMin;
  private long mMax;
  private long mDefault;
  private boolean mSpinner;
  private boolean mHaveDefault;
  public static final GoatImageButton mSpinnerUp = new GoatImageButton("spinnerup");
  public static final GoatImageButton mSpinnerDown = new GoatImageButton("spinnerdown");

  public IntegerField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    IntegerFieldLimit localIntegerFieldLimit = (IntegerFieldLimit)paramField.getFieldLimit();
    if (localIntegerFieldLimit != null)
    {
      setMMin(localIntegerFieldLimit.getLowRange());
      setMMax(localIntegerFieldLimit.getHighRange());
    }
    else
    {
      setMMin(-2147483647L);
      setMMax(2147483647L);
    }
    Value localValue = paramField.getDefaultValue();
    assert ((localValue != null) || (paramField.getFieldID() == 99L));
    if (localValue.getDataType().equals(DataType.INTEGER))
    {
      setMDefault(Long.parseLong(localValue.toString()));
      setMHaveDefault(true);
    }
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 62)
    {
      setMSpinner(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_SPIN: " + isMSpinner());
    }
    else
    {
      super.handleProperty(paramInt, paramValue);
    }
  }

  void setMDefault(long paramLong)
  {
    this.mDefault = paramLong;
  }

  public long getMDefault()
  {
    return this.mDefault;
  }

  void setMMax(long paramLong)
  {
    this.mMax = paramLong;
  }

  public long getMMax()
  {
    return this.mMax;
  }

  void setMMin(long paramLong)
  {
    this.mMin = paramLong;
  }

  public long getMMin()
  {
    return this.mMin;
  }

  void setMHaveDefault(boolean paramBoolean)
  {
    this.mHaveDefault = paramBoolean;
  }

  public boolean isMHaveDefault()
  {
    return this.mHaveDefault;
  }

  void setMSpinner(boolean paramBoolean)
  {
    this.mSpinner = paramBoolean;
  }

  public boolean isMSpinner()
  {
    return this.mSpinner;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.IntegerField
 * JD-Core Version:    0.6.1
 */