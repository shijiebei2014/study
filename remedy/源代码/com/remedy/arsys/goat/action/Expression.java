package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Operand;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.TargetAware;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.Set;

public class Expression
  implements FormAware, TargetAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -5649050643809755861L;
  public static final int OP_NONE = 0;
  public static final int OP_RIGHT = 1;
  public static final int OP_AND = 2;
  public static final int OP_OR = 3;
  public static final int OP_NOT = 4;
  public static final int OP_EQUAL = 5;
  public static final int OP_NOTEQUAL = 6;
  public static final int OP_GREATER = 7;
  public static final int OP_GREATER_EQUAL = 8;
  public static final int OP_LESS = 9;
  public static final int OP_LESS_EQUAL = 10;
  public static final int OP_LIKE = 11;
  public static final int OP_ADD = 12;
  public static final int OP_DIVIDE = 13;
  public static final int OP_MODULO = 14;
  public static final int OP_MULTIPLY = 15;
  public static final int OP_NEGATE = 16;
  public static final int OP_SUBTRACT = 17;
  private static final String[] MJSOpNames = { "", "", "&&", "||", "!", "EQ(", "NE(", "GT(", "GE(", "LT(", "LE(", "LIKE(", "ADD(", "DIV(", "MOD(", "MUL(", "NEG(", "SUB(" };
  private static final String[] MAROpNames = { "", "", "AND", "OR", "NOT", "=", "!=", ">", ">=", "<", "<=", "LIKE", "+", "/", "%", "*", "-", "-" };
  private Expression mLeftExpression;
  private Expression mRightExpression;
  private Operand mLeftOperand;
  private Operand mRightOperand;
  private int mOp;
  private int mDataType;
  private boolean mIsDynamicTarget;
  private String mDynamicTargetVar;
  private int mDynamicTargetField;
  private static transient Log mLog = Log.get(9);

  public Expression(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    this.mOp = paramInt;
    this.mLeftExpression = paramExpression1;
    this.mRightExpression = paramExpression2;
    this.mLeftOperand = null;
    this.mRightOperand = null;
    this.mDataType = 0;
    this.mIsDynamicTarget = false;
  }

  public Expression(int paramInt, Expression paramExpression, Operand paramOperand)
  {
    this.mOp = paramInt;
    this.mLeftExpression = paramExpression;
    this.mRightExpression = null;
    this.mLeftOperand = null;
    this.mRightOperand = paramOperand;
    this.mDataType = 0;
    this.mIsDynamicTarget = false;
  }

  public Expression(int paramInt, Operand paramOperand1, Operand paramOperand2)
  {
    this.mOp = paramInt;
    this.mLeftExpression = null;
    this.mRightExpression = null;
    this.mLeftOperand = paramOperand1;
    this.mRightOperand = paramOperand2;
    this.mDataType = 0;
    this.mIsDynamicTarget = false;
  }

  public Expression(int paramInt, Operand paramOperand, Expression paramExpression)
  {
    this.mOp = paramInt;
    this.mLeftExpression = null;
    this.mRightExpression = paramExpression;
    this.mLeftOperand = paramOperand;
    this.mRightOperand = null;
    this.mDataType = 0;
    this.mIsDynamicTarget = false;
  }

  public Expression(Operand paramOperand)
  {
    this.mOp = 1;
    this.mLeftExpression = null;
    this.mRightExpression = null;
    this.mLeftOperand = null;
    this.mRightOperand = paramOperand;
    this.mDataType = 0;
    this.mIsDynamicTarget = false;
  }

  public Expression(int paramInt)
  {
    this.mOp = 0;
    this.mLeftExpression = null;
    this.mRightExpression = null;
    this.mLeftOperand = null;
    this.mRightOperand = null;
    this.mDataType = paramInt;
    this.mIsDynamicTarget = false;
  }

  public int getOp()
  {
    return this.mOp;
  }

  public Expression getLeftExpression()
  {
    if (!$assertionsDisabled)
      if (((this.mLeftExpression != null ? 1 : 0) ^ (this.mLeftOperand != null ? 1 : 0)) == 0)
        throw new AssertionError();
    return this.mLeftExpression;
  }

  public Expression getRightExpression()
  {
    if (!$assertionsDisabled)
      if (((this.mRightExpression != null ? 1 : 0) ^ (this.mRightOperand != null ? 1 : 0)) == 0)
        throw new AssertionError();
    return this.mRightExpression;
  }

  public Operand getLeftOperand()
  {
    if (!$assertionsDisabled)
      if (((this.mLeftExpression != null ? 1 : 0) ^ (this.mLeftOperand != null ? 1 : 0)) == 0)
        throw new AssertionError();
    return this.mLeftOperand;
  }

  public Operand getRightOperand()
  {
    if (!$assertionsDisabled)
      if (((this.mRightExpression != null ? 1 : 0) ^ (this.mRightOperand != null ? 1 : 0)) == 0)
        throw new AssertionError();
    return this.mRightOperand;
  }

  public void setDataType(int paramInt)
  {
    this.mDataType = paramInt;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{").append(Operand.MDataTypeNames[this.mDataType]).append("}").append("expr:");
    if (!MAROpNames[this.mOp].endsWith("("))
      localStringBuilder.append("(");
    else
      localStringBuilder.append(MAROpNames[this.mOp]);
    if ((this.mOp != 0) && (this.mOp != 1) && (this.mOp != 4) && (this.mOp != 16))
    {
      assert ((this.mLeftExpression == null) || (this.mLeftOperand == null));
      if (this.mLeftExpression != null)
        localStringBuilder.append(this.mLeftExpression.toString());
      else if (this.mLeftOperand != null)
        localStringBuilder.append(this.mLeftOperand.toString());
      if (MAROpNames[this.mOp].endsWith("("))
        localStringBuilder.append(", ");
      else
        localStringBuilder.append(" ");
    }
    if ((this.mOp != 0) && (this.mOp != 1) && (!MAROpNames[this.mOp].endsWith("(")))
      localStringBuilder.append(MAROpNames[this.mOp]).append(" ");
    assert ((this.mRightExpression == null) || (this.mRightOperand == null));
    if (this.mRightExpression != null)
      localStringBuilder.append(this.mRightExpression.toString());
    else if (this.mRightOperand != null)
      localStringBuilder.append(this.mRightOperand.toString());
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    if (!MJSOpNames[this.mOp].endsWith("("))
      localJSWriter.append("(");
    else
      localJSWriter.append(MJSOpNames[this.mOp]);
    if ((this.mOp != 0) && (this.mOp != 1) && (this.mOp != 4) && (this.mOp != 16))
    {
      assert ((this.mLeftExpression == null) || (this.mLeftOperand == null));
      if (this.mLeftExpression != null)
        this.mLeftExpression.emitJS(paramEmitter);
      else if (this.mLeftOperand != null)
        this.mLeftOperand.emitJS(paramEmitter);
      if (MJSOpNames[this.mOp].endsWith("("))
        localJSWriter.append(", ");
      else
        localJSWriter.append(" ");
    }
    if ((this.mOp != 0) && (this.mOp != 1) && (!MJSOpNames[this.mOp].endsWith("(")))
      localJSWriter.append(MJSOpNames[this.mOp]).append(" ");
    assert ((this.mRightExpression == null) || (this.mRightOperand == null));
    if (this.mRightExpression != null)
      this.mRightExpression.emitJS(paramEmitter);
    else if (this.mRightOperand != null)
      this.mRightOperand.emitJS(paramEmitter);
    if ((this.mDataType != 0) || (this.mIsDynamicTarget))
      switch (this.mOp)
      {
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
        if (this.mIsDynamicTarget)
          localJSWriter.append(", \"" + this.mDynamicTargetField + "\" in " + this.mDynamicTargetVar + " ? " + this.mDynamicTargetVar + "[" + this.mDynamicTargetField + "].t : \"dt\"");
        else if (this.mDataType == 11)
          localJSWriter.append(", dt");
        else
          localJSWriter.append(", " + TypeMap.getARDataType(this.mDataType));
        break;
      }
    localJSWriter.append(")");
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mLeftExpression != null)
      this.mLeftExpression.emitInterruptibleJS(paramEmitter);
    else if (this.mLeftOperand != null)
      this.mLeftOperand.emitInterruptibleJS(paramEmitter);
    if (this.mRightExpression != null)
      this.mRightExpression.emitInterruptibleJS(paramEmitter);
    else if (this.mRightOperand != null)
      this.mRightOperand.emitInterruptibleJS(paramEmitter);
  }

  public String getUniqueFieldIds()
    throws GoatException
  {
    String str = null;
    if (this.mLeftExpression != null)
      str = this.mLeftExpression.getUniqueFieldIds();
    if (str != null)
      return str;
    if (this.mRightExpression != null)
      str = this.mRightExpression.getUniqueFieldIds();
    if (str != null)
      return str;
    if (this.mLeftOperand != null)
      str = this.mLeftOperand.getUniqueFieldIds();
    if (str != null)
      return str;
    if (this.mRightOperand != null)
      str = this.mRightOperand.getUniqueFieldIds();
    return str;
  }

  public String getVarName()
  {
    String str = "";
    if (this.mLeftExpression != null)
      str = this.mLeftExpression.getVarName();
    if (str.length() != 0)
      return str;
    if (this.mRightExpression != null)
      str = this.mRightExpression.getVarName();
    if (str.length() != 0)
      return str;
    if (this.mLeftOperand != null)
      str = this.mLeftOperand.getVarName();
    if (str.length() != 0)
      return str;
    if (this.mRightOperand != null)
      str = this.mRightOperand.getVarName();
    return str;
  }

  public String emitAR()
    throws GoatException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((this.mOp != 2) && (this.mOp != 1))
      localStringBuilder.append("(");
    if ((this.mOp != 0) && (this.mOp != 1) && (this.mOp != 4) && (this.mOp != 16))
    {
      assert ((this.mLeftExpression == null) || (this.mLeftOperand == null));
      if (this.mLeftExpression != null)
        localStringBuilder.append(this.mLeftExpression.emitAR());
      else if (this.mLeftOperand != null)
        localStringBuilder.append(this.mLeftOperand.emitAR());
      localStringBuilder.append(" ");
    }
    if ((this.mOp != 0) && (this.mOp != 1))
      localStringBuilder.append(MAROpNames[this.mOp]).append(" ");
    assert ((this.mRightExpression == null) || (this.mRightOperand == null));
    if (this.mRightExpression != null)
      localStringBuilder.append(this.mRightExpression.emitAR());
    else if (this.mRightOperand != null)
      localStringBuilder.append(this.mRightOperand.emitAR());
    if ((this.mOp != 2) && (this.mOp != 1))
      localStringBuilder.append(")");
    return localStringBuilder.toString();
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    if ((this.mOp != 0) && (this.mOp != 1) && (this.mOp != 4) && (this.mOp != 16))
    {
      assert ((this.mLeftExpression == null) || (this.mLeftOperand == null));
      if (this.mLeftExpression != null)
        this.mLeftExpression.bindToForm(paramCachedFieldMap);
      else if (this.mLeftOperand != null)
        this.mLeftOperand.bindToForm(paramCachedFieldMap);
    }
    assert ((this.mRightExpression == null) || (this.mRightOperand == null));
    if (this.mRightExpression != null)
      this.mRightExpression.bindToForm(paramCachedFieldMap);
    else if (this.mRightOperand != null)
      this.mRightOperand.bindToForm(paramCachedFieldMap);
  }

  public void bindToTarget(int paramInt1, int paramInt2)
    throws GoatException
  {
    int i = 0;
    int j = 0;
    if ((this.mOp != 0) && (this.mOp != 1) && (this.mOp != 4) && (this.mOp != 16))
    {
      assert ((this.mLeftExpression == null) || (this.mLeftOperand == null));
      if (this.mLeftExpression != null)
      {
        this.mLeftExpression.bindToTarget(paramInt1, paramInt2);
        i = this.mLeftExpression.mDataType;
      }
      else if (this.mLeftOperand != null)
      {
        this.mLeftOperand.bindToTarget(paramInt1, paramInt2);
        i = this.mLeftOperand.getDataType();
      }
    }
    assert ((this.mRightExpression == null) || (this.mRightOperand == null));
    if (this.mRightExpression != null)
    {
      this.mRightExpression.bindToTarget(paramInt1, paramInt2);
      j = this.mRightExpression.mDataType;
    }
    else if (this.mRightOperand != null)
    {
      this.mRightOperand.bindToTarget(paramInt1, paramInt2);
      j = this.mRightOperand.getDataType();
    }
    if ((paramInt1 >= 0) && (paramInt1 <= 11))
      this.mDataType = paramInt1;
    if (paramInt1 != 4)
      if (((i == 2) || (i == 5) || (i == 6) || (i == 3) || (i == 0)) && ((j == 2) || (j == 5) || (j == 6) || (j == 3) || (j == 0)))
      {
        if ((i != 2) && (i != 3) && (i != 0))
          this.mDataType = i;
        else if ((this.mOp == 13) && (j == 2))
          this.mDataType = 5;
        else
          this.mDataType = j;
      }
      else if (((i == 2) || (i == 5) || (i == 6)) && ((j == 8) || (j == 7) || (j == 9)))
        this.mDataType = j;
      else if (((i == 8) || (i == 7) || (i == 9)) && ((j == 2) || (j == 5) || (j == 6)))
        this.mDataType = i;
      else if ((this.mDataType == 0) && (i == 4) && ((j == 2) || (j == 5) || (j == 6)))
        this.mDataType = j;
  }

  public boolean isSingleOperand()
  {
    return (this.mOp == 1) && (this.mRightOperand != null);
  }

  public void emitJSAsRef(Emitter paramEmitter)
    throws GoatException
  {
    if (!isSingleOperand())
      throw new GoatException("Non-single operand cannot be a reference");
    this.mRightOperand.emitJSAsRef(paramEmitter);
  }

  public void simplify(SimplifyState paramSimplifyState)
    throws GoatException
  {
    if (this.mLeftExpression != null)
      this.mLeftExpression.simplify(paramSimplifyState);
    else if (this.mLeftOperand != null)
      this.mLeftOperand.simplify(paramSimplifyState);
    if (this.mRightExpression != null)
      this.mRightExpression.simplify(paramSimplifyState);
    else if (this.mRightOperand != null)
      this.mRightOperand.simplify(paramSimplifyState);
    if ((this.mLeftExpression != null) && (this.mLeftExpression.isSingleOperand()))
    {
      mLog.fine("Expression simplification: " + this.mLeftExpression.toString());
      this.mLeftOperand = this.mLeftExpression.mRightOperand;
      this.mLeftExpression = null;
    }
    if ((this.mRightExpression != null) && (this.mRightExpression.isSingleOperand()))
    {
      mLog.fine("Expression simplification: " + this.mRightExpression.toString());
      this.mRightOperand = this.mRightExpression.mRightOperand;
      this.mRightExpression = null;
    }
    if ((this.mLeftOperand != null) && (this.mRightOperand != null))
    {
      if (this.mLeftOperand.getDataType() != this.mRightOperand.getDataType())
        mLog.warning("Data type mismatch: " + toString());
      if ((this.mLeftOperand.isConstant()) && (this.mRightOperand.isConstant()))
      {
        mLog.fine("Expression elimination: " + toString());
        if ((this.mLeftOperand.getObjectType() == 4) && (this.mRightOperand.getObjectType() == 4))
        {
          if (this.mOp == 12)
          {
            this.mRightOperand = new Operand(4, 4, (String)this.mLeftOperand.getValue() + (String)this.mRightOperand.getValue());
            this.mLeftOperand = null;
            this.mOp = 1;
            mLog.fine("--> " + toString());
          }
          else
          {
            mLog.fine("--> op " + this.mOp + " not implemented");
          }
        }
        else if ((this.mLeftOperand.getObjectType() == 2) && (this.mRightOperand.getObjectType() == 2))
          mLog.fine("--> op " + this.mOp + " not implemented");
        else
          mLog.fine("--> type " + this.mLeftOperand.getObjectType() + " not implemented");
      }
    }
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    Expression localExpression = (Expression)super.clone();
    if (this.mLeftExpression != null)
      localExpression.mLeftExpression = ((Expression)this.mLeftExpression.clone());
    else if (this.mLeftOperand != null)
      localExpression.mLeftOperand = ((Operand)this.mLeftOperand.clone());
    if (this.mRightExpression != null)
      localExpression.mRightExpression = ((Expression)this.mRightExpression.clone());
    else if (this.mRightOperand != null)
      localExpression.mRightOperand = ((Operand)this.mRightOperand.clone());
    localExpression.mDataType = this.mDataType;
    localExpression.mIsDynamicTarget = this.mIsDynamicTarget;
    localExpression.mDynamicTargetField = this.mDynamicTargetField;
    localExpression.mDynamicTargetVar = this.mDynamicTargetVar;
    return localExpression;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    if (this.mLeftExpression != null)
      this.mLeftExpression.addToOutputNotes(paramOutputNotes);
    else if (this.mLeftOperand != null)
      this.mLeftOperand.addToOutputNotes(paramOutputNotes);
    if (this.mRightExpression != null)
      this.mRightExpression.addToOutputNotes(paramOutputNotes);
    else if (this.mRightOperand != null)
      this.mRightOperand.addToOutputNotes(paramOutputNotes);
  }

  public boolean isInterruptible()
  {
    if (this.mLeftExpression != null)
    {
      if (this.mLeftExpression.isInterruptible())
        return true;
    }
    else if ((this.mLeftOperand != null) && (this.mLeftOperand.isInterruptible()))
      return true;
    if (this.mRightExpression != null)
    {
      if (this.mRightExpression.isInterruptible())
        return true;
    }
    else if ((this.mRightOperand != null) && (this.mRightOperand.isInterruptible()))
      return true;
    return false;
  }

  public void collectFieldReferences(Set paramSet1, Set paramSet2)
    throws GoatException
  {
    if (this.mLeftExpression != null)
      this.mLeftExpression.collectFieldReferences(paramSet1, paramSet2);
    else if (this.mLeftOperand != null)
      this.mLeftOperand.collectFieldReferences(paramSet1, paramSet2);
    if (this.mRightExpression != null)
      this.mRightExpression.collectFieldReferences(paramSet1, paramSet2);
    else if (this.mRightOperand != null)
      this.mRightOperand.collectFieldReferences(paramSet1, paramSet2);
  }

  public void bindDefaultKeywordToField(int paramInt, String paramString)
  {
    if (this.mLeftExpression != null)
      this.mLeftExpression.bindDefaultKeywordToField(paramInt, paramString);
    else if (this.mLeftOperand != null)
      this.mLeftOperand.bindDefaultKeywordToField(paramInt, paramString);
    if (this.mRightExpression != null)
      this.mRightExpression.bindDefaultKeywordToField(paramInt, paramString);
    else if (this.mRightOperand != null)
      this.mRightOperand.bindDefaultKeywordToField(paramInt, paramString);
  }

  public void collectDefaultKeywordFieldIds(Set paramSet)
  {
    if (this.mLeftExpression != null)
      this.mLeftExpression.collectDefaultKeywordFieldIds(paramSet);
    else if (this.mLeftOperand != null)
      this.mLeftOperand.collectDefaultKeywordFieldIds(paramSet);
    if (this.mRightExpression != null)
      this.mRightExpression.collectDefaultKeywordFieldIds(paramSet);
    else if (this.mRightOperand != null)
      this.mRightOperand.collectDefaultKeywordFieldIds(paramSet);
  }

  public void bindToDynamicTarget(String paramString, int paramInt)
  {
    this.mIsDynamicTarget = true;
    this.mDynamicTargetVar = paramString;
    this.mDynamicTargetField = paramInt;
    if (this.mLeftExpression != null)
      this.mLeftExpression.bindToDynamicTarget(paramString, paramInt);
    if (this.mRightExpression != null)
      this.mRightExpression.bindToDynamicTarget(paramString, paramInt);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.Expression
 * JD-Core Version:    0.6.1
 */