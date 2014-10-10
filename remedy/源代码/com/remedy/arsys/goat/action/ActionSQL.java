package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.DirectSqlAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;

public class ActionSQL extends Action
{
  private static final long serialVersionUID = 7991130951809741301L;
  private String mActiveLinkName;
  private int mIndex;
  private ARCommandString mCmdString;

  public ActionSQL(ActiveLink paramActiveLink, DirectSqlAction paramDirectSqlAction, int paramInt)
    throws GoatException
  {
    if (paramDirectSqlAction.getCommand() == null)
    {
      if (paramDirectSqlAction.getCommand() == null)
        logAction("null command");
      throw new GoatException("Bad SQLInfo in SQL active link action");
    }
    this.mActiveLinkName = paramActiveLink.getName();
    this.mIndex = paramInt;
    this.mCmdString = new ARCommandString(paramDirectSqlAction.getCommand());
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
    return false;
  }

  public String toString()
  {
    return "ARACTSQL(" + JSWriter.escapeString(this.mActiveLinkName) + ", " + this.mIndex + "," + this.mCmdString.getKeywordReferencesAsJSArrayString() + ", " + this.mCmdString.getFieldReferencesAsJSArrayString() + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), "ARACTSQL", new Object[] { "windowID", JSWriter.escapeString(this.mActiveLinkName), JSWriter.escapeString(this.mCmdString.getCommandString()), new Integer(this.mIndex), this.mCmdString.getKeywordReferencesAsJSArrayString(), this.mCmdString.getFieldReferencesAsJSArrayString() });
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    this.mCmdString.bindToForm(paramCachedFieldMap);
  }

  public Object clone()
  {
    try
    {
      ActionSQL localActionSQL = (ActionSQL)super.clone();
      localActionSQL.mCmdString = ((ARCommandString)this.mCmdString.clone());
      return localActionSQL;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionSQL
 * JD-Core Version:    0.6.1
 */