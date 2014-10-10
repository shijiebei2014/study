package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Time;
import com.bmc.arsys.api.Value;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Date;

public class TODType extends GoatType
{
  private static final long serialVersionUID = 8135033567884413132L;
  private transient Log mLog = Log.get(11);
  private Time mValue;

  public TODType(Value paramValue, int paramInt)
  {
    this.mValue = ((Time)paramValue.getValue());
    assert (this.mValue != null);
  }

  public TODType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
  {
    this(paramValue, paramInt);
  }

  public TODType(String paramString, int paramInt)
  {
    this.mValue = new Time(Long.parseLong(paramString));
    assert (this.mValue != null);
  }

  public int getDataType()
  {
    return 14;
  }

  public int getOperandType()
  {
    return 9;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new TODType(").append(this.mValue.getValue()).append(")");
  }

  public String emitAR()
  {
    return "" + this.mValue.getValue();
  }

  public Value toValue()
  {
    return new Value(this.mValue);
  }

  public String forHTML()
  {
    return format(getTime());
  }

  public static String format(long paramLong)
  {
    return format(getTimeFromTimeSubParts(convertTimeInfo(paramLong / 1000L)));
  }

  public static String format(Date paramDate)
  {
    SessionData localSessionData = SessionData.get();
    String str = localSessionData.getTimeFormatPattern();
    SimpleDateFormat localSimpleDateFormat = localSessionData.getICUTimeOnlySimpleDateFormatInstance();
    synchronized (localSimpleDateFormat)
    {
      localSimpleDateFormat.applyPattern(str);
      return localSimpleDateFormat.format(paramDate);
    }
  }

  public static final TimeSubParts convertTimeInfo(long paramLong)
  {
    int i = (int)paramLong % 60;
    int j = (int)((paramLong - i) % 3600L) / 60;
    int k = (int)Math.floor(paramLong / 3600L);
    return new TimeSubParts(k, j, i, null);
  }

  private Date getTime()
  {
    long l = this.mValue.getValue();
    TimeSubParts localTimeSubParts = convertTimeInfo(l);
    return getTimeFromTimeSubParts(localTimeSubParts);
  }

  private static Date getTimeFromTimeSubParts(TimeSubParts paramTimeSubParts)
  {
    Calendar localCalendar = SessionData.get().getICUCalendarInstance();
    synchronized (localCalendar)
    {
      localCalendar.set(2004, 0, 1, paramTimeSubParts.getHours(), paramTimeSubParts.getMinutes(), paramTimeSubParts.getSeconds());
      return localCalendar.getTime();
    }
  }

  public String toPrimitive()
  {
    return "" + this.mValue.getValue();
  }

  public static class TimeSubParts
  {
    private int hours;
    private int minutes;
    private int seconds;

    private TimeSubParts(int paramInt1, int paramInt2, int paramInt3)
    {
      this.hours = paramInt1;
      this.minutes = paramInt2;
      this.seconds = paramInt3;
    }

    public int getHours()
    {
      return this.hours;
    }

    public int getMinutes()
    {
      return this.minutes;
    }

    public int getSeconds()
    {
      return this.seconds;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.TODType
 * JD-Core Version:    0.6.1
 */