/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldMapService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class GoatFieldMapService$AjcClosure5 extends AroundClosure
/*   */ {
/*   */   public GoatFieldMapService$AjcClosure5(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return GoatFieldMapService.getFieldMap_aroundBody4((GoatFieldMapService)arrayOfObject[0], (IFieldMapService)arrayOfObject[1], (Form)paramArrayOfObject[0], Conversions.booleanValue(paramArrayOfObject[1]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.GoatFieldMapService.AjcClosure5
 * JD-Core Version:    0.6.1
 */