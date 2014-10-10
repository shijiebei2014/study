package com.remedy.arsys.goat;

import com.bmc.arsys.api.ActiveLink;
import com.bmc.arsys.api.ObjectPropertyMap;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.log.Log;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.logging.Level;

public class ExecutionEvent
  implements Serializable
{
  private static final long serialVersionUID = 8883322025734391635L;
  public static final int ON_SUBMIT = 0;
  public static final int ON_AFTER_SUBMIT = 1;
  public static final int ON_MODIFY = 2;
  public static final int ON_AFTER_MODIFY = 3;
  public static final int ON_QUERY = 4;
  public static final int ON_SET_DEFAUT = 5;
  public static final int ON_DISPLAY = 6;
  public static final int ON_UNDISPLAY = 7;
  public static final int ON_WINDOW_OPEN = 8;
  public static final int ON_WINDOW_CLOSE = 9;
  public static final int ON_LOADED = 10;
  public static final int ON_COPY_SUBMIT = 11;
  public static final int ON_EVENT = 12;
  public static final int ON_INTERVAL = 13;
  public static final int ON_BUTTON = 14;
  public static final int ON_RETURN = 15;
  public static final int ON_MENU_CHOICE = 16;
  public static final int ON_GAIN_FOCUS = 17;
  public static final int ON_LOSE_FOCUS = 18;
  public static final int ON_TABLE_CONTENT_CHANGE = 19;
  public static final int ON_FIELD_HOVER_LABEL = 20;
  public static final int ON_FIELD_HOVER_DATA = 21;
  public static final int ON_FIELD_HOVER_FIELD = 22;
  public static final int ON_PAGE_EXPAND = 23;
  public static final int ON_PAGE_COLLAPSE = 24;
  public static final int ON_DRAG = 25;
  public static final int ON_DROP = 26;
  public static final int ON_MAX = 26;
  public static boolean[] logstatement = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, false, false, false, false, true, true, true, true };
  public static final int[] MEventMap = { 4, 4096, 8, 2048, 1024, 512, 16, 65536, 16384, 32768, 262144, 131072, 1048576, 524288, 1, 2, 128, 8192, 256, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456 };
  public static final String[] MEventNames = { "Submit", "AfterSubmit", "Modify", "AfterModify", "Query", "SetDefault", "Display", "Undisplay", "WindowOpen", "WindowClose", "WindowLoaded", "CopySubmit", "Event", "Interval", "Button", "Return", "MenuChoice", "GainFocus", "LoseFocus", "TableContentChange", "HoverLabel", "HoverData", "HoverField", "PageExpand", "PageCollapse", "Drag", "Drop" };
  private static final int evtHasFocusField = 134184960;
  public static final int hoverEvts = 7340032;
  public static final int hoverDataLabel = 3145728;
  private int mEventMask;
  private long mFocusFieldId;
  private long mControlFieldId;
  private int mInterval;
  private static transient Log MLog = Log.get(7);

  public ExecutionEvent(ActiveLink paramActiveLink)
  {
    long l1 = paramActiveLink.getExecuteMask();
    long l2 = paramActiveLink.getControlField();
    this.mEventMask = 0;
    for (int i = 0; i < MEventMap.length; i++)
      if ((l1 & MEventMap[i]) != 0L)
        this.mEventMask |= 1 << i;
    if ((this.mEventMask & 0x700000) != 0)
    {
      if ((this.mEventMask & 0x700000) == 7340032)
      {
        this.mEventMask &= -7340033;
        this.mEventMask |= 4194304;
        MLog.log(Level.WARNING, paramActiveLink.getName() + " event Hover firing condition changed to field");
      }
      if ((this.mEventMask & 0x300000) == 3145728)
      {
        this.mEventMask &= -7340033;
        this.mEventMask |= 4194304;
        MLog.log(Level.WARNING, paramActiveLink.getName() + " event Hover(data/label) firing condition changed to field");
      }
      if (((this.mEventMask & 0x400000) != 0) && (((this.mEventMask & 0x100000) != 0) || ((this.mEventMask & 0x200000) != 0)))
      {
        this.mEventMask &= -7340033;
        this.mEventMask |= 4194304;
        MLog.log(Level.WARNING, paramActiveLink.getName() + " event Hover(field with data or label) firing condition changed to field");
      }
    }
    this.mInterval = 0;
    if ((this.mEventMask & 0x2000) != 0)
    {
      Value localValue = findProperty(paramActiveLink.getProperties(), 60021);
      if (localValue != null)
      {
        String str = localValue.toString();
        try
        {
          this.mInterval = Integer.parseInt(str);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          System.out.println("Corrupted interval property: " + str);
        }
      }
      if (this.mInterval <= 0)
        this.mEventMask &= -8193;
    }
    if ((this.mEventMask & 0x4000) != 0)
    {
      if (l2 == 0L)
        this.mEventMask &= -16385;
      this.mControlFieldId = l2;
    }
    else
    {
      this.mControlFieldId = 0L;
    }
    if ((this.mEventMask & 0x7FF8000) != 0)
      this.mFocusFieldId = paramActiveLink.getFocusField();
    else
      this.mFocusFieldId = 0L;
  }

  public ExecutionEvent(int paramInt)
  {
    this.mEventMask = 31;
    this.mFocusFieldId = paramInt;
    this.mControlFieldId = paramInt;
    this.mInterval = 0;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < MEventNames.length; i++)
      if ((this.mEventMask & 1 << i) != 0)
      {
        localStringBuilder.append(MEventNames[i]);
        if (i == 13)
          localStringBuilder.append("(" + this.mInterval + ")");
        else if (i == 14)
          localStringBuilder.append("(" + this.mControlFieldId + ")");
        else if ((i >= 15) && (i <= 26))
          localStringBuilder.append("(" + this.mFocusFieldId + ")");
        if (this.mEventMask > 1 << i)
          localStringBuilder.append(",");
      }
    return localStringBuilder.toString();
  }

  public int getExecutionMask()
  {
    return this.mEventMask;
  }

  public void setExecutionMask(int paramInt)
  {
    this.mEventMask = paramInt;
  }

  public long getFocusField()
  {
    return this.mFocusFieldId;
  }

  public int getInteval()
  {
    return this.mInterval;
  }

  public long getControlFieldId()
  {
    return this.mControlFieldId;
  }

  public long getEvtParm(int paramInt)
  {
    if (paramInt == 524288)
      return new Integer(this.mInterval).longValue();
    if (paramInt == 1)
      return this.mControlFieldId;
    if ((paramInt >= 2) && (paramInt <= 268435456))
      return this.mFocusFieldId;
    return -1L;
  }

  public String getCollectionName(int paramInt)
  {
    assert ((paramInt >= 0) && (paramInt <= 26));
    StringBuilder localStringBuilder = new StringBuilder(MEventNames[paramInt]);
    if (paramInt == 13)
      localStringBuilder.append(this.mInterval);
    else if (paramInt == 14)
      localStringBuilder.append(this.mControlFieldId);
    else if ((paramInt >= 15) && (paramInt <= 26))
      localStringBuilder.append(this.mFocusFieldId);
    return localStringBuilder.toString();
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
  }

  public static Value findProperty(ObjectPropertyMap paramObjectPropertyMap, int paramInt)
  {
    if (paramObjectPropertyMap != null)
      return (Value)paramObjectPropertyMap.get(new Integer(paramInt));
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ExecutionEvent
 * JD-Core Version:    0.6.1
 */