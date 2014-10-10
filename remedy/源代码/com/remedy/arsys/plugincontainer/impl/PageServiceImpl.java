package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.plugincontainer.PageService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

class PageServiceImpl
  implements PageService
{
  private final String mPluginName;
  private String mFieldid;
  private final String mServer;
  private String mAppName;
  private String mWindowID;
  private static final Pattern REPPAT = Pattern.compile("((<[^>]+>)|(EventDispatcher))");

  public String getEventDispatcherName()
  {
    return "EventDispatcher";
  }

  PageServiceImpl(HttpServletRequest paramHttpServletRequest, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    assert ((paramHttpServletRequest != null) && (paramString1 != null) && (paramString2 != null));
    this.mPluginName = paramString2;
    this.mFieldid = paramString3;
    if (this.mFieldid == null)
      this.mFieldid = "0";
    else
      try
      {
        Integer.parseInt(this.mFieldid);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        this.mFieldid = "0";
      }
    this.mServer = paramString1;
    this.mWindowID = paramString5;
    this.mAppName = paramString4;
    if (this.mAppName == null)
      this.mAppName = "";
  }

  public String getRelativeContextURL()
  {
    return FormContext.get().getRelativeContextURL();
  }

  public String getPluginContextURL()
  {
    return getRelativeContextURL() + "plugins/" + this.mPluginName;
  }

  public String getEventInfrastructureCode()
  {
    Matcher localMatcher = REPPAT.matcher("var EventDispatcher=new function() {\n\n    var fieldID=\"<FIELDID>\";\n\tvar windowID=\"<WINDOWID>\";\n    var midTierEventURL=\"<MTEVENTURL>\";\n    var midTierContextPath=\"<MTCONTEXTPATH>\";\n    var mServer=\"<SERVER>\";\n    var appName=\"<APPNAME>\";\n    var tabKey = 9;\n    var mSetEnable = false;\n\n    this.createMidTierEvent=function(et,ed,cb) {\n    \treturn new function() {\n    \t\tvar fetcher;\n    \t\ttry {\n    \t\t\t/*ie5, saf, moz*/\n    \t\t\tfetcher=new XMLHttpRequest();\n    \t\t} catch(e){\n    \t\t\ttry {\n    \t\t\t\tfetcher=new ActiveXObject(\"Msxml2.XMLHTTP\");\n    \t\t\t} catch(e){\n    \t\t\t\ttry {\n    \t\t\t\t\tfetcher=new ActiveXObject(\"Microsoft.XMLHTTP\");\n    \t\t\t\t}\n    \t\t\t\tcatch (e) {\n    \t\t\t\t\t/*can't pull in assert because runs on web and WUT*/\n    \t\t\t\t}\n    \t\t\t};\n    \t\t};\n    \t\tvar async = (typeof cb==\"function\");\n    \t\tfetcher.open(\"POST\",midTierEventURL,async);\n    \t\tfetcher.setRequestHeader(\"Content-type\",\"application/x-www-form-urlencoded; charset=UTF-8\");\n    \t\tvar rtl = document.documentElement.parentNode.dir;\n    \t\tvar params = \"server=\"+encodeURIComponent(mServer)+\"&rtl=\"+rtl+\"&windowID=\"+windowID+\"&fieldid=\"+fieldID+\"&eventtype=\"+encodeURIComponent(et)+\"&eventdata=\"+encodeURIComponent(ed);\n    \t\tvar eventStatus=0;\n    \t\tif (async) {\n    \t\t\tfetcher.onreadystatechange=cb;\n    \t\t}\n\n    \t\tthis.send=function() {\n    \t\t\tfetcher.send(params);\n    \t\t}\n\n    \t\tthis.abort=function() {\n    \t\t\tfetcher.abort();\n    \t\t}\n\n    \t\tthis.sendCompleted=function() {\n    \t\t\treturn (fetcher.readyState==4);\n    \t\t}\n\n    \t\tthis.resultAvailable=function() {\n    \t\t\treturn (fetcher.readyState==4&&fetcher.status==200);\n    \t\t}\n\n    \t\tthis.getResultAsXML=function() {\n    \t\t\treturn fetcher.responseXML;\n    \t\t}\n\n    \t\tthis.getResultAsString=function() {\n    \t\t\treturn fetcher.responseText;\n    \t\t}\n    \t}\n    }\n\n    this.sendEventToMidTier=function(et, ed) {\n    \tvar mtEvt = this.createMidTierEvent(et,ed,null);\n    \tmtEvt.send();\n    \treturn mtEvt.getResultAsString();\n    };\n\n    this.tabForward=function() {\n    \tvar shiftPressed = (event.shiftKey || event.shiftLeft) ? true : false;\n    \tif (!shiftPressed && event.keyCode == tabKey) {\n    \t\ttry {\n    \t\t\tif(\"external\" in window && \"arJumpOutForward\" in window.external) {\n    \t\t\t\tsetTimeout(\"window.external.arJumpOutForward()\",500);\n    \t\t\t\treturn;\n    \t\t\t}\n    \t\t} catch(e) {\n    \t\t}\n    \t\treturn parent.DView.tabForward();\n    \t}\n    };\n\n    this.tabBackward=function() {\n    \tvar shiftPressed = (event.shiftKey || event.shiftLeft) ? true : false;\n    \tif (shiftPressed && event.keyCode == tabKey) {\n    \t\ttry {\n    \t\t\tif(\"external\" in window && \"arJumpOutBackward\" in window.external) {\n    \t\t\t\tsetTimeout(\"window.external.arJumpOutBackward()\", 500);\n    \t\t\t\treturn;\n    \t\t\t}\n    \t\t} catch(e) {\n    \t\t}\n    \t\treturn parent.DView.tabBackward();\n    \t}\n    };\n\n    this.sendEventToParentForm=function(eventType,eventData) {\n    \ttry {\n    \t\tif(\"external\" in window && \"SendSignal\" in window.external) {\n    \t\t\treturn window.external.SendSignal(windowID+\"\",fieldID+\"\",eventType+\"\",eventData+\"\");\n    \t\t}\n    \t} catch(e) {\n    \t}\n    \treturn parent.DView.SendSignalToParent(windowID+\"\",\"F\"+fieldID,eventType+\"\",eventData+\"\");\n    };\n\n    this.sendParentRefresh=function() {\n    \tparent.DView.Refresh(windowID,fieldID);\n    }\n\n    this.drillDownToForm=function(server,form,vui,qual) {\n    \ttry {\n    \t\tif(\"external\" in window) {\n    \t\t\t/*For backward compatibility look for fbDrillDown function.*/\n    \t\t\tif(\"DrillDownToForm\" in window.external) {\n    \t\t\t\treturn window.external.DrillDownToForm(windowID,server,form,vui,qual);\n    \t\t\t}\n    \t\t\telse if(\"fbDrillDown\" in window.external) {\n    \t\t\t\treturn window.external.fbDrillDown(windowID,\"\",server,\"\",form,vui,qual);\n    \t\t\t}\n    \t\t}\n    \t} catch(e) {\n    \t}\n\n    \tvar winname = \"\"+new Date().getTime();\n    \tserver=encodeURIComponent(server);\n    \tform=encodeURIComponent(form);\n    \tvui=(vui != null && vui.length>0)?encodeURIComponent(vui):null;\n    \tqual=(qual==null)?\"\":encodeURIComponent(qual);\n    \tvar url = midTierContextPath;\n    \tif (appName && appName.length>0 && (server == encodeURIComponent(mServer))) {\n    \t\turl += \"apps/\"+server+\"/\"+encodeURIComponent(appName);\n    \t} else {\n    \t\turl += \"forms/\"+server;\n    \t}\n    \turl+= \"/\" + form;\n    \tif (vui !=null)  url+=\"/\" + vui;\n    \turl+= \"?qual=\" + qual+\"&mode=modifyDirect\";\n    \tparent.open(url,winname); \n    };\n\n    this.setEventHandler=function(obj, fp) {\n    \tthis.mCBObj=obj;\n    \tthis.mCBFp=fp;\n    };\n\n    this.dispatch=function(et,ed) {\n    \tif (!this.mCBFp) return;\n    \tvar func=this.mCBFp;\n    \tvar obj=this.mCBObj;\n    \tsetTimeout(function() {\n    \t\tif (obj)\n    \t\t\tfunc.apply(obj,[et,ed]);\n    \t\telse\n    \t\t\tfunc(et,ed);\n    \t},1);\n    };\n\n    this.enableSetFieldsEvent=function() {\n    \tmSetEnable = true;\n    };\n\n    this.disableSetFieldsEvent=function() {\n    \tmSetEnable = false;\n    };\n\n    this.isSetFieldsEventEnabled=function() {\n    \treturn mSetEnable;\n    };  \n\n\tthis.plugin_getWindowID=function() {\n\t\tvar fieldname = window.name;\n\t\tif (fieldname == null || typeof fieldname == 'undefined') \n\t\t\treturn windowID;\n\t\tindex = fieldname.lastIndexOf(\"_\");\n\t\tif (index == -1) return windowID;\n\t\treturn fieldname.substr(index+1);\n\t};    \n\n\tthis.plugin_getFieldID=function() {\n\t\tvar fieldname = window.name;\n\t\tif (fieldname == null || typeof fieldname == 'undefined') \n\t\t\treturn fieldID;\n\t\t index = fieldname.indexOf(\"_\");\n\t\t if (index ==-1 || index-2 <=0) return fieldID;\n\t\t return fieldname.substr(2,index-2);\n    };\n\n\twindowID = this.plugin_getWindowID();\n\n    fieldID =  this.plugin_getFieldID();\n    \n    this.loadExternalScriptInParent=function(pluginName, relpath, func) {\n    \ttry {\n    \t\tvar path = relpath;\n    \t\tif(typeof pluginName != undefined && pluginName!=null && pluginName.length > 0)\n    \t\t\tpath = \"plugins/\" + pluginName + \"/\" + relpath;\n    \t\tparent.createScriptTag(parent.RelContextPath + path, func);\n    \t} catch(e) {\n    \t}\n    };\n\n    this.loadExternalStyleInParent=function(pluginName, relpath) {\n    \ttry {\n    \t\tvar path = relpath;\n    \t\tif(typeof pluginName != undefined && pluginName.length > 0)\n    \t\t\tpath = \"plugins/\" + pluginName + \"/\" + relpath;\n    \t\tparent.createStyleTag(parent.RelContextPath + path);\n    \t} catch(e) {\n    \t}\n    };\n\n    this.loadEmbeddedScriptInParent=function(script) {\n    \ttry {\n    \t\tparent.embedScript(script);\n    \t} catch(e) {\n    \t}\n    };\n\n    this.loadEmbeddedStyleInParent=function(style) {\n    \ttry {\n    \t\tparent.embedStyle(style);\n    \t} catch(e) {\n    \t}\n    };\n    \n    this.callParentFunction=function(objName, funcName, args) {\n    \ttry {\n    \t\tvar ret = parent.callFunction(objName, funcName, args, fieldID, windowID);\n            if(typeof ret != \"undefined\")\n            \treturn ret;\n    \t} catch(e) {\n    \t}\n    };\n    \n    this.callInternalFunction=function(objName, funcName, args) {\n        try {\n            var obj = window;\n            var func = null;\n            if(objName != null && objName.length > 0) {\n            \tobj = window[objName];\n            }\n            if(funcName in obj) {\n            \tfunc = obj[funcName];\n            \tvar ret = func.apply(obj,args);\n                if(typeof ret != \"undefined\")\n                \treturn ret;\n            }\n        } catch(e){\n        }\n    };\n\n}\n");
    StringBuffer localStringBuffer = new StringBuffer("<script>");
    while (localMatcher.find())
    {
      String str1 = localMatcher.group(1);
      if ("EventDispatcher".equals(str1))
      {
        localMatcher.appendReplacement(localStringBuffer, getEventDispatcherName());
      }
      else if ("<FIELDID>".equals(str1))
      {
        localMatcher.appendReplacement(localStringBuffer, this.mFieldid + "");
      }
      else if ("<MTEVENTURL>".equals(str1))
      {
        String str2 = getRelativeContextURL() + "pluginsignal/" + this.mPluginName;
        localMatcher.appendReplacement(localStringBuffer, str2);
      }
      else if ("<SERVER>".equals(str1))
      {
        localMatcher.appendReplacement(localStringBuffer, this.mServer);
      }
      else if ("<MTCONTEXTPATH>".equals(str1))
      {
        localMatcher.appendReplacement(localStringBuffer, getRelativeContextURL());
      }
      else if ("<APPNAME>".equals(str1))
      {
        localMatcher.appendReplacement(localStringBuffer, this.mAppName);
      }
      else if ("<WINDOWID>".equals(str1))
      {
        localMatcher.appendReplacement(localStringBuffer, this.mWindowID);
      }
    }
    localMatcher.appendTail(localStringBuffer);
    localStringBuffer.append("</script>");
    return localStringBuffer.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PageServiceImpl
 * JD-Core Version:    0.6.1
 */