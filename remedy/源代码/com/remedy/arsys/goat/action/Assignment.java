package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.ArithOpAssignInfo;
import com.bmc.arsys.api.AssignFieldInfo;
import com.bmc.arsys.api.AssignInfo;
import com.bmc.arsys.api.AssignSQLInfo;
import com.bmc.arsys.api.CurrencyPartInfo;
import com.bmc.arsys.api.FunctionAssignInfo;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.CurrencyFieldID;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.Function;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Operand;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.StatusHistoryFieldID;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Assignment
  implements FormAware, Cloneable, Serializable
{
  private static final long serialVersionUID = 46573124935303634L;
  private Expression mAssignment;

  public Assignment(AssignInfo paramAssignInfo, String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws GoatException
  {
    this.mAssignment = compileAssignInfo(paramAssignInfo, paramString1, paramInt1, paramInt2, paramString2);
  }

  public static Expression compileAssignInfo(AssignInfo paramAssignInfo, String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws GoatException
  {
    switch (paramAssignInfo.getAssignType())
    {
    case 4:
      return compileArithOpAssignInfo(paramAssignInfo.getArithOp(), paramString1, paramInt1, paramInt2, paramString2);
    case 2:
      return new Expression(compileAssignFieldInfo(paramAssignInfo.getField(), paramString2));
    case 1:
      return new Expression(Qualifier.compileValue(paramAssignInfo.getValue()));
    case 5:
      return new Expression(compileFunctionAssignInfo(paramAssignInfo.getFunction(), paramString1, paramInt1, paramInt2, paramString2));
    case 3:
      AssignmentProcess localAssignmentProcess = new AssignmentProcess(paramAssignInfo.getProcess(), paramString1, paramInt1, paramInt2);
      return new Expression(new Operand(0, 19, localAssignmentProcess));
    case 7:
      return new Expression(compileSQLAssignment(paramAssignInfo.getSql(), paramString1, paramInt1));
    case 6:
      throw new GoatException("DDE assignment not supported");
    case 8:
      throw new GoatException("filter api assignment not supported");
    case 0:
      throw new GoatException("none assignment not supported");
    }
    throw new GoatException("Unsupported assignment type " + paramAssignInfo.getAssignType());
  }

  public static Expression compileArithOpAssignInfo(ArithOpAssignInfo paramArithOpAssignInfo, String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws GoatException
  {
    int i = paramArithOpAssignInfo.getOperation();
    int j;
    switch (i)
    {
    case 1:
      j = 12;
      break;
    case 4:
      j = 13;
      break;
    case 5:
      j = 14;
      break;
    case 3:
      j = 15;
      break;
    case 6:
      j = 16;
      break;
    case 2:
      j = 17;
      break;
    default:
      throw new GoatException("Unsupported ArithmeticOperationInfo " + i);
    }
    Expression localExpression = null;
    if (i != 6)
      localExpression = compileAssignInfo(paramArithOpAssignInfo.getOperandLeft(), paramString1, paramInt1, paramInt2, paramString2);
    return new Expression(j, localExpression, compileAssignInfo(paramArithOpAssignInfo.getOperandRight(), paramString1, paramInt1, paramInt2, paramString2));
  }

  private static Operand compileFunctionAssignInfo(FunctionAssignInfo paramFunctionAssignInfo, String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws GoatException
  {
    return Function.compileFunction(paramFunctionAssignInfo, paramString1, paramInt1, paramInt2, paramString2);
  }

  private static Operand compileAssignFieldInfo(AssignFieldInfo paramAssignFieldInfo, String paramString)
    throws GoatException
  {
    Object localObject1;
    int i;
    if (paramAssignFieldInfo.getTag() == 6)
    {
      localObject2 = paramAssignFieldInfo.getCurrencyPart();
      if (((CurrencyPartInfo)localObject2).getPartTag() == 0)
      {
        localObject1 = Integer.valueOf(((CurrencyPartInfo)localObject2).getFieldId());
        i = 12;
      }
      else
      {
        localObject1 = new CurrencyFieldID((CurrencyPartInfo)localObject2);
        i = 23;
      }
    }
    else if (paramAssignFieldInfo.getTag() == 4)
    {
      localObject1 = new StatusHistoryFieldID(paramAssignFieldInfo.getStatHistory());
      i = 22;
    }
    else if (paramAssignFieldInfo.getTag() == 1)
    {
      localObject1 = Integer.valueOf(paramAssignFieldInfo.getFieldId());
      i = 12;
    }
    else
    {
      throw new GoatException("Unsupported AssignFieldInfo tag type " + paramAssignFieldInfo.getTag());
    }
    if (paramAssignFieldInfo.getQualifier() == null)
    {
      if (((paramAssignFieldInfo.getServer().equals("*")) || (paramAssignFieldInfo.getServer().equals("@"))) && (paramAssignFieldInfo.getForm().equals("*")))
        return new Operand(0, i, localObject1);
      if ((paramAssignFieldInfo.getServer().equals("@")) && (paramAssignFieldInfo.getForm().equals("@")))
      {
        if (i == 12)
          i = 13;
        return new Operand(0, i, localObject1);
      }
    }
    if ((paramAssignFieldInfo.getTag() != 1) && (paramAssignFieldInfo.getTag() != 6))
      throw new GoatException("Unsupported remote AssignFieldInfo tag type " + paramAssignFieldInfo.getTag());
    Object localObject2 = new Qualifier(paramAssignFieldInfo.getQualifier(), paramString);
    Object localObject3;
    if (i == 23)
      localObject3 = new CurrencyFieldQuery((CurrencyFieldID)localObject1, paramAssignFieldInfo.getMultiMatchOption(), paramAssignFieldInfo.getNoMatchOption(), (Qualifier)localObject2, paramAssignFieldInfo.getServer(), paramAssignFieldInfo.getForm());
    else
      localObject3 = new FieldQuery(((Integer)localObject1).intValue(), paramAssignFieldInfo.getMultiMatchOption(), paramAssignFieldInfo.getNoMatchOption(), (Qualifier)localObject2, paramAssignFieldInfo.getServer(), paramAssignFieldInfo.getForm());
    return new Operand(0, 16, localObject3);
  }

  private static Operand compileSQLAssignment(AssignSQLInfo paramAssignSQLInfo, String paramString, int paramInt)
  {
    return new Operand(0, 17, new SQLQuery(paramString, paramInt, paramAssignSQLInfo.getValueIndex(), paramAssignSQLInfo.getMultiMatchOption(), paramAssignSQLInfo.getNoMatchOption(), paramAssignSQLInfo.getSqlCommand()));
  }

  public String toString()
  {
    return this.mAssignment.toString();
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    this.mAssignment.emitInterruptibleJS(paramEmitter);
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    this.mAssignment.emitJS(paramEmitter);
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    this.mAssignment.bindToForm(paramCachedFieldMap);
  }

  public void bindToTarget(int paramInt1, int paramInt2)
    throws GoatException
  {
    this.mAssignment.bindToTarget(paramInt1, paramInt2);
    this.mAssignment.setDataType(paramInt1);
  }

  public void bindToDynamicTarget(String paramString, int paramInt)
  {
    this.mAssignment.bindToDynamicTarget(paramString, paramInt);
  }

  public void simplify(SimplifyState paramSimplifyState)
    throws GoatException
  {
    this.mAssignment.simplify(paramSimplifyState);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    Assignment localAssignment = (Assignment)super.clone();
    localAssignment.mAssignment = ((Expression)this.mAssignment.clone());
    return localAssignment;
  }

  public Expression getExpression()
  {
    return this.mAssignment;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    this.mAssignment.addToOutputNotes(paramOutputNotes);
  }

  public boolean isInterruptible()
  {
    return this.mAssignment.isInterruptible();
  }

  public static void ForceFieldsToCurrentForm(AssignInfo paramAssignInfo)
  {
    switch (paramAssignInfo.getAssignType())
    {
    case 2:
      paramAssignInfo.getField().setServer("@");
      paramAssignInfo.getField().setForm("@");
      break;
    case 4:
      if (paramAssignInfo.getArithOp().getOperation() != 6)
        ForceFieldsToCurrentForm(paramAssignInfo.getArithOp().getOperandLeft());
      ForceFieldsToCurrentForm(paramAssignInfo.getArithOp().getOperandRight());
      break;
    case 5:
      List localList = paramAssignInfo.getFunction().getParameterList();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        AssignInfo localAssignInfo = (AssignInfo)localIterator.next();
        ForceFieldsToCurrentForm(localAssignInfo);
      }
    case 3:
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.Assignment
 * JD-Core Version:    0.6.1
 */