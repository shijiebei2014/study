/*   */ package com.remedy.arsys.goat.field;
/*   */ 
/*   */ import com.bmc.arsys.api.ARException;
/*   */ import com.bmc.arsys.api.DataType;
/*   */ import com.bmc.arsys.api.DisplayInstanceMap;
/*   */ import com.bmc.arsys.api.DisplayPropertyMap;
/*   */ import com.bmc.arsys.api.Field;
/*   */ import com.bmc.arsys.api.FieldCriteria;
/*   */ import com.bmc.arsys.api.PropInfo;
/*   */ import com.bmc.arsys.api.TableFieldLimit;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.remedy.arsys.goat.ARBox;
/*   */ import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
/*   */ import com.remedy.arsys.goat.Form;
/*   */ import com.remedy.arsys.goat.Form.ViewInfo;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.goat.GoatImage;
/*   */ import com.remedy.arsys.goat.GoatImage.Fetcher;
/*   */ import com.remedy.arsys.goat.Qualifier;
/*   */ import com.remedy.arsys.goat.field.type.TypeMap;
/*   */ import com.remedy.arsys.log.Log;
/*   */ import com.remedy.arsys.share.MessageTranslation;
/*   */ import com.remedy.arsys.share.PropColorList;
/*   */ import com.remedy.arsys.stubs.ServerLogin;
/*   */ import com.remedy.arsys.stubs.SessionData;
/*   */ import java.io.IOException;
/*   */ import java.io.ObjectInputStream;
/*   */ import java.io.ObjectOutputStream;
/*   */ import java.util.ArrayList;
/*   */ import java.util.Arrays;
/*   */ import java.util.Comparator;
/*   */ import java.util.Iterator;
/*   */ import java.util.Map.Entry;
/*   */ import java.util.Set;
/*   */ import java.util.logging.Level;
/*   */ 
/*   */ public class TableField extends GoatField
/*   */ {
/*   */   private static final long serialVersionUID = 6548014454614320279L;
/* 1 */   protected static final transient Log mLog = Log.get(11);
/*   */   private String mServer;
/*   */   private String mSchema;
/*   */   private String mSampleServer;
/*   */   private String mSampleSchema;
/*   */   private int mMaxRows;
/*   */   private Qualifier mQualifier;
/*   */   private String mAliasSingular;
/*   */   private boolean mAutoFitColumns;
/*   */   private boolean mAutoRefresh;
/*   */   private boolean mDrillDown;
/*   */   private boolean mFixedTableHeaders;
/*   */   private String mQueryListColourString;
/*   */   private String mQueryListBKGColourString;
/*   */   private String[] mQueryListColours;
/*   */   private String[] mQueryListBKGColours;
/*   */   private int mQueryListColoursIndex;
/*   */   private int mQueryListColourField;
/*   */   private int mQueryListBKGColourField;
/*   */   private int mChunkSize;
/*   */   private int mContentClipped;
/*   */   private int mRowHeaderColumn;
/*   */   private int mSelInit;
/*   */   private int mSelRefresh;
/*   */   private int mSelrowsDisable;
/*   */   private int mDisplayType;
/*   */   private boolean mAmInPane;
/*   */   private int mAccess;
/*   */   private boolean mUseLocale;
/*   */   private String mSelectLabel;
/*   */   private boolean mColCheck;
/*   */   private String mRowLabel;
/*   */   private String mRowLabelPlural;
/*   */   private String mChunkNextStr;
/*   */   private String mChunkPrevStr;
/*   */   private String mDeleteStr;
/*   */   private String mDeselectAllStr;
/*   */   private String mEntriesReturnedStr;
/*   */   private String mNotRefreshedStr;
/*   */   private String mReadStr;
/*   */   private String mRefreshStr;
/*   */   private String mReportStr;
/*   */   private String mPreferences;
/*   */   private String mNullValue;
/*   */   private String mSelectAllStr;
/*   */   private String mUnreadStr;
/*   */   private String mAutoRefreshStr;
/*   */   private GoatImageButton mChunkLeftArrow;
/*   */   private GoatImageButton mChunkDownArrow;
/*   */   private GoatImageButton mChunkRightArrow;
/*   */   public static final int MResultsListFieldID = 1020;
/*   */   private String mImgRootAltText;
/*   */   private Value mImgRootValue;
/*   */   private transient GoatImage mImgRoot;
/*   */   private int mLeftMargin;
/*   */   private int mRightMargin;
/*   */   private int mTopMargin;
/*   */   private int mBottomMargin;
/*   */   private int mVisibleColumns;
/*   */   private ARBox mPanelBox;
/*   */   private long mPanelBorderWid;
/*   */   private String mPanelBorderColor;
/*   */   private String mBackgroundColor;
/*   */   private int mPanelHorSpace;
/*   */   private int mPanelVerSpace;
/*   */   private transient GoatImage mImage;
/*   */   private Value mImageValue;
/*   */   private int mJustify;
/*   */   private int mAlign;
/*   */ 
/*   */   public TableField(Form paramForm, Form.ViewInfo paramViewInfo)
/*   */   {
/* 1 */     this.mQueryListColoursIndex = -1; this.mQueryListColourField = 0; this.mQueryListBKGColourField = 0; this.mChunkLeftArrow = new GoatImageButton("tablechunkleft"); this.mChunkDownArrow = new GoatImageButton("tablechunkdown"); this.mChunkRightArrow = new GoatImageButton("tablechunkright"); setMARBox(new ARBox(0L, 0L, 0L, 0L)); setMFieldID(1020); setMDBName("CustomResultsList"); setMInView(true); setMLForm(new GoatField.LightForm(paramForm)); setMDataType(DataType.TABLE); setMDataTypeString(TypeMap.mapDataType(getMDataType(), getMFieldID())); setMFixedTableHeaders(true); setMDrillDown(true); PropInfo[] arrayOfPropInfo = paramViewInfo.getPossibleResultsListProps(); if (arrayOfPropInfo != null) for (int i = 0; i < arrayOfPropInfo.length; i++) { int j = arrayOfPropInfo[i].getPropertyTag(); Value localValue = arrayOfPropInfo[i].getPropertyValue();
/*   */         try { handleProperty(j, localValue); } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) {
/*   */         } }  if (getMSelInit() == 1) setMSelInit(2); setMMaxRows(0); setMServer(setMSchema("@")); setMQualifier(null); if (getMChunkSize() > 0) { setMChunkNextStr(MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "Next Chunk")); setMChunkPrevStr(MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "Previous Chunk")); } initialize(); setMAmInPane(true); } 
/* 1 */   private void initialize() { setMEmitViewable(0); setMEmitOptimised(0); setMEmitViewless(2); if (getMARBox() == null) setMInView(false); if (getMAccess() == 0) setMAccess(2); if (getMFieldID() == 1020L) setMDisplayType(1); else if (getMFieldID() == 706) setMDisplayType(2);
/* 1 */     PropColorList localPropColorList;
/* 1 */     if (getMQueryListColourString() != null) { localPropColorList = new PropColorList(getMQueryListColourString()); if (localPropColorList.isValidColorPropList()) { setMQueryListColourField(localPropColorList.getColorEnumId()); setMQueryListColours(localPropColorList.getColorList()); } else { setMQueryListColourField(0); setMQueryListColours(null); }  } if (getMQueryListBKGColourString() != null) { localPropColorList = new PropColorList(getMQueryListBKGColourString()); if (localPropColorList.isValidColorPropList()) { setMQueryListBKGColorField(localPropColorList.getColorEnumId()); setMQueryListBKGColours(localPropColorList.getColorList()); } else { setMQueryListBKGColorField(0); setMQueryListBKGColours(null); }  } if (getMQueryListColours() == null) setMQueryListColourString(null); if (getMQueryListBKGColours() == null) setMQueryListBKGColourString(null); if ((getMQueryListColourField() != 0) && (getMQueryListBKGColorField() != 0) && (getMQueryListColourField() != getMQueryListBKGColorField())) { setMQueryListBKGColorField(0); setMQueryListBKGColours(null); setMQueryListBKGColorField(0); setMQueryListBKGColours(null); setMQueryListColourString(null); setMQueryListBKGColourString(null); } if (getMChunkNextStr() == null) setMChunkNextStr("Next Chunk"); if (getMChunkPrevStr() == null) setMChunkPrevStr("Previous Chunk"); getMChunkLeftArrow().setTitle(this.mChunkPrevStr); getMChunkRightArrow().setTitle(this.mChunkNextStr); } 
/* 1 */   public TableField(Form paramForm, Field paramField, int paramInt) throws GoatException { super(paramForm, paramField, paramInt); this.mQueryListColoursIndex = -1; this.mQueryListColourField = 0; this.mQueryListBKGColourField = 0; this.mChunkLeftArrow = new GoatImageButton("tablechunkleft"); this.mChunkDownArrow = new GoatImageButton("tablechunkdown"); this.mChunkRightArrow = new GoatImageButton("tablechunkright"); TableFieldLimit localTableFieldLimit = (TableFieldLimit)paramField.getFieldLimit(); if (localTableFieldLimit != null) { setMMaxRows(localTableFieldLimit.getMaxRetrieve()); setMServer(localTableFieldLimit.getServer().toString()); setMSchema(localTableFieldLimit.getForm()); setSampleServer(localTableFieldLimit.getSampleServer()); setSampleSchema(localTableFieldLimit.getSampleForm()); setMQualifier(new Qualifier(localTableFieldLimit.getQualifier(), paramForm.getServer())); } else { setMEmitViewable(setMEmitOptimised(2)); } initialize(); setMAmInPane(false); } 
/* 1 */   public boolean amResultsList() { return (isMInView()) && (getMDisplayType() == 1); } 
/* 1 */   protected void handleProperty(int arg0, Value arg1) throws DisplayPropertyMappers.BadDisplayPropertyException { if (arg0 == 206) { setMAliasSingular(propToString(arg1)); } else if (arg0 == 227) { setMAutoFitColumns(propToBool(arg1)); } else if (arg0 == 226) { setMAutoRefresh(propToBool(arg1)); } else if (arg0 == 224) { setMDrillDown(propToBool(arg1)); } else if (arg0 == 5023) { setMFixedTableHeaders(propToBool(arg1)); } else if (arg0 == 216) { setMQueryListColourString(propToString(arg1)); } else if (arg0 == 5068) { setMQueryListBKGColourString(propToString(arg1)); } else if (arg0 == 5010) { setMAutoRefreshStr(propToString(arg1)); } else if (arg0 == 5006) { setMChunkNextStr(propToString(arg1)); } else if (arg0 == 5007) { setMChunkPrevStr(propToString(arg1)); } else if (arg0 == 5005) { setMChunkSize(propToInt(arg1)); } else if (arg0 == 5212) { setMContentClipped(propToInt(arg1)); } else if (arg0 == 5017) { setMDeleteStr(propToString(arg1)); } else if (arg0 == 5014) { setMDeselectAllStr(propToString(arg1)); } else if (arg0 == 5011) { setMRowHeaderColumn(propToFieldID(arg1)); } else if (arg0 == 5009) { setMEntriesReturnedStr(propToString(arg1)); } else if (arg0 == 5008) { setMNotRefreshedStr(propToString(arg1)); } else if (arg0 == 5018) { setMReadStr(propToString(arg1)); } else if (arg0 == 5015) { setMRefreshStr(propToString(arg1)); } else if (arg0 == 5016) { setMReportStr(propToString(arg1)); } else if (arg0 == 5013) { setMSelectAllStr(propToString(arg1)); } else if (arg0 == 5003) { setMSelInit(propToInt(arg1)); } else if (arg0 == 5004) { setMSelRefresh(propToInt(arg1)); } else if (arg0 == 5012) { setMSelrowsDisable(propToInt(arg1)); } else if (arg0 == 5019) { setMUnreadStr(propToString(arg1)); } else if (arg0 == 5001) { setMDisplayType(propToInt(arg1)); } else if (arg0 == 5) { setMAccess(propToInt(arg1)); } else if (arg0 == 5060) { setMPreferences(propToString(arg1)); } else if (arg0 == 5062) { setMNullValue(propToString(arg1)); } else if (arg0 == 5067) { setMUseLocale(propToBool(arg1)); } else if (arg0 == 5020) { setMSelectLabel(propToString(arg1)); } else if (arg0 == 5100) { setMLeftMargin((int)Math.round(propToInt(arg1) * ARBox.getXFactor())); } else if (arg0 == 5101) { setMRightMargin((int)Math.round(propToInt(arg1) * ARBox.getXFactor())); } else if (arg0 == 5102) { setMTopMargin((int)Math.round(propToInt(arg1) * ARBox.getYFactor())); } else if (arg0 == 5103) { setMBottomMargin((int)Math.round(propToInt(arg1) * ARBox.getYFactor())); } else if (arg0 == 5104) { setMVisibleColumns(propToInt(arg1)); } else if (arg0 == 182) { setMPanelBox(propToBox(arg1)); } else if (arg0 == 5109) { setMPanelHorSpace((int)Math.round(propToInt(arg1) * ARBox.getXFactor())); } else if (arg0 == 5110) { setMPanelVerSpace((int)Math.round(propToInt(arg1) * ARBox.getYFactor())); } else if (arg0 == 11) { setMPanelBorderColor(propToHTMLColour(arg1)); } else if (arg0 == 41) { setMPanelBorderWid(propToLong(arg1)); }
/*   */     else
/*   */     {
/* 1 */       LocalImageFetcher localLocalImageFetcher;
/* 1 */       if (arg0 == 100) { localLocalImageFetcher = new LocalImageFetcher(100); setMImageValue(arg1);
/*   */         try { setMImage(propToGoatImage(getMImageValue(), localLocalImageFetcher, getMLForm().getServerName())); } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException) { MLog.log(Level.SEVERE, localBadDisplayPropertyException.getMessage()); } } else if (arg0 == 169) { setMJustify(propToInt(arg1)); } else if (arg0 == 168) { setMAlign(propToInt(arg1)); } else if (arg0 == 8) { setMBackgroundColor(propToHTMLColour(arg1)); } else if (arg0 == 5114) { setMRootAltText(propToString(arg1)); } else if (arg0 == 5121) { setMColCheck(propToBool(arg1)); } else if (arg0 == 5113) { setMRootImageValue(arg1); localLocalImageFetcher = new LocalImageFetcher(5113);
/*   */         try { setMRootImage(propToGoatImage(getMRootImageValue(), localLocalImageFetcher, getMLForm().getServerName())); } catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException1) { MLog.log(Level.SEVERE, localBadDisplayPropertyException1.getMessage()); } } else if (arg0 == 330) { setMRowLabel(propToString(arg1)); } else if (arg0 == 331) { setMRowLabelPlural(propToString(arg1)); } else { super.handleProperty(arg0, arg1); }  }  } 
/* 1 */   public static FieldGraph.Node[] getSortedColumnChildren(FieldGraph.Node arg0) { FieldGraph.Node[] arrayOfNode = getColumnChildren(arg0); Arrays.sort(arrayOfNode, new Comparator()
/*   */     {
/*   */       public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
/*   */       {
/*   */         FieldGraph.Node localNode1 = (FieldGraph.Node)paramAnonymousObject1;
/*   */         FieldGraph.Node localNode2 = (FieldGraph.Node)paramAnonymousObject2;
/*   */         return ((ColumnField)localNode1.mField).compareDisplayOrder((ColumnField)localNode2.mField);
/*   */       }
/*   */     });
/* 1 */     return arrayOfNode; } 
/* 1 */   public static FieldGraph.Node[] getColumnSortOrder(FieldGraph.Node arg0) { FieldGraph.Node[] arrayOfNode = getColumnChildren(arg0); Arrays.sort(arrayOfNode, new Comparator()
/*   */     {
/*   */       public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
/*   */       {
/*   */         FieldGraph.Node localNode1 = (FieldGraph.Node)paramAnonymousObject1;
/*   */         FieldGraph.Node localNode2 = (FieldGraph.Node)paramAnonymousObject2;
/*   */         return ((ColumnField)localNode1.mField).compareSortOrder((ColumnField)localNode2.mField);
/*   */       }
/*   */     });
/* 1 */     return arrayOfNode; } 
/* 1 */   public static FieldGraph.Node[] getColumnChildren(FieldGraph.Node arg0) { FieldGraph.Node[] arrayOfNode = arg0.getChildNodes(1); ArrayList localArrayList = new ArrayList(); for (int i = 0; i < arrayOfNode.length; i++) if ((arrayOfNode[i].mField instanceof ColumnField)) localArrayList.add(arrayOfNode[i]); 
/* 1 */     return (FieldGraph.Node[])localArrayList.toArray(new FieldGraph.Node[0]); } 
/* 1 */   protected void setDefaultDisplayProperties() { super.setDefaultDisplayProperties(); setMAccess(0); setMAutoFitColumns(true); } 
/* 1 */   private String getHTMLBorderStyle(String arg0) { String str = "none"; if (arg0 == null) return str; if (arg0.equals("-")) str = "dashed"; else if (arg0.equals("*")) str = "dotted"; else if (arg0.equals("=")) str = "double"; return str; } 
/* 1 */   public String getQueryListColours() { StringBuilder localStringBuilder = new StringBuilder(); for (int i = 0; i < getMQueryListColours().length; i++) { if (i != 0) localStringBuilder.append("|"); if (getMDisplayType() == 3) localStringBuilder.append("style='"); localStringBuilder.append(" color:"); if (getMQueryListColours()[i] == null) localStringBuilder.append("inherit;"); else localStringBuilder.append(getMQueryListColours()[i] + ";"); if (getMDisplayType() == 3) localStringBuilder.append("'");  } return localStringBuilder.toString(); } 
/* 1 */   public String getQueryListBKGColours() { StringBuilder localStringBuilder = new StringBuilder(); for (int i = 0; i < getMQueryListBKGColours().length; i++) { if (i != 0) localStringBuilder.append("|"); localStringBuilder.append(" background-color:"); if (getMQueryListBKGColours()[i] == null) localStringBuilder.append("inherit;"); else localStringBuilder.append(getMQueryListBKGColours()[i] + ";");  } return localStringBuilder.toString(); } 
/* 1 */   public int getDisplayType() { return getMDisplayType(); } 
/* 1 */   public int getChunkSize() { return getMChunkSize(); } 
/* 1 */   protected void setMServer(String arg0) { this.mServer = arg0; } 
/* 1 */   public String getMServer() { return this.mServer; } 
/* 1 */   protected String setMSchema(String arg0) { this.mSchema = arg0; return arg0; } 
/* 1 */   public String getMSchema() { return this.mSchema; } 
/* 1 */   protected String setSampleServer(String arg0) { this.mSampleServer = arg0; return arg0; } 
/* 1 */   public String getSampleServer() { return this.mSampleServer; } 
/* 1 */   protected String setSampleSchema(String arg0) { this.mSampleSchema = arg0; return arg0; } 
/* 1 */   public String getSampleSchema() { return this.mSampleSchema; } 
/* 1 */   private void setMMaxRows(int arg0) { this.mMaxRows = arg0; } 
/* 1 */   public int getMMaxRows() { return this.mMaxRows; } 
/* 1 */   private void setMQualifier(Qualifier arg0) { this.mQualifier = arg0; } 
/* 1 */   public Qualifier getMQualifier() { return this.mQualifier; } 
/* 1 */   protected void setMAliasSingular(String arg0) { this.mAliasSingular = arg0; } 
/* 1 */   protected String getMAliasSingular() { return this.mAliasSingular; } 
/* 1 */   protected void setMAutoFitColumns(boolean arg0) { this.mAutoFitColumns = arg0; } 
/* 1 */   public boolean isMAutoFitColumns() { return this.mAutoFitColumns; } 
/* 1 */   protected void setMAutoRefresh(boolean arg0) { this.mAutoRefresh = arg0; } 
/* 1 */   public boolean isMAutoRefresh() { return this.mAutoRefresh; } 
/* 1 */   protected void setMDrillDown(boolean arg0) { this.mDrillDown = arg0; } 
/* 1 */   public boolean isMDrillDown() { return this.mDrillDown; } 
/* 1 */   protected void setMFixedTableHeaders(boolean arg0) { this.mFixedTableHeaders = arg0; } 
/* 1 */   public boolean isMFixedTableHeaders() { return this.mFixedTableHeaders; } 
/* 1 */   protected void setMQueryListColourString(String arg0) { this.mQueryListColourString = arg0; } 
/* 1 */   protected String getMQueryListColourString() { return this.mQueryListColourString; } 
/* 1 */   protected void setMQueryListBKGColourString(String arg0) { this.mQueryListBKGColourString = arg0; } 
/* 1 */   protected String getMQueryListBKGColourString() { return this.mQueryListBKGColourString; } 
/* 1 */   public void setMQueryListColours(String[] arg0) { this.mQueryListColours = arg0; } 
/* 1 */   public String[] getMQueryListColours() { return this.mQueryListColours; } 
/* 1 */   public void setMQueryListBKGColours(String[] arg0) { this.mQueryListBKGColours = arg0; } 
/* 1 */   public String[] getMQueryListBKGColours() { return this.mQueryListBKGColours; } 
/* 1 */   public void setMQueryListColoursIndex(int arg0) { this.mQueryListColoursIndex = arg0; } 
/* 1 */   public int getMQueryListColoursIndex() { return this.mQueryListColoursIndex; } 
/* 1 */   public void setMQueryListColourField(int arg0) { this.mQueryListColourField = arg0; } 
/* 1 */   public int getMQueryListColourField() { return this.mQueryListColourField; } 
/* 1 */   public int getMListColourField() { if (this.mQueryListColourField != 0) return this.mQueryListColourField; if (this.mQueryListBKGColourField != 0) return this.mQueryListBKGColourField; return 0; } 
/* 1 */   public void setMQueryListBKGColorField(int arg0) { this.mQueryListBKGColourField = arg0; } 
/* 1 */   public int getMQueryListBKGColorField() { return this.mQueryListBKGColourField; } 
/* 1 */   protected void setMChunkSize(int arg0) { this.mChunkSize = arg0; } 
/* 1 */   public int getMChunkSize() { return this.mChunkSize; } 
/* 1 */   protected void setMContentClipped(int arg0) { this.mContentClipped = arg0; } 
/* 1 */   public int getMContentClipped() { return this.mContentClipped; } 
/* 1 */   protected void setMRowHeaderColumn(int arg0) { this.mRowHeaderColumn = arg0; } 
/* 1 */   public int getMRowHeaderColumn() { return this.mRowHeaderColumn; } 
/* 1 */   protected void setMSelInit(int arg0) { this.mSelInit = arg0; } 
/* 1 */   public int getMSelInit() { return this.mSelInit; } 
/* 1 */   protected void setMSelRefresh(int arg0) { this.mSelRefresh = arg0; } 
/* 1 */   public int getMSelRefresh() { return this.mSelRefresh; } 
/* 1 */   protected void setMSelrowsDisable(int arg0) { this.mSelrowsDisable = arg0; } 
/* 1 */   public int getMSelrowsDisable() { return this.mSelrowsDisable; } 
/* 1 */   protected void setMDisplayType(int arg0) { this.mDisplayType = arg0; } 
/* 1 */   public int getMDisplayType() { return this.mDisplayType; } 
/* 1 */   protected void setMAmInPane(boolean arg0) { this.mAmInPane = arg0; } 
/* 1 */   public boolean isMAmInPane() { return this.mAmInPane; } 
/* 1 */   protected void setMAccess(int arg0) { this.mAccess = arg0; } 
/* 1 */   public int getMAccess() { return this.mAccess; } 
/* 1 */   public void setMUseLocale(boolean arg0) { this.mUseLocale = arg0; } 
/* 1 */   public boolean isMUseLocale() { return this.mUseLocale; } 
/* 1 */   protected void setMSelectLabel(String arg0) { this.mSelectLabel = arg0; } 
/* 1 */   public String getMSelectLabel() { return this.mSelectLabel; } 
/* 1 */   protected void setMDeleteStr(String arg0) { this.mDeleteStr = arg0; } 
/* 1 */   public String getMDeleteStr() { return this.mDeleteStr; } 
/* 1 */   protected void setMChunkPrevStr(String arg0) { this.mChunkPrevStr = arg0; } 
/* 1 */   public String getMChunkPrevStr() { return this.mChunkPrevStr; } 
/* 1 */   protected void setMChunkNextStr(String arg0) { this.mChunkNextStr = arg0; } 
/* 1 */   public String getMChunkNextStr() { return this.mChunkNextStr; } 
/* 1 */   protected void setMNotRefreshedStr(String arg0) { this.mNotRefreshedStr = arg0; } 
/* 1 */   public String getMNotRefreshedStr() { return this.mNotRefreshedStr; } 
/* 1 */   protected void setMEntriesReturnedStr(String arg0) { this.mEntriesReturnedStr = arg0; } 
/* 1 */   public String getMEntriesReturnedStr() { return this.mEntriesReturnedStr; } 
/* 1 */   protected void setMDeselectAllStr(String arg0) { this.mDeselectAllStr = arg0; } 
/* 1 */   public String getMDeselectAllStr() { return this.mDeselectAllStr; } 
/* 1 */   protected void setMNullValue(String arg0) { this.mNullValue = arg0; } 
/* 1 */   public String getMNullValue() { return this.mNullValue; } 
/* 1 */   protected void setMPreferences(String arg0) { this.mPreferences = arg0; } 
/* 1 */   public String getMPreferences() { return this.mPreferences; } 
/* 1 */   protected void setMReportStr(String arg0) { this.mReportStr = arg0; } 
/* 1 */   public String getMReportStr() { return this.mReportStr; } 
/* 1 */   protected void setMRefreshStr(String arg0) { this.mRefreshStr = arg0; } 
/* 1 */   public String getMRefreshStr() { return this.mRefreshStr; } 
/* 1 */   protected void setMReadStr(String arg0) { this.mReadStr = arg0; } 
/* 1 */   public String getMReadStr() { return this.mReadStr; } 
/* 1 */   protected void setMAutoRefreshStr(String arg0) { this.mAutoRefreshStr = arg0; } 
/* 1 */   public String getMAutoRefreshStr() { return this.mAutoRefreshStr; } 
/* 1 */   protected void setMUnreadStr(String arg0) { this.mUnreadStr = arg0; } 
/* 1 */   public String getMUnreadStr() { return this.mUnreadStr; } 
/* 1 */   protected void setMSelectAllStr(String arg0) { this.mSelectAllStr = arg0; } 
/* 1 */   public String getMSelectAllStr() { return this.mSelectAllStr; } 
/* 1 */   private void setMChunkLeftArrow(GoatImageButton arg0) { this.mChunkLeftArrow = arg0; } 
/* 1 */   public GoatImageButton getMChunkLeftArrow() { return this.mChunkLeftArrow; } 
/* 1 */   private void setMChunkDownArrow(GoatImageButton arg0) { this.mChunkDownArrow = arg0; } 
/* 1 */   public GoatImageButton getMChunkDownArrow() { return this.mChunkDownArrow; } 
/* 1 */   private void setMChunkRightArrow(GoatImageButton arg0) { this.mChunkRightArrow = arg0; } 
/* 1 */   public GoatImageButton getMChunkRightArrow() { return this.mChunkRightArrow; } 
/* 1 */   protected void setMBottomMargin(int arg0) { this.mBottomMargin = arg0; } 
/* 1 */   public int getMBottomMargin() { return this.mBottomMargin; } 
/* 1 */   protected void setMTopMargin(int arg0) { this.mTopMargin = arg0; } 
/* 1 */   public int getMTopMargin() { return this.mTopMargin; } 
/* 1 */   protected void setMRightMargin(int arg0) { this.mRightMargin = arg0; } 
/* 1 */   public int getMRightMargin() { return this.mRightMargin; } 
/* 1 */   protected void setMLeftMargin(int arg0) { this.mLeftMargin = arg0; } 
/* 1 */   public int getMLeftMargin() { return this.mLeftMargin; } 
/* 1 */   public void setMVisibleColumns(int arg0) { this.mVisibleColumns = arg0; } 
/* 1 */   public int getMVisibleColumns() { return this.mVisibleColumns; } 
/* 1 */   protected void setMPanelBox(ARBox arg0) { this.mPanelBox = arg0; } 
/* 1 */   public ARBox getMPanelBox() { return this.mPanelBox; } 
/* 1 */   protected void setMPanelBorderWid(long arg0) { this.mPanelBorderWid = arg0; } 
/* 1 */   public long getMPanelBorderWid() { return this.mPanelBorderWid; } 
/* 1 */   protected void setMBackgroundColor(String arg0) { this.mBackgroundColor = arg0; } 
/* 1 */   public String getMBackgroundColor() { return this.mBackgroundColor; } 
/* 1 */   public void setMPanelBorderColor(String arg0) { this.mPanelBorderColor = arg0; } 
/* 1 */   public String getMPanelBorderColor() { return this.mPanelBorderColor; } 
/* 1 */   protected void setMPanelVerSpace(int arg0) { this.mPanelVerSpace = arg0; } 
/* 1 */   public int getMPanelVerSpace() { return this.mPanelVerSpace; } 
/* 1 */   protected void setMPanelHorSpace(int arg0) { this.mPanelHorSpace = arg0; } 
/* 1 */   public int getMPanelHorSpace() { return this.mPanelHorSpace; } 
/* 1 */   private void setMImage(GoatImage arg0) { this.mImage = arg0; } 
/* 1 */   public GoatImage getMImage() { return this.mImage; } 
/* 1 */   private void setMImageValue(Value arg0) { this.mImageValue = arg0; } 
/* 1 */   private Value getMImageValue() { return this.mImageValue; } 
/* 1 */   private void setMAlign(int arg0) { this.mAlign = arg0; } 
/* 1 */   public int getMAlign() { return this.mAlign; } 
/* 1 */   private void setMJustify(int arg0) { this.mJustify = arg0; } 
/* 1 */   public int getMJustify() { return this.mJustify; } 
/* 1 */   private void setMRootAltText(String arg0) { this.mImgRootAltText = arg0; } 
/* 1 */   public String getMRootAltText() { return this.mImgRootAltText; } 
/* 1 */   private void setMRootImageValue(Value arg0) { this.mImgRootValue = arg0; } 
/* 1 */   public Value getMRootImageValue() { return this.mImgRootValue; } 
/* 1 */   private void setMRootImage(GoatImage arg0) { this.mImgRoot = arg0; } 
/* 1 */   public GoatImage getMRootImage() { return this.mImgRoot; } 
/* 1 */   private void setMColCheck(boolean arg0) { this.mColCheck = arg0; } 
/* 1 */   public boolean getMColCheck() { return this.mColCheck; } 
/* 1 */   public String getMRowLabel() { return this.mRowLabel; } 
/* 1 */   public void setMRowLabel(String arg0) { this.mRowLabel = arg0; } 
/* 1 */   public String getMRowLabelPlural() { return this.mRowLabelPlural; } 
/* 1 */   public void setMRowLabelPlural(String arg0) { this.mRowLabelPlural = arg0; } 
/*   */   private void writeObject(ObjectOutputStream arg0) { try { if ((getMFieldID() == 1020L) && (getMDBName().equals("CustomResultsList"))) { ARBox localARBox = getMARBox(); if (localARBox == null) { setMARBox(new ARBox(0L, 0L, 0L, 0L)); MLog.log(Level.SEVERE, "writeObject created new MARbox"); }  } arg0.defaultWriteObject(); } catch (IOException localIOException1) { MLog.log(Level.SEVERE, "writeObject " + localIOException1.getMessage()); }  } 
/*   */   private void readObject(ObjectInputStream arg0) throws IOException, ClassNotFoundException { try { arg0.defaultReadObject(); if ((getMFieldID() == 1020L) && (getMDBName().equals("CustomResultsList"))) { ARBox localARBox = getMARBox(); if (localARBox == null) { setMARBox(new ARBox(0L, 0L, 0L, 0L)); MLog.log(Level.SEVERE, "readObject created new MARbox"); }  }  } catch (IOException localIOException1) {
/* 1 */       MLog.log(Level.SEVERE, "readObject " + localIOException1.getMessage()); } try { if (getMImageValue() != null) setMImage(propToGoatImage(getMImageValue(), new LocalImageFetcher(100), getMLForm().getServerName())); if (getMRootImageValue() != null) setMRootImage(propToGoatImage(getMRootImageValue(), new LocalImageFetcher(5113), getMLForm().getServerName()));
/*   */     }
/*   */     catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException1)
/*   */     {
/*   */     }
/*   */   }
/*   */ 
/*   */   class LocalImageFetcher
/*   */     implements GoatImage.Fetcher
/*   */   {
/*   */     private static final long serialVersionUID = 5430293912891169327L;
/*   */     private int mProp = 100;
/*   */ 
/*   */     public LocalImageFetcher()
/*   */     {
/*   */     }
/*   */ 
/*   */     public LocalImageFetcher(int arg2)
/*   */     {
/*   */       int i;
/*   */       this.mProp = i;
/*   */     }
/*   */ 
/*   */     public byte[] reFetchImageData()
/*   */     {
/*   */       Field localField;
/*   */       try
/*   */       {
/*   */         FieldCriteria localFieldCriteria = new FieldCriteria();
/*   */         localFieldCriteria.setPropertiesToRetrieve(FieldCriteria.INSTANCE_LIST);
/*   */         localField = SessionData.get().getServerLogin(TableField.this.getMLForm().getServerName()).getField(TableField.this.getMLForm().getFormName(), TableField.this.getMFieldID(), localFieldCriteria);
/*   */       }
/*   */       catch (ARException localARException)
/*   */       {
/*   */         return null;
/*   */       }
/*   */       catch (GoatException localGoatException)
/*   */       {
/*   */         return null;
/*   */       }
/*   */       DisplayInstanceMap localDisplayInstanceMap = localField.getDisplayInstance();
/*   */       Iterator localIterator1 = localDisplayInstanceMap.entrySet().iterator();
/*   */       while (localIterator1.hasNext())
/*   */       {
/*   */         Map.Entry localEntry1 = (Map.Entry)localIterator1.next();
/*   */         if (((Integer)localEntry1.getKey()).intValue() == TableField.this.getMView())
/*   */         {
/*   */           DisplayPropertyMap localDisplayPropertyMap = null;
/*   */           Iterator localIterator2 = localDisplayInstanceMap.entrySet().iterator();
/*   */           Map.Entry localEntry2;
/*   */           while (localIterator2.hasNext())
/*   */           {
/*   */             localEntry2 = (Map.Entry)localIterator2.next();
/*   */             if ((((Integer)localEntry2.getKey()).intValue() == TableField.this.getMView()) && (((DisplayPropertyMap)localEntry2.getValue()).size() > 0))
/*   */             {
/*   */               localDisplayPropertyMap = (DisplayPropertyMap)localEntry2.getValue();
/*   */               break;
/*   */             }
/*   */           }
/*   */           if (localDisplayPropertyMap == null)
/*   */             return null;
/*   */           localIterator2 = localDisplayPropertyMap.entrySet().iterator();
/*   */           while (localIterator2.hasNext())
/*   */           {
/*   */             localEntry2 = (Map.Entry)localIterator2.next();
/*   */             byte[] arrayOfByte = null;
/*   */             String str;
/*   */             if (((Integer)localEntry2.getKey()).intValue() == this.mProp)
/*   */             {
/*   */               try
/*   */               {
/*   */                 Value localValue1 = (Value)localEntry2.getValue();
/*   */                 if (localValue1.getDataType().equals(DataType.CHAR))
/*   */                 {
/*   */                   str = localValue1.getValue().toString();
/*   */                   arrayOfByte = GoatImage.getImageReferenceData(str, TableField.this.getMLForm().getServerName());
/*   */                 }
/*   */                 else
/*   */                 {
/*   */                   arrayOfByte = TableField.propToByteArray(localValue1);
/*   */                 }
/*   */                 return arrayOfByte;
/*   */               }
/*   */               catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
/*   */               {
/*   */                 GoatField.MLog.log(Level.SEVERE, localBadDisplayPropertyException.getMessage());
/*   */               }
/*   */             }
/*   */             else if (((Integer)localEntry2.getKey()).intValue() == this.mProp)
/*   */             {
/*   */               Value localValue2 = (Value)localEntry2.getValue();
/*   */               if (localValue2.getDataType().equals(DataType.CHAR))
/*   */               {
/*   */                 str = localValue2.getValue().toString();
/*   */                 arrayOfByte = GoatImage.getImageReferenceData(str, TableField.this.getMLForm().getServerName());
/*   */                 return arrayOfByte;
/*   */               }
/*   */             }
/*   */           }
/*   */         }
/*   */       }
/*   */       return null;
/*   */     }
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.TableField
 * JD-Core Version:    0.6.1
 */