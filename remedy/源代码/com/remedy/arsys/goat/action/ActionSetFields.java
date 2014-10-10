package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldAssignInfo;
import com.bmc.arsys.api.SetFieldsAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionSetFields extends Action
{
  private static final long serialVersionUID = 1063553391461938129L;
  private SetField[] mSetFields;
  private final int mIndex;

  public ActionSetFields(ActiveLink paramActiveLink, SetFieldsAction paramSetFieldsAction, int paramInt)
    throws GoatException
  {
    this.mIndex = paramInt;
    FieldAssignInfo[] arrayOfFieldAssignInfo = (FieldAssignInfo[])paramSetFieldsAction.getSetFieldsList().toArray(new FieldAssignInfo[0]);
    String str = paramActiveLink.getName();
    this.mSetFields = new SetField[arrayOfFieldAssignInfo.length];
    for (int i = 0; i < arrayOfFieldAssignInfo.length; i++)
      try
      {
        int j = arrayOfFieldAssignInfo[i].getFieldId();
        this.mSetFields[i] = new SetField(j, new Assignment(arrayOfFieldAssignInfo[i].getAssignment(), str, paramInt, j, paramActiveLink.getServer()));
      }
      catch (GoatException localGoatException)
      {
        logAction("exception: " + localGoatException.getMessage());
        throw localGoatException;
      }
  }

  public boolean canTerminate()
  {
    return false;
  }

  public boolean hasGoto()
  {
    return false;
  }

  public boolean isInterruptible()
  {
    for (int i = 0; i < this.mSetFields.length; i++)
      if ((this.mSetFields[i] != null) && (this.mSetFields[i].isInterruptible()))
        return true;
    return false;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < this.mSetFields.length; i++)
      if (this.mSetFields[i] != null)
        localStringBuilder.append(this.mSetFields[i].toString());
    return localStringBuilder.toString();
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.statement("highlight=true;");
    localJSWriter.statement("LogWrite(\"&nbsp;&nbsp;&nbsp;&nbsp;SetFields:\");");
    for (int i = 0; i < this.mSetFields.length; i++)
      if (this.mSetFields[i] != null)
      {
        this.mSetFields[i].emitInterruptibleJS(paramEmitter);
        this.mSetFields[i].emitJS(paramEmitter);
      }
    localJSWriter.statement("highlight=false;");
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
  {
    for (int i = 0; i < this.mSetFields.length; i++)
      try
      {
        if (this.mSetFields[i] != null)
          this.mSetFields[i].bindToForm(paramCachedFieldMap);
      }
      catch (GoatException localGoatException)
      {
        logAction("Error binding set-fields action " + i + " - dropping action");
        this.mSetFields[i] = null;
      }
  }

  public void simplify()
    throws GoatException
  {
    Simplifier localSimplifier = new Simplifier();
    for (int i = 0; i < this.mSetFields.length; i++)
      if (this.mSetFields[i] != null)
        this.mSetFields[i].simplify(localSimplifier);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    ActionSetFields localActionSetFields = (ActionSetFields)super.clone();
    localActionSetFields.mSetFields = new SetField[this.mSetFields.length];
    for (int i = 0; i < this.mSetFields.length; i++)
      if (this.mSetFields[i] != null)
        localActionSetFields.mSetFields[i] = ((SetField)this.mSetFields[i].clone());
    return localActionSetFields;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    for (int i = 0; i < this.mSetFields.length; i++)
      if (this.mSetFields[i] != null)
        this.mSetFields[i].addToOutputNotes(paramOutputNotes);
  }

  private class Simplifier
    implements SimplifyState
  {
    private static final long serialVersionUID = -6736418699502285308L;
    private final ArrayList mFieldQueries = new ArrayList();
    private final Map mSQLQueries = new HashMap();

    public Simplifier()
    {
    }

    public FieldQuery crossReferenceFieldQuery(FieldQuery paramFieldQuery)
    {
      for (int i = 0; i < this.mFieldQueries.size(); i++)
      {
        FieldQuery localFieldQuery = (FieldQuery)this.mFieldQueries.get(i);
        if (localFieldQuery.compareTo(paramFieldQuery))
          return localFieldQuery;
      }
      this.mFieldQueries.add(paramFieldQuery);
      paramFieldQuery.setIndex("" + ActionSetFields.this.mIndex + "_" + this.mFieldQueries.size());
      return null;
    }

    public SQLQuery crossReferenceSQLQuery(SQLQuery paramSQLQuery)
    {
      SQLQuery localSQLQuery;
      if ((localSQLQuery = (SQLQuery)this.mSQLQueries.get(paramSQLQuery)) != null)
        return localSQLQuery;
      this.mSQLQueries.put(paramSQLQuery, paramSQLQuery);
      paramSQLQuery.setIndex("" + ActionSetFields.this.mIndex + "_" + this.mSQLQueries.size());
      return null;
    }
  }

  class SetField
    implements Cloneable, Serializable
  {
    private static final long serialVersionUID = -2860233939337597537L;
    protected int mFieldId;
    protected Assignment mAssignment;
    private int mType;
    private boolean mSetterInterruptible;

    SetField(int paramAssignment, Assignment arg3)
    {
      this.mFieldId = paramAssignment;
      Object localObject;
      this.mAssignment = localObject;
      this.mType = 0;
      this.mSetterInterruptible = false;
    }

    public String toString()
    {
      return "{" + com.remedy.arsys.goat.Operand.MDataTypeNames[this.mType] + "}:F(" + this.mFieldId + ").S(" + this.mAssignment + ")";
    }

    public void emitJS(Emitter paramEmitter)
      throws GoatException
    {
      JSWriter localJSWriter = paramEmitter.js();
      Emitter localEmitter = new Emitter(new JSWriter(), paramEmitter);
      this.mAssignment.emitJS(localEmitter);
      if (this.mFieldId == 98)
      {
        localJSWriter.callFunction(false, "ARACTSetAllFields", new Object[] { "windowID", localEmitter.js().toString() });
      }
      else
      {
        if (this.mType == 11)
          localJSWriter.statement("var dt=F(windowID," + this.mFieldId + ").GetType()");
        localJSWriter.callFunction(this.mSetterInterruptible, "F(windowID," + this.mFieldId + ")", "S", new Object[] { localEmitter.js().toString() });
        if (FormContext.get().getWorkflowLogging())
          localJSWriter.callFunction(false, "LogWrite", new Object[] { "\"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\" + F(windowID," + this.mFieldId + ").GLabelOrName()+" + "\"(" + this.mFieldId + ")\"" + " + \"&nbsp;=&nbsp;\" " + " + F(" + "windowID" + "," + this.mFieldId + ").G()" + ((this.mFieldId == 102) || (this.mFieldId == 123) ? ".toString().replace(/./g, \"*\")" : "") });
      }
    }

    public void emitInterruptibleJS(Emitter paramEmitter)
      throws GoatException
    {
      this.mAssignment.emitInterruptibleJS(paramEmitter);
    }

    public void bindToForm(CachedFieldMap paramCachedFieldMap)
      throws GoatException
    {
      this.mAssignment.bindToForm(paramCachedFieldMap);
      if (this.mFieldId == 98)
        return;
      Field localField = (Field)paramCachedFieldMap.get(Integer.valueOf(this.mFieldId));
      if (localField == null)
      {
        if (ActionSetFields.this.isNonOptimizedField(this.mFieldId))
          this.mType = TypeMap.getOperandDataType(ActionSetFields.this.getNonOptimizedFieldDataType(this.mFieldId));
        else
          throw new GoatException("Missing target fieldid " + this.mFieldId + " in setfields binding");
      }
      else
      {
        this.mType = TypeMap.getOperandDataType(DataType.toDataType(localField.getDataType()));
        if (localField.getDataType() == DataType.TABLE.toInt())
          this.mSetterInterruptible = true;
        if (localField.getDataType() == DataType.COLUMN.toInt())
          this.mType = 11;
      }
      this.mAssignment.bindToTarget(this.mType, this.mFieldId);
    }

    public boolean isInterruptible()
    {
      return (this.mAssignment.isInterruptible()) || (this.mSetterInterruptible);
    }

    public void simplify(SimplifyState paramSimplifyState)
      throws GoatException
    {
      this.mAssignment.simplify(paramSimplifyState);
    }

    public Object clone()
      throws CloneNotSupportedException
    {
      SetField localSetField = (SetField)super.clone();
      localSetField.mAssignment = ((Assignment)this.mAssignment.clone());
      return localSetField;
    }

    public void addToOutputNotes(OutputNotes paramOutputNotes)
    {
      this.mAssignment.addToOutputNotes(paramOutputNotes);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionSetFields
 * JD-Core Version:    0.6.1
 */