package com.remedy.arsys.session;

import java.io.Serializable;

public class UserCredentials
  implements Serializable
{
  private String mUser;
  private String mPassword;
  private String mAuthentication;
  private String mTimezone = null;

  public UserCredentials(String paramString1, String paramString2, String paramString3)
  {
    this.mUser = paramString1;
    this.mPassword = paramString2;
    this.mAuthentication = paramString3;
  }

  public String getUser()
  {
    return this.mUser;
  }

  public String getPassword()
  {
    return this.mPassword;
  }

  public String getAuthentication()
  {
    return this.mAuthentication;
  }

  public void setTimezone(String paramString)
  {
    if ((paramString != null) && (paramString.equals("")))
      paramString = null;
    this.mTimezone = paramString;
  }

  public String getTimezone()
  {
    return this.mTimezone;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.UserCredentials
 * JD-Core Version:    0.6.1
 */