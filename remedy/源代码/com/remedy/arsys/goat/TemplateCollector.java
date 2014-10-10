package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.service.SkinService;
import com.remedy.arsys.goat.sharedresource.ARSharedResource;
import com.remedy.arsys.goat.sharedresource.IResourceObject;
import com.remedy.arsys.goat.skins.SkinDefinitionList;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TemplateCollector
{
  private Set<String> mTemplateNames;
  private final List<ARSharedResource> mTemplateList = new ArrayList();
  public static final String ACTIVE_LINK_INIT = "Activelink_Init";
  private SkinDefinitionMap mSkinDefMap;
  private static Log MLog = Log.get(10);

  public TemplateCollector(Form paramForm, Form.ViewInfo paramViewInfo, Set<String> paramSet)
    throws GoatException
  {
    try
    {
      this.mTemplateNames = paramSet;
      String str = paramForm.getSchemaKey();
      try
      {
        populateTemplates(paramForm.getServer(), str);
        if ((this.mTemplateNames.size() > 0) && (Configuration.getInstance().getBooleanProperty("arsystem.enableSkins", false)))
          populateSkins(paramForm, paramViewInfo);
      }
      catch (ARException localARException)
      {
        localARException.printStackTrace();
        throw new GoatException(localARException);
      }
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
      throw localGoatException;
    }
  }

  private void populateTemplates(String paramString1, String paramString2)
    throws GoatException, ARException
  {
    Collection localCollection = ARSharedResource.get(paramString1, this.mTemplateNames, 0, null, null);
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      ARSharedResource localARSharedResource = (ARSharedResource)localIterator.next();
      String str = localARSharedResource.getName();
      if (this.mTemplateNames.contains(str))
        this.mTemplateList.add(localARSharedResource);
    }
  }

  private void populateSkins(Form paramForm, Form.ViewInfo paramViewInfo)
    throws GoatException, ARException
  {
    SkinService localSkinService = (SkinService)ServiceLocator.getInstance().getService("skinService");
    ServerLogin localServerLogin1 = SessionData.get().getServerLogin(paramForm.getServer());
    SkinDefinitionList localSkinDefinitionList = localSkinService.getSkins(localServerLogin1, paramViewInfo);
    if (localSkinDefinitionList.getSize() > 0)
    {
      ServerLogin localServerLogin2 = ServerLogin.getAdmin(paramForm.getServer());
      this.mSkinDefMap = localSkinService.getTemplatesSkinProperties(localServerLogin2, paramViewInfo, localSkinDefinitionList, this.mTemplateNames);
    }
  }

  public void emitTemplateCollection(JSWriter paramJSWriter, OutputNotes paramOutputNotes, IEmitterFactory paramIEmitterFactory, FieldGraph paramFieldGraph)
    throws GoatException
  {
    Emitter localEmitter = new Emitter(paramJSWriter, paramOutputNotes, paramIEmitterFactory);
    emitTemplateCollection(localEmitter, paramFieldGraph);
  }

  public void emitTemplateCollection(Emitter paramEmitter, FieldGraph paramFieldGraph)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.startThisFunction("Activelink_Init", "windowID");
    if (this.mTemplateList != null)
      for (int i = 0; i < this.mTemplateList.size(); i++)
      {
        ARSharedResource localARSharedResource = (ARSharedResource)this.mTemplateList.get(i);
        if (localARSharedResource != null)
        {
          IResourceObject localIResourceObject = localARSharedResource.getResourceObject();
          if (localIResourceObject != null)
            localIResourceObject.emitJS(paramEmitter, true, this.mSkinDefMap);
        }
      }
    localJSWriter.endFunction();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.TemplateCollector
 * JD-Core Version:    0.6.1
 */