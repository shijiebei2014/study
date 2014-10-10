package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;

public class DefaultKWType extends GoatType
{
  protected Keyword mValue = Keyword.AR_KEYWORD_DEFAULT;
  protected int mFieldid;

  public DefaultKWType(Value paramValue, int paramInt)
    throws GoatException
  {
    this.mFieldid = paramInt;
  }

  public DefaultKWType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt);
  }

  public DefaultKWType(String paramString, int paramInt)
  {
    this.mFieldid = paramInt;
  }

  public int getDataType()
  {
    return 1;
  }

  public int getOperandType()
  {
    return 14;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new DefaultKWType(").appendqs(toPrimitive()).append(")");
  }

  public String emitAR()
  {
    return escapeQuotedString("\"" + this.mValue + "\"");
  }

  public Value toValue()
  {
    return new Value(this.mValue);
  }

  public String toPrimitive()
  {
    return this.mValue.toString();
  }

  public String toChar()
  {
    return this.mValue.toString();
  }

  public GoatType bind(ServerLogin paramServerLogin, Field paramField)
    throws GoatException
  {
    Value localValue = paramField.getDefaultValue();
    GoatType localGoatType = GoatTypeFactory.create(localValue, this.mFieldid);
    return localGoatType;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.DefaultKWType
 * JD-Core Version:    0.6.1
 */