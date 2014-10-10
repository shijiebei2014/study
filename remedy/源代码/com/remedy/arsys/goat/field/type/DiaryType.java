package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.DiaryItem;
import com.bmc.arsys.api.DiaryListValue;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class DiaryType extends GoatType
{
  private String mValue;

  public DiaryType(Value paramValue, int paramInt)
    throws GoatException
  {
    if ((paramValue.getValue() instanceof DiaryListValue))
      this.mValue = format((DiaryItem[])((DiaryListValue)paramValue.getValue()).toArray(new DiaryItem[0]));
    else
      this.mValue = ((String)paramValue.getValue());
  }

  public DiaryType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt);
  }

  public DiaryType(String paramString, int paramInt)
    throws GoatException
  {
    throw new GoatException("Illegal use of DiaryType - should never happen");
  }

  public int getDataType()
  {
    return 5;
  }

  public int getOperandType()
  {
    return 4;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new CharType(").appendqs(this.mValue).append(")");
  }

  public String emitAR()
  {
    return escapeQuotedString("\"" + this.mValue + "\"");
  }

  public Value toValue()
  {
    try
    {
      return new Value(DiaryListValue.decode(this.mValue));
    }
    catch (ARException localARException)
    {
      if ((this.mValue != null) && (this.mValue.length() > 0))
        return new Value(this.mValue);
    }
    return new Value(new DiaryListValue());
  }

  public String toPrimitive()
  {
    return this.mValue;
  }

  private static void getDiaryEntry(StringBuilder paramStringBuilder, DiaryItem paramDiaryItem)
  {
    String str = new TimeType(paramDiaryItem.getTimestamp()).getDateTimeString();
    paramStringBuilder.append(str).append(" ").append(paramDiaryItem.getUser()).append("\n").append(paramDiaryItem.getText()).append("\n\n");
  }

  public static String format(DiaryItem[] paramArrayOfDiaryItem)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramArrayOfDiaryItem != null)
    {
      Long localLong = SessionData.get().getPreferences().getDiaryShowRecentFirst();
      int i;
      if (localLong.equals(ARUserPreferences.DIARY_YES))
        for (i = paramArrayOfDiaryItem.length - 1; i >= 0; i--)
          getDiaryEntry(localStringBuilder, paramArrayOfDiaryItem[i]);
      else
        for (i = 0; i < paramArrayOfDiaryItem.length; i++)
          getDiaryEntry(localStringBuilder, paramArrayOfDiaryItem[i]);
    }
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.DiaryType
 * JD-Core Version:    0.6.1
 */