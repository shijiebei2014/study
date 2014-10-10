/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IFormFieldService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class CachedFieldMapInternal$AjcClosure7 extends AroundClosure
/*   */ {
/*   */   public CachedFieldMapInternal$AjcClosure7(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return CachedFieldMapInternal.getField_aroundBody6((CachedFieldMapInternal)arrayOfObject[0], (IFormFieldService)arrayOfObject[1], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], Conversions.intValue(paramArrayOfObject[2]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CachedFieldMapInternal.AjcClosure7
 * JD-Core Version:    0.6.1
 */