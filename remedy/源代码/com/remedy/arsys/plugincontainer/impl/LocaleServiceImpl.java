package com.remedy.arsys.plugincontainer.impl;

import com.bmc.arsys.api.CurrencyDetail;
import com.bmc.arsys.api.CurrencyFieldLimit;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.DecimalFieldLimit;
import com.bmc.arsys.api.DiaryItem;
import com.bmc.arsys.api.DiaryListValue;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldLimit;
import com.bmc.arsys.api.LocalizedRequestInfo;
import com.bmc.arsys.api.RealFieldLimit;
import com.bmc.arsys.api.Time;
import com.bmc.arsys.api.Timestamp;
import com.remedy.arsys.api.CurrencyInfo;
import com.remedy.arsys.api.CurrencyLimitInfo;
import com.remedy.arsys.api.DiaryInfo;
import com.remedy.arsys.api.FieldID;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.LocaleUtil;
import com.remedy.arsys.goat.field.EnumField;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.type.CurrencyType;
import com.remedy.arsys.goat.field.type.DateType;
import com.remedy.arsys.goat.field.type.DecimalType;
import com.remedy.arsys.goat.field.type.DiaryType;
import com.remedy.arsys.goat.field.type.RealType;
import com.remedy.arsys.goat.field.type.TODType;
import com.remedy.arsys.goat.field.type.TimeType;
import com.remedy.arsys.goat.intf.service.IGoatFieldFactory;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.plugincontainer.ARLocaleService;
import com.remedy.arsys.plugincontainer.ARLocaleServiceEX;
import com.remedy.arsys.plugincontainer.ARLocaleServiceProperties;
import com.remedy.arsys.plugincontainer.DefinitionKey;
import com.remedy.arsys.plugincontainer.LocalizedStringID;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.SessionData;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

public class LocaleServiceImpl
  implements ARLocaleService, ARLocaleServiceEX, ARLocaleServiceProperties
{
  private String mServer;
  private String mPluginName;
  private Properties mLocaleServiceProps = new Properties();
  private static final Log MLog = Log.get(13);
  private static final String FLASHBOARD = "Flashboard";
  private static final String RTL = "rtl";

  public LocaleServiceImpl(String paramString1, String paramString2, Map paramMap)
  {
    this.mServer = paramString1;
    this.mPluginName = paramString2;
    buildLocaleServiceProperties(paramMap, this.mLocaleServiceProps);
  }

  public Locale getLocale()
  {
    String str = SessionData.get().getLocale();
    if (str.length() < 3)
      return new Locale(str);
    if (str.length() < 6)
      return new Locale(str.substring(0, 2), str.substring(3));
    return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
  }

  public TimeZone getTimeZone()
  {
    return TimeZone.getTimeZone(SessionData.get().getTimeZone());
  }

  public String formatCurrency(CurrencyValue paramCurrencyValue, CurrencyFieldLimit paramCurrencyFieldLimit)
  {
    return CurrencyType.format(paramCurrencyValue, (CurrencyDetail[])paramCurrencyFieldLimit.getAllowable().toArray(new CurrencyDetail[0]));
  }

  public String[] getLocalizedStrings(LocalizedStringID[] paramArrayOfLocalizedStringID)
    throws IOException
  {
    assert (paramArrayOfLocalizedStringID != null);
    LocalizedRequestInfo[] arrayOfLocalizedRequestInfo = new LocalizedRequestInfo[paramArrayOfLocalizedStringID.length];
    for (int i = paramArrayOfLocalizedStringID.length - 1; i >= 0; i--)
    {
      LocalizedStringID localLocalizedStringID1 = paramArrayOfLocalizedStringID[i];
      assert (localLocalizedStringID1 != null);
      arrayOfLocalizedRequestInfo[i] = new LocalizedRequestInfo(this.mPluginName + ":" + localLocalizedStringID1.getDefinitionKey(), 15, localLocalizedStringID1.getNumber());
    }
    String[] arrayOfString = MessageTranslation.getServerMultipleLocalizedMessage(this.mServer, SessionData.get().getLocale(), arrayOfLocalizedRequestInfo);
    if ((arrayOfString == null) && ("Flashboard".equals(this.mPluginName)))
    {
      for (int j = paramArrayOfLocalizedStringID.length - 1; j >= 0; j--)
      {
        LocalizedStringID localLocalizedStringID2 = paramArrayOfLocalizedStringID[j];
        assert (localLocalizedStringID2 != null);
        arrayOfLocalizedRequestInfo[j] = new LocalizedRequestInfo(localLocalizedStringID2.getDefinitionKey().toString(), 15, localLocalizedStringID2.getNumber());
      }
      arrayOfString = MessageTranslation.getServerMultipleLocalizedMessage(this.mServer, SessionData.get().getLocale(), arrayOfLocalizedRequestInfo);
    }
    if (arrayOfString == null)
      throw new IOException(String.valueOf(9402));
    return arrayOfString;
  }

  public String getLocalizedErrorMessage(int paramInt, Object[] paramArrayOfObject)
  {
    LocalizedRequestInfo[] arrayOfLocalizedRequestInfo = new LocalizedRequestInfo[1];
    arrayOfLocalizedRequestInfo[0] = new LocalizedRequestInfo(this.mPluginName, 14, paramInt);
    String[] arrayOfString = MessageTranslation.getServerMultipleLocalizedMessage(this.mServer, SessionData.get().getLocale(), arrayOfLocalizedRequestInfo);
    if (arrayOfString == null)
      return null;
    Locale localLocale = LocaleUtil.getLocale(SessionData.get().getLocale());
    return new MessageFormat(arrayOfString[0], localLocale).format(paramArrayOfObject);
  }

  public String formatDate(Date paramDate)
  {
    return DateType.format(paramDate);
  }

  public String formatDate(int paramInt)
  {
    return DateType.format(paramInt);
  }

  public String formatTime(Date paramDate)
  {
    return TODType.format(paramDate);
  }

  public String formatTime(long paramLong)
  {
    return TODType.format(paramLong);
  }

  public String formatDateAndTime(Date paramDate)
  {
    return TimeType.format(paramDate, 0);
  }

  public String formatDateAndTime(long paramLong)
  {
    return TimeType.format(paramLong, 0);
  }

  public String formatDecimal(BigDecimal paramBigDecimal, int paramInt)
  {
    return DecimalType.format(paramBigDecimal, paramInt);
  }

  public String formatDouble(Double paramDouble, int paramInt)
  {
    return RealType.format(paramDouble, paramInt);
  }

  public String formatFloat(Float paramFloat, int paramInt)
  {
    return formatDouble(new Double(paramFloat.doubleValue()), paramInt);
  }

  public String formatDiary(DiaryItem[] paramArrayOfDiaryItem)
  {
    return DiaryType.format(paramArrayOfDiaryItem);
  }

  public String formatEnumeration(Long paramLong, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    try
    {
      GoatField localGoatField = getGoatField(paramString1, paramString2, paramString3, paramInt);
      if ((localGoatField instanceof EnumField))
      {
        com.bmc.arsys.api.Value localValue = new com.bmc.arsys.api.Value(new Integer(paramLong.intValue()), DataType.ENUM);
        EnumField localEnumField = (EnumField)localGoatField;
        return localEnumField.labelFromEnumId(localValue);
      }
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.WARNING, "Could not get enum field info for fieldId " + paramInt + " on form " + paramString2 + " on server " + paramString1, localGoatException);
    }
    return paramLong.toString();
  }

  public String formatValue(com.bmc.arsys.api.Value paramValue, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    try
    {
      switch (paramValue.getDataType().toInt())
      {
      case 13:
        return formatDate(((DateInfo)paramValue.getValue()).getValue());
      case 14:
        return formatTime(((Time)paramValue.getValue()).getValue() * 1000L);
      case 7:
        return formatDateAndTime(((Timestamp)paramValue.getValue()).getValue() * 1000L);
      case 10:
        return formatDecimal((BigDecimal)paramValue.getValue(), getPrecision(paramString1, paramString2, paramInt));
      case 3:
        return formatDouble((Double)paramValue.getValue(), getPrecision(paramString1, paramString2, paramInt));
      case 12:
        Field localField = getField(paramString1, paramString2, paramInt);
        if ((localField != null) && ((localField.getFieldLimit() instanceof CurrencyFieldLimit)))
          return formatCurrency((CurrencyValue)paramValue.getValue(), (CurrencyFieldLimit)localField.getFieldLimit());
        break;
      case 5:
        return formatDiary((DiaryItem[])((DiaryListValue)paramValue.getValue()).toArray(new DiaryItem[0]));
      case 6:
        if (("".equals(paramString3)) || (paramString3 == null))
          paramString3 = Form.get(paramString1, paramString2).getViewInfoByInference(paramString3, false, false).getName();
        return formatEnumeration(Long.valueOf(paramValue.getValue().toString()), paramString1, paramString2, paramString3, paramInt);
      case 4:
      case 8:
      case 9:
      case 11:
      default:
        return paramValue.toString();
      }
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.WARNING, "Could not get field info for fieldId " + paramInt + " on form " + paramString2 + " on server " + paramString1, localGoatException);
    }
    return paramValue.toString();
  }

  public String getFieldLabel(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    try
    {
      GoatField localGoatField = getGoatField(paramString1, paramString2, paramString3, paramInt);
      if (localGoatField != null)
      {
        String str = localGoatField.getLabel();
        if (str == null)
          return localGoatField.getMDBName();
        return str;
      }
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.WARNING, "Could not get enum field info for fieldId " + paramInt + " on form " + paramString2 + " on server " + paramString1, localGoatException);
    }
    return "" + paramInt;
  }

  private GoatField getGoatField(String paramString1, String paramString2, String paramString3, int paramInt)
    throws GoatException
  {
    Form localForm = Form.get(paramString1, paramString2);
    Field localField = (Field)localForm.getCachedFieldMap().get(Integer.valueOf(paramInt));
    Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(paramString3, false, false);
    if (localField != null)
    {
      IGoatFieldFactory localIGoatFieldFactory = (IGoatFieldFactory)ServiceLocator.getInstance().getService("goatfieldFactory");
      return localIGoatFieldFactory.create(localForm, localViewInfo.getID(), localField);
    }
    return null;
  }

  private Field getField(String paramString1, String paramString2, int paramInt)
    throws GoatException
  {
    Form localForm = Form.get(paramString1, paramString2);
    return (Field)localForm.getCachedFieldMap().get(Integer.valueOf(paramInt));
  }

  private int getPrecision(String paramString1, String paramString2, int paramInt)
    throws GoatException
  {
    Field localField = getField(paramString1, paramString2, paramInt);
    if (localField != null)
    {
      FieldLimit localFieldLimit = localField.getFieldLimit();
      if ((localFieldLimit instanceof DecimalFieldLimit))
        return ((DecimalFieldLimit)localFieldLimit).getPrecision();
      if ((localFieldLimit instanceof RealFieldLimit))
        return ((RealFieldLimit)localFieldLimit).getPrecision();
    }
    return 2;
  }

  public String formatCurrency(CurrencyInfo paramCurrencyInfo, CurrencyLimitInfo paramCurrencyLimitInfo)
  {
    CurrencyValue localCurrencyValue = ARConversionHelper.convertToCurrent(paramCurrencyInfo);
    CurrencyFieldLimit localCurrencyFieldLimit = ARConversionHelper.convertToCurrent(paramCurrencyLimitInfo);
    return formatCurrency(localCurrencyValue, localCurrencyFieldLimit);
  }

  public String formatDiary(DiaryInfo[] paramArrayOfDiaryInfo)
  {
    DiaryItem[] arrayOfDiaryItem = ARConversionHelper.convertToCurrent(paramArrayOfDiaryInfo);
    return formatDiary(arrayOfDiaryItem);
  }

  public String formatEnumeration(Long paramLong, String paramString1, String paramString2, String paramString3, FieldID paramFieldID)
  {
    return formatEnumeration(paramLong, paramString1, paramString2, paramString3, (int)paramFieldID.getValue());
  }

  public String getFieldLabel(String paramString1, String paramString2, String paramString3, FieldID paramFieldID)
  {
    return getFieldLabel(paramString1, paramString2, paramString3, (int)paramFieldID.getValue());
  }

  public String formatValue(com.remedy.arsys.api.Value paramValue, String paramString1, String paramString2, String paramString3, FieldID paramFieldID)
  {
    com.bmc.arsys.api.Value localValue = ARConversionHelper.convertToCurrent(paramValue);
    return formatValue(localValue, paramString1, paramString2, paramString3, (int)paramFieldID.getValue());
  }

  public String getProperty(String paramString1, String paramString2)
  {
    return this.mLocaleServiceProps.getProperty(paramString1, paramString2);
  }

  private static void buildLocaleServiceProperties(Map paramMap, Properties paramProperties)
  {
    String[] arrayOfString = (String[])paramMap.get("rtl");
    if ((arrayOfString != null) && (arrayOfString.length > 0) && (!arrayOfString[0].equalsIgnoreCase("")) && (arrayOfString[0].equalsIgnoreCase("rtl")))
      paramProperties.setProperty("rtl", "true");
    else
      paramProperties.setProperty("rtl", "false");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.LocaleServiceImpl
 * JD-Core Version:    0.6.1
 */