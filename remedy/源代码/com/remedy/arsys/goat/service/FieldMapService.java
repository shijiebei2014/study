/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.CachedFieldMap;
/*   */ import com.remedy.arsys.goat.CachedFieldMapInternal;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.aspects.IFieldMapServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldMapService;
/*   */ import com.remedy.arsys.goat.intf.service.IFormFieldService;
/*   */ import com.remedy.arsys.stubs.ServerLogin;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class FieldMapService
/*   */   implements IFieldMapService
/*   */ {
/*   */   private IFormFieldService formFieldService;
/*   */ 
/*   */   public void setFormFieldService(IFormFieldService arg0)
/*   */   {
/* 1 */     this.formFieldService = arg0; } 
/* 1 */   public CachedFieldMap getFieldMap(Form arg0) throws GoatException { boolean bool = false; Form localForm = arg0; FieldMapService localFieldMapService = this; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localFieldMapService; arrayOfObject[2] = localForm; arrayOfObject[3] = Conversions.booleanObject(bool); return IFieldMapServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldMapServiceCacheAspect$1$37a4c128(localForm, bool, new FieldMapService.AjcClosure1(arrayOfObject)); } 
/* 1 */   public CachedFieldMap getFieldMap(Form arg0, boolean arg1) throws GoatException { String str = arg0.getServerName().toLowerCase() + "/" + arg0.getName() + "/" + arg0.getServerLogin().getPermissionsKey(); CachedFieldMap localCachedFieldMap = new CachedFieldMap(new CachedFieldMapInternal(str, arg0.getSchemaKey(), arg0.getServer(), arg1, this, this.formFieldService)); return localCachedFieldMap;
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.FieldMapService
 * JD-Core Version:    0.6.1
 */