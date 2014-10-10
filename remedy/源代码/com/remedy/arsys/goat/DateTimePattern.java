package com.remedy.arsys.goat;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.remedy.arsys.goat.preferences.AbstractARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

public class DateTimePattern
  implements Serializable
{
  private static final long serialVersionUID = 5397976456552381299L;
  private static transient Log MLog = Log.get(2);
  private String[] mParseDatePatterns;
  private String[] mParseTimePatterns;
  private String mDateFormatPattern;
  private String mTimeFormatPattern;
  private Long mDisplayTimeFormat;

  public DateTimePattern(Long paramLong)
  {
    this.mDisplayTimeFormat = paramLong;
  }

  public synchronized void initDateTimeFormatPattern()
  {
    if (AbstractARUserPreferences.CUSTOM_TIME_FORMAT.equals(this.mDisplayTimeFormat))
    {
      if (this.mDateFormatPattern != null)
      {
        localObject1 = SessionData.get().getICUDateOnlyShortSimpleDateFormatInstance();
        synchronized (localObject1)
        {
          ((SimpleDateFormat)localObject1).applyPattern(this.mDateFormatPattern);
          try
          {
            ((SimpleDateFormat)localObject1).format(new Date());
          }
          catch (IllegalArgumentException localIllegalArgumentException1)
          {
            MLog.warning("initializeDateTimePatterns:: Invalid Custom Date Format: " + this.mDateFormatPattern + "  Reset to use default.");
            this.mDateFormatPattern = null;
          }
        }
      }
      if (this.mTimeFormatPattern != null)
      {
        localObject1 = SessionData.get().getICUTimeOnlySimpleDateFormatInstance();
        synchronized (localObject1)
        {
          ((SimpleDateFormat)localObject1).applyPattern(this.mTimeFormatPattern);
          try
          {
            ((SimpleDateFormat)localObject1).format(new Date());
          }
          catch (IllegalArgumentException localIllegalArgumentException2)
          {
            MLog.warning("initializeDateTimePatterns:: Invalid Custom Time Format: " + this.mTimeFormatPattern + "  Reset to use default.");
            this.mTimeFormatPattern = null;
          }
        }
      }
    }
    Object localObject1 = WindowsDateTimeFormats.get(SessionData.get().getLocale());
    SimpleDateFormat localSimpleDateFormat;
    if ((this.mDateFormatPattern == null) || (this.mDateFormatPattern.length() == 0))
    {
      int i = 3;
      if (AbstractARUserPreferences.LONG_TIME_FORMAT.equals(this.mDisplayTimeFormat))
        i = 0;
      if (localObject1 != null)
      {
        this.mDateFormatPattern = (i == 3 ? ((LocaleContentHandler.CultureInfo)localObject1).getShortDate() : ((LocaleContentHandler.CultureInfo)localObject1).getLongDate());
      }
      else
      {
        localSimpleDateFormat = (SimpleDateFormat)DateFormat.getDateInstance(i, new Locale(SessionData.get().getLocale()));
        this.mDateFormatPattern = localSimpleDateFormat.toPattern();
      }
    }
    if ((this.mTimeFormatPattern == null) || (this.mTimeFormatPattern.length() == 0))
    {
      if (localObject1 != null)
      {
        str = ((LocaleContentHandler.CultureInfo)localObject1).getLongTime();
      }
      else
      {
        localSimpleDateFormat = (SimpleDateFormat)DateFormat.getTimeInstance(1, new Locale(SessionData.get().getLocale()));
        str = localSimpleDateFormat.toPattern();
      }
      String str = str.replace('z', ' ');
      this.mTimeFormatPattern = str.trim();
      int j = this.mTimeFormatPattern.charAt(this.mTimeFormatPattern.length() - 1);
      if ((j == 58) || (j == 46))
        this.mTimeFormatPattern = this.mTimeFormatPattern.substring(0, this.mTimeFormatPattern.length() - 1);
    }
    this.mParseDatePatterns = DateTimeParser.getParsePattern(this.mDateFormatPattern);
    this.mParseTimePatterns = DateTimeParser.getParsePattern(this.mTimeFormatPattern);
  }

  public String getDateFormatPattern()
  {
    return this.mDateFormatPattern;
  }

  public String getTimeFormatPattern()
  {
    return this.mTimeFormatPattern;
  }

  public String[] getDateParsePatterns()
  {
    return this.mParseDatePatterns;
  }

  public String[] getTimeParsePatterns()
  {
    return this.mParseTimePatterns;
  }

  public void setDisplayTimeFormat(Long paramLong)
  {
    this.mDisplayTimeFormat = paramLong;
  }

  public void setDatePattern(String paramString)
  {
    this.mDateFormatPattern = paramString;
  }

  public void setTimePattern(String paramString)
  {
    this.mTimeFormatPattern = paramString;
  }

  public void reset()
  {
    this.mDisplayTimeFormat = AbstractARUserPreferences.SHORT_TIME_FORMAT;
    this.mDateFormatPattern = null;
    this.mTimeFormatPattern = null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.DateTimePattern
 * JD-Core Version:    0.6.1
 */