/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IFieldMapService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class Form$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public Form$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return Form.getFieldMap_aroundBody0((Form)arrayOfObject[0], (IFieldMapService)arrayOfObject[1], (Form)paramArrayOfObject[0], Conversions.booleanValue(paramArrayOfObject[1]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Form.AjcClosure1
 * JD-Core Version:    0.6.1
 */