/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldGraphService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class FieldGraph$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public FieldGraph$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return FieldGraph.get_aroundBody0((IFieldGraphService)arrayOfObject[0], (Form.ViewInfo)paramArrayOfObject[0]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.FieldGraph.AjcClosure1
 * JD-Core Version:    0.6.1
 */