package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOperationInfo;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.CurrencyPartInfo;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierFromFieldInfo;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.StatusHistoryValueIndicator;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.action.Expression;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.WebWriter;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Qualifier
  implements FormAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -5296585880655413838L;
  private static transient Log mLog = Log.get(9);
  private Expression mQualifier;
  private QualifierInfo mOriginalARQualifier;
  private String mServerName;

  public Object clone()
    throws CloneNotSupportedException
  {
    Qualifier localQualifier = (Qualifier)super.clone();
    if (this.mQualifier != null)
      localQualifier.mQualifier = ((Expression)this.mQualifier.clone());
    if (this.mOriginalARQualifier != null)
      localQualifier.mOriginalARQualifier = this.mOriginalARQualifier;
    localQualifier.mServerName = this.mServerName;
    return localQualifier;
  }

  public Qualifier(QualifierInfo paramQualifierInfo, String paramString)
    throws GoatException
  {
    this.mServerName = paramString;
    this.mOriginalARQualifier = paramQualifierInfo;
    if (paramQualifierInfo == null)
      this.mQualifier = null;
    else
      this.mQualifier = compileQualifier(paramQualifierInfo);
  }

  public QualifierInfo getARQualifierInfo()
  {
    return this.mOriginalARQualifier;
  }

  public static Expression compileQualifier(QualifierInfo paramQualifierInfo)
    throws GoatException
  {
    assert (paramQualifierInfo != null);
    switch (paramQualifierInfo.getOperation())
    {
    case 1:
      return new Expression(2, compileQualifier(paramQualifierInfo.getLeftOperand()), compileQualifier(paramQualifierInfo.getRightOperand()));
    case 2:
      return new Expression(3, compileQualifier(paramQualifierInfo.getLeftOperand()), compileQualifier(paramQualifierInfo.getRightOperand()));
    case 3:
      return new Expression(4, (Expression)null, compileQualifier(paramQualifierInfo.getNotOperand()));
    case 5:
      return new Expression(new Operand(0, 21, Integer.valueOf(paramQualifierInfo.getFromFieldInfo().getValue())));
    case 0:
      return null;
    case 4:
      return new Expression(1, (Expression)null, compileRelationalOperation(paramQualifierInfo.getRelationalOperationInfo()));
    }
    throw new GoatException("Unsupported qualifer operation " + paramQualifierInfo.getOperation());
  }

  public static Expression compileRelationalOperation(RelationalOperationInfo paramRelationalOperationInfo)
    throws GoatException
  {
    assert (paramRelationalOperationInfo != null);
    int i;
    switch (paramRelationalOperationInfo.getOperation())
    {
    case 1:
      i = 5;
      break;
    case 2:
      i = 7;
      break;
    case 3:
      i = 8;
      break;
    case 4:
      i = 9;
      break;
    case 5:
      i = 10;
      break;
    case 7:
      i = 11;
      break;
    case 6:
      i = 6;
      break;
    default:
      throw new GoatException("Unsupported relational operation " + paramRelationalOperationInfo.getOperation());
    }
    return new Expression(i, compileArithmeticOrRelationalOperand(paramRelationalOperationInfo.getLeftOperand()), compileArithmeticOrRelationalOperand(paramRelationalOperationInfo.getRightOperand()));
  }

  public static Expression compileArithmeticOrRelationalOperand(ArithmeticOrRelationalOperand paramArithmeticOrRelationalOperand)
    throws GoatException
  {
    assert (paramArithmeticOrRelationalOperand != null);
    OperandType localOperandType = paramArithmeticOrRelationalOperand.getType();
    Object localObject;
    if (localOperandType.equals(OperandType.FIELDID))
    {
      localObject = new Operand(0, 12, paramArithmeticOrRelationalOperand.getValue());
      return new Expression((Operand)localObject);
    }
    if (localOperandType.equals(OperandType.VALUE))
      return new Expression(compileValue((Value)paramArithmeticOrRelationalOperand.getValue()));
    if (localOperandType.equals(OperandType.ARITHMETIC_OP))
      return compileArithmeticOperation((ArithmeticOperationInfo)paramArithmeticOrRelationalOperand.getValue());
    if (localOperandType.equals(OperandType.FIELDID_CURRENT))
    {
      localObject = new Operand(0, 13, paramArithmeticOrRelationalOperand.getValue());
      return new Expression((Operand)localObject);
    }
    if ((localOperandType.equals(OperandType.CURRENCY_FLD)) || (localOperandType.equals(OperandType.CURRENCY_FLD_CURRENT)))
    {
      localObject = (CurrencyPartInfo)paramArithmeticOrRelationalOperand.getValue();
      int i = 10;
      if (((CurrencyPartInfo)localObject).getPartTag() == 2)
        i = 4;
      else if ((((CurrencyPartInfo)localObject).getPartTag() == 0) || (((CurrencyPartInfo)localObject).getPartTag() == 4))
        i = 12;
      else if (((CurrencyPartInfo)localObject).getPartTag() == 3)
        i = 7;
      return new Expression(new Operand(i, localOperandType.equals(OperandType.CURRENCY_FLD) ? 23 : 25, new CurrencyFieldID((CurrencyPartInfo)localObject)));
    }
    if (localOperandType.equals(OperandType.STATUS_HISTORY))
    {
      localObject = new StatusHistoryFieldID((StatusHistoryValueIndicator)paramArithmeticOrRelationalOperand.getValue());
      return new Expression(new Operand(((StatusHistoryFieldID)localObject).getDataType(), 22, localObject));
    }
    throw new GoatException("Unsupported ArithmeticOrRelationalOperand type " + localOperandType.toString());
  }

  public static Operand compileValue(Value paramValue)
    throws GoatException
  {
    assert (paramValue != null);
    int i = TypeMap.getOperandDataType(paramValue.getDataType());
    switch (i)
    {
    case 0:
      throw new GoatException("Unsupported Value type " + paramValue.getDataType().toInt() + ", " + paramValue.toString());
    case 14:
      return Keyword.getKeywordOperand(paramValue.toString());
    case 1:
      return new Operand(0, 1, null);
    }
    GoatType localGoatType = GoatTypeFactory.create(paramValue, 0, null, null, null);
    return new Operand(localGoatType);
  }

  public static Expression compileArithmeticOperation(ArithmeticOperationInfo paramArithmeticOperationInfo)
    throws GoatException
  {
    assert (paramArithmeticOperationInfo != null);
    int i = paramArithmeticOperationInfo.getOperation();
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
      localExpression = compileArithmeticOrRelationalOperand(paramArithmeticOperationInfo.getLeftOperand());
    return new Expression(j, localExpression, compileArithmeticOrRelationalOperand(paramArithmeticOperationInfo.getRightOperand()));
  }

  public Expression getExpression()
  {
    return this.mQualifier;
  }

  public String toString()
  {
    if (this.mQualifier != null)
      return this.mQualifier.toString();
    return "";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mQualifier != null)
    {
      if (this.mQualifier.isInterruptible())
        throw new GoatException("Interruptible qualifier " + toString());
      this.mQualifier.emitJS(paramEmitter);
    }
  }

  public String emitAR()
    throws GoatException
  {
    return this.mQualifier == null ? "" : this.mQualifier.emitAR();
  }

  public void emitQualifier()
    throws GoatException
  {
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    collectFieldReferences(localHashSet1, localHashSet2);
    mLog.fine("QualifierA=" + (this.mQualifier == null ? "" : this.mQualifier.emitAR()));
    mLog.fine("QualifierS=" + (this.mQualifier == null ? "" : this.mQualifier.toString()));
  }

  public String emitARAsString()
    throws GoatException
  {
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    collectFieldReferences(localHashSet1, localHashSet2);
    return "{qual:\"" + emitAR() + "\"" + (localHashSet1.size() > 0 ? ",ids:" + localHashSet1.toString() : "") + (localHashSet2.size() > 0 ? ",extids:" + localHashSet2.toString() : "") + "}";
  }

  public String emitEncodedAsString()
    throws GoatException
  {
    assert (this.mServerName != null);
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    collectFieldReferences(localHashSet1, localHashSet2);
    String str = AREncodeARQualifierStruct(SessionData.get().getServerLogin(this.mServerName), this.mOriginalARQualifier);
    return "{qual:" + WebWriter.escapeString(str) + "" + (localHashSet1.size() > 0 ? ",ids:" + localHashSet1.toString() : "") + (localHashSet2.size() > 0 ? ",extids:" + localHashSet2.toString() : "") + "}";
  }

  public String emitEncodedAsStringAMF()
    throws GoatException
  {
    assert (this.mServerName != null);
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    collectFieldReferences(localHashSet1, localHashSet2);
    String str = AREncodeARQualifierStruct(SessionData.get().getServerLogin(this.mServerName), this.mOriginalARQualifier);
    return "{qual:" + WebWriter.escapeStringAMF(str) + "" + (localHashSet1.size() > 0 ? ",ids:" + localHashSet1.toString() : "") + (localHashSet2.size() > 0 ? ",extids:" + localHashSet2.toString() : "") + "}";
  }

  public String emitQualEncodedAsString()
    throws GoatException
  {
    assert (this.mServerName != null);
    HashSet localHashSet1 = new HashSet();
    HashSet localHashSet2 = new HashSet();
    collectFieldReferences(localHashSet1, localHashSet2);
    String str = AREncodeARQualifierStruct(SessionData.get().getServerLogin(this.mServerName), this.mOriginalARQualifier);
    return str;
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    if (this.mQualifier != null)
    {
      this.mQualifier.bindToForm(paramCachedFieldMap);
      this.mQualifier.simplify(null);
    }
  }

  public void simplify()
    throws GoatException
  {
    if (this.mQualifier != null)
      this.mQualifier.simplify(null);
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    if (this.mQualifier != null)
      this.mQualifier.addToOutputNotes(paramOutputNotes);
  }

  public void collectFieldReferences(Set paramSet1, Set paramSet2)
    throws GoatException
  {
    if (this.mQualifier != null)
      this.mQualifier.collectFieldReferences(paramSet1, paramSet2);
  }

  public static QualifierInfo ARDecodeARQualifierStruct(ARServerUser paramARServerUser, String paramString)
    throws GoatException
  {
    try
    {
      QualifierInfo localQualifierInfo = paramARServerUser.decodeARQualifierStruct(paramString);
      if (localQualifierInfo == null)
        localQualifierInfo = new QualifierInfo();
      return localQualifierInfo;
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
  }

  public static String AREncodeARQualifierStruct(ARServerUser paramARServerUser, QualifierInfo paramQualifierInfo)
    throws GoatException
  {
    try
    {
      String str = paramARServerUser.encodeARQualifierStruct(paramQualifierInfo);
      if (str == null)
        str = "";
      return str;
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Qualifier
 * JD-Core Version:    0.6.1
 */