/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.aspects.IARUserPreferencesServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.intf.service.IARUserPreferencesService;
/*   */ import com.remedy.arsys.goat.preferences.ARUserPreferences;
/*   */ import com.remedy.arsys.stubs.SessionData;
/*   */ 
/*   */ public class ARUserPreferencesService
/*   */   implements IARUserPreferencesService
/*   */ {
/*   */   protected ARUserPreferences getUserPreferencesHelper()
/*   */   {
/* 1 */     String str = SessionData.get().getUserName().intern(); return new ARUserPreferences(str); } 
/* 1 */   public ARUserPreferences getUserPreferences() { ARUserPreferencesService localARUserPreferencesService = this; Object[] arrayOfObject = new Object[2]; arrayOfObject[0] = this; arrayOfObject[1] = localARUserPreferencesService; ARUserPreferences localARUserPreferences = IARUserPreferencesServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IARUserPreferencesServiceCacheAspect$1$157bea7f(new ARUserPreferencesService.AjcClosure1(arrayOfObject)); return (ARUserPreferences)localARUserPreferences.clone();
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.ARUserPreferencesService
 * JD-Core Version:    0.6.1
 */