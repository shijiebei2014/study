package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatApplicationContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.support.BrowserType;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PublicApplicationServlet extends GoatHttpServlet
{
  protected final void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, GoatException
  {
    MLog.fine("PublicApplicationServlet: URI=" + paramHttpServletRequest.getRequestURI());
    String[] arrayOfString = GoatServlet.getI18nFriendlyPathElements(paramHttpServletRequest);
    if ((arrayOfString != null) && (arrayOfString.length > 2))
    {
      String str1 = arrayOfString[0];
      String str2 = arrayOfString[1];
      MLog.fine("PublicApplicationServlet: server=" + str1);
      MLog.fine("PublicApplicationServlet: appname=" + str2);
      if (arrayOfString[2].equals("resources"))
      {
        ServerLogin localServerLogin = ServerLogin.getAdmin(str1, null);
        GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(localServerLogin, str2);
        StringBuilder localStringBuilder = new StringBuilder("public");
        for (int i = 3; i < arrayOfString.length; i++)
        {
          if (localStringBuilder.length() > 0)
            localStringBuilder.append("/");
          localStringBuilder.append(arrayOfString[i]);
        }
        BrowserType localBrowserType = BrowserType.getTypeFromRequest(paramHttpServletRequest);
        Globule localGlobule = localGoatApplicationContainer.getResourceFile(localBrowserType, localStringBuilder.toString());
        if (localGlobule != null)
        {
          localGlobule.transmit(paramHttpServletRequest, paramHttpServletResponse);
          return;
        }
      }
    }
    paramHttpServletResponse.sendError(404);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.PublicApplicationServlet
 * JD-Core Version:    0.6.1
 */