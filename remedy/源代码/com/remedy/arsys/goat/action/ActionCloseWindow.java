package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.CloseWindowAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class ActionCloseWindow extends Action
{
  private static final long serialVersionUID = -8382098636611568092L;
  private boolean mCloseAll;

  public ActionCloseWindow(ActiveLink paramActiveLink, CloseWindowAction paramCloseWindowAction, int paramInt)
  {
    this.mCloseAll = paramCloseWindowAction.isCloseAll();
  }

  public boolean canTerminate()
  {
    return false;
  }

  public boolean hasGoto()
  {
    return false;
  }

  public boolean isInterruptible()
  {
    return true;
  }

  public String toString()
  {
    return "ARACTCloseWindow(" + this.mCloseAll + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), "ARACTCloseWindow", new Object[] { "windowID", this.mCloseAll ? "1" : "0" });
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionCloseWindow
 * JD-Core Version:    0.6.1
 */