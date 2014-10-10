package com.remedy.arsys.support;

import javax.servlet.http.HttpServletRequest;

public class BrowserType
{
  public static final int BROWSER_TYPE_IE = 0;
  public static final int BROWSER_TYPE_MOZILLA = 1;
  public static final int BROWSER_TYPE_SAFARI = 2;
  public static final String BROWSER_STRING_MSIE = "MSIE";
  public static final String BROWSER_STRING_MOZILLA = "Gecko";
  public static final String BROWSER_STRING_SAFARI = "AppleWebKit";
  public static final String BROWSER_ABBREV_IE = "ie";
  public static final String BROWSER_ABBREV_MOZILLA = "moz";
  public static final String BROWSER_ABBREV_SAFARI = "saf";
  public static final BrowserType IE = new BrowserType("MSIE", "ie", 0);
  public static final BrowserType MOZ = new BrowserType("Gecko", "moz", 1);
  public static final BrowserType SAF = new BrowserType("AppleWebKit", "saf", 2);
  public static final BrowserType[] BROWSER_TYPES = { IE, MOZ, SAF };
  private final String agent;
  private final String abbrev;
  private final int id;

  private BrowserType(String paramString1, String paramString2, int paramInt)
  {
    this.agent = paramString1;
    this.abbrev = paramString2;
    this.id = paramInt;
  }

  public static BrowserType getTypeFromRequest(HttpServletRequest paramHttpServletRequest)
  {
    String str = paramHttpServletRequest.getHeader("User-Agent");
    if (str.indexOf("AppleWebKit") >= 0)
      return SAF;
    if (str.indexOf("Gecko") >= 0)
      return MOZ;
    if (str.indexOf("MSIE") >= 0)
      return IE;
    return IE;
  }

  public static BrowserType getTypeFromRequest(String paramString)
  {
    if (paramString.indexOf("AppleWebKit") >= 0)
      return SAF;
    if (paramString.indexOf("Gecko") >= 0)
      return MOZ;
    if (paramString.indexOf("MSIE") >= 0)
      return IE;
    return IE;
  }

  public final String getAgent()
  {
    return this.agent;
  }

  public final String getAbbrev()
  {
    return this.abbrev;
  }

  public final String toString()
  {
    return this.agent;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.BrowserType
 * JD-Core Version:    0.6.1
 */