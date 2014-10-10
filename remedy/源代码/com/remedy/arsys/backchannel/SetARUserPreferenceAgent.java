package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.goat.preferences.ARUserPreferencesKey;
import com.remedy.arsys.stubs.SessionData;

public class SetARUserPreferenceAgent extends NDXSetARUserPreference
{
  public SetARUserPreferenceAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    ARUserPreferences localARUserPreferences = SessionData.get().getPreferences();
    ARUserPreferencesKey localARUserPreferencesKey;
    if (this.mPrefKey.length() > 0)
      localARUserPreferencesKey = new ARUserPreferencesKey(this.mFieldID, this.mPrefKey);
    else
      localARUserPreferencesKey = new ARUserPreferencesKey(this.mFieldID);
    localARUserPreferences.put(localARUserPreferencesKey, this.mValue);
    localARUserPreferences.emitJSForPreference(this, localARUserPreferencesKey);
    append("this.result=true;");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SetARUserPreferenceAgent
 * JD-Core Version:    0.6.1
 */