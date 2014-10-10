package com.remedy.arsys.goat.skins;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.LocalImageFetcher;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatImage;
import com.remedy.arsys.goat.GoatImage.Fetcher;
import com.remedy.arsys.goat.field.ControlField;
import com.remedy.arsys.goat.field.ControlField.LocalImageFetcher;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.PageField;
import com.remedy.arsys.goat.field.PageField.LocalImageFetcher;

public class SkinPropertyMapper extends DisplayPropertyMappers
{
  public static String mPropToHTMLColour(Value paramValue)
  {
    try
    {
      return propToHTMLColour(paramValue);
    }
    catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
    {
    }
    return null;
  }

  public static GoatImage mPropToGoatImage(Value paramValue, String paramString1, String paramString2, GoatField paramGoatField, Form.ViewInfo paramViewInfo)
  {
    try
    {
      Object localObject1;
      Object localObject2;
      if (paramString2.equals("Control"))
      {
        localObject1 = (ControlField)paramGoatField;
        Object tmp21_19 = localObject1;
        tmp21_19.getClass();
        localObject2 = new ControlField.LocalImageFetcher(tmp21_19);
        return propToGoatImage(paramValue, (GoatImage.Fetcher)localObject2, paramString1);
      }
      if (paramString2.equals("View"))
      {
        Form tmp57_54 = paramViewInfo.getForm();
        tmp57_54.getClass();
        localObject1 = new Form.LocalImageFetcher(tmp57_54);
        return propToGoatImage(paramValue, (GoatImage.Fetcher)localObject1, paramString1);
      }
      if (paramString2.equals("Panel"))
      {
        localObject1 = (PageField)paramGoatField;
        Object tmp96_94 = localObject1;
        tmp96_94.getClass();
        localObject2 = new PageField.LocalImageFetcher(tmp96_94);
        return propToGoatImage(paramValue, (GoatImage.Fetcher)localObject2, paramString1);
      }
      return null;
    }
    catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
    {
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.skins.SkinPropertyMapper
 * JD-Core Version:    0.6.1
 */