package com.remedy.arsys.arwebreport;

import com.remedy.arsys.config.Configuration;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class BIRTCacheFilter
  implements Filter
{
  FilterConfig filterConfig = null;

  public void init(FilterConfig paramFilterConfig)
  {
    this.filterConfig = paramFilterConfig;
  }

  public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse, FilterChain paramFilterChain)
    throws IOException, ServletException
  {
    int i = Configuration.getInstance().getResourceExpiryInterval();
    if (i <= 0)
      i = 86400;
    ((HttpServletResponse)paramServletResponse).setHeader("Cache-Control", "max-age=" + i);
    paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
  }

  public void destroy()
  {
    this.filterConfig = null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.BIRTCacheFilter
 * JD-Core Version:    0.6.1
 */