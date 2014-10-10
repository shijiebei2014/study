/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ 
/*   */ public class AppListField extends ViewField
/*   */ {
/*   */   private static final long serialVersionUID = 3754047259891388796L;
/*   */   private int mApplistMode;
/*   */   private boolean mInFill;
/*   */   private String mTpLvlColor;
/*   */   private String mSbLvlEvenColor;
/*   */   private String mSbLvlOddColor;
/*   */   private String mLTpLvlTxtColor;
/*   */   private String mSbLvlTxtColor;
/*   */ 
/*   */   public AppListField(Form paramForm, Field paramField, int paramInt)
/*   */   {
/* 1 */     super(paramForm, paramField, paramInt); if (getMAppMode() == 0) { setMTpLvlColor(""); setMLTpLvlTxtColor(""); setMSbLvlTxtColor(""); }  } 
/* 1 */   protected void handleProperty(int arg0, Value arg1) throws DisplayPropertyMappers.BadDisplayPropertyException { switch (arg0) { case 5120:
/* 1 */       setMAppMode(propToInt(arg1)); break;
/*   */     case 5227:
/* 1 */       String str1 = propToHTMLColour(arg1); if ((str1 != null) && (str1.length() > 0)) setMTpLvlColor(str1); break;
/*   */     case 5228:
/* 1 */       String str2 = propToHTMLColour(arg1); if ((str2 != null) && (str2.length() > 0)) setMSbLvlEvenColor(str2); break;
/*   */     case 5229:
/* 1 */       String str3 = propToHTMLColour(arg1); if ((str3 != null) && (str3.length() > 0)) setMSbLvlOddColor(str3); break;
/*   */     default:
/* 1 */       super.handleProperty(arg0, arg1); }  } 
/* 1 */   protected void setDefaultDisplayProperties() { super.setDefaultDisplayProperties(); setMTpLvlColor("#2188c9"); setMSbLvlOddColor("#6ab8e9"); setMSbLvlEvenColor("#eef3f5"); setMLTpLvlTxtColor("#ffffff"); setMSbLvlTxtColor("#196697"); } 
/* 1 */   public int getMAppMode() { return this.mApplistMode; } 
/* 1 */   public void setMAppMode(int arg0) { this.mApplistMode = arg0; } 
/* 1 */   public void setMInFill(boolean arg0) { this.mInFill = arg0; } 
/* 1 */   public boolean getMInFill() { return this.mInFill; } 
/* 1 */   public void setMSbLvlEvenColor(String arg0) { this.mSbLvlEvenColor = arg0; } 
/* 1 */   public String getMSbLvlEvenColor() { return this.mSbLvlEvenColor; } 
/* 1 */   public void setMSbLvlOddColor(String arg0) { this.mSbLvlOddColor = arg0; } 
/* 1 */   public String getMSbLvlOddColor() { return this.mSbLvlOddColor; } 
/* 1 */   public void setMTpLvlColor(String arg0) { this.mTpLvlColor = arg0; } 
/* 1 */   public String getMTopLvlColor() { return this.mTpLvlColor; } 
/* 1 */   public String getMLTpLvlTxtColor() { return this.mLTpLvlTxtColor; } 
/* 1 */   public void setMLTpLvlTxtColor(String arg0) { this.mLTpLvlTxtColor = arg0; } 
/* 1 */   public String getMSbLvlTxtColor() { return this.mSbLvlTxtColor; } 
/* 1 */   public void setMSbLvlTxtColor(String arg0) { this.mSbLvlTxtColor = arg0; }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.AppListField
 * JD-Core Version:    0.6.1
 */