/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class FormService$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public FormService$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return FormService.getForm_aroundBody0((FormService)arrayOfObject[0], (FormService)arrayOfObject[1], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], Conversions.booleanValue(paramArrayOfObject[2]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.FormService.AjcClosure1
 * JD-Core Version:    0.6.1
 */