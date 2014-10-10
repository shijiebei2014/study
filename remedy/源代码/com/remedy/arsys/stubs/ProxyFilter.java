package com.remedy.arsys.stubs;

import com.remedy.arsys.log.Log;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ProxyFilter
  implements Filter
{
  private static Log MLog = Log.get(10);
  private String mCookiePath;

  public void init(FilterConfig paramFilterConfig)
  {
    MLog.warning("ProxyFilter: init");
    this.mCookiePath = paramFilterConfig.getInitParameter("cookie-path");
  }

  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain)
    throws ServletException, IOException
  {
    if ((paramServletResponse instanceof HttpServletResponse))
      paramServletResponse = new Response((HttpServletRequest)paramServletRequest, (HttpServletResponse)paramServletResponse);
    paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
  }

  public void destroy()
  {
  }

  public class Response extends HttpServletResponseWrapper
  {
    private HttpServletRequest mRequest;

    public Response(HttpServletRequest paramHttpServletResponse, HttpServletResponse arg3)
    {
      super();
      this.mRequest = paramHttpServletResponse;
    }

    public void addCookie(Cookie paramCookie)
    {
      if (ProxyFilter.this.mCookiePath != null)
      {
        ProxyFilter.MLog.warning("ProxyFilter: cookie path was " + paramCookie.getPath() + " now " + ProxyFilter.this.mCookiePath);
        paramCookie.setPath(ProxyFilter.this.mCookiePath);
      }
      super.addCookie(paramCookie);
    }

    public void sendRedirect(String paramString)
      throws IOException
    {
      ProxyFilter.MLog.warning("ProxyFilter: sendRedirect: location=" + paramString);
      if (isCommitted())
        throw new IllegalStateException("ProxyFilter.sendRedirect - Already committed");
      resetBuffer();
      String str1 = this.mRequest.getRequestURI();
      String str2 = this.mRequest.getContextPath();
      ProxyFilter.MLog.fine("ProxyFilter: sendRedirect: origpath=" + str1);
      ProxyFilter.MLog.fine("ProxyFilter: sendRedirect: contextpath=" + str2);
      assert ((str2.length() > 0) && (str2.charAt(0) == '/'));
      int i = str1.indexOf(str2);
      assert (i != -1);
      assert (i + str2.length() < str1.length());
      StringBuilder localStringBuilder = new StringBuilder();
      int j = 0;
      int k = i + str2.length();
      while ((k = str1.indexOf("/", k + 1)) != -1)
      {
        localStringBuilder.append("../");
        j++;
      }
      if (!paramString.startsWith(str2))
      {
        super.sendRedirect(paramString);
        return;
      }
      assert (paramString.startsWith(str2));
      String str3 = paramString.substring(str2.length() + 1);
      localStringBuilder.append(str3);
      ProxyFilter.MLog.fine("ProxyFilter: sendRedirect: up=" + j + " " + localStringBuilder.toString());
      setStatus(302);
      setHeader("Location", localStringBuilder.toString());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ProxyFilter
 * JD-Core Version:    0.6.1
 */