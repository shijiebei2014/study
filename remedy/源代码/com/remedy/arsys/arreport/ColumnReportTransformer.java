package com.remedy.arsys.arreport;

import com.remedy.arsys.share.HTMLWriter;

public class ColumnReportTransformer extends ReportTransformer
{
  private int nColumnCount = 0;
  private ReportTransformer.RPTHTMLWriter hw = null;

  public void clear()
  {
    this.hw.clear();
  }

  public void doStartHeader()
  {
    this.hw.openTag("tr").attr("id", "tableHeaderColor").endTag();
  }

  public void doEndHeader()
  {
    this.hw.closeTag("tr");
  }

  public void doHeader(String paramString1, String paramString2)
  {
    this.hw.openTag("th").attr("scope", "col").endTag(false).cdataSpaceEncoded(paramString2).closeTag("th");
    this.nColumnCount += 1;
  }

  public void doStartEntry()
  {
    this.hw.openTag("tr").attr("valign", "top").endTag();
  }

  public void doEndEntry()
  {
    this.hw.closeTag("tr");
  }

  public void doEntry(String paramString1, String paramString2, String paramString3)
  {
    String str = paramString3;
    if (str.length() == 0)
      str = "&nbsp;";
    this.hw.openTag("td").endTag(false).append(str).closeTag("td");
  }

  public void doStartStat()
  {
    this.hw.openTag("tr").attr("valign", "top").endTag();
    this.hw.openTag("td").attr("colspan", this.nColumnCount).endTag();
  }

  public void doEndStat()
  {
    this.hw.closeTag("td").closeTag("tr");
  }

  private void formatStatLabelAndValue(String paramString1, String paramString2)
  {
    this.hw.openTag("b").endTag(false);
    int i = 0;
    String str1 = paramString1;
    i = str1.indexOf("\\n");
    if (i > -1)
    {
      while (i > -1)
      {
        String str2 = str1.substring(0, i);
        str1 = str1.substring(i + 2, str1.length());
        this.hw.cdata(str2).openTag("br").endTag();
        i = str1.indexOf("\\n");
      }
      if (str1.length() > 0)
        this.hw.cdata(str1);
    }
    else
    {
      this.hw.append(paramString1);
    }
    this.hw.append("&nbsp;").cdata(paramString2).closeTag("b").openWholeTag("br");
  }

  public void doStat(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (paramString3.equals("0"))
      this.hw.openTag("hr").attr("size", 1).endTag();
    formatStatLabelAndValue(paramString1, paramString4);
  }

  public void doInit(String paramString1, String paramString2, String paramString3)
  {
    this.hw.openWholeTag("html").openWholeTag("head");
    this.hw.openTag("style").attr("type", "text/css").endTag(false);
    this.hw.cdata("#tableHeaderColor {background-color: #F5F0D3}").closeTag("style");
    this.hw.openTag("title").endTag(false).cdata(paramString1).closeTag("title");
    this.hw.openTag("body").endTag();
    this.hw.cdata(paramString2);
    this.hw.openTag("p").endTag(false).closeTag("p");
    this.hw.openTag("table").attr("cellspacing", 1).attr("cellpadding", 5).attr("border", 1).attr("width", "100%").endTag();
    this.hw.openTag("caption").attr("align", "top").endTag();
    this.hw.openTag("h2").endTag(false).cdata(paramString1).closeTag("h2");
    this.hw.closeTag("caption");
  }

  public int getNColumnCount()
  {
    return this.nColumnCount;
  }

  public void setNColumnCount(int paramInt)
  {
    this.nColumnCount = paramInt;
  }

  public void doEnd(String paramString)
  {
    this.hw.closeTag("table").openTag("p").endTag(false).closeTag("p");
    this.hw.cdata(paramString);
    this.hw.closeTag("body").closeTag("html");
  }

  public String getHtml()
  {
    if (this.hw != null)
      return this.hw.toString();
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ColumnReportTransformer
 * JD-Core Version:    0.6.1
 */