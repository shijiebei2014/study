package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;

public class ActionProcess extends Action
{
  private static final long serialVersionUID = -2453321189482321212L;
  protected String mProcess;
  protected long mTimestamp;
  protected int mActionIndex;
  protected String mActiveLinkName;
  protected RunProcessCommandInfo mCmdInfo;

  public ActionProcess(String paramString1, String paramString2, int paramInt)
    throws GoatException
  {
    this.mProcess = paramString1;
    this.mCmdInfo = new RunProcessCommandInfo(this.mProcess);
    this.mActiveLinkName = paramString2;
    this.mActionIndex = paramInt;
  }

  public ActionProcess(ActiveLink paramActiveLink, String paramString, int paramInt)
    throws GoatException
  {
    this(paramString, paramActiveLink.getName(), paramInt);
    if (!this.mCmdInfo.isRunProcess())
      mLog.fineAndConsole("Process string " + paramString + " in active link " + this.mActiveLinkName + " is invalid in the context of  run process $PROCESS$");
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
    return this.mCmdInfo.isInterruptible();
  }

  public String toString()
  {
    if (this.mCmdInfo.isServerSideCommand())
    {
      String str1 = this.mCmdInfo.getKeywordReferencesAsJSArrayString();
      String str2 = this.mCmdInfo.getFieldReferencesAsJSArrayString();
      return "ARACTServerProcess(" + JSWriter.escapeString(this.mActiveLinkName) + ", " + this.mActionIndex + ", " + this.mTimestamp + ", 0, " + str1 + ", " + str2 + ");";
    }
    return "ARACTProcess(" + JSWriter.escapeString(this.mProcess) + ", " + 1 + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mCmdInfo.isServerSideCommand())
    {
      Object[] arrayOfObject = { "windowID", JSWriter.escapeString(this.mActiveLinkName), "" + this.mActionIndex, "" + this.mTimestamp, "0", this.mCmdInfo.getKeywordReferencesAsJSArrayString(), this.mCmdInfo.getFieldReferencesAsJSArrayString() };
      paramEmitter.js().callFunction(false, "ARACTServerProcess", arrayOfObject);
    }
    else
    {
      paramEmitter.js().callFunction(isInterruptible(), "ARACTProcess", new Object[] { "windowID", JSWriter.escapeString(this.mProcess), "1" });
    }
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    if (isInterruptible())
    {
      paramEmitter.js().statement("var rp" + hashCode() + " = new Object()");
      paramEmitter.js().callFunction(true, "ARACTProcess", new Object[] { "windowID", JSWriter.escapeString(this.mProcess), "1", "rp" + hashCode() });
    }
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    this.mCmdInfo.addToOutputNotes(paramOutputNotes);
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    this.mCmdInfo.bindToForm(paramCachedFieldMap);
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    ActionProcess localActionProcess = (ActionProcess)super.clone();
    localActionProcess.mCmdInfo = ((RunProcessCommandInfo)this.mCmdInfo.clone());
    return localActionProcess;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionProcess
 * JD-Core Version:    0.6.1
 */