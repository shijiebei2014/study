package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatImage;
import com.remedy.arsys.log.Log;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends GoatServlet
{
  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    MLog.fine("ImageServlet: URI=" + paramHttpServletRequest.getRequestURI());
    String[] arrayOfString = getI18nFriendlyPathElements(paramHttpServletRequest);
    if ((arrayOfString != null) && (arrayOfString.length == 1))
    {
      Globule localGlobule = GoatImage.getImageGlobule(arrayOfString[0]);
      if (localGlobule != null)
      {
        localGlobule.transmit(paramHttpServletRequest, paramHttpServletResponse);
        return;
      }
    }
    paramHttpServletResponse.sendError(404);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ImageServlet
 * JD-Core Version:    0.6.1
 */