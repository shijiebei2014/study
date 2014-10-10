/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.GoatServerMessage;
/*   */ import com.remedy.arsys.goat.field.FieldGraph;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class DHTMLService$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public DHTMLService$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return DHTMLService.getHTMLData_aroundBody0((DHTMLService)arrayOfObject[0], (DHTMLService)arrayOfObject[1], (FieldGraph)paramArrayOfObject[0], (GoatServerMessage[])paramArrayOfObject[1]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.DHTMLService.AjcClosure1
 * JD-Core Version:    0.6.1
 */