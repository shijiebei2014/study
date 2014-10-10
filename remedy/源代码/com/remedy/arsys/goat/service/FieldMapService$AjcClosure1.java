/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class FieldMapService$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public FieldMapService$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return FieldMapService.getFieldMap_aroundBody0((FieldMapService)arrayOfObject[0], (FieldMapService)arrayOfObject[1], (Form)paramArrayOfObject[0], Conversions.booleanValue(paramArrayOfObject[1]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.FieldMapService.AjcClosure1
 * JD-Core Version:    0.6.1
 */