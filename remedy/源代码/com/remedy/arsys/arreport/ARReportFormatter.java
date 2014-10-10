package com.remedy.arsys.arreport;

import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.DecimalFieldLimit;
import com.bmc.arsys.api.DiaryItem;
import com.bmc.arsys.api.DiaryListValue;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FuncCurrencyInfo;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.RealFieldLimit;
import com.bmc.arsys.api.Time;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.util.ARUtilEgcp;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.CurrencyField;
import com.remedy.arsys.goat.field.EnumField;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.TimeField;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.legacyshared.ARDataFormat;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.reporting.common.QueryExecutor;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class ARReportFormatter
{
  static final int ar_reserv_group_name = 105;
  static final int ar_reserv_group_id = 106;
  static final int ar_reserv_password = 102;
  static final int ar_reserv_encrypted_string = 123;
  static final int ar_currency_part_field = 0;
  static final int ar_currency_part_value = 1;
  static final int ar_currency_part_type = 2;
  static final int ar_currency_part_date = 3;
  static final int ar_currency_part_functional = 4;
  static final String PASSWORD_DISPLAY_STR = "*****";
  static final char AR_DEFN_STAT_HIST_SEP = '\003';
  static final char AR_DEFN_STAT_HIST_COMMA = '\004';
  static final String XMLVALUESTR = "value";
  private final CachedFieldMap mFieldMap;
  private final Form.ViewInfo mViewInfo;
  private final ServerLogin mContext;
  private final String mServer;
  private final Form mForm;
  private boolean bSetPrefs;
  private String mLocale;
  private String mTimeZone;
  private Long mDateTimeStyle;
  private String mCustomDateFormat;
  private String mCustomTimeFormat;
  private Entry[] mGroupEntries;

  public ARReportFormatter(ServerLogin paramServerLogin, Form paramForm, String paramString, CachedFieldMap paramCachedFieldMap, Form.ViewInfo paramViewInfo)
  {
    this.mContext = paramServerLogin;
    this.mForm = paramForm;
    this.mServer = paramString;
    this.mFieldMap = paramCachedFieldMap;
    this.mViewInfo = paramViewInfo;
  }

  public void setFormatPref(String paramString1, String paramString2, Long paramLong, String paramString3, String paramString4)
  {
    this.mLocale = paramString1;
    this.mTimeZone = paramString2;
    this.mDateTimeStyle = paramLong;
    this.mCustomDateFormat = paramString3;
    this.mCustomTimeFormat = paramString4;
    this.bSetPrefs = true;
  }

  private String expandXMLSpecials(String paramString)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '\b':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\b");
        i = j + 1;
        break;
      case '\f':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\f");
        i = j + 1;
        break;
      case '\n':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\n");
        i = j + 1;
        break;
      case '\r':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\r");
        i = j + 1;
        break;
      case '\\':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\\");
        i = j + 1;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }

  public static String expandSpecials(String paramString)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '\b':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\b");
        i = j + 1;
        break;
      case '\f':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\f");
        i = j + 1;
        break;
      case '\n':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\n");
        i = j + 1;
        break;
      case '\r':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\r");
        i = j + 1;
        break;
      case '"':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\"");
        i = j + 1;
        break;
      case '\\':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\\");
        i = j + 1;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }

  public HTMLWriter addIndents(HTMLWriter paramHTMLWriter, int paramInt)
  {
    for (int i = 0; i < paramInt; i++)
      paramHTMLWriter.increaseIndent();
    return paramHTMLWriter;
  }

  private String formatForXML(String paramString, int paramInt1, int paramInt2)
  {
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder());
    localHTMLWriter.setParams(false, false, paramInt2);
    addIndents(localHTMLWriter, paramInt1);
    localHTMLWriter.openTag("value").endTag(false).cdata(paramString).closeTag("value");
    return localHTMLWriter.toString();
  }

  public String formatTypeNULL(Object paramObject, long paramLong)
  {
    return "";
  }

  public String formatTypeNULLXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    return formatForXML("", paramInt1, paramInt2);
  }

  public String formatTypeInteger(Object paramObject, long paramLong)
  {
    return ((Integer)paramObject).toString();
  }

  public String formatTypeIntegerXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    return formatForXML(formatTypeInteger(paramObject, paramLong), paramInt1, paramInt2);
  }

  public String formatTypeReal(Object paramObject, long paramLong)
  {
    assert (this.bSetPrefs == true);
    Field localField = null;
    ARDataFormat localARDataFormat = new ARDataFormat();
    Double localDouble = (Double)paramObject;
    int i = 6;
    if (this.mFieldMap != null)
    {
      localField = (Field)this.mFieldMap.get(Integer.valueOf((int)paramLong));
      if (localField != null)
        i = ((RealFieldLimit)localField.getFieldLimit()).getPrecision();
    }
    if (i < 0)
      i = 6;
    return localARDataFormat.formatRealNumber(localDouble.toString(), this.mLocale, Integer.toString(i));
  }

  private String formatTypeRealExport(Object paramObject, long paramLong)
  {
    String str = formatTypeReal(paramObject, paramLong);
    return str.replace(',', '.');
  }

  public String formatTypeRealARX(Object paramObject, long paramLong)
  {
    return formatTypeRealExport(paramObject, paramLong);
  }

  public String formatTypeRealCSV(Object paramObject, long paramLong)
  {
    return formatTypeRealExport(paramObject, paramLong);
  }

  public String formatTypeRealXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    return formatForXML(formatTypeRealExport(paramObject, paramLong), paramInt1, paramInt2);
  }

  public String formatTypeChar(Object paramObject, long paramLong)
  {
    if (paramLong == CoreFieldId.Status.getFieldId())
      return null;
    String str = "";
    if (Group.isGroupField(paramLong))
      str = getGroupNames((String)paramObject);
    else if ((paramLong == 102L) || (paramLong == 123L))
      str = "*****";
    else
      str = (String)paramObject;
    return str;
  }

  private String exportEncryptedPassword(String paramString)
  {
    String str = new ARUtilEgcp().GCXPUtil(paramString);
    return str;
  }

  private String exportEncryptedString(String paramString)
  {
    String str = new ARUtilEgcp().GCXEUtil(paramString);
    return str;
  }

  public String formatTypeCharCSV(Object paramObject, long paramLong)
  {
    String str = (String)paramObject;
    if (paramLong == 102L)
      str = exportEncryptedPassword((String)paramObject);
    else if (paramLong == 123L)
      str = exportEncryptedString((String)paramObject);
    return str;
  }

  public String formatTypeCharARX(Object paramObject, long paramLong)
  {
    String str = (String)paramObject;
    if (paramLong == 102L)
      str = exportEncryptedPassword((String)paramObject);
    else if (paramLong == 123L)
      str = exportEncryptedString((String)paramObject);
    return str;
  }

  public String formatTypeCharXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    String str = (String)paramObject;
    if (paramLong == 102L)
      str = exportEncryptedPassword((String)paramObject);
    else if (paramLong == 123L)
      str = exportEncryptedString((String)paramObject);
    else
      str = expandXMLSpecials((String)paramObject);
    return formatForXML(str, paramInt1, paramInt2);
  }

  public String formatTypeTime(Object paramObject, long paramLong)
    throws GoatException
  {
    Field localField = null;
    int i = 0;
    if ((this.mFieldMap != null) && (this.mViewInfo.getID() > 0))
    {
      localField = (Field)this.mFieldMap.get(Integer.valueOf((int)paramLong));
      if (localField != null)
      {
        Map localMap = GoatField.get(this.mViewInfo);
        GoatField localGoatField = (GoatField)localMap.get(Integer.valueOf(localField.getFieldID()));
        if ((localGoatField != null) && (DataType.TIME.equals(localGoatField.getMDataType())))
          i = ((TimeField)localGoatField).getDateTimePopup();
      }
    }
    return NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, formatTypeTimeARX(paramObject, 0L), i, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat);
  }

  public String formatTypeTimeARX(Object paramObject, long paramLong)
  {
    Long localLong = new Long(((Timestamp)paramObject).getValue());
    return localLong.toString();
  }

  public String formatTypeTimeXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    Long localLong = new Long(((Timestamp)paramObject).getValue());
    return formatForXML(localLong.toString(), paramInt1, paramInt2);
  }

  public String formatTypeDate(Object paramObject, long paramLong)
  {
    return NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, formatTypeDateARX(paramObject, 0L), 4, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat);
  }

  public String formatTypeDateARX(Object paramObject, long paramLong)
  {
    Integer localInteger = new Integer(((DateInfo)paramObject).getValue());
    return localInteger.toString();
  }

  public String formatTypeDateXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    Integer localInteger = new Integer(((DateInfo)paramObject).getValue());
    return formatForXML(localInteger.toString(), paramInt1, paramInt2);
  }

  public String formatTypeTimeOfDay(Object paramObject, long paramLong)
  {
    return NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, formatTypeTimeOfDayARX(paramObject, 0L), 3, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat);
  }

  public String formatTypeTimeOfDayARX(Object paramObject, long paramLong)
  {
    Long localLong = new Long(((Time)paramObject).getValue());
    return localLong.toString();
  }

  public String formatTypeTimeOfDayXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    Long localLong = new Long(((Time)paramObject).getValue());
    return formatForXML(localLong.toString(), paramInt1, paramInt2);
  }

  public String formatTypeBitmask(Object paramObject, long paramLong)
  {
    return Long.toHexString(((Long)paramObject).longValue());
  }

  public String formatTypeDecimal(Object paramObject, long paramLong)
  {
    Field localField = null;
    ARDataFormat localARDataFormat = new ARDataFormat();
    BigDecimal localBigDecimal = (BigDecimal)paramObject;
    int i = 6;
    if (this.mFieldMap != null)
    {
      localField = (Field)this.mFieldMap.get(Integer.valueOf((int)paramLong));
      if (localField != null)
        i = ((DecimalFieldLimit)localField.getFieldLimit()).getPrecision();
    }
    if (i < 0)
      i = 6;
    return localARDataFormat.formatDecimalNumber(localBigDecimal.toString(), this.mLocale, Integer.toString(i));
  }

  private String formatTypeDecimalExport(Object paramObject, long paramLong)
  {
    BigDecimal localBigDecimal = (BigDecimal)paramObject;
    return localBigDecimal.toString();
  }

  public String formatTypeDecimalARX(Object paramObject, long paramLong)
  {
    return formatTypeDecimalExport(paramObject, paramLong);
  }

  public String formatTypeDecimalCSV(Object paramObject, long paramLong)
  {
    return formatTypeDecimalExport(paramObject, paramLong);
  }

  public String formatTypeDecimalXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    return formatForXML(formatTypeDecimalExport(paramObject, paramLong), paramInt1, paramInt2);
  }

  public String formatTypeUlong(Object paramObject, long paramLong)
  {
    return ((Long)paramObject).toString();
  }

  public String formatTypeEnum(Object paramObject, long paramLong)
  {
    String str = "";
    Field localField = null;
    Integer localInteger = (Integer)paramObject;
    str = localInteger.toString();
    if (this.mFieldMap != null)
    {
      localField = (Field)this.mFieldMap.get(Integer.valueOf((int)paramLong));
      if (localField != null)
      {
        EnumField localEnumField = new EnumField(this.mForm, localField, this.mViewInfo.mID);
        if ((localField != null) && (localEnumField != null))
          str = localEnumField.labelFromEnumId(localInteger.intValue());
      }
    }
    return str;
  }

  public String formatTypeEnumARX(Object paramObject, long paramLong)
  {
    return ((Integer)paramObject).toString();
  }

  public String formatTypeEnumXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    return formatForXML(((Integer)paramObject).toString(), paramInt1, paramInt2);
  }

  public String formatTypeKeyword(Object paramObject, long paramLong)
  {
    return ((Keyword)paramObject).toString();
  }

  public String formatTypeAttachment(Object paramObject, long paramLong)
  {
    return ((AttachmentValue)paramObject).getName();
  }

  public String formatTypeAttachmentXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    String str = ((AttachmentValue)paramObject).getName();
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(500));
    localHTMLWriter.setParams(false, false, paramInt2);
    addIndents(localHTMLWriter, paramInt1);
    localHTMLWriter.openTag("value").endTag();
    localHTMLWriter.openTag("attach_value").endTag();
    localHTMLWriter.openTag("attach_loc").endTag();
    localHTMLWriter.openTag("loc_type").endTag(false).cdata("1").closeTag("loc_type");
    localHTMLWriter.openTag("filename").endTag(false).cdata(expandSpecials(str)).closeTag("filename");
    localHTMLWriter.closeTag("attach_loc");
    localHTMLWriter.closeTag("attach_value");
    localHTMLWriter.closeTag("value");
    return localHTMLWriter.toString();
  }

  public String formatTypeAttachmentARX(Object paramObject, long paramLong)
  {
    return "1 " + ((AttachmentValue)paramObject).getName();
  }

  public static int getCurrencyPart(String paramString)
  {
    int i = 0;
    if ((paramString != null) && (paramString.length() > 0))
      try
      {
        String str = paramString.trim();
        i = Integer.parseInt(str.substring(0, 1));
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
    return i;
  }

  public String formatTypeCurrency(Object paramObject, long paramLong, String paramString)
    throws GoatException
  {
    String str = "";
    Field localField = null;
    ARDataFormat localARDataFormat = new ARDataFormat();
    int i = 0;
    if (paramString != null)
    {
      paramString = paramString.trim();
      i = getCurrencyPart(paramString);
    }
    CurrencyValue localCurrencyValue = (CurrencyValue)paramObject;
    if (i == 2)
    {
      str = localCurrencyValue.getCurrencyCode();
    }
    else
    {
      Object localObject1;
      if (i == 3)
      {
        localObject1 = localCurrencyValue.getConversionDate();
        str = NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, String.valueOf(((Timestamp)localObject1).getValue()), 0, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat);
      }
      else
      {
        localObject1 = localCurrencyValue.getCurrencyCode();
        BigDecimal localBigDecimal = null;
        Object localObject2;
        Object localObject3;
        if (i == 4)
        {
          List localList = localCurrencyValue.getFuncCurrencyList();
          if (localList != null)
          {
            localObject1 = paramString.substring(1).trim();
            localObject2 = localList.iterator();
            while (((Iterator)localObject2).hasNext())
            {
              localObject3 = (FuncCurrencyInfo)((Iterator)localObject2).next();
              if (((String)localObject1).equals(((FuncCurrencyInfo)localObject3).getCurrencyCode()))
              {
                localBigDecimal = ((FuncCurrencyInfo)localObject3).getValue();
                break;
              }
            }
          }
        }
        else
        {
          localBigDecimal = localCurrencyValue.getValue();
        }
        if (localBigDecimal != null)
        {
          int j = 2;
          if (this.mFieldMap != null)
          {
            localField = (Field)this.mFieldMap.get(Integer.valueOf((int)paramLong));
            if (localField != null)
            {
              localObject2 = GoatField.get(this.mViewInfo);
              localObject3 = (CurrencyField)((Map)localObject2).get(Integer.valueOf(localField.getFieldID()));
              if (localObject3 != null)
                j = ((CurrencyField)localObject3).getAllowPrecision(localCurrencyValue.getCurrencyCode());
            }
          }
          str = localARDataFormat.formatDecimalNumber(localBigDecimal.toString(), this.mLocale, Integer.toString(j));
          if (i != 1)
            str = str + " " + (String)localObject1;
        }
      }
    }
    return str;
  }

  public String formatTypeCurrencyExport(Object paramObject, long paramLong, char paramChar)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    CurrencyValue localCurrencyValue = (CurrencyValue)paramObject;
    localStringBuilder.append(localCurrencyValue.getValueString()).append(paramChar);
    localStringBuilder.append(localCurrencyValue.getCurrencyCode()).append(paramChar);
    long l = localCurrencyValue.getConversionDate().getValue();
    localStringBuilder.append(Long.toString(l)).append(paramChar);
    List localList = localCurrencyValue.getFuncCurrencyList();
    localStringBuilder.append(localList.size()).append(paramChar);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator.next();
      BigDecimal localBigDecimal = localFuncCurrencyInfo.getValue();
      if (localBigDecimal != null)
        if ((localBigDecimal.toString().length() == 0) && (paramChar == ' '))
          localStringBuilder.append(' ');
        else
          localStringBuilder.append(localBigDecimal.toString());
      localStringBuilder.append(paramChar);
      localStringBuilder.append(localFuncCurrencyInfo.getCurrencyCode()).append(paramChar);
    }
    return localStringBuilder.toString();
  }

  public String formatTypeCurrencyARX(Object paramObject, long paramLong)
  {
    return formatTypeCurrencyExport(paramObject, paramLong, '\004');
  }

  public String formatTypeCurrencyCSV(Object paramObject, long paramLong)
  {
    return formatTypeCurrencyExport(paramObject, paramLong, ' ');
  }

  public String formatTypeCurrencyXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(500));
    localHTMLWriter.setParams(false, false, paramInt2);
    addIndents(localHTMLWriter, paramInt1);
    CurrencyValue localCurrencyValue = (CurrencyValue)paramObject;
    localHTMLWriter.openTag("value").endTag();
    localHTMLWriter.openTag("currency").endTag();
    String str1 = expandSpecials(localCurrencyValue.getValueString());
    localHTMLWriter.openTag("currency_value").endTag(false).cdata(str1).closeTag("currency_value");
    str1 = expandSpecials(localCurrencyValue.getCurrencyCode());
    localHTMLWriter.openTag("currency_type").endTag(false).cdata(str1).closeTag("currency_type");
    long l = localCurrencyValue.getConversionDate().getValue();
    str1 = expandSpecials(Long.toString(l));
    localHTMLWriter.openTag("currency_date").endTag(false).cdata(str1).closeTag("currency_date");
    List localList = localCurrencyValue.getFuncCurrencyList();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(localList.size()).append(' ');
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      FuncCurrencyInfo localFuncCurrencyInfo = (FuncCurrencyInfo)localIterator.next();
      localStringBuilder.append(' ');
      BigDecimal localBigDecimal = localFuncCurrencyInfo.getValue();
      String str2 = "";
      if (localBigDecimal != null)
        str2 = localBigDecimal.toString();
      if (str2.length() == 0)
        str2 = " ";
      localStringBuilder.append(str2).append(' ');
      localStringBuilder.append(localFuncCurrencyInfo.getCurrencyCode());
    }
    str1 = expandSpecials(localStringBuilder.toString());
    localHTMLWriter.openTag("currency_functional").endTag(false).cdata(str1).closeTag("currency_functional");
    localHTMLWriter.closeTag("currency");
    localHTMLWriter.closeTag("value");
    return localHTMLWriter.toString();
  }

  public String formatTypeDiary(Object paramObject, long paramLong, boolean paramBoolean, String paramString1, String paramString2)
  {
    assert (this.bSetPrefs == true);
    String str2 = "";
    DiaryListValue localDiaryListValue = (DiaryListValue)paramObject;
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      DiaryItem[] arrayOfDiaryItem = (DiaryItem[])localDiaryListValue.toArray(new DiaryItem[0]);
      int i = 0;
      if (arrayOfDiaryItem != null)
        i = arrayOfDiaryItem.length;
      for (int j = 0; j < i; j++)
      {
        int k = paramBoolean ? j : i - 1 - j;
        localStringBuilder.append(arrayOfDiaryItem[k].getUser()).append(" ");
        String str1 = NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, formatTypeTimeARX(arrayOfDiaryItem[k].getTimestamp(), 0L), 0, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat);
        localStringBuilder.append(str1).append(paramString1).append(arrayOfDiaryItem[k].getText()).append(paramString2);
      }
    }
    finally
    {
      str2 = localStringBuilder.toString();
    }
    return str2;
  }

  public String formatTypeDiaryARX(Object paramObject, long paramLong)
  {
    return ((DiaryListValue)paramObject).getAppendedText();
  }

  public String formatTypeDiaryXML(Object paramObject, long paramLong, int paramInt1, int paramInt2)
  {
    DiaryListValue localDiaryListValue = (DiaryListValue)paramObject;
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(500));
    localHTMLWriter.setParams(false, false, paramInt2);
    addIndents(localHTMLWriter, paramInt1);
    DiaryItem[] arrayOfDiaryItem = (DiaryItem[])localDiaryListValue.toArray(new DiaryItem[0]);
    localHTMLWriter.openTag("value").endTag();
    for (int i = 0; i < arrayOfDiaryItem.length; i++)
    {
      localHTMLWriter.openTag("diary").endTag();
      Long localLong = new Long(arrayOfDiaryItem[i].getTimestamp().getValue());
      localHTMLWriter.openTag("date").endTag(false).cdata(localLong.toString()).closeTag("date");
      String str = arrayOfDiaryItem[i].getUser().toString();
      str = expandXMLSpecials(str);
      localHTMLWriter.openTag("user").endTag(false).cdata(str).closeTag("user");
      str = arrayOfDiaryItem[i].getText();
      str = expandXMLSpecials(str);
      localHTMLWriter.openTag("diary_contents").endTag(false).cdata(str).closeTag("diary_contents");
      localHTMLWriter.closeTag("diary");
    }
    localHTMLWriter.closeTag("value");
    return localHTMLWriter.toString();
  }

  private String getGroupNames(String paramString)
  {
    assert (this.mContext != null);
    assert (this.mServer != null);
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      if (this.mGroupEntries == null)
      {
        int i = 105;
        localObject1 = new int[] { i };
        localObject2 = SchemaKeyFactory.getInstance().getSchemaKey(ServerLogin.getAdmin(this.mServer), (int[])localObject1);
        localObject3 = new EntryListFieldInfo[2];
        localObject3[0] = new EntryListFieldInfo(106);
        localObject3[1] = new EntryListFieldInfo(105);
        localObject4 = new QueryExecutor(this.mContext, this.mServer, (String)localObject2, null, 0, 0L, (EntryListFieldInfo[])localObject3, null);
        this.mGroupEntries = ((QueryExecutor)localObject4).executeQuery();
      }
    }
    catch (Exception localException)
    {
      Log.get(0).log(Level.SEVERE, "Cannot retrieve entries from Group form", localException);
      return paramString;
    }
    Entry localEntry = null;
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    String str = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ";");
    while (localStringTokenizer.hasMoreElements())
    {
      if (localStringBuilder.length() > 0)
        localStringBuilder.append("; ");
      str = localStringTokenizer.nextToken();
      try
      {
        long l1 = Long.parseLong(str.trim());
        for (int j = 0; j < this.mGroupEntries.length; j++)
        {
          localEntry = this.mGroupEntries[j];
          if (localEntry != null)
          {
            localObject1 = ((Value)localEntry.get(Integer.valueOf(106))).getValue();
            localObject2 = ((Value)localEntry.get(Integer.valueOf(106))).getDataType();
            if (((DataType)localObject2).equals(DataType.INTEGER))
            {
              Integer localInteger = (Integer)localObject1;
              long l2 = localInteger.longValue();
              if (l2 == l1)
              {
                localObject3 = ((Value)localEntry.get(Integer.valueOf(105))).getValue();
                localObject4 = ((Value)localEntry.get(Integer.valueOf(105))).getDataType();
                if (((DataType)localObject4).equals(DataType.CHAR))
                  localStringBuilder.append((String)localObject3);
                else
                  localStringBuilder.append(str);
              }
            }
            else
            {
              localStringBuilder.append(str);
            }
          }
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localStringBuilder.append(str);
      }
    }
    return localStringBuilder.toString();
  }

  public String formatStatusHistory(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2, EnumLimit paramEnumLimit)
  {
    if (paramLong1 != CoreFieldId.StatusHistory.getFieldId())
      return "";
    StringBuilder localStringBuilder1 = new StringBuilder();
    StringBuilder localStringBuilder2 = new StringBuilder();
    int i = paramString.length();
    int j = 99;
    for (int k = 0; k < i; k++)
    {
      char c = paramString.charAt(k);
      if (k + 1 < i)
        j = paramString.charAt(k + 1);
      if ((k == 0) && (c == '\003'))
        localStringBuilder2.append(" \004 ");
      localStringBuilder2.append(c);
      if ((c == '\003') && (j == 3))
        localStringBuilder2.append(" \004 ");
      else if (((c == '\003') || (c == '\004')) && ((j == 3) || (j == 4)))
        localStringBuilder2.append(' ');
    }
    paramString = localStringBuilder2.toString();
    paramInt1 = 1 - (paramInt1 - paramInt2);
    if ((paramLong2 < 0L) || (paramInt1 < 0))
    {
      k = -1;
    }
    else
    {
      int m = paramEnumLimit.valToIndex(paramLong2);
      k = 2 * m + paramInt1;
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\003\004");
    int n = 0;
    int i1 = 0;
    String str = null;
    int i2 = paramEnumLimit.size();
    while (localStringTokenizer.hasMoreElements())
    {
      str = localStringTokenizer.nextToken();
      if (k >= 0)
      {
        if (n == k)
        {
          if (paramInt1 == 0)
          {
            localStringBuilder1.append(NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, str, 0, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat));
            break;
          }
          localStringBuilder1.append(str);
          break;
        }
      }
      else if (n % 2 == 0)
      {
        if (i1 >= i2)
          break;
        if (localStringBuilder1.length() > 0)
          localStringBuilder1.append("\n");
        localStringBuilder1.append(paramEnumLimit.valFromIndex(i1));
        localStringBuilder1.append(": ");
        localStringBuilder1.append(NativeReportServlet.formatDatesTimes(this.mLocale, this.mTimeZone, str, 0, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat));
      }
      else
      {
        str = str.trim();
        if (str.length() > 0)
          localStringBuilder1.append(": ");
        localStringBuilder1.append(str);
        i1++;
      }
      n++;
    }
    if (k == -1)
      while (i1 < i2)
        localStringBuilder1.append("\n").append(paramEnumLimit.valFromIndex(i1++)).append(":");
    return localStringBuilder1.toString();
  }

  private ArrayList makeSHArray(String paramString, EnumLimit paramEnumLimit)
  {
    int i = paramEnumLimit.size();
    ArrayList localArrayList1 = new ArrayList(i);
    for (int j = 0; j < i; j++)
      localArrayList1.add(j, null);
    String str1 = paramString;
    for (int m = 0; (j = str1.indexOf(3)) != -1; m++)
    {
      String str2 = str1.substring(0, j);
      str1 = str1.substring(j + 1);
      if (str2.length() > 0)
      {
        int k = str2.indexOf(4, 0);
        String str3 = str2.substring(0, k);
        String str4 = str2.substring(k + 1);
        ArrayList localArrayList2 = new ArrayList();
        localArrayList2.add(0, str3);
        localArrayList2.add(1, str4);
        localArrayList1.set(m, localArrayList2);
      }
    }
    assert (i == localArrayList1.size());
    return localArrayList1;
  }

  public String formatStatusHistoryXML(Object paramObject, long paramLong, EnumLimit paramEnumLimit, int paramInt1, int paramInt2)
  {
    String str = (String)paramObject;
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(500));
    localHTMLWriter.setParams(false, false, paramInt2);
    addIndents(localHTMLWriter, paramInt1);
    ArrayList localArrayList1 = makeSHArray(str, paramEnumLimit);
    int i = localArrayList1.size() - 1;
    for (int j = localArrayList1.size() - 1; (j >= 0) && ((ArrayList)localArrayList1.get(j) == null); j--)
      i--;
    j = i + 1;
    StringBuilder localStringBuilder = new StringBuilder();
    localHTMLWriter.openTag("value").endTag();
    for (int k = 0; k < j; k++)
    {
      ArrayList localArrayList2 = (ArrayList)localArrayList1.get(k);
      if (localArrayList2 == null)
      {
        localHTMLWriter.openTag("status_history").endTag().closeTag("status_history");
      }
      else
      {
        localHTMLWriter.openTag("status_history").endTag();
        localHTMLWriter.openTag("date").endTag(false).cdata((String)localArrayList2.get(0)).closeTag("date");
        localHTMLWriter.openTag("user").endTag(false).cdata((String)localArrayList2.get(1)).closeTag("user");
        localHTMLWriter.closeTag("status_history");
      }
    }
    localHTMLWriter.closeTag("value");
    return localHTMLWriter.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ARReportFormatter
 * JD-Core Version:    0.6.1
 */