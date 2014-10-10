package com.remedy.arsys.queryw;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FormType;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.CharField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.emit.html.EnumFieldEmitter;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public class QueryConfig
{
  private final String mGuid;
  private ServerLogin mServerUser;
  private final String mServer;
  private String mForm;
  private Map<Integer, Boolean> mFieldMap;
  private Map<Integer, FieldProperties> mFieldProperties;
  private final IEmitterFactory mEmitterFactory;
  private boolean mInitialized;
  static Log MFieldLog = Log.get(6);
  public static final String QCLOGHEADER = "QueryConfig:";
  private String mQueryInfoForm;
  private QueryQual mQQ;
  private final String CONFIG_FILE = "AR System Query Info";
  private static int GUID_ID = 179;
  private static int FNAME_ID = 53000;
  private static int ADD_FIELDS_BUTTON_ID = 53001;
  private static int FIELD_PANEL_ID = 53002;
  private static int DFIELD_ID_ID = 53004;
  private static int DFIELD_SHOWMENU_ID = 53005;
  private static int DFIELD_DESCRIPTION_ID = 53006;
  private static int DFIELD_NAME_ID = 53007;
  private static int FIELD_ADD_FIELD_ID = 53008;
  private static int FIELD_CLOSE_ID = 53009;
  private static int FIELD_ZTEMP_ID = 53010;
  private static int FIELD_ZTEMPINT_ID = 53011;
  private static int FIELD_ZTEMPINT2_ID = 53012;
  private static int FIELD_DELETE_ID = 53013;
  private static int FIELD_FORMID_ID = 53014;
  private static int FIELD_FIELDLISTTABLE_ID = 53015;
  private static int FIELD_COLFLFIELDDES_ID = 53016;
  private static int FIELD_COLFLSHOWMENU_ID = 53017;
  private static int FIELD_COLFLREQID_ID = 53018;
  private static int FIELD_COLFLFIELDID_ID = 53019;
  private static int FIELD_ID_ID = 53150;
  private static int FIELD_SHOWMENU_ID = 53151;
  private static int SHOWMENU_YES = 0;
  private static int SHOWMENU_NO = 1;
  private static int FIELD_DESCRIPTION_ID = 53152;
  private static final int[] QUERY_INFO_KEY = { FNAME_ID, FIELD_ID_ID, FIELD_SHOWMENU_ID };
  private final String QUERY_STRING = "'%1$s' = \"%2$s\"";

  public QueryConfig(String paramString1, String paramString2)
  {
    this.mGuid = paramString1;
    this.mServer = paramString2;
    this.mServerUser = null;
    this.mInitialized = false;
    this.mQQ = new QueryQual();
    this.mEmitterFactory = ((IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
    try
    {
      this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    }
    catch (GoatException localGoatException)
    {
      MFieldLog.log(Level.SEVERE, "QueryConfig:" + localGoatException.toString());
      return;
    }
    this.mFieldMap = new HashMap();
    this.mFieldProperties = new HashMap();
    this.mForm = null;
    this.mInitialized = getConfigForm();
  }

  private boolean getConfigForm()
  {
    boolean bool = false;
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(this.mServer);
      List localList;
      try
      {
        localList = localServerLogin.getListForm(0L, FormType.JOIN.toInt() | 0x400, null, QUERY_INFO_KEY);
      }
      catch (ARException localARException)
      {
        MFieldLog.log(Level.SEVERE, "QueryConfig:error finding config form, " + localARException.toString());
        return false;
      }
      if ((localList != null) && (localList.size() != 0))
      {
        this.mQueryInfoForm = ((String)localList.get(0));
        bool = true;
      }
    }
    catch (GoatException localGoatException)
    {
      MFieldLog.log(Level.SEVERE, "QueryConfig:could not find AR System Query Info file");
    }
    return bool;
  }

  public void init()
  {
    try
    {
      String str = String.format("'%1$s' = \"%2$s\"", new Object[] { String.valueOf(GUID_ID), this.mGuid });
      ServerLogin localServerLogin = ServerLogin.getAdmin(this.mServer);
      QualifierInfo localQualifierInfo = localServerLogin.parseQualification(this.mQueryInfoForm, str);
      int[] arrayOfInt = { FIELD_ID_ID, FIELD_SHOWMENU_ID, FNAME_ID };
      OutputInteger localOutputInteger = new OutputInteger();
      List localList = localServerLogin.getListEntryObjects(this.mQueryInfoForm, localQualifierInfo, 0, 0, null, arrayOfInt, false, localOutputInteger);
      if ((localList != null) && (localList.size() > 0))
      {
        int i = 1;
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          Entry localEntry = (Entry)localIterator.next();
          if (i != 0)
          {
            Value localValue = (Value)localEntry.get(Integer.valueOf(FNAME_ID));
            if (localValue != null)
              this.mForm = ((String)localValue.getValue());
            i = 0;
          }
          storeQueryField(localEntry);
        }
        this.mInitialized = getFieldProperties();
      }
      else
      {
        MFieldLog.log(Level.SEVERE, "QueryConfig:no fields for user " + this.mServerUser.getUser());
        this.mInitialized = false;
      }
    }
    catch (Exception localException)
    {
      MFieldLog.log(Level.SEVERE, "QueryConfig:" + localException.toString());
      this.mInitialized = false;
    }
  }

  private void storeQueryField(Entry paramEntry)
  {
    Value localValue1 = (Value)paramEntry.get(Integer.valueOf(FIELD_ID_ID));
    String str = localValue1 == null ? "-1" : (String)localValue1.getValue();
    Integer localInteger1 = new Integer(str);
    Value localValue2 = (Value)paramEntry.get(Integer.valueOf(FIELD_SHOWMENU_ID));
    Integer localInteger2 = localValue2 == null ? new Integer(-1) : (Integer)localValue2.getValue();
    int i = localInteger2.intValue();
    Boolean localBoolean = new Boolean(i == SHOWMENU_YES);
    this.mFieldMap.put(localInteger1, localBoolean);
  }

  private boolean getFieldProperties()
  {
    boolean bool = true;
    try
    {
      Form localForm = Form.get(this.mServer, this.mForm);
      Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(null, false, false);
      FieldGraph localFieldGraph = FieldGraph.get(localViewInfo);
      Iterator localIterator = this.mFieldMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Integer localInteger = (Integer)localEntry.getKey();
        try
        {
          GoatField localGoatField = localFieldGraph.getField(localInteger.intValue());
          if (localGoatField != null)
          {
            DataType localDataType = localGoatField.getMDataType();
            int i = localDataType.toInt();
            String str1 = localGoatField.getMLabel();
            String str2 = null;
            if (DataType.CHAR.equals(localDataType))
              str2 = ((CharField)localGoatField).getMCharMenu();
            String str3 = null;
            if (DataType.ENUM.equals(localDataType))
            {
              localObject = (EnumFieldEmitter)this.mEmitterFactory.getEmitter(localGoatField);
              if (localObject != null)
              {
                JSWriter localJSWriter = new JSWriter();
                ((EnumFieldEmitter)localObject).emitSelections(localJSWriter);
                str3 = localJSWriter.toString();
              }
            }
            Object localObject = new FieldProperties(i, localInteger.intValue(), str1, str2, str3);
            this.mFieldProperties.put(localInteger, localObject);
          }
        }
        catch (GoatException localGoatException2)
        {
          MFieldLog.log(Level.INFO, "QueryConfig:" + localGoatException2.toString() + ", field " + localInteger.toString());
          return false;
        }
      }
    }
    catch (GoatException localGoatException1)
    {
      MFieldLog.log(Level.INFO, "QueryConfig:" + localGoatException1.toString());
      return false;
    }
    return bool;
  }

  public boolean isInitialized()
  {
    return this.mInitialized;
  }

  public String getJSMenu()
  {
    if (!this.mInitialized)
      return null;
    JSWriter localJSWriter1 = new JSWriter();
    Iterator localIterator = this.mFieldMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Integer localInteger = (Integer)localEntry.getKey();
      Boolean localBoolean = (Boolean)localEntry.getValue();
      FieldProperties localFieldProperties = (FieldProperties)this.mFieldProperties.get(localInteger);
      if ((localFieldProperties != null) && (localFieldProperties.typeisChar()) && (localFieldProperties.hasMenu()) && (localBoolean.booleanValue()))
      {
        JSWriter localJSWriter2 = new JSWriter();
        boolean bool = localFieldProperties.getMenuDef(localJSWriter2);
        if (bool)
          localJSWriter1.append(localJSWriter2);
      }
    }
    return localJSWriter1.toString();
  }

  public String getJSFieldInfo()
  {
    if (!this.mInitialized)
      return null;
    JSWriter localJSWriter1 = new JSWriter();
    localJSWriter1.openObj();
    int i = 1;
    Iterator localIterator = this.mFieldMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Integer localInteger = (Integer)localEntry.getKey();
      Boolean localBoolean = (Boolean)localEntry.getValue();
      FieldProperties localFieldProperties = (FieldProperties)this.mFieldProperties.get(localInteger);
      if (localFieldProperties != null)
      {
        if (i == 0)
          localJSWriter1.listSep();
        else
          i = 0;
        JSWriter localJSWriter2 = new JSWriter();
        JSWriter localJSWriter3 = new JSWriter();
        localJSWriter3.openObj();
        localJSWriter3.property("t", localFieldProperties.getType());
        localJSWriter3.property("id", localFieldProperties.getFID());
        String str1 = null;
        if ((localFieldProperties.typeisChar()) && (localFieldProperties.hasMenu()) && (localBoolean.booleanValue()))
          str1 = localFieldProperties.getMenuName();
        localJSWriter3.property("m", str1);
        String str2 = null;
        if (localFieldProperties.typeisEnum())
          str2 = localFieldProperties.getEnumStr();
        localJSWriter3.property("e", str2);
        localJSWriter3.closeObj();
        localJSWriter2.property("'" + JSWriter.escape(localFieldProperties.getLabel()) + "'", localJSWriter3);
        localJSWriter1.append(localJSWriter2.toString());
      }
    }
    localJSWriter1.closeObj();
    return localJSWriter1.toString();
  }

  public boolean parseQual(String paramString)
  {
    if (!this.mInitialized)
      return false;
    if (!ARQualifier.isEncodedQualStr(paramString))
    {
      MFieldLog.log(Level.SEVERE, "QueryConfig:qualification not encoded " + paramString);
      return false;
    }
    boolean bool = false;
    try
    {
      ARQualifier localARQualifier = null;
      localARQualifier = new ARQualifier(Qualifier.ARDecodeARQualifierStruct(this.mServerUser, paramString));
      Form localForm = Form.get(this.mServer, this.mForm);
      Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(null, false, false);
      FieldGraph localFieldGraph = FieldGraph.get(localViewInfo);
      bool = parseQWQual(localARQualifier, localFieldGraph);
    }
    catch (Exception localException)
    {
      MFieldLog.log(Level.SEVERE, "QueryConfig:exception parsing qual, " + localException);
    }
    return bool;
  }

  private boolean parseQWQual(ARQualifier paramARQualifier, FieldGraph paramFieldGraph)
  {
    QualifierInfo localQualifierInfo = paramARQualifier.getQualInfo();
    boolean bool = parseQual(localQualifierInfo, paramFieldGraph, this.mQQ);
    return true;
  }

  private void logQualMsg(String paramString, QueryQual paramQueryQual)
  {
    String str = "QueryConfig:qualifier: " + paramString;
    MFieldLog.log(Level.INFO, str);
    paramQueryQual.pushMessage(str);
  }

  private boolean parseQual(QualifierInfo paramQualifierInfo, FieldGraph paramFieldGraph, QueryQual paramQueryQual)
  {
    boolean bool1 = false;
    int i = paramQualifierInfo.getOperation();
    boolean bool3;
    if (i == 4)
    {
      RelationalOperationInfo localRelationalOperationInfo = paramQualifierInfo.getRelationalOperationInfo();
      assert (localRelationalOperationInfo != null);
      bool3 = false;
      if (localRelationalOperationInfo != null)
      {
        bool3 = parseRelational(localRelationalOperationInfo, paramFieldGraph, paramQueryQual);
        bool1 = bool3;
      }
    }
    else if ((i == 1) || (i == 2))
    {
      paramQueryQual.setOP(i);
      assert ((paramQualifierInfo.getLeftOperand() != null) && (paramQualifierInfo.getRightOperand() != null));
      boolean bool2 = parseQual(paramQualifierInfo.getLeftOperand(), paramFieldGraph, paramQueryQual);
      if (bool2)
      {
        bool3 = parseQual(paramQualifierInfo.getRightOperand(), paramFieldGraph, paramQueryQual);
        bool1 = bool3;
      }
      else
      {
        bool1 = bool2;
      }
    }
    else
    {
      bool1 = false;
    }
    return bool1;
  }

  private boolean parseRelational(RelationalOperationInfo paramRelationalOperationInfo, FieldGraph paramFieldGraph, QueryQual paramQueryQual)
  {
    int i = paramRelationalOperationInfo.getOperation();
    if ((i == 8) || (i == 9))
    {
      logQualMsg("relational info not and/or," + i, paramQueryQual);
      return false;
    }
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = paramRelationalOperationInfo.getLeftOperand();
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = paramRelationalOperationInfo.getRightOperand();
    if (localArithmeticOrRelationalOperand1.getType() != OperandType.FIELDID)
    {
      logQualMsg("left hand side not field," + localArithmeticOrRelationalOperand1.getType(), paramQueryQual);
      return false;
    }
    if (localArithmeticOrRelationalOperand2.getType() != OperandType.VALUE)
    {
      logQualMsg("right hand side not value," + localArithmeticOrRelationalOperand2.getType(), paramQueryQual);
      return false;
    }
    try
    {
      Integer localInteger = (Integer)localArithmeticOrRelationalOperand1.getValue();
      int j = localInteger.intValue();
      GoatField localGoatField = paramFieldGraph.getField(j);
      DataType localDataType;
      if (localGoatField != null)
      {
        localDataType = localGoatField.getMDataType();
        if ((DataType.CURRENCY.equals(localDataType)) || (localInteger.intValue() == 15))
        {
          logQualMsg("currency/status history not supported," + localInteger.intValue(), paramQueryQual);
          return false;
        }
      }
      else
      {
        logQualMsg("field in qual not in fieldmap," + localInteger.intValue(), paramQueryQual);
        return true;
      }
      Value localValue = (Value)localArithmeticOrRelationalOperand2.getValue();
      if (DataType.KEYWORD.equals(localValue.getDataType()))
      {
        logQualMsg("keyword value not supported," + localValue.getIntValue(), paramQueryQual);
        return true;
      }
      FieldGraph.Node localNode = paramFieldGraph.getNode(j);
      boolean bool = false;
      if (localDataType.equals(DataType.CHAR))
      {
        FieldProperties localFieldProperties = (FieldProperties)this.mFieldProperties.get(Integer.valueOf(j));
        if ((localFieldProperties != null) && (localFieldProperties.hasMenu()) && (this.mFieldMap.containsKey(Integer.valueOf(j))))
          bool = ((Boolean)this.mFieldMap.get(Integer.valueOf(j))).booleanValue();
      }
      paramQueryQual.addtoQual(i, localDataType, localInteger.intValue(), localGoatField.getMLabel(), localValue, localNode, bool);
    }
    catch (GoatException localGoatException)
    {
      logQualMsg("parseRelational exception" + localGoatException.toString(), paramQueryQual);
      return false;
    }
    return true;
  }

  public List<QueryQual.OPRec> getOperations()
  {
    return this.mQQ.getOps();
  }

  public int getMatch()
  {
    return this.mQQ.getMatch();
  }

  class FieldProperties
  {
    int mType;
    int mfid;
    String mLabel;
    String mMenuName;
    String mEnumStr;

    public FieldProperties(int paramInt1, int paramString1, String paramString2, String paramString3, String arg6)
    {
      this.mType = paramInt1;
      this.mfid = paramString1;
      this.mLabel = paramString2;
      this.mMenuName = paramString3;
      Object localObject;
      this.mEnumStr = localObject;
    }

    public int getType()
    {
      return this.mType;
    }

    public int getFID()
    {
      return this.mfid;
    }

    public String getLabel()
    {
      return this.mLabel;
    }

    public boolean typeisEnum()
    {
      return this.mType == 6;
    }

    public boolean typeisChar()
    {
      return this.mType == 4;
    }

    public String getEnumStr()
    {
      return this.mEnumStr;
    }

    public boolean hasMenu()
    {
      return (this.mMenuName != null) && (this.mMenuName.length() > 0);
    }

    public String getMenuName()
    {
      return this.mMenuName;
    }

    public boolean getMenuDef(JSWriter paramJSWriter)
    {
      return getMenuDefInternal(paramJSWriter);
    }

    private boolean getMenuDefInternal(JSWriter paramJSWriter)
    {
      Menu.MKey localMKey = new Menu.MKey(QueryConfig.this.mServer, SessionData.get().getLocale(), this.mMenuName);
      Menu localMenu = null;
      try
      {
        localMenu = Menu.get(localMKey);
      }
      catch (GoatException localGoatException)
      {
        QueryConfig.MFieldLog.log(Level.INFO, "QueryConfig:menu " + this.mMenuName + "exception:" + localGoatException.toString());
        return false;
      }
      if (localMenu != null)
      {
        localMenu.emitJSDefinitionStatement(paramJSWriter, true);
        return true;
      }
      QueryConfig.MFieldLog.log(Level.INFO, "QueryConfig:menu " + this.mMenuName + " not found");
      return false;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.queryw.QueryConfig
 * JD-Core Version:    0.6.1
 */