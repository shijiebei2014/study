package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class ActionCommit extends Action
{
  private static final long serialVersionUID = 8005327659165975255L;

  public ActionCommit(ActiveLink paramActiveLink, int paramInt)
    throws GoatException
  {
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
    return "ARACTCommit()";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), "ARACTCommit", new Object[] { "windowID" });
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionCommit
 * JD-Core Version:    0.6.1
 */