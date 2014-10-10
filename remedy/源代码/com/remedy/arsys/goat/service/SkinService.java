package com.remedy.arsys.goat.service;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.intf.service.ISkinDefinitionService;
import com.remedy.arsys.goat.skins.SkinDefinitionList;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import com.remedy.arsys.goat.skins.SkinFactory;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.HashSet;
import java.util.Set;

public class SkinService
  implements ISkinDefinitionService
{
  public SkinDefinitionMap getSkinDefinitions(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkinDefinitions();
  }

  public SkinDefinitionList getSkins(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkins();
  }

  public SkinDefinitionMap getSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkinProperties(paramSkinDefinitionList, -1, "", new HashSet());
  }

  public String getSkinKey(Form.ViewInfo paramViewInfo)
    throws GoatException
  {
    return paramViewInfo.getForm().getServerName() + "/" + FormContext.get().getApplication() + "/" + paramViewInfo.getForm().getName() + "/" + paramViewInfo.mID + "/" + paramViewInfo.getForm().getServerLogin().getPermissionsKey();
  }

  public String getSkinCacheKey(String paramString1, String paramString2)
  {
    return paramString1 + "/" + paramString2;
  }

  public SkinDefinitionMap getSystemSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkinProperties(paramSkinDefinitionList, 3, "");
  }

  public SkinDefinitionMap getDVMSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList, String paramString)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkinProperties(paramSkinDefinitionList, 4, paramString);
  }

  public SkinDefinitionMap getTemplatesSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList, Set<String> paramSet)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkinProperties(paramSkinDefinitionList, 5, "", paramSet);
  }

  public SkinDefinitionMap getWebHeaderSkinProperties(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo, SkinDefinitionList paramSkinDefinitionList)
  {
    SkinFactory localSkinFactory = new SkinFactory(paramServerLogin, paramViewInfo);
    return localSkinFactory.getSkinProperties(paramSkinDefinitionList, 6, "");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.SkinService
 * JD-Core Version:    0.6.1
 */