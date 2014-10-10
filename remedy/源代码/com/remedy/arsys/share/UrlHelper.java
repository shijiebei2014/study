package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatApplicationContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.GoatHttpServlet;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.BrowserType;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class UrlHelper
{
  private int size;
  private String originalServer;
  private String server;
  private String app;
  private String form;
  private String view;
  private String file;
  private String appResource;

  public UrlHelper(HttpServletRequest paramHttpServletRequest, boolean paramBoolean)
    throws GoatException
  {
    String[] arrayOfString = GoatHttpServlet.getI18nFriendlyPathElements(paramHttpServletRequest);
    this.size = 0;
    if (arrayOfString == null)
      return;
    if (arrayOfString.length < 2)
    {
      if (arrayOfString.length > 0)
      {
        this.server = arrayOfString[0];
        if ((paramBoolean) && (arrayOfString.length > 1) && (arrayOfString[1].length() > 0))
          this.app = arrayOfString[1];
      }
      return;
    }
    this.server = arrayOfString[(this.size++)];
    this.originalServer = this.server;
    if ("".equals(this.server))
    {
      this.size -= 1;
      this.server = null;
      return;
    }
    this.server = Configuration.getInstance().getLongName(this.server);
    if ((arrayOfString.length > this.size + 1) && (arrayOfString[(this.size + 1)].equals("uds.js")))
      this.size += 1;
    if ((paramBoolean) && (arrayOfString[this.size] != null) && (!arrayOfString[this.size].equals("uds.js")))
    {
      this.app = arrayOfString[(this.size++)];
      if ("".equals(this.app))
      {
        this.size -= 1;
        this.app = null;
        return;
      }
    }
    Object localObject;
    if (arrayOfString.length > this.size)
      if ("".equals(arrayOfString[this.size]))
      {
        localObject = new ArrayList(Arrays.asList(arrayOfString));
        ((List)localObject).remove(this.size);
        arrayOfString = (String[])((List)localObject).toArray(new String[0]);
      }
      else
      {
        this.form = arrayOfString[(this.size++)];
      }
    if ((this.form != null) && (this.form.equals("uds.js")))
    {
      this.form = null;
      this.view = null;
      this.file = "uds.js";
      return;
    }
    if ("".equals(this.form))
    {
      this.size -= 1;
      this.form = null;
      return;
    }
    if (arrayOfString.length > this.size)
      this.view = arrayOfString[(this.size++)];
    if (("".equals(this.view)) || ("form.html".equals(this.view)))
    {
      this.size -= 1;
      this.view = null;
      return;
    }
    if (arrayOfString.length > this.size)
      this.file = arrayOfString[this.size];
    if ((paramBoolean) && (this.app != null))
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(this.server);
      localObject = GoatApplicationContainer.get(localServerLogin, this.app);
      if ((this.form != null) && (this.form.equals("resources")))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = arrayOfString.length;
        int j = i - 1;
        for (int k = 3; k < i; k++)
        {
          String str = arrayOfString[k];
          if ((k >= j) || (!isPseudoElement(str)))
          {
            if (localStringBuilder.length() > 0)
              localStringBuilder.append("/");
            localStringBuilder.append(str);
          }
        }
        this.appResource = localStringBuilder.toString();
      }
      else if (this.form == null)
      {
        this.form = ((GoatApplicationContainer)localObject).getPrimaryForm();
        this.view = ((GoatApplicationContainer)localObject).getPrimaryView();
      }
    }
  }

  public int getSize()
  {
    return this.size;
  }

  public String getServer()
  {
    return this.server;
  }

  public String getOriginalServer()
  {
    return this.originalServer;
  }

  public String getApp()
  {
    return this.app;
  }

  public String getForm()
  {
    return this.form;
  }

  public String getView()
  {
    return this.view;
  }

  public String getFile()
  {
    return this.file;
  }

  public String getAppResource()
  {
    return this.appResource;
  }

  public String toString()
  {
    return "server:" + this.server + "||app:" + this.app + "||form:" + this.form + "||view:" + this.view + "||file:" + this.file + "||appResource:" + this.appResource + "||size:" + this.size;
  }

  private static final boolean isPseudoElement(String paramString)
  {
    if ("8.1.00 201301251157".equals(paramString))
      return true;
    for (int i = 0; i < BrowserType.BROWSER_TYPES.length; i++)
    {
      BrowserType localBrowserType = BrowserType.BROWSER_TYPES[i];
      String str = localBrowserType.getAbbrev();
      if (str.equals(paramString))
        return true;
    }
    return false;
  }

  public static String getCacheID(Form paramForm, Form.ViewInfo paramViewInfo)
    throws GoatException
  {
    String str = FormContext.get().getFieldGraphURLParam(paramForm.getServerLogin().getPermissionsKey(), paramViewInfo.getID(), paramForm);
    return str;
  }

  public static String removeCacheID(String paramString)
  {
    return paramString.replaceAll("cacheid=(.*?)(&|$)", "$2");
  }

  public static String newUrlForCacheID(String paramString1, String paramString2, Form paramForm, Form.ViewInfo paramViewInfo)
    throws IOException, GoatException
  {
    String str1 = getCacheID(paramForm, paramViewInfo);
    int i = paramString2 == null ? -1 : paramString2.indexOf("cacheid=");
    StringBuilder localStringBuilder;
    String str2;
    if (i == -1)
    {
      localStringBuilder = new StringBuilder(paramString1);
      localStringBuilder.append("?");
      if (paramString2 != null)
      {
        str2 = Validator.URLParamsEscape(paramString2);
        localStringBuilder.append(str2).append("&");
      }
      localStringBuilder.append("cacheid=").append(str1);
      return localStringBuilder.toString();
    }
    if (!paramString2.regionMatches(i + 8, str1, 0, str1.length()))
    {
      localStringBuilder = new StringBuilder(paramString1);
      localStringBuilder.append("?");
      str2 = Validator.URLParamsEscape(paramString2);
      localStringBuilder.append(str2.replaceAll("cacheid=(.*?)(&|$)", "cacheid=" + str1 + "$2"));
      return localStringBuilder.toString();
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.UrlHelper
 * JD-Core Version:    0.6.1
 */