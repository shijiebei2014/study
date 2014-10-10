package com.remedy.arsys.goat;

import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class LocaleContentHandler extends DefaultHandler
{
  private static final String CULTURE = "Culture";
  private static final String NAME = "Name";
  private static final String SHORTDATEFORMAT = "ShortDateFormat";
  private static final String SHORTTIMEFORMAT = "ShortTimeFormat";
  private static final String LONGDATEFORMAT = "LongDateFormat";
  private static final String LONGTIMEFORMAT = "LongTimeFormat";
  private HashMap cultures;
  private CultureInfo culture;
  private boolean isName;
  private boolean isShortDateFormat;
  private boolean isShortTimeFormat;
  private boolean isLongDateFormat;
  private boolean isLongTimeFormat;

  public HashMap getCultures()
  {
    return this.cultures;
  }

  public void startDocument()
  {
    this.cultures = new HashMap(100);
  }

  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
  {
    if (paramString3.equals("Name"))
      this.isName = true;
    else if (paramString3.equals("ShortDateFormat"))
      this.isShortDateFormat = true;
    else if (paramString3.equals("LongDateFormat"))
      this.isLongDateFormat = true;
    else if (paramString3.equals("ShortTimeFormat"))
      this.isShortTimeFormat = true;
    else if (paramString3.equals("LongTimeFormat"))
      this.isLongTimeFormat = true;
  }

  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    String str;
    if (this.isName)
    {
      str = new String(paramArrayOfChar, paramInt1, paramInt2);
      str = str.replace('-', '_');
      this.culture = new CultureInfo();
      this.cultures.put(str, this.culture);
      this.isName = false;
    }
    else if (this.isShortDateFormat)
    {
      str = new String(paramArrayOfChar, paramInt1, paramInt2);
      this.culture.setShortDate(str);
      this.isShortDateFormat = false;
    }
    else if (this.isLongDateFormat)
    {
      str = new String(paramArrayOfChar, paramInt1, paramInt2);
      str = str.replaceFirst("d{4}+", "EEEE");
      this.culture.setLongDate(str);
      this.isLongDateFormat = false;
    }
    else if (this.isShortTimeFormat)
    {
      str = new String(paramArrayOfChar, paramInt1, paramInt2);
      str = str.replace('t', 'a');
      this.culture.setShortTime(str);
      this.isShortTimeFormat = false;
    }
    else if (this.isLongTimeFormat)
    {
      str = new String(paramArrayOfChar, paramInt1, paramInt2);
      str = str.replace('t', 'a');
      this.culture.setLongTime(str);
      this.isLongTimeFormat = false;
    }
  }

  public static class CultureInfo
  {
    private String shortDateFormat;
    private String shortTimeFormat;
    private String longDateFormat;
    private String longTimeFormat;

    private void setShortDate(String paramString)
    {
      this.shortDateFormat = paramString;
    }

    private void setLongDate(String paramString)
    {
      this.longDateFormat = paramString;
    }

    private void setShortTime(String paramString)
    {
      this.shortTimeFormat = paramString;
    }

    private void setLongTime(String paramString)
    {
      this.longTimeFormat = paramString;
    }

    public String getShortDate()
    {
      return this.shortDateFormat;
    }

    public String getLongDate()
    {
      return this.longDateFormat;
    }

    public String getShortTime()
    {
      return this.shortTimeFormat;
    }

    public String getLongTime()
    {
      return this.longTimeFormat;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.LocaleContentHandler
 * JD-Core Version:    0.6.1
 */