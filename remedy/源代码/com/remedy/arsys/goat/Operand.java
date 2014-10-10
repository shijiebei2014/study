package com.remedy.arsys.goat;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.action.ActionProcess;
import com.remedy.arsys.goat.action.FieldQuery;
import com.remedy.arsys.goat.action.SQLQuery;
import com.remedy.arsys.goat.action.SimplifyState;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;

public class Operand
  implements FormAware, TargetAware, Cloneable, Serializable
{
  private static final long serialVersionUID = 6584948292939095741L;
  private static transient Log mLog;
  public static final int DATATYPE_NONE = 0;
  public static final int DATATYPE_NULL = 1;
  public static final int DATATYPE_INTEGER = 2;
  public static final int DATATYPE_ENUM = 3;
  public static final int DATATYPE_CHAR = 4;
  public static final int DATATYPE_REAL = 5;
  public static final int DATATYPE_DECIMAL = 6;
  public static final int DATATYPE_TIME = 7;
  public static final int DATATYPE_DATE = 8;
  public static final int DATATYPE_TOD = 9;
  public static final int DATATYPE_CURRENCY = 10;
  public static final int DATATYPE_DYNAMIC = 11;
  public static final int DATATYPE_MAX = 11;
  public static final int OBJECTTYPE_FIELDID = 12;
  public static final int OBJECTTYPE_FIELDID_CURRENT = 13;
  public static final int OBJECTTYPE_KEYWORD = 14;
  public static final int OBJECTTYPE_FUNCTION = 15;
  public static final int OBJECTTYPE_FIELDQUERY = 16;
  public static final int OBJECTTYPE_SQLQUERY = 17;
  public static final int OBJECTTYPE_TIME = 18;
  public static final int OBJECTTYPE_PROCESS = 19;
  public static final int OBJECTTYPE_SQL = 20;
  public static final int OBJECTTYPE_FROM_FIELD = 21;
  public static final int OBJECTTYPE_STATUS_HISTORY = 22;
  public static final int OBJECTTYPE_CURRENCY = 23;
  public static final int OBJECTTYPE_GOATTYPE = 24;
  public static final int OBJECTTYPE_CURRENCY_CURRENT = 25;
  public static final String[] MDataTypeNames = { "unbound", "null", "integer", "long", "char", "real", "decimal", "time", "date", "tod", "currency", "dynamic", "fieldid", "fieldidc", "keyword", "function", "fieldquery", "sqlquery", "time", "process", "sql", "from", "statushistory", "currency", "goattype", "currencyc" };
  static final String[] MDataTypeJSNames = { null, "NullType", "IntegerType", "EnumType", "CharType", "RealType", "DecimalType", "TimeType", "DateType", "TODType", "CurrencyType", null };
  private int mDataType;
  private int mObjectType;
  private Object mValue;
  private boolean mConstant;
  private String mFieldType;

  public Operand(int paramInt1, int paramInt2, Object paramObject)
  {
    this.mDataType = paramInt1;
    this.mObjectType = paramInt2;
    this.mValue = paramObject;
    if (this.mObjectType <= 11)
      this.mConstant = true;
    else
      this.mConstant = false;
    this.mFieldType = null;
  }

  public Operand(GoatType paramGoatType)
  {
    this.mDataType = paramGoatType.getOperandType();
    this.mObjectType = 24;
    this.mValue = paramGoatType;
    this.mConstant = paramGoatType.isConstant();
    this.mFieldType = null;
  }

  public void setDataType(int paramInt)
  {
    assert (paramInt <= 11);
    this.mDataType = paramInt;
  }

  public int getDataType()
  {
    return this.mDataType;
  }

  public int getObjectType()
  {
    return this.mObjectType;
  }

  public Object getValue()
  {
    return this.mValue;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{").append(MDataTypeNames[this.mDataType]).append("}").append(MDataTypeNames[this.mObjectType]).append(":");
    if (this.mFieldType != null)
      localStringBuilder.append(this.mFieldType).append(":");
    if (this.mValue != null)
    {
      String str = this.mValue.toString();
      int i = str.indexOf("</script>");
      if (i != -1)
        localStringBuilder.append(str.substring(0, i + 2)).append("+").append(str.substring(i + 2));
      else
        localStringBuilder.append(str);
    }
    else
    {
      localStringBuilder.append("null");
    }
    return localStringBuilder.toString();
  }

  public void emitJSAsRef(Emitter paramEmitter)
    throws GoatException
  {
    if ((this.mObjectType == 12) || (this.mObjectType == 13))
      paramEmitter.js().append("F(windowID," + this.mValue + ")");
    else
      paramEmitter.js().append("DummyField");
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    switch (this.mObjectType)
    {
    case 12:
      localJSWriter.append("F(windowID," + this.mValue + ").G()");
      break;
    case 13:
      if ((this.mDataType == 3) || (("Column".equals(this.mFieldType)) && (this.mDataType == 0)))
        localJSWriter.append("F(windowID," + this.mValue + ").GP()");
      else
        localJSWriter.append("F(windowID," + this.mValue + ").G()");
      break;
    case 14:
      ((Keyword)this.mValue).emitJS(paramEmitter);
      break;
    case 15:
      ((Function)this.mValue).emitJS(paramEmitter);
      break;
    case 16:
      ((FieldQuery)this.mValue).emitJS(paramEmitter);
      break;
    case 17:
      ((SQLQuery)this.mValue).emitJS(paramEmitter);
      break;
    case 18:
      localJSWriter.append(((Date)this.mValue).toString());
      break;
    case 19:
      ((ActionProcess)this.mValue).emitJS(paramEmitter);
      break;
    case 20:
      ((Function)this.mValue).emitJS(paramEmitter);
      break;
    case 21:
      localJSWriter.append("External(windowID," + this.mValue + ")");
      break;
    case 22:
      ((StatusHistoryFieldID)this.mValue).emitJS(paramEmitter);
      break;
    case 23:
    case 25:
      ((CurrencyFieldID)this.mValue).emitJS(paramEmitter);
      break;
    case 1:
      localJSWriter.append("Null");
      break;
    case 24:
      ((GoatType)this.mValue).emitJS(paramEmitter);
      break;
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    case 9:
    case 10:
    case 11:
    default:
      throw new GoatException("Operand: JS compilation failed for " + MDataTypeNames[this.mObjectType]);
    }
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mObjectType == 15)
      ((Function)this.mValue).emitInterruptibleJS(paramEmitter);
    else if (this.mObjectType == 16)
      ((FieldQuery)this.mValue).emitInterruptibleJS(paramEmitter);
    else if (this.mObjectType == 17)
      ((SQLQuery)this.mValue).emitInterruptibleJS(paramEmitter);
    else if (this.mObjectType == 19)
      ((ActionProcess)this.mValue).emitInterruptibleJS(paramEmitter);
  }

  public String getUniqueFieldIds()
    throws GoatException
  {
    if (this.mObjectType == 16)
      return ((FieldQuery)this.mValue).getUniqueFieldIds();
    if (this.mObjectType == 15)
      return ((Function)this.mValue).getUniqueFieldIds();
    return null;
  }

  public String getVarName()
  {
    if (this.mObjectType == 16)
      return ((FieldQuery)this.mValue).getVarName();
    if (this.mObjectType == 15)
      return ((Function)this.mValue).getVarName();
    return "";
  }

  public String emitAR()
    throws GoatException
  {
    String str = null;
    switch (this.mObjectType)
    {
    case 12:
    case 22:
    case 23:
      str = "'" + this.mValue.toString() + "'";
      break;
    case 21:
      str = "EXTERNAL($" + this.mValue.toString() + "$)";
      break;
    case 13:
    case 25:
      str = "$" + this.mValue.toString() + "$";
      break;
    case 14:
      str = this.mValue.toString();
      break;
    case 18:
      str = ((Date)this.mValue).toString();
      break;
    case 1:
      str = "$NULL$";
      break;
    case 24:
      str = ((GoatType)this.mValue).emitAR();
      break;
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    case 9:
    case 10:
    case 11:
    case 15:
    case 16:
    case 17:
    case 19:
    case 20:
    default:
      throw new GoatException("Operand: AR compilation failed for " + MDataTypeNames[this.mObjectType]);
    }
    return str;
  }

  public void bind(int paramInt)
    throws GoatException
  {
    if (this.mDataType == 0)
    {
      this.mDataType = paramInt;
    }
    else
    {
      String str = "Unable to rebind operand " + toString() + "(" + this.mDataType + "/" + paramInt + ")";
      mLog.severe(str);
      throw new GoatException(str);
    }
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    if ((this.mObjectType == 15) || (this.mObjectType == 16) || (this.mObjectType == 17))
    {
      ((Serializable)this.mValue).bindToForm(paramCachedFieldMap);
    }
    else if ((this.mObjectType == 12) || (this.mObjectType == 13))
    {
      Field localField = (Field)paramCachedFieldMap.get(this.mValue);
      if (localField == null)
      {
        mLog.warning("Missing fieldid " + this.mValue + " in operand binding");
        assert (this.mFieldType == null);
        this.mDataType = 1;
        this.mObjectType = 1;
        this.mValue = null;
        this.mConstant = true;
      }
      else
      {
        DataType localDataType = DataType.toDataType(localField.getDataType());
        this.mFieldType = TypeMap.mapDataType(localDataType, ((Integer)this.mValue).intValue());
        int i = TypeMap.getOperandDataType(localDataType);
        if (i != 0)
          bind(i);
      }
    }
  }

  public void bindToTarget(int paramInt1, int paramInt2)
    throws GoatException
  {
    if ((this.mObjectType == 15) || (this.mObjectType == 16) || (this.mObjectType == 17) || (this.mObjectType == 14))
      ((TargetAware)this.mValue).bindToTarget(paramInt1, paramInt2);
    else if ((this.mObjectType == 12) || (this.mObjectType != 13));
  }

  public boolean isConstant()
  {
    return this.mConstant;
  }

  public void simplify(SimplifyState paramSimplifyState)
    throws GoatException
  {
    if (this.mObjectType == 15)
      ((Function)this.mValue).simplify(paramSimplifyState);
    else if (this.mObjectType == 16)
      ((FieldQuery)this.mValue).simplify(paramSimplifyState);
    else if (this.mObjectType == 17)
      ((SQLQuery)this.mValue).simplify(paramSimplifyState);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    Operand localOperand = (Operand)super.clone();
    if (this.mObjectType == 15)
      localOperand.mValue = ((Function)this.mValue).clone();
    else if (this.mObjectType == 16)
      localOperand.mValue = ((FieldQuery)this.mValue).clone();
    else if (this.mObjectType == 17)
      localOperand.mValue = ((SQLQuery)this.mValue).clone();
    else if (this.mObjectType == 14)
      localOperand.mValue = ((Keyword)this.mValue).clone();
    return localOperand;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    if (this.mObjectType == 19)
      ((ActionProcess)this.mValue).addToOutputNotes(paramOutputNotes);
    else if (this.mObjectType == 15)
      ((Function)this.mValue).addToOutputNotes(paramOutputNotes);
  }

  public boolean isInterruptible()
  {
    if (this.mObjectType == 15)
      return ((Function)this.mValue).isInterruptible();
    if (this.mObjectType == 16)
      return ((FieldQuery)this.mValue).isInterruptible();
    if (this.mObjectType == 17)
      return ((SQLQuery)this.mValue).isInterruptible();
    if (this.mObjectType == 19)
      return ((ActionProcess)this.mValue).isInterruptible();
    return false;
  }

  public void collectFieldReferences(Set paramSet1, Set paramSet2)
    throws GoatException
  {
    switch (this.mObjectType)
    {
    case 13:
      paramSet1.add(this.mValue);
      break;
    case 25:
      paramSet1.add(Integer.valueOf(((CurrencyFieldID)this.mValue).getFieldID()));
      break;
    case 14:
      paramSet1.add(Integer.valueOf(((Keyword)this.mValue).getAsFieldID()));
      break;
    case 21:
      paramSet2.add(this.mValue);
    }
  }

  public static String escapeQuotedString(String paramString)
  {
    paramString = paramString.replaceAll("\"", "\\\\\"");
    paramString = paramString.replaceAll("\n", "\\\\n");
    return paramString;
  }

  public void bindDefaultKeywordToField(int paramInt, String paramString)
  {
    if (this.mObjectType == 14)
      ((Keyword)this.mValue).bindDefaultKeywordToField(paramInt, paramString);
  }

  public void collectDefaultKeywordFieldIds(Set paramSet)
  {
    if (this.mObjectType == 14)
      ((Keyword)this.mValue).collectDefaultKeywordFieldIds(paramSet);
  }

  static
  {
    mLog = Log.get(9);
    mLog.setUseParentHandlers(false);
    mLog.setLevel(Level.OFF);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Operand
 * JD-Core Version:    0.6.1
 */