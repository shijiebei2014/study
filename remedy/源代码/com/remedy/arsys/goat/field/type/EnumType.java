package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.SelectionFieldLimit;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.EnumField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.PrintStream;

public class EnumType extends GoatType
{
  private static final long serialVersionUID = 4962426013864741214L;
  private Integer mValue;
  private String mLabel;
  private String mStrValue;

  public EnumType(Value paramValue, int paramInt, FieldGraph.Node paramNode)
    throws GoatException
  {
    try
    {
      this.mValue = ((Integer)paramValue.getValue());
    }
    catch (ClassCastException localClassCastException)
    {
      this.mValue = Integer.valueOf(((Long)paramValue.getValue()).intValue());
    }
    if (paramNode != null)
    {
      assert ((paramNode.mField instanceof EnumField));
      EnumField localEnumField = (EnumField)paramNode.mField;
      this.mLabel = localEnumField.labelFromEnumId(paramValue);
      this.mStrValue = localEnumField.valueFromEnumId(paramValue);
    }
    else
    {
      this.mStrValue = this.mValue.toString();
    }
  }

  public EnumType(Value paramValue, int paramInt)
    throws GoatException
  {
    this(paramValue, paramInt, null);
  }

  public EnumType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt, paramNode);
  }

  public EnumType(String paramString, int paramInt)
  {
    int i = paramString.indexOf(' ');
    String str = paramString;
    if (i > 0)
    {
      str = paramString.substring(0, i);
      this.mLabel = paramString.substring(i + 1);
    }
    try
    {
      this.mValue = Integer.valueOf(str, 10);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      System.out.println("ENUM BROKEN");
      this.mValue = Integer.valueOf(0);
    }
    assert (this.mValue != null);
  }

  public EnumType(int paramInt1, int paramInt2)
  {
    this.mValue = Integer.valueOf(paramInt1);
    assert (this.mValue != null);
  }

  public int getOperandType()
  {
    return 3;
  }

  public int getDataType()
  {
    return 6;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.append("new EnumType(").append(this.mValue.toString());
    if (this.mLabel != null)
      localJSWriter.append(",\"").append(this.mLabel).append("\"");
    localJSWriter.append(")");
  }

  public String emitAR()
  {
    return this.mValue.toString();
  }

  public Value toValue()
  {
    return new Value(Integer.valueOf(this.mValue.intValue()), DataType.ENUM);
  }

  public String toPrimitive()
  {
    if (this.mStrValue != null)
      return this.mValue.toString() + " " + this.mStrValue;
    return this.mValue.toString();
  }

  public String toJFieldPrimitive()
  {
    return this.mValue.toString();
  }

  public String forHTML()
  {
    return this.mLabel;
  }

  public GoatType bind(ServerLogin paramServerLogin, Field paramField)
    throws GoatException
  {
    DataType localDataType = DataType.toDataType(paramField.getDataType());
    if (DataType.ENUM.equals(localDataType))
    {
      SelectionFieldLimit localSelectionFieldLimit = (SelectionFieldLimit)paramField.getFieldLimit();
      if ((localSelectionFieldLimit != null) && (localSelectionFieldLimit.getDataType() == 6))
      {
        EnumLimit localEnumLimit = new EnumLimit(localSelectionFieldLimit);
        if (!localEnumLimit.isValidValue(this.mValue.intValue()))
          return NullType.MNullType;
      }
    }
    else if ((DataType.CHAR.equals(localDataType)) || (DataType.DIARY.equals(localDataType)))
    {
      if (this.mLabel != null)
        return new CharType(this.mLabel, paramField.getFieldID());
      if (this.mStrValue != null)
        return new CharType(this.mStrValue, paramField.getFieldID());
    }
    return this;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.EnumType
 * JD-Core Version:    0.6.1
 */