package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.DiaryListValue;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.EntryListInfo;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldCriteria;
import com.bmc.arsys.api.ObjectBaseCriteria;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.CurrencyRatios;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.DecimalField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class EntryListBase extends NDXRequest
{
  private static final int MLargeDiaryThreshold = 20000;
  private static final FieldCriteria FIELD_CRITERIA_NAME_ONLY;

  EntryListBase()
  {
  }

  EntryListBase(String paramString)
  {
    super(paramString);
  }

  EntryListBase(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  protected static List<Value> buildListItems(String paramString, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2)
    throws GoatException
  {
    int i = paramArrayOfInt1.length;
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      GoatType localGoatType = GoatTypeFactory.create(paramArrayOfString[j], paramArrayOfInt2[j], paramArrayOfInt1[j]);
      localArrayList.add(j, localGoatType.toValue());
    }
    return localArrayList;
  }

  protected static Entry buildEntryItems(String paramString, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2)
    throws GoatException
  {
    int i = paramArrayOfInt1.length;
    Entry localEntry = new Entry();
    for (int j = 0; j < i; j++)
    {
      GoatType localGoatType = GoatTypeFactory.create(paramArrayOfString[j], paramArrayOfInt2[j], paramArrayOfInt1[j]);
      localEntry.put(Integer.valueOf(paramArrayOfInt1[j]), localGoatType.toValue());
    }
    return localEntry;
  }

  protected static Entry buildEntryItems(int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, boolean paramBoolean)
    throws GoatException
  {
    int i = paramArrayOfInt1.length;
    Entry localEntry = new Entry();
    for (int j = 0; j < i; j++)
      if ((paramArrayOfString[j] != null) && (paramArrayOfString[j].length() > 0))
      {
        GoatType localGoatType = GoatTypeFactory.create(paramArrayOfString[j], paramArrayOfInt2[j], paramArrayOfInt1[j]);
        localEntry.put(Integer.valueOf(paramArrayOfInt1[j]), localGoatType.toValue());
      }
    return localEntry;
  }

  protected static Entry buildEntryItems(ServerLogin paramServerLogin, String paramString, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, boolean paramBoolean)
    throws GoatException
  {
    assert ((paramServerLogin != null) && (paramString != null) && (paramArrayOfInt1 != null));
    CachedFieldMap localCachedFieldMap = Form.get(paramServerLogin.getServer(), paramString).getCachedFieldMap(true);
    Entry localEntry = new Entry();
    for (int i = 0; i < paramArrayOfInt1.length; i++)
    {
      int j = paramArrayOfInt1[i];
      Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(j));
      GoatType localGoatType = GoatTypeFactory.create(paramArrayOfString[i], paramArrayOfInt2[i], j);
      if ((paramBoolean) && (localField != null))
      {
        int k = localField.getDataType();
        if ((k == 34) || (k == 43) || (k == 33) || (k == 31))
          continue;
        localGoatType = localGoatType.bind(paramServerLogin, localField);
      }
      else
      {
        if (localField == null)
          continue;
      }
      localEntry.put(Integer.valueOf(j), localGoatType.toValue());
    }
    return localEntry;
  }

  protected static QualifierInfo getQualifierInfo(ServerLogin paramServerLogin, Qualifier paramQualifier, String paramString, Entry paramEntry)
    throws GoatException
  {
    int i = (paramQualifier != null) && (paramQualifier.getARQualifierInfo() != null) ? 1 : 0;
    int j = paramString.length() > 0 ? 1 : 0;
    int k = (i != 0) || (j != 0) ? 1 : 0;
    QualifierInfo localQualifierInfo = null;
    if (i != 0)
      localQualifierInfo = paramQualifier.getARQualifierInfo();
    else if (j != 0)
      localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(paramServerLogin, paramString);
    if (k != 0)
    {
      ARQualifier localARQualifier = new ARQualifier(localQualifierInfo, paramEntry);
      localQualifierInfo = localARQualifier.getQualInfo();
    }
    return localQualifierInfo;
  }

  protected static Entry[] getResultList(ServerLogin paramServerLogin, String paramString1, List<EntryListFieldInfo> paramList, Qualifier paramQualifier, String paramString2, Entry paramEntry, int paramInt, OutputInteger paramOutputInteger)
    throws GoatException
  {
    if (paramList == null)
      throw new GoatException("EntryListFieldInfo is null.");
    QualifierInfo localQualifierInfo = getQualifierInfo(paramServerLogin, paramQualifier, paramString2, paramEntry);
    boolean bool = paramInt == 7;
    int i = 0;
    if ((paramInt == 3) || (paramInt == 7))
      i = 1;
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(7);
    Object localObject1 = null;
    int[] arrayOfInt = new int[paramList.size()];
    int j = 0;
    Iterator localIterator1 = paramList.iterator();
    Object localObject2;
    while (localIterator1.hasNext())
    {
      localObject2 = (EntryListFieldInfo)localIterator1.next();
      arrayOfInt[(j++)] = ((EntryListFieldInfo)localObject2).getFieldId();
    }
    try
    {
      localObject1 = paramServerLogin.getListEntryObjects(paramString1, localQualifierInfo, 0, i, new ArrayList(), arrayOfInt, bool, paramOutputInteger);
    }
    catch (ARException localARException1)
    {
      localObject2 = localARException1.getLastStatus();
      int k = 1;
      if (((List)localObject2).size() > 0)
      {
        Iterator localIterator2 = ((List)localObject2).iterator();
        while (localIterator2.hasNext())
        {
          StatusInfo localStatusInfo = (StatusInfo)localIterator2.next();
          if (localStatusInfo.getMessageNum() == 241L)
          {
            List localList1 = Arrays.asList(new EntryListFieldInfo[] { new EntryListFieldInfo(1) });
            try
            {
              List localList2 = paramServerLogin.getListEntry(paramString1, localQualifierInfo, 0, i, new ArrayList(), localList1, bool, paramOutputInteger);
              if ((localList2.size() > 0) && (paramInt != 1) && (paramInt != 2))
              {
                ArrayList localArrayList = new ArrayList();
                Iterator localIterator3 = localList2.iterator();
                while (localIterator3.hasNext())
                {
                  EntryListInfo localEntryListInfo = (EntryListInfo)localIterator3.next();
                  localArrayList.add(localEntryListInfo.getEntryID());
                }
                localObject1 = paramServerLogin.getListEntryObjects(paramString1, localArrayList, arrayOfInt);
              }
              else
              {
                localObject1 = new ArrayList();
              }
              k = 0;
            }
            catch (ARException localARException2)
            {
              throw new GoatException(localARException2);
            }
          }
        }
      }
      if (k != 0)
        throw new GoatException(localARException1);
    }
    finally
    {
      localMeasurement.end();
    }
    return (Entry[])((List)localObject1).toArray(new Entry[0]);
  }

  protected void getFieldValue(int paramInt, Value paramValue, boolean paramBoolean, ServerLogin paramServerLogin, String paramString, FieldGraph.Node paramNode)
    throws GoatException
  {
    GoatType localGoatType = GoatTypeFactory.create(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    openObj();
    if (paramBoolean)
      property("i", paramInt);
    localGoatType.emitPrimitive(this);
    if ((localGoatType.getDataType() == 10) && (paramNode != null))
    {
      DecimalField localDecimalField = (DecimalField)paramNode.mField;
      int i = localDecimalField.getPrecision();
      JSWriter localJSWriter = new JSWriter();
      localJSWriter.openObj();
      localJSWriter.property("p", i);
      localJSWriter.closeObj();
      property("a", localJSWriter);
    }
    closeObj();
  }

  protected static Entry lookupEntry(ServerLogin paramServerLogin, String paramString1, String paramString2, int[] paramArrayOfInt)
    throws GoatException
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(16);
    Entry localEntry;
    try
    {
      localEntry = paramServerLogin.getEntry(paramString1, paramString2, paramArrayOfInt);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
    CurrencyRatios.ReconvertValues(paramServerLogin.getServer(), localEntry);
    return localEntry;
  }

  protected void emitSingleEntry(Entry paramEntry, String paramString, boolean paramBoolean, ServerLogin paramServerLogin, int[] paramArrayOfInt)
    throws GoatException
  {
    assert (paramEntry != null);
    assert (paramServerLogin != null);
    openList();
    FieldGraph localFieldGraph = FieldGraph.get(Form.get(paramServerLogin.getServer(), paramString).getViewInfoByInference(null, false, false));
    if (paramEntry.size() > 0)
    {
      int i = 0;
      Object localObject;
      FieldGraph.Node localNode;
      if (paramArrayOfInt == null)
      {
        Iterator localIterator = paramEntry.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localObject = (Map.Entry)localIterator.next();
          if ((((Integer)((Map.Entry)localObject).getKey()).intValue() != 0) && (((Map.Entry)localObject).getValue() != null))
          {
            i = 1;
            listSep();
            localNode = localFieldGraph.getNode(((Integer)((Map.Entry)localObject).getKey()).intValue());
            getFieldValue(((Integer)((Map.Entry)localObject).getKey()).intValue(), (Value)((Map.Entry)localObject).getValue(), paramBoolean, paramServerLogin, paramEntry.getEntryId(), localNode);
          }
        }
      }
      else
      {
        for (int j = 0; j < paramArrayOfInt.length; j++)
        {
          localObject = (Value)paramEntry.get(Integer.valueOf(paramArrayOfInt[j]));
          if (localObject == null)
          {
            listSep();
            openObj();
            this.mBuffer.append("t:0");
            closeObj();
            i = 1;
          }
          else
          {
            i = 1;
            listSep();
            localNode = localFieldGraph.getNode(paramArrayOfInt[j]);
            getFieldValue(paramArrayOfInt[j], (Value)localObject, paramBoolean, paramServerLogin, paramEntry.getEntryId(), localNode);
          }
        }
      }
      if (i != 0)
      {
        listSep();
        openObj().property("ts", paramServerLogin.getOperationTime().getValue()).closeObj();
      }
    }
    closeList();
  }

  protected void loadSingleEntry(ServerLogin paramServerLogin, String paramString1, String paramString2, int[] paramArrayOfInt, boolean paramBoolean)
    throws GoatException
  {
    Entry localEntry = lookupEntry(paramServerLogin, paramString1, paramString2, paramArrayOfInt);
    loadSingleEntry(paramServerLogin, paramString1, localEntry, paramArrayOfInt, paramBoolean, false);
  }

  protected void loadSingleEntry(ServerLogin paramServerLogin, String paramString, Entry paramEntry, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException
  {
    if (paramEntry == null)
    {
      append(0);
    }
    else
    {
      if (paramBoolean2)
        CurrencyRatios.ReconvertValues(paramServerLogin.getServer(), paramEntry);
      if (paramBoolean1)
      {
        Iterator localIterator = paramEntry.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (((Value)localEntry.getValue()).getDataType().toInt() == 5)
          {
            Value localValue = (Value)localEntry.getValue();
            String str;
            if ((localValue.getValue() instanceof DiaryListValue))
              str = ((DiaryListValue)localValue.getValue()).toString();
            else
              str = (String)localValue.getValue();
            if ((str != null) && (str.length() > 20000))
            {
              MLog.fine("Deferring large diary data for field " + localEntry.getKey());
              localIterator.remove();
            }
          }
        }
      }
      emitSingleEntry(paramEntry, paramString, true, paramServerLogin, paramArrayOfInt);
    }
  }

  static
  {
    FIELD_CRITERIA_NAME_ONLY = new FieldCriteria();
    FIELD_CRITERIA_NAME_ONLY.setPropertiesToRetrieve(ObjectBaseCriteria.NAME);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.EntryListBase
 * JD-Core Version:    0.6.1
 */