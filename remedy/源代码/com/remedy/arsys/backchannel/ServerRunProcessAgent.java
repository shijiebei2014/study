package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ProcessResult;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Timestamp;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Arrays;
import java.util.List;

public class ServerRunProcessAgent extends NDXServerRunProcess
{
  public ServerRunProcessAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
    boolean bool = this.mFid != 0;
    int i;
    if (bool)
      i = this.mFid;
    else
      i = 0;
    List localList1 = buildListItems(this.mServer, this.mFieldIds, this.mFieldVals, this.mFieldTypes);
    List localList2 = buildListItems(this.mServer, this.mKeywordIds, this.mKeywordVals, this.mKeywordTypes);
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(35);
    ProcessResult localProcessResult;
    try
    {
      localProcessResult = localServerLogin.executeProcessForActiveLink(this.mAlName, this.mActIdx, bool ? 2 : 3, i, new Timestamp(this.mTs), localList2, localList1, bool);
    }
    catch (ARException localARException1)
    {
      throw new GoatException(localARException1);
    }
    finally
    {
      localMeasurement.end();
    }
    String str1 = localProcessResult != null ? localProcessResult.getOutput() : null;
    if ((bool) && ((localProcessResult == null) || (localProcessResult.getStatus() != 0)))
    {
      if (str1 == null)
        str1 = "";
      StatusInfo[] arrayOfStatusInfo = new StatusInfo[1];
      String str2 = SessionData.get().getLocale();
      arrayOfStatusInfo[0] = new StatusInfo(2, 9281L, MessageTranslation.getLocalizedErrorMessage(str2, 9281, null), str1);
      ARException localARException2 = new ARException(Arrays.asList(arrayOfStatusInfo));
      throw new GoatException(localARException2);
    }
    if ((bool) && (str1 != null))
    {
      append("this.result=");
      appendqs(str1);
      append(";");
    }
    else
    {
      append("this.result=\"\";");
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.ServerRunProcessAgent
 * JD-Core Version:    0.6.1
 */