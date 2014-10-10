package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.MessageAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;

public class ActionMessage extends Action
{
  private static final long serialVersionUID = -4974086181905766513L;
  public long mType;
  public long mNumber;
  private final String mName;
  public String mText;
  private final String mServer;
  private final boolean mUsePromptPane;

  public ActionMessage(ActiveLink paramActiveLink, MessageAction paramMessageAction, int paramInt)
    throws GoatException
  {
    this.mType = paramMessageAction.getMessageType();
    this.mNumber = paramMessageAction.getMessageNum();
    this.mName = paramActiveLink.getName();
    this.mText = paramMessageAction.getMessageText();
    this.mServer = paramActiveLink.getServer();
    this.mUsePromptPane = paramMessageAction.isUsePromptingPane();
    if (this.mText == null)
    {
      logAction("null text string");
      throw new GoatException("Bad MessageInfo");
    }
    if ((this.mType != 0L) && (this.mType != 1L) && (this.mType != 2L) && (this.mType != 3L) && (this.mType != 5L) && (this.mType != 7L))
    {
      logAction("bad type value " + this.mType);
      throw new GoatException("Bad MessageInfo");
    }
  }

  public boolean canTerminate()
  {
    return (this.mType == 2L) || (this.mType == 3L);
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
    return (this.mType == 5L ? "ARACTPrompt(" : "ARACTMessage(") + this.mType + ", " + this.mNumber + ", " + this.mUsePromptPane + ", " + this.mText + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), this.mType == 5L ? "ARACTPrompt" : "ARACTMessage", new Object[] { "windowID", "" + this.mType, "" + this.mNumber, "" + this.mUsePromptPane, getText() });
  }

  public void emitConditionalJS(Emitter paramEmitter, String paramString)
    throws GoatException
  {
    paramEmitter.js().callFunction(isInterruptible(), "CallIf", new Object[] { paramString, this.mType == 5L ? "ARACTPrompt" : "ARACTMessage", "windowID", "" + this.mType, "" + this.mNumber, "" + this.mUsePromptPane, getText() });
  }

  private String getText()
  {
    String str = MessageTranslation.getLocalizedActivelinkMessage(this.mServer, this.mName, this.mNumber);
    if (str == null)
      str = this.mText;
    return JSWriter.escapeString(str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionMessage
 * JD-Core Version:    0.6.1
 */