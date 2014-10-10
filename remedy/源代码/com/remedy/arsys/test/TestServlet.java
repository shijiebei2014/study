package com.remedy.arsys.test;

import com.remedy.arsys.config.ConfigProperties;
import com.remedy.arsys.config.Configuration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TestServlet extends HttpServlet
{
  public void init()
    throws ServletException
  {
    log("TestServlet init() method called.");
    Enumeration localEnumeration = getServletConfig().getInitParameterNames();
    if (localEnumeration != null)
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        String str2 = getServletConfig().getInitParameter(str1);
        log("TestServlet init argument: [" + str1 + "] = [" + str2 + "]");
      }
    log("TestServlet has no init arguments");
  }

  public void service(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    Enumeration localEnumeration = null;
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    localPrintWriter.println("<html><head><title>Goat TestServlet - Output</title>");
    localPrintWriter.println("<style>* {font-family: sans-serif} table {width:100%; border: 1px solid black; border-collapse:collapse;} td {border:1px solid black; padding:2px}</style>");
    localPrintWriter.println("</head><body>");
    localPrintWriter.println("<h1>TestServlet Output</h1>");
    localPrintWriter.println("<p><b>Here is the request information:</b><p><table>");
    localPrintWriter.println("<tr><td><b>Method</b></td><td>" + paramHttpServletRequest.getMethod() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>URI</b></td><td>" + paramHttpServletRequest.getRequestURI() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>URL</b></td><td>" + paramHttpServletRequest.getRequestURL() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Protocol</b></td><td>" + paramHttpServletRequest.getProtocol() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Servlet Path</b></td><td>" + paramHttpServletRequest.getServletPath() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Path Info</b></td><td>" + paramHttpServletRequest.getPathInfo() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Path Translated</b></td><td>" + paramHttpServletRequest.getPathTranslated() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Query String</b></td><td>" + paramHttpServletRequest.getQueryString() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Scheme</b></td><td>" + paramHttpServletRequest.getScheme() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Content Length</b></td><td>" + paramHttpServletRequest.getContentLength() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Content Type</b></td><td>" + paramHttpServletRequest.getContentType() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Char Encoding</b></td><td>" + paramHttpServletRequest.getCharacterEncoding() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Server Name</b></td><td>" + paramHttpServletRequest.getServerName() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Server Port</b></td><td>" + paramHttpServletRequest.getServerPort() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Remote Addr</b></td><td>" + paramHttpServletRequest.getRemoteAddr() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Remote User</b></td><td>" + paramHttpServletRequest.getRemoteUser() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Auth Type</b></td><td>" + paramHttpServletRequest.getAuthType() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Locale</b></td><td>" + paramHttpServletRequest.getLocale() + "</td></tr>");
    localPrintWriter.println("</table>");
    Properties localProperties = System.getProperties();
    Iterator localIterator = localProperties.keySet().iterator();
    localPrintWriter.println("<p><b>System Properties</b><p><table>");
    while (localIterator.hasNext())
    {
      localObject1 = (String)localIterator.next();
      localPrintWriter.println("<tr><td><b>" + (String)localObject1 + "</b></td><td>" + localProperties.getProperty((String)localObject1) + "</td></tr>");
    }
    localPrintWriter.println("</table>");
    Object localObject1 = Configuration.getInstance().getProperties();
    localIterator = ((ConfigProperties)localObject1).keySet().iterator();
    localPrintWriter.println("<p><b>Configuration Properties</b><p><table>");
    while (localIterator.hasNext())
    {
      localObject2 = (String)localIterator.next();
      localPrintWriter.println("<tr><td><b>" + (String)localObject2 + "</b></td><td>" + ((ConfigProperties)localObject1).get((String)localObject2) + "</td></tr>");
    }
    localPrintWriter.println("</table>");
    localEnumeration = paramHttpServletRequest.getHeaderNames();
    if ((localEnumeration != null) && (localEnumeration.hasMoreElements()))
    {
      localPrintWriter.println("<p><b>Here are the request headers:</b><p><table>");
      while (localEnumeration.hasMoreElements())
      {
        localObject2 = (String)localEnumeration.nextElement();
        localPrintWriter.println("<tr><td><b>" + (String)localObject2 + "</b></td><td>" + paramHttpServletRequest.getHeader((String)localObject2) + "</td></tr>");
      }
      localPrintWriter.println("</table>");
    }
    else
    {
      localPrintWriter.println("<p><b>There are no request headers.</b>");
    }
    localEnumeration = paramHttpServletRequest.getParameterNames();
    Object localObject3;
    if (localEnumeration.hasMoreElements())
    {
      localPrintWriter.println("<p><b>Here are the request parameters:</b><p><table>");
      while (localEnumeration.hasMoreElements())
      {
        localObject2 = (String)localEnumeration.nextElement();
        localObject3 = (String[])paramHttpServletRequest.getParameterValues((String)localObject2);
        for (int i = 0; i < localObject3.length; i++)
          localPrintWriter.println("<tr><td><b>" + (String)localObject2 + "</b></td><td>" + localObject3[i] + "</td></tr>");
      }
      localPrintWriter.println("</table>");
    }
    else
    {
      localPrintWriter.println("<p><b>There are no request parameters.</b>");
    }
    localEnumeration = paramHttpServletRequest.getAttributeNames();
    if (localEnumeration.hasMoreElements())
    {
      localPrintWriter.println("<p><b>Here are the request attributes:</b><p><table>");
      while (localEnumeration.hasMoreElements())
      {
        localObject2 = (String)localEnumeration.nextElement();
        localPrintWriter.println("<tr><td><b>" + (String)localObject2 + "</b></td><td>" + paramHttpServletRequest.getAttribute((String)localObject2) + "</td></tr>");
      }
      localPrintWriter.println("</table>");
    }
    else
    {
      localPrintWriter.println("<p><b>There are no request attributes.</b>");
    }
    Object localObject2 = paramHttpServletRequest.getSession(false);
    if (localObject2 != null)
    {
      localEnumeration = ((HttpSession)localObject2).getAttributeNames();
      if (localEnumeration.hasMoreElements())
      {
        localPrintWriter.println("<p><b>Here are the session attributes:</b><p><table>");
        while (localEnumeration.hasMoreElements())
        {
          localObject3 = (String)localEnumeration.nextElement();
          localPrintWriter.println("<tr><td><b>" + (String)localObject3 + "</b></td><td>" + ((HttpSession)localObject2).getAttribute((String)localObject3) + "</td></tr>");
        }
        localPrintWriter.println("</table>");
      }
      else
      {
        localPrintWriter.println("<p><b>There are no session attributes.</b>");
      }
    }
    else
    {
      localPrintWriter.println("<p><b>There is no session.</b>");
    }
    localPrintWriter.println("<p><b>Here is information from the Servlet Context:</b><p><table>");
    localPrintWriter.println("<tr><td><b>Servlet API Major Version</b></td><td>" + getServletContext().getMajorVersion() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Servlet API Minor Version</b></td><td>" + getServletContext().getMinorVersion() + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Mime Type for *.hqx Files</b></td><td>" + getServletContext().getMimeType("file.hqx") + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Root Document Directory</b></td><td>" + getServletContext().getRealPath("/") + "</td></tr>");
    localPrintWriter.println("<tr><td><b>Server Info</b></td><td>" + getServletContext().getServerInfo() + "</td></tr>");
    localPrintWriter.println("</table>");
    localPrintWriter.println("</center></body></html>");
  }

  public String getServletInfo()
  {
    return "TestServlet returns info from the request and servlet context.";
  }

  public void destroy()
  {
    super.destroy();
    log("TestServlet destroy() method called.");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.test.TestServlet
 * JD-Core Version:    0.6.1
 */