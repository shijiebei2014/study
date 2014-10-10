package com.remedy.arsys.goat.sharedresource.template;

import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.sharedresource.IResourceObject;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import com.remedy.arsys.goat.skins.SkinHelper;
import com.remedy.arsys.share.JSWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateObject
  implements IResourceObject, Serializable
{
  private static final long serialVersionUID = -267383278252350500L;
  String mName;
  String mMarkup;
  List<String> mParamList = new ArrayList();
  List<String> mFieldList = new ArrayList();

  public TemplateObject(String paramString)
  {
    this.mName = paramString;
    this.mMarkup = "";
  }

  public TemplateObject(String paramString1, String paramString2)
  {
    this.mName = paramString1;
    this.mMarkup = paramString2;
  }

  public String getMarkup()
  {
    return this.mMarkup;
  }

  public void setMarkup(String paramString)
  {
    this.mMarkup = paramString;
  }

  public void addParameter(String paramString)
  {
    if ((paramString != null) && (paramString.length() > 0) && (!this.mParamList.contains(paramString)))
      this.mParamList.add(paramString);
  }

  public void addFieldId(String paramString)
  {
    if ((paramString != null) && (paramString.length() > 0) && (!this.mFieldList.contains(paramString)))
      this.mFieldList.add(paramString);
  }

  public void emitParameterJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.openList();
    for (int i = 0; i < this.mParamList.size(); i++)
    {
      if ((i > 0) && (i < this.mParamList.size()))
        localJSWriter.comma();
      localJSWriter.appendqs((String)this.mParamList.get(i));
    }
    localJSWriter.closeList();
  }

  public void emitFieldIDJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.openList();
    for (int i = 0; i < this.mFieldList.size(); i++)
    {
      if ((i > 0) && (i < this.mFieldList.size()))
        localJSWriter.comma();
      localJSWriter.appendqs((String)this.mFieldList.get(i));
    }
    localJSWriter.closeList();
  }

  public void emitJS(Emitter paramEmitter, boolean paramBoolean, SkinDefinitionMap paramSkinDefinitionMap)
    throws GoatException
  {
    String str = SkinHelper.replaceSkinProperties(getMarkup(), this.mName, paramSkinDefinitionMap, (short)5);
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.startStatement("new Template(").appendqs(this.mName).comma().appendqs(str, paramBoolean).comma();
    emitParameterJS(paramEmitter);
    localJSWriter.comma();
    emitFieldIDJS(paramEmitter);
    localJSWriter.appendStr(")");
    localJSWriter.endStatement();
  }

  public void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.sharedresource.template.TemplateObject
 * JD-Core Version:    0.6.1
 */