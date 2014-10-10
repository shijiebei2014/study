package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;

public class NullType extends GoatType
{
  public static final NullType MNullType = new NullType((Value)null, 0);

  public NullType(Value paramValue, int paramInt)
  {
  }

  public NullType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
  {
  }

  public NullType(String paramString, int paramInt)
  {
  }

  public int getDataType()
  {
    return 0;
  }

  public int getOperandType()
  {
    return 1;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("Null");
  }

  public String emitAR()
  {
    return "$NULL$";
  }

  public Value toValue()
  {
    return new Value();
  }

  public String toPrimitive()
  {
    return "";
  }

  public void emitPrimitive(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", getDataType());
  }

  public boolean isNull()
  {
    return true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.NullType
 * JD-Core Version:    0.6.1
 */