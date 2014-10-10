package com.remedy.arsys.arwebreport;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.arreporting.ReportParameters;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.reporting.common.RepServletParms;
import com.remedy.arsys.reporting.common.ReportException;
import com.remedy.arsys.reporting.common.ReportMessageTranslator;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.GoatHttpServlet;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.ReportEngine;
import org.eclipse.birt.report.engine.api.script.element.IDataSet;
import org.eclipse.birt.report.engine.api.script.element.IReportDesign;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.elements.OdaDataSet;
import org.eclipse.birt.report.utility.ParameterAccessor;

public class BirtReportServlet extends GoatHttpServlet
{
  private static Log mLog = Log.get(0);
  private static final long serialVersionUID = -3691794996091561612L;
  private static String MReqLoc = null;
  private static boolean MFirstTime = false;
  private String resourcePath;
  private static final String UNQUALIFIED_SEARCH = "4\\1\\2\\2\\1\\2\\2\\1\\";
  private static final String QUALIFICATION = "qualification";
  private static String MBaseURL = null;

  public void init()
    throws ServletException
  {
  }

  private synchronized void buildContextPath(HttpServletRequest paramHttpServletRequest)
  {
    MReqLoc = paramHttpServletRequest.getContextPath() + "/";
    Map localMap = ParameterAccessor.initViewerProps(getServletContext(), null);
    String str = (String)localMap.get("base_url");
    if ((str != null) && (str.length() == 0))
      str = null;
    if (str == null)
      str = paramHttpServletRequest.getScheme() + "://" + paramHttpServletRequest.getServerName() + ":" + paramHttpServletRequest.getServerPort();
    MBaseURL = str + MReqLoc + "servlet/ViewFormServlet?";
    MFirstTime = true;
  }

  private synchronized String getMBaseURL()
  {
    return MBaseURL;
  }

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException, ServletException
  {
    if (!MFirstTime)
      buildContextPath(paramHttpServletRequest);
    ServerLogin localServerLogin = null;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    String str9 = null;
    String str10 = null;
    if (paramHttpServletRequest.getParameter("pl") != null)
    {
      str8 = "America/Los_Angeles";
      str7 = "en_US";
      str2 = "test";
      str3 = Configuration.getInstance().getRootPath() + "/reports/test.rptdesign";
      localObject1 = paramHttpServletRequest.getRequestDispatcher("/frameset?__report=" + str3.replace('\\', '/') + "&__svg=false&__locale=" + str7 + "&__timezone=" + str8);
      if (localObject1 != null)
        ((RequestDispatcher)localObject1).forward(paramHttpServletRequest, paramHttpServletResponse);
      return;
    }
    Object localObject1 = new RepServletParms();
    if (paramHttpServletRequest != null)
      ((RepServletParms)localObject1).addParams(paramHttpServletRequest);
    str1 = ((RepServletParms)localObject1).getParameter("S");
    if (str1 != null)
      localServerLogin = SessionData.get().getServerLogin(str1);
    str8 = ((RepServletParms)localObject1).getParameter("TZ");
    str7 = ((RepServletParms)localObject1).getParameter("LOC");
    str2 = ((RepServletParms)localObject1).getParameter("R");
    if (str2 == null)
      str2 = ((RepServletParms)localObject1).getParameter("name");
    str3 = ((RepServletParms)localObject1).getParameter("RF");
    str5 = ((RepServletParms)localObject1).getParameter("QR");
    str4 = ((RepServletParms)localObject1).getParameter("Q");
    if (str4 == null)
      str4 = "1=1";
    str9 = ((RepServletParms)localObject1).getParameter("DESVER");
    String str11 = ((RepServletParms)localObject1).getParameter("F");
    str6 = ((RepServletParms)localObject1).getParameter("entryids");
    if (str6 == null)
      str6 = "";
    str10 = ((RepServletParms)localObject1).getParameter("BASEURL");
    if ((str10 == null) || (str10.length() == 0))
    {
      str10 = MBaseURL;
    }
    else
    {
      str12 = paramHttpServletRequest.getContextPath();
      if ((str12 != null) && (str12.length() > 0))
        ParameterAccessorExtended.setInitProp("base_url", str10.substring(0, str10.indexOf(MReqLoc)));
      else
        ParameterAccessorExtended.setInitProp("base_url", str10);
      str10 = str10 + "servlet/ViewFormServlet?";
    }
    String str12 = null;
    if ((str6.length() == 0) && (localServerLogin != null))
      try
      {
        QualifierInfo localQualifierInfo1 = localServerLogin.decodeQualification(str4);
        str12 = localServerLogin.formatQualification(localQualifierInfo1, null, null, 0, false);
      }
      catch (ARException localARException1)
      {
        generateErrorResponse(paramHttpServletResponse, localARException1, str7);
        return;
      }
    ReportParameters localReportParameters = new ReportParameters();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    try
    {
      int i = 1;
      if (str9 != null)
      {
        try
        {
          if (Integer.parseInt(str9) <= 0)
            i = 0;
        }
        catch (Exception localException)
        {
        }
        if (i != 0)
        {
          localObject2 = new ReportEngine(new EngineConfig());
          localObject3 = null;
          localObject3 = ((ReportEngine)localObject2).openReportDesign(str3);
          localObject4 = getDataSet((IReportRunnable)localObject3);
          if (localObject4 != null)
            populateParametersFromQueryText(localReportParameters, ((IDataSet)localObject4).getQueryText());
        }
      }
    }
    catch (EngineException localEngineException)
    {
      mLog.severe(localEngineException.getMessage());
    }
    catch (ARException localARException2)
    {
      mLog.severe(localARException2.getMessage());
    }
    int j = Configuration.getInstance().getWebReportOnScreenMaxEntries();
    if ((str3 != null) && (localServerLogin != null) && (paramHttpServletRequest != null))
    {
      localObject2 = paramHttpServletRequest.getSession(false);
      ((HttpSession)localObject2).setAttribute("AppContextKey", "AR_SYSTEM_APP_CONTEXT");
      localObject3 = new HashMap();
      ((Map)localObject3).put("server", localServerLogin.getServer());
      ((Map)localObject3).put("user", localServerLogin.getUser());
      ((Map)localObject3).put("password", localServerLogin.getPassword());
      ((Map)localObject3).put("port", "" + localServerLogin.getPort());
      str11 = Validator.sanitizeCRandLF(str11);
      ((Map)localObject3).put("form", str11);
      ((Map)localObject3).put("authentication", localServerLogin.getAuthentication());
      str7 = Validator.sanitizeCRandLF(str7);
      ((Map)localObject3).put("locale", str7);
      str8 = Validator.sanitizeCRandLF(str8);
      ((Map)localObject3).put("timezone", str8);
      ((Map)localObject3).put("maxonscreenentries", Integer.valueOf(j));
      if (str6.length() > 0)
      {
        str6 = Validator.sanitizeCRandLF(str6);
        ((Map)localObject3).put("resultList", str6);
      }
      else
      {
        ((Map)localObject3).put("overrideQualification", Boolean.valueOf((str5 != null) && ((str5.equals("1")) || (str5.equalsIgnoreCase("Yes")))));
        str4 = Validator.sanitizeCRandLF(str4);
        ((Map)localObject3).put("qualification", str4);
      }
      ((HttpSession)localObject2).setAttribute("AppContextValue", localObject3);
      ((HttpSession)localObject2).setAttribute("midTierURL", str10 + "form=" + URLEncoder.encode(str11, "UTF-8") + "&server=" + URLEncoder.encode(localServerLogin.getServer(), "UTF-8") + "&eid=");
      ((HttpSession)localObject2).setAttribute("customDateFormat", localServerLogin.getCustomDateFormat());
      ((HttpSession)localObject2).setAttribute("customTimeFormat", localServerLogin.getCustomTimeFormat());
      localObject4 = null;
      try
      {
        if ((localReportParameters.getQualification() != null) && (!localReportParameters.getQualification().equals("")))
        {
          QualifierInfo localQualifierInfo2 = localServerLogin.decodeQualification(localReportParameters.getQualification());
          localObject4 = localServerLogin.formatQualification(localQualifierInfo2, null, null, 0, false);
        }
      }
      catch (ARException localARException3)
      {
        localObject4 = null;
      }
      int k = 0;
      k = (str5 != null) && ((str5.equals("1")) || (str5.equalsIgnoreCase("Yes"))) ? 1 : 0;
      if (k != 0)
        localObject4 = null;
      String str13 = ((str12 != null) && (!str12.equals("")) && (!str12.equals("1 = 1")) && (!str12.equals("1=1")) && (!str12.equalsIgnoreCase("4\\1\\2\\2\\1\\2\\2\\1\\")) ? str12 + " AND " : "") + ((localObject4 != null) && (!((String)localObject4).equals("")) && (!((String)localObject4).equals("1 = 1")) && (!((String)localObject4).equals("1=1")) && (!((String)localObject4).equalsIgnoreCase("4\\1\\2\\2\\1\\2\\2\\1\\")) ? (String)localObject4 + " AND " : "");
      Object localObject5;
      if (((str13 == null) || (str13.trim().equals(""))) && (!ServerInfo.get(localServerLogin.getServer()).getAllowUnqualQueries()))
      {
        mLog.severe(str2 + " - An unqualified search was issued and the server has been configured to disallow unqualified searches.");
        localObject5 = new ReportException(361, "An unqualified search was issued and the server has been configured to disallow unqualified searches.");
        generateErrorResponse(paramHttpServletResponse, (Exception)localObject5, str7);
        return;
      }
      ((HttpSession)localObject2).setAttribute("chartURL", str10 + "form=" + URLEncoder.encode(str11, "UTF-8") + "&server=" + URLEncoder.encode(localServerLogin.getServer(), "UTF-8") + "&qual=" + ((str12 != null) && (!str12.equals("")) && (!str12.equals("1 = 1")) && (!str12.equals("1=1")) ? URLEncoder.encode(str12, "UTF-8") + " AND " : "") + ((localObject4 != null) && (!((String)localObject4).equals("")) ? URLEncoder.encode((String)localObject4, "UTF-8") + " AND " : ""));
      if ((str6 != null) && (str6.length() <= 0))
        ((HttpSession)localObject2).setAttribute("entryIdsExist", "true");
      else
        ((HttpSession)localObject2).setAttribute("entryIdsExist", null);
      try
      {
        if (this.resourcePath == null)
        {
          localObject5 = new File(Configuration.getInstance().getRootPath());
          this.resourcePath = ((File)localObject5).getAbsolutePath();
          localObject6 = getServletContext().getInitParameter("BIRT_RESOURCE_PATH");
          if ((localObject6 != null) && (((String)localObject6).length() > 0))
            this.resourcePath += (String)localObject6;
          else
            this.resourcePath += "/libraries";
          this.resourcePath = this.resourcePath.replace('\\', '/');
        }
        BIRTLibraryUtil.updateLibraries(localServerLogin, this.resourcePath);
      }
      catch (ARException localARException4)
      {
        mLog.severe(localARException4.getMessage());
      }
      String str14 = "";
      if ((localServerLogin.getTimeZone() != null) && (localServerLogin.getTimeZone().length() > 0))
        str14 = "&__timezone=" + localServerLogin.getTimeZone();
      Object localObject6 = paramHttpServletRequest.getRequestDispatcher("/frameset?__report=" + str3.replace('\\', '/') + "&__svg=false&__locale=" + str7 + str14 + "&__resourceFolder=" + this.resourcePath + "/" + localServerLogin.getServer());
      if (localObject6 != null)
        ((RequestDispatcher)localObject6).forward(paramHttpServletRequest, paramHttpServletResponse);
      else
        paramHttpServletResponse.sendRedirect(Validator.sanitizeCRandLF(MReqLoc + "frameset?__report=" + str3.replace('\\', '/') + "&__svg=false&__locale=" + str7 + str14 + "&__resourceFolder=" + this.resourcePath + "/" + localServerLogin.getServer()));
    }
  }

  private IDataSet getDataSet(IReportRunnable paramIReportRunnable)
    throws ARException
  {
    IDataSet localIDataSet = null;
    SlotHandle localSlotHandle = paramIReportRunnable.getDesignHandle().getModuleHandle().getDataSets();
    Iterator localIterator = localSlotHandle.iterator();
    while (localIterator.hasNext())
    {
      OdaDataSetHandle localOdaDataSetHandle = (OdaDataSetHandle)localIterator.next();
      DesignElement localDesignElement = localOdaDataSetHandle.getElement();
      if ((localDesignElement instanceof OdaDataSet))
      {
        localIDataSet = paramIReportRunnable.getDesignInstance().getDataSet(((OdaDataSet)localDesignElement).getName());
        break;
      }
    }
    if (localIDataSet == null)
      localIDataSet = paramIReportRunnable.getDesignInstance().getDataSet("adhoc_dataset");
    if (localIDataSet == null)
      throw new ARException(2, 3356, "Unable to find data set");
    return localIDataSet;
  }

  private void populateParametersFromQueryText(ReportParameters paramReportParameters, String paramString)
    throws IOException
  {
    Properties localProperties = new Properties();
    localProperties.load(new ByteArrayInputStream(paramString.getBytes()));
    paramReportParameters.setQualification(localProperties.getProperty("qualification"));
  }

  protected void generateErrorResponse(HttpServletResponse paramHttpServletResponse, Exception paramException, String paramString)
    throws IOException
  {
    paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
    paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    localPrintWriter.print("<html><body>");
    if (paramString != null)
      localPrintWriter.println("<h2>" + ReportMessageTranslator.getLocalizedText(paramString, "Qualification Error:") + "</h2>");
    String str = paramException.getLocalizedMessage();
    if (str != null)
      localPrintWriter.println("<h3>" + str + "</h3>");
    Throwable localThrowable = paramException.getCause();
    if (localThrowable != null)
      localPrintWriter.println("<h3>" + localThrowable + "</h3>");
    localPrintWriter.println("</body></html>");
    localPrintWriter.close();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.BirtReportServlet
 * JD-Core Version:    0.6.1
 */