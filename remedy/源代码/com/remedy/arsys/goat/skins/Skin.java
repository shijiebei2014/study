package com.remedy.arsys.goat.skins;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.cache.sync.GarbageCollector;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.Cachetable;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Skin
  implements Cache.Item
{
  private static final long serialVersionUID = -6415496811889174885L;
  private String mServer = null;
  private String mGUID = null;
  private Map<String, String> mPropIdMap = null;
  private Map<String, String> mPropTypeMap = null;
  private Map<String, String> mPropStyleMap = null;
  private Map<String, String> mSysPropMap = null;
  private Map<String, String> mDVMPropMap = null;
  private Map<String, String> mTemplatePropMap = null;
  private Map<String, String> mWebHeaderPropMap = null;
  private static String skinSchema = null;
  private static String skinPropSchema = null;
  private static Map<String, List<String>> mSkinPropKeyMap = new HashMap();
  private static Map<String, List<String>> mSkinDefKeyMap = new HashMap();
  private static Map<String, Long> LastModifiedSkinTimeMap = new HashMap();
  private static transient Log MLog = Log.get(7);
  private static transient Log cacheLog = Log.get(1);
  private long mLastChecked;
  private long mLastModified;
  private ArrayList<String> mFormList;
  private final QualifierInfo mQual = null;
  private static final QualifierInfo stateQual = new QualifierInfo(new RelationalOperationInfo(1, SkinFactory.SKIN_STATE_OP, new ArithmeticOrRelationalOperand(new Value(1))));
  private static final ArithmeticOrRelationalOperand MOD_DATE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.ModifiedDate.getFieldId());
  private static final int[] TIME_FIELDS_TO_RETRIEVE = { CoreFieldId.ModifiedDate.getFieldId() };

  public String getServer()
  {
    return this.mServer;
  }

  public void setMServer(String paramString)
  {
    this.mServer = paramString;
  }

  public String getCacheKey()
  {
    return this.mGUID;
  }

  public void setMGUID(String paramString)
  {
    this.mGUID = paramString;
  }

  public String getMServer()
  {
    return this.mServer;
  }

  public String getMGUID()
  {
    return this.mGUID;
  }

  public Map<String, String> getMPropIdMap()
  {
    return this.mPropIdMap;
  }

  public void setMPropIdMap(Map<String, String> paramMap)
  {
    this.mPropIdMap = paramMap;
  }

  public Map<String, String> getMPropTypeMap()
  {
    return this.mPropTypeMap;
  }

  public void setMPropTypeMap(Map<String, String> paramMap)
  {
    this.mPropTypeMap = paramMap;
  }

  public Map<String, String> getMPropStyleMap()
  {
    return this.mPropStyleMap;
  }

  public void setMPropStyleMap(Map<String, String> paramMap)
  {
    this.mPropStyleMap = paramMap;
  }

  public Map<String, String> getmSysPropMap()
  {
    return this.mSysPropMap;
  }

  public Map<String, String> getmDVMPropMap()
  {
    return this.mDVMPropMap;
  }

  public void setmDVMPropMap(Map<String, String> paramMap)
  {
    this.mDVMPropMap = paramMap;
  }

  public void setmSysPropMap(Map<String, String> paramMap)
  {
    this.mSysPropMap = paramMap;
  }

  public Map<String, String> getmTemplatePropMap()
  {
    return this.mTemplatePropMap;
  }

  public void setmTemplatePropMap(Map<String, String> paramMap)
  {
    this.mTemplatePropMap = paramMap;
  }

  public Map<String, String> getmWebHeaderPropMap()
  {
    return this.mWebHeaderPropMap;
  }

  public void setmWebHeaderPropMap(Map<String, String> paramMap)
  {
    this.mWebHeaderPropMap = paramMap;
  }

  public void putMPropIdMapEntry(String paramString1, String paramString2)
  {
    if (this.mPropIdMap == null)
      this.mPropIdMap = new HashMap();
    this.mPropIdMap.put(paramString1, paramString2);
  }

  public void putMPropStyleMapEntry(String paramString1, String paramString2)
  {
    if (this.mPropStyleMap == null)
      this.mPropStyleMap = new HashMap();
    this.mPropStyleMap.put(paramString1, paramString2);
  }

  public void putMPropTypeMapEntry(String paramString1, String paramString2)
  {
    if (this.mPropTypeMap == null)
      this.mPropTypeMap = new HashMap();
    this.mPropTypeMap.put(paramString1, paramString2);
  }

  public void putMSysPropMapEntry(String paramString1, String paramString2)
  {
    if (this.mSysPropMap == null)
      this.mSysPropMap = new HashMap();
    this.mSysPropMap.put(paramString1, paramString2);
  }

  public void putMDVMPropMapEntry(String paramString1, String paramString2)
  {
    if (this.mDVMPropMap == null)
      this.mDVMPropMap = new HashMap();
    this.mDVMPropMap.put(paramString1, paramString2);
  }

  public void putMTemplatePropMapEntry(String paramString1, String paramString2)
  {
    if (this.mTemplatePropMap == null)
      this.mTemplatePropMap = new HashMap();
    this.mTemplatePropMap.put(paramString1, paramString2);
  }

  public void putMWebHeaderPropMapEntry(String paramString1, String paramString2)
  {
    if (this.mWebHeaderPropMap == null)
      this.mWebHeaderPropMap = new HashMap();
    this.mWebHeaderPropMap.put(paramString1, paramString2);
  }

  public int getSize()
  {
    return 1;
  }

  private Value checkPropIdMap(String paramString, int paramInt)
  {
    if (this.mPropIdMap == null)
      return null;
    String str = (String)this.mPropIdMap.get(paramString + "/" + paramInt);
    if (str != null)
      return new Value(str);
    return null;
  }

  private Value checkPropStyleMap(String paramString, int paramInt)
  {
    if ((paramString == null) || (this.mPropStyleMap == null))
      return null;
    String str = (String)this.mPropStyleMap.get(paramString + "/" + paramInt);
    if (str != null)
      return new Value(str);
    return null;
  }

  private Value checkPropTypeMap(String paramString, int paramInt)
  {
    if (this.mPropTypeMap == null)
      return null;
    String str = (String)this.mPropTypeMap.get(paramString + "/" + paramInt);
    if (str != null)
      return new Value(str);
    return null;
  }

  private Value checkSysPropMap(String paramString, int paramInt)
  {
    if (this.mSysPropMap == null)
      return null;
    String str = (String)this.mSysPropMap.get(paramString + "/" + paramInt);
    if (str != null)
      return new Value(str);
    return null;
  }

  private Value checkDVMPropMap(String paramString1, String paramString2)
  {
    if (this.mDVMPropMap == null)
      return null;
    String str = (String)this.mDVMPropMap.get(paramString1 + "/" + paramString2);
    if (str != null)
      return new Value(str);
    return null;
  }

  private Value checkTemplatePropMap(String paramString1, String paramString2)
  {
    if (this.mTemplatePropMap == null)
      return null;
    String str = (String)this.mTemplatePropMap.get(paramString1 + "/" + paramString2);
    if (str != null)
      return new Value(str);
    return null;
  }

  private Value checkWebHeaderPropMap(String paramString1, String paramString2)
  {
    if (this.mWebHeaderPropMap == null)
      return null;
    String str = (String)this.mWebHeaderPropMap.get(paramString1 + "/" + paramString2);
    if (str != null)
      return new Value(str);
    return null;
  }

  public Value getPropertyvalue(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    Value localValue = checkPropIdMap(paramString1, paramInt);
    if (localValue == null)
      localValue = checkPropStyleMap(paramString3, paramInt);
    if (localValue == null)
      localValue = checkPropTypeMap(paramString2, paramInt);
    if (localValue == null)
      localValue = checkSysPropMap(paramString2, paramInt);
    return localValue;
  }

  public Value getPropertyvalue(String paramString1, String paramString2, short paramShort)
  {
    Value localValue = null;
    if (4 == paramShort)
      localValue = checkDVMPropMap(paramString1, paramString2);
    else if (5 == paramShort)
      localValue = checkTemplatePropMap(paramString1, paramString2);
    else if (6 == paramShort)
      localValue = checkWebHeaderPropMap(paramString1, paramString2);
    return localValue;
  }

  public static void addSkinKeyMap(String paramString1, String paramString2)
  {
    Object localObject = (List)mSkinPropKeyMap.get(paramString1);
    if (localObject == null)
    {
      localObject = new ArrayList();
      mSkinPropKeyMap.put(paramString1, localObject);
    }
    if (!((List)localObject).contains(paramString1 + "/" + paramString2))
      ((List)localObject).add(paramString1 + "/" + paramString2);
  }

  public static void addSkinDefKeyMap(String paramString1, String paramString2)
  {
    Object localObject = (List)mSkinDefKeyMap.get(paramString1);
    if (localObject == null)
    {
      localObject = new ArrayList();
      mSkinDefKeyMap.put(paramString1, localObject);
    }
    if (!((List)localObject).contains(paramString2))
      ((List)localObject).add(paramString2);
  }

  private static QualifierInfo createSkinWithModifiedQual(String paramString)
  {
    Long localLong = (Long)LastModifiedSkinTimeMap.get(paramString);
    Timestamp localTimestamp = new Timestamp(localLong == null ? 0L : localLong.longValue());
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(localTimestamp));
    return new QualifierInfo(new RelationalOperationInfo(3, MOD_DATE_OP, localArithmeticOrRelationalOperand));
  }

  public static void checkSkinCache(String paramString)
  {
    if (!Configuration.getInstance().getBooleanProperty("arsystem.enableSkins", false))
      return;
    cacheLog.fine("Entering checkSkinCache for server: " + paramString + "==============");
    List localList1 = (List)mSkinPropKeyMap.get(paramString);
    if ((localList1 == null) || (localList1.size() <= 0))
    {
      cacheLog.fine("No skin properties cache exist, checking on the skin def cache.");
      List localList2 = (List)mSkinDefKeyMap.get(paramString);
      if (localList2 != null)
      {
        localObject1 = (Cachetable)GoatCacheManager.getInstance().getGoatCache("Skin Definitions");
        mSkinDefKeyMap.remove(paramString);
        for (int j = 0; j < localList2.size(); j++)
        {
          String str1 = (String)localList2.get(j);
          cacheLog.fine("Skin def cache exist, remove it: " + str1);
          if (localObject1 != null)
            ((Cachetable)localObject1).remove(str1);
        }
        if (localObject1 != null)
          ((Cachetable)localObject1).save();
      }
      cacheLog.fine("Leaving checkSkinCache for server: " + paramString + "==============");
      return;
    }
    int i = 0;
    Object localObject1 = null;
    try
    {
      localObject1 = ServerLogin.getAdmin(paramString);
      if (skinSchema == null)
        skinSchema = SchemaKeyFactory.getInstance().getSchemaKey((ARServerUser)localObject1, SkinFactory.SKIN_SCHEMA_KEY);
      Long localLong = (Long)LastModifiedSkinTimeMap.get(paramString);
      long l1 = localLong == null ? 0L : localLong.longValue();
      List localList3 = ((ServerLogin)localObject1).getListEntryObjects(skinSchema, createSkinWithModifiedQual(paramString), 0, 0, new ArrayList(), TIME_FIELDS_TO_RETRIEVE, false, null);
      if ((localList3 != null) && (localList3.size() > 0))
      {
        for (int k = 0; k < localList3.size(); k++)
        {
          localObject2 = (Entry)localList3.get(k);
          if (localObject2 != null)
          {
            long l2 = 0L;
            Value localValue = (Value)((Entry)localObject2).get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()));
            if (localValue != null)
              l2 = ((Timestamp)localValue.getValue()).getValue();
            if (l1 < l2)
              l1 = l2;
          }
        }
        LastModifiedSkinTimeMap.put(paramString, new Long(l1));
        cacheLog.fine("something has been changed since last check, need to check the individual one.");
        ArrayList localArrayList = new ArrayList();
        Object localObject2 = new ArrayList();
        Cachetable localCachetable1 = (Cachetable)GoatCacheManager.getInstance().getGoatCache("Skin Properties");
        Cachetable localCachetable2 = (Cachetable)GoatCacheManager.getInstance().getGoatCache("Skin Definitions");
        if ((localCachetable1 == null) || (localCachetable2 == null))
          return;
        String str2;
        Object localObject4;
        for (int m = 0; m < localList1.size(); m++)
        {
          str2 = (String)localList1.get(m);
          synchronized (str2)
          {
            localObject4 = (Skin)localCachetable1.get(str2, Skin.class);
            if (localObject4 == null)
            {
              localArrayList.add(str2);
            }
            else if (((Skin)localObject4).isSkinDeletedorModified())
            {
              cacheLog.fine("Skinitem: " + str2 + "deleted or modified, removing it's mapping information from the cache.");
              localArrayList.add(str2);
              ((List)localObject2).addAll(((Skin)localObject4).getFormList());
            }
          }
        }
        Object localObject3 = localArrayList.iterator();
        while (((Iterator)localObject3).hasNext())
        {
          str2 = (String)((Iterator)localObject3).next();
          localCachetable1.remove(str2);
          localList1.remove(str2);
        }
        localCachetable1.save();
        localObject3 = (List)mSkinDefKeyMap.get(paramString);
        if (localObject3 != null)
        {
          mSkinDefKeyMap.remove(paramString);
          for (int n = 0; n < ((List)localObject3).size(); n++)
          {
            ??? = (String)((List)localObject3).get(n);
            localObject4 = ((String)???).split("/");
            if (localObject4[2] != null)
              ((List)localObject2).add(localObject4[2]);
            localCachetable2.remove((String)???);
          }
          localCachetable2.save();
        }
        GarbageCollector localGarbageCollector = new GarbageCollector(paramString, GoatCacheManager.getInstance());
        ??? = ((List)localObject2).iterator();
        while (((Iterator)???).hasNext())
        {
          localObject4 = (String)((Iterator)???).next();
          cacheLog.fine("Also marking for related form: " + (String)localObject4);
          localGarbageCollector.markFormRecursive((String)localObject4, false);
        }
        localGarbageCollector.sweep();
        cacheLog.fine("Leaving checkSkinCache for server: " + paramString + "==============");
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
  }

  private QualifierInfo createQual()
  {
    return new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SkinFactory.SKIN_GUID_OP, new ArithmeticOrRelationalOperand(new Value(this.mGUID)))), stateQual);
  }

  private boolean isSkinDeletedorModified()
  {
    boolean bool = false;
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(this.mServer);
      if (skinSchema == null)
        skinSchema = SchemaKeyFactory.getInstance().getSchemaKey(localServerLogin, SkinFactory.SKIN_SCHEMA_KEY);
      List localList = localServerLogin.getListEntryObjects(skinSchema, createQual(), 0, 1, new ArrayList(), TIME_FIELDS_TO_RETRIEVE, true, null);
      if ((localList == null) || (localList.size() <= 0) || (localList.get(0) == null))
      {
        bool = true;
      }
      else
      {
        long l = 0L;
        Entry localEntry = (Entry)localList.get(0);
        if (localEntry != null)
        {
          Value localValue = (Value)localEntry.get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()));
          if (localValue != null)
            l = ((Timestamp)localValue.getValue()).getValue();
        }
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

  protected void setLastModified(long paramLong)
  {
    this.mLastModified = paramLong;
  }

  protected void setFormList(ArrayList<String> paramArrayList)
  {
    this.mFormList = paramArrayList;
  }

  protected ArrayList<String> getFormList()
  {
    return this.mFormList;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.skins.Skin
 * JD-Core Version:    0.6.1
 */