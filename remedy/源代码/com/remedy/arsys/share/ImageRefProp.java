package com.remedy.arsys.share;

import com.remedy.arsys.log.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class ImageRefProp
{
  private final transient Map<Integer, String> mImageRef = new HashMap();
  private final transient Map<Integer, String> mAltText = new HashMap();
  private int mNumEnums = 0;
  private int mEnumFldID = 0;
  protected static final transient Log MLog = Log.get(6);

  public ImageRefProp()
  {
  }

  public ImageRefProp(String paramString)
  {
    String str1 = paramString;
    str1 = str1.replace("``", "\002");
    str1 = str1.replace("`\\", "\001");
    String[] arrayOfString = str1.split("\\\\");
    if (arrayOfString.length <= 0)
    {
      MLog.log(Level.WARNING, "invalid image property:" + str1);
      return;
    }
    for (int i = 0; i < arrayOfString.length; i++)
    {
      arrayOfString[i] = arrayOfString[i].replace("\001", "\\");
      arrayOfString[i] = arrayOfString[i].replace("\002", "`");
    }
    try
    {
      i = Integer.parseInt(arrayOfString[0]);
      this.mEnumFldID = i;
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      MLog.log(Level.WARNING, "invalid image property, invalid fieldid:" + str1);
      return;
    }
    try
    {
      int j = Integer.parseInt(arrayOfString[1]);
      if (j <= 0)
      {
        MLog.log(Level.WARNING, "invalid image property, invalid number enums:" + str1);
        return;
      }
      if (arrayOfString.length < (j - 1) * 3 + 2 + 1)
      {
        MLog.log(Level.WARNING, "invalid image property, invalid number tuples:" + str1);
        return;
      }
      this.mNumEnums = j;
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      MLog.log(Level.WARNING, "invalid image property, invalid number enums:" + str1);
      return;
    }
    for (int k = 0; k < this.mNumEnums; k++)
    {
      int m = k * 3 + 2;
      int n = Integer.parseInt(arrayOfString[m]);
      String str2 = null;
      String str3 = null;
      if (m + 1 < arrayOfString.length)
      {
        str2 = arrayOfString[(m + 1)];
        if (str2.length() == 0)
          str2 = null;
      }
      if (m + 2 < arrayOfString.length)
      {
        str3 = arrayOfString[(m + 2)];
        if (str3.length() == 0)
          str3 = null;
      }
      this.mImageRef.put(Integer.valueOf(n), str2);
      this.mAltText.put(Integer.valueOf(n), str3);
    }
  }

  public String[][] getImgInfo()
  {
    if (this.mImageRef.size() <= 0)
      return (String[][])null;
    Iterator localIterator1 = this.mImageRef.keySet().iterator();
    Iterator localIterator2 = this.mAltText.keySet().iterator();
    int i = this.mNumEnums;
    String[][] arrayOfString = new String[i][3];
    for (int j = 0; localIterator1.hasNext(); j++)
    {
      Integer localInteger1 = (Integer)localIterator1.next();
      Integer localInteger2 = (Integer)localIterator2.next();
      String str1 = (String)this.mImageRef.get(localInteger1);
      String str2 = (String)this.mAltText.get(localInteger2);
      arrayOfString[j][0] = localInteger1.toString();
      arrayOfString[j][1] = str1;
      arrayOfString[j][2] = str2;
    }
    return arrayOfString;
  }

  public Map<Integer, String> getImageRefs()
  {
    if (this.mImageRef.size() <= 0)
      return null;
    return new HashMap(this.mImageRef);
  }

  public int getEnumFid()
  {
    return this.mEnumFldID;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ImageRefProp
 * JD-Core Version:    0.6.1
 */