package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.GoatException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotFoundServlet extends GoatHttpServlet
{
  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    paramHttpServletResponse.sendError(404);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.NotFoundServlet
 * JD-Core Version:    0.6.1
 */