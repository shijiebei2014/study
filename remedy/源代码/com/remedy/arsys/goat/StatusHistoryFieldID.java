package com.remedy.arsys.goat;

import com.bmc.arsys.api.StatusHistoryValueIndicator;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;

public class StatusHistoryFieldID
  implements Serializable
{
  private static final long serialVersionUID = -7740709792501423057L;
  private static final int USER = 1;
  private static final int TIME = 2;
  private int mEnumIndex;
  private int mSubPart;

  public StatusHistoryFieldID(StatusHistoryValueIndicator paramStatusHistoryValueIndicator)
    throws GoatException
  {
    this.mEnumIndex = paramStatusHistoryValueIndicator.getEnumValue();
    if (paramStatusHistoryValueIndicator.isTime())
      this.mSubPart = 2;
    else if (paramStatusHistoryValueIndicator.isUser())
      this.mSubPart = 1;
    else
      throw new GoatException("Unsupported Status History field enum:" + paramStatusHistoryValueIndicator.getEnumValue() + " time:" + paramStatusHistoryValueIndicator.isTime() + " user:" + paramStatusHistoryValueIndicator.isUser());
  }

  public int getDataType()
  {
    if (this.mSubPart == 2)
      return 7;
    return 4;
  }

  public String toString()
  {
    return "15." + this.mEnumIndex + "." + this.mSubPart;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("F(windowID,15).G().part(" + this.mEnumIndex + "," + this.mSubPart + ")");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.StatusHistoryFieldID
 * JD-Core Version:    0.6.1
 */