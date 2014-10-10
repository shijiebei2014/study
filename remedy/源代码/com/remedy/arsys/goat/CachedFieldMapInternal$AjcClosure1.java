/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IFormFieldService;
/*   */ import java.util.List;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class CachedFieldMapInternal$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public CachedFieldMapInternal$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return CachedFieldMapInternal.categorizeLocalRemoteFields_aroundBody0((CachedFieldMapInternal)arrayOfObject[0], (IFormFieldService)arrayOfObject[1], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], (List)paramArrayOfObject[2]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CachedFieldMapInternal.AjcClosure1
 * JD-Core Version:    0.6.1
 */