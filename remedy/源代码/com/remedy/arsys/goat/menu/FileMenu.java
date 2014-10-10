package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.Entry;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class FileMenu extends Menu
{
  private static final long serialVersionUID = 1050226000498416705L;
  private static String FIELDMENU_ID = "arfield://";

  protected FileMenu(Menu.MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
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
      com.bmc.arsys.api.FileMenu localFileMenu = (com.bmc.arsys.api.FileMenu)this.mAPIMenu;
      if ((localFileMenu != null) && (localFileMenu.getLocation() == 1))
      {
        localJSWriter.property("r", this.mAPIMenu.getRefreshCode());
      }
      else if (localFileMenu != null)
      {
        String str1 = localFileMenu.getFileName();
        if ((str1 != null) && (str1.length() >= FIELDMENU_ID.length()) && (str1.indexOf(FIELDMENU_ID) == 0) && (localFileMenu.getLocation() == 2))
        {
          String str2 = str1.substring(FIELDMENU_ID.length());
          Integer localInteger = null;
          try
          {
            localInteger = Integer.valueOf(str2);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            localInteger = null;
          }
          if (localInteger != null)
          {
            localJSWriter.property("r", this.mAPIMenu.getRefreshCode());
            localJSWriter.property("armfid", localInteger.intValue());
          }
        }
      }
      localJSWriter.property("t", 2);
    }
    localJSWriter.closeObj();
    return localJSWriter.toString();
  }

  public void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException
  {
    synchronized (this.mAPIMenu)
    {
      recurEmitJS(paramJSWriter, expandMenu(SessionData.get().getServerLogin(paramString1), this.mAPIMenu), 2);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.FileMenu
 * JD-Core Version:    0.6.1
 */