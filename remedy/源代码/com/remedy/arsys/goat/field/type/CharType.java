package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;

public class CharType extends GoatType
{
  private static final long serialVersionUID = 675875636075210666L;
  protected String mValue;

  public CharType(Value paramValue, int paramInt)
    throws GoatException
  {
    this.mValue = ((String)paramValue.getValue());
    assert (this.mValue != null);
  }

  public CharType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt);
  }

  public CharType(String paramString, int paramInt)
  {
    this.mValue = paramString;
    assert (this.mValue != null);
  }

  public int getDataType()
  {
    return 4;
  }

  public int getOperandType()
  {
    return 4;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    int i = this.mValue.indexOf("</script>");
    if (i != -1)
      paramEmitter.js().append("ADD(new CharType(").appendqs(this.mValue.substring(0, i + 2)).append("), new CharType(").appendqs(this.mValue.substring(i + 2)).append("),4)");
    else
      paramEmitter.js().append("new CharType(").appendqs(this.mValue).append(")");
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
    return this.mValue;
  }

  public String toChar()
  {
    return this.mValue;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.CharType
 * JD-Core Version:    0.6.1
 */