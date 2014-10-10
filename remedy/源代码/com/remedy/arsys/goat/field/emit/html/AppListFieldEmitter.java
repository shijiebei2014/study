/*    */ package com.remedy.arsys.goat.field.emit.html;
/*    */ 
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.FormContext;
/*    */ import com.remedy.arsys.goat.TextDirStyleContext;
/*    */ import com.remedy.arsys.goat.aspects.skins.AppListAspect;
/*    */ import com.remedy.arsys.goat.field.AppListField;
/*    */ import com.remedy.arsys.goat.field.FieldGraph.Node;
/*    */ import com.remedy.arsys.goat.field.GoatField;
/*    */ import com.remedy.arsys.goat.field.PageField;
/*    */ import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
/*    */ import com.remedy.arsys.share.HTMLWriter;
/*    */ import org.aspectj.runtime.reflect.Factory;
/*    */ 
/*    */ public class AppListFieldEmitter extends ViewFieldEmitter
/*    */ {
/*    */   private AppListField appListField;
/*    */ 
/*    */   public AppListFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
/*    */   {
/*  1 */     super(paramGoatField, paramIEmitterFactory); } 
/*  1 */   public void setGf(GoatField arg0) { super.setGf(arg0); setAppListField((AppListField)arg0); } 
/*  1 */   private void setAppListField(AppListField arg0) { this.appListField = arg0; } 
/*  1 */   private AppListField getAppListField() { return this.appListField; } 
/*  1 */   public void emitOpenMarkup(FieldGraph.Node arg0, HTMLWriter arg1) { int i = getAppMode(); if (FormContext.get().IsVoiceAccessibleUser()) i = 0; super.emitOpenMarkup(arg0, arg1, i); if (i == 1) { Box localBox = getMARBox().toBox(); StringBuilder localStringBuilder1 = new StringBuilder(); localStringBuilder1.append("FlyoutContainer Applist ").append(getSelectorClassNames()); arg1.openTag("div").attr("arid", getMFieldID()).attr("artype", getMDataTypeString()).attr("ardbn", getMDBName()); StringBuilder localStringBuilder2 = new StringBuilder(); if (!isMVisible()) localStringBuilder2.append("visibility:hidden;background-color:transparent;"); else localStringBuilder2.append("background-color:transparent;"); localStringBuilder2.append(localBox.toCSS(getMAlignment())); String str1 = emitFillAttsInMarkup(arg0, arg1); String str2 = emitFlowAttsInMarkup(arg0, arg1); arg1.attr("mode", i); arg1.attr("class", localStringBuilder1.toString()); arg1.attr("topbkcolor", getMTopLvlColor()); arg1.attr("lvlevenbkcolor", getMSbLvlEvenColor()); arg1.attr("lvloddbkcolor", getMSbLvlOddColor()); arg1.attr("oddlvltxtcolor", getMLTpLvlTxtColor()); arg1.attr("evenlvltxtcolor", getMSbLvlTxtColor()); if (str1 != null) { localStringBuilder2.append(str1); this.appListField.setMInFill(true); } else if ((getMParentFieldID() != 0) && ((arg0.mParent.mField instanceof PageField))) { arg1.attr("arparentid", getMParentFieldID()); } if (str2 != null) localStringBuilder2.append(str2); localStringBuilder2.append("z-index:999999;"); arg1.attr("style", localStringBuilder2.toString()).endTag(true); arg1.openTag("div"); if (TextDirStyleContext.get().isRTL()) arg1.attr("style", "height:100%;right:0;overflow:visible;width:" + (str1 == null ? localBox.mW - 2 + "px;" : "100%;")); else arg1.attr("style", "height:100%;left:0;overflow:visible;width:" + (str1 == null ? localBox.mW - 2 + "px;" : "100%;")); arg1.attr("class", "flyout").endTag(false); arg1.closeTag("div"); }  } 
/*  1 */   protected boolean isMDisableChange() { return getAppListField().isMDisableChange(); } 
/*  1 */   protected String getMDefaultValue() { return getAppListField().getMDefaultValue(); } 
/*  1 */   protected String getMBorder() { return getAppListField().getMBorder(); } 
/*  1 */   protected String getMScrollbars() { return getAppListField().getMScrollbars(); } 
/*  1 */   protected int getAppMode() { return getAppListField().getMAppMode(); } 
/*  1 */   public String getMTopLvlColor() { AppListField localAppListField = getAppListField(); return getMTopLvlColor_aroundBody1$advice(this, localAppListField, AppListAspect.aspectOf(), localAppListField, null, ajc$tjp_0); } 
/*  1 */   public String getMSbLvlEvenColor() { AppListField localAppListField = getAppListField(); return getMSbLvlEvenColor_aroundBody3$advice(this, localAppListField, AppListAspect.aspectOf(), localAppListField, null, ajc$tjp_1); } 
/*  1 */   public String getMSbLvlOddColor() { AppListField localAppListField = getAppListField(); return getMSbLvlOddColor_aroundBody5$advice(this, localAppListField, AppListAspect.aspectOf(), localAppListField, null, ajc$tjp_2); } 
/*  1 */   public String getMLTpLvlTxtColor() { AppListField localAppListField = getAppListField(); return getMLTpLvlTxtColor_aroundBody7$advice(this, localAppListField, AppListAspect.aspectOf(), localAppListField, null, ajc$tjp_3); } 
/*  1 */   public String getMSbLvlTxtColor() { AppListField localAppListField = getAppListField(); return getMSbLvlTxtColor_aroundBody9$advice(this, localAppListField, AppListAspect.aspectOf(), localAppListField, null, ajc$tjp_4); } 
/*  1 */   static { Factory localFactory = new Factory("<Unknown>", Class.forName("com.remedy.arsys.goat.field.emit.html.AppListFieldEmitter")); }
/*    */ 
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.AppListFieldEmitter
 * JD-Core Version:    0.6.1
 */