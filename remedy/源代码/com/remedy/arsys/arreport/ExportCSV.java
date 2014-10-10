package com.remedy.arsys.arreport;

import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;
import java.util.ArrayList;

public class ExportCSV extends ExportFormat
{
  private final String mSeparator = ",";
  private ReportUtil repUtil = null;

  public ExportCSV(int paramInt)
  {
    super(paramInt);
  }

  private String DoubleDoubleQuotes(String paramString)
  {
    return paramString.replaceAll("\"", "\"\"");
  }

  private String formatCSVValue(String paramString)
  {
    String str = paramString;
    if (str.length() == 0)
      return str;
    int i = str.indexOf('"', 0);
    int j = str.indexOf(',', 0);
    int k = str.indexOf('\r', 0);
    int m = str.indexOf('\n', 0);
    if ((k != -1) || (m != -1))
    {
      str = str.replaceAll("\r\n", "\n");
      str = str.replaceAll("\n", " ");
    }
    int n = str.charAt(0) == ' ' ? 1 : 0;
    int i1 = str.charAt(str.length() - 1) == ' ' ? 1 : 0;
    if ((i != -1) || (j != -1) || (n != 0) || (i1 != 0))
    {
      if (i != 1)
        str = DoubleDoubleQuotes(str);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append('"').append(str).append('"');
      str = localStringBuilder.toString();
    }
    return str;
  }

  public void writeHeader()
    throws ReportException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = this.mIdsLabelsNames.size();
    int j = i - 1;
    for (int k = 0; k < i; k++)
      if (addCurrencyField(k))
      {
        ArrayList localArrayList = (ArrayList)this.mIdsLabelsNames.get(k);
        localStringBuilder.append(formatCSVValue((String)localArrayList.get(1)));
        if (k < j)
          localStringBuilder.append(this.mSeparator);
      }
    localStringBuilder.append('\n');
    this.mWriter.print(localStringBuilder.toString());
  }

  private String getFormattedValue(Value paramValue, int paramInt1, int paramInt2, int paramInt3)
    throws GoatException
  {
    String str = "";
    Value localValue = paramValue;
    localValue = fixPwdEncryptStr(localValue, paramInt1);
    if (localValue != null)
    {
      Object localObject = localValue.getValue();
      int i = localValue.getDataType().toInt();
      if (i == DataType.ATTACHMENT.toInt())
        str = this.mARFormatter.formatTypeAttachment(localObject, paramInt1);
      else if (i == DataType.CHAR.toInt())
      {
        if (paramInt1 == CoreFieldId.StatusHistory.getFieldId())
        {
          assert (this.mStatusHistoryLimit != null);
          str = this.mARFormatter.formatStatusHistory((String)localObject, paramInt1, paramInt2, paramInt3, 0, this.mStatusHistoryLimit);
        }
        else
        {
          str = this.mARFormatter.formatTypeCharCSV(localObject, paramInt1);
        }
      }
      else if (i == DataType.CURRENCY.toInt())
        str = this.mARFormatter.formatTypeCurrencyCSV(localObject, paramInt1);
      else if (i == DataType.DATE.toInt())
        str = this.mARFormatter.formatTypeDate(localObject, paramInt1);
      else if (i == DataType.DECIMAL.toInt())
        str = this.mARFormatter.formatTypeDecimalCSV(localObject, paramInt1);
      else if (i == DataType.DIARY.toInt())
        str = this.mARFormatter.formatTypeDiary(localObject, paramInt1, this.mDiaryShowRecent, "  ", "    ");
      else if (i == DataType.ENUM.toInt())
        str = this.mARFormatter.formatTypeEnum(localObject, paramInt1);
      else if (i == DataType.INTEGER.toInt())
        str = this.mARFormatter.formatTypeInteger(localObject, paramInt1);
      else if (i == DataType.NULL.toInt())
        str = this.mARFormatter.formatTypeNULL(localObject, paramInt1);
      else if (i == DataType.REAL.toInt())
        str = this.mARFormatter.formatTypeRealCSV(localObject, paramInt1);
      else if (i == DataType.TIME.toInt())
        str = this.mARFormatter.formatTypeTime(localObject, paramInt1);
      else if (i == DataType.TIME_OF_DAY.toInt())
        str = this.mARFormatter.formatTypeTimeOfDay(localObject, paramInt1);
      else if (!$assertionsDisabled)
        throw new AssertionError();
    }
    if (str == null)
      str = "";
    return str;
  }

  public void writeData(CompactResult paramCompactResult)
    throws ReportException, GoatException
  {
    int i = paramCompactResult.getNumberofEntries();
    int j = this.mFieldTypes.length;
    if (this.mIteration == 0)
      this.repUtil = new ReportUtil(this.mARFormatter, this.mStatusHistoryLimit, this.mDiaryShowRecent);
    for (int k = 0; k < i; k++)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      CompactRecord localCompactRecord = paramCompactResult.getValueRecord(k);
      for (int m = 0; m < j; m++)
      {
        ReportField localReportField = this.mReportQuery.getReportFieldsEntry(m);
        assert (localReportField != null);
        if (localReportField != null)
        {
          Value localValue = localCompactRecord.getValue(this.mfldIds[m]);
          String str1 = null;
          if (localValue != null)
          {
            DataType localDataType = localValue.getDataType();
            String str2 = localDataType.equals(DataType.CURRENCY) ? localReportField.getFieldName() : null;
            str1 = this.repUtil.getFieldValueString(localValue, this.mfldIds[m], localReportField.getStatHistEnum(), localReportField.getUserOrTime(), 0, str2);
            str1 = getFormattedValue(localValue, this.mfldIds[m], localReportField.getStatHistEnum(), localReportField.getUserOrTime());
            assert (str1 != null);
            localStringBuilder.append(formatCSVValue(str1));
            if (m < j - 1)
              localStringBuilder.append(this.mSeparator);
          }
        }
      }
      localStringBuilder.append('\n');
      this.mWriter.print(localStringBuilder.toString());
    }
  }

  public void writeFooter()
    throws ReportException
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ExportCSV
 * JD-Core Version:    0.6.1
 */