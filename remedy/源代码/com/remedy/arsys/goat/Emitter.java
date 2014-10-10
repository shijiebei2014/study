package com.remedy.arsys.goat;

import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;
import java.util.Set;

public final class Emitter
{
  private JSWriter mJS;
  private OutputNotes mNotes;
  private IEmitterFactory emitterFactor;

  public Emitter(JSWriter paramJSWriter, IEmitterFactory paramIEmitterFactory)
  {
    this.mJS = paramJSWriter;
    this.mNotes = null;
    setEmitterFactor(paramIEmitterFactory);
  }

  public Emitter(JSWriter paramJSWriter, OutputNotes paramOutputNotes, IEmitterFactory paramIEmitterFactory)
  {
    this.mJS = paramJSWriter;
    this.mNotes = paramOutputNotes;
    setEmitterFactor(paramIEmitterFactory);
  }

  public Emitter(JSWriter paramJSWriter, Emitter paramEmitter)
  {
    this.mJS = paramJSWriter;
    this.mNotes = paramEmitter.mNotes;
  }

  public final JSWriter js()
  {
    return this.mJS;
  }

  public final OutputNotes notes()
  {
    return this.mNotes;
  }

  public final boolean inDOM(int paramInt)
  {
    if (this.mNotes != null)
      return this.mNotes.getFieldSetInDOM().contains(Integer.valueOf(paramInt));
    return true;
  }

  public void setEmitterFactor(IEmitterFactory paramIEmitterFactory)
  {
    this.emitterFactor = paramIEmitterFactory;
  }

  public IEmitterFactory getEmitterFactor()
  {
    return this.emitterFactor;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Emitter
 * JD-Core Version:    0.6.1
 */