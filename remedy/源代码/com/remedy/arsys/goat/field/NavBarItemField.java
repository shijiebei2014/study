package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.log.Log;

public class NavBarItemField extends GoatField
{
  private static final long serialVersionUID = -2636585741923628802L;
  private int mAccess;
  private int mPosition;
  private int mItemType;

  public NavBarItemField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 5)
    {
      setMAccess(propToInt(paramValue));
    }
    else
    {
      int i;
      if (paramInt == 123)
      {
        i = propToFieldID(paramValue);
        if (i != 0)
        {
          assert (getMParentFieldID() == 0);
          setMParentFieldID(i);
          MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_MENU_PARENT(nav): " + getMParentFieldID());
        }
      }
      else if (paramInt == 170)
      {
        i = propToFieldID(paramValue);
        if (i != 0)
        {
          assert (getMParentFieldID() == 0);
          setMParentFieldID(propToFieldID(paramValue));
          MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DISPLAY_PARENT: " + getMParentFieldID());
        }
      }
      else if (paramInt == 120)
      {
        setMLabel(propToString(paramValue));
      }
      else if (paramInt == 121)
      {
        setMPosition(propToInt(paramValue));
      }
      else if (paramInt == 122)
      {
        setMItemType(propToInt(paramValue));
      }
      else
      {
        super.handleProperty(paramInt, paramValue);
      }
    }
  }

  protected void setDefaultDisplayProperties()
  {
    super.setDefaultDisplayProperties();
    setMAccess(2);
  }

  protected void setMAccess(int paramInt)
  {
    this.mAccess = paramInt;
  }

  public int getMAccess()
  {
    return this.mAccess;
  }

  protected void setMPosition(int paramInt)
  {
    this.mPosition = paramInt;
  }

  public int getMPosition()
  {
    return this.mPosition;
  }

  protected void setMItemType(int paramInt)
  {
    this.mItemType = paramInt;
  }

  public int getMItemType()
  {
    return this.mItemType;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.NavBarItemField
 * JD-Core Version:    0.6.1
 */