package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.AssignInfo;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldAssignInfo;
import com.bmc.arsys.api.ServiceAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Operand;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServiceLocator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ActionService extends Action
{
  private static final long serialVersionUID = -175855157797252118L;
  private final int mIndex;
  private String mServer;
  private final String mSchemaName;
  private final int mRequestIdField;
  private final boolean mStaticFormAndServer;
  List<FieldAssignInfo> mAssignInput;
  List<FieldAssignInfo> mAssignOutput;
  List<Expression> mInputExpression = new ArrayList();
  List<Expression> mOutputExpression = new ArrayList();

  public ActionService(ActiveLink paramActiveLink, ServiceAction paramServiceAction, int paramInt)
    throws GoatException
  {
    this.mIndex = paramInt;
    this.mServer = paramServiceAction.getServerName();
    this.mSchemaName = paramServiceAction.getServiceForm();
    if ((this.mServer == null) || (this.mSchemaName == null))
    {
      if (this.mServer == null)
        logAction("null server name");
      if (this.mSchemaName == null)
        logAction("null schema name");
      throw new GoatException("ActionService requires server/form");
    }
    if ("@".equals(this.mServer))
      this.mServer = paramActiveLink.getServer();
    this.mStaticFormAndServer = ((!this.mServer.startsWith("$")) && (!this.mSchemaName.startsWith("$")));
    this.mRequestIdField = paramServiceAction.getRequestIdMap();
    String str = paramActiveLink.getName();
    this.mAssignInput = paramServiceAction.getInputFieldMapping();
    Object localObject;
    if (this.mAssignInput != null)
    {
      CachedFieldMap localCachedFieldMap = null;
      if (this.mStaticFormAndServer)
        try
        {
          localCachedFieldMap = Form.get(this.mServer, this.mSchemaName).getCachedFieldMap(true);
        }
        catch (GoatException localGoatException)
        {
          logAction("ActionService cannot bind to form:" + this.mSchemaName);
          throw new GoatException("Invalid server or form name", localGoatException);
        }
      for (int j = 0; j < this.mAssignInput.size(); j++)
      {
        localObject = (FieldAssignInfo)this.mAssignInput.get(j);
        AssignInfo localAssignInfo = ((FieldAssignInfo)localObject).getAssignment();
        if (!this.mStaticFormAndServer)
          Assignment.ForceFieldsToCurrentForm(localAssignInfo);
        int m = ((FieldAssignInfo)localObject).getFieldId();
        Expression localExpression2 = Assignment.compileAssignInfo(localAssignInfo, str, paramInt, m, this.mServer);
        localExpression2.simplify(null);
        if (this.mStaticFormAndServer)
        {
          Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(m));
          if (localField != null)
          {
            int n = localField.getDataType();
            localExpression2.bindToTarget(TypeMap.getOperandDataType(DataType.toDataType(n)), m);
          }
        }
        this.mInputExpression.add(localExpression2);
      }
    }
    this.mAssignOutput = paramServiceAction.getOutputFieldMapping();
    if (this.mAssignOutput != null)
      for (int i = 0; i < this.mAssignOutput.size(); i++)
      {
        FieldAssignInfo localFieldAssignInfo = (FieldAssignInfo)this.mAssignOutput.get(i);
        localObject = localFieldAssignInfo.getAssignment();
        int k = localFieldAssignInfo.getFieldId();
        Expression localExpression1 = Assignment.compileAssignInfo((AssignInfo)localObject, str, paramInt, k, this.mServer);
        this.mOutputExpression.add(localExpression1);
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
    return false;
  }

  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder("ARACTService(" + this.mServer + "," + this.mSchemaName + "," + this.mRequestIdField + "," + "inputField:");
    StringBuilder localStringBuilder2 = new StringBuilder();
    Expression localExpression;
    Emitter localEmitter;
    for (int i = 0; i < this.mInputExpression.size(); i++)
    {
      if (localStringBuilder2.length() > 0)
        localStringBuilder2.append(",");
      else
        localStringBuilder2.append("[");
      localExpression = (Expression)this.mInputExpression.get(i);
      if (localExpression != null)
      {
        localEmitter = new Emitter(new JSWriter(), (IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
        try
        {
          localExpression.emitJS(localEmitter);
          localStringBuilder2.append(((FieldAssignInfo)this.mAssignInput.get(i)).getFieldId()).append(":");
          localStringBuilder2.append(localEmitter.js().toString());
        }
        catch (GoatException localGoatException1)
        {
        }
      }
    }
    if (this.mInputExpression.size() > 0)
      localStringBuilder2.append("]");
    localStringBuilder1.append(localStringBuilder2);
    localStringBuilder1.append(",");
    localStringBuilder1.append("outputField:");
    localStringBuilder2 = new StringBuilder();
    for (i = 0; i < this.mOutputExpression.size(); i++)
    {
      if (localStringBuilder2.length() > 0)
        localStringBuilder2.append(",");
      else
        localStringBuilder2.append("[");
      localExpression = (Expression)this.mOutputExpression.get(i);
      if (localExpression != null)
      {
        localEmitter = new Emitter(new JSWriter(), (IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
        try
        {
          localExpression.emitJS(localEmitter);
          localStringBuilder2.append(((FieldAssignInfo)this.mAssignOutput.get(i)).getFieldId()).append(":");
          localStringBuilder2.append(localEmitter.js().toString());
        }
        catch (GoatException localGoatException2)
        {
        }
      }
    }
    if (this.mOutputExpression.size() > 0)
      localStringBuilder2.append("]");
    localStringBuilder1.append(localStringBuilder2);
    localStringBuilder1.append(")");
    return localStringBuilder1.toString();
  }

  Emitter dynInList()
  {
    if (this.mStaticFormAndServer)
      return null;
    if (this.mInputExpression.size() > 0)
    {
      Emitter localEmitter = new Emitter(new JSWriter(), (IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
      TreeSet localTreeSet = new TreeSet();
      for (int i = 0; i < this.mInputExpression.size(); i++)
      {
        int j = ((FieldAssignInfo)this.mAssignInput.get(i)).getFieldId();
        localTreeSet.add(Integer.valueOf(j));
      }
      JSWriter localJSWriter = new JSWriter();
      localJSWriter.openList();
      Iterator localIterator = localTreeSet.iterator();
      for (int k = 0; localIterator.hasNext(); k++)
      {
        if (k > 0)
          localJSWriter.listSep();
        localJSWriter.append(((Integer)localIterator.next()).toString());
      }
      localJSWriter.closeList();
      localEmitter.js().statement("var PFTypes = {}");
      localEmitter.js().appendln("try {");
      localEmitter.js().statement("PFTypes = new NDXGetPushFieldTypes(windowID, ExpandServerName(windowID," + JSWriter.escapeString(this.mServer) + "), ExpandSchemaName(" + "windowID" + "," + JSWriter.escapeString(this.mSchemaName) + "), " + localJSWriter.toString() + ").result");
      localEmitter.js().statement("}catch (exc){}");
      return localEmitter;
    }
    return null;
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    CachedFieldMap localCachedFieldMap = null;
    if (this.mStaticFormAndServer)
      try
      {
        localCachedFieldMap = Form.get(this.mServer, this.mSchemaName).getCachedFieldMap();
      }
      catch (GoatException localGoatException)
      {
        logAction("ActionService cannot bind to form:" + this.mSchemaName);
      }
    JSWriter localJSWriter1 = paramEmitter.js();
    Emitter localEmitter2;
    Object localObject2;
    if (this.mInputExpression.size() > 0)
    {
      if (!this.mStaticFormAndServer)
      {
        Emitter localEmitter1 = dynInList();
        if (localEmitter1 != null)
          localJSWriter1.append(localEmitter1.js().toString());
      }
      localJSWriter1.statement("var inmap=new Object()");
      localJSWriter1.statement("var val");
      localJSWriter1.statement("var desttype");
      for (int i = 0; i < this.mInputExpression.size(); i++)
      {
        localObject1 = (Expression)this.mInputExpression.get(i);
        String str2 = null;
        if (localObject1 != null)
        {
          int k = ((FieldAssignInfo)this.mAssignInput.get(i)).getFieldId();
          localEmitter2 = new Emitter(new JSWriter(), paramEmitter);
          localObject2 = ((Expression)localObject1).getRightOperand();
          JSWriter localJSWriter2;
          if ((localObject2 != null) && (((Operand)localObject2).getObjectType() == 14) && (((Operand)localObject2).getValue().toString().equals("$DEFAULT$")))
          {
            localJSWriter2 = new JSWriter();
            localJSWriter2.statement("val=new DefaultKWType(" + JSWriter.escapeString("$DEFAULT$") + ")");
            localJSWriter2.statement("inmap[" + k + "]={t:1,v:val,a:val.args()}");
            localJSWriter1.statement(localJSWriter2.toString());
          }
          else
          {
            ((Expression)localObject1).emitJS(localEmitter2);
            localJSWriter2 = new JSWriter();
            localJSWriter2.append("val=" + localEmitter2.js().toString());
            if (this.mStaticFormAndServer)
            {
              Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(k));
              if (localField != null)
              {
                int m = localField.getDataType();
                str2 = Integer.valueOf(m).toString();
                localJSWriter2.append(".ConvertForPushFields(").append(ActionPushFields.getPushFieldsType(localField));
                String str3 = ActionPushFields.getPushFieldArgs(this.mServer, this.mSchemaName, k, paramEmitter.getEmitterFactor());
                if (str3.length() > 0)
                  localJSWriter2.append(",").append(str3);
                localJSWriter2.append(")");
              }
            }
            else
            {
              str2 = "\"" + k + "\" in PFTypes ? PFTypes[" + k + "].t : \"dt\"";
              localJSWriter2.append(".ConvertForDynamicPushFields(PFTypes, ").append(k).append(")");
            }
            localJSWriter1.statement(localJSWriter2.toString());
            localJSWriter1.statement("inmap[" + k + "]={t:" + str2 + ",v:val,a:val.args()}");
          }
        }
      }
    }
    else
    {
      localJSWriter1.statement("var inmap=null");
    }
    String str1 = null;
    Object localObject1 = "";
    if (this.mOutputExpression.size() > 0)
    {
      localJSWriter1.statement("var outmap=new Object()");
      localJSWriter1.statement("var valout");
      for (int j = 0; j < this.mOutputExpression.size(); j++)
      {
        Expression localExpression = (Expression)this.mOutputExpression.get(j);
        if (localExpression != null)
        {
          localEmitter2 = new Emitter(new JSWriter(), paramEmitter);
          localExpression.emitJS(localEmitter2);
          localObject2 = localEmitter2.js().toString();
          localJSWriter1.statement("valout=" + JSWriter.escapeString((String)localObject2));
          localJSWriter1.statement("outmap[" + j + "]=" + "{f:" + ((FieldAssignInfo)this.mAssignOutput.get(j)).getFieldId() + ", e:valout}");
          if (str1 == null)
            str1 = localExpression.getUniqueFieldIds();
          if (((String)localObject1).length() == 0)
            localObject1 = localExpression.getVarName();
        }
      }
    }
    else
    {
      localJSWriter1.statement("var outmap=null");
    }
    if (((String)localObject1).length() == 0)
    {
      logAction("Service: no outMapPrefix");
      localObject1 = new String("xtemp");
    }
    if (str1 == null)
      str1 = "[]";
    Object[] arrayOfObject = new Object[8];
    arrayOfObject[0] = "windowID";
    arrayOfObject[1] = JSWriter.escapeString(this.mServer);
    arrayOfObject[2] = JSWriter.escapeString(this.mSchemaName);
    arrayOfObject[3] = Integer.valueOf(this.mRequestIdField);
    arrayOfObject[4] = "inmap";
    arrayOfObject[5] = "outmap";
    arrayOfObject[6] = str1;
    arrayOfObject[7] = JSWriter.escapeString((String)localObject1);
    localJSWriter1.callFunction(isInterruptible(), "ARACTService", arrayOfObject);
  }

  public void simplify()
    throws GoatException
  {
    Simplifier localSimplifier = new Simplifier();
    Iterator localIterator = this.mOutputExpression.iterator();
    while (localIterator.hasNext())
    {
      Expression localExpression = (Expression)localIterator.next();
      localExpression.simplify(localSimplifier);
    }
  }

  private class Simplifier
    implements SimplifyState
  {
    private static final long serialVersionUID = 2244615609942159430L;
    private final ArrayList<FieldQuery> mFieldQueries = new ArrayList();

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
      paramFieldQuery.setIndex("" + ActionService.this.mIndex + "_" + this.mFieldQueries.size());
      return null;
    }

    public SQLQuery crossReferenceSQLQuery(SQLQuery paramSQLQuery)
    {
      return null;
    }

    public String getVarName()
    {
      String str = "";
      if (this.mFieldQueries.size() > 0)
      {
        FieldQuery localFieldQuery = (FieldQuery)this.mFieldQueries.get(0);
        if (localFieldQuery != null)
          str = localFieldQuery.getVarName();
      }
      return str;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionService
 * JD-Core Version:    0.6.1
 */