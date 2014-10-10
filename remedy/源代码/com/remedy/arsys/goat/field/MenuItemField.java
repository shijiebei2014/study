package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.log.Log;

public class MenuItemField extends GoatField
{
  private static final long serialVersionUID = 6038002369738432001L;
  private int mAccess;
  private int mPosition;
  private int mMode;
  private long mParent;

  public MenuItemField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    setMEmitViewable(1);
    setMEmitOptimised(2);
    setMParentExecDep(true);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 5)
    {
      setMAccess(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_ENABLE: " + getMAccess());
    }
    else if (paramInt == 121)
    {
      setMPosition(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_MENU_POS: " + getMPosition());
    }
    else if (paramInt == 123)
    {
      setMMenuParent(propToLong(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_MENU_PARENT: " + getMMenuParent());
    }
    else if (paramInt == 122)
    {
      setMMode(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_MENU_MODE: " + getMMode());
    }
    else if (paramInt == 120)
    {
      setMLabel(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_MENU_TEXT: " + getMLabel());
    }
    else
    {
      super.handleProperty(paramInt, paramValue);
    }
  }

  public void setMAccess(int paramInt)
  {
    this.mAccess = paramInt;
  }

  public int getMAccess()
  {
    return this.mAccess;
  }

  public void setMPosition(int paramInt)
  {
    this.mPosition = paramInt;
  }

  public int getMPosition()
  {
    return this.mPosition;
  }

  public long getMMenuParent()
  {
    return this.mParent;
  }

  public void setMMenuParent(long paramLong)
  {
    this.mParent = paramLong;
  }

  public void setMMode(int paramInt)
  {
    this.mMode = paramInt;
  }

  public int getMMode()
  {
    return this.mMode;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.MenuItemField
 * JD-Core Version:    0.6.1
 */