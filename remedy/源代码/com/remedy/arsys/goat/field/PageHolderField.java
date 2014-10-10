package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.log.Log;

public class PageHolderField extends GoatField
{
  private static final long serialVersionUID = 1037568530694186887L;
  public static final String PAGEH_TABVIEW = "TabView";
  public static final String PAGEH_STACKVIEW = "StackView";
  public static final String PAGEH_STACKVIEW_FIXED = "fixed";
  public static final String PAGEH_STACKVIEW_RESIZE = "resizable";
  public static final String PAGEH_STACKVIEW_ACCORDION = "accordion";
  private boolean mTablessBorderless;
  private boolean mBorderless;
  private boolean mDropShadow;
  private boolean mTransparent;
  private long mDisplayType;
  private String mPHViewType;
  private String mStackViewType;
  private long mOrientation;
  private long mMarginTop;
  private long mMarginLeft;
  private long mMarginBottom;
  private long mMarginRight;
  private String mInitPage;
  private int mSplitter;

  public PageHolderField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    if (getMARBox() == null)
      setMInView(false);
    setMPHViewType(getMDisplayType() == 0L ? "TabView" : "StackView");
    setMStackViewType("");
    if (getMDisplayType() > 0L)
      if (getMDisplayType() == 2L)
        setMStackViewType("resizable");
      else if (getMDisplayType() == 3L)
        setMStackViewType("accordion");
      else
        setMStackViewType("fixed");
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 239)
    {
      setMTablessBorderless(propToBool(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_HIDE_PAGE_TABS_BORDERS: " + isMTablessBorderless());
    }
    else if (paramInt == 145)
    {
      setMTransparent(propToInt(paramValue) == 1);
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_BACKGROUND_MODE: " + isMTransparent());
    }
    else if (paramInt == 273)
    {
      setMDisplayType(propToLong(paramValue));
      if ((getMDisplayType() < 0L) || (getMDisplayType() > 3L))
        setMDisplayType(0L);
    }
    else if (paramInt == 274)
    {
      setMOrientation(propToLong(paramValue));
    }
    else if (paramInt == 275)
    {
      setMMarginLeft(new ARBox(0L, 0L, propToLong(paramValue), 0L).toBox().mW);
    }
    else if (paramInt == 276)
    {
      setMMarginTop(new ARBox(0L, 0L, 0L, propToLong(paramValue)).toBox().mH);
    }
    else if (paramInt == 277)
    {
      setMMarginRight(new ARBox(0L, 0L, propToLong(paramValue), 0L).toBox().mW);
    }
    else if (paramInt == 278)
    {
      setMMarginBottom(new ARBox(0L, 0L, 0L, propToLong(paramValue)).toBox().mH);
    }
    else if (paramInt == 280)
    {
      setMInitPage(propToString(paramValue));
    }
    else if (paramInt == 304)
    {
      setMSplitter(propToInt(paramValue));
    }
    else if (paramInt == 307)
    {
      setMBorderless(propToBool(paramValue));
    }
    else if (paramInt == 5216)
    {
      setMDropShadow(propToBool(paramValue));
    }
    else
    {
      super.handleProperty(paramInt, paramValue);
    }
  }

  public long getOrientation()
  {
    return getMOrientation();
  }

  public String getStackViewType()
  {
    if (getMDisplayType() > 0L)
      return getMStackViewType();
    return null;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes, int paramInt)
  {
    super.addToOutputNotes(paramOutputNotes, paramInt);
  }

  void setMTablessBorderless(boolean paramBoolean)
  {
    this.mTablessBorderless = paramBoolean;
  }

  public boolean isMTablessBorderless()
  {
    return this.mTablessBorderless;
  }

  void setMBorderless(boolean paramBoolean)
  {
    this.mBorderless = paramBoolean;
  }

  public boolean isMBorderless()
  {
    return this.mBorderless;
  }

  void setMDropShadow(boolean paramBoolean)
  {
    this.mDropShadow = paramBoolean;
  }

  public boolean isMDropShadow()
  {
    return this.mDropShadow;
  }

  void setMTransparent(boolean paramBoolean)
  {
    this.mTransparent = paramBoolean;
  }

  public boolean isMTransparent()
  {
    return this.mTransparent;
  }

  void setMDisplayType(long paramLong)
  {
    this.mDisplayType = paramLong;
  }

  public long getMDisplayType()
  {
    return this.mDisplayType;
  }

  void setMPHViewType(String paramString)
  {
    this.mPHViewType = paramString;
  }

  public String getMPHViewType()
  {
    return this.mPHViewType;
  }

  void setMStackViewType(String paramString)
  {
    this.mStackViewType = paramString;
  }

  public String getMStackViewType()
  {
    return this.mStackViewType;
  }

  void setMOrientation(long paramLong)
  {
    this.mOrientation = paramLong;
  }

  public long getMOrientation()
  {
    return this.mOrientation;
  }

  void setMMarginTop(long paramLong)
  {
    this.mMarginTop = paramLong;
  }

  public long getMMarginTop()
  {
    return this.mMarginTop;
  }

  void setMMarginLeft(long paramLong)
  {
    this.mMarginLeft = paramLong;
  }

  public long getMMarginLeft()
  {
    return this.mMarginLeft;
  }

  void setMMarginBottom(long paramLong)
  {
    this.mMarginBottom = paramLong;
  }

  public long getMMarginBottom()
  {
    return this.mMarginBottom;
  }

  void setMMarginRight(long paramLong)
  {
    this.mMarginRight = paramLong;
  }

  public long getMMarginRight()
  {
    return this.mMarginRight;
  }

  void setMInitPage(String paramString)
  {
    this.mInitPage = paramString;
  }

  public String getMInitPage()
  {
    return this.mInitPage;
  }

  void setMSplitter(int paramInt)
  {
    this.mSplitter = paramInt;
  }

  public int getMSplitter()
  {
    return this.mSplitter;
  }

  public Boolean getMTablessBorderless()
  {
    return Boolean.valueOf(this.mTablessBorderless);
  }

  public boolean getMDropShadow()
  {
    return this.mDropShadow;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.PageHolderField
 * JD-Core Version:    0.6.1
 */