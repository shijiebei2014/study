package com.remedy.arsys.arreport;

import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.SortInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class RemedyReportDefinition
{
  protected static final int REPORT_ATTR_LAYOUT = 1;
  protected static final int REPORT_ATTR_IDLIST = 2;
  protected static final int REPORT_ATTR_NAME = 3;
  protected static final int REPORT_ATTR_TITLE = 4;
  protected static final int REPORT_ATTR_HEADER = 5;
  protected static final int REPORT_ATTR_FOOTER = 6;
  protected static final int REPORT_ATTR_COL_SEP = 13;
  protected static final String REPORT_BEGIN_KEY = "Report: ";
  protected static final String REPORT_END_KEY = "end";
  public static final int REPORT_FORMAT_RECORD = 1;
  public static final int REPORT_FORMAT_COLUMN = 2;
  public static final int REPORT_FORMAT_COMPRESSED = 3;
  public static final int REPORT_FORMAT_CSV = 4;
  public static final int REPORT_FORMAT_AREXPORT = 5;
  public static final int REPORT_FORMAT_XML = 6;
  protected static final String STATS_KEY = "Statistics: ";
  protected static final int AR_SORT_ASCENDING = 1;
  protected static final int AR_SORT_DESCENDING = 2;
  protected static final String SORT_KEY = "report-sort: ";
  protected static final String QUERY_KEY = "Query: ";
  private int m_reportFormat = 1;
  private String m_query = null;
  private final int[] m_groupList = new int[5];
  private String m_title = "";
  private String m_header = "";
  private String m_footer = "";
  private String m_colSeparator = ",";
  private ArrayList m_fieldList = null;
  private ArrayList m_sortInfo = null;
  private ArrayList m_statsInfo = null;
  private ArrayList m_reportDef = null;

  public RemedyReportDefinition(String paramString1, String paramString2)
    throws ReportException
  {
    readDef(paramString1, paramString2);
  }

  private void readDef(String paramString1, String paramString2)
    throws ReportException
  {
    FileInputStream localFileInputStream = null;
    InputStreamReader localInputStreamReader = null;
    BufferedReader localBufferedReader = null;
    try
    {
      File localFile = new File(paramString1);
      localFileInputStream = new FileInputStream(localFile);
      if (paramString2.equals("euc_jp"))
        paramString2 = "windows-31j";
      localInputStreamReader = new InputStreamReader(localFileInputStream, paramString2);
      localBufferedReader = new BufferedReader(localInputStreamReader);
      this.m_reportDef = new ArrayList(10);
      int i = 0;
      String str = null;
      while (i == 0)
      {
        str = localBufferedReader.readLine();
        if (str != null)
          this.m_reportDef.add(str);
        else
          i = 1;
      }
    }
    catch (IOException localIOException2)
    {
      throw new ReportException(9236, paramString1);
    }
    finally
    {
      try
      {
        if (localBufferedReader != null)
          localBufferedReader.close();
        if (localInputStreamReader != null)
          localInputStreamReader.close();
        if (localFileInputStream != null)
          localFileInputStream.close();
      }
      catch (IOException localIOException3)
      {
        throw new ReportException(9236, paramString1);
      }
    }
  }

  public String parseQuery()
  {
    if (this.m_query == null)
    {
      this.m_query = getKeyString("Query: ");
      if (this.m_query != null)
      {
        this.m_query = this.m_query.substring("Query: ".length());
        this.m_query = this.m_query.trim();
      }
      else
      {
        this.m_query = "";
      }
    }
    return this.m_query;
  }

  public ArrayList parseReportFieldAttr()
    throws GoatException, ReportException
  {
    if (this.m_fieldList != null)
      return this.m_fieldList;
    this.m_fieldList = new ArrayList();
    String str1 = getKeyString("Report: ");
    ArrayList localArrayList = new ArrayList();
    String str2 = null;
    int i = 0;
    String str3 = null;
    String str4 = null;
    if (str1 == null)
      throw new GoatException(9237);
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, "\001");
    while (localStringTokenizer.hasMoreTokens())
      localArrayList.add(localStringTokenizer.nextToken());
    try
    {
      int j = localArrayList.size();
      for (int k = 0; k < j; k++)
      {
        str2 = (String)localArrayList.get(k);
        if (k != 0)
        {
          i = str2.indexOf("=");
          if (i >= 0)
          {
            str3 = str2.substring(0, i).trim();
            if (str2.length() - 1 != i)
              str4 = str2.substring(i + 1);
            else
              str4 = null;
            if (str4 != null)
              switch (Integer.parseInt(str3))
              {
              case 1:
                this.m_reportFormat = Integer.parseInt(str4);
                break;
              case 2:
                parseFieldList(str4);
                break;
              case 4:
                this.m_title = str4;
                break;
              case 5:
                this.m_header = str4;
                break;
              case 6:
                this.m_footer = str4;
                break;
              case 13:
                this.m_colSeparator = str4;
              case 3:
              case 7:
              case 8:
              case 9:
              case 10:
              case 11:
              case 12:
              }
          }
        }
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new ReportException(9231);
    }
    return this.m_fieldList;
  }

  private void parseFieldList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\002");
    while (localStringTokenizer.hasMoreTokens())
      localArrayList.add(localStringTokenizer.nextToken());
    int i = localArrayList.size();
    for (int j = 0; j < i; j++)
      parseField((String)localArrayList.get(j));
  }

  private void parseField(String paramString)
  {
    ReportField localReportField = new ReportField();
    ArrayList localArrayList1 = new ArrayList();
    String str = null;
    paramString = insertDummyString(paramString, '\003', '\004');
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, "\003");
    while (localStringTokenizer1.hasMoreTokens())
      localArrayList1.add(localStringTokenizer1.nextToken());
    int i = localArrayList1.size();
    for (int j = 0; j < i; j++)
    {
      str = (String)localArrayList1.get(j);
      switch (j)
      {
      case 0:
        localReportField.setId(str);
        break;
      case 1:
        if (!str.equals("\004"))
          if ((localReportField.getId() == CoreFieldId.StatusHistory.getFieldId()) && (str.length() > 0))
          {
            str = str.trim();
            ArrayList localArrayList2 = new ArrayList();
            StringTokenizer localStringTokenizer2 = new StringTokenizer(str, " ");
            while (localStringTokenizer2.hasMoreTokens())
              localArrayList2.add(localStringTokenizer2.nextToken());
            if (localArrayList2.size() != 2)
            {
              localReportField.setFieldName(str);
            }
            else
            {
              localReportField.setStatHistEnum(Integer.parseInt((String)localArrayList2.get(0)));
              localReportField.setStatHistUserTime(Integer.parseInt((String)localArrayList2.get(1)));
            }
          }
          else
          {
            localReportField.setFieldName(str);
          }
        break;
      case 2:
        if (!str.equals("\004"))
          localReportField.setLabel(str);
        else
          localReportField.setLabel("");
        break;
      }
    }
    this.m_fieldList.add(localReportField);
  }

  public ArrayList parseSort()
    throws GoatException
  {
    if (this.m_sortInfo != null)
      return this.m_sortInfo;
    this.m_sortInfo = new ArrayList();
    String str = getKeyString("report-sort: ");
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (str != null)
    {
      int j = "report-sort: ".length();
      str = str.substring(j);
      StringTokenizer localStringTokenizer = new StringTokenizer(str, "|");
      while (localStringTokenizer.hasMoreTokens())
        localArrayList.add(localStringTokenizer.nextToken());
      int k = localArrayList.size();
      for (int m = 0; m < k; m++)
        switch (m)
        {
        case 0:
          i = Integer.parseInt((String)localArrayList.get(m));
          break;
        case 1:
          break;
        default:
          parseSortField((String)localArrayList.get(m));
        }
    }
    switch (i)
    {
    case 5:
      this.m_groupList[4] = ((SortInfo)this.m_sortInfo.get(4)).getFieldID();
    case 4:
      this.m_groupList[3] = ((SortInfo)this.m_sortInfo.get(3)).getFieldID();
    case 3:
      this.m_groupList[2] = ((SortInfo)this.m_sortInfo.get(2)).getFieldID();
    case 2:
      this.m_groupList[1] = ((SortInfo)this.m_sortInfo.get(1)).getFieldID();
    case 1:
      this.m_groupList[0] = ((SortInfo)this.m_sortInfo.get(0)).getFieldID();
      break;
    case 0:
    }
    return this.m_sortInfo;
  }

  private void parseSortField(String paramString)
    throws GoatException
  {
    String str1 = null;
    String str2 = null;
    SortInfo localSortInfo = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\\");
    for (int j = 0; localStringTokenizer.hasMoreTokens(); j++)
      if (j == 0)
        str1 = localStringTokenizer.nextToken();
      else
        str2 = localStringTokenizer.nextToken();
    if (j < 2)
      throw new GoatException(9238);
    try
    {
      int i = (int)Long.parseLong(str1);
      int k = 1;
      try
      {
        int m = Integer.parseInt(str2);
        if (m == 2)
          k = 2;
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
      localSortInfo = new SortInfo(i, k);
      this.m_sortInfo.add(localSortInfo);
    }
    catch (Exception localException)
    {
    }
  }

  public ArrayList parseStatistics()
    throws GoatException
  {
    if (this.m_statsInfo != null)
      return this.m_statsInfo;
    this.m_statsInfo = new ArrayList();
    String str = getKeyString("Statistics: ");
    ArrayList localArrayList = new ArrayList();
    if (str != null)
    {
      int i = "Statistics: ".length();
      str = str.substring(i);
      StringTokenizer localStringTokenizer = new StringTokenizer(str, "\001");
      while (localStringTokenizer.hasMoreTokens())
        localArrayList.add(localStringTokenizer.nextToken());
      int j = localArrayList.size();
      for (int k = 0; k < j; k++)
        parseStatProp((String)localArrayList.get(k));
    }
    return this.m_statsInfo;
  }

  private void parseStatProp(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    ReportStatistics localReportStatistics = new ReportStatistics();
    paramString = insertDummyString(paramString, '\002', '\004');
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "\002");
    while (localStringTokenizer.hasMoreTokens())
      localArrayList.add(localStringTokenizer.nextToken());
    int i = localArrayList.size();
    for (int j = 0; j < i; j++)
      switch (j)
      {
      case 0:
        localReportStatistics.setOp(Integer.parseInt((String)localArrayList.get(j)));
        break;
      case 1:
        if (!((String)localArrayList.get(j)).equals("\004"))
          if ((localReportStatistics.getOp() == 1) && (((String)localArrayList.get(j)).length() == 0))
            localReportStatistics.setExpression(null);
          else
            localReportStatistics.setExpression((String)localArrayList.get(j));
        break;
      case 2:
        if (!((String)localArrayList.get(j)).equals("\004"))
          localReportStatistics.setLabel((String)localArrayList.get(j));
        break;
      case 3:
        localReportStatistics.setLayout(Integer.parseInt((String)localArrayList.get(j)));
        break;
      case 4:
        localReportStatistics.setComputeOn(Integer.parseInt((String)localArrayList.get(j)));
      }
    this.m_statsInfo.add(localReportStatistics);
  }

  private String getKeyString(String paramString)
  {
    if (this.m_reportDef == null)
      return null;
    String str = null;
    int i = this.m_reportDef.size();
    for (int j = 0; j < i; j++)
    {
      str = (String)this.m_reportDef.get(j);
      if ((str != null) && (str.startsWith(paramString)))
        return str;
    }
    return null;
  }

  protected String insertDummyString(String paramString, char paramChar1, char paramChar2)
  {
    StringBuilder localStringBuilder1 = new StringBuilder(paramString);
    StringBuilder localStringBuilder2 = new StringBuilder();
    for (int i = 0; i < localStringBuilder1.length(); i++)
    {
      localStringBuilder2.append(localStringBuilder1.charAt(i));
      if ((localStringBuilder1.charAt(i) == paramChar1) && (i + 1 != localStringBuilder1.length()) && (localStringBuilder1.charAt(i + 1) == paramChar1))
        localStringBuilder2.append(paramChar2);
    }
    return localStringBuilder2.toString();
  }

  public int getFormat()
    throws GoatException, ReportException
  {
    if (this.m_fieldList == null)
      parseReportFieldAttr();
    return this.m_reportFormat;
  }

  public int[] getGroupList()
    throws GoatException
  {
    if (this.m_sortInfo == null)
      parseSort();
    return this.m_groupList;
  }

  public String getTitle()
    throws GoatException, ReportException
  {
    if (this.m_fieldList == null)
      parseReportFieldAttr();
    return this.m_title;
  }

  public String getHeader()
    throws GoatException, ReportException
  {
    if (this.m_fieldList == null)
      parseReportFieldAttr();
    return this.m_header;
  }

  public String getColSeparator()
    throws GoatException, ReportException
  {
    if (this.m_fieldList == null)
      parseReportFieldAttr();
    return this.m_colSeparator;
  }

  public String getFooter()
    throws GoatException, ReportException
  {
    if (this.m_fieldList == null)
      parseReportFieldAttr();
    return this.m_footer;
  }

  public void setSortInfo(ArrayList paramArrayList)
  {
    this.m_sortInfo = paramArrayList;
  }

  public String getReportName()
  {
    return (String)this.m_reportDef.get(0);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.RemedyReportDefinition
 * JD-Core Version:    0.6.1
 */