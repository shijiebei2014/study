package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServerInfo;

public class GetServerTimestampAgent extends NDXGetServerTimestamp
{
  public GetServerTimestampAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    long l = ServerInfo.get(this.mServer, true).getServerTime();
    append("this.result=").append(l).append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetServerTimestampAgent
 * JD-Core Version:    0.6.1
 */