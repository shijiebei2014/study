package com.remedy.arsys.goat;

import java.io.Serializable;

public class Box
  implements Serializable
{
  private static final long serialVersionUID = -8424903374529431703L;
  public int mX;
  public int mY;
  public int mW;
  public int mH;

  public Box(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.mX = (int)paramLong1;
    this.mY = (int)paramLong2;
    this.mW = (int)(paramLong3 - paramLong1);
    this.mH = (int)(paramLong4 - paramLong2);
  }

  public Box(Box paramBox)
  {
    this.mX = paramBox.mX;
    this.mY = paramBox.mY;
    this.mW = paramBox.mW;
    this.mH = paramBox.mH;
  }

  public String toString()
  {
    return "" + this.mX + "," + this.mY + " " + this.mW + "," + this.mH + "";
  }

  public String toCSS()
  {
    String str = TextDirStyleContext.get().lleft;
    return "top:" + this.mY + "px; " + str + ":" + this.mX + "px; width:" + this.mW + "px; height:" + this.mH + "px;";
  }

  public String toCSS(int paramInt)
  {
    if (paramInt == 1)
    {
      String str = TextDirStyleContext.get().lright;
      return "top:" + this.mY + "px; " + str + ":" + this.mX + "px; width:" + this.mW + "px; height:" + this.mH + "px;";
    }
    return toCSS();
  }

  public String toVerticalCSS(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("top:" + this.mY).append("px;");
    localStringBuilder.append("left:" + this.mX).append("px;");
    localStringBuilder.append("width:" + this.mH).append("px;");
    localStringBuilder.append("height:" + this.mW).append("px;");
    if (paramInt == 1)
      localStringBuilder.append("margin-top:-" + this.mW).append("px;");
    else if (paramInt == 2)
      localStringBuilder.append("margin-top:" + this.mH).append("px;");
    return localStringBuilder.toString();
  }

  public String toAutoCSS()
  {
    String str = TextDirStyleContext.get().lleft;
    return "top:" + this.mY + "px; " + str + ":" + this.mX + "px; width:auto; height:auto;";
  }

  public String toAutoHeightCSS(int paramInt)
  {
    String str;
    if (paramInt == 1)
      str = TextDirStyleContext.get().lright;
    else
      str = TextDirStyleContext.get().lleft;
    return "top:" + this.mY + "px; " + str + ":" + this.mX + "px; width:" + this.mW + "px; height:auto;";
  }

  public String toList()
  {
    return "" + this.mX + "," + this.mY + "," + this.mW + "," + this.mH + "";
  }

  public Box getFixedVersionForBrokenARSystemTableFieldDimensions()
  {
    Box localBox = new Box(this);
    localBox.mX += 2;
    localBox.mY += 4;
    localBox.mW -= 4;
    localBox.mH -= 4;
    if (localBox.mW < 0)
      this.mW = 0;
    if (localBox.mH < 0)
      this.mH = 0;
    return localBox;
  }

  public Box newCentredBox(int paramInt1, int paramInt2)
  {
    int i = (this.mH - paramInt2) / 2;
    int j = (this.mW - paramInt1) / 2;
    return new Box(j, i, j + paramInt1, i + paramInt2);
  }

  public Box scaleCentredBox(int paramInt1, int paramInt2)
  {
    int i = paramInt1 * this.mH / paramInt2;
    if (i > this.mW)
    {
      paramInt2 = paramInt2 * this.mW / paramInt1;
      paramInt1 = this.mW;
    }
    else
    {
      paramInt1 = i;
      paramInt2 = this.mH;
    }
    return newCentredBox(paramInt1, paramInt2);
  }

  public Box wholeChildBox()
  {
    return new Box(0L, 0L, this.mW, this.mH);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Box
 * JD-Core Version:    0.6.1
 */