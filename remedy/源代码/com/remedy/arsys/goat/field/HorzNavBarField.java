package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;

public class HorzNavBarField extends GoatField
{
  private static final long serialVersionUID = -7422458521013425589L;
  private int mAccess;
  private long mInitialValue;
  private int mWorkflowOnSelected;
  private int mSelectOnClick;

  public HorzNavBarField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    switch (paramInt)
    {
    case 5:
      setMAccess(propToInt(paramValue));
      break;
    case 5063:
      setMInitialValue(propToLong(paramValue));
      break;
    case 5064:
      setMWorkflowOnSelected(propToInt(paramValue));
      if ((getMWorkflowOnSelected() != 1) && (getMWorkflowOnSelected() != 0))
        setMWorkflowOnSelected(0);
      break;
    case 5065:
      if (propToInt(paramValue) == 1)
        setMSelectOnClick(1);
      else
        setMSelectOnClick(0);
      break;
    default:
      super.handleProperty(paramInt, paramValue);
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

  protected void setMInitialValue(long paramLong)
  {
    this.mInitialValue = paramLong;
  }

  public long getMInitialValue()
  {
    return this.mInitialValue;
  }

  protected void setMWorkflowOnSelected(int paramInt)
  {
    this.mWorkflowOnSelected = paramInt;
  }

  public int getMWorkflowOnSelected()
  {
    return this.mWorkflowOnSelected;
  }

  protected void setMSelectOnClick(int paramInt)
  {
    this.mSelectOnClick = paramInt;
  }

  public int getMSelectOnClick()
  {
    return this.mSelectOnClick;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.HorzNavBarField
 * JD-Core Version:    0.6.1
 */