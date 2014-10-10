package com.remedy.arsys.arreport;

import com.bmc.arsys.api.Entry;

public class CompactResult
{
  private final Entry[] mResults;

  public CompactResult(Entry[] paramArrayOfEntry)
  {
    this.mResults = paramArrayOfEntry;
    assert (paramArrayOfEntry != null);
    assert (paramArrayOfEntry.length > 0);
  }

  public int getNumberofEntries()
  {
    return this.mResults.length;
  }

  public CompactRecord getValueRecord(int paramInt)
  {
    Entry localEntry = this.mResults[paramInt];
    return new CompactRecord(localEntry);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.CompactResult
 * JD-Core Version:    0.6.1
 */