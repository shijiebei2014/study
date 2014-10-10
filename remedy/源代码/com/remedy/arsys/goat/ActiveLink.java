/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.bmc.arsys.api.ActiveLinkAction;
/*   */ import com.bmc.arsys.api.Timestamp;
/*   */ import com.remedy.arsys.config.Configuration;
/*   */ import com.remedy.arsys.goat.action.ActionList;
/*   */ import com.remedy.arsys.goat.aspects.IActiveLinkServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.intf.service.IActiveLinkService;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import com.remedy.arsys.log.MeasureTime.Measurement;
/*   */ import com.remedy.arsys.share.Cache.Item;
/*   */ import com.remedy.arsys.share.JSWriter;
/*   */ import com.remedy.arsys.stubs.ServerLogin;
/*   */ import java.io.IOException;
/*   */ import java.io.ObjectInputStream;
/*   */ import java.util.ArrayList;
/*   */ import java.util.List;
/*   */ 
/*   */ public class ActiveLink
/*   */   implements ActiveLinkInfo, FormAware, Cloneable, Cache.Item
/*   */ {
/*   */   private static final long serialVersionUID = 3852102311640103119L;
/*   */   private static final long DISABLED_VAL = 0L;
/*   */   public static final String ARAL_FUNCTION_NAME = "ARAL";
/*   */   private final ExecutionEvent mExecutionEvent;
/*   */   private Qualifier mQualifier;
/*   */   private ActionList mTrueActions;
/*   */   private ActionList mFalseActions;
/*   */   private List<ActiveLinkAction> mTrueActionsList;
/*   */   private List<ActiveLinkAction> mFalseActionsList;
/*   */   private final long mExecutionOrder;
/*   */   private final boolean mEnabled;
/*   */   private final String mName;
/*   */   private String mNameAlias;
/*   */   private final String mServer;
/*   */   private final String mForm;
/*   */   private final String mView;
/*   */   private String mLastUpdateTime;
/*   */   private final long mLastUpdateTimeMs;
/*   */   private List<String> mFormList;
/* 1 */   private static boolean mWorkflowAliases = Configuration.getInstance().getWorkflowAliases(); private static boolean mWorkflowProfiling = Configuration.getInstance().getWorkflowProfiling(); private static boolean mWorkflowVerifyImode = Configuration.getInstance().getWorkflowVerifyImode(); private static transient Log MLog = Log.get(7); private static transient Log MPerformanceLog = Log.get(8); private static transient Log cacheLog = Log.get(1);
/*   */   private static IActiveLinkService ACTIVE_LINK_SERVICE;
/*   */ 
/*   */   public static final IActiveLinkService setActiveLinkService(IActiveLinkService arg0)
/*   */   {
/* 1 */     ACTIVE_LINK_SERVICE = arg0; return arg0; } 
/* 1 */   public ActiveLink(String paramString1, com.bmc.arsys.api.ActiveLink paramActiveLink, String paramString2, String paramString3) throws GoatException { this.mForm = paramString2; this.mView = paramString3; this.mName = paramActiveLink.getKey(); MPerformanceLog.fine("Processing active link " + this.mName); this.mNameAlias = null; this.mServer = paramString1; this.mExecutionOrder = paramActiveLink.getOrder(); this.mExecutionEvent = new ExecutionEvent(paramActiveLink); this.mQualifier = new Qualifier(paramActiveLink.getQualifier(), paramString1); this.mQualifier.simplify(); this.mEnabled = paramActiveLink.isEnable(); Timestamp localTimestamp = paramActiveLink.getLastUpdateTime(); this.mLastUpdateTime = localTimestamp.toString(); this.mLastUpdateTimeMs = localTimestamp.getValue(); this.mTrueActionsList = paramActiveLink.getActionList(); this.mFalseActionsList = paramActiveLink.getElseList(); this.mFormList = paramActiveLink.getFormList(); } 
/* 1 */   public int getSize() { return 1; } 
/* 1 */   protected ActiveLink(ActiveLink paramActiveLink, String paramString1, String paramString2) throws GoatException { this.mServer = paramActiveLink.mServer; this.mForm = paramString1; this.mView = paramString2; this.mName = paramActiveLink.mName; this.mNameAlias = paramActiveLink.mNameAlias; this.mExecutionEvent = paramActiveLink.mExecutionEvent; this.mExecutionOrder = paramActiveLink.mExecutionOrder; this.mEnabled = paramActiveLink.mEnabled; this.mLastUpdateTimeMs = paramActiveLink.mLastUpdateTimeMs; if (paramActiveLink.mFormList != null) { this.mFormList = new ArrayList(); this.mFormList.addAll(paramActiveLink.mFormList); } try { this.mQualifier = ((Qualifier)paramActiveLink.mQualifier.clone()); this.mTrueActions = ((ActionList)paramActiveLink.getTrueActions().clone()); this.mFalseActions = ((ActionList)paramActiveLink.getFalseActions().clone()); } catch (CloneNotSupportedException localCloneNotSupportedException) { throw new GoatException("ActiveLink cloned failed", localCloneNotSupportedException); }  } 
/* 1 */   protected ActiveLink(ActiveLink paramActiveLink, String paramString1, String paramString2, String paramString3) throws GoatException { this.mServer = paramActiveLink.mServer; this.mForm = paramString2; this.mView = paramString3; paramActiveLink.mName += System.currentTimeMillis(); this.mNameAlias = null; this.mExecutionEvent = paramActiveLink.mExecutionEvent; this.mExecutionOrder = paramActiveLink.mExecutionOrder; this.mEnabled = paramActiveLink.mEnabled; this.mLastUpdateTimeMs = paramActiveLink.mLastUpdateTimeMs; if (paramActiveLink.mFormList != null) { this.mFormList = new ArrayList(); this.mFormList.addAll(paramActiveLink.mFormList); } try { this.mQualifier = ((Qualifier)paramActiveLink.mQualifier.clone()); this.mTrueActions = paramActiveLink.getTrueActions().cloneForEPStartAL(this.mServer, paramString1); this.mFalseActions = paramActiveLink.getFalseActions().cloneForEPStartAL(this.mServer, paramString1); } catch (CloneNotSupportedException localCloneNotSupportedException) { throw new GoatException("ActiveLink cloned failed", localCloneNotSupportedException); }  } 
/* 1 */   public static final String getCacheKey(String arg0, String arg1) { return (arg0.toLowerCase() + "/" + arg1).intern(); } 
/* 1 */   public static final ActiveLink get(String arg0, String arg1, String arg2, String arg3) throws GoatException { String str1 = arg3; String str2 = arg2; String str3 = arg1; String str4 = arg0; IActiveLinkService localIActiveLinkService = ACTIVE_LINK_SERVICE; Object[] arrayOfObject = new Object[5]; arrayOfObject[0] = localIActiveLinkService; arrayOfObject[1] = str4; arrayOfObject[2] = str3; arrayOfObject[3] = str2; arrayOfObject[4] = str1; return IActiveLinkServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IActiveLinkServiceCacheAspect$1$dc6ea5b9(str4, str3, str2, str1, new ActiveLink.AjcClosure1(arrayOfObject)); } 
/* 1 */   public static ActiveLink getBound(String arg0, String arg1, String arg2, String arg3, CachedFieldMap arg4) throws GoatException { ActiveLink localActiveLink = new ActiveLink(get(arg0, arg3, arg1, arg2), arg1, arg2); localActiveLink.bindToForm(arg4); return localActiveLink; } 
/* 1 */   public static ActiveLink getAsEntryPointStartingAL(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws GoatException { ActiveLink localActiveLink = new ActiveLink(get(arg0, arg1, arg5, arg6), arg4, arg5, arg6); CachedFieldMap localCachedFieldMap = Form.get(arg2, arg3).getCachedFieldMap(); MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(5); localActiveLink.bindToForm(localCachedFieldMap); localMeasurement.end(); return localActiveLink; } 
/* 1 */   public void bindToForm(CachedFieldMap arg0) throws GoatException { getQualifier().bindToForm(arg0); getTrueActions().bindToForm(arg0); getTrueActions().simplify(); getFalseActions().bindToForm(arg0); getFalseActions().simplify(); } 
/* 1 */   public static List<ActiveLink> bulkLoad(ServerLogin arg0, String arg1, String[] arg2, String arg3, String arg4) throws GoatException { String str1 = arg0.getServer(); String str2 = arg4; String str3 = arg3; String[] arrayOfString = arg2; String str4 = arg1; String str5 = str1; IActiveLinkService localIActiveLinkService = ACTIVE_LINK_SERVICE; Object[] arrayOfObject = new Object[6]; arrayOfObject[0] = localIActiveLinkService; arrayOfObject[1] = str5; arrayOfObject[2] = str4; arrayOfObject[3] = arrayOfString; arrayOfObject[4] = str3; arrayOfObject[5] = str2; return IActiveLinkServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IActiveLinkServiceCacheAspect$2$ac2d72a0(str5, str4, arrayOfString, str3, str2, new ActiveLink.AjcClosure3(arrayOfObject)); } 
/* 1 */   public String getName() { return this.mName; } 
/* 1 */   public String getServer() { return this.mServer; } 
/* 1 */   public ExecutionEvent getEvent() { return this.mExecutionEvent; } 
/* 1 */   public void setNameAlias(String arg0) { this.mNameAlias = arg0; } 
/* 1 */   public Qualifier getQualifier() { return this.mQualifier; } 
/* 1 */   public ActionList getTrueActions() { if ((this.mTrueActions == null) && (this.mTrueActionsList != null)) { this.mTrueActions = new ActionList(this, this.mTrueActionsList, true); this.mTrueActions.simplify(); this.mTrueActionsList = null; } return this.mTrueActions; } 
/* 1 */   public ActionList getFalseActions() { if ((this.mFalseActions == null) && (this.mFalseActionsList != null)) { this.mFalseActions = new ActionList(this, this.mFalseActionsList, false); this.mFalseActions.simplify(); this.mFalseActionsList = null; } return this.mFalseActions; } 
/* 1 */   public boolean hasGoto() { return (getTrueActions().hasGoto()) || (getFalseActions().hasGoto()); } 
/* 1 */   public boolean canTerminate() { return (getTrueActions().canTerminate()) || (getFalseActions().canTerminate()); } 
/* 1 */   public boolean isInterruptible() { return (getTrueActions().isInterruptible()) || (getFalseActions().isInterruptible()); } 
/* 1 */   public long getExecutionOrder() { return this.mExecutionOrder; } 
/* 1 */   public boolean isEnabled() { return this.mEnabled; } 
/* 1 */   public String getLastUpdateTime() { return this.mLastUpdateTime; } 
/* 1 */   public String getJSFunctionName() { if ((this.mNameAlias != null) && (mWorkflowAliases)) return JSWriter.makeJSFunctionName("ARAL", this.mNameAlias, ""); return JSWriter.makeJSFunctionName("ARAL", this.mName, ""); } 
/* 1 */   public void emitJS(Emitter arg0) throws GoatException { JSWriter localJSWriter = arg0.js(); localJSWriter.comment("name = " + this.mName + " order = " + this.mExecutionOrder); localJSWriter.comment("Event = " + this.mExecutionEvent.toString()); localJSWriter.comment("Flags = " + (hasGoto() ? "G" : " ") + (canTerminate() ? "T" : " ") + (isInterruptible() ? "I" : " ") + (isEnabled() ? " " : "D")); localJSWriter.startThisFunction(getJSFunctionName(), "windowID"); if (mWorkflowProfiling) { localJSWriter.startStatement("var prof").endStatement(); localJSWriter.startStatement("try"); localJSWriter.startBlock(); } if ((mWorkflowVerifyImode) && (isInterruptible())) localJSWriter.statement("VerifyImode(arguments.caller ? arguments.caller : null, arguments.callee ? arguments.callee : null)"); if (FormContext.get().getWorkflowLogging()) localJSWriter.startStatement("ActiveLinkLogWriteExt(windowID, \"ActiveLink Start:- ").appends(this.mName).append("\")").endStatement(); if (this.mQualifier.getExpression() != null) { localJSWriter.comment("Qualification: " + this.mQualifier.toString()); localJSWriter.comment("Qualification: " + this.mQualifier.emitAR()); localJSWriter.startStatement("if "); this.mQualifier.emitJS(arg0); localJSWriter.startBlock(); } if (getTrueActions() != null) { if (mWorkflowProfiling) localJSWriter.startStatement("prof = Profile_Start(").appendqs(this.mName + "(true)").continueStatement(", 2)").endStatement(); if (FormContext.get().getWorkflowLogging()) localJSWriter.statement("LogPath(windowID, \"True actions:\")"); localJSWriter.comment("True action: " + this.mTrueActions.toString()); this.mTrueActions.emitJS(arg0); } if (this.mQualifier.getExpression() != null) { if (((getFalseActions() != null) && (!getFalseActions().isEmpty())) || (FormContext.get().getWorkflowLogging())) { localJSWriter.endBlock(); localJSWriter.continueStatement(" else"); localJSWriter.startBlock(); if (mWorkflowProfiling) localJSWriter.startStatement("prof = Profile_Start(").appendqs(this.mName + "(false)").continueStatement(", 2)").endStatement(); if (FormContext.get().getWorkflowLogging()) localJSWriter.statement("LogPath(windowID, \"False actions:\")"); if (this.mFalseActions != null) { localJSWriter.comment("False action: " + this.mFalseActions.toString()); this.mFalseActions.emitJS(arg0); }  } localJSWriter.endBlock(); } localJSWriter.endStatement(); if (mWorkflowProfiling) { localJSWriter.endBlock(); localJSWriter.startStatement("finally"); localJSWriter.startBlock(); localJSWriter.statement("Profile_Stop(prof)"); localJSWriter.endBlock().endStatement(); } localJSWriter.endFunction(); } 
/* 1 */   public void addToOutputNotes(OutputNotes arg0) { this.mExecutionEvent.addToOutputNotes(arg0); this.mQualifier.addToOutputNotes(arg0); getTrueActions().addToOutputNotes(arg0); getFalseActions().addToOutputNotes(arg0); } 
/* 1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { arg0.defaultReadObject(); } 
/* 1 */   public long getLastUpdateTimeMs() { return this.mLastUpdateTimeMs; } 
/* 1 */   public List<String> getFormList() { return this.mFormList; } 
/* 1 */   public String getForm() { return this.mForm; } 
/* 1 */   public String getView() { return this.mView; }
/*   */ 
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ActiveLink
 * JD-Core Version:    0.6.1
 */