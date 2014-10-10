package com.remedy.arsys.stubs;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public final class XSSRequestWrapper extends HttpServletRequestWrapper
{
  private static boolean detectSplChars = false;
  private static Log MLog = Log.get(8);
  private static char quotchar = '\'';
  private static char ltchar = '<';
  private static char gtchar = '>';
  private static String encltchar = "%3C";
  private static String encgtchar = "%3E";
  private static final String FB_PARAMS_URL = "/plugins/Flashboard/params";
  private static final String PLUGINSIGNAL_URL = "/pluginsignal/";
  private static final String PLUGIN_SERVLET_URL = "/plugins";
  private static boolean xmlHttpGET = Configuration.getInstance().isXMLHTTPGET();
  private boolean hasquotchar = false;
  private boolean hasltchar = false;
  private boolean hasgtchar = false;
  private String qs;
  private HashMap pmap;
  private HttpServletRequest httpServletRequest;

  public XSSRequestWrapper(HttpServletRequest paramHttpServletRequest)
  {
    super(paramHttpServletRequest);
    this.httpServletRequest = paramHttpServletRequest;
  }

  public String[] getParameterValues(String paramString)
  {
    String[] arrayOfString1 = super.getParameterValues(paramString);
    if (arrayOfString1 == null)
      return null;
    String str = getQueryString();
    if ((str != null) && (str.indexOf(paramString) != -1))
    {
      int i = arrayOfString1.length;
      String[] arrayOfString2 = new String[i];
      for (int j = 0; j < i; j++)
        arrayOfString2[j] = cleanXSS(arrayOfString1[j]);
      return arrayOfString2;
    }
    return arrayOfString1;
  }

  public String getParameter(String paramString)
  {
    String str1 = super.getParameter(paramString);
    if (str1 == null)
      return null;
    String str2 = null;
    String str3 = getQueryString();
    if ((str3 != null) && (str3.indexOf(paramString) != -1))
      str2 = cleanXSS(str1);
    else
      str2 = str1;
    return str2;
  }

  public Map getParameterMap()
  {
    if (this.pmap != null)
      return this.pmap;
    Map localMap = super.getParameterMap();
    this.pmap = new HashMap();
    Set localSet = localMap.keySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = localMap.get(str);
      String[] arrayOfString = (String[])localObject;
      for (int i = 0; i < arrayOfString.length; i++)
        arrayOfString[i] = cleanXSS(arrayOfString[i]);
      this.pmap.put(str, arrayOfString);
    }
    return this.pmap;
  }

  public String getQueryString()
  {
    if (this.qs != null)
      return this.qs;
    this.qs = cleanXSS(((HttpServletRequest)super.getRequest()).getQueryString());
    return this.qs;
  }

  private String cleanXSS(String paramString)
  {
    if (paramString == null)
      return null;
    if (detectSplChars)
    {
      if (paramString.indexOf(quotchar) != -1)
        this.hasquotchar = true;
      if ((paramString.indexOf(ltchar) != -1) || (paramString.indexOf(encltchar) != -1))
        this.hasltchar = true;
      if ((paramString.indexOf(gtchar) != -1) || (paramString.indexOf(encgtchar) != -1))
        this.hasgtchar = true;
      if ((this.hasquotchar) || (this.hasltchar) || (this.hasgtchar))
      {
        System.out.println("URL:" + this.httpServletRequest.getRequestURL() + " qs=" + this.httpServletRequest.getQueryString() + " quot=" + this.hasquotchar + " lt=" + this.hasltchar + " gt=" + this.hasgtchar);
        MLog.log(Level.WARNING, "URL:" + this.httpServletRequest.getRequestURL() + " qs=" + this.httpServletRequest.getQueryString() + " quot=" + this.hasquotchar + " lt=" + this.hasltchar + " gt=" + this.hasgtchar + "\n");
      }
    }
    if (((this.httpServletRequest.getRequestURL().indexOf("/plugins/Flashboard/params") != -1) || (this.httpServletRequest.getRequestURL().indexOf("/pluginsignal/") != -1)) && (!xmlHttpGET))
    {
      String str = paramString;
      paramString = paramString.replaceAll("\\$quot", "'");
      paramString = paramString.replaceAll("%24quot", "'");
      paramString = paramString.replaceAll("\\$lt", "<");
      paramString = paramString.replaceAll("%24lt", "<");
      paramString = paramString.replaceAll("\\$gt", ">");
      paramString = paramString.replaceAll("%24gt", ">");
      if (!paramString.equals(str))
        MLog.log(Level.FINE, "PREPL: URL=" + this.httpServletRequest.getRequestURL() + " qs=" + this.httpServletRequest.getQueryString() + " value=" + paramString + "\n");
    }
    return paramString;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.XSSRequestWrapper
 * JD-Core Version:    0.6.1
 */