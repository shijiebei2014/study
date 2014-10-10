/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.ActiveLinkCollector;
/*   */ import com.remedy.arsys.goat.field.FieldGraph;
/*   */ import com.remedy.arsys.goat.intf.service.IDHTMLService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class DHTMLRequestService$AjcClosure5 extends AroundClosure
/*   */ {
/*   */   public DHTMLRequestService$AjcClosure5(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return DHTMLRequestService.getJSData_aroundBody4((DHTMLRequestService)arrayOfObject[0], (IDHTMLService)arrayOfObject[1], (FieldGraph)paramArrayOfObject[0], (ActiveLinkCollector)paramArrayOfObject[1]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.DHTMLRequestService.AjcClosure5
 * JD-Core Version:    0.6.1
 */