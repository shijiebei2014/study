package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.plugincontainer.NoPermissionException;
import com.remedy.arsys.plugincontainer.Plugin;
import com.remedy.arsys.share.HTMLWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PluginServlet extends PluginContainer
{
  public void init(ServletConfig paramServletConfig)
    throws ServletException
  {
    super.init(paramServletConfig);
    PluginFactory.init(paramServletConfig.getServletName(), paramServletConfig.getServletContext());
  }

  protected void postPluginInfo(Plugin paramPlugin, PluginContextImpl paramPluginContextImpl)
    throws IOException, NoPermissionException
  {
    paramPlugin.processRequest(paramPluginContextImpl);
  }

  protected void generateErrorResponse(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, Exception paramException)
    throws IOException
  {
    paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    localPrintWriter.print("<html><body><h1>");
    localPrintWriter.println(HTMLWriter.escape(paramException.getLocalizedMessage()));
    localPrintWriter.println("</h1></body></html>");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PluginServlet
 * JD-Core Version:    0.6.1
 */