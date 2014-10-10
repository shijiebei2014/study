/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.ARBox;
/*   */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.FormContext;
/*   */ import com.remedy.arsys.goat.OutputNotes;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import com.remedy.arsys.share.MessageTranslation;
/*   */ import com.remedy.arsys.stubs.SessionData;
/*   */ import com.remedy.arsys.support.FontTable;
/*   */ 
/*   */ public abstract class DataField extends GoatField
/*   */ {
/*   */   private static final long serialVersionUID = -4706853412840372720L;
/*   */   private String mLabelFont;
/*   */   private String mLabelColour;
/*   */   private String mDataFont;
/*   */   private String mHighlightStartColor;
/*   */   private String mHighlightEndColor;
/*   */   private int mLabelAlign;
/*   */   private int mLabelJustify;
/*   */   private int mPosSector;
/*   */   private long mRows;
/*   */   private long mCols;
/*   */   private long mAccess;
/*   */   private boolean mTextOnly;
/*   */   private boolean mDisableChange;
/*   */   private boolean mHighlight;
/*   */   private ARBox mLabelARBox;
/*   */   private ARBox mDataARBox;
/*   */   private ARBox mExpandARBox;
/*   */   private ARBox mMenuARBox;
/* 1 */   public static final GoatImageButton MMenuButton = new GoatImageButton("menu"); private static final GoatImageButton MDefaultExpandButton = new GoatImageButton("text"); private static final GoatImageButton MDefaultFileUploadButton = new GoatImageButton("open"); private static final GoatImageButton MEditButton = new GoatImageButton("edit");
/*   */ 
/* 1 */   protected DataField(Form paramForm, Field paramField, int paramInt) { super(paramForm, paramField, paramInt); setMEmitViewable(0); setMEmitOptimised(1); setMEmitViewless(1); if (getMLabelARBox() == null) setMLabel(null); if ((getMARBox() == null) || (getMDataARBox() == null)) setMInView(false); if ((getMARBox() != null) && (getMDataARBox() != null) && (getMDataARBox().getW() == 0) && (getMDataARBox().getH() == 0)) { getMDataARBox().setW(getMARBox().getW() - getMDataARBox().getX()); getMDataARBox().setH(getMARBox().getH() - getMDataARBox().getY()); } if ((getMLabelFont() == null) || (getMLabelFont().equals("")) || (getMLabelFont().equals("Default"))) { if (getMFieldOption() == 1) setMLabelFont(FontTable.mapFontToClassName("Required")); else if (getMFieldOption() == 2) setMLabelFont(FontTable.mapFontToClassName("Optional")); else if (getMFieldOption() == 3) setMLabelFont(FontTable.mapFontToClassName("System")); else setMLabelFont(FontTable.mapFontToClassName("Default"));  } else setMLabelFont(FontTable.mapFontToClassName(getMLabelFont())); setMDataFont(FontTable.mapFontToClassName(getMDataFont())); if (getMAccess() == 0L) setMAccess(2L);  } 
/* 1 */   protected void handleProperty(int arg0, Value arg1) throws DisplayPropertyMappers.BadDisplayPropertyException { if (arg0 == 24) { setMLabelColour(propToHTMLColour(arg1)); } else if (arg0 == 21) { setMLabelARBox(propToBox(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_LABEL_BBOX: " + getMLabelARBox()); } else if (arg0 == 151) { setMDataARBox(propToBox(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_BBOX: " + getMDataARBox()); } else if (arg0 == 66) { setMExpandARBox(propToBox(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_EXPAND_BBOX: " + getMExpandARBox()); } else if (arg0 == 65) { setMMenuARBox(propToBox(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_MENU_BBOX: " + getMMenuARBox()); } else if (arg0 == 90) { setMLabelJustify(propToInt(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_JUSTIFY: " + getMLabelJustify()); } else if (arg0 == 91) { setMLabelAlign(propToInt(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_ALIGN: " + getMLabelAlign()); } else if (arg0 == 27) { setMPosSector(propToInt(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_LABEL_POS_SECTOR: " + getMPosSector()); } else if (arg0 == 60) { setMRows(propToLong(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_ROWS: " + getMRows()); } else if (arg0 == 61) { setMCols(propToLong(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_COLS: " + getMCols()); } else if (arg0 == 5) { setMAccess(propToInt(arg1)); } else if (arg0 == 240) { setMTextOnly(propToBool(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DISPLAY_AS_TEXT_ONLY: " + isMTextOnly()); } else if (arg0 == 22) { setMLabelFont(propToString(arg1)); } else if (arg0 == 81) { setMDataFont(propToString(arg1)); } else if (arg0 == 228) { setMDisableChange(propToBool(arg1)); MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_APPLY_DIRTY: " + isMDisableChange()); } else if (arg0 == 288) { setMHighlight(propToBool(arg1)); } else if (arg0 == 289) { setMHighlightStartColor(propToHTMLColour(arg1)); } else if (arg0 == 290) { setMHighlightEndColor(propToHTMLColour(arg1)); } else { super.handleProperty(arg0, arg1); }  } 
/* 1 */   public String getDisplayTitleForField() { if (FormContext.get().IsVoiceAccessibleUser()) { if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null)) return getTitleForFieldWithMenuAndExpandBox(); if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0)) return getTitleForFieldWithMenu(); if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null)) return getTitleForFieldWithExpandBox();  } return null; } 
/* 1 */   protected String getTitleForFieldWithMenuAndExpandBox() { assert (FormContext.get().IsVoiceAccessibleUser()); return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu and expand"); } 
/* 1 */   protected String getTitleForFieldWithExpandBox() { assert (FormContext.get().IsVoiceAccessibleUser()); return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with expand"); } 
/* 1 */   protected String getTitleForFieldWithMenu() { assert (FormContext.get().IsVoiceAccessibleUser()); return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu"); } 
/* 1 */   public String getForCodePrefix() { return "arid"; } 
/* 1 */   public String getDisplayTitleCodeForField() { if (FormContext.get().IsVoiceAccessibleUser()) { if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null)) return getTitleCodeForFieldWithMenuAndExpandBox(); if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0)) return getTitleCodeForFieldWithMenu(); if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null)) return getTitleCodeForFieldWithExpandBox();  } return ""; } 
/* 1 */   protected String getTitleCodeForFieldWithMenuAndExpandBox() { assert (FormContext.get().IsVoiceAccessibleUser()); return "ME"; } 
/* 1 */   protected String getTitleCodeForFieldWithExpandBox() { assert (FormContext.get().IsVoiceAccessibleUser()); return "E"; } 
/* 1 */   protected String getTitleCodeForFieldWithMenu() { assert (FormContext.get().IsVoiceAccessibleUser()); return "M"; } 
/* 1 */   public String getARType() { return getMDataTypeString(); } 
/* 1 */   public String getExpandBoxAltText() { if (getMAccess() == 3L) return getLocalizedDescriptionStringForWidget("Editor for {0}, unavailable"); return getLocalizedDescriptionStringForWidget("Editor for {0}"); } 
/* 1 */   public String getEditBoxClassesString() { return "edit"; } 
/* 1 */   public String getExpandBoxClassesString() { return "expand"; } 
/* 1 */   public String getFileUploadClassString() { return "fileupload"; } 
/* 1 */   public GoatImageButton getExpandButton() { return MDefaultExpandButton; } 
/* 1 */   public GoatImageButton getEditBoxButton() { return MEditButton; } 
/* 1 */   public GoatImageButton getFileUploadButton() { if (FormContext.get().IsVoiceAccessibleUser()) return null; return MDefaultFileUploadButton; } 
/* 1 */   public void addToOutputNotes(OutputNotes arg0, int arg1) { super.addToOutputNotes(arg0, arg1); } 
/* 1 */   protected void setDefaultDisplayProperties() { super.setDefaultDisplayProperties(); setMAccess(0L); setMDisableChange(false); } 
/* 1 */   public boolean hasSearchBarMenu() { return (isMInView()) && ((getMFieldID() == 178) || (getMFieldOption() != 4)); } 
/* 1 */   public String getSearchBarLabel() { String str = ""; if ((getMLabel() != null) && (getMLabel().length() > 0)) str = getMLabel(); if (str.trim().length() == 0) str = "" + getMFieldID(); return str; } 
/* 1 */   public boolean isDataField() { return true; } 
/* 1 */   public void setMHighlightEndColor(String arg0) { this.mHighlightEndColor = arg0; } 
/* 1 */   public String getMHighlightEndColor() { return this.mHighlightEndColor; } 
/* 1 */   public void setMHighlightStartColor(String arg0) { this.mHighlightStartColor = arg0; } 
/* 1 */   public String getMHighlightStartColor() { return this.mHighlightStartColor; } 
/* 1 */   public void setMDataFont(String arg0) { this.mDataFont = arg0; } 
/* 1 */   public String getMDataFont() { return this.mDataFont; } 
/* 1 */   public void setMLabelColour(String arg0) { this.mLabelColour = arg0; } 
/* 1 */   public String getMLabelColour() { return this.mLabelColour; } 
/* 1 */   public void setMLabelFont(String arg0) { this.mLabelFont = arg0; } 
/* 1 */   public String getMLabelFont() { return this.mLabelFont; } 
/* 1 */   private void setMLabelJustify(int arg0) { this.mLabelJustify = arg0; } 
/* 1 */   public int getMLabelJustify() { return this.mLabelJustify; } 
/* 1 */   private void setMLabelAlign(int arg0) { this.mLabelAlign = arg0; } 
/* 1 */   public int getMLabelAlign() { return this.mLabelAlign; } 
/* 1 */   private void setMPosSector(int arg0) { this.mPosSector = arg0; } 
/* 1 */   public int getMPosSector() { return this.mPosSector; } 
/* 1 */   public void setMCols(long arg0) { this.mCols = arg0; } 
/* 1 */   public long getMCols() { return this.mCols; } 
/* 1 */   public void setMRows(long arg0) { this.mRows = arg0; } 
/* 1 */   public long getMRows() { return this.mRows; } 
/* 1 */   public void setMAccess(long arg0) { this.mAccess = arg0; } 
/* 1 */   public long getMAccess() { return this.mAccess; } 
/* 1 */   public void setMDisableChange(boolean arg0) { this.mDisableChange = arg0; } 
/* 1 */   public boolean isMDisableChange() { return this.mDisableChange; } 
/* 1 */   public void setMTextOnly(boolean arg0) { this.mTextOnly = arg0; } 
/* 1 */   public boolean isMTextOnly() { return this.mTextOnly; } 
/* 1 */   public void setMHighlight(boolean arg0) { this.mHighlight = arg0; } 
/* 1 */   public boolean isMHighlight() { return this.mHighlight; } 
/* 1 */   public void setMMenuARBox(ARBox arg0) { this.mMenuARBox = arg0; } 
/* 1 */   public ARBox getMMenuARBox() { return this.mMenuARBox; } 
/* 1 */   public void setMExpandARBox(ARBox arg0) { this.mExpandARBox = arg0; } 
/* 1 */   public ARBox getMExpandARBox() { return this.mExpandARBox; } 
/* 1 */   public void setMDataARBox(ARBox arg0) { this.mDataARBox = arg0; } 
/* 1 */   public ARBox getMDataARBox() { return this.mDataARBox; } 
/* 1 */   public void setMLabelARBox(ARBox arg0) { this.mLabelARBox = arg0; } 
/* 1 */   public ARBox getMLabelARBox() { return this.mLabelARBox; }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.DataField
 * JD-Core Version:    0.6.1
 */