package com.remedy.arsys.stubs;

import com.remedy.arsys.log.Log;
import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import org.eclipse.birt.report.utility.ParameterAccessor;

public class SecureCookieFilter
  implements Filter
{
  private static Log MLog = Log.get(10);
  private static String baseURL;

  public void init(FilterConfig paramFilterConfig)
  {
    MLog.warning("SecureCookieProxy: init");
  }

  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain)
    throws ServletException, IOException
  {
    if ((paramServletRequest instanceof HttpServletRequest))
    {
      ServletContext localServletContext = ((HttpServletRequest)paramServletRequest).getSession().getServletContext();
      if ((localServletContext != null) && (baseURL == null))
      {
        Map localMap = ParameterAccessor.initViewerProps(localServletContext, null);
        baseURL = (String)localMap.get("base_url");
        if (baseURL == null)
          baseURL = "";
        MLog.warning("Base_URL is " + baseURL);
      }
    }
    if ((paramServletResponse instanceof HttpServletResponse))
      paramServletResponse = new Response((HttpServletRequest)paramServletRequest, (HttpServletResponse)paramServletResponse);
    paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
  }

  public void destroy()
  {
  }

  public class Response extends HttpServletResponseWrapper
  {
    private String scheme = null;

    public String getScheme()
    {
      return this.scheme;
    }

    public void setScheme(String paramString)
    {
      this.scheme = paramString;
    }

    public Response(HttpServletRequest paramHttpServletResponse, HttpServletResponse arg3)
    {
      super();
      if (paramHttpServletResponse.getScheme().equalsIgnoreCase("https"))
        setScheme("https");
      else if ((SecureCookieFilter.baseURL != null) && (SecureCookieFilter.baseURL.startsWith("https:")))
        setScheme("https");
    }

    public void addCookie(Cookie paramCookie)
    {
      if ((getScheme() != null) && (getScheme().equalsIgnoreCase("https")))
      {
        paramCookie.setSecure(true);
        SecureCookieFilter.MLog.warning("Setting secure cookie");
      }
      super.addCookie(paramCookie);
    }

    public void setHeader(String paramString1, String paramString2)
    {
      if ((getScheme() != null) && (getScheme().equalsIgnoreCase("https")) && (paramString1.equalsIgnoreCase("SET-COOKIE")))
      {
        paramString2 = paramString2 + "Secure;";
        SecureCookieFilter.MLog.warning("Setting secure cookie");
      }
      super.setHeader(paramString1, paramString2);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.SecureCookieFilter
 * JD-Core Version:    0.6.1
 */