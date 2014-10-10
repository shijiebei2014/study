package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;

public class AssignmentProcess extends ActionProcess
{
  private static final long serialVersionUID = -3640667022774073625L;
  private int mSetFieldID;

  public AssignmentProcess(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws GoatException
  {
    super(paramString1, paramString2, paramInt1);
    this.mSetFieldID = paramInt2;
    if (!this.mCmdInfo.isDollarProcess())
      mLog.fineAndConsole("Process string " + paramString1 + " in active link " + paramString2 + " is invalid in the context of $PROCESS$");
  }

  public String toString()
  {
    if (this.mCmdInfo.isServerSideCommand())
    {
      String str1 = this.mCmdInfo.getKeywordReferencesAsJSArrayString();
      String str2 = this.mCmdInfo.getFieldReferencesAsJSArrayString();
      return "ARACTServerProcess(" + JSWriter.escapeString(this.mActiveLinkName) + ", " + this.mActionIndex + ", " + this.mTimestamp + ", " + this.mSetFieldID + ", " + str1 + ", " + str2 + ");";
    }
    return "ARACTProcess(" + JSWriter.escapeString(this.mProcess) + ", " + 2 + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mCmdInfo.isServerSideCommand())
    {
      JSWriter localJSWriter = paramEmitter.js();
      localJSWriter.append("ARACTServerProcess(");
      localJSWriter.append("windowID").comma();
      localJSWriter.appendqs(this.mActiveLinkName).comma();
      localJSWriter.append(this.mActionIndex).comma();
      localJSWriter.append(this.mTimestamp).comma();
      localJSWriter.append(this.mSetFieldID).comma();
      this.mCmdInfo.emitKeywordReferencesAsJSArray(localJSWriter);
      localJSWriter.comma();
      this.mCmdInfo.emitFieldReferencesAsJSArray(localJSWriter);
      localJSWriter.append(")");
    }
    else if (isInterruptible())
    {
      paramEmitter.js().append("rp" + hashCode() + ".result");
    }
    else
    {
      paramEmitter.js().append("ARACTProcess(").append("windowID").comma().appendqs(this.mProcess).comma().append(2).append(")");
    }
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    if (isInterruptible())
    {
      paramEmitter.js().statement("var rp" + hashCode() + " = new Object()");
      paramEmitter.js().callFunction(true, "ARACTProcess", new Object[] { "windowID", JSWriter.escapeString(this.mProcess), "2", "rp" + hashCode() });
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.AssignmentProcess
 * JD-Core Version:    0.6.1
 */