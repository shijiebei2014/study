/*    */ package com.remedy.arsys.goat.preferences;
/*    */ 
/*    */ import com.bmc.arsys.api.ARException;
/*    */ import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
/*    */ import com.bmc.arsys.api.CoreFieldId;
/*    */ import com.bmc.arsys.api.DataType;
/*    */ import com.bmc.arsys.api.Entry;
/*    */ import com.bmc.arsys.api.EntryListFieldInfo;
/*    */ import com.bmc.arsys.api.Field;
/*    */ import com.bmc.arsys.api.FormType;
/*    */ import com.bmc.arsys.api.OperandType;
/*    */ import com.bmc.arsys.api.QualifierInfo;
/*    */ import com.bmc.arsys.api.RelationalOperationInfo;
/*    */ import com.bmc.arsys.api.SelectionFieldLimit;
/*    */ import com.bmc.arsys.api.Timestamp;
/*    */ import com.bmc.arsys.api.Value;
/*    */ import com.remedy.arsys.config.Configuration;
/*    */ import com.remedy.arsys.goat.ARBox;
/*    */ import com.remedy.arsys.goat.Box;
/*    */ import com.remedy.arsys.goat.CachedFieldMap;
/*    */ import com.remedy.arsys.goat.EnumLimit;
/*    */ import com.remedy.arsys.goat.Form;
/*    */ import com.remedy.arsys.goat.GoatException;
/*    */ import com.remedy.arsys.goat.aspects.IARUserPreferencesServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.intf.service.IARUserPreferencesService;
/*    */ import com.remedy.arsys.log.Log;
/*    */ import com.remedy.arsys.share.Cache.Item;
/*    */ import com.remedy.arsys.share.JSWriter;
/*    */ import com.remedy.arsys.share.ServerInfo;
/*    */ import com.remedy.arsys.share.ServiceLocator;
/*    */ import com.remedy.arsys.stubs.ServerLogin;
/*    */ import com.remedy.arsys.stubs.SessionData;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ import java.util.logging.Level;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public class ARUserPreferences extends AbstractARUserPreferences
/*    */   implements Cloneable, Cache.Item
/*    */ {
/*    */   private static final long serialVersionUID = -3176513852736629748L;
/*  1 */   private static final ArithmeticOrRelationalOperand SUBMITTER_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.Submitter.getFieldId()); private static final ArithmeticOrRelationalOperand MOD_DATE_OP = new ArithmeticOrRelationalOperand(CoreFieldId.ModifiedDate.getFieldId()); private static final int[] PREF_SCHEMA_KEY = { 30002 }; private static final Value NULL_VALUE = new Value();
/*    */   private QualifierInfo mQueryQual;
/*    */   private HashMap mPrefs;
/*    */   private long mPrefServerLoadedDate;
/*    */   private String mUser;
/*    */   private String mServer;
/*    */   private String mForm;
/*    */   private String mEntryID;
/*    */   private final Timestamp mLastUpdatedTimestamp;
/*  1 */   private static transient Log MLog = Log.get(2);
/*    */ 
/*  1 */   public ARUserPreferences(String paramString) { this.mLastUpdatedTimestamp = new Timestamp(0L); this.mPrefs = ((HashMap)DEFAULT_PREFS.clone()); setMUser(paramString); addConfiguredPreferences(); } 
/*  1 */   public void setMUser(String arg0) { this.mUser = arg0; } 
/*  1 */   public String getMUser() { return this.mUser; } 
/*  1 */   public void setMServer(String arg0) { this.mServer = arg0; } 
/*  1 */   public String getMServer() { return this.mServer; } 
/*  1 */   public void setMForm(String arg0) { this.mForm = arg0; } 
/*  1 */   public String getMForm() { return this.mForm; } 
/*  1 */   public void setMEntryID(String arg0) { this.mEntryID = arg0; } 
/*  1 */   public String getMEntryID() { return this.mEntryID; } 
/*  1 */   public void setMPrefServerLoadedDate(long arg0) { this.mPrefServerLoadedDate = arg0; } 
/*  1 */   public long getMPrefServerLoadedDate() { return this.mPrefServerLoadedDate; } 
/*    */   public Object clone() { try { ARUserPreferences localARUserPreferences = (ARUserPreferences)super.clone(); synchronized (this.mPrefs) { localARUserPreferences.mPrefs = ((HashMap)this.mPrefs.clone()); } return localARUserPreferences; } catch (CloneNotSupportedException localCloneNotSupportedException1) {  } return null; } 
/*  1 */   public void flush() throws GoatException { if ((getMServer() != null) && (getMForm() != null)) { savePreferencesToServer(); loadFromServer(getMServer()); }  } 
/*  1 */   protected ARUserPreferences syncHelper() { String str = getMUser().intern();
/*    */     ARUserPreferences localARUserPreferences;
/*  1 */     synchronized (str) { localARUserPreferences = new ARUserPreferences(str); } return localARUserPreferences; } 
/*  1 */   public void sync() { ARUserPreferences localARUserPreferences2 = this; ARUserPreferences localARUserPreferences1 = syncHelper_aroundBody1$advice(this, localARUserPreferences2, IARUserPreferencesServiceCacheAspect.aspectOf(), localARUserPreferences2, null); assert (localARUserPreferences1 != null); synchronized (localARUserPreferences1.mPrefs) { this.mPrefs = ((HashMap)localARUserPreferences1.mPrefs.clone()); }  } 
/*  1 */   public void put(PreferencesKey arg0, String arg1) { if ((arg0 instanceof ARUserPreferencesKey)) { ARUserPreferencesKey localARUserPreferencesKey1 = (ARUserPreferencesKey)arg0; if (localARUserPreferencesKey1.getPrefName() != null) { ARUserPreferencesKey localARUserPreferencesKey2 = localARUserPreferencesKey1.getRootKey(); String str1 = get(localARUserPreferencesKey2); if (str1 == null) str1 = ""; if ((str1 instanceof String)) { String str2 = setCompoundVal(str1, localARUserPreferencesKey1.getPrefName(), arg1); put(localARUserPreferencesKey2, str2); }  } else { setPreferenceValue(localARUserPreferencesKey1, arg1, null, getMForm()); }  }  } 
/*  1 */   public String get(PreferencesKey arg0) { if ((arg0 instanceof ARUserPreferencesKey)) { ARUserPreferencesKey localARUserPreferencesKey = (ARUserPreferencesKey)arg0;
/*    */       Object localObject1;
/*  1 */       if (localARUserPreferencesKey.getPrefName() != null) { localObject1 = localARUserPreferencesKey.getRootKey(); return getFromCompoundVal(localARUserPreferencesKey.getPrefName(), get((PreferencesKey)localObject1)); } synchronized (this.mPrefs) { if (!this.mPrefs.containsKey(localARUserPreferencesKey)) { localObject1 = getPrefFromServer(localARUserPreferencesKey); put(localARUserPreferencesKey, (String)localObject1); } localObject1 = (String)this.mPrefs.get(localARUserPreferencesKey); } return localObject1; } return null; } 
/*  1 */   private String getFromCompoundVal(String arg0, String arg1) { Pattern localPattern = Pattern.compile(arg0 + "=([^\n]*)"); Matcher localMatcher = localPattern.matcher(arg1); if (localMatcher.find()) { String str = localMatcher.group(); return str; } return null; } 
/*  1 */   private String setCompoundVal(String arg0, String arg1, String arg2) { Pattern localPattern = Pattern.compile(arg1 + "=[^\n]*"); Matcher localMatcher = localPattern.matcher(arg0); if (localMatcher.find()) { StringBuffer localStringBuffer = new StringBuffer(); String str = arg1 + '=' + arg2; localMatcher.appendReplacement(localStringBuffer, str); localMatcher.appendTail(localStringBuffer); return localStringBuffer.toString(); } return arg0 + arg1 + '=' + arg2 + '\n'; } 
/*  1 */   public static ARUserPreferences getUserPreferences() { return ((IARUserPreferencesService)ServiceLocator.getInstance().getService("arUserPreferencesService")).getUserPreferences(); } 
/*  1 */   public void emitJS(JSWriter arg0) throws GoatException { arg0.startStatement("ARPrefsReplaceAll("); arg0.openObj(); for (int i = BROWSER_PREFS.length - 1; i >= 0; i--) { String str1 = "" + BROWSER_PREFS[i].getFieldID(); String str2 = get(BROWSER_PREFS[i]); if (str2 == null) arg0.property(str1); else arg0.property(str1, str2);  } arg0.closeObj(); arg0.append(")"); arg0.endStatement(); } 
/*  1 */   public void emitTablePrefs(JSWriter arg0, String arg1, String arg2, int arg3) throws GoatException { if ((getMServer() == null) || (getMForm() == null)) return; arg0.startStatement("this.tablePrefs="); arg0.openObj(); String str1 = get(AbstractARUserPreferences.TABLE_COLUMN_WIDTH); String str2 = get(AbstractARUserPreferences.TABLE_COLUMN_ORDER); String str3 = get(AbstractARUserPreferences.TABLE_COLUMN_SORT); String str4 = get(AbstractARUserPreferences.TABLE_REFRESH_INTERVAL); arg2 = escape(arg2); JSWriter localJSWriter = new JSWriter(); localJSWriter.openObj(); String str5 = Configuration.getInstance().getShortName(arg1) + ".*";
/*    */     Pattern localPattern;
/*    */     Object localObject;
/*    */     String str6;
/*    */     String str7;
/*    */     int j;
/*  1 */     if (str1 != null) { localPattern = Pattern.compile("^(" + str5 + "\\|" + arg2 + "\\|" + arg3 + "\\|)([0-9]+)=([0-9]+)$", 8); for (localObject = localPattern.matcher(str1); ((Matcher)localObject).find(); localJSWriter.property("'" + str6 + "'", j)) { str6 = ((Matcher)localObject).group(2); str7 = ((Matcher)localObject).group(3); j = new ARBox(0L, 0L, Integer.parseInt(str7), 0L).toBox().mW; }  } localJSWriter.closeObj(); arg0.property("colWs", localJSWriter); localJSWriter = new JSWriter(); localJSWriter.openObj();
/*    */     String[] arrayOfString;
/*    */     int m;
/*  1 */     if (str2 != null) { localPattern = Pattern.compile("^(" + str5 + "\\|" + arg2 + "\\|" + arg3 + "\\|)([0-9]+)=([0-9\\|]*)$", 8); localObject = localPattern.matcher(str2); for (j = 1; ((Matcher)localObject).find(); localJSWriter.closeList()) { if (j == 0) localJSWriter.comma(); str6 = ((Matcher)localObject).group(2); str7 = ((Matcher)localObject).group(3); localJSWriter.append("'" + str6 + "':").openList(); if (str7 != null) { arrayOfString = str7.split("\\|"); for (m = 0; m < arrayOfString.length; m++) if (arrayOfString[m].trim().length() > 0) localJSWriter.listSep().append(arrayOfString[m]);  
/*    */         }
/*  1 */         j = 0; }  } localJSWriter.closeObj(); arg0.property("colOrd", localJSWriter); localJSWriter = new JSWriter(); localJSWriter.openObj(); int i = 1;
/*    */     Matcher localMatcher;
/*  1 */     if (str3 != null) { localObject = Pattern.compile("^(" + str5 + "\\|" + arg2 + "\\|" + arg3 + "\\|)([0-9]+)=([0-9\\|\\+\\-]*)$", 8); for (localMatcher = ((Pattern)localObject).matcher(str3); localMatcher.find(); ) { str6 = localMatcher.group(2); str7 = localMatcher.group(3); if (str7 != null) { arrayOfString = str7.split("\\|"); if (arrayOfString.length > 1) { if (i == 0) localJSWriter.comma(); localJSWriter.append("'" + str6 + "':").openList(); m = Integer.parseInt(arrayOfString[0]); for (int n = 1; n <= m; n++) localJSWriter.listSep().append(arrayOfString[n].substring(1)).listSep().append(arrayOfString[n].substring(0, 1) + "1"); i = 0; localJSWriter.closeList(); }  }  } }
/*  1 */     localJSWriter.closeObj(); arg0.property("colSort", localJSWriter); localJSWriter = new JSWriter(); localJSWriter.openObj(); if (str4 != null) { localObject = Pattern.compile("^(" + str5 + "\\|" + arg2 + "\\|)([0-9]+)=([0-9]+)$", 8);
/*    */       int k;
/*  1 */       for (localMatcher = ((Pattern)localObject).matcher(str4); localMatcher.find(); localJSWriter.property("'" + str6 + "'", k)) { str6 = localMatcher.group(2); k = Integer.parseInt(localMatcher.group(3)); }  } localJSWriter.closeObj(); arg0.property("interval", localJSWriter); arg0.closeObj(); arg0.endStatement(); } 
/*  1 */   public void emitJSForPreference(JSWriter arg0, ARUserPreferencesKey arg1) { arg1 = arg1.getRootKey(); String str1 = get(arg1);
/*    */     String str2;
/*    */     String str3;
/*  1 */     if (str1 == null) { str2 = "ARPrefsSetAsNull("; str3 = "null"; } else { str2 = "ARPrefsSetAsString("; str3 = JSWriter.escapeString(str1); } arg0.startStatement(str2); arg0.append(arg1.getFieldID()); arg0.append(","); arg0.append(str3); arg0.append(")"); arg0.endStatement(); } 
/*  1 */   private void setPreferenceValue(ARUserPreferencesKey arg0, String arg1, ServerLogin arg2, String arg3) { if ((arg0.getFieldID() >= 20000L) && (arg0.getFieldID() <= 29999L)) { if ((arg2 != null) || (getMEntryID() != null))
/*    */         try
/*    */         {
/*  1 */           ServerLogin localServerLogin;
/*  1 */           if (arg2 == null) localServerLogin = SessionData.get().getServerLogin(getMServer()); else localServerLogin = arg2; String str = getEnumValue(localServerLogin, arg3, arg0.getFieldID(), arg1); if ((str == null) || (str.equals(""))) arg1 = (String)DEFAULT_PREFS.get(arg0); if (USER_LOCALE.equals(arg0)) arg1 = getEnumValue(localServerLogin, arg3, USER_LOCALE.getFieldID(), arg1); else if (CUSTOM_DATE_FORMAT_STR.equals(arg0)) arg1 = getEnumValue(localServerLogin, arg3, CUSTOM_DATE_FORMAT_STR.getFieldID(), arg1);  } catch (GoatException localGoatException1) { localGoatException1.printStackTrace(); }  synchronized (this.mPrefs) { this.mPrefs.put(arg0, arg1); }  }  } 
/*  1 */   private void addConfiguredPreferences() { List localList = Configuration.getInstance().getPreferenceServers(); setMPrefServerLoadedDate(Configuration.getInstance().getPreferenceServerLastModifiedDate()); if (localList.size() > 0) { createQualifier();
/*    */       String str;
/*  1 */       for (Iterator localIterator = localList.iterator(); localIterator.hasNext(); loadFromServer(str)) { str = (String)localIterator.next(); if (getMServer() == null) setMServer(str);  }  }  } 
/*  1 */   private String getPrefFromServer(ARUserPreferencesKey arg0) { if ((getMServer() == null) || (getMForm() == null) || (getMEntryID() == null)) { MLog.fine("No preference server for this user " + getMUser() + " Using default preferences."); return (String)DEFAULT_PREFS.get(arg0); } int i = arg0.getFieldID(); ArrayList localArrayList = new ArrayList(); localArrayList.add(getMEntryID());
/*    */     List localList;
/*    */     try { ServerLogin localServerLogin = SessionData.get().getServerLogin(getMServer()); localList = localServerLogin.getListEntryObjects(getMForm(), localArrayList, new int[] { arg0.getFieldID() }); } catch (ARException localARException) { MLog.log(Level.WARNING, "Could not load preference field " + i + " from the preference server " + getMServer() + " for the user " + getMUser(), localARException); return (String)DEFAULT_PREFS.get(arg0); } catch (GoatException localGoatException) { MLog.log(Level.WARNING, "Could not load preference field " + i + " from the preference server " + getMServer() + " for the user " + getMUser(), localGoatException); return (String)DEFAULT_PREFS.get(arg0); } if (localList.size() > 0) { localGoatException = (Entry)localList.iterator().next(); Value localValue = (Value)((Map.Entry)localGoatException.entrySet().iterator().next()).getValue(); assert (localValue != null); Object localObject = localValue.getValue(); if (localObject == null) return null; return localObject.toString(); } return (String)DEFAULT_PREFS.get(arg0); } 
/*    */   private boolean loadFromServer(String arg0) { try { ServerLogin localServerLogin = ServerLogin.getAdmin(arg0);
/*    */       List localList;
/*    */       try { localList = localServerLogin.getListForm(0L, FormType.ALL.toInt() | 0x400, null, PREF_SCHEMA_KEY); } catch (ARException localARException) { throw new GoatException(localARException); } if ((localList != null) && (localList.size() != 0)) { if (getMForm() == null) setMForm((String)localList.get(0)); for (localARException = 0; localARException < localList.size(); localARException++) if (clearAndLoadDefaultFields(localServerLogin, (String)localList.get(localARException))) { setMForm((String)localList.get(localARException)); setMServer(arg0); MLog.fine("Using the preference server " + getMServer() + " form " + getMForm() + " for user " + getMUser()); return true; }   } } catch (GoatException localGoatException1) {
/*  1 */       MLog.log(Level.WARNING, "Error loading preference from server " + arg0 + " for the user " + getMUser(), localGoatException1); } return false; } 
/*  1 */   public boolean clearAndLoadDefaultFields(ServerLogin arg0, String arg1) throws GoatException { Entry localEntry1 = null; AbstractARUserPreferences.ARVersionInfo localARVersionInfo = getInfoForServerVersion(ServerInfo.get(arg0, true).getVersion());
/*    */     Object localObject1;
/*    */     try { EntryListFieldInfo[] arrayOfEntryListFieldInfo = localARVersionInfo.getEntryCriteria(); int[] arrayOfInt = new int[arrayOfEntryListFieldInfo.length]; for (int j = 0; j < arrayOfEntryListFieldInfo.length; j++) { EntryListFieldInfo localEntryListFieldInfo = arrayOfEntryListFieldInfo[j]; arrayOfInt[j] = localEntryListFieldInfo.getFieldId(); } localObject1 = arg0.getListEntryObjects(arg1, this.mQueryQual, 0, 1, new ArrayList(), arrayOfInt, false, null); if (((List)localObject1).size() > 0) { localEntry1 = (Entry)((List)localObject1).get(0); setMEntryID(localEntry1.getKey()); }  } catch (ARException localARException1) { throw new GoatException(localARException1); } if (localEntry1 != null) { Entry localEntry2 = localEntry1; int i = CoreFieldId.ModifiedDate.getFieldId(); localObject1 = (Value)localEntry2.get(Integer.valueOf(i)); if ((localObject1 != null) && (((Value)localObject1).getValue() != null) && (((Timestamp)((Value)localObject1).getValue()).getValue() <= this.mLastUpdatedTimestamp.getValue())) return true; synchronized (this.mPrefs) { this.mPrefs.clear(); } for (??? = localEntry2.entrySet().iterator(); ((Iterator)???).hasNext(); ) { Map.Entry localEntry = (Map.Entry)((Iterator)???).next(); int k = ((Integer)localEntry.getKey()).intValue(); Value localValue = (Value)localEntry.getValue(); assert (localValue != null); if (CoreFieldId.ModifiedDate.getFieldId() == k) { assert ((localValue.getDataType().equals(DataType.TIME)) || (localValue.getDataType().equals(DataType.NULL))); if (localValue.getDataType().equals(DataType.TIME)) this.mLastUpdatedTimestamp.setValue(((Timestamp)localValue.getValue()).getValue()); else this.mLastUpdatedTimestamp.setValue(0L);  } else { setPreferenceValue(new ARUserPreferencesKey(k), localValue.toString(), arg0, arg1); }  } return true; } return false; } 
/*  1 */   private Field getPreferenceField(int arg0) throws GoatException { assert ((getMServer() != null) && (getMForm() != null) && (arg0 > 0)); ServerLogin localServerLogin = SessionData.get().getServerLogin(getMServer()); CachedFieldMap localCachedFieldMap = Form.get(localServerLogin.getServer(), getMForm()).getCachedFieldMap(true); return (Field)localCachedFieldMap.get(Integer.valueOf(arg0)); } 
/*  1 */   private String getEnumValue(ServerLogin arg0, String arg1, int arg2, String arg3) throws GoatException { assert ((arg0 != null) && (arg1 != null) && (arg2 > 0)); if (arg3 != null) { CachedFieldMap localCachedFieldMap = Form.get(arg0.getServer(), arg1).getCachedFieldMap(true); Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(arg2)); if ((localField != null) && (localField.getDataType() == DataType.ENUM.toInt())) try { long l = Long.parseLong(arg3); SelectionFieldLimit localSelectionFieldLimit = (SelectionFieldLimit)localField.getFieldLimit(); if (localSelectionFieldLimit != null) { EnumLimit localEnumLimit = new EnumLimit(localSelectionFieldLimit); return localEnumLimit.valToString(l); }  } catch (NumberFormatException localNumberFormatException1) {  }
/*    */   } return arg3; } 
/*  1 */   private void createQualifier() { ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(getMUser())); RelationalOperationInfo localRelationalOperationInfo = new RelationalOperationInfo(1, SUBMITTER_OP, localArithmeticOrRelationalOperand); this.mQueryQual = new QualifierInfo(localRelationalOperationInfo); } 
/*  1 */   private void savePreferencesToServer() throws GoatException { assert ((getMServer() != null) && (getMForm() != null)); ServerLogin localServerLogin = SessionData.get().getServerLogin(getMServer());
/*    */     try { Entry localEntry = getPreferenceValuesToSave(); if (getMEntryID() == null) localServerLogin.createEntry(getMForm(), localEntry); else localServerLogin.setEntry(getMForm(), getMEntryID(), localEntry, new Timestamp(), 1);  } catch (ARException localARException1) { throw new GoatException(localARException1); }  } 
/*    */   private static Value getAREnumValueFromString(Field arg0, String arg1) { try { return new Value(arg1, DataType.ENUM); } catch (NumberFormatException localNumberFormatException) { SelectionFieldLimit localSelectionFieldLimit = (SelectionFieldLimit)arg0.getFieldLimit(); if (localSelectionFieldLimit != null) { EnumLimit localEnumLimit = new EnumLimit(localSelectionFieldLimit); long l = localEnumLimit.stringToVal(arg1); if (l != 9223372036854775807L) return new Value(Long.valueOf(l), DataType.ENUM);  }  }
/*  1 */     return new Value(); } 
/*  1 */   private Entry getPreferenceValuesToSave() throws GoatException { assert ((getMServer() != null) && (getMForm() != null)); Entry localEntry = new Entry(); ServerLogin localServerLogin = ServerLogin.getAdmin(getMServer()); AbstractARUserPreferences.ARVersionInfo localARVersionInfo = getInfoForServerVersion(ServerInfo.get(localServerLogin, true).getVersion()); List localList = Arrays.asList(localARVersionInfo.getKnownKeys()); synchronized (this.mPrefs) { for (Map.Entry localEntry1 : this.mPrefs.entrySet()) { ARUserPreferencesKey localARUserPreferencesKey = (ARUserPreferencesKey)localEntry1.getKey(); if (localList.contains(localARUserPreferencesKey)) { int i = localARUserPreferencesKey.getFieldID(); Field localField = getPreferenceField(i); if (localField != null) { String str = (String)localEntry1.getValue();
/*    */             Value localValue;
/*    */             try { if (str == null) localValue = NULL_VALUE; else if (DataType.ENUM.toInt() == localField.getDataType()) localValue = getAREnumValueFromString(localField, str); else if ((localARUserPreferencesKey.equals(SESSION_TIMEOUT_IN_MINUTES)) && (str.equals("-1"))) localValue = NULL_VALUE; else localValue = new Value(str, DataType.toDataType(localField.getDataType()));  } catch (Exception localException) { MLog.log(Level.SEVERE, "Cannot convert preference value to AR Value for field id " + i + " value trying to be saved is " + str, localException); throw new GoatException("Cannot convert preference value to AR Value for field id " + i + " value trying to be saved is " + str); } localEntry.put(Integer.valueOf(i), localValue); }  }  } }
/*  1 */     return localEntry; } 
/*  1 */   public Long getDisplayTimeFormat() { Long localLong = super.getDisplayTimeFormat(); return localLong == null ? SHORT_TIME_FORMAT : localLong; } 
/*  1 */   public int getSize() { return 1; } 
/*  1 */   public String getServer() { return getMServer(); } 
/*  1 */   public static final String escape(String arg0) { assert (arg0 != null); StringBuilder localStringBuilder = new StringBuilder(arg0.length()); char[] arrayOfChar = arg0.toCharArray(); int i = 0; for (int j = 0; j < arrayOfChar.length; j++) switch (arrayOfChar[j]) { case '*':
/*  1 */         localStringBuilder.append(arrayOfChar, i, j - i); localStringBuilder.append("\\*"); i = j + 1; break;
/*    */       case '?':
/*  1 */         localStringBuilder.append(arrayOfChar, i, j - i); localStringBuilder.append("\\?"); i = j + 1; break;
/*    */       case '^':
/*  1 */         localStringBuilder.append(arrayOfChar, i, j - i); localStringBuilder.append("\\^"); i = j + 1; break;
/*    */       case '$':
/*  1 */         localStringBuilder.append(arrayOfChar, i, j - i); localStringBuilder.append("\\$"); i = j + 1; } localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i); return localStringBuilder.toString(); } 
/*  1 */   public Long getLimitQueryItemsWithPreferenceServer(String arg0) { Long localLong = getLimitQueryItems(); if (localLong.equals(NO)) try { if (ServerInfo.get(arg0).getGetListECount() > 0) localLong = YES;  } catch (GoatException localGoatException) {  }
/*    */  return localLong; } 
/*  1 */   public int getMaxQueryItemsWithPreferenceServer(String arg0) { int i = getMaxQueryItems();
/*    */     try { int j = ServerInfo.get(arg0).getGetListECount(); if (getLimitQueryItems().equals(NO)) i = j; else if ((j == 0) && (i == 0)) i = 1000; else if ((j != 0) && ((i == 0) || (i > j))) i = j;  } catch (GoatException localGoatException1) {  }
/*  1 */     return i;
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.preferences.ARUserPreferences
 * JD-Core Version:    0.6.1
 */