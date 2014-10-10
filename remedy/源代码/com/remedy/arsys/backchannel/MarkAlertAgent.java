package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class MarkAlertAgent extends NDXMarkAlert
{
  public MarkAlertAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
    String str1 = this.mSchema;
    for (int i = 0; i < this.mIds.length; i++)
    {
      String str2;
      if (this.mIds[i].indexOf('|') != -1)
        str2 = this.mIds[i];
      else
        str2 = this.mIds[i];
      Entry localEntry = new Entry();
      localEntry.put(Integer.valueOf(7), new Value(Long.valueOf(this.mRead ? 1L : 0L), DataType.ENUM));
      try
      {
        localServerLogin.setEntry(this.mSchema, str2, localEntry, new Timestamp(), 1);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
    }
    this.mStatus = localServerLogin.getLastStatus();
    this.mConvertIdToLabel = true;
    append("this.result=true;");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.MarkAlertAgent
 * JD-Core Version:    0.6.1
 */