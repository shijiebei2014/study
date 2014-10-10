package com.remedy.arsys.share;

import com.remedy.arsys.log.Log;
import java.util.logging.Level;

public class PropColorList
{
  private String[] mColorList = null;
  private int mEnumId;
  private boolean mIsValidColorProp;
  protected static final transient Log MLog = Log.get(6);

  public PropColorList(String paramString)
  {
    String[] arrayOfString1 = paramString.split("\\\\");
    String[] arrayOfString2 = null;
    this.mIsValidColorProp = false;
    if (arrayOfString1.length > 3)
    {
      int i = (int)Long.parseLong(arrayOfString1[0]);
      int j = Integer.parseInt(arrayOfString1[1]);
      if ((j > 0) && (arrayOfString1.length == j * 2 + 2))
      {
        this.mEnumId = i;
        int k = 0;
        for (int m = 0; m < j; m++)
          try
          {
            int n = Integer.parseInt(arrayOfString1[(m * 2 + 2)]) + 1;
            if (n > k)
              k = n;
            if (n < 0)
              MLog.log(Level.SEVERE, "Error parsing color prop list idx:" + paramString);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            MLog.log(Level.SEVERE, "Error parsing color prop list:" + paramString);
          }
        arrayOfString2 = new String[k + 1];
        for (m = 0; m < j; m++)
        {
          int i1 = Integer.parseInt(arrayOfString1[(m * 2 + 2)]) + 1;
          Long localLong = new Long(arrayOfString1[(m * 2 + 3)]);
          arrayOfString2[i1] = propToHTMLColor("0x" + Long.toHexString(localLong.longValue()));
        }
        this.mIsValidColorProp = true;
        this.mColorList = arrayOfString2;
      }
      else
      {
        this.mIsValidColorProp = false;
      }
    }
    else
    {
      this.mIsValidColorProp = false;
    }
  }

  private String propToHTMLColor(String paramString)
  {
    String str = paramString;
    if (str.startsWith("0x"))
      try
      {
        int i = Integer.parseInt(str.substring(2), 16);
        i = (i & 0xFF) << 16 | i & 0xFF00 | i >> 16 & 0xFF;
        str = "000000" + Integer.toHexString(i);
        str = "#" + str.substring(str.length() - 6);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        MLog.log(Level.SEVERE, "Error parsing color prop list color:" + paramString);
        str = "#000000";
      }
    else if (!str.startsWith("#"))
      str = "#" + str;
    return str;
  }

  public boolean isValidColorPropList()
  {
    return this.mIsValidColorProp;
  }

  public String[] getColorList()
  {
    if (this.mIsValidColorProp)
      return this.mColorList;
    return null;
  }

  public int getColorEnumId()
  {
    if (this.mIsValidColorProp)
      return this.mEnumId;
    return 0;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.PropColorList
 * JD-Core Version:    0.6.1
 */