package com.remedy.arsys.legacyshared;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

public class ARDataFormat
{
  public static final int CUSTOM_STYLE = 4;
  private static final int YEAR = 0;
  private static final int MONTH = 1;
  private static final int DAY = 2;
  private static final int HOUR = 3;
  private static final int MINUTE = 4;
  private static final int SECOND = 5;
  private static final int WEEKDAY = 6;
  private static final int WEEKDAY_STRING = 7;
  public static final int TYPE_DATETIME = 0;
  public static final int TYPE_DATETIME_TIME_ONLY = 1;
  public static final int TYPE_DATETIME_DATE_ONLY = 2;
  public static final int TYPE_TIME_OF_DAY = 3;
  public static final int TYPE_DATE_ONLY = 4;
  private static final int SECONDS_IN_HOUR = 3600;
  private static final int SECONDS_IN_MIN = 60;
  private static final char SCIENTIFIC_NOTATION_LOWER_CH = 'e';
  private static final char SCIENTIFIC_NOTATION_UPPER_CH = 'E';
  private static final char NEGATIVE_SIGN = '-';
  private static final int PARSE_STATE_INTEGER = 0;
  private static final int PARSE_STATE_FRACTION = 1;
  private static final int PARSE_STATE_EXPONENT = 2;
  private static final String[][] displayNameToCountry = { { "United Arab Emirates", "ae" }, { "Bahrain", "BH" }, { "Algeria", "DZ" }, { "Egypt", "EG" }, { "Iraq", "IQ" }, { "Jordan", "JO" }, { "Kuwait", "KW" }, { "Lebanon", "LB" }, { "Libya", "LY" }, { "Morocco", "MA" }, { "Oman", "OM" }, { "Qatar", "QA" }, { "Saudi Arabia", "SA" }, { "Sudan", "SD" }, { "Syria", "SY" }, { "Tunisia", "TN" }, { "Yemen", "YE" }, { "Byelorussia", "BY" }, { "Bulgaria", "BG" }, { "Spain", "ES" }, { "Czech Republic", "CZ" }, { "Denmark", "DK" }, { "Austria", "AT" }, { "Switzerland", "CH" }, { "Germany", "DE" }, { "Luxembourg", "LU" }, { "Greece", "GR" }, { "Australia", "AU" }, { "Canada", "CA" }, { "United Kingdom", "GB" }, { "Ireland", "IE" }, { "New Zealand", "NZ" }, { "United States", "US" }, { "South Africa", "ZA" }, { "Argentina", "AR" }, { "Bolivia", "BO" }, { "Chile", "CL" }, { "Colombia", "CO" }, { "Costa Rica", "CR" }, { "Dominican Republic", "DO" }, { "Ecuador", "EC" }, { "Spain", "ES" }, { "Guatemala", "GT" }, { "Honduras", "HN" }, { "Mexico", "MX" }, { "Nicaragua", "NI" }, { "Panama", "PA" }, { "Peru", "PE" }, { "Puerto Rico", "PR" }, { "Paraguay", "PY" }, { "El Salvador", "SV" }, { "Uruguay", "UY" }, { "Venezuela", "VE" }, { "Estonia", "EE" }, { "Finland", "FI" }, { "France", "FR" }, { "Luxembourg", "LU" }, { "Croatia", "HR" }, { "Hungary", "HU" }, { "Iceland", "IS" }, { "Switzerland", "CH" }, { "Italy", "IT" }, { "Israel", "IL" }, { "Japan", "JP" }, { "Korea", "KR" }, { "Lithuania", "LT" }, { "Latvia", "LV" }, { "Macedonia", "MK" }, { "Belgium", "BE" }, { "Netherlands", "NL" }, { "Norway", "NO" }, { "Poland", "PL" }, { "Brazil", "BR" }, { "Portugal", "PT" }, { "Romania", "RO" }, { "Russia", "RU" }, { "Yugoslavia", "YU" }, { "Slovakia", "SK" }, { "Slovenia", "SL" }, { "Albania", "AL" }, { "Yugoslavia", "YU" }, { "Sweden", "SE" }, { "Thailand", "TH" }, { "Turkey", "TR" }, { "Ukraine", "UA" }, { "China", "CN" }, { "Hong Kong", "HK" }, { "Taiwan", "TW" } };
  private DateFormat m_dateFormat = null;
  private DecimalFormat m_decimalFormat = null;
  private DecimalFormat m_parseDecimalFormat = null;
  private Hashtable m_datePattern = null;
  private Hashtable m_timePattern = null;
  private Hashtable m_customPattern = null;

  private boolean useSimpleDateFormat(int paramInt1, int paramInt2, String paramString)
  {
    if (((paramInt2 == 4) && (paramInt1 == 3)) || ((paramInt2 == 0) && (paramInt1 == 3)))
      return true;
    return (paramInt1 == 4) && (paramString != null) && (paramString.length() != 0);
  }

  public boolean setDateFormat(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
  {
    boolean bool = (paramInt != 1) && (paramInt != 3);
    int i = getFormattingStyle(paramString3, bool);
    if (i < 0)
      return false;
    Locale localLocale = convertToLocaleObject(paramString1);
    if (localLocale == null)
      return false;
    return setDateFormat(localLocale, paramString2, i, paramInt, paramString4);
  }

  private String replaceTwoDigitYear(String paramString)
  {
    String str = paramString;
    int i = paramString.indexOf("yyyy");
    int j = paramString.indexOf("yy");
    if ((i == -1) && (j != -1))
    {
      str = paramString.substring(0, j);
      str = str + "yyyy";
      str = str + paramString.substring(j + 2);
    }
    return str;
  }

  public boolean setDateFormat(Locale paramLocale, String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    Object localObject;
    if ((paramInt1 == 4) && ((paramString2 == null) || (paramString2.length() == 0)))
    {
      localObject = new Throwable("setDateFormat() caller error, is custom style but custom format is empty!!!");
      ((Throwable)localObject).printStackTrace();
    }
    if (useSimpleDateFormat(paramInt1, paramInt2, paramString2))
    {
      int m;
      if ((paramInt2 == 4) && ((paramInt1 == 3) || (paramInt1 == 4)))
      {
        localObject = paramString2;
        if (paramInt1 == 3)
        {
          SimpleDateFormat localSimpleDateFormat1 = (SimpleDateFormat)DateFormat.getDateInstance(3, paramLocale);
          localObject = localSimpleDateFormat1.toPattern();
        }
        paramString2 = (String)localObject;
        int j = ((String)localObject).indexOf("yyyy");
        m = ((String)localObject).indexOf("yy");
        if ((j == -1) && (m != -1))
        {
          paramString2 = replaceTwoDigitYear((String)localObject);
        }
        else if (paramInt1 != 4)
        {
          SimpleDateFormat localSimpleDateFormat2 = (SimpleDateFormat)DateFormat.getDateInstance(2, paramLocale);
          String str2 = localSimpleDateFormat2.toPattern();
          if ((str2.indexOf("yyyy") == -1) && (str2.indexOf("yy") != -1))
            paramString2 = replaceTwoDigitYear(str2);
        }
      }
      else if ((paramInt2 == 0) && (paramInt1 == 3) && (paramInt1 != 4))
      {
        localObject = (SimpleDateFormat)DateFormat.getDateTimeInstance(3, 2, paramLocale);
        String str1 = paramString2;
        str1 = ((SimpleDateFormat)localObject).toPattern();
        paramString2 = str1;
        m = str1.indexOf("yyyy");
        int n = str1.indexOf("yy");
        if ((m == -1) && (n != -1))
          paramString2 = replaceTwoDigitYear(str1);
      }
      this.m_dateFormat = new SimpleDateFormat(paramString2, paramLocale);
    }
    else
    {
      int i = paramInt1 == 1 ? 0 : paramInt1;
      int k = 2;
      if (paramInt1 == 1)
        k = (paramLocale.getLanguage().equals("ja")) || (paramLocale.getLanguage().equals("zh")) || (paramLocale.getLanguage().equals("ko")) ? 0 : 1;
      switch (paramInt2)
      {
      case 2:
      case 4:
        this.m_dateFormat = DateFormat.getDateInstance(i, paramLocale);
        break;
      case 1:
      case 3:
        this.m_dateFormat = DateFormat.getTimeInstance(k, paramLocale);
        break;
      default:
        this.m_dateFormat = DateFormat.getDateTimeInstance(i, k, paramLocale);
      }
    }
    TimeZone localTimeZone = getTimeZone(paramString1);
    this.m_dateFormat.setTimeZone(localTimeZone);
    if (paramInt2 == 4)
    {
      GregorianCalendar localGregorianCalendar = new GregorianCalendar(localTimeZone);
      this.m_dateFormat.setCalendar(localGregorianCalendar);
    }
    else
    {
      this.m_dateFormat.setLenient(false);
    }
    return true;
  }

  public String formatDateTime(String paramString1, int paramInt, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if (!setDateFormat(paramString3, paramString4, paramString1, paramInt, paramString5))
      return " Error - Incorrect locale/formatting style ";
    return formatDateTime(paramString2, paramInt, new Locale(paramString3, ""));
  }

  public String formatDateTime(String paramString, int paramInt, Locale paramLocale)
  {
    try
    {
      long l = Long.parseLong(paramString);
      Date localDate = new Date(l * 1000L);
      if (localDate == null)
        return " Error Invalid date passed ";
      if (paramInt != 4)
      {
        localObject = this.m_dateFormat.format(localDate);
        return localObject;
      }
      Object localObject = new CalendarInfo((int)l, paramLocale);
      int i = ((CalendarInfo)localObject).getYear();
      int j = ((CalendarInfo)localObject).getMonth();
      int k = ((CalendarInfo)localObject).getDay();
      int m = ((CalendarInfo)localObject).getEra();
      j -= 1;
      if (j < 0)
      {
        i -= 1;
        j = 12;
      }
      if (i > 9999)
      {
        i = 9999;
        j = 0;
        k = 1;
      }
      GregorianCalendar localGregorianCalendar = (GregorianCalendar)this.m_dateFormat.getCalendar();
      localGregorianCalendar.set(1, i);
      localGregorianCalendar.set(2, j);
      localGregorianCalendar.set(5, k);
      localGregorianCalendar.set(0, m);
      localDate = localGregorianCalendar.getTime();
      StringBuffer localStringBuffer1 = new StringBuffer();
      StringBuffer localStringBuffer2 = new StringBuffer();
      FieldPosition localFieldPosition = new FieldPosition(1);
      localStringBuffer1 = this.m_dateFormat.format(localDate, localStringBuffer2, localFieldPosition);
      String str1 = localStringBuffer1.toString();
      if (str1.length() == 0)
        return " Error Invalid date passed ";
      if (m == 0)
      {
        Vector localVector1 = CalendarInfo.getPossibleEraValues(paramLocale);
        if ((localVector1 != null) && (localVector1.size() > 0))
        {
          SimpleDateFormat localSimpleDateFormat = (SimpleDateFormat)this.m_dateFormat;
          String str2 = localSimpleDateFormat.toPattern();
          Vector localVector2 = getPatternContents(str2, false);
          String str3 = ((Character)localVector2.elementAt(0)).toString();
          StringTokenizer localStringTokenizer1 = new StringTokenizer(str2, str3);
          StringTokenizer localStringTokenizer2 = new StringTokenizer(str1, str3);
          String str4 = null;
          String str5 = null;
          int n = -1;
          while ((localStringTokenizer1.hasMoreElements()) && (localStringTokenizer2.hasMoreElements()))
          {
            str4 = (String)localStringTokenizer1.nextElement();
            str4 = str4.toLowerCase().trim();
            str5 = (String)localStringTokenizer2.nextElement();
            n += str5.length() + 1;
            if (str4.startsWith("y"))
            {
              String str6 = str1.substring(0, n);
              String str7 = null;
              if (n < str1.length())
                str7 = str1.substring(n);
              if ((str6 != null) && (str6.length() > 0))
              {
                str1 = str6 + " " + localVector1.elementAt(0);
                if ((str7 != null) && (str7.length() > 0))
                  str1 = str1 + " " + str7;
              }
            }
          }
        }
      }
      str1 = str1.trim();
      return str1;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return " Error Invalid date passed ";
    }
    catch (Exception localException)
    {
    }
    return " Error Not a valid date format";
  }

  public static String mapTimeFormatLongToLegacyString(Long paramLong)
  {
    assert (paramLong != null);
    String str;
    if (paramLong.equals(ARUserPreferences.CUSTOM_TIME_FORMAT))
      str = "0";
    else if (paramLong.equals(ARUserPreferences.LONG_TIME_FORMAT))
      str = "2";
    else if (paramLong.equals(ARUserPreferences.SHORT_TIME_FORMAT))
      str = "1";
    else
      str = "4";
    return str;
  }

  public static boolean isLongTimeFormat(String paramString)
  {
    return paramString.equals("2");
  }

  public static boolean isCustomTimeFormat(String paramString)
  {
    return paramString.equals("0");
  }

  private int getFormattingStyle(String paramString, boolean paramBoolean)
  {
    int i = 3;
    try
    {
      i = Integer.parseInt(paramString);
      switch (i)
      {
      case 0:
        return 4;
      case 1:
        if (paramBoolean)
          return 3;
        return 2;
      case 2:
        return 1;
      case 3:
        return 2;
      case 4:
        return 0;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return -1;
  }

  public static Locale convertToLocaleObject(String paramString)
  {
    String str1 = "";
    String str2 = "";
    int i = paramString.indexOf("_");
    if (i > 0)
    {
      try
      {
        str1 = paramString.substring(0, i);
        str2 = paramString.substring(i + 1, paramString.length());
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException1)
      {
        return null;
      }
    }
    else
    {
      int j = paramString.indexOf("(");
      if (j > 0)
        try
        {
          str1 = paramString.substring(0, j);
          str2 = paramString.substring(j + 1, paramString.length() - 1);
        }
        catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException2)
        {
          return null;
        }
      else
        return new Locale(paramString, "");
    }
    return new Locale(str1, str2);
  }

  public String getLocalizedDatePattern(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    Locale localLocale = convertToLocaleObject(paramString1);
    if (localLocale == null)
      return "Error in getting the locale object";
    SimpleDateFormat localSimpleDateFormat = null;
    int i = getFormattingStyle(paramString2, true);
    int j = getFormattingStyle(paramString3, false);
    if ((i == -1) || (j == -1))
      return "Error Incorrect formatting style";
    switch (paramInt)
    {
    case 2:
    case 4:
      localSimpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance(i, localLocale);
      break;
    case 1:
    case 3:
      localSimpleDateFormat = (SimpleDateFormat)DateFormat.getTimeInstance(j, localLocale);
      break;
    case 0:
      localSimpleDateFormat = (SimpleDateFormat)DateFormat.getDateTimeInstance(i, j, localLocale);
      break;
    default:
      localSimpleDateFormat = (SimpleDateFormat)DateFormat.getDateTimeInstance(i, j, localLocale);
    }
    return localSimpleDateFormat.toPattern();
  }

  private Vector getPatternContents(String paramString, boolean paramBoolean)
  {
    Vector localVector1 = new Vector();
    char[] arrayOfChar = paramString.toCharArray();
    StringBuilder localStringBuilder = new StringBuilder();
    char c = 'S';
    for (int i = 0; i < arrayOfChar.length; i++)
      if (Character.isLetterOrDigit(arrayOfChar[i]))
      {
        localStringBuilder.append(arrayOfChar[i]);
      }
      else
      {
        if (arrayOfChar[i] != ' ')
          c = arrayOfChar[i];
        localVector1.addElement(localStringBuilder.toString());
        localStringBuilder = new StringBuilder();
      }
    if (!localStringBuilder.toString().equals(""))
      localVector1.addElement(localStringBuilder.toString());
    if (paramBoolean)
      return localVector1;
    Vector localVector2 = new Vector();
    localVector2.addElement(new Character(c));
    return localVector2;
  }

  public String getDateTimeInfo(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    TimeZone localTimeZone = getTimeZone(paramString4);
    Locale localLocale = convertToLocaleObject(paramString3);
    if ((localLocale == null) || (localTimeZone == null))
      return "Error in getting date/time info";
    Calendar localCalendar = Calendar.getInstance(localTimeZone, localLocale);
    Date localDate = null;
    if ((paramString1 != null) && (!paramString1.equals("")))
      try
      {
        long l = Long.parseLong(paramString1);
        localDate = new Date(l * 1000L);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return "Error in getting date/time info";
      }
    else
      localDate = new Date();
    localCalendar.setTime(localDate);
    int i = Integer.parseInt(paramString2);
    switch (i)
    {
    case 2:
      return String.valueOf(localCalendar.get(5));
    case 1:
      return String.valueOf(localCalendar.get(2) + 1);
    case 0:
      return String.valueOf(localCalendar.get(1));
    case 3:
      return String.valueOf(localCalendar.get(11));
    case 4:
      return String.valueOf(localCalendar.get(12));
    case 5:
      return String.valueOf(localCalendar.get(13));
    case 6:
      return String.valueOf(localCalendar.get(7));
    case 7:
      DateFormatSymbols localDateFormatSymbols = new DateFormatSymbols(localLocale);
      String[] arrayOfString = localDateFormatSymbols.getWeekdays();
      return arrayOfString[localCalendar.get(7)];
    }
    return "Error in getting date/time info";
  }

  public static TimeZone getTimeZone(String paramString)
  {
    TimeZone localTimeZone = TimeZone.getTimeZone(paramString);
    if ((paramString == null) || (paramString.length() <= 0) || (localTimeZone == null) || (localTimeZone.getID().length() <= 0) || (localTimeZone.getID().startsWith("?")))
      localTimeZone = TimeZone.getDefault();
    return localTimeZone;
  }

  public void setDecimalFormat(Locale paramLocale, boolean paramBoolean, int paramInt)
  {
    this.m_decimalFormat = ((DecimalFormat)NumberFormat.getInstance(paramLocale));
    this.m_decimalFormat.setGroupingUsed(paramBoolean);
    this.m_decimalFormat.setMinimumIntegerDigits(1);
    this.m_decimalFormat.setMinimumFractionDigits(paramInt);
    this.m_decimalFormat.setMaximumFractionDigits(paramInt);
  }

  public Number parseNumber(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean1)
    {
      int i = paramString1.indexOf(' ');
      if (i >= 0)
        paramString1 = paramString1.substring(0, i);
    }
    Locale localLocale = null;
    if ((paramString2 != null) && (paramString2.length() > 0))
      localLocale = convertToLocaleObject(paramString2);
    if (localLocale == null)
      localLocale = Locale.getDefault();
    if (paramString1 == null)
      return null;
    paramString1 = paramString1.trim();
    int j = paramString1.length();
    if (j == 0)
      return null;
    this.m_parseDecimalFormat = ((DecimalFormat)NumberFormat.getInstance(localLocale));
    paramString1 = prepareNumberString(paramString1, this.m_parseDecimalFormat, paramBoolean1);
    if (paramString1 == null)
      return null;
    j = paramString1.length();
    Object localObject1 = null;
    int k = 0;
    ParsePosition localParsePosition = new ParsePosition(k);
    int m = -1;
    m = paramString1.indexOf('E');
    if (m == -1)
    {
      m = paramString1.indexOf('e');
      if (m != -1)
        paramString1 = paramString1.replace('e', 'E');
    }
    if (paramBoolean2)
      return createBigDecimal(paramString1, this.m_parseDecimalFormat, true, paramBoolean1);
    String str = System.getProperty("java.version");
    try
    {
      if (((str != null) && (!str.startsWith("1.1")) && (!str.startsWith("1.0"))) || (m == -1))
      {
        localObject1 = this.m_parseDecimalFormat.parse(paramString1, localParsePosition);
        if ((localObject1 == null) || ((paramBoolean1) && (localParsePosition.getIndex() != j)))
          return null;
      }
      else
      {
        int n = -1;
        Object localObject2 = null;
        Object localObject3 = null;
        while (k < j)
        {
          if (n == k)
            return null;
          localObject1 = this.m_parseDecimalFormat.parse(paramString1, localParsePosition);
          int i1 = paramString1.charAt(k);
          if ((i1 == 45) && (((k != 0) && (m != k - 1)) || (k == j - 1)))
            return null;
          if ((localObject1 == null) && ((i1 == 69) || (i1 == 101)) && (k + 1 < j))
          {
            m = k;
            localParsePosition.setIndex(k + 1);
          }
          else if (localObject1 != null)
          {
            if (m >= 0)
              localObject3 = localObject1;
            else
              localObject2 = localObject1;
          }
          else
          {
            if (!paramBoolean1)
              break;
            return null;
          }
          n = k;
          k = localParsePosition.getIndex();
        }
        if ((localObject3 == null) || (localObject3.intValue() == 0))
          localObject1 = localObject2;
        else
          localObject1 = new Double(localObject2.doubleValue() + "E" + localObject3.intValue());
      }
    }
    catch (Exception localException1)
    {
      try
      {
        localObject1 = new Double(paramString1);
      }
      catch (Exception localException2)
      {
        return null;
      }
      localParsePosition.setIndex(j);
    }
    if (localObject1 == null)
      return null;
    return localObject1;
  }

  public String formatRealNumber(String paramString1, String paramString2, String paramString3)
  {
    Locale localLocale = convertToLocaleObject(paramString2);
    if ((localLocale == null) || (paramString1 == null) || (paramString1.length() == 0))
      return "Error Cannot format number";
    try
    {
      int i = Integer.parseInt(paramString3);
      setDecimalFormat(localLocale, false, i);
      return formatNumber(paramString1, localLocale, i, false, false);
    }
    catch (Exception localException)
    {
    }
    return "Error";
  }

  public String formatDecimalNumber(String paramString1, String paramString2, String paramString3)
  {
    Locale localLocale = convertToLocaleObject(paramString2);
    if ((localLocale == null) || (paramString1 == null) || (paramString1.length() == 0))
      return "Error Cannot format number";
    try
    {
      int i = Integer.parseInt(paramString3);
      setDecimalFormat(localLocale, true, i);
      return formatNumber(paramString1, localLocale, i, true, true);
    }
    catch (Exception localException)
    {
    }
    return "Error";
  }

  private String formatNumber(String paramString, Locale paramLocale, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str1 = null;
    if ((paramLocale == null) || (paramString == null) || (paramString.length() == 0))
      str1 = "Error Cannot format number";
    else
      try
      {
        Object localObject = null;
        String str2 = Locale.US.toString();
        if (paramLocale.toString().equals(str2))
        {
          if (paramBoolean2)
          {
            localObject = createBigDecimal(paramString, this.m_decimalFormat, false, true);
            if (localObject == null)
              return "Error";
          }
          else
          {
            localObject = new Double(paramString);
          }
        }
        else
        {
          localObject = parseNumber(paramString, str2, true, paramBoolean2);
          if (localObject == null)
            return "Error";
        }
        if (paramBoolean2)
        {
          if (!(localObject instanceof BigDecimal))
            localObject = new BigDecimal(((Number)localObject).doubleValue());
          str1 = formatBigDecimal((BigDecimal)localObject, paramInt);
        }
        else
        {
          if (!(localObject instanceof Double))
            localObject = new Double(((Number)localObject).doubleValue());
          str1 = formatDouble((Double)localObject);
        }
      }
      catch (Exception localException)
      {
        str1 = "Error";
      }
    return str1;
  }

  private String prepareNumberString(String paramString, DecimalFormat paramDecimalFormat, boolean paramBoolean)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramDecimalFormat == null))
      return null;
    int i = paramString.length();
    char[] arrayOfChar = new char[i + 1];
    int j = 0;
    DecimalFormatSymbols localDecimalFormatSymbols = paramDecimalFormat.getDecimalFormatSymbols();
    char c1 = localDecimalFormatSymbols.getDecimalSeparator();
    char c2 = localDecimalFormatSymbols.getGroupingSeparator();
    int k = paramDecimalFormat.getGroupingSize();
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = -1;
    int i4 = -1;
    int i5 = -1;
    int i6 = 0;
    for (int i7 = 0; i7 < i; i7++)
    {
      char c3 = paramString.charAt(i7);
      if (Character.isDigit(c3))
      {
        if (m == 0)
        {
          i2++;
          if ((i1 != 0) && (i2 % k == 0))
            i3 = j;
          if ((i7 == i - 1) && (i1 != 0) && (i2 % k != 0))
            n = 1;
        }
        else if (m == 2)
        {
          i6 = 1;
        }
        if (n == 0)
          arrayOfChar[(j++)] = c3;
      }
      else if ((c3 == c2) && (m == 0))
      {
        if (i1 == 0)
        {
          if (i2 == 0)
          {
            n = 1;
          }
          else
          {
            i1 = 1;
            i3 = j - 1;
          }
        }
        else if ((i2 == 0) || (i2 % k != 0))
          n = 1;
        if (n == 0)
          i2 = 0;
      }
      else if ((c3 == c1) && (m == 0))
      {
        if ((i1 != 0) && ((i2 == 0) || (i2 % k != 0)))
        {
          n = 1;
        }
        else
        {
          i4 = j;
          arrayOfChar[(j++)] = c3;
          m = 1;
        }
      }
      else if (((c3 == '-') || (c3 == '+')) && (m != 1))
      {
        if (m == 0)
        {
          if (j != 0)
            n = 1;
        }
        else if (j != i5 + 1)
          n = 1;
        if (n == 0)
          arrayOfChar[(j++)] = c3;
      }
      else if (((c3 == 'E') || (c3 == 'e')) && (m != 2))
      {
        if (m == 0)
        {
          if ((i1 != 0) && ((i2 == 0) || (i2 % k != 0)))
            n = 1;
        }
        else if ((m == 1) && (j == i4 + 1))
          j = i4;
        if (n == 0)
        {
          i5 = j;
          arrayOfChar[(j++)] = 'E';
          m = 2;
        }
      }
      else
      {
        n = 1;
      }
      if (n != 0)
      {
        if (paramBoolean)
          return null;
        if (m == 0)
        {
          if (i3 < 0)
            break;
          j = i3 + 1;
          break;
        }
        if (m == 1)
        {
          if (j != i4 + 1)
            break;
          j = i4;
          break;
        }
        if ((m != 2) || (i6 != 0))
          break;
        j = i5;
        break;
      }
    }
    paramString = new String(arrayOfChar, 0, j);
    return paramString;
  }

  private BigDecimal createBigDecimal(String paramString, DecimalFormat paramDecimalFormat, boolean paramBoolean1, boolean paramBoolean2)
  {
    BigDecimal localBigDecimal = null;
    if (paramDecimalFormat == null)
      return null;
    if (!paramBoolean1)
      paramString = prepareNumberString(paramString, paramDecimalFormat, paramBoolean2);
    if (paramString == null)
      return null;
    DecimalFormatSymbols localDecimalFormatSymbols = paramDecimalFormat.getDecimalFormatSymbols();
    char c = localDecimalFormatSymbols.getDecimalSeparator();
    if ((c != '.') && (paramString.indexOf(c) >= 0))
      paramString = paramString.replace(c, '.');
    int i = paramString.indexOf('E');
    if (i < 0)
      i = paramString.indexOf('e');
    try
    {
      if (i >= 0)
      {
        String str1 = paramString.substring(0, i);
        String str2 = paramString.substring(i + 1);
        localBigDecimal = new BigDecimal(str1);
        localBigDecimal = localBigDecimal.movePointRight(Integer.parseInt(str2));
      }
      else
      {
        localBigDecimal = new BigDecimal(paramString);
      }
    }
    catch (Exception localException)
    {
      return null;
    }
    return localBigDecimal;
  }

  private String formatDouble(Double paramDouble)
  {
    String str1 = paramDouble.toString();
    int i = str1.indexOf('E');
    if (i < 0)
      i = str1.indexOf('e');
    DecimalFormatSymbols localDecimalFormatSymbols = this.m_decimalFormat.getDecimalFormatSymbols();
    int j = localDecimalFormatSymbols.getZeroDigit();
    if ((i >= 0) && (this.m_decimalFormat != null))
    {
      String str2 = str1.substring(0, i);
      String str3 = str1.substring(i);
      if (j != 48)
        str3 = str3.replace('0', localDecimalFormatSymbols.getZeroDigit());
      paramDouble = new Double(str2);
      str2 = this.m_decimalFormat.format(paramDouble);
      str1 = str2 + str3;
    }
    else
    {
      str1 = this.m_decimalFormat.format(paramDouble);
    }
    if ((str1.charAt(0) == '-') && (str1.charAt(1) == j))
    {
      int k = localDecimalFormatSymbols.getDecimalSeparator();
      int m = str1.length();
      for (int n = 2; (n < m) && ((str1.charAt(n) == j) || (str1.charAt(n) == k)); n++);
      if (n == m)
        str1 = str1.substring(1);
    }
    return str1;
  }

  private String formatBigDecimal(BigDecimal paramBigDecimal, int paramInt)
  {
    if (this.m_decimalFormat == null)
      return null;
    paramBigDecimal = paramBigDecimal.setScale(paramInt, 4);
    Object localObject1 = paramBigDecimal.toString();
    int i = 0;
    if (((String)localObject1).charAt(0) == '-')
    {
      localObject1 = ((String)localObject1).substring(1);
      i = 1;
    }
    int j = ((String)localObject1).indexOf('.');
    Object localObject2 = localObject1;
    String str = null;
    int k = 0;
    if (j != -1)
    {
      localObject2 = ((String)localObject1).substring(0, j);
      str = ((String)localObject1).substring(j + 1);
      k = str.length();
    }
    DecimalFormatSymbols localDecimalFormatSymbols = this.m_decimalFormat.getDecimalFormatSymbols();
    int m = ((String)localObject2).length();
    int n = this.m_decimalFormat.getGroupingSize();
    int i1 = m / n;
    StringBuilder localStringBuilder;
    int i2;
    char c2;
    int i3;
    if (i1 > 0)
    {
      localStringBuilder = new StringBuilder(m + i1 + 5);
      i2 = m % n;
      if (i2 > 0)
        localStringBuilder.append(((String)localObject2).substring(0, i2));
      c2 = localDecimalFormatSymbols.getGroupingSeparator();
      for (i3 = 0; i3 < i1; i3++)
      {
        if (i2 > 0)
          localStringBuilder.append(c2);
        localStringBuilder.append(((String)localObject2).substring(i2, i2 + n));
        i2 += n;
      }
      localObject2 = localStringBuilder.toString();
    }
    if (k < paramInt)
    {
      localStringBuilder = new StringBuilder(paramInt + 5);
      if ((str != null) && (k > 0))
        localStringBuilder.append(str);
      i2 = localStringBuilder.length();
      c2 = localDecimalFormatSymbols.getZeroDigit();
      for (i3 = 0; i3 < paramInt - i2; i3++)
        localStringBuilder.append(c2);
      str = localStringBuilder.toString();
      k = str.length();
    }
    if (i != 0)
      localObject2 = "-" + (String)localObject2;
    if ((str != null) && (k > 0))
    {
      char c1 = localDecimalFormatSymbols.getDecimalSeparator();
      localObject1 = (String)localObject2 + c1 + str;
    }
    else
    {
      localObject1 = localObject2;
    }
    return localObject1;
  }

  public String formatElapsedTime(long paramLong, String paramString)
  {
    String str1 = ":";
    String str2 = "";
    if (paramLong < 0L)
    {
      paramLong *= -1L;
      str2 = "-";
    }
    long l1 = paramLong / 3600L;
    long l2 = paramLong - l1 * 3600L;
    long l3 = l2 / 60L;
    long l4 = l2 - l3 * 60L;
    String str3 = str2 + l1 + str1 + l3 + str1 + l4;
    return str3;
  }

  public String getCustomFormat(boolean paramBoolean, String paramString1, String paramString2, int paramInt, String paramString3)
  {
    if (!paramBoolean)
      return "";
    if ((paramString1 == null) || (paramString1.length() == 0))
      return "";
    if ((paramInt == 4) || (paramInt == 2))
      return paramString1;
    if ((paramString2.equals("")) || (paramString2 == null))
      paramString2 = getLocalizedDatePattern(paramString3, String.valueOf(3), String.valueOf(3), 1);
    if ((paramInt == 3) || (paramInt == 1))
      return paramString2;
    String str = paramString1 + " " + paramString2;
    return str;
  }

  private Hashtable getDatePatterns(Locale paramLocale)
  {
    Hashtable localHashtable = null;
    if (this.m_datePattern != null)
      localHashtable = (Hashtable)this.m_datePattern.get(paramLocale);
    if (localHashtable == null)
    {
      int[] arrayOfInt = { 3, 2, 0, 1 };
      int i = arrayOfInt.length;
      SimpleDateFormat localSimpleDateFormat = null;
      String str = null;
      localHashtable = new Hashtable(i);
      int j = 0;
      int k = 0;
      while (j < i)
      {
        localSimpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance(arrayOfInt[j], paramLocale);
        str = localSimpleDateFormat.toPattern();
        if (str != null)
          localHashtable.put(new Integer(k++), str);
        j++;
      }
      if (this.m_datePattern == null)
        this.m_datePattern = new Hashtable();
      this.m_datePattern.put(paramLocale, localHashtable);
    }
    return localHashtable;
  }

  private Hashtable getTimePatterns(Locale paramLocale)
  {
    Hashtable localHashtable = null;
    if (this.m_timePattern != null)
      localHashtable = (Hashtable)this.m_timePattern.get(paramLocale);
    if (localHashtable == null)
    {
      int[] arrayOfInt = { 1, 0, 2, 3 };
      int i = arrayOfInt.length;
      SimpleDateFormat localSimpleDateFormat = null;
      String str = null;
      localHashtable = new Hashtable(i + 2);
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        localSimpleDateFormat = (SimpleDateFormat)DateFormat.getTimeInstance(arrayOfInt[k], paramLocale);
        str = localSimpleDateFormat.toPattern();
        if (str != null)
          localHashtable.put(new Integer(j++), str);
      }
      localHashtable.put(new Integer(j++), "HH:mm:ss");
      localHashtable.put(new Integer(j++), "HH:mm");
      if (this.m_timePattern == null)
        this.m_timePattern = new Hashtable();
      this.m_timePattern.put(paramLocale, localHashtable);
    }
    return localHashtable;
  }

  private void initCustomFormat()
  {
    this.m_customPattern = new Hashtable(75);
    this.m_customPattern.put(new Integer(1), "MM/dd/yyyy");
    this.m_customPattern.put(new Integer(2), "MM/dd/yy");
    this.m_customPattern.put(new Integer(3), "MM/d/yyyy");
    this.m_customPattern.put(new Integer(4), "MM/d/yy");
    this.m_customPattern.put(new Integer(5), "M/dd/yyyy");
    this.m_customPattern.put(new Integer(6), "M/dd/yy");
    this.m_customPattern.put(new Integer(7), "M/d/yyyy");
    this.m_customPattern.put(new Integer(8), "M/d/yy");
    this.m_customPattern.put(new Integer(9), "dd/MM/yyyy");
    this.m_customPattern.put(new Integer(10), "dd/MM/yy");
    this.m_customPattern.put(new Integer(11), "dd/M/yyyy");
    this.m_customPattern.put(new Integer(12), "dd/M/yy");
    this.m_customPattern.put(new Integer(13), "d/MM/yyyy");
    this.m_customPattern.put(new Integer(14), "d/MM/yy");
    this.m_customPattern.put(new Integer(15), "d/M/yyyy");
    this.m_customPattern.put(new Integer(16), "d/M/yy");
    this.m_customPattern.put(new Integer(17), "yyyy/MM/dd");
    this.m_customPattern.put(new Integer(18), "yyyy/MM/d");
    this.m_customPattern.put(new Integer(19), "yyyy/M/dd");
    this.m_customPattern.put(new Integer(20), "yyyy/M/d");
    this.m_customPattern.put(new Integer(21), "yy/MM/dd");
    this.m_customPattern.put(new Integer(22), "yy/MM/d");
    this.m_customPattern.put(new Integer(23), "yy/M/dd");
    this.m_customPattern.put(new Integer(24), "yy/M/d");
    this.m_customPattern.put(new Integer(25), "dd MMMM, yyyy");
    this.m_customPattern.put(new Integer(26), "dd MMMM, yy");
    this.m_customPattern.put(new Integer(27), "dd MMM, yyyy");
    this.m_customPattern.put(new Integer(28), "dd MMM, yy");
    this.m_customPattern.put(new Integer(29), "d MMMM, yyyy");
    this.m_customPattern.put(new Integer(30), "d MMM, yyyy");
    this.m_customPattern.put(new Integer(31), "d MMM, yy");
    this.m_customPattern.put(new Integer(32), "MMMM dd, yyyy");
    this.m_customPattern.put(new Integer(33), "MMMM dd, yy");
    this.m_customPattern.put(new Integer(34), "MMMM d, yyyy");
    this.m_customPattern.put(new Integer(35), "MMMM d, yy");
    this.m_customPattern.put(new Integer(36), "MMM dd, yyyy");
    this.m_customPattern.put(new Integer(37), "MMM dd, yy");
    this.m_customPattern.put(new Integer(38), "MMM d, yyyy");
    this.m_customPattern.put(new Integer(39), "MMM d, yy");
    this.m_customPattern.put(new Integer(40), "EEEE, MMMM dd, yyyy");
    this.m_customPattern.put(new Integer(41), "EEEE, MMMM dd, yy");
    this.m_customPattern.put(new Integer(42), "EEEE, MMMM d, yyyy");
    this.m_customPattern.put(new Integer(43), "EEEE, MMMM d, yy");
    this.m_customPattern.put(new Integer(44), "EEEE, dd MMMM, yyyy");
    this.m_customPattern.put(new Integer(45), "EEEE, dd MMMM, yy");
    this.m_customPattern.put(new Integer(46), "EEEE, d MMMM, yyyy");
    this.m_customPattern.put(new Integer(47), "EEEE, d MMMM, yy");
    this.m_customPattern.put(new Integer(48), "EEEE, MMM dd, yyyy");
    this.m_customPattern.put(new Integer(49), "EEEE, MMM dd, yy");
    this.m_customPattern.put(new Integer(50), "EEEE, MMM d, yyyy");
    this.m_customPattern.put(new Integer(51), "EEEE, MMM d, yy");
    this.m_customPattern.put(new Integer(52), "EEEE, dd MMM, yyyy");
    this.m_customPattern.put(new Integer(53), "EEEE, dd MMM, yy");
    this.m_customPattern.put(new Integer(54), "EEEE, d MMM, yyyy");
    this.m_customPattern.put(new Integer(55), "EEEE, d MMM, yy");
    this.m_customPattern.put(new Integer(56), "EEE, MMMM dd, yyyy");
    this.m_customPattern.put(new Integer(57), "EEE, MMMM dd, yy");
    this.m_customPattern.put(new Integer(58), "EEE, MMMM d, yyyy");
    this.m_customPattern.put(new Integer(59), "EEE, MMMM d, yy");
    this.m_customPattern.put(new Integer(60), "EEE, dd MMMM, yyyy");
    this.m_customPattern.put(new Integer(61), "EEE, dd MMMM, yy");
    this.m_customPattern.put(new Integer(62), "EEE, d MMMM, yyyy");
    this.m_customPattern.put(new Integer(63), "EEE, d MMMM, yy");
    this.m_customPattern.put(new Integer(64), "EEE, MMM dd, yyyy");
    this.m_customPattern.put(new Integer(65), "EEE, MMM dd, yyyy");
    this.m_customPattern.put(new Integer(66), "EEE, MMM dd, yy");
    this.m_customPattern.put(new Integer(67), "EEE, MMM d, yyyy");
    this.m_customPattern.put(new Integer(68), "EEE, MMM d, yy");
    this.m_customPattern.put(new Integer(69), "EEE, dd MMM, yyyy");
    this.m_customPattern.put(new Integer(70), "EEE, dd MMM, yy");
    this.m_customPattern.put(new Integer(71), "EEE, d MMM, yyyy");
    this.m_customPattern.put(new Integer(72), "EEE, d MMM, yy");
  }

  public String parseDate(String paramString1, int paramInt, String paramString2, String paramString3)
  {
    Locale localLocale = convertToLocaleObject(paramString2);
    Hashtable localHashtable1 = getDatePatterns(localLocale);
    Hashtable localHashtable2 = getTimePatterns(localLocale);
    switch (paramInt)
    {
    case 1:
      localMatchingPattern = parseTimePattern(localHashtable2, paramString1, paramInt, localLocale, paramString3);
      return localMatchingPattern.foundMatch() ? addCurrentDateToTime(localMatchingPattern.getValue(), paramString3) : "error";
    case 3:
      localMatchingPattern = parseTimePattern(localHashtable2, paramString1, paramInt, localLocale, paramString3);
      return localMatchingPattern.foundMatch() ? localMatchingPattern.getValue() : "error";
    }
    MatchingPattern localMatchingPattern = parseDatePattern(localHashtable1, localHashtable2, 0, localHashtable1.size(), paramString1, paramInt, localLocale, paramString3);
    if (localMatchingPattern.foundMatch())
      return localMatchingPattern.getValue();
    localMatchingPattern = parseCustomFormat(paramString1, paramInt, localLocale, paramString3, localHashtable2);
    if (localMatchingPattern.foundMatch())
      return localMatchingPattern.getValue();
    if (paramInt != 4)
      localMatchingPattern = parseTimePattern(localHashtable2, paramString1, paramInt, localLocale, paramString3);
    return localMatchingPattern.foundMatch() ? addCurrentDateToTime(localMatchingPattern.getValue(), paramString3) : "error";
  }

  private MatchingPattern parseTimePattern(Hashtable paramHashtable, String paramString1, int paramInt, Locale paramLocale, String paramString2)
  {
    MatchingPattern localMatchingPattern = null;
    String str = null;
    int i = paramHashtable.size();
    for (int j = 0; j < i; j++)
    {
      str = (String)paramHashtable.get(new Integer(j));
      if (str != null)
      {
        localMatchingPattern = parse(str, paramString1, paramInt, paramLocale, paramString2, true);
        if (localMatchingPattern.foundMatch())
          return localMatchingPattern;
      }
    }
    return new MatchingPattern(false, null, null);
  }

  private MatchingPattern parseDatePattern(Hashtable paramHashtable1, Hashtable paramHashtable2, int paramInt1, int paramInt2, String paramString1, int paramInt3, Locale paramLocale, String paramString2)
  {
    int i = paramHashtable1.size();
    String str = null;
    MatchingPattern localMatchingPattern1 = null;
    for (int j = paramInt1; j < paramInt2; j++)
    {
      localMatchingPattern1 = null;
      str = (String)paramHashtable1.get(new Integer(j));
      if (str != null)
      {
        if (paramInt3 == 4)
          localMatchingPattern1 = parseDateOnly(str, paramString1, paramLocale, paramString2);
        else
          localMatchingPattern1 = parse(str, paramString1, paramInt3, paramLocale, paramString2, false);
        if ((localMatchingPattern1 != null) && (localMatchingPattern1.foundMatch()))
        {
          if ((paramInt3 == 2) || (paramInt3 == 4))
            return localMatchingPattern1;
          MatchingPattern localMatchingPattern2 = parseDateTimePattern(paramHashtable2, localMatchingPattern1.getMatchingPattern(), paramString1, paramInt3, paramLocale, paramString2);
          if (localMatchingPattern2.foundMatch())
            return localMatchingPattern2;
          return localMatchingPattern1;
        }
      }
    }
    return new MatchingPattern(false, null, null);
  }

  private MatchingPattern parseDateTimePattern(Hashtable paramHashtable, String paramString1, String paramString2, int paramInt, Locale paramLocale, String paramString3)
  {
    MatchingPattern localMatchingPattern = null;
    StringBuilder localStringBuilder = null;
    String str = null;
    int i = paramHashtable.size();
    for (int j = 0; j < i; j++)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString1);
      str = (String)paramHashtable.get(new Integer(j));
      if (str != null)
        localStringBuilder.append(" ").append(str);
      str = localStringBuilder.toString();
      localMatchingPattern = parse(str, paramString2, paramInt, paramLocale, paramString3, true);
      if (localMatchingPattern.foundMatch())
        return localMatchingPattern;
    }
    return new MatchingPattern(false, null, null);
  }

  private MatchingPattern parseCustomFormat(String paramString1, int paramInt, Locale paramLocale, String paramString2, Hashtable paramHashtable)
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt = getParseRangeIndices(paramString1, paramInt, paramLocale, paramString2);
    if (this.m_customPattern == null)
      initCustomFormat();
    return parseDatePattern(this.m_customPattern, paramHashtable, arrayOfInt[0], arrayOfInt[1] + 1, paramString1, paramInt, paramLocale, paramString2);
  }

  private int[] getParseRangeIndices(String paramString1, int paramInt, Locale paramLocale, String paramString2)
  {
    if (paramString1.indexOf("/") != -1)
    {
      int[] arrayOfInt1 = { 0, 23 };
      return arrayOfInt1;
    }
    if (paramString1.indexOf(",") != -1)
    {
      int i = paramString1.indexOf(" ");
      if (i == -1)
      {
        localObject = new int[] { 0, 0 };
        return localObject;
      }
      String str = paramString1.substring(0, i);
      Object localObject = parse("MMM", str, paramInt, paramLocale, paramString2, true);
      if (((MatchingPattern)localObject).foundMatch())
      {
        arrayOfInt3 = new int[] { 36, 39 };
        return arrayOfInt3;
      }
      localObject = parse("EEE", str, paramInt, paramLocale, paramString2, true);
      if (((MatchingPattern)localObject).foundMatch())
      {
        arrayOfInt3 = new int[] { 56, this.m_customPattern.size() - 1 };
        return arrayOfInt3;
      }
      int[] arrayOfInt3 = { 24, 31 };
      return arrayOfInt3;
    }
    int[] arrayOfInt2 = { 0, 0 };
    return arrayOfInt2;
  }

  private MatchingPattern parse(String paramString1, String paramString2, int paramInt, Locale paramLocale, String paramString3, boolean paramBoolean)
  {
    Date localDate = null;
    int i = 0;
    try
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString1, paramLocale);
      localSimpleDateFormat.setTimeZone(getTimeZone(paramString3));
      localSimpleDateFormat.setLenient(false);
      ParsePosition localParsePosition = new ParsePosition(0);
      localDate = localSimpleDateFormat.parse(paramString2, localParsePosition);
      i = localParsePosition.getIndex();
    }
    catch (Exception localException)
    {
    }
    if ((localDate != null) && ((i == paramString2.length()) || (!paramBoolean)))
    {
      String str = getDateInSeconds(localDate, paramInt, paramString3);
      if (str != null)
        return new MatchingPattern(true, paramString1, str);
    }
    return new MatchingPattern(false, null, null);
  }

  private MatchingPattern parseDateOnly(String paramString1, String paramString2, Locale paramLocale, String paramString3)
  {
    int i = -1;
    try
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString1, paramLocale);
      localSimpleDateFormat.setTimeZone(getTimeZone(paramString3));
      localSimpleDateFormat.setLenient(false);
      ParsePosition localParsePosition = new ParsePosition(0);
      Date localDate = localSimpleDateFormat.parse(paramString2, localParsePosition);
      if (localDate != null)
      {
        CalendarInfo localCalendarInfo = new CalendarInfo();
        TimeZone localTimeZone = localSimpleDateFormat.getTimeZone();
        i = localCalendarInfo.computeTotalNumDays(paramString2, paramString1, paramLocale, localTimeZone);
        if (i > 0)
          return new MatchingPattern(true, paramString1, String.valueOf(i));
      }
    }
    catch (Exception localException)
    {
    }
    return new MatchingPattern(false, null, null);
  }

  private String getDateInSeconds(Date paramDate, int paramInt, String paramString)
  {
    long l = paramDate.getTime();
    double d = l / 1000.0D;
    l = ()Math.floor(d);
    return String.valueOf(l);
  }

  private String addCurrentDateToTime(String paramString1, String paramString2)
  {
    Calendar localCalendar = Calendar.getInstance();
    TimeZone localTimeZone = getTimeZone(paramString2);
    long l1 = Integer.parseInt(paramString1);
    l1 *= 1000L;
    long l2 = localCalendar.getTime().getTime() / 1000L - localCalendar.get(11) * 3600 - localCalendar.get(12) * 60 - localCalendar.get(13) + (l1 + localTimeZone.getRawOffset()) / 1000L;
    return String.valueOf(l2);
  }

  private String toStyleString(int paramInt)
  {
    String str = "unknown";
    switch (paramInt)
    {
    case 3:
      str = "short";
      break;
    case 2:
      str = "medium";
      break;
    case 1:
      str = "long";
      break;
    case 0:
      str = "full";
      break;
    case 4:
      str = "custom";
    }
    return str;
  }

  private String toPrefStyleString(String paramString)
  {
    String str = "unknown";
    if (paramString.equals("0"))
      str = "custom";
    else if (paramString.equals("1"))
      str = "short";
    else if (paramString.equals("2"))
      str = "long";
    return str;
  }

  private String toFormatTypeString(int paramInt)
  {
    String str = "unknown";
    switch (paramInt)
    {
    case 0:
      str = "datetime both";
      break;
    case 1:
      str = "datetime time only";
      break;
    case 2:
      str = "datetime date only";
      break;
    case 3:
      str = "TIME OF DAY";
      break;
    case 4:
      str = "DATE FIELD";
    }
    return str;
  }

  private void dumpPatterns(Hashtable paramHashtable)
  {
    System.out.println("patterns:");
    String str = null;
    int i = 0;
    Enumeration localEnumeration = paramHashtable.elements();
    while (localEnumeration.hasMoreElements())
    {
      str = (String)localEnumeration.nextElement();
      System.out.println("style[" + i + "] = " + str);
      i++;
    }
  }

  static class MatchingPattern
  {
    private boolean match;
    private String pattern;
    private String value;

    MatchingPattern(boolean paramBoolean, String paramString1, String paramString2)
    {
      this.match = paramBoolean;
      this.pattern = paramString1;
      this.value = paramString2;
    }

    public boolean foundMatch()
    {
      return this.match;
    }

    public String getMatchingPattern()
    {
      return this.pattern;
    }

    public String getValue()
    {
      return this.value;
    }

    public String setValue(String paramString)
    {
      return this.value = paramString;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.legacyshared.ARDataFormat
 * JD-Core Version:    0.6.1
 */