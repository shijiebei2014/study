package com.remedy.arsys.share;

import com.bmc.arsys.api.ARQualifierHelper;
import java.util.HashMap;

public class ARQualifierHelper2 extends ARQualifierHelper
{
  private static final long serialVersionUID = -3418170928397329622L;

  protected void finalize()
    throws Throwable
  {
    try
    {
      HashMap localHashMap1 = getLocalFieldLabelMap();
      if (localHashMap1 != null)
        localHashMap1.clear();
      HashMap localHashMap2 = getLocalFieldIdMap();
      if (localHashMap2 != null)
        localHashMap2.clear();
    }
    finally
    {
      super.finalize();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ARQualifierHelper2
 * JD-Core Version:    0.6.1
 */