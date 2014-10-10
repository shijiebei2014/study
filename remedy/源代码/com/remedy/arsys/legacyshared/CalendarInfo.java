package com.remedy.arsys.legacyshared;

import com.remedy.arsys.share.MessageTranslation;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

public class CalendarInfo
{
  private static final int GREGORGIAN_CHANGEOVER = 2299161;
  private static final String m_patternChars = "GyMdkHmsSEDFwWahKz";
  private static final String GMT = "GMT";
  private static final int defaultYearCutOff = 29;
  private static final int millisPerHour = 3600000;
  private static final int millisPerMinute = 60000;
  private DateFormatSymbols m_formatData = null;
  private static Hashtable m_era_locale_table = null;
  private int m_num_days = 0;
  private int m_year = 0;
  private int m_month = 0;
  private int m_day = 0;
  private int m_era = 1;
  private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = { 0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15 };

  public CalendarInfo()
  {
  }

  public CalendarInfo(int paramInt, Locale paramLocale)
  {
    this.m_num_days = paramInt;
    if (this.m_formatData == null)
      this.m_formatData = new DateFormatSymbols(paramLocale);
    computeDate();
  }

  public int getNumberOfDays()
  {
    return this.m_num_days;
  }

  public String getNumberOfDaysString()
  {
    return Integer.toString(this.m_num_days);
  }

  public String getNumberOfDaysString(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    setFullDate(paramString1, paramString2, paramString3, paramString4);
    return Integer.toString(this.m_num_days);
  }

  public int getEra()
  {
    return this.m_era;
  }

  public String getEraString()
  {
    return Integer.toString(this.m_era);
  }

  private static String getLanguage(Locale paramLocale)
  {
    String str = paramLocale.getLanguage();
    if (str != null)
    {
      str = str.trim().toLowerCase();
      int i = str.indexOf("_");
      if (i >= 0)
        str = str.substring(0, i);
    }
    return str;
  }

  public static Vector getPossibleEraValues(Locale paramLocale)
  {
    String str = getLanguage(paramLocale);
    if (m_era_locale_table != null)
    {
      if (str != null)
        return (Vector)m_era_locale_table.get(str);
    }
    else
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(MessageTranslation.getLocalizedText(str, "BC"));
      localStringBuilder.append("|");
      localStringBuilder.append(MessageTranslation.getLocalizedText(str, "B.C."));
      localStringBuilder.append("|");
      localStringBuilder.append(MessageTranslation.getLocalizedText(str, "B.C"));
      localStringBuilder.append("|");
      localStringBuilder.append(MessageTranslation.getLocalizedText(str, "bc"));
      localStringBuilder.append("|");
      localStringBuilder.append(MessageTranslation.getLocalizedText(str, "b.c."));
      localStringBuilder.append("|");
      localStringBuilder.append(MessageTranslation.getLocalizedText(str, "b.c"));
      setPossibleEraValues(localStringBuilder.toString(), paramLocale);
      if (m_era_locale_table != null)
        return (Vector)m_era_locale_table.get(str);
    }
    return null;
  }

  public int getYear()
  {
    return this.m_year;
  }

  public String getYearString()
  {
    return Integer.toString(this.m_year);
  }

  public int getMonth()
  {
    return this.m_month;
  }

  public String getMonthString()
  {
    return Integer.toString(this.m_month);
  }

  public int getDay()
  {
    return this.m_day;
  }

  public String getDayString()
  {
    return Integer.toString(this.m_day);
  }

  public void setDate(String paramString)
  {
    if (paramString != null)
      try
      {
        this.m_num_days = Integer.parseInt(paramString);
        computeDate();
      }
      catch (NumberFormatException localNumberFormatException)
      {
        this.m_num_days = 0;
      }
  }

  public void setFullDate(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.m_year = Integer.parseInt(paramString1);
    this.m_month = Integer.parseInt(paramString2);
    this.m_day = Integer.parseInt(paramString3);
    this.m_era = Integer.parseInt(paramString4);
    if (this.m_era == 0)
      this.m_year = ((this.m_year - 1) * -1);
    computeTotalNumDays();
  }

  public static void setPossibleEraValues(String paramString, Locale paramLocale)
  {
    if (m_era_locale_table == null)
      m_era_locale_table = new Hashtable();
    String str1 = getLanguage(paramLocale);
    Vector localVector1 = (Vector)m_era_locale_table.get(str1);
    if (((localVector1 == null) || (localVector1.size() <= 0)) && (paramString != null) && (paramString.length() > 0))
    {
      Vector localVector2 = new Vector();
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "|");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str2 = localStringTokenizer.nextToken();
        if (str2 != null)
          localVector2.addElement(str2.trim());
      }
      m_era_locale_table.put(str1, localVector2);
    }
  }

  public int ConvertToFourDigitYear(int paramInt)
  {
    if (Integer.toString(paramInt).length() > 2)
      return paramInt;
    int i = paramInt;
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    int j = localGregorianCalendar.get(1);
    int k = j + 29;
    if ((paramInt >= 0) && (paramInt <= 99))
      if (paramInt <= k % 100)
        i = paramInt + k / 100 * 100;
      else
        i = paramInt + (k - 99) / 100 * 100;
    return i;
  }

  private synchronized void computeDate()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    if (this.m_num_days < 2299161)
    {
      k = this.m_num_days + 32082;
    }
    else
    {
      i = this.m_num_days + 32044;
      j = (4 * i + 3) / 146097;
      k = i - j * 146097 / 4;
    }
    m = (4 * k + 3) / 1461;
    n = k - 1461 * m / 4;
    i1 = (5 * n + 2) / 153;
    this.m_year = (j * 100 + m - 4800 + i1 / 10);
    this.m_month = (i1 + 3 - 12 * (i1 / 10));
    this.m_day = (n - (153 * i1 + 2) / 5 + 1);
    this.m_era = 1;
    if (this.m_year < 0)
      this.m_era = 0;
  }

  private void computeTotalNumDays()
  {
    this.m_num_days = 0;
    int i = (14 - this.m_month) / 12;
    int j = this.m_year + 4800 - i;
    int k = this.m_month + 12 * i - 3;
    if ((this.m_year < 1582) || ((this.m_year == 1582) && (this.m_month < 10)) || ((this.m_year == 1582) && (this.m_month == 10) && (this.m_day <= 4)))
      this.m_num_days = (this.m_day + (153 * k + 2) / 5 + j * 365 + j / 4 - 32083);
    else
      this.m_num_days = (this.m_day + (153 * k + 2) / 5 + j * 365 + j / 4 - j / 100 + j / 400 - 32045);
  }

  public int computeTotalNumDays(String paramString1, String paramString2, Locale paramLocale, com.ibm.icu.util.TimeZone paramTimeZone)
  {
    if ((paramString1 == null) || (paramString2 == null))
      return -1;
    paramString1 = paramString1.trim();
    int i = 0;
    Object localObject = null;
    Vector localVector = null;
    if (m_era_locale_table != null)
      localVector = (Vector)m_era_locale_table.get(getLanguage(paramLocale));
    int j;
    int k;
    int n;
    int i1;
    String str2;
    if ((localVector != null) && (localVector.size() > 0))
    {
      j = localVector.size();
      for (k = 0; k < j; k++)
      {
        String str1 = (String)localVector.elementAt(k);
        if (localObject == null)
          localObject = str1;
        n = paramString1.indexOf(str1);
        if (n >= 0)
        {
          if (str1.equals(localObject))
          {
            i = 1;
            break;
          }
          i1 = n + str1.length();
          str2 = "";
          if (i1 < paramString1.length())
            str2 = paramString1.substring(n + str1.length());
          paramString1 = paramString1.substring(0, n);
          paramString1 = paramString1.trim();
          StringBuilder localStringBuilder1 = new StringBuilder(paramString1);
          localStringBuilder1.append(" ").append(localObject);
          if ((str2 != null) && (str2.length() > 0))
            localStringBuilder1.append(" ").append(str2.trim());
          paramString1 = localStringBuilder1.toString();
          i = 1;
          break;
        }
      }
    }
    if ((i != 0) && (paramString2.indexOf("G") < 0))
    {
      j = paramString2.length();
      k = -1;
      int m = 0;
      for (n = 0; n < j; n++)
      {
        i1 = paramString2.charAt(n);
        if ((k < 0) && (i1 == 121))
        {
          k = n;
        }
        else if ((k >= 0) && ((i1 != m) || (n == j - 1)))
        {
          str2 = null;
          int i2 = -1;
          if (n != j - 1)
            str2 = paramString2.substring(n).trim();
          paramString2 = paramString2.substring(0, n + 1);
          paramString2 = paramString2.trim();
          StringBuilder localStringBuilder2 = new StringBuilder(paramString2);
          localStringBuilder2.append(" G");
          if ((str2 != null) && (str2.length() > 0))
            localStringBuilder2.append(" ").append(str2.trim());
          paramString2 = localStringBuilder2.toString();
          break;
        }
        m = i1;
      }
    }
    paramString2 = paramString2.trim();
    GregorianCalendar localGregorianCalendar = parsePattern(paramString1, paramString2, 0, paramLocale, paramTimeZone);
    if (localGregorianCalendar != null)
    {
      this.m_year = localGregorianCalendar.get(1);
      this.m_month = localGregorianCalendar.get(2);
      this.m_month += 1;
      if (this.m_month < 1)
      {
        this.m_year -= 1;
        this.m_month = 12;
      }
      this.m_day = localGregorianCalendar.get(5);
      this.m_era = localGregorianCalendar.get(0);
      if (this.m_era == 0)
        this.m_year = ((this.m_year - 1) * -1);
      if ((this.m_month > 0) && (this.m_day > 0))
        computeTotalNumDays();
    }
    return this.m_num_days;
  }

  private static boolean isLeapYear(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 >= 1582) && (paramInt2 >= 10) && (paramInt3 >= 15))
      return ((paramInt1 % 4 == 0) && (paramInt1 % 100 != 0)) || (paramInt1 % 400 == 0);
    return (paramInt1 % 4 == 0) && (paramInt1 % 100 != 0);
  }

  private int matchString(String paramString, int paramInt1, int paramInt2, String[] paramArrayOfString, GregorianCalendar paramGregorianCalendar)
  {
    int i = 0;
    int j = paramArrayOfString.length;
    if (paramInt2 == 7)
      i = 1;
    int k = 0;
    int m = -1;
    while (i < j)
    {
      int n = paramArrayOfString[i].length();
      if ((n > k) && (paramString.regionMatches(true, paramInt1, paramArrayOfString[i], 0, n)))
      {
        m = i;
        k = n;
      }
      i++;
    }
    if (m >= 0)
    {
      paramGregorianCalendar.set(paramInt2, m);
      return paramInt1 + k;
    }
    return -paramInt1;
  }

  private int matchZoneString(String paramString, int paramInt1, int paramInt2)
  {
    String[][] arrayOfString = this.m_formatData.getZoneStrings();
    for (int i = 1; (i <= 4) && (!paramString.regionMatches(true, paramInt1, arrayOfString[paramInt2][i], 0, arrayOfString[paramInt2][i].length())); i++);
    return i > 4 ? -1 : i;
  }

  final int getZoneIndex(String paramString)
  {
    String[][] arrayOfString = this.m_formatData.getZoneStrings();
    if (arrayOfString != null)
      for (int i = 0; i < arrayOfString.length; i++)
        if (paramString.equalsIgnoreCase(arrayOfString[i][0]))
          return i;
    return -1;
  }

  private int subParseZoneString(String paramString, int paramInt, GregorianCalendar paramGregorianCalendar, com.ibm.icu.util.TimeZone paramTimeZone)
  {
    int i = getZoneIndex(paramTimeZone.getID());
    String[][] arrayOfString = this.m_formatData.getZoneStrings();
    com.ibm.icu.util.TimeZone localTimeZone = null;
    int j = 0;
    int k = 0;
    if ((i != -1) && ((j = matchZoneString(paramString, paramInt, i)) > 0))
    {
      localTimeZone = com.ibm.icu.util.TimeZone.getTimeZone(arrayOfString[i][0]);
      k = i;
    }
    if (localTimeZone == null)
    {
      i = getZoneIndex(com.ibm.icu.util.TimeZone.getDefault().getID());
      if ((i != -1) && ((j = matchZoneString(paramString, paramInt, i)) > 0))
      {
        localTimeZone = com.ibm.icu.util.TimeZone.getTimeZone(arrayOfString[i][0]);
        k = i;
      }
    }
    if (localTimeZone == null)
      for (k = 0; k < arrayOfString.length; k++)
        if ((j = matchZoneString(paramString, paramInt, k)) > 0)
        {
          localTimeZone = com.ibm.icu.util.TimeZone.getTimeZone(arrayOfString[k][0]);
          break;
        }
    if (localTimeZone != null)
    {
      paramGregorianCalendar.set(15, localTimeZone.getRawOffset());
      paramGregorianCalendar.set(16, j >= 3 ? 3600000 : 0);
      return paramInt + arrayOfString[k][j].length();
    }
    return 0;
  }

  private int subParse(String paramString, int paramInt1, char paramChar, int paramInt2, boolean paramBoolean, GregorianCalendar paramGregorianCalendar, Locale paramLocale, com.ibm.icu.util.TimeZone paramTimeZone)
  {
    NumberFormat localNumberFormat = NumberFormat.getInstance(paramLocale);
    if ((localNumberFormat instanceof DecimalFormat))
      ((DecimalFormat)localNumberFormat).setDecimalSeparatorAlwaysShown(false);
    localNumberFormat.setGroupingUsed(false);
    localNumberFormat.setParseIntegerOnly(true);
    localNumberFormat.setMinimumFractionDigits(0);
    if (this.m_formatData == null)
      this.m_formatData = new DateFormatSymbols(paramLocale);
    Number localNumber1 = null;
    int j = 0;
    int k = paramInt1;
    int m = -1;
    if ((m = "GyMdkHmsSEDFwWahKz".indexOf(paramChar)) == -1)
      return -paramInt1;
    ParsePosition localParsePosition = new ParsePosition(0);
    localParsePosition.setIndex(k);
    int n = PATTERN_INDEX_TO_CALENDAR_FIELD[m];
    while (true)
    {
      k = localParsePosition.getIndex();
      if (k >= paramString.length())
        return -paramInt1;
      int i1 = paramString.charAt(k);
      if ((i1 != 32) && (i1 != 9))
        break;
      localParsePosition.setIndex(++k);
    }
    if ((m == 4) || (m == 15) || (m == 1) || ((m == 2) && (paramInt2 <= 2)))
    {
      if (paramBoolean)
      {
        if (paramInt1 + paramInt2 > paramString.length())
          return -paramInt1;
        localNumber1 = localNumberFormat.parse(paramString.substring(0, paramInt1 + paramInt2), localParsePosition);
      }
      else
      {
        localNumber1 = localNumberFormat.parse(paramString, localParsePosition);
      }
      if (localNumber1 == null)
        return -paramInt1;
      j = localNumber1.intValue();
    }
    int i2;
    String[] arrayOfString1;
    String[] arrayOfString2;
    switch (m)
    {
    case 0:
      Vector localVector = (Vector)m_era_locale_table.get(getLanguage(paramLocale));
      if ((localVector != null) && (localVector.size() > 0))
      {
        i2 = localVector.size();
        arrayOfString1 = new String[i2];
        for (int i3 = 0; i3 < i2; i3++)
          if (localVector.elementAt(i3) != null)
            arrayOfString1[i3] = ((String)localVector.elementAt(i3));
        return matchString(paramString, paramInt1, 0, arrayOfString1, paramGregorianCalendar);
      }
      return -1;
    case 1:
      k = localParsePosition.getIndex();
      if (((paramInt2 <= 2) || ((j >= 0) && (j <= 99))) && (k - paramInt1 < 3))
        j = ConvertToFourDigitYear(j);
      paramGregorianCalendar.set(1, j);
      return k;
    case 2:
      if (paramInt2 <= 2)
      {
        paramGregorianCalendar.set(2, j - 1);
        return localParsePosition.getIndex();
      }
      i2 = 0;
      arrayOfString1 = this.m_formatData.getMonths();
      arrayOfString2 = this.m_formatData.getShortMonths();
      if ((i2 = matchString(paramString, paramInt1, 2, arrayOfString1, paramGregorianCalendar)) > 0)
        return i2;
      return matchString(paramString, paramInt1, 2, arrayOfString2, paramGregorianCalendar);
    case 4:
      if (j == paramGregorianCalendar.getMaximum(11) + 1)
        j = 0;
      paramGregorianCalendar.set(11, j);
      return localParsePosition.getIndex();
    case 9:
      i2 = 0;
      arrayOfString1 = this.m_formatData.getWeekdays();
      arrayOfString2 = this.m_formatData.getShortWeekdays();
      if ((i2 = matchString(paramString, paramInt1, 7, arrayOfString1, paramGregorianCalendar)) > 0)
        return i2;
      return matchString(paramString, paramInt1, 7, arrayOfString2, paramGregorianCalendar);
    case 14:
      String[] arrayOfString3 = this.m_formatData.getAmPmStrings();
      return matchString(paramString, paramInt1, 9, arrayOfString3, paramGregorianCalendar);
    case 15:
      if (j == paramGregorianCalendar.getLeastMaximum(10) + 1)
        j = 0;
      paramGregorianCalendar.set(10, j);
      k = localParsePosition.getIndex();
      return k;
    case 17:
      int i4 = 0;
      Object localObject;
      int i5;
      if ((paramString.length() - paramInt1 >= "GMT".length()) && (paramString.regionMatches(true, paramInt1, "GMT", 0, "GMT".length())))
      {
        paramGregorianCalendar.set(16, 0);
        k = paramInt1 + "GMT".length();
        localParsePosition.setIndex(k);
        try
        {
          if (paramString.charAt(k) == '+')
            i4 = 1;
          else if (paramString.charAt(k) == '-')
            i4 = -1;
        }
        catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
        {
        }
        if (i4 == 0)
        {
          paramGregorianCalendar.set(15, 0);
          return k;
        }
        localParsePosition.setIndex(++k);
        localObject = localNumberFormat.parse(paramString, localParsePosition);
        if (localObject == null)
          return -paramInt1;
        k = localParsePosition.getIndex();
        if (paramString.charAt(k) == ':')
        {
          i5 = ((Number)localObject).intValue() * 60;
          localParsePosition.setIndex(++k);
          localObject = localNumberFormat.parse(paramString, localParsePosition);
          if (localObject == null)
            return -paramInt1;
          i5 += ((Number)localObject).intValue();
        }
        else
        {
          i5 = ((Number)localObject).intValue();
          if (i5 < 24)
            i5 *= 60;
          else
            i5 = i5 % 100 + i5 / 100 * 60;
        }
      }
      else
      {
        int i = subParseZoneString(paramString, paramInt1, paramGregorianCalendar, paramTimeZone);
        if (i != 0)
          return i;
        localObject = new DecimalFormat("+####;-####");
        ((DecimalFormat)localObject).setParseIntegerOnly(true);
        Number localNumber2 = ((DecimalFormat)localObject).parse(paramString, localParsePosition);
        if (localNumber2 == null)
          return -paramInt1;
        i5 = localNumber2.intValue();
        i4 = 1;
        if (i5 < 0)
        {
          i4 = -1;
          i5 = -i5;
        }
        if (i5 < 24)
          i5 *= 60;
        else
          i5 = i5 % 100 + i5 / 100 * 60;
      }
      if (i4 != 0)
      {
        i5 *= 60000 * i4;
        if (paramGregorianCalendar.getTimeZone().useDaylightTime())
        {
          paramGregorianCalendar.set(16, 3600000);
          i5 -= 3600000;
        }
        paramGregorianCalendar.set(15, i5);
        k = localParsePosition.getIndex();
        return k;
      }
      return -paramInt1;
    case 3:
    case 5:
    case 6:
    case 7:
    case 8:
    case 10:
    case 11:
    case 12:
    case 13:
    case 16:
    }
    if (paramBoolean)
    {
      if (paramInt1 + paramInt2 > paramString.length())
        return -paramInt1;
      localNumber1 = localNumberFormat.parse(paramString.substring(0, paramInt1 + paramInt2), localParsePosition);
    }
    else
    {
      localNumber1 = localNumberFormat.parse(paramString, localParsePosition);
    }
    if (localNumber1 != null)
    {
      paramGregorianCalendar.set(n, localNumber1.intValue());
      k = localParsePosition.getIndex();
      return k;
    }
    return -paramInt1;
  }

  private GregorianCalendar parsePattern(String paramString1, String paramString2, int paramInt, Locale paramLocale, com.ibm.icu.util.TimeZone paramTimeZone)
  {
    int i = 0;
    int j = i;
    int k = 0;
    int m = 0;
    int n = 0;
    char c1 = '\000';
    int i1 = 0;
    int i2 = 1;
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    ParsePosition localParsePosition = new ParsePosition(paramInt);
    for (int i3 = 0; i3 < paramString2.length(); i3++)
    {
      char c2 = paramString2.charAt(i3);
      if (n != 0)
      {
        if (c2 == '\'')
        {
          n = 0;
          if (i1 == 0)
          {
            if ((i >= paramString1.length()) || (c2 != paramString1.charAt(i)))
            {
              localParsePosition.setIndex(j);
              return null;
            }
            i++;
          }
          i1 = 0;
          i2 = 0;
        }
        else
        {
          if ((i >= paramString1.length()) || (c2 != paramString1.charAt(i)))
          {
            localParsePosition.setIndex(j);
            return null;
          }
          i1++;
          i++;
        }
      }
      else
      {
        int i4;
        if (c2 == '\'')
        {
          n = 1;
          if (i1 > 0)
          {
            i4 = i;
            i = subParse(paramString1, i, c1, i1, false, localGregorianCalendar, paramLocale, paramTimeZone);
            if (i < 0)
            {
              localParsePosition.setIndex(j);
              return null;
            }
            i1 = 0;
          }
          if (i2 == 0)
          {
            i4 = i;
            if ((i >= paramString1.length()) || (c2 != paramString1.charAt(i)))
            {
              localParsePosition.setIndex(j);
              return null;
            }
            i++;
            i1 = 1;
          }
        }
        else if (((c2 >= 'a') && (c2 <= 'z')) || ((c2 >= 'A') && (c2 <= 'Z')))
        {
          if ((c2 != c1) && (i1 > 0))
          {
            i4 = i;
            i = subParse(paramString1, i, c1, i1, true, localGregorianCalendar, paramLocale, paramTimeZone);
            if (i < 0)
            {
              localParsePosition.setIndex(j);
              return null;
            }
            c1 = c2;
            i1 = 1;
          }
          else
          {
            if (c2 != c1)
              c1 = c2;
            i1++;
          }
        }
        else if (i1 > 0)
        {
          i4 = i;
          i = subParse(paramString1, i, c1, i1, false, localGregorianCalendar, paramLocale, paramTimeZone);
          if (i < 0)
          {
            localParsePosition.setIndex(j);
            return null;
          }
          if ((i >= paramString1.length()) || (c2 != paramString1.charAt(i)))
          {
            localParsePosition.setIndex(j);
            return null;
          }
          i++;
          i1 = 0;
          c1 = '\000';
        }
        else
        {
          if ((i >= paramString1.length()) || (c2 != paramString1.charAt(i)))
          {
            localParsePosition.setIndex(j);
            return null;
          }
          i++;
        }
        i2++;
      }
    }
    if (i1 > 0)
    {
      i3 = i;
      i = subParse(paramString1, i, c1, i1, false, localGregorianCalendar, paramLocale, paramTimeZone);
      if (i < 0)
      {
        localParsePosition.setIndex(j);
        return null;
      }
    }
    return localGregorianCalendar;
  }

  public static boolean validateDate(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    if ((paramInt1 < -4712) || (paramInt1 > 9999))
      return false;
    if ((paramInt2 < 1) || (paramInt3 < 1))
      return false;
    if ((paramInt2 > 12) || (paramInt3 > 31))
      return false;
    if ((paramInt1 == 1582) && (paramInt2 == 8) && (paramInt3 > 4) && (paramInt3 < 15))
      return false;
    if (paramInt2 == 2)
    {
      if (isLeapYear(paramInt1, paramInt2, paramInt3))
        return paramInt3 <= 29;
      return paramInt3 <= 28;
    }
    return paramInt3 <= arrayOfInt[paramInt2];
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.legacyshared.CalendarInfo
 * JD-Core Version:    0.6.1
 */