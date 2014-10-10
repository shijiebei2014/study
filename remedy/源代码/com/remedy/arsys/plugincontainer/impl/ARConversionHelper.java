package com.remedy.arsys.plugincontainer.impl;

import com.bmc.arsys.api.CurrencyFieldLimit;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DiaryItem;
import com.bmc.arsys.api.DiaryListValue;
import com.remedy.arsys.api.ARServerUser;
import com.remedy.arsys.api.AccessNameID;
import com.remedy.arsys.api.CurrencyInfo;
import com.remedy.arsys.api.CurrencyLimitInfo;
import com.remedy.arsys.api.Diary;
import com.remedy.arsys.api.DiaryInfo;
import com.remedy.arsys.api.NameID;
import com.remedy.arsys.api.ProxyManager;
import com.remedy.arsys.api.Util;
import com.remedy.arsys.api.VerifyUserCriteria;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.Configuration.ServerInformation;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ARConversionHelper
{
  private static Object MContextLock = new Object();
  private static Map<String, ARServerUser> MContextMap = Collections.synchronizedMap(new HashMap());

  public static CurrencyValue convertToCurrent(CurrencyInfo paramCurrencyInfo)
  {
    com.remedy.arsys.api.FuncCurrencyInfo[] arrayOfFuncCurrencyInfo = paramCurrencyInfo.getFuncCurrencyList();
    ArrayList localArrayList = new ArrayList();
    if ((arrayOfFuncCurrencyInfo != null) && (arrayOfFuncCurrencyInfo.length > 0))
      for (int i = 0; i < arrayOfFuncCurrencyInfo.length; i++)
        if (arrayOfFuncCurrencyInfo[i] != null)
        {
          com.bmc.arsys.api.FuncCurrencyInfo localFuncCurrencyInfo = new com.bmc.arsys.api.FuncCurrencyInfo(arrayOfFuncCurrencyInfo[i].getValue(), arrayOfFuncCurrencyInfo[i].getCurrencyCode());
          localArrayList.add(localFuncCurrencyInfo);
        }
    com.bmc.arsys.api.Timestamp localTimestamp = new com.bmc.arsys.api.Timestamp(paramCurrencyInfo.getConversionDate().getValue());
    return new CurrencyValue(paramCurrencyInfo.getValue(), paramCurrencyInfo.getCurrencyCode(), localTimestamp, localArrayList);
  }

  public static CurrencyFieldLimit convertToCurrent(CurrencyLimitInfo paramCurrencyLimitInfo)
  {
    com.remedy.arsys.api.CurrencyDetail[] arrayOfCurrencyDetail1 = paramCurrencyLimitInfo.getFunctional();
    com.remedy.arsys.api.CurrencyDetail[] arrayOfCurrencyDetail2 = paramCurrencyLimitInfo.getAllowable();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i;
    if ((arrayOfCurrencyDetail1 != null) && (arrayOfCurrencyDetail1.length > 0))
      for (i = 0; i < arrayOfCurrencyDetail1.length; i++)
        if (arrayOfCurrencyDetail1[i] != null)
          localArrayList2.add(new com.bmc.arsys.api.CurrencyDetail(arrayOfCurrencyDetail1[i].getCurrencyCode(), arrayOfCurrencyDetail1[i].getPrecision()));
    if ((arrayOfCurrencyDetail2 != null) && (arrayOfCurrencyDetail2.length > 0))
      for (i = 0; i < arrayOfCurrencyDetail2.length; i++)
        if (arrayOfCurrencyDetail2[i] != null)
          localArrayList1.add(new com.bmc.arsys.api.CurrencyDetail(arrayOfCurrencyDetail2[i].getCurrencyCode(), arrayOfCurrencyDetail2[i].getPrecision()));
    return new CurrencyFieldLimit(paramCurrencyLimitInfo.getLowRange(), paramCurrencyLimitInfo.getHighRange(), paramCurrencyLimitInfo.getDataType(), localArrayList2, localArrayList1);
  }

  public static DiaryItem[] convertToCurrent(DiaryInfo[] paramArrayOfDiaryInfo)
  {
    DiaryItem[] arrayOfDiaryItem = null;
    if ((paramArrayOfDiaryInfo != null) && (paramArrayOfDiaryInfo.length > 0))
    {
      arrayOfDiaryItem = new DiaryItem[paramArrayOfDiaryInfo.length];
      for (int i = 0; i < paramArrayOfDiaryInfo.length; i++)
        arrayOfDiaryItem[i] = convertToCurrent(paramArrayOfDiaryInfo[i]);
    }
    return arrayOfDiaryItem;
  }

  public static DiaryItem convertToCurrent(DiaryInfo paramDiaryInfo)
  {
    if (paramDiaryInfo == null)
      return null;
    com.bmc.arsys.api.Timestamp localTimestamp = new com.bmc.arsys.api.Timestamp(paramDiaryInfo.getTimestamp().getValue());
    return new DiaryItem(paramDiaryInfo.getUser().getValue(), paramDiaryInfo.getDiaryInfo(), localTimestamp);
  }

  public static com.bmc.arsys.api.DateInfo convertToCurrent(com.remedy.arsys.api.DateInfo paramDateInfo)
  {
    return new com.bmc.arsys.api.DateInfo(paramDateInfo.getValue());
  }

  public static com.bmc.arsys.api.Time convertToCurrent(com.remedy.arsys.api.Time paramTime)
  {
    return new com.bmc.arsys.api.Time(paramTime.getValue());
  }

  public static com.bmc.arsys.api.Timestamp convertToCurrent(com.remedy.arsys.api.Timestamp paramTimestamp)
  {
    return new com.bmc.arsys.api.Timestamp(paramTimestamp.getValue());
  }

  public static DiaryListValue convertToCurrent(Diary paramDiary)
  {
    if (paramDiary == null)
      return null;
    DiaryListValue localDiaryListValue = null;
    try
    {
      localDiaryListValue = DiaryListValue.decode(paramDiary.getValue());
    }
    catch (com.bmc.arsys.api.ARException localARException)
    {
      localDiaryListValue = new DiaryListValue();
    }
    return localDiaryListValue;
  }

  public static com.bmc.arsys.api.Value convertToCurrent(com.remedy.arsys.api.Value paramValue)
  {
    if (paramValue == null)
      return null;
    Object localObject = null;
    switch (paramValue.getDataType().toInt())
    {
    case 13:
      localObject = convertToCurrent((com.remedy.arsys.api.DateInfo)paramValue.getValue());
      break;
    case 14:
      localObject = convertToCurrent((com.remedy.arsys.api.Time)paramValue.getValue());
      break;
    case 7:
      localObject = convertToCurrent((com.remedy.arsys.api.Timestamp)paramValue.getValue());
      break;
    case 10:
      localObject = paramValue.getValue();
      break;
    case 3:
      localObject = paramValue.getValue();
      break;
    case 12:
      localObject = convertToCurrent((CurrencyInfo)paramValue.getValue());
      break;
    case 5:
      localObject = convertToCurrent((Diary)paramValue.getValue());
      break;
    case 6:
      localObject = new Integer(((Long)paramValue.getValue()).intValue());
      break;
    case 4:
    case 8:
    case 9:
    case 11:
    default:
      localObject = paramValue.toString();
    }
    return new com.bmc.arsys.api.Value(localObject, com.bmc.arsys.api.DataType.toDataType(paramValue.getDataType().toInt()));
  }

  public static void cleanContext(ServerLogin paramServerLogin)
  {
    if (paramServerLogin == null)
      return;
    String str = getKey(paramServerLogin);
    synchronized (str)
    {
      Map localMap;
      synchronized (MContextLock)
      {
        localMap = MContextMap;
      }
      if (localMap.containsKey(str))
      {
        ??? = (ARServerUser)localMap.remove(str);
        if (??? != null)
          ((ARServerUser)???).logout();
      }
    }
  }

  private static String getKey(ServerLogin paramServerLogin)
  {
    String str = "CONTEXT:";
    str = str + paramServerLogin.getServer() + ":" + paramServerLogin.getPort() + ":" + paramServerLogin.getUser() + ":" + paramServerLogin.getReservedParam1();
    return str;
  }

  public static ARServerUser convertTo70(ServerLogin paramServerLogin)
    throws GoatException
  {
    if (paramServerLogin == null)
      return null;
    String str1 = getKey(paramServerLogin);
    synchronized (str1)
    {
      synchronized (paramServerLogin)
      {
        Map localMap;
        synchronized (MContextLock)
        {
          localMap = MContextMap;
        }
        ??? = (ARServerUser)localMap.get(str1);
        if (??? != null)
          return ???;
        ??? = new ARServerUser();
        String str2 = paramServerLogin.getServer();
        AccessNameID localAccessNameID1 = new AccessNameID(paramServerLogin.getUser());
        AccessNameID localAccessNameID2 = new AccessNameID(paramServerLogin.getPasswd());
        ((ARServerUser)???).setUser(localAccessNameID1);
        ((ARServerUser)???).setPassword(localAccessNameID2);
        if (paramServerLogin.getAuthentication() != null)
          ((ARServerUser)???).setAuthentication(paramServerLogin.getAuthentication());
        ((ARServerUser)???).setServer(str2);
        String str3 = paramServerLogin.getLocale();
        if (str3 != null)
          ((ARServerUser)???).setLocale(str3);
        String str4 = paramServerLogin.getTimeZone();
        if (str4 != null)
          ((ARServerUser)???).setTimeZone(str4);
        String str5 = paramServerLogin.getCustomDateFormat();
        if (str5 != null)
          ((ARServerUser)???).setCustomDateFormat(str5);
        String str6 = paramServerLogin.getCustomTimeFormat();
        if (str6 != null)
          ((ARServerUser)???).setCustomTimeFormat(str6);
        com.bmc.arsys.api.LoggingInfo localLoggingInfo = paramServerLogin.getLogging();
        if (localLoggingInfo != null)
        {
          com.remedy.arsys.api.LoggingInfo localLoggingInfo1 = new com.remedy.arsys.api.LoggingInfo(localLoggingInfo.isEnabled(), localLoggingInfo.getType(), localLoggingInfo.getWriteToFileOrStatus(), localLoggingInfo.getPath());
          ((ARServerUser)???).setLogging(localLoggingInfo1);
        }
        try
        {
          Util.ARSetSessionConfiguration((ARServerUser)???, 7, new com.remedy.arsys.api.Value(paramServerLogin.getClientType()));
          int i = Configuration.getInstance().getPreferNativeViews() ? 1 : 3;
          Util.ARSetSessionConfiguration((ARServerUser)???, 8, new com.remedy.arsys.api.Value(i));
          ((ARServerUser)???).setPort(paramServerLogin.getPort());
          Util.ARSetServerPort((ARServerUser)???, new NameID(str2), paramServerLogin.getPort(), ServerLogin.getServerInformation(str2).getRPC());
          if (paramServerLogin.getReservedParam1() != null)
          {
            Util.ARSetSessionConfiguration((ARServerUser)???, 1960, new com.remedy.arsys.api.Value(paramServerLogin.getReservedParam1()));
            Util.ARSetSessionConfiguration((ARServerUser)???, 9, new com.remedy.arsys.api.Value(paramServerLogin.getOverridePrevIP() ? 1 : 0));
          }
          VerifyUserCriteria localVerifyUserCriteria = new VerifyUserCriteria();
          localVerifyUserCriteria.setPropertiesToRetrieve(VerifyUserCriteria.ADMIN_FLAG | VerifyUserCriteria.SUBADMIN_FLAG);
          ((ARServerUser)???).verifyUser(localVerifyUserCriteria);
        }
        catch (com.remedy.arsys.api.ARException localARException)
        {
          throw new GoatException(localARException.getLocalizedMessage());
        }
        synchronized (MContextLock)
        {
          localMap = MContextMap;
        }
        localMap.put(str1, ???);
        return ???;
      }
    }
  }

  static
  {
    int i = Configuration.getInstance().getIntProperty("arsystem.pooling_max_connections_per_server", 80);
    ProxyManager.setConnectionLimits(i);
    ProxyManager.setUseConnectionPooling(Configuration.getInstance().getEnableUseConnectionPooling());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.ARConversionHelper
 * JD-Core Version:    0.6.1
 */