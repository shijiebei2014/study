package com.remedy.arsys.goat.service;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatFieldMap;
import com.remedy.arsys.goat.intf.service.IGoatFieldMapService;
import com.remedy.arsys.goat.intf.service.IGoatFieldService;
import java.util.HashMap;
import java.util.Map;

public class GoatFieldService
  implements IGoatFieldService
{
  private IGoatFieldMapService goatfieldMapService;

  public void setGoatFieldMapService(IGoatFieldMapService paramIGoatFieldMapService)
  {
    this.goatfieldMapService = paramIGoatFieldMapService;
  }

  public Map get(Form.ViewInfo paramViewInfo)
    throws GoatException
  {
    return GoatField.get(paramViewInfo, false);
  }

  public Map get(Form.ViewInfo paramViewInfo, boolean paramBoolean)
    throws GoatException
  {
    return get(paramViewInfo, paramBoolean, false);
  }

  public Map get(Form.ViewInfo paramViewInfo, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException
  {
    Form localForm = paramViewInfo.getContainingForm();
    int i = paramViewInfo.getID();
    int[] arrayOfInt = localForm.getFieldIDs(paramBoolean1);
    int j = arrayOfInt.length;
    GoatFieldMap localGoatFieldMap = this.goatfieldMapService.getGoatFieldMap(localForm, paramViewInfo, paramBoolean1, paramBoolean2);
    HashMap localHashMap = new HashMap();
    for (int k = 0; k < j; k++)
    {
      String str = genHashKey(localForm, i, arrayOfInt[k]);
      GoatField localGoatField = (GoatField)localGoatFieldMap.get(str);
      assert (localGoatField != null);
      localHashMap.put(Integer.valueOf(arrayOfInt[k]), localGoatField);
    }
    return localHashMap;
  }

  static String genHashKey(Form paramForm, int paramInt1, int paramInt2)
  {
    return "" + paramInt2 + paramInt1 + paramForm.getServerFormNames();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.GoatFieldService
 * JD-Core Version:    0.6.1
 */