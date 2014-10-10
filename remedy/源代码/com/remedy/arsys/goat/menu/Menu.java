package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.DataDictionaryMenu;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.ListMenu;
import com.bmc.arsys.api.MenuCriteria;
import com.bmc.arsys.api.MenuItem;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.SqlMenu;
import com.bmc.arsys.api.Timestamp;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.cache.sync.ServerSync;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.share.MiscCache;
import com.remedy.arsys.share.ObjectCreationCount;
import com.remedy.arsys.share.ObjectReleaseCount;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class Menu
  implements Cache.Item
{
  private static final long serialVersionUID = 8555597234771742380L;
  protected static final Cache MCache = new MiscCache("Menus");
  protected static final int MENU_TYPE_CHAR = 0;
  protected static final int MENU_TYPE_DD = 1;
  protected static final int MENU_TYPE_FILE = 2;
  protected static final int MENU_TYPE_GROUP = 3;
  protected static final int MENU_TYPE_QUERY = 4;
  protected static final int MENU_TYPE_SQL = 5;
  protected com.bmc.arsys.api.Menu mAPIMenu;
  protected String mServer;
  protected String mName;
  protected String mAppName;
  protected String mLocale;
  protected String mJSStr;
  protected MKey mKey;
  protected boolean mReturnFullQuery = true;
  private static ObjectReleaseCount delObjCnt = new ObjectReleaseCount();
  private static ObjectCreationCount createObjCnt = new ObjectCreationCount();
  private final long mLastUpdateTimeMs;

  protected Menu(MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    synchronized (createObjCnt)
    {
      ObjectCreationCount.inc(9);
    }
    this.mAPIMenu = paramMenu;
    this.mName = paramMKey.getMenuName();
    this.mAppName = paramMKey.getAppName();
    this.mServer = paramMKey.getServer();
    this.mLocale = paramMKey.getLocale();
    this.mJSStr = getJS();
    this.mKey = paramMKey;
    assert (this.mJSStr != null);
    putCache(paramMKey);
    if (this.mAPIMenu != null)
      this.mLastUpdateTimeMs = this.mAPIMenu.getLastUpdateTime().getValue();
    else
      this.mLastUpdateTimeMs = 0L;
  }

  protected void putCache(MKey paramMKey)
  {
    MCache.put(paramMKey.getCacheKey(), this);
  }

  protected List<MenuItem> expandMenu(ServerLogin paramServerLogin, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(6);
    try
    {
      List localList = paramServerLogin.expandMenu(paramMenu);
      return localList;
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException, false);
    }
    finally
    {
      localMeasurement.end();
    }
  }

  protected List<MenuItem> expandMenu(ServerLogin paramServerLogin, com.bmc.arsys.api.Menu paramMenu, Entry paramEntry1, Entry paramEntry2)
    throws GoatException
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(6);
    try
    {
      if (isReturnFullQuery())
      {
        List localList1 = paramServerLogin.expandMenu(paramMenu, paramEntry1, paramEntry2);
        return localList1;
      }
      int i = Configuration.getInstance().getMaxRetrieveForAutoCompleteMenu();
      List localList2 = paramServerLogin.expandMenu(paramMenu, paramEntry1, paramEntry2, i + 1, null);
      List localList3 = localList2;
      return localList3;
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException, false);
    }
    finally
    {
      localMeasurement.end();
    }
  }

  public void emitJSDefinitionStatement(JSWriter paramJSWriter, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramJSWriter.startStatement("new ARMenu(").append("windowID").comma().appendqs(this.mName).comma();
      paramJSWriter.append(this.mJSStr);
      paramJSWriter.append(")");
    }
    else
    {
      paramJSWriter.startStatement("this.ARMenus[").appendqs(this.mName).append("]=");
      paramJSWriter.append(this.mJSStr);
    }
    paramJSWriter.endStatement();
  }

  protected void recurEmitJS(JSWriter paramJSWriter, List<MenuItem> paramList, int paramInt)
  {
    paramJSWriter.openObj();
    MenuItem[] arrayOfMenuItem = (MenuItem[])paramList.toArray(new MenuItem[0]);
    boolean bool = true;
    if ((arrayOfMenuItem != null) && (arrayOfMenuItem.length > 0))
      bool = false;
    paramJSWriter.property("t", paramInt);
    paramJSWriter.property("novals", bool);
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openList();
    int i = Configuration.getInstance().getMaxRetrieveForAutoCompleteMenu();
    OutputInteger localOutputInteger = new OutputInteger(0);
    if ((arrayOfMenuItem != null) && (arrayOfMenuItem.length > 0))
    {
      for (int j = 0; j < arrayOfMenuItem.length; j++)
        if (arrayOfMenuItem[j] != null)
        {
          Object localObject;
          if (arrayOfMenuItem[j].getType() == 1)
          {
            if (!isReturnFullQuery())
            {
              localOutputInteger.setValue(localOutputInteger.intValue() + 1);
              if (localOutputInteger.intValue() > i);
            }
            else
            {
              localObject = arrayOfMenuItem[j].getLabel();
              String str2 = (String)arrayOfMenuItem[j].getContent();
              assert ((localObject != null) && (str2 != null));
              if (((String)localObject).length() == 0)
                localObject = str2;
              if (((String)localObject).trim().length() == 0)
                localObject = "";
              localJSWriter.listSep().openObj();
              localJSWriter.propertyDestinedForHTML("l", (String)localObject);
              if (str2.equals(localObject))
                localJSWriter.property("v");
              else
                localJSWriter.propertyDestinedForHTML("v", str2);
              localJSWriter.closeObj();
            }
          }
          else if (arrayOfMenuItem[j].getSubMenu() != null)
            if ((!isReturnFullQuery()) && (localOutputInteger.intValue() + 1 > i))
            {
              localOutputInteger.setValue(localOutputInteger.intValue() + 1);
            }
            else
            {
              localJSWriter.listSep();
              localObject = arrayOfMenuItem[j].getLabel().toString();
              assert (localObject != null);
              localJSWriter.openObj().propertyDestinedForHTML("l", (String)localObject).comma().append("v:");
              recurEmitJSInside(localJSWriter, arrayOfMenuItem[j].getSubMenu(), localOutputInteger, i);
              localJSWriter.closeObj();
            }
        }
    }
    else
    {
      localJSWriter.openObj();
      String str1 = MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "(no entries in menu)");
      localJSWriter.propertyDestinedForHTML("l", str1);
      localJSWriter.property("v");
      localJSWriter.propertyDestinedForHTML("id", "clearValue");
      localJSWriter.closeObj();
    }
    localJSWriter.closeList();
    paramJSWriter.property("mval", localJSWriter);
    if (isReturnFullQuery())
    {
      if (arrayOfMenuItem == null)
        paramJSWriter.property("tItems", 0);
      else
        paramJSWriter.property("tItems", arrayOfMenuItem.length);
    }
    else
      paramJSWriter.property("tItems", localOutputInteger);
    paramJSWriter.closeObj();
  }

  protected void recurEmitJSInside(JSWriter paramJSWriter, List<MenuItem> paramList, OutputInteger paramOutputInteger, int paramInt)
  {
    paramJSWriter.openList();
    MenuItem[] arrayOfMenuItem = (MenuItem[])paramList.toArray(new MenuItem[0]);
    if ((arrayOfMenuItem != null) && (arrayOfMenuItem.length > 0))
      for (int i = 0; i < arrayOfMenuItem.length; i++)
        if (arrayOfMenuItem[i] != null)
        {
          Object localObject;
          if (arrayOfMenuItem[i].getType() == 1)
          {
            if (!isReturnFullQuery())
            {
              paramOutputInteger.setValue(paramOutputInteger.intValue() + 1);
              if (paramOutputInteger.intValue() > paramInt);
            }
            else
            {
              localObject = arrayOfMenuItem[i].getLabel().toString();
              String str = (String)arrayOfMenuItem[i].getContent();
              assert ((localObject != null) && (str != null));
              if (((String)localObject).length() == 0)
                localObject = str;
              if (((String)localObject).trim().length() == 0)
                localObject = "";
              paramJSWriter.listSep().openObj();
              paramJSWriter.propertyDestinedForHTML("l", (String)localObject);
              if (str.equals(localObject))
                paramJSWriter.property("v");
              else
                paramJSWriter.propertyDestinedForHTML("v", str);
              paramJSWriter.closeObj();
            }
          }
          else if (arrayOfMenuItem[i].getSubMenu() != null)
            if ((!isReturnFullQuery()) && (paramOutputInteger.intValue() + 1 > paramInt))
            {
              paramOutputInteger.setValue(paramOutputInteger.intValue() + 1);
            }
            else
            {
              paramJSWriter.listSep();
              localObject = arrayOfMenuItem[i].getLabel().toString();
              assert (localObject != null);
              paramJSWriter.openObj().propertyDestinedForHTML("l", (String)localObject).comma().append("v:");
              recurEmitJSInside(paramJSWriter, arrayOfMenuItem[i].getSubMenu(), paramOutputInteger, paramInt);
              paramJSWriter.closeObj();
            }
        }
    paramJSWriter.closeList();
  }

  protected abstract String getJS()
    throws GoatException;

  public abstract void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException;

  public static void emitMenus(JSWriter paramJSWriter, String paramString, Set paramSet)
  {
    Iterator localIterator = paramSet.iterator();
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if ((!GroupMenu.isGroupMenu(str1)) && (!str1.equals("$NULL$")))
      {
        localObject = new MKey(paramString, SessionData.get().getLocale(), str1);
        String str3 = ((MKey)localObject).getCacheKeyWithLocale();
        Menu localMenu = null;
        localMenu = (Menu)MCache.get(str3, Menu.class);
        if (localMenu == null)
        {
          str3 = ((MKey)localObject).getCacheKey();
          localMenu = (Menu)MCache.get(str3, Menu.class);
          if (localMenu == null)
            localArrayList.add(str1);
        }
      }
    }
    if (localArrayList.size() > 0)
      try
      {
        bulkLoad(paramString, localArrayList, false);
      }
      catch (GoatException localGoatException1)
      {
        localGoatException1.printStackTrace();
      }
    localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      if (!str2.equals("$NULL$"))
        try
        {
          localObject = get(new MKey(paramString, SessionData.get().getLocale(), str2));
          if (localObject != null)
            ((Menu)localObject).emitJSDefinitionStatement(paramJSWriter, false);
        }
        catch (GoatException localGoatException2)
        {
          localGoatException2.printStackTrace();
        }
    }
  }

  public static void bulkLoad(String paramString, List<String> paramList, boolean paramBoolean)
    throws GoatException
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(15);
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString);
    Object localObject1 = new ArrayList();
    try
    {
      MenuCriteria localMenuCriteria = new MenuCriteria();
      localMenuCriteria.setRetrieveAll(true);
      localObject1 = localServerLogin.getListMenuObjects(0L, paramList, localMenuCriteria);
      ServerSync.initCharMenuObjectTimesIfNeeded(paramString);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
    Iterator localIterator = ((List)localObject1).iterator();
    while (localIterator.hasNext())
    {
      com.bmc.arsys.api.Menu localMenu = (com.bmc.arsys.api.Menu)localIterator.next();
      if (localMenu != null)
      {
        MKey localMKey = new MKey(paramString, SessionData.get().getLocale(), localMenu.getKey());
        if ((paramBoolean) && (!(localMenu instanceof com.bmc.arsys.api.QueryMenu)))
          createMenu(localMKey, localMenu);
        else if (!paramBoolean)
          createMenu(localMKey, localMenu);
      }
    }
  }

  public static com.bmc.arsys.api.Menu getMenuFromServer(MKey paramMKey)
    throws GoatException
  {
    com.bmc.arsys.api.Menu localMenu = null;
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(15);
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramMKey.getServer());
    try
    {
      MenuCriteria localMenuCriteria = new MenuCriteria();
      localMenuCriteria.setRetrieveAll(true);
      localMenu = localServerLogin.getMenu(paramMKey.getMenuName(), localMenuCriteria);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
    if (localMenu == null)
      throw new GoatException("MenuFactory.findByKey(" + paramMKey.getMenuName() + ") failed");
    return localMenu;
  }

  public static Menu get(MKey paramMKey)
    throws GoatException
  {
    String str1 = paramMKey.getCacheKeyWithLocale().intern();
    boolean bool = GroupMenu.isGroupMenu(paramMKey.getMenuName());
    synchronized (str1)
    {
      Menu localMenu = null;
      Cache.Item localItem = MCache.get(str1);
      localMenu = (Menu)MCache.get(str1, Menu.class);
      if (localMenu != null)
      {
        if ((localItem == null) && (bool))
          localMenu.mJSStr = localMenu.getJS();
        return localMenu;
      }
      String str2 = paramMKey.getCacheKey().intern();
      synchronized (str2)
      {
        localItem = MCache.get(str2);
        localMenu = (Menu)MCache.get(str2, Menu.class);
        if (localMenu != null)
        {
          if ((localItem == null) && (bool))
            localMenu.mJSStr = localMenu.getJS();
          return localMenu;
        }
        com.bmc.arsys.api.Menu localMenu1 = null;
        if (bool)
          return new GroupMenu(paramMKey);
        localMenu1 = getMenuFromServer(paramMKey);
        if (localMenu1 != null)
        {
          ServerSync.initCharMenuObjectTimesIfNeeded(paramMKey.getServer());
          return createMenu(paramMKey, localMenu1);
        }
      }
    }
    return null;
  }

  public static Menu createMenu(MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    if ((paramMenu instanceof com.bmc.arsys.api.FileMenu))
      return new FileMenu(paramMKey, paramMenu);
    if ((paramMenu instanceof ListMenu))
      return new CharMenu(paramMKey, paramMenu);
    if ((paramMenu instanceof com.bmc.arsys.api.QueryMenu))
      return new QueryMenu(paramMKey, paramMenu);
    if ((paramMenu instanceof SqlMenu))
      return new SQLMenu(paramMKey, paramMenu);
    if ((paramMenu instanceof DataDictionaryMenu))
      return new DDMenu(paramMKey, paramMenu);
    return null;
  }

  public int getSize()
  {
    return 1;
  }

  public String getServer()
  {
    return this.mServer;
  }

  public long getLastUpdateTimeMs()
  {
    return this.mLastUpdateTimeMs;
  }

  public String getMenuName()
  {
    return this.mName;
  }

  public MKey getKey()
  {
    return this.mKey;
  }

  public static Cache getCache()
  {
    return MCache;
  }

  public void finalize()
    throws Throwable
  {
    try
    {
      synchronized (delObjCnt)
      {
        delObjCnt.inc(9);
      }
    }
    finally
    {
      super.finalize();
    }
  }

  public boolean isReturnFullQuery()
  {
    return this.mReturnFullQuery;
  }

  public void setReturnFullQuery(boolean paramBoolean)
  {
    this.mReturnFullQuery = paramBoolean;
  }

  public static class MKey
    implements Serializable
  {
    private static final long serialVersionUID = 6958191073610824497L;
    private final String mServer;
    private final String mLocale;
    private final String mName;
    private String mAppName;

    public MKey(String paramString1, String paramString2, String paramString3)
    {
      this.mServer = paramString1;
      this.mLocale = paramString2;
      this.mName = paramString3;
      this.mAppName = "";
    }

    public MKey(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      this(paramString1, paramString2, paramString3);
      this.mAppName = (paramString4 == null ? "" : paramString4);
    }

    private String getServer()
    {
      return this.mServer;
    }

    private String getLocale()
    {
      return this.mLocale;
    }

    private String getMenuName()
    {
      return this.mName;
    }

    private String getAppName()
    {
      return this.mAppName;
    }

    public String getCacheKey()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String str = "/";
      localStringBuilder.append(this.mServer.toLowerCase()).append(str).append(this.mAppName).append(str).append(this.mName);
      return localStringBuilder.toString();
    }

    public String getCacheKeyWithLocale()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String str = "/";
      localStringBuilder.append(this.mServer.toLowerCase()).append(str).append(this.mAppName).append(str).append(this.mLocale).append(str).append(this.mName);
      return localStringBuilder.toString();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.Menu
 * JD-Core Version:    0.6.1
 */