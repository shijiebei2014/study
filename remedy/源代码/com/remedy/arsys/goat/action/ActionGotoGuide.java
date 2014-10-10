package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.GotoGuideLabelAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class ActionGotoGuide extends Action
{
  private static final long serialVersionUID = -6871693301557510498L;
  private String mLabel;

  public ActionGotoGuide(ActiveLink paramActiveLink, GotoGuideLabelAction paramGotoGuideLabelAction, int paramInt)
    throws GoatException
  {
    this.mLabel = paramGotoGuideLabelAction.getLabel();
    if (this.mLabel == null)
    {
      logAction("null label name");
      throw new GoatException("Bad GotoGuideLabelInfo");
    }
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
    return "ARACTGotoGuideLabel(windowID," + JSWriter.escapeString(this.mLabel) + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), "ARACTGotoGuideLabel", new Object[] { "windowID", JSWriter.escapeString(this.mLabel) });
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionGotoGuide
 * JD-Core Version:    0.6.1
 */