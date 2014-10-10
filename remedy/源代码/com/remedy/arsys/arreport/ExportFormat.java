package com.remedy.arsys.arreport;

import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.AttachmentData.AttachmentDataKey;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;
import com.remedy.arsys.stubs.SessionData;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public abstract class ExportFormat
{
  public static final int DEST_PRINT = 0;
  public static final int DEST_SCREEN = 1;
  public static final int DEST_FILE = 2;
  static final char VDELIM = '"';
  static final char LSEP = '\n';
  protected int mExportType;
  protected RemedyReportDefinition mReportDef;
  protected ArrayList mIdsLabelsNames;
  protected int[] mFieldTypes;
  protected int[] mfldIds;
  protected String mServerName;
  protected String mFormName;
  protected ExportWriter mWriter;
  protected ARReportFormatter mARFormatter;
  protected ArrayList mReportFieldAttrs;
  protected EnumLimit mStatusHistoryLimit;
  protected boolean mhasAttachments;
  protected ArrayList mAttachList;
  protected String mAttachDir;
  protected int mAttachIndex;
  protected String mEntryIdAttach;
  protected long mTs;
  protected NativeReportManager.ReportQuery mReportQuery = null;
  protected int mIteration = 0;
  protected boolean mDiaryShowRecent;

  public ExportFormat(int paramInt)
  {
    this.mExportType = paramInt;
    this.mTs = System.currentTimeMillis();
  }

  public void setup(RemedyReportDefinition paramRemedyReportDefinition, ArrayList paramArrayList, int[] paramArrayOfInt, String paramString1, String paramString2, ExportWriter paramExportWriter, ARReportFormatter paramARReportFormatter)
    throws ReportException
  {
    assert (paramRemedyReportDefinition != null);
    assert (paramArrayList != null);
    assert (paramArrayOfInt != null);
    assert (paramString1 != null);
    assert (paramString2 != null);
    assert (paramExportWriter != null);
    assert (paramARReportFormatter != null);
    this.mReportDef = paramRemedyReportDefinition;
    this.mIdsLabelsNames = paramArrayList;
    this.mFieldTypes = paramArrayOfInt;
    this.mServerName = paramString1;
    this.mFormName = paramString2;
    this.mWriter = paramExportWriter;
    this.mARFormatter = paramARReportFormatter;
    this.mfldIds = new int[this.mIdsLabelsNames.size()];
    for (int i = 0; i < this.mIdsLabelsNames.size(); i++)
    {
      ArrayList localArrayList = (ArrayList)this.mIdsLabelsNames.get(i);
      String str = (String)localArrayList.get(0);
      try
      {
        this.mfldIds[i] = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        this.mfldIds[i] = -1;
      }
    }
    try
    {
      this.mReportFieldAttrs = ((ArrayList)this.mReportDef.parseReportFieldAttr().clone());
    }
    catch (GoatException localGoatException)
    {
    }
    assert (this.mReportFieldAttrs != null);
    this.mDiaryShowRecent = false;
  }

  public void setDiaryPref(boolean paramBoolean)
  {
    this.mDiaryShowRecent = paramBoolean;
  }

  public void setStatusHistoryLimit(EnumLimit paramEnumLimit)
  {
    this.mStatusHistoryLimit = paramEnumLimit;
  }

  private void makeAttachDir()
  {
    File localFile = new File(this.mWriter.getFilename());
    String str1 = localFile.getName();
    int i = str1.lastIndexOf('.');
    if (i != -1)
      str1 = str1.substring(0, i);
    String str2 = Long.toString(this.mTs);
    this.mAttachDir = (str1 + "_" + str2);
  }

  protected String makeAttachmentFilename(String paramString1, String paramString2, int paramInt)
  {
    if (paramString1.length() == 0)
      return "";
    File localFile = new File(paramString1);
    String str1 = localFile.getName();
    int i = str1.lastIndexOf("\\");
    if (i != -1)
      str1 = str1.substring(i + 1);
    String str2 = "";
    try
    {
      int j = str1.lastIndexOf('.');
      String str3;
      if (j != -1)
        str3 = str1.substring(0, j) + "_" + Integer.toString(paramInt) + str1.substring(j);
      else
        str3 = str1 + "_" + Integer.toString(paramInt);
      str2 = URLEncoder.encode(str3, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    return str2;
  }

  public void setHasAttach(boolean paramBoolean)
  {
    this.mhasAttachments = paramBoolean;
    if (this.mhasAttachments)
    {
      this.mAttachList = new ArrayList();
      makeAttachDir();
      this.mAttachIndex = 0;
    }
  }

  protected void addToAttachmentList(String paramString, long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramString);
    localArrayList.add(this.mEntryIdAttach);
    localArrayList.add(new Long(paramLong));
    this.mAttachList.add(localArrayList);
  }

  private void writeAttachments()
    throws ReportException
  {
    if ((this.mAttachList != null) && (!this.mAttachList.isEmpty()))
      try
      {
        String str = "Export" + Long.toString(this.mTs);
        for (int i = 0; i < this.mAttachList.size(); i++)
        {
          ArrayList localArrayList = (ArrayList)this.mAttachList.get(i);
          this.mWriter.openFile((String)localArrayList.get(0));
          AttachmentData.AttachmentDataKey localAttachmentDataKey = new AttachmentData.AttachmentDataKey(this.mServerName, this.mFormName, (String)localArrayList.get(1), (int)((Long)localArrayList.get(2)).longValue(), str, SessionData.get().getID());
          AttachmentData localAttachmentData = AttachmentData.get(localAttachmentDataKey);
          localAttachmentData.writeTo(this.mWriter.getZipStream());
          this.mWriter.closeFile();
          AttachmentData.remove(localAttachmentDataKey);
        }
      }
      catch (GoatException localGoatException)
      {
        throw new ReportException(9301, localGoatException.toString());
      }
      catch (IOException localIOException)
      {
        throw new ReportException(9301, localIOException.toString());
      }
  }

  protected boolean addStatusHistoryField(int paramInt)
  {
    boolean bool = true;
    long l = this.mfldIds[paramInt];
    assert (l != -1L);
    int i = -1;
    if (l == CoreFieldId.StatusHistory.getFieldId())
    {
      assert (this.mReportFieldAttrs != null);
      ReportField localReportField = (ReportField)this.mReportFieldAttrs.get(paramInt);
      i = localReportField.getStatHistEnum();
      if (i != -1)
        bool = false;
    }
    return bool;
  }

  protected boolean addCurrencyField(int paramInt)
  {
    boolean bool = true;
    long l = this.mfldIds[paramInt];
    assert (l != -1L);
    ArrayList localArrayList = (ArrayList)this.mIdsLabelsNames.get(paramInt);
    String str = (String)localArrayList.get(2);
    int i = this.mFieldTypes[paramInt];
    if (i == DataType.CURRENCY.toInt())
      bool = ARReportFormatter.getCurrencyPart(str) == 0;
    return bool;
  }

  protected Value fixPwdEncryptStr(Value paramValue, long paramLong)
  {
    if (paramValue == null)
      return paramValue;
    if ((paramLong == 102L) || (paramLong == 123L))
    {
      if (paramValue.getDataType().toInt() == DataType.NULL.toInt())
        return new Value("");
      return paramValue;
    }
    return paramValue;
  }

  protected String getDataType(int paramInt)
  {
    String str = "";
    switch (paramInt)
    {
    case 2:
      str = " INTEGER";
      break;
    case 3:
      str = " REAL";
      break;
    case 4:
      str = " CHAR";
      break;
    case 5:
      str = " DIARY";
      break;
    case 11:
      str = " ATTACH";
      break;
    case 6:
      str = " ENUM";
      break;
    case 7:
      str = " TIME";
      break;
    case 13:
      str = " DATE";
      break;
    case 10:
      str = " DECIMAL";
      break;
    case 14:
      str = " TIMEOFDAY";
      break;
    case 12:
      str = " CURRENCY";
      break;
    case 8:
    case 9:
    default:
      str = " Unknown";
    }
    return str;
  }

  abstract void writeHeader()
    throws ReportException;

  abstract void writeFooter()
    throws ReportException;

  abstract void writeData(CompactResult paramCompactResult)
    throws ReportException, GoatException;

  public void startExport()
    throws ReportException
  {
    this.mWriter.openExportFile();
    writeHeader();
  }

  public void processResults(CompactResult paramCompactResult)
    throws ReportException, GoatException
  {
    writeData(paramCompactResult);
  }

  public void finishExport()
    throws ReportException
  {
    writeFooter();
    this.mWriter.closeExportFile();
    writeAttachments();
    this.mWriter.flush();
  }

  public static ExportFormat getExport(int paramInt)
  {
    switch (paramInt)
    {
    case 4:
      return new ExportCSV(paramInt);
    case 5:
      return new ExportARX(paramInt);
    case 6:
      return new ExportXML(paramInt);
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ExportFormat
 * JD-Core Version:    0.6.1
 */