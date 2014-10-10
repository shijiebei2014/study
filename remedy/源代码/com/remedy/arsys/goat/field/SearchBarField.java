package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.Form;

public class SearchBarField extends GoatField
{
  private static final long serialVersionUID = 9061757033274394712L;

  public SearchBarField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    setMEmitViewable(0);
    setMEmitOptimised(0);
    setMEmitViewless(2);
  }

  public boolean amSearchBar()
  {
    return isMInView();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.SearchBarField
 * JD-Core Version:    0.6.1
 */