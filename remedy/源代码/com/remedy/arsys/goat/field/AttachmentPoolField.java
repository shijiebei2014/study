/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ 
/*   */ public class AttachmentPoolField extends GoatField
/*   */ {
/*   */   private static final long serialVersionUID = 8984522814696310896L;
/*   */   private int mMaxRows;
/*   */   private String mAddStr;
/*   */   private String mDeleteStr;
/*   */   private String mDisplayStr;
/*   */   private String mSaveStr;
/*   */   private String mDeselectStr;
/*   */   private String mLabelTitle;
/*   */   private String mFileNameTitle;
/*   */   private String mFileSizeTitle;
/*   */ 
/*   */   public AttachmentPoolField(Form paramForm, Field paramField, int paramInt)
/*   */   {
/* 1 */     super(paramForm, paramField, paramInt); setMEmitOptimised(0); if ((getMFileNameTitle() == null) || (getMFileNameTitle().length() == 0)) setMFileNameTitle("File Name"); if ((getMFileSizeTitle() == null) || (getMFileSizeTitle().length() == 0)) setMFileSizeTitle("Max Size"); if ((getMLabelTitle() == null) || (getMLabelTitle().length() == 0)) setMLabelTitle("Attach Label"); if (getMAddStr() == null) setMAddStr("Add"); if (getMDeleteStr() == null) setMDeleteStr("Delete"); if (getMDisplayStr() == null) setMDisplayStr("Display"); if (getMSaveStr() == null) setMSaveStr("Save"); if (getMDeselectStr() == null) setMDeselectStr("Deselect"); if (getMARBox() == null) setMInView(false);  } 
/* 1 */   protected void handleProperty(int arg0, Value arg1) throws DisplayPropertyMappers.BadDisplayPropertyException { if (arg0 == 232) setMAddStr(propToString(arg1)); else if (arg0 == 233) setMDeleteStr(propToString(arg1)); else if (arg0 == 234) setMDisplayStr(propToString(arg1)); else if (arg0 == 235) setMSaveStr(propToString(arg1)); else if (arg0 == 271) setMDeselectStr(propToString(arg1)); else if (arg0 == 236) setMLabelTitle(propToString(arg1)); else if (arg0 == 237) setMFileNameTitle(propToString(arg1)); else if (arg0 == 238) setMFileSizeTitle(propToString(arg1)); else if (arg0 == 5220) setMColHdrGradColor(propToHTMLColour(arg1)); else if (arg0 == 5221) setMColHdrGradBkgColor(propToHTMLColour(arg1)); else if (arg0 == 5222) setMColHdrGradType(propToLong(arg1)); else if (arg0 == 5223) setMColHdrTxtCol(propToHTMLColour(arg1)); else if (arg0 == 5217) setMHdrFtrGradColor(propToHTMLColour(arg1)); else if (arg0 == 5218) setMHdrFtrGradBkgCol(propToHTMLColour(arg1)); else if (arg0 == 5219) setMHdrFtrGradType(propToLong(arg1)); else super.handleProperty(arg0, arg1);  } 
/* 1 */   protected void setMMaxRows(int arg0) { this.mMaxRows = arg0; } 
/* 1 */   protected int getMMaxRows() { return this.mMaxRows; } 
/* 1 */   protected void setMDeselectStr(String arg0) { this.mDeselectStr = arg0; } 
/* 1 */   public String getMDeselectStr() { return this.mDeselectStr; } 
/* 1 */   protected void setMSaveStr(String arg0) { this.mSaveStr = arg0; } 
/* 1 */   public String getMSaveStr() { return this.mSaveStr; } 
/* 1 */   protected void setMDisplayStr(String arg0) { this.mDisplayStr = arg0; } 
/* 1 */   public String getMDisplayStr() { return this.mDisplayStr; } 
/* 1 */   protected void setMDeleteStr(String arg0) { this.mDeleteStr = arg0; } 
/* 1 */   public String getMDeleteStr() { return this.mDeleteStr; } 
/* 1 */   protected void setMAddStr(String arg0) { this.mAddStr = arg0; } 
/* 1 */   public String getMAddStr() { return this.mAddStr; } 
/* 1 */   protected void setMFileSizeTitle(String arg0) { this.mFileSizeTitle = arg0; } 
/* 1 */   public String getMFileSizeTitle() { return this.mFileSizeTitle; } 
/* 1 */   protected void setMFileNameTitle(String arg0) { this.mFileNameTitle = arg0; } 
/* 1 */   public String getMFileNameTitle() { return this.mFileNameTitle; } 
/* 1 */   protected void setMLabelTitle(String arg0) { this.mLabelTitle = arg0; } 
/* 1 */   public String getMLabelTitle() { return this.mLabelTitle; }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.AttachmentPoolField
 * JD-Core Version:    0.6.1
 */