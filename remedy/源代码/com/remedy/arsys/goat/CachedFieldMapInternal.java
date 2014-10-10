/*     */ package com.remedy.arsys.goat;
/*     */ 
/*     */ import com.bmc.arsys.api.ARException;
/*     */ import com.bmc.arsys.api.Field;
/*     */ import com.remedy.arsys.goat.aspects.IFormFieldServiceCacheAspect;
/*     */ import com.remedy.arsys.goat.intf.service.IFieldMapService;
/*     */ import com.remedy.arsys.goat.intf.service.IFormFieldService;
/*     */ import com.remedy.arsys.log.Log;
/*     */ import com.remedy.arsys.log.MeasureTime.Measurement;
/*     */ import com.remedy.arsys.share.Cache.Item;
/*     */ import com.remedy.arsys.stubs.ServerLogin;
/*     */ import com.remedy.arsys.stubs.SessionData;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.aspectj.runtime.internal.Conversions;
/*     */ 
/*     */ public class CachedFieldMapInternal
/*     */   implements Cache.Item
/*     */ {
/*     */   private static final long serialVersionUID = -693948815573475099L;
/*   1 */   private static transient Log MPerformanceLog = Log.get(8);
/*     */   transient IFormFieldService formFieldService;
/*     */   String SchemaKey;
/*     */   String serName;
/*     */   Map<Integer, Integer> fieldIDMap;
/*     */   boolean mDataFieldsOnly;
/*     */   private transient Map mMapByDBName;
/*     */ 
/*     */   public CachedFieldMapInternal(String paramString1, String paramString2)
/*     */   {
/*   1 */     this.SchemaKey = null; this.serName = null; this.mMapByDBName = new HashMap(); this.SchemaKey = paramString1; this.serName = paramString2; this.fieldIDMap = new HashMap(); } 
/*   1 */   public CachedFieldMapInternal(String paramString1, String paramString2, String paramString3, boolean paramBoolean, IFieldMapService paramIFieldMapService, IFormFieldService paramIFormFieldService) throws GoatException { this.SchemaKey = null; this.serName = null; this.mMapByDBName = new HashMap(); setFormFieldService(paramIFormFieldService); this.SchemaKey = paramString2; this.serName = paramString3; this.mDataFieldsOnly = paramBoolean; this.fieldIDMap = new HashMap(); MPerformanceLog.fine("Form.CachedFieldMap: Constructing for missing key " + paramString1); List localList1 = null; MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(3); ServerLogin localServerLogin = getServerLogin(); int i = paramBoolean ? 129 : 511;
/*     */     try { localList1 = localServerLogin.getListField(this.SchemaKey, i, 0L); }
/*     */     catch (ARException localARException)
/*     */     {
/*   1 */       throw new GoatException(localARException); } finally { localMeasurement.end(); } if (localList1.size() == 0) return; HashSet localHashSet = new HashSet(); for (Object localObject2 = localList1.iterator(); ((Iterator)localObject2).hasNext(); localHashSet.add(localObject3)) localObject3 = (Integer)((Iterator)localObject2).next(); List localList3 = localList1; String str1 = paramString2; String str2 = paramString3; IFormFieldService localIFormFieldService1 = paramIFormFieldService; Object[] arrayOfObject1 = new Object[5]; arrayOfObject1[0] = this; arrayOfObject1[1] = localIFormFieldService1; arrayOfObject1[2] = str2; arrayOfObject1[3] = str1; arrayOfObject1[4] = localList3; localObject2 = IFormFieldServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFormFieldServiceCacheAspect$4$97447da8(str2, str1, localList3, new CachedFieldMapInternal.AjcClosure1(arrayOfObject1)); Object localObject3 = (List)((List)localObject2).get(0); List localList2 = (List)((List)localObject2).get(1); for (Object localObject4 = ((List)localObject3).iterator(); ((Iterator)localObject4).hasNext(); putFieldID(Integer.valueOf(j), Integer.valueOf(j))) j = ((Integer)((Iterator)localObject4).next()).intValue(); localObject4 = localList2; int j = !isEmpty() ? 1 : 0; if (((List)localObject4).size() > 0) { StringBuilder localStringBuilder = null; Integer[] arrayOfInteger1 = null;
/*     */       Object localObject5;
/*   1 */       if (j != 0) { localStringBuilder = new StringBuilder(); arrayOfInteger1 = new Integer[((List)localObject4).size()]; int k = 0; for (Iterator localIterator = ((List)localObject4).iterator(); localIterator.hasNext(); arrayOfInteger1[(k++)] = Integer.valueOf(((Integer)localObject5).intValue())) { localObject5 = (Integer)localIterator.next(); localStringBuilder.append(localObject5).append(","); } localStringBuilder.deleteCharAt(localStringBuilder.length() - 1); } long l1 = new Date().getTime(); if (j != 0) { Integer[] arrayOfInteger2 = arrayOfInteger1; String str3 = paramString2; String str4 = paramString3; IFormFieldService localIFormFieldService2 = paramIFormFieldService; Object[] arrayOfObject2 = new Object[5]; arrayOfObject2[0] = this; arrayOfObject2[1] = localIFormFieldService2; arrayOfObject2[2] = str4; arrayOfObject2[3] = str3; arrayOfObject2[4] = arrayOfInteger2; localObject5 = IFormFieldServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFormFieldServiceCacheAspect$1$bbb611aa(str4, str3, arrayOfInteger2, new CachedFieldMapInternal.AjcClosure3(arrayOfObject2)); } else { int n = i; String str5 = paramString2; String str6 = paramString3; IFormFieldService localIFormFieldService3 = paramIFormFieldService; localObject5 = getFieldsAsAdmin_aroundBody5$advice(this, localIFormFieldService3, str6, str5, n, IFormFieldServiceCacheAspect.aspectOf(), str6, str5, n, null); } for (int m = 0; m < localObject5.length; m++) { Object localObject6 = localObject5[m]; Integer localInteger = Integer.valueOf(localObject6.getFieldID()); if ((j != 0) || (localHashSet.contains(Integer.valueOf(localInteger.intValue())))) putFieldID(localInteger, localInteger);  } long l2 = new Date().getTime(); if (j != 0) MPerformanceLog.fine("Form.CachedFieldMap: Key " + paramString1 + " missing fields took " + (l2 - l1) + " [" + localStringBuilder.toString() + "]"); else MPerformanceLog.fine("Form.CachedFieldMap: Key " + paramString1 + " all fields took " + (l2 - l1));  }  } 
/*   1 */   public void setFormFieldService(IFormFieldService arg0) { this.formFieldService = arg0; } 
/*   1 */   public int[] getFieldIDs() throws GoatException { Iterator localIterator = this.fieldIDMap.values().iterator(); int[] arrayOfInt = new int[this.fieldIDMap.size()]; for (int i = 0; localIterator.hasNext(); arrayOfInt[(i++)] = ((Integer)localIterator.next()).intValue());
/*   1 */     assert (i == this.fieldIDMap.size()); return arrayOfInt; } 
/*   1 */   public Field get(Integer arg0) { Integer localInteger = (Integer)this.fieldIDMap.get(arg0); if (localInteger == null) return null; int i = localInteger.intValue(); String str1 = this.SchemaKey; String str2 = this.serName; IFormFieldService localIFormFieldService = this.formFieldService; Object[] arrayOfObject = new Object[5]; arrayOfObject[0] = this; arrayOfObject[1] = localIFormFieldService; arrayOfObject[2] = str2; arrayOfObject[3] = str1; arrayOfObject[4] = Conversions.intObject(i); Field localField = IFormFieldServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFormFieldServiceCacheAspect$3$ddf09d4e(str2, str1, i, new CachedFieldMapInternal.AjcClosure7(arrayOfObject)); return localField; } 
/*   1 */   private void putFieldID(Integer arg0, Integer arg1) { this.fieldIDMap.put(arg0, arg1); } 
/*   1 */   public Object put(Object arg0, Object arg1) { Field localField1 = get((Integer)arg0); int i = ((Integer)arg0).intValue(); String str1 = this.SchemaKey; String str2 = this.serName; IFormFieldService localIFormFieldService = this.formFieldService; Object[] arrayOfObject = new Object[5]; arrayOfObject[0] = this; arrayOfObject[1] = localIFormFieldService; arrayOfObject[2] = str2; arrayOfObject[3] = str1; arrayOfObject[4] = Conversions.intObject(i); Field localField2 = IFormFieldServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFormFieldServiceCacheAspect$3$ddf09d4e(str2, str1, i, new CachedFieldMapInternal.AjcClosure9(arrayOfObject)); if (localField2 != null) putFieldID((Integer)arg0, (Integer)arg0); return localField1; } 
/*   1 */   public Object remove(Object arg0) { Field localField = get((Integer)arg0); this.fieldIDMap.remove(arg0); return localField; } 
/*   1 */   public boolean containsKey(Object arg0) { return this.fieldIDMap.containsKey(arg0); } 
/*   1 */   public boolean containsValue(Object arg0) { Field localField = (Field)arg0; int i = localField.getFieldID(); return this.fieldIDMap.containsValue(Integer.valueOf(i)); } 
/*   1 */   public Set entrySet() { Set localSet = this.fieldIDMap.entrySet(); LinkedHashSet localLinkedHashSet = new LinkedHashSet(localSet.size()); for (Iterator localIterator = localSet.iterator(); localIterator.hasNext(); ) { Object localObject = localIterator.next(); Map.Entry localEntry = (Map.Entry)localObject; int i = ((Integer)localEntry.getKey()).intValue(); Field localField = get(Integer.valueOf(i)); if (localField != null) localLinkedHashSet.add(new MapEntry(Integer.valueOf(i), localField));  } return localLinkedHashSet; } 
/*   1 */   public int size() { return this.fieldIDMap.size(); } 
/*   1 */   public void clear() { this.fieldIDMap.clear(); } 
/*   1 */   public boolean isEmpty() { return this.fieldIDMap.isEmpty(); } 
/*   1 */   public Set keySet() { return this.fieldIDMap.keySet(); } 
/*   1 */   public Collection values() { Collection localCollection = this.fieldIDMap.values(); ArrayList localArrayList = new ArrayList(localCollection.size()); for (Iterator localIterator = localCollection.iterator(); localIterator.hasNext(); ) { Object localObject = localIterator.next(); int i = ((Integer)localObject).intValue(); Field localField = get(Integer.valueOf(i)); if (localField != null) localArrayList.add(localField);  } return localArrayList; } 
/*   1 */   public Field getFieldByDBName(String arg0) { synchronized (this.mMapByDBName) { if (this.mMapByDBName.size() != size())
/*     */       {
/*   1 */         Field localField;
/*   1 */         for (Iterator localIterator = values().iterator(); localIterator.hasNext(); this.mMapByDBName.put(localField.getName().toString(), localField)) localField = (Field)localIterator.next(); if ((!$assertionsDisabled) && (this.mMapByDBName.size() != size())) throw new AssertionError();  }  } return (Field)this.mMapByDBName.get(arg0); } 
/*   1 */   public int getSize() { return 1; } 
/*   1 */   public String getServer() { return this.serName; } 
/*   1 */   public String getForm() { return this.SchemaKey; } 
/*   1 */   public boolean isDataFieldsOnly() { return this.mDataFieldsOnly; } 
/*   1 */   public final ServerLogin getServerLogin() throws GoatException { return SessionData.get().getServerLogin(this.serName); } 
/*   1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { arg0.defaultReadObject(); this.mMapByDBName = new HashMap();
/*     */   }
/*     */ 
/*     */   private static final class MapEntry
/*     */     implements Map.Entry
/*     */   {
/*     */     private Object key;
/*     */     private Object value;
/*     */ 
/*     */     public MapEntry(Object paramObject1, Object paramObject2)
/*     */     {
/*     */       this.key = paramObject1;
/*     */       this.value = paramObject2;
/*     */     }
/*     */ 
/*     */     public Object getKey()
/*     */     {
/*     */       return this.key;
/*     */     }
/*     */ 
/*     */     public Object getValue()
/*     */     {
/*     */       return this.value;
/*     */     }
/*     */ 
/*     */     public Object setValue(Object paramObject)
/*     */     {
/*     */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CachedFieldMapInternal
 * JD-Core Version:    0.6.1
 */