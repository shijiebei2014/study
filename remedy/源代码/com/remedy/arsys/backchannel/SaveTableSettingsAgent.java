/*    */ package com.remedy.arsys.backchannel;
/*    */ 
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Form;
/*    */ import com.remedy.arsys.goat.Form.ViewInfo;
/*    */ import com.remedy.arsys.goat.GoatException;
/*    */ import com.remedy.arsys.goat.aspects.IARUserPreferencesServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.preferences.ARUserPreferences;
/*    */ import com.remedy.arsys.goat.preferences.ARUserPreferencesKey;
/*    */ import com.remedy.arsys.stubs.SessionData;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public class SaveTableSettingsAgent extends NDXSaveTableSettings
/*    */ {
/*    */   public SaveTableSettingsAgent(String paramString)
/*    */   {
/*  1 */     super(paramString); } 
/*  1 */   protected void process() throws GoatException { ARUserPreferences localARUserPreferences1 = SessionData.get().getPreferences(); ARUserPreferencesKey localARUserPreferencesKey1 = ARUserPreferences.TABLE_COLUMN_ORDER; ARUserPreferencesKey localARUserPreferencesKey2 = ARUserPreferences.TABLE_COLUMN_WIDTH; ARUserPreferencesKey localARUserPreferencesKey3 = ARUserPreferences.TABLE_COLUMN_SORT; ARUserPreferencesKey localARUserPreferencesKey4 = ARUserPreferences.TABLE_REFRESH_INTERVAL; Form localForm = Form.get(this.mServer, this.mSchema); int i = localForm.getViewInfoByInference(this.mVui, false, false).getID(); int j = 0; String str3 = localARUserPreferences1.get(localARUserPreferencesKey2); StringBuilder localStringBuilder1 = new StringBuilder(); StringBuilder localStringBuilder2 = new StringBuilder(); String str4 = ARUserPreferences.escape(this.mSchema); double d = ARBox.getXFactor();
/*    */     Object localObject3;
/*    */     Matcher localMatcher;
/*    */     String str6;
/*    */     String str7;
/*  1 */     for (int k = 0; k < this.mColIds.length; k++) { localStringBuilder2.append(this.mColIds[k]).append("|"); str1 = this.mServer + "|" + this.mSchema + "|" + i + "|" + this.mColIds[k]; str2 = this.mServer + "\\|" + str4 + "\\|" + i + "\\|" + this.mColIds[k]; long l = Math.round(this.mColValues[k] / d); j = 0; if (str3 != null) { localObject3 = Pattern.compile("(" + str2 + ")=([0-9]+)", 8); localMatcher = ((Pattern)localObject3).matcher(str3); if (localMatcher.find()) { str6 = str3.substring(0, localMatcher.start(2)); str7 = str3.substring(localMatcher.end(2)); str3 = str6 + l + str7; j = 1; }  } if (j == 0) localStringBuilder1.append(str1 + "=" + l + "\n");  } localARUserPreferences1.put(localARUserPreferencesKey2, str3 + localStringBuilder1.toString()); String str5 = localARUserPreferences1.get(localARUserPreferencesKey1); String str1 = this.mServer + "|" + this.mSchema + "|" + i + "|" + this.mTableId; String str2 = this.mServer + "\\|" + str4 + "\\|" + i + "\\|" + this.mTableId; j = 0; if (str5 != null) { localObject1 = Pattern.compile("(" + str2 + ")=([0-9\\|]*)", 8); localMatcher = ((Pattern)localObject1).matcher(str5); if (localMatcher.find()) { localObject2 = str5.substring(0, localMatcher.start(2)); localObject3 = str5.substring(localMatcher.end(2)); str5 = (String)localObject2 + localStringBuilder2.toString() + (String)localObject3; j = 1; }  } if (j == 0) { localObject1 = str1 + "=" + localStringBuilder2.toString() + "\n"; str5 = str5 + (String)localObject1; } localARUserPreferences1.put(localARUserPreferencesKey1, str5); Object localObject1 = localARUserPreferences1.get(localARUserPreferencesKey3); j = 0; if (localObject1 != null) { localObject2 = Pattern.compile("(" + str2 + ")=([0-9\\|\\+-]*)", 8); localMatcher = ((Pattern)localObject2).matcher((CharSequence)localObject1); if (localMatcher.find()) { localObject3 = ((String)localObject1).substring(0, localMatcher.start(2)); str6 = ((String)localObject1).substring(localMatcher.end(2)); localObject1 = (String)localObject3 + this.mSortOrder + str6; j = 1; }  } if (j == 0) { localObject2 = str1 + "=" + this.mSortOrder + "\n"; localObject1 = (String)localObject1 + (String)localObject2; } localARUserPreferences1.put(localARUserPreferencesKey3, (String)localObject1); Object localObject2 = localARUserPreferences1.get(localARUserPreferencesKey4); str1 = this.mServer + "|" + this.mSchema + "|" + this.mTableId; j = 0; str2 = this.mServer + "\\|" + str4 + "\\|" + this.mTableId; if (localObject2 != null) { localObject3 = Pattern.compile("(" + str2 + ")=([0-9]+)", 8); localMatcher = ((Pattern)localObject3).matcher((CharSequence)localObject2); if (localMatcher.find()) { str6 = ((String)localObject2).substring(0, localMatcher.start(2)); str7 = ((String)localObject2).substring(localMatcher.end(2)); localObject2 = str6 + this.mInterval + str7; j = 1; }  } if ((j == 0) && (this.mInterval != 0)) { localObject3 = str1 + "=" + this.mInterval + "\n"; localObject2 = (String)localObject2 + (String)localObject3; } localARUserPreferences1.put(localARUserPreferencesKey4, (String)localObject2); ARUserPreferences localARUserPreferences2 = localARUserPreferences1; flush_aroundBody1$advice(this, localARUserPreferences2, IARUserPreferencesServiceCacheAspect.aspectOf(), localARUserPreferences2, null); append("this.result=true;");
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SaveTableSettingsAgent
 * JD-Core Version:    0.6.1
 */