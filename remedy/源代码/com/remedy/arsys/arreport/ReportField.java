package com.remedy.arsys.arreport;

public class ReportField
{
  private int m_fid;
  private String m_label = null;
  private String m_fName = null;
  private int m_StatHistEnum = -1;
  private int m_StatHistUserTime = -1;

  public void setId(String paramString)
  {
    this.m_fid = (int)Long.parseLong(paramString);
  }

  public void setLabel(String paramString)
  {
    this.m_label = paramString;
  }

  public void setFieldName(String paramString)
  {
    this.m_fName = paramString;
  }

  public void setStatHistEnum(int paramInt)
  {
    this.m_StatHistEnum = paramInt;
  }

  public void setStatHistUserTime(int paramInt)
  {
    this.m_StatHistUserTime = paramInt;
  }

  public int getId()
  {
    return this.m_fid;
  }

  public String getLabel()
  {
    return this.m_label;
  }

  public String getFieldName()
  {
    return this.m_fName;
  }

  public int getStatHistEnum()
  {
    return this.m_StatHistEnum;
  }

  public int getUserOrTime()
  {
    return this.m_StatHistUserTime;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ReportField
 * JD-Core Version:    0.6.1
 */