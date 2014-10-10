package com.remedy.arsys.goat;

import com.remedy.arsys.share.Cache.Item;

public class FormTimeStamp
  implements Cache.Item
{
  long[] ts = new long[3];

  FormTimeStamp(long[] paramArrayOfLong)
  {
    this.ts[0] = paramArrayOfLong[0];
    this.ts[1] = paramArrayOfLong[1];
    this.ts[2] = paramArrayOfLong[2];
  }

  public int getSize()
  {
    return 1;
  }

  public String getServer()
  {
    return "";
  }

  public long[] getTimes()
  {
    return this.ts;
  }

  public long getGroupCacheTime()
  {
    return this.ts[1];
  }

  public long getUserCacheTime()
  {
    return this.ts[0];
  }

  public long getStructCacheTime()
  {
    return this.ts[2];
  }

  public static boolean compare(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if ((paramArrayOfLong1 == null) || (paramArrayOfLong2 == null))
      return false;
    return (paramArrayOfLong2[0] > paramArrayOfLong1[0]) || (paramArrayOfLong2[1] > paramArrayOfLong1[1]) || (paramArrayOfLong2[2] > paramArrayOfLong1[2]);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.FormTimeStamp
 * JD-Core Version:    0.6.1
 */