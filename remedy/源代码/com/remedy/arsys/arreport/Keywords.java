package com.remedy.arsys.arreport;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.goat.permissions.Role;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.legacyshared.ARDataFormat;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.support.BrowserType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Keywords
{
  private static final String[] vuiTypes = { "NONE", "WINDOWS", "WEB(RELATIVE)", "WEB(FIXED)", "WIRELESS" };
  static final String[] LABELS = { "$DEFAULT$", "$USER$", "$TIMESTAMP$", "$TIME$", "$DATE$", "$SCHEMA$", "$SERVER$", "$WEEKDAY$", "$GROUPS$", "$OPERATION$", "$HARDWARE$", "$OS$", "$DATABASE$", "$LASTID$", "$LASTCOUNT$", "$VERSION$", "$VUI$", "$GUIDETEXT$", "$FIELDHELP$", "$GUIDE$", "$APPLICATION$", "$LOCALE$", "$CLIENT-TYPE$", "$SCHEMA-ALIAS$", "$ROWSELECTED$", "$ROWCHANGED$", "$BROWSER$", "$VUI-TYPE$", "$TCPPORT$", "$HOMEURL$", "$ROLES$", "$EVENTTYPE$", "$EVENTSRCWINID$", "$CURRENTWINID$", "$LASTOPENEDWINID$" };
  private static Log mLog = Log.get(0);

  private static String getDatabaseName(ServerLogin paramServerLogin)
  {
    String str = paramServerLogin.getServer();
    Object localObject = null;
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      ServerInfo localServerInfo = ServerInfo.get(str);
      if (localServerInfo.getDBType() != null)
        localStringBuilder.append(localServerInfo.getDBType());
      if (localServerInfo.getDBVersion() != null)
        localStringBuilder.append(" ").append(localServerInfo.getDBVersion());
    }
    catch (GoatException localGoatException)
    {
      mLog.log(Level.SEVERE, "Keywords - resolving: DATABASE ", localGoatException);
    }
    return localStringBuilder.toString().trim();
  }

  private static String resolveTime(String paramString1, String paramString2, String paramString3, Long paramLong, String paramString4, String paramString5)
  {
    int i = -1;
    ARDataFormat localARDataFormat = new ARDataFormat();
    if (paramString1.equals("WEEKDAY"))
      return localARDataFormat.getDateTimeInfo(null, "7", paramString2, paramString3);
    if (paramString1.equals("TIMESTAMP"))
      i = 0;
    else if (paramString1.equals("TIME"))
      i = 1;
    else if (paramString1.equals("DATE"))
      i = 2;
    else
      return "";
    Calendar localCalendar = Calendar.getInstance();
    boolean bool = (paramLong != null) && (paramLong.equals(ARUserPreferences.CUSTOM_TIME_FORMAT));
    String str = localARDataFormat.getCustomFormat(bool, paramString4, paramString5, i, paramString2);
    return localARDataFormat.formatDateTime(ARDataFormat.mapTimeFormatLongToLegacyString(paramLong), i, String.valueOf(localCalendar.getTime().getTime() / 1000L), paramString2, paramString3, str);
  }

  private static String resolveSystem(String paramString)
  {
    String str = null;
    if (paramString.equals("VERSION"))
      return Configuration.getInstance().getClientVersion();
    if (paramString.equals("OS"))
      str = "os.name";
    else if (paramString.equals("HARDWARE"))
      str = "os.arch";
    else
      return "";
    try
    {
      return Configuration.getInstance().getProperty(str);
    }
    catch (Exception localException)
    {
      mLog.log(Level.SEVERE, "Keywords - resolving: " + paramString, localException);
    }
    return "";
  }

  public static String getGroupNames(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject = null;
    try
    {
      Map localMap = Group.getAllInstances(paramString);
      if (localMap != null)
      {
        ArrayList localArrayList = null;
        Iterator localIterator1 = null;
        int i = 1;
        Iterator localIterator2 = localMap.values().iterator();
        while (localIterator2.hasNext())
        {
          localArrayList = (ArrayList)localIterator2.next();
          localIterator1 = localArrayList.iterator();
          while (localIterator1.hasNext())
          {
            if (i == 0)
              localStringBuilder.append(" ");
            localStringBuilder.append(localIterator1.next());
            i = 0;
          }
        }
        return localStringBuilder.toString();
      }
    }
    catch (GoatException localGoatException)
    {
    }
    return localStringBuilder.toString();
  }

  public static String getRoleNames(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      Map localMap = Role.getAllInstances(paramString1, paramString2);
      if ((localMap != null) && (localMap.size() > 0))
      {
        Iterator localIterator = localMap.values().iterator();
        for (int i = 1; localIterator.hasNext(); i = 0)
        {
          if (i == 0)
            localStringBuilder.append(" ");
          localStringBuilder.append(localIterator.next());
        }
      }
    }
    catch (GoatException localGoatException)
    {
    }
    return localStringBuilder.toString();
  }

  public static String replaceKeywordsAtWebTier(ServerLogin paramServerLogin, String paramString1, String paramString2, String paramString3, String paramString4, Long paramLong, String paramString5, String paramString6, String paramString7, String paramString8, boolean paramBoolean)
  {
    String str1 = null;
    String str2 = "";
    if (paramBoolean)
      str2 = "\"";
    if (paramString2.startsWith("-"))
      paramString2 = paramString2.substring(1);
    int i;
    try
    {
      i = Integer.parseInt(paramString2);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return null;
    }
    switch (i)
    {
    case 4:
      str1 = str2 + resolveTime("DATE", paramString3, paramString4, paramLong, paramString5, paramString6) + str2;
      break;
    case 6:
      str1 = str2 + paramServerLogin.getServer() + str2;
      break;
    case 3:
      str1 = str2 + resolveTime("TIME", paramString3, paramString4, paramLong, paramString5, paramString6) + str2;
      break;
    case 2:
      str1 = str2 + resolveTime("TIMESTAMP", paramString3, paramString4, paramLong, paramString5, paramString6) + str2;
      break;
    case 1:
      str1 = str2 + paramServerLogin.getUser().toString() + str2;
      break;
    case 7:
      str1 = str2 + resolveTime("WEEKDAY", paramString3, paramString4, paramLong, paramString5, paramString6) + str2;
      break;
    case 5:
      str1 = str2 + paramString1 + str2;
      break;
    case 53:
      str1 = str2 + "" + str2;
      break;
    case 8:
      str1 = str2 + getGroupNames(paramServerLogin.getServer()) + str2;
      break;
    case 30:
      str1 = str2 + getRoleNames(paramServerLogin.getServer(), paramString8) + str2;
      break;
    case 12:
      str1 = str2 + getDatabaseName(paramServerLogin) + str2;
      break;
    case 20:
      if (paramString8 != null)
        str1 = str2 + paramString8 + str2;
      else
        str1 = str2 + "" + str2;
      break;
    case 11:
      str1 = str2 + resolveSystem("OS") + str2;
      break;
    case 15:
      str1 = resolveSystem("VERSION");
      break;
    case 10:
      str1 = str2 + resolveSystem("HARDWARE") + str2;
      break;
    case 21:
      str1 = str2 + paramString3 + str2;
      break;
    case 22:
      str1 = String.valueOf(9);
      break;
    case 26:
      str1 = getBrowserTypeAndVersion(paramString7);
      break;
    case -1:
      str1 = "$NULL$";
      break;
    case 0:
    case 9:
    case 13:
    case 14:
    case 16:
    case 17:
    case 18:
    case 19:
    case 23:
    case 24:
    case 25:
    case 27:
    case 28:
    case 29:
    case 31:
    case 32:
    case 33:
    case 34:
    case 35:
    case 36:
    case 37:
    case 38:
    case 39:
    case 40:
    case 41:
    case 42:
    case 43:
    case 44:
    case 45:
    case 46:
    case 47:
    case 48:
    case 49:
    case 50:
    case 51:
    case 52:
    }
    return str1;
  }

  private static String getBrowserTypeAndVersion(String paramString)
  {
    BrowserType localBrowserType = BrowserType.getTypeFromRequest(paramString);
    if ((paramString.indexOf("Opera") < 0) && (paramString.indexOf("mac") < 0))
    {
      f = getBrowserVersion(paramString, ".*MSIE ([0-9]+.[0-9]+).*");
      if ((f != -1.0F) && (f >= 5.5F))
        return localBrowserType.getAgent() + " " + f;
    }
    float f = getBrowserVersion(paramString, ".*Gecko/([0-9]+)*.");
    if ((f != -1.0F) && (f >= 20030210.0F))
      return localBrowserType.getAgent() + " " + f;
    f = getBrowserVersion(paramString, ".*AppleWebKit/([0-9]+).*");
    if ((f != -1.0F) && (f >= 522.0F))
      return localBrowserType.getAgent() + " " + f;
    return paramString;
  }

  private static float getBrowserVersion(String paramString1, String paramString2)
  {
    Pattern localPattern = Pattern.compile(paramString2);
    Matcher localMatcher = localPattern.matcher(paramString1);
    if (localMatcher.matches())
      try
      {
        return Float.parseFloat(localMatcher.group(1));
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
    return -1.0F;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.Keywords
 * JD-Core Version:    0.6.1
 */