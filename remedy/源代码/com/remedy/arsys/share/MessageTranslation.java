package com.remedy.arsys.share;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.LocalizedRequestInfo;
import com.bmc.arsys.api.LocalizedValueCriteria;
import com.bmc.arsys.api.LocalizedValueInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.LocaleUtil;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class MessageTranslation
  implements Cache.Item
{
  private static final long serialVersionUID = -8207900365905339803L;
  private final String mValue;
  private static final Cache MCache = new MiscCache("Translated messages");
  private static final String LOCALIZED_FILE_START_NAME = "LocalizedMessages";
  private static final Map MLocalizedBundleMap = new HashMap();

  MessageTranslation(String paramString1, String paramString2, LocalizedRequestInfo paramLocalizedRequestInfo, String paramString3)
  {
    this.mValue = paramString3;
    MCache.put(buildKey(paramString1, paramString2, paramLocalizedRequestInfo), this);
  }

  public int getSize()
  {
    return 1;
  }

  public String getServer()
  {
    return null;
  }

  public String getValue()
  {
    return this.mValue;
  }

  public static String getLocalizedErrorMessage(String paramString, int paramInt, Object[] paramArrayOfObject)
  {
    return localTranslate(paramString, Integer.toString(paramInt), null, paramArrayOfObject);
  }

  public static String getLocalizedText(String paramString1, String paramString2, Object[] paramArrayOfObject)
  {
    return localTranslate(paramString1, paramString2, null, paramArrayOfObject);
  }

  public static String getLocalizedText(String paramString1, String paramString2)
  {
    return localTranslate(paramString1, paramString2, null, null);
  }

  public static String getLocalizedText(String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject)
  {
    return localTranslate(paramString1, paramString2, paramString3, paramArrayOfObject);
  }

  public static String getLocalizedText(String paramString1, String paramString2, String paramString3)
  {
    return localTranslate(paramString1, paramString2, paramString3, null);
  }

  public static String[] getServerMultipleLocalizedMessage(String paramString1, String paramString2, LocalizedRequestInfo[] paramArrayOfLocalizedRequestInfo)
  {
    return serverTranslate(paramString1, paramString2, paramArrayOfLocalizedRequestInfo);
  }

  public static final String getLocalizedActivelinkMessage(String paramString1, String paramString2, long paramLong)
  {
    return serverTranslate(paramString1, SessionData.get().getLocale(), 1, paramString2, (int)paramLong);
  }

  public static final String getLocalizedMenu(String paramString1, String paramString2, String paramString3)
  {
    return serverTranslate(paramString1, paramString2, 7, paramString3, 0);
  }

  public static final String getLocalizedFieldHelp(String paramString1, String paramString2, int paramInt)
  {
    return serverTranslate(paramString1, SessionData.get().getLocale(), 5, paramString2, paramInt);
  }

  public static final String getLocalizedFormHelp(String paramString1, String paramString2)
  {
    return serverTranslate(paramString1, SessionData.get().getLocale(), 4, paramString2, 0);
  }

  private static String localTranslate(String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject)
  {
    Locale localLocale = LocaleUtil.getLocale(paramString1);
    ResourceBundle localResourceBundle = null;
    String str = paramString2;
    if ((paramString2 != null) && (paramString3 != null))
      str = paramString3 + "|" + paramString2;
    synchronized (MLocalizedBundleMap)
    {
      localResourceBundle = (ResourceBundle)MLocalizedBundleMap.get(paramString1.intern());
      if (localResourceBundle == null)
        try
        {
          localResourceBundle = ResourceBundle.getBundle("LocalizedMessages", localLocale);
          MLocalizedBundleMap.put(paramString1.intern(), localResourceBundle);
        }
        catch (MissingResourceException localMissingResourceException2)
        {
        }
    }
    if (localResourceBundle != null)
      try
      {
        str = localResourceBundle.getString(str);
      }
      catch (MissingResourceException localMissingResourceException1)
      {
      }
    if (str != null)
    {
      try
      {
        str = new MessageFormat(str, localLocale).format(paramArrayOfObject);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
      }
      return str;
    }
    return paramString2;
  }

  private static String buildKey(String paramString1, String paramString2, LocalizedRequestInfo paramLocalizedRequestInfo)
  {
    return paramString1 + "/" + paramString2 + "/" + paramLocalizedRequestInfo.getMessageType() + "/" + paramLocalizedRequestInfo.getName() + "/" + paramLocalizedRequestInfo.getInternalID();
  }

  private static String[] getServerMultiple(String paramString1, String paramString2, LocalizedRequestInfo[] paramArrayOfLocalizedRequestInfo)
  {
    String[] arrayOfString = new String[paramArrayOfLocalizedRequestInfo.length];
    ArrayList localArrayList = new ArrayList();
    Object localObject1;
    Object localObject2;
    for (int i = 0; i < paramArrayOfLocalizedRequestInfo.length; i++)
    {
      localObject1 = buildKey(paramString1, paramString2, paramArrayOfLocalizedRequestInfo[i]);
      if (MCache.containsKey((String)localObject1))
      {
        localObject2 = (MessageTranslation)MCache.get((String)localObject1, MessageTranslation.class);
        if (localObject2 != null)
          arrayOfString[i] = ((MessageTranslation)localObject2).getValue();
        else
          arrayOfString[i] = "";
      }
      else
      {
        localArrayList.add(paramArrayOfLocalizedRequestInfo[i]);
        arrayOfString[i] = null;
      }
    }
    i = localArrayList.size();
    if (i > 0)
      try
      {
        localObject1 = ServerLogin.getAdmin(paramString1, paramString2);
        localObject2 = new LocalizedValueCriteria();
        ((LocalizedValueCriteria)localObject2).setRetrieveAll(true);
        LocalizedValueInfo[] arrayOfLocalizedValueInfo = (LocalizedValueInfo[])((ServerLogin)localObject1).getMultipleLocalizedValues((LocalizedValueCriteria)localObject2, localArrayList).toArray(new LocalizedValueInfo[0]);
        assert (arrayOfLocalizedValueInfo != null);
        int j = 0;
        for (int k = 0; k < arrayOfLocalizedValueInfo.length; k++)
        {
          while (arrayOfString[j] != null)
            j++;
          String str = arrayOfLocalizedValueInfo[k] == null ? null : arrayOfLocalizedValueInfo[k].getValue().toString();
          arrayOfString[j] = new MessageTranslation(paramString1, paramString2, (LocalizedRequestInfo)localArrayList.get(k), str).getValue();
          j++;
        }
      }
      catch (ARException localARException)
      {
        System.out.println(localARException.toString());
        return null;
      }
      catch (GoatException localGoatException)
      {
        System.out.println(localGoatException.toString());
        return null;
      }
    return arrayOfString;
  }

  private static String serverTranslate(String paramString1, String paramString2, int paramInt1, String paramString3, int paramInt2)
  {
    assert (paramString2 != null);
    try
    {
      if ((paramString1 == null) || (!ServerInfo.get(paramString1).isLocalized()))
        return null;
    }
    catch (GoatException localGoatException)
    {
      return null;
    }
    LocalizedRequestInfo[] arrayOfLocalizedRequestInfo = new LocalizedRequestInfo[1];
    if (paramInt2 <= 0)
      arrayOfLocalizedRequestInfo[0] = new LocalizedRequestInfo(paramString3, paramInt1);
    else
      arrayOfLocalizedRequestInfo[0] = new LocalizedRequestInfo(paramString3, paramInt1, paramInt2);
    String[] arrayOfString = getServerMultiple(paramString1, paramString2, arrayOfLocalizedRequestInfo);
    return arrayOfString == null ? null : arrayOfString[0];
  }

  private static String[] serverTranslate(String paramString1, String paramString2, LocalizedRequestInfo[] paramArrayOfLocalizedRequestInfo)
  {
    assert (paramString2 != null);
    assert (paramString1 != null);
    try
    {
      if (!ServerInfo.get(paramString1).isLocalized())
        return null;
    }
    catch (GoatException localGoatException)
    {
      return null;
    }
    return getServerMultiple(paramString1, paramString2, paramArrayOfLocalizedRequestInfo);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.MessageTranslation
 * JD-Core Version:    0.6.1
 */