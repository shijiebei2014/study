/*   */ package com.remedy.arsys.goat.savesearches;
/*   */ 
/*   */ import com.bmc.arsys.api.ARException;
/*   */ import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
/*   */ import com.bmc.arsys.api.CoreFieldId;
/*   */ import com.bmc.arsys.api.DataType;
/*   */ import com.bmc.arsys.api.Entry;
/*   */ import com.bmc.arsys.api.FormType;
/*   */ import com.bmc.arsys.api.OperandType;
/*   */ import com.bmc.arsys.api.QualifierInfo;
/*   */ import com.bmc.arsys.api.RelationalOperationInfo;
/*   */ import com.bmc.arsys.api.StatusInfo;
/*   */ import com.bmc.arsys.api.Timestamp;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.config.Configuration;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.aspects.IARUserSearchesServiceCacheAspect;
/*   */ import com.remedy.arsys.goat.intf.service.IARUserSearchesService;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import com.remedy.arsys.share.Cache.Item;
/*   */ import com.remedy.arsys.share.JSWriter;
/*   */ import com.remedy.arsys.share.ServiceLocator;
/*   */ import com.remedy.arsys.stubs.ServerLogin;
/*   */ import com.remedy.arsys.stubs.SessionData;
/*   */ import java.io.IOException;
/*   */ import java.io.ObjectInputStream;
/*   */ import java.io.ObjectOutputStream;
/*   */ import java.io.Serializable;
/*   */ import java.util.ArrayList;
/*   */ import java.util.Collections;
/*   */ import java.util.Comparator;
/*   */ import java.util.HashMap;
/*   */ import java.util.Iterator;
/*   */ import java.util.LinkedHashMap;
/*   */ import java.util.List;
/*   */ import java.util.Map;
/*   */ import java.util.Map.Entry;
/*   */ import java.util.Set;
/*   */ import java.util.SortedMap;
/*   */ import java.util.TreeMap;
/*   */ import java.util.logging.Level;
/*   */ import org.aspectj.runtime.internal.Conversions;
/*   */ 
/*   */ public class ARUserSearches
/*   */   implements Cache.Item
/*   */ {
/*   */   private static final long serialVersionUID = -5593152281188340367L;
/*   */   private final Map mSearches;
/*   */   private transient SortedMap mRecent;
/*   */   private long mPrefServerLoadedDate;
/*   */   private final String mForm;
/*   */   private final String mUser;
/*   */   private final String mServer;
/*   */   private String mPrefServer;
/*   */   private String mPrefForm;
/* 1 */   private static int mMaxAllowedSearches = Configuration.getInstance().getMaxSavedSearches(); private static int TYPE_FID = 51003; private static int FORM_FID = 51001; private static int QUAL_FID = 51000; private static int SERVER_FID = 51004; private static int DISABLE_FID = 51002; private static int NAME_FID = CoreFieldId.ShortDescription.getFieldId(); private static int USER_FID = CoreFieldId.Submitter.getFieldId();
/*   */   public static final int SEARCHTYPE_RECENT = 0;
/*   */   public static final int SEARCHTYPE_SAVED = 1;
/*   */   public static final int SEARCHTYPE_DEFINED = 2;
/*   */   public static final int TYPE_QUICKREPORTS = 3;
/* 1 */   private static final ArithmeticOrRelationalOperand USER_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, USER_FID); private static final ArithmeticOrRelationalOperand FORM_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, FORM_FID); private static final ArithmeticOrRelationalOperand SERVER_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SERVER_FID); private static final int[] SEARCHES_SCHEMA_KEY = { QUAL_FID, FORM_FID, TYPE_FID, SERVER_FID }; private static transient Log MLog = Log.get(2); private static final int[] fieldIds = { QUAL_FID, DISABLE_FID, NAME_FID, TYPE_FID };
/*   */   private transient List<Entry> qrEntryList;
/*   */   private QualifierInfo qualifier;
/* 1 */   private static IARUserSearchesService userSearchesService = null;
/*   */ 
/* 1 */   public static ARUserSearches getUserSearches(String arg0, String arg1, String arg2, boolean arg3) { if (userSearchesService == null) userSearchesService = (IARUserSearchesService)ServiceLocator.getInstance().getService("arUserSearchesService"); boolean bool = arg3; String str1 = arg2; String str2 = arg1; String str3 = arg0; IARUserSearchesService localIARUserSearchesService = userSearchesService; Object[] arrayOfObject = new Object[5]; arrayOfObject[0] = localIARUserSearchesService; arrayOfObject[1] = str3; arrayOfObject[2] = str2; arrayOfObject[3] = str1; arrayOfObject[4] = Conversions.booleanObject(bool); return IARUserSearchesServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IARUserSearchesServiceCacheAspect$1$181ba497(str3, str2, str1, bool, new ARUserSearches.AjcClosure1(arrayOfObject)); } 
/* 1 */   public ARUserSearches(String paramString1, String paramString2, String paramString3) { this.mSearches = Collections.synchronizedMap(new LinkedHashMap()); this.mRecent = null; this.qrEntryList = null; this.qualifier = null; this.mForm = paramString2; this.mServer = paramString3; this.mUser = paramString1; this.mRecent = Collections.synchronizedSortedMap(new TreeMap(new RecentSearchComparator())); loadSavedSearches(paramString1); } 
/* 1 */   public long getMPrefServerLoadedDate() { return this.mPrefServerLoadedDate; } 
/* 1 */   public String getMForm() { return this.mForm; } 
/* 1 */   public String getMUser() { return this.mUser; } 
/* 1 */   public String getMServer() { return this.mServer; } 
/* 1 */   private void loadSavedSearches(String arg0) { List localList = Configuration.getInstance().getPreferenceServers(); this.mPrefServerLoadedDate = Configuration.getInstance().getPreferenceServerLastModifiedDate(); if (localList.size() > 0) { QualifierInfo localQualifierInfo = buildQualifier();
/*   */       String str;
/* 1 */       for (Iterator localIterator = localList.iterator(); localIterator.hasNext(); loadFromServer(str, localQualifierInfo)) str = (String)localIterator.next();  } else { this.mPrefServer = null; }  } 
/*   */   public synchronized boolean loadFromServer(String arg0, QualifierInfo arg1) { try { SessionData localSessionData = SessionData.get(); ServerLogin localServerLogin = localSessionData.getServerLogin(arg0); String str = localSessionData.getSearchesPrefFormForServer(arg0); if (str != null) this.mPrefForm = str; else try { List localList = localServerLogin.getListForm(0L, FormType.ALL.toInt() | 0x400, null, SEARCHES_SCHEMA_KEY); if ((localList != null) && (localList.size() > 0)) { this.mPrefForm = ((String)localList.get(0)); localSessionData.setSearchesPrefFormForServer(arg0, this.mPrefForm); }  } catch (ARException localARException) { this.mPrefForm = null; throw new GoatException(localARException); } if (this.mPrefForm != null) { this.mPrefServer = arg0; localARException = localServerLogin.getListEntryObjects(this.mPrefForm, arg1, 0, 0, new ArrayList(), fieldIds, false, null);
/*   */         int i;
/* 1 */         if (localARException.size() > 0) for (i = 0; i < localARException.size(); ) { Entry localEntry = (Entry)localARException.get(i); Value localValue = (Value)localEntry.get(Integer.valueOf(TYPE_FID)); if ((localValue != null) && (localValue.getIntValue() == 3)) { if (this.qrEntryList == null) this.qrEntryList = new ArrayList(); this.qrEntryList.add(localEntry); localARException.remove(i); } else { i++; } } if (localARException.size() > 0) { populateUserSearches((Entry[])localARException.toArray(new Entry[0])); } else { this.mSearches.clear(); this.mRecent.clear(); } return true; }  } catch (ARException localARException2) { MLog.log(Level.WARNING, "Error loading saved searches from server " + arg0, localARException2); } catch (GoatException localGoatException1) { MLog.log(Level.WARNING, "Error loading saved searches from server " + arg0, localGoatException1); } return false; } 
/* 1 */   private static boolean reloadFromServer(ARUserSearches arg0, QualifierInfo arg1) throws GoatException { return arg0.loadFromServer(arg0.getServer(), arg1); } 
/* 1 */   public synchronized QualifierInfo buildQualifier() { if (this.qualifier != null) return this.qualifier; RelationalOperationInfo localRelationalOperationInfo1 = new RelationalOperationInfo(1, USER_OP, new ArithmeticOrRelationalOperand(new Value(this.mUser))); RelationalOperationInfo localRelationalOperationInfo2 = new RelationalOperationInfo(1, FORM_OP, new ArithmeticOrRelationalOperand(new Value(this.mForm))); RelationalOperationInfo localRelationalOperationInfo3 = new RelationalOperationInfo(7, SERVER_OP, new ArithmeticOrRelationalOperand(new Value(Configuration.getInstance().getShortName(this.mServer) + "%"))); QualifierInfo localQualifierInfo = new QualifierInfo(1, new QualifierInfo(localRelationalOperationInfo1), new QualifierInfo(localRelationalOperationInfo2)); this.qualifier = new QualifierInfo(1, localQualifierInfo, new QualifierInfo(localRelationalOperationInfo3)); return this.qualifier; } 
/* 1 */   private synchronized void populateUserSearches(Entry[] arg0) { this.mSearches.clear(); this.mRecent.clear(); for (int i = 0; i < arg0.length; i++) { String str1 = ""; String str2 = ""; boolean bool = false; int j = -1; Entry localEntry = arg0[i]; for (Object localObject = localEntry.entrySet().iterator(); ((Iterator)localObject).hasNext(); ) { Map.Entry localEntry1 = (Map.Entry)((Iterator)localObject).next(); if (((Integer)localEntry1.getKey()).intValue() == NAME_FID) str1 = (String)((Value)localEntry1.getValue()).getValue(); else if (((Integer)localEntry1.getKey()).intValue() == QUAL_FID) str2 = (String)((Value)localEntry1.getValue()).getValue(); else if (((Integer)localEntry1.getKey()).intValue() == DISABLE_FID) { if (((Value)localEntry1.getValue()).getValue() != null) bool = true;  } else if (((Integer)localEntry1.getKey()).intValue() == TYPE_FID) j = ((Integer)((Value)localEntry1.getValue()).getValue()).intValue();  } if (j == 1) { if (!this.mSearches.containsKey(str1)) { this.mSearches.put(str1, new SearchItem(str1, str2, bool, arg0[i].getEntryId(), null)); } else { localObject = (SearchItem)this.mSearches.get(str1); if (!((SearchItem)localObject).getQualification().equals(str2)) this.mSearches.put(str1, new SearchItem(str1, str2, bool, arg0[i].getEntryId(), null)); else if (((SearchItem)localObject).getDisabledState() != bool) ((SearchItem)localObject).setDisabledState(bool);  } 
/* 1 */       } else if ((j == 0) && (!this.mRecent.containsKey(arg0[i].getEntryId().toString()))) this.mRecent.put(arg0[i].getEntryId().toString(), new SearchItem(str1, str2, false, arg0[i].getEntryId(), null));  }  } 
/* 1 */   public synchronized boolean saveSearch(SessionData arg0, String arg1, String arg2, boolean arg3, int arg4, int arg5) throws GoatException { ServerLogin localServerLogin = arg0.getServerLogin(this.mPrefServer); Entry localEntry = null;
/*   */     try
/*   */     {
/* 1 */       SearchItem localSearchItem;
/* 1 */       if (arg3) { if ((this.mSearches.size() > mMaxAllowedSearches) && (arg5 == 1)) throw new GoatException(9422, Integer.valueOf(this.mSearches.size() - mMaxAllowedSearches)); localEntry = new Entry(); localEntry.put(Integer.valueOf(NAME_FID), new Value(arg1)); localEntry.put(Integer.valueOf(SERVER_FID), new Value(this.mServer)); localEntry.put(Integer.valueOf(FORM_FID), new Value(this.mForm)); localEntry.put(Integer.valueOf(QUAL_FID), new Value(arg2)); localEntry.put(Integer.valueOf(USER_FID), new Value(this.mUser)); localEntry.put(Integer.valueOf(TYPE_FID), new Value(Integer.valueOf(arg5), DataType.ENUM)); localServerLogin.createEntry(this.mPrefForm, localEntry); localSearchItem = new SearchItem(arg1, arg2, false, localEntry.getEntryId(), null); if (arg5 == 1) this.mSearches.put(arg1, localSearchItem); else this.mRecent.put(localEntry.getEntryId(), localSearchItem);  } else { localSearchItem = (SearchItem)this.mSearches.get(arg1); assert (localSearchItem != null); int[] arrayOfInt = null; if (arg2.length() == 0) { arrayOfInt = new int[1]; arrayOfInt[0] = DISABLE_FID; localSearchItem.setDisabledState(arg4 == 1); } else { arrayOfInt = new int[2]; arrayOfInt[0] = DISABLE_FID; localSearchItem.setDisabledState(arg4 == 1); arrayOfInt[1] = QUAL_FID; localSearchItem.setQualification(arg2); } localEntry = localServerLogin.getEntry(this.mPrefForm, localSearchItem.mID, arrayOfInt); assert (localEntry != null); int i = arrayOfInt.length; for (int j = 0; j < i; j++) if (arrayOfInt[j] == QUAL_FID) localEntry.put(Integer.valueOf(QUAL_FID), new Value(arg2)); else if (arrayOfInt[j] == DISABLE_FID) localEntry.put(Integer.valueOf(DISABLE_FID), arg4 == 1 ? new Value(0) : new Value()); 
/* 1 */         localServerLogin.setEntry(this.mPrefForm, localSearchItem.mID, localEntry, new Timestamp(), 1); this.mSearches.put(arg1, localSearchItem); }  } catch (ARException localARException1) { MLog.log(Level.SEVERE, "Unable to save search", localARException1); return false; } return true;
/*   */   }
/*   */ 
/*   */   public synchronized void deleteSearch(String[] arg0)
/*   */     throws GoatException
/*   */   {
/* 1 */     Object localObject1;
/* 1 */     if (arg0.length == 0) synchronized (this.mRecent) { if (this.mRecent.isEmpty()) return; localObject1 = (String)this.mRecent.lastKey(); assert (localObject1 != null); int i = !removeEntry((String)localObject1) ? 1 : 0; if (i == 1) { MLog.warning("AR Error 302 while deleting the recent search entry. \nRecent Searches are not up-to-date as same user might have connected to the server via other Midtier"); removeLastSearchEntry(); } this.mRecent.remove(localObject1); } else for (??? = 0; ??? < arg0.length; ???++) { localObject1 = (SearchItem)this.mSearches.get(arg0[???]); assert (localObject1 != null); removeEntry(((SearchItem)localObject1).mID); this.mSearches.remove(arg0[???]); }   } 
/*   */   private synchronized boolean removeEntry(String arg0) throws GoatException { try { ServerLogin.getAdmin(this.mPrefServer).deleteEntry(this.mPrefForm, arg0, 0); } catch (ARException localARException) { int i = 0; List localList = localARException.getLastStatus(); for (StatusInfo localStatusInfo : localList) if (localStatusInfo.getMessageNum() != 302L) { i = 1; break; } if (i != 0) throw new GoatException(localARException); return false; } return true; } 
/* 1 */   private synchronized void removeLastSearchEntry() throws GoatException { int[] arrayOfInt = { QUAL_FID, DISABLE_FID, NAME_FID, TYPE_FID }; String str1 = ""; String str2 = ""; int i = 0; int j = -1; SortedMap localSortedMap = Collections.synchronizedSortedMap(new TreeMap(new RecentSearchComparator()));
/*   */     try { List localList = ServerLogin.getAdmin(this.mPrefServer).getListEntryObjects(this.mPrefForm, buildQualifier(), 0, 0, new ArrayList(), arrayOfInt, false, null); if ((localList != null) && (localList.size() > 0)) { for (int k = 0; k < localList.size(); k++) { Entry localEntry = (Entry)localList.get(k); for (Map.Entry localEntry1 : localEntry.entrySet()) if (((Integer)localEntry1.getKey()).intValue() == NAME_FID) str1 = (String)((Value)localEntry1.getValue()).getValue(); else if (((Integer)localEntry1.getKey()).intValue() == QUAL_FID) str2 = (String)((Value)localEntry1.getValue()).getValue(); else if (((Integer)localEntry1.getKey()).intValue() == DISABLE_FID) { if (((Value)localEntry1.getValue()).getValue() != null) i = 1;  } else if (((Integer)localEntry1.getKey()).intValue() == TYPE_FID) j = ((Integer)((Value)localEntry1.getValue()).getValue()).intValue(); 
/* 1 */           if ((j == 0) && (!localSortedMap.containsKey(((Entry)localList.get(k)).getEntryId().toString()))) localSortedMap.put(((Entry)localList.get(k)).getEntryId().toString(), new SearchItem(str1, str2, false, ((Entry)localList.get(k)).getEntryId(), null));  } if (localSortedMap.size() > 0) { String str3 = (String)localSortedMap.lastKey(); removeEntry(str3); }  }  } catch (ARException localARException1) {
/* 1 */       MLog.warning("Exception while removing last recent search in case where recent seraches are not up-to-date"); }  } 
/* 1 */   public synchronized void emitSavedJS(JSWriter arg0) throws GoatException { if (this.mSearches == null) return; Iterator localIterator = this.mSearches.keySet().iterator(); synchronized (this.mSearches)
/*   */     {
/* 1 */       SearchItem localSearchItem;
/* 1 */       for (String str = null; localIterator.hasNext(); emitSearchCriteriaJS(localSearchItem, arg0)) { str = (String)localIterator.next(); arg0.listSep().append("\"" + JSWriter.escape(str) + "\" : "); localSearchItem = (SearchItem)this.mSearches.get(str); }  }  } 
/* 1 */   public synchronized void emitRecentJS(JSWriter arg0, int arg1) throws GoatException { if (this.mRecent == null) return; Iterator localIterator = this.mRecent.keySet().iterator(); synchronized (this.mRecent) { String str = null; for (int i = 0; (localIterator.hasNext()) && (i < arg1); i++) { str = (String)localIterator.next(); SearchItem localSearchItem = (SearchItem)this.mRecent.get(str); arg0.listSep(); arg0.openObj(); arg0.property("l", localSearchItem.mName); JSWriter localJSWriter = new JSWriter(); emitSearchCriteriaJS(localSearchItem, localJSWriter); arg0.property("v", localJSWriter); arg0.closeObj(); }  }  } 
/* 1 */   private synchronized void emitSearchCriteriaJS(SearchItem arg0, JSWriter arg1) throws GoatException { arg1.openObj(); JSWriter localJSWriter1 = new JSWriter(); JSWriter localJSWriter2 = new JSWriter(); localJSWriter2.openList(); localJSWriter1.openList(); Map localMap = arg0.getFieldValuePairs(); if (localMap != null)
/*   */     {
/* 1 */       String str2;
/* 1 */       for (Iterator localIterator = localMap.entrySet().iterator(); localIterator.hasNext(); localJSWriter1.listSep().appendqs(str2)) { Map.Entry localEntry = (Map.Entry)localIterator.next(); String str1 = (String)localEntry.getKey(); localJSWriter2.listSep().append(str1); str2 = (String)localEntry.getValue(); }  } localJSWriter2.closeList(); localJSWriter1.closeList(); arg1.property("ids", localJSWriter2); arg1.property("vals", localJSWriter1); arg1.property("disable", arg0.getDisabledState() ? 1 : 0); arg1.closeObj(); } 
/* 1 */   public int getSize() { return 1; } 
/* 1 */   public synchronized String getPrefForm() { return this.mPrefForm; } 
/* 1 */   public synchronized String getFormServer() { return this.mServer; } 
/* 1 */   public synchronized String getServer() { return this.mPrefServer; } 
/* 1 */   public synchronized int getRecentSize() { return this.mRecent.size(); } 
/* 1 */   private synchronized void writeObject(ObjectOutputStream arg0) throws IOException { synchronized (this.mSearches) { arg0.defaultWriteObject(); }  } 
/* 1 */   public synchronized List<Entry> getQREntries() { if ((this.qrEntryList == null) || (this.qrEntryList.size() == 0)) return null; return this.qrEntryList; } 
/* 1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { arg0.defaultReadObject(); this.mRecent = Collections.synchronizedSortedMap(new TreeMap(new RecentSearchComparator()));
/*   */   }
/*   */ 
/*   */   private static final class RecentSearchComparator
/*   */     implements Comparator
/*   */   {
/*   */     public int compare(Object paramObject1, Object paramObject2)
/*   */     {
/*   */       return ((String)paramObject2).compareTo((String)paramObject1);
/*   */     }
/*   */   }
/*   */ 
/*   */   private static class SearchItem
/*   */     implements Serializable
/*   */   {
/*   */     private static final long serialVersionUID = 8846632035777433283L;
/*   */     private Map mValuesMap;
/*   */     private boolean mDisable;
/*   */     private String mQualification;
/*   */     private final String mID;
/*   */     private final String mName;
/*   */ 
/*   */     private SearchItem(String paramString1, String paramString2, boolean paramBoolean, String paramString3)
/*   */     {
/*   */       this.mDisable = paramBoolean;
/*   */       this.mQualification = paramString2;
/*   */       populateFieldValuePairs();
/*   */       this.mID = paramString3;
/*   */       this.mName = paramString1;
/*   */     }
/*   */ 
/*   */     private void populateFieldValuePairs()
/*   */     {
/*   */       int i = this.mQualification.indexOf('/');
/*   */       if (i != -1)
/*   */         try
/*   */         {
/*   */           int j = Integer.parseInt(this.mQualification.substring(0, i));
/*   */           this.mValuesMap = new HashMap(j);
/*   */           for (int k = 0; k < j; k++)
/*   */           {
/*   */             int n = this.mQualification.indexOf('/', ++i);
/*   */             String str1 = this.mQualification.substring(i, n);
/*   */             i = n;
/*   */             n = this.mQualification.indexOf('/', ++i);
/*   */             int m = Integer.parseInt(this.mQualification.substring(i, n));
/*   */             i = n + m + 1;
/*   */             String str2 = this.mQualification.substring(++n, i);
/*   */             this.mValuesMap.put(str1, str2);
/*   */           }
/*   */         }
/*   */         catch (Exception localException)
/*   */         {
/*   */           ARUserSearches.MLog.log(Level.FINE, "Unable to parse saved search", localException);
/*   */         }
/*   */     }
/*   */ 
/*   */     public synchronized String getQualification()
/*   */     {
/*   */       return this.mQualification;
/*   */     }
/*   */ 
/*   */     public synchronized boolean getDisabledState()
/*   */     {
/*   */       return this.mDisable;
/*   */     }
/*   */ 
/*   */     public synchronized void setDisabledState(boolean paramBoolean)
/*   */     {
/*   */       this.mDisable = paramBoolean;
/*   */     }
/*   */ 
/*   */     public synchronized Map getFieldValuePairs()
/*   */     {
/*   */       return this.mValuesMap;
/*   */     }
/*   */ 
/*   */     public synchronized void setQualification(String paramString)
/*   */     {
/*   */       this.mQualification = paramString;
/*   */       populateFieldValuePairs();
/*   */     }
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.savesearches.ARUserSearches
 * JD-Core Version:    0.6.1
 */