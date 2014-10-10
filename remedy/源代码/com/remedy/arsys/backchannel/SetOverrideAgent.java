package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.SessionData;

public class SetOverrideAgent extends NDXSetOverride
{
  public SetOverrideAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    SessionData.get().setOverride(this.mOverride ? 1 : 0);
    append("this.result=true;");
  }

  public boolean getOverride()
  {
    return this.mOverride;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SetOverrideAgent
 * JD-Core Version:    0.6.1
 */