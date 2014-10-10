package com.remedy.arsys.ws.services;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.GoatServlet;
import com.remedy.arsys.stubs.SessionData;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProtectedWSDLServlet extends GoatServlet
{
  private static Log mLog = Log.get(5);

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    String str1 = paramHttpServletRequest.getRequestURI().substring(paramHttpServletRequest.getContextPath().length() + paramHttpServletRequest.getServletPath().length());
    if ((str1 != null) && (str1.startsWith("/list")))
    {
      WSDLUtil.showWebserviceList(paramHttpServletRequest, paramHttpServletResponse);
      return;
    }
    String str2 = WSDLUtil.getWSServer(str1);
    String str3 = WSDLUtil.getWSName(str1);
    try
    {
      str2 = URLDecoder.decode(str2, "UTF-8");
      str3 = URLDecoder.decode(str3, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    mLog.fine("WSDL Request:: server: " + str2 + " webService: " + str3);
    if ((str2 == null) || (str3 == null))
      throw new GoatException(9336, paramHttpServletRequest.getRequestURL());
    WSDLUtil.showWSDL(paramHttpServletRequest, paramHttpServletResponse, str2, str3, SessionData.get().getServerLogin(str2));
  }

  protected boolean redirectToLogin(HttpServletRequest paramHttpServletRequest)
  {
    return true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.ProtectedWSDLServlet
 * JD-Core Version:    0.6.1
 */