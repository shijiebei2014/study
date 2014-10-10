package com.remedy.arsys.support;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressUtil
{
  public static boolean isHostReachable(String paramString)
  {
    boolean bool = false;
    if ((paramString == null) || (paramString.length() == 0))
      return false;
    InetAddress localInetAddress;
    String str;
    try
    {
      localInetAddress = InetAddress.getByName(paramString);
      str = localInetAddress.getHostAddress();
    }
    catch (UnknownHostException localUnknownHostException)
    {
      return false;
    }
    catch (IOException localIOException)
    {
      return false;
    }
    catch (Exception localException)
    {
      return false;
    }
    if ((localInetAddress != null) || (str != null))
      bool = true;
    return bool;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.InetAddressUtil
 * JD-Core Version:    0.6.1
 */