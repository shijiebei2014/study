package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FormAliasInfo;
import com.bmc.arsys.api.FormType;
import com.bmc.arsys.api.IARRowIterator;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.EntryListCriteria;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatApplicationContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.ColumnField;
import com.remedy.arsys.goat.field.CurrencyField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.TableField;
import com.remedy.arsys.goat.field.TimeField;
import com.remedy.arsys.goat.field.type.CurrencyType;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.goat.field.type.NullType;
import com.remedy.arsys.goat.field.type.TimeType;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.share.MiscCache;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TableEntryListBase extends EntryListBase
{
  public static final int OBJECT_LIST = 1040;
  private static final int OBJECT_LIST_FILTER_SERVER = 1041;
  private static final int OBJECT_LIST_FILTER_APPLICATION = 1042;
  private static final int OBJECT_LIST_FILTER_SEARCH = 1043;
  private static final int OBJECT_LIST_FILTER_SCHEMA = 1044;
  private static final int OBJECT_LIST_SHOW_HIDDEN = 1045;
  private static final long OBJECT_LIST_TYPE_FORM = 0L;
  private static final long OBJECT_LIST_TYPE_APPLICATION = 1L;
  private static final int RETRIEVE_SCHEMAS = 1;
  private static final int RETRIEVE_APPLICATIONS = 2;
  private IEmitterFactory emitterFactory;
  private int maxLimit = 0;
  private BufferedOutputStream bOutputStream;
  protected ServerLogin mServerUser;
  protected ServerLogin mTableServerUser;
  private static Log mLog = Log.get(11);
  private static final int[] MObjectListFieldIds = { 1040, 1041, 1042, 1043, 1045 };
  private static String MObjectListSchema = null;
  private FieldGraph mTableFG;
  private FieldGraph.Node mTableFieldNode;
  private TableField mTableField;
  private ColumnField[] mColumns;
  private FieldGraph.Node[] mColumnNodes;
  private int[] mColumnDataFieldIDs;
  private int[] mColumnDataTypes;
  private EntryListCriteria mEntryListCriteria;
  private Map mDataFieldMap;
  protected OutputInteger mMatches;
  private int[] mIrid;
  private int[] mCcid;
  private static final Pattern COLUMN_REFERENCE_PATTERN = Pattern.compile("\\$([^\\$]*)\\$");
  protected String mFirstEntryId;
  private static Log MPerformanceLog = Log.get(8);
  protected boolean mbCombinedCall = false;

  TableEntryListBase()
  {
  }

  TableEntryListBase(String paramString)
  {
    super(paramString);
  }

  TableEntryListBase(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  protected abstract QualifierInfo getELCQualifier()
    throws GoatException;

  protected boolean sendReturnQualification()
  {
    return false;
  }

  private void emitColumnInfoAndGetTypes()
    throws GoatException
  {
    if (getEmitterFactory() == null)
      setEmitterFactory((IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
    append("f:").openList();
    this.mColumnDataTypes = new int[this.mColumns.length];
    for (int i = 0; i < this.mColumns.length; i++)
    {
      listSep().openObj();
      GoatField localGoatField;
      if (this.mColumns[i].isLocal())
        localGoatField = this.mTableFG.getField(this.mColumnDataFieldIDs[i]);
      else
        localGoatField = (GoatField)this.mDataFieldMap.get(Integer.valueOf(this.mColumnDataFieldIDs[i]));
      if (localGoatField != null)
      {
        IGoatFieldEmitter localIGoatFieldEmitter = getEmitterFactory().getEmitter(localGoatField);
        localIGoatFieldEmitter.emitTableProperties(this);
        this.mColumnDataTypes[i] = localGoatField.getMDataType().toInt();
      }
      else if (this.mColumns[i].getDataFieldID() == ColumnField.getMWeightFieldID())
      {
        GoatField.emitWeightHackTableProperties(this);
        this.mColumnDataTypes[i] = DataType.INTEGER.toInt();
      }
      else
      {
        GoatField.emitEmptyTableProperties(this);
        this.mColumnDataTypes[i] = DataType.NULL.toInt();
      }
      closeObj();
    }
    closeList();
  }

  private List<StatusInfo> doQuery(String paramString, long paramLong, int paramInt1, int paramInt2, long[] paramArrayOfLong, int paramInt3, RowEmitter paramRowEmitter)
    throws GoatException
  {
    ArrayList localArrayList1 = new ArrayList(0);
    ArrayList localArrayList2 = new ArrayList();
    Form localForm = Form.get(this.mServerUser.getServer(), paramString);
    int i = 0;
    if (this.mTableField.getMListColourField() != 0)
      i = 1;
    int j = 0;
    int k = 0;
    while (j < this.mColumnDataFieldIDs.length)
    {
      if (!this.mColumns[j].isLocal())
      {
        assert (k < paramInt3);
        if ((localForm.getCachedFieldMap(true).containsKey(Integer.valueOf(this.mColumnDataFieldIDs[j]))) || (this.mColumnDataFieldIDs[j] == 99L))
        {
          localArrayList2.add(Integer.valueOf(this.mColumnDataFieldIDs[j]));
          if ((i != 0) && (this.mColumnDataFieldIDs[j] == this.mTableField.getMListColourField()))
          {
            i = 0;
            this.mTableField.setMQueryListColoursIndex(j);
          }
        }
        else
        {
          mLog.fine("Removed missing field " + this.mColumnDataFieldIDs[j] + " from table query against schema " + paramString);
        }
      }
      j++;
    }
    GoatField localGoatField;
    if ((this.mIrid != null) && (this.mIrid.length > 0))
      for (j = 0; j < this.mIrid.length; j++)
      {
        localGoatField = (GoatField)this.mDataFieldMap.get(Integer.valueOf(this.mIrid[j]));
        if ((localGoatField != null) && (DataType.ENUM.equals(localGoatField.getMDataType())) && (!localArrayList2.contains(Integer.valueOf(this.mIrid[j]))))
          localArrayList2.add(Integer.valueOf(this.mIrid[j]));
      }
    if ((this.mCcid != null) && (this.mCcid.length > 0))
      for (j = 0; j < this.mCcid.length; j++)
      {
        localGoatField = (GoatField)this.mDataFieldMap.get(Integer.valueOf(this.mCcid[j]));
        if ((localGoatField != null) && (DataType.ENUM.equals(localGoatField.getMDataType())) && (!localArrayList2.contains(Integer.valueOf(this.mCcid[j]))))
          localArrayList2.add(Integer.valueOf(this.mCcid[j]));
      }
    if (i != 0)
    {
      this.mTableField.setMQueryListColoursIndex(localArrayList2.size());
      localArrayList2.add(Integer.valueOf(this.mTableField.getMListColourField()));
    }
    int[] arrayOfInt = new int[localArrayList2.size()];
    int m = 0;
    Object localObject1 = localArrayList2.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Integer localInteger = (Integer)((Iterator)localObject1).next();
      arrayOfInt[(m++)] = localInteger.intValue();
    }
    this.mEntryListCriteria = new EntryListCriteria();
    this.mEntryListCriteria.setSchemaID(paramString);
    this.mEntryListCriteria.setMaxLimit(paramInt1);
    this.mEntryListCriteria.setFirstRetrieve(paramInt2);
    this.mEntryListCriteria.setQualifier(getELCQualifier());
    localObject1 = null;
    Object localObject2;
    Object localObject3;
    if (paramArrayOfLong.length > 0)
    {
      if (paramArrayOfLong.length % 2 != 0)
        throw new GoatException("Bad sort order");
      localObject1 = new SortInfo[paramArrayOfLong.length / 2];
      for (int n = 0; n < paramArrayOfLong.length; n += 2)
      {
        if ((paramArrayOfLong[n] < 0L) || (paramArrayOfLong[n] >= this.mColumnDataFieldIDs.length))
          throw new GoatException("sort column out of range");
        localObject1[(n / 2)] = new SortInfo(this.mColumnDataFieldIDs[(int)paramArrayOfLong[n]], paramArrayOfLong[(n + 1)] == -1L ? 2 : 1);
      }
    }
    else
    {
      localObject2 = TableField.getColumnSortOrder(this.mTableFieldNode);
      i2 = 0;
      int i3 = 0;
      while (i2 < localObject2.length)
      {
        localObject3 = (ColumnField)localObject2[i2].mField;
        if (((ColumnField)localObject3).getMSortSeq() > 0)
        {
          if (localObject1 == null)
            localObject1 = new SortInfo[localObject2.length - i2];
          assert (i3 < localObject1.length);
          localObject1[(i3++)] = new SortInfo(((ColumnField)localObject3).getDataFieldID(), ((ColumnField)localObject3).getMSortDir() == 1 ? 2 : 1);
        }
        else
        {
          assert (localObject1 == null);
        }
        i2++;
      }
    }
    if (localObject1 == null)
      this.mEntryListCriteria.setSortInfos(null);
    else
      this.mEntryListCriteria.setSortInfos(Arrays.asList((Object[])localObject1));
    if (paramLong == 1020L)
    {
      localObject2 = SessionData.get().getPreferences();
      if (((ARUserPreferences)localObject2).getLimitQueryItemsWithPreferenceServer(this.mServerUser.getServer()).equals(ARUserPreferences.YES))
      {
        i2 = ((ARUserPreferences)localObject2).getMaxQueryItemsWithPreferenceServer(this.mServerUser.getServer());
        if ((this.mEntryListCriteria.getMaxLimit() == 0) || (i2 < this.mEntryListCriteria.getMaxLimit()))
          this.mEntryListCriteria.setMaxLimit(i2);
      }
    }
    int i1 = ServerInfo.get(this.mServerUser.getServer(), true).getVersionAsNumber();
    this.mMatches = new OutputInteger(0);
    for (int i2 = 0; i2 < 2; i2++)
    {
      MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(12);
      try
      {
        this.mServerUser.getListEntryObjects(this.mEntryListCriteria.getSchemaID(), this.mEntryListCriteria.getQualifier(), this.mEntryListCriteria.getFirstRetrieve(), this.mEntryListCriteria.getMaxLimit(), this.mEntryListCriteria.getSortInfos(), arrayOfInt, paramLong == 1020L ? false : this.mTableField.isMUseLocale(), this.mMatches, paramRowEmitter);
        localObject3 = this.mServerUser.getLastStatus();
        for (int i4 = 0; i4 < ((List)localObject3).size(); i4++)
        {
          StatusInfo localStatusInfo = (StatusInfo)((List)localObject3).get(i4);
          if (localStatusInfo.getMessageNum() == 75L)
            localArrayList1.add(localStatusInfo);
        }
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
      finally
      {
        localMeasurement.end();
      }
      if ((this.mMatches.intValue() <= 0) || (paramRowEmitter.mNumRows > 0))
        break;
      double d = this.mMatches.intValue() / this.mTableField.getChunkSize();
      int i5 = ((int)Math.ceil(d) - 1) * this.mTableField.getChunkSize();
      this.mEntryListCriteria.setFirstRetrieve(i5);
    }
    return localArrayList1;
  }

  protected void getAndEmitTable(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, long paramLong1, long paramLong2, long[] paramArrayOfLong, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, String paramString5, String paramString6, int[] paramArrayOfInt3, int[] paramArrayOfInt4)
    throws GoatException
  {
    this.mTableFG = FieldGraph.get(Form.get(this.mTableServerUser.getServer(), paramString1).getViewInfoByInference(paramString2, false, false));
    if (this.outStream != null)
      this.bOutputStream = new BufferedOutputStream(this.outStream, 1024);
    if (paramInt == 1020L)
    {
      localObject1 = SessionData.get().getPreferences();
      if (((ARUserPreferences)localObject1).getLimitQueryItemsWithPreferenceServer(this.mServerUser.getServer()).equals(ARUserPreferences.YES))
        this.maxLimit = ((ARUserPreferences)localObject1).getMaxQueryItemsWithPreferenceServer(this.mServerUser.getServer());
    }
    this.mTableFG.instantiateFields();
    this.mIrid = paramArrayOfInt3;
    this.mCcid = paramArrayOfInt4;
    this.mTableFieldNode = this.mTableFG.getNode(paramInt);
    if ((this.mTableFieldNode == null) && (paramInt == 1020L))
    {
      this.mTableFG.addResultsListNode();
      this.mTableFieldNode = this.mTableFG.getNode(paramInt);
    }
    if ((this.mTableFieldNode == null) || (!(this.mTableFieldNode.mField instanceof TableField)))
      throw new GoatException(9352);
    this.mTableField = ((TableField)this.mTableFieldNode.mField);
    Object localObject1 = FieldGraph.get(Form.get(this.mServerUser.getServer(), paramString3).getViewInfoByInference(null, false, false));
    FieldGraph.Node[] arrayOfNode = TableField.getSortedColumnChildren(this.mTableFieldNode);
    this.mColumns = new ColumnField[arrayOfNode.length];
    this.mColumnNodes = new FieldGraph.Node[arrayOfNode.length];
    this.mColumnDataFieldIDs = new int[arrayOfNode.length];
    int i = 0;
    for (int j = 0; j < arrayOfNode.length; j++)
    {
      this.mColumns[j] = ((ColumnField)(ColumnField)arrayOfNode[j].mField);
      this.mColumnDataFieldIDs[j] = this.mColumns[j].getDataFieldID();
      this.mColumnNodes[j] = ((FieldGraph)localObject1).getNode(this.mColumnDataFieldIDs[j]);
      if (!this.mColumns[j].isLocal())
        i++;
    }
    this.mDataFieldMap = GoatField.get(Form.get(this.mServerUser.getServer(), paramString3).getViewInfoByInference(null, false, false), true);
    if (!this.mbCombinedCall)
      append("this.result=");
    openObj();
    emitColumnInfoAndGetTypes();
    append(",r:").openList();
    ArrayList localArrayList = new ArrayList(0);
    Object localObject2;
    Object localObject3;
    if (paramInt == 1040)
    {
      localObject2 = new ObjectListRowEmitter(null);
      doObjectListQuery(paramArrayOfLong, (IARRowIterator)localObject2, paramArrayOfInt1, paramArrayOfString, paramArrayOfInt2);
    }
    else
    {
      localObject2 = new RowEmitter(null);
      localObject3 = doQuery(paramString3, paramInt, (int)paramLong2, (int)paramLong1, paramArrayOfLong, i, (RowEmitter)localObject2);
      localArrayList.addAll((Collection)localObject3);
    }
    closeList();
    ((RowEmitter)localObject2).emitIDMapProperty();
    if ((sendReturnQualification()) && (this.mEntryListCriteria != null))
    {
      localObject3 = new Qualifier(this.mEntryListCriteria.getQualifier(), this.mServerUser.getServer());
      property("q", ((Qualifier)localObject3).emitEncodedAsString());
    }
    property("n", this.mMatches);
    this.mFirstEntryId = ((RowEmitter)localObject2).getFirstEntryId();
    if (this.mEntryListCriteria != null)
      property("start", this.mEntryListCriteria.getFirstRetrieve());
    else
      property("start", paramLong1);
    if ((this.mMatches.intValue() > 0) && (paramString5 != null))
    {
      localObject3 = null;
      if (paramString5.equals("0"))
        localObject3 = this.mFirstEntryId;
      else
        localObject3 = paramString5;
      append(",e:");
      try
      {
        loadSingleEntry(this.mServerUser, paramString3, (String)localObject3, null, true);
      }
      catch (GoatException localGoatException)
      {
        if (checkIfEntryDeleted(localGoatException.getStatusInfo()))
        {
          localObject3 = this.mFirstEntryId;
          loadSingleEntry(this.mServerUser, paramString3, (String)localObject3, null, true);
        }
        else
        {
          throw localGoatException;
        }
      }
      append(",eid:'").append((String)localObject3).append("'");
      List localList = this.mServerUser.getLastStatus();
      localArrayList.addAll(localList);
      if (paramString6 != null)
        AttachmentData.removeAllUnused(paramString6, SessionData.get().getID());
    }
    closeObj();
    if (!this.mbCombinedCall)
      append(";");
    if ((this.bOutputStream != null) && (this.outputStreamed))
    {
      try
      {
        emitToStream(this.bOutputStream, toString().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException1)
      {
        throw new GoatException(localUnsupportedEncodingException1.getMessage());
      }
      clear();
    }
    if ((paramInt == 1020L) && (((this.maxLimit > 0) && (paramLong2 == 0L) && (this.mMatches.intValue() > this.maxLimit)) || ((paramLong2 > this.maxLimit) && (((RowEmitter)localObject2).mNumRows == this.maxLimit) && ((int)paramLong1 + ((RowEmitter)localObject2).mNumRows != this.mMatches.intValue()))))
    {
      String str = SessionData.get().getLocale();
      localArrayList.add(new StatusInfo(1, 9378L, MessageTranslation.getLocalizedErrorMessage(str, 9378, null), null));
    }
    this.mStatus = localArrayList;
    this.mConvertIdToLabel = true;
    if (length() > 524288)
      MPerformanceLog.fine("getAndEmitTable: size > 0.5MChars table=" + this.mTableServerUser.getServer() + "/" + paramString1 + "/" + paramInt + " remote=" + this.mServerUser.getServer() + "/" + paramString3 + " rows=" + ((RowEmitter)localObject2).mNumRows);
    if ((this.bOutputStream != null) && (this.outputStreamed))
    {
      emitStatus(this.mStatus, true);
      try
      {
        emitToStream(this.bOutputStream, toString().getBytes("UTF-8"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException2)
      {
        throw new GoatException(localUnsupportedEncodingException2.getMessage());
      }
      try
      {
        this.bOutputStream.flush();
        this.bOutputStream.close();
      }
      catch (IOException localIOException)
      {
        throw new GoatException(localIOException.getMessage());
      }
    }
  }

  private void emitToStream(OutputStream paramOutputStream, byte[] paramArrayOfByte)
    throws GoatException
  {
    if (paramOutputStream == null)
      throw new GoatException("Cannot write data to output stream. The stream is null");
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0))
      return;
    try
    {
      paramOutputStream.write(toString().getBytes("UTF-8"));
    }
    catch (IOException localIOException)
    {
      throw new GoatException(localIOException.getMessage(), localIOException.getCause());
    }
  }

  private boolean checkIfEntryDeleted(List<StatusInfo> paramList)
  {
    if (paramList != null)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        StatusInfo localStatusInfo = (StatusInfo)localIterator.next();
        if (localStatusInfo.getMessageNum() == 302L)
          return true;
      }
    }
    return false;
  }

  public static final synchronized String getObjectListSchema(String paramString)
  {
    assert (paramString != null);
    if (MObjectListSchema == null)
      try
      {
        ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString);
        try
        {
          List localList = localServerLogin.getListForm(0L, FormType.ALL.toInt(), null, MObjectListFieldIds);
          if (localList.size() > 0)
            MObjectListSchema = (String)localList.get(0);
        }
        catch (ARException localARException)
        {
        }
      }
      catch (GoatException localGoatException)
      {
      }
    return MObjectListSchema;
  }

  private void doObjectListQuery(long[] paramArrayOfLong, IARRowIterator paramIARRowIterator, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2)
  {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    boolean bool = false;
    for (int i = 0; i < paramArrayOfInt1.length; i++)
      if ((paramArrayOfInt1[i] == 1041) && (paramArrayOfInt2[i] == 4))
        str1 = paramArrayOfString[i].trim();
      else if ((paramArrayOfInt1[i] == 1042) && (paramArrayOfInt2[i] == 4))
        str2 = paramArrayOfString[i].trim();
      else if ((paramArrayOfInt1[i] == 1043) && (paramArrayOfInt2[i] == 4))
        str3 = paramArrayOfString[i].trim();
      else if ((paramArrayOfInt1[i] == 1045) && (paramArrayOfInt2[i] != 0))
        bool = true;
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    if ((str1 != null) && (str1.length() > 0))
    {
      if ((str2 != null) && (str2.length() > 0))
        getObjectsForApplication(str1, str2, localArrayList);
      else
        getObjectsForServer(str1, localArrayList, 3, bool);
    }
    else
    {
      localObject = Configuration.getInstance().getShortServers();
      for (int j = 0; j < ((List)localObject).size(); j++)
        if ((str2 != null) && (str2.length() > 0))
          getObjectsForApplication((String)((List)localObject).get(j), str2, localArrayList);
        else
          getObjectsForServer((String)((List)localObject).get(j), localArrayList, 3, bool);
    }
    if ((str3 != null) && (str3.length() > 0))
    {
      str3 = str3.toLowerCase();
      localObject = localArrayList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str4 = ((Value[])(Value[])localObject.next())[0].toString().toLowerCase();
        if (str4.indexOf(str3) == -1)
          ((Iterator)localObject).remove();
      }
    }
    if (paramArrayOfLong.length == 0)
      localObject = new long[] { 0L, 1L };
    else
      localObject = paramArrayOfLong;
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        Value[] arrayOfValue1 = (Value[])paramAnonymousObject1;
        Value[] arrayOfValue2 = (Value[])paramAnonymousObject2;
        for (int i = 0; i < this.val$so.length; i += 2)
        {
          int j = (int)this.val$so[i];
          Value localValue1 = arrayOfValue1[j];
          Value localValue2 = arrayOfValue2[j];
          DataType localDataType = localValue1.getDataType();
          assert (localDataType.equals(localValue2.getDataType()));
          Object localObject1 = localValue1.getValue();
          Object localObject2 = localValue2.getValue();
          int k = 0;
          if (localDataType.equals(DataType.CHAR))
            k = ((String)localObject1).compareToIgnoreCase((String)localObject2);
          else if (localDataType.equals(DataType.ENUM))
            k = ((Integer)localObject1).intValue() - ((Integer)localObject2).intValue();
          k = (int)(k * this.val$so[(i + 1)]);
          if (k != 0)
            return k;
        }
        return 0;
      }
    });
    int k = localArrayList.size();
    this.mMatches = new OutputInteger(k);
    for (int m = 0; m < k; m++)
    {
      Entry localEntry = new Entry();
      localEntry.setEntryId("" + m);
      for (int n = 0; n < this.mColumns.length; n++)
        localEntry.put(Integer.valueOf(this.mColumns[n].getDataFieldID()), ((Value[])(Value[])localArrayList.get(m))[n]);
      paramIARRowIterator.iteratorCallback(localEntry);
    }
  }

  private void getObjectsForServer(String paramString, List paramList, int paramInt, boolean paramBoolean)
  {
    assert (this.mColumns.length == 4);
    try
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString);
      Value localValue1 = new Value(paramString);
      Value localValue2;
      Object localObject;
      if ((paramInt & 0x1) != 0)
      {
        localValue2 = new Value(Long.valueOf(0L), DataType.ENUM);
        localObject = SchemaList.getSchemaList(localServerLogin, paramBoolean);
        for (int i = 0; i < localObject.length; i++)
        {
          Value[] arrayOfValue1 = new Value[4];
          arrayOfValue1[0] = localObject[i][1];
          arrayOfValue1[1] = localValue1;
          arrayOfValue1[2] = localValue2;
          arrayOfValue1[3] = localObject[i][0];
          paramList.add(arrayOfValue1);
        }
      }
      if ((paramInt & 0x2) != 0)
      {
        localValue2 = new Value(Long.valueOf(1L), DataType.ENUM);
        localObject = new Value();
        String[] arrayOfString = GoatApplicationContainer.getContainerList(localServerLogin);
        for (int j = 0; j < arrayOfString.length; j++)
        {
          Value localValue3 = new Value(arrayOfString[j]);
          Value[] arrayOfValue2 = new Value[4];
          arrayOfValue2[0] = localValue3;
          arrayOfValue2[1] = localValue1;
          arrayOfValue2[2] = localValue2;
          arrayOfValue2[3] = localObject;
          paramList.add(arrayOfValue2);
        }
      }
    }
    catch (GoatException localGoatException)
    {
    }
  }

  private void getObjectsForApplication(String paramString1, String paramString2, List paramList)
  {
    assert (this.mColumns.length == 4);
    try
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
      GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(localServerLogin, paramString2);
      Value localValue1 = new Value(paramString1);
      Value localValue2 = new Value();
      Value[] arrayOfValue = new Value[4];
      Value localValue4;
      if (localGoatApplicationContainer.getPrimaryForm() != null)
      {
        localValue3 = new Value(Long.valueOf(1L), DataType.ENUM);
        localValue4 = new Value(paramString2);
        arrayOfValue[0] = localValue4;
        arrayOfValue[1] = localValue1;
        arrayOfValue[2] = localValue3;
        arrayOfValue[3] = localValue2;
        paramList.add(arrayOfValue);
      }
      Value localValue3 = new Value(Long.valueOf(0L), DataType.ENUM);
      Set localSet = localGoatApplicationContainer.getFormSet();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        localValue4 = new Value((String)localIterator.next());
        arrayOfValue = new Value[4];
        arrayOfValue[0] = localValue4;
        arrayOfValue[1] = localValue1;
        arrayOfValue[2] = localValue3;
        arrayOfValue[3] = localValue4;
        paramList.add(arrayOfValue);
      }
    }
    catch (GoatException localGoatException)
    {
    }
  }

  private void setEmitterFactory(IEmitterFactory paramIEmitterFactory)
  {
    this.emitterFactory = paramIEmitterFactory;
  }

  private IEmitterFactory getEmitterFactory()
  {
    return this.emitterFactory;
  }

  public static class SchemaList
    implements Cache.Item
  {
    private static final long serialVersionUID = -8872130602383349320L;
    private static Cache MCache = new MiscCache("SchemaList");
    private Value[][] mSchemaList;
    private String mServer;

    public final int getSize()
    {
      return 1;
    }

    public static Cache getCache()
    {
      return MCache;
    }

    private static String getKey(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    {
      return paramString1 + "/" + paramString2 + "/" + paramString3 + "/" + paramBoolean;
    }

    private SchemaList(ServerLogin paramServerLogin, boolean paramBoolean)
      throws GoatException
    {
      try
      {
        this.mServer = paramServerLogin.getServer();
        FormType localFormType = FormType.ALL;
        if (paramBoolean)
          localFormType.setHiddenIncrement();
        else
          localFormType.unSetHiddenIncrement();
        List localList = paramServerLogin.getListFormAliases(0L, localFormType.toInt(), null, new int[0], null);
        this.mSchemaList = new Value[localList.size()][2];
        for (int i = 0; i < this.mSchemaList.length; i++)
        {
          String str1 = ((FormAliasInfo)localList.get(i)).getFormKey().toString();
          String str2 = ((FormAliasInfo)localList.get(i)).getAlias();
          if ((str2 == null) || (str2.length() == 0))
            str2 = str1;
          Value[] arrayOfValue = this.mSchemaList[i];
          arrayOfValue[0] = new Value(str1);
          arrayOfValue[1] = new Value(str2);
          this.mSchemaList[i] = arrayOfValue;
        }
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
    }

    public final String getServer()
    {
      return this.mServer;
    }

    public static Value[][] getSchemaList(ServerLogin paramServerLogin, boolean paramBoolean)
      throws GoatException
    {
      String str1 = paramServerLogin.getServer();
      String str2 = getKey(str1, paramServerLogin.getPermissionsKey(), paramServerLogin.getLocale(), paramBoolean).intern();
      synchronized (str2)
      {
        SchemaList localSchemaList = (SchemaList)MCache.get(str2, SchemaList.class);
        if (localSchemaList == null)
        {
          localSchemaList = new SchemaList(paramServerLogin, paramBoolean);
          MCache.put(str2, localSchemaList);
        }
        return localSchemaList.mSchemaList;
      }
    }
  }

  private class ObjectListRowEmitter extends TableEntryListBase.RowEmitter
  {
    private ObjectListRowEmitter()
    {
      super(null);
    }

    public void emitIDMapProperty()
    {
      TableEntryListBase.this.append(",idmap:").openObj();
      for (int i = 0; i < this.mNumRows; i++)
        TableEntryListBase.this.escapedProperty("" + i, i);
      TableEntryListBase.this.closeObj();
    }
  }

  protected class RowEmitter
    implements IARRowIterator
  {
    private int[] mFieldIDs;
    protected int mNumRows = 0;
    private final JSWriter mIDWriter = new JSWriter();
    private String mEntryId;
    private boolean streamCheckDone = false;
    private int minEntriesForStreaming = 0;

    private RowEmitter()
    {
      this.mIDWriter.openObj();
      this.minEntriesForStreaming = Configuration.getInstance().getIntProperty("arsystem.min_entries_for_streaming", 0);
    }

    public String getFirstEntryId()
    {
      return this.mEntryId;
    }

    public void iteratorCallback(Entry paramEntry)
    {
      if ((!this.streamCheckDone) && (this.minEntriesForStreaming > 0))
      {
        int i = (TableEntryListBase.this.mMatches.intValue() > TableEntryListBase.this.maxLimit) && (TableEntryListBase.this.maxLimit > 0) ? TableEntryListBase.this.maxLimit : TableEntryListBase.this.mMatches.intValue();
        if (i > this.minEntriesForStreaming)
          TableEntryListBase.this.outputStreamed = true;
        this.streamCheckDone = true;
      }
      assert (paramEntry != null);
      try
      {
        emitRow(paramEntry);
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
      this.mIDWriter.escapedProperty(paramEntry.getEntryId(), this.mNumRows++);
    }

    public void emitIDMapProperty()
    {
      if (this.mIDWriter != null)
      {
        this.mIDWriter.closeObj();
        TableEntryListBase.this.property("idmap", this.mIDWriter);
      }
      else
      {
        TableEntryListBase.this.append(",idmap:").openObj().closeObj();
      }
    }

    private void emitRow(Entry paramEntry)
      throws GoatException
    {
      int i = -1;
      TableEntryListBase.this.listSep();
      TableEntryListBase.this.openObj();
      String str1 = paramEntry.getEntryId();
      if (this.mEntryId == null)
        this.mEntryId = str1;
      TableEntryListBase.this.property("i", str1);
      TableEntryListBase.this.append(",d:");
      TableEntryListBase.this.openList();
      GoatType[] arrayOfGoatType = new GoatType[TableEntryListBase.this.mColumns.length];
      HashMap localHashMap = new HashMap(TableEntryListBase.this.mColumns.length);
      LinkedList localLinkedList = new LinkedList();
      Object localObject1;
      Object localObject2;
      for (int j = 0; j < TableEntryListBase.this.mColumns.length; j++)
      {
        localObject1 = (GoatType)localHashMap.get(Integer.valueOf(TableEntryListBase.this.mColumns[j].getMFieldID()));
        if (localObject1 == null)
        {
          localObject2 = (Value)paramEntry.get(Integer.valueOf(TableEntryListBase.this.mColumns[j].getDataFieldID()));
          if ((localObject2 != null) && ((!TableEntryListBase.this.mColumns[j].isLocal()) || (TableEntryListBase.this.mTableField.getMFieldID() == 1040)))
            localObject1 = GoatTypeFactory.create((Value)localObject2, TableEntryListBase.this.mColumns[j].getDataFieldID(), str1, TableEntryListBase.this.mServerUser, TableEntryListBase.this.mColumnNodes[j]);
          else if (TableEntryListBase.this.mColumns[j].isLocal())
            localObject1 = createTypeForLocalColumn(str1, j, localHashMap, localLinkedList);
          else
            localObject1 = NullType.MNullType;
          localHashMap.put(Integer.valueOf(TableEntryListBase.this.mColumns[j].getMFieldID()), localObject1);
        }
        arrayOfGoatType[j] = localObject1;
      }
      if (localLinkedList.size() > 0)
      {
        Iterator localIterator = localLinkedList.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (FwdRefData)localIterator.next();
          assert (localObject1 != null);
          localObject2 = (GoatType)localHashMap.get(Integer.valueOf(((FwdRefData)localObject1).mReferredColFid));
          if ((localObject2 != null) && (((GoatType)localObject2).getDataType() == ((FwdRefData)localObject1).mReferrerType))
          {
            assert (arrayOfGoatType.length > ((FwdRefData)localObject1).mReferrerIdx);
            arrayOfGoatType[localObject1.mReferrerIdx] = localObject2;
          }
        }
      }
      int k = 0;
      Object localObject3;
      for (int m = 0; m < TableEntryListBase.this.mColumns.length; m++)
      {
        localObject2 = arrayOfGoatType[m];
        TableEntryListBase.this.listSep().openObj();
        localObject3 = (GoatField)TableEntryListBase.this.mDataFieldMap.get(Integer.valueOf(TableEntryListBase.this.mColumns[m].getDataFieldID()));
        if ((localObject3 != null) && (DataType.TIME.equals(((GoatField)localObject3).getMDataType())) && (((GoatType)localObject2).getDataType() == 7))
          if (TableEntryListBase.this.mTableField.getDisplayType() == 5)
            ((TimeType)localObject2).setDisplayType(TableEntryListBase.this.mColumns[m].getMDateTimePopup());
          else
            ((TimeType)localObject2).setDisplayType(((TimeField)localObject3).getDateTimePopup());
        String str2 = null;
        if ((localObject2 != null) && (((GoatType)localObject2).toValue() != null) && (TableEntryListBase.this.mColumns[m] != null) && (TableEntryListBase.this.mColumns[m].getMEnumLabels() != null) && (((GoatType)localObject2).getDataType() == 6))
        {
          int n = TableEntryListBase.this.mColumns[m].idToIndex(((GoatType)localObject2).toValue().getIntValue());
          if ((n != 2147483647) && (n >= 0) && (n < TableEntryListBase.this.mColumns[m].getMEnumLabels().length))
            str2 = TableEntryListBase.this.mColumns[m].getMEnumLabels()[n];
        }
        else
        {
          str2 = ((GoatType)localObject2).forHTML();
        }
        if (str2 == null)
          str2 = "";
        if (k == 0)
          k = str2.trim().length() > 0 ? 1 : 0;
        int i1 = (k == 0) && (m == TableEntryListBase.this.mColumns.length - 1) && (TableEntryListBase.this.mTableField.getDisplayType() != 5) ? 1 : 0;
        if ((TableEntryListBase.this.mColumns[m].getDisplayType() == 2) && (TableEntryListBase.this.mTableField.getDisplayType() != 3))
          TableEntryListBase.this.property("v", i1 != 0 ? "&nbsp;" : str2);
        else if (i1 != 0)
          TableEntryListBase.this.property("v", "&nbsp;");
        else
          TableEntryListBase.this.propertyDestinedForHTML("v", str2);
        if (TableEntryListBase.this.mColumnDataTypes[m] != ((GoatType)localObject2).getDataType())
          TableEntryListBase.this.property("t", ((GoatType)localObject2).getDataType());
        TableEntryListBase.this.property("p", ((GoatType)localObject2).toJFieldPrimitive());
        TableEntryListBase.this.closeObj();
      }
      TableEntryListBase.this.closeList();
      if ((TableEntryListBase.this.mIrid != null) && (TableEntryListBase.this.mIrid.length > 0))
      {
        TableEntryListBase.this.append(",irv:");
        TableEntryListBase.this.openObj();
        for (m = 0; m < TableEntryListBase.this.mIrid.length; m++)
        {
          localObject2 = (Value)paramEntry.get(Integer.valueOf(TableEntryListBase.this.mIrid[m]));
          localObject3 = "";
          if (localObject2 != null)
          {
            if (((Value)localObject2).getDataType().equals(DataType.ENUM))
              localObject3 = ((Integer)((Value)localObject2).getValue()).toString();
            TableEntryListBase.this.property(TableEntryListBase.this.mIrid[m], (String)localObject3);
          }
        }
        TableEntryListBase.this.closeObj();
      }
      if ((TableEntryListBase.this.mCcid != null) && (TableEntryListBase.this.mCcid.length > 0))
      {
        TableEntryListBase.this.append(",ccV:");
        TableEntryListBase.this.openObj();
        for (m = 0; m < TableEntryListBase.this.mCcid.length; m++)
        {
          localObject2 = (Value)paramEntry.get(Integer.valueOf(TableEntryListBase.this.mCcid[m]));
          localObject3 = "";
          if (localObject2 != null)
          {
            if (((Value)localObject2).getDataType().equals(DataType.ENUM))
              localObject3 = ((Integer)((Value)localObject2).getValue()).toString();
            TableEntryListBase.this.property(TableEntryListBase.this.mCcid[m], (String)localObject3);
          }
        }
        TableEntryListBase.this.closeObj();
      }
      Value localValue = (Value)paramEntry.get(Integer.valueOf(TableEntryListBase.this.mTableField.getMListColourField()));
      if (localValue != null)
      {
        if (localValue.getDataType().equals(DataType.NULL))
          i = 0;
        else if (localValue.getDataType().equals(DataType.ENUM))
          i = ((Integer)localValue.getValue()).intValue() + 1;
        if ((i < 0) || (((TableEntryListBase.this.mTableField.getMQueryListColours() == null) || ((TableEntryListBase.this.mTableField.getMQueryListColours() != null) && (i >= TableEntryListBase.this.mTableField.getMQueryListColours().length))) && ((TableEntryListBase.this.mTableField.getMQueryListBKGColours() == null) || ((TableEntryListBase.this.mTableField.getMQueryListBKGColours() != null) && (i >= TableEntryListBase.this.mTableField.getMQueryListBKGColours().length)))))
          i = -1;
      }
      TableEntryListBase.this.property("rc", i);
      if ((TableEntryListBase.this.bOutputStream != null) && (TableEntryListBase.this.outputStreamed))
      {
        try
        {
          TableEntryListBase.this.emitToStream(TableEntryListBase.this.bOutputStream, TableEntryListBase.this.mBuffer.toString().getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new GoatException(localUnsupportedEncodingException.getMessage());
        }
        TableEntryListBase.this.clear();
      }
      TableEntryListBase.this.closeObj();
    }

    private GoatType createTypeForLocalColumn(String paramString, int paramInt, Map paramMap, List paramList)
      throws GoatException
    {
      ColumnField localColumnField = TableEntryListBase.this.mColumns[paramInt];
      int i = TableEntryListBase.this.mColumnDataFieldIDs[paramInt];
      String str = localColumnField.getDefault();
      Value localValue = new Value();
      if ((str != null) && (str.length() > 0))
      {
        GoatField localGoatField1 = TableEntryListBase.this.mTableFG.getField(i);
        if (localGoatField1 == null)
          return NullType.MNullType;
        Matcher localMatcher = TableEntryListBase.COLUMN_REFERENCE_PATTERN.matcher(str);
        if (localMatcher.find())
        {
          int j = Integer.parseInt(localMatcher.group(1));
          GoatField localGoatField2 = TableEntryListBase.this.mTableFG.getField(j);
          if (localGoatField2 != null)
          {
            Object localObject = (GoatType)paramMap.get(Integer.valueOf(j));
            if (localObject != null)
            {
              if (((GoatType)localObject).getDataType() == localGoatField1.getMDataType().toInt())
              {
                if (((GoatType)localObject).getDataType() == 12)
                  localObject = new CurrencyType((CurrencyType)localObject, (CurrencyField)localGoatField1);
                return localObject;
              }
            }
            else
              paramList.add(new FwdRefData(paramInt, localGoatField1.getMDataType().toInt(), j, null));
          }
        }
        if ((DataType.CHAR.equals(localGoatField1.getMDataType())) || (DataType.DIARY.equals(localGoatField1.getMDataType())))
          localValue = new Value(str);
      }
      return GoatTypeFactory.create(localValue, i, paramString, TableEntryListBase.this.mServerUser, TableEntryListBase.this.mColumnNodes[paramInt]);
    }

    private class FwdRefData
    {
      int mReferrerIdx;
      int mReferrerType;
      int mReferredColFid;

      private FwdRefData(int paramInt1, int paramInt2, int arg4)
      {
        this.mReferrerIdx = paramInt1;
        this.mReferrerType = paramInt2;
        int i;
        this.mReferredColFid = i;
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.TableEntryListBase
 * JD-Core Version:    0.6.1
 */