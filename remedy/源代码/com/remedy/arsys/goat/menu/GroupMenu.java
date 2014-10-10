package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.Entry;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.goat.permissions.Role;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GroupMenu extends Menu
{
  private static final long serialVersionUID = -2188710692703386967L;
  public static final String AR_MULTI_ASSIGN_GROUP_MENU = "MAMENU";
  public static final String AR_GROUP_LIST_MENU = "GLMENU";
  public static final String AR_ROLE_STATE_MENU = "RSMENU";

  public GroupMenu(Menu.MKey paramMKey)
    throws GoatException
  {
    super(paramMKey, null);
  }

  protected void putCache(Menu.MKey paramMKey)
  {
    MCache.put(paramMKey.getCacheKeyWithLocale(), this);
  }

  private void emitGroupIds(JSWriter paramJSWriter)
    throws GoatException
  {
    Map localMap = Group.getAllInstances(this.mServer);
    if ((localMap == null) || (localMap.isEmpty()))
      return;
    String str = MessageTranslation.getLocalizedText(this.mLocale, "Groups", "Menu Label", null);
    if (str == null)
      str = "";
    if (this.mName.startsWith("MAMENU"))
      paramJSWriter.listSep().openObj().property("l", str).append(",v:").openList();
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Long localLong = (Long)localEntry.getKey();
      Group localGroup = (Group)localEntry.getValue();
      if ((!localGroup.isImplicit()) && ((!this.mName.startsWith("GLMENU")) || (!localGroup.isComputed())) && (localLong.intValue() == 0L ? this.mName.startsWith("MAMENU") : !localGroup.isDynamic()))
        paramJSWriter.listSep().appendqs(localLong.toString());
    }
    if (this.mName.startsWith("MAMENU"))
      paramJSWriter.closeList().closeObj();
  }

  private void emitRoleIds(JSWriter paramJSWriter)
    throws GoatException
  {
    if ((this.mAppName == null) || (this.mAppName.length() <= 0))
      return;
    Map localMap = Role.getAllInstances(this.mServer, this.mAppName);
    if ((localMap == null) || (localMap.isEmpty()))
      return;
    String str = MessageTranslation.getLocalizedText(this.mLocale, "Roles", "Menu Label", null);
    if (str == null)
      str = "";
    paramJSWriter.listSep().openObj().property("l", str).append(",v:").openList();
    Iterator localIterator = localMap.keySet().iterator();
    Long localLong = null;
    while (localIterator.hasNext())
    {
      paramJSWriter.listSep();
      localLong = (Long)localIterator.next();
      if (localLong != null)
        paramJSWriter.appendqs(localLong.toString());
    }
    paramJSWriter.closeList();
    paramJSWriter.property("t", 3);
    paramJSWriter.closeObj();
  }

  protected String getJS()
    throws GoatException
  {
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openList();
    emitGroupIds(localJSWriter);
    if (this.mName.startsWith("MAMENU"))
      emitRoleIds(localJSWriter);
    localJSWriter.closeList();
    return localJSWriter.toString();
  }

  public void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException
  {
  }

  private static void emitGroupIDNameMap(JSWriter paramJSWriter, Map paramMap, boolean paramBoolean)
  {
    ArrayList localArrayList = null;
    int i = 1;
    JSWriter localJSWriter = new JSWriter(true);
    paramJSWriter.startStatement(paramBoolean ? "this.gIdNameMap=" : "this.cIdNameMap=").openObj();
    localJSWriter.startStatement(paramBoolean ? "this.gNameIdMap=" : "this.cNameIdMap=").openObj();
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      Group localGroup = (Group)localEntry.getValue();
      Long localLong = (Long)localEntry.getKey();
      boolean bool = localGroup.isComputed();
      if (((!paramBoolean) || (!bool)) && ((paramBoolean) || (bool)))
      {
        localArrayList = localGroup.getGroupNameList();
        Iterator localIterator2 = localArrayList.iterator();
        if (i == 0)
        {
          paramJSWriter.comma();
          localJSWriter.comma();
        }
        else
        {
          i = 0;
        }
        paramJSWriter.append(JSWriter.escapeString(localLong.toString())).append(":").openList();
        int j = 1;
        while (localIterator2.hasNext())
        {
          String str = (String)localIterator2.next();
          paramJSWriter.listSep().append(JSWriter.escapeString(str));
          if (j == 0)
            localJSWriter.comma();
          else
            j = 0;
          localJSWriter.append(JSWriter.escapeString(str)).append(":").append(JSWriter.escapeString(localLong.toString()));
        }
        paramJSWriter.closeList();
      }
    }
    paramJSWriter.closeObj().endStatement();
    localJSWriter.closeObj().endStatement();
    paramJSWriter.append(localJSWriter);
  }

  private static void emitGroupIDNameMap(JSWriter paramJSWriter, String paramString)
  {
    Map localMap = null;
    try
    {
      localMap = Group.getAllInstances(paramString);
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
    }
    if ((localMap == null) || (localMap.isEmpty()))
    {
      paramJSWriter.append("this.gIdNameMap={};").append("this.cIdNameMap={};");
      return;
    }
    emitGroupIDNameMap(paramJSWriter, localMap, true);
    emitGroupIDNameMap(paramJSWriter, localMap, false);
  }

  private static void emitRoleIDNameMap(JSWriter paramJSWriter, Map paramMap)
  {
    paramJSWriter.startStatement("this.rIdNameMap=").openObj();
    if (paramMap != null)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Long localLong = (Long)localEntry.getKey();
        Role localRole = (Role)localEntry.getValue();
        paramJSWriter.property(JSWriter.escapeString(localLong.toString()), localRole.toString());
      }
    }
    paramJSWriter.closeObj().endStatement();
  }

  private static void emitRoleIDNameMap(JSWriter paramJSWriter, String paramString1, String paramString2)
  {
    Map localMap = null;
    if ((paramString2 != null) && (paramString2.length() > 0))
      try
      {
        localMap = Role.getAllInstances(paramString1, paramString2);
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
    emitRoleIDNameMap(paramJSWriter, localMap);
  }

  private static void emitMenuConstructor(JSWriter paramJSWriter)
  {
    paramJSWriter.append("this.getLabel=").startFunction("", "id");
    paramJSWriter.append("var v=this.gIdNameMap[id];").eol();
    paramJSWriter.append("if(!v){v=this.cIdNameMap[id]; if(!v) v=this.rIdNameMap[id];}").eol();
    paramJSWriter.append("return v;").eol();
    paramJSWriter.endFunction();
    paramJSWriter.append("this.cMenu=").startFunction("", "obj");
    paramJSWriter.append("var res={},n,k=0;").eol();
    paramJSWriter.append("var emptyMenu=false;if (obj.length <= 0)emptyMenu = true;").eol();
    paramJSWriter.append("res.novals = emptyMenu;").eol();
    paramJSWriter.append("res.mval = [];").eol();
    paramJSWriter.append("if (emptyMenu) {").eol();
    paramJSWriter.append("res.mval[0] = {l:");
    paramJSWriter.append(JSWriter.escapeString(MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "(no entries in menu)")));
    paramJSWriter.append(", v: null};").eol();
    paramJSWriter.append("} else {").eol();
    paramJSWriter.append("for(var i in obj){").eol();
    paramJSWriter.append("if (typeof obj[i]==\"string\"){n=this.getLabel(obj[i]);");
    paramJSWriter.append("if(n){if(n instanceof Object)for(var x in n)res.mval[k++]={l:n[x], v:null};else");
    paramJSWriter.append(" res.mval[k++]={l:n, v:null};").eol();
    paramJSWriter.append("}}else{var mo={},val=obj[i].v;k=0,mo.l=obj[i].l;mo.v=[];").eol();
    paramJSWriter.append("for(var j in val){n=this.getLabel(val[j]);");
    paramJSWriter.append("if(n){if(n instanceof Object)for(var x in n)mo.v[k++]={l:n[x], v:null};else");
    paramJSWriter.append(" mo.v[k++]={l:n, v:null};}}").eol();
    paramJSWriter.append("res.mval[i]=mo;}}").eol();
    paramJSWriter.append("}").eol();
    paramJSWriter.append("return res;").eol();
    paramJSWriter.endFunction();
  }

  private static void emitARMenuFunctionCall(JSWriter paramJSWriter, String paramString1, Set paramSet, String paramString2)
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramJSWriter.startStatement("ARMenus[").appendqs(str).append("]=");
      paramJSWriter.append("this.cMenu(");
      try
      {
        Menu localMenu = Menu.get(new Menu.MKey(paramString1, SessionData.get().getLocale(), str, paramString2));
        if (localMenu != null)
          paramJSWriter.append(localMenu.mJSStr);
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
      paramJSWriter.append(")").endStatement();
    }
  }

  public static void emitGroupInfo(JSWriter paramJSWriter, String paramString1, Set paramSet, String paramString2)
  {
    assert (paramSet != null);
    paramJSWriter.append("this.GInfo=new function()").startBlock();
    SessionData.get().setGroupModified(Group.isGroupModified(paramString1));
    emitGroupIDNameMap(paramJSWriter, paramString1);
    emitRoleIDNameMap(paramJSWriter, paramString1, paramString2);
    emitMenuConstructor(paramJSWriter);
    if (paramSet.size() > 0)
      emitARMenuFunctionCall(paramJSWriter, paramString1, paramSet, paramString2);
    paramJSWriter.endBlock().append(";").eol();
  }

  public static boolean isGroupMenu(String paramString)
  {
    return (paramString.startsWith("GLMENU")) || (paramString.startsWith("RSMENU")) || (paramString.startsWith("MAMENU"));
  }

  public static String getMenuName(long paramLong)
  {
    return Group.isMultiAssignField(paramLong) ? "MAMENU" : Group.isRoleStateMapping(paramLong) ? "RSMENU" : Group.isGroupList(paramLong) ? "GLMENU" : "";
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.GroupMenu
 * JD-Core Version:    0.6.1
 */