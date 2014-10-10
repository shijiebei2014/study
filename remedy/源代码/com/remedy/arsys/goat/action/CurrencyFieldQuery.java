package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.CurrencyFieldID;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.share.JSWriter;

public class CurrencyFieldQuery extends FieldQuery
{
  private static final long serialVersionUID = 3074008289824803848L;
  private CurrencyFieldID mCurrencyFieldId;

  public CurrencyFieldQuery(CurrencyFieldID paramCurrencyFieldID, long paramLong1, long paramLong2, Qualifier paramQualifier, String paramString1, String paramString2)
  {
    super(paramCurrencyFieldID.getFieldID(), paramLong1, paramLong2, paramQualifier, paramString1, paramString2);
    this.mCurrencyFieldId = paramCurrencyFieldID;
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().append(getVarName());
    paramEmitter.js().append(".");
    this.mCurrencyFieldId.emitRemoteJS(paramEmitter);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.CurrencyFieldQuery
 * JD-Core Version:    0.6.1
 */