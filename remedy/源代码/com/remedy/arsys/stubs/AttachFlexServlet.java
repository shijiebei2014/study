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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AttachFlexServlet extends GoatHttpServlet
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
        doUpload(localMultipartParser, paramHttpServletRequest, paramHttpServletResponse);
      }
    }
    else
      doDownload(paramHttpServletRequest, paramHttpServletResponse);
  }

  private void doUpload(MultipartParser paramMultipartParser, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, GoatException
  {
    String str1 = "ERROR";
    String str2 = paramHttpServletRequest.getParameter("cmdKey");
    String str3 = paramHttpServletRequest.getParameter("attName");
    String str4 = "";
    int i = 0;
    try
    {
      if (GoatServlet.staticSetupSessionData(paramHttpServletRequest))
      {
        str4 = SessionData.get().getID();
        i = 1;
      }
    }
    catch (GoatException localGoatException1)
    {
      str1 = "SESSION_ERROR";
    }
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    for (Part localPart = paramMultipartParser.readNextPart(); localPart != null; localPart = paramMultipartParser.readNextPart())
      while (true)
      {
        localObject1 = null;
        try
        {
          if (localPart.isFile())
          {
            FilePart localFilePart = (FilePart)localPart;
            InputStream localInputStream = localFilePart.getInputStream();
            if (str3 == null)
            {
              localPart = paramMultipartParser.readNextPart();
              break;
            }
            if ((str2 == null) || (str3 == null) || (localInputStream == null))
            {
              sendSpoofMsg(paramHttpServletResponse);
              return;
            }
            NDXRequest.Parser localParser = new NDXRequest.Parser(str2);
            String str5 = localParser.next();
            String str6 = localParser.next();
            if ((str5 == null) || (str6 == null))
            {
              sendSpoofMsg(paramHttpServletResponse);
              return;
            }
            str6 = str6 + str4.length() + "/" + str4;
            localObject1 = new AttachmentData.AttachmentDataKey(str6);
            if (i != 0)
            {
              if ("3".equals(str5))
              {
                assert (localParser.next() == null);
                AttachmentData.uploadToField((AttachmentData.AttachmentDataKey)localObject1, str3, localInputStream);
              }
              else if ("4".equals(str5))
              {
                localObject2 = parseAndGetEmptyFieldsList(localParser);
                localObject1 = AttachmentData.uploadToPool(((AttachmentData.AttachmentDataKey)localObject1).getServer(), ((AttachmentData.AttachmentDataKey)localObject1).getSchema(), ((AttachmentData.AttachmentDataKey)localObject1).getEntryID(), ((AttachmentData.AttachmentDataKey)localObject1).getFieldID(), (int[])localObject2, ((AttachmentData.AttachmentDataKey)localObject1).getScreenName(), ((AttachmentData.AttachmentDataKey)localObject1).getSessionID(), str3, localInputStream);
              }
              else
              {
                sendSpoofMsg(paramHttpServletResponse);
                return;
              }
              Object localObject2 = new AttachmentType((AttachmentData.AttachmentDataKey)localObject1);
              str1 = ((AttachmentType)localObject2).toPrimitive();
            }
            localInputStream.close();
          }
        }
        catch (GoatException localGoatException2)
        {
          assert (localObject1 != null);
          localHashMap1.put(Integer.valueOf(((AttachmentData.AttachmentDataKey)localObject1).getFieldID()), localGoatException2.toString());
        }
      }
    Object localObject1 = paramHttpServletResponse.getWriter();
    ((PrintWriter)localObject1).print(str1);
  }

  private String getAttKey(String paramString)
  {
    return paramString.substring(paramString.indexOf('=') + 1).trim();
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

  private void doDownload(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, GoatException
  {
    String str = null;
    byte[] arrayOfByte = new byte[1024];
    if (!GoatServlet.staticSetupSessionData(paramHttpServletRequest))
      return;
    if ((paramHttpServletRequest.getQueryString() == null) || (paramHttpServletRequest.getQueryString() == ""))
      str = paramHttpServletRequest.getParameter("ATTKey");
    else
      str = URLDecoder.decode(getAttKey(paramHttpServletRequest.getQueryString()), "UTF-8");
    paramHttpServletResponse.setContentType("application/zip");
    ServletOutputStream localServletOutputStream = paramHttpServletResponse.getOutputStream();
    File localFile = AttachmentData.downloadAllAttachment(str);
    FileInputStream localFileInputStream = new FileInputStream(localFile);
    int i = 0;
    while ((i = localFileInputStream.read(arrayOfByte)) > 0)
      localServletOutputStream.write(arrayOfByte, 0, i);
    localServletOutputStream.close();
    localFileInputStream.close();
    localFile.delete();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.AttachFlexServlet
 * JD-Core Version:    0.6.1
 */