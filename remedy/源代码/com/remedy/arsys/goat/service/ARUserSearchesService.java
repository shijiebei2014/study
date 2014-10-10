package com.remedy.arsys.goat.service;

import com.remedy.arsys.goat.intf.service.IARUserSearchesService;
import com.remedy.arsys.goat.savesearches.ARUserSearches;

public class ARUserSearchesService
  implements IARUserSearchesService
{
  public ARUserSearches getUserSearches(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    ARUserSearches localARUserSearches = new ARUserSearches(paramString1, paramString2, paramString3);
    if ((localARUserSearches.getServer() == null) || (localARUserSearches.getPrefForm() == null))
      return null;
    return localARUserSearches;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.ARUserSearchesService
 * JD-Core Version:    0.6.1
 */