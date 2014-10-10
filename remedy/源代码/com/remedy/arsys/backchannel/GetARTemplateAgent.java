package com.remedy.arsys.backchannel;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.service.SkinService;
import com.remedy.arsys.goat.sharedresource.ARSharedResource;
import com.remedy.arsys.goat.sharedresource.IResourceObject;
import com.remedy.arsys.goat.skins.SkinDefinitionList;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.HashSet;
import java.util.Set;

public class GetARTemplateAgent extends NDXGetARTemplate
{
  public GetARTemplateAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    ARSharedResource localARSharedResource = ARSharedResource.get(this.mServer, this.mTemplate, 0, null, this.mContextPath);
    IResourceObject localIResourceObject = null;
    if (localARSharedResource != null)
      localIResourceObject = localARSharedResource.getResourceObject();
    append("this.result=");
    Emitter localEmitter = new Emitter(this, (IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
    if (localIResourceObject != null)
    {
      if (!FormContext.get().IsVoiceAccessibleUser())
      {
        if (Configuration.getInstance().getBooleanProperty("arsystem.enableSkins", false))
          localIResourceObject.emitJS(localEmitter, false, populateSkins());
        else
          localIResourceObject.emitJS(localEmitter, false, new SkinDefinitionMap());
      }
      else
        localIResourceObject.emitJS(localEmitter, false, new SkinDefinitionMap());
    }
    else
      append("null;");
  }

  private SkinDefinitionMap populateSkins()
    throws GoatException
  {
    Form localForm = Form.get(this.mServer, this.mForm);
    Form.ViewInfo localViewInfo = localForm.getViewInfo(this.mVui);
    SkinService localSkinService = (SkinService)ServiceLocator.getInstance().getService("skinService");
    SkinDefinitionMap localSkinDefinitionMap = new SkinDefinitionMap();
    ServerLogin localServerLogin1 = SessionData.get().getServerLogin(localForm.getServer());
    SkinDefinitionList localSkinDefinitionList = localSkinService.getSkins(localServerLogin1, localViewInfo);
    if (localSkinDefinitionList.getSize() > 0)
    {
      HashSet localHashSet = new HashSet();
      localHashSet.add(this.mTemplate);
      ServerLogin localServerLogin2 = ServerLogin.getAdmin(localForm.getServer());
      localSkinDefinitionMap = localSkinService.getTemplatesSkinProperties(localServerLogin2, localViewInfo, localSkinDefinitionList, localHashSet);
    }
    return localSkinDefinitionMap;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetARTemplateAgent
 * JD-Core Version:    0.6.1
 */