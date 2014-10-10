package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.LocaleUtil;
import com.remedy.arsys.goat.field.DecimalField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.math.BigDecimal;

public class DecimalType extends GoatType
{
  private BigDecimal mValue;
  private int mPrecision = 2;

  public DecimalType(Value paramValue, int paramInt, FieldGraph.Node paramNode)
    throws GoatException
  {
    this.mValue = ((BigDecimal)paramValue.getValue());
    assert (this.mValue != null);
    if (paramNode != null)
    {
      assert ((paramNode.mField instanceof DecimalField));
      DecimalField localDecimalField = (DecimalField)paramNode.mField;
      this.mPrecision = localDecimalField.getPrecision();
    }
  }

  public DecimalType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt, paramNode);
  }

  public DecimalType(Value paramValue, int paramInt)
    throws GoatException
  {
    this(paramValue, paramInt, null);
  }

  public DecimalType(String paramString, int paramInt)
  {
    this.mValue = new BigDecimal(paramString);
    assert (this.mValue != null);
  }

  public int getDataType()
  {
    return 10;
  }

  public int getOperandType()
  {
    return 6;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new DecimalType(").appendqs(this.mValue.toString()).append(")");
  }

  public String emitAR()
  {
    return this.mValue.toString();
  }

  public Value toValue()
  {
    return new Value(this.mValue);
  }

  public String toPrimitive()
  {
    return this.mValue.toString();
  }

  public String forHTML()
  {
    return format(this.mValue, this.mPrecision);
  }

  public static String format(BigDecimal paramBigDecimal, int paramInt)
  {
    return LocaleUtil.FormatDecimal(SessionData.get().getLocale(), paramBigDecimal, paramInt);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.DecimalType
 * JD-Core Version:    0.6.1
 */