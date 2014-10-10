package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.LocaleUtil;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.RealField;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class RealType extends GoatType
{
  private Double mValue;
  private int mPrecision = 6;

  public RealType(Value paramValue, int paramInt, FieldGraph.Node paramNode)
    throws GoatException
  {
    this.mValue = ((Double)paramValue.getValue());
    assert (this.mValue != null);
    if (paramNode != null)
    {
      assert ((paramNode.mField instanceof RealField));
      RealField localRealField = (RealField)paramNode.mField;
      this.mPrecision = localRealField.getPrecision();
    }
  }

  public RealType(Value paramValue, int paramInt)
    throws GoatException
  {
    this(paramValue, paramInt, null);
  }

  public RealType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt, paramNode);
  }

  public RealType(String paramString, int paramInt)
  {
    this.mValue = new Double(paramString);
    assert (this.mValue != null);
  }

  public RealType(Double paramDouble, int paramInt)
  {
    assert (paramDouble != null);
    this.mValue = paramDouble;
    this.mPrecision = paramInt;
  }

  public int getDataType()
  {
    return 3;
  }

  public int getOperandType()
  {
    return 5;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new RealType(").append(this.mValue.toString()).append(")");
  }

  public String emitAR()
  {
    return this.mValue.toString();
  }

  public Value toValue()
  {
    return new Value(this.mValue.doubleValue());
  }

  public String toPrimitive()
  {
    return this.mValue.toString();
  }

  public String forHTML()
  {
    return format(this.mValue, this.mPrecision);
  }

  public static String format(Double paramDouble, int paramInt)
  {
    return LocaleUtil.FormatReal(SessionData.get().getLocale(), paramDouble, paramInt);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.RealType
 * JD-Core Version:    0.6.1
 */