package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXGetURLForForm extends GetURLBase
{
  protected boolean mGetParts;
  protected String mServer;
  protected String mAppName;
  protected String mForm;
  protected String mView;
  protected boolean mFetchDimensions;
  protected String mWinName;
  protected String mWinArg;
  protected boolean mIsinline;

  NDXGetURLForForm(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetURLForForm");
    if (paramArrayList.size() != 9)
      throw new GoatException("Wrong argument length, spoofed");
    this.mGetParts = argToBoolean((String)paramArrayList.get(0));
    MLog.fine("mGetParts=" + this.mGetParts);
    this.mServer = ((String)paramArrayList.get(1));
    MLog.fine("mServer=" + this.mServer);
    this.mAppName = ((String)paramArrayList.get(2));
    MLog.fine("mAppName=" + this.mAppName);
    this.mForm = ((String)paramArrayList.get(3));
    MLog.fine("mForm=" + this.mForm);
    this.mView = ((String)paramArrayList.get(4));
    MLog.fine("mView=" + this.mView);
    this.mFetchDimensions = argToBoolean((String)paramArrayList.get(5));
    MLog.fine("mFetchDimensions=" + this.mFetchDimensions);
    this.mWinName = ((String)paramArrayList.get(6));
    MLog.fine("mWinName=" + this.mWinName);
    this.mWinArg = ((String)paramArrayList.get(7));
    MLog.fine("mWinArg=" + this.mWinArg);
    this.mIsinline = argToBoolean((String)paramArrayList.get(8));
    MLog.fine("mIsinline=" + this.mIsinline);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXGetURLForForm
 * JD-Core Version:    0.6.1
 */