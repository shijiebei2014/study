package com.remedy.arsys.arreport;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Value;

public class CompactRecord
{
  private final Entry entry;

  public CompactRecord(Entry paramEntry)
  {
    assert (paramEntry.size() > 0);
    this.entry = paramEntry;
  }

  public Value getValue(int paramInt)
  {
    return (Value)this.entry.get(new Integer(paramInt));
  }

  public Entry getEntryItems()
  {
    return this.entry;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.CompactRecord
 * JD-Core Version:    0.6.1
 */