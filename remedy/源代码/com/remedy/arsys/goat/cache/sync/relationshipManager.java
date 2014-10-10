package com.remedy.arsys.goat.cache.sync;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.SetItem;
import com.remedy.arsys.share.Cachetable;
import com.remedy.arsys.share.FieldMapCache;

public class relationshipManager
{
  private static relationshipManager instance = new relationshipManager();
  private final Cache mRelationshipCache = new Cachetable("Sync relationships", 1, 3);

  public static relationshipManager getInstance()
  {
    return instance;
  }

  public void trackFormFieldGraphKey(Form.ViewInfo paramViewInfo, String paramString)
    throws GoatException
  {
    String str = getRelationshipCachekey_form_fieldgraph(paramViewInfo);
    Cache.SetItem localSetItem = (Cache.SetItem)this.mRelationshipCache.get(str, Cache.SetItem.class);
    if (localSetItem == null)
      localSetItem = new Cache.SetItem(paramViewInfo.getContainingForm().getServer());
    localSetItem.addSetItem(paramString);
    this.mRelationshipCache.put(str, localSetItem);
  }

  public void trackFormHTMLDataKey(Form.ViewInfo paramViewInfo, String paramString)
    throws GoatException
  {
    String str = getRelationshipCachekey_form_html(paramViewInfo);
    Cache.SetItem localSetItem = (Cache.SetItem)this.mRelationshipCache.get(str, Cache.SetItem.class);
    if (localSetItem == null)
      localSetItem = new Cache.SetItem(paramViewInfo.getContainingForm().getServer());
    localSetItem.addSetItem(paramString);
    this.mRelationshipCache.put(str, localSetItem);
  }

  public void trackFormJSDataKey(Form.ViewInfo paramViewInfo, String paramString)
    throws GoatException
  {
    String str = getRelationshipCachekey_form_js(paramViewInfo);
    Cache.SetItem localSetItem = (Cache.SetItem)this.mRelationshipCache.get(str, Cache.SetItem.class);
    if (localSetItem == null)
      localSetItem = new Cache.SetItem(paramViewInfo.getContainingForm().getServer());
    localSetItem.addSetItem(paramString);
    this.mRelationshipCache.put(str, localSetItem);
  }

  public void trackFormFieldMapKey(Form paramForm, String paramString, Cache paramCache)
    throws GoatException
  {
    String str1 = ((FieldMapCache)paramCache).getPrefix();
    String str2 = getRelationshipCachekey_form_fieldMap(paramForm);
    Cache.SetItem localSetItem = (Cache.SetItem)this.mRelationshipCache.get(str2, Cache.SetItem.class);
    if (localSetItem == null)
      localSetItem = new Cache.SetItem(paramForm.getServer());
    localSetItem.addSetItem(str1 + paramString);
    this.mRelationshipCache.put(str2, localSetItem);
  }

  public void trackFormFormFieldKey(String paramString1, String paramString2, int paramInt, String paramString3)
  {
    String str = getRelationshipCachekey_form_formField(paramString1, paramString2, paramInt);
    Cache.SetItem localSetItem = (Cache.SetItem)this.mRelationshipCache.get(str, Cache.SetItem.class);
    if (localSetItem == null)
      localSetItem = new Cache.SetItem(paramString1);
    localSetItem.addSetItem(paramString3);
    this.mRelationshipCache.put(str, localSetItem);
  }

  public void trackFormGoatFieldMapKey(Form paramForm, String paramString)
    throws GoatException
  {
    String str = getRelationshipCachekey_form_goatFieldMap(paramForm);
    Cache.SetItem localSetItem = (Cache.SetItem)this.mRelationshipCache.get(str, Cache.SetItem.class);
    if (localSetItem == null)
      localSetItem = new Cache.SetItem(paramForm.getServer());
    localSetItem.addSetItem(paramString);
    this.mRelationshipCache.put(str, localSetItem);
  }

  protected void removeRelationshipCachekey_form_all(String paramString)
  {
    removeRelationshipCachekey_form_fieldgraph(paramString);
    removeRelationshipCachekey_form_html(paramString);
    removeRelationshipCachekey_form_js(paramString);
  }

  protected void removeRelationshipCachekey_form_fieldgraph(String paramString)
  {
    this.mRelationshipCache.remove(getRelationshipCachekey_form_fieldgraph(paramString));
  }

  protected void removeRelationshipCachekey_form_html(String paramString)
  {
    this.mRelationshipCache.remove(getRelationshipCachekey_form_html(paramString));
  }

  protected void removeRelationshipCachekey_form_js(String paramString)
  {
    this.mRelationshipCache.remove(getRelationshipCachekey_form_js(paramString));
  }

  protected void removeRelationshipCachekey_form_fieldMap(String paramString)
  {
    this.mRelationshipCache.remove(getRelationshipCachekey_form_fieldMap(paramString));
  }

  protected void removeRelationshipCachekey_form_formField(String paramString)
  {
    this.mRelationshipCache.remove(getRelationshipCachekey_form_formField(paramString));
  }

  protected void removeRelationshipCachekey_form_goatFieldMap(String paramString)
  {
    this.mRelationshipCache.remove(getRelationshipCachekey_form_goatFieldMap(paramString));
  }

  protected String getRelationshipCachekey_form_fieldgraph(String paramString)
  {
    return "fg:" + paramString;
  }

  protected String getRelationshipCachekey_form_html(String paramString)
  {
    return "fh:" + paramString;
  }

  protected String getRelationshipCachekey_form_js(String paramString)
  {
    return "fj:" + paramString;
  }

  protected String getRelationshipCachekey_form_fieldMap(String paramString)
  {
    return "ffm:" + paramString;
  }

  protected String getRelationshipCachekey_form_formField(String paramString)
  {
    return "fff:" + paramString;
  }

  protected String getRelationshipCachekey_form_goatFieldMap(String paramString)
  {
    return "fgfm:" + paramString;
  }

  private String getRelationshipCachekey_form_fieldgraph(Form.ViewInfo paramViewInfo)
  {
    return getRelationshipCachekey_form_fieldgraph(FieldGraph.getServerFormKey(paramViewInfo));
  }

  private String getRelationshipCachekey_form_html(Form.ViewInfo paramViewInfo)
  {
    return getRelationshipCachekey_form_html(FieldGraph.getServerFormKey(paramViewInfo));
  }

  private String getRelationshipCachekey_form_js(Form.ViewInfo paramViewInfo)
  {
    return getRelationshipCachekey_form_js(FieldGraph.getServerFormKey(paramViewInfo));
  }

  private String getRelationshipCachekey_form_fieldMap(Form paramForm)
  {
    return getRelationshipCachekey_form_fieldMap(FieldGraph.getServerFormKey(paramForm.getServer(), paramForm.getName()));
  }

  private String getRelationshipCachekey_form_formField(String paramString1, String paramString2, int paramInt)
  {
    return getRelationshipCachekey_form_formField(Form.getFormFieldCacheKey(paramString1, paramString2, paramInt));
  }

  private String getRelationshipCachekey_form_goatFieldMap(Form paramForm)
  {
    return getRelationshipCachekey_form_goatFieldMap(FieldGraph.getServerFormKey(paramForm.getServer(), paramForm.getName()));
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.cache.sync.relationshipManager
 * JD-Core Version:    0.6.1
 */