/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IFormService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class Form$AjcClosure3 extends AroundClosure
/*   */ {
/*   */   public Form$AjcClosure3(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return Form.getForm_aroundBody2((IFormService)arrayOfObject[0], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], Conversions.booleanValue(paramArrayOfObject[2]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Form.AjcClosure3
 * JD-Core Version:    0.6.1
 */