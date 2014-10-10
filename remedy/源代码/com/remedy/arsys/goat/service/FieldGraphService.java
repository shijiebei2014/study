/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.aspects.IFieldGraphServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.field.FieldGraph;
/*   */ import com.remedy.arsys.goat.field.FieldGraph.Node;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldGraphService;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldGraphVisitor;
/*   */ import com.remedy.arsys.goat.intf.service.IFormService;
/*   */ import com.remedy.arsys.goat.intf.service.IVisitorAccess;
/*   */ 
/*   */ public class FieldGraphService
/*   */   implements IFieldGraphService
/*   */ {
/*   */   private IFormService formService;
/*   */ 
/*   */   public void setFormService(IFormService arg0)
/*   */   {
/* 1 */     this.formService = arg0; } 
/* 1 */   public FieldGraph get(Form.ViewInfo arg0) throws GoatException { String str = FieldGraph.getFieldGraphKey(arg0); return new FieldGraph(arg0, str); } 
/* 1 */   public FieldGraph get(String arg0, String arg1, String arg2) throws GoatException { Form localForm = this.formService.getForm(arg0, arg1); Form.ViewInfo localViewInfo1 = localForm.getViewInfoByInference(arg2, false, false); Form.ViewInfo localViewInfo2 = localViewInfo1; FieldGraphService localFieldGraphService = this; Object[] arrayOfObject = new Object[3]; arrayOfObject[0] = this; arrayOfObject[1] = localFieldGraphService; arrayOfObject[2] = localViewInfo2; return IFieldGraphServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldGraphServiceCacheAspect$1$c3e5eaec(localViewInfo2, new FieldGraphService.AjcClosure1(arrayOfObject)); } 
/* 1 */   public FieldGraph get(String arg0, String arg1, int arg2) throws GoatException { Form localForm = this.formService.getForm(arg0, arg1); Form.ViewInfo localViewInfo1 = localForm.getViewInfo(arg2); Form.ViewInfo localViewInfo2 = localViewInfo1; FieldGraphService localFieldGraphService = this; Object[] arrayOfObject = new Object[3]; arrayOfObject[0] = this; arrayOfObject[1] = localFieldGraphService; arrayOfObject[2] = localViewInfo2; return IFieldGraphServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldGraphServiceCacheAspect$1$c3e5eaec(localViewInfo2, new FieldGraphService.AjcClosure3(arrayOfObject)); } 
/* 1 */   public void instantiateFields(FieldGraph arg0) throws GoatException { arg0.instantiateFieldsImpl(); } 
/* 1 */   public void instantiateFieldsOnly(FieldGraph arg0) throws GoatException { arg0.instantiateFieldsOnlyImpl(); } 
/* 1 */   public void traverseDepthFirst(FieldGraph arg0, IFieldGraphVisitor arg1, IVisitorAccess arg2) throws GoatException { arg0.getRoot().traverseDepthFirst(arg1, arg2); } 
/* 1 */   public void traverseDepthFirst(FieldGraph arg0, FieldGraph.Node arg1, IFieldGraphVisitor arg2, IVisitorAccess arg3) throws GoatException { arg1.traverseDepthFirst(arg2, arg3); } 
/* 1 */   public void traverseDepthFirst_takeTwo(FieldGraph arg0, IFieldGraphVisitor arg1, IVisitorAccess arg2, IFieldGraphVisitor arg3, IVisitorAccess arg4) throws GoatException { arg0.getRoot().traverseDepthFirst_takeTwo(arg1, arg2, arg3, arg4); }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.FieldGraphService
 * JD-Core Version:    0.6.1
 */