package com.remedy.arsys.stubs;

import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class OverrideServlet extends GoatServlet
{
  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    String str1 = paramHttpServletRequest.getParameter("ipoverride");
    if (str1 == null)
      str1 = "";
    if ((str1 != null) && (str1.equals("1")))
      SessionData.get().setOverride(1);
    else
      SessionData.get().setOverride(0);
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    String str2;
    if (str1.equals("1"))
    {
      str2 = (String)localHttpSession.getAttribute("overrideURI");
      CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
      paramHttpServletResponse.sendRedirect(Validator.sanitizeCRandLF(str2));
    }
    else
    {
      SessionData.get().dispose(true);
      localHttpSession.invalidate();
      str2 = paramHttpServletRequest.getContextPath();
      String str3 = str2 + "/shared/login.jsp";
      paramHttpServletResponse.sendRedirect(Validator.sanitizeCRandLF(str3));
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.OverrideServlet
 * JD-Core Version:    0.6.1
 */