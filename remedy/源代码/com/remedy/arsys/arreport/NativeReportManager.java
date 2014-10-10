package com.remedy.arsys.arreport;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.SelectionFieldLimit;
import com.bmc.arsys.api.ServerInfoMap;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.reporting.common.QueryExecutor;
import com.remedy.arsys.reporting.common.RepServletParms;
import com.remedy.arsys.reporting.common.ReportException;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NativeReportManager
{
  private static Log mLog = Log.get(0);
  private static final String EXP_FNAME = "Report";
  private static final String EXP_CSV_FNAME = "Report.csv";
  private static final String EXP_ARX_FNAME = "Report.arx";
  private static final String EXP_XML_FNAME = "Report.xml";
  private static final String EXP_ZIP_FNAME = "Report.zip";
  private static final String EXP_ZIP_EXT = "zip";
  private static final String RPT_FNAME = "Report.rep";
  private static final String UNQUALIFIED_SEARCH = "4\\1\\2\\2\\1\\2\\2\\1\\";
  private HttpServletResponse mResponse = null;
  private BufferedOutputStream mBOutputStream = null;
  private ServerLogin mContext = null;
  private String mServer = null;
  private String mBrowser = null;
  private String mAppName = null;
  private Form mForm = null;
  private Form.ViewInfo mViewInfo = null;
  private CachedFieldMap mFieldMap = null;
  private String mQueryString = null;
  private String mQueryOverride = null;
  private String mReportLocation = null;
  private String mLocale = null;
  private String mLanguage = null;
  private String mCountry = null;
  private String mTimeZone = null;
  private String mCustomDateFormat = null;
  private String mCustomTimeFormat = null;
  private Long mDateTimeStyle = null;
  private boolean mShowRecentDiary = false;
  private EnumLimit mstatusHistoryLimit = null;
  private String mDestination;
  private String mReportFilename;
  private String mCharSet;
  private ARReportFormatter mARFormatter;
  private int mLocalReportFormat;
  private boolean mIsRFCCompat;
  private String mRFCLocale;
  private String mEntryIDs;
  private String mSortOrder;
  private ReportUtil repUtil;

  public NativeReportManager(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    this.mResponse = paramHttpServletResponse;
    this.mIsRFCCompat = browserIsRFC2184Compatible(paramHttpServletRequest);
    RepServletParms localRepServletParms = new RepServletParms();
    localRepServletParms.addParams(paramHttpServletRequest);
    this.mRFCLocale = SessionData.get().getLocale();
    this.mServer = localRepServletParms.getParameter("S");
    this.mContext = SessionData.get().getServerLogin(this.mServer);
    this.mAppName = localRepServletParms.getParameter("APP");
    this.mReportLocation = localRepServletParms.getParameter("RF");
    this.mBrowser = paramHttpServletRequest.getHeader("User-Agent");
    this.mQueryOverride = localRepServletParms.getParameter("QR");
    this.mQueryString = localRepServletParms.getParameter("Q");
    if (this.mQueryString == null)
      this.mQueryString = "";
    this.mEntryIDs = localRepServletParms.getParameter("entryids");
    if (this.mEntryIDs == null)
      this.mEntryIDs = "";
    this.mSortOrder = localRepServletParms.getParameter("sortorder");
    if (this.mSortOrder == null)
      this.mSortOrder = "";
    this.mTimeZone = localRepServletParms.getParameter("TZ");
    this.mLocale = localRepServletParms.getParameter("LOC");
    setCountryAndLanguage(localRepServletParms, this.mLocale);
    String str1 = localRepServletParms.getParameter("F");
    String str2 = localRepServletParms.getParameter("VW");
    this.mDestination = localRepServletParms.getParameter("DT");
    if (this.mDestination == null)
      this.mDestination = "";
    this.mCharSet = localRepServletParms.getParameter("CS");
    if (this.mCharSet == null)
      this.mCharSet = "";
    try
    {
      this.mForm = Form.get(this.mServer, str1);
      this.mFieldMap = this.mForm.getCachedFieldMap();
      this.mViewInfo = this.mForm.getViewInfoByInference(str2, false, true);
    }
    catch (GoatException localGoatException)
    {
      throw new GoatException(localGoatException.getMessage());
    }
    ARUserPreferences localARUserPreferences = SessionData.get().getPreferences();
    this.mDateTimeStyle = localARUserPreferences.getDisplayTimeFormat();
    this.mCustomDateFormat = localARUserPreferences.getCustomDateFormatStr();
    this.mCustomTimeFormat = localARUserPreferences.getCustomTimeFormatStr();
    if (localARUserPreferences.getDiaryShowRecentFirst().equals(ARUserPreferences.DIARY_NO))
      this.mShowRecentDiary = true;
    this.mstatusHistoryLimit = getStatusEnumValues();
    assert (this.mstatusHistoryLimit != null);
    processReportRequest(localRepServletParms);
  }

  private void setCountryAndLanguage(RepServletParms paramRepServletParms, String paramString)
  {
    String str1 = null;
    String str2 = null;
    if (paramString != null)
    {
      int i = paramString.indexOf("_");
      int j = -1;
      if (i >= 0)
      {
        str1 = paramString.substring(0, i);
        j = paramString.indexOf("_", i + 1);
        if (j < 0)
          str2 = paramString.substring(i + 1);
        else
          str2 = paramString.substring(i + 1, j);
      }
      else
      {
        str1 = paramString;
      }
    }
    this.mLanguage = paramRepServletParms.getParameter("LNG");
    if (this.mLanguage == null)
      this.mLanguage = str1;
    this.mCountry = paramRepServletParms.getParameter("CTRY");
    if (this.mCountry == null)
      this.mCountry = str2;
  }

  private EnumLimit getStatusEnumValues()
  {
    Field localField = (Field)this.mFieldMap.get(Integer.valueOf(CoreFieldId.Status.getFieldId()));
    if (localField != null)
      return new EnumLimit((SelectionFieldLimit)localField.getFieldLimit());
    return null;
  }

  public void create()
    throws ReportException
  {
  }

  public void edit()
    throws ReportException
  {
  }

  private int getFormat(RemedyReportDefinition paramRemedyReportDefinition)
    throws ReportException
  {
    try
    {
      if (this.mLocalReportFormat == -1)
        return paramRemedyReportDefinition.getFormat();
      return this.mLocalReportFormat;
    }
    catch (GoatException localGoatException)
    {
    }
    return 1;
  }

  int extensionToReportFormat(String paramString)
  {
    int i = "Report.csv".lastIndexOf(".");
    String str = "Report.csv".substring(i + 1);
    if (str.equalsIgnoreCase(paramString))
      return 4;
    i = "Report.arx".lastIndexOf(".");
    str = "Report.arx".substring(i + 1);
    if (str.equalsIgnoreCase(paramString))
      return 5;
    i = "Report.xml".lastIndexOf(".");
    str = "Report.xml".substring(i + 1);
    if (str.equalsIgnoreCase(paramString))
      return 6;
    return -1;
  }

  private String getExtension(String paramString)
  {
    String str = "";
    int i = paramString.lastIndexOf('.');
    if (i != -1)
    {
      str = paramString.substring(i + 1);
      str = str.trim();
    }
    return str;
  }

  private void dynamicReportFormat()
  {
    if (this.mReportFilename.length() > 0)
    {
      String str = getExtension(this.mReportFilename);
      if (str.length() > 0)
        this.mLocalReportFormat = extensionToReportFormat(str);
    }
  }

  public void processReportRequest(RepServletParms paramRepServletParms)
    throws GoatException, IOException
  {
    if (this.mReportLocation == null)
      throw new GoatException(9230);
    try
    {
      RemedyReportDefinition localRemedyReportDefinition = new RemedyReportDefinition(this.mReportLocation, CharsetsExport.getUseServer(this.mServer));
      if (localRemedyReportDefinition == null)
        throw new GoatException(9231);
      this.mARFormatter = new ARReportFormatter(this.mContext, this.mForm, this.mServer, this.mFieldMap, this.mViewInfo);
      if (this.mARFormatter == null)
        throw new GoatException(9302);
      this.repUtil = new ReportUtil(this.mARFormatter, this.mstatusHistoryLimit, this.mShowRecentDiary);
      this.mARFormatter.setFormatPref(this.mLocale, this.mTimeZone, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat);
      this.mLocalReportFormat = -1;
      getDestinationParams();
      dynamicReportFormat();
      int i = getFormat(localRemedyReportDefinition);
      switch (i)
      {
      case 1:
      case 2:
      case 3:
        doViewReport(localRemedyReportDefinition, paramRepServletParms);
        break;
      case 4:
      case 5:
      case 6:
        doExportReport(localRemedyReportDefinition);
      }
    }
    catch (ReportException localReportException)
    {
      localObject1 = localReportException.getStatusInfo(this.mContext.getLocale());
      sendErrMsg2((List)localObject1);
    }
    catch (ARException localARException)
    {
      Object localObject1 = new GoatException(localARException);
      List localList = ((GoatException)localObject1).getStatusInfo(this.mContext.getLocale());
      sendErrMsg2(localList);
    }
    finally
    {
      try
      {
        if (this.mBOutputStream != null)
          this.mBOutputStream.flush();
      }
      catch (IOException localIOException4)
      {
        throw new GoatException(localIOException4.getMessage());
      }
    }
  }

  private String statusInfoToString(List<StatusInfo> paramList, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramList == null)
      return null;
    for (int i = 0; i < paramList.size(); i++)
    {
      if (i > 0)
        localStringBuilder.append("\n");
      int j = ((StatusInfo)paramList.get(i)).getMessageType();
      if (j == 2)
        localStringBuilder.append("ARERR [");
      else if (j == 1)
        localStringBuilder.append("ARWARN [");
      else if (j == 0)
        localStringBuilder.append("ARNOTE [");
      localStringBuilder.append(((StatusInfo)paramList.get(i)).getMessageNum()).append("] ").append(((StatusInfo)paramList.get(i)).getMessageText());
      String str = ((StatusInfo)paramList.get(i)).getAppendedText();
      if ((str != null) && (str.length() != 0))
        localStringBuilder.append(" : ").append(str);
    }
    return localStringBuilder.toString();
  }

  private void sendErrMsg2(List<StatusInfo> paramList)
    throws IOException
  {
    this.mResponse.setHeader("Cache-Control", "no-cache");
    this.mResponse.setHeader("Pragma", "no-cache");
    this.mResponse.setDateHeader("Last-Modified", new Date().getTime());
    this.mResponse.setContentType("text/html;charset=UTF-8");
    String str = statusInfoToString(paramList, this.mLocale);
    if (this.mBOutputStream == null)
      this.mBOutputStream = new BufferedOutputStream(this.mResponse.getOutputStream(), 2048);
    StringBuffer localStringBuffer = new StringBuffer("<html><head>");
    if ((str != null) && (str.length() > 0))
    {
      mLog.log(Level.FINE, str.toString());
      localStringBuffer.append("<script>var Msg=");
      localStringBuffer.append("\"").append(JSWriter.escape(str)).append("\"");
      localStringBuffer.append(";</script>");
    }
    localStringBuffer.append("</head><body>");
    if ((str != null) && (str.length() > 0))
      localStringBuffer.append("<h1>").append(HTMLWriter.escape(str)).append("</h1>");
    localStringBuffer.append("</body></html>");
    this.mBOutputStream.write(localStringBuffer.toString().getBytes());
  }

  private String getServerCharset()
  {
    return CharsetsExport.getUseServer(this.mServer);
  }

  private void verifyCharSet()
  {
    CharsetsExport localCharsetsExport = CharsetsExport.getInstance();
    boolean bool1 = false;
    if (this.mCharSet.length() > 0)
    {
      boolean bool2 = CharsetsExport.requestIsUseServer(this.mCharSet);
      if (bool2)
      {
        String str = getServerCharset();
        if (!localCharsetsExport.containsKey(str))
          mLog.log(Level.WARNING, "Request for ServerCharset returned invalid valid value");
        this.mCharSet = str;
      }
      bool1 = localCharsetsExport.containsKey(this.mCharSet);
      if (bool1)
        this.mCharSet = localCharsetsExport.getProperCharset(this.mCharSet);
    }
    if ((this.mCharSet.length() == 0) || (!bool1))
      this.mCharSet = CharsetsExport.getDefaultCharset(this.mServer);
  }

  private String getNameOfFile(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
      return "";
    boolean bool = false;
    try
    {
      bool = Pattern.matches(".*[\\;\\*\\@\\%\\^\\(\\)\\[\\]\\{\\}\\'\\<\\>\\?\\+\\=\\|\\\"\\&].*", paramString);
    }
    catch (PatternSyntaxException localPatternSyntaxException)
    {
    }
    String str1 = "";
    File localFile = new File(paramString);
    String str2 = localFile.getParent();
    if (str2 != null)
      str1 = localFile.getName();
    else
      str1 = paramString;
    String[] arrayOfString = str1.split("\\.");
    if (arrayOfString.length > 2)
      str1 = arrayOfString[(arrayOfString.length - 2)] + "." + arrayOfString[(arrayOfString.length - 1)];
    if (str1.indexOf('.') == str1.length() - 1)
      str1 = str1.substring(0, str1.length() - 1);
    if (bool)
    {
      int i = str1.indexOf('.');
      if (i > 0)
      {
        int j = extensionToReportFormat(str1.substring(i + 1));
        if (j != -1)
          str1 = "Report." + str1.substring(i + 1);
        else
          str1 = "";
      }
      else
      {
        str1 = "";
      }
    }
    return str1;
  }

  private void getDestinationParams()
  {
    this.mReportFilename = "";
    if (this.mDestination.length() > 0)
    {
      int i = this.mDestination.indexOf(": ");
      if (this.mDestination.length() > i + 2)
      {
        String str = this.mDestination.substring(i + 2);
        this.mReportFilename = getNameOfFile(str);
        this.mDestination = this.mDestination.substring(0, i + 2);
      }
      if (((this.mDestination.equals("to-print: ")) || (this.mDestination.equals("to-screen: ")) || (this.mDestination.equals("to-file: ")) ? 1 : 0) == 0)
        this.mDestination = "to-screen: ";
    }
    else
    {
      this.mDestination = "to-screen: ";
    }
  }

  ArrayList getIdsLabels(ReportQuery paramReportQuery)
    throws ReportException
  {
    return paramReportQuery.getReportFieldsIdsLabels();
  }

  int[] getFieldTypes(ArrayList<ArrayList<String>> paramArrayList)
  {
    int i = paramArrayList.size();
    int[] arrayOfInt = new int[i];
    for (int k = 0; k < i; k++)
    {
      ArrayList localArrayList = (ArrayList)paramArrayList.get(k);
      String str = (String)localArrayList.get(0);
      int j;
      try
      {
        j = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        j = -1;
      }
      assert (j != -1);
      Field localField = null;
      if (j != -1)
        localField = (Field)this.mFieldMap.get(Integer.valueOf(j));
      assert (localField != null);
      arrayOfInt[k] = (localField != null ? localField.getDataType() : DataType.CHAR.toInt());
    }
    return arrayOfInt;
  }

  String[] makeExportFilename(int paramInt, boolean paramBoolean)
  {
    Object localObject = "";
    String str = "";
    if (this.mReportFilename.length() > 0)
    {
      str = this.mReportFilename;
      localObject = this.mReportFilename;
      int i = this.mReportFilename.lastIndexOf(".");
      if (paramBoolean)
        if (i != -1)
          localObject = this.mReportFilename.substring(0, i) + '.' + "zip";
        else
          localObject = this.mReportFilename + '.' + "zip";
    }
    else
    {
      switch (paramInt)
      {
      case 4:
        str = "Report.csv";
        break;
      case 5:
        str = "Report.arx";
        break;
      case 6:
        str = "Report.xml";
      }
      localObject = str;
      if (paramBoolean)
        localObject = "Report.zip";
    }
    String[] arrayOfString = new String[2];
    if (paramInt == 4)
      localObject = str;
    arrayOfString[0] = localObject;
    arrayOfString[1] = str;
    return arrayOfString;
  }

  private boolean attachmentRequested(int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramArrayOfInt.length; i++)
      if (paramArrayOfInt[i] == 11)
        return true;
    return false;
  }

  private void doExportReport(RemedyReportDefinition paramRemedyReportDefinition)
    throws GoatException, ReportException, ARException
  {
    verifyCharSet();
    ReportQuery localReportQuery = new ReportQuery(paramRemedyReportDefinition, this.mEntryIDs, this.mSortOrder);
    localReportQuery.setupForQuery(this.mQueryString);
    ArrayList localArrayList = localReportQuery.getReportFieldsIdsLabels();
    int[] arrayOfInt = getFieldTypes(localArrayList);
    int i = getFormat(paramRemedyReportDefinition);
    boolean bool = attachmentRequested(arrayOfInt);
    if ((bool) && (i != 4))
      localReportQuery.addFieldToRequest(CoreFieldId.EntryId.getFieldId());
    ExportFormat localExportFormat = ExportFormat.getExport(i);
    assert (localExportFormat != null);
    localExportFormat.mReportQuery = localReportQuery;
    int j = 1;
    if (this.mDestination.equals("to-file: "))
      j = 2;
    if (this.mDestination.equals("to-print: "))
      j = 1;
    String[] arrayOfString = makeExportFilename(i, bool);
    ExportWriter localExportWriter = new ExportWriter(this.mResponse, j, this.mCharSet, arrayOfString, i, this.mIsRFCCompat, this.mRFCLocale);
    localExportFormat.setup(paramRemedyReportDefinition, localArrayList, arrayOfInt, this.mForm.getServerName(), this.mForm.getName().toString(), localExportWriter, this.mARFormatter);
    localExportFormat.setDiaryPref(this.mShowRecentDiary);
    localExportFormat.setStatusHistoryLimit(this.mstatusHistoryLimit);
    localExportFormat.setHasAttach(bool);
    localExportFormat.startExport();
    ExportNativeReportEmitter localExportNativeReportEmitter = new ExportNativeReportEmitter(localExportFormat);
    localReportQuery.execute(localExportNativeReportEmitter);
    localExportFormat.finishExport();
  }

  int getServerMaxEntries()
    throws GoatException
  {
    ServerInfoMap localServerInfoMap;
    try
    {
      int[] arrayOfInt = { 28 };
      localServerInfoMap = this.mContext.getServerInfo(arrayOfInt);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    if ((localServerInfoMap != null) && (localServerInfoMap.size() > 0))
      return Integer.parseInt(((Value)((Map.Entry)localServerInfoMap.entrySet().iterator().next()).getValue()).toString());
    return 0;
  }

  String makeRPTFilename()
  {
    if (this.mReportFilename.length() > 0)
      return this.mReportFilename;
    return "Report.rep";
  }

  private void doViewReport(RemedyReportDefinition paramRemedyReportDefinition, RepServletParms paramRepServletParms)
    throws GoatException, ReportException, ARException
  {
    if (!this.mDestination.equals("to-screen: "))
      mLog.log(Level.WARNING, "Destination for Record, Column or Compressed is not screen");
    ReportQuery localReportQuery = new ReportQuery(paramRemedyReportDefinition, this.mEntryIDs, this.mSortOrder);
    localReportQuery.setupForQuery(this.mQueryString);
    localReportQuery.addStatusHistoryField();
    localReportQuery.addStatisticsFields();
    try
    {
      this.mResponse.setContentType("text/html;charset=UTF-8");
      String str1 = this.mDestination.equals("to-screen: ") ? "inline" : "attachment";
      String str2 = makeRPTFilename();
      if (str2.length() > 0)
        str1 = str1 + ";filename=" + str2;
      this.mResponse.setHeader("Content-Disposition", str1);
      this.mBOutputStream = new BufferedOutputStream(this.mResponse.getOutputStream(), 2048);
    }
    catch (IOException localIOException1)
    {
      throw new ReportException(9234, localIOException1.toString());
    }
    Object localObject1 = null;
    int i = getFormat(paramRemedyReportDefinition);
    if ((i == 2) || (i == 3))
      localObject1 = new ColumnReportTransformer();
    else if (i == 1)
      localObject1 = new RecordReportTransformer();
    String str3 = this.repUtil.resolveKeywords(paramRemedyReportDefinition.getTitle(), null, null, 0, false, this.mLocale, this.mTimeZone, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat, this.mBrowser, this.mAppName, this.mContext, this.mForm);
    String str4 = this.repUtil.resolveKeywords(paramRemedyReportDefinition.getHeader(), null, null, 0, false, this.mLocale, this.mTimeZone, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat, this.mBrowser, this.mAppName, this.mContext, this.mForm);
    String str5 = this.repUtil.resolveKeywords(paramRemedyReportDefinition.getFooter(), null, null, 0, false, this.mLocale, this.mTimeZone, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat, this.mBrowser, this.mAppName, this.mContext, this.mForm);
    ((ReportTransformer)localObject1).doInit(str3, str4, str5);
    ((ReportTransformer)localObject1).doStartHeader();
    ArrayList localArrayList1 = localReportQuery.getReportFieldsIdsLabels();
    for (int j = 0; j < localArrayList1.size(); j++)
    {
      localObject2 = (ArrayList)localArrayList1.get(j);
      ((ReportTransformer)localObject1).doHeader((String)((ArrayList)localObject2).get(0), (String)((ArrayList)localObject2).get(1));
    }
    ((ReportTransformer)localObject1).doEndHeader();
    ArrayList localArrayList2 = paramRemedyReportDefinition.parseStatistics();
    Object localObject2 = null;
    int k = localArrayList2.size();
    for (int m = 0; m < k; m++)
    {
      localObject2 = (ReportStatistics)localArrayList2.get(m);
      ((ReportStatistics)localObject2).setUserContext(this.mContext);
      ((ReportStatistics)localObject2).resetValues();
    }
    int[] arrayOfInt = null;
    arrayOfInt = paramRemedyReportDefinition.getGroupList();
    ReportStatistics localReportStatistics = null;
    int n = 0;
    ViewNativeReportEmitter localViewNativeReportEmitter = new ViewNativeReportEmitter(paramRemedyReportDefinition, localArrayList2, this.mstatusHistoryLimit, this.repUtil, (ReportTransformer)localObject1, SessionData.get().getPreferences(), paramRepServletParms, localReportQuery, this.mForm, this.mContext, this.mBrowser, this.mBOutputStream, this.mDestination);
    localReportQuery.execute(localViewNativeReportEmitter);
    n = 0;
    try
    {
      int i1 = localArrayList2.size();
      int i2 = 0;
      long l = 0L;
      for (int i3 = 0; i3 < i1; i3++)
      {
        localReportStatistics = (ReportStatistics)localArrayList2.get(i3);
        i2 = localReportStatistics.getComputeOn();
        l = 0L;
        if ((arrayOfInt != null) && (arrayOfInt.length >= i2) && (i2 <= 5) && (arrayOfInt[(i2 - 1)] != 0))
          l = arrayOfInt[(i2 - 1)];
        if (n == 0)
        {
          n = 1;
          ((ReportTransformer)localObject1).doStartStat();
        }
        String str6 = this.repUtil.resolveKeywords(localReportStatistics.getLabel(), localViewNativeReportEmitter.prevEntry, null, 1, false, this.mLocale, this.mTimeZone, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat, this.mBrowser, this.mAppName, this.mContext, this.mForm);
        String str7 = localReportStatistics.getValueString(this.mDateTimeStyle, this.mLocale, this.mTimeZone, this.mCustomDateFormat, this.mCustomTimeFormat);
        ((ReportTransformer)localObject1).doStat(str6, localReportStatistics.getLayoutString(), Long.toString(l), str7);
        localReportStatistics.resetValues();
      }
      if (n != 0)
      {
        n = 0;
        ((ReportTransformer)localObject1).doEndStat();
      }
    }
    catch (Exception localException)
    {
      mLog.log(Level.SEVERE, localException.getMessage(), localException);
    }
    ((ReportTransformer)localObject1).doEnd(str5);
    try
    {
      ReportUtil.emitToStream(this.mBOutputStream, ((ReportTransformer)localObject1).getHtml().getBytes("UTF-8"));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new GoatException(localUnsupportedEncodingException.getMessage());
    }
    try
    {
      this.mBOutputStream.flush();
    }
    catch (IOException localIOException2)
    {
      throw new GoatException(localIOException2.getMessage());
    }
  }

  private ArrayList<ReportField> getStatFields(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramString == null) || (this.mFieldMap == null))
      return localArrayList;
    int i = 0;
    int j = 0;
    StringBuilder localStringBuilder = null;
    Field localField = null;
    ReportField localReportField = null;
    int k = paramString.length();
    while (i < k)
    {
      char c = paramString.charAt(i);
      if (c == '\'')
      {
        if (j == 0)
        {
          localStringBuilder = new StringBuilder();
          j = 1;
        }
        else if ((i + 1 < paramString.length()) && (paramString.charAt(i + 1) == '\''))
        {
          localStringBuilder.append('\'');
          i++;
        }
        else
        {
          String str = localStringBuilder.toString();
          int m = str.indexOf('.');
          if (m > 0)
            str = str.substring(0, m);
          long l = -1L;
          try
          {
            l = Long.parseLong(str);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            l = -1L;
          }
          localField = (Field)this.mFieldMap.get(Integer.valueOf((int)l));
          if (localField != null)
          {
            localReportField = new ReportField();
            localReportField.setFieldName(localField.getName());
            localReportField.setId(String.valueOf(localField.getFieldID()));
            localArrayList.add(localReportField);
          }
          j = 0;
          localStringBuilder = null;
        }
      }
      else if (j != 0)
        localStringBuilder.append(c);
      i++;
    }
    return localArrayList;
  }

  private String getStatLabelFields(ArrayList<ReportField> paramArrayList, String paramString)
  {
    String str1 = "";
    if ((paramString == null) || (this.mFieldMap == null))
      return str1;
    ArrayList localArrayList = new ArrayList();
    str1 = this.repUtil.resolveKeywords(paramString, null, localArrayList, 1, false, this.mLocale, this.mTimeZone, this.mDateTimeStyle, this.mCustomDateFormat, this.mCustomTimeFormat, this.mBrowser, this.mAppName, this.mContext, this.mForm);
    Field localField = null;
    ReportField localReportField = null;
    int i = localArrayList.size();
    for (int j = 0; j < i; j++)
      try
      {
        String str2 = (String)localArrayList.get(j);
        if (str2 != null)
        {
          localField = (Field)this.mFieldMap.get(Integer.valueOf((int)Long.parseLong(str2)));
          if (localField != null)
          {
            localReportField = new ReportField();
            localReportField.setFieldName(localField.getName());
            localReportField.setId(str2);
            paramArrayList.add(localReportField);
          }
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
    return str1;
  }

  public static boolean browserIsRFC2184Compatible(HttpServletRequest paramHttpServletRequest)
  {
    String str = paramHttpServletRequest.getHeader("User-Agent");
    return (str != null) && (str.indexOf("MSIE") == -1) && (str.indexOf("AppleWebKit") == -1);
  }

  class ReportQuery
  {
    private final RemedyReportDefinition mRQreportDef;
    ARQualifier mRQqual = null;
    ArrayList mRQsortFields;
    ArrayList mRQstatInfo;
    EntryListFieldInfo[] mRQfieldsToRetrieve;
    int[] mRQFieldIds;
    SortInfo[] mRQsortInfo;
    ArrayList mRQreportFields;
    int mRQreportFieldsSize;
    ReportStatistics mRQrs;
    boolean mRptInQuery = false;
    String mEntryIDs;
    String mSortOrder;
    String mReportQueryString = "";
    protected static final int AR_SORT_ASCENDING = 1;
    protected static final int AR_SORT_DESCENDING = 2;
    List<String> mEntryKeyList = null;

    public ReportQuery(RemedyReportDefinition arg2)
      throws GoatException, ReportException
    {
      Object localObject;
      this.mRQreportDef = localObject;
      try
      {
        this.mEntryIDs = "";
        this.mSortOrder = "";
        this.mRQreportFields = ((ArrayList)this.mRQreportDef.parseReportFieldAttr().clone());
        this.mRQreportFieldsSize = this.mRQreportFields.size();
        if ((!$assertionsDisabled) && (this.mRQreportFields.size() <= 0))
          throw new AssertionError();
      }
      catch (GoatException localGoatException)
      {
        throw localGoatException;
      }
      catch (ReportException localReportException)
      {
        throw localReportException;
      }
    }

    public ReportQuery(RemedyReportDefinition paramString, String arg3)
      throws GoatException, ReportException
    {
      this(paramString);
      Object localObject;
      this.mEntryIDs = localObject;
    }

    public ReportQuery(RemedyReportDefinition paramString1, String paramString2, String arg4)
      throws GoatException, ReportException
    {
      this(paramString1, paramString2);
      Object localObject;
      this.mSortOrder = localObject;
    }

    public void setEntryIDs(String paramString)
    {
      this.mEntryIDs = paramString;
    }

    public String getEntryIDs()
    {
      return this.mEntryIDs;
    }

    public void setSortOrderString(String paramString)
    {
      this.mSortOrder = paramString;
    }

    public String getSortOrderString()
    {
      return this.mSortOrder;
    }

    public void setupForQuery(String paramString)
      throws GoatException, ReportException
    {
      String str1 = paramString;
      String str2 = this.mRQreportDef.parseQuery();
      if ((str2 != null) && (str2.length() > 0) && ((NativeReportManager.this.mQueryOverride == null) || (NativeReportManager.this.mQueryOverride.equals("0")) || (NativeReportManager.this.mQueryOverride.equals("no"))))
      {
        str1 = NativeReportManager.this.repUtil.resolveKeywords(str2, null, null, 0, true, NativeReportManager.this.mLocale, NativeReportManager.this.mTimeZone, NativeReportManager.this.mDateTimeStyle, NativeReportManager.this.mCustomDateFormat, NativeReportManager.this.mCustomTimeFormat, NativeReportManager.this.mBrowser, NativeReportManager.this.mAppName, NativeReportManager.this.mContext, NativeReportManager.this.mForm);
        this.mRptInQuery = true;
      }
      str1 = str1.replaceAll("Status-History", "Status History");
      this.mReportQueryString = str1;
      if ((this.mReportQueryString.equalsIgnoreCase("4\\1\\2\\2\\1\\2\\2\\1\\")) && (!ServerInfo.get(NativeReportManager.this.mServer).getAllowUnqualQueries()))
      {
        NativeReportManager.mLog.log(Level.SEVERE, this.mRQreportDef.getReportName() + " - An unqualified search was issued and the server has been configured to disallow unqualified searches.");
        throw new ReportException(361, "An unqualified search was issued and the server has been configured to disallow unqualified searches.");
      }
      if (this.mSortOrder.length() > 0)
      {
        this.mRQsortFields = parseSortOrderString(this.mSortOrder);
        this.mRQreportDef.setSortInfo(this.mRQsortFields);
      }
      else
      {
        this.mRQsortFields = this.mRQreportDef.parseSort();
      }
      this.mRQstatInfo = this.mRQreportDef.parseStatistics();
      this.mRQsortInfo = makeSortInfo(this.mRQsortFields);
      buildEntryFieldInfo();
      this.mRQreportFieldsSize = this.mRQreportFields.size();
    }

    private void buildEntryFieldInfo()
    {
      this.mRQfieldsToRetrieve = makeEntryListFieldInfo(this.mRQreportFields);
      this.mRQFieldIds = (this.mRQfieldsToRetrieve != null ? new int[this.mRQfieldsToRetrieve.length] : new int[0]);
      if (this.mRQfieldsToRetrieve != null)
        for (int i = 0; i < this.mRQFieldIds.length; i++)
          this.mRQFieldIds[i] = this.mRQfieldsToRetrieve[i].getFieldId();
    }

    private ArrayList<SortInfo> parseSortOrderString(String paramString)
    {
      ArrayList localArrayList = new ArrayList();
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ":");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str1 = localStringTokenizer.nextToken();
        String str2 = localStringTokenizer.nextToken();
        try
        {
          int i = (int)Long.parseLong(str1);
          int j = 1;
          try
          {
            int k = Integer.parseInt(str2);
            if (k == 2)
              j = 2;
          }
          catch (NumberFormatException localNumberFormatException)
          {
          }
          localArrayList.add(new SortInfo(i, j));
        }
        catch (Exception localException)
        {
        }
      }
      return localArrayList;
    }

    public int getReportFieldsSize()
    {
      return this.mRQreportFieldsSize;
    }

    public int getFieldsSize()
    {
      return this.mRQreportFields.size();
    }

    int calcReturnMaxEntries()
    {
      int i = 0;
      try
      {
        int j = NativeReportManager.this.getServerMaxEntries();
        ARUserPreferences localARUserPreferences = SessionData.get().getPreferences();
        int k = 0;
        if (localARUserPreferences.getLimitQueryItems().equals(ARUserPreferences.YES))
          k = localARUserPreferences.getMaxQueryItems();
        if ((j != 0) || (k != 0))
        {
          if ((j != 0) && (k == 0))
            i = j;
          else if ((j == 0) && (k != 0))
            i = k;
          else
            i = Math.min(j, k);
        }
        else
          i = j;
      }
      catch (GoatException localGoatException)
      {
      }
      return i;
    }

    public Entry[] execute(IReportRowEmitter paramIReportRowEmitter)
      throws ReportException, ARException, GoatException
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1;
      if ((this.mEntryIDs.length() > 0) && (!this.mRptInQuery))
      {
        localObject1 = new StringTokenizer(this.mEntryIDs, ",");
        while (((StringTokenizer)localObject1).hasMoreTokens())
        {
          localObject2 = ((StringTokenizer)localObject1).nextToken();
          localObject2 = ((String)localObject2).trim();
          if (((String)localObject2).length() > 0)
            localArrayList.add(localObject2);
        }
      }
      Object localObject3;
      if (localArrayList.size() == 0)
      {
        localObject1 = getARQualifier();
        localObject2 = null;
        if (localObject1 != null)
          localObject2 = ((ARQualifier)localObject1).getQualInfo();
        localObject3 = new QueryExecutor(NativeReportManager.this.mContext, NativeReportManager.this.mServer, NativeReportManager.this.mForm.getName(), (QualifierInfo)localObject2);
        ((QueryExecutor)localObject3).setSortInfo(this.mRQsortInfo);
        int j = calcReturnMaxEntries();
        ((QueryExecutor)localObject3).getAllEntryIds(j, localArrayList, paramIReportRowEmitter.getEntriesLimit());
      }
      if (localArrayList.size() == 0)
        return null;
      this.mEntryKeyList = localArrayList;
      NativeReportManager.mLog.log(Level.WARNING, "AR System Report " + this.mRQreportDef.getReportName() + " - Total records is:" + this.mEntryKeyList.size());
      int i = 0;
      Object localObject2 = new ArrayList();
      do
      {
        localObject3 = getEntryChunk(100, i);
        if ((localObject3 != null) && (((List)localObject3).size() > 0))
        {
          ((List)localObject2).addAll((Collection)localObject3);
          paramIReportRowEmitter.iteratorCallback((Entry[])((List)localObject2).toArray(new Entry[0]), this.mEntryKeyList.size());
          i += ((List)localObject3).size();
          ((List)localObject2).clear();
        }
      }
      while (i < this.mEntryKeyList.size());
      localArrayList.clear();
      this.mEntryKeyList = null;
      return (Entry[])((List)localObject2).toArray(new Entry[0]);
    }

    private ARQualifier getARQualifier()
    {
      try
      {
        if (this.mRQqual == null)
          if (!ARQualifier.isEncodedQualStr(this.mReportQueryString))
          {
            this.mRQqual = new ARQualifier(NativeReportManager.this.mContext, this.mReportQueryString, NativeReportManager.this.mFieldMap, null, 0);
          }
          else
          {
            QualifierInfo localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(SessionData.get().getServerLogin(NativeReportManager.this.mServer), this.mReportQueryString);
            this.mRQqual = new ARQualifier(localQualifierInfo);
          }
      }
      catch (GoatException localGoatException)
      {
      }
      return this.mRQqual;
    }

    private List<Entry> getEntryChunk(int paramInt1, int paramInt2)
      throws ReportException, ARException
    {
      int i = this.mEntryKeyList.size();
      int j = i - paramInt2 > paramInt1 ? paramInt1 : i - paramInt2;
      List localList = this.mEntryKeyList.subList(paramInt2, paramInt2 + j);
      return NativeReportManager.this.mContext.getListEntryObjects(NativeReportManager.this.mForm.getName(), localList, this.mRQFieldIds);
    }

    public Entry[] executeQuery(int paramInt, long paramLong)
      throws ReportException, ARException
    {
      assert (paramInt >= 0);
      assert (paramLong >= 0L);
      QualifierInfo localQualifierInfo = this.mRQqual.getQualInfo();
      QueryExecutor localQueryExecutor = new QueryExecutor(NativeReportManager.this.mContext, NativeReportManager.this.mServer, NativeReportManager.this.mForm.getName(), localQualifierInfo, paramInt, paramLong, this.mRQfieldsToRetrieve, this.mRQsortInfo, true);
      return localQueryExecutor.executeQuery();
    }

    public Map getReportLabels()
      throws ReportException
    {
      HashMap localHashMap = new HashMap();
      for (int j = 0; j < this.mRQreportFieldsSize; j++)
      {
        ReportField localReportField = (ReportField)this.mRQreportFields.get(j);
        if (localReportField == null)
          throw new ReportException(9233);
        int i = localReportField.getId();
        String str = localReportField.getLabel();
        if (str == null)
          str = "";
        localHashMap.put(Integer.valueOf(i), str);
      }
      return localHashMap;
    }

    public ArrayList<ArrayList<String>> getReportFieldsIdsLabels()
      throws ReportException
    {
      ArrayList localArrayList1 = new ArrayList();
      for (int j = 0; j < this.mRQreportFieldsSize; j++)
      {
        ReportField localReportField = (ReportField)this.mRQreportFields.get(j);
        if (localReportField == null)
          throw new ReportException(9233);
        int i = localReportField.getId();
        String str1 = "";
        if (i != 0)
          str1 = String.valueOf(i);
        String str2 = localReportField.getLabel();
        if (str2 == null)
          str2 = "";
        String str3 = localReportField.getFieldName();
        if (str3 == null)
          str3 = "";
        ArrayList localArrayList2 = new ArrayList();
        localArrayList2.add(str1);
        localArrayList2.add(str2);
        localArrayList2.add(str3);
        localArrayList1.add(localArrayList2);
      }
      return localArrayList1;
    }

    public int getReportFieldsId(int paramInt)
    {
      ReportField localReportField = (ReportField)this.mRQreportFields.get(paramInt);
      assert (localReportField != null);
      return localReportField.getId();
    }

    public ReportField getReportFieldsEntry(int paramInt)
    {
      return (ReportField)this.mRQreportFields.get(paramInt);
    }

    public int addFieldToRequest(int paramInt)
    {
      int i = -1;
      if (this.mRQreportFields != null)
      {
        Field localField = (Field)NativeReportManager.this.mFieldMap.get(Integer.valueOf(paramInt));
        if (localField != null)
        {
          String str = String.valueOf(localField.getFieldID());
          ReportField localReportField = new ReportField();
          localReportField.setId(str);
          localReportField.setFieldName(localField.getName());
          if (!this.mRQreportFields.contains(localReportField))
            this.mRQreportFields.add(localReportField);
          i = this.mRQreportFields.indexOf(localReportField);
        }
      }
      return i;
    }

    public void addStatusHistoryField()
    {
      addFieldToRequest(CoreFieldId.StatusHistory.getFieldId());
      buildEntryFieldInfo();
    }

    public void addStatisticsFields()
    {
      if ((this.mRQstatInfo == null) || (this.mRQstatInfo.size() <= 0))
        return;
      ReportStatistics localReportStatistics = null;
      ArrayList localArrayList = null;
      ReportField localReportField = null;
      int i = this.mRQstatInfo.size();
      for (int j = 0; j < i; j++)
      {
        localReportStatistics = (ReportStatistics)this.mRQstatInfo.get(j);
        String str1 = localReportStatistics.getExpression();
        localArrayList = NativeReportManager.this.getStatFields(str1);
        String str2 = localReportStatistics.getLabel();
        String str3 = NativeReportManager.this.getStatLabelFields(localArrayList, str2);
        localReportStatistics.setLabel(str3);
        int k = localArrayList.size();
        for (int m = 0; m < k; m++)
        {
          localReportField = (ReportField)localArrayList.get(m);
          addFieldToRequest(localReportField.getId());
        }
      }
      buildEntryFieldInfo();
    }

    private EntryListFieldInfo[] makeEntryListFieldInfo(ArrayList paramArrayList)
    {
      EntryListFieldInfo[] arrayOfEntryListFieldInfo = null;
      int i = paramArrayList.size();
      ReportField localReportField;
      int j;
      if (i > 0)
      {
        localReportField = null;
        for (j = i - 1; j >= 0; j--)
        {
          localReportField = (ReportField)paramArrayList.get(j);
          if (!NativeReportManager.this.mFieldMap.containsKey(Integer.valueOf(localReportField.getId())))
            try
            {
              paramArrayList.remove(j);
            }
            catch (Exception localException)
            {
              NativeReportManager.mLog.log(Level.SEVERE, "Error removing nonexistent field from report: " + localException.getMessage(), localException);
            }
        }
      }
      i = paramArrayList.size();
      if (i > 0)
      {
        localReportField = null;
        arrayOfEntryListFieldInfo = new EntryListFieldInfo[i];
        for (j = 0; j < i; j++)
        {
          localReportField = (ReportField)paramArrayList.get(j);
          arrayOfEntryListFieldInfo[j] = new EntryListFieldInfo(localReportField.getId());
        }
      }
      return arrayOfEntryListFieldInfo;
    }

    private SortInfo[] makeSortInfo(ArrayList paramArrayList)
    {
      SortInfo[] arrayOfSortInfo = null;
      int i = paramArrayList.size();
      if (i > 0)
      {
        SortInfo localSortInfo = null;
        for (int k = i - 1; k >= 0; k--)
        {
          localSortInfo = (SortInfo)paramArrayList.get(k);
          if (!NativeReportManager.this.mFieldMap.containsKey(Integer.valueOf(localSortInfo.getFieldID())))
            try
            {
              paramArrayList.remove(k);
            }
            catch (Exception localException)
            {
              NativeReportManager.mLog.log(Level.SEVERE, "Error removing nonexistent field from sort: " + localException.getMessage(), localException);
            }
        }
      }
      i = paramArrayList.size();
      if (i > 0)
      {
        arrayOfSortInfo = new SortInfo[i];
        for (int j = 0; j < i; j++)
          arrayOfSortInfo[j] = ((SortInfo)paramArrayList.get(j));
      }
      return arrayOfSortInfo;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.NativeReportManager
 * JD-Core Version:    0.6.1
 */