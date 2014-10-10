package com.remedy.arsys.arreport;

import com.bmc.arsys.api.Entry;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;

public abstract interface IReportRowEmitter
{
  public abstract void iteratorCallback(Entry[] paramArrayOfEntry, int paramInt)
    throws GoatException, ReportException;

  public abstract int getEntriesLimit();
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.IReportRowEmitter
 * JD-Core Version:    0.6.1
 */