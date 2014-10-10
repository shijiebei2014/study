package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DecimalFieldLimit;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Form;
import java.math.BigDecimal;

public class DecimalField extends DataField
{
  private static final long serialVersionUID = 3876431177224052902L;
  private int mPrecision = 2;
  private boolean mHaveDefault;
  private BigDecimal mDefault;
  private BigDecimal mMax = new BigDecimal("99999999999999999999999999.99");
  private BigDecimal mMin = new BigDecimal("-99999999999999999999999999.99");

  public DecimalField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    DecimalFieldLimit localDecimalFieldLimit = (DecimalFieldLimit)paramField.getFieldLimit();
    if (localDecimalFieldLimit != null)
    {
      setMPrecision(localDecimalFieldLimit.getPrecision());
      if (getMPrecision() < 0)
        setMPrecision(2);
      setMMin(localDecimalFieldLimit.getLowRange());
      setMMax(localDecimalFieldLimit.getHighRange());
    }
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (localValue.getDataType().equals(DataType.DECIMAL))
    {
      setMDefault((BigDecimal)localValue.getValue());
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

  void setMDefault(BigDecimal paramBigDecimal)
  {
    this.mDefault = paramBigDecimal;
  }

  public BigDecimal getMDefault()
  {
    return this.mDefault;
  }

  void setMMin(BigDecimal paramBigDecimal)
  {
    this.mMin = paramBigDecimal;
  }

  public BigDecimal getMMin()
  {
    return this.mMin;
  }

  void setMMax(BigDecimal paramBigDecimal)
  {
    this.mMax = paramBigDecimal;
  }

  public BigDecimal getMMax()
  {
    return this.mMax;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.DecimalField
 * JD-Core Version:    0.6.1
 */