package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class DeleteEntryAgent extends NDXDeleteEntry
{
  ServerLogin mServerUser;

  public DeleteEntryAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    String str = null;
    for (int i = 0; i < this.mEntries.length; i++)
    {
      str = this.mEntries[i];
      if (!str.equals(""))
      {
        try
        {
          this.mServerUser.deleteEntry(this.mForm, str, 0);
        }
        catch (ARException localARException)
        {
          throw new GoatException(localARException);
        }
        if (this.mServerUser.getLastStatus() != null)
        {
          this.mStatus = this.mServerUser.getLastStatus();
          this.mConvertIdToLabel = true;
        }
      }
    }
    append("this.result=true;");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.DeleteEntryAgent
 * JD-Core Version:    0.6.1
 */