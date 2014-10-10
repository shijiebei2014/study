package com.remedy.arsys.arreport;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.legacyshared.ARDataFormat;
import com.remedy.arsys.stubs.GoatHttpServlet;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NativeReportServlet extends GoatHttpServlet
{
  private static final long serialVersionUID = 1L;

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    new NativeReportManager(paramHttpServletRequest, paramHttpServletResponse);
  }

  protected static String formatDatesTimes(String paramString1, String paramString2, String paramString3, int paramInt, Long paramLong, String paramString4, String paramString5)
  {
    ARDataFormat localARDataFormat = new ARDataFormat();
    if ((paramString3 == null) || (paramString3.trim().length() == 0))
      return "";
    if (paramString1 == null)
      paramString1 = Locale.getDefault().toString();
    if (paramString2 == null)
      paramString2 = TimeZone.getDefault().getID();
    if (paramInt == 3)
      paramString2 = "GMT";
    boolean bool = (paramLong != null) && (paramLong.equals(ARUserPreferences.CUSTOM_TIME_FORMAT));
    String str = localARDataFormat.getCustomFormat(bool, paramString4, paramString5, paramInt, paramString1);
    if (paramLong != null)
      return localARDataFormat.formatDateTime(ARDataFormat.mapTimeFormatLongToLegacyString(paramLong), paramInt, paramString3, paramString1, paramString2, str);
    return "";
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.NativeReportServlet
 * JD-Core Version:    0.6.1
 */