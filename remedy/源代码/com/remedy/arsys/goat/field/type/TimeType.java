package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.ibm.icu.text.SimpleDateFormat;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Date;

public class TimeType extends GoatType
{
  private Timestamp mValue;
  private int mDisplayType;

  public TimeType(Value paramValue, int paramInt)
  {
    this.mValue = ((Timestamp)paramValue.getValue());
    assert (this.mValue != null);
    this.mDisplayType = 0;
  }

  public TimeType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
  {
    this(paramValue, paramInt);
  }

  public TimeType(String paramString, int paramInt)
  {
    this.mValue = new Timestamp(Long.parseLong(paramString));
    assert (this.mValue != null);
    this.mDisplayType = 0;
  }

  public TimeType(Timestamp paramTimestamp)
  {
    this.mValue = paramTimestamp;
    assert (this.mValue != null);
    this.mDisplayType = 0;
  }

  public int getDataType()
  {
    return 7;
  }

  public void setDisplayType(int paramInt)
  {
    this.mDisplayType = paramInt;
  }

  public int getOperandType()
  {
    return 7;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new TimeType(").append(this.mValue.getValue()).append(")");
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
    return getDateTimeString();
  }

  public String getDateTimeString()
  {
    return format(this.mValue.toDate(), this.mDisplayType);
  }

  public static String format(long paramLong, int paramInt)
  {
    return format(new Date(paramLong), paramInt);
  }

  public static String format(Date paramDate, int paramInt)
  {
    SessionData localSessionData = SessionData.get();
    String str1 = localSessionData.getDateFormatPattern();
    String str2 = localSessionData.getTimeFormatPattern();
    String str3;
    if (paramInt == 2)
      str3 = str1;
    else if (paramInt == 1)
      str3 = str2;
    else
      str3 = str1 + " " + str2;
    SimpleDateFormat localSimpleDateFormat = localSessionData.getICULocalisedSimpleDateFormatInstance();
    synchronized (localSimpleDateFormat)
    {
      localSimpleDateFormat.applyPattern(str3);
      return localSimpleDateFormat.format(paramDate);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.TimeType
 * JD-Core Version:    0.6.1
 */