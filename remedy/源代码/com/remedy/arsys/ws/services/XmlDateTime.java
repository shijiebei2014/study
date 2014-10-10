package com.remedy.arsys.ws.services;

import com.remedy.arsys.goat.GoatException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class XmlDateTime
{
  public static long parse(String paramString)
    throws GoatException
  {
    try
    {
      String str1 = paramString.substring(0, 4);
      String str2 = paramString.substring(5, 7);
      String str3 = paramString.substring(8, 10);
      String str4 = paramString.substring(11, 13);
      String str5 = paramString.substring(14, 16);
      String str6 = paramString.substring(17, 19);
      String str7 = "-00:00";
      int i;
      if (((i = paramString.indexOf("-", 19)) != -1) || ((i = paramString.indexOf("+", 18)) != -1))
        str7 = paramString.substring(i);
      TimeZone localTimeZone = TimeZone.getTimeZone("GMT" + str7);
      Calendar localCalendar = Calendar.getInstance(localTimeZone);
      localCalendar.set(Integer.parseInt(str1), Integer.parseInt(str2) - 1, Integer.parseInt(str3), Integer.parseInt(str4), Integer.parseInt(str5), Integer.parseInt(str6));
      return localCalendar.getTime().getTime() / 1000L;
    }
    catch (Exception localException)
    {
    }
    throw new GoatException(9335, paramString);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.XmlDateTime
 * JD-Core Version:    0.6.1
 */