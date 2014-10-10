/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.remedy.arsys.goat.CachedFieldMap;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.aspects.IFieldMapServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.aspects.IGoatFieldMapServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.field.GoatField;
/*   */ import com.remedy.arsys.goat.field.GoatFieldMap;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldMapService;
/*   */ import com.remedy.arsys.goat.intf.service.IFormFieldService;
/*   */ import com.remedy.arsys.goat.intf.service.IGoatFieldFactory;
/*   */ import com.remedy.arsys.goat.intf.service.IGoatFieldMapService;
/*   */ import java.util.HashMap;
/*   */ import java.util.HashSet;
/*   */ import java.util.Iterator;
/*   */ import java.util.Map;
/*   */ import java.util.Map.Entry;
/*   */ import java.util.Set;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class GoatFieldMapService
/*   */   implements IGoatFieldMapService
/*   */ {
/*   */   private IFormFieldService formFieldService;
/*   */   private IFieldMapService fieldMapService;
/*   */   private IGoatFieldFactory goatfieldFactory;
/*   */ 
/*   */   public void setFormFieldService(IFormFieldService arg0)
/*   */   {
/* 1 */     this.formFieldService = arg0; } 
/* 1 */   public void setFieldMapService(IFieldMapService arg0) { this.fieldMapService = arg0; } 
/* 1 */   public void setGoatFieldFactory(IGoatFieldFactory arg0) { this.goatfieldFactory = arg0; } 
/* 1 */   public GoatFieldMap getGoatFieldMap(Form arg0, Form.ViewInfo arg1, boolean arg2) throws GoatException { boolean bool1 = false; boolean bool2 = arg2; int[] arrayOfInt = arg0.getFieldIDs(arg2); Form.ViewInfo localViewInfo = arg1; Form localForm = arg0; GoatFieldMapService localGoatFieldMapService = this; Object[] arrayOfObject = new Object[7]; arrayOfObject[0] = this; arrayOfObject[1] = localGoatFieldMapService; arrayOfObject[2] = localForm; arrayOfObject[3] = localViewInfo; arrayOfObject[4] = arrayOfInt; arrayOfObject[5] = Conversions.booleanObject(bool2); arrayOfObject[6] = Conversions.booleanObject(bool1); return IGoatFieldMapServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IGoatFieldMapServiceCacheAspect$1$d227381c(localForm, localViewInfo, arrayOfInt, bool2, bool1, new GoatFieldMapService.AjcClosure1(arrayOfObject)); } 
/* 1 */   public GoatFieldMap getGoatFieldMap(Form arg0, Form.ViewInfo arg1, boolean arg2, boolean arg3) throws GoatException { boolean bool1 = arg3; boolean bool2 = arg2; int[] arrayOfInt = arg0.getFieldIDs(arg2); Form.ViewInfo localViewInfo = arg1; Form localForm = arg0; GoatFieldMapService localGoatFieldMapService = this; Object[] arrayOfObject = new Object[7]; arrayOfObject[0] = this; arrayOfObject[1] = localGoatFieldMapService; arrayOfObject[2] = localForm; arrayOfObject[3] = localViewInfo; arrayOfObject[4] = arrayOfInt; arrayOfObject[5] = Conversions.booleanObject(bool2); arrayOfObject[6] = Conversions.booleanObject(bool1); return IGoatFieldMapServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IGoatFieldMapServiceCacheAspect$1$d227381c(localForm, localViewInfo, arrayOfInt, bool2, bool1, new GoatFieldMapService.AjcClosure3(arrayOfObject)); } 
/* 1 */   public GoatFieldMap getGoatFieldMap(Form arg0, Form.ViewInfo arg1, int[] arg2, boolean arg3, boolean arg4) throws GoatException { GoatFieldMap localGoatFieldMap = new GoatFieldMap(); int i = arg1.getID(); HashMap localHashMap = new HashMap(); boolean bool = arg3; Form localForm = arg0; IFieldMapService localIFieldMapService = this.fieldMapService; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localIFieldMapService; arrayOfObject[2] = localForm; arrayOfObject[3] = Conversions.booleanObject(bool); CachedFieldMap localCachedFieldMap = IFieldMapServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldMapServiceCacheAspect$1$37a4c128(localForm, bool, new GoatFieldMapService.AjcClosure5(arrayOfObject)); for (int m : arg2) { GoatField localGoatField = this.goatfieldFactory.create(arg0, i, (Field)localCachedFieldMap.get(Integer.valueOf(m))); localGoatFieldMap.put(genHashKey(arg0, i, m), localGoatField); localHashMap.put(Integer.valueOf(m), localGoatField); } if (arg4) initHelpText(arg0, localHashMap); return localGoatFieldMap; } 
/* 1 */   static String genHashKey(Form arg0, int arg1, int arg2) { return "" + arg2 + arg1 + arg0.getServerFormNames(); } 
/* 1 */   public boolean initHelpText(Form arg0, Map arg1) throws GoatException { HashSet localHashSet = new HashSet(); Set localSet = arg1.entrySet(); for (Object localObject1 = localSet.iterator(); ((Iterator)localObject1).hasNext(); ) { Map.Entry localEntry1 = (Map.Entry)((Iterator)localObject1).next(); localObject2 = (GoatField)localEntry1.getValue(); if (!((GoatField)localObject2).isHelpTextInit()) { int i = ((Integer)localEntry1.getKey()).intValue(); localHashSet.add(Integer.valueOf(i)); }  } localObject1 = this.formFieldService.getHelpText(arg0.getServer(), arg0.getSchemaKey(), localHashSet); boolean bool = false; Object localObject2 = ((Map)localObject1).entrySet(); for (Map.Entry localEntry2 : (Set)localObject2) { int j = ((Integer)localEntry2.getKey()).intValue(); String str = (String)localEntry2.getValue(); GoatField localGoatField = (GoatField)arg1.get(Integer.valueOf(j)); if (localGoatField != null) { localGoatField.setHelpText(str); bool = true; }  } return bool;
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.GoatFieldMapService
 * JD-Core Version:    0.6.1
 */