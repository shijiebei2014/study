package com.remedy.arsys.arreport;

import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportUtil
{
  private final ARReportFormatter mARFormatter;
  private final EnumLimit mStatusHistoryLimit;
  private final boolean mDiaryShowRecent;
  private static final Pattern KEYWORD_FIELD_REF_PAT = Pattern.compile("(\\$([^$]+)\\$)");
  private static final String STATUS_HISTORY_START_STR = String.valueOf(CoreFieldId.StatusHistory.getFieldId()) + ".";

  public ReportUtil(ARReportFormatter paramARReportFormatter, EnumLimit paramEnumLimit, boolean paramBoolean)
  {
    this.mARFormatter = paramARReportFormatter;
    this.mStatusHistoryLimit = paramEnumLimit;
    this.mDiaryShowRecent = paramBoolean;
  }

  public String getFieldValueString(Value paramValue, long paramLong, int paramInt1, int paramInt2, int paramInt3, String paramString)
    throws GoatException
  {
    String str = "";
    if (paramValue != null)
    {
      Object localObject = paramValue.getValue();
      int i = paramValue.getDataType().toInt();
      if (i == DataType.ATTACHMENT.toInt())
        str = this.mARFormatter.formatTypeAttachment(localObject, paramLong);
      else if (i == DataType.BITMASK.toInt())
        str = this.mARFormatter.formatTypeBitmask(localObject, paramLong);
      else if (i == DataType.CHAR.toInt())
      {
        if (paramLong == CoreFieldId.StatusHistory.getFieldId())
          str = this.mARFormatter.formatStatusHistory((String)localObject, paramLong, paramInt1, paramInt2, paramInt3, this.mStatusHistoryLimit);
        else
          str = this.mARFormatter.formatTypeChar(localObject, paramLong);
      }
      else if (i == DataType.CURRENCY.toInt())
        str = this.mARFormatter.formatTypeCurrency(localObject, paramLong, paramString);
      else if (i == DataType.DATE.toInt())
        str = this.mARFormatter.formatTypeDate(localObject, paramLong);
      else if (i == DataType.DECIMAL.toInt())
        str = this.mARFormatter.formatTypeDecimal(localObject, paramLong);
      else if (i == DataType.DIARY.toInt())
        str = this.mARFormatter.formatTypeDiary(localObject, paramLong, this.mDiaryShowRecent, "\n", "\n\n");
      else if (i == DataType.ENUM.toInt())
        str = this.mARFormatter.formatTypeEnum(localObject, paramLong);
      else if (i == DataType.INTEGER.toInt())
        str = this.mARFormatter.formatTypeInteger(localObject, paramLong);
      else if (i == DataType.KEYWORD.toInt())
        str = this.mARFormatter.formatTypeKeyword(localObject, paramLong);
      else if (i == DataType.NULL.toInt())
        str = this.mARFormatter.formatTypeNULL(localObject, paramLong);
      else if (i == DataType.REAL.toInt())
        str = this.mARFormatter.formatTypeReal(localObject, paramLong);
      else if (i == DataType.TIME.toInt())
        str = this.mARFormatter.formatTypeTime(localObject, paramLong);
      else if (i == DataType.TIME_OF_DAY.toInt())
        str = this.mARFormatter.formatTypeTimeOfDay(localObject, paramLong);
      else if (i == DataType.ULONG.toInt())
        str = this.mARFormatter.formatTypeUlong(localObject, paramLong);
      else if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return HTMLWriter.escapeSpace(str);
  }

  public String resolveKeywords(String paramString1, CompactRecord paramCompactRecord, ArrayList paramArrayList, int paramInt, boolean paramBoolean, String paramString2, String paramString3, Long paramLong, String paramString4, String paramString5, String paramString6, String paramString7, ServerLogin paramServerLogin, Form paramForm)
  {
    int i = 0;
    int j = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    Matcher localMatcher = KEYWORD_FIELD_REF_PAT.matcher(paramString1);
    while (localMatcher.find())
    {
      String str1 = localMatcher.group(2);
      if (paramArrayList != null)
        paramArrayList.add(str1);
      String str2 = null;
      if ((paramCompactRecord == null) && (str1 != null) && (str1.charAt(0) == '-'))
        str2 = Keywords.replaceKeywordsAtWebTier(paramServerLogin, paramForm.getName(), str1, paramString2, paramString3, paramLong, paramString4, paramString5, paramString6, paramString7, paramBoolean);
      else if (paramCompactRecord != null)
        str2 = replaceFieldRef(str1, paramCompactRecord, paramInt);
      j = localMatcher.start();
      localStringBuilder.append(paramString1.substring(i, j)).append(str2 != null ? str2 : localMatcher.group());
      i = localMatcher.end();
    }
    if (i < paramString1.length())
      localStringBuilder.append(paramString1.substring(i));
    return localStringBuilder.toString();
  }

  private String replaceFieldRef(String paramString, CompactRecord paramCompactRecord, int paramInt)
  {
    if (paramString != null)
      try
      {
        int i = 0;
        int j = -1;
        int k = -1;
        if (paramString.startsWith(STATUS_HISTORY_START_STR))
        {
          localObject = new StringTokenizer(paramString, ".");
          if (((StringTokenizer)localObject).countTokens() == 3)
          {
            i = Integer.parseInt(((StringTokenizer)localObject).nextToken());
            j = Integer.parseInt(((StringTokenizer)localObject).nextToken());
            k = Integer.parseInt(((StringTokenizer)localObject).nextToken());
          }
          else
          {
            i = CoreFieldId.StatusHistory.getFieldId();
          }
        }
        else
        {
          i = Integer.parseInt(paramString);
        }
        Object localObject = paramCompactRecord.getValue(i);
        return getFieldValueString((Value)localObject, i, j, k, paramInt, null);
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
      catch (GoatException localGoatException)
      {
      }
    return null;
  }

  public static void emitToStream(OutputStream paramOutputStream, byte[] paramArrayOfByte)
    throws GoatException
  {
    if (paramOutputStream == null)
      throw new GoatException("Cannot write data to output stream. The stream is null");
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0))
      return;
    try
    {
      paramOutputStream.write(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      throw new GoatException(localIOException.getMessage(), localIOException.getCause());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ReportUtil
 * JD-Core Version:    0.6.1
 */