package com.remedy.arsys.goat.savesearches;

import java.io.Serializable;

public class ARUserSearchesKey
  implements Serializable
{
  private static final long serialVersionUID = 1438222685173892706L;
  private String mUser;
  private String mServer;
  private String mForm;

  public ARUserSearchesKey(String paramString1, String paramString2, String paramString3)
  {
    this.mUser = paramString1.toLowerCase();
    this.mServer = paramString2.toLowerCase();
    this.mForm = paramString3.toLowerCase();
  }

  public String getUser()
  {
    return this.mUser;
  }

  public String getServer()
  {
    return this.mServer;
  }

  public String getForm()
  {
    return this.mForm;
  }

  public String getCacheKey()
  {
    return (this.mServer + "/" + this.mForm + "/" + this.mUser).intern();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.savesearches.ARUserSearchesKey
 * JD-Core Version:    0.6.1
 */