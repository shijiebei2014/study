package com.remedy.arsys.ws.services;

import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.ExternalReference;
import com.bmc.arsys.api.Reference;
import com.bmc.arsys.api.ReferenceType;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.ws.mapping.ARSOperation;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.support.PwdUtility;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.session.Session;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.DOM2Writer;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ARService
{
  private String nameSpace = null;
  private String server = null;
  private String webService = null;
  private String operationName = null;
  private String inputMapping = null;
  private String outputMapping = null;
  private ARSOperation opInfo = null;
  private ServerLogin context = null;
  private HttpServletRequest request = null;
  private static Log mLog = Log.get(5);
  private static final Log MServerLog = Log.get(12);
  private static final Pattern ROOT_PATTERN = Pattern.compile("(: *ROOT(\\s|>))");

  public Document processRequest(Document paramDocument)
    throws AxisFault
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(20);
    try
    {
      MessageContext localMessageContext = MessageContext.getCurrentContext();
      this.request = ((HttpServletRequest)localMessageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST));
      SOAPBodyElement localSOAPBodyElement = getWebServiceInfo(localMessageContext);
      this.context = login(this.server, localMessageContext, this.nameSpace);
      mLog.fine("LOGIN SUCCESS: User[" + this.context.getUser().toString() + "]");
      getOperationInfo();
      Document localDocument = performOperation(localSOAPBodyElement);
      Message localMessage = localMessageContext.getResponseMessage();
      if (localMessage != null)
      {
        localObject1 = (SOAPEnvelope)localMessage.getSOAPPart().getEnvelope();
        ((SOAPEnvelope)localObject1).setNamespaceURI(this.nameSpace);
      }
      localMeasurement.end();
      Object localObject1 = localDocument;
      return localObject1;
    }
    catch (AxisFault localAxisFault)
    {
      throw localAxisFault;
    }
    catch (Exception localException)
    {
      mLog.log(Level.SEVERE, "AxisFault : ", localException);
      localMeasurement.end();
      throw AxisFault.makeFault(localException);
    }
    finally
    {
      ServerLogin.threadDepartingGoatSpace(true);
    }
  }

  private SOAPBodyElement getWebServiceInfo(MessageContext paramMessageContext)
    throws Exception
  {
    Message localMessage = paramMessageContext.getRequestMessage();
    SOAPEnvelope localSOAPEnvelope = localMessage.getSOAPEnvelope();
    SOAPBody localSOAPBody = (SOAPBody)localSOAPEnvelope.getBody();
    SOAPBodyElement localSOAPBodyElement = (SOAPBodyElement)localSOAPBody.getChildElements().next();
    this.operationName = localSOAPBodyElement.getLocalName();
    this.nameSpace = localSOAPBodyElement.getNamespaceURI();
    mLog.fine("operationName: " + this.operationName);
    mLog.fine("nameSpace " + this.nameSpace);
    this.server = this.request.getParameter("server");
    this.webService = this.request.getParameter("webService");
    mLog.fine("server: " + this.server);
    mLog.fine("webService: " + this.webService);
    if ((this.server == null) || (this.server.length() == 0) || (this.server.length() > 64) || (this.webService == null) || (this.webService.length() == 0) || (this.webService.length() > 254))
    {
      GoatException localGoatException = new GoatException(9329);
      mLog.log(Level.SEVERE, "", localGoatException);
      throw localGoatException;
    }
    return localSOAPBodyElement;
  }

  private SOAPHeaderElement getSOAPHeader(MessageContext paramMessageContext, String paramString)
  {
    Object localObject1 = null;
    if (paramMessageContext == null)
      return localObject1;
    Message localMessage = paramMessageContext.getRequestMessage();
    if (localMessage == null)
      return localObject1;
    org.apache.axis.SOAPPart localSOAPPart = (org.apache.axis.SOAPPart)localMessage.getSOAPPart();
    if (localSOAPPart == null)
      return localObject1;
    try
    {
      SOAPEnvelope localSOAPEnvelope = (SOAPEnvelope)localSOAPPart.getEnvelope();
      localObject2 = (SOAPHeader)localSOAPEnvelope.getHeader();
      Iterator localIterator = ((SOAPHeader)localObject2).getChildElements();
      while (localIterator.hasNext())
      {
        SOAPHeaderElement localSOAPHeaderElement = (SOAPHeaderElement)localIterator.next();
        if ("AuthenticationInfo".equals(localSOAPHeaderElement.getElementName().getLocalName()))
        {
          localObject1 = localSOAPHeaderElement;
          break;
        }
      }
    }
    catch (SOAPException localSOAPException)
    {
      Object localObject2 = Log.get(2);
      ((Log)localObject2).log(Level.SEVERE, "", localSOAPException);
    }
    return localObject1;
  }

  private ServerLogin login(String paramString1, MessageContext paramMessageContext, String paramString2)
    throws Exception
  {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    SOAPHeaderElement localSOAPHeaderElement = getSOAPHeader(paramMessageContext, paramString2);
    Object localObject1;
    if (localSOAPHeaderElement != null)
    {
      localObject1 = localSOAPHeaderElement.getChildNodes();
      if ((localObject1 != null) && (((NodeList)localObject1).getLength() > 0))
      {
        HashMap localHashMap = new HashMap();
        for (int j = 0; j < ((NodeList)localObject1).getLength(); j++)
        {
          localObject2 = ((NodeList)localObject1).item(j);
          if (((Node)localObject2).getNodeType() == 1)
          {
            String str7 = ((Node)localObject2).getFirstChild() != null ? ((Node)localObject2).getFirstChild().getNodeValue() : "";
            localHashMap.put(((Node)localObject2).getLocalName(), str7);
          }
        }
        str1 = (String)localHashMap.get("userName");
        str2 = (String)localHashMap.get("password");
        str3 = (String)localHashMap.get("authentication");
        str4 = (String)localHashMap.get("locale");
        str5 = (String)localHashMap.get("timeZone");
      }
    }
    if (str1 == null)
    {
      localObject1 = Configuration.getInstance();
      try
      {
        str1 = ((Configuration)localObject1).getProperty("arsystem.ws_anonymous_user");
        if (str1 == null)
          throw new GoatException(9338, paramString1);
      }
      catch (Exception localException1)
      {
        throw new GoatException(9338, paramString1);
      }
      try
      {
        str2 = ((Configuration)localObject1).getProperty("arsystem.ws_anonymous_pwd");
        if (str2 == null)
          str2 = "";
        else
          str2 = PwdUtility.decryptPswdUsingBlowfish(str2);
      }
      catch (Exception localException2)
      {
        str2 = "";
      }
      str3 = "";
    }
    if (str4 == null)
      str4 = Locale.getDefault().toString();
    if (str5 == null)
      str5 = TimeZone.getDefault().getID();
    int i = 0;
    String str6 = null;
    HttpServletRequest localHttpServletRequest = (HttpServletRequest)paramMessageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
    if (localHttpServletRequest != null)
    {
      i = localHttpServletRequest.getServerPort();
      str6 = localHttpServletRequest.getSession().getId();
    }
    Object localObject2 = MessageContext.getCurrentContext().getSession();
    return Login.initWebService((Session)localObject2, str6, paramString1, str1, str2, str4, str5, str3, i);
  }

  private void getOperationInfo()
    throws Exception
  {
    GoatContainer localGoatContainer = GoatContainer.getContainer(this.context, this.webService);
    Reference[] arrayOfReference = (Reference[])localGoatContainer.getContainer().getReferences().toArray(new Reference[0]);
    ExternalReference localExternalReference;
    String str;
    for (int i = 0; i < arrayOfReference.length; i++)
      if (ReferenceType.WS_OPERATION.equals(arrayOfReference[i].getReferenceType()))
      {
        localExternalReference = (ExternalReference)arrayOfReference[i];
        str = (String)localExternalReference.getValue().getValue();
        if (str.indexOf(this.operationName) != -1)
        {
          Document localDocument;
          try
          {
            localDocument = XMLUtils.newDocument(new InputSource(new StringReader(str)));
          }
          catch (Exception localException)
          {
            throw localException;
          }
          ARSOperation localARSOperation = new ARSOperation(localDocument.getDocumentElement());
          if (this.operationName.equals(localARSOperation.getName()))
          {
            this.opInfo = localARSOperation;
            mLog.fine("Operation Info: " + str);
          }
        }
      }
    if (this.opInfo == null)
      throw new GoatException(9331, this.operationName, this.webService);
    for (i = 0; i < arrayOfReference.length; i++)
      if (ReferenceType.WS_ARXML_MAPPING.equals(arrayOfReference[i].getReferenceType()))
      {
        localExternalReference = (ExternalReference)arrayOfReference[i];
        Value localValue = localExternalReference.getValue();
        if (DataType.CHAR.equals(localValue.getDataType()))
        {
          str = (String)localValue.getValue();
          if (str.indexOf("<arDocMapping") != -1)
          {
            if ((this.opInfo.getInputMappingName() != null) && (str != null) && (str.indexOf("name=\"" + this.opInfo.getInputMappingName() + "\"") != -1))
              this.inputMapping = str;
            if ((this.opInfo.getOutputMappingName() != null) && (str != null) && (str.indexOf("name=\"" + this.opInfo.getOutputMappingName() + "\"") != -1))
              this.outputMapping = str;
          }
        }
      }
  }

  private boolean checkStatus()
  {
    List localList = this.context.getLastStatus();
    int i = 0;
    mLog.fine("STATUS MESSAGE COUNT: " + localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      StatusInfo localStatusInfo = (StatusInfo)localIterator.next();
      i = (int)localStatusInfo.getMessageNum();
      mLog.fine("MESSAGE NUMBER: " + i);
    }
    return true;
  }

  private Document createOutputDocument(String paramString1, String paramString2, String paramString3)
  {
    Document localDocument = null;
    if ((paramString1 != null) && (paramString1.indexOf("ROOT/>") == -1))
    {
      int i = 0;
      StringBuilder localStringBuilder = new StringBuilder(paramString1);
      while (true)
      {
        Matcher localMatcher = ROOT_PATTERN.matcher(localStringBuilder.toString());
        if (!localMatcher.find())
          break;
        MatchResult localMatchResult = localMatcher.toMatchResult();
        int j = localMatchResult.start(1);
        if ((j != -1) && (localStringBuilder.charAt(j) == ':'))
          j++;
        int k = localMatchResult.end(1);
        if (k != -1)
          k--;
        if ((j != -1) && (k != -1))
        {
          localStringBuilder.replace(j, k, paramString3);
          i = 1;
        }
      }
      if (i != 0)
        try
        {
          localDocument = XMLUtils.newDocument(new InputSource(new StringReader(localStringBuilder.toString())));
        }
        catch (Exception localException2)
        {
          localDocument = null;
          return localDocument;
        }
    }
    if (localDocument == null)
    {
      try
      {
        localDocument = XMLUtils.newDocument();
      }
      catch (Exception localException1)
      {
        localDocument = null;
        return localDocument;
      }
      Element localElement = localDocument.createElementNS(paramString2, paramString3);
      localElement.setAttribute("xmlns", paramString2);
      localDocument.appendChild(localElement);
    }
    return localDocument;
  }

  private Document performOperation(SOAPBodyElement paramSOAPBodyElement)
    throws Exception
  {
    MeasureTime.Measurement localMeasurement = null;
    paramSOAPBodyElement.setName("ROOT");
    String str1 = DOM2Writer.nodeToString(paramSOAPBodyElement, false);
    mLog.fine("input document: " + str1);
    mLog.fine("input mapping: " + this.inputMapping);
    mLog.fine("output mapping: " + this.outputMapping);
    String str2;
    if ("create".equals(this.opInfo.getType()))
    {
      localMeasurement = new MeasureTime.Measurement(17);
      str2 = this.context.xmlCreateEntry(this.inputMapping, str1, this.outputMapping, this.opInfo.getOptions());
    }
    else if ("set".equals(this.opInfo.getType()))
    {
      localMeasurement = new MeasureTime.Measurement(18);
      str3 = expandXPathExpressions(this.opInfo.getQualification(), paramSOAPBodyElement, this.inputMapping);
      str2 = this.context.xmlSetEntry(null, str3, this.inputMapping, str1, this.outputMapping, this.opInfo.getOptions());
    }
    else if ("get".equals(this.opInfo.getType()))
    {
      localMeasurement = new MeasureTime.Measurement(19);
      str3 = expandXPathExpressions(this.opInfo.getQualification(), paramSOAPBodyElement, this.inputMapping);
      str2 = this.context.xmlGetEntry(null, str3, this.outputMapping, getGetOptionsForInput(paramSOAPBodyElement));
    }
    else if ("service".equals(this.opInfo.getType()))
    {
      localMeasurement = new MeasureTime.Measurement(32);
      str3 = expandXPathExpressions(this.opInfo.getQualification(), paramSOAPBodyElement, this.inputMapping);
      str2 = this.context.xmlExecuteService(null, str3, this.inputMapping, str1, this.outputMapping, this.opInfo.getOptions());
    }
    else
    {
      if (localMeasurement != null)
        localMeasurement.end();
      throw new GoatException(9332, this.opInfo.getType());
    }
    if (localMeasurement != null)
      localMeasurement.end();
    assert (checkStatus());
    mLog.fine("output document from AR Server: " + str2);
    String str3 = this.opInfo.getOutputTopElement();
    if ((str3 == null) || (str3.length() == 0))
      str3 = this.opInfo.getName() + "Response";
    return createOutputDocument(str2, this.nameSpace, str3);
  }

  String expandXPathExpressions(String paramString1, Node paramNode, String paramString2)
    throws GoatException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = -1;
    boolean bool = false;
    Document localDocument = null;
    try
    {
      localDocument = XMLUtils.newDocument(new InputSource(new StringReader(paramString2)));
    }
    catch (Exception localException)
    {
      return null;
    }
    bool = paramString1.startsWith("XPATH");
    if (paramString1.indexOf('=') != -1)
      bool = false;
    while ((i = paramString1.indexOf("XPATH(", j + 1)) != -1)
    {
      localStringBuilder.append(paramString1.substring(j + 1, i));
      j = paramString1.indexOf(")", i + 6);
      String str1 = paramString1.substring(i + 6, j);
      String str2 = getXPathValue(str1, paramNode, localDocument.getDocumentElement(), bool);
      localStringBuilder.append(str2);
      mLog.fine("Old XPATH Expression: " + str1 + " Expanded XPATH Expression: " + str2);
    }
    localStringBuilder.append(paramString1.substring(j + 1, paramString1.length()));
    String str3 = localStringBuilder.toString();
    mLog.fine("Old Qualification: " + paramString1 + " Expanded Qualification: " + str3);
    return str3;
  }

  private String getXPathValue(String paramString, Node paramNode1, Node paramNode2, boolean paramBoolean)
    throws GoatException
  {
    String str1 = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, "/");
    boolean bool = false;
    Object localObject1 = paramNode1;
    Node localNode = null;
    while (localStringTokenizer.hasMoreElements())
    {
      str2 = localStringTokenizer.nextToken();
      mLog.fine("Token: " + str2);
      if ((str2.charAt(0) == '@') && (!localStringTokenizer.hasMoreElements()))
      {
        str2 = str2.substring(1, str2.length());
        bool = true;
      }
      paramNode2 = findMappingChildNode(paramNode2, str2, bool);
      if (paramNode2 == null)
        throw new GoatException(9334, paramString);
      if ((!str2.equals("ROOT")) || (!paramNode1.getLocalName().equals("ROOT")))
      {
        Object localObject2;
        if (bool)
        {
          localObject2 = ((Node)localObject1).getAttributes();
          localNode = ((NamedNodeMap)localObject2).getNamedItem(str2);
          if ((localNode != null) && (localNode.getNodeType() == 2))
            str1 = localNode.getNodeValue();
        }
        else
        {
          NodeList localNodeList = ((Node)localObject1).getChildNodes();
          int j = localNodeList.getLength();
          for (int i = 0; i < j; i++)
          {
            localNode = localNodeList.item(i);
            if (localNodeList.item(i).getNodeType() == 1)
            {
              mLog.fine("CHILD " + localNode.getLocalName());
              if (localNode.getLocalName().equals(str2))
              {
                localObject1 = localNode;
                if (localStringTokenizer.hasMoreElements())
                  break;
                localObject2 = localNode.getFirstChild();
                if ((localObject2 != null) && (((Node)localObject2).getNodeType() == 3))
                {
                  str1 = ((Node)localObject2).getNodeValue();
                  mLog.fine("Found matching text node.  Value = " + str1);
                }
                break;
              }
            }
          }
          if (localObject1 != localNode)
            break;
        }
      }
    }
    if ((paramNode2 != null) && (str1 == null))
    {
      str1 = ((Element)paramNode2).getAttribute("default");
      if (str1.length() == 0)
        str1 = null;
    }
    if (paramBoolean)
    {
      if (str1 == null)
        str1 = "";
      return str1;
    }
    if (str1 == null)
      return "$NULL$";
    String str2 = ((Element)paramNode2).getAttribute("dataType");
    if (("string".equals(str2)) || ("enumeration".equals(str2)))
    {
      str1 = "\"" + str1 + "\"";
    }
    else if ("dateTime".equals(str2))
    {
      mLog.fine("DateTime(String): " + str1);
      str1 = Long.toString(XmlDateTime.parse(str1));
      mLog.fine("DateTime(long): " + str1);
    }
    return str1;
  }

  Element findMappingChildNode(Node paramNode, String paramString, boolean paramBoolean)
  {
    NodeList localNodeList = paramNode.getChildNodes();
    int j = localNodeList.getLength();
    Object localObject = null;
    for (int i = 0; (i < j) && (localObject == null); i++)
      if (localNodeList.item(i).getNodeType() == 1)
      {
        Element localElement = (Element)localNodeList.item(i);
        String str = localElement.getLocalName();
        if (paramBoolean)
        {
          if ((str.equals("attribute")) && (paramString.equals(localElement.getAttribute("name"))))
            localObject = localElement;
        }
        else if ((str.equals("element")) && (paramString.equals(localElement.getAttribute("name"))))
          localObject = localElement;
        if (str.equalsIgnoreCase("formMapping"))
          localObject = findMappingChildNode(localElement, paramString, paramBoolean);
      }
    return localObject;
  }

  private String getGetOptionsForInput(Node paramNode)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    try
    {
      String str1 = expandXPathExpressions(this.opInfo.getMaxLimit(), paramNode, this.inputMapping);
      if ((str1 != null) && (!str1.equals("")))
        i = Integer.parseInt(str1);
      String str2 = expandXPathExpressions(this.opInfo.getStartRecord(), paramNode, this.inputMapping);
      if ((str2 != null) && (!str2.equals("")))
        j = Integer.parseInt(str2);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      mLog.fine("Invalid format for number of records to return or starting index.\n" + localNumberFormatException.getLocalizedMessage());
    }
    catch (GoatException localGoatException)
    {
      mLog.fine("Given Xpath expression not found in the input document.\n" + localGoatException.getLocalizedMessage());
    }
    if ((i > 0) || (j > 0))
    {
      localStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><arOptions><getOptions><maxReturnEntries>");
      localStringBuilder.append(i);
      localStringBuilder.append("</maxReturnEntries>");
      localStringBuilder.append("<startAtEntry>");
      localStringBuilder.append(j);
      localStringBuilder.append("</startAtEntry>");
      localStringBuilder.append("</getOptions></arOptions>");
    }
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.ARService
 * JD-Core Version:    0.6.1
 */