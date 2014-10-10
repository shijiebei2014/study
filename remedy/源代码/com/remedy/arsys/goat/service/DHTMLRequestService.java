/*   */ package com.remedy.arsys.goat.service;
/*   */ 
/*   */ import com.bmc.arsys.api.ARException;
/*   */ import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
/*   */ import com.bmc.arsys.api.DataType;
/*   */ import com.bmc.arsys.api.Entry;
/*   */ import com.bmc.arsys.api.OperandType;
/*   */ import com.bmc.arsys.api.QualifierInfo;
/*   */ import com.bmc.arsys.api.RelationalOperationInfo;
/*   */ import com.bmc.arsys.api.SortInfo;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.config.Configuration;
/*   */ import com.remedy.arsys.goat.ActiveLinkCollector;
/*   */ import com.remedy.arsys.goat.AppList;
/*   */ import com.remedy.arsys.goat.AppList.AppListSubset;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import com.remedy.arsys.goat.FormContext;
/*   */ import com.remedy.arsys.goat.Globule;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.GoatServerMessage;
/*   */ import com.remedy.arsys.goat.JavaScriptGenerationException;
/*   */ import com.remedy.arsys.goat.LocaleUtil;
/*   */ import com.remedy.arsys.goat.UserDataEmitter;
/*   */ import com.remedy.arsys.goat.aspects.IDHTMLServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.aspects.IFieldGraphServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.field.FieldGraph;
/*   */ import com.remedy.arsys.goat.intf.Constants;
/*   */ import com.remedy.arsys.goat.intf.service.IDHTMLService;
/*   */ import com.remedy.arsys.goat.intf.service.IFieldGraphService;
/*   */ import com.remedy.arsys.goat.intf.service.IRequestService;
/*   */ import com.remedy.arsys.session.Login;
/*   */ import com.remedy.arsys.share.CacheDirectiveController;
/*   */ import com.remedy.arsys.share.CompressionHelper;
/*   */ import com.remedy.arsys.share.GoatCacheManager;
/*   */ import com.remedy.arsys.share.UrlHelper;
/*   */ import com.remedy.arsys.stubs.ServerLogin;
/*   */ import com.remedy.arsys.stubs.SessionData;
/*   */ import com.remedy.arsys.support.BrowserType;
/*   */ import com.remedy.arsys.support.Validator;
/*   */ import java.io.IOException;
/*   */ import java.io.PrintWriter;
/*   */ import java.io.UnsupportedEncodingException;
/*   */ import java.net.URLDecoder;
/*   */ import java.util.ArrayList;
/*   */ import java.util.Date;
/*   */ import java.util.HashMap;
/*   */ import java.util.Iterator;
/*   */ import java.util.List;
/*   */ import java.util.Map;
/*   */ import java.util.Map.Entry;
/*   */ import java.util.Set;
/*   */ import java.util.StringTokenizer;
/*   */ import javax.servlet.RequestDispatcher;
/*   */ import javax.servlet.ServletException;
/*   */ import javax.servlet.http.HttpServletRequest;
/*   */ import javax.servlet.http.HttpServletResponse;
/*   */ 
/*   */ public class DHTMLRequestService
/*   */   implements IRequestService
/*   */ {
/* 1 */   private static int WAIT_CURSOR_MODE = ((Configuration)localObject).getWaitCursorMode(); private static boolean enableContentCompression = ((Configuration)localObject).getEnableContentCompression();
/*   */   private static int formHttpCacheType;
/* 1 */   private static int formSUDCacheType = DHTMLRequestService.formHttpCacheType = 2;
/*   */   private static final Map<String, String> REQUEST_PARAMS_MAP;
/*   */   private static final int HPL_ARRNAME_FID = 80005;
/*   */   private static final int HPL_TYPE_FID = 7;
/*   */   private static final int HPL_USER_FID = 80013;
/*   */   private static final int HPL_LOCALE_FID = 160;
/*   */   private static final int HPL_PRIORITY_FID = 80015;
/*   */   private static final int HPL_CONTENT1_GUID_FID = 80034;
/*   */   private static final int HPL_CONTENT2_GUID_FID = 80035;
/*   */   private static final int HPL_CONTENT4_GUID_FID = 80037;
/*   */   private static final int HPL_CONTENT5_GUID_FID = 80038;
/*   */   private static final int HPD_GUID_FID = 179;
/*   */   private static final int HPD_LOCALE_FID = 160;
/*   */   private static final int HPD_FORM_FID = 80002;
/*   */   private static final int HPD_VIEW_FID = 80004;
/*   */   private static final int[] HPL_FIELD_IDS;
/*   */   private static final int[] HPD_FIELD_IDS;
/*   */   private static final QualifierInfo HPLArrnameOpQual;
/*   */   private static final QualifierInfo HPLType1OpQual;
/*   */   private static final QualifierInfo HPLType0OpQual;
/*   */   private static final QualifierInfo HPLLocaleNullQual;
/*   */   private static final QualifierInfo HPDLocaleNullQual;
/*   */   private static final QualifierInfo HPDGUIDEmptyQual;
/*   */   private static final ArithmeticOrRelationalOperand HPL_USER_OP;
/*   */   private static final ArithmeticOrRelationalOperand HPL_LOCALE_OP;
/*   */   private static final ArithmeticOrRelationalOperand HPD_LOCALE_OP;
/*   */   private static final ArithmeticOrRelationalOperand HPD_GUID_OP;
/*   */   private static final ArrayList<SortInfo> HPL_SORTINFO;
/*   */   private static final ArrayList<SortInfo> HPD_SORTINFO;
/*   */   private IFieldGraphService fieldgraphService;
/*   */   private IDHTMLService dhtmlService;
/*   */ 
/*   */   public void setFieldGraphService(IFieldGraphService arg0)
/*   */   {
/* 1 */     this.fieldgraphService = arg0; } 
/* 1 */   public void setDhtmlService(IDHTMLService arg0) { this.dhtmlService = arg0; } 
/* 1 */   public void transmitHTML(FieldGraph arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws GoatException { GoatServerMessage[] arrayOfGoatServerMessage1 = Login.getEmbeddedServerMessages(arg1); GoatServerMessage[] arrayOfGoatServerMessage2 = arrayOfGoatServerMessage1; FieldGraph localFieldGraph = arg0; IDHTMLService localIDHTMLService = this.dhtmlService; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localIDHTMLService; arrayOfObject[2] = localFieldGraph; arrayOfObject[3] = arrayOfGoatServerMessage2; Globule localGlobule = IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$1$517e374f(localFieldGraph, arrayOfGoatServerMessage2, new DHTMLRequestService.AjcClosure1(arrayOfObject)); assert (localGlobule != null); localGlobule.transmit(arg1, arg2, true); if (GoatCacheManager.getForceSaveFlag()) GoatCacheManager.forceSave();  } 
/* 1 */   public void calcCHPViewFieldInfo(String arg0) throws GoatException { SessionData localSessionData = SessionData.get(); ServerLogin localServerLogin = localSessionData.getServerLogin(arg0); QualifierInfo localQualifierInfo3 = new QualifierInfo(1, HPLType1OpQual, new QualifierInfo(new RelationalOperationInfo(1, HPL_USER_OP, new ArithmeticOrRelationalOperand(new Value(localSessionData.getUserName()))))); QualifierInfo localQualifierInfo2 = new QualifierInfo(2, localQualifierInfo3, HPLType0OpQual); String str1 = LocaleUtil.mapToLocaleWithCountryCode(localSessionData.getLocale()); String[] arrayOfString = LocaleUtil.normalizeLocale(str1); ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = new ArithmeticOrRelationalOperand(new Value(str1)); ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = new ArithmeticOrRelationalOperand(new Value(arrayOfString[0])); QualifierInfo localQualifierInfo5 = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(1, HPL_LOCALE_OP, localArithmeticOrRelationalOperand1)), new QualifierInfo(new RelationalOperationInfo(1, HPL_LOCALE_OP, localArithmeticOrRelationalOperand2))); QualifierInfo localQualifierInfo4 = new QualifierInfo(2, localQualifierInfo5, HPLLocaleNullQual); QualifierInfo localQualifierInfo6 = new QualifierInfo(1, localQualifierInfo2, localQualifierInfo4); QualifierInfo localQualifierInfo1 = new QualifierInfo(1, HPLArrnameOpQual, localQualifierInfo6); String str2 = null; String str3 = null; String str4 = null; String str5 = null;
/*   */     try { List localList = localServerLogin.getListEntryObjects("AR System Home Page Layout", localQualifierInfo1, 0, 0, HPL_SORTINFO, HPL_FIELD_IDS, false, null); if (localList.size() > 0) { localObject = (Entry)localList.get(0); str2 = getStringFromEntry(80034, (Entry)localObject); str3 = getStringFromEntry(80035, (Entry)localObject); str4 = getStringFromEntry(80037, (Entry)localObject); str5 = getStringFromEntry(80038, (Entry)localObject); }  } catch (ARException localARException1) { localARException1.printStackTrace(); } QualifierInfo localQualifierInfo8 = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(1, HPD_LOCALE_OP, localArithmeticOrRelationalOperand1)), new QualifierInfo(new RelationalOperationInfo(1, HPD_LOCALE_OP, localArithmeticOrRelationalOperand2))); QualifierInfo localQualifierInfo7 = new QualifierInfo(2, localQualifierInfo8, HPDLocaleNullQual); HashMap localHashMap = new HashMap(4, 1.0F); Object localObject = getOBJForCHPContentGUID(arg0, str2, localQualifierInfo7, localServerLogin); if (localObject != null) localHashMap.put(Integer.valueOf(80028), localObject); localObject = getOBJForCHPContentGUID(arg0, str3, localQualifierInfo7, localServerLogin); if (localObject != null) localHashMap.put(Integer.valueOf(80058), localObject); localObject = getOBJForCHPContentGUID(arg0, str4, localQualifierInfo7, localServerLogin); if (localObject != null) localHashMap.put(Integer.valueOf(80043), localObject); localObject = getOBJForCHPContentGUID(arg0, str5, localQualifierInfo7, localServerLogin); if (localObject != null) localHashMap.put(Integer.valueOf(80073), localObject); localSessionData.setCHPContentOBJMap(localHashMap); } 
/* 1 */   public CHPContent getOBJForCHPContentGUID(String arg0, String arg1, QualifierInfo arg2, ServerLogin arg3) throws GoatException { String str1 = Constants.VF_DEFURL; CHPContent localCHPContent = null; if ((arg1 == null) || (arg1.length() == 0)) return null; QualifierInfo localQualifierInfo2 = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(1, HPD_GUID_OP, new ArithmeticOrRelationalOperand(new Value(arg1)))), HPDGUIDEmptyQual); QualifierInfo localQualifierInfo1 = new QualifierInfo(1, localQualifierInfo2, arg2); List localList = null; String str2 = null; String str3 = null;
/*   */     try { localList = arg3.getListEntryObjects("AR System Home Page Descriptor", localQualifierInfo1, 0, 0, HPD_SORTINFO, HPD_FIELD_IDS, false, null); if (localList.size() > 0) { Entry localEntry = (Entry)localList.get(0); str2 = getStringFromEntry(80002, localEntry); str3 = getStringFromEntry(80004, localEntry); if (str2 != null) { FormContext localFormContext = FormContext.get();
/*   */           Form localForm;
/*   */           try { localForm = Form.get(arg0, str2); } catch (GoatException localGoatException) { return null; } localGoatException = localForm.getViewInfoByInference(str3, true, false); String str4 = localGoatException.getLabel(); String str5 = localFormContext.getFieldGraphURLParam(arg3.getPermissionsKey(), localGoatException.getID(), localForm); localCHPContent = new CHPContent(arg0, str2, str4); }  }  } catch (ARException localARException1) {
/* 1 */       localARException1.printStackTrace(); } return localCHPContent; } 
/* 1 */   public static String getStringFromEntry(int arg0, Entry arg1) { if (arg1 == null) return null; Value localValue = (Value)arg1.get(Integer.valueOf(arg0)); if ((localValue != null) && (localValue.getDataType() == DataType.CHAR)) return (String)localValue.getValue(); return null; } 
/* 1 */   public void transmitJS(FieldGraph arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws GoatException { ActiveLinkCollector localActiveLinkCollector = null; FieldGraph localFieldGraph = arg0; IDHTMLService localIDHTMLService = this.dhtmlService; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localIDHTMLService; arrayOfObject[2] = localFieldGraph; arrayOfObject[3] = localActiveLinkCollector; Globule localGlobule = IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$2$72981592(localFieldGraph, localActiveLinkCollector, new DHTMLRequestService.AjcClosure3(arrayOfObject)); assert (localGlobule != null); localGlobule.transmit(arg1, arg2, true); if (GoatCacheManager.getForceSaveFlag()) GoatCacheManager.forceSave();  } 
/* 1 */   public void emitSystemSkins(FieldGraph arg0, HttpServletRequest arg1, HttpServletResponse arg2) throws GoatException { BrowserType localBrowserType = BrowserType.getTypeFromRequest(arg1); Globule localGlobule = this.dhtmlService.emitSystemSkins(arg0, localBrowserType); assert (localGlobule != null); localGlobule.transmit(arg1, arg2, true); if (GoatCacheManager.getForceSaveFlag()) GoatCacheManager.forceSave();  } 
/* 1 */   public void emitHelp(FieldGraph arg0, PrintWriter arg1) throws GoatException { this.dhtmlService.emitHelp(arg0, arg1); } 
/* 1 */   public void emitHTMLToFile(FieldGraph arg0, PrintWriter arg1) throws GoatException { Globule localGlobule = this.dhtmlService.getHTMLData(arg0); localGlobule.write(arg1); } 
/* 1 */   public void emitJSToFile(FieldGraph arg0, PrintWriter arg1) throws GoatException { ActiveLinkCollector localActiveLinkCollector = null; FieldGraph localFieldGraph = arg0; IDHTMLService localIDHTMLService = this.dhtmlService; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localIDHTMLService; arrayOfObject[2] = localFieldGraph; arrayOfObject[3] = localActiveLinkCollector; Globule localGlobule = IDHTMLServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IDHTMLServiceCacheAspect$2$72981592(localFieldGraph, localActiveLinkCollector, new DHTMLRequestService.AjcClosure5(arrayOfObject)); assert (localGlobule != null); localGlobule.write(arg1);
/*   */   }
/*   */ 
/*   */   public void requestDispatch(HttpServletRequest arg0, HttpServletResponse arg1, UrlHelper arg2)
/*   */     throws GoatException, IOException
/*   */   {
/* 1 */     Object localObject2;
/*   */     Object localObject3;
/*   */     Object localObject4;
/*   */     Object localObject5;
/*   */     String str2;
/* 1 */     if ((arg2.getFile().equals("")) || (arg2.getFile().equals("form.html"))) { Form localForm = Form.get(arg2.getServer(), arg2.getForm()); localObject2 = localForm.getViewInfoByInference(arg2.getView(), false, false); localObject3 = arg0.getRequestURI(); localObject4 = arg0.getQueryString(); localObject5 = UrlHelper.newUrlForCacheID((String)localObject3, (String)localObject4, localForm, (Form.ViewInfo)localObject2); if (localObject5 != null) { CacheDirectiveController.forceContentUpdate(arg0, arg1); arg1.sendRedirect(Validator.sanitizeCRandLF((String)localObject5)); return; } if ((WAIT_CURSOR_MODE == 1) || (WAIT_CURSOR_MODE == 4)) { String str1 = arg0.getParameter("wait"); if (!"0".equals(str1)) { arg0.setAttribute("cacheid", UrlHelper.getCacheID(localForm, (Form.ViewInfo)localObject2)); str2 = localObject4 == null ? null : UrlHelper.removeCacheID(Validator.URLParamsEscape((String)localObject4)); localObject6 = Validator.sanitizeCRandLF((String)localObject3); str2 = Validator.sanitizeCRandLF((str2 == null) || (str2.length() == 0) ? "" : str2); arg0.setAttribute("form_wait_returnBack", localObject6); List localList = parseQueryString(str2); arg0.setAttribute("form_wait_returnBack_params", localList); RequestDispatcher localRequestDispatcher = arg0.getRequestDispatcher("/shared/wait.jsp"); if (localRequestDispatcher != null) try { localRequestDispatcher.forward(arg0, arg1); return; } catch (ServletException localServletException) {
/*   */             }  }  } long l3 = System.currentTimeMillis(); Object localObject6 = FieldGraph.get((Form.ViewInfo)localObject2); transmitHTML((FieldGraph)localObject6, arg0, arg1); } else if (arg2.getFile().equals("form.js")) { try { long l1 = System.currentTimeMillis(); localObject3 = FieldGraph.get(arg2.getServer(), arg2.getForm(), arg2.getView()); transmitJS((FieldGraph)localObject3, arg0, arg1); } catch (GoatException localGoatException4) { throw new JavaScriptGenerationException(localGoatException4); } } else if (arg2.getFile().equals("ARSystemSkin.css")) { try { long l2 = System.currentTimeMillis(); localObject3 = FieldGraph.get(arg2.getServer(), arg2.getForm(), arg2.getView()); emitSystemSkins((FieldGraph)localObject3, arg0, arg1); } catch (GoatException localGoatException5) { throw new JavaScriptGenerationException(localGoatException5); }
/*   */ 
/*   */     }
/*   */     else
/*   */     {
/* 1 */       Object localObject1;
/* 1 */       if (arg2.getFile().equals("help.html")) { localObject1 = Form.get(arg2.getServer(), arg2.getForm(), true); localObject2 = ((Form)localObject1).getViewInfoByInference(arg2.getView(), false, false); localObject3 = UrlHelper.newUrlForCacheID(arg0.getRequestURI(), arg0.getQueryString(), (Form)localObject1, (Form.ViewInfo)localObject2); if (localObject3 != null) { CacheDirectiveController.forceContentUpdate(arg0, arg1); arg1.sendRedirect(Validator.sanitizeCRandLF((String)localObject3)); return; } arg1.setContentType("text/html; charset=UTF-8"); Object localObject7 = localObject2; IFieldGraphService localIFieldGraphService = this.fieldgraphService; Object[] arrayOfObject = new Object[3]; arrayOfObject[0] = this; arrayOfObject[1] = localIFieldGraphService; arrayOfObject[2] = localObject7; localObject4 = IFieldGraphServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldGraphServiceCacheAspect$1$c3e5eaec(localObject7, new DHTMLRequestService.AjcClosure7(arrayOfObject)); emitHelp((FieldGraph)localObject4, arg1.getWriter()); } else if (arg2.getFile().equals("uds.js")) { localObject1 = SessionData.get(); localObject2 = ((SessionData)localObject1).getStaticUserDataJS(); if (localObject2 == null) { localObject3 = null;
/*   */           try { localObject3 = new UserDataEmitter(arg2.getServer()); } catch (GoatException localGoatException6) { throw new JavaScriptGenerationException(localGoatException6); } localObject2 = new Globule(((UserDataEmitter)localObject3).getUserDataString(), "text/js;charset=UTF-8", System.currentTimeMillis(), formSUDCacheType); ((SessionData)localObject1).setStaticUserDataJS((Globule)localObject2); ((SessionData)localObject1).updateSessionDataIfModified(arg0); } ((Globule)localObject2).transmit(arg0, arg1); } else if (arg2.getFile().equals("udd.js")) { setCacheHeader(arg1, 0L, "text/js;charset=UTF-8"); localObject1 = BrowserType.getTypeFromRequest(arg0); if (localObject1 == BrowserType.MOZ) arg1.setHeader("Cache-Control", "no-cache,no-store"); localObject2 = Form.get(arg2.getServer(), arg2.getForm()); localObject3 = ((Form)localObject2).getViewInfoByInference(arg2.getView(), false, false); if (((Form)localObject2).getName().equals("AR System Customizable Home Page")) calcCHPViewFieldInfo(arg2.getServer()); PrintWriter localPrintWriter = null; if (!enableContentCompression) localPrintWriter = arg1.getWriter(); try { localObject5 = FieldGraph.get(arg2.getServer(), arg2.getForm(), arg2.getView()); UserDataEmitter localUserDataEmitter = new UserDataEmitter(((Form)localObject2).getServer(), (Form)localObject2, arg0.getParameter("w"), localPrintWriter, ((Form.ViewInfo)localObject3).getID(), (Form.ViewInfo)localObject3, ((FieldGraph)localObject5).getOutputNotes()); if ((enableContentCompression) && (localUserDataEmitter != null)) { str2 = localUserDataEmitter.getUserDataString(); if (str2 != null) CompressionHelper.compressContents(str2, arg0, arg1, false);  } SessionData.get().updateSessionDataIfModified(arg0); } catch (GoatException localGoatException7) { throw new JavaScriptGenerationException(localGoatException7); } } else if (arg2.getFile().equals("AppList.html")) { setCacheHeader(arg1, Configuration.getInstance().getFormHTMLJSExpiryInterval(), "text/html;charset=UTF-8"); localObject1 = null; if (!enableContentCompression) localObject1 = arg1.getWriter(); localObject2 = null; localObject3 = arg0.getParameter("mode"); if (localObject3 != null) { localObject2 = AppList.getAppList(AppList.AppListSubset.parseSubsetString(arg0.getParameter("AppListSubset")), Integer.parseInt((String)localObject3), Integer.parseInt(arg0.getParameter("mID"))); ((AppList)localObject2).emitFlyout((PrintWriter)localObject1, Integer.parseInt(arg0.getParameter("mID")), arg2.getServer(), arg2.getForm(), arg2.getView()); } else { localObject2 = AppList.getAppList(AppList.AppListSubset.parseSubsetString(arg0.getParameter("AppListSubset"))); if (arg0.getParameter("mID") != null) ((AppList)localObject2).emit((PrintWriter)localObject1, arg2.getServer(), arg2.getForm(), arg2.getView(), Integer.parseInt(arg0.getParameter("mID"))); else ((AppList)localObject2).emit((PrintWriter)localObject1, arg2.getServer(), arg2.getForm(), arg2.getView());  } if (enableContentCompression) CompressionHelper.compressContents(((AppList)localObject2).getAppListString(), arg0, arg1, true);  } else { arg1.sendError(404); }  }  } 
/* 1 */   private static void setCacheHeader(HttpServletResponse arg0, long arg1, String arg2) { if (arg1 > 0L) { arg0.addHeader("Cache-Control", "public,max-age=" + arg1); arg0.setDateHeader("Expires", System.currentTimeMillis() + arg1 * 1000L); arg0.setContentType(arg2); return; } arg0.setHeader("Cache-Control", "no-cache"); arg0.setHeader("Pragma", "no-cache"); arg0.setDateHeader("Last-Modified", new Date().getTime()); arg0.setContentType(arg2); } 
/* 1 */   private static final List<String[]> parseQueryString(String arg0) { ArrayList localArrayList = new ArrayList();
/*   */     StringTokenizer localStringTokenizer1;
/* 1 */     if ((arg0 != null) && (arg0.length() > 0)) for (localStringTokenizer1 = new StringTokenizer(arg0, "&"); localStringTokenizer1.hasMoreTokens(); ) { String str1 = localStringTokenizer1.nextToken(); if (str1.length() != 0) { StringTokenizer localStringTokenizer2 = new StringTokenizer(str1, "="); String str2 = null; String str3 = null; if (localStringTokenizer2.hasMoreTokens()) { str2 = localStringTokenizer2.nextToken();
/*   */             try { str2 = URLDecoder.decode(str2, "UTF-8"); } catch (UnsupportedEncodingException localUnsupportedEncodingException) { localUnsupportedEncodingException.printStackTrace(); } if (localStringTokenizer2.hasMoreTokens()) { str3 = localStringTokenizer2.nextToken();
/*   */               try { str3 = URLDecoder.decode(str3, "UTF-8"); } catch (UnsupportedEncodingException localUnsupportedEncodingException1) { localUnsupportedEncodingException1.printStackTrace(); }  } localArrayList.add(new String[] { str2, str3 }); } } } return localArrayList; } 
/* 1 */   public Map<String, String> getRequestParams() { return REQUEST_PARAMS_MAP; } 
/* 1 */   public String getRequestParamsString() { Set localSet = REQUEST_PARAMS_MAP.entrySet(); StringBuilder localStringBuilder = new StringBuilder(); int i = 1;
/*   */     Map.Entry localEntry;
/* 1 */     for (Iterator localIterator = localSet.iterator(); localIterator.hasNext(); localStringBuilder.append((String)localEntry.getKey()).append("=").append((String)localEntry.getValue())) { localEntry = (Map.Entry)localIterator.next(); if (i != 0) i = 0; else localStringBuilder.append("&");  } return localStringBuilder.toString(); } 
/* 1 */   public void setFormSUDCacheTime(int arg0) { formSUDCacheType = arg0; } 
/* 1 */   public void setFormHttpCacheTime(int arg0) { formHttpCacheType = arg0; } 
/* 1 */   static { WAIT_CURSOR_MODE = 2; enableContentCompression = true; formHttpCacheType = 0; formSUDCacheType = 0; REQUEST_PARAMS_MAP = new HashMap(); HPL_FIELD_IDS = new int[] { 80034, 80035, 80037, 80038 }; HPD_FIELD_IDS = new int[] { 80002, 80004 }; HPL_USER_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 80013); HPL_LOCALE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 160); HPD_LOCALE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 160); HPD_GUID_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 179); REQUEST_PARAMS_MAP.put("format", "html"); Object localObject = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 7); ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value()); SortInfo localSortInfo = new SortInfo(1, 1); HPLArrnameOpQual = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 80005), new ArithmeticOrRelationalOperand(new Value("Default")))); HPLType1OpQual = new QualifierInfo(new RelationalOperationInfo(1, (ArithmeticOrRelationalOperand)localObject, new ArithmeticOrRelationalOperand(new Value(1)))); HPLType0OpQual = new QualifierInfo(new RelationalOperationInfo(1, (ArithmeticOrRelationalOperand)localObject, new ArithmeticOrRelationalOperand(new Value(0)))); HPLLocaleNullQual = new QualifierInfo(new RelationalOperationInfo(1, HPL_LOCALE_OP, localArithmeticOrRelationalOperand)); HPL_SORTINFO = new ArrayList(4); HPL_SORTINFO.add(new SortInfo(7, 2)); HPL_SORTINFO.add(new SortInfo(80015, 1)); HPL_SORTINFO.add(new SortInfo(160, 2)); HPL_SORTINFO.add(localSortInfo); HPDLocaleNullQual = new QualifierInfo(new RelationalOperationInfo(1, HPD_LOCALE_OP, localArithmeticOrRelationalOperand)); HPDGUIDEmptyQual = new QualifierInfo(new RelationalOperationInfo(1, HPD_GUID_OP, new ArithmeticOrRelationalOperand(new Value(" ")))); HPD_SORTINFO = new ArrayList(2); HPD_SORTINFO.add(new SortInfo(160, 2)); HPD_SORTINFO.add(localSortInfo); localObject = Configuration.getInstance();
/*   */   }
/*   */ 
/*   */   public static class CHPContent
/*   */   {
/*   */     private String mServer;
/*   */     private String mForm;
/*   */     private String mViewLabel;
/*   */ 
/*   */     public CHPContent(String paramString1, String paramString2, String paramString3)
/*   */     {
/*   */       this.mServer = paramString1;
/*   */       this.mForm = paramString2;
/*   */       this.mViewLabel = paramString3;
/*   */     }
/*   */ 
/*   */     public String getServer()
/*   */     {
/*   */       return this.mServer;
/*   */     }
/*   */ 
/*   */     public String getForm()
/*   */     {
/*   */       return this.mForm;
/*   */     }
/*   */ 
/*   */     public String getViewLabel()
/*   */     {
/*   */       return this.mViewLabel;
/*   */     }
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.DHTMLRequestService
 * JD-Core Version:    0.6.1
 */