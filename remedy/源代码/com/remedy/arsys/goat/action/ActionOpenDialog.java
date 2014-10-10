package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.AssignInfo;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldAssignInfo;
import com.bmc.arsys.api.MessageAction;
import com.bmc.arsys.api.OpenWindowAction;
import com.bmc.arsys.api.SortInfo;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.share.JSWriter;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionOpenDialog extends Action
{
  private static final long serialVersionUID = 5228951604169110093L;
  private String mServer;
  private String mSchemaName;
  private String mViewId;
  private boolean mCloseBox;
  private int mWindowMode;
  private FieldAssignInfo[] mInputValueFieldPairs;
  private FieldAssignInfo[] mOutputValueFieldPairs;
  private String mReportString;
  private String mTargetLocation;
  private Qualifier mQualifier;
  private ActionMessage mMessage;
  private long mPollingInterval;
  private boolean mNoMatchContinue;
  private boolean mSuppressEmptyList;
  private Expression[] mInputExpressions;
  private Expression[] mOutputExpressions;
  private SortInfo[] mSortOrder;
  private String mActiveLinkName;
  private int mActionIdx;
  private String mLastUpdateTime;
  private String epGuideServer;
  private String epGuideName;
  private static final Pattern MDefaultPattern = Pattern.compile("\\(CommitWindowFieldDefaults\\[([0-9]+)\\]\\)");
  private static final Pattern MFQPattern = Pattern.compile("fq[0-9]*\\.");
  protected static final int RPT_TYPE = 21;
  protected static final int RPT_NAME = 22;
  protected static final int RPT_ENTRYID = 27;
  protected static final int RPT_QRYORIDE = 28;
  protected static final int RPT_OP = 29;
  protected static final int RPT_LOC = 30;
  protected static final int RPT_CS = 31;
  protected static final int OPNWIN_ILINE = 32;

  public ActionOpenDialog(ActiveLink paramActiveLink, OpenWindowAction paramOpenWindowAction, int paramInt)
    throws GoatException
  {
    this.mActionIdx = paramInt;
    this.mLastUpdateTime = paramActiveLink.getLastUpdateTime();
    String str1 = paramOpenWindowAction.getServerName();
    String str2 = paramOpenWindowAction.getFormName();
    String str3 = paramOpenWindowAction.getVuiLabel();
    if ((str1 == null) || (str2 == null) || (str3 == null))
    {
      if (str1 == null)
        logAction("null server name");
      if (str2 == null)
        logAction("null schema name");
      if (str3 == null)
        logAction("null view id");
      throw new GoatException("Bad OpenDialogInfo");
    }
    this.mServer = str1.toString();
    if ("@".equals(this.mServer))
      this.mServer = paramActiveLink.getServer();
    this.mSchemaName = str2;
    this.mViewId = str3;
    this.epGuideServer = (this.epGuideName = null);
    this.mCloseBox = paramOpenWindowAction.isCloseBox();
    this.mWindowMode = paramOpenWindowAction.getWindowMode();
    this.mInputValueFieldPairs = null;
    this.mOutputValueFieldPairs = null;
    switch (this.mWindowMode)
    {
    case 0:
    case 20:
      this.mInputValueFieldPairs = ((FieldAssignInfo[])paramOpenWindowAction.getInputValueFieldPairs().toArray(new FieldAssignInfo[0]));
      this.mOutputValueFieldPairs = ((FieldAssignInfo[])paramOpenWindowAction.getOutputValueFieldPairs().toArray(new FieldAssignInfo[0]));
      break;
    case 1:
    case 2:
      this.mReportString = paramOpenWindowAction.getReportString();
      this.mTargetLocation = paramOpenWindowAction.getTargetLocation();
      this.mInputValueFieldPairs = ((FieldAssignInfo[])paramOpenWindowAction.getInputValueFieldPairs().toArray(new FieldAssignInfo[0]));
      break;
    case 9:
      this.mReportString = paramOpenWindowAction.getReportString();
      this.mActiveLinkName = paramActiveLink.getName();
      assert (this.mActiveLinkName != null);
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    case 15:
    case 16:
    case 17:
    case 18:
    case 19:
      this.mReportString = paramOpenWindowAction.getReportString();
      this.mTargetLocation = paramOpenWindowAction.getTargetLocation();
      this.mMessage = new ActionMessage(paramActiveLink, paramOpenWindowAction.getMsg(), paramInt);
      localObject = paramOpenWindowAction.getMsg();
      if (((MessageAction)localObject).getMessageText().length() == 0)
      {
        ((MessageAction)localObject).setMessageNum(9296);
        ((MessageAction)localObject).setMessageType(1);
        this.mMessage = new ActionMessage(paramActiveLink, (MessageAction)localObject, paramInt);
      }
      this.mSortOrder = ((SortInfo[])paramOpenWindowAction.getSortOrderList().toArray(new SortInfo[0]));
      this.mPollingInterval = paramOpenWindowAction.getPollinginterval();
      this.mQualifier = new Qualifier(paramOpenWindowAction.getQuery(), paramActiveLink.getServer());
      this.mNoMatchContinue = paramOpenWindowAction.isNoMatchContinue();
      this.mSuppressEmptyList = paramOpenWindowAction.isSuppressEmptyLst();
      break;
    default:
      throw new GoatException("Bad OpenDialogInfo.getWindowMode() : " + this.mWindowMode);
    }
    Object localObject = paramActiveLink.getName();
    int i = (!this.mServer.startsWith("$")) && (!this.mSchemaName.startsWith("$")) ? 1 : 0;
    int k;
    if ((this.mInputValueFieldPairs != null) && (this.mInputValueFieldPairs.length > 0))
    {
      this.mInputExpressions = new Expression[this.mInputValueFieldPairs.length];
      CachedFieldMap localCachedFieldMap = null;
      if (i != 0)
        try
        {
          localCachedFieldMap = Form.get(this.mServer, this.mSchemaName).getCachedFieldMap();
        }
        catch (GoatException localGoatException)
        {
        }
      for (k = 0; k < this.mInputValueFieldPairs.length; k++)
      {
        AssignInfo localAssignInfo = this.mInputValueFieldPairs[k].getAssignment();
        int m = this.mInputValueFieldPairs[k].getFieldId();
        Assignment.ForceFieldsToCurrentForm(localAssignInfo);
        this.mInputExpressions[k] = Assignment.compileAssignInfo(localAssignInfo, (String)localObject, paramInt, m, this.mServer);
        this.mInputExpressions[k].bindDefaultKeywordToField(m, "TargetWindowFieldDefaults");
        this.mInputExpressions[k].simplify(null);
        if (localCachedFieldMap != null)
        {
          Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(m));
          if (localField != null)
            this.mInputExpressions[k].bindToTarget(TypeMap.getOperandDataType(DataType.toDataType(localField.getDataType())), m);
        }
      }
    }
    if ((this.mOutputValueFieldPairs != null) && (this.mOutputValueFieldPairs.length > 0))
    {
      this.mOutputExpressions = new Expression[this.mOutputValueFieldPairs.length];
      for (int j = 0; j < this.mOutputValueFieldPairs.length; j++)
      {
        k = this.mOutputValueFieldPairs[j].getFieldId();
        this.mOutputExpressions[j] = Assignment.compileAssignInfo(this.mOutputValueFieldPairs[j].getAssignment(), (String)localObject, paramInt, k, this.mServer);
        this.mOutputExpressions[j].bindDefaultKeywordToField(k, "CommitWindowFieldDefaults");
      }
    }
  }

  public boolean canTerminate()
  {
    if (this.mWindowMode == 0)
      return true;
    if (this.mMessage != null)
      return this.mMessage.canTerminate();
    return false;
  }

  public boolean hasGoto()
  {
    if (this.mMessage != null)
      return this.mMessage.hasGoto();
    return false;
  }

  public boolean isInterruptible()
  {
    return true;
  }

  private String getSortOrderString(boolean paramBoolean)
  {
    String str = "[]";
    if (this.mSortOrder != null)
    {
      StringBuilder localStringBuilder = new StringBuilder("[");
      for (int i = 0; i < this.mSortOrder.length; i++)
      {
        if (i > 0)
          localStringBuilder.append(",");
        long l = this.mSortOrder[i].getSortOrder();
        if ((paramBoolean) && (l == 2L))
          l = -1L;
        localStringBuilder.append(this.mSortOrder[i].getFieldID()).append(",").append(l);
      }
      localStringBuilder.append("]");
      str = localStringBuilder.toString();
    }
    return str;
  }

  public String toString()
  {
    try
    {
      String str = getSortOrderString(false);
      switch (this.mWindowMode)
      {
      case 0:
      case 20:
        return "ARACTOpenDialog(" + JSWriter.escapeString(this.mServer) + ", " + JSWriter.escapeString(this.mSchemaName) + ", " + JSWriter.escapeString(this.mViewId) + ", " + this.mCloseBox + ", inmap, outmap," + this.epGuideServer + ',' + this.epGuideName + ")";
      case 1:
      case 2:
        return "ARACTOpenWindow(" + JSWriter.escapeString(this.mServer) + ", " + JSWriter.escapeString(this.mSchemaName) + ", " + JSWriter.escapeString(this.mViewId) + ", " + this.mTargetLocation + ", setDefaults, inmap, " + this.mWindowMode + ',' + this.epGuideServer + ',' + this.epGuideName + ")";
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
        return "ARACTOpenQueryWindow(" + JSWriter.escapeString(this.mServer) + ", " + JSWriter.escapeString(this.mSchemaName) + ", " + JSWriter.escapeString(this.mViewId) + ", " + this.mTargetLocation + ", " + this.mWindowMode + ", " + this.mQualifier.emitEncodedAsString() + ", " + this.mNoMatchContinue + ", " + getNoMatchCustomMessage() + ", " + (this.mSuppressEmptyList ? "true" : "false") + ", " + str + ", " + this.epGuideServer + ", " + this.epGuideName + ",res, " + (this.mMessage != null ? this.mMessage.toString() : "") + ", " + this.mPollingInterval + ")";
      case 9:
        return "ARACTOpenReportWindow(" + JSWriter.escapeString(this.mServer) + ", " + JSWriter.escapeString(this.mSchemaName) + ", " + JSWriter.escapeString(this.mViewId) + ", " + this.mTargetLocation + ", " + this.mWindowMode + ", " + this.mQualifier.emitEncodedAsString() + ", " + this.mNoMatchContinue + ", " + getNoMatchCustomMessage() + ", " + this.mSuppressEmptyList + ", " + ", " + str + ", repParams, " + this.epGuideServer + ", " + this.epGuideName + ", " + this.mActiveLinkName + ", " + this.mActionIdx + ", " + this.mLastUpdateTime + ")" + (this.mMessage != null ? this.mMessage.toString() : "");
      }
    }
    catch (GoatException localGoatException)
    {
    }
    return null;
  }

  public void tokenizeReportString(Emitter paramEmitter, String paramString)
    throws GoatException
  {
    int i = 0;
    JSWriter localJSWriter1 = paramEmitter.js();
    JSWriter localJSWriter2 = new JSWriter();
    localJSWriter2.startStatement("var repParams = ");
    localJSWriter2.openObj();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\001");
    int j = localStringTokenizer.countTokens();
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      i++;
      if ((i > 1) || ((str.charAt(0) >= '0') && (str.charAt(0) <= '9')))
        if ((i == j) && (str.startsWith("\n")) && (str.endsWith("\n")))
        {
          if (str.length() > 1)
            localJSWriter2.property("dt", str.substring(1, str.length() - 1));
        }
        else
        {
          int k = str.indexOf("=");
          if (k != -1)
            switch (Integer.parseInt(str.substring(0, 2)))
            {
            case 21:
              localJSWriter2.property("ty", str.substring(3));
              break;
            case 22:
              localJSWriter2.property("na", str.substring(3));
              break;
            case 27:
              localJSWriter2.property("id", str.substring(3));
              break;
            case 28:
              localJSWriter2.property("or", str.substring(3));
              break;
            case 29:
              localJSWriter2.property("op", str.substring(3));
              break;
            case 30:
              localJSWriter2.property("lc", str.substring(3));
              break;
            case 31:
              localJSWriter2.property("cs", str.substring(3));
              break;
            case 32:
              localJSWriter2.property("inline", (str.substring(3) != null) && (str.substring(3).equalsIgnoreCase("true")));
              break;
            case 23:
            case 24:
            case 25:
            case 26:
            }
        }
    }
    localJSWriter2.closeObj();
    localJSWriter2.endStatement();
    localJSWriter1.statement(localJSWriter2.toString());
  }

  private String getNoMatchCustomMessage()
  {
    if (this.mMessage == null)
      return "null";
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openObj();
    localJSWriter.property("t", this.mMessage.mType);
    localJSWriter.property("m", this.mMessage.mText);
    localJSWriter.property("n", this.mMessage.mNumber);
    localJSWriter.property("a", "");
    localJSWriter.closeObj();
    return localJSWriter.toString();
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter1 = paramEmitter.js();
    int i = 0;
    Object localObject1;
    Object localObject3;
    Object localObject2;
    if (this.mInputExpressions != null)
    {
      TreeSet localTreeSet = new TreeSet();
      for (int k = 0; k < this.mInputExpressions.length; k++)
        this.mInputExpressions[k].collectDefaultKeywordFieldIds(localTreeSet);
      if (localTreeSet.size() > 0)
      {
        JSWriter localJSWriter2 = new JSWriter();
        localJSWriter2.openList();
        localObject1 = localTreeSet.iterator();
        for (int n = 0; ((Iterator)localObject1).hasNext(); n++)
        {
          if (n > 0)
            localJSWriter2.listSep();
          localObject3 = (Integer)((Iterator)localObject1).next();
          localJSWriter2.append(((Integer)localObject3).toString());
        }
        localJSWriter2.closeList();
        localJSWriter1.statement("var TargetWindowFieldDefaults = new NDXGetFieldDefaults(windowID, ExpandServerName(windowID," + getStringArg(this.mServer) + "), ExpandSchemaName(" + "windowID" + "," + getStringArg(this.mSchemaName) + "), " + localJSWriter2.toString() + ").result");
      }
      if ((this.mInputValueFieldPairs.length == 1) && (this.mInputValueFieldPairs[0].getFieldId() == 97))
      {
        localJSWriter1.statement("var inmap=null");
        i = 1;
      }
      else
      {
        localJSWriter1.statement("var inmap=new Object()");
        localJSWriter1.statement("var val");
        for (int m = 0; m < this.mInputValueFieldPairs.length; m++)
        {
          localObject1 = this.mInputExpressions[m];
          localObject2 = new Emitter(new JSWriter(), paramEmitter);
          ((Expression)localObject1).emitJS((Emitter)localObject2);
          localJSWriter1.statement("val=" + ((Emitter)localObject2).js().toString());
          localJSWriter1.statement("inmap[" + this.mInputValueFieldPairs[m].getFieldId() + "]={t:val.type,v:val.toPrimitive(),a:val.args()}");
        }
      }
    }
    else
    {
      localJSWriter1.statement("var inmap=null");
    }
    if (this.mOutputExpressions != null)
    {
      localJSWriter1.statement("var outmap=new Object()");
      for (int j = 0; j < this.mOutputValueFieldPairs.length; j++)
      {
        Expression localExpression = this.mOutputExpressions[j];
        localObject1 = new Emitter(new JSWriter(), paramEmitter);
        localExpression.emitJS((Emitter)localObject1);
        localObject2 = ((Emitter)localObject1).js().toString();
        localObject3 = MFQPattern.matcher((CharSequence)localObject2);
        String str2 = ((Matcher)localObject3).replaceAll("dlgOut.");
        str2 = JSWriter.escapeString(str2);
        Matcher localMatcher = MDefaultPattern.matcher(str2);
        str2 = localMatcher.replaceAll("Datatype_Factory(\" + F(windowID,$1).GDefault().SerializeAsString() + \")");
        localJSWriter1.statement("outmap[" + this.mOutputValueFieldPairs[j].getFieldId() + "]=" + str2 + "");
      }
    }
    else
    {
      localJSWriter1.statement("var outmap=null");
    }
    String str1 = getSortOrderString(this.mWindowMode != 9);
    switch (this.mWindowMode)
    {
    case 0:
    case 20:
      localJSWriter1.callFunction(true, "ARACTOpenDialog", new Object[] { "windowID", getStringArg(this.mServer), getStringArg(this.mSchemaName), getStringArg(this.mViewId), this.mCloseBox ? "1" : "0", "inmap", "outmap", getStringArg(this.epGuideServer), getStringArg(this.epGuideName), "" + this.mWindowMode });
      break;
    case 1:
    case 2:
      if ((this.mReportString != null) && (this.mReportString.length() > 0))
        tokenizeReportString(paramEmitter, this.mReportString);
      else
        localJSWriter1.statement("var repParams={inline:false};");
      localJSWriter1.callFunction(true, "ARACTOpenWindow", new Object[] { "windowID", getStringArg(this.mServer), getStringArg(this.mSchemaName), getStringArg(this.mViewId), getStringArg(this.mTargetLocation), i != 0 ? "1" : "0", "inmap", "" + this.mWindowMode, getStringArg(this.epGuideServer), getStringArg(this.epGuideName), "repParams" });
      break;
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    case 15:
    case 16:
    case 17:
    case 18:
    case 19:
      localJSWriter1.statement("var res = {}");
      if ((this.mReportString != null) && (this.mReportString.length() > 0))
        tokenizeReportString(paramEmitter, this.mReportString);
      else
        localJSWriter1.statement("var repParams={inline:false}");
      localJSWriter1.callFunction(true, "ARACTOpenQueryWindow", new Object[] { "windowID", getStringArg(this.mServer), getStringArg(this.mSchemaName), getStringArg(this.mViewId), getStringArg(this.mTargetLocation), "" + this.mWindowMode, this.mQualifier.emitEncodedAsString(), this.mNoMatchContinue ? "1" : "0", getNoMatchCustomMessage(), this.mSuppressEmptyList ? "true" : "false", str1, getStringArg(this.epGuideServer), getStringArg(this.epGuideName), "res", Long.valueOf(this.mPollingInterval), "repParams" });
      if ((this.mMessage != null) && (!this.mNoMatchContinue))
        this.mMessage.emitConditionalJS(paramEmitter, "res.v");
      break;
    case 9:
      assert (this.mActiveLinkName != null);
      assert (this.mReportString != null);
      tokenizeReportString(paramEmitter, this.mReportString);
      localJSWriter1.callFunction(true, "ARACTOpenReportWindow", new Object[] { "windowID", getStringArg(this.mServer), getStringArg(this.mSchemaName), getStringArg(this.mViewId), getStringArg(this.mTargetLocation), "" + this.mWindowMode, this.mQualifier.emitEncodedAsString(), this.mNoMatchContinue ? "1" : "0", this.mSuppressEmptyList ? "1" : "0", str1, "repParams", getStringArg(this.epGuideServer), getStringArg(this.epGuideName), getStringArg(this.mActiveLinkName), String.valueOf(this.mActionIdx), getStringArg(this.mLastUpdateTime) });
    }
  }

  private static String getStringArg(String paramString)
  {
    return paramString == null ? "null" : JSWriter.escapeString(paramString);
  }

  public ActionOpenDialog cloneForEPStartAL(String paramString1, String paramString2)
    throws CloneNotSupportedException
  {
    ActionOpenDialog localActionOpenDialog = (ActionOpenDialog)clone();
    localActionOpenDialog.epGuideServer = paramString1;
    localActionOpenDialog.epGuideName = paramString2;
    return localActionOpenDialog;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionOpenDialog
 * JD-Core Version:    0.6.1
 */