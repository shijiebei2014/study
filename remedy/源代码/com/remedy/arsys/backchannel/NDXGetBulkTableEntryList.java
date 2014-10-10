package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetBulkTableEntryList extends NDXRequest
{
  protected String[] mTableList;

  NDXGetBulkTableEntryList(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetBulkTableEntryList");
    if (paramArrayList.size() != 1)
      throw new GoatException("Wrong argument length, spoofed");
    this.mTableList = argToStringArray((String)paramArrayList.get(0));
    StringBuilder localStringBuilder = new StringBuilder("mTableList=");
    for (int i = 0; i < this.mTableList.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mTableList[i]);
    }
    MLog.fine(localStringBuilder.toString());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetBulkTableEntryList
 * JD-Core Version:    0.6.1
 */