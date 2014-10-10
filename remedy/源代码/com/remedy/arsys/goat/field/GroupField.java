package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.menu.GroupMenu;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.SessionData;

public class GroupField extends CharField
{
  private static final long serialVersionUID = 3833786117691471872L;
  private boolean mIsGroupMenu;

  public GroupField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    if (getMCharMenu() == null)
    {
      int i = getMFieldID();
      setMCharMenu(GroupMenu.getMenuName(i));
      if (getMCharMenu() != null)
      {
        setMIsGroupMenu(true);
        try
        {
          Menu.get(new Menu.MKey(paramForm.getServerName(), SessionData.get().getLocale(), getMCharMenu(), paramForm.getAppName()));
        }
        catch (GoatException localGoatException1)
        {
          localGoatException1.printStackTrace();
        }
        setMMenuStyle(1);
        try
        {
          if ((Group.isMultiAssignField(i)) && (!ServerInfo.get(paramForm.getServerName()).isMultiAssignGroup()))
            setMMenuStyle(2);
        }
        catch (GoatException localGoatException2)
        {
        }
      }
    }
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes, int paramInt)
  {
    paramOutputNotes.setGroupFieldPresent(true);
    super.addToOutputNotes(paramOutputNotes, paramInt);
  }

  protected void addMenuToOutputNotes(OutputNotes paramOutputNotes)
  {
    if (isMIsGroupMenu())
      paramOutputNotes.addGroupFieldMenus(getMCharMenu());
    else
      super.addMenuToOutputNotes(paramOutputNotes);
  }

  protected void setMIsGroupMenu(boolean paramBoolean)
  {
    this.mIsGroupMenu = paramBoolean;
  }

  protected boolean isMIsGroupMenu()
  {
    return this.mIsGroupMenu;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.GroupField
 * JD-Core Version:    0.6.1
 */