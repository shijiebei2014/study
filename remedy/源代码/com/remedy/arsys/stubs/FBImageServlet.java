package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FBImageServlet extends GoatHttpServlet
{
  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    Map localMap = paramHttpServletRequest.getParameterMap();
    if (Validator.URLParamHasXSSTag(localMap))
    {
      paramHttpServletResponse.sendError(400);
      return;
    }
    String str = "/plugins/Flashboard/params";
    RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher(str);
    if (localRequestDispatcher != null)
    {
      paramHttpServletRequest.setAttribute("fbimageservlet_fwd", "true");
      localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.FBImageServlet
 * JD-Core Version:    0.6.1
 */