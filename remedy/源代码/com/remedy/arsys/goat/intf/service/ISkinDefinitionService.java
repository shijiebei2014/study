package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.skins.SkinDefinitionList;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.Set;

public abstract interface ISkinDefinitionService
{
  public abstract SkinDefinitionMap getSkinDefinitions(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo);

  public abstract SkinDefinitionList getSkins(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo);

  public abstract SkinDefinitionMap getSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList);

  public abstract String getSkinKey(Form.ViewInfo paramViewInfo)
    throws GoatException;

  public abstract String getSkinCacheKey(String paramString1, String paramString2);

  public abstract SkinDefinitionMap getSystemSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList);

  public abstract SkinDefinitionMap getTemplatesSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList, Set<String> paramSet);

  public abstract SkinDefinitionMap getWebHeaderSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList);
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.ISkinDefinitionService
 * JD-Core Version:    0.6.1
 */