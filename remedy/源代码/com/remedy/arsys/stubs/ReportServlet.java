package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.GoatException;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReportServlet extends GoatHttpServlet
{
  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    String str = "/plugins/Report/params";
    RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher(str);
    if (localRequestDispatcher != null)
      localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ReportServlet
 * JD-Core Version:    0.6.1
 */