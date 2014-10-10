package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.plugincontainer.NoPermissionException;
import com.remedy.arsys.plugincontainer.Plugin;
import com.remedy.arsys.share.JSWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PluginEventServlet extends PluginContainer
{
  private static final String EVENTTYPE = "eventtype";
  private static final String EVENTDATA = "eventdata";
  private static final Log MLog = Log.get(13);

  protected void postPluginInfo(Plugin paramPlugin, PluginContextImpl paramPluginContextImpl)
    throws IOException, NoPermissionException
  {
    String str1 = paramPluginContextImpl.getRequest().getParameter("eventtype");
    String str2 = paramPluginContextImpl.getRequest().getParameter("eventdata");
    HttpServletResponse localHttpServletResponse = paramPluginContextImpl.getResponse();
    paramPluginContextImpl.resetResponse();
    String str3 = paramPlugin.handleEvent(paramPluginContextImpl, str1, str2);
    localHttpServletResponse.setHeader("Cache-Control", "no-cache");
    localHttpServletResponse.setHeader("Pragma", "no-cache");
    localHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
    localHttpServletResponse.setContentType("text/plain;charset=UTF-8");
    PrintWriter localPrintWriter = localHttpServletResponse.getWriter();
    if (str3 == null)
      str3 = "";
    localPrintWriter.print(str3);
  }

  protected void generateErrorResponse(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, Exception paramException)
    throws IOException
  {
    paramHttpServletResponse.setContentType("text/plain; charset=UTF-8");
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    localPrintWriter.print("alert(\"");
    localPrintWriter.print(JSWriter.escape(paramException.getLocalizedMessage()));
    localPrintWriter.println("\");");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PluginEventServlet
 * JD-Core Version:    0.6.1
 */