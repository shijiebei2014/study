package com.remedy.arsys.arreport;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.EnumLimit;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.reporting.common.RepServletParms;
import com.remedy.arsys.reporting.common.ReportException;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class ViewNativeReportEmitter
  implements IReportRowEmitter
{
  private RemedyReportDefinition reportDef;
  private ArrayList statsList;
  private EnumLimit mstatusHistoryLimit;
  private ReportUtil repUtil;
  private ReportTransformer rt;
  private NativeReportManager.ReportQuery rq;
  private ARUserPreferences preferences;
  private Form mForm = null;
  private ServerLogin mContext;
  private String mBrowser;
  private String mLanguage = null;
  private String mCountry = null;
  private String mAppName = null;
  private HashMap<String, String> sortFieldMap = null;
  public CompactRecord prevEntry = null;
  private CompactRecord curEntry = null;
  private boolean startedBlock = false;
  private ReportStatistics rStat = null;
  private int[] groupList = null;
  private String mTimeZone = null;
  private String mLocale = null;
  private static Log mLog = Log.get(0);
  private boolean streamCheckDone = false;
  private int minEntriesForStreaming = 0;
  private int maxOnscreenEntries = 0;
  public boolean outputStreamed = false;
  private BufferedOutputStream bOutputStream;

  public ViewNativeReportEmitter(RemedyReportDefinition paramRemedyReportDefinition, ArrayList paramArrayList, EnumLimit paramEnumLimit, ReportUtil paramReportUtil, ReportTransformer paramReportTransformer, ARUserPreferences paramARUserPreferences, RepServletParms paramRepServletParms, NativeReportManager.ReportQuery paramReportQuery, Form paramForm, ServerLogin paramServerLogin, String paramString1, BufferedOutputStream paramBufferedOutputStream, String paramString2)
    throws GoatException, ReportException
  {
    this.reportDef = paramRemedyReportDefinition;
    this.statsList = paramArrayList;
    this.mstatusHistoryLimit = paramEnumLimit;
    this.rq = paramReportQuery;
    this.groupList = paramRemedyReportDefinition.getGroupList();
    this.repUtil = paramReportUtil;
    this.rt = paramReportTransformer;
    this.mForm = paramForm;
    this.mContext = paramServerLogin;
    this.mBrowser = paramString1;
    this.bOutputStream = paramBufferedOutputStream;
    this.preferences = paramARUserPreferences;
    this.mTimeZone = paramRepServletParms.getParameter("TZ");
    this.mLocale = paramRepServletParms.getParameter("LOC");
    setCountryAndLanguage(paramRepServletParms, this.mLocale);
    this.mAppName = paramRepServletParms.getParameter("APP");
    if (paramRemedyReportDefinition.getFormat() == 2)
    {
      ArrayList localArrayList = paramRemedyReportDefinition.parseSort();
      if ((localArrayList != null) && (localArrayList.size() > 0))
      {
        this.sortFieldMap = new HashMap();
        for (int i = 0; i < localArrayList.size(); i++)
          this.sortFieldMap.put("" + ((SortInfo)localArrayList.get(i)).getFieldID(), "");
      }
    }
    if (paramString2.equals("to-screen: "))
    {
      this.minEntriesForStreaming = Configuration.getInstance().getIntProperty("arsystem.min_entries_for_streaming", 0);
      this.maxOnscreenEntries = Configuration.getInstance().getNativeReportOnScreenMaxEntries();
    }
  }

  public void iteratorCallback(Entry[] paramArrayOfEntry, int paramInt)
    throws GoatException, ReportException
  {
    int i = 0;
    Value localValue1 = null;
    Value localValue2 = null;
    Value localValue3 = null;
    DataType localDataType = null;
    if ((!this.streamCheckDone) && (this.minEntriesForStreaming > 0))
    {
      int j = paramInt;
      System.out.println("Total records is:" + j);
      if (j > this.minEntriesForStreaming)
        this.outputStreamed = true;
      this.streamCheckDone = true;
    }
    CompactResult localCompactResult = new CompactResult(paramArrayOfEntry);
    int k = localCompactResult.getNumberofEntries();
    ArrayList localArrayList = null;
    for (int m = 0; m < k; m++)
    {
      if (this.reportDef.getFormat() == 2)
        localArrayList = new ArrayList();
      this.curEntry = localCompactResult.getValueRecord(m);
      if (this.curEntry != null)
      {
        this.startedBlock = false;
        int n = this.statsList.size();
        int i2;
        int i3;
        Object localObject2;
        for (int i1 = 0; i1 < n; i1++)
        {
          this.rStat = ((ReportStatistics)this.statsList.get(i1));
          this.rStat.setStatusHistoryEnumLimit(this.mstatusHistoryLimit);
          i2 = this.rStat.getComputeOn();
          i3 = 0;
          if ((this.groupList != null) && (i2 <= this.groupList.length))
            for (int i4 = 0; i4 < i2; i4++)
            {
              i = this.groupList[i4];
              if ((i > 0) && (this.prevEntry != null))
              {
                localValue1 = this.curEntry.getValue(i);
                localValue2 = this.prevEntry.getValue(i);
                assert (this.repUtil != null);
                String str2 = this.repUtil.getFieldValueString(localValue1, i, -1, -1, 0, null);
                localObject2 = this.repUtil.getFieldValueString(localValue2, i, -1, -1, 0, null);
                if (!str2.equals(localObject2))
                {
                  i3 = 1;
                  break;
                }
              }
            }
          if (i3 != 0)
          {
            if (!this.startedBlock)
            {
              this.startedBlock = true;
              this.rt.doStartStat();
            }
            String str1 = this.repUtil.resolveKeywords(this.rStat.getLabel(), this.prevEntry, null, 1, false, this.mLocale, this.mTimeZone, this.preferences.getDisplayTimeFormat(), this.preferences.getCustomDateFormatStr(), this.preferences.getCustomTimeFormatStr(), this.mBrowser, this.mAppName, this.mContext, this.mForm);
            long l = 0L;
            if ((this.groupList != null) && (this.groupList.length >= i2) && (i2 <= 5) && (this.groupList[(i2 - 1)] != 0))
              l = this.groupList[(i2 - 1)];
            String str3 = this.rStat.getValueString(this.preferences.getDisplayTimeFormat(), this.mLocale, this.mTimeZone, this.preferences.getCustomDateFormatStr(), this.preferences.getCustomTimeFormatStr());
            this.rt.doStat(str1, this.rStat.getLayoutString(), Long.toString(l), str3);
            this.rStat.resetValues();
          }
          try
          {
            this.rStat.accumulate(this.rStat.evalQualification(this.mForm.getName(), this.curEntry.getEntryItems(), this.mLanguage, this.mCountry, this.mTimeZone));
          }
          catch (Exception localException2)
          {
            mLog.log(Level.SEVERE, "Accumulate Statistics Exception: " + localException2.getMessage(), localException2);
          }
        }
        if (this.startedBlock)
        {
          this.rt.doEndStat();
          this.startedBlock = false;
        }
        i1 = 1;
        if (this.rq.getReportFieldsSize() > 0)
        {
          if (this.reportDef.getFormat() != 2)
            this.rt.doStartEntry();
          try
          {
            i2 = this.rq.getReportFieldsSize();
            Object localObject1 = "";
            localObject2 = "";
            for (int i5 = 0; i5 < i2; i5++)
            {
              ReportField localReportField = this.rq.getReportFieldsEntry(i5);
              assert (localReportField != null);
              if (localReportField != null)
              {
                i3 = localReportField.getId();
                try
                {
                  localValue3 = this.curEntry.getValue(i3);
                }
                catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
                {
                  mLog.log(Level.INFO, "No matching fieldId found. Setting the entry value to empty string", localArrayIndexOutOfBoundsException);
                  localValue3 = new Value("");
                }
                String str4 = null;
                if (localValue3 != null)
                {
                  localDataType = localValue3.getDataType();
                  try
                  {
                    String str5 = localDataType.equals(DataType.CURRENCY) ? localReportField.getFieldName() : null;
                    assert (this.repUtil != null);
                    str4 = this.repUtil.getFieldValueString(localValue3, i3, localReportField.getStatHistEnum(), localReportField.getUserOrTime(), 0, str5);
                    if (this.reportDef.getFormat() == 2)
                    {
                      int i6 = 0;
                      if (this.groupList != null)
                        for (int i7 = 0; i7 < this.groupList.length; i7++)
                          if (this.groupList[i7] == i3)
                          {
                            i6 = 1;
                            break;
                          }
                      if ((this.sortFieldMap != null) && (this.sortFieldMap.containsKey("" + i3)) && (i6 != 0))
                        if (((String)this.sortFieldMap.get("" + i3)).equals(str4))
                          str4 = "";
                        else
                          this.sortFieldMap.put("" + i3, str4);
                      localObject2 = str4;
                      if ((i5 == 0) && (((String)localObject2).equals("")))
                        i1 = 0;
                      if ((i1 != 0) || (!((String)localObject1).equals("")) || (!((String)localObject2).equals("")))
                        i1 = 1;
                      localObject1 = localObject2;
                    }
                  }
                  catch (GoatException localGoatException)
                  {
                    mLog.log(Level.SEVERE, localGoatException.getMessage(), localGoatException);
                  }
                }
                if (this.reportDef.getFormat() != 2)
                  this.rt.doEntry(localReportField.getLabel(), String.valueOf(i3), str4 == null ? "" : str4);
                else if (localArrayList != null)
                  localArrayList.add(new String[] { localReportField.getLabel(), String.valueOf(i3), str4 == null ? "" : str4 });
              }
            }
          }
          catch (Exception localException1)
          {
            mLog.log(Level.SEVERE, localException1.getMessage(), localException1);
          }
          finally
          {
            if (this.reportDef.getFormat() != 2)
            {
              this.rt.doEndEntry();
            }
            else if ((i1 != 0) && (localArrayList != null))
            {
              this.rt.doStartEntry();
              int i8 = localArrayList.size();
              for (int i9 = 0; i9 < i8; i9++)
                this.rt.doEntry(((String[])localArrayList.get(i9))[0], ((String[])localArrayList.get(i9))[1], ((String[])localArrayList.get(i9))[2]);
              this.rt.doEndEntry();
              localArrayList = null;
            }
          }
        }
        this.prevEntry = this.curEntry;
      }
    }
    if ((this.bOutputStream != null) && (this.outputStreamed))
    {
      try
      {
        ReportUtil.emitToStream(this.bOutputStream, this.rt.getHtml().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new GoatException(localUnsupportedEncodingException.getMessage());
      }
      this.rt.clear();
    }
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

  public int getEntriesLimit()
  {
    return this.maxOnscreenEntries;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ViewNativeReportEmitter
 * JD-Core Version:    0.6.1
 */