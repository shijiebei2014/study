package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.AssignFieldInfo;
import com.bmc.arsys.api.AssignInfo;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.PushFieldsAction;
import com.bmc.arsys.api.PushFieldsInfo;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.CurrencyFieldID;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Operand;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionPushFields extends Action
{
  private static final long serialVersionUID = 3727842355288990541L;
  private PushField[] mPushFields;
  private final int mIndex;
  String mServer;
  private static final Pattern MDynamicPattern = Pattern.compile("\\$-?([0-9])+\\$");

  public ActionPushFields(ActiveLink paramActiveLink, PushFieldsAction paramPushFieldsAction, int paramInt)
    throws GoatException
  {
    assert (paramPushFieldsAction != null);
    this.mIndex = paramInt;
    String str = paramActiveLink.getName();
    PushFieldsInfo[] arrayOfPushFieldsInfo = (PushFieldsInfo[])paramPushFieldsAction.getPushFieldsList().toArray(new PushFieldsInfo[0]);
    this.mPushFields = new PushField[arrayOfPushFieldsInfo.length];
    for (int i = 0; i < arrayOfPushFieldsInfo.length; i++)
      this.mPushFields[i] = new PushField(arrayOfPushFieldsInfo[i], str, paramInt, paramActiveLink.getServer(), null);
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
    return false;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < this.mPushFields.length; i++)
      localStringBuilder.append(this.mPushFields[i].toString());
    return localStringBuilder.toString();
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter1 = paramEmitter.js();
    FieldQuery localFieldQuery = this.mPushFields[0].mAssignField;
    localJSWriter1.statement("var " + localFieldQuery.getVarName() + " = new FieldList(" + "windowID" + ")");
    TreeSet localTreeSet = new TreeSet();
    for (int i = 0; i < this.mPushFields.length; i++)
      this.mPushFields[i].collectDynamicTargetFields(localTreeSet);
    if (localTreeSet.size() > 0)
    {
      JSWriter localJSWriter2 = new JSWriter();
      localJSWriter2.openList();
      Iterator localIterator = localTreeSet.iterator();
      for (int k = 0; localIterator.hasNext(); k++)
      {
        if (k > 0)
          localJSWriter2.listSep();
        localJSWriter2.append(((Integer)localIterator.next()).toString());
      }
      localJSWriter2.closeList();
      paramEmitter.js().statement("var PFTypes = new NDXGetPushFieldTypes(windowID, ExpandServerName(windowID," + JSWriter.escapeString(localFieldQuery.getServer()) + "), ExpandSchemaName(" + "windowID" + "," + JSWriter.escapeString(localFieldQuery.getSchema()) + "), " + localJSWriter2.toString() + ").result");
    }
    localJSWriter1.statement("LogWrite(\"&nbsp;&nbsp;&nbsp;&nbsp;Push-fields :\");");
    for (int j = 0; j < this.mPushFields.length; j++)
    {
      this.mPushFields[j].emitInterruptibleJS(paramEmitter);
      this.mPushFields[j].emitJS(paramEmitter);
    }
    paramEmitter.js().callFunction(isInterruptible(), localFieldQuery.getVarName(), "Push", new Object[] { localFieldQuery.getQualifier().emitEncodedAsString(), JSWriter.escapeString(localFieldQuery.getServer()), JSWriter.escapeString(localFieldQuery.getSchema()), "" + localFieldQuery.getMultiMatch(), "" + localFieldQuery.getNoMatch(), "" + (localFieldQuery.getFieldId() == 98 ? 1 : false), Boolean.valueOf(false) });
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    for (int i = 0; i < this.mPushFields.length; i++)
      this.mPushFields[i].bindToForm(paramCachedFieldMap);
  }

  public void simplify()
    throws GoatException
  {
    Simplifier localSimplifier = new Simplifier();
    for (int i = 0; i < this.mPushFields.length; i++)
      this.mPushFields[i].simplify(localSimplifier);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    ActionPushFields localActionPushFields = (ActionPushFields)super.clone();
    localActionPushFields.mPushFields = new PushField[this.mPushFields.length];
    for (int i = 0; i < this.mPushFields.length; i++)
      localActionPushFields.mPushFields[i] = ((PushField)this.mPushFields[i].clone());
    return localActionPushFields;
  }

  public static int getPushFieldsType(Field paramField)
  {
    return paramField.getDataType() == DataType.DIARY.toInt() ? 4 : paramField.getDataType();
  }

  public static String getPushFieldArgs(String paramString1, String paramString2, int paramInt, IEmitterFactory paramIEmitterFactory)
    throws GoatException
  {
    JSWriter localJSWriter = new JSWriter();
    FieldGraph localFieldGraph = FieldGraph.get(Form.get(paramString1, paramString2).getViewInfoByInference(null, false, false));
    GoatField localGoatField = localFieldGraph.getField(paramInt);
    if (localGoatField != null)
    {
      IGoatFieldEmitter localIGoatFieldEmitter = paramIEmitterFactory.getEmitter(localGoatField);
      localIGoatFieldEmitter.emitPushFieldProperties(localJSWriter);
    }
    return localJSWriter.toString();
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    for (int i = 0; i < this.mPushFields.length; i++)
      this.mPushFields[i].addToOutputNotes(paramOutputNotes);
  }

  private class Simplifier
    implements SimplifyState
  {
    private final ArrayList mFieldQueries = new ArrayList();

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
      paramFieldQuery.setIndex("" + ActionPushFields.this.mIndex + "_" + this.mFieldQueries.size());
      return null;
    }

    public SQLQuery crossReferenceSQLQuery(SQLQuery paramSQLQuery)
    {
      return null;
    }
  }

  private class PushField
    implements Cloneable, Serializable
  {
    private static final long serialVersionUID = -9143905094176511259L;
    protected FieldQuery mAssignField;
    private Assignment mAssignment;

    private PushField(PushFieldsInfo paramString1, String paramInt, int paramString2, String arg5)
      throws GoatException
    {
      String str;
      ActionPushFields.this.mServer = str;
      AssignFieldInfo localAssignFieldInfo = paramString1.getField();
      Qualifier localQualifier = new Qualifier(localAssignFieldInfo.getQualifier(), str);
      if (localAssignFieldInfo.getTag() == 6)
        this.mAssignField = new CurrencyFieldQuery(new CurrencyFieldID(localAssignFieldInfo.getCurrencyPart()), localAssignFieldInfo.getMultiMatchOption(), localAssignFieldInfo.getNoMatchOption(), localQualifier, localAssignFieldInfo.getServer(), localAssignFieldInfo.getForm());
      else
        this.mAssignField = new FieldQuery(localAssignFieldInfo.getFieldId(), localAssignFieldInfo.getMultiMatchOption(), localAssignFieldInfo.getNoMatchOption(), localQualifier, localAssignFieldInfo.getServer(), localAssignFieldInfo.getForm());
      AssignInfo localAssignInfo = paramString1.getAssign();
      Assignment.ForceFieldsToCurrentForm(localAssignInfo);
      this.mAssignment = new Assignment(localAssignInfo, paramInt, paramString2, localAssignFieldInfo.getFieldId(), localAssignFieldInfo.getServer());
    }

    private boolean isDynamicPushFields()
    {
      Matcher localMatcher = ActionPushFields.MDynamicPattern.matcher(this.mAssignField.getServer());
      if (localMatcher.matches())
        return true;
      localMatcher = ActionPushFields.MDynamicPattern.matcher(this.mAssignField.getSchema());
      return localMatcher.matches();
    }

    public String toString()
    {
      return "Push-field | " + this.mAssignField.toString() + " | " + this.mAssignment.toString();
    }

    public void emitJS(Emitter paramEmitter)
      throws GoatException
    {
      if (this.mAssignField.getFieldId() == 98)
      {
        paramEmitter.js().callFunction(false, "Field_BuildList", new Object[] { "windowID", this.mAssignField.getVarName(), "false", "false", "false", "false", "false", "false" });
      }
      else
      {
        JSWriter localJSWriter = new JSWriter();
        Operand localOperand = this.mAssignment.getExpression().getRightOperand();
        if ((localOperand != null) && (localOperand.getObjectType() == 14) && (localOperand.getValue().toString().equals("$DEFAULT$")))
        {
          localJSWriter.append("new DefaultKWType(").append(JSWriter.escapeString("$DEFAULT$")).append(")");
        }
        else
        {
          this.mAssignment.emitJS(new Emitter(localJSWriter, paramEmitter));
          if (!isDynamicPushFields())
          {
            Field localField = getTargetField();
            if (localField != null)
            {
              localJSWriter.append(".ConvertForPushFields(").append(ActionPushFields.getPushFieldsType(localField));
              String str = ActionPushFields.getPushFieldArgs(getTargetServer(), this.mAssignField.getSchema(), localField.getFieldID(), paramEmitter.getEmitterFactor());
              if (str.length() > 0)
                localJSWriter.append(",").append(str);
              localJSWriter.append(")");
            }
          }
          else
          {
            localJSWriter.append(".ConvertForDynamicPushFields(PFTypes, ").append(this.mAssignField.getFieldId()).append(")");
          }
        }
        paramEmitter.js().callFunction(false, this.mAssignField.getVarName() + ".AddField", new Object[] { "" + this.mAssignField.getFieldId(), localJSWriter.toString() });
        if (FormContext.get().getWorkflowLogging())
          paramEmitter.js().statement("LogWrite(\"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\" + F(windowID," + this.mAssignField.getFieldId() + ").GLabelOrName()+" + "\"(" + this.mAssignField.getFieldId() + ")\"" + " + \"&nbsp;=&nbsp;\"+" + this.mAssignField.getVarName() + ".F(" + this.mAssignField.getFieldId() + ")" + ((this.mAssignField.getFieldId() == 102) || (this.mAssignField.getFieldId() == 123) ? ".toString().replace(/./g, \"*\"))" : ")"));
      }
    }

    public void emitInterruptibleJS(Emitter paramEmitter)
      throws GoatException
    {
      this.mAssignment.emitInterruptibleJS(paramEmitter);
    }

    private String getTargetServer()
      throws GoatException
    {
      String str = this.mAssignField.getServer();
      if ("@".equals(str))
        str = ActionPushFields.this.mServer;
      return str;
    }

    private Field getTargetField()
      throws GoatException
    {
      if (isDynamicPushFields())
        return null;
      String str = this.mAssignField.getServer();
      if ("@".equals(str))
        str = ActionPushFields.this.mServer;
      Form localForm = Form.get(str, this.mAssignField.getSchema());
      if (localForm != null)
      {
        int i = this.mAssignField.getFieldId();
        return (Field)localForm.getCachedFieldMap(true).get(Integer.valueOf(i));
      }
      return null;
    }

    public void bindToForm(CachedFieldMap paramCachedFieldMap)
      throws GoatException
    {
      this.mAssignment.bindToForm(paramCachedFieldMap);
      if (!isDynamicPushFields())
      {
        Field localField = getTargetField();
        if (localField != null)
          this.mAssignment.bindToTarget(TypeMap.getOperandDataType(DataType.toDataType(localField.getDataType())), localField.getFieldID());
      }
      else
      {
        this.mAssignment.bindToDynamicTarget("PFTypes", this.mAssignField.getFieldId());
      }
    }

    public void simplify(SimplifyState paramSimplifyState)
      throws GoatException
    {
      this.mAssignField.simplify(paramSimplifyState);
      this.mAssignment.simplify(paramSimplifyState);
    }

    public Object clone()
      throws CloneNotSupportedException
    {
      PushField localPushField = (PushField)super.clone();
      localPushField.mAssignment = ((Assignment)this.mAssignment.clone());
      localPushField.mAssignField = ((FieldQuery)this.mAssignField.clone());
      return localPushField;
    }

    public void collectDynamicTargetFields(Set paramSet)
    {
      if (isDynamicPushFields())
        paramSet.add(Integer.valueOf(this.mAssignField.getFieldId()));
    }

    public void addToOutputNotes(OutputNotes paramOutputNotes)
    {
      this.mAssignment.addToOutputNotes(paramOutputNotes);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionPushFields
 * JD-Core Version:    0.6.1
 */