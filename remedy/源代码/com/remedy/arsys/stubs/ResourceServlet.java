package com.remedy.arsys.stubs;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.IConfigObserver;
import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.support.BrowserType;
import com.remedy.arsys.support.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceServlet extends GoatHttpServlet
{
  private static Map mObjects = new HashMap();

  public void init(ServletConfig paramServletConfig)
    throws ServletException
  {
    super.init(paramServletConfig);
    Configuration.getInstance().addConfigObserver(new ConfigObserver(null));
  }

  private ResourceObject getResource(String paramString)
  {
    return getResource(paramString, new String[] { paramString });
  }

  private ResourceObject getResource(String paramString, String[] paramArrayOfString)
  {
    String str = paramString.intern();
    synchronized (str)
    {
      ResourceObject localResourceObject = (ResourceObject)mObjects.get(str);
      if (localResourceObject != null)
        try
        {
          localResourceObject.update();
          return localResourceObject;
        }
        catch (GoatException localGoatException1)
        {
          mObjects.remove(str);
        }
      File[] arrayOfFile = new File[paramArrayOfString.length];
      for (int i = 0; i < arrayOfFile.length; i++)
      {
        arrayOfFile[i] = new File(paramArrayOfString[i]);
        if (!arrayOfFile[i].exists())
          return null;
      }
      try
      {
        localResourceObject = new ResourceObject(arrayOfFile);
        mObjects.put(str, localResourceObject);
        return localResourceObject;
      }
      catch (GoatException localGoatException2)
      {
      }
    }
    return null;
  }

  protected void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws GoatException, IOException
  {
    String str1 = paramHttpServletRequest.getPathInfo();
    if (str1 == null)
    {
      paramHttpServletResponse.sendError(404);
      return;
    }
    assert (str1.charAt(0) == '/');
    BrowserType localBrowserType = BrowserType.getTypeFromRequest(paramHttpServletRequest);
    str1 = parsePath(str1);
    String str2 = "/resources/" + localBrowserType.getAbbrev();
    if (Validator.isPathDepthLessOrEqual(str1, 7))
      str2 = str2 + str1;
    str2 = getServletContext().getRealPath(str2);
    String str3 = "/resources/standard/";
    if (Validator.isPathDepthLessOrEqual(str1, 7))
      str3 = str3 + str1;
    str3 = getServletContext().getRealPath(str3);
    ResourceObject localResourceObject = null;
    if (str1.startsWith("/stylesheets"))
      localResourceObject = getResource(str2, new String[] { str3, str2 });
    if (localResourceObject == null)
      localResourceObject = getResource(str2);
    if (localResourceObject == null)
    {
      localResourceObject = getResource(str3);
      if (localResourceObject == null)
      {
        paramHttpServletResponse.sendError(404);
        return;
      }
    }
    localResourceObject.transmit(paramHttpServletRequest, paramHttpServletResponse);
  }

  private String parsePath(String paramString)
  {
    String str1 = paramString;
    if (paramString.contains("8.1.00 201301251157"))
      str1 = paramString.replace("8.1.00 201301251157/", "");
    for (int i = 0; i < BrowserType.BROWSER_TYPES.length; i++)
    {
      BrowserType localBrowserType = BrowserType.BROWSER_TYPES[i];
      String str2 = "/" + localBrowserType.getAbbrev() + "/";
      if (str1.contains(str2))
      {
        str1 = str1.replace(str2, "/");
        break;
      }
    }
    return str1;
  }

  protected static class ResourceObject
  {
    Globule mData;
    private final File[] mFiles;

    public ResourceObject(File[] paramArrayOfFile)
      throws GoatException
    {
      this.mFiles = paramArrayOfFile;
      loadData();
    }

    public void update()
      throws GoatException
    {
      for (int i = 0; i < this.mFiles.length; i++)
        if (this.mFiles[i].lastModified() > this.mData.modified())
        {
          loadData();
          break;
        }
    }

    public void loadData()
      throws GoatException
    {
      assert (this.mFiles.length > 0);
      try
      {
        int i = 0;
        long l = 0L;
        for (int j = 0; j < this.mFiles.length; j++)
        {
          i += (int)this.mFiles[j].length();
          if (this.mFiles[j].lastModified() > l)
            l = this.mFiles[j].lastModified();
        }
        byte[] arrayOfByte = new byte[i];
        int k = 0;
        for (int m = 0; m < this.mFiles.length; m++)
        {
          FileInputStream localFileInputStream = new FileInputStream(this.mFiles[m].getAbsolutePath());
          try
          {
            i = (int)this.mFiles[m].length();
            if (localFileInputStream.read(arrayOfByte, k, i) != i)
              throw new GoatException("File IO Error");
            k += i;
          }
          finally
          {
            localFileInputStream.close();
          }
        }
        Configuration localConfiguration = Configuration.getInstance();
        int n = 3;
        String str = localConfiguration.getMimeType(this.mFiles[0].getAbsolutePath());
        if (str.startsWith("text/"))
          str = str + ";charset=utf-8";
        this.mData = new Globule(arrayOfByte, str, l, n);
      }
      catch (IOException localIOException)
      {
        throw new GoatException("File IO Error");
      }
    }

    public void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
      throws IOException
    {
      this.mData.transmit(paramHttpServletRequest, paramHttpServletResponse);
    }
  }

  private static final class ConfigObserver
    implements IConfigObserver
  {
    public void notify(Object paramObject1, int paramInt, Object paramObject2)
    {
      switch (paramInt)
      {
      case 0:
        String str = ((String)paramObject2).trim();
        if ("arsystem.resource_expiry_interval".equals(str))
        {
          Collection localCollection = ResourceServlet.mObjects.values();
          Iterator localIterator = localCollection.iterator();
          while (localIterator.hasNext())
          {
            Object localObject = localIterator.next();
            ((ResourceServlet.ResourceObject)localObject).mData.assignHTTPCacheTime();
          }
        }
        break;
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ResourceServlet
 * JD-Core Version:    0.6.1
 */