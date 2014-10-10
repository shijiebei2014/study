package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.CallGuideAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.ActiveLinkCollector;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GuideALCollection;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.share.JSWriter;

public class ActionCallGuide extends Action
{
  private static final long serialVersionUID = -4143963649906565703L;
  private final String mServer;
  private final String mServerName;
  private final String mForm;
  private final String mView;
  private final String mGuideName;
  private final int mTableId;
  private final boolean mSelectedRowsOnly;
  private final boolean mVisibleRowsOnly;
  private final boolean mHiddenForm;

  public ActionCallGuide(ActiveLink paramActiveLink, CallGuideAction paramCallGuideAction, int paramInt)
    throws GoatException
  {
    this.mServer = paramCallGuideAction.getServerName().toString();
    this.mServerName = paramActiveLink.getServer();
    this.mForm = paramActiveLink.getForm();
    this.mView = paramActiveLink.getView();
    this.mGuideName = paramCallGuideAction.getGuideName().toString();
    if ((this.mServer == null) || (this.mGuideName == null))
    {
      if (this.mServer == null)
        logAction("null server name");
      if (this.mGuideName == null)
        logAction("null guide name");
      throw new GoatException("Bad CallGuideInfo");
    }
    this.mTableId = paramCallGuideAction.getGuideTableId();
    int i = paramCallGuideAction.getGuideMode();
    if (paramActiveLink.getName().matches(".*`\\$$"))
      i |= 2;
    this.mSelectedRowsOnly = ((i & 0x2) != 0);
    this.mVisibleRowsOnly = ((i & 0x4) != 0);
    this.mHiddenForm = ((i & 0x1) != 0);
  }

  public void guideMark(ActiveLinkCollector paramActiveLinkCollector, String paramString)
  {
    if ((this.mServer.equals("@")) || (this.mServer.equals("")) || (this.mServer.equalsIgnoreCase(paramString)))
    {
      GuideALCollection localGuideALCollection = paramActiveLinkCollector.getGuideALCollectionGivenName(this.mGuideName);
      if (localGuideALCollection != null)
        localGuideALCollection.guideMark(paramActiveLinkCollector, paramString);
    }
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
    return true;
  }

  private int getLoopType()
  {
    assert ((this.mSelectedRowsOnly) && (this.mVisibleRowsOnly));
    if (this.mSelectedRowsOnly)
      return 2;
    if (this.mVisibleRowsOnly)
      return 4;
    return 0;
  }

  public String toString()
  {
    return "ARACTCallGuide(windowID," + JSWriter.escapeString(this.mServer) + ", " + JSWriter.escapeString(this.mGuideName) + ", " + this.mHiddenForm + ", " + this.mTableId + ", " + String.valueOf(getLoopType()) + ")";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    Object localObject = { "windowID", JSWriter.escapeString(this.mServer), JSWriter.escapeString(this.mGuideName), "" + this.mHiddenForm };
    if (this.mTableId != 0)
    {
      Object[] arrayOfObject = new Object[localObject.length + 2];
      System.arraycopy(localObject, 0, arrayOfObject, 0, localObject.length);
      arrayOfObject[4] = Integer.valueOf(this.mTableId);
      arrayOfObject[5] = ("" + getLoopType());
      localObject = arrayOfObject;
    }
    paramEmitter.js().callFunction(true, "ARACTCallGuide", (Object[])localObject);
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    paramOutputNotes.addCalledGuide(this.mGuideName);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionCallGuide
 * JD-Core Version:    0.6.1
 */