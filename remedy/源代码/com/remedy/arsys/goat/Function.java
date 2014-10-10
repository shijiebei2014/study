package com.remedy.arsys.goat;

import com.bmc.arsys.api.AssignInfo;
import com.bmc.arsys.api.FunctionAssignInfo;
import com.remedy.arsys.goat.action.Assignment;
import com.remedy.arsys.goat.action.Expression;
import com.remedy.arsys.goat.action.SimplifyState;
import com.remedy.arsys.goat.field.type.CharType;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Function
  implements FormAware, TargetAware, Cloneable, Serializable
{
  private static final long serialVersionUID = 8296222781469660890L;
  private static final Map<Integer, FunctionMapping> MFunctionMap = Collections.synchronizedMap(new HashMap());
  private FunctionMapping mFunction;
  private Expression[] mArgs;

  private Function(FunctionAssignInfo paramFunctionAssignInfo, String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws GoatException
  {
    int i = paramFunctionAssignInfo.getFunctionCode();
    this.mFunction = ((FunctionMapping)MFunctionMap.get(new Integer(i)));
    if (this.mFunction == null)
      throw new GoatException("Unsupported function assignment " + i);
    this.mArgs = null;
    if (paramFunctionAssignInfo.getNumItems() > 0)
    {
      AssignInfo[] arrayOfAssignInfo = (AssignInfo[])paramFunctionAssignInfo.getParameterList().toArray(new AssignInfo[0]);
      if ((arrayOfAssignInfo.length > this.mFunction.mSignature.length) && (paramFunctionAssignInfo.getFunctionCode() != 26) && (paramFunctionAssignInfo.getFunctionCode() != 25) && (paramFunctionAssignInfo.getFunctionCode() != 50))
        throw new GoatException("Incorrect number of arguments to function " + arrayOfAssignInfo.length + "/" + this.mFunction.mSignature.length);
      this.mArgs = new Expression[arrayOfAssignInfo.length];
      for (int j = 0; j < arrayOfAssignInfo.length; j++)
        this.mArgs[j] = Assignment.compileAssignInfo(arrayOfAssignInfo[j], paramString1, paramInt1, paramInt2, paramString2);
    }
    if ((this.mFunction.mFunctionName.startsWith("ARFuncCol")) && (!this.mArgs[0].isSingleOperand()))
    {
      this.mFunction = ((FunctionMapping)MFunctionMap.get(new Integer(-1)));
      this.mArgs = new Expression[0];
    }
  }

  public static Operand compileFunction(FunctionAssignInfo paramFunctionAssignInfo, String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws GoatException
  {
    Function localFunction = new Function(paramFunctionAssignInfo, paramString1, paramInt1, paramInt2, paramString2);
    return new Operand((localFunction.mFunction != null) && (localFunction.mFunction.mSignature != null) && (localFunction.mFunction.mSignature.length > 0) ? localFunction.mFunction.mSignature[0] : 0, 15, localFunction);
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.mFunction.mFunctionName);
    localStringBuilder.append("(");
    for (int i = 0; i < this.mArgs.length; i++)
    {
      if (i != 0)
        localStringBuilder.append(", ");
      localStringBuilder.append(this.mArgs[i].toString());
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.append(this.mFunction.mFunctionName).append("(");
    for (int i = 0; i < this.mArgs.length; i++)
    {
      if (i != 0)
        localJSWriter.comma();
      if (this.mFunction.mFunctionName.startsWith("ARFuncCol"))
        this.mArgs[i].emitJSAsRef(paramEmitter);
      else
        this.mArgs[i].emitJS(paramEmitter);
    }
    localJSWriter.append(")");
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    for (int i = 0; i < this.mArgs.length; i++)
      this.mArgs[i].emitInterruptibleJS(paramEmitter);
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    for (int i = 0; i < this.mArgs.length; i++)
      this.mArgs[i].bindToForm(paramCachedFieldMap);
  }

  public void bindToTarget(int paramInt1, int paramInt2)
    throws GoatException
  {
    for (int i = 0; i < this.mArgs.length; i++)
      this.mArgs[i].bindToTarget(this.mFunction.mSignature.length > i + 1 ? this.mFunction.mSignature[(i + 1)] : 0, paramInt2);
  }

  public void simplify(SimplifyState paramSimplifyState)
    throws GoatException
  {
    for (int i = 0; i < this.mArgs.length; i++)
      this.mArgs[i].simplify(paramSimplifyState);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    Function localFunction = (Function)super.clone();
    localFunction.mArgs = new Expression[this.mArgs.length];
    for (int i = 0; i < this.mArgs.length; i++)
      localFunction.mArgs[i] = ((Expression)this.mArgs[i].clone());
    return localFunction;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    if ((this.mFunction.mFunctionName.startsWith("ARFuncTemplate")) && (this.mArgs.length > 0) && (this.mArgs[0].isSingleOperand()))
    {
      Object localObject = this.mArgs[0].getRightOperand().getValue();
      if ((localObject != null) && ((localObject instanceof CharType)))
        paramOutputNotes.addTemplate(((CharType)localObject).toChar());
    }
    for (int i = 0; i < this.mArgs.length; i++)
      this.mArgs[i].addToOutputNotes(paramOutputNotes);
  }

  public boolean isInterruptible()
  {
    for (int i = 0; i < this.mArgs.length; i++)
      if (this.mArgs[i].isInterruptible())
        return true;
    return false;
  }

  public String getUniqueFieldIds()
    throws GoatException
  {
    String str1 = "";
    for (int i = 0; i < this.mArgs.length; i++)
    {
      String str2 = this.mArgs[i].getUniqueFieldIds();
      if (str2 != null)
        str1 = str1 + str2;
    }
    return str1.length() == 0 ? null : str1;
  }

  public String getVarName()
  {
    String str1 = "";
    for (int i = 0; i < this.mArgs.length; i++)
    {
      String str2 = this.mArgs[i].getVarName();
      if ((str2 != null) && (str2.length() > 0))
        return str2;
    }
    return str1;
  }

  static
  {
    MFunctionMap.put(new Integer(-1), new FunctionMapping("SilentFail", new int[0]));
    MFunctionMap.put(new Integer(1), new FunctionMapping("Date", new int[] { 4, 7 }));
    MFunctionMap.put(new Integer(2), new FunctionMapping("Time", new int[] { 4, 7 }));
    MFunctionMap.put(new Integer(3), new FunctionMapping("Month", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(4), new FunctionMapping("Day", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(5), new FunctionMapping("Year", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(6), new FunctionMapping("Weekday", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(7), new FunctionMapping("Hour", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(8), new FunctionMapping("Minute", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(9), new FunctionMapping("Second", new int[] { 2, 7 }));
    MFunctionMap.put(new Integer(10), new FunctionMapping("Trunc", new int[] { 2, 0 }));
    MFunctionMap.put(new Integer(11), new FunctionMapping("Round", new int[] { 2, 0 }));
    MFunctionMap.put(new Integer(12), new FunctionMapping("Convert", new int[0]));
    MFunctionMap.put(new Integer(13), new FunctionMapping("Length", new int[] { 2, 4 }));
    MFunctionMap.put(new Integer(14), new FunctionMapping("Upper", new int[] { 4, 4 }));
    MFunctionMap.put(new Integer(15), new FunctionMapping("Lower", new int[] { 4, 4 }));
    MFunctionMap.put(new Integer(16), new FunctionMapping("Substr", new int[] { 4, 4, 2, 2 }));
    MFunctionMap.put(new Integer(17), new FunctionMapping("Left", new int[] { 4, 4, 2 }));
    MFunctionMap.put(new Integer(18), new FunctionMapping("Right", new int[] { 4, 4, 2 }));
    MFunctionMap.put(new Integer(19), new FunctionMapping("LTrim", new int[] { 4, 4 }));
    MFunctionMap.put(new Integer(20), new FunctionMapping("RTrim", new int[] { 4, 4 }));
    MFunctionMap.put(new Integer(21), new FunctionMapping("LPad", new int[] { 4, 4, 2, 4 }));
    MFunctionMap.put(new Integer(22), new FunctionMapping("RPad", new int[] { 4, 4, 2, 4 }));
    MFunctionMap.put(new Integer(23), new FunctionMapping("Replace", new int[] { 4, 4, 4, 4 }));
    MFunctionMap.put(new Integer(24), new FunctionMapping("Strstr", new int[] { 2, 4, 4 }));
    MFunctionMap.put(new Integer(25), new FunctionMapping("Min", new int[0]));
    MFunctionMap.put(new Integer(26), new FunctionMapping("Max", new int[0]));
    MFunctionMap.put(new Integer(27), new FunctionMapping("ColSum", new int[] { 5, 0 }));
    MFunctionMap.put(new Integer(28), new FunctionMapping("ColCount", new int[] { 2, 0 }));
    MFunctionMap.put(new Integer(29), new FunctionMapping("ColAvg", new int[] { 5, 0 }));
    MFunctionMap.put(new Integer(30), new FunctionMapping("ColMin", new int[] { 0, 0 }));
    MFunctionMap.put(new Integer(31), new FunctionMapping("ColMax", new int[] { 0, 0 }));
    MFunctionMap.put(new Integer(32), new FunctionMapping("DateAdd", new int[] { 8, 4, 2, 8 }));
    MFunctionMap.put(new Integer(33), new FunctionMapping("DateDiff", new int[] { 2, 4, 8, 8 }));
    MFunctionMap.put(new Integer(34), new FunctionMapping("DateName", new int[] { 4, 4, 8 }));
    MFunctionMap.put(new Integer(35), new FunctionMapping("DateNum", new int[] { 2, 4, 8 }));
    MFunctionMap.put(new Integer(36), new FunctionMapping("CurrConvert", new int[] { 10, 10, 4, 7 }));
    MFunctionMap.put(new Integer(37), new FunctionMapping("CurrSetDate", new int[] { 10, 10, 7 }));
    MFunctionMap.put(new Integer(38), new FunctionMapping("CurrSetType", new int[] { 10, 10, 4 }));
    MFunctionMap.put(new Integer(39), new FunctionMapping("CurrSetValue", new int[] { 10, 10, 6 }));
    MFunctionMap.put(new Integer(40), new FunctionMapping("LengthC", new int[] { 2, 4 }));
    MFunctionMap.put(new Integer(41), new FunctionMapping("LeftC", new int[] { 4, 4, 2 }));
    MFunctionMap.put(new Integer(42), new FunctionMapping("RightC", new int[] { 4, 4, 2 }));
    MFunctionMap.put(new Integer(43), new FunctionMapping("LPadC", new int[] { 4, 4, 2, 4 }));
    MFunctionMap.put(new Integer(44), new FunctionMapping("RPadC", new int[] { 4, 4, 2, 4 }));
    MFunctionMap.put(new Integer(45), new FunctionMapping("StrstrC", new int[] { 2, 4, 4 }));
    MFunctionMap.put(new Integer(46), new FunctionMapping("SubstrC", new int[] { 4, 4, 2, 2 }));
    MFunctionMap.put(new Integer(47), new FunctionMapping("Encrypt", new int[0]));
    MFunctionMap.put(new Integer(48), new FunctionMapping("Decrypt", new int[0]));
    MFunctionMap.put(new Integer(50), new FunctionMapping("Template", new int[0]));
    MFunctionMap.put(new Integer(49), new FunctionMapping("Hover", new int[] { 2 }));
    MFunctionMap.put(new Integer(51), new FunctionMapping("SelectedRowCount", new int[] { 4 }));
    MFunctionMap.put(new Integer(52), new FunctionMapping("DroppedRowIndex", new int[] { 2 }));
    MFunctionMap.put(new Integer(53), new FunctionMapping("DroppedColumnIndex", new int[] { 2 }));
    MFunctionMap.put(new Integer(54), new FunctionMapping("MapGet", new int[] { 2, 4 }));
    MFunctionMap.put(new Integer(55), new FunctionMapping("ListGet", new int[] { 2, 2 }));
    MFunctionMap.put(new Integer(56), new FunctionMapping("ListSize", new int[] { 2 }));
    MFunctionMap.put(new Integer(58), new FunctionMapping("VisibleRows", new int[] { 2 }));
  }

  private static class FunctionMapping
    implements Serializable
  {
    private static final long serialVersionUID = 5017277160572678104L;
    protected String mFunctionName;
    protected int[] mSignature;

    FunctionMapping(String paramString, int[] paramArrayOfInt)
    {
      this.mFunctionName = ("ARFunc" + paramString);
      this.mSignature = paramArrayOfInt;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Function
 * JD-Core Version:    0.6.1
 */