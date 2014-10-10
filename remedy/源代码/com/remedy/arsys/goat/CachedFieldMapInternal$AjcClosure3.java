/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IFormFieldService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class CachedFieldMapInternal$AjcClosure3 extends AroundClosure
/*   */ {
/*   */   public CachedFieldMapInternal$AjcClosure3(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return CachedFieldMapInternal.getFields_aroundBody2((CachedFieldMapInternal)arrayOfObject[0], (IFormFieldService)arrayOfObject[1], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], (Integer[])paramArrayOfObject[2]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CachedFieldMapInternal.AjcClosure3
 * JD-Core Version:    0.6.1
 */