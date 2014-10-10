package com.remedy.arsys.stubs;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import com.remedy.arsys.arreport.CharsetsExport;
import com.remedy.arsys.backchannel.NDXRequest.Parser;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.AttachmentData.AttachmentDataKey;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.type.AttachmentType;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.WebWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AttachServlet extends GoatServlet
{
  private static final int MAX_SIZE = 1073741824;
  private static final String ATT_KEY = "ATTKey";
  private static final String DISPLAY = "1";
  private static final String DOWNLOAD = "2";
  private static final String FIELD_UPLOAD = "3";
  private static final String POOL_UPLOAD = "4";

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    String str = paramHttpServletRequest.getContentType();
    if (str != null)
    {
      if (str.toLowerCase().indexOf("multipart/form-data") != -1)
      {
        MultipartParser localMultipartParser = new MultipartParser(paramHttpServletRequest, 1073741824, true, true, "UTF-8");
        doUpload(localMultipartParser, paramHttpServletResponse);
      }
      else
      {
        doDownload(paramHttpServletRequest, paramHttpServletResponse);
      }
    }
    else if ("GET".equals(paramHttpServletRequest.getMethod()))
      doDownload(paramHttpServletRequest, paramHttpServletResponse);
    else
      sendSpoofMsg(paramHttpServletResponse);
  }

  private void writeUploadReturn(HttpServletResponse paramHttpServletResponse, Map<Integer, String> paramMap1, Map<Integer, String> paramMap2)
    throws IOException
  {
    paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    localPrintWriter.print("<html><head><script>");
    Iterator localIterator;
    int i;
    Integer localInteger;
    String str;
    if (!paramMap1.isEmpty())
    {
      localPrintWriter.print("var RetVal= [");
      localIterator = paramMap1.keySet().iterator();
      for (i = 1; localIterator.hasNext(); i = 0)
      {
        if (i == 0)
          localPrintWriter.print(", ");
        localInteger = (Integer)localIterator.next();
        str = (String)paramMap1.get(localInteger);
        localPrintWriter.print("{v:" + WebWriter.escapeString(str) + ", id:" + localInteger + "}");
      }
      localPrintWriter.println("];");
    }
    if (!paramMap2.isEmpty())
    {
      localIterator = paramMap2.keySet().iterator();
      i = 1;
      localPrintWriter.print("var Msg=[");
      while (localIterator.hasNext())
      {
        if (i == 0)
          localPrintWriter.print(", ");
        localInteger = (Integer)localIterator.next();
        str = (String)paramMap2.get(localInteger);
        localPrintWriter.print("{v:" + WebWriter.escapeString(str) + ", id:" + localInteger + "}");
        i = 0;
      }
      localPrintWriter.println("];");
    }
    localPrintWriter.println("</script></head><body></body></html>");
  }

  private void doUpload(MultipartParser paramMultipartParser, HttpServletResponse paramHttpServletResponse)
    throws IOException, GoatException
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    for (Part localPart = paramMultipartParser.readNextPart(); localPart != null; localPart = paramMultipartParser.readNextPart())
      while (true)
      {
        AttachmentData.AttachmentDataKey localAttachmentDataKey = null;
        try
        {
          if (!localPart.isFile())
          {
            sendSpoofMsg(paramHttpServletResponse);
            return;
          }
          FilePart localFilePart = (FilePart)localPart;
          String str1 = localFilePart.getName();
          String str2 = localFilePart.getFileName();
          InputStream localInputStream = localFilePart.getInputStream();
          if (str2 == null)
          {
            localPart = paramMultipartParser.readNextPart();
            break;
          }
          if ((str1 == null) || (str2 == null) || (localInputStream == null))
          {
            sendSpoofMsg(paramHttpServletResponse);
            return;
          }
          NDXRequest.Parser localParser = new NDXRequest.Parser(str1);
          String str3 = localParser.next();
          String str4 = localParser.next();
          if ((str3 == null) || (str4 == null))
          {
            sendSpoofMsg(paramHttpServletResponse);
            return;
          }
          String str5 = SessionData.get().getID();
          str4 = str4 + str5.length() + "/" + str5;
          localAttachmentDataKey = new AttachmentData.AttachmentDataKey(str4);
          if ("3".equals(str3))
          {
            assert (localParser.next() == null);
            AttachmentData.uploadToField(localAttachmentDataKey, str2, localInputStream);
          }
          else if ("4".equals(str3))
          {
            localObject = parseAndGetEmptyFieldsList(localParser);
            localAttachmentDataKey = AttachmentData.uploadToPool(localAttachmentDataKey.getServer(), localAttachmentDataKey.getSchema(), localAttachmentDataKey.getEntryID(), localAttachmentDataKey.getFieldID(), (int[])localObject, localAttachmentDataKey.getScreenName(), localAttachmentDataKey.getSessionID(), str2, localInputStream);
          }
          else
          {
            sendSpoofMsg(paramHttpServletResponse);
            return;
          }
          Object localObject = new AttachmentType(localAttachmentDataKey);
          localHashMap2.put(Integer.valueOf(localAttachmentDataKey.getFieldID()), ((AttachmentType)localObject).toPrimitive());
        }
        catch (GoatException localGoatException)
        {
          assert (localAttachmentDataKey != null);
          localHashMap1.put(Integer.valueOf(localAttachmentDataKey.getFieldID()), localGoatException.toString());
        }
      }
    writeUploadReturn(paramHttpServletResponse, localHashMap2, localHashMap1);
  }

  private String getAttKey(String paramString)
  {
    return paramString.substring(paramString.indexOf('=') + 1).trim();
  }

  private void doDownload(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, GoatException
  {
    String str1 = null;
    if ((paramHttpServletRequest.getQueryString() == null) || (paramHttpServletRequest.getQueryString() == ""))
      str1 = paramHttpServletRequest.getParameter("ATTKey");
    else
      str1 = URLDecoder.decode(getAttKey(paramHttpServletRequest.getQueryString()), "UTF-8");
    try
    {
      if (str1 != null)
      {
        NDXRequest.Parser localParser = new NDXRequest.Parser(str1);
        String str2 = localParser.next();
        boolean bool = "1".equals(str2);
        if ((!bool) && (!"2".equals(str2)))
          str2 = null;
        if (str2 != null)
        {
          String str3 = localParser.next();
          if (str3 != null)
          {
            AttachmentData.AttachmentDataKey localAttachmentDataKey = new AttachmentData.AttachmentDataKey(str3);
            AttachmentData localAttachmentData = AttachmentData.get(localAttachmentDataKey);
            localAttachmentData.setBusy(true);
            try
            {
              String str4 = "";
              String str5;
              if (bool)
              {
                paramHttpServletResponse.setContentType(getMimeType(localAttachmentData.getName(), localAttachmentDataKey.getServer()));
                str5 = Pattern.compile("attachment", 2).matcher(localAttachmentData.getName()).replaceAll("DisplayContent");
                str4 = "inline;filename=" + URLEncoder.encode(stripFileName(str5), "UTF-8");
              }
              else
              {
                paramHttpServletResponse.setContentType("application/octet-stream");
                str5 = localParser.next();
                String str6 = str5 != null ? str5 : localAttachmentData.getName();
                if (browserIsRFC2184Compatible(paramHttpServletRequest))
                  str4 = "attachment;filename*=utf-8'" + SessionData.get().getLocale() + "'" + URLEncoder.encode(stripFileName(str6), "UTF-8");
                else
                  str4 = "attachment;filename=" + URLEncoder.encode(stripFileName(str6), "UTF-8");
              }
              paramHttpServletResponse.setHeader("Content-Disposition", str4);
              localAttachmentData.writeTo(paramHttpServletResponse.getOutputStream());
            }
            finally
            {
              localAttachmentData.setBusy(false);
            }
            if (canDeleteAttachment(localAttachmentDataKey.getScreenName()))
              AttachmentData.removeAllUnused(localAttachmentDataKey.getScreenName(), localAttachmentDataKey.getSessionID());
            return;
          }
        }
      }
      sendSpoofMsg(paramHttpServletResponse);
    }
    catch (GoatException localGoatException)
    {
      sendErrMsg(paramHttpServletResponse, localGoatException, false);
    }
  }

  private String stripFileName(String paramString)
  {
    String str = paramString;
    if (str != null)
    {
      int i = str.lastIndexOf("/");
      if (i != -1)
      {
        str = str.substring(i + 1, str.length());
      }
      else
      {
        i = str.lastIndexOf("\\");
        if (i != -1)
          str = str.substring(i + 1, str.length());
      }
    }
    return str;
  }

  private String getMimeType(String paramString1, String paramString2)
  {
    String str1 = Configuration.getInstance().getMimeType(paramString1);
    String str2 = CharsetsExport.getUseServer(paramString2);
    str2 = str2.toUpperCase(Locale.US);
    if ((str1 != null) && (str1.startsWith("text/")) && (str2 != null) && (!str2.equals("")))
      str1 = str1 + ";charset=" + str2;
    return str1;
  }

  private int[] parseAndGetEmptyFieldsList(NDXRequest.Parser paramParser)
    throws GoatException
  {
    String str = paramParser.next();
    try
    {
      int i = Integer.parseInt(str);
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < arrayOfInt.length; j++)
      {
        str = paramParser.next();
        arrayOfInt[j] = (int)Long.parseLong(str);
      }
      return arrayOfInt;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Some one is trying to spoof. Empty field list is incorrect", localNumberFormatException);
    }
  }

  private void sendSpoofMsg(HttpServletResponse paramHttpServletResponse)
    throws IOException, GoatException
  {
    MLog.fineAndConsole("Somebody is trying to spoof attachment servlet parameters");
    sendErrMsg(paramHttpServletResponse, new GoatException("Somebody is trying to spoof attachment servlet parameters"), false);
  }

  private void sendErrMsg(HttpServletResponse paramHttpServletResponse, GoatException paramGoatException, boolean paramBoolean)
    throws IOException
  {
    MLog.log(Level.FINE, "", paramGoatException);
    String str = paramGoatException.toString();
    paramHttpServletResponse.setContentType("text/html;charset=UTF-8");
    PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
    if ((paramBoolean) && (!paramGoatException.amInternalError()))
    {
      localPrintWriter.print("<html><head><script>var Msg=");
      localPrintWriter.print(WebWriter.escapeString(str));
      localPrintWriter.println(";</script></head><body></body></html>");
    }
    else
    {
      localPrintWriter.print("<html><head><title>");
      localPrintWriter.print(str);
      localPrintWriter.println("</title></head><body><h1>");
      localPrintWriter.print(str);
      localPrintWriter.println("</h1></body></html>");
    }
  }

  private boolean canDeleteAttachment(String paramString)
  {
    try
    {
      long l = Long.parseLong(paramString);
    }
    catch (Exception localException)
    {
      return false;
    }
    return true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.AttachServlet
 * JD-Core Version:    0.6.1
 */