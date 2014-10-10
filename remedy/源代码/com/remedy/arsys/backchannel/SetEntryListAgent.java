package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.ForcedLoginException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SetEntryListAgent extends NDXSetEntryList
{
  private static final List<EntryListFieldInfo> MInfos = Arrays.asList(new EntryListFieldInfo[] { new EntryListFieldInfo(1) });
  private ServerLogin mServerUser;
  private String mForm;
  private static final int PASSWORD_FID = 123;

  SetEntryListAgent(String paramString)
  {
    super(paramString);
  }

  private void createNewEntry(Entry paramEntry)
    throws GoatException
  {
    String str1 = null;
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(34);
    try
    {
      str1 = this.mServerUser.createEntry(this.mForm, paramEntry);
    }
    catch (ARException localARException1)
    {
      throw new GoatException(localARException1, this.mSchema.length() == 0);
    }
    finally
    {
      localMeasurement.end();
    }
    List localList = this.mServerUser.getLastStatus();
    String str2 = null;
    Object localObject4;
    if (this.mReqIdfield != 0)
    {
      localObject3 = (Value)paramEntry.get(Integer.valueOf(this.mReqIdfield));
      if (localObject3 == null)
        try
        {
          int[] arrayOfInt = new int[1];
          arrayOfInt[0] = this.mReqIdfield;
          localObject4 = this.mServerUser.getEntry(this.mForm, str1, arrayOfInt);
          localObject3 = (Value)((Entry)localObject4).get(Integer.valueOf(this.mReqIdfield));
          if (localObject3 != null)
            str2 = ((Value)localObject3).toString();
        }
        catch (ARException localARException2)
        {
        }
      else
        str2 = ((Value)localObject3).toString();
      if (str2 == null)
      {
        localObject3 = (Value)paramEntry.get(Integer.valueOf(CoreFieldId.EntryId.getFieldId()));
        if (localObject3 != null)
          str2 = ((Value)localObject3).toString();
        else
          str2 = "";
      }
    }
    append("this.result=").openObj().property("n", 1).property("id", paramEntry.getEntryId());
    if (str2 != null)
      property("reqStr", str2);
    if ((this.mReload) && (!str1.equals("")))
    {
      append(",e:");
      loadSingleEntry(this.mServerUser, this.mForm, str1, null, true);
    }
    closeObj().append(";");
    this.mStatus = localList;
    this.mConvertIdToLabel = (this.mSchema.length() == 0);
    Object localObject3 = SessionData.get();
    Value localValue;
    if (Login.isUserPasswordChangeForm(this.mForm))
    {
      localValue = (Value)paramEntry.get(Integer.valueOf(123));
      localObject4 = "";
      if (localValue != null)
        localObject4 = (String)localValue.getValue();
      if ((((SessionData)localObject3).getPasswordChangeTargetURL() != null) && (!Login.getForcedChangePasswordFlag(this.mServerUser.getUser())))
        throw new ForcedLoginException(this.mServerUser.getUser(), (String)localObject4, this.mStatus);
      ((SessionData)localObject3).changePassword((String)localObject4);
    }
    else if (Login.isUserPasswordChangeForm(this.mServer, this.mForm))
    {
      localValue = (Value)paramEntry.get(Integer.valueOf(123));
      if (localValue != null)
      {
        localObject4 = (String)localValue.getValue();
        ((SessionData)localObject3).changePassword((String)localObject4);
      }
    }
  }

  private void pushEntries(Entry[] paramArrayOfEntry, Entry paramEntry)
    throws GoatException
  {
    CachedFieldMap localCachedFieldMap = Form.get(this.mServerUser.getServer(), this.mForm).getCachedFieldMap(true);
    for (Entry localEntry1 : paramArrayOfEntry)
    {
      String str = localEntry1.getEntryId();
      Entry localEntry2 = new Entry();
      if (str.indexOf('|') != -1)
      {
        if (this.mLikeId)
        {
          localObject1 = paramEntry.entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject1).next();
            Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(((Integer)localEntry.getKey()).intValue()));
            if ((localField != null) && (localField.getFieldOption() != 3))
              localEntry2.put((Integer)localEntry.getKey(), (Value)localEntry.getValue());
          }
        }
        if (localEntry2.size() > 0)
          paramEntry = localEntry2;
      }
      Object localObject1 = new MeasureTime.Measurement(33);
      try
      {
        this.mServerUser.setEntry(this.mForm, localEntry1.getEntryId(), paramEntry, new Timestamp(), 1);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException, this.mSchema.length() == 0);
      }
      finally
      {
        ((MeasureTime.Measurement)localObject1).end();
      }
      this.mStatus = this.mServerUser.getLastStatus();
      this.mConvertIdToLabel = (this.mSchema.length() == 0);
    }
    append("this.result=").append(paramArrayOfEntry.length).append(";");
  }

  protected void process()
    throws GoatException, ARException
  {
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    this.mForm = (this.mSchema.length() == 0 ? this.mCurrentSchema : this.mSchema);
    if ((this.mFields.length != this.mFieldValues.length) || (this.mFields.length != this.mFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    Entry localEntry1 = buildEntryItems(this.mServerUser, this.mForm, this.mFields, this.mFieldValues, this.mFieldTypes, true);
    Entry localEntry2 = new Entry();
    Object localObject1 = localEntry1.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      Value localValue = (Value)((Map.Entry)localObject2).getValue();
      if ((!localValue.getDataType().equals(DataType.KEYWORD)) || (!localValue.getValue().toString().equals("$DEFAULT$")))
        localEntry2.put((Integer)((Map.Entry)localObject2).getKey(), (Value)((Map.Entry)localObject2).getValue());
    }
    if ((this.mQualification.length() == 0) && (this.mNoMatchOpt == 4) && (this.mMultiMatchOpt == 6))
    {
      createNewEntry(localEntry2);
      return;
    }
    if ((this.mQualFieldIds.length != this.mQualFieldValues.length) || (this.mQualFieldIds.length != this.mQualFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    localObject1 = buildEntryItems(this.mServer, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
    Object localObject2 = getResultList(this.mServerUser, this.mForm, MInfos, null, this.mQualification, (Entry)localObject1, this.mMultiMatchOpt, null);
    if ((localObject2 == null) || (localObject2.length == 0));
    switch (this.mNoMatchOpt)
    {
    case 1:
    case 3:
      append("this.result=0;");
      break;
    case 4:
      createNewEntry(localEntry2);
      break;
    case 2:
    default:
      throw new GoatException("Badly formatted backchannel request (nomatch)");
      switch (this.mMultiMatchOpt)
      {
      case 3:
      case 7:
        pushEntries(new Entry[] { localObject2[0] }, localEntry1);
        break;
      case 1:
      case 6:
        append("this.result=").append(localObject2.length).append(";");
        break;
      case 5:
        pushEntries((Entry[])localObject2, localEntry1);
        break;
      case 2:
      case 4:
      default:
        throw new GoatException("Badly formatted backchannel request (multimatch)");
      }
      break;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SetEntryListAgent
 * JD-Core Version:    0.6.1
 */