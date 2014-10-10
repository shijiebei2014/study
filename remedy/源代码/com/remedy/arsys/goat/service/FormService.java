/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.aspects.IFormServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.cache.sync.ServerSync;
/*   */ import com.remedy.arsys.goat.intf.service.IFormService;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import java.util.Date;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class FormService
/*   */   implements IFormService
/*   */ {
/*   */   public Form getForm(String arg0, String arg1)
/*   */     throws GoatException
/*   */   {
/* 1 */     boolean bool = false; String str1 = arg1; String str2 = arg0; FormService localFormService = this; Object[] arrayOfObject = new Object[5]; arrayOfObject[0] = this; arrayOfObject[1] = localFormService; arrayOfObject[2] = str2; arrayOfObject[3] = str1; arrayOfObject[4] = Conversions.booleanObject(bool); return IFormServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFormServiceCacheAspect$1$1c93230b(str2, str1, bool, new FormService.AjcClosure1(arrayOfObject)); } 
/* 1 */   public Form getForm(String arg0, String arg1, boolean arg2) throws GoatException { String str = Form.getCacheKey(arg0, arg1); Form.MPerformanceLog.fine("Form: Constructing for missing key " + str); long l1 = new Date().getTime(); Form localForm = new Form(arg0, arg1); if (arg2) localForm.initHelpText(); long l2 = new Date().getTime(); Form.MPerformanceLog.fine("Form: Construction for key " + str + " took " + (l2 - l1)); ServerSync.initFormObjectTimesIfNeeded(arg0); return localForm;
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.FormService
 * JD-Core Version:    0.6.1
 */