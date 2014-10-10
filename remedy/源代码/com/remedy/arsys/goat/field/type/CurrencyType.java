package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.CurrencyDetail;
import com.bmc.arsys.api.CurrencyFieldLimit;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldLimit;
import com.bmc.arsys.api.FuncCurrencyInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.CurrencyCodes;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.LocaleUtil;
import com.remedy.arsys.goat.field.CurrencyField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CurrencyType extends GoatType
{
  private CurrencyValue mVal;
  private CurrencyField mField;

  public CurrencyType(Value paramValue, int paramInt, FieldGraph.Node paramNode)
    throws GoatException
  {
    this.mVal = ((CurrencyValue)paramValue.getValue());
    assert (this.mVal != null);
    if (paramNode != null)
    {
      assert ((paramNode.mField instanceof CurrencyField));
      this.mField = ((CurrencyField)paramNode.mField);
    }
  }

  public CurrencyType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt, paramNode);
  }

  public CurrencyType(Value paramValue, int paramInt)
    throws GoatException
  {
    this(paramValue, paramInt, null);
  }

  public CurrencyType(String paramString, int paramInt)
    throws GoatException
  {
    this.mVal = new CurrencyValue();
    String[] arrayOfString = paramString.split(" ");
    if (arrayOfString.length > 0)
      try
      {
        this.mVal.setValue(new BigDecimal(arrayOfString[0]));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new GoatException(9325);
      }
    else
      this.mVal.setValue(null);
    if (arrayOfString.length > 1)
      this.mVal.setCurrencyCode(arrayOfString[1]);
    else
      this.mVal.setCurrencyCode(null);
    if (arrayOfString.length > 2)
      this.mVal.setConversionDate(Long.parseLong(arrayOfString[2]));
    if (arrayOfString.length > 3)
    {
      ArrayList localArrayList = new ArrayList((arrayOfString.length - 3) / 2);
      int i = 3;
      for (int j = 0; i < arrayOfString.length - 1; j++)
      {
        FuncCurrencyInfo localFuncCurrencyInfo = new FuncCurrencyInfo();
        localFuncCurrencyInfo.setValue(new BigDecimal(arrayOfString[i]));
        localFuncCurrencyInfo.setCurrencyCode(arrayOfString[(i + 1)]);
        localArrayList.add(localFuncCurrencyInfo);
        i += 2;
      }
      this.mVal.setFuncCurrencyList(localArrayList);
    }
    assert (this.mVal != null);
  }

  public CurrencyType(CurrencyType paramCurrencyType, CurrencyField paramCurrencyField)
  {
    this.mVal = paramCurrencyType.mVal;
    this.mField = paramCurrencyField;
  }

  public int getDataType()
  {
    return 12;
  }

  public int getOperandType()
  {
    return 1;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    StringBuilder localStringBuilder = new StringBuilder(this.mVal.getValueString());
    if ((this.mVal.getCurrencyCode() != null) && (this.mVal.getCurrencyCode().length() > 0))
    {
      localStringBuilder.append(" ").append(this.mVal.getCurrencyCode());
      List localList = this.mVal.getFuncCurrencyList();
      if (localList != null)
      {
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator.next();
          if (localFuncCurrencyInfo.getValue() != null)
          {
            localStringBuilder.append(" ").append(localFuncCurrencyInfo.getCurrencyCode());
            localStringBuilder.append(" ").append(localFuncCurrencyInfo.getValue().toString());
          }
        }
      }
    }
    paramEmitter.js().append("new CurrencyType(").appendqs(localStringBuilder.toString()).append(")");
  }

  public String emitAR()
  {
    return escapeQuotedString("\"" + this.mVal.getValueString() + " " + this.mVal.getCurrencyCode() + "\"");
  }

  public Value toValue()
  {
    return new Value(this.mVal);
  }

  public String toPrimitive()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.mVal.getValueString());
    if (this.mVal.getCurrencyCode() != null)
    {
      localStringBuilder.append(" " + this.mVal.getCurrencyCode());
      if (this.mVal.getConversionDate() != null)
      {
        localStringBuilder.append(" " + this.mVal.getConversionDate().getValue());
        List localList = this.mVal.getFuncCurrencyList();
        Iterator localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator.next();
          if (localFuncCurrencyInfo.getValue() != null)
          {
            String str = localFuncCurrencyInfo.getCurrencyCode();
            localStringBuilder.append(" ").append(localFuncCurrencyInfo.getValue().toString()).append(" ").append(str);
          }
        }
      }
    }
    return localStringBuilder.toString();
  }

  public String forHTML()
  {
    if (this.mField != null)
      return format(this.mVal, this.mField.getAllowableCurrDetails());
    return format(this.mVal, null);
  }

  public static String format(CurrencyValue paramCurrencyValue, CurrencyDetail[] paramArrayOfCurrencyDetail)
  {
    assert (paramCurrencyValue != null);
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 2;
    String str = paramCurrencyValue.getCurrencyCode();
    if (paramArrayOfCurrencyDetail != null)
      for (int j = 0; j < paramArrayOfCurrencyDetail.length; j++)
        if (paramArrayOfCurrencyDetail[j].getCurrencyCode().equalsIgnoreCase(str))
        {
          i = paramArrayOfCurrencyDetail[j].getPrecision();
          break;
        }
    if (paramCurrencyValue.getValue() != null)
    {
      localStringBuilder.append(LocaleUtil.FormatDecimal(SessionData.get().getLocale(), paramCurrencyValue.getValue(), i));
      if (str != null)
        localStringBuilder.append(" ").append(str);
    }
    return localStringBuilder.toString();
  }

  public GoatType bind(ServerLogin paramServerLogin, Field paramField)
    throws GoatException
  {
    if ((this.mVal.getCurrencyCode() == null) || (this.mVal.getCurrencyCode().length() == 0))
    {
      FieldLimit localFieldLimit = paramField.getFieldLimit();
      if ((localFieldLimit != null) && (localFieldLimit.getDataType() == 12))
      {
        CurrencyFieldLimit localCurrencyFieldLimit = (CurrencyFieldLimit)localFieldLimit;
        CurrencyDetail[] arrayOfCurrencyDetail1 = (CurrencyDetail[])localCurrencyFieldLimit.getAllowable().toArray(new CurrencyDetail[0]);
        if ((arrayOfCurrencyDetail1 != null) && (arrayOfCurrencyDetail1.length > 0))
        {
          this.mVal.setCurrencyCode(arrayOfCurrencyDetail1[0].getCurrencyCode());
        }
        else
        {
          CurrencyDetail[] arrayOfCurrencyDetail2 = CurrencyCodes.GetAllCurrencyCodes(paramServerLogin);
          if ((arrayOfCurrencyDetail2 != null) && (arrayOfCurrencyDetail2.length > 0))
            this.mVal.setCurrencyCode(arrayOfCurrencyDetail2[0].getCurrencyCode());
        }
      }
    }
    return this;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.CurrencyType
 * JD-Core Version:    0.6.1
 */