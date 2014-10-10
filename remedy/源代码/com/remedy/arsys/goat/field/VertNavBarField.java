/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.support.FontTable;
/*   */ 
/*   */ public class VertNavBarField extends GoatField
/*   */ {
/*   */   private static final long serialVersionUID = -7199255644660855047L;
/*   */   private int mAccess;
/*   */   private long mInitialValue;
/*   */   private int mWorkflowOnSelected;
/*   */   private int mSelectOnClick;
/*   */   private String mTpLvlGradientColor;
/*   */   private String mTpLvlColor;
/*   */   private String mSbLvlColor;
/*   */   private String mSbLvlOddColor;
/*   */   private String mColor;
/*   */   private String mLblColor;
/*   */   private String mItemColor;
/*   */   private int mHeight;
/*   */   private long mHdrGradientType;
/*   */   private String mFont;
/*   */   private int mNavMode;
/*   */   private boolean mInFill;
/*   */ 
/*   */   public VertNavBarField(Form paramForm, Field paramField, int paramInt)
/*   */   {
/* 1 */     super(paramForm, paramField, paramInt); } 
/* 1 */   protected void handleProperty(int arg0, Value arg1) throws DisplayPropertyMappers.BadDisplayPropertyException { switch (arg0) { case 5:
/* 1 */       setMAccess(propToInt(arg1)); break;
/*   */     case 5063:
/* 1 */       setMInitialValue(propToLong(arg1)); break;
/*   */     case 5064:
/* 1 */       setMWorkflowOnSelected(propToInt(arg1)); if ((getMWorkflowOnSelected() != 1) && (getMWorkflowOnSelected() != 0)) setMWorkflowOnSelected(0); break;
/*   */     case 305:
/* 1 */       String str1 = propToHTMLColour(arg1); if ((str1 != null) && (str1.length() > 0)) setMTpLvlGradientColor(str1); break;
/*   */     case 282:
/* 1 */       String str2 = propToHTMLColour(arg1); if ((str2 != null) && (str2.length() > 0)) setMTpLvlColor(str2); break;
/*   */     case 8:
/* 1 */       String str3 = propToHTMLColour(arg1); if ((str3 != null) && (str3.length() > 0)) setMSbLvlColor(str3); break;
/*   */     case 306:
/* 1 */       setMHdrGradientType(propToLong(arg1)); break;
/*   */     case 24:
/* 1 */       setMLabelColor(propToHTMLColour(arg1)); break;
/*   */     case 22:
/* 1 */       setMFont(FontTable.mapFontToClassName(propToString(arg1))); break;
/*   */     case 312:
/* 1 */       setMHdrHeight(propToInt(arg1)); break;
/*   */     case 313:
/* 1 */       setMItemColor(propToHTMLColour(arg1)); break;
/*   */     case 5065:
/* 1 */       if (propToInt(arg1) == 1) setMSelectOnClick(1); else setMSelectOnClick(0); break;
/*   */     case 5117:
/* 1 */       setMNavMode(propToInt(arg1)); break;
/*   */     case 5211:
/* 1 */       String str4 = propToHTMLColour(arg1); if ((str4 != null) && (str4.length() > 0)) setMSbLvlOddColor(str4); break;
/*   */     default:
/* 1 */       super.handleProperty(arg0, arg1); }  } 
/* 1 */   protected void setDefaultDisplayProperties() { super.setDefaultDisplayProperties(); setMAccess(2); setMTpLvlColor("#2188c9"); setMSbLvlColor("#eef3f5"); setMSbLvlOddColor("#6ab8e9"); setMHdrHeight(25); } 
/* 1 */   protected void setMAccess(int arg0) { this.mAccess = arg0; } 
/* 1 */   public int getMAccess() { return this.mAccess; } 
/* 1 */   protected void setMInitialValue(long arg0) { this.mInitialValue = arg0; } 
/* 1 */   public long getMInitialValue() { return this.mInitialValue; } 
/* 1 */   protected void setMWorkflowOnSelected(int arg0) { this.mWorkflowOnSelected = arg0; } 
/* 1 */   public int getMWorkflowOnSelected() { return this.mWorkflowOnSelected; } 
/* 1 */   protected void setMSelectOnClick(int arg0) { this.mSelectOnClick = arg0; } 
/* 1 */   public int getMSelectOnClick() { return this.mSelectOnClick; } 
/* 1 */   protected void setMTpLvlGradientColor(String arg0) { this.mTpLvlGradientColor = arg0; } 
/* 1 */   public String getMTpLvlGradientColor() { return this.mTpLvlGradientColor; } 
/* 1 */   protected void setMTpLvlColor(String arg0) { this.mTpLvlColor = arg0; } 
/* 1 */   public String getMTpLvlColor() { return this.mTpLvlColor; } 
/* 1 */   protected void setMSbLvlColor(String arg0) { this.mSbLvlColor = arg0; } 
/* 1 */   public String getMSbLvlColor() { return this.mSbLvlColor; } 
/* 1 */   protected void setMSbLvlOddColor(String arg0) { this.mSbLvlOddColor = arg0; } 
/* 1 */   public String getMSbLvlOddColor() { return this.mSbLvlOddColor; } 
/* 1 */   protected void setMColour(String arg0) { this.mColor = arg0; } 
/* 1 */   public String getMColour() { return this.mColor; } 
/* 1 */   protected void setMHdrGradientType(long arg0) { this.mHdrGradientType = arg0; } 
/* 1 */   public long getMHdrGradientType() { return this.mHdrGradientType; } 
/* 1 */   protected void setMLabelColor(String arg0) { this.mLblColor = arg0; } 
/* 1 */   public String getMLabelColor() { return this.mLblColor; } 
/* 1 */   protected void setMItemColor(String arg0) { this.mItemColor = arg0; } 
/* 1 */   public String getMItemColor() { return this.mItemColor; } 
/* 1 */   protected void setMHdrHeight(int arg0) { this.mHeight = arg0; } 
/* 1 */   public int getMHdrHeight() { return this.mHeight; } 
/* 1 */   protected void setMFont(String arg0) { this.mFont = arg0; } 
/* 1 */   public String getMFont() { return this.mFont; } 
/* 1 */   public int getMNavMode() { return this.mNavMode; } 
/* 1 */   public void setMNavMode(int arg0) { this.mNavMode = arg0; } 
/* 1 */   public void setMInFill(boolean arg0) { this.mInFill = arg0; } 
/* 1 */   public boolean getMInFill() { return this.mInFill; }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.VertNavBarField
 * JD-Core Version:    0.6.1
 */