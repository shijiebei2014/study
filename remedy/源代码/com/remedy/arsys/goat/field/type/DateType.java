package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.DateInfo;
import com.bmc.arsys.api.Value;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Date;

public class DateType extends GoatType
{
  private static final int GREGORGIAN_CHANGEOVER = 2299161;
  private static final long ADSTARTTIME = -62135686138161L;
  private DateInfo mValue;

  public DateType(Value paramValue, int paramInt)
  {
    this.mValue = ((DateInfo)paramValue.getValue());
    assert (this.mValue != null);
  }

  public DateType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
  {
    this(paramValue, paramInt);
  }

  public DateType(String paramString, int paramInt)
  {
    this.mValue = new DateInfo(Integer.parseInt(paramString));
    assert (this.mValue != null);
  }

  public int getDataType()
  {
    return 13;
  }

  public int getOperandType()
  {
    return 8;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new DateType(").append(this.mValue.getValue()).append(")");
  }

  public String emitAR()
  {
    return "" + this.mValue.getValue();
  }

  public Value toValue()
  {
    return new Value(this.mValue);
  }

  public String toPrimitive()
  {
    return "" + this.mValue.getValue();
  }

  public String forHTML()
  {
    return format(getDate());
  }

  public static String format(int paramInt)
  {
    return format(getDateFromNumDays(paramInt));
  }

  public static String format(Date paramDate)
  {
    SessionData localSessionData = SessionData.get();
    String str = adjustPatternToFourDigitYear(localSessionData.getDateFormatPattern());
    SimpleDateFormat localSimpleDateFormat = localSessionData.getICUDateOnlyShortSimpleDateFormatInstance();
    synchronized (localSimpleDateFormat)
    {
      if (paramDate.getTime() < -62135686138161L)
        localSimpleDateFormat.applyPattern(addEraToPattern(str));
      else
        localSimpleDateFormat.applyPattern(str);
      return localSimpleDateFormat.format(paramDate);
    }
  }

  private static String addEraToPattern(String paramString)
  {
    int j = 0;
    int k = 0;
    for (int i = 0; i < paramString.length(); i++)
    {
      if ((k == 0) && (paramString.charAt(i) == 'G'))
      {
        j = 1;
        break;
      }
      if (paramString.charAt(i) == '\'')
        k = k == 0 ? 1 : 0;
    }
    if (j == 0)
    {
      String str = SessionData.get().getLocale();
      int m = (str.indexOf("ko") == 0) || (str.indexOf("zh") == 0) || (str.indexOf("ja") == 0) ? 1 : 0;
      for (i = 0; i < paramString.length(); i++)
        if (paramString.charAt(i) == 'y')
        {
          if (m != 0)
          {
            paramString = new StringBuilder(paramString).insert(i, "G ").toString();
            break;
          }
          while ((i + 1 < paramString.length()) && (paramString.charAt(i + 1) == 'y'))
            i++;
          paramString = new StringBuilder(paramString).insert(i + 1, " G").toString();
          break;
        }
    }
    return paramString;
  }

  private static String adjustPatternToFourDigitYear(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    StringBuilder localStringBuilder = new StringBuilder(i + 3);
    for (int m = 0; m < i; m++)
    {
      int k;
      localStringBuilder.append(k = paramString.charAt(m));
      if ((j == 0) && (k == 121))
      {
        while ((m + 1 < i) && (paramString.charAt(m + 1) == 'y'))
          m++;
        localStringBuilder.append("yyy");
      }
      else if (k == 39)
      {
        j = j == 0 ? 1 : 0;
      }
    }
    return localStringBuilder.toString();
  }

  public static final DateSubParts convertDateInfo(int paramInt)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    if (paramInt < 2299161)
    {
      k = paramInt + 32082;
    }
    else
    {
      i = paramInt + 32044;
      j = (4 * i + 3) / 146097;
      k = i - j * 146097 / 4;
    }
    m = (4 * k + 3) / 1461;
    n = k - 1461 * m / 4;
    i1 = (5 * n + 2) / 153;
    int i2 = j * 100 + m - 4800 + i1 / 10;
    int i3 = i1 + 2 - 12 * (i1 / 10);
    int i4 = n - (153 * i1 + 2) / 5 + 1;
    return new DateSubParts(i2, i3, i4, null);
  }

  private Date getDate()
  {
    return getDateFromNumDays(this.mValue.getValue());
  }

  private static Date getDateFromNumDays(int paramInt)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    DateSubParts localDateSubParts = convertDateInfo(paramInt);
    Calendar localCalendar = SessionData.get().getICUCalendarInstance();
    synchronized (localCalendar)
    {
      localCalendar.set(1, localDateSubParts.getYear());
      localCalendar.set(2, localDateSubParts.getMonth());
      localCalendar.set(5, localDateSubParts.getDay());
      return localCalendar.getTime();
    }
  }

  public static class DateSubParts
  {
    private int year;
    private int month;
    private int day;

    private DateSubParts(int paramInt1, int paramInt2, int paramInt3)
    {
      this.year = paramInt1;
      this.month = paramInt2;
      this.day = paramInt3;
    }

    public int getYear()
    {
      return this.year;
    }

    public int getMonth()
    {
      return this.month;
    }

    public int getDay()
    {
      return this.day;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.DateType
 * JD-Core Version:    0.6.1
 */