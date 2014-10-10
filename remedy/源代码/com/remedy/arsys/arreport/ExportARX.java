package com.remedy.arsys.arreport;

import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;
import java.util.ArrayList;

public class ExportARX extends ExportFormat
{
  static final String kCharSet = "CHAR-SET";
  static final String kSchema = "SCHEMA";
  static final String kFlds = "FIELDS";
  static final String kFldIds = "FLD-ID";
  static final String kDataTypes = "DTYPES";
  static final String kData = "DATA";
  static final String PSEP = " ";

  public ExportARX(int paramInt)
  {
    super(paramInt);
  }

  private String escapeValues(String paramString)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '\\':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\\");
        i = j + 1;
        break;
      case '"':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\"");
        i = j + 1;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }

  private String addDelimiters(int paramInt, String paramString)
  {
    if (paramString.length() == 0)
      return "\"\"";
    String str = paramString;
    if ((this.mFieldTypes[paramInt] == DataType.ATTACHMENT.toInt()) || (this.mFieldTypes[paramInt] == DataType.CHAR.toInt()) || (this.mFieldTypes[paramInt] == DataType.CURRENCY.toInt()) || (this.mFieldTypes[paramInt] == DataType.DECIMAL.toInt()) || (this.mFieldTypes[paramInt] == DataType.DIARY.toInt()) || (this.mFieldTypes[paramInt] == DataType.VIEW.toInt()))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append('"').append(ARReportFormatter.expandSpecials(paramString)).append('"');
      str = localStringBuilder.toString();
    }
    return str;
  }

  void writeHeader()
    throws ReportException
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("CHAR-SET").append(" ").append(this.mWriter.getCharSet()).append('\n');
    this.mWriter.print(localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
    localStringBuilder1.append("SCHEMA").append(" ").append('"').append(ARReportFormatter.expandSpecials(this.mFormName)).append('"').append('\n');
    this.mWriter.print(localStringBuilder1.toString());
    localStringBuilder1.setLength(0);
    StringBuilder localStringBuilder2 = new StringBuilder();
    StringBuilder localStringBuilder3 = new StringBuilder();
    localStringBuilder1.append("FIELDS").append(" ");
    localStringBuilder2.append("FLD-ID").append(" ");
    localStringBuilder3.append("DTYPES").append(" ");
    for (int i = 0; i < this.mFieldTypes.length; i++)
      if ((addStatusHistoryField(i)) && (addCurrencyField(i)))
      {
        ArrayList localArrayList = (ArrayList)this.mIdsLabelsNames.get(i);
        String str2 = (String)localArrayList.get(0);
        String str1 = (String)localArrayList.get(1);
        String str3 = getDataType(this.mFieldTypes[i]);
        localStringBuilder1.append('"').append(escapeValues(str1)).append('"');
        localStringBuilder2.append(str2);
        localStringBuilder3.append(str3);
        if (i < this.mFieldTypes.length - 1)
        {
          localStringBuilder1.append(" ");
          localStringBuilder2.append(" ");
          localStringBuilder3.append(" ");
        }
      }
    localStringBuilder1.append('\n');
    localStringBuilder2.append('\n');
    localStringBuilder3.append('\n');
    this.mWriter.print(localStringBuilder1.toString());
    this.mWriter.print(localStringBuilder2.toString());
    this.mWriter.print(localStringBuilder3.toString());
  }

  void writeFooter()
    throws ReportException
  {
  }

  private String getFormattedValue(Value paramValue, long paramLong)
    throws GoatException
  {
    String str1 = "";
    Value localValue = paramValue;
    localValue = fixPwdEncryptStr(localValue, paramLong);
    if (localValue != null)
    {
      Object localObject = localValue.getValue();
      int i = localValue.getDataType().toInt();
      if (i == DataType.ATTACHMENT.toInt())
      {
        AttachmentValue localAttachmentValue = (AttachmentValue)localObject;
        String str2 = makeAttachmentFilename(localAttachmentValue.getName(), this.mAttachDir, this.mAttachIndex);
        if (str2.length() > 0)
          localAttachmentValue.setName(str2);
        str1 = this.mARFormatter.formatTypeAttachmentARX(localObject, paramLong);
        if (str2.length() > 0)
        {
          addToAttachmentList(str2, paramLong);
          this.mAttachIndex += 1;
        }
      }
      else if (i == DataType.CHAR.toInt())
      {
        str1 = this.mARFormatter.formatTypeCharARX(localObject, paramLong);
      }
      else if (i == DataType.CURRENCY.toInt())
      {
        str1 = this.mARFormatter.formatTypeCurrencyARX(localObject, paramLong);
      }
      else if (i == DataType.DATE.toInt())
      {
        str1 = this.mARFormatter.formatTypeDateARX(localObject, paramLong);
      }
      else if (i == DataType.DECIMAL.toInt())
      {
        str1 = this.mARFormatter.formatTypeDecimalARX(localObject, paramLong);
      }
      else if (i == DataType.DIARY.toInt())
      {
        str1 = this.mARFormatter.formatTypeDiary(localObject, paramLong, this.mDiaryShowRecent, " ", "  ");
      }
      else if (i == DataType.ENUM.toInt())
      {
        str1 = this.mARFormatter.formatTypeEnumARX(localObject, paramLong);
      }
      else if (i == DataType.INTEGER.toInt())
      {
        str1 = this.mARFormatter.formatTypeInteger(localObject, paramLong);
      }
      else if (i == DataType.NULL.toInt())
      {
        str1 = this.mARFormatter.formatTypeNULL(localObject, paramLong);
      }
      else if (i == DataType.REAL.toInt())
      {
        str1 = this.mARFormatter.formatTypeRealARX(localObject, paramLong);
      }
      else if (i == DataType.TIME.toInt())
      {
        str1 = this.mARFormatter.formatTypeTimeARX(localObject, paramLong);
      }
      else if (i == DataType.TIME_OF_DAY.toInt())
      {
        str1 = this.mARFormatter.formatTypeTimeOfDayARX(localObject, paramLong);
      }
      else if (!$assertionsDisabled)
      {
        throw new AssertionError();
      }
    }
    if (str1 == null)
      str1 = "";
    return str1;
  }

  void writeData(CompactResult paramCompactResult)
    throws ReportException, GoatException
  {
    int i = paramCompactResult.getNumberofEntries();
    int j = this.mFieldTypes.length;
    StringBuilder localStringBuilder = new StringBuilder();
    for (int k = 0; k < i; k++)
    {
      CompactRecord localCompactRecord = paramCompactResult.getValueRecord(k);
      localStringBuilder.setLength(0);
      localStringBuilder.append("DATA").append(" ");
      if (this.mhasAttachments)
      {
        Value localValue2 = localCompactRecord.getValue(CoreFieldId.EntryId.getFieldId());
        assert (localValue2 != null);
        if (localValue2 != null)
          this.mEntryIdAttach = ((String)localValue2.getValue());
      }
      for (int m = 0; m < j; m++)
        if ((addStatusHistoryField(m)) && (addCurrencyField(m)))
        {
          long l = this.mfldIds[m];
          if (l != -1L)
          {
            Value localValue1 = localCompactRecord.getValue(this.mfldIds[m]);
            String str = getFormattedValue(localValue1, l);
            assert (str != null);
            localStringBuilder.append(addDelimiters(m, str));
            if (m < j - 1)
              localStringBuilder.append(" ");
          }
        }
      localStringBuilder.append('\n');
      this.mWriter.print(localStringBuilder.toString());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ExportARX
 * JD-Core Version:    0.6.1
 */