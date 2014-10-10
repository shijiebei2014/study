/*    */ package com.remedy.arsys.goat.field.emit.html;
/*    */ 
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.FormContext;
/*    */ import com.remedy.arsys.goat.aspects.skins.AttachmentFieldAspect;
/*    */ import com.remedy.arsys.goat.field.AttachmentField;
/*    */ import com.remedy.arsys.goat.field.AttachmentPoolField;
/*    */ import com.remedy.arsys.goat.field.FieldGraph.Node;
/*    */ import com.remedy.arsys.goat.field.GoatField;
/*    */ import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
/*    */ import com.remedy.arsys.share.HTMLWriter;
/*    */ import com.remedy.arsys.share.JSWriter;
/*    */ import org.aspectj.runtime.reflect.Factory;
/*    */ 
/*    */ public class AttachmentPoolFieldEmitter extends GoatFieldEmitter
/*    */ {
/*    */   private AttachmentPoolField attachmentPoolField;
/*    */ 
/*    */   public AttachmentPoolFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
/*    */   {
/*  1 */     super(paramGoatField, paramIEmitterFactory); } 
/*  1 */   public void setGf(GoatField arg0) { super.setGf(arg0); setAttachmentPoolField((AttachmentPoolField)arg0); } 
/*  1 */   private void setAttachmentPoolField(AttachmentPoolField arg0) { this.attachmentPoolField = arg0; } 
/*  1 */   private AttachmentPoolField getAttachmentPoolField() { return this.attachmentPoolField; } 
/*  1 */   public void emitOpenMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { super.emitOpenMarkup(arg0, arg1); arg1.openTag("div").attr("id", "WIN_0_" + getMFieldID()).attr("arid", getMFieldID()).attr("artype", getMDataTypeString()).attr("ardbn", getMDBName()); if (getParentFillStyle(arg0) == 2) arg1.attr("class", getSelectorClassNames() + " FillWidth"); else arg1.attr("class", getSelectorClassNames()); String str1 = emitFillAttsInMarkup(arg0, arg1); String str2 = emitFlowAttsInMarkup(arg0, arg1); StringBuilder localStringBuilder = new StringBuilder(); Box localBox = getMARBox().toBox(); localBox = localBox.getFixedVersionForBrokenARSystemTableFieldDimensions(); localStringBuilder.append(localBox.toCSS(getMAlignment())); if (arg0.getState() == 2) localStringBuilder.append("visibility:hidden;display:none;z-index:1;left:10000px;"); if (!isMVisible()) localStringBuilder.append("visibility:hidden;"); if (getMZOrder() != -1L) localStringBuilder.append("z-index:" + getMZOrder() + ";"); if (str1 != null) localStringBuilder.append(str1); if (str2 != null) localStringBuilder.append(str2); arg1.attr("style", localStringBuilder.toString()); FieldGraph.Node[] arrayOfNode = arg0.getChildNodes(); if (arrayOfNode != null) { AttachmentField localAttachmentField = null; JSWriter localJSWriter = new JSWriter(); localJSWriter.openList(); for (int i = 0; i < arrayOfNode.length; i++) if ((arrayOfNode[i].mField instanceof AttachmentField)) { localAttachmentField = (AttachmentField)arrayOfNode[i].mField; if (localAttachmentField.isMVisible()) { localJSWriter.listSep().openObj(); localJSWriter.property("f", localAttachmentField.getMFieldID()); localJSWriter.property("l", localAttachmentField.getMLabel()); localJSWriter.closeObj(); } } localJSWriter.closeList(); arg1.attr("arrows", localJSWriter.toString()); } arg1.attr("arcol0", getMFileNameTitle()).attr("arcol1", getMFileSizeTitle()).attr("arcol2", getMLabelTitle()); arg1.attr("arcolw", localBox.mW / 3); if ((getMAddStr() != null) && (getMAddStr().length() > 0)) arg1.attr("aradd", getMAddStr()); if ((getMDeleteStr() != null) && (getMDeleteStr().length() > 0)) arg1.attr("ardelete", getMDeleteStr()); if ((getMDisplayStr() != null) && (getMDisplayStr().length() > 0)) arg1.attr("ardisplay", getMDisplayStr()); if ((getMSaveStr() != null) && (getMSaveStr().length() > 0)) arg1.attr("arsave", getMSaveStr()); if ((getMDeselectStr() != null) && (getMDeselectStr().length() > 0)) arg1.attr("ardeselect", getMDeselectStr()); if (FormContext.get().IsVoiceAccessibleUser()) if ((getMAltText() != null) && (getMAltText().trim().length() > 0)) arg1.attr("arselectlabel", getMAltText()); else if ((getMLabel() != null) && (getMLabel().length() > 0)) arg1.attr("arselectlabel", getMLabel()); 
/*  1 */     if (getMColHdrGradBkgColor() != null) { arg1.attr("arcolgradbkgcol", getMColHdrGradBkgColor()); arg1.attr("arcolgradtype", getMColHdrGradtype()); } if (getMColHdrGradColor() != null) { arg1.attr("arcolgradcol", getMColHdrGradColor()); arg1.attr("arcolgradtype", getMColHdrGradtype()); } if ((getMColHdrTxtCol() != null) && (getMColHdrTxtCol().length() > 0)) arg1.attr("arcolhdrtxtcol", getMColHdrTxtCol()); if (getMHdrFtrGradColor() != null) { arg1.attr("arhdrftrgradcol", getMHdrFtrGradColor()); arg1.attr("arhdrftrgradtype", getMHdrFtrGradType()); } if (getMHdrFtrGradBkgColor() != null) { arg1.attr("arhdrftrgradbkgcol", getMHdrFtrGradBkgColor()); arg1.attr("arhdrftrgradtype", getMHdrFtrGradType()); } arg1.endTag(); } 
/*  1 */   public void emitCloseMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { super.emitCloseMarkup(arg0, arg1); arg1.closeTag("div"); } 
/*  1 */   public void emitDefaults(FieldGraph.Node arg0, JSWriter arg1) { super.emitDefaults(arg0, arg1); if (FormContext.get().IsVoiceAccessibleUser()) { if ((getMAltText() != null) && (getMAltText().trim().length() > 0)) arg1.property("l", getMAltText()); else arg1.property("l", getMLabel() == null ? "" : getMLabel());  } else arg1.property("l", getMLabel() == null ? "" : getMLabel()); arg1.property("v", isMVisible()); } 
/*  1 */   protected String getMFileSizeTitle() { return getAttachmentPoolField().getMFileSizeTitle(); } 
/*  1 */   protected String getMFileNameTitle() { return getAttachmentPoolField().getMFileNameTitle(); } 
/*  1 */   protected String getMLabelTitle() { return getAttachmentPoolField().getMLabelTitle(); } 
/*  1 */   protected String getMAddStr() { return getAttachmentPoolField().getMAddStr(); } 
/*  1 */   protected String getMDeleteStr() { return getAttachmentPoolField().getMDeleteStr(); } 
/*  1 */   protected String getMDisplayStr() { return getAttachmentPoolField().getMDisplayStr(); } 
/*  1 */   protected String getMSaveStr() { return getAttachmentPoolField().getMSaveStr(); } 
/*  1 */   protected String getMDeselectStr() { return getAttachmentPoolField().getMDeselectStr(); } 
/*  1 */   protected String getMAltText() { return getAttachmentPoolField().getMAltText(); } 
/*  1 */   public String getMColHdrTxtCol() { AttachmentPoolField localAttachmentPoolField = getAttachmentPoolField(); return getMColHdrTxtCol_aroundBody1$advice(this, localAttachmentPoolField, AttachmentFieldAspect.aspectOf(), localAttachmentPoolField, null, ajc$tjp_0); } 
/*  1 */   public String getMColHdrGradColor() { AttachmentPoolField localAttachmentPoolField = getAttachmentPoolField(); return getMColHdrGradColor_aroundBody3$advice(this, localAttachmentPoolField, AttachmentFieldAspect.aspectOf(), localAttachmentPoolField, null, ajc$tjp_1); } 
/*  1 */   public String getMColHdrGradBkgColor() { AttachmentPoolField localAttachmentPoolField = getAttachmentPoolField(); return getMColHdrGradBkgColor_aroundBody5$advice(this, localAttachmentPoolField, AttachmentFieldAspect.aspectOf(), localAttachmentPoolField, null, ajc$tjp_2); } 
/*  1 */   public long getMColHdrGradtype() { return getAttachmentPoolField().getMColHdrGradtype(); } 
/*  1 */   public String getMHdrFtrGradColor() { AttachmentPoolField localAttachmentPoolField = getAttachmentPoolField(); return getMHdrFtrGradColor_aroundBody7$advice(this, localAttachmentPoolField, AttachmentFieldAspect.aspectOf(), localAttachmentPoolField, null, ajc$tjp_3); } 
/*  1 */   public String getMHdrFtrGradBkgColor() { AttachmentPoolField localAttachmentPoolField = getAttachmentPoolField(); return getMHdrFtrGradBkgColor_aroundBody9$advice(this, localAttachmentPoolField, AttachmentFieldAspect.aspectOf(), localAttachmentPoolField, null, ajc$tjp_4); } 
/*  1 */   public long getMHdrFtrGradType() { return getAttachmentPoolField().getMHdrFtrGradType(); } 
/*  1 */   static { Factory localFactory = new Factory("<Unknown>", Class.forName("com.remedy.arsys.goat.field.emit.html.AttachmentPoolFieldEmitter")); }
/*    */ 
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.AttachmentPoolFieldEmitter
 * JD-Core Version:    0.6.1
 */