package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;

public class IntegerType extends GoatType
{
  private Integer mValue;

  public IntegerType(Value paramValue, int paramInt)
  {
    this.mValue = ((Integer)paramValue.getValue());
    assert (this.mValue != null);
  }

  public IntegerType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
  {
    this(paramValue, paramInt);
  }

  public IntegerType(String paramString, int paramInt)
  {
    this.mValue = new Integer(Integer.parseInt(paramString));
    assert (this.mValue != null);
  }

  public IntegerType(Integer paramInteger, int paramInt)
  {
    this.mValue = new Integer(paramInteger.intValue());
    assert (this.mValue != null);
  }

  public int getDataType()
  {
    return 2;
  }

  public int getOperandType()
  {
    return 2;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new IntegerType(").append(this.mValue.toString()).append(")");
  }

  public String emitAR()
  {
    return this.mValue.toString();
  }

  public Value toValue()
  {
    return new Value(this.mValue.intValue());
  }

  public String toPrimitive()
  {
    return this.mValue.toString();
  }

  public Integer toInteger()
  {
    return this.mValue;
  }

  public static String format(Integer paramInteger)
  {
    return paramInteger.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.IntegerType
 * JD-Core Version:    0.6.1
 */