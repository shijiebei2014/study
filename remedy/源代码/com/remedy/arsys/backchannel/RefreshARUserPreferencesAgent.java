/*    */ package com.remedy.arsys.backchannel;
/*    */ 
/*    */ import com.remedy.arsys.goat.GoatException;
/*    */ import com.remedy.arsys.goat.aspects.IARUserPreferencesServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.preferences.ARUserPreferences;
/*    */ import com.remedy.arsys.stubs.SessionData;
/*    */ 
/*    */ public class RefreshARUserPreferencesAgent extends NDXRefreshARUserPreferences
/*    */ {
/*    */   public RefreshARUserPreferencesAgent(String paramString)
/*    */   {
/*  1 */     super(paramString);
/*    */   }
/*    */ 
/*    */   protected void process()
/*    */     throws GoatException
/*    */   {
/*    */     ARUserPreferences localARUserPreferences1;
/*  1 */     switch (this.mFlag) { case 0:
/*  1 */       localARUserPreferences1 = SessionData.get().getPreferences(); localARUserPreferences1.sync(); localARUserPreferences1.emitJS(this); append("this.result=true;"); break;
/*    */     case 1:
/*    */     default:
/*  1 */       localARUserPreferences1 = SessionData.get().getPreferences(); ARUserPreferences localARUserPreferences2 = localARUserPreferences1; flush_aroundBody1$advice(this, localARUserPreferences2, IARUserPreferencesServiceCacheAspect.aspectOf(), localARUserPreferences2, null); append("this.result=true;");
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.RefreshARUserPreferencesAgent
 * JD-Core Version:    0.6.1
 */