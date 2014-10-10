package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.ColumnField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GetEntryListAgent extends NDXGetEntryList
{
  private ServerLogin mServerUser;
  private String mForm;

  GetEntryListAgent(String paramString)
  {
    super(paramString);
  }

  private void emitSingleEntryFieldValues(int paramInt, Entry paramEntry)
    throws GoatException
  {
    if (paramEntry == null)
    {
      property("n", 0);
    }
    else
    {
      property("n", paramInt).append(",f:");
      emitSingleEntry(paramEntry, this.mForm, false, this.mServerUser, this.mFields);
    }
  }

  protected void emitMultipleEntryList(Entry[] paramArrayOfEntry, ServerLogin paramServerLogin, String paramString, EntryListFieldInfo[] paramArrayOfEntryListFieldInfo)
    throws GoatException
  {
    assert (paramString != null);
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(10);
    com.remedy.arsys.goat.Form localForm = com.remedy.arsys.goat.Form.get(paramServerLogin.getServer(), paramString);
    FieldGraph localFieldGraph = FieldGraph.get(localForm.getViewInfoByInference(null, false, false));
    if ((paramArrayOfEntryListFieldInfo == null) || (paramArrayOfEntryListFieldInfo.length == 0))
    {
      paramArrayOfEntryListFieldInfo = new EntryListFieldInfo[1];
      paramArrayOfEntryListFieldInfo[0] = new EntryListFieldInfo(8, 128, " ");
    }
    property("n", paramArrayOfEntry.length).append(",l:");
    openObj().append("h:").openList();
    int i = paramArrayOfEntryListFieldInfo.length;
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++)
    {
      GoatField localGoatField1 = localFieldGraph.getField(paramArrayOfEntryListFieldInfo[j].getFieldId());
      String str1 = null;
      if (localGoatField1 != null)
      {
        if (localGoatField1.isDataField())
          str1 = localGoatField1.getLabel();
        if (str1 == null)
          str1 = localGoatField1.getMDBName();
      }
      else
      {
        str1 = "???";
      }
      listSep().appendqs(str1);
      arrayOfInt[j] = paramArrayOfEntryListFieldInfo[j].getColumnWidth();
    }
    closeList().append(",w:").openList();
    int k;
    for (j = 0; j < i; j++)
    {
      k = (int)(arrayOfInt[j] * ColumnField.getMPixelsPerColumnChar() / ARBox.MXFactor);
      k += ColumnField.getMPixelsPerColumnChar();
      listSep().append(new ARBox(0L, 0L, k, 0L).toBox().mW);
    }
    closeList().append(",r:").openList();
    for (Entry localEntry : paramArrayOfEntry)
    {
      listSep();
      openObj().property("i", localEntry.getEntryId()).append(",d:");
      openList();
      int n = 0;
      for (int i1 = 0; i1 < i; i1++)
      {
        int i2 = paramArrayOfEntryListFieldInfo[i1].getFieldId();
        FieldGraph.Node localNode = localFieldGraph.getNode(i2);
        GoatField localGoatField2 = localFieldGraph.getField(i2);
        Value localValue = (Value)localEntry.get(Integer.valueOf(i2));
        String str2 = "";
        if ((localGoatField2 != null) && (localValue != null))
        {
          GoatType localGoatType = GoatTypeFactory.create(localValue, i2, localEntry.getEntryId(), paramServerLogin, localNode);
          str2 = localGoatType.forHTML();
          if ((n == 0) && (str2.length() > 0))
            n = 1;
        }
        if ((n == 0) && (i1 == i - 1))
          str2 = "&nbsp;";
        listSep().openObj().property("v", str2).closeObj();
      }
      closeList().closeObj();
    }
    closeList().closeObj();
    localMeasurement.end();
  }

  private int[] getMatchingIds(String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    CachedFieldMap localCachedFieldMap1 = com.remedy.arsys.goat.Form.get(paramString1, paramString2).getCachedFieldMap(true);
    CachedFieldMap localCachedFieldMap2 = com.remedy.arsys.goat.Form.get(paramString3, paramString4).getCachedFieldMap();
    int[] arrayOfInt = new int[localCachedFieldMap1.size()];
    Iterator localIterator = localCachedFieldMap1.values().iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      localObject = (Field)localIterator.next();
      int j = ((Field)localObject).getFieldID();
      Field localField = (Field)localCachedFieldMap2.get(Integer.valueOf(j));
      if (((((Field)localObject).getFieldType() & 0x81) != 0) && (j != 15) && ((j < 1000000) || (j > 1999999)) && ((j < 3000000) || (j > 3999999)) && (localField != null) && ((localField.getFieldType() & 0x81) != 0))
        arrayOfInt[(i++)] = j;
    }
    Object localObject = new int[i];
    System.arraycopy(arrayOfInt, 0, localObject, 0, i);
    return localObject;
  }

  private List<EntryListFieldInfo> getSingleEntryCriteria()
  {
    int i = this.mFields.length;
    EntryListFieldInfo[] arrayOfEntryListFieldInfo = new EntryListFieldInfo[i];
    for (int j = 0; j < i; j++)
      arrayOfEntryListFieldInfo[j] = new EntryListFieldInfo(this.mFields[j]);
    return Arrays.asList(arrayOfEntryListFieldInfo);
  }

  private static List<EntryListFieldInfo> getPickListCriteria(String paramString1, String paramString2)
    throws GoatException
  {
    com.remedy.arsys.goat.Form localForm = com.remedy.arsys.goat.Form.get(paramString1, paramString2);
    EntryListFieldInfo[] arrayOfEntryListFieldInfo1 = (EntryListFieldInfo[])localForm.getSchema().getEntryListFieldInfo().toArray(new EntryListFieldInfo[0]);
    if (arrayOfEntryListFieldInfo1 == null)
      return null;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    for (EntryListFieldInfo localEntryListFieldInfo : arrayOfEntryListFieldInfo1)
    {
      i += localEntryListFieldInfo.getColumnWidth();
      if (i > 256)
        break;
      localArrayList.add(localEntryListFieldInfo);
    }
    return localArrayList;
  }

  protected void process()
    throws GoatException
  {
    Qualifier localQualifier = null;
    if (this.mCompile)
    {
      localObject1 = CompileExternalQualificationAgent.splitFields(this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes, this.mQualFieldExterns);
      int[] arrayOfInt1 = (int[])localObject1[0][0];
      localObject2 = (String[])localObject1[0][1];
      localObject3 = (int[])localObject1[0][2];
      this.mQualFieldIds = ((int[])localObject1[1][0]);
      this.mQualFieldValues = ((String[])localObject1[1][1]);
      this.mQualFieldTypes = ((int[])localObject1[1][2]);
      localQualifier = CompileExternalQualificationAgent.compileExternalQualification(this.mCurrentServer, this.mCurrentSchema, this.mCurrentVui, this.mServer, this.mSchema, this.mQualification, arrayOfInt1, (String[])localObject2, (int[])localObject3);
    }
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    this.mForm = this.mSchema;
    if (this.mAppName.length() == 0)
      this.mAppName = null;
    if ((this.mQualFieldIds.length != this.mQualFieldValues.length) || (this.mQualFieldIds.length != this.mQualFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    Object localObject1 = buildEntryItems(this.mServer, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
    int i = 0;
    if ((this.mFields.length == 1) && (this.mFields[0] == 98))
    {
      this.mFields = getMatchingIds(this.mServer, this.mSchema, this.mCurrentServer, this.mCurrentSchema);
      i = 1;
    }
    Object localObject2 = new OutputInteger(0);
    Object localObject3 = this.mMultiMatchOpt == 4 ? getPickListCriteria(this.mServer, this.mForm) : getSingleEntryCriteria();
    Entry[] arrayOfEntry = null;
    ServerInfo localServerInfo = ServerInfo.get(this.mServer, true);
    int k;
    if (((this.mMultiMatchOpt == 3) || (this.mMultiMatchOpt == 7)) && (localServerInfo.getVersionAsNumber() >= 70500))
    {
      localObject4 = getQualifierInfo(this.mServerUser, localQualifier, this.mQualification, (Entry)localObject1);
      if (localObject3 == null)
        throw new GoatException("EntryListFieldInfo is null.");
      int[] arrayOfInt2 = new int[((List)localObject3).size()];
      k = 0;
      Iterator localIterator = ((List)localObject3).iterator();
      while (localIterator.hasNext())
      {
        EntryListFieldInfo localEntryListFieldInfo = (EntryListFieldInfo)localIterator.next();
        arrayOfInt2[(k++)] = localEntryListFieldInfo.getFieldId();
      }
      boolean bool = this.mMultiMatchOpt == 7;
      try
      {
        arrayOfEntry = new Entry[1];
        arrayOfEntry[0] = this.mServerUser.getOneEntryObject(this.mSchema, (QualifierInfo)localObject4, new ArrayList(), arrayOfInt2, bool, (OutputInteger)localObject2);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
    }
    else
    {
      arrayOfEntry = getResultList(this.mServerUser, this.mForm, (List)localObject3, localQualifier, this.mQualification, (Entry)localObject1, this.mMultiMatchOpt, (OutputInteger)localObject2);
    }
    append("this.result=").openObj();
    if (i != 0)
    {
      append("mids:").openList();
      for (int m : this.mFields)
        listSep().append(m);
      closeList();
    }
    Object localObject4 = new ArrayList();
    if (((OutputInteger)localObject2).intValue() == 0)
    {
      switch (this.mNoMatchOpt)
      {
      case 1:
      case 2:
        property("n", 0);
        break;
      default:
        throw new GoatException("Badly formatted backchannel request (Illegal nomatch option [" + this.mNoMatchOpt + "])");
      }
    }
    else
    {
      Entry localEntry;
      StatusInfo localStatusInfo;
      if (((OutputInteger)localObject2).intValue() == 1)
      {
        localEntry = arrayOfEntry[0];
        if ((this.mMultiMatchOpt == 4) || (localServerInfo.getVersionAsNumber() < 70500))
          localEntry = lookupEntry(this.mServerUser, this.mForm, arrayOfEntry[0].getEntryId(), this.mFields);
        localObject4 = this.mServerUser.getLastStatus();
        if (i != 0)
          for (k = ((List)localObject4).size() - 1; k >= 0; k--)
          {
            localStatusInfo = (StatusInfo)((List)localObject4).get(k);
            if (localStatusInfo.getMessageNum() == 61L)
              ((List)localObject4).remove(k);
          }
        emitSingleEntryFieldValues(((OutputInteger)localObject2).intValue(), localEntry);
      }
      else
      {
        switch (this.mMultiMatchOpt)
        {
        case 3:
        case 7:
          localEntry = arrayOfEntry[0];
          if (localServerInfo.getVersionAsNumber() < 70500)
            localEntry = lookupEntry(this.mServerUser, this.mForm, arrayOfEntry[0].getEntryId(), this.mFields);
          emitSingleEntryFieldValues(((OutputInteger)localObject2).intValue(), localEntry);
          localObject4 = this.mServerUser.getLastStatus();
          for (k = ((List)localObject4).size() - 1; k >= 0; k--)
          {
            localStatusInfo = (StatusInfo)((List)localObject4).get(k);
            if (localStatusInfo.getMessageNum() == 66L)
              ((List)localObject4).remove(k);
            else if ((i != 0) && (localStatusInfo.getMessageNum() == 61L))
              ((List)localObject4).remove(k);
          }
          break;
        case 4:
          emitMultipleEntryList(arrayOfEntry, this.mServerUser, this.mForm, (EntryListFieldInfo[])((List)localObject3).toArray(new EntryListFieldInfo[((List)localObject3).size()]));
          break;
        case 1:
        case 2:
          property("n", (Number)localObject2);
          break;
        case 5:
        case 6:
        default:
          throw new GoatException("Badly formatted backchannel request (Illegal multimatch option [" + this.mMultiMatchOpt + "])");
        }
      }
    }
    closeObj().append(";");
    this.mStatus = ((List)localObject4);
    this.mConvertIdToLabel = this.mSchema.equals(this.mCurrentSchema);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetEntryListAgent
 * JD-Core Version:    0.6.1
 */