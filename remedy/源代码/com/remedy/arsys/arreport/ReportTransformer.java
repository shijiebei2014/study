package com.remedy.arsys.arreport;

import com.remedy.arsys.share.HTMLWriter;

public abstract class ReportTransformer
{
  public abstract void doInit(String paramString1, String paramString2, String paramString3);

  public abstract void doEnd(String paramString);

  public abstract void doStartHeader();

  public abstract void doEndHeader();

  public abstract void doHeader(String paramString1, String paramString2);

  public abstract void doStartEntry();

  public abstract void doEndEntry();

  public abstract void doEntry(String paramString1, String paramString2, String paramString3);

  public abstract void doStartStat();

  public abstract void doEndStat();

  public abstract void doStat(String paramString1, String paramString2, String paramString3, String paramString4);

  public abstract String getHtml();

  public abstract void clear();

  protected static class RPTHTMLWriter extends HTMLWriter
  {
    RPTHTMLWriter()
    {
      this.mNeedIndent = false;
      this.mStripWhitespace = true;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ReportTransformer
 * JD-Core Version:    0.6.1
 */