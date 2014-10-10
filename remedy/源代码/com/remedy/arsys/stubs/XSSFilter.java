package com.remedy.arsys.stubs;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public final class XSSFilter
  implements Filter
{
  private FilterConfig filterConfig = null;

  public void init(FilterConfig paramFilterConfig)
    throws ServletException
  {
    this.filterConfig = paramFilterConfig;
  }

  public void destroy()
  {
    this.filterConfig = null;
  }

  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain)
    throws IOException, ServletException
  {
    paramFilterChain.doFilter(new XSSRequestWrapper((HttpServletRequest)paramServletRequest), paramServletResponse);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.XSSFilter
 * JD-Core Version:    0.6.1
 */