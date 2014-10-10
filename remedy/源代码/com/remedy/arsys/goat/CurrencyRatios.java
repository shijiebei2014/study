package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FuncCurrencyInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CurrencyRatios
  implements Serializable
{
  private static final long serialVersionUID = -1493390993535632258L;
  public long refreshInterval = 3600000L;
  private static Map mServers = Collections.synchronizedMap(new HashMap());
  private Map mRatios = Collections.synchronizedMap(new LinkedHashMap(10, 0.75F, true)
  {
    protected boolean removeEldestEntry(Map.Entry paramAnonymousEntry)
    {
      return size() > 100;
    }
  });
  private static final int MAX_ENTRIES = 100;

  private CurrencyRatios(String paramString)
    throws GoatException
  {
    this.refreshInterval = (ServerInfo.get(paramString).getCurrencyInterval() * 60000L);
  }

  public static void ReconvertValues(String paramString, Entry paramEntry)
  {
    paramString = paramString.toLowerCase();
    Iterator localIterator1 = paramEntry.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      Value localValue = (Value)localEntry.getValue();
      if (localValue.getDataType().equals(DataType.CURRENCY))
      {
        CurrencyValue localCurrencyValue = (CurrencyValue)localValue.getValue();
        List localList = localCurrencyValue.getFuncCurrencyList();
        Iterator localIterator2 = localList.iterator();
        while (localIterator2.hasNext())
        {
          FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator2.next();
          if ((!localFuncCurrencyInfo.getCurrencyCode().equals(localCurrencyValue.getCurrencyCode())) && (localFuncCurrencyInfo.getValue() != null))
            localFuncCurrencyInfo.setValue(ConvertValue(localCurrencyValue.getValue(), paramString, localCurrencyValue.getCurrencyCode(), localFuncCurrencyInfo.getCurrencyCode(), localCurrencyValue.getConversionDate().getValue()));
        }
      }
    }
  }

  public static BigDecimal ConvertValue(BigDecimal paramBigDecimal, String paramString1, String paramString2, String paramString3, long paramLong)
  {
    BigDecimal localBigDecimal;
    try
    {
      localBigDecimal = GetCurrencyRatio(paramString1, paramString2, paramString3, paramLong);
    }
    catch (GoatException localGoatException)
    {
      return null;
    }
    if (localBigDecimal == null)
      return null;
    return paramBigDecimal.multiply(localBigDecimal);
  }

  private static CurrencyRatios getRatios(String paramString)
    throws GoatException
  {
    CurrencyRatios localCurrencyRatios = null;
    paramString = paramString.toLowerCase();
    String str = "CurrencyRatios:" + paramString;
    synchronized (str.intern())
    {
      if ((localCurrencyRatios = (CurrencyRatios)mServers.get(paramString)) == null)
      {
        localCurrencyRatios = new CurrencyRatios(paramString);
        mServers.put(paramString, localCurrencyRatios);
      }
    }
    return localCurrencyRatios;
  }

  public static BigDecimal GetCurrencyRatio(String paramString1, String paramString2, String paramString3, long paramLong)
    throws GoatException
  {
    return getRatios(paramString1).doGetCurrencyRatio(paramString1, paramString2, paramString3, paramLong);
  }

  public static CurrencyRatio GetCurrencyRatios(String paramString, long paramLong)
    throws GoatException
  {
    return getRatios(paramString).doGetCurrencyRatios(paramString, paramLong);
  }

  private CurrencyRatio doGetCurrencyRatios(String paramString, long paramLong)
    throws GoatException
  {
    synchronized (new Long(paramLong).toString().intern())
    {
      CurrencyRatio localCurrencyRatio1 = (CurrencyRatio)this.mRatios.get(new Long(paramLong).toString());
      if (localCurrencyRatio1 != null)
        if (localCurrencyRatio1.dateExpires < new Date().getTime())
          this.mRatios.remove(new Long(paramLong).toString());
        else
          return localCurrencyRatio1;
      ServerLogin localServerLogin = null;
      try
      {
        localServerLogin = SessionData.get().getServerLogin(paramString);
        List localList = localServerLogin.getMultipleCurrencyRatioSets(Arrays.asList(new Timestamp[] { new Timestamp(paramLong) }));
        CurrencyRatio localCurrencyRatio2 = new CurrencyRatio((String)localList.get(0), this.refreshInterval + new Date().getTime());
        this.mRatios.put(new Long(paramLong).toString(), localCurrencyRatio2);
        return localCurrencyRatio2;
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
    }
  }

  private BigDecimal doGetCurrencyRatio(String paramString1, String paramString2, String paramString3, long paramLong)
    throws GoatException
  {
    paramString1 = paramString1.toLowerCase();
    try
    {
      CurrencyRatio localCurrencyRatio = doGetCurrencyRatios(paramString1, paramLong);
      ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
      return localServerLogin.getCurrencyRatio(localCurrencyRatio.ratios, paramString2, paramString3);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
  }

  public static class CurrencyRatio
  {
    public String ratios;
    public long dateExpires;

    public CurrencyRatio(String paramString, long paramLong)
    {
      this.ratios = paramString;
      this.dateExpires = paramLong;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CurrencyRatios
 * JD-Core Version:    0.6.1
 */