package com.remedy.arsys.arreport;

import com.bmc.arsys.api.Entry;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.reporting.common.ReportException;

public class ExportNativeReportEmitter
  implements IReportRowEmitter
{
  private ExportFormat exFmt;
  private int maxFileExpEntries = 0;

  public ExportNativeReportEmitter(ExportFormat paramExportFormat)
  {
    this.exFmt = paramExportFormat;
    this.maxFileExpEntries = Configuration.getInstance().getNativeReportExportMaxEntries();
  }

  public void iteratorCallback(Entry[] paramArrayOfEntry, int paramInt)
    throws GoatException, ReportException
  {
    if (paramArrayOfEntry != null)
    {
      CompactResult localCompactResult = new CompactResult(paramArrayOfEntry);
      this.exFmt.processResults(localCompactResult);
      this.exFmt.mIteration += 1;
    }
  }

  public int getEntriesLimit()
  {
    return this.maxFileExpEntries;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arreport.ExportNativeReportEmitter
 * JD-Core Version:    0.6.1
 */