package com.remedy.arsys.goat.sharedresource;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.sharedresource.template.TemplateResourceFactory;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.Cachetable;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ARSharedResource
  implements Cache.Item
{
  private static final long serialVersionUID = 4909496661034506641L;
  private String mServer;
  private String mName;
  private String mLocale;
  private String mSchemaKey;
  private byte[] mData;
  private int mType;
  private String mSubType;
  private String mMimeType;
  private String mContextPath;
  private IResourceObject mResource;
  private QualifierInfo mQual = null;
  private long mLastChecked;
  private long mLastModified;
  private static final int NAME_FID = 41100;
  private static final int CONTENT_FID = 41103;
  private static final int TYPE_FID = 41105;
  private static final int SUBTYPE_FID = 41106;
  private static final int MIME_TYPE = 41107;
  private static Map<String, Long> LastModifiedTemplateTimeMap;
  private static final int[] RESOURCE_SCHEMA_IDS;
  private static final Value STATUS_ACTIVE;
  private static final int TEMPLATE_TYPE = 0;
  private static transient Log MLog;
  private static final ArithmeticOrRelationalOperand NAME_OPERAND;
  private static final QualifierInfo STATUS_EQ_ACTIVE;
  private static final ArithmeticOrRelationalOperand MOD_DATE_OP;
  private static final QualifierInfo TEMPLATE_TYPE_QUAL;
  private static final int[] FIELDS_TO_RETRIEVE;
  private static final int[] TIME_FIELDS_TO_RETRIEVE;
  private static final int[] BULK_TIME_FIELDS_TO_RETRIEVE;
  private static final Cache MResourceCache;
  private static final Set<String> MResourceLocks;
  private static Map<String, List<String>> MTemplateKeyMap;
  private static final IResourceFactory DEFAULT_RESOURCE_FACTORY;
  private static Map<Integer, IResourceFactory> MFactoryMap;

  private ARSharedResource(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5)
  {
    try
    {
      init(paramString1, paramString2, paramInt, paramString3, paramString4, paramString5);
      ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
      this.mData = getResourceData(localServerLogin);
      initFromData(localServerLogin, this.mData);
    }
    catch (GoatException localGoatException)
    {
      MLog.severe("Error processing the Resource" + paramString2);
      MLog.severe("ARSharedResource Error - " + localGoatException.getMessage());
    }
  }

  private ARSharedResource(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
  {
    init(paramString1, paramString2, paramInt, paramString3, paramString4, paramString5);
  }

  private void init(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5)
  {
    this.mServer = paramString1;
    this.mName = paramString2;
    this.mType = paramInt;
    this.mLocale = paramString4;
    this.mSubType = paramString3;
    this.mMimeType = "text/html";
    this.mContextPath = paramString5;
  }

  private void initFromData(ServerLogin paramServerLogin, byte[] paramArrayOfByte)
    throws GoatException
  {
    IResourceFactory localIResourceFactory = (IResourceFactory)MFactoryMap.get(new Integer(this.mType));
    localIResourceFactory = localIResourceFactory == null ? DEFAULT_RESOURCE_FACTORY : localIResourceFactory;
    String str = "UTF-8";
    try
    {
      str = paramServerLogin.getServerCharSet();
      str = str.toLowerCase(Locale.US);
    }
    catch (ARException localARException)
    {
      MLog.severe("ARSharedResource Error - " + localARException.getMessage());
    }
    this.mResource = localIResourceFactory.createFromData(this.mServer, this.mName, this.mType, this.mSubType, paramArrayOfByte, this.mMimeType, str, this.mContextPath);
  }

  private static final String getCacheKey(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4)
  {
    return ("ARRESOURCE:" + paramString1 + "/" + paramString2 + "/" + paramInt + "/" + paramString3 + "/" + paramString4).intern();
  }

  private final String getCacheKey()
  {
    return getCacheKey(getServer(), getName(), getType(), getSubType(), getLocale());
  }

  private static QualifierInfo createTemplateWithModifiedQual(String paramString)
  {
    Long localLong = (Long)LastModifiedTemplateTimeMap.get(paramString);
    Timestamp localTimestamp = new Timestamp(localLong == null ? 0L : localLong.longValue());
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(localTimestamp));
    return new QualifierInfo(1, TEMPLATE_TYPE_QUAL, new QualifierInfo(new RelationalOperationInfo(2, MOD_DATE_OP, localArithmeticOrRelationalOperand)));
  }

  public static boolean checkTemplateCache(String paramString)
  {
    List localList1 = (List)MTemplateKeyMap.get(paramString);
    if ((localList1 == null) || (localList1.size() <= 0))
      return false;
    boolean bool = false;
    ServerLogin localServerLogin = null;
    try
    {
      localServerLogin = ServerLogin.getAdmin(paramString);
      String str = SchemaKeyFactory.getInstance().getSchemaKey(localServerLogin, RESOURCE_SCHEMA_IDS);
      Long localLong = (Long)LastModifiedTemplateTimeMap.get(paramString);
      long l1 = localLong == null ? 0L : localLong.longValue();
      List localList2 = localServerLogin.getListEntryObjects(str, createTemplateWithModifiedQual(paramString), 0, 0, new ArrayList(), TIME_FIELDS_TO_RETRIEVE, false, null);
      if ((localList2 != null) && (localList2.size() > 0))
      {
        Object localObject1;
        for (int i = 0; i < localList2.size(); i++)
        {
          Entry localEntry = (Entry)localList2.get(i);
          if (localEntry != null)
          {
            localObject1 = (Value)localEntry.get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()));
            long l2 = 0L;
            if (localObject1 != null)
              l2 = ((Timestamp)((Value)localObject1).getValue()).getValue();
            if (l1 < l2)
              l1 = l2;
          }
        }
        LastModifiedTemplateTimeMap.put(paramString, new Long(l1));
        ArrayList localArrayList = new ArrayList();
        for (int j = 0; j < localList1.size(); j++)
        {
          localObject1 = (String)localList1.get(j);
          synchronized (localObject1)
          {
            try
            {
              while (MResourceLocks.contains(localObject1))
                localObject1.wait();
            }
            catch (InterruptedException localInterruptedException)
            {
              localInterruptedException.printStackTrace();
            }
            ARSharedResource localARSharedResource = (ARSharedResource)MResourceCache.get((String)localObject1, ARSharedResource.class);
            if ((localARSharedResource == null) || (localARSharedResource.isResourceDeletedorModified()))
            {
              bool = true;
              localArrayList.add(localObject1);
            }
          }
        }
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          localObject1 = (String)localIterator.next();
          MResourceCache.remove((String)localObject1);
        }
        MResourceCache.save();
      }
    }
    catch (ARException localARException)
    {
      MLog.severe(localARException.getLocalizedMessage());
    }
    catch (GoatException localGoatException)
    {
      MLog.severe(localGoatException.getLocalizedMessage());
    }
    return bool;
  }

  public static ARSharedResource get(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4)
  {
    ARSharedResource localARSharedResource = null;
    String str1 = SessionData.get().getLocale();
    String str2 = getCacheKey(paramString1, paramString2, paramInt, paramString3, str1);
    synchronized (str2)
    {
      try
      {
        while (MResourceLocks.contains(str2))
          str2.wait();
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
      localARSharedResource = getFromCache(str2);
      if (localARSharedResource == null)
      {
        localARSharedResource = new ARSharedResource(paramString1, paramString2, paramInt, paramString3, str1, paramString4);
        localARSharedResource = putInCache(localARSharedResource, paramString1, paramInt, str2);
      }
    }
    return localARSharedResource;
  }

  public static Collection<ARSharedResource> get(String paramString1, Collection<String> paramCollection, int paramInt, String paramString2, String paramString3)
    throws GoatException, ARException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
    ArrayList localArrayList1 = new ArrayList();
    String str1 = SessionData.get().getLocale();
    HashSet localHashSet = new HashSet(paramCollection.size());
    try
    {
      ArrayList localArrayList2 = new ArrayList(paramCollection.size());
      Object localObject1 = paramCollection.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        localObject3 = getCacheKey(paramString1, (String)localObject2, paramInt, paramString2, str1);
        localArrayList2.add(localObject3);
        synchronized (localObject3)
        {
          try
          {
            while (MResourceLocks.contains(localObject3))
              localObject3.wait();
            MResourceLocks.add(localObject3);
            localHashSet.add(localObject3);
          }
          catch (InterruptedException localInterruptedException)
          {
            localInterruptedException.printStackTrace();
          }
        }
      }
      localObject1 = new ArrayList();
      Object localObject2 = getFromCache(localArrayList2);
      Object localObject3 = paramCollection.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        ??? = (String)((Iterator)localObject3).next();
        String str2 = getCacheKey(paramString1, (String)???, paramInt, paramString2, str1);
        ARSharedResource localARSharedResource = (ARSharedResource)((Map)localObject2).get(str2);
        if (localARSharedResource != null)
        {
          localArrayList1.add(localARSharedResource);
          localHashSet.remove(str2);
          synchronized (str2)
          {
            MResourceLocks.remove(str2);
            str2.notifyAll();
          }
        }
        else
        {
          ??? = new ARSharedResource(paramString1, (String)???, paramInt, paramString2, str1, paramString3, true);
          ((List)localObject1).add(???);
        }
      }
      initResourceData(localServerLogin, (List)localObject1);
      localObject3 = ((List)localObject1).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        ??? = (ARSharedResource)((Iterator)localObject3).next();
        ??? = putInCache((ARSharedResource)???, paramString1, paramInt, ((ARSharedResource)???).getCacheKey());
        if (??? != null)
          localArrayList1.add(???);
      }
    }
    finally
    {
      Iterator localIterator = localHashSet.iterator();
      while (localIterator.hasNext())
      {
        String str3 = (String)localIterator.next();
        synchronized (str3)
        {
          MResourceLocks.remove(str3);
          str3.notifyAll();
        }
      }
    }
    return localArrayList1;
  }

  private static ARSharedResource putInCache(ARSharedResource paramARSharedResource, String paramString1, int paramInt, String paramString2)
  {
    if (paramARSharedResource.getResourceObject() == null)
      return null;
    MResourceCache.put(paramString2, paramARSharedResource);
    if (paramInt == 0)
    {
      Object localObject = (List)MTemplateKeyMap.get(paramString1);
      if (localObject == null)
      {
        localObject = new ArrayList();
        MTemplateKeyMap.put(paramString1, localObject);
      }
      ((List)localObject).add(paramString2);
    }
    return paramARSharedResource;
  }

  public static ARSharedResource getFromCache(String paramString)
  {
    ARSharedResource localARSharedResource = null;
    localARSharedResource = (ARSharedResource)MResourceCache.get(paramString, ARSharedResource.class);
    if (localARSharedResource != null)
    {
      long l = System.currentTimeMillis() / 1000L;
      if ((l - localARSharedResource.getLastChecked() > Configuration.getInstance().getCacheUpdateInterval()) && (localARSharedResource.isResourceUpdated(false)))
        localARSharedResource = null;
    }
    return localARSharedResource;
  }

  public static Map<String, ARSharedResource> getFromCache(List<String> paramList)
  {
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = paramList.iterator();
    Object localObject2;
    ARSharedResource localARSharedResource;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (String)((Iterator)localObject1).next();
      localARSharedResource = (ARSharedResource)MResourceCache.get((String)localObject2, ARSharedResource.class);
      if (localARSharedResource != null)
      {
        long l = System.currentTimeMillis() / 1000L;
        if (l - localARSharedResource.getLastChecked() > Configuration.getInstance().getCacheUpdateInterval())
          localArrayList.add(localARSharedResource);
        else
          localHashMap.put(localObject2, localARSharedResource);
      }
    }
    if (!localArrayList.isEmpty())
    {
      localObject1 = getUpdatedResources(localArrayList, false);
      localObject2 = localArrayList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localARSharedResource = (ARSharedResource)((Iterator)localObject2).next();
        String str = localARSharedResource.getCacheKey();
        if (!((Map)localObject1).containsKey(str))
          localHashMap.put(str, localARSharedResource);
      }
    }
    return localHashMap;
  }

  public static Map<String, ARSharedResource> getUpdatedResources(List<ARSharedResource> paramList, boolean paramBoolean)
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    Object localObject1 = paramList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (ARSharedResource)((Iterator)localObject1).next();
      localHashMap2.put(((ARSharedResource)localObject2).getName(), localObject2);
    }
    localObject1 = (ARSharedResource)paramList.get(0);
    Object localObject2 = ((ARSharedResource)localObject1).getServer();
    String str1 = ((ARSharedResource)localObject1).getLocale();
    Object localObject4;
    try
    {
      ServerLogin localServerLogin = null;
      if (paramBoolean)
        localServerLogin = ServerLogin.getAdmin((String)localObject2, str1);
      else
        localServerLogin = SessionData.get().getServerLogin((String)localObject2);
      QualifierInfo localQualifierInfo = createQual(paramList, true);
      initSchemaKey(paramList);
      localObject3 = localServerLogin.getListEntryObjects(((ARSharedResource)localObject1).mSchemaKey, localQualifierInfo, 0, 0, new ArrayList(), BULK_TIME_FIELDS_TO_RETRIEVE, true, null);
      localObject4 = ((List)localObject3).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        Entry localEntry = (Entry)((Iterator)localObject4).next();
        String str2 = ((Value)localEntry.get(Integer.valueOf(41100))).getValue().toString();
        ARSharedResource localARSharedResource = (ARSharedResource)localHashMap2.get(str2);
        if (localARSharedResource != null)
          localHashMap1.put(localARSharedResource.getCacheKey(), localARSharedResource);
      }
    }
    catch (ARException localARException)
    {
      MLog.severe(localARException.getLocalizedMessage());
    }
    catch (GoatException localGoatException)
    {
      MLog.severe(localGoatException.getLocalizedMessage());
    }
    long l = System.currentTimeMillis() / 1000L;
    Object localObject3 = paramList.iterator();
    while (((Iterator)localObject3).hasNext())
    {
      localObject4 = (ARSharedResource)((Iterator)localObject3).next();
      ((ARSharedResource)localObject4).mLastChecked = l;
    }
    return localHashMap1;
  }

  private boolean isResourceDeletedorModified()
  {
    boolean bool = false;
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(this.mServer, this.mLocale);
      QualifierInfo localQualifierInfo = this.mQual != null ? this.mQual : createQual();
      if (this.mSchemaKey == null)
        this.mSchemaKey = getResourceSchemaKey();
      List localList = localServerLogin.getListEntryObjects(this.mSchemaKey, localQualifierInfo, 0, 1, new ArrayList(), TIME_FIELDS_TO_RETRIEVE, true, null);
      if ((localList == null) || (localList.size() <= 0) || (localList.get(0) == null))
      {
        bool = true;
      }
      else
      {
        Value localValue = (Value)((Entry)localList.get(0)).get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()));
        long l = 0L;
        if (localValue != null)
          l = ((Timestamp)localValue.getValue()).getValue();
        if (l > this.mLastModified)
          bool = true;
      }
    }
    catch (ARException localARException)
    {
      MLog.severe(localARException.getLocalizedMessage());
    }
    catch (GoatException localGoatException)
    {
      MLog.severe(localGoatException.getLocalizedMessage());
    }
    this.mLastChecked = (System.currentTimeMillis() / 1000L);
    return bool;
  }

  public boolean isResourceUpdated(boolean paramBoolean)
  {
    boolean bool = true;
    try
    {
      ServerLogin localServerLogin = null;
      if (paramBoolean)
        localServerLogin = ServerLogin.getAdmin(this.mServer, this.mLocale);
      else
        localServerLogin = SessionData.get().getServerLogin(this.mServer);
      QualifierInfo localQualifierInfo = createQual(this.mLastModified);
      if (this.mSchemaKey == null)
        this.mSchemaKey = getResourceSchemaKey();
      List localList = localServerLogin.getListEntryObjects(this.mSchemaKey, localQualifierInfo, 0, 1, new ArrayList(), TIME_FIELDS_TO_RETRIEVE, true, null);
      if ((localList == null) || (localList.size() <= 0))
        bool = false;
    }
    catch (ARException localARException)
    {
      MLog.severe(localARException.getLocalizedMessage());
    }
    catch (GoatException localGoatException)
    {
      MLog.severe(localGoatException.getLocalizedMessage());
    }
    this.mLastChecked = (System.currentTimeMillis() / 1000L);
    return bool;
  }

  String getResourceSchemaKey()
    throws GoatException, ARException
  {
    return SchemaKeyFactory.getInstance().getSchemaKey(ServerLogin.getAdmin(this.mServer), RESOURCE_SCHEMA_IDS);
  }

  private byte[] getResourceData(ServerLogin paramServerLogin)
    throws GoatException
  {
    byte[] arrayOfByte = null;
    try
    {
      if (this.mSchemaKey == null)
        this.mSchemaKey = getResourceSchemaKey();
      List localList = paramServerLogin.getListEntryObjects(this.mSchemaKey, createQual(), 0, 1, new ArrayList(), FIELDS_TO_RETRIEVE, true, null);
      if ((localList == null) || (localList.size() <= 0))
        throw new GoatException("TemplateObject not found");
      Entry localEntry = (Entry)localList.get(0);
      arrayOfByte = getResourceData(paramServerLogin, localEntry);
    }
    catch (ARException localARException)
    {
      MLog.severe(localARException.getMessage());
    }
    return arrayOfByte;
  }

  private byte[] getResourceData(ServerLogin paramServerLogin, Entry paramEntry)
    throws GoatException, ARException
  {
    byte[] arrayOfByte = null;
    assert (paramEntry != null);
    assert (this.mName.equals(((Value)paramEntry.get(Integer.valueOf(41100))).getValue().toString()));
    AttachmentValue localAttachmentValue = (AttachmentValue)((Value)paramEntry.get(Integer.valueOf(41103))).getValue();
    if (localAttachmentValue != null)
      arrayOfByte = paramServerLogin.getEntryBlob(this.mSchemaKey, paramEntry.getEntryId(), 41103);
    Value localValue = (Value)paramEntry.get(Integer.valueOf(41107));
    Object localObject = null;
    if (localValue != null)
      localObject = ((Value)paramEntry.get(Integer.valueOf(41107))).getValue();
    if (localObject != null)
      this.mMimeType = localObject.toString();
    this.mLastChecked = (System.currentTimeMillis() / 1000L);
    localValue = (Value)paramEntry.get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()));
    this.mLastModified = 0L;
    if (localValue != null)
      this.mLastModified = ((Timestamp)localValue.getValue()).getValue();
    if (this.mType == 0)
    {
      Long localLong = (Long)LastModifiedTemplateTimeMap.get(this.mServer);
      if (localLong == null)
        localLong = new Long(0L);
      if (localLong.longValue() < this.mLastModified)
        LastModifiedTemplateTimeMap.put(this.mServer, new Long(this.mLastModified));
    }
    return arrayOfByte;
  }

  private static void initSchemaKey(List<ARSharedResource> paramList)
    throws GoatException, ARException
  {
    int i = paramList.size();
    ARSharedResource localARSharedResource1 = (ARSharedResource)paramList.get(0);
    if (localARSharedResource1.mSchemaKey == null)
      localARSharedResource1.mSchemaKey = localARSharedResource1.getResourceSchemaKey();
    for (int j = 1; j < i; j++)
    {
      ARSharedResource localARSharedResource2 = (ARSharedResource)paramList.get(j);
      if (localARSharedResource2.mSchemaKey == null)
        localARSharedResource2.mSchemaKey = localARSharedResource1.mSchemaKey;
    }
  }

  private static void initResourceData(ServerLogin paramServerLogin, List<ARSharedResource> paramList)
    throws GoatException, ARException
  {
    try
    {
      int i = paramList.size();
      if (i == 0)
        return;
      ARSharedResource localARSharedResource1 = (ARSharedResource)paramList.get(0);
      HashMap localHashMap = new HashMap();
      Object localObject1 = paramList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ARSharedResource)((Iterator)localObject1).next();
        localHashMap.put(((ARSharedResource)localObject2).getName(), localObject2);
      }
      initSchemaKey(paramList);
      localObject1 = createQual(paramList, false);
      Object localObject2 = paramServerLogin.getListEntryObjects(localARSharedResource1.mSchemaKey, (QualifierInfo)localObject1, 0, 0, new ArrayList(), FIELDS_TO_RETRIEVE, true, null);
      if ((localObject2 == null) || (((List)localObject2).isEmpty()))
        return;
      Iterator localIterator = ((List)localObject2).iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        String str = ((Value)localEntry.get(Integer.valueOf(41100))).getValue().toString();
        ARSharedResource localARSharedResource2 = (ARSharedResource)localHashMap.get(str);
        if (localARSharedResource2 != null)
        {
          byte[] arrayOfByte = localARSharedResource2.getResourceData(paramServerLogin, localEntry);
          localARSharedResource2.initFromData(paramServerLogin, arrayOfByte);
        }
      }
    }
    catch (ARException localARException)
    {
      MLog.severe(localARException.getMessage());
    }
  }

  private QualifierInfo createQual(long paramLong)
  {
    if (this.mQual == null)
      createQual();
    Timestamp localTimestamp = new Timestamp(paramLong);
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(localTimestamp));
    return new QualifierInfo(1, this.mQual, new QualifierInfo(new RelationalOperationInfo(2, MOD_DATE_OP, localArithmeticOrRelationalOperand)));
  }

  private QualifierInfo createQual()
  {
    assert (this.mName != null);
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(this.mName));
    Value localValue = new Value(Integer.valueOf(this.mType), DataType.ENUM);
    QualifierInfo localQualifierInfo1 = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41105), new ArithmeticOrRelationalOperand(localValue)));
    QualifierInfo localQualifierInfo2 = null;
    if ((this.mSubType != null) && (this.mSubType.length() > 0))
    {
      localObject = new Value(this.mSubType);
      localQualifierInfo2 = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41106), new ArithmeticOrRelationalOperand((Value)localObject)));
    }
    Object localObject = new QualifierInfo(1, STATUS_EQ_ACTIVE, new QualifierInfo(new RelationalOperationInfo(1, NAME_OPERAND, localArithmeticOrRelationalOperand)));
    QualifierInfo localQualifierInfo3 = localQualifierInfo1;
    if (localQualifierInfo2 != null)
      localQualifierInfo3 = new QualifierInfo(1, localQualifierInfo1, localQualifierInfo2);
    this.mQual = new QualifierInfo(1, (QualifierInfo)localObject, localQualifierInfo3);
    return this.mQual;
  }

  private static QualifierInfo createQual(List<ARSharedResource> paramList, boolean paramBoolean)
  {
    int i = paramList.size();
    ARSharedResource localARSharedResource1 = (ARSharedResource)paramList.get(0);
    QualifierInfo localQualifierInfo1 = null;
    if (i == 1)
    {
      if (!paramBoolean)
        localQualifierInfo1 = localARSharedResource1.createQual();
      else
        localQualifierInfo1 = localARSharedResource1.createQual(localARSharedResource1.mLastModified);
    }
    else if (i > 1)
    {
      localQualifierInfo1 = new QualifierInfo();
      localQualifierInfo1.setOperation(2);
      if (!paramBoolean)
        localQualifierInfo1.setLeftOperand(localARSharedResource1.createQual());
      else
        localQualifierInfo1.setLeftOperand(localARSharedResource1.createQual(localARSharedResource1.mLastModified));
      Object localObject = localQualifierInfo1;
      for (int j = 1; j < i; j++)
      {
        ARSharedResource localARSharedResource2 = (ARSharedResource)paramList.get(j);
        if (j + 1 == i)
        {
          if (!paramBoolean)
            ((QualifierInfo)localObject).setRightOperand(localARSharedResource2.createQual());
          else
            ((QualifierInfo)localObject).setRightOperand(localARSharedResource2.createQual(localARSharedResource2.mLastModified));
        }
        else
        {
          QualifierInfo localQualifierInfo2 = new QualifierInfo();
          localQualifierInfo2.setOperation(2);
          if (!paramBoolean)
            localQualifierInfo2.setLeftOperand(localARSharedResource2.createQual());
          else
            localQualifierInfo2.setLeftOperand(localARSharedResource2.createQual(localARSharedResource2.mLastModified));
          ((QualifierInfo)localObject).setRightOperand(localQualifierInfo2);
          localObject = localQualifierInfo2;
        }
      }
    }
    return localQualifierInfo1;
  }

  public String getServer()
  {
    return this.mServer;
  }

  public int getSize()
  {
    return 1;
  }

  public String getName()
  {
    return this.mName;
  }

  public int getType()
  {
    return this.mType;
  }

  public String getSubType()
  {
    return this.mSubType;
  }

  public String getLocale()
  {
    return this.mLocale;
  }

  public IResourceObject getResourceObject()
  {
    return this.mResource;
  }

  public byte[] getData()
  {
    return this.mData;
  }

  public String getMimeType()
  {
    return this.mMimeType;
  }

  public String getContextPath()
  {
    return this.mContextPath;
  }

  public long getLastChecked()
  {
    return this.mLastChecked;
  }

  public long getLastModified()
  {
    return this.mLastModified;
  }

  static
  {
    LastModifiedTemplateTimeMap = Collections.synchronizedMap(new HashMap());
    RESOURCE_SCHEMA_IDS = new int[] { 41100, 41103 };
    STATUS_ACTIVE = new Value(Long.valueOf(0L), DataType.ENUM);
    MLog = Log.get(7);
    NAME_OPERAND = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41100);
    STATUS_EQ_ACTIVE = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.Status.getFieldId()), new ArithmeticOrRelationalOperand(STATUS_ACTIVE)));
    MOD_DATE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.ModifiedDate.getFieldId());
    TEMPLATE_TYPE_QUAL = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41105), new ArithmeticOrRelationalOperand(new Value(Integer.valueOf(0), DataType.ENUM))));
    FIELDS_TO_RETRIEVE = new int[] { 41100, 41103, 41107, CoreFieldId.ModifiedDate.getFieldId() };
    TIME_FIELDS_TO_RETRIEVE = new int[] { CoreFieldId.ModifiedDate.getFieldId() };
    BULK_TIME_FIELDS_TO_RETRIEVE = new int[] { 41100, CoreFieldId.ModifiedDate.getFieldId() };
    MResourceCache = new Cachetable("SharedResourceList", 1);
    MResourceLocks = Collections.synchronizedSet(new HashSet());
    MTemplateKeyMap = Collections.synchronizedMap(new HashMap());
    DEFAULT_RESOURCE_FACTORY = new IResourceFactory()
    {
      public IResourceObject createFromData(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt, String paramAnonymousString3, byte[] paramAnonymousArrayOfByte, String paramAnonymousString4, String paramAnonymousString5, String paramAnonymousString6)
      {
        try
        {
          if (paramAnonymousArrayOfByte != null)
            return new DefaultResourceObject(paramAnonymousArrayOfByte, paramAnonymousString4);
        }
        catch (GoatException localGoatException)
        {
          ARSharedResource.MLog.severe("ARSharedResource Error - " + localGoatException.getMessage());
        }
        return null;
      }
    };
    MFactoryMap = new HashMap();
    MFactoryMap.put(new Integer(0), TemplateResourceFactory.getInstance());
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.sharedresource.ARSharedResource
 * JD-Core Version:    0.6.1
 */