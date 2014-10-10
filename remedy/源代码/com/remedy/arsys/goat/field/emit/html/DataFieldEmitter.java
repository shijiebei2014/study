/*    */ package com.remedy.arsys.goat.field.emit.html;
/*    */ 
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.FormContext;
/*    */ import com.remedy.arsys.goat.TextDirStyleContext;
/*    */ import com.remedy.arsys.goat.aspects.skins.DataFieldAspect;
/*    */ import com.remedy.arsys.goat.field.CharField;
/*    */ import com.remedy.arsys.goat.field.DataField;
/*    */ import com.remedy.arsys.goat.field.FieldGraph.Node;
/*    */ import com.remedy.arsys.goat.field.GoatField;
/*    */ import com.remedy.arsys.goat.field.GoatImageButton;
/*    */ import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
/*    */ import com.remedy.arsys.share.HTMLWriter;
/*    */ import com.remedy.arsys.share.JSWriter;
/*    */ import org.aspectj.runtime.reflect.Factory;
/*    */ 
/*    */ public class DataFieldEmitter extends GoatFieldEmitter
/*    */ {
/*    */   private DataField dataField;
/*    */ 
/*    */   public DataFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
/*    */   {
/*  1 */     super(paramGoatField, paramIEmitterFactory); } 
/*  1 */   public void setGf(GoatField arg0) { super.setGf(arg0); setDataField((DataField)arg0); } 
/*  1 */   public void emitOpenMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { super.emitOpenMarkup(arg0, arg1); TextDirStyleContext localTextDirStyleContext = TextDirStyleContext.get(); Box localBox1 = getMARBox().toBox(); Box localBox2 = getMLabelARBox().toBox(); StringBuilder localStringBuilder1 = new StringBuilder(); StringBuilder localStringBuilder2 = new StringBuilder(); Box localBox3 = localBox2; if (((getMPosSector() & 0x2) != 0) && (getMLabelAlign() != 4)) localBox3.mW = localBox1.mW; arg1.openTag("div").attr("id", "WIN_0_" + getMFieldID()).attr("arid", getMFieldID()).attr("artype", getARType()).attr("ardbn", getMDBName()).attr("arlbox", localBox3.toList()); String str1 = emitFillAttsInMarkup(arg0, arg1); String str2 = emitFlowAttsInMarkup(arg0, arg1); if ((this.dataField instanceof CharField)) { CharField localCharField = (CharField)this.dataField; if ((localCharField.isMRichTextField()) && ((getMPosSector() & 0x2) != 0) && (getMPosSector() == 2)) arg1.attr("labelLoc", "top");  } if (isMDisableChange()) arg1.attr("ardcf", 1); if (isMHighlight()) { arg1.attr("hlight"); if ((getMHighlightStartColor() != null) && (!getMHighlightStartColor().equals("#000000"))) arg1.attr("hColorS", getMHighlightStartColor()); if ((getMHighlightEndColor() != null) && (!getMHighlightEndColor().equals("#000000"))) arg1.attr("hColorE", getMHighlightEndColor());  } localStringBuilder1.append("df "); if ((getMAccess() == 1L) && (!isMTextOnly())) localStringBuilder1.append(" dfro "); else if (getMAccess() == 3L) localStringBuilder1.append(" dfd "); localStringBuilder1.append(getSelectorClassNames()); arg1.attr("class", localStringBuilder1.toString() + " " + getARType()); assert (getMAccess() != 0L); if (!isMVisible()) localStringBuilder2.append("visibility:hidden;"); if (getMZOrder() != -1L) localStringBuilder2.append("z-index:" + getMZOrder() + ";"); localStringBuilder2.append(localBox1.toCSS(getMAlignment())); if (str1 != null) localStringBuilder2.append(str1); if (str2 != null) localStringBuilder2.append(str2); arg1.attr("style", localStringBuilder2.toString()); arg1.endTag(); boolean bool = isChildOfFlowLayout(arg0);
/*    */     Object localObject;
/*  1 */     if (getMLabelAlign() == 4) { arg1.openTag("span"); localObject = localBox2.toCSS() + (bool ? ";position:static;" : ";position:absolute;"); arg1.attr("style", (String)localObject); arg1.endTag(false); } localStringBuilder1 = new StringBuilder(); localStringBuilder2 = new StringBuilder(); arg1.openTag("label").attr("id", "label" + getMFieldID()); localStringBuilder1.append("label "); localStringBuilder1.append(getMLabelFont()); if ((bool) && (!localTextDirStyleContext.isRTL())) localStringBuilder1.append(" flbl "); if (getMFieldOption() == 4) localStringBuilder1.append(" do"); arg1.attr("class", localStringBuilder1.toString()); arg1.attr("for", getForCodePrefix() + "_WIN_0_" + getMFieldID()); if (getMLabelAlign() == 4) { localStringBuilder2.append("width:").append(localBox2.mW).append("px;left:0px;bottom:0px;"); } else { localObject = new StringBuilder(); ((StringBuilder)localObject).append(localBox2.toCSS()); if (localTextDirStyleContext.isRTL()) { String[] arrayOfString = ((StringBuilder)localObject).toString().split(";"); localObject = new StringBuilder(); for (int i = 0; i < arrayOfString.length; i++) if (!arrayOfString[i].contains("top:")) ((StringBuilder)localObject).append(arrayOfString[i]);  
/*    */       }
/*  1 */       localStringBuilder2.append(((StringBuilder)localObject).toString()); } if (getMLabelColour() != null) localStringBuilder2.append("color:").append(getMLabelColour()).append(";"); if (getMLabelJustify() == 2) localStringBuilder2.append("text-align:center;"); else if (getMLabelJustify() == 4) localStringBuilder2.append("text-align:right;"); if (getMLabelAlign() == 2) localStringBuilder2.append("line-height:" + (localBox2.mH + 1) + "px;"); arg1.attr("style", localStringBuilder2.toString()).endTag(false); if (getMLabel() != null) arg1.cdata(getMLabel()); arg1.closeTag("label", false); if (getMLabelAlign() == 4) arg1.closeTag("span", false);  } 
/*  1 */   protected void emitExpandBox(HTMLWriter arg0) { String str = getExpandBoxAltText(); GoatImageButton localGoatImageButton = getExpandButton(); if (localGoatImageButton != null) localGoatImageButton.emitMarkup(arg0, getMExpandARBox().toBox(), getExpandBoxClassesString(), str);  } 
/*  1 */   protected void emitMenuBox(HTMLWriter arg0) { emitMenuBox(arg0, getMMenuARBox().toBox(), false); } 
/*  1 */   protected void emitMenuBox(HTMLWriter arg0, Box arg1, boolean arg2) { String str = null; if (getMAccess() == 3L) str = getLocalizedDescriptionStringForWidget("Menu for {0},unavailable"); else str = getLocalizedDescriptionStringForWidget("Menu for {0}"); DataField.MMenuButton.emitMarkup(arg0, arg1, "menu", str, arg2); } 
/*  1 */   public void emitCloseMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { super.emitCloseMarkup(arg0, arg1); if ((this.dataField instanceof CharField)) { CharField localCharField = (CharField)this.dataField; if ((localCharField.isMAutoCompleteHideMenuButton()) && (localCharField.getMDisplayType() != 1)) setMMenuARBox(null);  } if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0)) emitMenuBox(arg1); if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0)) emitExpandBox(arg1); arg1.closeTag("div"); } 
/*  1 */   public void emitOpenHelp(HTMLWriter arg0) { String str = getHelpText(); if ((str == null) || (str.trim().length() == 0)) return; emitOpenHelpBase(arg0); StringBuilder localStringBuilder1 = new StringBuilder(); StringBuilder localStringBuilder2 = new StringBuilder(); localStringBuilder1.append("FieldName ").append(getMLabelFont()); if (getMLabelColour() != null) localStringBuilder2.append("color:").append(getMLabelColour()).append(";"); arg0.openTag("span").attr("class", localStringBuilder1.toString()).attr("style", localStringBuilder2.toString()).endTag(); if (getMLabel() != null) arg0.cdata(getMLabel()); else arg0.cdata(getMDBName()); arg0.closeTag("span"); arg0.openTag("span").attr("class", "HelpText").endTag(); arg0.append(str); arg0.closeTag("span"); } 
/*  1 */   public void emitCloseHelp(HTMLWriter arg0) { String str = getHelpText(); if ((str == null) || (str.trim().length() == 0)) return; emitCloseHelpBase(arg0); } 
/*  1 */   protected void emitScriptProperties(FieldGraph.Node arg0, JSWriter arg1) { super.emitScriptProperties(arg0, arg1); if ((isMDisableChange()) || ((!isMInView()) && (getMARBox() == null))) arg1.property("dcf", 1);  } 
/*  1 */   public void emitDefaults(FieldGraph.Node arg0, JSWriter arg1) { super.emitDefaults(arg0, arg1); arg1.property("v", isMVisible()); String str1 = getMLabel(); String str2 = getGf().getMAltText(); arg1.property("l", (str1 == null) || (str1.length() == 0) ? "" : FormContext.get().IsVoiceAccessibleUser() ? "" : (str2 != null) && (str2.length() > 0) ? str2 : str1); int i = arg0.getEmitMode(); if (i == 0) { arg1.property("a", getMAccess()); if (getMLabelFont() != null) arg1.property("f", getMLabelFont()); if (getMLabelColour() != null) arg1.property("c", getMLabelColour()); if (getMFieldOption() == 1) arg1.property("o", getMFieldOption());  }  } 
/*  1 */   private void setDataField(DataField arg0) { this.dataField = arg0; } 
/*  1 */   private DataField getDataField() { return this.dataField; } 
/*  1 */   protected ARBox getMLabelARBox() { return getDataField().getMLabelARBox(); } 
/*  1 */   protected int getMPosSector() { return getDataField().getMPosSector(); } 
/*  1 */   protected int getMLabelAlign() { return getDataField().getMLabelAlign(); } 
/*  1 */   protected String getARType() { return getDataField().getARType(); } 
/*  1 */   protected boolean isMDisableChange() { return getDataField().isMDisableChange(); } 
/*  1 */   protected boolean isMHighlight() { return getDataField().isMHighlight(); } 
/*  1 */   protected String getMHighlightStartColor() { return getDataField().getMHighlightStartColor(); } 
/*  1 */   protected String getMHighlightEndColor() { return getDataField().getMHighlightEndColor(); } 
/*  1 */   protected long getMAccess() { return getDataField().getMAccess(); } 
/*  1 */   protected boolean isMTextOnly() { return getDataField().isMTextOnly(); } 
/*  1 */   protected String getMLabelFont() { return getDataField().getMLabelFont(); } 
/*  1 */   protected String getForCodePrefix() { return getDataField().getForCodePrefix(); } 
/*  1 */   protected String getMLabelColour() { DataField localDataField = getDataField(); return getMLabelColour_aroundBody1$advice(this, localDataField, DataFieldAspect.aspectOf(), localDataField, null, ajc$tjp_0); } 
/*  1 */   protected int getMLabelJustify() { return getDataField().getMLabelJustify(); } 
/*  1 */   protected String getExpandBoxAltText() { return getDataField().getExpandBoxAltText(); } 
/*  1 */   protected GoatImageButton getExpandButton() { return getDataField().getExpandButton(); } 
/*  1 */   protected ARBox getMExpandARBox() { return getDataField().getMExpandARBox(); } 
/*  1 */   protected String getExpandBoxClassesString() { return getDataField().getExpandBoxClassesString(); } 
/*  1 */   protected ARBox getMMenuARBox() { return getDataField().getMMenuARBox(); } 
/*  1 */   protected void setMMenuARBox(ARBox arg0) { getDataField().setMMenuARBox(arg0); } 
/*  1 */   protected long getMRows() { return getDataField().getMRows(); } 
/*  1 */   protected String getMDataFont() { return getDataField().getMDataFont(); } 
/*  1 */   protected String getDisplayTitleCodeForField() { return getDataField().getDisplayTitleCodeForField(); } 
/*  1 */   protected String getDisplayTitleForField() { return getDataField().getDisplayTitleForField(); } 
/*  1 */   protected ARBox getMDataARBox() { return getDataField().getMDataARBox(); } 
/*  1 */   protected String getSearchBarLabel() { return getDataField().getSearchBarLabel(); } 
/*  1 */   static { Factory localFactory = new Factory("<Unknown>", Class.forName("com.remedy.arsys.goat.field.emit.html.DataFieldEmitter")); ajc$tjp_0 = localFactory.makeSJP("method-call", localFactory.makeMethodSig("1", "getMLabelColour", "com.remedy.arsys.goat.field.DataField", "", "", "", "java.lang.String"), 0);
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.DataFieldEmitter
 * JD-Core Version:    0.6.1
 */