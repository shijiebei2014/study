package com.remedy.arsys.goat.preferences;

import com.remedy.arsys.goat.GoatException;
import java.io.Serializable;

public abstract class Preferences
  implements Serializable
{
  public abstract String get(PreferencesKey paramPreferencesKey);

  public abstract void put(PreferencesKey paramPreferencesKey, String paramString);

  public abstract void flush()
    throws GoatException;

  public abstract void sync()
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.preferences.Preferences
 * JD-Core Version:    0.6.1
 */