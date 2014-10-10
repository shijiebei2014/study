package com.remedy.arsys.plugincontainer.impl;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
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
import com.remedy.arsys.plugincontainer.AuthenticationException;
import com.remedy.arsys.plugincontainer.CacheableDefinition;
import com.remedy.arsys.plugincontainer.DefaultDefinition;
import com.remedy.arsys.plugincontainer.Definition;
import com.remedy.arsys.plugincontainer.DefinitionFactory;
import com.remedy.arsys.plugincontainer.DefinitionKey;
import com.remedy.arsys.plugincontainer.DefinitionService;
import com.remedy.arsys.plugincontainer.NoPermissionException;
import com.remedy.arsys.plugincontainer.Plugin;
import com.remedy.arsys.plugincontainer.PluginContext;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.SchemaKeyFactory;
import com.remedy.arsys.support.SoftCache;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefinitionServiceImpl
  implements DefinitionService
{
  private static final int MAX_IN_MEM_SIZE = 4194304;
  private static final byte[] EMPTY_BUF;
  private static final File PLUGIN_DEFS_DIR;
  private static final int PLUGIN_NAME_FIELD_ID = 41000;
  private static final int PLUGIN_DEF_NAME_FIELD_ID = 41050;
  private static final int PLUGIN_DEF_TYPE_FIELD_ID = 41052;
  private static final int PLUGIN_DEF_SDATA_FIELD_ID = 41053;
  private static final int PLUGIN_DEF_BDATA_FIELD_ID = 41054;
  private static final Value STATUS_ACTIVE = new Value(Long.valueOf(0L), DataType.ENUM);
  private static final ArithmeticOrRelationalOperand PLUGIN_NAME_OPERAND = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41000);
  private static final ArithmeticOrRelationalOperand PLUGIN_DEF_NAME_OPERAND = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41050);
  private static final ArithmeticOrRelationalOperand PLUGIN_DEF_TYPE_OPERAND = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41052);
  private static final QualifierInfo STATUS_EQ_ACTIVE = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.Status.getFieldId()), new ArithmeticOrRelationalOperand(STATUS_ACTIVE)));
  private static final int[] DEF_FIELDS_TO_RETRIEVE = { CoreFieldId.ModifiedDate.getFieldId(), 41054 };
  private static final int[] PLUGIN_DEF_SCHEMA_IDS = { 41050, 41053, 41054 };
  private static final Map MCache = Collections.synchronizedMap(new HashMap());
  private static final DefinitionFactory DEFAULT_FACTORY = new DefinitionFactory()
  {
    public Definition createFromString(PluginContext paramAnonymousPluginContext, DefinitionKey paramAnonymousDefinitionKey, String paramAnonymousString)
    {
      return new DefaultDefinition(paramAnonymousString);
    }

    public Definition createFromStream(PluginContext paramAnonymousPluginContext, DefinitionKey paramAnonymousDefinitionKey, InputStream paramAnonymousInputStream)
    {
      return new DefaultDefinition(paramAnonymousInputStream);
    }
  };
  private PluginContextImpl mpci;
  private String mServer;
  private String mPluginName;
  private DefinitionFactory mFactory;

  public DefinitionServiceImpl(PluginContextImpl paramPluginContextImpl)
    throws GoatException
  {
    this.mpci = paramPluginContextImpl;
    this.mServer = paramPluginContextImpl.getServer();
    this.mPluginName = paramPluginContextImpl.getPluginName();
    DefinitionFactory localDefinitionFactory = paramPluginContextImpl.getPlugin().getDefinitionFactory();
    this.mFactory = (localDefinitionFactory == null ? DEFAULT_FACTORY : localDefinitionFactory);
  }

  public Definition[] getDefinitions(DefinitionKey[] paramArrayOfDefinitionKey)
    throws IOException, NoPermissionException
  {
    assert (paramArrayOfDefinitionKey != null);
    Definition[] arrayOfDefinition = new Definition[paramArrayOfDefinitionKey.length];
    try
    {
      Entry[] arrayOfEntry = getEntriesForPluginDefs(paramArrayOfDefinitionKey);
      assert (arrayOfEntry != null);
      DefRetrieveList localDefRetrieveList = new DefRetrieveList(arrayOfDefinition, null);
      for (int i = 0; i < arrayOfEntry.length; i++)
      {
        Entry localEntry = arrayOfEntry[i];
        if (localEntry.size() != DEF_FIELDS_TO_RETRIEVE.length + 1)
          throw new NoPermissionException(getNoDefLocalizedErrorMessage(new String[] { paramArrayOfDefinitionKey[i].toString() }));
        SoftCache localSoftCache = getPluginDefsCache();
        String str = getDefCacheLock(paramArrayOfDefinitionKey[i]);
        synchronized (str)
        {
          DefHolder localDefHolder = (DefHolder)localSoftCache.get(str);
          Value localValue1 = (Value)localEntry.get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()));
          long l = localValue1 != null ? ((Timestamp)localValue1.getValue()).getValue() : 0L;
          if ((localDefHolder == null) || (l > localDefHolder.mLastMod))
          {
            localSoftCache.remove(str);
            Value localValue2 = (Value)localEntry.get(Integer.valueOf(41054));
            AttachmentValue localAttachmentValue = (localValue2 != null) && (DataType.ATTACHMENT.equals(localValue2.getDataType())) ? (AttachmentValue)localValue2.getValue() : null;
            localDefRetrieveList.add(i, arrayOfEntry[i].getEntryId(), paramArrayOfDefinitionKey[i], l, localAttachmentValue);
          }
          else
          {
            arrayOfDefinition[i] = localDefHolder.mDef;
          }
        }
      }
      localDefRetrieveList.retrieve();
      return arrayOfDefinition;
    }
    catch (AuthenticationException localAuthenticationException)
    {
      throw new NoPermissionException(getNoDefLocalizedErrorMessage(paramArrayOfDefinitionKey), localAuthenticationException);
    }
    catch (ARException localARException)
    {
      throw new NoPermissionException(getNoDefLocalizedErrorMessage(paramArrayOfDefinitionKey), localARException);
    }
    catch (GoatException localGoatException)
    {
      throw new NoPermissionException(getNoDefLocalizedErrorMessage(paramArrayOfDefinitionKey), localGoatException);
    }
  }

  public Definition getDefinition(DefinitionKey paramDefinitionKey)
    throws IOException, NoPermissionException
  {
    Definition[] arrayOfDefinition = getDefinitions(new DefinitionKey[] { paramDefinitionKey });
    return arrayOfDefinition[0];
  }

  static void pluginRemoved(String paramString)
  {
    String str = getPluginLock(paramString);
    synchronized (str)
    {
      MCache.remove(str);
    }
  }

  private SoftCache getPluginDefsCache()
  {
    String str = getPluginLock(this.mPluginName);
    synchronized (str)
    {
      SoftCache localSoftCache = (SoftCache)MCache.get(str);
      if (localSoftCache == null)
      {
        localSoftCache = SoftCache.synchronizedCache(new SoftCache());
        MCache.put(str, localSoftCache);
      }
      return localSoftCache;
    }
  }

  private static String getPluginLock(String paramString)
  {
    return (paramString + ":PluginDefs").intern();
  }

  private String getDefCacheLock(DefinitionKey paramDefinitionKey)
  {
    return (paramDefinitionKey + this.mServer + ":PluginDefs").intern();
  }

  private String getSchema()
    throws ARException, GoatException
  {
    return SchemaKeyFactory.getInstance().getSchemaKey(ServerLogin.getAdmin(this.mServer), PLUGIN_DEF_SCHEMA_IDS);
  }

  private Entry[] getEntriesForPluginDefs(DefinitionKey[] paramArrayOfDefinitionKey)
    throws ARException, GoatException, AuthenticationException, NoPermissionException
  {
    ARServerUser localARServerUser = (ARServerUser)this.mpci.getServerUserObject();
    String str = getSchema();
    QualifierInfo localQualifierInfo = createQual(paramArrayOfDefinitionKey);
    List localList = localARServerUser.getListEntryObjects(str, localQualifierInfo, 0, 0, new ArrayList(), DEF_FIELDS_TO_RETRIEVE, false, null);
    if ((localList == null) || (localList.size() < 1))
      throw new NoPermissionException(getNoDefLocalizedErrorMessage(paramArrayOfDefinitionKey));
    return (Entry[])localList.toArray(new Entry[0]);
  }

  private QualifierInfo createQual(DefinitionKey[] paramArrayOfDefinitionKey)
  {
    QualifierInfo localQualifierInfo1 = createQual(paramArrayOfDefinitionKey[0]);
    for (int i = 1; i < paramArrayOfDefinitionKey.length; i++)
    {
      QualifierInfo localQualifierInfo2 = createQual(paramArrayOfDefinitionKey[i]);
      localQualifierInfo1 = new QualifierInfo(2, localQualifierInfo1, localQualifierInfo2);
    }
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(this.mPluginName));
    return new QualifierInfo(1, STATUS_EQ_ACTIVE, new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, PLUGIN_NAME_OPERAND, localArithmeticOrRelationalOperand)), localQualifierInfo1));
  }

  private QualifierInfo createQual(DefinitionKey paramDefinitionKey)
  {
    assert (this.mPluginName != null);
    assert (paramDefinitionKey != null);
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = new ArithmeticOrRelationalOperand(new Value(paramDefinitionKey.getName()));
    QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(1, PLUGIN_DEF_NAME_OPERAND, localArithmeticOrRelationalOperand1));
    if (paramDefinitionKey.getType() != null)
    {
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = new ArithmeticOrRelationalOperand(new Value(paramDefinitionKey.getType()));
      return new QualifierInfo(1, localQualifierInfo, new QualifierInfo(new RelationalOperationInfo(1, PLUGIN_DEF_TYPE_OPERAND, localArithmeticOrRelationalOperand2)));
    }
    return localQualifierInfo;
  }

  private Definition getDefinitionFromAttachment(String paramString, DefinitionKey paramDefinitionKey, AttachmentValue paramAttachmentValue)
    throws IOException, ARException, GoatException, AuthenticationException
  {
    ARServerUser localARServerUser = (ARServerUser)this.mpci.getServerUserObject();
    try
    {
      if (paramAttachmentValue.getOriginalSize() < 4194304L)
      {
        paramAttachmentValue.setValue(EMPTY_BUF);
        byte[] arrayOfByte = localARServerUser.getEntryBlob(getSchema(), paramString, 41054);
        return this.mFactory.createFromStream(this.mpci, paramDefinitionKey, new ByteArrayInputStream(arrayOfByte));
      }
    }
    catch (ARException localARException)
    {
    }
    BufferedInputStream localBufferedInputStream = null;
    FileInputStream localFileInputStream = null;
    File localFile = null;
    try
    {
      localFile = File.createTempFile("def", null, PLUGIN_DEFS_DIR);
      paramAttachmentValue.setValue(localFile.getCanonicalPath());
      ((ARServerUser)this.mpci.getServerUserObject()).getEntryBlob(getSchema(), paramString, 41054, localFile.getCanonicalPath());
      localFileInputStream = new FileInputStream(localFile);
      localBufferedInputStream = new BufferedInputStream(localFileInputStream);
      Definition localDefinition = this.mFactory.createFromStream(this.mpci, paramDefinitionKey, localBufferedInputStream);
      return localDefinition;
    }
    finally
    {
      try
      {
        if ((localBufferedInputStream != null) && (localFileInputStream != null))
        {
          localBufferedInputStream.close();
          localFileInputStream.close();
        }
      }
      catch (IOException localIOException)
      {
      }
      if (localFile != null)
        localFile.delete();
    }
  }

  private void getAttachmentDefs(Definition[] paramArrayOfDefinition, List paramList)
    throws ARException, IOException, GoatException, AuthenticationException
  {
    assert ((paramArrayOfDefinition != null) && (paramList != null));
  }

  private String getNoDefLocalizedErrorMessage(Object[] paramArrayOfObject)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfObject.length; i++)
      localArrayList.add(paramArrayOfObject[i]);
    String[] arrayOfString = { SessionData.get().getUserName(), localArrayList.toString(), this.mPluginName, this.mServer };
    return new GoatException(9390, arrayOfString).toString();
  }

  static
  {
    EMPTY_BUF = new byte[0];
    PLUGIN_DEFS_DIR = new File(Configuration.getInstance().getRootPath() + "/PluginDefsCache");
    PLUGIN_DEFS_DIR.mkdirs();
  }

  private class DefRetrieveList
  {
    private List mRetrieveList = new ArrayList();
    private Definition[] mRetrieveInto;
    private List mStrDefInfos = new ArrayList();

    private DefRetrieveList(Definition[] arg2)
    {
      Object localObject;
      assert (localObject != null);
      this.mRetrieveInto = localObject;
    }

    private void add(int paramInt, String paramString, DefinitionKey paramDefinitionKey, long paramLong, AttachmentValue paramAttachmentValue)
    {
      assert ((this.mRetrieveInto[paramInt] == null) && (paramInt >= 0) && (paramString != null));
      DefRetrieveInfo localDefRetrieveInfo = new DefRetrieveInfo(paramInt, paramString, paramDefinitionKey, paramLong, paramAttachmentValue, null);
      if (paramAttachmentValue == null)
        this.mStrDefInfos.add(localDefRetrieveInfo);
      this.mRetrieveList.add(localDefRetrieveInfo);
    }

    private void retrieve()
      throws ARException, GoatException, IOException, AuthenticationException, NoPermissionException
    {
      retrieveStringDefs();
      Iterator localIterator = this.mRetrieveList.iterator();
      SoftCache localSoftCache = DefinitionServiceImpl.this.getPluginDefsCache();
      while (localIterator.hasNext())
      {
        DefRetrieveInfo localDefRetrieveInfo = (DefRetrieveInfo)localIterator.next();
        String str = DefinitionServiceImpl.this.getDefCacheLock(localDefRetrieveInfo.mKey);
        synchronized (str)
        {
          DefinitionServiceImpl.DefHolder localDefHolder = (DefinitionServiceImpl.DefHolder)localSoftCache.get(str);
          if ((localDefHolder == null) || (DefinitionServiceImpl.DefHolder.access$100(localDefHolder) < localDefRetrieveInfo.mLastMod))
          {
            Definition localDefinition = localDefRetrieveInfo.getDefinition();
            assert (this.mRetrieveInto[localDefRetrieveInfo.mRetIndex] == null);
            this.mRetrieveInto[localDefRetrieveInfo.mRetIndex] = localDefinition;
            if ((localDefinition instanceof CacheableDefinition))
              localSoftCache.put(str, new DefinitionServiceImpl.DefHolder(localDefinition, localDefRetrieveInfo.mLastMod, null));
          }
          else
          {
            this.mRetrieveInto[localDefRetrieveInfo.mRetIndex] = DefinitionServiceImpl.DefHolder.access$300(localDefHolder);
          }
        }
      }
    }

    private void retrieveStringDefs()
      throws ARException, GoatException, AuthenticationException, NoPermissionException
    {
      if (this.mStrDefInfos.size() > 0)
      {
        ArrayList localArrayList = new ArrayList();
        Object localObject1 = this.mStrDefInfos.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          Object localObject2 = ((Iterator)localObject1).next();
          localArrayList.add(((DefRetrieveInfo)localObject2).mEntryID);
        }
        localObject1 = ((ARServerUser)DefinitionServiceImpl.this.mpci.getServerUserObject()).getListEntryObjects(DefinitionServiceImpl.this.getSchema(), localArrayList, new int[] { 41053 });
        if ((localObject1 == null) || (((List)localObject1).size() != localArrayList.size()))
          throw new NoPermissionException(DefinitionServiceImpl.this.getNoDefLocalizedErrorMessage(this.mStrDefInfos.toArray()));
        int i = 0;
        Iterator localIterator = this.mStrDefInfos.iterator();
        while (localIterator.hasNext())
        {
          DefRetrieveInfo localDefRetrieveInfo = (DefRetrieveInfo)localIterator.next();
          Entry localEntry = (Entry)((List)localObject1).get(i++);
          assert (localDefRetrieveInfo.mEntryID.equals(localEntry.get(Integer.valueOf(CoreFieldId.EntryId.getFieldId()))));
          Value localValue = (Value)localEntry.get(Integer.valueOf(41053));
          localDefRetrieveInfo.updateStrDef(localValue != null ? localValue.toString() : null);
        }
      }
    }

    private class DefRetrieveInfo
    {
      private int mRetIndex;
      private AttachmentValue mAtt;
      private String mEntryID;
      private DefinitionKey mKey;
      private long mLastMod;
      private String mStr;

      private DefRetrieveInfo(int paramString, String paramDefinitionKey, DefinitionKey paramLong, long arg5, AttachmentValue arg7)
      {
        this.mRetIndex = paramString;
        this.mEntryID = paramDefinitionKey;
        this.mKey = paramLong;
        this.mLastMod = ???;
        Object localObject;
        this.mAtt = localObject;
      }

      private void updateStrDef(String paramString)
      {
        assert ((this.mAtt == null) && (this.mStr == null));
        this.mStr = paramString;
      }

      private Definition getDefinition()
        throws ARException, AuthenticationException, GoatException, IOException
      {
        if (this.mAtt != null)
          return DefinitionServiceImpl.this.getDefinitionFromAttachment(this.mEntryID, this.mKey, this.mAtt);
        return DefinitionServiceImpl.this.mFactory.createFromString(DefinitionServiceImpl.this.mpci, this.mKey, this.mStr);
      }

      public String toString()
      {
        return "eid:" + this.mEntryID + " key:" + this.mKey;
      }
    }
  }

  private static class DefHolder
  {
    private Definition mDef;
    private long mLastMod;

    private DefHolder(Definition paramDefinition, long paramLong)
    {
      this.mDef = paramDefinition;
      this.mLastMod = paramLong;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.DefinitionServiceImpl
 * JD-Core Version:    0.6.1
 */