package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.ExitGuideAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class ActionExitGuide extends Action
{
  private static final long serialVersionUID = -8742491118667000604L;
  private boolean mExitAll;

  public ActionExitGuide(ActiveLink paramActiveLink, ExitGuideAction paramExitGuideAction, int paramInt)
  {
    this.mExitAll = paramExitGuideAction.isCloseAll();
  }

  public boolean canTerminate()
  {
    return true;
  }

  public boolean hasGoto()
  {
    return false;
  }

  public boolean isInterruptible()
  {
    return false;
  }

  public String toString()
  {
    return "ARACTExitGuide(" + this.mExitAll + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), "ARACTExitGuide", new Object[] { "windowID", this.mExitAll ? "1" : "0" });
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionExitGuide
 * JD-Core Version:    0.6.1
 */