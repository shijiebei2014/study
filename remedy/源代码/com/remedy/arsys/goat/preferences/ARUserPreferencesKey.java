package com.remedy.arsys.goat.preferences;

public class ARUserPreferencesKey
  implements PreferencesKey
{
  private static final long serialVersionUID = 4533580729226055081L;
  private int fieldID;
  private String prefName;
  private int fieldIDObj;
  private ARUserPreferencesKey root;

  public ARUserPreferencesKey(int paramInt)
  {
    this(paramInt, null);
  }

  public ARUserPreferencesKey(int paramInt, String paramString)
  {
    this.fieldID = paramInt;
    this.prefName = paramString;
    if (paramString == null)
      this.root = this;
    else
      this.root = new ARUserPreferencesKey(paramInt);
  }

  int getFieldID()
  {
    return this.fieldID;
  }

  String getPrefName()
  {
    return this.prefName;
  }

  ARUserPreferencesKey getRootKey()
  {
    return this.root;
  }

  public String toString()
  {
    return "Name " + this.prefName + " | fieldID " + this.fieldID;
  }

  public int hashCode()
  {
    return this.fieldID;
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ARUserPreferencesKey))
    {
      ARUserPreferencesKey localARUserPreferencesKey = (ARUserPreferencesKey)paramObject;
      return (this.fieldID == localARUserPreferencesKey.fieldID) && (this.prefName == null ? localARUserPreferencesKey.prefName == null : this.prefName.equals(localARUserPreferencesKey.prefName));
    }
    return false;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.preferences.ARUserPreferencesKey
 * JD-Core Version:    0.6.1
 */