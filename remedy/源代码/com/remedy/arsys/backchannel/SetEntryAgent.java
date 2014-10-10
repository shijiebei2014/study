package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARSetGetEntryException;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class SetEntryAgent extends NDXSetEntry
{
  private ServerLogin mServerUser;
  private static int[] CREDENTIAL_FIDS = { 101, 102 };

  SetEntryAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException, ARException
  {
    SessionData localSessionData = SessionData.get();
    this.mServerUser = localSessionData.getServerLogin(this.mServer);
    String str1 = this.mSchema;
    if ((this.mFields.length != this.mFieldValues.length) || (this.mFields.length != this.mFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    if ((this.mEntryIds.length > 1) && (this.mTimestamp > 0L))
      throw new GoatException("Badly formatted backchannel request (entryids & timestamp)");
    Entry localEntry1 = buildEntryItems(this.mServerUser, str1, this.mFields, this.mFieldValues, this.mFieldTypes, true);
    int i = 0;
    String str2 = null;
    Object localObject1 = null;
    Entry localEntry2 = null;
    if ((this.mEntryIds.length == 1) && (this.mReload))
    {
      MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(36);
      try
      {
        str2 = this.mEntryIds[0];
        localEntry2 = this.mServerUser.setGetEntry(str1, str2, localEntry1, new Timestamp(this.mTimestamp), 1, null);
        i++;
      }
      catch (ARSetGetEntryException localARSetGetEntryException)
      {
        bool1 = handleException(localARSetGetEntryException);
        if (!bool1)
          return;
      }
      catch (ARException localARException1)
      {
        boolean bool1 = handleException(localARException1);
        if (!bool1)
          return;
      }
      finally
      {
        localMeasurement.end();
      }
      List localList1 = this.mServerUser.getMultiLastStatus();
      if (localList1 != null)
      {
        localObject1 = new ArrayList();
        Iterator localIterator = localList1.iterator();
        List localList2 = null;
        while (localIterator.hasNext())
        {
          localList2 = (List)localIterator.next();
          ((List)localObject1).addAll(localList2);
        }
      }
    }
    else
    {
      for (int j = 0; j < this.mEntryIds.length; j++)
      {
        str2 = this.mEntryIds[j];
        try
        {
          this.mServerUser.setEntry(str1, str2, localEntry1, new Timestamp(this.mTimestamp), 1);
          i++;
        }
        catch (ARException localARException2)
        {
          boolean bool2 = handleException(localARException2);
          if (!bool2)
            return;
        }
      }
      localObject1 = this.mServerUser.getLastStatus();
    }
    if (isSelfPWChange(str1, localEntry1, str2, i))
    {
      Value localValue = (Value)localEntry1.get(Integer.valueOf(CREDENTIAL_FIDS[1]));
      String str3 = "";
      if (localValue != null)
        str3 = (String)localValue.getValue();
      SessionData.get().changePassword(str3);
    }
    append("this.result=").openObj();
    property("n", i);
    if ((i == 1) && (this.mReload))
    {
      append(",e:");
      loadSingleEntry(this.mServerUser, str1, localEntry2, null, true, true);
      append(",eid:'").append(str2).append("'");
    }
    closeObj().append(";");
    this.mStatus = ((List)localObject1);
    this.mConvertIdToLabel = true;
    AttachmentData.removeAllUnused(this.mScreenName, localSessionData.getID());
  }

  private boolean isSelfPWChange(String paramString1, Entry paramEntry, String paramString2, int paramInt)
  {
    if ((paramInt == 1) && (Login.isUserForm(paramString1, this.mServerUser.getServer())))
      try
      {
        ServerLogin localServerLogin = ServerLogin.getAdmin(this.mServer);
        Entry localEntry = localServerLogin.getEntry(paramString1, paramString2, CREDENTIAL_FIDS);
        Value localValue = (Value)localEntry.get(Integer.valueOf(CREDENTIAL_FIDS[0]));
        if ((localValue != null) && (this.mServerUser.getUser().equals(localValue.getValue())))
          return true;
      }
      catch (ARException localARException)
      {
        localARException.printStackTrace();
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
    return false;
  }

  private boolean handleException(ARException paramARException)
    throws GoatException
  {
    StatusInfo[] arrayOfStatusInfo = (StatusInfo[])paramARException.getLastStatus().toArray(new StatusInfo[0]);
    boolean bool = handleStatusIno(arrayOfStatusInfo);
    if (!bool)
      return false;
    throw new GoatException(paramARException);
  }

  private boolean handleException(ARSetGetEntryException paramARSetGetEntryException)
    throws GoatException
  {
    ArrayList localArrayList = new ArrayList();
    List localList1 = paramARSetGetEntryException.getStatus1();
    List localList2 = paramARSetGetEntryException.getStatus2();
    int i = 1;
    if ((localList1 != null) && (localList1.size() > 0))
      localArrayList.addAll(localList1);
    if ((localList2 != null) && (localList2.size() > 0))
    {
      localArrayList.addAll(localList2);
      i = 0;
    }
    StatusInfo[] arrayOfStatusInfo = (StatusInfo[])localArrayList.toArray(new StatusInfo[localArrayList.size()]);
    boolean bool = handleStatusIno(arrayOfStatusInfo);
    if (!bool)
      return false;
    MLog.log(Level.SEVERE, "Exception during " + (i != 0 ? "Set" : "Get") + " operation;  ");
    paramARSetGetEntryException.setLastStatus(localArrayList);
    throw new GoatException(paramARSetGetEntryException);
  }

  private boolean handleStatusIno(StatusInfo[] paramArrayOfStatusInfo)
  {
    int i = 0;
    String str = null;
    int j = 0;
    int k = paramArrayOfStatusInfo.length;
    while (j < k)
    {
      if (paramArrayOfStatusInfo[j].getMessageNum() == 309L)
      {
        i = 1;
        break;
      }
      if (paramArrayOfStatusInfo[j].getMessageNum() == 302L)
      {
        str = paramArrayOfStatusInfo[j].getMessageText();
        break;
      }
      j++;
    }
    if (i != 0)
    {
      append("this.result={n:0};");
      return false;
    }
    if (str != null)
    {
      append("this.result={n:-1,m:\"" + str + "\"};");
      return false;
    }
    return true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SetEntryAgent
 * JD-Core Version:    0.6.1
 */