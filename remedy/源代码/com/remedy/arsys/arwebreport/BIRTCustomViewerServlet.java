package com.remedy.arsys.arwebreport;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.reporting.common.ReportMessageTranslator;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.axis.AxisFault;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.context.BaseAttributeBean;
import org.eclipse.birt.report.context.IContext;
import org.eclipse.birt.report.exception.ViewerException;
import org.eclipse.birt.report.resource.BirtResources;
import org.eclipse.birt.report.servlet.ViewerServlet;
import org.eclipse.birt.report.session.IViewingSession;
import org.eclipse.birt.report.session.ViewingSessionUtil;
import org.eclipse.birt.report.utility.BirtUtility;
import org.eclipse.birt.report.utility.ParameterAccessor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BIRTCustomViewerServlet extends ViewerServlet
{
  private static final long serialVersionUID = 1L;
  protected static final Log mLog = Log.get(0);
  Object[] mMsgObjs = new Object[1];

  public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    String str1 = paramHttpServletRequest.getHeader("request-type");
    boolean bool = "soap".equalsIgnoreCase(str1);
    if (!bool)
    {
      doSuperPost(paramHttpServletRequest, paramHttpServletResponse);
      return;
    }
    ServletOutputStream localServletOutputStream = paramHttpServletResponse.getOutputStream();
    ResponseWrapper localResponseWrapper = new ResponseWrapper(paramHttpServletResponse);
    doSuperPost(paramHttpServletRequest, localResponseWrapper);
    if (500 == localResponseWrapper.getStatus())
    {
      String str2 = new String(localResponseWrapper.getData());
      StringBuffer localStringBuffer = new StringBuffer();
      int i = str2.indexOf("<faultstring>");
      int j = str2.indexOf("</faultstring>");
      if ((i != -1) && (j != -1))
      {
        localStringBuffer.append(str2.substring(0, i + 13));
        String str3;
        if (SessionData.get() != null)
        {
          str3 = SessionData.get().getLocale();
        }
        else
        {
          str3 = paramHttpServletRequest.getParameter("__locale");
          if (str3 == null)
            str3 = Locale.getDefault().toString();
        }
        String str4 = parseXMLandLogException(localResponseWrapper.getData());
        String str5;
        if (str4 != null)
          str5 = str4;
        else
          str5 = ReportMessageTranslator.getLocalizedErrorMessage(str3, 9426, this.mMsgObjs);
        localStringBuffer.append(str5);
        if (str2.lastIndexOf("Caused by:") != -1)
        {
          String str6 = str2.substring(str2.lastIndexOf("Caused by:"));
          str6 = str6.substring(0, str6.indexOf('\n'));
          localStringBuffer.append(" " + str6);
        }
        localStringBuffer.append(str2.substring(j));
        try
        {
          localServletOutputStream.write(localStringBuffer.toString().getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          localServletOutputStream.write(localStringBuffer.toString().getBytes());
        }
      }
      else
      {
        localServletOutputStream.write(localResponseWrapper.getData());
      }
    }
    else
    {
      localServletOutputStream.write(localResponseWrapper.getData());
    }
    localServletOutputStream.flush();
    localServletOutputStream.close();
  }

  public void doSuperPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    if (!__authenticate(paramHttpServletRequest, paramHttpServletResponse))
      return;
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = paramHttpServletRequest.getParameterMap().keySet().iterator();
    while (localIterator.hasNext())
    {
      str1 = (String)localIterator.next();
      if ((str1 != null) && (str1.startsWith("__")))
      {
        str2 = ParameterAccessor.urlEncode(ParameterAccessor.getParameter(paramHttpServletRequest, str1), "UTF-8");
        if (str1.equals("__timezone"))
          str2 = URLDecoder.decode(str2, "UTF-8");
        localStringBuffer.append("&" + str1 + "=" + str2);
      }
    }
    String str1 = paramHttpServletRequest.getRequestURL().toString();
    if (ParameterAccessor.getBaseURL() != null)
      str1 = ParameterAccessor.getBaseURL() + paramHttpServletRequest.getContextPath() + paramHttpServletRequest.getServletPath();
    localStringBuffer.deleteCharAt(0);
    str1 = str1 + "?" + localStringBuffer.toString();
    paramHttpServletRequest.setAttribute("SoapURL", Validator.sanitizeCRandLF(str1));
    String str2 = paramHttpServletRequest.getHeader("request-type");
    boolean bool = "soap".equalsIgnoreCase(str2);
    IContext localIContext = null;
    IViewingSession localIViewingSession;
    try
    {
      localIViewingSession = ViewingSessionUtil.getSession(paramHttpServletRequest);
      if ((localIViewingSession == null) && (!bool))
        if (ViewingSessionUtil.getSessionId(paramHttpServletRequest) == null)
          localIViewingSession = ViewingSessionUtil.createSession(paramHttpServletRequest);
        else
          throw new ViewerException(BirtResources.getMessage("birt.viewer.error.noviewingsession"));
      localIContext = __getContext(paramHttpServletRequest, paramHttpServletResponse);
    }
    catch (BirtException localBirtException1)
    {
      mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(localBirtException1), localBirtException1);
      __handleCustomNonSoapException(paramHttpServletRequest, paramHttpServletResponse, localBirtException1);
      return;
    }
    try
    {
      if (localIViewingSession != null)
        localIViewingSession.lock();
      __doPost(localIContext);
      if (bool)
      {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        super.doPost(paramHttpServletRequest, paramHttpServletResponse);
      }
      else
      {
        try
        {
          if (localIContext.getBean().getException() != null)
            __handleCustomNonSoapException(paramHttpServletRequest, paramHttpServletResponse, localIContext.getBean().getException());
          else
            __doGet(localIContext);
        }
        catch (BirtException localBirtException2)
        {
          mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(localBirtException2), localBirtException2);
          __handleCustomNonSoapException(paramHttpServletRequest, paramHttpServletResponse, localBirtException2);
        }
      }
    }
    catch (BirtException localBirtException3)
    {
      mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(localBirtException3), localBirtException3);
    }
    finally
    {
      if ((localIViewingSession != null) && (!localIViewingSession.isExpired()))
        localIViewingSession.unlock();
    }
  }

  protected void __handleCustomNonSoapException(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, Throwable paramThrowable)
    throws ServletException, IOException
  {
    mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(paramThrowable), paramThrowable);
    appendErrorMessage(paramHttpServletResponse.getOutputStream(), paramThrowable);
  }

  public static void appendErrorMessage(OutputStream paramOutputStream, Throwable paramThrowable)
    throws IOException
  {
    String str1 = "<html>\n<head>\n<title>" + BirtResources.getMessage("birt.viewer.title.error") + "</title>\n";
    str1 = str1 + "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=utf-8\">\n</head>\n";
    str1 = str1 + "<body>\n";
    String str2 = "document.getElementById('error_detail')";
    String str3 = "document.getElementById('error_icon')";
    String str4 = "if (" + str2 + ".style.display == 'none') { " + str3 + ".innerHTML = '- '; " + str2 + ".style.display = 'block'; }" + "else { " + str3 + ".innerHTML = '+ '; " + str2 + ".style.display = 'none'; }";
    str1 = str1 + "<div id=\"birt_errorPage\" style=\"color:red\">\n";
    str1 = str1 + "<span id=\"error_icon\"  style=\"cursor:pointer\" onclick=\"" + str4 + "\" > + </span>\n";
    String str5 = null;
    if ((paramThrowable instanceof AxisFault))
      str5 = ((AxisFault)paramThrowable).getFaultString();
    else
      str5 = paramThrowable.getLocalizedMessage();
    if (str5 != null)
      str1 = str1 + ParameterAccessor.htmlEncode(str5);
    else
      str1 = str1 + "Unknown error!";
    str1 = str1 + "<br>\n";
    str1 = str1 + "<pre id=\"error_detail\" style=\"display:none;\" >\n";
    str1 = str1 + ParameterAccessor.htmlEncode(BirtUtility.getDetailMessage(paramThrowable));
    str1 = str1 + "</pre>\n";
    str1 = str1 + "</div>\n";
    str1 = str1 + "</body>\n</html>";
    paramOutputStream.write(str1.getBytes("UTF-8"));
    paramOutputStream.flush();
    paramOutputStream.close();
  }

  private String parseXMLandLogException(byte[] paramArrayOfByte)
  {
    Object localObject1 = null;
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      Document localDocument = localDocumentBuilder.parse(localByteArrayInputStream);
      mLog.log(Level.WARNING, xmlToString(localDocument));
      NodeList localNodeList = localDocument.getElementsByTagName("faultcode");
      if ((localNodeList != null) && (localNodeList.getLength() > 0))
      {
        localObject2 = localNodeList.item(0);
        if (localObject2 != null)
        {
          localObject3 = ((Node)localObject2).getTextContent();
          mLog.log(Level.SEVERE, "BIRT Report runtimeException faultcode: " + (String)localObject3);
        }
      }
      Object localObject2 = localDocument.getElementsByTagName("faultstring");
      Object localObject4;
      Object localObject5;
      if ((localObject2 != null) && (((NodeList)localObject2).getLength() > 0))
      {
        localObject3 = ((NodeList)localObject2).item(0);
        if (localObject3 != null)
        {
          localObject4 = ((Node)localObject3).getTextContent();
          localObject5 = localObject4;
          int i = ((String)localObject5).indexOf(":");
          if (i != -1)
          {
            if (i + 1 <= ((String)localObject5).length())
            {
              localObject1 = ((String)localObject5).substring(i + 1);
              localObject1 = ((String)localObject1).trim();
            }
          }
          else
            localObject1 = localObject4;
          mLog.log(Level.SEVERE, "BIRT Report runtimeException faultstring: " + (String)localObject4);
        }
      }
      Object localObject3 = localDocument.getElementsByTagName("string");
      if ((localObject3 != null) && (((NodeList)localObject3).getLength() > 0))
      {
        localObject4 = ((NodeList)localObject3).item(0);
        if (localObject4 != null)
        {
          localObject5 = ((Node)localObject4).getTextContent();
          mLog.log(Level.SEVERE, "BIRT Report runtimeException stacktrace: " + (String)localObject5);
        }
      }
    }
    catch (IOException localIOException)
    {
      mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(localIOException), localIOException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(localParserConfigurationException), localParserConfigurationException);
    }
    catch (SAXException localSAXException)
    {
      mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(localSAXException), localSAXException);
    }
    return localObject1;
  }

  public static String xmlToString(Node paramNode)
  {
    try
    {
      DOMSource localDOMSource = new DOMSource(paramNode);
      StringWriter localStringWriter = new StringWriter();
      StreamResult localStreamResult = new StreamResult(localStringWriter);
      TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
      Transformer localTransformer = localTransformerFactory.newTransformer();
      localTransformer.transform(localDOMSource, localStreamResult);
      return localStringWriter.getBuffer().toString();
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      localTransformerConfigurationException.printStackTrace();
    }
    catch (TransformerException localTransformerException)
    {
      localTransformerException.printStackTrace();
    }
    return null;
  }

  protected void __handleNonSoapException(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, Exception paramException)
    throws ServletException, IOException
  {
    mLog.log(Level.SEVERE, BirtUtility.getDetailMessage(paramException), paramException);
    super.__handleNonSoapException(paramHttpServletRequest, paramHttpServletResponse, paramException);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.BIRTCustomViewerServlet
 * JD-Core Version:    0.6.1
 */