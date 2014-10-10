package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARQualifierHelper;
import com.bmc.arsys.api.ArithmeticOperationInfo;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.CharacterFieldLimit;
import com.bmc.arsys.api.CurrencyPartInfo;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DisplayInstanceMap;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FuncCurrencyInfo;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierFromFieldInfo;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ARQualifier
  implements Serializable
{
  private static final long serialVersionUID = -8401970432903421131L;
  private static final int MAX_AR_COND_OP_CODE = 5;
  private static final int MIN_AR_COND_OP_CODE = 0;
  private QualifierInfo mQualInfo;
  private static transient Log mLog = Log.get(9);
  private static Map mRelOpCodesMap = Collections.unmodifiableMap(localHashMap);

  public ARQualifier(QualifierInfo paramQualifierInfo)
    throws GoatException
  {
    this.mQualInfo = paramQualifierInfo;
  }

  public ARQualifier(QualifierInfo paramQualifierInfo, Entry paramEntry)
    throws GoatException
  {
    this.mQualInfo = paramQualifierInfo;
    substituteFields(paramEntry);
  }

  public ARQualifier(ServerLogin paramServerLogin, String paramString, Entry paramEntry, Map paramMap1, Map paramMap2, int paramInt)
    throws GoatException
  {
    parseQualification(paramServerLogin, paramString, paramMap1, paramMap2, paramInt, 0);
    substituteFields(paramEntry);
  }

  public ARQualifier(ServerLogin paramServerLogin, String paramString, Map paramMap1, Map paramMap2, int paramInt)
    throws GoatException
  {
    parseQualification(paramServerLogin, paramString, paramMap1, paramMap2, paramInt, 0);
  }

  public ARQualifier(ServerLogin paramServerLogin, Entry paramEntry, Map paramMap, int paramInt)
    throws GoatException
  {
    this.mQualInfo = constructQBEQualification(paramServerLogin, paramEntry, paramMap, paramInt);
  }

  public ARQualifier(ServerLogin paramServerLogin, String paramString, ARQualifierHelper paramARQualifierHelper)
    throws GoatException
  {
    parseQualification(paramServerLogin, paramString, paramARQualifierHelper);
  }

  public static final boolean isEncodedQualStr(String paramString)
  {
    int i;
    return (paramString != null) && (paramString.length() > 2) && ((i = paramString.charAt(0) - '0') <= 5) && (i >= 0) && (paramString.charAt(1) == '\\');
  }

  private void parseQualification(ServerLogin paramServerLogin, String paramString, ARQualifierHelper paramARQualifierHelper)
    throws GoatException
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(9);
    try
    {
      this.mQualInfo = paramARQualifierHelper.parseQualification(paramServerLogin.getLocale(), paramString, paramServerLogin.getTimeZone());
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
  }

  private void parseQualification(ServerLogin paramServerLogin, String paramString, Map paramMap1, Map paramMap2, int paramInt1, int paramInt2)
    throws GoatException
  {
    ArrayList localArrayList1 = new ArrayList(paramMap1.values());
    ArrayList localArrayList2 = null;
    if (paramMap2 != null)
      localArrayList2 = new ArrayList(paramMap2.values());
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(9);
    try
    {
      if ((localArrayList2 == null) && (paramInt2 != 0))
        this.mQualInfo = paramServerLogin.parseQualification(paramString, localArrayList1, null, paramInt1);
      else
        this.mQualInfo = paramServerLogin.parseQualification(paramString, localArrayList1, localArrayList2, paramInt1);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
  }

  public QualifierInfo getQualInfo()
  {
    return this.mQualInfo;
  }

  private void substituteFields(Entry paramEntry)
    throws GoatException
  {
    this.mQualInfo = substituteQualification(this.mQualInfo, paramEntry, null);
  }

  public void substituteExternal(Map paramMap)
    throws GoatException
  {
    this.mQualInfo = substituteQualification(this.mQualInfo, null, paramMap);
  }

  private static QualifierInfo substituteQualification(QualifierInfo paramQualifierInfo, Map paramMap1, Map paramMap2)
    throws GoatException
  {
    switch (paramQualifierInfo.getOperation())
    {
    case 1:
    case 2:
      return new QualifierInfo(paramQualifierInfo.getOperation(), substituteQualification(paramQualifierInfo.getLeftOperand(), paramMap1, paramMap2), substituteQualification(paramQualifierInfo.getRightOperand(), paramMap1, paramMap2));
    case 3:
      return new QualifierInfo(paramQualifierInfo.getOperation(), substituteQualification(paramQualifierInfo.getNotOperand(), paramMap1, paramMap2), null);
    case 5:
      if (paramMap2 != null)
      {
        int i = paramQualifierInfo.getFromFieldInfo().getValue();
        Object localObject = paramMap2.get(Integer.valueOf(i));
        if (localObject != null)
        {
          QualifierInfo localQualifierInfo = ((ARQualifier)localObject).getQualInfo();
          if (localQualifierInfo == null)
            return new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(new Value(1)), new ArithmeticOrRelationalOperand(new Value(1))));
          return localQualifierInfo;
        }
      }
      return paramQualifierInfo;
    case 0:
      return paramQualifierInfo;
    case 4:
      if (paramMap1 == null)
        return paramQualifierInfo;
      RelationalOperationInfo localRelationalOperationInfo = paramQualifierInfo.getRelationalOperationInfo();
      switch (localRelationalOperationInfo.getOperation())
      {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        return new QualifierInfo(new RelationalOperationInfo(localRelationalOperationInfo.getOperation(), substituteOperand(localRelationalOperationInfo.getLeftOperand(), paramMap1), substituteOperand(localRelationalOperationInfo.getRightOperand(), paramMap1)));
      }
      throw new GoatException("Unsupported relational operation " + localRelationalOperationInfo.getOperation());
    }
    throw new GoatException("Unsupported qualifer operation " + paramQualifierInfo.getOperation());
  }

  private static ArithmeticOrRelationalOperand substituteOperand(ArithmeticOrRelationalOperand paramArithmeticOrRelationalOperand, Map paramMap)
    throws GoatException
  {
    OperandType localOperandType = paramArithmeticOrRelationalOperand.getType();
    if (OperandType.ARITHMETIC_OP.equals(localOperandType))
      return new ArithmeticOrRelationalOperand(substituteArithmeticOperation((ArithmeticOperationInfo)paramArithmeticOrRelationalOperand.getValue(), paramMap));
    Object localObject1;
    Object localObject2;
    if (OperandType.FIELDID_CURRENT.equals(localOperandType))
    {
      localObject1 = (Integer)paramArithmeticOrRelationalOperand.getValue();
      localObject2 = (Value)paramMap.get(localObject1);
      if (localObject2 != null)
        return new ArithmeticOrRelationalOperand((Value)localObject2);
    }
    else if (OperandType.VALUE.equals(localOperandType))
    {
      localObject1 = (Value)paramArithmeticOrRelationalOperand.getValue();
      if (((Value)localObject1).getDataType().equals(DataType.KEYWORD))
      {
        localObject2 = Keyword.getKeyword(((Value)localObject1).toString());
        if (localObject2 != null)
        {
          if (((Keyword)localObject2).isTimeRelated())
            return new ArithmeticOrRelationalOperand((Value)localObject1);
          int j = ((Keyword)localObject2).getAsFieldID();
          localObject1 = (Value)paramMap.get(Integer.valueOf(j));
          if (localObject1 != null)
            return new ArithmeticOrRelationalOperand((Value)localObject1);
        }
      }
    }
    else if (OperandType.CURRENCY_FLD_CURRENT.equals(localOperandType))
    {
      localObject1 = (CurrencyPartInfo)paramArithmeticOrRelationalOperand.getValue();
      int i = ((CurrencyPartInfo)localObject1).getFieldId();
      Value localValue = (Value)paramMap.get(Integer.valueOf(i));
      if (localValue != null)
      {
        if (localValue.getDataType().equals(DataType.CURRENCY))
          switch (((CurrencyPartInfo)localObject1).getPartTag())
          {
          case 0:
            break;
          case 1:
            localValue = new Value(((CurrencyValue)localValue.getValue()).getValue());
            break;
          case 2:
            localValue = new Value(((CurrencyValue)localValue.getValue()).getCurrencyCode());
            break;
          case 3:
            localValue = new Value(((CurrencyValue)localValue.getValue()).getConversionDate());
            break;
          case 4:
            List localList = ((CurrencyValue)localValue.getValue()).getFuncCurrencyList();
            Iterator localIterator = localList.iterator();
            while (localIterator.hasNext())
            {
              FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator.next();
              if (localFuncCurrencyInfo.getCurrencyCode().equals(((CurrencyPartInfo)localObject1).getCurrencyCode()))
                localValue = new Value(localFuncCurrencyInfo.getValue());
            }
          }
        return new ArithmeticOrRelationalOperand(localValue);
      }
    }
    return paramArithmeticOrRelationalOperand;
  }

  private static ArithmeticOperationInfo substituteArithmeticOperation(ArithmeticOperationInfo paramArithmeticOperationInfo, Map paramMap)
    throws GoatException
  {
    switch (paramArithmeticOperationInfo.getOperation())
    {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
      return new ArithmeticOperationInfo(paramArithmeticOperationInfo.getOperation(), substituteOperand(paramArithmeticOperationInfo.getLeftOperand(), paramMap), substituteOperand(paramArithmeticOperationInfo.getRightOperand(), paramMap));
    case 6:
      return new ArithmeticOperationInfo(paramArithmeticOperationInfo.getOperation(), null, substituteOperand(paramArithmeticOperationInfo.getRightOperand(), paramMap));
    }
    throw new GoatException("Unsupported ArithmeticOperationInfo " + paramArithmeticOperationInfo.getOperation());
  }

  private static QualifierInfo appendQBEField(ServerLogin paramServerLogin, QualifierInfo paramQualifierInfo, Field paramField, int paramInt, Value paramValue)
    throws GoatException
  {
    RelationalOperationInfo localRelationalOperationInfo;
    if (paramField.getDataType() == DataType.CURRENCY.toInt())
      localRelationalOperationInfo = new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(new CurrencyPartInfo(paramField.getFieldID(), 0, "")), new ArithmeticOrRelationalOperand(new Value()));
    else
      localRelationalOperationInfo = new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(paramField.getFieldID()), new ArithmeticOrRelationalOperand(new Value()));
    QualifierInfo localQualifierInfo = new QualifierInfo(localRelationalOperationInfo);
    if (paramValue.getDataType().equals(DataType.NULL))
    {
      if (paramQualifierInfo.getOperation() == 0)
        return localQualifierInfo;
      return new QualifierInfo(1, paramQualifierInfo, localQualifierInfo);
    }
    String str1 = paramValue.toString().trim();
    int i = 0;
    int j = 0;
    if ((paramInt > 0) && (paramField.getDataType() == DataType.CHAR.toInt()) && (paramField.getFieldID() != 1) && (paramField.getDisplayInstance() != null))
    {
      Value localValue = paramField.getDisplayInstance().getProperty(paramInt, Integer.valueOf(67));
      if ((localValue != null) && (localValue.getValue() != null) && ((localValue.getValue() instanceof Integer)))
      {
        int m = ((Integer)localValue.getValue()).intValue();
        j = (m == 4) || (m == 5) ? 1 : 0;
      }
    }
    int k;
    if (j != 0)
      while (i < str1.length())
      {
        k = str1.charAt(i);
        String str3 = "";
        if (i + 4 < str1.length())
          str3 = str1.substring(i, i + 4);
        if ((str3.equals("&lt;")) || (str3.equals("&gt;")))
          i += 3;
        else
          if ((k != 61) && (k != 33))
            break;
        i++;
      }
    while (i < str1.length())
    {
      k = str1.charAt(i);
      if ((k != 61) && (k != 33) && (k != 62) && (k != 60))
        break;
      i++;
    }
    String str2 = str1.substring(0, i);
    int n = 1;
    int i1 = 0;
    boolean bool = false;
    Object localObject;
    if (mRelOpCodesMap.containsKey(str2))
    {
      str1 = str1.substring(i).trim();
      localObject = (Integer)mRelOpCodesMap.get(str2);
      n = localObject != null ? ((Integer)localObject).intValue() : 0;
      i1 = 1;
    }
    else if (paramField.getDataType() == DataType.CHAR.toInt())
    {
      localObject = (CharacterFieldLimit)paramField.getFieldLimit();
      int i2 = ((CharacterFieldLimit)localObject).getMaxLength();
      int i3 = str1.length();
      int i4 = 1;
      int i6;
      if ((paramField.getFieldID() == 1) && (i3 < i2) && (i2 != 1))
        try
        {
          Integer.parseInt(str1);
          String str4 = paramField.getDefaultValue().toString();
          if (str4 == null)
            str4 = "";
          i6 = i2 - str4.length() - i3;
          StringBuilder localStringBuilder = new StringBuilder();
          if (i2 > 1)
          {
            localStringBuilder.append(str4);
            for (int i7 = 0; i7 < i6; i7++)
              localStringBuilder.append("0");
          }
          localStringBuilder.append(str1);
          str1 = localStringBuilder.toString();
          i4 = 0;
        }
        catch (NumberFormatException localNumberFormatException2)
        {
        }
      if (i4 != 0)
      {
        int i5 = ((CharacterFieldLimit)localObject).getQBEMatch();
        i6 = str1.length();
        if ((i5 == 1) || ((i6 > 0) && ((str1.charAt(0) == '%') || (str1.charAt(i6 - 1) == '%'))))
        {
          n = 7;
          bool = Group.isGroupField(paramField.getFieldID());
          if (bool)
            str1 = "%;" + str1 + "%";
          else if (i5 == 1)
            str1 = "%" + str1 + "%";
        }
        else if (i5 == 2)
        {
          n = 7;
          str1 = str1 + "%";
        }
        else
        {
          n = 1;
        }
      }
    }
    else
    {
      n = 1;
    }
    if (paramField.getDataType() == DataType.DIARY.toInt())
    {
      if (n == 1)
        n = 7;
      str1 = "%" + str1 + "%";
    }
    localRelationalOperationInfo.setOperation(n);
    if ((paramField.getDataType() == DataType.CHAR.toInt()) || (paramField.getDataType() == DataType.DIARY.toInt()))
    {
      if ((i1 != 0) && (paramField.getFieldID() == 1))
        try
        {
          Integer.parseInt(str1);
          localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(GoatTypeFactory.create(str1, 2, paramField.getFieldID()).toValue()));
        }
        catch (NumberFormatException localNumberFormatException1)
        {
          localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(new Value(str1)));
        }
      else if ((str1 == null) || (str1.length() == 0))
        localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(new Value()));
      else
        localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(new Value(str1)));
    }
    else if (i1 == 0)
      localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(paramValue));
    else if ((str1 == null) || (str1.length() == 0))
      localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(new Value()));
    else
      localRelationalOperationInfo.setRightOperand(new ArithmeticOrRelationalOperand(GoatTypeFactory.create(str1, paramField.getDataType(), paramField.getFieldID()).toValue()));
    if (paramQualifierInfo.getOperation() == 0)
      return localQualifierInfo;
    return new QualifierInfo(1, paramQualifierInfo, localQualifierInfo);
  }

  private static QualifierInfo constructQBEQualification(ServerLogin paramServerLogin, Entry paramEntry, Map paramMap, int paramInt)
    throws GoatException
  {
    QualifierInfo localQualifierInfo = new QualifierInfo();
    Iterator localIterator = paramEntry.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Field localField = (Field)paramMap.get(Integer.valueOf(((Integer)localEntry.getKey()).intValue()));
      if (localField == null)
        throw new GoatException("Badly formatted backchannel request (non-existant field)");
      DataType localDataType = DataType.toDataType(localField.getDataType());
      if ((TypeMap.isValidForQBE(localDataType, ((Integer)localEntry.getKey()).intValue())) && ((localField.getFieldOption() != 4) || (localField.getFieldID() == 178)))
        localQualifierInfo = appendQBEField(paramServerLogin, localQualifierInfo, localField, paramInt, (Value)localEntry.getValue());
    }
    return localQualifierInfo;
  }

  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("=", new Integer(1));
    localHashMap.put("<", new Integer(4));
    localHashMap.put(">", new Integer(2));
    localHashMap.put("<=", new Integer(5));
    localHashMap.put(">=", new Integer(3));
    localHashMap.put("!=", new Integer(6));
    localHashMap.put("<>", new Integer(6));
    localHashMap.put("&lt;", new Integer(4));
    localHashMap.put("&gt;", new Integer(2));
    localHashMap.put("&lt;&gt;", new Integer(6));
    localHashMap.put("&lt;=", new Integer(5));
    localHashMap.put("&gt;=", new Integer(3));
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ARQualifier
 * JD-Core Version:    0.6.1
 */