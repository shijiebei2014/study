package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.SqlMenu;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.action.ARCommandString;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class SQLMenu extends Menu
{
  private static final long serialVersionUID = 1301314673855068962L;

  public SQLMenu(Menu.MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    super(paramMKey, paramMenu);
  }

  protected String getJS()
  {
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openObj();
    synchronized (this.mAPIMenu)
    {
      SqlMenu localSqlMenu = (SqlMenu)this.mAPIMenu;
      if (localSqlMenu != null)
      {
        String str = localSqlMenu.getSQLCommand();
        assert (str != null);
        ARCommandString localARCommandString = new ARCommandString(str);
        assert (localARCommandString != null);
        localJSWriter.append("ids:");
        localARCommandString.emitFieldReferencesAsJSArray(localJSWriter);
        localJSWriter.comma();
        localJSWriter.append("kwds:");
        localARCommandString.emitKeywordReferencesAsJSArray(localJSWriter);
        localJSWriter.property("r", this.mAPIMenu.getRefreshCode());
      }
      localJSWriter.property("t", 5);
    }
    localJSWriter.closeObj();
    return localJSWriter.toString();
  }

  public void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException
  {
    recurEmitJS(paramJSWriter, expandMenu(SessionData.get().getServerLogin(paramString1), this.mAPIMenu, paramEntry2, paramEntry3), 5);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.SQLMenu
 * JD-Core Version:    0.6.1
 */