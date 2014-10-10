package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class SaveSearchAgent extends NDXSaveSearch
{
  public SaveSearchAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    ARUserSearches localARUserSearches = ARUserSearches.getUserSearches(localSessionData.getUserName(), this.mSchema, this.mServer, false);
    boolean bool = false;
    if (localARUserSearches != null)
      if (!this.mChangeState)
      {
        assert (this.mFieldIds.length == this.mFieldValues.length);
        StringBuilder localStringBuilder = new StringBuilder(50);
        int i = this.mFieldIds.length;
        localStringBuilder.append(i);
        for (int j = 0; j < i; j++)
        {
          localStringBuilder.append('/');
          localStringBuilder.append(this.mFieldIds[j]).append('/');
          localStringBuilder.append(this.mFieldValues[j].length()).append('/').append(this.mFieldValues[j]);
        }
        String str = localStringBuilder.toString();
        bool = localARUserSearches.saveSearch(localSessionData, this.mName, str, this.mNewrecord, this.mDisable, this.mType);
      }
      else
      {
        bool = localARUserSearches.saveSearch(localSessionData, this.mName, "", this.mNewrecord, this.mDisable, this.mType);
      }
    append("this.result=").append(bool).append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SaveSearchAgent
 * JD-Core Version:    0.6.1
 */