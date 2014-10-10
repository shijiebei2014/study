package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.MenuItem;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.List;

public class CharMenu extends Menu
{
  private static final long serialVersionUID = 3835182931053013103L;

  public CharMenu(Menu.MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    super(paramMKey, paramMenu);
  }

  protected void putCache(Menu.MKey paramMKey)
  {
    MCache.put(paramMKey.getCacheKeyWithLocale(), this);
  }

  protected String getJS()
    throws GoatException
  {
    JSWriter localJSWriter = new JSWriter();
    List localList = null;
    String str = MessageTranslation.getLocalizedMenu(this.mServer, this.mLocale, this.mName);
    if (str != null)
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
      try
      {
        localList = localServerLogin.convertStringToListMenu(str);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
    }
    else
    {
      synchronized (this.mAPIMenu)
      {
        if (this.mAPIMenu != null)
          localList = this.mAPIMenu.getContent();
      }
    }
    recurEmitJS(localJSWriter, localList, 0);
    return localJSWriter.toString();
  }

  public void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException
  {
    synchronized (this.mAPIMenu)
    {
      recurEmitJS(paramJSWriter, expandMenu(SessionData.get().getServerLogin(paramString1), this.mAPIMenu), 0);
    }
  }

  public List<MenuItem> getItems()
  {
    return this.mAPIMenu.getContent();
  }

  public static String getJSSimpleCharMenu(String paramString, ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2)
  {
    JSWriter localJSWriter1 = new JSWriter();
    localJSWriter1.startStatement("new ARMenu(").append("windowID").comma().appendqs(paramString).comma();
    JSWriter localJSWriter2 = new JSWriter();
    localJSWriter2.openObj();
    localJSWriter2.property("t", 0);
    localJSWriter2.property("novals", false);
    JSWriter localJSWriter3 = new JSWriter();
    localJSWriter3.openList();
    for (int i = 0; i < paramArrayList1.size(); i++)
    {
      if (i != 0)
        localJSWriter3.comma();
      localJSWriter3.openObj();
      localJSWriter3.property("l", (String)paramArrayList1.get(i));
      localJSWriter3.property("v", (String)paramArrayList2.get(i));
      localJSWriter3.closeObj();
    }
    localJSWriter3.closeList();
    localJSWriter2.property("mval", localJSWriter3);
    localJSWriter2.property("tItems", paramArrayList1.size());
    localJSWriter2.closeObj();
    localJSWriter1.append(localJSWriter2.toString());
    localJSWriter1.append(")");
    return localJSWriter1.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.CharMenu
 * JD-Core Version:    0.6.1
 */