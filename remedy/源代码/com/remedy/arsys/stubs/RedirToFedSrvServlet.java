package com.remedy.arsys.stubs;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.session.Params;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirToFedSrvServlet extends GoatServlet
{
  private static final long serialVersionUID = 100L;
  public static final String VIEWFORMSERVLET_BASE_URI = "/servlet/ViewFormServlet?";

  protected final void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, ServletException, GoatException
  {
    String str1 = "";
    try
    {
      paramHttpServletResponse.setHeader("Cache-Control", "no-store");
      paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
      paramHttpServletResponse.setHeader("Pragma", "no-cache");
      paramHttpServletResponse.setDateHeader("Expires", 0L);
      Params localParams = new Params(paramHttpServletRequest);
      Map localMap = localParams.getMap();
      if (localMap == null)
        localMap = paramHttpServletRequest.getParameterMap();
      String str2 = localParams.get("server");
      str1 = localParams.get("fedsrv");
      String str3 = localParams.get("form");
      String str4 = localParams.get("view");
      String str5 = localParams.get("app");
      if ((str2 == null) || (str1 == null) || (str3 == null))
      {
        paramHttpServletResponse.sendError(404);
        return;
      }
      str1 = str1.trim().toLowerCase();
      RSAAuthenticator localRSAAuthenticator = null;
      Entry localEntry = null;
      localRSAAuthenticator = new RSAAuthenticator(str2);
      try
      {
        localEntry = localRSAAuthenticator.getPublicKeyEntry(str1);
      }
      catch (Exception localException2)
      {
        throw new GoatException(9282, str1);
      }
      Value localValue1;
      Value localValue2;
      if ((localEntry == null) || ((localValue1 = (Value)localEntry.get(Integer.valueOf(RSAAuthenticator.SERVER_KEY_MAP_SCHEMA_KEY_IDS[0]))) == null) || ((localValue2 = (Value)localEntry.get(Integer.valueOf(RSAAuthenticator.SERVER_KEY_MAP_SCHEMA_KEY_IDS[2]))) == null))
        throw new GoatException(9282, str1);
      String str6 = (String)localValue1.getValue();
      String str7 = (String)localValue2.getValue();
      if ((str6.length() == 0) || (str7.length() == 0))
        throw new GoatException(9282, str1);
      StringBuilder localStringBuilder = new StringBuilder(str7);
      localStringBuilder.append("/servlet/ViewFormServlet?").append("server=" + str1).append("&form=" + str3);
      if ((str4 != null) && (str4.length() > 0))
        localStringBuilder.append("&view=" + str4);
      if ((str5 != null) && (str5.length() > 0))
        localStringBuilder.append("&app=" + str5);
      localStringBuilder.append("&enccred=1");
      localStringBuilder.append("&gw=1");
      String str8;
      if (localMap != null)
      {
        localObject1 = localMap.keySet();
        if (localObject1 != null)
        {
          localObject2 = ((Set)localObject1).iterator();
          while (((Iterator)localObject2).hasNext())
          {
            str8 = (String)((Iterator)localObject2).next();
            if (((str8.charAt(0) == 'F') || ((str8.charAt(0) == 'f') && (str8.length() > 1))) && (str8.charAt(1) >= '0') && (str8.charAt(1) <= '9'))
              localStringBuilder.append("&").append(str8).append("=").append(URLEncoder.encode(localParams.getMap() == null ? paramHttpServletRequest.getParameter(str8) : localParams.get(str8), "UTF-8"));
          }
        }
      }
      Object localObject1 = SessionData.get();
      if (localObject1 == null)
      {
        paramHttpServletResponse.sendError(401);
        return;
      }
      Object localObject2 = ((SessionData)localObject1).getcTimeZone();
      if (localObject2 != null)
        localStringBuilder.append("&usertimezone=" + (String)localObject2);
      String str9;
      String str10;
      try
      {
        str8 = localRSAAuthenticator.encrypt(((SessionData)localObject1).getUserName(), str1);
        str9 = localRSAAuthenticator.encrypt(((SessionData)localObject1).getPassword(), str1);
        str10 = localRSAAuthenticator.encrypt(((SessionData)localObject1).getAuthentication(), str1);
      }
      catch (Exception localException3)
      {
        localException3.printStackTrace();
        throw new GoatException(9285, str1);
      }
      paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
      PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
      localPrintWriter.print("<html><body onLoad=\"document.forms[0].submit()\">");
      localPrintWriter.print("<form name=\"redirForm\" action=\"" + localStringBuilder + "\" method=\"post\">");
      localPrintWriter.print("<input type=\"hidden\" name=\"username\" value=\"" + str8 + "\"/>");
      localPrintWriter.print("<input type=\"hidden\" name=\"pwd\" value=\"" + str9 + "\"/>");
      localPrintWriter.print("<input type=\"hidden\" name=\"auth\" value=\"" + str10 + "\"/>");
      localPrintWriter.print("</form>");
      localPrintWriter.print("</body></html>");
      localPrintWriter.flush();
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
      throw localGoatException;
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
      throw new GoatException(9286, str1);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.RedirToFedSrvServlet
 * JD-Core Version:    0.6.1
 */