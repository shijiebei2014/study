/*   */ package com.remedy.arsys.goat.savesearches;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IARUserSearchesService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class ARUserSearches$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public ARUserSearches$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return ARUserSearches.getUserSearches_aroundBody0((IARUserSearchesService)arrayOfObject[0], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], (String)paramArrayOfObject[2], Conversions.booleanValue(paramArrayOfObject[3]));
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.savesearches.ARUserSearches.AjcClosure1
 * JD-Core Version:    0.6.1
 */