package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.CoordinateInfo;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DisplayInstanceMap;
import com.bmc.arsys.api.DisplayPropertyMap;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.DisplayPropertyMappers;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.intf.service.IGoatFieldService;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class GoatField extends DisplayPropertyMappers
  implements Cache.Item, Cloneable
{
  private static final long serialVersionUID = -9135891337399017511L;
  private static IGoatFieldService GOATFIELD_SERVICE;
  private DataType mDataType;
  private String mDataTypeString;
  private String mDBName;
  private int mFieldID;
  private int mParentFieldID;
  private int mView;
  private int mFieldOption;
  private String mHelpText;
  private boolean mHelpTextInit;
  private LightForm mLForm;
  public static final int EMIT_HTML = 0;
  public static final int EMIT_JS = 1;
  public static final int EMIT_NONE = 2;
  private int mEmitViewable = 0;
  private int mEmitOptimised = 2;
  private int mEmitViewless = 2;
  private boolean mInView = false;
  private boolean mParentExecDep = false;
  private boolean mHideWebHelp;
  private boolean mVisible;
  private long mZOrder;
  private long mTabOrder;
  private ARBox mARBox;
  private ARBox mARRightBox;
  private int mAlignment = 0;
  private String mLabel;
  private String mAltText;
  private String mCustomCSSStyle;
  private int mFillStyle;
  private long mMinWidth;
  private long mMaxWidth;
  private long mMinHeight;
  private long mMaxHeight;
  private String mSkinSelector;
  private boolean mDraggable;
  private boolean mDroppable;
  private int mFloat;
  private boolean mAutoCompleteHideMenuButton;
  private static final Comparator MDefaultChildSorter = new DefaultChildSortComparator();
  private static final int FUDGE_FACTOR = 10;
  public static final Field[] EmptyField = new Field[0];
  public static final int[] EmptyFieldID = new int[0];
  public static final int NullFieldID = 0;
  protected static final transient Log MLog = Log.get(6);
  private String mColHdrGradCol;
  private String mColHdrGradBkgCol;
  private long mColHdrGradType;
  private String mColHdrTextCol;
  private String mHdrFtrGradCol;
  private String mHdrFtrGradBkgCol;
  private long mHdrFtrGradType;

  public static final IGoatFieldService setGoatFieldService(IGoatFieldService paramIGoatFieldService)
  {
    GOATFIELD_SERVICE = paramIGoatFieldService;
    return paramIGoatFieldService;
  }

  public static Map get(Form.ViewInfo paramViewInfo)
    throws GoatException
  {
    return get(paramViewInfo, false);
  }

  public static Map get(Form.ViewInfo paramViewInfo, boolean paramBoolean)
    throws GoatException
  {
    return get(paramViewInfo, paramBoolean, false);
  }

  public static Map get(Form.ViewInfo paramViewInfo, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException
  {
    return GOATFIELD_SERVICE.get(paramViewInfo, paramBoolean1, paramBoolean2);
  }

  protected GoatField(Form paramForm, Field paramField, int paramInt)
  {
    setMLForm(new LightForm(paramForm));
    setMView(paramInt);
    setMVisible(true);
    setMFillStyle(0);
    setMDataType(DataType.toDataType(paramField.getDataType()));
    setMDataTypeString(TypeMap.mapDataType(getMDataType(), paramField, paramInt));
    setMFieldID(paramField.getFieldID());
    setMDBName(paramField.getName().toString());
    setMFieldOption(paramField.getFieldOption());
    setDefaultDisplayProperties();
    DisplayInstanceMap localDisplayInstanceMap = paramField.getDisplayInstance();
    Iterator localIterator1 = localDisplayInstanceMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry1 = (Map.Entry)localIterator1.next();
      if ((!isMInView()) && (((Integer)localEntry1.getKey()).intValue() == paramInt))
      {
        setMInView(true);
        DisplayPropertyMap localDisplayPropertyMap = null;
        Iterator localIterator2 = localDisplayInstanceMap.entrySet().iterator();
        Map.Entry localEntry2;
        while (localIterator2.hasNext())
        {
          localEntry2 = (Map.Entry)localIterator2.next();
          if ((((Integer)localEntry2.getKey()).intValue() == getMView()) && (((DisplayPropertyMap)localEntry2.getValue()).size() > 0))
          {
            localDisplayPropertyMap = (DisplayPropertyMap)localEntry2.getValue();
            break;
          }
        }
        if (localDisplayPropertyMap != null)
        {
          localIterator2 = localDisplayPropertyMap.entrySet().iterator();
          while (localIterator2.hasNext())
          {
            localEntry2 = (Map.Entry)localIterator2.next();
            try
            {
              handleProperty(((Integer)localEntry2.getKey()).intValue(), (Value)localEntry2.getValue());
            }
            catch (DisplayPropertyMappers.BadDisplayPropertyException localBadDisplayPropertyException)
            {
              localBadDisplayPropertyException.printStackTrace();
              MLog.warning("Bad display property found!- Form :" + getMLForm().getFormName() + " Field ID:" + getMFieldID() + " Property :" + localEntry2.getKey());
            }
          }
        }
      }
    }
    if (getMAlignment() == 1)
      setMARBox(getMARRightBox());
    if ((!isMInView()) || (getMParentFieldID() == 0))
    {
      setMParentFieldID(0);
      if (!isMInView())
        setMVisible(false);
    }
  }

  protected GoatField()
  {
  }

  public void brokenParent()
  {
    setMParentFieldID(0);
    setMInView(setMVisible(false));
  }

  public Object clone()
  {
    GoatField localGoatField = null;
    try
    {
      localGoatField = (GoatField)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return localGoatField;
  }

  public String getLocalizedTitleForField(String paramString)
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (paramString == null)
    {
      if ((getMLabel() != null) && (getMLabel().trim().length() != 0))
        return getMLabel();
      return getMDBName();
    }
    String[] arrayOfString;
    if ((getMLabel() != null) && (getMLabel().trim().length() != 0))
      arrayOfString = new String[] { getMLabel() };
    else
      arrayOfString = new String[] { getMDBName() };
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), paramString, arrayOfString);
  }

  public String getLocalizedDescriptionStringForWidget(String paramString)
  {
    String[] arrayOfString;
    if ((getMAltText() != null) && (getMAltText().trim().length() != 0))
      arrayOfString = new String[] { getMAltText() };
    else if ((getMLabel() != null) && (getMLabel().trim().length() != 0))
      arrayOfString = new String[] { getMLabel() };
    else
      arrayOfString = new String[] { getMDBName() };
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), paramString, arrayOfString);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 20)
    {
      setMLabel(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_LABEL: " + getMLabel());
    }
    else if (paramInt == 5066)
    {
      setMAltText(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + "AR_DPROP_BUTTON_ALT_TEXT:" + getMAltText());
    }
    else if (paramInt == 4)
    {
      int i = propToInt(paramValue);
      setMVisible(i != 0);
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_VISIBLE: " + isMVisible());
    }
    else if (paramInt == 3)
    {
      setMARBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BBOX: " + getMARBox().toString());
    }
    else if (paramInt == 311)
    {
      setMARRightBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_RIGHT_BBOX: " + getMARRightBox().toString());
    }
    else if (paramInt == 310)
    {
      setMAlignment(propToInt(paramValue));
    }
    else if (paramInt == 170)
    {
      setMParentFieldID(propToFieldID(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DISPLAY_PARENT: " + getMParentFieldID());
    }
    else if (paramInt == 14)
    {
      setMHideWebHelp(propToBool(paramValue));
    }
    else if (paramInt == 7)
    {
      setMZOrder(propToLong(paramValue));
      if (getMZOrder() != -1L)
        setMZOrder(getMZOrder() + -2147482648L);
      if (getMZOrder() > 2147483647L)
        setMZOrder(2147483647L);
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_Z_ORDER: " + getMZOrder());
    }
    else if (paramInt == 143)
    {
      setMTabOrder(propToLong(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_TAB_ORDER: " + getMTabOrder());
    }
    else if (paramInt == 272)
    {
      setMFillStyle(propToInt(paramValue));
    }
    else if (paramInt == 292)
    {
      setMMinWidth(new ARBox(0L, 0L, propToLong(paramValue), 0L).toBox().mW);
    }
    else if (paramInt == 293)
    {
      setMMaxWidth(new ARBox(0L, 0L, propToLong(paramValue), 0L).toBox().mW);
      if (getMMaxWidth() == 0L)
        setMMaxWidth(32767L);
    }
    else if (paramInt == 294)
    {
      setMMinHeight(new ARBox(0L, 0L, 0L, propToLong(paramValue)).toBox().mH);
    }
    else if (paramInt == 295)
    {
      setMMaxHeight(new ARBox(0L, 0L, 0L, propToLong(paramValue)).toBox().mH);
      if (getMMaxHeight() == 0L)
        setMMaxHeight(32767L);
    }
    else if (paramInt == 5061)
    {
      setMCustomCSSStyle(propToString(paramValue));
    }
    else if (paramInt == 5200)
    {
      setMSkinSelector(propToString(paramValue));
    }
    else if (paramInt == 314)
    {
      setMDraggable(propToBool(paramValue));
    }
    else if (paramInt == 315)
    {
      setMDroppable(propToBool(paramValue));
    }
    else if (paramInt == 324)
    {
      setMFloat(propToInt(paramValue));
    }
    else if (paramInt == 329)
    {
      setMAutoCompleteHideMenuButton(propToBool(paramValue));
    }
  }

  protected void setDefaultDisplayProperties()
  {
    setMHideWebHelp(false);
  }

  public String getSelectorClassNames()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (getMCustomCSSStyle() != null)
      localStringBuilder.append(getMCustomCSSStyle()).append(" ");
    localStringBuilder.append("arfid");
    localStringBuilder.append(getMFieldID());
    localStringBuilder.append(" ardbn");
    String str = getMDBName();
    for (int i = 0; i < str.length(); i++)
      if (str.charAt(i) != ' ')
        localStringBuilder.append(str.charAt(i));
    return localStringBuilder.toString();
  }

  public boolean isChildOfFillLayout(FieldGraph.Node paramNode)
  {
    boolean bool = false;
    if (getMParentFieldID() != 0)
      bool = (paramNode.mParent.mField.getMFillStyle() == 1) || (paramNode.mParent.mField.getMFillStyle() == 3);
    else
      try
      {
        Form localForm = Form.get(getMLForm().getServerName(), getMLForm().getFormName());
        bool = (localForm.getViewInfo(getMView()).getFillStyle() == 1) || (localForm.getViewInfo(getMView()).getFillStyle() == 3);
      }
      catch (GoatException localGoatException)
      {
      }
    return bool;
  }

  public boolean isChildOfFlowLayout(FieldGraph.Node paramNode)
  {
    boolean bool = false;
    if (getMParentFieldID() != 0)
      bool = paramNode.mParent.mField.getMFillStyle() == 2;
    else
      try
      {
        Form localForm = Form.get(getMLForm().getServerName(), getMLForm().getFormName());
        bool = localForm.getViewInfo(getMView()).getFillStyle() == 2;
      }
      catch (GoatException localGoatException)
      {
      }
    return bool;
  }

  public static void emitEmptyTableProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", TypeMap.mapDataType(DataType.NULL, 0));
    paramJSWriter.property("dt", TypeMap.getARDataType(TypeMap.getOperandDataType(DataType.NULL)));
  }

  public static void emitWeightHackTableProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", TypeMap.mapDataType(DataType.INTEGER, ColumnField.getMWeightFieldID()));
    paramJSWriter.property("dt", TypeMap.getARDataType(TypeMap.getOperandDataType(DataType.INTEGER)));
  }

  public String getLabel()
  {
    return getMLabel();
  }

  public String getSearchBarLabel()
  {
    return getMDBName();
  }

  public Object getSearchBarValue()
    throws GoatException
  {
    return "'" + getSearchBarLabel().replaceAll("'", "''") + "'";
  }

  public boolean hasSearchBarMenu()
  {
    return false;
  }

  public boolean amResultsList()
  {
    return false;
  }

  public boolean isVisible()
  {
    return isMVisible();
  }

  public boolean amSearchBar()
  {
    return false;
  }

  public boolean isGlobal()
  {
    return (getMFieldID() >= 1000000) && (getMFieldID() <= 1999999);
  }

  public boolean isSystemField()
  {
    return getMFieldOption() == 3;
  }

  public boolean isDataField()
  {
    return false;
  }

  public boolean hasQualifierFieldLabel()
  {
    return (isMInView()) && (getMLabel() != null) && (getMLabel().length() > 0) && (isDataField());
  }

  public String getQualifierFieldLabel()
  {
    return getMLabel();
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes, int paramInt)
  {
    assert ((paramInt == 0) || (paramInt == 1) || (paramInt == 2));
    if ((paramInt == 0) && (getMParentFieldID() == 0))
    {
      ARBox localARBox = getMARBox();
      if (localARBox != null)
      {
        Box localBox = localARBox.toBox();
        paramOutputNotes.addDimensions(localBox.mX + localBox.mW, localBox.mY + localBox.mH);
      }
    }
  }

  public Comparator getChildSortComparator()
  {
    return MDefaultChildSorter;
  }

  public int getSize()
  {
    return 1;
  }

  public final String getServer()
  {
    return getMLForm().getServerName();
  }

  public void setMDataType(DataType paramDataType)
  {
    this.mDataType = paramDataType;
  }

  public DataType getMDataType()
  {
    return this.mDataType;
  }

  public void setMDataTypeString(String paramString)
  {
    this.mDataTypeString = paramString;
  }

  public String getMDataTypeString()
  {
    return this.mDataTypeString;
  }

  public void setMDBName(String paramString)
  {
    this.mDBName = paramString;
  }

  public String getMDBName()
  {
    return this.mDBName;
  }

  public void setMParentFieldID(int paramInt)
  {
    this.mParentFieldID = paramInt;
  }

  public int getMParentFieldID()
  {
    return this.mParentFieldID;
  }

  public void setMFieldID(int paramInt)
  {
    this.mFieldID = paramInt;
  }

  public int getMFieldID()
  {
    return this.mFieldID;
  }

  public void setMView(int paramInt)
  {
    this.mView = paramInt;
  }

  public int getMView()
  {
    return this.mView;
  }

  public void setMFieldOption(int paramInt)
  {
    this.mFieldOption = paramInt;
  }

  public int getMFieldOption()
  {
    return this.mFieldOption;
  }

  private void setMHelpText(String paramString)
  {
    this.mHelpText = paramString;
  }

  public String getMHelpText()
  {
    return this.mHelpText;
  }

  protected void setMLForm(LightForm paramLightForm)
  {
    this.mLForm = paramLightForm;
  }

  public LightForm getMLForm()
  {
    return this.mLForm;
  }

  public void setMEmitViewable(int paramInt)
  {
    this.mEmitViewable = paramInt;
  }

  public int getMEmitViewable()
  {
    return this.mEmitViewable;
  }

  public int setMEmitOptimised(int paramInt)
  {
    this.mEmitOptimised = paramInt;
    return paramInt;
  }

  public int getMEmitOptimised()
  {
    return this.mEmitOptimised;
  }

  public void setMEmitViewless(int paramInt)
  {
    this.mEmitViewless = paramInt;
  }

  public int getMEmitViewless()
  {
    return this.mEmitViewless;
  }

  public void setMInView(boolean paramBoolean)
  {
    this.mInView = paramBoolean;
  }

  public boolean isMInView()
  {
    return this.mInView;
  }

  public void setMParentExecDep(boolean paramBoolean)
  {
    this.mParentExecDep = paramBoolean;
  }

  public boolean isMParentExecDep()
  {
    return this.mParentExecDep;
  }

  protected void setMHideWebHelp(boolean paramBoolean)
  {
    this.mHideWebHelp = paramBoolean;
  }

  protected boolean isMHideWebHelp()
  {
    return this.mHideWebHelp;
  }

  protected boolean setMVisible(boolean paramBoolean)
  {
    this.mVisible = paramBoolean;
    return paramBoolean;
  }

  public boolean isMVisible()
  {
    return this.mVisible;
  }

  protected void setMZOrder(long paramLong)
  {
    this.mZOrder = paramLong;
  }

  public long getMZOrder()
  {
    return this.mZOrder;
  }

  protected void setMTabOrder(long paramLong)
  {
    this.mTabOrder = paramLong;
  }

  public long getMTabOrder()
  {
    return this.mTabOrder;
  }

  public void setMARBox(ARBox paramARBox)
  {
    this.mARBox = paramARBox;
  }

  public ARBox getMARBox()
  {
    return this.mARBox;
  }

  public int getMAlignment()
  {
    return this.mAlignment;
  }

  public void setMAlignment(int paramInt)
  {
    this.mAlignment = paramInt;
  }

  public ARBox getMARRightBox()
  {
    return this.mARRightBox;
  }

  public void setMARRightBox(ARBox paramARBox)
  {
    this.mARRightBox = paramARBox;
  }

  public void setMLabel(String paramString)
  {
    this.mLabel = paramString;
  }

  public String getMLabel()
  {
    return this.mLabel;
  }

  public void setMAltText(String paramString)
  {
    this.mAltText = paramString;
  }

  public String getMAltText()
  {
    return this.mAltText;
  }

  protected void setMCustomCSSStyle(String paramString)
  {
    this.mCustomCSSStyle = paramString;
  }

  public String getMCustomCSSStyle()
  {
    return this.mCustomCSSStyle;
  }

  protected void setMFillStyle(int paramInt)
  {
    this.mFillStyle = paramInt;
  }

  public int getMFillStyle()
  {
    return this.mFillStyle;
  }

  protected void setMMaxWidth(long paramLong)
  {
    this.mMaxWidth = paramLong;
  }

  public long getMMaxWidth()
  {
    return this.mMaxWidth;
  }

  protected void setMMinWidth(long paramLong)
  {
    this.mMinWidth = paramLong;
  }

  public long getMMinWidth()
  {
    return this.mMinWidth;
  }

  protected void setMMaxHeight(long paramLong)
  {
    this.mMaxHeight = paramLong;
  }

  public long getMMaxHeight()
  {
    return this.mMaxHeight;
  }

  protected void setMMinHeight(long paramLong)
  {
    this.mMinHeight = paramLong;
  }

  public long getMMinHeight()
  {
    return this.mMinHeight;
  }

  public String getMSkinSelector()
  {
    return this.mSkinSelector;
  }

  public void setMSkinSelector(String paramString)
  {
    this.mSkinSelector = paramString;
  }

  public boolean isMDraggable()
  {
    return this.mDraggable;
  }

  public void setMDraggable(boolean paramBoolean)
  {
    this.mDraggable = paramBoolean;
  }

  public boolean isMDroppable()
  {
    return this.mDroppable;
  }

  public void setMDroppable(boolean paramBoolean)
  {
    this.mDroppable = paramBoolean;
  }

  public void setMFloat(int paramInt)
  {
    this.mFloat = paramInt;
  }

  public int getMFloat()
  {
    return this.mFloat;
  }

  public boolean isMFloat()
  {
    return this.mFloat > 0;
  }

  protected String getStyleAttributesForFill()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("min-width:" + getMMinWidth() + ";");
    localStringBuilder.append("max-width:" + getMMaxWidth() + ";");
    localStringBuilder.append("min-height:" + getMMinHeight() + ";");
    localStringBuilder.append("max-height:" + getMMaxHeight() + ";");
    return localStringBuilder.toString();
  }

  protected String getStyleAttributesForFlow()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("position: static; float: left; clear: left;");
    return localStringBuilder.toString();
  }

  protected String getHelpText()
  {
    String str = MessageTranslation.getLocalizedFieldHelp(getServer(), getMLForm().getFormName().toString(), getMFieldID());
    if (str != null)
    {
      str = HTMLWriter.escape(str);
      str = str.replaceAll("\\n", "<br>");
      return str;
    }
    return getMHelpText();
  }

  public void setHelpText(String paramString)
  {
    this.mHelpText = paramString;
    if (this.mHelpText != null)
    {
      this.mHelpText = HTMLWriter.escape(this.mHelpText);
      this.mHelpText = this.mHelpText.replaceAll("\\n", "<br>");
    }
    setHelpTextInit(true);
  }

  public boolean isHelpTextInit()
  {
    return this.mHelpTextInit;
  }

  private void setHelpTextInit(boolean paramBoolean)
  {
    this.mHelpTextInit = paramBoolean;
  }

  public boolean isMAutoCompleteHideMenuButton()
  {
    return this.mAutoCompleteHideMenuButton;
  }

  public void setMAutoCompleteHideMenuButton(boolean paramBoolean)
  {
    this.mAutoCompleteHideMenuButton = paramBoolean;
  }

  protected Value updateCoordination(Value paramValue, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    List localList = (List)paramValue.getValue();
    ArrayList localArrayList = new ArrayList();
    if (localList.size() < 2)
    {
      localArrayList.add(new CoordinateInfo((localList.size() != 0) || (paramInt1 > -1) ? paramInt1 : ((CoordinateInfo)localList.get(0)).getXCoordinate(), paramInt2 > -1 ? paramInt2 : ((CoordinateInfo)localList.get(0)).getYCoordinate()));
      localArrayList.add(new CoordinateInfo(paramInt3 > -1 ? paramInt3 : ((CoordinateInfo)localList.get(0)).getXCoordinate(), paramInt4 > -1 ? paramInt4 : ((CoordinateInfo)localList.get(0)).getYCoordinate()));
      return new Value(localArrayList);
    }
    int i = 1;
    if (localList.size() == 4)
      i = 2;
    localArrayList.add(new CoordinateInfo(paramInt1 > -1 ? paramInt1 : ((CoordinateInfo)localList.get(0)).getXCoordinate(), paramInt2 > -1 ? paramInt2 : ((CoordinateInfo)localList.get(0)).getYCoordinate()));
    localArrayList.add(new CoordinateInfo(paramInt3 > -1 ? paramInt3 : ((CoordinateInfo)localList.get(i)).getXCoordinate(), paramInt4 > -1 ? paramInt4 : ((CoordinateInfo)localList.get(i)).getYCoordinate()));
    return new Value(localArrayList);
  }

  public String getMColHdrGradColor()
  {
    return this.mColHdrGradCol;
  }

  public void setMColHdrGradColor(String paramString)
  {
    this.mColHdrGradCol = paramString;
  }

  public String getMColHdrGradBkgColor()
  {
    return this.mColHdrGradBkgCol;
  }

  public void setMColHdrGradBkgColor(String paramString)
  {
    this.mColHdrGradBkgCol = paramString;
  }

  public long getMColHdrGradtype()
  {
    return this.mColHdrGradType;
  }

  public void setMColHdrGradType(long paramLong)
  {
    this.mColHdrGradType = paramLong;
  }

  public String getMColHdrTxtCol()
  {
    return this.mColHdrTextCol;
  }

  public void setMColHdrTxtCol(String paramString)
  {
    this.mColHdrTextCol = paramString;
  }

  public void setMHdrFtrGradColor(String paramString)
  {
    this.mHdrFtrGradCol = paramString;
  }

  public String getMHdrFtrGradColor()
  {
    return this.mHdrFtrGradCol;
  }

  public void setMHdrFtrGradBkgCol(String paramString)
  {
    this.mHdrFtrGradBkgCol = paramString;
  }

  public String getMHdrFtrGradBkgColor()
  {
    return this.mHdrFtrGradBkgCol;
  }

  public void setMHdrFtrGradType(long paramLong)
  {
    this.mHdrFtrGradType = paramLong;
  }

  public long getMHdrFtrGradType()
  {
    return this.mHdrFtrGradType;
  }

  public static class LightForm
    implements Serializable
  {
    private final String mAppName;
    private final String mWebAlias;
    private final String mHelpText;
    private final String mServerName;
    private final String mServerFormNames;
    private final String mFormName;

    protected LightForm(Form paramForm)
    {
      this.mFormName = paramForm.getName();
      this.mAppName = paramForm.getAppName();
      this.mWebAlias = paramForm.getWebAlias();
      this.mHelpText = paramForm.getHelpText();
      this.mServerName = paramForm.getServerName();
      this.mServerFormNames = paramForm.getServerFormNames();
    }

    public String getAppName()
    {
      return this.mAppName;
    }

    protected String getWebAlias()
    {
      return this.mWebAlias;
    }

    public String getServerName()
    {
      return this.mServerName;
    }

    protected String getHelpText()
    {
      return this.mHelpText;
    }

    protected String getServerFormNames()
    {
      return this.mServerFormNames;
    }

    public String getFormName()
    {
      return this.mFormName;
    }
  }

  public static class DefaultChildSortComparator
    implements Comparator
  {
    public int compare(Object paramObject1, Object paramObject2)
    {
      GoatField localGoatField1 = (GoatField)paramObject1;
      GoatField localGoatField2 = (GoatField)paramObject2;
      if (localGoatField1.getMTabOrder() < localGoatField2.getMTabOrder())
        return -1;
      if (localGoatField1.getMTabOrder() > localGoatField2.getMTabOrder())
        return 1;
      int i;
      if ((localGoatField1.getMARBox() != null) && (localGoatField2.getMARBox() != null))
      {
        Box localBox1 = localGoatField1.getMARBox().toBox();
        Box localBox2 = localGoatField2.getMARBox().toBox();
        i = localBox1.mY - localBox2.mY;
        if (Math.abs(i) < 10)
          i = localBox1.mX - localBox2.mX;
      }
      else
      {
        i = -1;
      }
      if ((i == 0) && (!localGoatField1.equals(localGoatField2)))
        return -1;
      return i;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.GoatField
 * JD-Core Version:    0.6.1
 */