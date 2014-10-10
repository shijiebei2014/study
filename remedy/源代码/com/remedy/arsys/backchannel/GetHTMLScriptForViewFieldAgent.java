/*   */ package com.remedy.arsys.backchannel;
/*   */ 
/*   */ import com.remedy.arsys.goat.ActiveLinkCollector;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import com.remedy.arsys.goat.Globule;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.GoatServerMessage;
/*   */ import com.remedy.arsys.goat.aspects.IDHTMLServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.field.FieldGraph;
/*   */ import com.remedy.arsys.goat.intf.service.IDHTMLService;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import com.remedy.arsys.share.ServiceLocator;
/*   */ import java.util.ArrayList;
/*   */ 
/*   */ public class GetHTMLScriptForViewFieldAgent extends NDXRequest
/*   */ {
/*   */   protected String mServer;
/*   */   protected String mAppName;
/*   */   protected String mForm;
/*   */   protected String mView;
/*   */   protected boolean mJSGet;
/*   */   protected String mcacheid;
/*   */   protected Form.ViewInfo mViewInfo;
/*   */ 
/*   */   public GetHTMLScriptForViewFieldAgent(String paramString)
/*   */   {
/* 1 */     super(paramString); } 
/* 1 */   protected void mapProperties(ArrayList arg0) throws GoatException { MLog.fine("--> GetHTMLScriptForViewFieldAgent"); if (arg0.size() != 6) throw new GoatException("Wrong argument length, spoofed"); this.mServer = ((String)arg0.get(0)); MLog.fine("mServer=" + this.mServer); this.mAppName = ((String)arg0.get(1)); MLog.fine("mAppName=" + this.mAppName); this.mForm = ((String)arg0.get(2)); MLog.fine("mForm=" + this.mForm); this.mView = ((String)arg0.get(3)); this.mJSGet = argToBoolean((String)arg0.get(4)); MLog.fine("mView=" + this.mView); this.mcacheid = ((String)arg0.get(5)); MLog.fine("mcacheid=" + this.mcacheid); } 
/* 1 */   protected void process() throws GoatException { Form localForm = Form.get(this.mServer, this.mForm); Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(this.mView, false, false); FieldGraph localFieldGraph1 = FieldGraph.get(localViewInfo); IDHTMLService localIDHTMLService1 = (IDHTMLService)ServiceLocator.getInstance().getService("dhtmlService");
/*   */     Globule localGlobule;
/* 1 */     if (!this.mJSGet) { GoatServerMessage[] arrayOfGoatServerMessage = null; FieldGraph localFieldGraph2 = localFieldGraph1; IDHTMLService localIDHTMLService2 = localIDHTMLService1; Object[] arrayOfObject1 = new Object[4]; arrayOfObject1[0] = this; arrayOfObject1[1] = localIDHTMLService2; arrayOfObject1[2] = localFieldGraph2; arrayOfObject1[3] = arrayOfGoatServerMessage; localGlobule = IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$3$29757c3f(localFieldGraph2, arrayOfGoatServerMessage, new GetHTMLScriptForViewFieldAgent.AjcClosure1(arrayOfObject1)); } else { ActiveLinkCollector localActiveLinkCollector = null; FieldGraph localFieldGraph3 = localFieldGraph1; IDHTMLService localIDHTMLService3 = localIDHTMLService1; Object[] arrayOfObject2 = new Object[4]; arrayOfObject2[0] = this; arrayOfObject2[1] = localIDHTMLService3; arrayOfObject2[2] = localFieldGraph3; arrayOfObject2[3] = localActiveLinkCollector; localGlobule = IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$4$a01666a2(localFieldGraph3, localActiveLinkCollector, new GetHTMLScriptForViewFieldAgent.AjcClosure3(arrayOfObject2)); } setGlobuleObject(localGlobule);
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetHTMLScriptForViewFieldAgent
 * JD-Core Version:    0.6.1
 */