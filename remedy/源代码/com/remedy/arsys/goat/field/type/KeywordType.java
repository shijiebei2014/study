package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;

public class KeywordType extends GoatType
{
  private Keyword mValue;
  private int mFieldid;

  public KeywordType(Value paramValue, int paramInt)
    throws GoatException
  {
    if (paramValue.getDataType().equals(DataType.KEYWORD))
      this.mValue = ((Keyword)paramValue.getValue());
    this.mFieldid = paramInt;
    assert (this.mValue != null);
  }

  public KeywordType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt);
  }

  public KeywordType(String paramString, int paramInt)
  {
    this.mValue = Keyword.toKeyword(paramString);
    this.mFieldid = paramInt;
    assert (this.mValue != null);
  }

  public String emitAR()
  {
    return escapeQuotedString("\"" + this.mValue.toString() + "\"");
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new KeywordType(").appendqs(toPrimitive()).append(")");
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

  public String toPrimitive()
  {
    return this.mValue.toString();
  }

  public Value toValue()
  {
    return new Value(this.mValue);
  }

  public Integer toInteger()
  {
    return Integer.valueOf(this.mValue.toInt());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.KeywordType
 * JD-Core Version:    0.6.1
 */