package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;

public class AlertListField extends TableField
{
  private static final long serialVersionUID = 336097189938118355L;

  public AlertListField(Form paramForm, Field paramField, int paramInt)
    throws GoatException
  {
    super(paramForm, paramField, paramInt);
    setMFixedTableHeaders(true);
    setMDrillDown(true);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.AlertListField
 * JD-Core Version:    0.6.1
 */