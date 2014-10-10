/*    */ package com.remedy.arsys.goat.field.emit.html;
/*    */ 
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.FormContext;
/*    */ import com.remedy.arsys.goat.aspects.skins.ViewFieldAspect;
/*    */ import com.remedy.arsys.goat.field.FieldGraph.Node;
/*    */ import com.remedy.arsys.goat.field.GoatField;
/*    */ import com.remedy.arsys.goat.field.ViewField;
/*    */ import com.remedy.arsys.goat.intf.Constants;
/*    */ import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
/*    */ import com.remedy.arsys.share.HTMLWriter;
/*    */ import com.remedy.arsys.share.JSWriter;
/*    */ import org.aspectj.runtime.reflect.Factory;
/*    */ 
/*    */ public class ViewFieldEmitter extends GoatFieldEmitter
/*    */ {
/*    */   private ViewField viewField;
/*    */ 
/*    */   public ViewFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
/*    */   {
/*  1 */     super(paramGoatField, paramIEmitterFactory); } 
/*  1 */   public void setGf(GoatField arg0) { super.setGf(arg0); setViewField((ViewField)arg0); } 
/*  1 */   private void setViewField(ViewField arg0) { this.viewField = arg0; } 
/*  1 */   private ViewField getViewField() { return this.viewField; } 
/*  1 */   public void emitOpenMarkup(FieldGraph.Node arg0, HTMLWriter arg1, int arg2) { if (arg2 == 1) super.emitOpenMarkup(arg0, arg1); else emitMarkup(arg0, arg1);  } 
/*  1 */   public void emitOpenMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { emitMarkup(arg0, arg1); } 
/*  1 */   private void emitIFrameContent(FieldGraph.Node arg0, HTMLWriter arg1) { arg1.openTag("iframe"); if (getMARBox() != null) arg1.attr("style", getMARBox().toBox().wholeChildBox().toCSS(getMAlignment()) + "background-color: transparent;"); arg1.attr("name", "VF" + getMFieldID()); arg1.attr("frameborder", Integer.parseInt(getMBorder())); arg1.attr("scrolling", getMScrollbars()); arg1.attr("allowtransparency", "true"); arg1.attr("arviewbordercolor", getMViewBorderColor()); String str = getMLabel(); if ((str == null) || ((str != null) && (str.trim().length() <= 0))) str = getMDBName(); if (FormContext.get().IsVoiceAccessibleUser()) { if ((getMAltText() != null) && (getMAltText().trim().length() > 0)) arg1.attr("title", getMAltText()); else arg1.attr("title", str);  } else arg1.attr("title", str); arg1.attrnoesc("src", Constants.VF_DEFURL); arg1.attr("onload", "DVFol()"); arg1.endTag(); arg1.closeTag("iframe"); } 
/*  1 */   private void emitMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { super.emitOpenMarkup(arg0, arg1); int i = getMFieldID(); arg1.openTag("div").attr("id", "WIN_0_" + i).attr("arid", i).attr("artype", getMDataTypeString()).attr("ardbn", getMDBName()); String str1 = emitFillAttsInMarkup(arg0, arg1); String str2 = emitFlowAttsInMarkup(arg0, arg1); StringBuffer localStringBuffer = new StringBuffer(); if (isChildOfFlowLayout(arg0)) localStringBuffer.append("VFlow "); if (getParentFillStyle(arg0) == 2) localStringBuffer.append("FillWidth "); localStringBuffer.append(getSelectorClassNames()); arg1.attr("class", localStringBuffer.toString()); if (isMDisableChange()) arg1.attr("ardcf", 1); StringBuilder localStringBuilder = new StringBuilder(); if (getMARBox() != null) localStringBuilder.append(getMARBox().toBox().toCSS(getMAlignment())); if (getMZOrder() != -1L) localStringBuilder.append("z-index:").append(getMZOrder()).append(';'); if (!isMVisible()) localStringBuilder.append("visibility:hidden;background-color:transparent;"); else localStringBuilder.append("background-color:transparent;"); if (str1 != null) localStringBuilder.append(str1); if (str2 != null) localStringBuilder.append(str2); arg1.attr("STYLE", localStringBuilder.toString()); HTMLWriter localHTMLWriter = new HTMLWriter(new StringBuilder(262144)); emitIFrameContent(arg0, localHTMLWriter); arg1.attr("arvfframe", localHTMLWriter.toString()); arg1.endTag(); } 
/*  1 */   public void emitCloseMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { super.emitCloseMarkup(arg0, arg1); arg1.closeTag("div"); } 
/*  1 */   public void emitDefaults(FieldGraph.Node arg0, JSWriter arg1) { super.emitDefaults(arg0, arg1); arg1.property("v", isMVisible()); if ((getMDefaultValue() != null) && (!getMDefaultValue().equals(""))) arg1.property("d", getMDefaultValue());  } 
/*  1 */   protected boolean isMDisableChange() { return getViewField().isMDisableChange(); } 
/*  1 */   protected String getMDefaultValue() { return getViewField().getMDefaultValue(); } 
/*  1 */   protected String getMBorder() { return getViewField().getMBorder(); } 
/*  1 */   protected String getMScrollbars() { return getViewField().getMScrollbars(); } 
/*  1 */   protected String getMAltText() { return getViewField().getMAltText(); } 
/*  1 */   protected String getMViewBorderColor() { ViewField localViewField = getViewField(); return getMViewBorderColor_aroundBody1$advice(this, localViewField, ViewFieldAspect.aspectOf(), localViewField, null, ajc$tjp_0); } 
/*  1 */   static { Factory localFactory = new Factory("<Unknown>", Class.forName("com.remedy.arsys.goat.field.emit.html.ViewFieldEmitter")); }
/*    */ 
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.ViewFieldEmitter
 * JD-Core Version:    0.6.1
 */