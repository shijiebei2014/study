package com.remedy.arsys.ws.services;

import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.ContainerType;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.ExternalReference;
import com.bmc.arsys.api.Reference;
import com.bmc.arsys.api.ReferenceType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatContainer;
import com.remedy.arsys.goat.GoatContainer.ContainerList;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.stubs.SetupServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class WSDLUtil
{
  private static Log mLog = Log.get(5);

  public static String getWSServer(String paramString)
    throws GoatException
  {
    if ((paramString != null) && (paramString.length() != 0))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "/");
      if ((localStringTokenizer != null) && (localStringTokenizer.hasMoreTokens()))
        return localStringTokenizer.nextToken();
    }
    return null;
  }

  public static String getWSName(String paramString)
    throws GoatException
  {
    if ((paramString != null) && (paramString.length() != 0))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "/");
      if ((localStringTokenizer != null) && (localStringTokenizer.hasMoreTokens()))
      {
        localStringTokenizer.nextToken();
        if (localStringTokenizer.hasMoreTokens())
          return localStringTokenizer.nextToken();
      }
    }
    return null;
  }

  public static void showWSDL(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, String paramString1, String paramString2, ServerLogin paramServerLogin)
    throws IOException, GoatException
  {
    Container localContainer = GoatContainer.getContainer(paramServerLogin, paramString2).getContainer();
    Reference[] arrayOfReference = (Reference[])localContainer.getReferences().toArray(new Reference[0]);
    Value localValue = null;
    for (int i = 0; (arrayOfReference != null) && (i < arrayOfReference.length); i++)
      if (ReferenceType.WS_WSDL.equals(arrayOfReference[i].getReferenceType()))
      {
        ExternalReference localExternalReference = (ExternalReference)arrayOfReference[i];
        localValue = localExternalReference.getValue();
        if (!DataType.CHAR.equals(localValue.getDataType()))
          localValue = null;
      }
    if (localValue != null)
    {
      String str1 = (String)localValue.getValue();
      int j = -1;
      int k = -1;
      int m = str1.indexOf("location=\"@LOCATION@\"");
      if (m != -1)
      {
        j = str1.indexOf("@LOCATION@", m);
        k = str1.indexOf("@", j + 1);
        if ((j != -1) && (k != -1))
        {
          localObject = new StringBuilder(str1);
          String str2 = SetupServlet.baseURL;
          if (str2 == null)
          {
            str3 = paramHttpServletRequest.getRequestURL().toString();
            int n = str3.indexOf("WSDL");
            str2 = str3.substring(0, n);
          }
          else
          {
            str2 = str2 + paramHttpServletRequest.getContextPath() + "/";
          }
          String str3 = str2 + "services/ARService";
          str3 = str3 + "?server=" + paramString1;
          str3 = str3 + "&amp;webService=" + paramString2;
          ((StringBuilder)localObject).replace(j, k + 1, str3);
          str1 = ((StringBuilder)localObject).toString();
        }
      }
      paramHttpServletResponse.setContentType("text/xml;charset=utf-8");
      Object localObject = paramHttpServletResponse.getWriter();
      mLog.fine("WSDL Content: " + str1);
      ((PrintWriter)localObject).println(str1);
      return;
    }
    throw new GoatException(9337, paramString2);
  }

  public static void showWebserviceList(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    String str1 = (String)localHttpSession.getAttribute("BaseAliasName");
    if (str1 == null)
      str1 = "";
    String str2 = (String)localHttpSession.getAttribute("auth.authenticated");
    String str3 = "en_US";
    Configuration localConfiguration = Configuration.getInstance();
    List localList = localConfiguration.getServers();
    assert (localList.size() != 0);
    String str4 = paramHttpServletRequest.getParameter("server");
    if (str4 == null)
      str4 = (String)localList.get(0);
    ServerLogin localServerLogin = SessionData.get().getServerLogin(str4);
    paramHttpServletResponse.setContentType("text/html;charset=utf-8");
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    String str5 = SetupServlet.baseURL;
    String str6 = paramHttpServletRequest.getRequestURL().toString();
    if (str5 == null)
    {
      localObject1 = str6;
      if (localObject1 == null)
        localObject1 = "http";
      else
        localObject1 = ((String)localObject1).substring(0, ((String)localObject1).indexOf(":"));
      str5 = (String)localObject1 + "://" + paramHttpServletRequest.getServerName() + ":" + paramHttpServletRequest.getServerPort() + paramHttpServletRequest.getContextPath() + "/shared";
    }
    else
    {
      str5 = str5 + paramHttpServletRequest.getContextPath() + "/shared";
    }
    localPrintWriter.println("<HTML> \n <HEAD> \n");
    localPrintWriter.println("<TITLE>");
    localPrintWriter.println(MessageTranslation.getLocalizedText(str3, "AR SYSTEM WEB SERVICE LIST"));
    localPrintWriter.println("</TITLE> \n");
    localPrintWriter.println(" <link href=\"" + str5 + "/config/config.css\"  type=\"text/css\"  rel=\"stylesheet\">");
    localPrintWriter.println("<SCRIPT language=\"Javascript\">");
    localPrintWriter.println("function loadWSList()\n{\n  ");
    localPrintWriter.println("var selectSer = document.forms[0].ws_server.options[document.forms[0].ws_server.selectedIndex].value;\n");
    localPrintWriter.println("var addPage = location.href;\n");
    localPrintWriter.println("var sep = '&';");
    localPrintWriter.println("var index1 = addPage.indexOf(\"?server\");\n");
    localPrintWriter.println("var index2 = addPage.indexOf(\"&server\");\n");
    localPrintWriter.println("if (index1 > 0 ) \n { addPage = addPage.substring(0, index1); \n sep = '?'; \n } \n");
    localPrintWriter.println("else if (index2 > 0 )\n  { addPage = addPage.substring(0, index2); } \n");
    localPrintWriter.println("if (location.href.indexOf(\"?\") <= 0 ) \n { sep = '?'; } \n ");
    localPrintWriter.println("if (selectSer != null && selectSer.length > 0 ) \n");
    localPrintWriter.println("{ location.href = addPage + sep + 'server=' + selectSer;} \n");
    localPrintWriter.println("else location.href = addPage; \n }\n");
    localPrintWriter.println("</SCRIPT>\n</HEAD>");
    localPrintWriter.println("<BODY style=\"background-repeat: no-repeat;\" leftmargin=\"0\"  topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" > \n");
    localPrintWriter.println("<table border=0 cellpadding=0 cellspacing=0 width=\"100%\">");
    localPrintWriter.println("<tr>");
    localPrintWriter.println("<td width=\"100%\">");
    localPrintWriter.println("<table border=0 cellpadding=0 cellspacing=0 width=\"100%\" height=60>");
    localPrintWriter.println("<tr>");
    localPrintWriter.println("<td height=60 background=\"");
    localPrintWriter.println(str5 + "/images/bkgd_image.gif\">");
    localPrintWriter.println("<span id=\"product\">AR System Web Service List</span>");
    localPrintWriter.println("<div id=\"logo\" align=\"right\">");
    localPrintWriter.println("<img src=\"");
    localPrintWriter.println(str5 + "/images/bmc_logo.gif\" width=\"118\" height=\"26\" hspace=\"20\" alt=\"BMC logo\">");
    localPrintWriter.println("</div>");
    localPrintWriter.println("</td>");
    localPrintWriter.println("</tr>");
    localPrintWriter.println("</table>");
    localPrintWriter.println("</td>");
    localPrintWriter.println("</tr>");
    localPrintWriter.println("</table>");
    localPrintWriter.println("<FORM name=\"wslist_form\" METHOD=\"post\"> \n");
    localPrintWriter.println("<TABLE  align=\"left\"  valign=\"top\" WIDTH=\"770\" BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"1\"> \n");
    localPrintWriter.println(" <!-- AR SERVER -->");
    localPrintWriter.println("<TR><TD NOWRAP valign=\"top\" class=\"textBody\" > &nbsp;&nbsp; <B>");
    localPrintWriter.println(MessageTranslation.getLocalizedText(str3, "ARServer Name"));
    localPrintWriter.println("</B> \n &nbsp; &nbsp; &nbsp; &nbsp; ");
    localPrintWriter.println("<SELECT name=\"ws_server\" onChange=\"loadWSList()\"> \n");
    localPrintWriter.println("<OPTION Value=\"\"> -- Select an AR Server --\n");
    Object localObject1 = localConfiguration.getServers();
    assert (((List)localObject1).size() != 0);
    Object localObject2 = ((List)localObject1).iterator();
    String str8;
    while (((Iterator)localObject2).hasNext())
    {
      String str7 = (String)((Iterator)localObject2).next();
      assert (str7 != null);
      str8 = HTMLWriter.escape(str7);
      if (str7.equalsIgnoreCase(str4))
        localPrintWriter.println("<OPTION value=\"" + str8 + "\" selected=\"selected\">" + str8 + "\n");
      else
        localPrintWriter.println("<OPTION value=\"" + str8 + "\">" + str8 + "\n");
    }
    localPrintWriter.println("</SELECT> \n </TD> \n </TR>\n");
    localPrintWriter.println("<TR valign=\"top\" height=\"20\"> \n <TD></TD> \n </TR> <TR><TD>\n");
    localPrintWriter.println("<!-- Web service list --> \n");
    localPrintWriter.println("<table cellspacing=0 border=0 cellpadding=6>");
    localPrintWriter.println("<tr><td>");
    localPrintWriter.println("<TABLE class=\"BaseTable\" cellspacing=0 border=0 width=620 cellpadding=2>");
    localPrintWriter.println("<TR> \n");
    localPrintWriter.println("<th class=\"BaseTableHeader\" width=\"30%\">");
    localPrintWriter.println(MessageTranslation.getLocalizedText(str3, "Web Service Name"));
    localPrintWriter.println("</th> \n");
    localPrintWriter.println("<th class=\"BaseTableHeader\" width=\"30%\">");
    localPrintWriter.println(MessageTranslation.getLocalizedText(str3, "Service Type"));
    localPrintWriter.println("</th> \n");
    localPrintWriter.println("<th class=\"BaseTableHeader\" width=\"40%\">");
    localPrintWriter.println(MessageTranslation.getLocalizedText(str3, "Description"));
    localPrintWriter.println("</th> \n");
    localPrintWriter.println("</TR> \n");
    localObject2 = "";
    int i;
    int j;
    Object localObject3;
    if (SetupServlet.baseURL == null)
    {
      if (str6 != null)
      {
        i = str6.indexOf("list");
        if (i > 0)
        {
          str8 = str6.substring(0, i);
          localObject2 = str8 + str4 + "/";
        }
      }
    }
    else if (str6 == null)
    {
      localObject2 = SetupServlet.baseURL + paramHttpServletRequest.getContextPath() + "/WSDL/protected/" + str4 + "/";
    }
    else
    {
      i = str6.indexOf("list");
      j = str6.indexOf(paramHttpServletRequest.getContextPath());
      if (i > 0)
      {
        localObject3 = str6.substring(j, i);
        localObject2 = SetupServlet.baseURL + (String)localObject3 + str4 + "/";
      }
    }
    if (str4 != null)
    {
      String[] arrayOfString = GoatContainer.ContainerList.get(localServerLogin, ContainerType.WEBSERVICE);
      for (j = 0; j < arrayOfString.length; j++)
      {
        localObject3 = GoatContainer.getContainer(localServerLogin, arrayOfString[j]).getContainer();
        String str9 = ((Container)localObject3).getName();
        String str10 = (String)localObject2 + str9;
        String str11 = getWebServiceType((Container)localObject3);
        String str12 = ((Container)localObject3).getDescription();
        String str13;
        if (str12 != null)
          str13 = str12;
        else
          str13 = "  ";
        String str14 = j % 2 == 0 ? "BaseTableCellOdd" : "BaseTableCell";
        String str15 = "";
        if (j == 0)
        {
          if (j == arrayOfString.length - 1)
            str15 = str14 + " " + "BaseTableCellLeftBottom";
          else
            str15 = str14 + " " + "BaseTableCellLeftTop";
        }
        else if (j == arrayOfString.length - 1)
          str15 = str14 + " " + "BaseTableCellLeftBottom";
        else
          str15 = str14 + " " + "BaseTableCellLeft";
        localPrintWriter.println("<TR> \n <TD class=\"" + str15 + "\" width=\"30%\" >");
        localPrintWriter.println("<A HREF=\"" + HTMLWriter.escape(str10) + "\">" + HTMLWriter.escape(str9) + "</A> \n </TD> \n");
        if (j == 0)
          str15 = str14 + " " + "BaseTableCellCenterTop";
        else if (j == arrayOfString.length - 1)
          str15 = str14 + " " + "BaseTableCellCenterBottom";
        else
          str15 = str14 + " " + "BaseTableCellCenter";
        localPrintWriter.println("<TD class=\"" + str15 + "\" width=\"30%\" >");
        String str16 = HTMLWriter.escape(str11);
        localPrintWriter.println(str16 + "</TD> \n");
        if (j == 0)
          str15 = str14 + " " + "BaseTableCellRightTop";
        else if (j == arrayOfString.length - 1)
          str15 = str14 + " " + "BaseTableCellRightBottom";
        else
          str15 = str14 + " " + "BaseTableCellRight";
        localPrintWriter.println("<TD class=\"" + str15 + "\" width=\"40%\" >");
        str16 = HTMLWriter.escape(str13);
        localPrintWriter.println(str16 + "</TD> </TR>\n");
      }
    }
    localPrintWriter.println("</TABLE>\n");
    localPrintWriter.println(" <!--- Close the top opened  table --> \n </TD></TR>");
    localPrintWriter.println("</TABLE> \n  \n </FORM> \n </BODY> \n </HTML> \n");
  }

  public static String getWebServiceType(Container paramContainer)
  {
    String str1 = "";
    String str2 = null;
    Reference[] arrayOfReference = (Reference[])paramContainer.getReferences().toArray(new Reference[0]);
    int i;
    if (arrayOfReference != null)
      for (i = 0; i < arrayOfReference.length; i++)
        if (((arrayOfReference[i] == null) || ((arrayOfReference[i] instanceof ExternalReference))) && (ReferenceType.WS_PROPERTIES.equals(arrayOfReference[i].getReferenceType())))
        {
          str2 = ((ExternalReference)arrayOfReference[i]).getValue().toString();
          break;
        }
    if (str2 != null)
    {
      mLog.fine("Get WSDL LIST: " + str2);
      i = str2.indexOf("portType name=");
      int j = str2.indexOf("/>");
      if ((i > 0) && (j > 0))
        str1 = str2.substring(i + 15, j - 1);
    }
    return str1;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.WSDLUtil
 * JD-Core Version:    0.6.1
 */