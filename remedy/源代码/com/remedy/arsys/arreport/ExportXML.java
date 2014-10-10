package com.remedy.arsys.arreport;

import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;
import com.remedy.arsys.share.HTMLWriter;
import java.text.MessageFormat;
import java.util.ArrayList;

public class ExportXML extends ExportFormat
{
  static final String tVersion = "?xml version=\"1.0\" encoding=\"{0}\"?";
  static final String tARRep = "arsystem_xml_report";
  static final String tXForm = "xform";
  static final String tInstance = "instance";
  static final int TABSIZEINSPACE = 2;

  public ExportXML(int paramInt)
  {
    super(paramInt);
  }

  private HTMLWriter writeFieldInformation(HTMLWriter paramHTMLWriter)
  {
    paramHTMLWriter.openTag("field-info").endTag();
    for (int i = 0; i < this.mFieldTypes.length; i++)
      if ((addStatusHistoryField(i)) && (addCurrencyField(i)))
      {
        paramHTMLWriter.openTag("field").endTag();
        ArrayList localArrayList = (ArrayList)this.mIdsLabelsNames.get(i);
        String str2 = (String)localArrayList.get(0);
        String str1 = (String)localArrayList.get(1);
        String str3 = getDataType(this.mFieldTypes[i]);
        paramHTMLWriter.openTag("name").endTag(false).cdata(str1).closeTag("name");
        paramHTMLWriter.openTag("id").endTag(false).cdata(str2).closeTag("id");
        paramHTMLWriter.openTag("type").endTag(false).cdata(str3).closeTag("type");
        paramHTMLWriter.closeTag("field");
      }
    paramHTMLWriter.closeTag("field-info");
    return paramHTMLWriter;
  }

  private HTMLWriter writeModelString(HTMLWriter paramHTMLWriter)
  {
    paramHTMLWriter.openTag("model").endTag();
    paramHTMLWriter.openTag("group").attr("name", "field-info").attr("minOccurs", "1").attr("maxOccurs", "1").endTag();
    paramHTMLWriter.openTag("group").attr("name", "field").attr("minOccurs", "1").attr("maxOccurs", "*").endTag();
    paramHTMLWriter.openTag("string").attr("name", "name").closeOpenTag();
    paramHTMLWriter.openTag("number").attr("name", "id").attr("long", "true").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "type").attr("range", "closed").endTag();
    paramHTMLWriter.openTag("value").endTag(false).cdata("CHAR").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("TIME").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("DECIMAL").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("REAL").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("INT").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("ENUM").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("ATTACH").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("DIARY").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("TIMEOFDAY").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("DATE").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("CURRENCY").closeTag("value");
    paramHTMLWriter.openTag("value").endTag(false).cdata("NULL").closeTag("value");
    paramHTMLWriter.closeTag("string");
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.openTag("group").attr("name", "entry").attr("minOccurs", "1").attr("maxOccurs", "*").endTag();
    paramHTMLWriter.openTag("group").attr("name", "field_value").attr("minOccurs", "1").attr("maxOccurs", "*").endTag();
    paramHTMLWriter.openTag("string").attr("name", "value").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("group").attr("name", "status_history").attr("maxOccurs", "*").endTag();
    paramHTMLWriter.openTag("string").attr("name", "date").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "user").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.openTag("group").attr("name", "currency").attr("maxOccurs", "*").endTag();
    paramHTMLWriter.openTag("string").attr("name", "currency_value").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "currency_type").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "currency_date").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "currency_functional").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.openTag("group").attr("name", "diary").attr("maxOccurs", "*").endTag();
    paramHTMLWriter.openTag("string").attr("name", "date").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "user").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.openTag("string").attr("name", "diary_content").attr("maxOccurs", "*").closeOpenTag();
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.closeTag("group");
    paramHTMLWriter.closeTag("model");
    return paramHTMLWriter;
  }

  void writeHeader()
    throws ReportException
  {
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(1000));
    localHTMLWriter.setParams(false, false, 2);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = this.mWriter.getCharSet();
    localHTMLWriter.openWholeTag(MessageFormat.format("?xml version=\"1.0\" encoding=\"{0}\"?", arrayOfObject));
    localHTMLWriter.openTag("arsystem_xml_report").endTag();
    localHTMLWriter.openTag("xform").attr("id", ARReportFormatter.expandSpecials(this.mFormName)).attr("xmlns", "http://www.w3.org/2000/xforms").endTag();
    writeModelString(localHTMLWriter);
    localHTMLWriter.openTag("instance").endTag();
    writeFieldInformation(localHTMLWriter);
    this.mWriter.print(localHTMLWriter.toString());
  }

  void writeFooter()
    throws ReportException
  {
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder());
    localHTMLWriter.setParams(false, false, 2);
    localHTMLWriter.increaseIndent();
    localHTMLWriter.increaseIndent();
    localHTMLWriter.increaseIndent();
    localHTMLWriter.closeTag("instance");
    localHTMLWriter.closeTag("xform");
    localHTMLWriter.closeTag("arsystem_xml_report");
    this.mWriter.print(localHTMLWriter.toString());
  }

  private String getFormattedValue(Value paramValue, long paramLong, int paramInt)
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
        str1 = this.mARFormatter.formatTypeAttachmentXML(localObject, paramLong, paramInt, 2);
        if (str2.length() > 0)
        {
          addToAttachmentList(str2, paramLong);
          this.mAttachIndex += 1;
        }
      }
      else if (i == DataType.CHAR.toInt())
      {
        if (paramLong == CoreFieldId.StatusHistory.getFieldId())
        {
          assert (this.mStatusHistoryLimit != null);
          str1 = this.mARFormatter.formatStatusHistoryXML(localObject, paramLong, this.mStatusHistoryLimit, paramInt, 2);
        }
        else
        {
          str1 = this.mARFormatter.formatTypeCharXML(localObject, paramLong, paramInt, 2);
        }
      }
      else if (i == DataType.CURRENCY.toInt())
      {
        str1 = this.mARFormatter.formatTypeCurrencyXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.DATE.toInt())
      {
        str1 = this.mARFormatter.formatTypeDateXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.DECIMAL.toInt())
      {
        str1 = this.mARFormatter.formatTypeDecimalXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.DIARY.toInt())
      {
        str1 = this.mARFormatter.formatTypeDiaryXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.ENUM.toInt())
      {
        str1 = this.mARFormatter.formatTypeEnumXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.INTEGER.toInt())
      {
        str1 = this.mARFormatter.formatTypeIntegerXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.NULL.toInt())
      {
        str1 = this.mARFormatter.formatTypeNULLXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.REAL.toInt())
      {
        str1 = this.mARFormatter.formatTypeRealXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.TIME.toInt())
      {
        str1 = this.mARFormatter.formatTypeTimeXML(localObject, paramLong, paramInt, 2);
      }
      else if (i == DataType.TIME_OF_DAY.toInt())
      {
        str1 = this.mARFormatter.formatTypeTimeOfDayXML(localObject, paramLong, paramInt, 2);
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
    int j = paramCompactResult.getNumberofEntries();
    int k = this.mFieldTypes.length;
    HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(2000));
    localHTMLWriter.setParams(false, false, 2);
    if (this.mIteration == 0)
    {
      localHTMLWriter.increaseIndent();
      localHTMLWriter.increaseIndent();
      localHTMLWriter.increaseIndent();
    }
    for (int m = 0; m < j; m++)
    {
      CompactRecord localCompactRecord = paramCompactResult.getValueRecord(m);
      localHTMLWriter.openTag("entry").endTag();
      if (this.mhasAttachments)
      {
        Value localValue2 = localCompactRecord.getValue(CoreFieldId.EntryId.getFieldId());
        assert (localValue2 != null);
        if (localValue2 != null)
          this.mEntryIdAttach = ((String)localValue2.getValue());
      }
      for (int n = 0; n < k; n++)
        if ((addStatusHistoryField(n)) && (addCurrencyField(n)))
        {
          int i = this.mfldIds[n];
          if (i != -1)
          {
            Value localValue1 = localCompactRecord.getValue(i);
            String str = getFormattedValue(localValue1, i, 5);
            assert (str != null);
            localHTMLWriter.openTag("field_value").endTag();
            localHTMLWriter.append(str);
            localHTMLWriter.closeTag("field_value");
          }
        }
      localHTMLWriter.closeTag("entry");
    }
    this.mWriter.print(localHTMLWriter.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ExportXML
 * JD-Core Version:    0.6.1
 */