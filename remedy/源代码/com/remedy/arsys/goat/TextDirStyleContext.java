package com.remedy.arsys.goat;

public class TextDirStyleContext
{
  public final String textDir;
  public final String lleft;
  public final String lright;
  public final String tabPrevSideRounded;
  public final String tabNextSideRounded;
  public final String tabPrev;
  public final String tabNext;
  public final String prevLabel;
  public final String nextLabel;
  public final String ljustifyLeftSuffix;
  public final String ljustifyRightSuffix;
  public final String lpadding;
  public final String leftrnd;
  public final String rightrnd;
  private static final TextDirStyleContext LTRStyle = new TextDirStyleContext(true);
  private static final TextDirStyleContext RTLStyle = new TextDirStyleContext(false);
  private final boolean isLTR;
  private static ThreadLocal<TextDirStyleContext> MThreadLocal = new ThreadLocal()
  {
    protected synchronized TextDirStyleContext initialValue()
    {
      return null;
    }
  };

  private TextDirStyleContext(boolean paramBoolean)
  {
    this.isLTR = paramBoolean;
    this.textDir = (paramBoolean ? "ltr" : "rtl");
    this.lleft = (paramBoolean ? "left" : "right");
    this.lright = (paramBoolean ? "right" : "left");
    this.tabPrevSideRounded = (paramBoolean ? "TabLeftRounded" : "TabRightRounded");
    this.tabNextSideRounded = (paramBoolean ? "TabRightRounded" : "TabRightRounded");
    this.tabPrev = (paramBoolean ? "TabLeft" : "TabRight");
    this.tabNext = (paramBoolean ? "TabRight" : "TabLeft");
    this.prevLabel = (paramBoolean ? "<<" : ">>");
    this.nextLabel = (paramBoolean ? ">>" : "<<");
    this.ljustifyLeftSuffix = (paramBoolean ? "l" : "r");
    this.ljustifyRightSuffix = (paramBoolean ? "r" : "l");
    this.lpadding = (paramBoolean ? "padding-left:" : "padding-right:");
    this.leftrnd = (paramBoolean ? "leftrnd" : "rightrnd");
    this.rightrnd = (paramBoolean ? "rightrnd" : "leftrnd");
  }

  public boolean isRTL()
  {
    return !this.isLTR;
  }

  public static TextDirStyleContext getLTRStyle()
  {
    return LTRStyle;
  }

  public static TextDirStyleContext getRTLStyle()
  {
    return RTLStyle;
  }

  public static void set(TextDirStyleContext paramTextDirStyleContext)
  {
    if (MThreadLocal.get() == null)
      MThreadLocal.set(paramTextDirStyleContext);
  }

  public static TextDirStyleContext get()
  {
    TextDirStyleContext localTextDirStyleContext = (TextDirStyleContext)MThreadLocal.get();
    if (localTextDirStyleContext == null)
      return RTLStyle;
    return localTextDirStyleContext;
  }

  public static void reset()
  {
    MThreadLocal.set(null);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.TextDirStyleContext
 * JD-Core Version:    0.6.1
 */