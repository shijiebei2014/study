/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.remedy.arsys.goat.intf.service.IActiveLinkService;
/*   */ import org.aspectj.runtime.internal.AroundClosure;
/*   */ 
/*   */ public class ActiveLink$AjcClosure1 extends AroundClosure
/*   */ {
/*   */   public ActiveLink$AjcClosure1(Object[] paramArrayOfObject)
/*   */   {
/* 1 */     super(paramArrayOfObject); } 
/* 1 */   public Object run(Object[] paramArrayOfObject) { Object[] arrayOfObject = this.state; return ActiveLink.get_aroundBody0((IActiveLinkService)arrayOfObject[0], (String)paramArrayOfObject[0], (String)paramArrayOfObject[1], (String)paramArrayOfObject[2], (String)paramArrayOfObject[3]);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ActiveLink.AjcClosure1
 * JD-Core Version:    0.6.1
 */