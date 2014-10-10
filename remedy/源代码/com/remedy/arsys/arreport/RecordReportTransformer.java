package com.remedy.arsys.arreport;

import com.remedy.arsys.share.HTMLWriter;

public class RecordReportTransformer extends ReportTransformer
{
  private ReportTransformer.RPTHTMLWriter hw = null;

  public void clear()
  {
    this.hw.clear();
  }

  public void doInit(String paramString1, String paramString2, String paramString3)
  {
    this.hw.openWholeTag("html").openWholeTag("head");
    this.hw.openTag("style").attr("type", "text/css").endTag(false);
    this.hw.cdata("#statColor {background-color: #F5F0D3}").closeTag("style");
    this.hw.openTag("title").endTag(false).cdata(paramString1).closeTag("title");
    this.hw.openTag("body").endTag();
    this.hw.cdata(paramString2);
    this.hw.openTag("div").attr("align", "center").endTag();
    this.hw.openTag("h2").endTag(false).cdata(paramString1).closeTag("h2").closeTag("div");
    this.hw.openTag("p").endTag(false).closeTag("p");
    this.hw.openTag("hr").attr("size", 4).attr("noshade").endTag();
  }

  public void doEnd(String paramString)
  {
    this.hw.openTag("p").endTag(false).closeTag("p");
    this.hw.cdata(paramString);
    this.hw.closeTag("body").closeTag("html");
  }

  public void doStartHeader()
  {
  }

  public void doEndHeader()
  {
  }

  public void doHeader(String paramString1, String paramString2)
  {
  }

  public void doStartEntry()
  {
    this.hw.openTag("table").attr("cellpadding", 1).attr("border", 0).endTag();
  }

  public void doEndEntry()
  {
    this.hw.closeTag("table");
    this.hw.openTag("hr").attr("size", 1).endTag();
  }

  public void doEntry(String paramString1, String paramString2, String paramString3)
  {
    this.hw.openTag("tr").attr("valign", "top").endTag();
    this.hw.openTag("td").attr("align", "right").endTag();
    this.hw.openTag("b").endTag(false).cdataSpaceEncoded(paramString1).closeTag("b").closeTag("td");
    this.hw.openTag("td").endTag(false).append("&nbsp;").append(paramString3).closeTag("td");
    this.hw.closeTag("tr");
  }

  public void doStartStat()
  {
  }

  public void doEndStat()
  {
    this.hw.openTag("hr").attr("size", 4).attr("noshade").endTag();
  }

  private void formatStatLabelAndValue(String paramString1, String paramString2)
  {
    this.hw.openTag("b").endTag(false).openTag("i").endTag(false);
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
    this.hw.append("&nbsp;").cdata(paramString2).closeTag("i").closeTag("b");
  }

  public void doStat(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (paramString3.equals("0"))
    {
      this.hw.openTag("hr").attr("size", 4).attr("noshade").endTag();
      this.hw.openTag("p").endTag(false).closeTag("p");
      formatStatLabelAndValue(paramString1, paramString4);
    }
    else if (paramString2.equals("multi-line"))
    {
      formatStatLabelAndValue(paramString1, paramString4);
      this.hw.openTag("br").endTag(false);
    }
    else if (paramString2.equals("one-line"))
    {
      formatStatLabelAndValue(paramString1, paramString4);
      this.hw.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    }
  }

  public String getHtml()
  {
    if (this.hw != null)
      return this.hw.toString();
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.RecordReportTransformer
 * JD-Core Version:    0.6.1
 */