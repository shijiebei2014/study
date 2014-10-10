/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class GoatFieldMapService$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public GoatFieldMapService$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return GoatFieldMapService.getGoatFieldMap_aroundBody0((GoatFieldMapService)arrayOfObject[0], (GoatFieldMapService)arrayOfObject[1], (Form)paramArrayOfObject[0], (Form.ViewInfo)paramArrayOfObject[1], (int[])paramArrayOfObject[2], Conversions.booleanValue(paramArrayOfObject[3]), Conversions.booleanValue(paramArrayOfObject[4]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.GoatFieldMapService.AjcClosure1
 * JD-Core Version:    0.6.1
 */