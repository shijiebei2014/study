/*    */ package com.remedy.arsys.goat;
/*    */ 
/*    */ import com.bmc.arsys.api.ARException;
/*    */ import com.bmc.arsys.api.DataType;
/*    */ import com.bmc.arsys.api.Field;
/*    */ import com.bmc.arsys.api.FormCriteria;
/*    */ import com.bmc.arsys.api.ObjectPropertyMap;
/*    */ import com.bmc.arsys.api.PropInfo;
/*    */ import com.bmc.arsys.api.PropertyMap;
/*    */ import com.bmc.arsys.api.StatusInfo;
/*    */ import com.bmc.arsys.api.Timestamp;
/*    */ import com.bmc.arsys.api.Value;
/*    */ import com.bmc.arsys.api.View;
/*    */ import com.bmc.arsys.api.ViewCriteria;
/*    */ import com.bmc.arsys.api.ViewDisplayPropertyMap;
/*    */ import com.remedy.arsys.config.Configuration;
/*    */ import com.remedy.arsys.goat.aspects.IFieldMapServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.aspects.IFormServiceCacheAspect;
/*    */ import com.remedy.arsys.goat.aspects.skins.ViewInfoAspect;
/*    */ import com.remedy.arsys.goat.intf.service.IFieldMapService;
/*    */ import com.remedy.arsys.goat.intf.service.IFormService;
/*    */ import com.remedy.arsys.goat.preferences.ARUserPreferences;
/*    */ import com.remedy.arsys.log.Log;
/*    */ import com.remedy.arsys.log.MeasureTime.Measurement;
/*    */ import com.remedy.arsys.share.ActorViewCache;
/*    */ import com.remedy.arsys.share.Cache;
/*    */ import com.remedy.arsys.share.Cache.Item;
/*    */ import com.remedy.arsys.share.HTMLWriter;
/*    */ import com.remedy.arsys.share.JSWriter;
/*    */ import com.remedy.arsys.share.MessageTranslation;
/*    */ import com.remedy.arsys.share.MiscCache;
/*    */ import com.remedy.arsys.share.ServerInfo;
/*    */ import com.remedy.arsys.share.UserActorCache;
/*    */ import com.remedy.arsys.stubs.ServerLogin;
/*    */ import com.remedy.arsys.stubs.SessionData;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Date;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ import java.util.logging.Level;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import org.aspectj.runtime.internal.Conversions;
/*    */ 
/*    */ public class Form
/*    */   implements Cache.Item
/*    */ {
/*    */   private final long mLastUpdateTimeMs;
/*    */   private static IFormService FORM_SERVICE;
/*    */   private static IFieldMapService FIELDMAP_SERVICE;
/*  1 */   private static final Pattern BGCOLOR = Pattern.compile("bgcolor=([#0-9A-Fa-f]{7})", 11); private static final Pattern BGAPPCOLOR = Pattern.compile("background=(?:\"(.*?)\"|'(.*?)'|([^>\\s]*))", 11);
/*    */   private final ArrayList mViews;
/*    */   private transient Map mNameMap;
/*    */   private transient Map mIDMap;
/*  1 */   private static final FormCriteria REQUIRED_SCHEMA_PROPS = new FormCriteria(); private static final FormCriteria MINIMAL_SCHEMA_PROPS = new FormCriteria(); private static final ViewCriteria REQUIRED_VIEW_PROPS = new ViewCriteria(); private static final FormCriteria HELPTEXT_SCHEMA_PROPS = new FormCriteria();
/*    */   public static Log MPerformanceLog;
/*    */   static Log MFieldLog;
/*    */   static Log MInternalLog;
/*    */   private static final transient Log cacheLog;
/*    */   private static Cache TimeStampCache;
/*    */   private final Set mGrantedKeys;
/*    */   private final Set mForbiddenKeys;
/*    */   private com.bmc.arsys.api.Form mSchema;
/*    */   private String mAppName;
/*    */   private String mWebAlias;
/*    */   private String mHelpText;
/*    */   private boolean mIsHelpTextInit;
/*    */   private String mServerName;
/*    */   private String mTag;
/*    */   private transient String mServerFormNames;
/*    */ 
/*    */   public static IFormService setFormService(IFormService arg0)
/*    */   {
/*  1 */     FORM_SERVICE = arg0; return arg0; } 
/*  1 */   public static IFieldMapService setFieldMapService(IFieldMapService arg0) { FIELDMAP_SERVICE = arg0; return arg0; } 
/*  1 */   public CachedFieldMap getCachedFieldMap() throws GoatException { return getCachedFieldMap(false); } 
/*  1 */   public CachedFieldMap getCachedFieldMap(boolean arg0) throws GoatException { boolean bool = arg0; Form localForm = this; IFieldMapService localIFieldMapService = FIELDMAP_SERVICE; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = this; arrayOfObject[1] = localIFieldMapService; arrayOfObject[2] = localForm; arrayOfObject[3] = Conversions.booleanObject(bool); return IFieldMapServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFieldMapServiceCacheAspect$1$37a4c128(localForm, bool, new Form.AjcClosure1(arrayOfObject)); } 
/*  1 */   public static final String getFormFieldCacheKey(String arg0, String arg1, int arg2) { return (arg0 + "/" + arg1 + "/" + arg2).intern(); } 
/*  1 */   public ViewInfo getViewInfo(String arg0) throws GoatException { ViewInfo localViewInfo = (ViewInfo)this.mNameMap.get(arg0); if (localViewInfo == null) throw new GoatException(9354); return localViewInfo; } 
/*  1 */   public ViewInfo getViewInfo(int arg0) throws GoatException { ViewInfo localViewInfo = (ViewInfo)this.mIDMap.get(Integer.valueOf(arg0)); if (localViewInfo == null) throw new GoatException(9354); return localViewInfo; } 
/*  1 */   public ViewInfo getViewInfoByInference(String arg0, boolean arg1, boolean arg2) throws GoatException { SessionData localSessionData = SessionData.get(); String str1 = null;
/*    */     Object localObject1;
/*    */     Object localObject2;
/*  1 */     if ((arg0 == null) || (arg0.length() <= 0)) { MPerformanceLog.fine("View: Searching using User to Actor to View map."); Date localDate = new Date(); localObject1 = localSessionData.getActorViewCache(this.mServerName); if (localObject1 != null) { localObject2 = localSessionData.getUserActorCache(this.mServerName); String str3 = localSessionData.getUserName(); if (localObject2 != null) { MPerformanceLog.fine("View: Attempting to locate actor for user " + str3 + " and application " + this.mAppName); String str4 = ((UserActorCache)localObject2).getActor(this.mAppName, str3); if ((str4 == null) || ("".equals(str4))) { MPerformanceLog.fine("View: Attempting to locate actor for user " + str3 + " and application $NULL$"); str4 = ((UserActorCache)localObject2).getActor(null, str3); if ((str4 == null) || ("".equals(str4))) { MPerformanceLog.fine("View: Attempting to locate actor for user $NULL$ and application " + this.mAppName); str4 = ((UserActorCache)localObject2).getActor(this.mAppName, null); if ((str4 == null) || ("".equals(str4))) { MPerformanceLog.fine("View: Attempting to locate actor for user $NULL$ and application $NULL$"); str4 = ((UserActorCache)localObject2).getActor(null, null); }  }  }
/*  1 */           MPerformanceLog.fine("View: Found actor " + (str4 == null ? "" : str4)); if ((str4 != null) && (!"".equals(str4))) { MPerformanceLog.fine("View: Attempting to locate view label for Actor " + str4 + " and Form " + getName()); str1 = ((ActorViewCache)localObject1).getViewLabel(this.mAppName, getName(), str4); if ((str1 == null) || ("".equals(str1))) { MPerformanceLog.fine("View: Attempting to locate view label for Actor " + str4 + " and Form $NULL$"); str1 = ((ActorViewCache)localObject1).getViewLabel(this.mAppName, null, str4); if ((str1 == null) || ("".equals(str1))) { MPerformanceLog.fine("View: Attempting to locate view label for Actor $NULL$ and Form " + getName()); str1 = ((ActorViewCache)localObject1).getViewLabel(this.mAppName, getName(), null); if ((str1 == null) || ("".equals(str1))) { MPerformanceLog.fine("View: Attempting to locate view label for Actor $NULL$ and Form $NULL$"); str1 = ((ActorViewCache)localObject1).getViewLabel(this.mAppName, null, null); }  }  }
/*  1 */             MPerformanceLog.fine("View: Found view label " + (str1 == null ? "" : str1)); }  }  } }
/*  1 */     boolean bool = Configuration.getInstance().getPreferNativeViews(); if (((arg0 == null) || (arg0.equals(""))) && ((str1 == null) || ("".equals(str1)))) { arg0 = localSessionData.getPreferences().getDefaultFormView(); if ((arg0 == null) && (this.mAppName != null)) try { localObject1 = GoatApplicationContainer.get(getServerLogin(), this.mAppName); localObject2 = (String)((GoatApplicationContainer)localObject1).getViewSet().get(this.mSchema.getName()); if (localObject2 != null) arg0 = (String)localObject2;  } catch (GoatException localGoatException1) { MInternalLog.warning(localGoatException1.getMessage()); } if ((arg0 == null) || (this.mNameMap.get(arg0) == null)) { String str2 = getDefaultVUI(); arg0 = str2 != null ? str2.toString() : ""; }  } int i = -1; int j = -1; for (int k = 0; k < this.mViews.size(); k++) { int m = ((ViewInfo)this.mViews.get(k)).getWeight(arg0, str1, arg1, bool, arg2, localSessionData); if (m > i) { i = m; j = k; }  } if (j == -1) throw new GoatException(9354); return (ViewInfo)this.mViews.get(j); } 
/*  1 */   public Form(String paramString1, String paramString2) throws GoatException { this.mViews = new ArrayList(); this.mNameMap = new HashMap(); this.mIDMap = new HashMap(); this.mGrantedKeys = Collections.synchronizedSet(new HashSet()); this.mForbiddenKeys = Collections.synchronizedSet(new HashSet()); paramString1 = paramString1.toLowerCase(); ServerLogin localServerLogin = ServerLogin.getAdmin(paramString1, SessionData.get().getLocale()); MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(1);
/*    */     try { this.mSchema = localServerLogin.getForm(paramString2, REQUIRED_SCHEMA_PROPS); if (this.mSchema == null) throw new GoatException(9355, paramString2); ObjectPropertyMap localObjectPropertyMap = this.mSchema.getProperties(); for (Object localObject1 = localObjectPropertyMap.entrySet().iterator(); ((Iterator)localObject1).hasNext(); ) { localObject2 = (Map.Entry)((Iterator)localObject1).next(); if (((Integer)((Map.Entry)localObject2).getKey()).intValue() == 90002) { this.mAppName = ((Value)((Map.Entry)localObject2).getValue()).toString();
/*    */           try { GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(localServerLogin, this.mAppName); Set localSet = localGoatApplicationContainer.getFormSet(); if (!localSet.contains(paramString2)) this.mAppName = null;  } catch (GoatException localGoatException) { this.mAppName = null; } } else if (((Integer)((Map.Entry)localObject2).getKey()).intValue() == 60018) { this.mWebAlias = ((Value)((Map.Entry)localObject2).getValue()).toString(); } else if (((Integer)((Map.Entry)localObject2).getKey()).intValue() == 60066) { this.mTag = ((Value)((Map.Entry)localObject2).getValue()).toString(); }  } localObject1 = (View[])localServerLogin.getListViewObjects(paramString2, 0L, REQUIRED_VIEW_PROPS).toArray(new View[0]); Object localObject2 = this.mSchema.getDefaultVUI(); this.mServerName = paramString1; ViewInfo localViewInfo = null; for (int i = 0; i < localObject1.length; i++) localViewInfo = new ViewInfo(localObject1[i], (String)localObject2); this.mServerFormNames = (getServerName().toLowerCase() + "/" + getName()); ServerInfo localServerInfo = ServerInfo.get(this.mServerName, true); long[] arrayOfLong = localServerInfo.getCacheChangeTimes(); TimeStampCache.put(this.mServerName, new FormTimeStamp(arrayOfLong)); this.mLastUpdateTimeMs = this.mSchema.getLastUpdateTime().getValue(); } catch (ARException localARException) { throw new GoatException(localARException); } finally { localMeasurement.end(); }  } 
/*  1 */   protected void initHelpText(String arg0) { this.mHelpText = arg0; if (this.mHelpText != null) { this.mHelpText = HTMLWriter.escape(this.mHelpText); this.mHelpText = this.mHelpText.replaceAll("\\n", "<br>"); }  } 
/*    */   public void initHelpText() throws GoatException { try { ServerLogin localServerLogin = ServerLogin.getAdmin(getServer(), SessionData.get().getLocale()); com.bmc.arsys.api.Form localForm = localServerLogin.getForm(getSchemaKey(), HELPTEXT_SCHEMA_PROPS); if (localForm == null) throw new GoatException(9355, getSchemaKey()); initHelpText(localForm.getHelpText()); setHelpTextInit(true); } catch (ARException localARException1) { throw new GoatException(localARException1); }  } 
/*  1 */   public static long[] getFormTimeStamp(String arg0) { if (TimeStampCache.get(arg0, FormTimeStamp.class) != null) { FormTimeStamp localFormTimeStamp = (FormTimeStamp)TimeStampCache.get(arg0, FormTimeStamp.class); if (localFormTimeStamp != null) return localFormTimeStamp.getTimes();  } return null; } 
/*  1 */   public long getLastUpdateTimeMs() { return this.mLastUpdateTimeMs; } 
/*  1 */   public void checkAccess() throws GoatException { ServerLogin localServerLogin = getServerLogin(); String str = (localServerLogin.getUser() != null) && (localServerLogin.getUser().length() > 0) ? localServerLogin.getUser() + "/" + localServerLogin.getPermissionsKey() : localServerLogin.getPermissionsKey(); if (this.mGrantedKeys.contains(str)) return; if (this.mForbiddenKeys.contains(str)) throw new GoatException(9264, getSchemaKey().toString()); try { com.bmc.arsys.api.Form localForm = localServerLogin.getForm(this.mSchema.getKey(), MINIMAL_SCHEMA_PROPS); if (localForm != null) { this.mGrantedKeys.add(str); return; }  } catch (ARException localARException1) { List localList = localARException1.getLastStatus(); int i = 0; for (StatusInfo localStatusInfo : localList) if (localStatusInfo.getMessageNum() == 353L) { i = 1; break; } if (i == 0) throw new GoatException(localARException1);  } this.mForbiddenKeys.add(str); throw new GoatException(9264, getSchemaKey()); } 
/*  1 */   public static final String getCacheKey(String arg0, String arg1) { return ("Form:" + arg0.toLowerCase() + "/" + arg1).intern(); } 
/*  1 */   public static final Form get(String arg0, String arg1, boolean arg2) throws GoatException { boolean bool = arg2; String str1 = arg1; String str2 = arg0; IFormService localIFormService = FORM_SERVICE; Object[] arrayOfObject = new Object[4]; arrayOfObject[0] = localIFormService; arrayOfObject[1] = str2; arrayOfObject[2] = str1; arrayOfObject[3] = Conversions.booleanObject(bool); return IFormServiceCacheAspect.aspectOf().ajc$around$com_remedy_arsys_goat_aspects_IFormServiceCacheAspect$1$1c93230b(str2, str1, bool, new Form.AjcClosure3(arrayOfObject)); } 
/*  1 */   public static final Form get(String arg0, String arg1) throws GoatException { return get(arg0, arg1, false); } 
/*  1 */   public com.bmc.arsys.api.Form getSchema() { return this.mSchema; } 
/*  1 */   public String getSchemaKey() { return this.mSchema.getKey(); } 
/*  1 */   public String getName() { return this.mSchema.getName(); } 
/*  1 */   public String getTagName() { return this.mTag; } 
/*  1 */   public String getServerFormNames() { return this.mServerFormNames; } 
/*  1 */   public String getDefaultVUI() { return this.mSchema.getDefaultVUI(); } 
/*  1 */   public int[] getFieldIDs(boolean arg0) throws GoatException { CachedFieldMap localCachedFieldMap = getCachedFieldMap(arg0); return localCachedFieldMap.getFieldIDs(); } 
/*  1 */   public String getAppName() { return this.mAppName; } 
/*  1 */   public String getWebAlias() { return this.mWebAlias; } 
/*  1 */   public String getServerName() { return this.mServerName; } 
/*  1 */   public final ServerLogin getServerLogin() throws GoatException { return SessionData.get().getServerLogin(this.mServerName); } 
/*  1 */   public String getHelpText() { String str = MessageTranslation.getLocalizedFormHelp(this.mServerName, getName()); if (str != null) { str = HTMLWriter.escape(str); str = str.replaceAll("\\n", "<br>"); return str; } return this.mHelpText; } 
/*  1 */   public boolean isHelpTextInit() { return this.mIsHelpTextInit; } 
/*  1 */   public void setHelpTextInit(boolean arg0) { this.mIsHelpTextInit = arg0; } 
/*  1 */   public int getSize() { return 1; } 
/*  1 */   public String getServer() { return this.mServerName; } 
/*  1 */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { this.mNameMap = new HashMap(); this.mIDMap = new HashMap(); arg0.defaultReadObject(); this.mServerFormNames = (getServerName().toLowerCase() + "/" + getName());
/*    */     Object localObject;
/*  1 */     if (TimeStampCache.get(this.mServerName, FormTimeStamp.class) == null) try { ServerInfo localServerInfo = ServerInfo.get(this.mServerName, true); localObject = localServerInfo.getCacheChangeTimes(); TimeStampCache.put(this.mServerName, new FormTimeStamp((long[])localObject)); } catch (GoatException localGoatException1) { localGoatException1.printStackTrace(); } for (int i = 0; i < this.mViews.size(); i++) { localObject = (ViewInfo)this.mViews.get(i); ((ViewInfo)localObject).initDetailImage(); ((ViewInfo)localObject).initTitleImage(); this.mIDMap.put(Integer.valueOf(((ViewInfo)localObject).mID), localObject); this.mNameMap.put(((ViewInfo)localObject).mNameMapStr, localObject); }  } 
/*  1 */   static { REQUIRED_SCHEMA_PROPS.setPropertiesToRetrieve(FormCriteria.DEFAULT_VUI | FormCriteria.PROPERTY_LIST | FormCriteria.ENTRYLIST_FIELDLIST | FormCriteria.SORT_LIST | FormCriteria.PERMISSIONS | FormCriteria.SCHEMA_TYPE); MINIMAL_SCHEMA_PROPS.setPropertiesToRetrieve(FormCriteria.LAST_CHANGED); REQUIRED_VIEW_PROPS.setPropertiesToRetrieve(ViewCriteria.VIEW_NAME | ViewCriteria.VUI_TYPE | ViewCriteria.LOCALE | ViewCriteria.PROPERTY_LIST); HELPTEXT_SCHEMA_PROPS.setPropertiesToRetrieve(FormCriteria.HELP_TEXT); MPerformanceLog = Log.get(8); MFieldLog = Log.get(6); MInternalLog = Log.get(11); cacheLog = Log.get(1); TimeStampCache = new MiscCache("timestamp"); Cache.initCacheClasses(); } 
/*    */   public static final class FormFieldItem implements Cache.Item { private static final long serialVersionUID = -5516973492998227942L;
/*    */     private final String server;
/*    */     private final Field arField;
/*    */ 
/*    */     public FormFieldItem(String paramString, Field paramField) { this.server = paramString;
/*    */       this.arField = paramField; } 
/*    */     public String getServer() { return this.server; } 
/*    */     public int getSize() { return 0; } 
/*    */     public Field getField() { return this.arField; }  } 
/*    */   public class LocalImageFetcher extends DisplayPropertyMappers implements GoatImage.Fetcher { private static final long serialVersionUID = -3720150939061490742L;
/*    */     private int mID;
/*    */ 
/*    */     public LocalImageFetcher() {  } 
/*    */     public byte[] reFetchImageData() { return doImageDataRefetch(167); } 
/*    */     private byte[] doImageDataRefetch(int paramInt) { ViewCriteria localViewCriteria = new ViewCriteria();
/*    */       localViewCriteria.setPropertiesToRetrieve(ViewCriteria.VIEW_NAME | ViewCriteria.VUI_TYPE | ViewCriteria.PROPERTY_LIST);
/*    */       View localView;
/*    */       try { localView = Form.this.getServerLogin().getView(Form.this.getName(), this.mID, localViewCriteria);
/*    */         if (localView == null) return null;  } catch (ARException localARException) { return null; } catch (GoatException localGoatException) { return null; } ViewDisplayPropertyMap localViewDisplayPropertyMap = localView.getDisplayProperties();
/*    */       Iterator localIterator = localViewDisplayPropertyMap.entrySet().iterator();
/*    */       while (localIterator.hasNext()) { Map.Entry localEntry = (Map.Entry)localIterator.next();
/*    */         if (((Integer)localEntry.getKey()).intValue() == paramInt) { byte[] arrayOfByte = null;
/*    */           try { Value localValue = (Value)localEntry.getValue();
/*    */             if (localValue.getDataType().equals(DataType.CHAR)) { String str = localValue.getValue().toString();
/*    */               arrayOfByte = GoatImage.getImageReferenceData(str, Form.this.mServerName); } else { arrayOfByte = propToByteArray(localValue); } return arrayOfByte; } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) { Form.MInternalLog.log(Level.SEVERE, localBadDisplayPropertyException.getMessage());
/*    */             return null; }  }  } return null; } 
/*    */     public LocalImageFetcher(int arg2) { int i;
/*    */       this.mID = i; }  } 
/*    */   public class ViewInfo extends DisplayPropertyMappers implements Serializable { private static final long serialVersionUID = 2921030764874272555L;
/*    */     public int mID;
/*    */     public String mName;
/*    */     public String mCountry;
/*    */     public String mLanguage;
/*    */     public boolean mAmDefault;
/*    */     public boolean mAmWebFixed;
/*    */     public boolean mAmNative;
/*    */     public boolean mNoLocale;
/*    */     public boolean mShowToolbar;
/*    */     public boolean mMaximizeWindow;
/*    */     public int mType;
/*    */     public String mLabel;
/*    */     public String mAliasAbbrevPlural;
/*    */     public String mAliasAbbrevSingle;
/*    */     public String mAliasPlural;
/*    */     public String mAliasShortPlural;
/*    */     public String mAliasSingular;
/*    */     public String mAliasShortSingular;
/*    */     public boolean mDetailBannerVisible;
/*    */     public boolean mDetailPaneVisible;
/*    */     public boolean mQueryBannerVisible;
/*    */     public String mDetailColour;
/*    */     public ARBox mDetailBox;
/*    */     public transient GoatImage mDetailImage;
/*    */     public transient GoatImage mTitleImage;
/*    */     private Value mDetailImageValue;
/*    */     private Value mTitleImageValue;
/*    */     public int mDetailImageAlign;
/*    */     public int mDetailImageJustify;
/*    */     public long mMenuAccess;
/*    */     public int mPaneLayout;
/*    */     public boolean mMaster;
/*    */     public PropInfo[] mPossibleResultsListProps;
/*    */     public String mApplicationBackgroundImage;
/*    */     public String mHeaderContent;
/*    */     public String mFooterContent;
/*    */     public LinkedHashMap mDefinedSearches;
/*    */     public String mNameMapStr;
/*    */     public int mRequestIdField;
/*    */     private boolean mRTL;
/*    */     private int mFillStyle;
/*    */     public String mSkinSelector;
/*    */     private int mMarginLeft;
/*    */     private int mMarginRight;
/*    */     private int mMarginTop;
/*    */     private int mMarginBottom;
/*    */     private long mVerSpace;
/*    */     static final int BIGSCREENW = 1600;
/*    */     static final int BIGSCREENH = 1200;
/*    */     private static final int CLIENT_LESS_GOOD_PLATFORM = 1;
/*    */     private static final int CLIENT_PREFERRED_PLATFORM = 2;
/*    */     private static final int EMPTY_LOCALE_MATCH = 4;
/*    */     private static final int COUNTRY_MATCH = 8;
/*    */     private static final int LANGUAGE_MATCH = 16;
/*    */     private static final int DEFAULT_MATCH = 32;
/*    */     private static final int VIEW_LABEL_MATCH = 56;
/*    */     private static final int NAME_MATCH = 64;
/*    */     private static final int EXTENSION_MATCH = 128;
/*    */     private static final int MASTER_MATCH = 256;
/*    */     private static final String PRIMARY_COUNTRY = "_XXX_NO_COUNTRY_MATCH";
/*    */ 
/*  1 */     public Form getForm() { return Form.this; } 
/*  1 */     public void emitBackgroundColor(HTMLWriter arg0) { ViewInfo localViewInfo1 = this; if (getViewBGColor_aroundBody1$advice(this, localViewInfo1, ViewInfoAspect.aspectOf(), localViewInfo1, null) != null) { ViewInfo localViewInfo2 = this; arg0.attr("style", "background-color:" + getViewBGColor_aroundBody3$advice(this, localViewInfo2, ViewInfoAspect.aspectOf(), localViewInfo2, null) + ";"); ViewInfo localViewInfo3 = this; arg0.attr("arcolor", getViewBGColor_aroundBody5$advice(this, localViewInfo3, ViewInfoAspect.aspectOf(), localViewInfo3, null)); }  } 
/*  1 */     public String emitFlowBackgroundColor(HTMLWriter arg0) { StringBuilder localStringBuilder = new StringBuilder(); ViewInfo localViewInfo1 = this; if (getViewBGColor_aroundBody7$advice(this, localViewInfo1, ViewInfoAspect.aspectOf(), localViewInfo1, null) != null) { ViewInfo localViewInfo2 = this; arg0.attr("arcolor", getViewBGColor_aroundBody9$advice(this, localViewInfo2, ViewInfoAspect.aspectOf(), localViewInfo2, null)); ViewInfo localViewInfo3 = this; localStringBuilder.append("background-color:" + getViewBGColor_aroundBody11$advice(this, localViewInfo3, ViewInfoAspect.aspectOf(), localViewInfo3, null) + ";"); } return localStringBuilder.toString(); } 
/*  1 */     public String getViewBGColor() { return this.mDetailColour; } 
/*  1 */     public GoatImage getImage() { return this.mDetailImage; } 
/*  1 */     public void buildBackgroundContainer(HTMLWriter arg0, int arg1, int arg2) throws GoatException { GoatImage localGoatImage = null; int i = arg1; int j = arg2; ViewInfo localViewInfo = this; localGoatImage = getImage_aroundBody13$advice(this, localViewInfo, ViewInfoAspect.aspectOf(), localViewInfo, null); if (this.mApplicationBackgroundImage != null) { final String str = FormContext.get().getApplication(); if (str != null) { GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(Form.this.getServerLogin(), str); Globule localGlobule = localGoatApplicationContainer.getResourceFile(null, this.mApplicationBackgroundImage); if ((localGlobule != null) && (!localGlobule.isCompressed())) try { localGoatImage = GoatImage.put(localGlobule.data(), localGlobule.contentType(), new GoatImage.Fetcher() { private static final long serialVersionUID = -3720150939061490743L;
/*    */ 
/*    */                 public byte[] reFetchImageData() { try { GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(Form.this.getServerLogin(), str);
/*    */                     Globule localGlobule = localGoatApplicationContainer.getResourceFile(null, Form.ViewInfo.this.mApplicationBackgroundImage);
/*    */                     if (localGlobule != null) return localGlobule.data();  } catch (GoatException localGoatException) {  } return null; }  } ); } catch (GoatImage.GoatImageException localGoatImageException) {  }   }  } int k = 0; int m = 0; if ((localGoatImage == null) || ((k = localGoatImage.getW()) <= 0) || ((m = localGoatImage.getH()) <= 0)) return; if (localGoatImage.isOverridden()) { this.mDetailImageAlign = 0; this.mDetailImageJustify = 0; } if ((this.mDetailImageAlign == 5) && (m < 1200) && (arg2 < 1200)) arg2 = 1200; if ((this.mDetailImageJustify == 5) && (k < 1600) && (arg1 < 1600)) arg1 = 1600; localGoatImage.emitAsBackgroundImage(arg0, this.mDetailImageAlign, this.mDetailImageJustify, arg2, arg1, i, j); } 
/*  1 */     public PropInfo[] getPossibleResultsListProps() { return this.mPossibleResultsListProps; } 
/*  1 */     public int getID() { return this.mID; } 
/*  1 */     public String getName() { return this.mName; } 
/*  1 */     public String getLabel() { return this.mLabel != null ? this.mLabel : this.mName; } 
/*  1 */     public boolean getAmWeb() { return this.mAmWebFixed; } 
/*  1 */     public boolean isMaster() { return this.mMaster; } 
/*  1 */     public boolean getAmNative() { return this.mAmNative; } 
/*  1 */     public boolean getShowToolbar() { return this.mShowToolbar; } 
/*  1 */     public Form getContainingForm() { return Form.this; } 
/*  1 */     public int getType() { return this.mType; } 
/*  1 */     public int getFillStyle() { return this.mFillStyle; } 
/*  1 */     public int getMMarginTop() { return this.mMarginTop; } 
/*  1 */     public int getMMarginLeft() { return this.mMarginLeft; } 
/*  1 */     public int getMMarginBottom() { return this.mMarginBottom; } 
/*  1 */     public int getMMarginRight() { return this.mMarginRight; } 
/*  1 */     public long getMVerSpace() { return this.mVerSpace; } 
/*  1 */     public Box getDetailBox() { if (this.mDetailBox == null) return null; return this.mDetailBox.toBox(); } 
/*  1 */     public long getMenuAccessHiddenMask() { return this.mMenuAccess; } 
/*  1 */     public boolean getMaximizeWindow() { return this.mMaximizeWindow; } 
/*  1 */     public int getRequestIdField() { return this.mRequestIdField; } 
/*  1 */     public boolean isRTL() { return this.mRTL; } 
/*    */     private void initDetailImage() { try { if (this.mDetailImageValue != null) this.mDetailImage = propToGoatImage(this.mDetailImageValue, new Form.LocalImageFetcher(Form.this, this.mID), Form.this.getServerName());  } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) { Form.MFieldLog.fine(localBadDisplayPropertyException.toString()); }  } 
/*    */     private void initTitleImage() { try { if (this.mTitleImageValue != null) this.mTitleImage = propToGoatImage(this.mTitleImageValue, new Form.LocalImageFetcher(Form.this, this.mID), Form.this.getServerName());  } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) { Form.MFieldLog.fine(localBadDisplayPropertyException.toString()); }  } 
/*  1 */     public void setmHeaderContent(String arg0) { this.mHeaderContent = arg0; } 
/*  1 */     ViewInfo(View paramString, String arg3) { this.mRequestIdField = 1; this.mRTL = false; this.mFillStyle = 0; this.mName = paramString.getName().toString(); if (this.mName == null) this.mName = "_XXXNO_NAME_MATCH"; String str = paramString.getLocale(); String[] arrayOfString = (str != null) && (str.length() > 0) ? str.split("_") : JSWriter.EmptyString; this.mCountry = ((arrayOfString.length > 1) && (arrayOfString[1].length() > 0) ? arrayOfString[1] : "_XXX_NO_COUNTRY_MATCH"); this.mLanguage = ((arrayOfString.length > 0) && (arrayOfString[0].length() > 0) ? arrayOfString[0] : "_XXX_NO_LANGUAGE_MATCH"); this.mNoLocale = ((arrayOfString.length == 0) || ((arrayOfString.length == 1) && (arrayOfString[0].length() == 0)) || ((arrayOfString.length == 2) && (arrayOfString[0].length() == 0) && (arrayOfString[1].length() == 0))); this.mType = paramString.getVUIType(); this.mAmWebFixed = ((this.mType == 3) || (paramString.getVUIType() == 6)); this.mAmNative = ((this.mType == 1) || (this.mType == 0)); this.mID = paramString.getVUIId(); Form.this.mViews.add(this); Form.this.mIDMap.put(Integer.valueOf(this.mID), this); this.mMenuAccess = 0L; this.mDetailImageAlign = 5; this.mDetailImageJustify = 5; ViewDisplayPropertyMap localViewDisplayPropertyMap = paramString.getDisplayProperties(); int i = 0; int j = 0; ArrayList localArrayList = new ArrayList(); this.mShowToolbar = this.mAmNative;
/*    */       Map.Entry localEntry;
/*    */       int k;
/*    */       Value localValue;
/*  1 */       for (Iterator localIterator = localViewDisplayPropertyMap.entrySet().iterator(); localIterator.hasNext(); ) localEntry = (Map.Entry)localIterator.next(); if (localArrayList.size() > 0) this.mPossibleResultsListProps = ((PropInfo[])localArrayList.toArray(new PropInfo[0])); if ((i != 0) && (j != 0)) this.mDetailBox = new ARBox(0L, 0L, i, j);
/*  1 */       Object localObject;
/*  1 */       if (this.mLabel != null) this.mAmDefault = this.mLabel.equals(localObject.toString()); else this.mAmDefault = this.mName.equals(localObject.toString()); this.mNameMapStr = (this.mLabel != null ? this.mLabel : this.mName); Form.this.mNameMap.put(this.mLabel != null ? this.mLabel : this.mName, this); } 
/*  1 */     private void setBKGColorAndImageFromLegacyView(String arg0) { Matcher localMatcher = Form.BGCOLOR.matcher(arg0); if (localMatcher.find()) this.mDetailColour = localMatcher.group(1); localMatcher = Form.BGAPPCOLOR.matcher(arg0); if (localMatcher.find()) { Object localObject = null; for (int i = 1; i < 4; i++) { String str = localMatcher.group(i); if (str != null) { localObject = str; break; }  } this.mApplicationBackgroundImage = localObject.replaceAll("&quot;", "\""); }  } 
/*  1 */     private int getWeight(String arg0, String arg1, boolean arg2, boolean arg3, boolean arg4, SessionData arg5) { int i = 0; String str1 = arg2 ? arg0 + arg5.getPreferences().getOpenWindowViewExt() : "__FOO_BAR_HI_MA"; if ((!this.mAmWebFixed) && (!this.mAmNative)) return -1; String str2 = this.mLabel != null ? this.mLabel : this.mName; if (str2 == null) str2 = "__NEVER_MATCH"; if (str2.equals(str1)) i += 192; else if (str2.equals(arg0)) i += 64; else if ((arg1 != null) && (!"".equals(arg1)) && (getLabel().equals(arg1))) i += 56; if (this.mAmDefault) { i += 32; localObject = arg5.getPreferences().getOpenWindowViewExt(); if ((localObject != null) && (arg2) && (str2.endsWith((String)localObject))) i += 128;  } Object localObject = arg5.getLocale().split("_"); assert (localObject.length > 0); String str3 = localObject.length > 1 ? (str3 = localObject[1]) : "__FOO_BAR"; String str4 = localObject.length > 0 ? localObject[0] : "__FOO_BAR_TOO"; if (this.mNoLocale) i += 4; if (this.mLanguage.equalsIgnoreCase(str4)) i += 16; if ((this.mCountry.equalsIgnoreCase(str3)) || ((str3.equalsIgnoreCase(str4)) && (this.mCountry.equalsIgnoreCase("_XXX_NO_COUNTRY_MATCH")))) i += 8; if (arg3) { if (this.mAmWebFixed) i++; if (this.mAmNative) i += 2;  } else { if (this.mAmNative) i++; if (this.mAmWebFixed) i += 2;  } if ((arg4) && (this.mMaster)) i = getMasterWeight(i); return i; } 
/*  1 */     private int getMasterWeight(int arg0) { if ((arg0 & 0x10) != 0) { arg0 += 257; if ((arg0 & 0x8) != 0) arg0++;  } if ((arg0 & 0x4) != 0) arg0 += 256; return arg0;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Form
 * JD-Core Version:    0.6.1
 */