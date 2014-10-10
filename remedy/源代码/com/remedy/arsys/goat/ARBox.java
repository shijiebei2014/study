package com.remedy.arsys.goat;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;

public class ARBox
  implements Serializable
{
  private int mX;
  private int mY;
  private int mW;
  private int mH;
  private static final long serialVersionUID = 1243435129375117555L;
  private static final Configuration cfg = Configuration.getInstance();
  static final double XFactor = Double.parseDouble(cfg.getProperty("arsystem.scale_factor_X", "1.0"));
  static final double YFactor = Double.parseDouble(cfg.getProperty("arsystem.scale_factor_Y", "1.0"));
  public static final double MXFactor = 0.00889D * XFactor;
  private static final double MYFactor = 0.01D * YFactor;
  private static final double MXFactor_J = 0.00889D * XFactor;
  private static final double MYFactor_J = 0.00847D * YFactor;
  private static final double MXFactor_CK = 0.01D * XFactor;
  private static final double MYFactor_CK = 0.00923D * YFactor;

  public ARBox(long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    this.mX = (int)paramLong1;
    this.mY = (int)paramLong2;
    this.mW = (int)(paramLong3 - paramLong1);
    this.mH = (int)(paramLong4 - paramLong2);
  }

  public ARBox(ARBox paramARBox)
  {
    this.mX = paramARBox.mX;
    this.mY = paramARBox.mY;
    this.mW = paramARBox.mW;
    this.mH = paramARBox.mH;
  }

  public Box toBox()
  {
    double d1 = MXFactor;
    double d2 = MYFactor;
    String str = SessionData.get().getLocale();
    if (str.startsWith("ja"))
    {
      d1 = MXFactor_J;
      d2 = MYFactor_J;
    }
    else if ((str.startsWith("ko")) || (str.startsWith("zh")))
    {
      d1 = MXFactor_CK;
      d2 = MYFactor_CK;
    }
    return new Box(Math.round(this.mX * d1), Math.round(this.mY * d2), Math.round(this.mX + this.mW * d1), Math.round(this.mY + this.mH * d2));
  }

  public static double getXFactor()
  {
    double d = MXFactor;
    String str = SessionData.get().getLocale();
    if (str.startsWith("ja"))
      d = MXFactor_J;
    else if ((str.startsWith("ko")) || (str.startsWith("zh")))
      d = MXFactor_CK;
    return d;
  }

  public static double getYFactor()
  {
    double d = MYFactor;
    String str = SessionData.get().getLocale();
    if (str.startsWith("ja"))
      d = MYFactor_J;
    else if ((str.startsWith("ko")) || (str.startsWith("zh")))
      d = MYFactor_CK;
    return d;
  }

  public int getX()
  {
    return this.mX;
  }

  public int getY()
  {
    return this.mY;
  }

  public int getW()
  {
    return this.mW;
  }

  public int getH()
  {
    return this.mH;
  }

  public void setX(int paramInt)
  {
    this.mX = paramInt;
  }

  public void setY(int paramInt)
  {
    this.mY = paramInt;
  }

  public void setW(int paramInt)
  {
    this.mW = paramInt;
  }

  public void setH(int paramInt)
  {
    this.mH = paramInt;
  }

  public String toString()
  {
    return "x:" + this.mX + " y:" + this.mY + " h:" + this.mH + " w:" + this.mW;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ARBox
 * JD-Core Version:    0.6.1
 */