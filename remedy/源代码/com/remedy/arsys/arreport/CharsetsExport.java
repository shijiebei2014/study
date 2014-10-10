package com.remedy.arsys.arreport;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.MenuItem;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.menu.CharMenu;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class CharsetsExport
{
  private static CharsetsExport mInstance;
  private static Map mCharsets;
  private static final String mDefaultCharset = "windows-1252";
  private static final String mUseServer = "useserver";
  private static final String mCharsetMenuName = "ReportSelectionCharset";
  private static final String mUTF8BOM = "utf-8";
  private String[][] mNioCharsets = { { "gb2312", "x-euc-cn" } };

  private List getServerList()
  {
    ArrayList localArrayList = new ArrayList();
    List localList1 = Configuration.getInstance().getPreferenceServers();
    String str;
    for (int i = 0; i < localList1.size(); i++)
    {
      str = (String)localList1.get(i);
      localArrayList.add(str);
    }
    List localList2 = Configuration.getInstance().getServers();
    for (int j = 0; j < localList2.size(); j++)
    {
      str = (String)localList2.get(j);
      localArrayList.add(str);
    }
    return localArrayList;
  }

  private CharMenu getExportMenu()
  {
    List localList = getServerList();
    if (localList.size() == 0)
      return null;
    CharMenu localCharMenu = null;
    for (int i = 0; i < localList.size(); i++)
      try
      {
        String str = (String)localList.get(i);
        ServerInfo localServerInfo = ServerInfo.get(str, true);
        if (localServerInfo.getVersionAsNumber() >= 70000)
        {
          Menu.MKey localMKey = new Menu.MKey(str, "en_US", "ReportSelectionCharset");
          localCharMenu = (CharMenu)Menu.get(localMKey);
          if (localCharMenu != null)
            break;
        }
      }
      catch (GoatException localGoatException)
      {
      }
    return localCharMenu;
  }

  private boolean createCharSetMapFromMenu(CharMenu paramCharMenu)
  {
    int i = 0;
    List localList = paramCharMenu.getItems();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      MenuItem localMenuItem = (MenuItem)localIterator.next();
      mCharsets = new HashMap();
      if (localMenuItem != null)
        if (localMenuItem.getType() == 1)
        {
          String str = (String)localMenuItem.getContent();
          mCharsets.put(str, str);
        }
    }
    return localList.size() > 0;
  }

  private void createCharsetMapFromConstants()
  {
    mCharsets = new HashMap();
    mCharsets.put("iso-8859-6", " iso-8859-6");
    mCharsets.put("windows-1256", "windows-1256");
    mCharsets.put("iso-8859-4", "iso-8859-4");
    mCharsets.put("windows-1257", "windows-1257");
    mCharsets.put("iso-8859-2", "iso-8859-2");
    mCharsets.put("gb2312", "gb2312");
    mCharsets.put("big5", "big5");
    mCharsets.put("iso-8859-5", "iso-8859-5");
    mCharsets.put("windows-1251", "windows-1251");
    mCharsets.put("iso-8859-7", "iso-8859-7");
    mCharsets.put("windows-1253", "windows-1253");
    mCharsets.put("iso-8859-8", "iso-8859-8");
    mCharsets.put("windows-1255", "windows-1255");
    mCharsets.put("euc-jp", "euc-jp");
    mCharsets.put("shift_jis", "shift_jis");
    mCharsets.put("euc-kr", "euc-kr");
    mCharsets.put("iso-8859-9", "iso-8859-9");
    mCharsets.put("windows-1254", "windows-1254");
    mCharsets.put("utf-8", "utf-8");
    mCharsets.put("windows-1258", "windows-1258");
    mCharsets.put("iso-8859-1", "iso-8859-1");
    mCharsets.put("windows-1252", "windows-1252");
    mCharsets.put("useserver", "useserver");
  }

  private CharsetsExport()
  {
    CharMenu localCharMenu = getExportMenu();
    if ((localCharMenu == null) || (!createCharSetMapFromMenu(localCharMenu)))
      createCharsetMapFromConstants();
    for (int i = 0; i < this.mNioCharsets.length; i++)
      if (mCharsets.containsKey(this.mNioCharsets[0][0]))
        mCharsets.put(this.mNioCharsets[0][0], this.mNioCharsets[0][1]);
      else
        Log.get(0).log(Level.SEVERE, "CharsetExport could not find " + this.mNioCharsets[0][0] + " in map");
  }

  public static synchronized CharsetsExport getInstance()
  {
    if (mInstance == null)
      mInstance = new CharsetsExport();
    return mInstance;
  }

  public String getNIO(String paramString)
  {
    String str = (String)mCharsets.get(paramString.toLowerCase(Locale.US));
    if (str == null)
      str = "";
    return str;
  }

  public boolean containsKey(String paramString)
  {
    return mCharsets.containsKey(paramString.toLowerCase(Locale.US));
  }

  public String getProperCharset(String paramString)
  {
    return paramString.toLowerCase(Locale.US);
  }

  public static String getDefaultCharset(String paramString)
  {
    try
    {
      ServerInfo localServerInfo = ServerInfo.get(paramString, true);
      if (localServerInfo.getVersionAsNumber() < 70000)
        return getUseServer(paramString);
      return "windows-1252";
    }
    catch (GoatException localGoatException)
    {
    }
    return "windows-1252";
  }

  public static boolean BOMRequired(String paramString)
  {
    return "utf-8".equals(paramString.toLowerCase(Locale.US));
  }

  public static boolean requestIsUseServer(String paramString)
  {
    return "useserver".equals(paramString.toLowerCase(Locale.US));
  }

  public static String getUseServer(String paramString)
  {
    String str = "";
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(paramString);
      str = localServerLogin.getServerCharSet();
      str = str.toLowerCase(Locale.US);
    }
    catch (ARException localARException)
    {
      Log.get(0).log(Level.SEVERE, "Exception calling ARGetServerCharSet: " + localARException.getMessage(), localARException);
    }
    catch (GoatException localGoatException)
    {
      Log.get(0).log(Level.SEVERE, "Exception calling ServerLogin.getAdmin: " + localGoatException.getMessage(), localGoatException);
    }
    return str;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.CharsetsExport
 * JD-Core Version:    0.6.1
 */