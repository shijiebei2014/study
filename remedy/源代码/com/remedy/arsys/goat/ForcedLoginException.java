package com.remedy.arsys.goat;

import com.bmc.arsys.api.StatusInfo;
import java.util.List;

public class ForcedLoginException extends GoatException
{
  private final String mNewPassword;
  private final String mUser;
  private final List<StatusInfo> mStatus;

  public ForcedLoginException(String paramString1, String paramString2, List<StatusInfo> paramList)
  {
    this.mNewPassword = paramString2;
    this.mUser = paramString1;
    this.mStatus = paramList;
  }

  public String getUser()
  {
    return this.mUser;
  }

  public String getNewPassword()
  {
    return this.mNewPassword;
  }

  public List<StatusInfo> getStatus()
  {
    return this.mStatus;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ForcedLoginException
 * JD-Core Version:    0.6.1
 */