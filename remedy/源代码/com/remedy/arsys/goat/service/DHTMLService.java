/*    */ package com.remedy.arsys.goat.service;
/*    */ 
/*    */ import com.remedy.arsys.goat.ActiveLinkCollector;
/*    */ import com.remedy.arsys.goat.Form;
/*    */ import com.remedy.arsys.goat.Form.ViewInfo;
/*    */ import com.remedy.arsys.goat.Globule;
/*    */ import com.remedy.arsys.goat.GoatException;
/*    */ import com.remedy.arsys.goat.GoatServerMessage;
/*    */ import com.remedy.arsys.goat.JavaScriptGenerationException;
/*    */ import com.remedy.arsys.goat.OutputNotes;
/*    */ import com.remedy.arsys.goat.TemplateCollector;
/*    */ import com.remedy.arsys.goat.UserDataEmitter;
/*    */ import com.remedy.arsys.goat.aspects.IDHTMLServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.aspects.IFieldGraphServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.field.FieldGraph;
/*    */ import com.remedy.arsys.goat.intf.service.IDHTMLService;
/*    */ import com.remedy.arsys.goat.intf.service.IFieldGraphService;
/*    */ import com.remedy.arsys.goat.intf.service.IRequestService;
/*    */ import com.remedy.arsys.goat.intf.service.ISkinDefinitionService;
/*    */ import com.remedy.arsys.log.MeasureTime.Measurement;
/*    */ import com.remedy.arsys.share.JSWriter;
/*    */ import com.remedy.arsys.support.BrowserType;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ public class DHTMLService
/*    */   implements IDHTMLService
/*    */ {
/*    */   private IRequestService requestService;
/*    */   private IFieldGraphService fieldgraphService;
/*  1 */   private int mExpiryType = 1;
/*    */   private ISkinDefinitionService skinService;
/*    */ 
/*    */   public void setRequestService(IRequestService arg0)
/*    */   {
/*  1 */     this.requestService = arg0; } 
/*  1 */   public void setFieldGraphService(IFieldGraphService arg0) { this.fieldgraphService = arg0; } 
/*  1 */   public Globule getHTMLData(FieldGraph arg0) throws GoatException { GoatServerMessage[] arrayOfGoatServerMessage = null; FieldGraph localFieldGraph = arg0; DHTMLService localDHTMLService = this; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localDHTMLService; arrayOfObject[2] = localFieldGraph; arrayOfObject[3] = arrayOfGoatServerMessage; return IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$1$517e374f(localFieldGraph, arrayOfGoatServerMessage, new DHTMLService.AjcClosure1(arrayOfObject)); } 
/*  1 */   public void setSkinService(ISkinDefinitionService arg0) { this.skinService = arg0; } 
/*  1 */   public synchronized Globule getHTMLData(FieldGraph arg0, GoatServerMessage[] arg1) throws GoatException { MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(27);
/*    */     int i;
/*  1 */     if (arg1 != null) i = 1; else i = this.mExpiryType; FieldGraph localFieldGraph = arg0; IFieldGraphService localIFieldGraphService = this.fieldgraphService; instantiateFields_aroundBody3$advice(this, localIFieldGraphService, localFieldGraph, IFieldGraphServiceCacheAspect.aspectOf(), localFieldGraph, null); DHTMLBuilder localDHTMLBuilder = new DHTMLBuilder(this.requestService, this.fieldgraphService, arg0, this.skinService); String str = localDHTMLBuilder.genHTML(arg0.getOutputNotes(), arg1); Globule localGlobule = new Globule(str, "text/html;charset=UTF-8", arg0.getInstantiationTime(), i); localMeasurement.end(); return localGlobule; } 
/*  1 */   public void preCache(FieldGraph arg0) throws GoatException { getHTMLData(arg0); ActiveLinkCollector localActiveLinkCollector = null; FieldGraph localFieldGraph = arg0; DHTMLService localDHTMLService = this; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localDHTMLService; arrayOfObject[2] = localFieldGraph; arrayOfObject[3] = localActiveLinkCollector; IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$2$72981592(localFieldGraph, localActiveLinkCollector, new DHTMLService.AjcClosure5(arrayOfObject)); } 
/*  1 */   public synchronized Globule getJSData(FieldGraph arg0, ActiveLinkCollector arg1) throws GoatException { MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(28); FieldGraph localFieldGraph = arg0; IFieldGraphService localIFieldGraphService = this.fieldgraphService; instantiateFields_aroundBody7$advice(this, localIFieldGraphService, localFieldGraph, IFieldGraphServiceCacheAspect.aspectOf(), localFieldGraph, null); if (arg1 == null) arg1 = new ActiveLinkCollector(arg0.getForm(), arg0.getViewInfo()); arg1.filterFormCollections(arg0.getOutputNotes()); TemplateCollector localTemplateCollector = new TemplateCollector(arg0.getForm(), arg0.getViewInfo(), arg0.getOutputNotes().getTemplates()); DHTMLBuilder localDHTMLBuilder = new DHTMLBuilder(this.requestService, this.fieldgraphService, arg0, this.skinService); String str = localDHTMLBuilder.genJS(arg1, localTemplateCollector, arg0.getOutputNotes()); Globule localGlobule = new Globule(str, "text/js; charset=UTF-8", arg0.getInstantiationTime(), this.mExpiryType); localMeasurement.end(); return localGlobule; } 
/*  1 */   public void emitHelp(FieldGraph arg0, PrintWriter arg1) throws GoatException { DHTMLBuilder localDHTMLBuilder = new DHTMLBuilder(this.requestService, this.fieldgraphService, arg0, this.skinService); localDHTMLBuilder.emitHelp(arg1); } 
/*  1 */   public void setFormHttpCacheTime(int arg0) { this.mExpiryType = arg0; } 
/*  1 */   public synchronized Globule getVFHTMLData(FieldGraph arg0, GoatServerMessage[] arg1) throws GoatException { MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(27);
/*    */     int i;
/*  1 */     if (arg1 != null) i = 1; else i = this.mExpiryType; FieldGraph localFieldGraph = arg0; IFieldGraphService localIFieldGraphService = this.fieldgraphService; instantiateFields_aroundBody9$advice(this, localIFieldGraphService, localFieldGraph, IFieldGraphServiceCacheAspect.aspectOf(), localFieldGraph, null); DHTMLBuilder localDHTMLBuilder = new DHTMLBuilder(this.requestService, this.fieldgraphService, arg0, this.skinService); String str = localDHTMLBuilder.genVFHTML(arg0.getOutputNotes(), arg1); Globule localGlobule = new Globule(str, "text/html;charset=UTF-8", arg0.getInstantiationTime(), i); localMeasurement.end(); return localGlobule; } 
/*  1 */   public synchronized Globule getVFJSData(FieldGraph arg0, ActiveLinkCollector arg1) throws GoatException { MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(28); FieldGraph localFieldGraph = arg0; IFieldGraphService localIFieldGraphService = this.fieldgraphService; instantiateFields_aroundBody11$advice(this, localIFieldGraphService, localFieldGraph, IFieldGraphServiceCacheAspect.aspectOf(), localFieldGraph, null); if (arg1 == null) arg1 = new ActiveLinkCollector(arg0.getForm(), arg0.getViewInfo()); arg1.filterFormCollections(arg0.getOutputNotes()); TemplateCollector localTemplateCollector = new TemplateCollector(arg0.getForm(), arg0.getViewInfo(), arg0.getOutputNotes().getTemplates()); DHTMLBuilder localDHTMLBuilder = new DHTMLBuilder(this.requestService, this.fieldgraphService, arg0, this.skinService); String str1 = localDHTMLBuilder.genJS(arg1, localTemplateCollector, arg0.getOutputNotes()); Form localForm = arg0.getForm(); Form.ViewInfo localViewInfo = arg0.getViewInfo(); String str2 = null;
/*    */     try { UserDataEmitter localUserDataEmitter = new UserDataEmitter(arg0.getServer(), localForm, null, null, localViewInfo.getID(), localViewInfo, arg0.getOutputNotes()); if (localUserDataEmitter != null) str2 = localUserDataEmitter.getUserDataString();  } catch (GoatException localGoatException1) { throw new JavaScriptGenerationException(localGoatException1); } JSWriter localJSWriter = new JSWriter(new StringBuilder(196608)); localJSWriter.append("this.result=").openObj().property("udd", str2).property("viewname", localViewInfo.getLabel()).property("js", str1).closeObj().append(";"); Globule localGlobule = new Globule(localJSWriter.toString(), "text/js; charset=UTF-8", arg0.getInstantiationTime(), this.mExpiryType); localMeasurement.end(); return localGlobule; } 
/*  1 */   public synchronized Globule emitSystemSkins(FieldGraph arg0, BrowserType arg1) throws GoatException { MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(28); DHTMLBuilder localDHTMLBuilder = new DHTMLBuilder(this.requestService, this.fieldgraphService, arg0, this.skinService); String str = localDHTMLBuilder.emitSystemSkins(arg1); Globule localGlobule = new Globule(str, "text/css; charset=UTF-8", arg0.getInstantiationTime(), this.mExpiryType); localMeasurement.end(); return localGlobule;
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.DHTMLService
 * JD-Core Version:    0.6.1
 */