package com.remedy.arsys.stubs;

import java.net.URL;
import java.net.URLClassLoader;

public class SharedObjectLoader extends URLClassLoader
{
  private static SharedObjectLoader mInstance = null;

  private SharedObjectLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
  {
    super(paramArrayOfURL, paramClassLoader);
  }

  public static SharedObjectLoader getInstance(URL[] paramArrayOfURL)
  {
    if (mInstance == null)
    {
      if ((paramArrayOfURL != null) && (paramArrayOfURL.length > 0))
        mInstance = new SharedObjectLoader(paramArrayOfURL, Thread.currentThread().getContextClassLoader());
      return mInstance;
    }
    mInstance.insertURLS(paramArrayOfURL);
    return mInstance;
  }

  public void insertURL(URL paramURL)
  {
    if (paramURL == null)
      return;
    addURL(paramURL);
  }

  public void insertURLS(URL[] paramArrayOfURL)
  {
    if ((paramArrayOfURL == null) || (paramArrayOfURL.length == 0))
      return;
    for (int i = 0; i < paramArrayOfURL.length; i++)
      if (paramArrayOfURL[i] != null)
        insertURL(paramArrayOfURL[i]);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.SharedObjectLoader
 * JD-Core Version:    0.6.1
 */