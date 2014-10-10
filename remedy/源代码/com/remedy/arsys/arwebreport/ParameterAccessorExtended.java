package com.remedy.arsys.arwebreport;

import java.util.Map;
import org.eclipse.birt.report.utility.ParameterAccessor;

public class ParameterAccessorExtended extends ParameterAccessor
{
  public static void setInitProp(String paramString1, String paramString2)
  {
    initProps.put(paramString1, paramString2);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.ParameterAccessorExtended
 * JD-Core Version:    0.6.1
 */