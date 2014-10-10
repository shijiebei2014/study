package com.remedy.arsys.goat;

import com.remedy.arsys.backchannel.TableEntryListBase;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.intf.service.IRequestService;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.stubs.SessionData;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FormContext
  implements Serializable
{
  private static final long serialVersionUID = -3762249331802047072L;
  protected String mContextURL;
  protected String mAbsoluteContextURL;
  protected String mRelativeContextURL;
  protected String mContextPath;
  protected String mApplication;
  private String mLocale;
  private boolean mWorkflowLogging;
  private Long mAccessibilityPref;
  private IRequestService requestService;
  private static ThreadLocal MContextLocal;
  private static final int MCRCBase = -306674912;
  private static final int[] MCRCTable;

  public void setRequestService(IRequestService paramIRequestService)
  {
    this.requestService = paramIRequestService;
  }

  public IRequestService getRequestService()
  {
    return this.requestService;
  }

  public static FormContext get()
  {
    FormContext localFormContext = (FormContext)MContextLocal.get();
    assert (localFormContext != null);
    return localFormContext;
  }

  public static void set(FormContext paramFormContext)
  {
    MContextLocal.set(paramFormContext);
  }

  public static void dispose()
  {
    MContextLocal.set(null);
  }

  public FormContext(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.mAbsoluteContextURL = paramString1;
    this.mRelativeContextURL = paramString2;
    this.mContextURL = paramString3;
    this.mContextPath = paramString4;
    if (this.mContextPath.charAt(this.mContextPath.length() - 1) != File.separatorChar)
      this.mContextPath += File.separatorChar;
    this.mApplication = null;
    assert (MContextLocal.get() == null);
    SessionData localSessionData = SessionData.get();
    this.mLocale = localSessionData.getLocale();
    this.mWorkflowLogging = ((localSessionData.getPreferences().getLogActiveLinks().equals(ARUserPreferences.YES)) || (Configuration.getInstance().getWorkflowLogging()));
    this.mAccessibilityPref = localSessionData.getPreferences().getWebAccessibleMode();
    MContextLocal.set(this);
  }

  public String getResourceURL()
  {
    return this.mRelativeContextURL + "resources/";
  }

  public String getResourcePath()
  {
    return this.mContextPath + "resources" + File.separator + "standard" + File.separator;
  }

  public String getFormURL(String paramString1, String paramString2, String paramString3)
  {
    assert (paramString1 != null);
    assert (paramString2 != null);
    assert (paramString3 != null);
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(getFormURL()).append(URLEncoder.encode(paramString1, "UTF-8")).append("/");
      if (this.mApplication != null)
        localStringBuilder.append(URLEncoder.encode(this.mApplication, "UTF-8")).append("/");
      return URLEncoder.encode(paramString2, "UTF-8") + "/" + URLEncoder.encode(paramString3, "UTF-8") + "/";
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return null;
  }

  public String getFormURL(String paramString)
  {
    assert (paramString != null);
    assert (this.mApplication != null);
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(getFormURL()).append(URLEncoder.encode(paramString, "UTF-8")).append("/").append(URLEncoder.encode(this.mApplication, "UTF-8"));
      return localStringBuilder.toString();
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return null;
  }

  public String getFormURL()
  {
    if (this.mApplication == null)
      return "forms/";
    return "apps/";
  }

  public String getContextURL()
  {
    return this.mContextURL;
  }

  public String getRelativeContextURL()
  {
    return this.mRelativeContextURL;
  }

  public String getAbsoluteContextURL()
  {
    return this.mAbsoluteContextURL;
  }

  public String getContextPath()
  {
    return this.mContextPath;
  }

  public String getApplication()
  {
    return this.mApplication;
  }

  public void setApplication(String paramString)
  {
    String str = this.mApplication;
    this.mApplication = paramString;
    if ((str == null) && (paramString != null))
      this.mRelativeContextURL += "../";
    else if ((str != null) && (paramString == null))
      this.mRelativeContextURL = this.mRelativeContextURL.substring(3);
  }

  public final String getJSURL()
  {
    return getResourceURL() + "javascript/";
  }

  public final String getJSPath()
  {
    return getResourcePath() + "javascript/";
  }

  public final String getImageURL()
  {
    return getResourceURL() + "images/";
  }

  public final String getImagePath()
  {
    return getResourcePath() + "images" + File.separator;
  }

  public String getImagepoolURL()
  {
    return this.mRelativeContextURL + "imagepool/";
  }

  public String getImagepoolPath()
  {
    return this.mContextPath + "imagepool" + File.separator;
  }

  public final String getStylesheetURL()
  {
    return getResourceURL() + "stylesheets/";
  }

  public final String getStylesheet(String paramString)
  {
    return "stylesheets/" + paramString;
  }

  public final boolean getWorkflowLogging()
  {
    return this.mWorkflowLogging;
  }

  public final boolean IsVoiceAccessibleUser()
  {
    return (this.mAccessibilityPref.equals(ARUserPreferences.ACCESSIBLE_MODE_VOICE)) || (this.mAccessibilityPref.equals(ARUserPreferences.ACCESIBLE_MODE_VISUAL));
  }

  public final boolean IsLowVisionUser()
  {
    return this.mAccessibilityPref.equals(ARUserPreferences.ACCESIBLE_MODE_VISUAL);
  }

  public final String getObjectListURL(String paramString1, String paramString2)
    throws GoatException
  {
    String str1 = null;
    String str2 = Configuration.getInstance().getObjectListServer();
    if (str2 != null)
      str1 = TableEntryListBase.getObjectListSchema(str2);
    if (str1 == null)
      throw new GoatException(9217);
    assert ((paramString2 == null) || (paramString1 != null));
    if (paramString2 != null)
      try
      {
        paramString2 = URLEncoder.encode(paramString2, "UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
      }
    return getContextURL() + getFormURL(Configuration.getInstance().getHomePageServer(), str1, "") + (paramString1 != null ? "?olserver=" + paramString1 : "") + (paramString2 != null ? "&olapp=" + paramString2 : "");
  }

  public final String getAppResURL(String paramString)
  {
    assert (this.mApplication != null);
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(this.mRelativeContextURL).append(getFormURL()).append(URLEncoder.encode(paramString, "UTF-8")).append("/");
      if (this.mApplication != null)
        localStringBuilder.append(URLEncoder.encode(this.mApplication, "UTF-8"));
      localStringBuilder.append("/resources/");
      return localStringBuilder.toString();
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return null;
  }

  public final String getAppStylesheetPrefixURL(String paramString)
  {
    return getAppResURL(paramString) + "stylesheets/";
  }

  private static final int computeCRC32(byte[] paramArrayOfByte)
  {
    int i = -1;
    for (int j = 0; j < paramArrayOfByte.length; j++)
      i = i >>> 8 ^ MCRCTable[((i ^ paramArrayOfByte[j]) & 0xFF)];
    return i;
  }

  public String getFieldGraphCacheKey()
  {
    assert (this.mLocale != null);
    return (this.mLocale + "/" + this.mWorkflowLogging + "/" + this.mApplication + "/" + this.mAccessibilityPref).intern();
  }

  public String getFieldGraphURLParam(String paramString, int paramInt, Form paramForm)
  {
    assert (paramString != null);
    assert (this.mLocale != null);
    String str = paramString + this.mLocale + this.mWorkflowLogging + this.mApplication + this.mAccessibilityPref + paramInt + (FieldGraph.MWorkflowProfiling ? 1 : 0) + "8.1.00 201301251157" + paramForm.getLastUpdateTimeMs() + paramForm.getServer();
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = str.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return Integer.toHexString(computeCRC32(arrayOfByte));
  }

  public String getInlineFormUniqueId(String paramString)
  {
    assert (paramString != null);
    assert (this.mLocale != null);
    String str = paramString + this.mLocale + this.mWorkflowLogging + this.mApplication + this.mAccessibilityPref + (FieldGraph.MWorkflowProfiling ? 1 : 0) + "8.1.00 201301251157";
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = str.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return Integer.toHexString(computeCRC32(arrayOfByte));
  }

  static
  {
    MContextLocal = new ThreadLocal()
    {
      protected synchronized Object initialValue()
      {
        return null;
      }
    };
    MCRCTable = new int[256];
    for (int i = 0; i < 256; i++)
    {
      int j = i;
      for (int k = 0; k < 8; k++)
        j = (j & 0x1) == 1 ? j >>> 1 ^ 0xEDB88320 : j >>> 1;
      MCRCTable[i] = j;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.FormContext
 * JD-Core Version:    0.6.1
 */