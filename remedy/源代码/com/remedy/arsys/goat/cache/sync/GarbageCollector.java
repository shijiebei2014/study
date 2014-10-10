package com.remedy.arsys.goat.cache.sync;

import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.ContainerOwner;
import com.remedy.arsys.backchannel.TableEntryListBase.SchemaList;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatContainer;
import com.remedy.arsys.goat.GoatContainer.ContainerList;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Guide;
import com.remedy.arsys.goat.Guide.Key;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ActiveLinkCache;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.Cache.SetItem;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.share.MiscCache;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GarbageCollector
{
  protected static Log cacheLog = Log.get(1);
  private String mServer;
  private GoatCacheManager mcacheMgr;
  private final Cache activeLinkCache;
  private final Cache fieldGraphCache;
  private final Cache htmlDataCache;
  private final Cache jsDataCache;
  private final Cache relationshipCache;
  private final Cache guideCache;
  private final Cache sysDataCache;
  private final Cache formCache;
  private final Cache fieldMapCache;
  private final Cache formFieldCache;
  private final Cache goatFieldMapCache;
  private final Cache menuCache;
  private final Cache containerCache;
  private final Cache containerListCache;
  private final Cache groupCache;
  private final Cache roleCache;
  private final Cache imageCache;
  private final Cache schemaListCache;
  private final Set<String> activeLinksToRemoveSet = new LinkedHashSet();
  private final transient Set<String> formsVisitedSet = new LinkedHashSet();
  private final Set<String> guidesToRemoveSet = new LinkedHashSet();
  private final Set<String> sysDataToRemoveSet = new LinkedHashSet();
  private final Set<String> formsToRemoveSet = new LinkedHashSet();
  private final Set<String> fieldGraphsToRemoveSet = new LinkedHashSet();
  private final Set<String> htmlDataToRemoveSet = new LinkedHashSet();
  private final Set<String> JSDataToRemoveSet = new LinkedHashSet();
  private final Set<String> relationshipToRemoveSet = new LinkedHashSet();
  private final Set<String> fieldMapToRemoveSet = new LinkedHashSet();
  private final Set<String> formFieldToRemoveSet = new LinkedHashSet();
  private final Set<String> goatFieldMapToRemoveSet = new LinkedHashSet();
  private final Set<String> menusToRemoveSet = new LinkedHashSet();
  private final Set<String> containersToRemoveSet = new LinkedHashSet();
  private final Set<String> containerListsToRemoveSet = new LinkedHashSet();
  private final Set<String> groupsToRemoveSet = new LinkedHashSet();
  private final Set<String> rolesToRemoveSet = new LinkedHashSet();
  private final Set<String> imagesToRemoveSet = new LinkedHashSet();
  private final Set<String> schemaListToRemoveSet = new LinkedHashSet();
  private final Map<String, Set<String>> formViewsRemovedMap = new HashMap();

  public GarbageCollector(String paramString, GoatCacheManager paramGoatCacheManager)
  {
    setServer(paramString);
    setCacheMgr(paramGoatCacheManager);
    this.activeLinkCache = this.mcacheMgr.getGoatCache("Active links");
    this.fieldGraphCache = this.mcacheMgr.getGoatCache("Compiled forms");
    this.htmlDataCache = this.mcacheMgr.getGoatCache("Html");
    this.jsDataCache = this.mcacheMgr.getGoatCache("Js");
    this.relationshipCache = this.mcacheMgr.getGoatCache("Sync relationships");
    this.guideCache = Guide.getCache();
    this.sysDataCache = this.mcacheMgr.getGoatCache("Sysdata");
    this.formCache = this.mcacheMgr.getGoatCache("Forms");
    this.fieldMapCache = this.mcacheMgr.getGoatCache("Field maps");
    this.formFieldCache = this.mcacheMgr.getGoatCache("Form fields");
    this.goatFieldMapCache = this.mcacheMgr.getGoatCache("Displayed fields");
    this.menuCache = Menu.getCache();
    this.containerCache = GoatContainer.getCache();
    this.containerListCache = GoatContainer.ContainerList.getCache();
    this.groupCache = this.mcacheMgr.getGoatCache("Groups");
    this.roleCache = this.mcacheMgr.getGoatCache("Roles");
    this.imageCache = this.mcacheMgr.getGoatCache("Form images");
    this.schemaListCache = TableEntryListBase.SchemaList.getCache();
  }

  public void reset()
  {
    cacheLog.fine("Reset garbage collector marks");
    this.activeLinksToRemoveSet.clear();
    this.guidesToRemoveSet.clear();
    this.sysDataToRemoveSet.clear();
    this.formsToRemoveSet.clear();
    this.formsVisitedSet.clear();
    this.fieldGraphsToRemoveSet.clear();
    this.htmlDataToRemoveSet.clear();
    this.JSDataToRemoveSet.clear();
    this.relationshipToRemoveSet.clear();
    this.fieldMapToRemoveSet.clear();
    this.formFieldToRemoveSet.clear();
    this.goatFieldMapToRemoveSet.clear();
    this.menusToRemoveSet.clear();
    this.containersToRemoveSet.clear();
    this.containerListsToRemoveSet.clear();
    this.groupsToRemoveSet.clear();
    this.rolesToRemoveSet.clear();
    this.imagesToRemoveSet.clear();
    this.formViewsRemovedMap.clear();
    this.schemaListToRemoveSet.clear();
  }

  public Map<String, Set<String>> getFormViewsToRemoveMap()
  {
    return this.formViewsRemovedMap;
  }

  public void devMarkActiveLink(com.remedy.arsys.goat.ActiveLink paramActiveLink, String paramString)
    throws GoatException
  {
    String str1 = paramActiveLink.getName();
    cacheLog.fine("Marking cached ActiveLink: " + str1);
    String str2 = com.remedy.arsys.goat.ActiveLink.getCacheKey(this.mServer, str1);
    this.activeLinksToRemoveSet.add(str2);
    markFormRecursive(paramString, false);
  }

  public void devMarkActiveLinks(String paramString)
    throws GoatException
  {
    Object localObject = this.activeLinkCache != null ? this.activeLinkCache.getAll(this.mServer, com.remedy.arsys.goat.ActiveLink.class) : null;
    if (localObject != null)
    {
      Iterator localIterator = localObject.iterator();
      while (localIterator.hasNext())
      {
        Cache.Item localItem = (Cache.Item)localIterator.next();
        com.remedy.arsys.goat.ActiveLink localActiveLink = (com.remedy.arsys.goat.ActiveLink)localItem;
        List localList = localActiveLink.getFormList();
        if (localList.contains(paramString))
        {
          String str1 = localActiveLink.getName();
          cacheLog.fine("Marking cached ActiveLink: " + str1);
          String str2 = com.remedy.arsys.goat.ActiveLink.getCacheKey(this.mServer, str1);
          this.activeLinksToRemoveSet.add(str2);
        }
      }
    }
    markFormRecursive(paramString, false);
  }

  public void markActiveLinkRecursive(com.remedy.arsys.goat.ActiveLink paramActiveLink)
  {
    String str1 = paramActiveLink.getName();
    cacheLog.fine("Marking cached ActiveLink: " + str1);
    String str2 = com.remedy.arsys.goat.ActiveLink.getCacheKey(this.mServer, str1);
    this.activeLinksToRemoveSet.add(str2);
    List localList = paramActiveLink.getFormList();
    if (localList != null)
      markFormsRecursive(localList);
    else
      cacheLog.warning("No form name list exists for cached ActiveLink: " + str1);
  }

  public void markActiveLinkRecursive(com.bmc.arsys.api.ActiveLink paramActiveLink)
  {
    cacheLog.fine("Marking ActiveLink: " + paramActiveLink.getName());
    List localList = paramActiveLink.getFormList();
    if (localList != null)
      markFormsRecursive(localList);
    else
      cacheLog.fine("No form name list exists for ActiveLink: " + paramActiveLink.getName());
  }

  public void devMarkMenus(List<String> paramList, String paramString)
    throws GoatException
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      cacheLog.fine("Marking cached Menu: " + str);
      Menu.MKey localMKey = new Menu.MKey(this.mServer, SessionData.get().getLocale(), str, com.remedy.arsys.goat.Form.get(this.mServer, paramString).getAppName());
      this.menusToRemoveSet.add(localMKey.getCacheKeyWithLocale().intern());
      this.menusToRemoveSet.add(localMKey.getCacheKey().intern());
    }
    markFormRecursive(paramString, false);
  }

  public void devMarkMenus(String paramString)
    throws GoatException
  {
    Set localSet = this.menuCache.getAll(this.mServer, Menu.class);
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Cache.Item localItem = (Cache.Item)localIterator.next();
      if ((localItem instanceof Menu))
      {
        Menu localMenu = (Menu)localItem;
        String str = localMenu.getMenuName();
        cacheLog.fine("Marking cached Menu: " + str);
        Menu.MKey localMKey = new Menu.MKey(this.mServer, SessionData.get().getLocale(), str, com.remedy.arsys.goat.Form.get(this.mServer, paramString).getAppName());
        this.menusToRemoveSet.add(localMKey.getCacheKeyWithLocale().intern());
      }
    }
    markFormRecursive(paramString, false);
  }

  public void devMarkGuides(List<String> paramList, String paramString)
    throws GoatException
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      cacheLog.fine("Marking cached Guide: " + str1);
      Guide.Key localKey = new Guide.Key(ServerLogin.getAdmin(this.mServer), str1);
      String str2 = localKey.getCacheKey();
      this.guidesToRemoveSet.add(str2);
    }
    markFormRecursive(paramString, false);
  }

  public void devMarkGuides(String paramString)
    throws GoatException
  {
    Set localSet = this.guideCache.getAll(this.mServer, Guide.class);
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Cache.Item localItem = (Cache.Item)localIterator.next();
      Guide localGuide = (Guide)localItem;
      String str1 = localGuide.getGuideName();
      cacheLog.fine("Marking cached Guide: " + str1);
      Guide.Key localKey = new Guide.Key(ServerLogin.getAdmin(this.mServer), str1);
      String str2 = localKey.getCacheKey();
      this.guidesToRemoveSet.add(str2);
    }
    markFormRecursive(paramString, false);
  }

  public void markGuideRecursive(Guide paramGuide)
    throws GoatException
  {
    String str1 = paramGuide.getGuideName();
    cacheLog.fine("Marking cached Guide: " + str1);
    Guide.Key localKey = new Guide.Key(ServerLogin.getAdmin(this.mServer), str1);
    String str2 = localKey.getCacheKey();
    this.guidesToRemoveSet.add(str2);
    List localList = paramGuide.getFormList();
    if (localList != null)
      markFormsRecursive(localList);
    else
      cacheLog.warning("No form name list exists for cached Guide: " + str1);
  }

  public void markGuideRecursive(Container paramContainer)
  {
    cacheLog.fine("Marking Guide: " + paramContainer.getName());
    List localList = paramContainer.getContainerOwner();
    if (localList != null)
    {
      ArrayList localArrayList = new ArrayList(localList.size());
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ContainerOwner localContainerOwner = (ContainerOwner)localIterator.next();
        localArrayList.add(localContainerOwner.getName());
      }
      markFormsRecursive(localArrayList);
    }
    else
    {
      cacheLog.fine("No form name list exists for ActiveLink: " + paramContainer.getName());
    }
  }

  public void markContainerRecursive(GoatContainer paramGoatContainer)
    throws GoatException
  {
    String str1 = paramGoatContainer.getContainer().getName();
    cacheLog.fine("Marking cached Container: " + str1);
    String str2 = GoatContainer.getKey(this.mServer, str1);
    this.containersToRemoveSet.add(str2);
  }

  public void markAllContainerLists()
    throws GoatException
  {
    List localList = this.containerListCache.getAllKeys(this.mServer, List.class);
    this.containersToRemoveSet.addAll(localList);
  }

  public void markAllSchemaLists()
    throws GoatException
  {
    List localList = this.schemaListCache.getAllKeys(this.mServer, List.class);
    this.schemaListToRemoveSet.addAll(localList);
  }

  public void markFormRecursive(String paramString, boolean paramBoolean)
    throws GoatException
  {
    if (paramBoolean)
    {
      cacheLog.fine("Marking cached Form: " + paramString);
      localObject = com.remedy.arsys.goat.Form.getCacheKey(this.mServer, paramString);
      this.formsToRemoveSet.add(localObject);
    }
    Object localObject = new ArrayList(1);
    ((List)localObject).add(paramString);
    markFormsRecursive((List)localObject);
  }

  public void markFormRecursive(com.bmc.arsys.api.Form paramForm)
  {
    cacheLog.fine("Marking Form: " + paramForm.getName());
    String str = paramForm.getName();
    ArrayList localArrayList = new ArrayList(1);
    localArrayList.add(str);
    markFormsRecursive(localArrayList);
  }

  private void markFormsRecursive(List<String> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (!isFormVisited(str1))
      {
        cacheLog.fine("Mark related FieldGraphs, HTML/JS Globules for removal for form: " + str1);
        String str2 = FieldGraph.getServerFormKey(this.mServer, str1);
        if (this.relationshipCache != null)
        {
          Cache.SetItem localSetItem = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_html(str2), Cache.SetItem.class);
          Object localObject3;
          if (localSetItem != null)
          {
            localObject1 = localSetItem.getSet();
            localObject2 = ((Set)localObject1).iterator();
            while (((Iterator)localObject2).hasNext())
            {
              localObject3 = (String)((Iterator)localObject2).next();
              markHTMLData((String)localObject3);
            }
          }
          else
          {
            cacheLog.info("No Form HTML Data keys exist for server/form key: " + str2);
          }
          Object localObject1 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_js(str2), Cache.SetItem.class);
          Object localObject4;
          if (localObject1 != null)
          {
            localObject2 = ((Cache.SetItem)localObject1).getSet();
            localObject3 = ((Set)localObject2).iterator();
            while (((Iterator)localObject3).hasNext())
            {
              localObject4 = (String)((Iterator)localObject3).next();
              markJSData((String)localObject4);
            }
          }
          else
          {
            cacheLog.info("No Form HTML/JS Data keys exist for server/form key: " + str2);
          }
          markRelationshipKey(str2);
          Object localObject2 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_fieldgraph(str2), Cache.SetItem.class);
          if (localObject2 != null)
          {
            localObject3 = ((Cache.SetItem)localObject2).getSet();
            localObject4 = ((Set)localObject3).iterator();
            while (((Iterator)localObject4).hasNext())
            {
              String str3 = (String)((Iterator)localObject4).next();
              markFieldGraph(str3);
            }
          }
          else
          {
            cacheLog.info("No Form FieldGraph keys exist for server/form key: " + str2);
          }
        }
        markFormsVisited(str1);
        cacheLog.fine("End of Marking for form: " + str1);
      }
    }
  }

  public void markFieldsRecursive(String paramString, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramArrayOfInt.length; i++)
      markFieldRecursive(paramString, paramArrayOfInt[i]);
  }

  public void markFieldRecursive(String paramString, int paramInt)
  {
    String str1 = com.remedy.arsys.goat.Form.getFormFieldCacheKey(this.mServer, paramString, paramInt);
    String str2 = FieldGraph.getServerFormKey(this.mServer, paramString);
    cacheLog.fine("Mark related FieldMap, FormField and GoatFieldMap for removal for form: " + paramString + ", fieldId: " + paramInt);
    Cache.SetItem localSetItem = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_fieldMap(str2), Cache.SetItem.class);
    if (localSetItem != null)
    {
      localObject1 = localSetItem.getSet();
      localObject2 = ((Set)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (String)((Iterator)localObject2).next();
        markFieldMap((String)localObject3);
      }
    }
    else
    {
      cacheLog.info("No fieldMap keys exist for server/form key: " + str2);
    }
    Object localObject1 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_formField(str1), Cache.SetItem.class);
    if (localObject1 != null)
    {
      localObject2 = ((Cache.SetItem)localObject1).getSet();
      localObject3 = ((Set)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (String)((Iterator)localObject3).next();
        markFormField((String)localObject4);
      }
    }
    else
    {
      cacheLog.info("No formField keys exist for server/form key: " + str1);
    }
    Object localObject2 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_goatFieldMap(str2), Cache.SetItem.class);
    if (localObject2 != null)
    {
      localObject3 = ((Cache.SetItem)localObject2).getSet();
      localObject4 = ((Set)localObject3).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (String)((Iterator)localObject4).next();
        markGoatFieldMap((String)localObject5);
      }
    }
    else
    {
      cacheLog.info("No goatFieldMap keys exist for server/form key: " + str2);
    }
    if (isFormVisited(paramString))
      return;
    cacheLog.fine("Mark related FieldGraphs, HTML/JS Globules for removal for form: " + paramString);
    Object localObject3 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_html(str2), Cache.SetItem.class);
    Object localObject6;
    if (localObject3 != null)
    {
      localObject4 = ((Cache.SetItem)localObject3).getSet();
      localObject5 = ((Set)localObject4).iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject6 = (String)((Iterator)localObject5).next();
        markHTMLData((String)localObject6);
      }
    }
    else
    {
      cacheLog.info("No Form HTML Data keys exist for server/form key: " + str2);
    }
    Object localObject4 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_js(str2), Cache.SetItem.class);
    Object localObject7;
    if (localObject4 != null)
    {
      localObject5 = ((Cache.SetItem)localObject4).getSet();
      localObject6 = ((Set)localObject5).iterator();
      while (((Iterator)localObject6).hasNext())
      {
        localObject7 = (String)((Iterator)localObject6).next();
        markJSData((String)localObject7);
      }
    }
    else
    {
      cacheLog.info("No Form HTML/JS Data keys exist for server/form key: " + str2);
    }
    markRelationshipKey(str2);
    Object localObject5 = (Cache.SetItem)this.relationshipCache.get(relationshipManager.getInstance().getRelationshipCachekey_form_fieldgraph(str2), Cache.SetItem.class);
    if (localObject5 != null)
    {
      localObject6 = ((Cache.SetItem)localObject5).getSet();
      localObject7 = ((Set)localObject6).iterator();
      while (((Iterator)localObject7).hasNext())
      {
        String str3 = (String)((Iterator)localObject7).next();
        markFieldGraph(str3);
      }
    }
    else
    {
      cacheLog.info("No Form FieldGraph keys exist for server/form key: " + str2);
    }
    markFormsVisited(paramString);
    cacheLog.fine("End of Marking for Field: " + paramString + ", fieldId: " + paramInt);
  }

  public void markCharMenuRecursive(String paramString, Menu.MKey paramMKey, List<String> paramList)
  {
    cacheLog.fine("Mark related Menu/Form for removal: " + paramString);
    markCharMenu(paramMKey);
    markFormsRecursive(paramList);
  }

  public void markAllGroupsRecursive(List<String> paramList)
    throws GoatException
  {
    Collection localCollection = this.groupCache != null ? this.groupCache.getAllKeys(this.mServer, List.class) : null;
    if (localCollection != null)
      this.groupsToRemoveSet.addAll(localCollection);
    Object localObject = this.groupCache != null ? this.groupCache.getAll(this.mServer, null).iterator() : null;
    if (localObject != null)
      while (localObject.hasNext())
      {
        localItem = (Cache.Item)localObject.next();
        if (!this.groupsToRemoveSet.contains(localItem.getServer()))
          this.groupsToRemoveSet.add(localItem.getServer());
      }
    Cache.Item localItem = this.sysDataCache != null ? this.sysDataCache.getAllKeys(this.mServer, List.class) : null;
    if (localItem != null)
    {
      Iterator localIterator = localItem.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.indexOf("GLMENU") != -1)
          this.sysDataToRemoveSet.add(str);
      }
    }
    if (paramList != null)
      markFormsRecursive(paramList);
  }

  public void markAllRolesRecursive()
    throws GoatException
  {
    Collection localCollection = this.roleCache != null ? this.roleCache.getAllKeys(this.mServer, List.class) : null;
    if (localCollection != null)
      this.rolesToRemoveSet.addAll(localCollection);
  }

  public void markAllImagesRecursive()
    throws GoatException
  {
    Collection localCollection = this.imageCache != null ? this.imageCache.getAllKeys(null, List.class) : null;
    if (localCollection != null)
      this.imagesToRemoveSet.addAll(localCollection);
  }

  private void markFormsVisited(List<String> paramList)
  {
    this.formsVisitedSet.addAll(paramList);
  }

  private void markFormsVisited(String paramString)
  {
    this.formsVisitedSet.add(paramString);
  }

  private boolean isFormVisited(String paramString)
  {
    return this.formsVisitedSet.contains(paramString);
  }

  public void markRelationshipKey(String paramString)
  {
    cacheLog.fine("Marked Form FieldGraph/HTMLJSData key: " + paramString);
    this.relationshipToRemoveSet.add(paramString);
  }

  public void markFieldGraph(String paramString)
  {
    cacheLog.fine("Marked FieldGraph: " + paramString);
    this.fieldGraphsToRemoveSet.add(paramString);
  }

  public void markHTMLData(String paramString)
  {
    cacheLog.fine("Marked HTMLJSData: " + paramString);
    this.htmlDataToRemoveSet.add(paramString);
  }

  public void markJSData(String paramString)
  {
    cacheLog.fine("Marked JSData: " + paramString);
    this.JSDataToRemoveSet.add(paramString);
  }

  public void markFieldMap(String paramString)
  {
    cacheLog.fine("Marked FieldMap: " + paramString);
    this.fieldMapToRemoveSet.add(paramString);
  }

  public void markFormField(String paramString)
  {
    cacheLog.fine("Marked FormField: " + paramString);
    this.formFieldToRemoveSet.add(paramString);
  }

  public void markGoatFieldMap(String paramString)
  {
    cacheLog.fine("Marked GoatFieldMap: " + paramString);
    this.goatFieldMapToRemoveSet.add(paramString);
  }

  public void markCharMenu(Menu.MKey paramMKey)
  {
    if (paramMKey == null)
      return;
    cacheLog.fine("Marked Menu: " + paramMKey.getCacheKeyWithLocale().intern());
    this.menusToRemoveSet.add(paramMKey.getCacheKeyWithLocale().intern());
  }

  public void sweep()
  {
    cacheLog.fine("Sweeping");
    Object localObject2;
    if (this.htmlDataCache != null)
    {
      localObject1 = this.htmlDataToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing HTMLData: " + (String)localObject2);
        this.htmlDataCache.remove((String)localObject2);
      }
    }
    if (this.jsDataCache != null)
    {
      localObject1 = this.JSDataToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing JSData: " + (String)localObject2);
        this.jsDataCache.remove((String)localObject2);
      }
    }
    Object localObject3;
    if (this.fieldGraphCache != null)
    {
      localObject1 = this.fieldGraphsToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing FieldGraph: " + (String)localObject2);
        localObject3 = (FieldGraph)this.fieldGraphCache.get((String)localObject2, FieldGraph.class);
        try
        {
          if (localObject3 != null)
          {
            com.remedy.arsys.goat.Form localForm = ((FieldGraph)localObject3).getForm();
            Form.ViewInfo localViewInfo = ((FieldGraph)localObject3).getViewInfo();
            if (localForm == null)
            {
              cacheLog.warning("Can't resolve form for invalidated fieldgraph: " + (String)localObject2 + "; skipping refresh");
              continue;
            }
            if (localViewInfo == null)
            {
              cacheLog.warning("Can't resolve view for invalidated fieldgraph: " + (String)localObject2 + "; skipping refresh");
              continue;
            }
            String str1 = localForm.getName();
            String str2 = localViewInfo.getName();
            Object localObject4 = (Set)this.formViewsRemovedMap.get(str1);
            if (localObject4 == null)
            {
              localObject4 = new LinkedHashSet();
              this.formViewsRemovedMap.put(str1, localObject4);
            }
            ((Set)localObject4).add(str2);
          }
        }
        finally
        {
          this.fieldGraphCache.remove((String)localObject2);
        }
      }
    }
    Object localObject1 = this.fieldMapToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing FieldMap: " + (String)localObject2);
      if (this.fieldMapCache != null)
        this.fieldMapCache.remove((String)localObject2);
      relationshipManager.getInstance().removeRelationshipCachekey_form_fieldMap((String)localObject2);
    }
    localObject1 = this.formFieldToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing FormField: " + (String)localObject2);
      if (this.formFieldCache != null)
        this.formFieldCache.remove((String)localObject2);
      relationshipManager.getInstance().removeRelationshipCachekey_form_formField((String)localObject2);
    }
    localObject1 = this.goatFieldMapToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing GoatFieldMap: " + (String)localObject2);
      if (this.goatFieldMapCache != null)
        this.goatFieldMapCache.remove((String)localObject2);
      relationshipManager.getInstance().removeRelationshipCachekey_form_goatFieldMap((String)localObject2);
    }
    localObject1 = this.relationshipToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing FieldGraph/HTMLJSData key: " + (String)localObject2);
      relationshipManager.getInstance().removeRelationshipCachekey_form_all((String)localObject2);
    }
    if (this.activeLinkCache != null)
    {
      localObject1 = this.activeLinksToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing ActiveLink: " + (String)localObject2);
        this.activeLinkCache.remove((String)localObject2);
      }
    }
    localObject1 = this.guidesToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing Guide: " + (String)localObject2);
      this.guideCache.remove((String)localObject2);
    }
    if (this.sysDataCache != null)
    {
      localObject1 = new MiscCache("Menus");
      localObject2 = this.sysDataToRemoveSet.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (String)((Iterator)localObject2).next();
        cacheLog.fine("Removing Misc cache: " + (String)localObject3);
        this.sysDataCache.remove((String)localObject3);
        if (((String)localObject3).startsWith("Menus"))
        {
          localObject3 = ((String)localObject3).substring("Menus".length());
          ((Cache)localObject1).remove((String)localObject3);
        }
      }
    }
    if (this.formCache != null)
    {
      localObject1 = this.formsToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing Form: " + (String)localObject2);
        this.formCache.remove((String)localObject2);
      }
    }
    localObject1 = this.menusToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing Menu: " + (String)localObject2);
      this.menuCache.remove((String)localObject2);
    }
    localObject1 = this.containersToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing Container: " + (String)localObject2);
      this.containerCache.remove((String)localObject2);
    }
    localObject1 = this.containerListsToRemoveSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      cacheLog.fine("Removing ContainerList: " + (String)localObject2);
      this.containerListCache.remove((String)localObject2);
    }
    if (this.groupCache != null)
    {
      localObject1 = this.groupsToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing Group: " + (String)localObject2);
        this.groupCache.remove((String)localObject2);
      }
    }
    if (this.roleCache != null)
    {
      localObject1 = this.rolesToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing Role: " + (String)localObject2);
        this.roleCache.remove((String)localObject2);
      }
    }
    if (this.imageCache != null)
    {
      localObject1 = this.imagesToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        cacheLog.fine("Removing Image: " + (String)localObject2);
        this.imageCache.remove((String)localObject2);
      }
    }
    if (this.schemaListCache != null)
    {
      localObject1 = this.schemaListToRemoveSet.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        if (((String)localObject2).startsWith("SchemaList"))
        {
          localObject2 = ((String)localObject2).substring("SchemaList".length());
          this.schemaListCache.remove((String)localObject2);
        }
      }
    }
    cacheLog.fine("Committing caches");
    if (this.jsDataCache != null)
      this.jsDataCache.save();
    if (this.htmlDataCache != null)
      this.htmlDataCache.save();
    if (this.fieldGraphCache != null)
      this.fieldGraphCache.save();
    if (this.relationshipCache != null)
      this.relationshipCache.save();
    if ((this.activeLinkCache != null) && ((this.activeLinkCache instanceof ActiveLinkCache)))
    {
      localObject1 = (ActiveLinkCache)this.activeLinkCache;
      synchronized (localObject1)
      {
        ((ActiveLinkCache)localObject1).forceSave();
      }
    }
    this.guideCache.save();
    if (this.formCache != null)
      this.formCache.save();
    if (this.fieldMapCache != null)
      this.fieldMapCache.save();
    if (this.formFieldCache != null)
      this.formFieldCache.save();
    if (this.goatFieldMapCache != null)
      this.goatFieldMapCache.save();
    this.menuCache.save();
    this.containerCache.save();
    this.containerListCache.save();
    if (this.groupCache != null)
      this.groupCache.save();
    if (this.roleCache != null)
      this.roleCache.save();
    if (this.sysDataCache != null)
      this.sysDataCache.save();
    if (this.imageCache != null)
      this.imageCache.save();
    if (this.schemaListCache != null)
      this.schemaListCache.save();
    cacheLog.fine("Done sweeping for server: " + this.mServer);
  }

  public void setServer(String paramString)
  {
    this.mServer = paramString;
  }

  public void setCacheMgr(GoatCacheManager paramGoatCacheManager)
  {
    this.mcacheMgr = paramGoatCacheManager;
  }

  private static void debug(Collection<?> paramCollection)
  {
    int i = 1;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (i != 0)
        i = 0;
      else
        cacheLog.fine(", ");
      cacheLog.fine(localObject.toString());
    }
    cacheLog.fine("End of the list.");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.cache.sync.GarbageCollector
 * JD-Core Version:    0.6.1
 */