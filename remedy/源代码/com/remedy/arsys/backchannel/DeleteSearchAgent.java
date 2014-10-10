package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.stubs.SessionData;

public class DeleteSearchAgent extends NDXDeleteSearch
{
  public DeleteSearchAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    ARUserSearches localARUserSearches = ARUserSearches.getUserSearches(SessionData.get().getUserName(), this.mSchema, this.mServer, false);
    if (localARUserSearches != null)
      localARUserSearches.deleteSearch(this.mNames);
    append("this.result=true;");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.DeleteSearchAgent
 * JD-Core Version:    0.6.1
 */