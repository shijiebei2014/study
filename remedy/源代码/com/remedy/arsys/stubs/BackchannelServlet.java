package com.remedy.arsys.stubs;

import com.remedy.arsys.backchannel.NDXFactory;
import com.remedy.arsys.backchannel.NDXRequest;
import com.remedy.arsys.backchannel.SetOverrideAgent;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.CompressionHelper;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BackchannelServlet extends GoatServlet
{
  private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\\n");
  private static Log MPerformanceLog = Log.get(8);

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    try
    {
      String str2 = paramHttpServletRequest.getMethod();
      String str1;
      if (str2.equals("GET"))
      {
        str1 = paramHttpServletRequest.getQueryString();
        if ((str1 == null) || (str1.length() <= 0))
          throw new GoatException(9350);
        int i = str1.indexOf('=');
        if (i == -1)
          throw new GoatException(9350);
        str1 = str1.substring(i + 1);
        str1 = URLDecoder.decode(str1, "UTF-8");
        int k = str1.indexOf('/');
        if (k == -1)
          throw new GoatException(9350);
        str1 = str1.substring(k + 1);
      }
      else
      {
        if (!str2.equals("POST"))
        {
          paramHttpServletResponse.sendError(404);
          return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localObject = paramHttpServletRequest.getReader();
        while (true)
        {
          m = ((Reader)localObject).read();
          if ((m == -1) || (m == 47))
            break;
          localStringBuilder.append((char)m);
        }
        int m = Integer.parseInt(localStringBuilder.toString());
        if ((m < 0) || (m > paramHttpServletRequest.getContentLength()))
        {
          MLog.fine("Corrupted content length " + m + " - " + paramHttpServletRequest.getContentLength() + " buff is " + localStringBuilder.toString());
          throw new GoatException(9350);
        }
        char[] arrayOfChar = new char[m];
        n = 0;
        int i1;
        while ((m > 0) && ((i1 = ((Reader)localObject).read(arrayOfChar, n, m)) != -1))
        {
          n += i1;
          m -= i1;
        }
        if (n != arrayOfChar.length)
        {
          MLog.fine("Corrupted chars " + n + " - " + arrayOfChar.length + " buff is " + localStringBuilder.toString());
          throw new GoatException(9350);
        }
        str1 = new String(arrayOfChar);
      }
      int j = str1.indexOf('/');
      Object localObject = "";
      if (j != -1)
        localObject = str1.substring(0, j);
      MPerformanceLog.fine("Backchannel start: " + (String)localObject);
      long l1 = System.currentTimeMillis();
      int n = 1;
      if ((str1.contains("GetHTMLScriptForViewField")) || (str1.contains("LoadQueryWidget")) || (str1.contains("GetFieldDefaults")) || (str1.contains("GetARTemplate")))
        n = 0;
      if (n != 0)
      {
        paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
        paramHttpServletResponse.setHeader("Pragma", "no-cache");
      }
      else
      {
        long l2 = Configuration.getInstance().getFormHTMLJSExpiryInterval();
        paramHttpServletResponse.setDateHeader("Expires", System.currentTimeMillis() + l2 * 1000L);
        paramHttpServletResponse.addHeader("Cache-Control", "public,max-age=" + l2);
      }
      paramHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
      paramHttpServletResponse.setContentType("text/plain;charset=UTF-8");
      ServletOutputStream localServletOutputStream = null;
      if ((str1.contains("GetQBETableEntryList")) || (str1.contains("GetTableEntryList")))
        localServletOutputStream = paramHttpServletResponse.getOutputStream();
      NDXRequest localNDXRequest = NDXFactory.handleRequest(str1, localServletOutputStream);
      if (localNDXRequest == null)
      {
        MLog.fine("NDXFactory.handleRequest returned null, wad: " + str1);
        throw new GoatException(9350);
      }
      String str3 = localNDXRequest.toString();
      if (((localNDXRequest instanceof SetOverrideAgent)) && (!((SetOverrideAgent)localNDXRequest).getOverride()))
      {
        SessionData.get().dispose(true);
        paramHttpServletRequest.getSession(false).invalidate();
        String str4 = paramHttpServletRequest.getContextPath();
        String str5 = str4 + "/shared/login.jsp";
        str5 = Validator.sanitizeCRandLF(str5);
        str3 = str3 + "window.location='" + str5 + "';";
      }
      long l3 = System.currentTimeMillis();
      if (!localNDXRequest.isOutputStreamed())
        sendResponse(paramHttpServletRequest, paramHttpServletResponse, str3);
      long l4 = System.currentTimeMillis();
      MPerformanceLog.fine("Backchannel end: " + (String)localObject + ": Process: " + (l3 - l1) + "; Send: " + (l4 - l3) + "; Chars: " + str3.length());
    }
    catch (IOException localIOException)
    {
      MLog.fine(getStackTraceString(localIOException));
      throw new GoatException(9350);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      MLog.fine(getStackTraceString(localNumberFormatException));
      throw new GoatException(9350);
    }
  }

  private void sendResponse(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, String paramString)
    throws GoatException
  {
    try
    {
      if (Configuration.getInstance().getEnableBackChannelCompression())
      {
        CompressionHelper.compressContents(paramString, paramHttpServletRequest, paramHttpServletResponse, true);
      }
      else
      {
        paramHttpServletResponse.setContentLength(paramString.getBytes("UTF-8").length);
        paramHttpServletResponse.getWriter().print(paramString);
      }
    }
    catch (IOException localIOException)
    {
      MLog.fine(getStackTraceString(localIOException));
      throw new GoatException(9350);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      MLog.fine(getStackTraceString(localNumberFormatException));
      throw new GoatException(9350);
    }
  }

  protected void invalidSession(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException
  {
    String str = NDXRequest.returnError(paramHttpServletRequest.getLocale().toString(), new GoatException(9201));
    paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    paramHttpServletResponse.setHeader("Pragma", "no-cache");
    paramHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
    sendResponse(paramHttpServletRequest, paramHttpServletResponse, str);
  }

  protected void errorPage(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, GoatException paramGoatException)
  {
    try
    {
      String str = NDXRequest.returnError(paramHttpServletRequest.getLocale().toString(), paramGoatException);
      sendResponse(paramHttpServletRequest, paramHttpServletResponse, str);
    }
    catch (GoatException localGoatException)
    {
      MLog.fine(getStackTraceString(localGoatException));
    }
  }

  private static final String getStackTraceString(Throwable paramThrowable)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramThrowable.getClass().getName() + ": " + paramThrowable.getMessage());
    StackTraceElement[] arrayOfStackTraceElement1 = paramThrowable.getStackTrace();
    if (arrayOfStackTraceElement1 == null)
      return localStringBuilder.toString();
    for (StackTraceElement localStackTraceElement : arrayOfStackTraceElement1)
    {
      localStringBuilder.append(LINE_SEPARATOR);
      localStringBuilder.append("    " + localStackTraceElement);
    }
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.BackchannelServlet
 * JD-Core Version:    0.6.1
 */