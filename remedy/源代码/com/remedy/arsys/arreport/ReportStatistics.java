package com.remedy.arsys.arreport;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOperationInfo;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.CurrencyPartInfo;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FuncCurrencyInfo;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.StatusHistoryItem;
import com.bmc.arsys.api.StatusHistoryValue;
import com.bmc.arsys.api.StatusHistoryValueIndicator;
import com.bmc.arsys.api.Time;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.legacyshared.ARDataFormat;
import com.remedy.arsys.stubs.ServerLogin;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

public class ReportStatistics
{
  private static ARDataFormat m_formatter = new ARDataFormat();
  protected static final int STATS_LAYOUT_COLUMN = 3;
  protected static final int STATS_LAYOUT_MULTILINE = 2;
  protected static final int STATS_LAYOUT_ONELINE = 1;
  private int m_op = 1;
  private String m_expression = null;
  private ArithmeticOrRelationalOperand m_operand = null;
  private String m_formName = null;
  private String m_label = "";
  private int m_layout = 2;
  private int m_computeOn = 6;
  private int m_count = 0;
  private int m_avgCount = 0;
  private Value m_max = null;
  private Value m_min = null;
  private Value m_sum = null;
  private boolean m_isValidStat = true;
  private ServerLogin m_context = null;
  private boolean m_isElapsedTime = false;
  private String m_locale = null;
  private String m_timezone = null;
  private EnumLimit mstatHistoryEnumLimit;

  public int getOp()
  {
    return this.m_op;
  }

  public int computeAutoPrecision(String paramString)
  {
    int i = 9;
    int j = paramString.indexOf(".");
    if (j == -1)
    {
      i = 2;
    }
    else
    {
      j++;
      int k = paramString.length();
      if (j < k)
      {
        i = k - j;
        if (i > 9)
          i = 9;
      }
    }
    return i;
  }

  public String getExpression()
  {
    return this.m_expression;
  }

  public String getLabel()
  {
    return this.m_label;
  }

  public int getLayout()
  {
    return this.m_layout;
  }

  public String getLayoutString()
  {
    int i = getLayout();
    String str = "unknown";
    switch (i)
    {
    case 3:
      str = "column";
      break;
    case 2:
      str = "multi-line";
      break;
    case 1:
      str = "one-line";
    }
    return str;
  }

  public int getComputeOn()
  {
    return this.m_computeOn;
  }

  public void setOp(int paramInt)
  {
    this.m_op = paramInt;
  }

  public void setExpression(String paramString)
  {
    if (paramString != null)
      paramString = paramString.replaceAll("Status-History", "Status History");
    this.m_expression = paramString;
  }

  public void setLabel(String paramString)
  {
    this.m_label = paramString;
  }

  public void setLayout(int paramInt)
  {
    this.m_layout = paramInt;
  }

  public void setComputeOn(int paramInt)
  {
    this.m_computeOn = paramInt;
  }

  public Value evalQualification(String paramString1, Entry paramEntry, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    this.m_isValidStat = true;
    Value localValue = new Value();
    if ((paramString1 == null) || (paramEntry == null))
      return localValue;
    if ((this.m_operand == null) || (!this.m_formName.equals(paramString1)))
    {
      this.m_formName = paramString1;
      if ((this.m_expression != null) && (this.m_expression.length() > 0))
      {
        String str = "(" + this.m_expression + ") > 0";
        CachedFieldMap localCachedFieldMap = Form.get(this.m_context.getServer(), this.m_formName).getCachedFieldMap();
        ARQualifier localARQualifier = new ARQualifier(this.m_context, str, localCachedFieldMap, null, 0);
        QualifierInfo localQualifierInfo = localARQualifier.getQualInfo();
        RelationalOperationInfo localRelationalOperationInfo = localQualifierInfo.getRelationalOperationInfo();
        this.m_operand = localRelationalOperationInfo.getLeftOperand();
      }
    }
    if (paramString2 != null)
      if (paramString3 == null)
        this.m_locale = paramString2;
      else
        this.m_locale = (paramString2 + "_" + paramString3);
    this.m_timezone = paramString4;
    if (this.m_operand != null)
      localValue = evalQualification(this.m_operand, paramEntry, paramString2, paramString3, paramString4);
    return localValue;
  }

  public Value evalQualification(ArithmeticOrRelationalOperand paramArithmeticOrRelationalOperand, Entry paramEntry, String paramString1, String paramString2, String paramString3)
    throws GoatException
  {
    Value localValue = null;
    Object localObject1;
    Object localObject2;
    Object localObject4;
    switch (paramArithmeticOrRelationalOperand.getType().toInt())
    {
    case 1:
      localObject1 = paramEntry.entrySet().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Map.Entry)((Iterator)localObject1).next();
        if (((Integer)paramArithmeticOrRelationalOperand.getValue()).intValue() == ((Integer)((Map.Entry)localObject2).getKey()).intValue())
        {
          localValue = (Value)((Map.Entry)localObject2).getValue();
          if (localValue.getDataType().toInt() != 12)
            break;
          localValue = new Value();
          break;
        }
      }
      break;
    case 2:
      localValue = (Value)paramArithmeticOrRelationalOperand.getValue();
      if (localValue.getDataType().equals(DataType.KEYWORD))
        switch (((Keyword)localValue.getValue()).toInt())
        {
        case 2:
        case 3:
          localValue = getTime(paramString1, paramString2, paramString3);
          break;
        case 4:
          localValue = getDate(paramString1, paramString2, paramString3);
          break;
        default:
          this.m_isValidStat = false;
        }
      break;
    case 3:
      localObject1 = null;
      localObject2 = null;
      localObject1 = evalQualification(((ArithmeticOperationInfo)paramArithmeticOrRelationalOperand.getValue()).getLeftOperand(), paramEntry, paramString1, paramString2, paramString3);
      localObject2 = evalQualification(((ArithmeticOperationInfo)paramArithmeticOrRelationalOperand.getValue()).getRightOperand(), paramEntry, paramString1, paramString2, paramString3);
      localValue = performArithOp((Value)localObject1, (Value)localObject2, ((ArithmeticOperationInfo)paramArithmeticOrRelationalOperand.getValue()).getOperation());
      break;
    case 4:
      StatusHistoryValueIndicator localStatusHistoryValueIndicator = (StatusHistoryValueIndicator)paramArithmeticOrRelationalOperand.getValue();
      StatusHistoryValue localStatusHistoryValue = null;
      String str = null;
      Iterator localIterator = paramEntry.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry1 = (Map.Entry)localIterator.next();
        if (CoreFieldId.StatusHistory.getFieldId() == ((Integer)localEntry1.getKey()).intValue())
        {
          str = (String)((Value)localEntry1.getValue()).getValue();
          break;
        }
      }
      if (str != null)
        try
        {
          localStatusHistoryValue = StatusHistoryValue.decode(str);
          long l = localStatusHistoryValueIndicator.getEnumValue();
          if (this.mstatHistoryEnumLimit.isValidValue(l))
          {
            int j = this.mstatHistoryEnumLimit.valToIndex(l);
            localObject4 = ((StatusHistoryItem)localStatusHistoryValue.get(j)).getTimestamp();
            if (localObject4 != null)
              localValue = new Value((Timestamp)localObject4);
          }
        }
        catch (ARException localARException)
        {
          throw new GoatException(localARException);
        }
      break;
    case 6:
      CurrencyPartInfo localCurrencyPartInfo = (CurrencyPartInfo)paramArithmeticOrRelationalOperand.getValue();
      if (localCurrencyPartInfo != null)
      {
        int i = localCurrencyPartInfo.getFieldId();
        Object localObject3 = null;
        localObject4 = paramEntry.entrySet().iterator();
        while (((Iterator)localObject4).hasNext())
        {
          Map.Entry localEntry2 = (Map.Entry)((Iterator)localObject4).next();
          if (i == ((Integer)localEntry2.getKey()).intValue())
          {
            localValue = (Value)localEntry2.getValue();
            if ((localValue == null) || (localValue.getDataType().toInt() != 12))
              break;
            localValue = getCurrencyValue(localCurrencyPartInfo, (CurrencyValue)localValue.getValue());
            break;
          }
        }
      }
      break;
    case 5:
    }
    return localValue;
  }

  private Value getCurrencyValue(CurrencyPartInfo paramCurrencyPartInfo, CurrencyValue paramCurrencyValue)
  {
    switch (paramCurrencyPartInfo.getPartTag())
    {
    case 1:
      return new Value(paramCurrencyValue.getValue());
    case 3:
      return new Value(paramCurrencyValue.getConversionDate());
    case 4:
      List localList = paramCurrencyValue.getFuncCurrencyList();
      String str = paramCurrencyPartInfo.getCurrencyCode();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator.next();
        if (str.equals(localFuncCurrencyInfo.getCurrencyCode()))
        {
          BigDecimal localBigDecimal = localFuncCurrencyInfo.getValue();
          if (localBigDecimal == null)
            break;
          CurrencyValue localCurrencyValue = new CurrencyValue();
          localCurrencyValue.setValue(localBigDecimal);
          localCurrencyValue.setCurrencyCode(str);
          if (paramCurrencyValue.getConversionDate() != null)
            localCurrencyValue.setConversionDate(paramCurrencyValue.getConversionDate().getValue());
          return new Value(localCurrencyValue);
        }
      }
      break;
    case 2:
    }
    return new Value();
  }

  private Value getTime(String paramString1, String paramString2, String paramString3)
  {
    Calendar localCalendar = null;
    TimeZone localTimeZone = null;
    Locale localLocale = null;
    if ((paramString1 != null) && (paramString2 != null) && (paramString3 != null))
    {
      localTimeZone = TimeZone.getTimeZone(paramString3);
      localLocale = new Locale(paramString1, paramString2);
      localCalendar = Calendar.getInstance(localTimeZone, localLocale);
    }
    else
    {
      localCalendar = Calendar.getInstance();
    }
    Date localDate = localCalendar.getTime();
    long l = localDate.getTime() / 1000L;
    return new Value(new Timestamp(l));
  }

  private Value getDate(String paramString1, String paramString2, String paramString3)
  {
    Calendar localCalendar = null;
    TimeZone localTimeZone = null;
    Locale localLocale = null;
    if ((paramString1 != null) && (paramString2 != null) && (paramString3 != null))
    {
      localTimeZone = TimeZone.getTimeZone(paramString3);
      localLocale = new Locale(paramString1, paramString2);
      localCalendar = Calendar.getInstance(localTimeZone, localLocale);
    }
    else
    {
      localCalendar = Calendar.getInstance();
    }
    int i = localCalendar.get(11);
    int j = localCalendar.get(12);
    int k = localCalendar.get(13);
    Date localDate = localCalendar.getTime();
    long l = localDate.getTime() / 1000L;
    l -= i * 3600 + j * 60 + k;
    return new Value(new Timestamp(l));
  }

  protected Value performArithOp(Value paramValue1, Value paramValue2, int paramInt)
    throws GoatException
  {
    Value localValue = new Value();
    if (checkNullOperands(paramValue1, paramValue2))
    {
      this.m_isValidStat = false;
    }
    else
    {
      DataType localDataType = matchDataTypes(paramValue1, paramValue2);
      if (localDataType != null)
      {
        paramValue1 = convertToDataType(paramValue1, localDataType, paramValue2);
        paramValue2 = convertToDataType(paramValue2, localDataType, paramValue1);
      }
      int i = paramValue1.getDataType().toInt();
      int j = paramValue2.getDataType().toInt();
      if ((i == 7) || (i == 14) || (i == 13) || (j == 7) || (j == 14) || (j == 13))
        localValue = doTimeOp(paramValue1, paramValue2, paramInt);
      else if (i == 2)
        localValue = doIntegerOp(paramValue1, paramValue2, paramInt);
      else if (i == 3)
        localValue = doRealOp(paramValue1, paramValue2, paramInt);
      else if ((i == 10) || (i == 12))
        localValue = doDecimalOp(paramValue1, paramValue2, paramInt);
      else if (i != 0)
        this.m_isValidStat = false;
    }
    return localValue;
  }

  private boolean checkNullOperands(Value paramValue1, Value paramValue2)
  {
    if ((paramValue1 == null) || (paramValue2 == null) || (paramValue1.getDataType().toInt() == 0) || (paramValue2.getDataType().toInt() == 0) || (paramValue1.getValue() == null) || (paramValue2.getValue() == null))
      return true;
    CurrencyValue localCurrencyValue = null;
    if (paramValue1.getDataType().toInt() == 12)
    {
      localCurrencyValue = (CurrencyValue)paramValue1.getValue();
      if (localCurrencyValue.getValue() == null)
        return true;
    }
    if (paramValue2.getDataType().toInt() == 12)
    {
      localCurrencyValue = (CurrencyValue)paramValue2.getValue();
      if (localCurrencyValue.getValue() == null)
        return true;
    }
    return false;
  }

  private DataType matchDataTypes(Value paramValue1, Value paramValue2)
  {
    DataType localDataType = null;
    if ((paramValue1.getDataType().equals(DataType.REAL)) && (paramValue2.getDataType().equals(DataType.INTEGER)))
      localDataType = DataType.REAL;
    else if ((paramValue1.getDataType().equals(DataType.INTEGER)) && (paramValue2.getDataType().equals(DataType.REAL)))
      localDataType = DataType.REAL;
    if ((paramValue1.getDataType().equals(DataType.DECIMAL)) && (paramValue2.getDataType().equals(DataType.INTEGER)))
      localDataType = DataType.DECIMAL;
    else if ((paramValue2.getDataType().equals(DataType.DECIMAL)) && (paramValue1.getDataType().equals(DataType.INTEGER)))
      localDataType = DataType.DECIMAL;
    else if ((paramValue1.getDataType().equals(DataType.DECIMAL)) && (paramValue2.getDataType().equals(DataType.REAL)))
      localDataType = DataType.DECIMAL;
    else if ((paramValue2.getDataType().equals(DataType.DECIMAL)) && (paramValue1.getDataType().equals(DataType.REAL)))
      localDataType = DataType.DECIMAL;
    if (((paramValue1.getDataType().equals(DataType.CURRENCY)) && (paramValue2.getDataType().equals(DataType.INTEGER))) || ((paramValue2.getDataType().equals(DataType.CURRENCY)) && (paramValue1.getDataType().equals(DataType.INTEGER))) || ((paramValue1.getDataType().equals(DataType.CURRENCY)) && (paramValue2.getDataType().equals(DataType.REAL))) || ((paramValue2.getDataType().equals(DataType.CURRENCY)) && (paramValue1.getDataType().equals(DataType.REAL))) || ((paramValue1.getDataType().equals(DataType.CURRENCY)) && (paramValue2.getDataType().equals(DataType.DECIMAL))) || ((paramValue2.getDataType().equals(DataType.CURRENCY)) && (paramValue1.getDataType().equals(DataType.DECIMAL))))
      return DataType.CURRENCY;
    return localDataType;
  }

  private Value convertToDataType(Value paramValue1, DataType paramDataType, Value paramValue2)
  {
    Value localValue = null;
    if (paramValue1.getDataType().toInt() == paramDataType.toInt())
    {
      localValue = paramValue1;
    }
    else if (paramDataType.toInt() == DataType.REAL.toInt())
    {
      if (paramValue1.getDataType().toInt() == DataType.INTEGER.toInt())
        localValue = new Value(((Integer)paramValue1.getValue()).doubleValue());
    }
    else
    {
      Object localObject;
      if (paramDataType.toInt() == DataType.DECIMAL.toInt())
      {
        if (paramValue1.getDataType().toInt() == DataType.INTEGER.toInt())
        {
          localObject = new BigDecimal(((Integer)paramValue1.getValue()).toString());
          localValue = new Value((BigDecimal)localObject);
        }
        else if (paramValue1.getDataType().toInt() == DataType.REAL.toInt())
        {
          localObject = new BigDecimal(((Double)paramValue1.getValue()).doubleValue());
          localValue = new Value((BigDecimal)localObject);
        }
      }
      else if (paramDataType.toInt() == DataType.CURRENCY.toInt())
      {
        localObject = new CurrencyValue();
        if (paramValue2 != null)
        {
          CurrencyValue localCurrencyValue = (CurrencyValue)paramValue2.getValue();
          if (localCurrencyValue != null)
          {
            ((CurrencyValue)localObject).setCurrencyCode(localCurrencyValue.getCurrencyCode());
            if (localCurrencyValue.getConversionDate() != null)
              ((CurrencyValue)localObject).setConversionDate(localCurrencyValue.getConversionDate().getValue());
          }
        }
        if (paramValue1.getDataType().toInt() == DataType.INTEGER.toInt())
        {
          ((CurrencyValue)localObject).setValue(new BigDecimal(((Integer)paramValue1.getValue()).toString()));
          localValue = new Value((CurrencyValue)localObject);
        }
        else if (paramValue1.getDataType().toInt() == DataType.REAL.toInt())
        {
          ((CurrencyValue)localObject).setValue(new BigDecimal(((Double)paramValue1.getValue()).doubleValue()));
          localValue = new Value((CurrencyValue)localObject);
        }
        else if (paramValue1.getDataType().toInt() == DataType.DECIMAL.toInt())
        {
          ((CurrencyValue)localObject).setValue((BigDecimal)paramValue1.getValue());
          localValue = new Value((CurrencyValue)localObject);
        }
      }
      else
      {
        localValue = paramValue1;
      }
    }
    return localValue;
  }

  private Value doTimeOp(Value paramValue1, Value paramValue2, int paramInt)
  {
    Value localValue = null;
    int i = paramValue1.getDataType().toInt();
    int j = paramValue2.getDataType().toInt();
    long l1 = getLongValue(i, paramValue1);
    long l2 = getLongValue(j, paramValue2);
    long l3 = 0L;
    if (paramInt == 1)
    {
      localValue = doTimeAdd(i, j, l1, l2);
    }
    else
    {
      if ((i == 7) || (i == 14) || (i == 13));
      switch (paramInt)
      {
      case 2:
        localValue = doTimeSubtract(i, j, l1, l2);
        break;
      case 3:
        localValue = doTimeMultiply(i, j, l1, l2);
        break;
      case 4:
      case 5:
        localValue = doTimeDivide(paramInt, i, j, l1, l2);
        break;
      default:
        this.m_isValidStat = false;
        break;
        this.m_isValidStat = false;
      }
    }
    return localValue;
  }

  private long getLongValue(int paramInt, Value paramValue)
  {
    long l = 0L;
    switch (paramInt)
    {
    case 2:
      l = ((Integer)paramValue.getValue()).longValue();
      break;
    case 3:
      l = ((Double)paramValue.getValue()).longValue();
      break;
    case 10:
      l = ((BigDecimal)paramValue.getValue()).longValue();
      break;
    case 12:
      CurrencyValue localCurrencyValue = (CurrencyValue)paramValue.getValue();
      if (localCurrencyValue != null)
      {
        BigDecimal localBigDecimal = localCurrencyValue.getValue();
        if (localBigDecimal != null)
          l = localBigDecimal.longValue();
      }
      break;
    case 7:
      l = ((Timestamp)paramValue.getValue()).getValue();
      break;
    case 14:
      l = ((Time)paramValue.getValue()).getValue();
      break;
    case 13:
      l = ((DateInfo)paramValue.getValue()).getValue();
      break;
    case 4:
    case 5:
    case 6:
    case 8:
    case 9:
    case 11:
    }
    return l;
  }

  private Value doTimeAdd(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    Value localValue = null;
    long l = 0L;
    if ((paramInt1 == 7) || (paramInt1 == 14) || (paramInt1 == 13))
    {
      switch (paramInt2)
      {
      case 2:
      case 3:
      case 10:
      case 12:
        l = paramLong1 + paramLong2;
        this.m_isElapsedTime = false;
        break;
      case 7:
        if (paramInt1 == 13)
        {
          paramLong2 = convertDateToDateTime(paramLong2);
          if (paramLong2 >= 0L)
          {
            l = paramLong1 + paramLong2;
            this.m_isElapsedTime = false;
          }
          else
          {
            this.m_isValidStat = false;
          }
        }
        else
        {
          l = paramLong1 + paramLong2;
          this.m_isElapsedTime = false;
        }
        break;
      case 14:
        if (paramInt1 == 13)
        {
          this.m_isValidStat = false;
        }
        else
        {
          l = paramLong1 + paramLong2;
          this.m_isElapsedTime = false;
        }
        break;
      case 13:
        if (paramInt1 == 14)
        {
          this.m_isValidStat = false;
        }
        else if (paramInt1 == 7)
        {
          paramLong2 = convertDateTimeToDate(paramLong2);
          if (paramLong2 >= 0L)
          {
            l = paramLong1 + paramLong2;
            this.m_isElapsedTime = false;
          }
          else
          {
            this.m_isValidStat = false;
          }
        }
        else
        {
          l = paramLong1 + paramLong2;
          this.m_isElapsedTime = false;
        }
        break;
      case 4:
      case 5:
      case 6:
      case 8:
      case 9:
      case 11:
      default:
        this.m_isValidStat = false;
      }
      if (this.m_isValidStat)
        switch (paramInt1)
        {
        case 7:
          localValue = new Value(new Timestamp(l));
          break;
        case 14:
          localValue = new Value(new Time(l));
          break;
        case 13:
          localValue = new Value(new DateInfo((int)l));
        }
    }
    else
    {
      switch (paramInt1)
      {
      case 2:
      case 3:
      case 10:
      case 12:
        l = paramLong1 + paramLong2;
        this.m_isElapsedTime = false;
        break;
      default:
        this.m_isValidStat = false;
      }
      if (this.m_isValidStat)
        switch (paramInt2)
        {
        case 7:
          localValue = new Value(new Timestamp(l));
          break;
        case 14:
          localValue = new Value(new Time(l));
          break;
        case 13:
          localValue = new Value(new DateInfo((int)l));
        }
    }
    return localValue;
  }

  private Value doTimeSubtract(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    Value localValue = null;
    long l = 0L;
    switch (paramInt2)
    {
    case 2:
    case 3:
    case 10:
    case 12:
      l = paramLong1 - paramLong2;
      this.m_isElapsedTime = false;
      break;
    case 7:
      if (paramInt1 == 13)
      {
        paramLong2 = convertDateTimeToDate(paramLong2);
        if (paramLong2 >= 0L)
        {
          l = paramLong1 - paramLong2;
          this.m_isElapsedTime = true;
        }
        else
        {
          this.m_isValidStat = false;
        }
      }
      else
      {
        l = paramLong1 - paramLong2;
        this.m_isElapsedTime = true;
      }
      break;
    case 14:
      if (paramInt1 == 13)
      {
        this.m_isValidStat = false;
      }
      else
      {
        l = paramLong1 - paramLong2;
        this.m_isElapsedTime = true;
      }
      break;
    case 13:
      if (paramInt1 == 14)
      {
        this.m_isValidStat = false;
      }
      else if (paramInt1 == 7)
      {
        paramLong2 = convertDateToDateTime(paramLong2);
        if (paramLong2 >= 0L)
        {
          l = paramLong1 - paramLong2;
          this.m_isElapsedTime = true;
        }
        else
        {
          this.m_isValidStat = false;
        }
      }
      else
      {
        l = paramLong1 - paramLong2;
        this.m_isElapsedTime = true;
      }
      break;
    case 4:
    case 5:
    case 6:
    case 8:
    case 9:
    case 11:
    default:
      this.m_isValidStat = false;
    }
    if (this.m_isValidStat)
      if (this.m_isElapsedTime)
      {
        Long localLong = new Long(l);
        localValue = new Value(localLong.intValue());
      }
      else
      {
        switch (paramInt1)
        {
        case 7:
          localValue = new Value(new Timestamp(l));
          break;
        case 14:
          localValue = new Value(new Time(l));
          break;
        case 13:
          localValue = new Value(new DateInfo((int)l));
        }
      }
    return localValue;
  }

  private Value doTimeMultiply(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    Value localValue = null;
    long l = 0L;
    switch (paramInt2)
    {
    case 2:
    case 3:
    case 10:
    case 12:
      l = paramLong1 * paramLong2;
      this.m_isElapsedTime = false;
      break;
    case 7:
      if (paramInt1 == 13)
      {
        paramLong2 = convertDateTimeToDate(paramLong2);
        if (paramLong2 >= 0L)
        {
          l = paramLong1 * paramLong2;
          this.m_isElapsedTime = false;
        }
        else
        {
          this.m_isValidStat = false;
        }
      }
      else
      {
        l = paramLong1 * paramLong2;
        this.m_isElapsedTime = false;
      }
      break;
    case 14:
      if (paramInt1 == 13)
      {
        this.m_isValidStat = false;
      }
      else
      {
        l = paramLong1 * paramLong2;
        this.m_isElapsedTime = false;
      }
      break;
    case 13:
      if (paramInt1 == 14)
      {
        this.m_isValidStat = false;
      }
      else if (paramInt1 == 7)
      {
        paramLong2 = convertDateToDateTime(paramLong2);
        if (paramLong2 >= 0L)
        {
          l = paramLong1 * paramLong2;
          this.m_isElapsedTime = false;
        }
        else
        {
          this.m_isValidStat = false;
        }
      }
      else
      {
        l = paramLong1 * paramLong2;
        this.m_isElapsedTime = false;
      }
      break;
    case 4:
    case 5:
    case 6:
    case 8:
    case 9:
    case 11:
    default:
      this.m_isValidStat = false;
    }
    if (this.m_isValidStat)
      switch (paramInt1)
      {
      case 7:
        localValue = new Value(new Timestamp(l));
        break;
      case 14:
        localValue = new Value(new Time(l));
        break;
      case 13:
        localValue = new Value(new DateInfo((int)l));
      }
    return localValue;
  }

  private Value doTimeDivide(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2)
  {
    Value localValue = null;
    long l = 0L;
    if (paramLong2 == 0L)
      this.m_isValidStat = false;
    else
      switch (paramInt3)
      {
      case 2:
      case 3:
      case 10:
      case 12:
        if (paramInt1 == 5)
          l = paramLong1 % paramLong2;
        else
          l = paramLong1 / paramLong2;
        this.m_isElapsedTime = false;
        break;
      default:
        this.m_isValidStat = false;
      }
    if (this.m_isValidStat)
      switch (paramInt2)
      {
      case 7:
        localValue = new Value(new Timestamp(l));
        break;
      case 14:
        localValue = new Value(new Time(l));
        break;
      case 13:
        localValue = new Value(new DateInfo((int)l));
      }
    return localValue;
  }

  private long convertDateToDateTime(long paramLong)
  {
    String str1 = m_formatter.formatDateTime("1", 4, String.valueOf(paramLong), this.m_locale, this.m_timezone, null);
    if (str1.indexOf("Error") == -1)
    {
      String str2 = m_formatter.parseDate(str1, 0, this.m_locale, this.m_timezone);
      if (str2.indexOf("Error") == -1)
        try
        {
          return Long.parseLong(str2);
        }
        catch (Exception localException)
        {
          return -1L;
        }
    }
    return -1L;
  }

  private long convertDateTimeToDate(long paramLong)
  {
    String str1 = m_formatter.formatDateTime("1", 2, String.valueOf(paramLong), this.m_locale, this.m_timezone, null);
    if (str1.indexOf("Error") == -1)
    {
      String str2 = m_formatter.parseDate(str1, 4, this.m_locale, this.m_timezone);
      if (str2.indexOf("Error") == -1)
        try
        {
          return Long.parseLong(str2);
        }
        catch (Exception localException)
        {
          return -1L;
        }
    }
    return -1L;
  }

  private Value doIntegerOp(Value paramValue1, Value paramValue2, int paramInt)
    throws GoatException
  {
    Value localValue = null;
    int i = ((Integer)paramValue1.getValue()).intValue();
    int j = ((Integer)paramValue2.getValue()).intValue();
    int k = 0;
    double d = 0.0D;
    int m = 0;
    switch (paramInt)
    {
    case 1:
      k = i + j;
      break;
    case 2:
      k = i - j;
      break;
    case 3:
      k = i * j;
      break;
    case 4:
      if (j != 0)
      {
        d = ((Integer)paramValue1.getValue()).doubleValue() / ((Integer)paramValue2.getValue()).doubleValue();
        m = 1;
      }
      else
      {
        this.m_isValidStat = false;
        k = 0;
      }
      break;
    case 5:
      if (j != 0)
      {
        k = i % j;
      }
      else
      {
        this.m_isValidStat = false;
        k = 0;
      }
      break;
    default:
      this.m_isValidStat = false;
    }
    if ((this.m_isValidStat) && (m == 0))
      localValue = new Value(k);
    else if ((this.m_isValidStat) && (m != 0))
      localValue = new Value(d);
    return localValue;
  }

  private Value doRealOp(Value paramValue1, Value paramValue2, int paramInt)
    throws GoatException
  {
    Value localValue = null;
    double d1 = ((Double)paramValue1.getValue()).doubleValue();
    double d2 = ((Double)paramValue2.getValue()).doubleValue();
    double d3 = 0.0D;
    switch (paramInt)
    {
    case 1:
      d3 = d1 + d2;
      break;
    case 2:
      d3 = d1 - d2;
      break;
    case 3:
      d3 = d1 * d2;
      break;
    case 4:
      if (d2 != 0.0D)
      {
        d3 = d1 / d2;
      }
      else
      {
        this.m_isValidStat = false;
        d3 = 0.0D;
      }
      break;
    case 5:
      d3 = 0.0D;
    default:
      this.m_isValidStat = false;
    }
    if (this.m_isValidStat)
      localValue = new Value(d3);
    return localValue;
  }

  private Value doDecimalOp(Value paramValue1, Value paramValue2, int paramInt)
  {
    Value localValue = null;
    int i = paramValue1.getDataType().toInt();
    BigDecimal localBigDecimal1 = null;
    BigDecimal localBigDecimal2 = null;
    CurrencyValue localCurrencyValue1 = null;
    CurrencyValue localCurrencyValue2 = null;
    if (i == 12)
    {
      localCurrencyValue1 = (CurrencyValue)paramValue1.getValue();
      localCurrencyValue2 = (CurrencyValue)paramValue2.getValue();
      localBigDecimal1 = localCurrencyValue1.getValue();
      localBigDecimal2 = localCurrencyValue2.getValue();
    }
    else
    {
      localBigDecimal1 = (BigDecimal)paramValue1.getValue();
      localBigDecimal2 = (BigDecimal)paramValue2.getValue();
    }
    BigDecimal localBigDecimal3 = null;
    switch (paramInt)
    {
    case 1:
      localBigDecimal3 = localBigDecimal1.add(localBigDecimal2);
      break;
    case 2:
      localBigDecimal3 = localBigDecimal1.subtract(localBigDecimal2);
      break;
    case 3:
      localBigDecimal3 = localBigDecimal1.multiply(localBigDecimal2);
      break;
    case 4:
      if (localBigDecimal2.longValue() != 0L)
      {
        localBigDecimal3 = localBigDecimal1.divide(localBigDecimal2, 6);
      }
      else
      {
        this.m_isValidStat = false;
        localBigDecimal3 = new BigDecimal("0.0");
      }
      break;
    case 5:
      localBigDecimal3 = new BigDecimal("0.0");
    default:
      this.m_isValidStat = false;
    }
    if (this.m_isValidStat)
      if (i == 12)
      {
        CurrencyValue localCurrencyValue3 = new CurrencyValue();
        localCurrencyValue3.setValue(localBigDecimal3);
        localCurrencyValue3.setCurrencyCode(localCurrencyValue1.getCurrencyCode());
        if (localCurrencyValue1.getConversionDate() != null)
          localCurrencyValue3.setConversionDate(localCurrencyValue1.getConversionDate().getValue());
        localValue = new Value(localCurrencyValue3);
      }
      else
      {
        localValue = new Value(localBigDecimal3);
      }
    return localValue;
  }

  public void setUserContext(ServerLogin paramServerLogin)
  {
    this.m_context = paramServerLogin;
  }

  public void setStatusHistoryEnumLimit(EnumLimit paramEnumLimit)
  {
    this.mstatHistoryEnumLimit = paramEnumLimit;
  }

  public void resetValues()
  {
    this.m_isValidStat = true;
    this.m_isElapsedTime = false;
    this.m_count = 0;
    this.m_avgCount = 0;
    this.m_max = null;
    this.m_min = null;
    this.m_sum = null;
  }

  public void accumulate(Value paramValue)
    throws GoatException
  {
    if (paramValue == null)
      return;
    if (!this.m_isValidStat)
    {
      this.m_isValidStat = true;
      return;
    }
    if ((paramValue.getDataType().toInt() == 0) && (this.m_expression != null) && (this.m_expression.length() > 0))
      return;
    this.m_count += 1;
    if (paramValue.getDataType().toInt() == 0)
      return;
    this.m_avgCount += 1;
    boolean bool = false;
    if (this.m_avgCount == 1)
    {
      this.m_min = paramValue;
      this.m_max = paramValue;
      this.m_sum = paramValue;
    }
    else
    {
      if (this.m_min != null)
        bool = performRelOp(4, paramValue, this.m_min);
      if (bool)
        this.m_min = paramValue;
      if (this.m_max != null)
        bool = performRelOp(2, paramValue, this.m_max);
      if (bool)
        this.m_max = paramValue;
      this.m_sum = performArithOp(this.m_sum, paramValue, 1);
    }
  }

  protected boolean performRelOp(int paramInt, Value paramValue1, Value paramValue2)
  {
    boolean bool = false;
    DataType localDataType = matchDataTypes(paramValue1, paramValue2);
    if (localDataType != null)
    {
      paramValue1 = convertToDataType(paramValue1, localDataType, paramValue2);
      paramValue2 = convertToDataType(paramValue2, localDataType, paramValue1);
    }
    if (paramValue2 == null)
      return bool;
    int i = paramValue1.getDataType().toInt();
    switch (i)
    {
    case 2:
      if (paramInt == 4)
        bool = ((Integer)paramValue1.getValue()).intValue() < ((Integer)paramValue2.getValue()).intValue();
      else if (paramInt == 2)
        bool = ((Integer)paramValue1.getValue()).intValue() > ((Integer)paramValue2.getValue()).intValue();
      break;
    case 7:
      if (paramInt == 4)
        bool = ((Timestamp)paramValue1.getValue()).getValue() < ((Timestamp)paramValue2.getValue()).getValue();
      else if (paramInt == 2)
        bool = ((Timestamp)paramValue1.getValue()).getValue() > ((Timestamp)paramValue2.getValue()).getValue();
      break;
    case 14:
      if (paramInt == 4)
        bool = ((Time)paramValue1.getValue()).getValue() < ((Time)paramValue2.getValue()).getValue();
      else if (paramInt == 2)
        bool = ((Time)paramValue1.getValue()).getValue() > ((Time)paramValue2.getValue()).getValue();
      break;
    case 13:
      if (paramInt == 4)
        bool = ((DateInfo)paramValue1.getValue()).getValue() < ((DateInfo)paramValue2.getValue()).getValue();
      else if (paramInt == 2)
        bool = ((DateInfo)paramValue1.getValue()).getValue() > ((DateInfo)paramValue2.getValue()).getValue();
      break;
    case 3:
      if (paramInt == 4)
        bool = ((Double)paramValue1.getValue()).doubleValue() < ((Double)paramValue2.getValue()).doubleValue();
      else if (paramInt == 2)
        bool = ((Double)paramValue1.getValue()).doubleValue() > ((Double)paramValue2.getValue()).doubleValue();
      break;
    case 10:
    case 12:
      BigDecimal localBigDecimal1 = null;
      BigDecimal localBigDecimal2 = null;
      if (i == 12)
      {
        localObject = (CurrencyValue)paramValue1.getValue();
        CurrencyValue localCurrencyValue = (CurrencyValue)paramValue2.getValue();
        localBigDecimal1 = ((CurrencyValue)localObject).getValue();
        localBigDecimal2 = localCurrencyValue.getValue();
      }
      else
      {
        localBigDecimal1 = (BigDecimal)paramValue1.getValue();
        localBigDecimal2 = (BigDecimal)paramValue2.getValue();
      }
      Object localObject = null;
      if (paramInt == 4)
      {
        localObject = localBigDecimal1.min(localBigDecimal2);
        if (((BigDecimal)localObject).equals(localBigDecimal1))
          bool = true;
        else
          bool = false;
      }
      else if (paramInt == 2)
      {
        localObject = localBigDecimal1.max(localBigDecimal2);
        if (((BigDecimal)localObject).equals(localBigDecimal1))
          bool = true;
        else
          bool = false;
      }
      break;
    case 0:
      bool = false;
      break;
    case 6:
      if (paramInt == 4)
        bool = ((Long)paramValue1.getValue()).longValue() < ((Long)paramValue2.getValue()).longValue();
      else if (paramInt == 2)
        bool = ((Long)paramValue1.getValue()).longValue() > ((Long)paramValue2.getValue()).longValue();
      break;
    case 1:
    case 4:
    case 5:
    case 8:
    case 9:
    case 11:
    }
    return bool;
  }

  public String getValueString(Long paramLong, String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    String str = "";
    Value localValue = null;
    Object localObject1;
    if (this.m_op == 1)
      str = Integer.toString(this.m_count);
    else if (this.m_op == 5)
      localValue = this.m_max;
    else if (this.m_op == 4)
      localValue = this.m_min;
    else if (this.m_op == 2)
      localValue = this.m_sum;
    else if (this.m_op == 3)
      if (this.m_avgCount == 0)
      {
        str = "";
      }
      else
      {
        localObject1 = new Value(this.m_avgCount);
        localValue = performArithOp(this.m_sum, (Value)localObject1, 4);
      }
    if (localValue != null)
    {
      localObject1 = localValue.getValue();
      DataType localDataType = localValue.getDataType();
      Object localObject2;
      if (localDataType.equals(DataType.CURRENCY))
      {
        localObject2 = (CurrencyValue)localObject1;
        BigDecimal localBigDecimal = ((CurrencyValue)localObject2).getValue();
        if (localBigDecimal != null)
          if (this.m_isElapsedTime)
          {
            str = m_formatter.formatElapsedTime(localBigDecimal.longValue(), paramString1);
          }
          else
          {
            int j = computeAutoPrecision(localBigDecimal.toString());
            str = m_formatter.formatDecimalNumber(localBigDecimal.toString(), paramString1, Integer.toString(j));
            str = str + " " + ((CurrencyValue)localObject2).getCurrencyCode();
          }
      }
      else
      {
        int i;
        if (localDataType.equals(DataType.DECIMAL))
        {
          localObject2 = (BigDecimal)localObject1;
          if (this.m_isElapsedTime)
          {
            str = m_formatter.formatElapsedTime(((BigDecimal)localObject2).longValue(), paramString1);
          }
          else
          {
            i = computeAutoPrecision(((BigDecimal)localObject2).toString());
            str = m_formatter.formatDecimalNumber(((BigDecimal)localObject2).toString(), paramString1, Integer.toString(i));
          }
        }
        else if (localDataType.equals(DataType.REAL))
        {
          localObject2 = (Double)localObject1;
          if (this.m_isElapsedTime)
          {
            str = m_formatter.formatElapsedTime(((Double)localObject2).longValue(), paramString1);
          }
          else
          {
            i = computeAutoPrecision(((Double)localObject2).toString());
            str = m_formatter.formatRealNumber(((Double)localObject2).toString(), paramString1, Integer.toString(i));
          }
        }
        else if (localDataType.equals(DataType.INTEGER))
        {
          localObject2 = (Integer)localObject1;
          if (this.m_isElapsedTime)
            str = m_formatter.formatElapsedTime(((Integer)localObject2).longValue(), paramString1);
          else
            str = ((Integer)localObject2).toString();
        }
        else if ((localDataType.equals(DataType.TIME)) || (localDataType.equals(DataType.DATE)) || (localDataType.equals(DataType.TIME_OF_DAY)))
        {
          localObject2 = localObject1.toString();
          if ((localObject1 instanceof Timestamp))
            localObject2 = "" + ((Timestamp)localObject1).getValue();
          i = 0;
          if (localDataType.equals(DataType.DATE))
          {
            i = 4;
            localObject2 = typeDateVal(localObject1, 0L);
          }
          else if (localDataType.equals(DataType.TIME_OF_DAY))
          {
            i = 3;
            localObject2 = typeTimeVal(localObject1, 0L);
          }
          str = NativeReportServlet.formatDatesTimes(paramString1, paramString2, (String)localObject2, i, paramLong, paramString3, paramString4);
        }
        else if (localDataType.equals(DataType.ENUM))
        {
          localObject2 = (Long)localObject1;
          str = ((Long)localObject2).toString();
        }
      }
    }
    return str;
  }

  public String typeDateVal(Object paramObject, long paramLong)
  {
    Integer localInteger = new Integer(((DateInfo)paramObject).getValue());
    return localInteger.toString();
  }

  public String typeTimeVal(Object paramObject, long paramLong)
  {
    Long localLong = new Long(((Time)paramObject).getValue());
    return localLong.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ReportStatistics
 * JD-Core Version:    0.6.1
 */