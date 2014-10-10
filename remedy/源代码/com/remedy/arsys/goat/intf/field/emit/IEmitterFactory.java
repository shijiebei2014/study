package com.remedy.arsys.goat.intf.field.emit;

import com.remedy.arsys.goat.field.GoatField;

public abstract interface IEmitterFactory
{
  public abstract IGoatFieldEmitter getEmitter(GoatField paramGoatField);
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.field.emit.IEmitterFactory
 * JD-Core Version:    0.6.1
 */