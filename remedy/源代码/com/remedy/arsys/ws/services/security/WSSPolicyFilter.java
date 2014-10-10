package com.remedy.arsys.ws.services.security;

import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.Reference;
import com.bmc.arsys.api.ReferenceType;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatContainer;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;

public final class WSSPolicyFilter
  implements Filter
{
  private FilterConfig filterConfig = null;
  private static Log logger = Log.get(5);
  private static final String POLICY_DIR = "policies";
  private static final String SVR_NM = "server";
  private static final String WEB_SVC_NM = "webService";

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
    int i = 0;
    if (this.filterConfig == null)
      return;
    ServerLogin localServerLogin = null;
    try
    {
      String str1 = paramServletRequest.getParameter("server");
      String str2 = paramServletRequest.getParameter("webService");
      if (str1 != null)
        localServerLogin = ServerLogin.getAdmin(str1);
      Vector localVector = null;
      String str3 = null;
      if ((localServerLogin != null) && (str1 != null))
        str3 = getPolicyId(localServerLogin, str1, str2);
      if (str3 != null)
      {
        Object localObject1 = paramServletRequest;
        Object localObject2 = paramServletResponse;
        WSSResponseWrapper localWSSResponseWrapper = null;
        String str4 = Configuration.getInstance().getRootPath() + File.separator + "policies";
        PolicyDetails localPolicyDetails = getSecurityPolicy(localServerLogin, str3, str4);
        localPolicyDetails.setWebSvcNmSpace(str2);
        logger.info("web service security policy:" + localPolicyDetails);
        if ((localPolicyDetails != null) && ((localPolicyDetails.getRequestSecurityAction() != null) || (localPolicyDetails.getResponseSecurityAction() != null)))
        {
          Object localObject3;
          Object localObject4;
          Object localObject5;
          ByteArrayOutputStream localByteArrayOutputStream;
          if (localPolicyDetails.getRequestSecurityAction() != null)
          {
            localObject3 = WSPolicyProcessor.getWSReceiver(localPolicyDetails);
            localObject4 = new WSSRequestWrapper((HttpServletRequest)paramServletRequest);
            localObject5 = getMessageContext(((WSSRequestWrapper)localObject4).getRequestStream(), 1);
            ((Handler)localObject3).invoke((MessageContext)localObject5);
            logger.info("web service security processing completed for request soap message");
            localVector = (Vector)((MessageContext)localObject5).getProperty("RECV_RESULTS");
            localByteArrayOutputStream = new ByteArrayOutputStream();
            ((MessageContext)localObject5).getCurrentMessage().writeTo(localByteArrayOutputStream);
            ((WSSRequestWrapper)localObject4).setRequestStream(new ByteArrayInputStream(localByteArrayOutputStream.toByteArray()));
            localObject1 = localObject4;
          }
          if (localPolicyDetails.getResponseSecurityAction() != null)
          {
            localWSSResponseWrapper = new WSSResponseWrapper((HttpServletResponse)paramServletResponse);
            localObject2 = localWSSResponseWrapper;
          }
          paramFilterChain.doFilter((ServletRequest)localObject1, (ServletResponse)localObject2);
          i = 1;
          if ((localPolicyDetails.getResponseSecurityAction() != null) && (localWSSResponseWrapper != null))
          {
            localObject3 = getMessageContext(new ByteArrayInputStream(localWSSResponseWrapper.getData()), 2);
            if (localVector != null)
              ((MessageContext)localObject3).setProperty("RECV_RESULTS", localVector);
            localObject4 = WSPolicyProcessor.getWSSender(localPolicyDetails);
            ((Handler)localObject4).invoke((MessageContext)localObject3);
            logger.fine("web service security processing completed for response soap message");
            localObject5 = null;
            localByteArrayOutputStream = null;
            try
            {
              localByteArrayOutputStream = new ByteArrayOutputStream();
              ((MessageContext)localObject3).getCurrentMessage().writeTo(localByteArrayOutputStream);
              localObject5 = paramServletResponse.getOutputStream();
              ((OutputStream)localObject5).write(localByteArrayOutputStream.toByteArray());
              localByteArrayOutputStream.flush();
              ((OutputStream)localObject5).flush();
            }
            finally
            {
              if (localByteArrayOutputStream != null)
                localByteArrayOutputStream.close();
              if (localObject5 != null)
                ((OutputStream)localObject5).close();
            }
          }
        }
      }
      if (i == 0)
      {
        logger.fine("No security policy or security actions! Security filter processing skipped");
        paramFilterChain.doFilter(paramServletRequest, paramServletResponse);
      }
    }
    catch (Exception localException)
    {
      throw new ServletException(localException);
    }
  }

  private String getPolicyId(ServerLogin paramServerLogin, String paramString1, String paramString2)
    throws Exception
  {
    GoatContainer localGoatContainer = GoatContainer.getContainer(paramServerLogin, paramString2);
    Iterator localIterator = localGoatContainer.getContainer().getReferences().iterator();
    while (localIterator.hasNext())
    {
      Reference localReference = (Reference)localIterator.next();
      if (ReferenceType.WS_SEC_POLICY_ID.toInt() == localReference.getReferenceType().toInt())
        return localReference.getName();
    }
    return null;
  }

  private PolicyDetails getSecurityPolicy(ServerLogin paramServerLogin, String paramString1, String paramString2)
    throws Exception
  {
    return ARPolicyLoader.loadPolicyDetails(paramServerLogin, paramString1, paramString2);
  }

  private MessageContext getMessageContext(InputStream paramInputStream, int paramInt)
    throws AxisFault
  {
    AxisClient localAxisClient = new AxisClient(new NullProvider());
    MessageContext localMessageContext = new MessageContext(localAxisClient);
    Message localMessage = new Message(paramInputStream);
    localMessage.setMessageContext(localMessageContext);
    if (paramInt == 2)
      localMessageContext.setPastPivot(true);
    localMessageContext.setCurrentMessage(localMessage);
    return localMessageContext;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.WSSPolicyFilter
 * JD-Core Version:    0.6.1
 */