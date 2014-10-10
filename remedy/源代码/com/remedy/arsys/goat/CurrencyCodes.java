package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.CurrencyDetail;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FormType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CurrencyCodes
  implements Serializable
{
  private static final long serialVersionUID = -6460188147852266243L;
  private static final int AR_RESERV_CURRENCY_CODE = 1521;
  private static final int AR_RESERV_CURRENCY_LOCALE = 1523;
  private static final int AR_RESERV_CURRENCY_LABEL = 1524;
  private static final int AR_RESERV_CURRENCY_DESCRIPTION = 8;
  private static final int AR_RESERV_CURRENCY_PRECISION = 1522;
  private static Map mServers = Collections.synchronizedMap(new HashMap());
  private Map mLocales = Collections.synchronizedMap(new HashMap());

  private CurrencyCodes(ServerLogin paramServerLogin)
    throws ARException
  {
    int[] arrayOfInt1 = { 1521, 1523, 1524 };
    List localList1 = paramServerLogin.getListForm(0L, FormType.REGULAR.toInt() | 0x400, null, arrayOfInt1);
    int[] arrayOfInt2 = { 1521, 8, 1522 };
    List localList2 = paramServerLogin.getListForm(0L, FormType.REGULAR.toInt() | 0x400, null, arrayOfInt2);
    TreeSet localTreeSet1 = new TreeSet();
    TreeSet localTreeSet2 = new TreeSet();
    if ((localList1 != null) && (localList1.size() > 0))
      localTreeSet1.addAll(localList1);
    if ((localList2 != null) && (localList2.size() > 0))
      localTreeSet2.addAll(localList2);
    QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(new Value(1)), new ArithmeticOrRelationalOperand(new Value(1))));
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new SortInfo(1521, 1));
    Iterator localIterator1 = localTreeSet1.iterator();
    String str1;
    List localList3;
    Entry localEntry;
    String str2;
    String str3;
    Object localObject;
    while (localIterator1.hasNext())
    {
      str1 = (String)localIterator1.next();
      try
      {
        localList3 = paramServerLogin.getListEntryObjects(str1, localQualifierInfo, 0, 0, localArrayList, arrayOfInt1, false, null);
      }
      catch (ARException localARException1)
      {
      }
      continue;
      Iterator localIterator2 = localList3.iterator();
      while (localIterator2.hasNext())
      {
        localEntry = (Entry)localIterator2.next();
        str2 = null;
        str3 = null;
        String str4 = null;
        int j = 2;
        if (localEntry.containsKey(Integer.valueOf(1521)))
          str2 = (String)((Value)localEntry.get(Integer.valueOf(1521))).getValue();
        if (localEntry.containsKey(Integer.valueOf(1523)))
          str4 = (String)((Value)localEntry.get(Integer.valueOf(1523))).getValue();
        if (localEntry.containsKey(Integer.valueOf(1524)))
          str3 = (String)((Value)localEntry.get(Integer.valueOf(1524))).getValue();
        if ((str3 != null) && (str2 != null))
        {
          localObject = "default";
          if (str4 != null)
            localObject = str4;
          Map localMap = (Map)this.mLocales.get(localObject);
          if (localMap == null)
          {
            localMap = Collections.synchronizedMap(new TreeMap());
            this.mLocales.put(localObject, localMap);
          }
          localMap.put(str2, new CurrencyCode(str2, str3, j));
        }
      }
    }
    localIterator1 = localTreeSet2.iterator();
    while (localIterator1.hasNext())
    {
      str1 = (String)localIterator1.next();
      try
      {
        localList3 = paramServerLogin.getListEntryObjects(str1, localQualifierInfo, 0, 0, localArrayList, arrayOfInt2, false, null);
      }
      catch (ARException localARException2)
      {
      }
      continue;
      Iterator localIterator3 = localList3.iterator();
      while (localIterator3.hasNext())
      {
        localEntry = (Entry)localIterator3.next();
        str2 = null;
        str3 = null;
        int i = 2;
        if (localEntry.containsKey(Integer.valueOf(1521)))
          str2 = (String)((Value)localEntry.get(Integer.valueOf(1521))).getValue();
        if (localEntry.containsKey(Integer.valueOf(8)))
          str3 = (String)((Value)localEntry.get(Integer.valueOf(8))).getValue();
        if (localEntry.containsKey(Integer.valueOf(1522)))
          i = ((Integer)((Value)localEntry.get(Integer.valueOf(1522))).getValue()).intValue();
        if ((str3 != null) && (str2 != null))
        {
          String str5 = "default";
          localObject = (Map)this.mLocales.get(str5);
          if (localObject == null)
          {
            localObject = Collections.synchronizedMap(new TreeMap());
            this.mLocales.put(str5, localObject);
          }
          ((Map)localObject).put(str2, new CurrencyCode(str2, str3, i));
        }
      }
    }
  }

  public static String GetCurrencyLabel(ServerLogin paramServerLogin, String paramString1, String paramString2)
    throws GoatException
  {
    CurrencyCodes localCurrencyCodes = getCodes(paramServerLogin);
    String str = localCurrencyCodes.GetCurrencyLabel(paramString1, paramString2);
    if (str == null)
      str = localCurrencyCodes.GetCurrencyLabel(paramString1.substring(0, 2), paramString2);
    if (str == null)
      str = localCurrencyCodes.GetCurrencyLabel("default", paramString2);
    return str;
  }

  private String GetCurrencyLabel(String paramString1, String paramString2)
  {
    Map localMap = (Map)this.mLocales.get(paramString1);
    if (localMap == null)
      return null;
    CurrencyCode localCurrencyCode = (CurrencyCode)localMap.get(paramString2);
    return localCurrencyCode != null ? localCurrencyCode.description : null;
  }

  public static CurrencyDetail[] GetAllCurrencyCodes(ServerLogin paramServerLogin)
    throws GoatException
  {
    CurrencyCodes localCurrencyCodes = getCodes(paramServerLogin);
    return localCurrencyCodes.GetAllCurrencyCodes();
  }

  private CurrencyDetail[] GetAllCurrencyCodes()
  {
    Map localMap = (Map)this.mLocales.get("default");
    if (localMap == null)
      return new CurrencyDetail[0];
    Collection localCollection = localMap.values();
    CurrencyDetail[] arrayOfCurrencyDetail = new CurrencyDetail[localCollection.size()];
    Iterator localIterator = localCollection.iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      CurrencyCode localCurrencyCode = (CurrencyCode)localIterator.next();
      arrayOfCurrencyDetail[i] = new CurrencyDetail();
      arrayOfCurrencyDetail[i].setCurrencyCode(localCurrencyCode.code);
      arrayOfCurrencyDetail[(i++)].setPrecision(localCurrencyCode.precision);
    }
    return arrayOfCurrencyDetail;
  }

  private static CurrencyCodes getCodes(ServerLogin paramServerLogin)
    throws GoatException
  {
    CurrencyCodes localCurrencyCodes = null;
    String str = "CurrencyCodes:" + paramServerLogin.getServer();
    synchronized (str.intern())
    {
      if ((localCurrencyCodes = (CurrencyCodes)mServers.get(paramServerLogin.getServer())) == null)
      {
        try
        {
          localCurrencyCodes = new CurrencyCodes(paramServerLogin);
        }
        catch (ARException localARException)
        {
          throw new GoatException(localARException);
        }
        mServers.put(paramServerLogin.getServer(), localCurrencyCodes);
      }
    }
    return localCurrencyCodes;
  }

  public static class CurrencyCode
  {
    public String code;
    public String description;
    public int precision;

    public CurrencyCode(String paramString1, String paramString2, int paramInt)
    {
      this.code = paramString1;
      this.description = paramString2;
      this.precision = paramInt;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CurrencyCodes
 * JD-Core Version:    0.6.1
 */