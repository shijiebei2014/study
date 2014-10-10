package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Entry;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.List;

public class GetEntryAgent extends NDXGetEntry
{
  private static Log MLog = Log.get(10);
  private ServerLogin mServerUser;

  GetEntryAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    this.mServerUser = localSessionData.getServerLogin(this.mServer);
    String str1 = this.mSchema;
    if (this.mAppName.length() == 0)
      this.mAppName = null;
    String str2 = this.mEntryId;
    append("this.result=");
    Object localObject = new ArrayList();
    if (this.mFields.length == 0)
    {
      loadSingleEntry(this.mServerUser, str1, str2, null, this.mDeferLargeDiary);
      localObject = this.mServerUser.getLastStatus();
      AttachmentData.removeAllUnused(this.mScreenName, localSessionData.getID());
    }
    else
    {
      Entry localEntry = lookupEntry(this.mServerUser, str1, str2, this.mFields);
      localObject = this.mServerUser.getLastStatus();
      if (localEntry == null)
        append(0);
      else
        emitSingleEntry(localEntry, str1, false, this.mServerUser, this.mFields);
    }
    append(";");
    this.mStatus = ((List)localObject);
    this.mConvertIdToLabel = true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetEntryAgent
 * JD-Core Version:    0.6.1
 */