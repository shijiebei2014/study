package com.remedy.arsys.goat;

import com.bmc.arsys.api.CurrencyPartInfo;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;

public class CurrencyFieldID
  implements Serializable
{
  private static final long serialVersionUID = -2994510606550550944L;
  private static final int FIELD = 0;
  private static final int VALUE = 1;
  private static final int TYPE = 2;
  private static final int DATE = 3;
  private static final int FUNCTIONAL = 4;
  private CurrencyPartInfo mInfo;

  public int getFieldID()
  {
    return this.mInfo.getFieldId();
  }

  public CurrencyFieldID(CurrencyPartInfo paramCurrencyPartInfo)
    throws GoatException
  {
    this.mInfo = paramCurrencyPartInfo;
  }

  public String toString()
  {
    if (this.mInfo.getPartTag() == 0)
      return "" + this.mInfo.getFieldId();
    if (this.mInfo.getPartTag() == 4)
      return this.mInfo.getFieldId() + "." + this.mInfo.getCurrencyCode();
    return this.mInfo.getFieldId() + "." + this.mInfo.getPartTag();
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("F(windowID," + this.mInfo.getFieldId() + ").G().part(\"" + this.mInfo.getCurrencyCode() + "\"," + this.mInfo.getPartTag() + ")");
  }

  public void emitRemoteJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("F(" + this.mInfo.getFieldId() + ").part(\"" + this.mInfo.getCurrencyCode() + "\"," + this.mInfo.getPartTag() + ")");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.CurrencyFieldID
 * JD-Core Version:    0.6.1
 */