package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.ChangeFieldAction;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DisplayPropertyMap;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.PropertyMap;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.FontTable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public class ActionChangeFields extends Action
{
  private static final long serialVersionUID = -4427229970777053130L;
  private final int mFieldId;
  private final boolean mIsFieldRef;
  private final boolean mFocus;
  private int mAccess;
  private String mCharMenu;
  private boolean mSetVisibility;
  private boolean mVisible;
  private boolean mSetColour;
  private String mColour;
  private boolean mTableRefresh;
  private boolean mBulkTableRefresh = false;
  private boolean mSetExpandCollapseTreeLevels;
  private int mExpandCollapseTreeLevels;
  private String mLabel;
  private boolean mSetFont;
  private String mFont;
  private DataType mType;
  private boolean mSetState;
  private int mState;
  private boolean mSetDraggable;
  private boolean mDraggable;
  private boolean mSetDroppable;
  private boolean mDroppable;
  private int mEntryMode;
  private boolean mSetEntryMode;
  private String mDecorator;
  private int mReqIdentifierLoc;
  private boolean mSetExpandBoxVisibility;
  private boolean mExpandBoxVisible;
  private int mExpandBoxType;
  private boolean mSetMenuBoxVisibility;
  private boolean mMenuBoxTypeVisible;
  private int mMenuBoxType;
  private boolean mSetCharBorderVisibility;
  private boolean mCharBorderVisible;

  public ActionChangeFields(ActiveLink paramActiveLink, ChangeFieldAction paramChangeFieldAction, int paramInt)
  {
    this.mFieldId = paramChangeFieldAction.getFieldId();
    this.mIsFieldRef = ((paramChangeFieldAction.getOption() & 0x1) != 0);
    this.mFocus = (paramChangeFieldAction.getFocus() == 1);
    this.mAccess = paramChangeFieldAction.getAccessOption();
    if ((this.mAccess != 0) && (this.mAccess != 1) && (this.mAccess != 2) && (this.mAccess != 3))
    {
      logAction("Change-fields access " + this.mAccess + " not supported");
      this.mAccess = 0;
    }
    this.mCharMenu = paramChangeFieldAction.getCharMenu();
    if (this.mCharMenu != null)
      if (this.mCharMenu.equals(""))
        this.mCharMenu = null;
      else if (this.mCharMenu.indexOf("$") == -1)
        try
        {
          Menu.MKey localMKey = new Menu.MKey(paramActiveLink.getServer(), SessionData.get().getLocale(), this.mCharMenu);
          Menu.getMenuFromServer(localMKey);
        }
        catch (GoatException localGoatException)
        {
          this.mCharMenu = null;
        }
    DisplayPropertyMap localDisplayPropertyMap = paramChangeFieldAction.getProps();
    Iterator localIterator = localDisplayPropertyMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      int i = ((Integer)localEntry.getKey()).intValue();
      Value localValue = (Value)localEntry.getValue();
      if (i == 4)
        setVisibility(localValue);
      else if (i == 24)
        setColour(localValue);
      else if (i == 225)
        setTableRefresh(localValue);
      else if (i == 270)
        setExpandCollapseTreeLevels(localValue);
      else if (i == 20)
        setLabel(localValue);
      else if (i == 22)
        setFont(localValue);
      else if (i == 286)
        setState(localValue);
      else if (i == 314)
        setDraggable(localValue);
      else if (i == 315)
        setDroppable(localValue);
      else if (i == 5203)
        setEntryMode(localValue, paramActiveLink);
      else if (i == 231)
        setExpandBoxVisibility(localValue, i);
      else if (i == 5224)
        setMenuBoxVisibility(localValue, i);
      else if (i == 5225)
        setCharFieldBorderVisibility(localValue);
      else
        logAction("Change-fields tag " + i + " not supported");
    }
  }

  private void setState(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.INTEGER))
    {
      this.mSetState = true;
      this.mState = ((Integer)paramValue.getValue()).intValue();
    }
    else
    {
      logAction("Unsupported AR_DPROP_PAGE_BODY_STATE data type");
    }
  }

  private void setVisibility(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetVisibility = true;
      this.mVisible = (((Integer)paramValue.getValue()).intValue() != 0);
    }
    else
    {
      logAction("Unsupported AR_DPROP_VISIBLE data type");
    }
  }

  private void setColour(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.CHAR))
    {
      String str = paramValue.toString();
      assert (str != null);
      if (str.startsWith("0x"))
      {
        int i = Integer.parseInt(str.substring(2), 16);
        i = (i & 0xFF) << 16 | i & 0xFF00 | i >> 16 & 0xFF;
        str = "000000" + Integer.toHexString(i);
        this.mSetColour = true;
        this.mColour = ("#" + str.substring(str.length() - 6));
      }
      else if (str.equals(""))
      {
        this.mSetColour = true;
        this.mColour = null;
      }
      else
      {
        logAction("Unsupported AR_DPROP_LABEL_COLOR_TEXT value " + str);
      }
    }
    else
    {
      logAction("Unsupported AR_DPROP_LABEL_COLOR_TEXT data type");
    }
  }

  private void setLabel(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.CHAR))
    {
      this.mLabel = paramValue.toString();
      if ((!$assertionsDisabled) && (this.mLabel == null))
        throw new AssertionError();
    }
    else
    {
      logAction("Unsupported AR_DPROP_LABEL data type");
    }
  }

  private void setFont(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.CHAR))
    {
      String str = paramValue.toString();
      if ((str != null) && (!str.equalsIgnoreCase("default")))
      {
        this.mSetFont = true;
        this.mFont = FontTable.mapFontToClassName(str);
      }
    }
    else
    {
      logAction("Unsupported AR_DPROP_LABEL_FONT_STYLE data type");
    }
  }

  private void setTableRefresh(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      if (((Integer)paramValue.getValue()).intValue() == 1)
        this.mTableRefresh = true;
    }
    else
      logAction("Unsupported AR_DPROP_REFRESH data type");
  }

  private void setExpandCollapseTreeLevels(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.INTEGER))
    {
      this.mSetExpandCollapseTreeLevels = true;
      this.mExpandCollapseTreeLevels = ((Integer)paramValue.getValue()).intValue();
      if ((this.mExpandCollapseTreeLevels != 1) && (this.mExpandCollapseTreeLevels != 2))
      {
        logAction("Change-fields exapnd/collapse tree levels " + this.mExpandCollapseTreeLevels + " not supported");
        this.mSetExpandCollapseTreeLevels = false;
      }
    }
    else
    {
      logAction("Unsupported AR_DPROP_EXPAND_COLLAPSE_TREE_LEVELS data type");
    }
  }

  private void setDraggable(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetDraggable = true;
      this.mDraggable = (((Integer)paramValue.getValue()).intValue() != 0);
    }
    else
    {
      logAction("Unsupported AR_DPROP_FIELD_DRAGGABLE data type");
    }
  }

  private void setDroppable(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetDroppable = true;
      this.mDroppable = (((Integer)paramValue.getValue()).intValue() != 0);
    }
    else
    {
      logAction("Unsupported AR_DPROP_FIELD_DROPPABLE data type");
    }
  }

  private void setEntryMode(Value paramValue, ActiveLink paramActiveLink)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetEntryMode = true;
      this.mEntryMode = ((Integer)paramValue.getValue()).intValue();
      ServerInfo localServerInfo = null;
      try
      {
        localServerInfo = ServerInfo.get(paramActiveLink.getServer());
        this.mDecorator = localServerInfo.getDecorator();
        this.mReqIdentifierLoc = localServerInfo.getReqIdentifierLoc();
      }
      catch (GoatException localGoatException)
      {
        mLog.log(Level.SEVERE, "Retrieving Required Identifier :ActionChangeFields", localGoatException);
      }
    }
    else
    {
      logAction("Unsupported AR_DPROP_FIELD_PROCESS_ENTRY_MODE data type");
    }
  }

  private void setExpandBoxVisibility(Value paramValue, int paramInt)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetExpandBoxVisibility = true;
      this.mExpandBoxVisible = (((Integer)paramValue.getValue()).intValue() != 0);
      this.mExpandBoxType = paramInt;
    }
    else
    {
      logAction("Unsupported " + paramInt + " data type");
    }
  }

  private void setMenuBoxVisibility(Value paramValue, int paramInt)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetMenuBoxVisibility = true;
      this.mMenuBoxTypeVisible = (((Integer)paramValue.getValue()).intValue() != 0);
      this.mMenuBoxType = paramInt;
    }
    else
    {
      logAction("Unsupported " + paramInt + " data type");
    }
  }

  private void setCharFieldBorderVisibility(Value paramValue)
  {
    if (paramValue.getDataType().equals(DataType.ENUM))
    {
      this.mSetCharBorderVisibility = true;
      this.mCharBorderVisible = (((Integer)paramValue.getValue()).intValue() != 0);
    }
    else
    {
      logAction("Unsupported AR_DPROP_CHARFIELD_BORDER data type");
    }
  }

  public boolean canTerminate()
  {
    return false;
  }

  public boolean hasGoto()
  {
    return false;
  }

  public boolean isInterruptible()
  {
    return (this.mFocus) || (this.mTableRefresh) || (this.mSetState) || (this.mBulkTableRefresh);
  }

  boolean isTableRefresh()
  {
    return this.mTableRefresh;
  }

  void setBulkTableRefresh(boolean paramBoolean)
  {
    this.mBulkTableRefresh = paramBoolean;
    if (paramBoolean)
      this.mTableRefresh = false;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.mFocus)
      localStringBuilder.append("ARACTSetFocus(").append(getFieldId()).append(");");
    if (this.mAccess != 0)
      localStringBuilder.append("ARACTSetAccess(").append(getFieldId()).append(", ").append(this.mAccess).append(");");
    if (this.mCharMenu != null)
      localStringBuilder.append("ARACTSetCharMenu(").append(getFieldId()).append(", \"").append(this.mCharMenu).append("\");");
    if (this.mSetVisibility)
      localStringBuilder.append("ARACTSetVisibility(").append(getFieldId()).append(", ").append(this.mVisible).append(");");
    if (this.mSetColour)
      localStringBuilder.append("ARACTSetColour(").append(getFieldId()).append(", ").append("\"" + this.mColour + "\"").append(");");
    if (this.mTableRefresh)
      localStringBuilder.append("ARACTTableRefresh(").append(getFieldId()).append(");");
    if (this.mSetExpandCollapseTreeLevels)
      localStringBuilder.append("ARACTExpandCollapseTreeLevels(").append(getFieldId()).append(", ").append(this.mExpandCollapseTreeLevels).append(");");
    if (this.mLabel != null)
      localStringBuilder.append("ARACTSetLabel(").append(getFieldId()).append(", ").append(this.mLabel).append(");");
    if (this.mSetFont)
      localStringBuilder.append("ARACTSetFont(").append(getFieldId()).append(", ").append(this.mFont).append(");");
    if (this.mSetState)
      localStringBuilder.append("ARACTSetPageState(").append(getFieldId()).append(", ").append(this.mState).append(");");
    if (this.mSetDraggable)
      localStringBuilder.append("ARACTSetDraggable(").append(getFieldId()).append(", ").append(this.mDraggable).append(");");
    if (this.mSetDroppable)
      localStringBuilder.append("ARACTSetDroppable(").append(getFieldId()).append(", ").append(this.mDroppable).append(");");
    if (this.mSetEntryMode)
      localStringBuilder.append("ARACTSetEntryMode(").append(getFieldId()).append(", ").append(this.mEntryMode).append(" , ").append(this.mDecorator).append(" , ").append(this.mReqIdentifierLoc).append(");");
    if (this.mSetExpandBoxVisibility)
      localStringBuilder.append("ARACTSetExpandBoxVisibility(").append(getFieldId()).append(", ").append(this.mExpandBoxVisible).append(", ").append(this.mExpandBoxType).append(");");
    if (this.mSetMenuBoxVisibility)
      localStringBuilder.append("ARACTSetExpandBoxVisibility(").append(getFieldId()).append(", ").append(this.mMenuBoxTypeVisible).append(", ").append(this.mMenuBoxType).append(");");
    if (this.mSetCharBorderVisibility)
      localStringBuilder.append("ARACTSetBorderVisibility(").append(getFieldId()).append(", ").append(this.mCharBorderVisible).append(");");
    return localStringBuilder.toString();
  }

  String getFieldId()
  {
    return (this.mIsFieldRef ? "*" : "") + this.mFieldId;
  }

  int getFieldIdInt()
  {
    return this.mFieldId;
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    if ((!this.mIsFieldRef) && (!paramEmitter.inDOM(this.mFieldId)) && (!DataType.COLUMN.equals(this.mType)) && (!DataType.CONTROL.equals(this.mType)) && (!isNonOptimizedField(this.mFieldId)))
    {
      localJSWriter.comment("Change-field: " + this.mFieldId + " not in HTML DOM");
      if (FormContext.get().getWorkflowLogging())
        localJSWriter.statement("LogWrite(\"Change-field: " + this.mFieldId + " not in HTML DOM\");");
      return;
    }
    if (FormContext.get().getWorkflowLogging())
      paramEmitter.js().statement("LogWrite(\"&nbsp;&nbsp;For field -- \" + F(windowID," + this.mFieldId + ").GLabelOrName()+" + "\"(" + this.mFieldId + ")\")");
    if ((this.mAccess == 0) && (this.mCharMenu == null) && (!this.mSetVisibility) && (!this.mSetColour) && (!this.mTableRefresh) && (!this.mSetExpandCollapseTreeLevels) && (this.mLabel == null) && (!this.mSetFont) && (!this.mFocus) && (!this.mSetState) && (!this.mSetDraggable) && (!this.mSetDroppable) && (!this.mSetEntryMode) && (!this.mSetExpandBoxVisibility) && (!this.mSetMenuBoxVisibility) && (!this.mSetCharBorderVisibility))
    {
      localJSWriter.comment("Change-field: " + this.mFieldId + " doesn't change anything");
      return;
    }
    if (this.mAccess != 0)
      emitJS(localJSWriter, false, "ARACTSetAccess", new String[] { "windowID", null, "" + this.mAccess });
    if (this.mCharMenu != null)
      emitJS(localJSWriter, false, "ARACTSetMenu", new String[] { "windowID", null, JSWriter.escapeString(this.mCharMenu) });
    if (this.mSetVisibility)
      emitJS(localJSWriter, false, "ARACTSetVisibility", new String[] { "windowID", null, this.mVisible ? "1" : "0" });
    if (this.mSetColour)
      emitJS(localJSWriter, false, "ARACTSetLabelColour", new String[] { "windowID", null, this.mColour == null ? "null" : JSWriter.escapeString(this.mColour) });
    if ((this.mTableRefresh) && (!this.mBulkTableRefresh))
      emitJS(localJSWriter, true, "ARACTTableRefresh", new String[] { "windowID", null });
    if (this.mSetExpandCollapseTreeLevels)
      emitJS(localJSWriter, false, "ARACTExpandCollapseTreeLevels", new String[] { "windowID", null, "" + this.mExpandCollapseTreeLevels });
    if (this.mLabel != null)
      emitJS(localJSWriter, false, "ARACTSetLabel", new String[] { "windowID", null, JSWriter.escapeString(this.mLabel) });
    if (this.mSetFont)
      emitJS(localJSWriter, false, "ARACTSetFont", new String[] { "windowID", null, JSWriter.escapeString(this.mFont) });
    if (this.mFocus)
      emitJS(localJSWriter, true, "ARACTSetFocus", new String[] { "windowID", null });
    if (this.mSetState)
      emitJS(localJSWriter, true, "ARACTSetPageState", new String[] { "windowID", null, "" + this.mState });
    if (this.mSetDraggable)
      emitJS(localJSWriter, false, "ARACTSetDraggable", new String[] { "windowID", null, this.mDraggable ? "1" : "0" });
    if (this.mSetDroppable)
      emitJS(localJSWriter, false, "ARACTSetDroppable", new String[] { "windowID", null, this.mDroppable ? "1" : "0" });
    if (this.mSetEntryMode)
      emitJS(localJSWriter, false, "ARACTSetEntryMode", new String[] { "windowID", null, "" + this.mEntryMode, "\"" + this.mDecorator + "\"", "" + this.mReqIdentifierLoc });
    if (this.mSetExpandBoxVisibility)
      emitJS(localJSWriter, false, "ARACTSetExpandBoxVisibility", new String[] { "windowID", null, this.mExpandBoxVisible ? "1" : "0", this.mExpandBoxType + "" });
    if (this.mSetMenuBoxVisibility)
      emitJS(localJSWriter, false, "ARACTSetExpandBoxVisibility", new String[] { "windowID", null, this.mMenuBoxTypeVisible ? "1" : "0", this.mMenuBoxType + "" });
    if (this.mSetCharBorderVisibility)
      emitJS(localJSWriter, false, "ARACTSetBorderVisibility", new String[] { "windowID", null, this.mCharBorderVisible ? "1" : "0" });
  }

  private void emitJS(JSWriter paramJSWriter, boolean paramBoolean, String paramString, String[] paramArrayOfString)
  {
    assert (paramArrayOfString[1] == null);
    paramArrayOfString[1] = ("" + this.mFieldId);
    if (this.mIsFieldRef)
    {
      String[] arrayOfString = new String[paramArrayOfString.length + 1];
      arrayOfString[0] = paramString;
      System.arraycopy(paramArrayOfString, 0, arrayOfString, 1, paramArrayOfString.length);
      paramJSWriter.callFunction(paramBoolean, "ARACTIndirectChangeField", arrayOfString);
    }
    else
    {
      paramJSWriter.callFunction(paramBoolean, paramString, paramArrayOfString);
    }
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    Field localField = (Field)paramCachedFieldMap.get(Integer.valueOf(this.mFieldId));
    if (localField == null)
    {
      if (isNonOptimizedField(this.mFieldId))
      {
        this.mType = getNonOptimizedFieldDataType(this.mFieldId);
        return;
      }
      throw new GoatException("Missing field " + this.mFieldId + " in changefields binding");
    }
    this.mType = DataType.toDataType(localField.getDataType());
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    if ((this.mCharMenu != null) && (this.mCharMenu.indexOf("$") == -1))
      paramOutputNotes.addFieldMenus(this.mCharMenu);
    if ((this.mSetVisibility) && (this.mVisible))
      if (this.mIsFieldRef)
        paramOutputNotes.addIndirectChangeFieldVisibility();
      else
        paramOutputNotes.addFieldMadeVisible(this.mFieldId);
    if (this.mSetEntryMode)
      paramOutputNotes.addFieldMadeProcessReq(this.mFieldId);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionChangeFields
 * JD-Core Version:    0.6.1
 */