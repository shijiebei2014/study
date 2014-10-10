package com.remedy.arsys.stubs;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.AttachmentValue;
import com.remedy.arsys.arreport.CharsetsExport;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.sharedresource.ARSharedResource;
import com.remedy.arsys.goat.sharedresource.DefaultResourceObject;
import com.remedy.arsys.goat.sharedresource.IResourceObject;
import com.remedy.arsys.log.Log;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SharedResourceServlet extends AuthenticationHelperServlet
{
  protected FormContext arrangeFormContext(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException
  {
    return new FormContext(null, "../../../../", paramHttpServletRequest.getContextPath() + "/", getServletContext().getRealPath("/"));
  }

  protected void processRequestInfo(RequestInfo paramRequestInfo, HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, GoatException, IOException
  {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    Integer localInteger = null;
    int i = -1;
    String str6 = paramHttpServletRequest.getPathInfo();
    String str7 = paramHttpServletRequest.getParameter("server");
    Object localObject1 = null;
    if ((str6 == null) || (str7 == null))
    {
      paramHttpServletResponse.sendError(404);
      return;
    }
    assert (str6.charAt(0) == '/');
    str6 = str6.substring(1);
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if (str6.startsWith("attachment/"))
    {
      localObject2 = null;
      localObject3 = str6.split("/");
      assert (localObject3.length == 4);
      try
      {
        if (localObject3.length == 4)
        {
          str5 = localObject3[(localObject3.length - 1)];
          i = Integer.parseInt(localObject3[(localObject3.length - 2)]);
          str4 = localObject3[(localObject3.length - 3)];
        }
        else
        {
          return;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        MLog.severe("SharedResourceServlet Error parsing field ID - " + localNumberFormatException.getMessage());
        return;
      }
      localObject4 = SessionData.get().getServerLogin(str7);
      try
      {
        AttachmentValue localAttachmentValue = AttachmentData.getAttachmentValue((ServerLogin)localObject4, str4, i, str5);
        if (localAttachmentValue != null)
        {
          localObject2 = ((ServerLogin)localObject4).getEntryBlob(str4, str5, i);
          if (localObject2 != null)
          {
            str2 = localAttachmentValue.getName();
            str3 = getMimeType(str2, str7);
            localObject1 = new DefaultResourceObject((byte[])localObject2, str3);
            if ((str3 != null) && (str3.equals("unknown/unknown")))
              paramHttpServletResponse.setContentType("application/octet-stream");
            String str8 = Pattern.compile("attachment", 2).matcher(str2).replaceAll("DisplayContent");
            String str9 = "inline;filename=" + URLEncoder.encode(stripFileName(str8), "UTF-8");
            paramHttpServletResponse.setHeader("Content-Disposition", str9);
          }
        }
      }
      catch (ARException localARException)
      {
        MLog.severe("SharedResourceServlet Error - " + localARException.getMessage());
      }
    }
    else
    {
      localObject2 = str6.split("/");
      assert (localObject2.length == 2);
      if (localObject2.length == 2)
      {
        str1 = localObject2[(localObject2.length - 1)];
        localInteger = getType(localObject2[(localObject2.length - 2)]);
      }
      else
      {
        return;
      }
      localObject3 = paramHttpServletRequest.getParameter("subtype");
      if (localInteger != null)
      {
        localObject4 = ARSharedResource.get(str7, str1, localInteger.intValue(), (String)localObject3, null);
        if (localObject4 != null)
          localObject1 = ((ARSharedResource)localObject4).getResourceObject();
      }
    }
    if (localObject1 == null)
    {
      paramHttpServletResponse.sendError(404);
      return;
    }
    ((IResourceObject)localObject1).transmit(paramHttpServletRequest, paramHttpServletResponse);
  }

  private Integer getType(String paramString)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("image", Integer.valueOf(1));
    localHashMap.put("template", Integer.valueOf(0));
    localHashMap.put("custom", Integer.valueOf(1000));
    Integer localInteger;
    if (paramString.equalsIgnoreCase("image"))
      localInteger = (Integer)localHashMap.get("image");
    else if (paramString.equalsIgnoreCase("template"))
      localInteger = (Integer)localHashMap.get("template");
    else if (paramString.equalsIgnoreCase("custom"))
      localInteger = (Integer)localHashMap.get("custom");
    else
      return null;
    return localInteger;
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
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.SharedResourceServlet
 * JD-Core Version:    0.6.1
 */