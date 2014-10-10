package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.RealFieldLimit;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Form;

public class RealField extends DataField
{
  private static final long serialVersionUID = 7383156361081690219L;
  private int mPrecision = 6;
  private boolean mHaveDefault;
  private Double mDefault;
  private double mMax = 1.845E+019D;
  private double mMin = -1.845E+019D;

  public RealField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    RealFieldLimit localRealFieldLimit = (RealFieldLimit)paramField.getFieldLimit();
    if (localRealFieldLimit != null)
    {
      setMPrecision(localRealFieldLimit.getPrecision());
      if (getMPrecision() < 0)
        setMPrecision(6);
      setMMin(localRealFieldLimit.getLowRange());
      setMMax(localRealFieldLimit.getHighRange());
    }
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (localValue.getDataType().equals(DataType.REAL))
    {
      setMDefault((Double)localValue.getValue());
      setMHaveDefault(true);
    }
  }

  public int getPrecision()
  {
    return getMPrecision();
  }

  void setMPrecision(int paramInt)
  {
    this.mPrecision = paramInt;
  }

  public int getMPrecision()
  {
    return this.mPrecision;
  }

  void setMHaveDefault(boolean paramBoolean)
  {
    this.mHaveDefault = paramBoolean;
  }

  public boolean isMHaveDefault()
  {
    return this.mHaveDefault;
  }

  void setMDefault(Double paramDouble)
  {
    this.mDefault = paramDouble;
  }

  public Double getMDefault()
  {
    return this.mDefault;
  }

  void setMMin(double paramDouble)
  {
    this.mMin = paramDouble;
  }

  public double getMMin()
  {
    return this.mMin;
  }

  void setMMax(double paramDouble)
  {
    this.mMax = paramDouble;
  }

  public double getMMax()
  {
    return this.mMax;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.RealField
 * JD-Core Version:    0.6.1
 */