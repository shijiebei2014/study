package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CacheDirectiveController
{
  private static String CACHE_CONTROL_COOKIE = "q";
  private static String FORCE_UPDATE_FLAG = "forcecontentupdate";
  private static int DEFAULT_CACHE_CONTROL_LIFETIME = 604800;
  private static boolean CACHE_ON = true;
  private static int CACHE_LIMIT = 30;
  private static MD5Encryptor MD5;

  public static String computeContentSignature(byte[] paramArrayOfByte)
  {
    if (!CACHE_ON)
      return null;
    return MD5.hashData(paramArrayOfByte);
  }

  public static boolean needNewContent(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, String paramString)
  {
    if (!CACHE_ON)
      return true;
    TimeHashMap localTimeHashMap = getCacheContentTimeHashMap(paramHttpServletRequest, paramHttpServletResponse);
    if (isForcedUpdate(paramHttpServletRequest, paramHttpServletResponse, localTimeHashMap, paramString))
      return true;
    String str1 = request2Signature(paramHttpServletRequest);
    String str2 = (String)localTimeHashMap.get(str1);
    long l = paramHttpServletRequest.getDateHeader("If-Modified-Since");
    if ((str2 == null) || (str2.compareToIgnoreCase(paramString) != 0) || (l == -1L))
    {
      localTimeHashMap.put(str1, paramString);
      return true;
    }
    return false;
  }

  public static void forceContentUpdate(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    if (!CACHE_ON)
      return;
    TimeHashMap localTimeHashMap = getCacheContentTimeHashMap(paramHttpServletRequest, paramHttpServletResponse);
    String str = FORCE_UPDATE_FLAG;
    localTimeHashMap.put(str, "" + System.currentTimeMillis());
  }

  private static boolean isForcedUpdate(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, TimeHashMap<String, String> paramTimeHashMap, String paramString)
  {
    if (paramTimeHashMap.get(FORCE_UPDATE_FLAG) != null)
    {
      paramTimeHashMap.remove(FORCE_UPDATE_FLAG);
      paramTimeHashMap.put(request2Signature(paramHttpServletRequest), paramString);
      return true;
    }
    return false;
  }

  private static String request2Signature(HttpServletRequest paramHttpServletRequest)
  {
    String str = paramHttpServletRequest.getQueryString();
    str = MD5.hashData(str.getBytes());
    return str;
  }

  private static TimeHashMap<String, String> getCacheContentTimeHashMap(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
    TimeHashMap localTimeHashMap = (TimeHashMap)localHttpSession.getAttribute("arsystem_cache_control");
    if (localTimeHashMap == null)
    {
      localTimeHashMap = buildCacheContent(paramHttpServletRequest, paramHttpServletResponse);
      localHttpSession.setAttribute("arsystem_cache_control", localTimeHashMap);
    }
    return localTimeHashMap;
  }

  private static TimeHashMap<String, String> buildCacheContent(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    String str = getCookieValue(paramHttpServletRequest, CACHE_CONTROL_COOKIE);
    deleteCacheControlCookie(paramHttpServletResponse);
    if (str == null)
      return new TimeHashMap(CACHE_LIMIT);
    String[] arrayOfString = str.split(":");
    if (arrayOfString.length % 2 != 0)
      return new TimeHashMap(CACHE_LIMIT);
    TimeHashMap localTimeHashMap = new TimeHashMap(CACHE_LIMIT);
    int i = 0;
    while (i < arrayOfString.length)
    {
      localTimeHashMap.put(arrayOfString[i], arrayOfString[(i + 1)]);
      i += 2;
    }
    return localTimeHashMap;
  }

  public static void setCacheControlCookie(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    TimeHashMap localTimeHashMap = getCacheContentTimeHashMap(paramHttpServletRequest, paramHttpServletResponse);
    Cookie localCookie = new Cookie(CACHE_CONTROL_COOKIE, localTimeHashMap.toString());
    localCookie.setMaxAge(DEFAULT_CACHE_CONTROL_LIFETIME);
    localCookie.setPath("/");
    paramHttpServletResponse.addCookie(localCookie);
  }

  private static void deleteCacheControlCookie(HttpServletResponse paramHttpServletResponse)
  {
    Cookie localCookie = new Cookie(CACHE_CONTROL_COOKIE, "");
    localCookie.setMaxAge(0);
    localCookie.setPath("/");
    paramHttpServletResponse.addCookie(localCookie);
  }

  private static String getCookieValue(HttpServletRequest paramHttpServletRequest, String paramString)
  {
    Cookie[] arrayOfCookie = paramHttpServletRequest.getCookies();
    if (arrayOfCookie != null)
      for (int i = 0; i < arrayOfCookie.length; i++)
      {
        String str = arrayOfCookie[i].getName();
        if ((str != null) && (str.equals(paramString)))
          return arrayOfCookie[i].getValue();
      }
    return null;
  }

  static
  {
    int i = Configuration.getInstance().getWaitCursorMode();
    if (i % 2 != 0)
      CACHE_ON = false;
    if (CACHE_ON)
      try
      {
        MD5 = MD5Encryptor.getInstance();
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        CACHE_ON = false;
      }
  }

  public static class MD5Encryptor
  {
    private static MessageDigest _MD = null;
    private static MD5Encryptor _MD5;
    private static final char[] _XCHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private MD5Encryptor()
      throws NoSuchAlgorithmException
    {
      _MD = MessageDigest.getInstance("MD5");
    }

    public static MD5Encryptor getInstance()
      throws NoSuchAlgorithmException
    {
      if (_MD5 == null)
        _MD5 = new MD5Encryptor();
      return _MD5;
    }

    public String hashData(byte[] paramArrayOfByte)
    {
      return hexStringFromBytes(calculateHash(paramArrayOfByte));
    }

    private static synchronized byte[] calculateHash(byte[] paramArrayOfByte)
    {
      _MD.update(paramArrayOfByte, 0, paramArrayOfByte.length);
      return _MD.digest();
    }

    private static String hexStringFromBytes(byte[] paramArrayOfByte)
    {
      String str = "";
      int j = 0;
      for (int k = 0; k < paramArrayOfByte.length; k++)
      {
        int i = (paramArrayOfByte[k] & 0xFF) / 16;
        j = (paramArrayOfByte[k] & 0xFF) % 16;
        str = str + _XCHARS[i] + _XCHARS[j];
      }
      return str;
    }
  }

  public static class TimeHashMap<K, V> extends HashMap<K, V>
  {
    private int mLimit;
    ArrayList<K> mArray;

    public TimeHashMap(int paramInt)
    {
      super();
      this.mLimit = paramInt;
      this.mArray = new ArrayList(paramInt);
    }

    public V get(Object paramObject)
    {
      Object localObject = super.get(paramObject);
      if ((localObject != null) && (this.mArray.indexOf(paramObject) > 0))
      {
        this.mArray.remove(paramObject);
        this.mArray.add(0, paramObject);
      }
      return localObject;
    }

    public V put(K paramK, V paramV)
    {
      if (this.mArray.contains(paramK))
      {
        this.mArray.remove(paramK);
        super.remove(paramK);
      }
      if (this.mArray.size() >= this.mLimit)
      {
        super.remove(this.mArray.get(this.mLimit - 1));
        this.mArray.remove(this.mLimit - 1);
      }
      this.mArray.add(0, paramK);
      return super.put(paramK, paramV);
    }

    public V remove(Object paramObject)
    {
      this.mArray.remove(paramObject);
      return super.remove(paramObject);
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = this.mArray.size();
      if (i == 0)
        return null;
      Object localObject1 = null;
      Object localObject2 = null;
      for (int j = 0; j < i; j++)
      {
        localObject1 = this.mArray.get(j);
        localObject2 = super.get(localObject1);
        if (localObject2 != null)
          localStringBuilder.append(localObject1.toString()).append(":").append(localObject2.toString()).append(":");
      }
      return localStringBuilder.toString();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.CacheDirectiveController
 * JD-Core Version:    0.6.1
 */