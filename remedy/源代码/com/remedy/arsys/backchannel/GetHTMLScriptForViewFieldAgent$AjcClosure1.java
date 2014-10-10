/*   */ package com.remedy.arsys.backchannel;
/*   */ 
/*   */ import com.remedy.arsys.goat.GoatServerMessage;
/*   */ import com.remedy.arsys.goat.field.FieldGraph;
/*   */ import com.remedy.arsys.goat.intf.service.IDHTMLService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class GetHTMLScriptForViewFieldAgent$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public GetHTMLScriptForViewFieldAgent$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return GetHTMLScriptForViewFieldAgent.getVFHTMLData_aroundBody0((GetHTMLScriptForViewFieldAgent)arrayOfObject[0], (IDHTMLService)arrayOfObject[1], (FieldGraph)paramArrayOfObject[0], (GoatServerMessage[])paramArrayOfObject[1]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetHTMLScriptForViewFieldAgent.AjcClosure1
 * JD-Core Version:    0.6.1
 */