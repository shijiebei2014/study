package com.remedy.arsys.plugincontainer.impl;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.intf.service.IFormService;
import com.remedy.arsys.goat.service.SkinService;
import com.remedy.arsys.goat.skins.SkinDefinitionList;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import com.remedy.arsys.plugincontainer.AuthenticationException;
import com.remedy.arsys.plugincontainer.IDVMSkinService;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.RequestInfo;
import com.remedy.arsys.stubs.ServerLogin;

public class DVMSkinServiceImpl
  implements IDVMSkinService
{
  private String mServer;
  private String mForm;
  private String mViewName;
  private String mAppName;
  private String mPluginName;
  private SkinDefinitionMap mSkinDefMap;
  private SkinService mSkinService;

  public DVMSkinServiceImpl(PluginContextImpl paramPluginContextImpl, RequestInfo paramRequestInfo)
    throws GoatException
  {
    this.mServer = paramPluginContextImpl.getServer();
    this.mPluginName = paramPluginContextImpl.getPluginName();
    this.mForm = paramRequestInfo.getFormName();
    this.mViewName = paramRequestInfo.getViewName();
    this.mAppName = paramRequestInfo.getAppname();
    this.mSkinService = ((SkinService)ServiceLocator.getInstance().getService("skinService"));
    Form localForm = ((IFormService)ServiceLocator.getInstance().getService("formService")).getForm(this.mServer, this.mForm);
    Form.ViewInfo localViewInfo = localForm.getViewInfo(this.mViewName);
    try
    {
      ServerLogin localServerLogin1 = (ServerLogin)paramPluginContextImpl.getServerUserObject();
      SkinDefinitionList localSkinDefinitionList = this.mSkinService.getSkins(localServerLogin1, localViewInfo);
      if (localSkinDefinitionList.getSize() > 0)
      {
        ServerLogin localServerLogin2 = ServerLogin.getAdmin(this.mServer);
        this.mSkinDefMap = this.mSkinService.getDVMSkinProperties(localServerLogin2, localViewInfo, localSkinDefinitionList, this.mPluginName);
      }
    }
    catch (AuthenticationException localAuthenticationException)
    {
      throw new GoatException(localAuthenticationException.getLocalizedMessage());
    }
  }

  public String getSkinProperty(String paramString)
  {
    if (this.mSkinDefMap != null)
    {
      Value localValue = this.mSkinDefMap.getProperty(this.mPluginName, paramString, (short)4);
      if (localValue != null)
        return localValue.toString();
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.DVMSkinServiceImpl
 * JD-Core Version:    0.6.1
 */