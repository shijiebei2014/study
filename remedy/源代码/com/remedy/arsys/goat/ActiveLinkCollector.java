/*   */ package com.remedy.arsys.goat;
/*   */ 
/*   */ import com.bmc.arsys.api.ARException;
/*   */ import com.bmc.arsys.api.ContainerOwner;
/*   */ import com.bmc.arsys.api.ContainerType;
/*   */ import com.bmc.arsys.api.DisplayInstanceMap;
/*   */ import com.bmc.arsys.api.ExternalReference;
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.Reference;
/*   */ import com.bmc.arsys.api.ReferenceType;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.action.ActionList;
/*   */ import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import com.remedy.arsys.share.JSWriter;
/*   */ import com.remedy.arsys.stubs.ServerLogin;
/*   */ import com.remedy.arsys.stubs.SessionData;
/*   */ import java.io.IOException;
/*   */ import java.io.ObjectInputStream;
/*   */ import java.io.ObjectOutputStream;
/*   */ import java.io.Serializable;
/*   */ import java.util.ArrayList;
/*   */ import java.util.Arrays;
/*   */ import java.util.Collection;
/*   */ import java.util.HashMap;
/*   */ import java.util.HashSet;
/*   */ import java.util.Iterator;
/*   */ import java.util.List;
/*   */ import java.util.Map;
/*   */ import java.util.Set;
/*   */ import java.util.logging.Level;
/*   */ 
/*   */ public class ActiveLinkCollector
/*   */   implements Serializable
/*   */ {
/*   */   private static final long serialVersionUID = -8127204359137436250L;
/*   */   private transient Form mForm;
/*   */   private transient Form.ViewInfo mViewInfo;
/*   */   private Map mCollectionHash;
/*   */   private Map mGuideCollectionHash;
/*   */   private Map mIntervalEventHash;
/*   */   private Map mActiveLinks;
/* 1 */   private static transient Log MPerformanceLog = Log.get(8); private static transient Log MLog = Log.get(7); private static transient Map<Integer, boolean[]> MHover = initHover();
/*   */ 
/* 1 */   private static Map<Integer, boolean[]> initHover() { HashMap localHashMap = new HashMap(); localHashMap.put(new Integer(37), new boolean[] { true, false, false }); localHashMap.put(new Integer(11), new boolean[] { true, false, false }); localHashMap.put(new Integer(42), new boolean[] { false, false, false }); localHashMap.put(new Integer(35), new boolean[] { true, false, false }); localHashMap.put(new Integer(36), new boolean[] { false, false, false }); localHashMap.put(new Integer(31), new boolean[] { false, false, false }); localHashMap.put(new Integer(34), new boolean[] { true, false, false }); return localHashMap; } 
/* 1 */   public ActiveLinkCollector(Form paramForm, Form.ViewInfo paramViewInfo) throws GoatException { this.mForm = paramForm; this.mViewInfo = paramViewInfo; ServerLogin localServerLogin = SessionData.get().getServerLogin(paramForm.getServer()); String str = paramForm.getSchemaKey();
/*   */     String[] arrayOfString;
/*   */     try { arrayOfString = (String[])localServerLogin.getListActiveLink(str, 0L).toArray(new String[0]); } catch (ARException localARException) { throw new GoatException(localARException); } ActiveLink.bulkLoad(localServerLogin, str, arrayOfString, paramForm.getName(), paramViewInfo.getLabel()); populateEventCollections(paramForm.getServer(), paramForm.getName(), paramViewInfo.getLabel(), str, arrayOfString); populateCallguideCollections(localServerLogin, str, arrayOfString, paramForm.getName(), paramViewInfo.getLabel()); } 
/* 1 */   public ActiveLinkCollector(Form paramForm, Form.ViewInfo paramViewInfo, HashMap<String, List<String>> paramHashMap) throws GoatException { this.mForm = paramForm; this.mViewInfo = paramViewInfo; ServerLogin localServerLogin = SessionData.get().getServerLogin(paramForm.getServer()); String str1 = paramForm.getSchemaKey(); String[] arrayOfString = null; if (paramHashMap != null) { String str2 = localServerLogin.getServer(); List localList = (List)paramHashMap.get((str2 + "_" + str1).intern()); if (localList != null) arrayOfString = (String[])localList.toArray(new String[0]);  } if (arrayOfString == null) try { arrayOfString = (String[])localServerLogin.getListActiveLink(str1, 0L).toArray(new String[0]); } catch (ARException localARException) { throw new GoatException(localARException); } ActiveLink.bulkLoad(localServerLogin, str1, arrayOfString, paramForm.getName(), paramViewInfo.getLabel()); populateEventCollections(paramForm.getServer(), paramForm.getName(), paramViewInfo.getLabel(), str1, arrayOfString); populateCallguideCollections(localServerLogin, str1, arrayOfString, paramForm.getName(), paramViewInfo.getLabel()); } 
/* 1 */   private int hoverCtl(int arg0, int arg1, int arg2) { int i = arg2;
/*   */     try { if ((arg0 == 1) || (arg0 == 16) || (arg0 == 512)) { if ((arg1 & 0x300000) != 0) i |= arg1 & 0x300000;  } else i |= arg1 & 0x700000;  } catch (NumberFormatException localNumberFormatException) {  }
/* 1 */     return i; } 
/* 1 */   private int hoverTable(int arg0, int arg1, int arg2) { int i = arg2;
/*   */     try { if ((arg0 == 3) && ((arg1 & 0x300000) != 0)) i |= arg1 & 0x300000;  } catch (NumberFormatException localNumberFormatException) {  }
/* 1 */     return i; } 
/* 1 */   private boolean removeHoverEvent(ActiveLink arg0, CachedFieldMap arg1) { int[] arrayOfInt1 = { 4194304, 1048576, 2097152 }; int[] arrayOfInt2 = { 22, 20, 21 }; ExecutionEvent localExecutionEvent = arg0.getEvent(); boolean[] arrayOfBoolean1 = localExecutionEvent.getExecutionMask(); boolean bool = false; if ((arrayOfBoolean1 & 0x700000) != 0) { long l = localExecutionEvent.getFocusField(); int i = (int)l; Field localField = (Field)arg1.get(new Integer(i)); if (localField == null) return bool; int j = 0; int k = localField.getDataType();
/*   */       int i1;
/*   */       boolean[] arrayOfBoolean2;
/* 1 */       if ((k == 32) || (k == 33)) { try { String str = this.mForm.getDefaultVUI(); Form.ViewInfo localViewInfo = this.mForm.getViewInfo(str); i1 = localViewInfo.getID(); Value localValue = localField.getDisplayInstance().getProperty(i1, new Integer(2));
/*   */           int i2;
/* 1 */           if ((k == 32) && (localValue != null) && (localValue.getValue() != null)) { i2 = Integer.parseInt(localValue.getValue().toString()); j = hoverCtl(i2, arrayOfBoolean1, j); } localValue = localField.getDisplayInstance().getProperty(i1, new Integer(5001)); if ((k == 33) && (localValue != null) && (localValue.getValue() != null)) { i2 = Integer.parseInt(localValue.getValue().toString()); j = hoverTable(i2, arrayOfBoolean1, j); }  } catch (GoatException localGoatException1) {  } } else { arrayOfBoolean2 = (boolean[])MHover.get(new Integer(k)); if (arrayOfBoolean2 == null) return bool; for (int n = 0; n < arrayOfBoolean2.length; n++) if ((arrayOfBoolean2[n] == 0) && ((arrayOfBoolean1 & arrayOfInt1[n]) != 0)) j |= arrayOfInt1[n];  
/*   */       }
/* 1 */       if (j != 0) { arrayOfBoolean2 = arrayOfBoolean1;
/*   */         int m;
/* 1 */         arrayOfBoolean2 &= (j ^ 0xFFFFFFFF); if (((m & 0x700000) == 0) && (m == 0)) bool = true; localExecutionEvent.setExecutionMask(m); StringBuilder localStringBuilder = new StringBuilder("Removed the following execution events from Active Link " + arg0.getName() + ": "); for (i1 = 0; i1 < arrayOfInt1.length; i1++) if ((j & arrayOfInt1[i1]) != 0) localStringBuilder.append(" " + ExecutionEvent.MEventNames[arrayOfInt2[i1]]); 
/* 1 */         MLog.log(Level.WARNING, localStringBuilder.toString()); if (m == 0) { localStringBuilder.setLength(0); localStringBuilder.append("Active link " + arg0.getName() + "does not have any more execution events"); MLog.log(Level.WARNING, localStringBuilder.toString()); } localStringBuilder = null; }  } return bool; } 
/* 1 */   private void populateEventCollections(String arg0, String arg1, String arg2, String arg3, String[] arg4) throws GoatException { this.mCollectionHash = new HashMap(); for (int i = 0; i < 13; i++) { String str1 = ExecutionEvent.MEventNames[i]; int k = ExecutionEvent.MEventMap[i]; this.mCollectionHash.put(str1, new EventALCollection(str1, k, i, arg0, arg1, arg2)); } CachedFieldMap localCachedFieldMap = Form.get(arg0, arg3).getCachedFieldMap(); this.mActiveLinks = new HashMap(); for (int j = 0; j < arg4.length; j++) try { ActiveLink localActiveLink = ActiveLink.getBound(arg0, arg1, arg2, arg4[j], localCachedFieldMap); if (!removeHoverEvent(localActiveLink, localCachedFieldMap)) { ExecutionEvent localExecutionEvent = localActiveLink.getEvent(); int m = localExecutionEvent.getExecutionMask(); this.mActiveLinks.put(arg4[j], localActiveLink); if ((m != 0) && (localActiveLink.isEnabled())) for (int n = 0; n <= 26; n++) if ((m & 1 << n) != 0) { String str2 = localExecutionEvent.getCollectionName(n); Object localObject = (ActiveLinkCollection)this.mCollectionHash.get(str2); if (localObject == null) { localObject = new EventALCollection(str2, ExecutionEvent.MEventMap[n], n, arg0, arg1, arg2); this.mCollectionHash.put(str2, localObject); } ((ActiveLinkCollection)localObject).add(localActiveLink); if (n == 13) { if (this.mIntervalEventHash == null) this.mIntervalEventHash = new HashMap(); this.mIntervalEventHash.put(((ActiveLinkCollection)localObject).getJSFunctionName(), new Integer(localExecutionEvent.getInteval())); }  }    }
/*   */       } catch (GoatException localGoatException1) {
/* 1 */         MLog.log(Level.FINE, "Caught exception while collecting activelinks - dropping " + arg4[j].toString(), localGoatException1); }   } 
/* 1 */   private static Guide[] getContainerObjectsFromServer(ServerLogin arg0, String arg1) throws GoatException { ContainerOwner[] arrayOfContainerOwner = { new ContainerOwner(2, arg1) };
/*   */     List localList;
/*   */     try { localList = arg0.getListContainer(0L, new int[] { ContainerType.GUIDE.toInt() }, true, Arrays.asList(arrayOfContainerOwner), null); } catch (ARException localARException) { throw new GoatException(localARException); } if (localList == null) return null; localARException = new ArrayList();
/*   */     String str;
/* 1 */     for (Iterator localIterator = localList.iterator(); localIterator.hasNext(); localARException.add(Guide.get(new Guide.Key(arg0, str)))) str = (String)localIterator.next(); return (Guide[])localARException.toArray(new Guide[0]); } 
/* 1 */   private static Guide[] getContainerObjectsFromServer(ServerLogin arg0, String[] arg1) { ArrayList localArrayList = new ArrayList(arg1.length); for (int i = 0; i < arg1.length; i++) try { Guide localGuide = Guide.get(new Guide.Key(arg0, arg1[i])); if (localGuide != null) localArrayList.add(localGuide);  } catch (GoatException localGoatException1) { MLog.log(Level.INFO, localGoatException1.getMessage()); } return (Guide[])localArrayList.toArray(new Guide[localArrayList.size()]); } 
/* 1 */   private Guide[] getGuidesFromActiveLinkActions(ServerLogin arg0, Guide[] arg1) throws GoatException { HashSet localHashSet = new HashSet(); for (ActiveLink localActiveLink : this.mActiveLinks.values()) { if (localActiveLink.getTrueActions().getGuideNames() != null) localHashSet.addAll(localActiveLink.getTrueActions().getGuideNames()); if (localActiveLink.getFalseActions().getGuideNames() != null) localHashSet.addAll(localActiveLink.getFalseActions().getGuideNames());  } if (localHashSet.size() > 0)
/*   */     {
/* 1 */       Object localObject;
/* 1 */       for (int i = 0; (arg1 != null) && (i < arg1.length); i++) { localObject = arg1[i].getGuideName(); if (localHashSet.contains(localObject)) localHashSet.remove(localObject);  } if (localHashSet.size() > 0) { String[] arrayOfString = (String[])localHashSet.toArray(JSWriter.EmptyString); localObject = getContainerObjectsFromServer(arg0, arrayOfString); int j = arg1 == null ? 0 : arg1.length; Guide[] arrayOfGuide = new Guide[j + localObject.length]; for (int k = 0; (arg1 != null) && (k < arg1.length); k++) arrayOfGuide[k] = arg1[k]; for (k = 0; k < localObject.length; k++) arrayOfGuide[(j + k)] = localObject[k]; return arrayOfGuide; }  } return arg1; } 
/* 1 */   private void populateCallguideCollections(ServerLogin arg0, String arg1, String[] arg2, String arg3, String arg4) throws GoatException { this.mGuideCollectionHash = new HashMap(); Guide[] arrayOfGuide = getContainerObjectsFromServer(arg0, arg1); arrayOfGuide = getGuidesFromActiveLinkActions(arg0, arrayOfGuide); if (arrayOfGuide == null) return; for (int i = 0; i < arrayOfGuide.length; i++) { boolean bool = false; int j = 0; String str1 = arrayOfGuide[i].getOriginalGuideName(); GuideALCollection localGuideALCollection = new GuideALCollection(str1, this.mForm.getServer(), arg3, arg4); EPGuideALCollection localEPGuideALCollection = null; Reference[] arrayOfReference = arrayOfGuide[i].getReferences(); if (arrayOfReference != null) { bool = arrayOfGuide[i].isEntryPointGuide(); if (bool) localEPGuideALCollection = new EPGuideALCollection(localGuideALCollection, this.mForm.getServer(), arg3, arg4); for (int k = 0; k < arrayOfReference.length; k++)
/*   */         {
/* 1 */           String str2;
/* 1 */           if (arrayOfReference[k].getReferenceType().equals(ReferenceType.ACTIVELINK)) { str2 = arrayOfReference[k].getName(); String str3 = str2; ActiveLink localActiveLink1 = (ActiveLink)this.mActiveLinks.get(str3); if ((j != 0) && (localActiveLink1 == null)) localActiveLink1 = ActiveLink.get(arg0.getServer(), str3, arg3, arg4); if ((localActiveLink1 != null) && (localActiveLink1.isEnabled())) { if (bool) { if (j == 0) { assert (localEPGuideALCollection != null); localEPGuideALCollection.add(localActiveLink1); } else { ActiveLink localActiveLink2 = ActiveLink.getAsEntryPointStartingAL(arg0.getServer(), str3, arg0.getServer(), arg1, arrayOfGuide[i].getGuideName(), arg3, arg4); localGuideALCollection.add(localActiveLink2); j = 0; } } else localGuideALCollection.add(localActiveLink1);  } else MLog.fine("Dropping al " + str2 + " from guide " + localGuideALCollection.getName());  } else if (arrayOfReference[k].getReferenceType().equals(ReferenceType.NULL_STRING)) { str2 = new String(((ExternalReference)arrayOfReference[k]).getLabel()); if (bool) { assert (localEPGuideALCollection != null); localEPGuideALCollection.add(new ActiveLinkLabel(str2)); } else { localGuideALCollection.add(new ActiveLinkLabel(str2)); }  } if (arrayOfReference[k].getReferenceType().equals(ReferenceType.ARREF_ENTRYPOINT_START_ACTLINK)) j = 1;  }  } this.mGuideCollectionHash.put(localGuideALCollection.getName(), localGuideALCollection); if (bool) this.mGuideCollectionHash.put(localEPGuideALCollection.getName(), localEPGuideALCollection);  }  } 
/* 1 */   private void removeEvents(int arg0) { ExecutionEvent localExecutionEvent = new ExecutionEvent(arg0); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(14)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(15)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(16)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(17)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(18)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(19)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(20)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(21)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(22)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(23)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(24)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(25)); this.mCollectionHash.remove(localExecutionEvent.getCollectionName(26)); } 
/* 1 */   public void filterFormCollections(OutputNotes arg0) { if (!arg0.isPerformActionDynamic()) { Iterator localIterator = arg0.getFieldSetInJS().iterator(); Set localSet1 = arg0.getParentExecDep();
/*   */       label28: Integer localInteger;
/* 1 */       for (Set localSet2 = arg0.getFieldSetInRunProcess(); localIterator.hasNext(); removeEvents(localInteger.intValue())) { localInteger = (Integer)localIterator.next(); if ((localSet2.contains(localInteger)) || (localSet1.contains(localInteger))) break label28;  } for (localIterator = arg0.getParentHidden().iterator(); localIterator.hasNext(); removeEvents(localInteger.intValue())) localInteger = (Integer)localIterator.next();  } else { MPerformanceLog.fine("Untriggerable event optimization disabled due to dynamic run-process action"); }  } 
/* 1 */   public GuideALCollection getGuideALCollectionGivenName(String arg0) { assert (arg0 != null); return (GuideALCollection)this.mGuideCollectionHash.get(arg0); } 
/* 1 */   public Map getAllActiveLinks() { HashMap localHashMap = new HashMap(); assert ((this.mCollectionHash != null) && (this.mGuideCollectionHash != null));
/*   */     ActiveLinkCollection localActiveLinkCollection;
/* 1 */     for (Iterator localIterator = this.mCollectionHash.values().iterator(); localIterator.hasNext(); localActiveLinkCollection.collectAllActivelinks(localHashMap)) localActiveLinkCollection = (ActiveLinkCollection)localIterator.next(); for (localIterator = this.mGuideCollectionHash.values().iterator(); localIterator.hasNext(); localActiveLinkCollection.collectAllActivelinks(localHashMap)) localActiveLinkCollection = (ActiveLinkCollection)localIterator.next(); return localHashMap; } 
/* 1 */   private void emitActivelinks(Emitter arg0) throws GoatException { Map localMap = getAllActiveLinks(); Iterator localIterator = localMap.values().iterator();
/*   */     ActiveLink localActiveLink;
/* 1 */     for (int i = 0; localIterator.hasNext(); localActiveLink.emitJS(arg0)) { localActiveLink = (ActiveLink)localIterator.next(); localActiveLink.setNameAlias("" + i++); }  } 
/* 1 */   private void emitIntervalEvents(Emitter arg0) { if (this.mIntervalEventHash == null) return; JSWriter localJSWriter = arg0.js(); localJSWriter.startThisFunction("RegisterIntervalEvents", "windowID"); for (Iterator localIterator = this.mIntervalEventHash.keySet().iterator(); localIterator.hasNext(); localJSWriter.endStatement()) { String str = (String)localIterator.next(); int i = ((Integer)this.mIntervalEventHash.get(str)).intValue(); localJSWriter.statement("ARRegisterInterval(windowID,this." + str + " ," + i + ")"); } localJSWriter.endFunction(); } 
/* 1 */   public void emitFormCollections(JSWriter arg0, OutputNotes arg1, IEmitterFactory arg2) throws GoatException { Emitter localEmitter = new Emitter(arg0, arg1, arg2); emitActivelinks(localEmitter);
/*   */     Object localObject;
/* 1 */     for (Iterator localIterator = this.mGuideCollectionHash.values().iterator(); localIterator.hasNext(); ((GuideALCollection)localObject).emitJS(localEmitter)) localObject = (GuideALCollection)localIterator.next(); for (localIterator = this.mCollectionHash.values().iterator(); localIterator.hasNext(); ((ActiveLinkCollection)localObject).emitJS(localEmitter)) localObject = (ActiveLinkCollection)localIterator.next(); emitIntervalEvents(localEmitter); } 
/* 1 */   public void addToOutputNotes(OutputNotes arg0) { Map localMap = getAllActiveLinks();
/*   */     ActiveLink localActiveLink;
/* 1 */     for (Iterator localIterator = localMap.values().iterator(); localIterator.hasNext(); localActiveLink.addToOutputNotes(arg0)) localActiveLink = (ActiveLink)localIterator.next();  } 
/* 1 */   private void writeObject(ObjectOutputStream arg0) throws IOException { arg0.defaultWriteObject(); String str1 = this.mForm.getServerName(); String str2 = this.mForm.getName(); arg0.writeObject(str1); arg0.writeObject(str2); } 
/* 1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { arg0.defaultReadObject(); String str1 = (String)arg0.readObject(); String str2 = (String)arg0.readObject();
/*   */     try { this.mForm = Form.get(str1, str2); }
/*   */     catch (GoatException localGoatException)
/*   */     {
/*   */     }
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ActiveLinkCollector
 * JD-Core Version:    0.6.1
 */