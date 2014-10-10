package com.remedy.arsys.border;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BMCBorderServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    String str1 = paramHttpServletRequest.getParameter("c");
    String str2 = paramHttpServletRequest.getParameter("g");
    String str3 = paramHttpServletRequest.getParameter("t");
    BufferedImage localBufferedImage = new BufferedImage(1, 100, 2);
    Graphics2D localGraphics2D = localBufferedImage.createGraphics();
    localGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Object localObject;
    if ((str1 != null) && (str1.length() > 5))
    {
      if (str3 != null)
        if (str3.equals("LH"))
        {
          localObject = new GradientPaint(15.0F, 15.0F, new Color(Integer.parseInt(str1, 16)), 15.0F, 110.0F, new Color(Integer.parseInt(str2, 16)), true);
          localGraphics2D.setPaint((Paint)localObject);
        }
        else
        {
          localObject = new GradientPaint(15.0F, 5.0F, new Color(Integer.parseInt(str1, 16)), 15.0F, 50.0F, new Color(Integer.parseInt(str2, 16)), true);
          localGraphics2D.setPaint((Paint)localObject);
        }
      localGraphics2D.fillRect(0, 0, 100, 100);
    }
    localGraphics2D.dispose();
    paramHttpServletResponse.setHeader("Cache-Control", "public, max-age=3600000, post-check=3600000, pre-check=3600000");
    try
    {
      localObject = paramHttpServletResponse.getOutputStream();
      paramHttpServletResponse.setContentType("image/gif");
      GifEncoder localGifEncoder = new GifEncoder(localBufferedImage, (OutputStream)localObject);
      localGifEncoder.encode();
      ((OutputStream)localObject).close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.border.BMCBorderServlet
 * JD-Core Version:    0.6.1
 */