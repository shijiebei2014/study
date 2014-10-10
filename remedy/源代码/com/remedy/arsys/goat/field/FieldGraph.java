/*    */ package com.remedy.arsys.goat.field;
/*    */ 
/*    */ import com.bmc.arsys.api.ARQualifierHelper;
/*    */ import com.bmc.arsys.api.CoreFieldId;
/*    */ import com.bmc.arsys.api.DataType;
/*    */ import com.bmc.arsys.api.EntryListFieldInfo;
/*    */ import com.bmc.arsys.api.Field;
/*    */ import com.bmc.arsys.api.SortInfo;
/*    */ import com.remedy.arsys.config.Configuration;
/*    */ import com.remedy.arsys.goat.ActiveLinkCollector;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.CachedFieldMap;
/*    */ import com.remedy.arsys.goat.DisplayPropertyMappers;
/*    */ import com.remedy.arsys.goat.FieldStubMap;
/*    */ import com.remedy.arsys.goat.Form.ViewInfo;
/*    */ import com.remedy.arsys.goat.FormContext;
/*    */ import com.remedy.arsys.goat.GoatException;
/*    */ import com.remedy.arsys.goat.OutputNotes;
/*    */ import com.remedy.arsys.goat.aspects.IFieldGraphServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.aspects.skins.ViewInfoAspect;
/*    */ import com.remedy.arsys.goat.cache.sync.relationshipManager;
/*    */ import com.remedy.arsys.goat.intf.service.IDHTMLService;
/*    */ import com.remedy.arsys.goat.intf.service.IFieldGraphService;
/*    */ import com.remedy.arsys.goat.intf.service.IFieldGraphVisitor;
/*    */ import com.remedy.arsys.goat.intf.service.IVisitorAccess;
/*    */ import com.remedy.arsys.log.Log;
/*    */ import com.remedy.arsys.prefetch.PrefetchWorker.Item;
/*    */ import com.remedy.arsys.share.ARQualifierHelper2;
/*    */ import com.remedy.arsys.share.Cache.Item;
/*    */ import com.remedy.arsys.share.GoatCacheManager;
/*    */ import com.remedy.arsys.share.GoatThread;
/*    */ import com.remedy.arsys.share.ServiceLocator;
/*    */ import com.remedy.arsys.stubs.GoatServlet;
/*    */ import com.remedy.arsys.stubs.ServerLogin;
/*    */ import com.remedy.arsys.stubs.SessionData;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Comparator;
/*    */ import java.util.Date;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.TreeSet;
/*    */ import java.util.concurrent.locks.Lock;
/*    */ import java.util.concurrent.locks.ReentrantLock;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ public final class FieldGraph extends DisplayPropertyMappers
/*    */   implements Cache.Item
/*    */ {
/*    */   private static final long serialVersionUID = -6414031172715798032L;
/*    */   private static long curMemSize;
/*    */   private static final long m100M = 104857600L;
/*    */   private static IFieldGraphService FIELDGRAPH_SERVICE;
/*    */   private Node mRoot;
/*    */   private Node mResultsList;
/*    */   private boolean mHaveAuthoredResultsList;
/*    */   private boolean mHaveAuthoredSearchBar;
/*    */   private boolean mHaveRTFField;
/*    */   private Map<Integer, Node> mNodeMap;
/*    */   private boolean mFieldsInstantiated;
/*    */   private boolean mFieldsInstantiating;
/*    */   private boolean mFieldsOnlyInstantiated;
/*    */   private long mInstantiationTime;
/*    */   private transient ARQualifierHelper mQualHelper;
/*    */   private final String mCKey;
/*    */   private transient com.remedy.arsys.goat.Form mForm;
/*    */   private transient Form.ViewInfo mViewInfo;
/*    */   private transient Lock arQualifierHelperLock;
/*    */   private OutputNotes mOutputNotes;
/*    */   private static final Node[] EmptyNode;
/*    */   public static boolean MWorkflowProfiling;
/*    */   private static final Comparator MDefaultChildSortComparator;
/*    */   private static transient Log mPerformanceLog;
/*    */   static EntryListFieldInfo[] MDefaultResultsListFields;
/*    */   static EntryListFieldInfo[] MDefaultResultsListFields2;
/*    */   private transient Map displayFieldMap;
/*    */   private String mhtmlVF;
/*    */   private String mhtmlFPVF;
/*    */   private String mjsVF;
/*    */   private String muddVF;
/*    */ 
/*    */   public static final IFieldGraphService setFieldGraphService(IFieldGraphService arg0)
/*    */   {
/*  1 */     FIELDGRAPH_SERVICE = arg0; return arg0; } 
/*  1 */   public static String getFieldGraphKey(Form.ViewInfo arg0) throws GoatException { com.remedy.arsys.goat.Form localForm = arg0.getContainingForm();
/*    */     String str1;
/*  1 */     if (FormContext.get().IsVoiceAccessibleUser()) str1 = "/A"; else str1 = "/N"; String str2 = localForm.getServerLogin().getPermissionsKey() + "|" + localForm.getServerFormNames() + "/" + arg0.getID() + str1; String str3 = str2.intern(); return str3; } 
/*  1 */   public static FieldGraph get(Form.ViewInfo arg0) throws GoatException { Form.ViewInfo localViewInfo = arg0; IFieldGraphService localIFieldGraphService = FIELDGRAPH_SERVICE; Object[] arrayOfObject = new Object[2]; arrayOfObject[0] = localIFieldGraphService; arrayOfObject[1] = localViewInfo; return IFieldGraphServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldGraphServiceCacheAspect$1$c3e5eaec(localViewInfo, new FieldGraph.AjcClosure1(arrayOfObject)); } 
/*  1 */   public static FieldGraph get(String arg0, String arg1, String arg2) throws GoatException { return FIELDGRAPH_SERVICE.get(arg0, arg1, arg2); } 
/*  1 */   public static FieldGraph get(String arg0, String arg1, int arg2) throws GoatException { return FIELDGRAPH_SERVICE.get(arg0, arg1, arg2); } 
/*  1 */   public FieldGraph(Form.ViewInfo paramViewInfo, String paramString) throws GoatException { this.mHaveAuthoredResultsList = false; this.mHaveAuthoredSearchBar = false; this.mFieldsInstantiated = false; this.mFieldsInstantiating = false; this.mFieldsOnlyInstantiated = false; this.arQualifierHelperLock = new ReentrantLock(); this.mhtmlVF = null; this.mhtmlFPVF = null; this.mjsVF = null; this.muddVF = null; mPerformanceLog.fine("FieldGraph: Constructing for missing key " + paramString); this.mViewInfo = paramViewInfo; this.mForm = paramViewInfo.getContainingForm(); this.mCKey = paramString; relationshipManager.getInstance().trackFormFieldGraphKey(paramViewInfo, paramString); } 
/*  1 */   public String getKey() { return this.mCKey; } 
/*  1 */   public static String getServerFormKey(String arg0, String arg1) { return (arg0 + "/" + arg1).intern(); } 
/*  1 */   public static String getServerFormKey(Form.ViewInfo arg0) { com.remedy.arsys.goat.Form localForm = arg0.getContainingForm(); return getServerFormKey(localForm.getServer(), localForm.getName()); } 
/*  1 */   public ARQualifierHelper GetQualifierHelper() throws GoatException { return GetQualifierHelperDetail(false); } 
/*    */   private ARQualifierHelper GetQualifierHelperDetail(boolean arg0) throws GoatException { try { this.arQualifierHelperLock.lock(); if ((this.mQualHelper != null) && (this.mQualHelper.getLocalFieldLabelMap().size() != 0) && (this.mQualHelper.getLocalFieldIdMap().size() != 0)) return this.mQualHelper; if ((this.mQualHelper != null) && (this.mQualHelper.getLocalFieldLabelMap().size() == 0)) { mPerformanceLog.warning("DEBUG:FieldGraph#GetQualifierHelperDetail-mLocalFieldLabelMap is empty"); mPerformanceLog.warning("DEBUG:FieldGraph#GetQualifierHelperDetail-form name= " + this.mForm.getName()); mPerformanceLog.warning("DEBUG:FieldGraph#GetQualifierHelperDetail-view name= " + this.mViewInfo.getName()); mPerformanceLog.warning("DEBUG:FieldGraph#GetQualifierHelperDetail- re-initializing new mQualHelper"); } this.mQualHelper = new ARQualifierHelper2(); Object localObject1 = new FieldStubMap(this.mForm.getServerName(), this.mForm.getName()); FieldStubMap localFieldStubMap = new FieldStubMap(this.mForm.getServerName(), this.mForm.getName()); Map localMap = GoatField.get(this.mViewInfo); CachedFieldMap localCachedFieldMap = this.mForm.getCachedFieldMap(); for (Iterator localIterator = localMap.values().iterator(); localIterator.hasNext(); ) { localObject2 = (GoatField)localIterator.next(); localField = (Field)localCachedFieldMap.get(Integer.valueOf(((GoatField)localObject2).getMFieldID())); if ((((GoatField)localObject2).isMInView()) && (((GoatField)localObject2).getLabel() != null) && (((GoatField)localObject2).getLabel().length() > 0) && (((GoatField)localObject2).isDataField())) ((Map)localObject1).put(((GoatField)localObject2).getQualifierFieldLabel(), localField); else if (((GoatField)localObject2).getMFieldID() == 15) ((Map)localObject1).put(((GoatField)localObject2).getQualifierFieldLabel(), localField); if (!(localObject2 instanceof ColumnField)) localFieldStubMap.put(Integer.valueOf(((GoatField)localObject2).getMFieldID()).toString(), localField);
/*    */       }
/*  1 */       Object localObject2;
/*    */       Field localField;
/*  1 */       for (localIterator = localMap.values().iterator(); localIterator.hasNext(); ((Map)localObject1).put(((GoatField)localObject2).getMDBName(), localField)) { localObject2 = (GoatField)localIterator.next(); if ((!((GoatField)localObject2).isMInView()) && (!((Map)localObject1).containsKey(((GoatField)localObject2).getMDBName()))) localField = (Field)localCachedFieldMap.get(Integer.valueOf(((GoatField)localObject2).getMFieldID()));  } this.mQualHelper.setLocalFieldLabelMap((Map)localObject1, this.mViewInfo.getID()); this.mQualHelper.setLocalFieldIdMap(localFieldStubMap); return this.mQualHelper; } finally { this.arQualifierHelperLock.unlock(); }  } 
/*  1 */   public synchronized boolean areFieldsInstantiated() { return this.mFieldsInstantiated; } 
/*  1 */   public void instantiateFields() throws GoatException { FieldGraph localFieldGraph = this; IFieldGraphService localIFieldGraphService = FIELDGRAPH_SERVICE; instantiateFields_aroundBody3$advice(this, localIFieldGraphService, localFieldGraph, IFieldGraphServiceCacheAspect.aspectOf(), localFieldGraph, null); } 
/*  1 */   public void instantiateFieldsImpl() throws GoatException { synchronized (this) { if (this.mFieldsInstantiated) return; assert (!this.mFieldsInstantiating); this.mFieldsInstantiating = true;
/*    */       try { long l1 = new Date().getTime(); this.mResultsList = null; this.mNodeMap = new HashMap(); this.mRoot = new Node(); this.mHaveRTFField = false; this.mOutputNotes = new OutputNotes(); ActiveLinkCollectorThread localActiveLinkCollectorThread = new ActiveLinkCollectorThread(); GoatThread.cloneThreadContext(localActiveLinkCollectorThread); localActiveLinkCollectorThread.start(); FieldGraphBuilder localFieldGraphBuilder = new FieldGraphBuilder(this.mViewInfo); localFieldGraphBuilder.addToOutputNotes(this.mOutputNotes);
/*    */         try { if (!this.mHaveAuthoredResultsList) addResultsListNode();  } catch (Exception localException) { Log.get(6).log(Level.SEVERE, "exception addResultsListNode", localException); } try { localActiveLinkCollectorThread.waitForAlCollectorDone(); if (localActiveLinkCollectorThread.getGoatEx() != null) throw localActiveLinkCollectorThread.getGoatEx();  } catch (InterruptedException localInterruptedException) { localInterruptedException.printStackTrace(); } this.mRoot.recurColourState(this.mOutputNotes); this.mRoot.recurAddToOutputNotes(this.mOutputNotes); if (this.mResultsList != null) this.mResultsList.recurAddToOutputNotes(this.mOutputNotes); localInterruptedException = this.mViewInfo.getDetailBox(); if (localInterruptedException != null) this.mOutputNotes.addDimensions(localInterruptedException.mW, localInterruptedException.mH); this.mInstantiationTime = new Date().getTime(); this.mFieldsInstantiated = true; long l2 = new Date().getTime(); mPerformanceLog.fine("FieldGraph: Populated fields/workflow for key " + this.mCKey + ", took " + (l2 - l1)); } finally { this.mFieldsInstantiating = false; }  } GetQualifierHelperDetail(true); } 
/*  1 */   public synchronized boolean areFieldsOnlyInstantiated() { return this.mFieldsOnlyInstantiated; } 
/*  1 */   private void instantiateFieldsOnly() throws GoatException { FieldGraph localFieldGraph = this; IFieldGraphService localIFieldGraphService = FIELDGRAPH_SERVICE; instantiateFieldsOnly_aroundBody5$advice(this, localIFieldGraphService, localFieldGraph, IFieldGraphServiceCacheAspect.aspectOf(), localFieldGraph, null); } 
/*  1 */   public void instantiateFieldsOnlyImpl() throws GoatException { synchronized (this) { if ((this.mFieldsInstantiated) || (this.mFieldsOnlyInstantiated)) return; this.mNodeMap = new HashMap(); this.mRoot = new Node(); new FieldGraphBuilder(this.mViewInfo, true); this.mFieldsOnlyInstantiated = true; }  } 
/*  1 */   public synchronized void addResultsListNode() throws GoatException { this.mResultsList = new Node(null, new TableField(this.mForm, this.mViewInfo), null); int i = 2147483647; EntryListFieldInfo[] arrayOfEntryListFieldInfo = (EntryListFieldInfo[])this.mForm.getSchema().getEntryListFieldInfo().toArray(new EntryListFieldInfo[0]); if ((arrayOfEntryListFieldInfo == null) || (arrayOfEntryListFieldInfo.length == 0)) if (this.mNodeMap.get(Integer.valueOf(MDefaultResultsListFields[0].getFieldId())) != null) arrayOfEntryListFieldInfo = MDefaultResultsListFields; else if (this.mNodeMap.get(Integer.valueOf(MDefaultResultsListFields2[0].getFieldId())) != null) arrayOfEntryListFieldInfo = MDefaultResultsListFields2;
/*  1 */     Object localObject;
/*  1 */     for (int j = 0; (arrayOfEntryListFieldInfo != null) && (j < arrayOfEntryListFieldInfo.length); j++) { while (this.mNodeMap.get(Integer.valueOf(i)) != null) i--; Node localNode = (Node)this.mNodeMap.get(Integer.valueOf(arrayOfEntryListFieldInfo[j].getFieldId())); if (localNode != null) { localObject = localNode.mField; new Node(this.mResultsList, new ColumnField(arrayOfEntryListFieldInfo[j], this.mResultsList.mField, this.mForm, i, (GoatField)localObject, j), null); i--; } else if (arrayOfEntryListFieldInfo[j].getFieldId() == ColumnField.getMWeightFieldID()) { new Node(this.mResultsList, new ColumnField(arrayOfEntryListFieldInfo[j], this.mResultsList.mField, this.mForm, i, null, j), null); }  } SortInfo[] arrayOfSortInfo = (SortInfo[])this.mForm.getSchema().getSortInfo().toArray(new SortInfo[0]); int k = 1; if (arrayOfSortInfo != null) { localObject = this.mResultsList.getChildNodes(); for (int m = 0; m < arrayOfSortInfo.length; m++) { int n = arrayOfSortInfo[m].getFieldID(); for (int i1 = 0; i1 < localObject.length; i1++) { ColumnField localColumnField = (ColumnField)localObject[i1].mField; if (localColumnField.getDataFieldID() == n) { localColumnField.setMSortSeq(k++); if (arrayOfSortInfo[m].getSortOrder() == 1) { localColumnField.setMSortDir(0); break; } localColumnField.setMSortDir(1); break; }  }  } }
/*  1 */     this.mResultsList.forceColourViewable(); this.mResultsList.mParent = this.mResultsList; } 
/*  1 */   public GoatField getField(int arg0) throws GoatException { instantiateFieldsOnly(); Node localNode = (Node)this.mNodeMap.get(Integer.valueOf(arg0)); if (localNode == null) return null; return localNode.mField; } 
/*  1 */   public Node getNode(int arg0) throws GoatException { instantiateFieldsOnly(); if (GoatCacheManager.getForceSaveFlag()) GoatCacheManager.forceSave(); return (Node)this.mNodeMap.get(Integer.valueOf(arg0)); } 
/*  1 */   public static void preCache(PrefetchWorker.Item arg0, boolean arg1, boolean arg2) throws GoatException { com.remedy.arsys.goat.Form localForm = com.remedy.arsys.goat.Form.get(arg0.getServer(), arg0.getSchema()); Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(arg0.getViewname(), false, false); FieldGraph localFieldGraph = get(localViewInfo); if (!arg1) { localFieldGraph.instantiateFields(); if (arg2) new ActiveLinkCollector(localForm, localViewInfo);  } else if (arg0.getViewname() != null) { IDHTMLService localIDHTMLService = (IDHTMLService)ServiceLocator.getInstance().getService("dhtmlService"); localIDHTMLService.preCache(localFieldGraph); } GoatCacheManager.backup(); } 
/*  1 */   public static void preCache(PrefetchWorker.Item arg0, boolean arg1, boolean arg2, HashMap<String, List<String>> arg3) throws GoatException { com.remedy.arsys.goat.Form localForm = com.remedy.arsys.goat.Form.get(arg0.getServer(), arg0.getSchema()); Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(arg0.getViewname(), false, false); FieldGraph localFieldGraph = get(localViewInfo); if (!arg1) { localFieldGraph.instantiateFields(); if (arg2) new ActiveLinkCollector(localForm, localViewInfo, arg3);  } else if (arg0.getViewname() != null) { IDHTMLService localIDHTMLService = (IDHTMLService)ServiceLocator.getInstance().getService("dhtmlService"); localIDHTMLService.preCache(localFieldGraph); } GoatCacheManager.backup(); } 
/*  1 */   private static String getLightViewInfoKey1(String arg0, String arg1) throws GoatException { ServerLogin localServerLogin = SessionData.get().getServerLogin(arg0); String str1 = arg0 + "/" + arg1; String str2 = localServerLogin.getPermissionsKey();
/*    */     String str3;
/*  1 */     if (FormContext.get().IsVoiceAccessibleUser()) str3 = "/A"; else str3 = "/N"; String str4 = str2 + "|" + str1 + "/" + str3; String str5 = str4.intern(); return str5; } 
/*  1 */   private static String getFieldGraphKey(String arg0, String arg1, int arg2) throws GoatException { ServerLogin localServerLogin = SessionData.get().getServerLogin(arg0); String str1 = arg0 + "/" + arg1; String str2 = localServerLogin.getPermissionsKey();
/*    */     String str3;
/*  1 */     if (FormContext.get().IsVoiceAccessibleUser()) str3 = "/A"; else str3 = "/N"; String str4 = str2 + "|" + str1 + "/" + arg2 + str3; String str5 = str4.intern(); return str5; } 
/*  1 */   public String getDetailColor() { Form.ViewInfo localViewInfo = this.mViewInfo; return getViewBGColor_aroundBody7$advice(this, localViewInfo, ViewInfoAspect.aspectOf(), localViewInfo, null); } 
/*  1 */   public String getMDetailColor() { return this.mViewInfo.mDetailColour; } 
/*  1 */   public boolean getResultsListBottom() { return (this.mViewInfo.mPaneLayout == -4) || (this.mViewInfo.mPaneLayout == -8); } 
/*  1 */   public int getSize() { return 1; } 
/*  1 */   public String getServer() { return this.mForm.getServer(); } 
/*  1 */   public com.remedy.arsys.goat.Form getForm() { return this.mForm; } 
/*  1 */   public OutputNotes getOutputNotes() { return this.mOutputNotes; } 
/*  1 */   public long getInstantiationTime() { return this.mInstantiationTime; } 
/*  1 */   public int getViewID() { return this.mViewInfo.getID(); } 
/*  1 */   public Form.ViewInfo getViewInfo() { return this.mViewInfo; } 
/*  1 */   public Node getResultsList() { return this.mResultsList; } 
/*  1 */   public Node getRoot() { return this.mRoot; } 
/*  1 */   public boolean hasAuthoredSearchBar() { return this.mHaveAuthoredSearchBar; } 
/*  1 */   public Map<Integer, Node> getNodeMap() { return this.mNodeMap; } 
/*  1 */   private String getKey(Form.ViewInfo arg0) throws GoatException { com.remedy.arsys.goat.Form localForm = arg0.getContainingForm();
/*    */     String str1;
/*  1 */     if (FormContext.get().IsVoiceAccessibleUser()) str1 = "/A"; else str1 = "/N"; String str2 = localForm.getServerLogin().getPermissionsKey() + "|" + localForm.getServerFormNames() + "/" + arg0.getID() + str1; String str3 = str2.intern(); return str3; } 
/*  1 */   private synchronized void writeObject(ObjectOutputStream arg0) throws IOException { arg0.defaultWriteObject(); String str1 = this.mForm.getServerName(); String str2 = this.mForm.getName(); int i = this.mViewInfo.mID; arg0.writeObject(str1); arg0.writeObject(str2); arg0.writeInt(i); } 
/*  1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { this.arQualifierHelperLock = new ReentrantLock(); arg0.defaultReadObject(); String str1 = (String)arg0.readObject(); String str2 = (String)arg0.readObject(); int i = arg0.readInt();
/*    */     try { this.mForm = com.remedy.arsys.goat.Form.get(str1, str2); this.mViewInfo = this.mForm.getViewInfo(i); } catch (GoatException localGoatException) {  } if (this.mNodeMap != null)
/*    */     {
/*  1 */       Node localNode;
/*  1 */       for (localGoatException = this.mNodeMap.keySet().iterator(); localGoatException.hasNext(); localNode.initNodeLinks()) { Object localObject = localGoatException.next(); localNode = (Node)this.mNodeMap.get(localObject); }  } if (this.mRoot != null) this.mRoot.initNodeLinks(); if (this.mResultsList != null) this.mResultsList.initNodeLinks();  } 
/*  1 */   public boolean isHaveRTFField() { return this.mHaveRTFField; } 
/*  1 */   public String getHTMLForVF() { return this.mhtmlVF; } 
/*  1 */   public void setHTMLForVF(String arg0) { this.mhtmlVF = arg0; } 
/*  1 */   public String getHTMLFPForVF() { return this.mhtmlFPVF; } 
/*  1 */   public void setHTMLFPForVF(String arg0) { this.mhtmlFPVF = arg0; } 
/*  1 */   public String getJSForVF() { return this.mjsVF; } 
/*  1 */   public void setJSForVF(String arg0) { this.mjsVF = arg0; } 
/*  1 */   public String getUDDForVF() { return this.muddVF; } 
/*  1 */   public void setUDDForVF(String arg0) { this.muddVF = arg0; } 
/*  1 */   static { curMemSize = 419430400L; EmptyNode = new Node[0]; MWorkflowProfiling = Configuration.getInstance().getWorkflowProfiling(); MDefaultChildSortComparator = new GoatField.DefaultChildSortComparator(); mPerformanceLog = Log.get(8); MDefaultResultsListFields = new EntryListFieldInfo[1]; MDefaultResultsListFields[0] = new EntryListFieldInfo(8, 200, "z"); MDefaultResultsListFields2 = new EntryListFieldInfo[1]; MDefaultResultsListFields2[0] = new EntryListFieldInfo(1, 200, "z");
/*    */   }
/*    */ 
/*    */   private final class ActiveLinkCollectorThread extends GoatThread
/*    */   {
/*    */     private GoatException goatEx;
/*    */     private boolean alCollectorDone = false;
/*    */ 
/*    */     public ActiveLinkCollectorThread()
/*    */     {
/*    */       setName("ALCollector/" + FieldGraph.this.mForm.getServer() + "/" + FieldGraph.this.mForm.getName() + "/" + FieldGraph.this.mViewInfo.getName());
/*    */     }
/*    */ 
/*    */     public synchronized boolean isAlCollectorDone()
/*    */     {
/*    */       return this.alCollectorDone;
/*    */     }
/*    */ 
/*    */     public synchronized void setAlCollectorDone(boolean paramBoolean)
/*    */     {
/*    */       this.alCollectorDone = paramBoolean;
/*    */       notifyAll();
/*    */     }
/*    */ 
/*    */     public synchronized void waitForAlCollectorDone()
/*    */       throws InterruptedException
/*    */     {
/*    */       while (!isAlCollectorDone())
/*    */         wait();
/*    */     }
/*    */ 
/*    */     public GoatException getGoatEx()
/*    */     {
/*    */       return this.goatEx;
/*    */     }
/*    */ 
/*    */     public void run()
/*    */     {
/*    */       try
/*    */       {
/*    */         super.run();
/*    */         try
/*    */         {
/*    */           ActiveLinkCollector localActiveLinkCollector = new ActiveLinkCollector(FieldGraph.this.mForm, FieldGraph.this.mViewInfo);
/*    */           localActiveLinkCollector.addToOutputNotes(FieldGraph.this.mOutputNotes);
/*    */         }
/*    */         finally
/*    */         {
/*    */           setAlCollectorDone(true);
/*    */           FormContext.dispose();
/*    */           GoatServlet.teardownSessionData();
/*    */         }
/*    */       }
/*    */       catch (GoatException localGoatException)
/*    */       {
/*    */         this.goatEx = localGoatException;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private final class FieldGraphBuilder
/*    */   {
/*    */     private final Form.ViewInfo viewInfo;
/*    */     private final boolean noDisplay;
/*    */     private int mItemsInTree = 0;
/*    */     private Map<Integer, GoatField> mFields = new HashMap();
/*    */     private final Set<Integer> mUniqueChildren = new HashSet();
/*    */ 
/*    */     FieldGraphBuilder(Form.ViewInfo arg2)
/*    */       throws GoatException
/*    */     {
/*    */       this(localViewInfo, false);
/*    */     }
/*    */ 
/*    */     FieldGraphBuilder(Form.ViewInfo paramBoolean, boolean arg3)
/*    */       throws GoatException
/*    */     {
/*    */       this.viewInfo = paramBoolean;
/*    */       boolean bool;
/*    */       this.noDisplay = bool;
/*    */       this.mFields = GoatField.get(paramBoolean, bool);
/*    */       GoatField localGoatField1;
/*    */       if (bool)
/*    */       {
/*    */         for (localIterator = this.mFields.values().iterator(); localIterator.hasNext(); this.mItemsInTree += 1)
/*    */         {
/*    */           localGoatField1 = (GoatField)localIterator.next();
/*    */           new FieldGraph.Node(FieldGraph.this, null, localGoatField1, bool, null);
/*    */           if (localGoatField1.amResultsList())
/*    */             FieldGraph.this.mHaveAuthoredResultsList = true;
/*    */         }
/*    */         return;
/*    */       }
/*    */       Iterator localIterator = this.mFields.values().iterator();
/*    */       while (localIterator.hasNext())
/*    */       {
/*    */         localGoatField1 = (GoatField)localIterator.next();
/*    */         Integer localInteger = Integer.valueOf(localGoatField1.getMParentFieldID());
/*    */         if ((localInteger.intValue() != 0) && (!this.mFields.containsKey(localInteger)))
/*    */         {
/*    */           GoatField localGoatField2 = (GoatField)localGoatField1.clone();
/*    */           if (localGoatField2 != null)
/*    */           {
/*    */             localGoatField2.brokenParent();
/*    */             this.mFields.put(Integer.valueOf(localGoatField1.getMFieldID()), localGoatField2);
/*    */           }
/*    */         }
/*    */         else
/*    */         {
/*    */           this.mUniqueChildren.add(localInteger);
/*    */         }
/*    */       }
/*    */       recurBuild(0, 0, FieldGraph.this.mRoot);
/*    */       assert (this.mItemsInTree == this.mFields.size());
/*    */     }
/*    */ 
/*    */     private void recurBuild(int paramInt1, int paramInt2, FieldGraph.Node paramNode)
/*    */     {
/*    */       assert (paramInt1 < 64);
/*    */       if (paramInt1 >= 64)
/*    */       {
/*    */         System.out.println("Messed up depth :(");
/*    */         return;
/*    */       }
/*    */       Comparator localComparator = paramNode.mField != null ? paramNode.mField.getChildSortComparator() : FieldGraph.MDefaultChildSortComparator;
/*    */       TreeSet localTreeSet = new TreeSet(localComparator);
/*    */       Iterator localIterator = this.mFields.values().iterator();
/*    */       GoatField localGoatField;
/*    */       while (localIterator.hasNext())
/*    */       {
/*    */         localGoatField = (GoatField)localIterator.next();
/*    */         if (localGoatField.getMParentFieldID() == paramInt2)
/*    */           localTreeSet.add(localGoatField);
/*    */       }
/*    */       localIterator = localTreeSet.iterator();
/*    */       while (localIterator.hasNext())
/*    */       {
/*    */         localGoatField = (GoatField)localIterator.next();
/*    */         FieldGraph.Node localNode = new FieldGraph.Node(FieldGraph.this, paramNode, localGoatField, null);
/*    */         if (localGoatField.amResultsList())
/*    */           FieldGraph.this.mHaveAuthoredResultsList = true;
/*    */         this.mItemsInTree += 1;
/*    */         if (this.mUniqueChildren.contains(Integer.valueOf(localGoatField.getMFieldID())))
/*    */         {
/*    */           assert (localGoatField.getMFieldID() != 0);
/*    */           recurBuild(paramInt1 + 1, localGoatField.getMFieldID(), localNode);
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/*    */     public void addToOutputNotes(OutputNotes paramOutputNotes)
/*    */       throws GoatException
/*    */     {
/*    */       if (this.noDisplay)
/*    */         return;
/*    */       Map localMap = GoatField.get(this.viewInfo, this.noDisplay);
/*    */       Iterator localIterator = localMap.values().iterator();
/*    */       while (localIterator.hasNext())
/*    */       {
/*    */         GoatField localGoatField = (GoatField)localIterator.next();
/*    */         if ((localGoatField instanceof ViewField))
/*    */         {
/*    */           ViewField localViewField = (ViewField)localGoatField;
/*    */           String str = localViewField.getTemplateName();
/*    */           if (str != null)
/*    */             paramOutputNotes.addTemplate(str);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public final class Node
/*    */     implements Serializable
/*    */   {
/*    */     private static final long serialVersionUID = 3267047337393002415L;
/*    */     public static final int NODE_BROKEN = 0;
/*    */     public static final int NODE_VIEWABLE = 1;
/*    */     public static final int NODE_OPTIMISED = 2;
/*    */     public static final int NODE_VIEWLESS = 3;
/*    */     public transient Node mParent;
/*    */     public transient Node mChild;
/*    */     public transient Node mLastChild;
/*    */     public transient Node mNext;
/*    */     private Integer mParentKey;
/*    */     private Integer mChildKey;
/*    */     private Integer mLastChildKey;
/*    */     private Integer mNextKey;
/*    */     private int mNumChildren;
/*    */     public transient GoatField mField;
/*    */     public int mState;
/*    */     private String sName;
/*    */     private String scName;
/*    */     private int vID;
/*    */     private int fldID = -1;
/*    */     boolean isFldCached = false;
/*    */     boolean mNoDisplay;
/*    */ 
/*    */     private Node(Node paramGoatField, GoatField arg3)
/*    */     {
/*    */       this(paramGoatField, localGoatField, false);
/*    */     }
/*    */ 
/*    */     private Node(Node paramGoatField, GoatField paramBoolean, boolean arg4)
/*    */     {
/*    */       assert (paramBoolean != null);
/*    */       this.mParent = paramGoatField;
/*    */       this.mNext = (this.mChild = null);
/*    */       this.mField = paramBoolean;
/*    */       boolean bool;
/*    */       this.mNoDisplay = bool;
/*    */       if (this.mParent != null)
/*    */       {
/*    */         if (this.mParent.mChild == null)
/*    */         {
/*    */           this.mParent.mLastChild = this;
/*    */           this.mParent.mChild = this;
/*    */         }
/*    */         else
/*    */         {
/*    */           this.mParent.mLastChild.mNext = this;
/*    */           this.mParent.mLastChild = this;
/*    */         }
/*    */         if ((this.mField.isVisible()) && (this.mField.isMInView()) && (this.mField.getMFieldID() != CoreFieldId.StatusHistory.getFieldId()) && (!this.mField.isMFloat()) && (!(this.mField instanceof MenuItemField)))
/*    */           this.mParent.pushChild();
/*    */       }
/*    */       this.mState = 0;
/*    */       this.fldID = this.mField.getMFieldID();
/*    */       FieldGraph.this.mNodeMap.put(Integer.valueOf(this.mField.getMFieldID()), this);
/*    */       if ((!FieldGraph.this.mHaveRTFField) && (this.mField != null))
/*    */         if ((this.mField instanceof CharField))
/*    */         {
/*    */           if (((CharField)this.mField).isMRichTextField())
/*    */             FieldGraph.this.mHaveRTFField = true;
/*    */         }
/*    */         else if (((this.mField instanceof ColumnField)) && (((ColumnField)this.mField).isMRichTextField()))
/*    */           FieldGraph.this.mHaveRTFField = true;
/*    */       if ((!bool) && (this.mField.amSearchBar()))
/*    */         FieldGraph.this.mHaveAuthoredSearchBar = true;
/*    */       try
/*    */       {
/*    */         this.isFldCached = false;
/*    */         if (FieldGraph.this.displayFieldMap == null)
/*    */           FieldGraph.this.displayFieldMap = GoatField.get(FieldGraph.this.mViewInfo, bool);
/*    */         if (FieldGraph.this.displayFieldMap != null)
/*    */         {
/*    */           GoatField localGoatField = (GoatField)FieldGraph.this.displayFieldMap.get(Integer.valueOf(this.fldID));
/*    */           if (localGoatField != null)
/*    */             this.isFldCached = true;
/*    */         }
/*    */       }
/*    */       catch (GoatException localGoatException)
/*    */       {
/*    */       }
/*    */     }
/*    */ 
/*    */     Node()
/*    */     {
/*    */       this.mState = 0;
/*    */       this.fldID = -1;
/*    */       this.isFldCached = false;
/*    */       FieldGraph.this.mNodeMap.put(Integer.valueOf(-1), this);
/*    */     }
/*    */ 
/*    */     private SiblingIterator newSiblingIterator()
/*    */     {
/*    */       return new SiblingIterator(this);
/*    */     }
/*    */ 
/*    */     public int getNumChildren()
/*    */     {
/*    */       return this.mNumChildren;
/*    */     }
/*    */ 
/*    */     public void pushChild()
/*    */     {
/*    */       this.mNumChildren += 1;
/*    */     }
/*    */ 
/*    */     public void popChild()
/*    */     {
/*    */       if (this.mNumChildren > 0)
/*    */         this.mNumChildren -= 1;
/*    */     }
/*    */ 
/*    */     private void forceColourViewable()
/*    */     {
/*    */       SiblingIterator localSiblingIterator = newSiblingIterator();
/*    */       while (localSiblingIterator.hasNext())
/*    */       {
/*    */         Node localNode = (Node)localSiblingIterator.next();
/*    */         assert (localNode.mState == 0);
/*    */         localNode.mState = 1;
/*    */         if (localNode.mChild != null)
/*    */           localNode.mChild.forceColourViewable();
/*    */       }
/*    */     }
/*    */ 
/*    */     private void recurColourState(OutputNotes paramOutputNotes)
/*    */     {
/*    */       SiblingIterator localSiblingIterator = newSiblingIterator();
/*    */       while (localSiblingIterator.hasNext())
/*    */       {
/*    */         Node localNode = (Node)localSiblingIterator.next();
/*    */         assert (localNode.mState == 0);
/*    */         if (localNode.mParent == null)
/*    */         {
/*    */           localNode.mState = 1;
/*    */         }
/*    */         else
/*    */         {
/*    */           assert (localNode.mParent.mState != 0);
/*    */           if (!localNode.mField.isMInView())
/*    */           {
/*    */             localNode.mState = 3;
/*    */           }
/*    */           else if ((localNode.mParent.mState == 2) || (localNode.mParent.mState == 3))
/*    */           {
/*    */             localNode.mState = localNode.mParent.mState;
/*    */           }
/*    */           else
/*    */           {
/*    */             assert (localNode.mParent.mState == 1);
/*    */             if ((!localNode.mField.isMVisible()) && (!paramOutputNotes.getIndirectChangeFieldVisibility()) && (!paramOutputNotes.getFieldSetMadeVisible().contains(Integer.valueOf(localNode.mField.getMFieldID()))) && (!paramOutputNotes.getFieldSetMadeProcessReq().contains(Integer.valueOf(localNode.mField.getMFieldID()))))
/*    */               localNode.mState = 2;
/*    */             else
/*    */               localNode.mState = 1;
/*    */           }
/*    */         }
/*    */         if (localNode.mChild != null)
/*    */           localNode.mChild.recurColourState(paramOutputNotes);
/*    */       }
/*    */     }
/*    */ 
/*    */     private void recurAddToOutputNotes(OutputNotes paramOutputNotes)
/*    */     {
/*    */       SiblingIterator localSiblingIterator = newSiblingIterator();
/*    */       while (localSiblingIterator.hasNext())
/*    */       {
/*    */         Node localNode1 = (Node)localSiblingIterator.next();
/*    */         if (localNode1.mParent != null)
/*    */         {
/*    */           int i = localNode1.getEmitMode();
/*    */           if (i == 0)
/*    */             paramOutputNotes.addDOMField(localNode1.mField.getMFieldID());
/*    */           else if (i == 1)
/*    */             paramOutputNotes.addJSField(localNode1.mField.getMFieldID());
/*    */           if (localNode1.mField.isMParentExecDep())
/*    */             if (!localNode1.mField.isMInView())
/*    */             {
/*    */               paramOutputNotes.addParentHidden(localNode1.mField.getMFieldID());
/*    */             }
/*    */             else
/*    */             {
/*    */               int j = 0;
/*    */               Node localNode2 = localNode1.mParent;
/*    */               if (localNode2.mField != null)
/*    */                 if (localNode2.mField.isMVisible())
/*    */                   j = 1;
/*    */                 else if ((paramOutputNotes.getIndirectChangeFieldVisibility()) || (paramOutputNotes.getFieldSetMadeVisible().contains(Integer.valueOf(localNode2.mField.getMFieldID()))))
/*    */                   j = 1;
/*    */               if (j != 0)
/*    */                 paramOutputNotes.addParentExecDep(localNode1.mField.getMFieldID());
/*    */             }
/*    */           localNode1.mField.addToOutputNotes(paramOutputNotes, localNode1.getEmitMode());
/*    */         }
/*    */         if (localNode1.mChild != null)
/*    */           localNode1.mChild.recurAddToOutputNotes(paramOutputNotes);
/*    */       }
/*    */     }
/*    */ 
/*    */     public int getEmitMode()
/*    */     {
/*    */       assert (this.mState != 0);
/*    */       if (this.mState == 1)
/*    */         return this.mField.getMEmitViewable();
/*    */       if (this.mState == 2)
/*    */         return this.mField.getMEmitOptimised();
/*    */       return this.mField.getMEmitViewless();
/*    */     }
/*    */ 
/*    */     public int getState()
/*    */     {
/*    */       assert (this.mState != 0);
/*    */       return this.mState;
/*    */     }
/*    */ 
/*    */     private void recurPrintChildren(int paramInt)
/*    */     {
/*    */       SiblingIterator localSiblingIterator = newSiblingIterator();
/*    */       while (localSiblingIterator.hasNext())
/*    */       {
/*    */         Node localNode = (Node)localSiblingIterator.next();
/*    */         for (int i = 0; i < paramInt * 4; i++)
/*    */           System.out.print(" ");
/*    */         if (localNode.mField != null)
/*    */           System.out.println(localNode.mField.getMDataTypeString() + " " + localNode.mField.getMFieldID() + "(" + localNode.mField.getMDBName() + ")" + "; Parent:" + localNode.mField.getMParentFieldID() + "; State:" + localNode.mState + "; InView:" + localNode.mField.isMInView() + "; Visible:" + localNode.mField.isMVisible() + "; EmitOptimised:" + localNode.mField.getMEmitOptimised() + "; EmitViewable:" + localNode.mField.getMEmitViewable() + ": EmitViewless:" + localNode.mField.getMEmitViewless() + "; ZOrder:" + localNode.mField.getMZOrder() + "; TabOrder:" + localNode.mField.getMTabOrder());
/*    */         if (localNode.mChild != null)
/*    */           localNode.mChild.recurPrintChildren(paramInt + 1);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void traverseDepthFirst(IFieldGraphVisitor paramIFieldGraphVisitor, IVisitorAccess paramIVisitorAccess)
/*    */       throws GoatException
/*    */     {
/*    */       SiblingIterator localSiblingIterator = newSiblingIterator();
/*    */       while (localSiblingIterator.hasNext())
/*    */       {
/*    */         Node localNode = (Node)localSiblingIterator.next();
/*    */         if (paramIVisitorAccess.canAccessBeforeChildrenVisitor(localNode))
/*    */           paramIFieldGraphVisitor.visitBeforeChildren(localNode);
/*    */         if ((localNode.mChild != null) && (paramIVisitorAccess.canAccessTreeTraversal(localNode)))
/*    */           localNode.mChild.traverseDepthFirst(paramIFieldGraphVisitor, paramIVisitorAccess);
/*    */         if (paramIVisitorAccess.canAccessAfterChildrenVisitor(localNode))
/*    */           paramIFieldGraphVisitor.visitAfterChildren(localNode);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void traverseDepthFirst_takeTwo(IFieldGraphVisitor paramIFieldGraphVisitor1, IVisitorAccess paramIVisitorAccess1, IFieldGraphVisitor paramIFieldGraphVisitor2, IVisitorAccess paramIVisitorAccess2)
/*    */       throws GoatException
/*    */     {
/*    */       SiblingIterator localSiblingIterator = newSiblingIterator();
/*    */       while (localSiblingIterator.hasNext())
/*    */       {
/*    */         Node localNode = (Node)localSiblingIterator.next();
/*    */         if (paramIVisitorAccess1.canAccessBeforeChildrenVisitor(localNode))
/*    */           paramIFieldGraphVisitor1.visitBeforeChildren(localNode);
/*    */         else if (paramIVisitorAccess2.canAccessBeforeChildrenVisitor(localNode))
/*    */           paramIFieldGraphVisitor2.visitBeforeChildren(localNode);
/*    */         if (localNode.mChild != null)
/*    */           if (paramIVisitorAccess1.canAccessTreeTraversal(localNode))
/*    */             localNode.mChild.traverseDepthFirst_takeTwo(paramIFieldGraphVisitor1, paramIVisitorAccess1, paramIFieldGraphVisitor2, paramIVisitorAccess2);
/*    */           else if (paramIVisitorAccess2.canAccessTreeTraversal(localNode))
/*    */             localNode.mChild.traverseDepthFirst(paramIFieldGraphVisitor2, paramIVisitorAccess2);
/*    */         if (paramIVisitorAccess1.canAccessAfterChildrenVisitor(localNode))
/*    */           paramIFieldGraphVisitor1.visitAfterChildren(localNode);
/*    */         else if (paramIVisitorAccess2.canAccessAfterChildrenVisitor(localNode))
/*    */           paramIFieldGraphVisitor2.visitAfterChildren(localNode);
/*    */       }
/*    */     }
/*    */ 
/*    */     public Node[] getChildNodes()
/*    */     {
/*    */       ArrayList localArrayList = new ArrayList();
/*    */       for (Node localNode = this.mChild; localNode != null; localNode = localNode.mNext)
/*    */         localArrayList.add(localNode);
/*    */       return (Node[])localArrayList.toArray(FieldGraph.EmptyNode);
/*    */     }
/*    */ 
/*    */     public Node[] getChildNodes(int paramInt)
/*    */     {
/*    */       assert ((paramInt == 0) || (paramInt == 1) || (paramInt == 2));
/*    */       ArrayList localArrayList = new ArrayList();
/*    */       for (Node localNode = this.mChild; localNode != null; localNode = localNode.mNext)
/*    */         if (localNode.getEmitMode() == paramInt)
/*    */           localArrayList.add(localNode);
/*    */       return (Node[])localArrayList.toArray(FieldGraph.EmptyNode);
/*    */     }
/*    */ 
/*    */     public FieldGraph getParentFieldGraph()
/*    */     {
/*    */       return FieldGraph.this;
/*    */     }
/*    */ 
/*    */     private Integer getNodeLink(Node paramNode)
/*    */       throws IOException
/*    */     {
/*    */       if (paramNode == null)
/*    */         return null;
/*    */       if (paramNode.mField == null)
/*    */         return new Integer(-1);
/*    */       return new Integer(paramNode.mField.getMFieldID());
/*    */     }
/*    */ 
/*    */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*    */       throws IOException
/*    */     {
/*    */       this.mParentKey = getNodeLink(this.mParent);
/*    */       this.mChildKey = getNodeLink(this.mChild);
/*    */       this.mNextKey = getNodeLink(this.mNext);
/*    */       this.mLastChildKey = getNodeLink(this.mLastChild);
/*    */       this.sName = FieldGraph.this.mForm.getServerName();
/*    */       this.scName = FieldGraph.this.mForm.getName();
/*    */       this.vID = FieldGraph.this.mViewInfo.mID;
/*    */       paramObjectOutputStream.defaultWriteObject();
/*    */       if (!this.isFldCached)
/*    */         paramObjectOutputStream.writeObject(this.mField);
/*    */     }
/*    */ 
/*    */     private void readObject(ObjectInputStream paramObjectInputStream)
/*    */       throws IOException, ClassNotFoundException
/*    */     {
/*    */       paramObjectInputStream.defaultReadObject();
/*    */       if (!this.isFldCached)
/*    */       {
/*    */         this.mField = ((GoatField)paramObjectInputStream.readObject());
/*    */         return;
/*    */       }
/*    */       try
/*    */       {
/*    */         if (FieldGraph.this.displayFieldMap == null)
/*    */           FieldGraph.this.displayFieldMap = GoatField.get(com.remedy.arsys.goat.Form.get(this.sName, this.scName).getViewInfo(this.vID), this.mNoDisplay);
/*    */         if (FieldGraph.this.displayFieldMap != null)
/*    */         {
/*    */           GoatField localGoatField = (GoatField)FieldGraph.this.displayFieldMap.get(Integer.valueOf(this.fldID));
/*    */           if (localGoatField != null)
/*    */             this.mField = localGoatField;
/*    */         }
/*    */       }
/*    */       catch (GoatException localGoatException)
/*    */       {
/*    */       }
/*    */     }
/*    */ 
/*    */     private Node initLink(Integer paramInteger)
/*    */     {
/*    */       if (paramInteger == null)
/*    */         return null;
/*    */       return (Node)FieldGraph.this.mNodeMap.get(paramInteger);
/*    */     }
/*    */ 
/*    */     private void initNodeLinks()
/*    */     {
/*    */       this.mParent = initLink(this.mParentKey);
/*    */       this.mChild = initLink(this.mChildKey);
/*    */       this.mNext = initLink(this.mNextKey);
/*    */       this.mLastChild = initLink(this.mLastChildKey);
/*    */     }
/*    */ 
/*    */     public String getTableSchema()
/*    */     {
/*    */       if (this.mField.getMDataType().toInt() == DataType.COLUMN.toInt())
/*    */       {
/*    */         TableField localTableField = (TableField)this.mParent.mField;
/*    */         return localTableField.getMSchema();
/*    */       }
/*    */       return null;
/*    */     }
/*    */ 
/*    */     public String getTableServer()
/*    */     {
/*    */       if (this.mField.getMDataType().toInt() == DataType.COLUMN.toInt())
/*    */       {
/*    */         TableField localTableField = (TableField)this.mParent.mField;
/*    */         return localTableField.getMServer();
/*    */       }
/*    */       return null;
/*    */     }
/*    */ 
/*    */     public final class SiblingIterator
/*    */       implements Iterator
/*    */     {
/*    */       private FieldGraph.Node mNextNode;
/*    */ 
/*    */       public SiblingIterator(FieldGraph.Node arg2)
/*    */       {
/*    */         Object localObject;
/*    */         this.mNextNode = localObject;
/*    */       }
/*    */ 
/*    */       public boolean hasNext()
/*    */       {
/*    */         return this.mNextNode != null;
/*    */       }
/*    */ 
/*    */       public Object next()
/*    */       {
/*    */         FieldGraph.Node localNode = this.mNextNode;
/*    */         this.mNextNode = this.mNextNode.mNext;
/*    */         return localNode;
/*    */       }
/*    */ 
/*    */       public void remove()
/*    */       {
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.FieldGraph
 * JD-Core Version:    0.6.1
 */