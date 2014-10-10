package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.GotoAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class ActionGoto extends Action
{
  private static final long serialVersionUID = -7088948008374584334L;
  private long mTag;
  private long mValue;

  public ActionGoto(ActiveLink paramActiveLink, GotoAction paramGotoAction, int paramInt)
    throws GoatException
  {
    this.mTag = paramGotoAction.getTag();
    if ((this.mTag != 2L) && (this.mTag != 1L))
    {
      logAction("Unsupported goto action " + this.mTag);
      throw new GoatException("Unsupported goto action " + this.mTag);
    }
    this.mValue = paramGotoAction.getFieldIdOrValue();
  }

  public boolean canTerminate()
  {
    return false;
  }

  public boolean hasGoto()
  {
    return true;
  }

  public boolean isInterruptible()
  {
    return false;
  }

  public String toString()
  {
    if (this.mTag == 2L)
      return "ARACTGoto(" + this.mValue + ")";
    if (this.mTag == 1L)
      return "ARACTGoto(" + this.mValue + ")";
    return "";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mTag == 2L)
      paramEmitter.js().callFunction(isInterruptible(), "ARACTGoto", new Object[] { "windowID", "" + this.mValue });
    else if (this.mTag == 1L)
      paramEmitter.js().callFunction(isInterruptible(), "ARACTGoto", new Object[] { "windowID", "F(windowID," + this.mValue + ").G().toInteger()" });
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionGoto
 * JD-Core Version:    0.6.1
 */