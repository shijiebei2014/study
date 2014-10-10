package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.stubs.SessionData;

public class GetMenuDefinitionAgent extends NDXGetMenuDefinition
{
  public GetMenuDefinitionAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    Menu.MKey localMKey = new Menu.MKey(this.mServer, SessionData.get().getLocale(), this.mName);
    Menu localMenu;
    try
    {
      localMenu = Menu.get(localMKey);
    }
    catch (GoatException localGoatException)
    {
      throw new GoatException(9372, localGoatException);
    }
    if (localMenu != null)
    {
      append("this.result=");
      localMenu.emitJSDefinitionStatement(this, true);
    }
    else
    {
      throw new GoatException(9372);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetMenuDefinitionAgent
 * JD-Core Version:    0.6.1
 */