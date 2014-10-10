package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ColumnFieldLimit;
import com.bmc.arsys.api.CurrencyDetail;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DisplayInstanceMap;
import com.bmc.arsys.api.DisplayPropertyMap;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldCriteria;
import com.bmc.arsys.api.SelectionFieldLimit;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatImage;
import com.remedy.arsys.goat.GoatImage.Fetcher;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.menu.GroupMenu;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ImageRefProp;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.FontTable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public class ColumnField extends GoatField
{
  private static final long serialVersionUID = 8600274589214583758L;
  private boolean mShowURL;
  private static final int MASKED_EDIT = 2;
  private static final int DROP_DOWN_LIST = 1;
  private static final int AR_DVAL_VIEWFIELD_SCROLLBARS_AUTO = 0;
  private static final int AR_DVAL_VIEWFIELD_SCROLLBARS_ON = 1;
  private static final int AR_DVAL_VIEWFIELD_SCROLLBARS_HIDDEN = 2;
  private static final int AR_DVAL_VIEWFIELD_BORDERS_DEFAULT = 0;
  private static final int AR_DVAL_VIEWFIELD_BORDERS_NONE = 1;
  private static final int AR_DVAL_VIEWFIELD_BORDERS_ENABLE = 2;
  private int mOrder;
  private int mWidth;
  private int mSortDir;
  private int mSortSeq;
  private boolean mAllowSort;
  private int mGroupSeq;
  private int mFunc;
  private int mDataFieldID;
  private boolean mFromLocalForm;
  private int mAccess;
  private int mDisplayType;
  private boolean mWrapText;
  private String mDefaultValue;
  private int mColumnLength = 254;
  private static final int MWeightFieldID = 99;
  private static final int MPixelsPerColumnChar = 7;
  private static final int MMinimumSaneColumnWidth = 3;
  private String mLabelFont;
  private String mLabelColour;
  private String mDataFont;
  private String mHighlightStartColor;
  private String mHighlightEndColor;
  private int mLabelAlign;
  private int mLabelJustify;
  private long mRows;
  private long mCols;
  private boolean mTextOnly;
  private boolean mDisableChange;
  private boolean mHighlight;
  private String mAltText;
  private ARBox mLabelARBox;
  private ARBox mDataARBox;
  private ARBox mExpandARBox;
  private ARBox mMenuARBox;
  private ARBox mFileUploadARBox;
  public static final GoatImageButton MMenuButton = new GoatImageButton("menu");
  private static final GoatImageButton MDefaultExpandButton = new GoatImageButton("text");
  private static final GoatImageButton MDefaultFileUploadButton = new GoatImageButton("open");
  private static final GoatImageButton MDefaultEditButton = new GoatImageButton("edit");
  private static final GoatImageButton MDefaultRTFButton = new GoatImageButton("rtf");
  private int mMaxLength = -1;
  private int mMenuStyle = 1;
  private int mAutoCompleteStyle;
  private int mAutoCompleteMatchBy;
  private int mQBEMatch = -1;
  private String mCharMenu;
  private String mCharPattern;
  private int mFullTextOption = -1;
  private boolean mMasked;
  private boolean mDropDownList;
  private boolean mFileUpload;
  private boolean mEditField;
  private boolean mRichTextField;
  private boolean mRichTextFieldAdv;
  public static final long PASSWORD_FIELD_ID = 102L;
  public static final long EMAIL_PASSWORD_FIELD_ID = 123L;
  private long mMin;
  private long mMax;
  private long mDefault;
  private boolean mSpinner;
  private boolean mHaveDefault;
  public static final GoatImageButton mSpinnerUp = new GoatImageButton("spinnerup");
  public static final GoatImageButton mSpinnerDown = new GoatImageButton("spinnerdown");
  private int mPrecision = 2;
  private int mDateTimePopup;
  private static final GoatImageButton mExpandButtonDate = new GoatImageButton("calendar");
  private static final GoatImageButton mExpandButtonTime = new GoatImageButton("time");
  private String mDefaultCode;
  private String mDefaultCurrency;
  private CurrencyDetail[] mAllowable = new CurrencyDetail[0];
  private CurrencyDetail[] mFunctional = new CurrencyDetail[0];
  private String mInitialCode;
  private static GoatImageButton mExpandButtonCurrency = new GoatImageButton("currency");
  private static final int DROPDOWN = 0;
  private static final int RADIO = 1;
  private static final int CHECKBOX = 2;
  private int mEnumType = -1;
  private String[] mEnumLabels;
  private String[] mEnumValues;
  private SelectionFieldLimit mEnumLimitInfo;
  private long[] mEnumIds;
  private boolean mIsCustom;
  private int mEnumDefaultValue;
  private String mRadioString;
  private String mLabelString;
  public static final GoatImageButton mSelectionButton = new GoatImageButton("menu");
  public static final int CONTROL_TYPE_BUTTON = 0;
  public static final int CONTROL_TYPE_URL = 1;
  public static final int CONTROL_TYPE_DONTCARE = 2;
  private boolean mImageFlat;
  private int mControlType;
  private String mButtonText;
  private String mFont;
  private String mColour;
  private String mHtmlColour;
  private boolean mTransparent;
  private transient GoatImage mImage;
  private transient GoatImage mDisabledImage;
  private Value mImageValue;
  private Value mDisabledImageValue;
  private Value mImgColValue;
  private boolean mImageMaintainRatio;
  private boolean mScaleImageToFit;
  private int mImagePosition;
  private int mTextJustify;
  private String mImgButtonAltText;
  private String mTableCellColorString;
  private int mImgBtnMouseoverEffect;
  private int mTrimType;
  private int mEffect;
  private long mLineWidth;
  private String mText;
  private String mTextVAlign;
  private String mTrimTextJustify;
  private String mTextFont;
  private String mTextColor;
  private String mHtmlTextColor;
  private String mBackgroundColor;
  private String mBorderColor;
  private boolean mButtonTBL = false;
  private String mScrollbars;
  private String mBorder;
  private int mAttachmentFieldID;
  private boolean mIsGroupMenu;
  private int mHeaderAlignment;
  private int mDataAlignment;
  private int mInitialState;
  private boolean hAlignSet;
  private boolean dAlignSet;

  public ColumnField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    setMEmitViewable(setMEmitOptimised(1));
    setMEmitViewless(2);
    if (getMAccess() == 0)
      setMAccess(2);
    setMParentExecDep(true);
    ColumnFieldLimit localColumnFieldLimit = (ColumnFieldLimit)paramField.getFieldLimit();
    if (localColumnFieldLimit != null)
    {
      setMDataFieldID(localColumnFieldLimit.getDataField());
      setMFromLocalForm(localColumnFieldLimit.getDataSource() != 0);
      setMButtonTBL(localColumnFieldLimit.getDataSource() == 2);
      setMParentFieldID(localColumnFieldLimit.getParent());
      if (!isMInView())
        try
        {
          Field localField = (Field)paramForm.getCachedFieldMap().get(Integer.valueOf(localColumnFieldLimit.getParent()));
          if (localField != null)
          {
            Value localValue = localField.getDisplayInstance().getProperty(paramInt, new Integer(3));
            if (localValue != null)
              setMEmitViewless(1);
          }
        }
        catch (GoatException localGoatException)
        {
          MLog.log(Level.WARNING, "Unable to figure out the visibility of table field");
        }
    }
    else
    {
      setMEmitViewable(setMEmitOptimised(2));
    }
    if (getMDisplayType() == 3)
    {
      if (getMARBox() == null)
        setMInView(false);
      if ((getMARBox() != null) && (getMDataARBox() != null) && (getMDataARBox().getW() == 0) && (getMDataARBox().getH() == 0))
      {
        getMDataARBox().setW(getMARBox().getW() - getMDataARBox().getX());
        getMDataARBox().setH(getMARBox().getH() - getMDataARBox().getY());
      }
      if ((getMLabelFont() == null) || (getMLabelFont().equals("")) || (getMLabelFont().equals("Default")))
      {
        if (getMFieldOption() == 1)
          setMLabelFont(FontTable.mapFontToClassName("Required"));
        else if (getMFieldOption() == 2)
          setMLabelFont(FontTable.mapFontToClassName("Optional"));
        else if (getMFieldOption() == 3)
          setMLabelFont(FontTable.mapFontToClassName("System"));
        else
          setMLabelFont(FontTable.mapFontToClassName("Default"));
      }
      else
        setMLabelFont(FontTable.mapFontToClassName(getMLabelFont()));
      setMDataFont(FontTable.mapFontToClassName(getMDataFont()));
      if (getMBorder() == null)
        setMBorder("1");
      if (getMScrollbars() == null)
        setMScrollbars("auto");
    }
  }

  public ColumnField(EntryListFieldInfo paramEntryListFieldInfo, GoatField paramGoatField1, Form paramForm, int paramInt1, GoatField paramGoatField2, int paramInt2)
    throws GoatException
  {
    setDefaultDisplayProperties();
    setMVisible(true);
    setMAccess(2);
    setMEmitViewable(setMEmitOptimised(1));
    setMEmitViewless(2);
    setMFieldID(paramInt1);
    setMDBName("RL" + getMFieldID());
    setMInView(true);
    setMLForm(new GoatField.LightForm(paramForm));
    setMParentFieldID(paramGoatField1.getMFieldID());
    int i = paramEntryListFieldInfo.getColumnWidth();
    if (i < getMMinimumSaneColumnWidth())
      i = getMMinimumSaneColumnWidth();
    setMWidth((int)(i * getMPixelsPerColumnChar() / ARBox.MXFactor));
    setMWidth(getMWidth() + getMPixelsPerColumnChar());
    setMDataFieldID(paramEntryListFieldInfo.getFieldId());
    setMDataType(DataType.COLUMN);
    setMDataTypeString(TypeMap.mapDataType(getMDataType(), getMFieldID()));
    if (paramGoatField2 != null)
    {
      if (paramGoatField2.isDataField())
        setMLabel(paramGoatField2.getMLabel());
      if ((getMLabel() == null) || (!paramGoatField2.isDataField()))
        setMLabel(paramGoatField2.getMDBName());
    }
    else if (getMDataFieldID() == getMWeightFieldID())
    {
      setMLabel(MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "WEIGHT"));
    }
    else
    {
      setMLabel("???");
    }
    setMOrder(paramInt2);
  }

  protected void setDefaultDisplayProperties()
  {
    super.setDefaultDisplayProperties();
    setMOrder(2147483647);
    setMAccess(0);
    setMWidth((int)(89.0D / ARBox.MXFactor));
    setMSortDir(0);
    setMSortSeq(0);
    setMWrapText(false);
    setMAllowSort(true);
    setMImgBtnMouseoverEffect(1);
    setMInitialState(1);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    LocalImageFetcher localLocalImageFetcher;
    int m;
    switch (paramInt)
    {
    case 221:
      setMOrder(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_COLUMN_ORDER: " + getMOrder());
      break;
    case 220:
      setMWidth(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_COLUMN_WIDTH: " + getMWidth());
      break;
    case 5:
      setMAccess(propToInt(paramValue));
      if (getMAccess() == 0)
        setMAccess(2);
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_ENABLE: " + getMAccess());
      break;
    case 5021:
      setMDisplayType(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TABLE_COL_DISPLAY_TYPE: " + getMDisplayType());
      break;
    case 5058:
      setMWrapText(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TABLE_COL_WRAP_TEXT: " + isMWrapText());
      break;
    case 223:
      setMSortDir(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_SORT_DIR: " + getMSortDir());
      break;
    case 222:
      setMSortSeq(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_SORT_SEQ: " + getMSortSeq());
      break;
    case 5204:
      setMGroupSeq(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_SORT_GROUP: " + getMGroupSeq());
      break;
    case 5205:
      setMFunc(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_SORT_AGGREGATION_TYPE: " + getMFunc());
      break;
    case 5022:
      setMDefaultValue(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TABLE_COL_INITVAL: " + getMDefaultValue());
      break;
    case 24:
      setMLabelColour(propToHTMLColour(paramValue));
      setMColour(getMLabelColour());
      setMTextColor(getMLabelColour());
      break;
    case 21:
      setMLabelARBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_LABEL_BBOX: " + getMLabelARBox());
      break;
    case 151:
      setMDataARBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_BBOX: " + getMDataARBox());
      break;
    case 66:
      setMFileUploadARBox(propToBox(paramValue));
      setMExpandARBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_EXPAND_BBOX: " + getMExpandARBox());
      break;
    case 65:
      setMMenuARBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_MENU_BBOX: " + getMMenuARBox());
      break;
    case 90:
      setMLabelJustify(propToInt(paramValue));
      setMTextJustify(getMLabelJustify());
      setMTrimTextJustify(getMLabelJustify() == 2 ? "c" : getMLabelJustify() == 1 ? "l" : "r");
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_JUSTIFY: " + getMLabelJustify());
      break;
    case 91:
      setMLabelAlign(propToInt(paramValue));
      setMTextVAlign(getMLabelAlign() == 2 ? "middle" : getMLabelAlign() == 1 ? "top" : "bottom");
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_ALIGN: " + getMLabelAlign());
      break;
    case 60:
      setMRows(propToLong(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_ROWS: " + getMRows());
      break;
    case 61:
      setMCols(propToLong(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_COLS: " + getMCols());
      break;
    case 240:
      setMTextOnly(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DISPLAY_AS_TEXT_ONLY: " + isMTextOnly());
      break;
    case 22:
      setMLabelFont(propToString(paramValue));
      setMFont(FontTable.mapFontToClassName(propToString(paramValue)));
      break;
    case 81:
      setMDataFont(propToString(paramValue));
      setMFont(FontTable.mapFontToClassName(propToString(paramValue)));
      setMTextFont(getMFont());
      break;
    case 228:
      setMDisableChange(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_APPLY_DIRTY: " + isMDisableChange());
      break;
    case 288:
      setMHighlight(propToBool(paramValue));
      break;
    case 289:
      setMHighlightStartColor(propToHTMLColour(paramValue));
      break;
    case 290:
      setMHighlightEndColor(propToHTMLColour(paramValue));
      break;
    case 67:
      int i = propToInt(paramValue);
      setMMasked(i == 2);
      setMDropDownList(i == 1);
      setMFileUpload(i == 3);
      setMRichTextField((i == 4) || (i == 5));
      setMRichTextFieldAdv(i == 5);
      break;
    case 5116:
      setMShowURL(propToBool(paramValue));
      break;
    case 231:
      int j = propToInt(paramValue);
      if (j == 0)
      {
        setMFileUploadARBox(null);
      }
      else if (j == 1)
      {
        setMFileUploadARBox(null);
        setMExpandARBox(null);
      }
      else if (j == 2)
      {
        setMFileUploadARBox(null);
      }
      break;
    case 68:
      setMAutoCompleteStyle(propToInt(paramValue));
      break;
    case 69:
      setMAutoCompleteMatchBy(propToInt(paramValue));
      break;
    case 62:
      setMSpinner(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_SPIN: " + isMSpinner());
      break;
    case 144:
      setMDateTimePopup(propToInt(paramValue));
      break;
    case 245:
      setMInitialCode(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_INITIAL_CURRENCY_TYPE: " + getMInitialCode());
      break;
    case 64:
      setMRadioString(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_RADIO: " + getMRadioString());
      break;
    case 230:
      setMLabelString(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_ENUM_LABELS: " + getMLabelString());
      break;
    case 5119:
      setMAllowSort(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TABLE_COL_ALLOW_SORT: " + getMAllowSort());
      break;
    case 2:
      int k = propToInt(paramValue);
      if ((k & 0x1) != 0)
        setMControlType(0);
      else if ((k & 0x10) != 0)
        setMControlType(1);
      else
        setMControlType(2);
      break;
    case 110:
      setMButtonText(propToString(paramValue));
      break;
    case 145:
      setMTransparent(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BACKGROUND_MODE: " + isMTransparent());
      break;
    case 111:
      setMImageFlat(propToBool(paramValue));
      break;
    case 112:
      setMImagePosition(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BUTTON_IMAGE_POSITION: " + getMImagePosition());
      break;
    case 114:
      setMImageMaintainRatio(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BUTTON_MAINTAIN_RATIO: " + isMImageMaintainRatio());
      break;
    case 113:
      setMScaleImageToFit(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BUTTON_SCALE_IMAGE: " + isMScaleImageToFit());
      break;
    case 84:
      setMHtmlColour(propToHTMLColour(paramValue));
      setMHtmlTextColor(getMHtmlColour());
      break;
    case 5066:
      setMImgButtonAltText(propToString(paramValue));
      setMAltText(propToString(paramValue));
      break;
    case 101:
      localLocalImageFetcher = new LocalImageFetcher(101, null);
      setMImageValue(paramValue);
      try
      {
        setMImage(propToGoatImage(getMImageValue(), localLocalImageFetcher, getMLForm().getServerName()));
      }
      catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException1)
      {
        MLog.log(Level.SEVERE, localBadDisplayPropertyException1.getMessage());
      }
    case 5226:
      localLocalImageFetcher = new LocalImageFetcher(5226, null);
      setMDisabledImageValue(paramValue);
      try
      {
        setMDisabledImage(propToGoatImage(getMDisabledImageValue(), localLocalImageFetcher, getMLForm().getServerName()));
      }
      catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException2)
      {
        MLog.log(Level.SEVERE, localBadDisplayPropertyException2.getMessage());
      }
    case 1:
      setMTrimType(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TRIM_TYPE: " + getMTrimType());
      break;
    case 41:
      setMLineWidth(propToLong(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_LINE_WIDTH: " + getMLineWidth());
      break;
    case 9:
      setMEffect(propToInt(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DEPTH_EFFECT: " + getMEffect());
      break;
    case 80:
      setMText(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TEXT: " + getMText());
      break;
    case 8:
      setMBackgroundColor(propToHTMLColour(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_COLOR_FILL: " + getMBackgroundColor());
      break;
    case 11:
      setMBorderColor(propToHTMLColour(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_COLOR_LINE: " + getMBorderColor());
      break;
    case 5024:
      m = propToInt(paramValue);
      switch (m)
      {
      case 2:
        setMScrollbars("no");
        break;
      case 1:
        setMScrollbars("yes");
        break;
      case 0:
      default:
        setMScrollbars("auto");
      }
      break;
    case 5025:
      m = propToInt(paramValue);
      switch (m)
      {
      case 1:
        setMBorder("0");
        break;
      case 2:
      default:
        setMBorder("1");
      }
      break;
    case 5115:
      setMImgColValue(paramValue);
      break;
    case 5118:
      setMTableCellColorString(propToString(paramValue));
      break;
    case 5201:
      setMAttachmendFieldID(propToInt(paramValue));
      break;
    case 332:
      setMHeaderAlignment(propToInt(paramValue));
      break;
    case 333:
      setMDataAlignment(propToInt(paramValue));
      break;
    case 334:
      setMImgBtnMouseoverEffect(propToInt(paramValue));
      break;
    case 5230:
      setMInitialState(propToInt(paramValue));
      break;
    default:
      super.handleProperty(paramInt, paramValue);
    }
  }

  private void setMShowURL(boolean paramBoolean)
  {
    this.mShowURL = paramBoolean;
  }

  public String getBackgroundColor(FieldGraph.Node paramNode)
  {
    if (isMTransparent())
      return "transparent";
    String str;
    if (((getMTrimType() == 3) || (getMTrimType() == 2)) && (getMBackgroundColor() != null))
      str = getMBackgroundColor();
    else
      str = paramNode.getParentFieldGraph().getDetailColor();
    if ((str == null) || (str.length() <= 0))
      str = "white";
    return str;
  }

  public final Box[] placeQuadrants()
  {
    Box localBox1 = getMARBox().toBox();
    int i = getMImage().getW();
    int j = getMImage().getH();
    int k = localBox1.mW - 1;
    int m = localBox1.mH - 1;
    int n = k / 2;
    int i1 = m / 2;
    if (!isMScaleImageToFit())
    {
      if (i < n - 4)
        n = i + 4;
      if (j < i1 - 4)
        i1 = i + 4;
    }
    Box localBox2;
    Box localBox4;
    if (getMImagePosition() == 0)
    {
      localBox2 = localBox1.wholeChildBox();
      if (!isMImageFlat())
      {
        if (localBox2.mW > 0)
          localBox2.mW -= 1;
        if (localBox2.mH > 0)
          localBox2.mH -= 1;
      }
      localBox4 = null;
    }
    else if (getMImagePosition() == 1)
    {
      localBox2 = new Box(0L, 0L, n, m);
      localBox4 = new Box(n, 0L, k, m);
    }
    else if (getMImagePosition() == 2)
    {
      localBox2 = new Box(k - n, 0L, k, m);
      localBox4 = new Box(0L, 0L, k - n, m);
    }
    else if (getMImagePosition() == 3)
    {
      localBox2 = new Box(0L, 0L, k, i1);
      localBox4 = new Box(0L, i1, k, m);
    }
    else
    {
      localBox2 = new Box(0L, m - i1, k, m);
      localBox4 = new Box(0L, 0L, k, i1);
    }
    Box localBox3;
    if (isMScaleImageToFit())
    {
      if (isMImageMaintainRatio())
        localBox3 = localBox2.scaleCentredBox(i, j);
      else
        localBox3 = localBox2.wholeChildBox();
    }
    else
      localBox3 = localBox2.newCentredBox(i, j);
    assert ((localBox3.mW >= 0) && (localBox3.mH >= 0));
    Box[] arrayOfBox = new Box[3];
    arrayOfBox[0] = localBox2;
    arrayOfBox[1] = localBox3;
    arrayOfBox[2] = localBox4;
    return arrayOfBox;
  }

  public int idToIndex(long paramLong)
  {
    assert ((getMEnumValues() != null) || (getMEnumLabels() != null) || (getMEnumIds() != null));
    for (int i = 0; i < getMEnumIds().length; i++)
      if (getMEnumIds()[i] == paramLong)
        return i;
    return 2147483647;
  }

  public String getARType(GoatField paramGoatField)
  {
    if (getMEnumType() == 0)
      return paramGoatField.getMDataTypeString() + "Sel";
    return paramGoatField.getMDataTypeString();
  }

  public GoatImageButton getFileUploadButton()
  {
    if (FormContext.get().IsVoiceAccessibleUser())
      return null;
    return MDefaultFileUploadButton;
  }

  public GoatImageButton getRichTextEditorButton()
  {
    if (FormContext.get().IsVoiceAccessibleUser())
      return null;
    return MDefaultRTFButton;
  }

  public GoatImageButton getMDefaultEditButton()
  {
    if (FormContext.get().IsVoiceAccessibleUser())
      return null;
    return MDefaultEditButton;
  }

  public boolean isMShowURL()
  {
    return this.mShowURL;
  }

  public String getFileUploadClassString()
  {
    return "fileupload";
  }

  public String getEditFieldClassString()
  {
    return "edit";
  }

  public String getExpandBoxAltText(GoatField paramGoatField)
  {
    if (DataType.CURRENCY.equals(paramGoatField.getMDataType()))
      return getLocalizedDescriptionStringForWidget("Functional currency list for {0}");
    return getLocalizedDescriptionStringForWidget("Editor for {0}");
  }

  public String getExpandBoxClassesString()
  {
    return "expand";
  }

  public String getDisplayTitleForField(GoatField paramGoatField)
  {
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      if (DataType.CHAR.equals(paramGoatField.getMDataType()))
      {
        if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
          return getTitleForFieldWithMenuAndExpandBox(paramGoatField);
        if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")))
          return getTitleForFieldWithMenu();
        if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
          return getTitleForFieldWithExpandBox(paramGoatField);
      }
      else if (DataType.ENUM.equals(paramGoatField.getMDataType()))
      {
        if (getMEnumType() == 0)
          return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "ReadOnly {0} with value selectable menu");
        return null;
      }
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
        return getTitleForFieldWithMenuAndExpandBox(paramGoatField);
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0))
        return getTitleForFieldWithMenu();
      if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
        return getTitleForFieldWithExpandBox(paramGoatField);
    }
    return null;
  }

  public String getDisplayTitleCodeForField(GoatField paramGoatField)
  {
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      if (DataType.CHAR.equals(paramGoatField.getMDataType()))
      {
        if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
          return getTitleCodeForFieldWithMenuAndExpandBox(paramGoatField);
        if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")))
          return getTitleCodeForFieldWithMenu();
        if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
          return getTitleCodeForFieldWithExpandBox(paramGoatField);
      }
      else if (DataType.ENUM.equals(paramGoatField.getMDataType()))
      {
        if (getMEnumType() == 0)
          return "MR";
        return "";
      }
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
        return getTitleCodeForFieldWithMenuAndExpandBox(paramGoatField);
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0))
        return getTitleCodeForFieldWithMenu();
      if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton(paramGoatField) != null))
        return getTitleCodeForFieldWithExpandBox(paramGoatField);
    }
    return "";
  }

  public GoatImageButton getExpandButton(GoatField paramGoatField)
  {
    if ((paramGoatField == null) || ((FormContext.get().IsVoiceAccessibleUser()) && ((DataType.DATE.equals(paramGoatField.getMDataType())) || (DataType.TIME.equals(paramGoatField.getMDataType())) || (DataType.TIME_OF_DAY.equals(paramGoatField.getMDataType())))))
      return null;
    if (DataType.DATE.equals(paramGoatField.getMDataType()))
      return mExpandButtonDate;
    if (DataType.TIME.equals(paramGoatField.getMDataType()))
      return mExpandButtonDate;
    if (DataType.TIME_OF_DAY.equals(paramGoatField.getMDataType()))
      return mExpandButtonTime;
    if (DataType.CURRENCY.equals(paramGoatField.getMDataType()))
      return mExpandButtonCurrency;
    return MDefaultExpandButton;
  }

  protected String getTitleCodeForFieldWithExpandBox(GoatField paramGoatField)
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (DataType.CURRENCY.equals(paramGoatField.getMDataType()))
      return "FC";
    return "E";
  }

  protected String getTitleForFieldWithExpandBox(GoatField paramGoatField)
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (DataType.CURRENCY.equals(paramGoatField.getMDataType()))
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with functional currency");
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with expand");
  }

  protected String getTitleForFieldWithMenuAndExpandBox(GoatField paramGoatField)
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (DataType.CURRENCY.equals(paramGoatField.getMDataType()))
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu and functional currency");
    if (isMDropDownList())
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "ReadOnly {0} with value selectable menu and expand");
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu and expand");
  }

  protected String getTitleForFieldWithMenu()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (isMDropDownList())
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "ReadOnly {0} with value selectable menu");
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu");
  }

  protected String getTitleCodeForFieldWithMenuAndExpandBox(GoatField paramGoatField)
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (DataType.CURRENCY.equals(paramGoatField.getMDataType()))
      return "MFC";
    if (isMDropDownList())
      return "MER";
    return "ME";
  }

  protected String getTitleCodeForFieldWithMenu()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (isMDropDownList())
      return "MR";
    return "M";
  }

  public String getForCodePrefix_DataField(GoatField paramGoatField)
  {
    if ((DataType.CURRENCY.equals(paramGoatField.getMDataType())) || (DataType.CHAR.equals(paramGoatField.getMDataType())))
      return "x-arid";
    return "arid";
  }

  public int compareDisplayOrder(ColumnField paramColumnField)
  {
    return getMOrder() - paramColumnField.getMOrder();
  }

  public int compareSortOrder(ColumnField paramColumnField)
  {
    int i = getMSortSeq() - paramColumnField.getMSortSeq();
    if (i != 0)
      return i;
    if (getMFieldID() > paramColumnField.getMFieldID())
      return 1;
    if (getMFieldID() < paramColumnField.getMFieldID())
      return -1;
    return 0;
  }

  public int getDataFieldID()
  {
    return getMDataFieldID();
  }

  public boolean isLocal()
  {
    return isMFromLocalForm();
  }

  public String getDefault()
  {
    return getMDefaultValue();
  }

  public int getDisplayType()
  {
    return getMDisplayType();
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      if (getMImageValue() != null)
        setMImage(propToGoatImage(getMImageValue(), new LocalImageFetcher(101, null), getMLForm().getServerName()));
      if (getMDisabledImageValue() != null)
        setMDisabledImage(propToGoatImage(getMDisabledImageValue(), new LocalImageFetcher(5226, null), getMLForm().getServerName()));
      if (getMImgColValue() != null)
        Map localMap = cacheImgCols(getMImgColValue());
    }
    catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
    {
    }
  }

  public Map<Integer, GoatImage> cacheImgCols(Value paramValue)
  {
    ImageRefProp localImageRefProp = new ImageRefProp(paramValue.toString());
    Map localMap = localImageRefProp.getImageRefs();
    if ((localMap == null) || (localMap.size() <= 0))
      return null;
    HashMap localHashMap = new HashMap();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Integer localInteger = (Integer)localIterator.next();
      String str = (String)localMap.get(localInteger);
      try
      {
        if (str != null)
        {
          GoatImage localGoatImage = propToGoatImage(new Value(str), new LocalImageFetcher(5115, str), getMLForm().getServerName());
          if (localGoatImage != null)
            localHashMap.put(localInteger, localGoatImage);
        }
      }
      catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
      {
      }
    }
    if (localHashMap.size() <= 0)
      return null;
    return localHashMap;
  }

  public void setMWidth(int paramInt)
  {
    this.mWidth = paramInt;
  }

  public int getMWidth()
  {
    return this.mWidth;
  }

  public void setMOrder(int paramInt)
  {
    this.mOrder = paramInt;
  }

  public int getMOrder()
  {
    return this.mOrder;
  }

  public void setMSortDir(int paramInt)
  {
    this.mSortDir = paramInt;
  }

  public int getMSortDir()
  {
    return this.mSortDir;
  }

  public void setMSortSeq(int paramInt)
  {
    this.mSortSeq = paramInt;
  }

  public int getMSortSeq()
  {
    return this.mSortSeq;
  }

  public void setMGroupSeq(int paramInt)
  {
    this.mGroupSeq = paramInt;
  }

  public int getMGroupSeq()
  {
    return this.mGroupSeq;
  }

  public void setMFunc(int paramInt)
  {
    this.mFunc = paramInt;
  }

  public int getMFunc()
  {
    return this.mFunc;
  }

  private void setMDataFieldID(int paramInt)
  {
    this.mDataFieldID = paramInt;
  }

  public int getMDataFieldID()
  {
    return this.mDataFieldID;
  }

  private void setMFromLocalForm(boolean paramBoolean)
  {
    this.mFromLocalForm = paramBoolean;
  }

  public boolean isMFromLocalForm()
  {
    return this.mFromLocalForm;
  }

  public void setMAccess(int paramInt)
  {
    this.mAccess = paramInt;
  }

  public int getMAccess()
  {
    return this.mAccess;
  }

  public void setMDisplayType(int paramInt)
  {
    this.mDisplayType = paramInt;
  }

  public int getMDisplayType()
  {
    return this.mDisplayType;
  }

  public void setMWrapText(boolean paramBoolean)
  {
    this.mWrapText = paramBoolean;
  }

  public boolean isMWrapText()
  {
    return this.mWrapText;
  }

  private void setMDefaultValue(String paramString)
  {
    this.mDefaultValue = paramString;
  }

  public String getMDefaultValue()
  {
    return this.mDefaultValue;
  }

  public void setMColumnLength(int paramInt)
  {
    this.mColumnLength = paramInt;
  }

  public int getMColumnLength()
  {
    return this.mColumnLength;
  }

  public static int getMWeightFieldID()
  {
    return 99;
  }

  public static int getMPixelsPerColumnChar()
  {
    return 7;
  }

  public static int getMMinimumSaneColumnWidth()
  {
    return 3;
  }

  private void setMHighlightEndColor(String paramString)
  {
    this.mHighlightEndColor = paramString;
  }

  public String getMHighlightEndColor()
  {
    return this.mHighlightEndColor;
  }

  private void setMHighlightStartColor(String paramString)
  {
    this.mHighlightStartColor = paramString;
  }

  public String getMHighlightStartColor()
  {
    return this.mHighlightStartColor;
  }

  private void setMDataFont(String paramString)
  {
    this.mDataFont = paramString;
  }

  public String getMDataFont()
  {
    return this.mDataFont;
  }

  private void setMLabelColour(String paramString)
  {
    this.mLabelColour = paramString;
  }

  public String getMLabelColour()
  {
    return this.mLabelColour;
  }

  private void setMLabelFont(String paramString)
  {
    this.mLabelFont = paramString;
  }

  public String getMLabelFont()
  {
    return this.mLabelFont;
  }

  private void setMLabelJustify(int paramInt)
  {
    this.mLabelJustify = paramInt;
  }

  public int getMLabelJustify()
  {
    return this.mLabelJustify;
  }

  private void setMLabelAlign(int paramInt)
  {
    this.mLabelAlign = paramInt;
  }

  public int getMLabelAlign()
  {
    return this.mLabelAlign;
  }

  private void setMCols(long paramLong)
  {
    this.mCols = paramLong;
  }

  public long getMCols()
  {
    return this.mCols;
  }

  public void setMRows(long paramLong)
  {
    this.mRows = paramLong;
  }

  public long getMRows()
  {
    return this.mRows;
  }

  private void setMDisableChange(boolean paramBoolean)
  {
    this.mDisableChange = paramBoolean;
  }

  public boolean isMDisableChange()
  {
    return this.mDisableChange;
  }

  private void setMTextOnly(boolean paramBoolean)
  {
    this.mTextOnly = paramBoolean;
  }

  public boolean isMTextOnly()
  {
    return this.mTextOnly;
  }

  public void setMHighlight(boolean paramBoolean)
  {
    this.mHighlight = paramBoolean;
  }

  public boolean isMHighlight()
  {
    return this.mHighlight;
  }

  private void setMFileUploadARBox(ARBox paramARBox)
  {
    this.mFileUploadARBox = paramARBox;
  }

  public ARBox getMFileUploadARBox()
  {
    return this.mFileUploadARBox;
  }

  private void setMMenuARBox(ARBox paramARBox)
  {
    this.mMenuARBox = paramARBox;
  }

  public ARBox getMMenuARBox()
  {
    return this.mMenuARBox;
  }

  private void setMExpandARBox(ARBox paramARBox)
  {
    this.mExpandARBox = paramARBox;
  }

  public ARBox getMExpandARBox()
  {
    return this.mExpandARBox;
  }

  private void setMDataARBox(ARBox paramARBox)
  {
    this.mDataARBox = paramARBox;
  }

  public ARBox getMDataARBox()
  {
    return this.mDataARBox;
  }

  private void setMLabelARBox(ARBox paramARBox)
  {
    this.mLabelARBox = paramARBox;
  }

  public ARBox getMLabelARBox()
  {
    return this.mLabelARBox;
  }

  public void setMMaxLength(int paramInt)
  {
    this.mMaxLength = paramInt;
  }

  public int getMMaxLength()
  {
    return this.mMaxLength;
  }

  public void setMMenuStyle(int paramInt)
  {
    this.mMenuStyle = paramInt;
  }

  public int getMMenuStyle()
  {
    return this.mMenuStyle;
  }

  protected void setMAutoCompleteStyle(int paramInt)
  {
    this.mAutoCompleteStyle = paramInt;
  }

  public int getMAutoCompleteStyle()
  {
    return this.mAutoCompleteStyle;
  }

  protected void setMAutoCompleteMatchBy(int paramInt)
  {
    this.mAutoCompleteMatchBy = paramInt;
  }

  public int getMAutoCompleteMatchBy()
  {
    return this.mAutoCompleteMatchBy;
  }

  public void setMQBEMatch(int paramInt)
  {
    this.mQBEMatch = paramInt;
  }

  public int getMQBEMatch()
  {
    return this.mQBEMatch;
  }

  public void setMCharMenu(String paramString)
  {
    this.mCharMenu = paramString;
  }

  public String getMCharMenu()
  {
    return this.mCharMenu;
  }

  public void setMCharPattern(String paramString)
  {
    this.mCharPattern = paramString;
  }

  public String getMCharPattern()
  {
    return this.mCharPattern;
  }

  public void setMFullTextOption(int paramInt)
  {
    this.mFullTextOption = paramInt;
  }

  public int getMFullTextOption()
  {
    return this.mFullTextOption;
  }

  public void setMMasked(boolean paramBoolean)
  {
    this.mMasked = paramBoolean;
  }

  public boolean isMMasked()
  {
    return this.mMasked;
  }

  protected void setMDropDownList(boolean paramBoolean)
  {
    this.mDropDownList = paramBoolean;
  }

  public boolean isMDropDownList()
  {
    return this.mDropDownList;
  }

  protected void setMFileUpload(boolean paramBoolean)
  {
    this.mFileUpload = paramBoolean;
  }

  public boolean isMFileUpload()
  {
    return this.mFileUpload;
  }

  public boolean ismEditField()
  {
    return this.mEditField;
  }

  public void setmEditField(boolean paramBoolean)
  {
    this.mEditField = paramBoolean;
  }

  public void setMDefault(long paramLong)
  {
    this.mDefault = paramLong;
  }

  public long getMDefault()
  {
    return this.mDefault;
  }

  public void setMMax(long paramLong)
  {
    this.mMax = paramLong;
  }

  public boolean isMRichTextField()
  {
    return this.mRichTextField;
  }

  public long getMMax()
  {
    return this.mMax;
  }

  public void setMMin(long paramLong)
  {
    this.mMin = paramLong;
  }

  public long getMMin()
  {
    return this.mMin;
  }

  public void setMHaveDefault(boolean paramBoolean)
  {
    this.mHaveDefault = paramBoolean;
  }

  public boolean isMHaveDefault()
  {
    return this.mHaveDefault;
  }

  void setMSpinner(boolean paramBoolean)
  {
    this.mSpinner = paramBoolean;
  }

  public boolean isMSpinner()
  {
    return this.mSpinner;
  }

  public void setMPrecision(int paramInt)
  {
    this.mPrecision = paramInt;
  }

  public int getMPrecision()
  {
    return this.mPrecision;
  }

  private void setMDateTimePopup(int paramInt)
  {
    this.mDateTimePopup = paramInt;
  }

  public int getMDateTimePopup()
  {
    return this.mDateTimePopup;
  }

  public void setMDefaultCode(String paramString)
  {
    this.mDefaultCode = paramString;
  }

  public String getMDefaultCode()
  {
    return this.mDefaultCode;
  }

  public void setMDefaultCurrency(String paramString)
  {
    this.mDefaultCurrency = paramString;
  }

  public String getMDefaultCurrency()
  {
    return this.mDefaultCurrency;
  }

  public void setMAllowable(CurrencyDetail[] paramArrayOfCurrencyDetail)
  {
    this.mAllowable = paramArrayOfCurrencyDetail;
  }

  public CurrencyDetail[] getMAllowable()
  {
    return this.mAllowable;
  }

  public void setMFunctional(CurrencyDetail[] paramArrayOfCurrencyDetail)
  {
    this.mFunctional = paramArrayOfCurrencyDetail;
  }

  public CurrencyDetail[] getMFunctional()
  {
    return this.mFunctional;
  }

  void setMInitialCode(String paramString)
  {
    this.mInitialCode = paramString;
  }

  public String getMInitialCode()
  {
    return this.mInitialCode;
  }

  public void setMEnumType(int paramInt)
  {
    this.mEnumType = paramInt;
  }

  public int getMEnumType()
  {
    return this.mEnumType;
  }

  public void setMEnumLabels(String[] paramArrayOfString)
  {
    this.mEnumLabels = paramArrayOfString;
  }

  public String[] getMEnumLabels()
  {
    return this.mEnumLabels;
  }

  public void setMEnumValues(String[] paramArrayOfString)
  {
    this.mEnumValues = paramArrayOfString;
  }

  public String[] getMEnumValues()
  {
    return this.mEnumValues;
  }

  public void setMEnumLimitInfo(SelectionFieldLimit paramSelectionFieldLimit)
  {
    this.mEnumLimitInfo = paramSelectionFieldLimit;
  }

  public SelectionFieldLimit getMEnumLimitInfo()
  {
    return this.mEnumLimitInfo;
  }

  public void setMEnumIds(long[] paramArrayOfLong)
  {
    this.mEnumIds = paramArrayOfLong;
  }

  public long[] getMEnumIds()
  {
    return this.mEnumIds;
  }

  public void setMIsCustom(boolean paramBoolean)
  {
    this.mIsCustom = paramBoolean;
  }

  public boolean isMIsCustom()
  {
    return this.mIsCustom;
  }

  public void setMEnumDefaultValue(int paramInt)
  {
    this.mEnumDefaultValue = paramInt;
  }

  public int getMEnumDefaultValue()
  {
    return this.mEnumDefaultValue;
  }

  private void setMRadioString(String paramString)
  {
    this.mRadioString = paramString;
  }

  public String getMRadioString()
  {
    return this.mRadioString;
  }

  public void setMLabelString(String paramString)
  {
    this.mLabelString = paramString;
  }

  public String getMLabelString()
  {
    return this.mLabelString;
  }

  protected void setMImageFlat(boolean paramBoolean)
  {
    this.mImageFlat = paramBoolean;
  }

  public boolean isMImageFlat()
  {
    return this.mImageFlat;
  }

  protected void setMControlType(int paramInt)
  {
    this.mControlType = paramInt;
  }

  public int getMControlType()
  {
    return this.mControlType;
  }

  protected void setMHtmlColour(String paramString)
  {
    this.mHtmlColour = paramString;
  }

  public String getMHtmlColour()
  {
    return this.mHtmlColour;
  }

  public void setMColour(String paramString)
  {
    this.mColour = paramString;
  }

  public String getMColour()
  {
    return this.mColour;
  }

  public void setMFont(String paramString)
  {
    this.mFont = paramString;
  }

  public String getMFont()
  {
    return this.mFont;
  }

  protected void setMButtonText(String paramString)
  {
    this.mButtonText = paramString;
  }

  public String getMButtonText()
  {
    return this.mButtonText;
  }

  protected void setMTransparent(boolean paramBoolean)
  {
    this.mTransparent = paramBoolean;
  }

  public void setMAltText(String paramString)
  {
    this.mAltText = paramString;
  }

  public String getMAltText()
  {
    return this.mAltText;
  }

  public boolean isMTransparent()
  {
    return this.mTransparent;
  }

  protected void setMImage(GoatImage paramGoatImage)
  {
    this.mImage = paramGoatImage;
  }

  public GoatImage getMImage()
  {
    return this.mImage;
  }

  protected void setMDisabledImage(GoatImage paramGoatImage)
  {
    this.mDisabledImage = paramGoatImage;
  }

  public GoatImage getMDisabledImage()
  {
    return this.mDisabledImage;
  }

  private void setMImageValue(Value paramValue)
  {
    this.mImageValue = paramValue;
  }

  public Value getMImageValue()
  {
    return this.mImageValue;
  }

  private void setMDisabledImageValue(Value paramValue)
  {
    this.mDisabledImageValue = paramValue;
  }

  public Value getMDisabledImageValue()
  {
    return this.mDisabledImageValue;
  }

  protected void setMImageMaintainRatio(boolean paramBoolean)
  {
    this.mImageMaintainRatio = paramBoolean;
  }

  public boolean isMImageMaintainRatio()
  {
    return this.mImageMaintainRatio;
  }

  protected void setMScaleImageToFit(boolean paramBoolean)
  {
    this.mScaleImageToFit = paramBoolean;
  }

  public boolean isMScaleImageToFit()
  {
    return this.mScaleImageToFit;
  }

  protected void setMImagePosition(int paramInt)
  {
    this.mImagePosition = paramInt;
  }

  public int getMImagePosition()
  {
    return this.mImagePosition;
  }

  protected void setMTextJustify(int paramInt)
  {
    this.mTextJustify = paramInt;
  }

  public int getMTextJustify()
  {
    return this.mTextJustify;
  }

  protected void setMImgButtonAltText(String paramString)
  {
    this.mImgButtonAltText = paramString;
  }

  public String getMImgButtonAltText()
  {
    return this.mImgButtonAltText;
  }

  protected void setMImgBtnMouseoverEffect(int paramInt)
  {
    this.mImgBtnMouseoverEffect = paramInt;
  }

  public int getMImgBtnMouseoverEffect()
  {
    return this.mImgBtnMouseoverEffect;
  }

  private void setMTrimType(int paramInt)
  {
    this.mTrimType = paramInt;
  }

  public int getMAttachmentFieldID()
  {
    return this.mAttachmentFieldID;
  }

  public void setMAttachmendFieldID(int paramInt)
  {
    this.mAttachmentFieldID = paramInt;
  }

  public int getMTrimType()
  {
    return this.mTrimType;
  }

  private void setMEffect(int paramInt)
  {
    this.mEffect = paramInt;
  }

  public int getMEffect()
  {
    return this.mEffect;
  }

  private void setMLineWidth(long paramLong)
  {
    this.mLineWidth = paramLong;
  }

  public long getMLineWidth()
  {
    return this.mLineWidth;
  }

  private void setMBorderColor(String paramString)
  {
    this.mBorderColor = paramString;
  }

  public String getMBorderColor()
  {
    return this.mBorderColor;
  }

  private void setMBackgroundColor(String paramString)
  {
    this.mBackgroundColor = paramString;
  }

  public String getMBackgroundColor()
  {
    return this.mBackgroundColor;
  }

  private void setMHtmlTextColor(String paramString)
  {
    this.mHtmlTextColor = paramString;
  }

  public String getMHtmlTextColor()
  {
    return this.mHtmlTextColor;
  }

  private void setMTextColor(String paramString)
  {
    this.mTextColor = paramString;
  }

  public String getMTextColor()
  {
    return this.mTextColor;
  }

  private void setMTextFont(String paramString)
  {
    this.mTextFont = paramString;
  }

  public String getMTextFont()
  {
    return this.mTextFont;
  }

  private void setMTrimTextJustify(String paramString)
  {
    this.mTrimTextJustify = paramString;
  }

  public String getMTrimTextJustify()
  {
    return this.mTrimTextJustify;
  }

  private void setMTextVAlign(String paramString)
  {
    this.mTextVAlign = paramString;
  }

  public String getMTextVAlign()
  {
    return this.mTextVAlign;
  }

  private void setMText(String paramString)
  {
    this.mText = paramString;
  }

  public String getMText()
  {
    return this.mText;
  }

  protected void setMButtonTBL(boolean paramBoolean)
  {
    this.mButtonTBL = paramBoolean;
  }

  public boolean isMButtonTBL()
  {
    return this.mButtonTBL;
  }

  protected void setMScrollbars(String paramString)
  {
    this.mScrollbars = paramString;
  }

  public String getMScrollbars()
  {
    return this.mScrollbars;
  }

  protected void setMBorder(String paramString)
  {
    this.mBorder = paramString;
  }

  public String getMBorder()
  {
    return this.mBorder;
  }

  public void setMIsGroupMenu(boolean paramBoolean)
  {
    this.mIsGroupMenu = paramBoolean;
  }

  public boolean isMIsGroupMenu()
  {
    return this.mIsGroupMenu;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes, int paramInt)
  {
    if ((Group.isGroupList(this.mDataFieldID)) || (Group.isRoleStateMapping(this.mDataFieldID)) || (Group.isMultiAssignField(this.mDataFieldID)))
    {
      paramOutputNotes.setGroupFieldPresent(true);
      paramOutputNotes.addGroupFieldMenus(GroupMenu.getMenuName(this.mDataFieldID));
    }
  }

  protected void setMImgColValue(Value paramValue)
  {
    this.mImgColValue = paramValue;
  }

  public Value getMImgColValue()
  {
    return this.mImgColValue;
  }

  public void setMAllowSort(boolean paramBoolean)
  {
    this.mAllowSort = paramBoolean;
  }

  public boolean getMAllowSort()
  {
    return this.mAllowSort;
  }

  protected void setMTableCellColorString(String paramString)
  {
    this.mTableCellColorString = paramString;
  }

  public String getMTableCellColorString()
  {
    return this.mTableCellColorString;
  }

  public static GoatImageButton getMDefaultFileUploadButton()
  {
    return MDefaultFileUploadButton;
  }

  public String getRichTextEditorClassString()
  {
    return "richtext";
  }

  public boolean isMRichTextFieldAdv()
  {
    return this.mRichTextFieldAdv;
  }

  public void setMRichTextFieldAdv(boolean paramBoolean)
  {
    this.mRichTextFieldAdv = paramBoolean;
  }

  public void setMRichTextField(boolean paramBoolean)
  {
    this.mRichTextField = paramBoolean;
  }

  public int getMHeaderAlignment(FieldGraph.Node paramNode)
  {
    if (!this.hAlignSet)
    {
      DataType localDataType = getMDataFieldDataType(paramNode);
      if (localDataType == null)
        return 2;
      if (localDataType.equals(DataType.CONTROL))
        this.mHeaderAlignment = 1;
      else if ((localDataType.equals(DataType.INTEGER)) || (localDataType.equals(DataType.DECIMAL)) || (localDataType.equals(DataType.REAL)) || (localDataType.equals(DataType.CURRENCY)))
        this.mHeaderAlignment = 0;
      else
        this.mHeaderAlignment = 2;
    }
    return this.mHeaderAlignment;
  }

  public void setMHeaderAlignment(int paramInt)
  {
    this.mHeaderAlignment = paramInt;
    this.hAlignSet = true;
  }

  public int getMDataAlignment(FieldGraph.Node paramNode)
  {
    if (!this.dAlignSet)
    {
      DataType localDataType = getMDataFieldDataType(paramNode);
      if (localDataType == null)
        return 2;
      if (localDataType.equals(DataType.CONTROL))
        this.mDataAlignment = 1;
      else if ((localDataType.equals(DataType.INTEGER)) || (localDataType.equals(DataType.DECIMAL)) || (localDataType.equals(DataType.CURRENCY)) || (localDataType.equals(DataType.REAL)))
        this.mDataAlignment = 0;
      else
        this.mDataAlignment = 2;
    }
    return this.mDataAlignment;
  }

  public void setMDataAlignment(int paramInt)
  {
    this.mDataAlignment = paramInt;
    this.dAlignSet = true;
  }

  public void setMInitialState(int paramInt)
  {
    this.mInitialState = paramInt;
  }

  public int getMInitialState()
  {
    return this.mInitialState;
  }

  public DataType getMDataFieldDataType(FieldGraph.Node paramNode)
  {
    try
    {
      String str1 = null;
      String str2 = null;
      if (isLocal())
      {
        str1 = getMLForm().getServerName();
        str2 = getMLForm().getFormName();
      }
      else
      {
        str1 = paramNode.getTableServer();
        str2 = paramNode.getTableSchema();
      }
      if ((str1 == null) || (str2 == null))
        return null;
      if (str1.equals("@"))
        str1 = getMLForm().getServerName();
      if (str2.equals("@"))
        str2 = getMLForm().getFormName();
      Map localMap = GoatField.get(Form.get(str1, str2).getViewInfoByInference(null, false, false), true);
      GoatField localGoatField = (GoatField)localMap.get(Integer.valueOf(getMDataFieldID()));
      if (localGoatField != null)
        return localGoatField.getMDataType();
      return null;
    }
    catch (Exception localException)
    {
    }
    return null;
  }

  class LocalImageFetcher
    implements GoatImage.Fetcher
  {
    private static final long serialVersionUID = 5430293912891169327L;
    private int mProp = 101;
    private String mImgRef;

    public LocalImageFetcher()
    {
    }

    public LocalImageFetcher(int paramString, String arg3)
    {
      this.mProp = paramString;
      Object localObject;
      this.mImgRef = localObject;
    }

    public byte[] reFetchImageData()
    {
      Field localField;
      try
      {
        FieldCriteria localFieldCriteria = new FieldCriteria();
        localFieldCriteria.setPropertiesToRetrieve(FieldCriteria.INSTANCE_LIST);
        localField = SessionData.get().getServerLogin(ColumnField.this.getMLForm().getServerName()).getField(ColumnField.this.getMLForm().getFormName(), ColumnField.this.getMFieldID(), localFieldCriteria);
      }
      catch (ARException localARException)
      {
        return null;
      }
      catch (GoatException localGoatException)
      {
        return null;
      }
      DisplayInstanceMap localDisplayInstanceMap = localField.getDisplayInstance();
      Iterator localIterator1 = localDisplayInstanceMap.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        Map.Entry localEntry1 = (Map.Entry)localIterator1.next();
        if (((Integer)localEntry1.getKey()).intValue() == ColumnField.this.getMView())
        {
          DisplayPropertyMap localDisplayPropertyMap = null;
          Iterator localIterator2 = localDisplayInstanceMap.entrySet().iterator();
          Map.Entry localEntry2;
          while (localIterator2.hasNext())
          {
            localEntry2 = (Map.Entry)localIterator2.next();
            if ((((Integer)localEntry2.getKey()).intValue() == ColumnField.this.getMView()) && (((DisplayPropertyMap)localEntry2.getValue()).size() > 0))
            {
              localDisplayPropertyMap = (DisplayPropertyMap)localEntry2.getValue();
              break;
            }
          }
          if (localDisplayPropertyMap == null)
            return null;
          localIterator2 = localDisplayPropertyMap.entrySet().iterator();
          while (localIterator2.hasNext())
          {
            localEntry2 = (Map.Entry)localIterator2.next();
            byte[] arrayOfByte;
            if (((Integer)localEntry2.getKey()).intValue() == this.mProp)
            {
              arrayOfByte = null;
              try
              {
                Value localValue = (Value)localEntry2.getValue();
                if (localValue.getDataType().equals(DataType.CHAR))
                {
                  String str = localValue.getValue().toString();
                  arrayOfByte = GoatImage.getImageReferenceData(str, ColumnField.this.getMLForm().getServerName());
                }
                else
                {
                  arrayOfByte = ColumnField.propToByteArray(localValue);
                }
                return arrayOfByte;
              }
              catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
              {
                GoatField.MLog.log(Level.SEVERE, localBadDisplayPropertyException.getMessage());
              }
            }
            else if (((Integer)localEntry2.getKey()).intValue() == this.mProp)
            {
              arrayOfByte = null;
              if (this.mImgRef != null)
              {
                arrayOfByte = GoatImage.getImageReferenceData(this.mImgRef, ColumnField.this.getMLForm().getServerName());
                return arrayOfByte;
              }
            }
          }
        }
      }
      return null;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.ColumnField
 * JD-Core Version:    0.6.1
 */