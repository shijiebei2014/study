package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.AttachmentFieldLimit;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.ibm.icu.util.StringTokenizer;
import com.remedy.arsys.backchannel.NDXRequest.Parser;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.stubs.SessionData.SessionDataEventListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AttachmentData
  implements Serializable
{
  private static final long serialVersionUID = 7801189734251548354L;
  private static final boolean DEBUG = false;
  protected static final transient Log arLog;
  private static final int MAX_MEM_SIZE;
  private static final File ATT_STORE;
  private static final long ATTACHMENT_INMEMORY_HEAPSIZE_MAX;
  private static final double ATTACHMENT_INMEMORY_HEAPSIZE_RESIZE_RATIO;
  private static long ATTACHMENT_INMEMORY_HEAPSIZE;
  private static long ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED;
  private static final Lock ATTACHMENT_CACHE_LOCK;
  private static final String NO_SCREEN_KEY = "0";
  private static final String WITH_SCREEN_KEY = "1";
  private static final byte[] EMPTY_BUF;
  private static final Map<String, AttachmentDataCache> MAttCaches;
  private static final Map<AttachmentDataKey, AttachmentDataKey> ATTACHMENT_DATA_KEY_INMEMORY_MAP;
  private byte[] mAttData;
  private File mAttFile;
  private String mName;
  private boolean mBusy;
  public static final String FILENAME_SEPARATOR = ",";
  private static final String DISPLAY = "1";
  private static final String DOWNLOAD = "2";

  private static final void checkAndMakeAttStoreDir()
  {
    if (!ATT_STORE.exists())
      ATT_STORE.mkdir();
  }

  private AttachmentData()
  {
  }

  private AttachmentData(String paramString, byte[] paramArrayOfByte)
  {
    this.mName = paramString;
    this.mAttData = paramArrayOfByte;
    this.mBusy = false;
  }

  private AttachmentData(String paramString, File paramFile)
  {
    this.mName = paramString;
    this.mAttFile = paramFile;
  }

  public static AttachmentDataKey uploadToPool(String paramString1, String paramString2, String paramString3, int paramInt, int[] paramArrayOfInt, String paramString4, String paramString5, String paramString6, InputStream paramInputStream)
    throws IOException, GoatException
  {
    assert ((paramString1 != null) && (paramString2 != null) && (paramArrayOfInt != null) && (paramString4 != null) && (paramString6 != null) && (paramInputStream != null));
    long l1 = 0L;
    CachedFieldMap localCachedFieldMap = Form.get(paramString1, paramString2).getCachedFieldMap();
    Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(paramInt));
    if ((localField == null) || (DataType.ATTACHMENT_POOL.toInt() != localField.getDataType()))
      throw new GoatException("Invalid fieldid used for uploading attachment pool data. ");
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    ServerInfo localServerInfo = ServerInfo.get(paramString1, true);
    long l2 = localServerInfo.getMaxAttachSize();
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      localField = (Field)localCachedFieldMap.get(Integer.valueOf(paramArrayOfInt[i]));
      assert (localField != null);
      if ((localField == null) || (DataType.ATTACHMENT.toInt() != localField.getDataType()))
        throw new GoatException("Invalid fieldid used for uploading attachment pool data. ");
      l3 = ((AttachmentFieldLimit)localField.getFieldLimit()).getMaxSize();
      if ((l2 != 0L) && ((l3 == 0L) || (l3 > l2)))
        l3 = l2;
      if (l3 == 0L)
        l1 = l3 = 9223372036854775807L;
      else if (l3 > l1)
        l1 = l3;
      arrayOfLong[i] = l3;
    }
    AttachmentData localAttachmentData = getData(l1, paramString6, paramInputStream, true);
    long l3 = localAttachmentData.getSize();
    for (int j = 0; j < arrayOfLong.length; j++)
      if (l3 <= arrayOfLong[j])
      {
        AttachmentDataKey localAttachmentDataKey = new AttachmentDataKey(paramString1, paramString2, paramString3, paramArrayOfInt[j], paramString4, paramString5);
        getOrCreateCache(paramString4, paramString5).add(localAttachmentDataKey, localAttachmentData);
        return localAttachmentDataKey;
      }
    if (localAttachmentData.mAttData != null)
      decrementInMemoryReservedHeapSize(l3);
    throw new GoatException(9209);
  }

  public static AttachmentData uploadToField(AttachmentDataKey paramAttachmentDataKey, String paramString, InputStream paramInputStream)
    throws IOException, GoatException
  {
    assert (paramAttachmentDataKey.mScreenName != null);
    Field localField = (Field)Form.get(paramAttachmentDataKey.mServer, paramAttachmentDataKey.mSchema).getCachedFieldMap().get(Integer.valueOf(paramAttachmentDataKey.mFieldID));
    if ((localField == null) || (DataType.ATTACHMENT.toInt() != localField.getDataType()))
      throw new GoatException("Invalid fieldid used for uploading attachmetn data. Key = " + paramAttachmentDataKey);
    long l1 = ((AttachmentFieldLimit)localField.getFieldLimit()).getMaxSize();
    ServerInfo localServerInfo = ServerInfo.get(paramAttachmentDataKey.mServer, true);
    long l2 = localServerInfo.getMaxAttachSize();
    if ((l2 != 0L) && ((l1 == 0L) || (l1 > l2)))
      l1 = l2;
    AttachmentData localAttachmentData = getData(l1, paramString, paramInputStream, false);
    getOrCreateCache(paramAttachmentDataKey.mScreenName, paramAttachmentDataKey.mSessionID).add(paramAttachmentDataKey, localAttachmentData);
    return localAttachmentData;
  }

  private static final boolean tryReserveInMemoryHeapSize(long paramLong)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      if (paramLong > ATTACHMENT_INMEMORY_HEAPSIZE_MAX)
      {
        boolean bool1 = false;
        return bool1;
      }
      long l1 = getAvailHeapSize();
      if (paramLong <= l1)
      {
        incrementInMemoryReservedHeapSize(paramLong);
        boolean bool2 = true;
        return bool2;
      }
      long l2 = ATTACHMENT_INMEMORY_HEAPSIZE_MAX - ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED;
      if (paramLong > l2)
      {
        boolean bool3 = false;
        return bool3;
      }
      long l3 = ()(ATTACHMENT_INMEMORY_HEAPSIZE_MAX * ATTACHMENT_INMEMORY_HEAPSIZE_RESIZE_RATIO) - ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED - paramLong;
      if (l3 < 0L)
        l3 = 0L;
      long l4 = ATTACHMENT_INMEMORY_HEAPSIZE - l3;
      if (l4 <= 0L)
      {
        incrementInMemoryReservedHeapSize(paramLong);
        boolean bool4 = true;
        return bool4;
      }
      Collection localCollection = ATTACHMENT_DATA_KEY_INMEMORY_MAP.values();
      AttachmentDataKey[] arrayOfAttachmentDataKey = (AttachmentDataKey[])localCollection.toArray(new AttachmentDataKey[0]);
      Arrays.sort(arrayOfAttachmentDataKey, new Comparator()
      {
        public final int compare(AttachmentData.AttachmentDataKey paramAnonymousAttachmentDataKey1, AttachmentData.AttachmentDataKey paramAnonymousAttachmentDataKey2)
        {
          long l = paramAnonymousAttachmentDataKey1.getLastAccessed() - paramAnonymousAttachmentDataKey2.getLastAccessed();
          if (l < 0L)
            return -1;
          if (l > 0L)
            return 1;
          return 0;
        }
      });
      int i = arrayOfAttachmentDataKey.length;
      for (int j = 0; (j < i) && (l4 > 0L); j++)
      {
        AttachmentDataKey localAttachmentDataKey = arrayOfAttachmentDataKey[j];
        String str1 = localAttachmentDataKey.getScreenName();
        String str2 = localAttachmentDataKey.getSessionID();
        AttachmentDataCache localAttachmentDataCache = getCache(str1, str2);
        AttachmentData localAttachmentData = null;
        if (localAttachmentDataCache != null)
          localAttachmentData = localAttachmentDataCache.get(localAttachmentDataKey, false);
        try
        {
          long l5 = 0L;
          if (localAttachmentData != null)
          {
            l5 = localAttachmentData.getSize();
            moveAttachmentToDisk(localAttachmentDataKey, localAttachmentData);
            l4 -= l5;
          }
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace();
          break;
        }
      }
      l1 = getAvailHeapSize();
      if (paramLong <= l1)
      {
        incrementInMemoryReservedHeapSize(paramLong);
        j = 1;
        return j;
      }
      boolean bool5 = false;
      return bool5;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final long getAvailHeapSize()
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      long l = ATTACHMENT_INMEMORY_HEAPSIZE_MAX - ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED - ATTACHMENT_INMEMORY_HEAPSIZE;
      return l;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void moveAttachmentToDisk(AttachmentDataKey paramAttachmentDataKey, AttachmentData paramAttachmentData)
    throws IOException
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      if (paramAttachmentData.mAttData == null)
      {
        arLog.log(Level.WARNING, "Skipping an attachment to be written on disk with mAttData=null");
        ATTACHMENT_DATA_KEY_INMEMORY_MAP.remove(paramAttachmentDataKey);
        return;
      }
      checkAndMakeAttStoreDir();
      File localFile = File.createTempFile("att", "data", ATT_STORE);
      FileOutputStream localFileOutputStream = null;
      try
      {
        localFileOutputStream = new FileOutputStream(localFile);
        localFileOutputStream.write(paramAttachmentData.mAttData);
      }
      finally
      {
        if (localFileOutputStream != null)
          localFileOutputStream.close();
      }
      ATTACHMENT_DATA_KEY_INMEMORY_MAP.remove(paramAttachmentDataKey);
      long l = paramAttachmentData.getSize();
      decrementInMemoryHeapSize(l);
      paramAttachmentData.mAttData = null;
      paramAttachmentData.mAttFile = localFile;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void moveAttachmentToMemory(AttachmentDataKey paramAttachmentDataKey, AttachmentData paramAttachmentData)
    throws IOException
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      long l = paramAttachmentData.getSize();
      paramAttachmentData.mAttData = new byte[(int)l];
      File localFile = paramAttachmentData.mAttFile;
      FileInputStream localFileInputStream = null;
      try
      {
        localFileInputStream = new FileInputStream(localFile);
        localFileInputStream.read(paramAttachmentData.mAttData);
      }
      finally
      {
        if (localFileInputStream != null)
          localFileInputStream.close();
      }
      setLastAccessed(paramAttachmentDataKey);
      decrementInMemoryReservedHeapSize(l);
      incrementInMemoryHeapSize(l);
      paramAttachmentData.mAttFile.delete();
      paramAttachmentData.mAttFile = null;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void setLastAccessed(AttachmentDataKey paramAttachmentDataKey)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      paramAttachmentDataKey.setLastAccessed(new Date().getTime());
      ATTACHMENT_DATA_KEY_INMEMORY_MAP.put(paramAttachmentDataKey, paramAttachmentDataKey);
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void incrementInMemoryReservedHeapSize(long paramLong)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED += paramLong;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void decrementInMemoryReservedHeapSize(long paramLong)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED -= paramLong;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void incrementInMemoryHeapSize(long paramLong)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      ATTACHMENT_INMEMORY_HEAPSIZE += paramLong;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static final void decrementInMemoryHeapSize(long paramLong)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      ATTACHMENT_INMEMORY_HEAPSIZE -= paramLong;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public static final long getInMemoryHeapSize()
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      long l = ATTACHMENT_INMEMORY_HEAPSIZE;
      return l;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static AttachmentData getData(long paramLong, String paramString, InputStream paramInputStream, boolean paramBoolean)
    throws IOException, GoatException
  {
    assert ((paramString != null) && (paramInputStream != null));
    if (paramLong == 0L)
      paramLong = 9223372036854775807L;
    LinkedList localLinkedList = new LinkedList();
    byte[] arrayOfByte = new byte[8192];
    int j = 0;
    int k = paramLong < MAX_MEM_SIZE ? (int)paramLong : MAX_MEM_SIZE;
    int i;
    while ((j <= k) && ((i = paramInputStream.read(arrayOfByte)) != -1))
    {
      j += i;
      BuffAtt localBuffAtt = new BuffAtt(i, arrayOfByte, null);
      localLinkedList.add(localBuffAtt);
      arrayOfByte = new byte[8192];
    }
    if (j > paramLong)
    {
      while (paramInputStream.read(arrayOfByte) != -1);
      if (paramBoolean)
        throw new GoatException(9209);
      throw new GoatException(9210, Long.valueOf(paramLong));
    }
    boolean bool = false;
    Object localObject1;
    Object localObject2;
    if (j <= MAX_MEM_SIZE)
    {
      bool = tryReserveInMemoryHeapSize(j);
      if (bool)
      {
        arrayOfByte = new byte[j];
        localObject1 = localLinkedList.iterator();
        j = 0;
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (BuffAtt)((Iterator)localObject1).next();
          System.arraycopy(((BuffAtt)localObject2).data, 0, arrayOfByte, j, ((BuffAtt)localObject2).len);
          j += ((BuffAtt)localObject2).len;
        }
        return new AttachmentData(paramString, arrayOfByte);
      }
    }
    if (!bool)
      try
      {
        localObject1 = null;
        checkAndMakeAttStoreDir();
        localObject2 = File.createTempFile("att", "data", ATT_STORE);
        FileOutputStream localFileOutputStream = new FileOutputStream((File)localObject2);
        try
        {
          Iterator localIterator = localLinkedList.iterator();
          while (localIterator.hasNext())
          {
            localObject3 = (BuffAtt)localIterator.next();
            localFileOutputStream.write(((BuffAtt)localObject3).data, 0, ((BuffAtt)localObject3).len);
          }
          localLinkedList = null;
          while ((i = paramInputStream.read(arrayOfByte)) != -1)
          {
            localFileOutputStream.write(arrayOfByte, 0, i);
            j += i;
            if (j > paramLong)
            {
              while (paramInputStream.read(arrayOfByte) != -1);
              if (paramBoolean)
                throw new GoatException(9209);
              throw new GoatException(9210, Long.valueOf(paramLong));
            }
          }
          localObject1 = new AttachmentData(paramString, (File)localObject2);
          Object localObject3 = localObject1;
          return localObject3;
        }
        finally
        {
          localFileOutputStream.close();
          if (localObject1 == null)
            ((File)localObject2).delete();
        }
      }
      catch (IOException localIOException)
      {
        throw new GoatException("Could not create temporary file to upload attachment data to the ar server due to IOException ", localIOException);
      }
    return null;
  }

  private static AttachmentData download(AttachmentDataKey paramAttachmentDataKey)
    throws GoatException
  {
    AttachmentData localAttachmentData = new AttachmentData();
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramAttachmentDataKey.mServer);
    AttachmentValue localAttachmentValue;
    try
    {
      localAttachmentValue = getAttachmentValue(localServerLogin, paramAttachmentDataKey.mSchema, paramAttachmentDataKey.mFieldID, paramAttachmentDataKey.mEntryID);
    }
    catch (ARException localARException1)
    {
      throw new GoatException(localARException1);
    }
    boolean bool = false;
    long l = localAttachmentValue.getOriginalSize();
    byte[] arrayOfByte = localAttachmentValue.getContent();
    if (l <= MAX_MEM_SIZE)
    {
      bool = tryReserveInMemoryHeapSize(l);
      if (bool)
        try
        {
          if (arrayOfByte == null)
          {
            localAttachmentValue.setValue(EMPTY_BUF);
            localAttachmentData.mAttData = localServerLogin.getEntryBlob(paramAttachmentDataKey.mSchema, paramAttachmentDataKey.mEntryID, paramAttachmentDataKey.mFieldID);
          }
          else
          {
            localAttachmentData.mAttData = arrayOfByte;
          }
          localAttachmentData.mName = localAttachmentValue.getName();
        }
        catch (ARException localARException2)
        {
          bool = false;
          decrementInMemoryReservedHeapSize(l);
        }
    }
    if (!bool)
      try
      {
        checkAndMakeAttStoreDir();
        File localFile = File.createTempFile("att", "data", ATT_STORE);
        localFile.deleteOnExit();
        localFile = new File(localFile.getCanonicalPath());
        if ((arrayOfByte == null) || (arrayOfByte.length == 0))
        {
          localAttachmentValue.setValue(localFile.getPath());
          try
          {
            localAttachmentData.mName = localAttachmentValue.getName();
            localServerLogin.getEntryBlob(paramAttachmentDataKey.mSchema, paramAttachmentDataKey.mEntryID, paramAttachmentDataKey.mFieldID, localFile.getPath());
            localAttachmentData.mAttFile = localFile;
          }
          catch (ARException localARException3)
          {
            throw new GoatException(localARException3);
          }
        }
        else
        {
          localAttachmentData.mName = localAttachmentValue.getName();
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
          localFileOutputStream.write(arrayOfByte);
          localAttachmentData.mAttFile = localFile;
          localFileOutputStream.close();
        }
      }
      catch (IOException localIOException)
      {
        throw new GoatException("Could not create temporary file to receive attachment data " + paramAttachmentDataKey + " from ar server due to IOException ", localIOException);
      }
    return localAttachmentData;
  }

  public static AttachmentData get(AttachmentDataKey paramAttachmentDataKey)
    throws GoatException
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      AttachmentDataCache localAttachmentDataCache = getOrCreateCache(paramAttachmentDataKey.mScreenName, paramAttachmentDataKey.mSessionID);
      AttachmentData localAttachmentData1 = localAttachmentDataCache.get(paramAttachmentDataKey);
      if (localAttachmentData1 == null)
      {
        localAttachmentData1 = download(paramAttachmentDataKey);
        localAttachmentDataCache.add(paramAttachmentDataKey, localAttachmentData1);
      }
      else if (localAttachmentData1.mAttFile != null)
      {
        long l = localAttachmentData1.getSize();
        if (l <= MAX_MEM_SIZE)
        {
          boolean bool = tryReserveInMemoryHeapSize(l);
          if (bool)
            try
            {
              moveAttachmentToMemory(paramAttachmentDataKey, localAttachmentData1);
            }
            catch (IOException localIOException)
            {
              localIOException.printStackTrace();
            }
        }
      }
      AttachmentData localAttachmentData2 = localAttachmentData1;
      return localAttachmentData2;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public static void remove(AttachmentDataKey paramAttachmentDataKey)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      AttachmentDataCache localAttachmentDataCache = getCache(paramAttachmentDataKey.mScreenName, paramAttachmentDataKey.mSessionID);
      if (localAttachmentDataCache != null)
        localAttachmentDataCache.remove(paramAttachmentDataKey);
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public static void removeAllUnused(String paramString1, String paramString2)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      AttachmentDataCache localAttachmentDataCache = getCache(paramString1, paramString2);
      if (localAttachmentDataCache != null)
        localAttachmentDataCache.removeAllUnused(SessionData.get());
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public static void removeAll(String paramString1, String paramString2)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      AttachmentDataCache localAttachmentDataCache = getCache(paramString1, paramString2);
      if (localAttachmentDataCache != null)
        localAttachmentDataCache.removeAll(SessionData.get());
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public String getName()
  {
    return this.mName;
  }

  public long getSize()
  {
    return this.mAttData != null ? this.mAttData.length : this.mAttFile != null ? this.mAttFile.length() : 0L;
  }

  public AttachmentValue toAttachmentValue()
  {
    try
    {
      return this.mAttFile != null ? new AttachmentValue(this.mName, this.mAttFile.getPath()) : new AttachmentValue(this.mName, this.mAttData);
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }

  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    if (this.mAttFile == null)
    {
      if (this.mAttData != null)
        paramOutputStream.write(this.mAttData);
    }
    else
    {
      byte[] arrayOfByte = new byte[8192];
      FileInputStream localFileInputStream = new FileInputStream(this.mAttFile);
      try
      {
        int i;
        while ((i = localFileInputStream.read(arrayOfByte)) != -1)
          paramOutputStream.write(arrayOfByte, 0, i);
      }
      finally
      {
        localFileInputStream.close();
      }
    }
  }

  public static AttachmentValue getAttachmentValue(ServerLogin paramServerLogin, String paramString1, int paramInt, String paramString2)
    throws ARException
  {
    Entry localEntry = paramServerLogin.getEntry(paramString1, paramString2, new int[] { paramInt });
    Iterator localIterator = localEntry.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry1 = (Map.Entry)localIterator.next();
      if (DataType.ATTACHMENT.equals(((Value)localEntry1.getValue()).getDataType()))
        return (AttachmentValue)((Value)localEntry1.getValue()).getValue();
    }
    return new AttachmentValue("", EMPTY_BUF);
  }

  private static AttachmentDataCache getCache(String paramString1, String paramString2)
  {
    String str = getAttachmentCacheID(paramString1, paramString2);
    return (AttachmentDataCache)MAttCaches.get(str);
  }

  private static AttachmentDataCache getOrCreateCache(String paramString1, String paramString2)
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      SessionData localSessionData = SessionData.get();
      String str = getAttachmentCacheID(paramString1, paramString2);
      AttachmentDataCache localAttachmentDataCache;
      synchronized (str.intern())
      {
        if ((localAttachmentDataCache = (AttachmentDataCache)MAttCaches.get(str)) == null)
        {
          localAttachmentDataCache = new AttachmentDataCache(paramString1, localSessionData, null);
          MAttCaches.put(str, localAttachmentDataCache);
        }
      }
      ??? = localAttachmentDataCache;
      return ???;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  private static String getAttachmentCacheID(String paramString1, String paramString2)
  {
    return paramString1 + "/" + paramString2;
  }

  private void dispose()
  {
    if (this.mAttData != null)
    {
      long l = getSize();
      this.mAttData = null;
      ATTACHMENT_CACHE_LOCK.lock();
      try
      {
        decrementInMemoryHeapSize(l);
      }
      finally
      {
        ATTACHMENT_CACHE_LOCK.unlock();
      }
    }
    if (this.mAttFile != null)
    {
      this.mAttFile.delete();
      this.mAttFile = null;
    }
  }

  protected void finalize()
    throws Throwable
  {
    try
    {
      dispose();
    }
    finally
    {
      super.finalize();
    }
  }

  private static final void sanityCheckHeapSize()
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      int i = ATTACHMENT_DATA_KEY_INMEMORY_MAP.values().size();
      long l1 = getInMemoryHeapSize();
      long l2 = 0L;
      Iterator localIterator = getInMemoryAttachmentData();
      int j = 0;
      while (localIterator.hasNext())
      {
        j++;
        l2 += ((AttachmentData)((Map.Entry)localIterator.next()).getValue()).getSize();
      }
      if (l1 != l2)
      {
        System.out.println("heapsize: " + l1 + " != heapsize calc: " + l2);
        Thread.dumpStack();
      }
      if (i != j)
      {
        System.out.println("in-memory count: " + i + " != in-memory count calc: " + j);
        Thread.dumpStack();
      }
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public static final int getInMemoryAttachmentDataCount()
  {
    ATTACHMENT_CACHE_LOCK.lock();
    try
    {
      int i = ATTACHMENT_DATA_KEY_INMEMORY_MAP.size();
      return i;
    }
    finally
    {
      ATTACHMENT_CACHE_LOCK.unlock();
    }
  }

  public static final Iterator<Map.Entry<AttachmentDataKey, AttachmentData>> getInMemoryAttachmentData()
  {
    return new Iterator()
    {
      private Map.Entry<AttachmentData.AttachmentDataKey, AttachmentData> attachmentData;
      private final Iterator<AttachmentData.AttachmentDataCache> attCachesIter = AttachmentData.MAttCaches.values().iterator();
      private Iterator<Map.Entry<AttachmentData.AttachmentDataKey, AttachmentData>> attDataIter;

      public void remove()
      {
        throw new UnsupportedOperationException();
      }

      public Map.Entry<AttachmentData.AttachmentDataKey, AttachmentData> next()
      {
        initNext();
        Map.Entry localEntry = this.attachmentData;
        this.attachmentData = null;
        return localEntry;
      }

      public boolean hasNext()
      {
        initNext();
        return this.attachmentData != null;
      }

      private void initNext()
      {
        if (this.attachmentData != null)
          return;
        while (true)
        {
          if (this.attDataIter == null)
            if (this.attCachesIter.hasNext())
              this.attDataIter = ((AttachmentData.AttachmentDataCache)this.attCachesIter.next()).getAttachmentData();
            else
              return;
          while (this.attDataIter.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)this.attDataIter.next();
            if (((AttachmentData)localEntry.getValue()).mAttData != null)
            {
              this.attachmentData = localEntry;
              return;
            }
          }
          this.attDataIter = null;
        }
      }
    };
  }

  public static File createTemporary(InputStream paramInputStream)
  {
    checkAndMakeAttStoreDir();
    File localFile = null;
    try
    {
      localFile = File.createTempFile("att", "data", ATT_STORE);
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      byte[] arrayOfByte = new byte[1024];
      int i;
      while ((i = paramInputStream.read(arrayOfByte)) > 0)
        localFileOutputStream.write(arrayOfByte, 0, i);
      localFileOutputStream.close();
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return localFile;
  }

  public static File downloadAllAttachment(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    byte[] arrayOfByte = new byte[1024];
    File localFile = null;
    ZipOutputStream localZipOutputStream = null;
    try
    {
      localFile = File.createTempFile("att", "data", ATT_STORE);
      localZipOutputStream = new ZipOutputStream(new FileOutputStream(localFile));
      ArrayList localArrayList = new ArrayList();
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        getFileOutputStream(str, localZipOutputStream, localArrayList);
      }
      localZipOutputStream.close();
    }
    catch (IOException localIOException)
    {
    }
    return localFile;
  }

  private static void getFileOutputStream(String paramString, ZipOutputStream paramZipOutputStream, List<String> paramList)
  {
    try
    {
      if (paramString != null)
      {
        NDXRequest.Parser localParser = new NDXRequest.Parser(paramString);
        String str1 = localParser.next();
        boolean bool = "1".equals(str1);
        if ((!bool) && (!"2".equals(str1)))
          str1 = null;
        if (str1 != null)
        {
          String str2 = localParser.next();
          if (str2 != null)
          {
            AttachmentDataKey localAttachmentDataKey = new AttachmentDataKey(str2);
            AttachmentData localAttachmentData = get(localAttachmentDataKey);
            String str3 = "";
            String str4 = localParser.next();
            String str5 = str4 != null ? str4 : localAttachmentData.getName();
            getValidName(paramList, localAttachmentData);
            paramZipOutputStream.putNextEntry(new ZipEntry(localAttachmentData.getName()));
            localAttachmentData.writeTo(paramZipOutputStream);
            paramZipOutputStream.closeEntry();
            return;
          }
        }
      }
    }
    catch (GoatException localGoatException)
    {
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }

  private static void getValidName(List<String> paramList, AttachmentData paramAttachmentData)
  {
    String str1 = paramAttachmentData.getName();
    String str2 = str1;
    int i = 0;
    while (paramList.contains(str2))
    {
      i++;
      String str3 = str1.substring(0, str1.lastIndexOf('.'));
      String str4 = str1.substring(str1.lastIndexOf('.'), str1.length());
      str2 = str3 + " (" + i + ")" + str4.toLowerCase();
    }
    paramList.add(str2);
    paramAttachmentData.mName = str2;
  }

  synchronized boolean isBusy()
  {
    return this.mBusy;
  }

  public synchronized void setBusy(boolean paramBoolean)
  {
    this.mBusy = paramBoolean;
  }

  static
  {
    arLog = Log.get(11);
    MAX_MEM_SIZE = Configuration.getInstance().getAttachmentInMemoryDataLimit();
    ATT_STORE = new File(Configuration.getInstance().getRootPath() + "/attstore");
    ATTACHMENT_INMEMORY_HEAPSIZE_MAX = Configuration.getInstance().getAttachmentInMemoryMaxHeapSize();
    ATTACHMENT_INMEMORY_HEAPSIZE_RESIZE_RATIO = Configuration.getInstance().getAttachmentInMemoryHeapSizeResizeRatio();
    ATTACHMENT_INMEMORY_HEAPSIZE = 0L;
    ATTACHMENT_INMEMORY_HEAPSIZE_RESERVED = 0L;
    ATTACHMENT_CACHE_LOCK = new ReentrantLock();
    EMPTY_BUF = new byte[0];
    MAttCaches = Collections.synchronizedMap(new HashMap());
    ATTACHMENT_DATA_KEY_INMEMORY_MAP = new HashMap();
    checkAndMakeAttStoreDir();
  }

  private static class BuffAtt
  {
    private final int len;
    private final byte[] data;

    private BuffAtt(int paramInt, byte[] paramArrayOfByte)
    {
      this.len = paramInt;
      this.data = paramArrayOfByte;
    }
  }

  private static class AttachmentDataCache
    implements SessionData.SessionDataEventListener, Serializable
  {
    private static final long serialVersionUID = -7547774117364520829L;
    private transient Map<AttachmentData.AttachmentDataKey, AttachmentData> mFieldData = Collections.synchronizedMap(new HashMap());
    private final String mScreenName;

    private AttachmentDataCache(String paramString, SessionData paramSessionData)
    {
      this.mScreenName = paramString;
      paramSessionData.addEventListener(this);
    }

    public final Iterator<Map.Entry<AttachmentData.AttachmentDataKey, AttachmentData>> getAttachmentData()
    {
      return this.mFieldData.entrySet().iterator();
    }

    public void activating(SessionData paramSessionData, ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
    }

    public void disposing(SessionData paramSessionData)
    {
      AttachmentData.arLog.log(Level.INFO, "Disposing calling ...");
      AttachmentData.ATTACHMENT_CACHE_LOCK.lock();
      try
      {
        if (this.mFieldData != null)
        {
          synchronized (this.mFieldData)
          {
            Iterator localIterator = this.mFieldData.entrySet().iterator();
            while (localIterator.hasNext())
            {
              localObject1 = (Map.Entry)localIterator.next();
              AttachmentData.AttachmentDataKey localAttachmentDataKey = (AttachmentData.AttachmentDataKey)((Map.Entry)localObject1).getKey();
              AttachmentData localAttachmentData = (AttachmentData)((Map.Entry)localObject1).getValue();
              if (localAttachmentData.mAttData != null)
                AttachmentData.ATTACHMENT_DATA_KEY_INMEMORY_MAP.remove(localAttachmentDataKey);
              localAttachmentData.dispose();
              AttachmentData.arLog.log(Level.INFO, "Disposed name " + localAttachmentData.getName());
            }
            Object localObject1 = (AttachmentDataCache)AttachmentData.MAttCaches.remove(AttachmentData.getAttachmentCacheID(this.mScreenName, paramSessionData.getID()));
            if ((!$assertionsDisabled) && (localObject1 != this))
              throw new AssertionError();
          }
          this.mFieldData = null;
        }
      }
      finally
      {
        AttachmentData.ATTACHMENT_CACHE_LOCK.unlock();
      }
    }

    public void passivating(SessionData paramSessionData, ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
    }

    private AttachmentData get(AttachmentData.AttachmentDataKey paramAttachmentDataKey)
    {
      return get(paramAttachmentDataKey, true);
    }

    private AttachmentData get(AttachmentData.AttachmentDataKey paramAttachmentDataKey, boolean paramBoolean)
    {
      assert (this.mFieldData != null);
      AttachmentData.ATTACHMENT_CACHE_LOCK.lock();
      AttachmentData localAttachmentData = null;
      try
      {
        localAttachmentData = (AttachmentData)this.mFieldData.get(paramAttachmentDataKey);
        if ((localAttachmentData != null) && (localAttachmentData.mAttData != null) && (paramBoolean))
          AttachmentData.setLastAccessed(paramAttachmentDataKey);
      }
      finally
      {
        AttachmentData.ATTACHMENT_CACHE_LOCK.unlock();
      }
      return localAttachmentData;
    }

    private void add(AttachmentData.AttachmentDataKey paramAttachmentDataKey, AttachmentData paramAttachmentData)
    {
      assert (this.mFieldData != null);
      AttachmentData.ATTACHMENT_CACHE_LOCK.lock();
      try
      {
        if (paramAttachmentData.mAttData != null)
        {
          AttachmentData.setLastAccessed(paramAttachmentDataKey);
          long l = paramAttachmentData.getSize();
          AttachmentData.decrementInMemoryReservedHeapSize(l);
          AttachmentData.incrementInMemoryHeapSize(l);
        }
        AttachmentData localAttachmentData = (AttachmentData)this.mFieldData.put(paramAttachmentDataKey, paramAttachmentData);
        if (localAttachmentData != null)
          localAttachmentData.dispose();
      }
      finally
      {
        AttachmentData.ATTACHMENT_CACHE_LOCK.unlock();
      }
    }

    private void remove(AttachmentData.AttachmentDataKey paramAttachmentDataKey)
    {
      assert (this.mFieldData != null);
      AttachmentData.ATTACHMENT_CACHE_LOCK.lock();
      try
      {
        AttachmentData localAttachmentData = (AttachmentData)this.mFieldData.remove(paramAttachmentDataKey);
        if (localAttachmentData != null)
        {
          if (localAttachmentData.mAttData != null)
            AttachmentData.ATTACHMENT_DATA_KEY_INMEMORY_MAP.remove(paramAttachmentDataKey);
          localAttachmentData.dispose();
        }
      }
      finally
      {
        AttachmentData.ATTACHMENT_CACHE_LOCK.unlock();
      }
    }

    private void removeAllUnused(SessionData paramSessionData)
    {
      AttachmentData.ATTACHMENT_CACHE_LOCK.lock();
      try
      {
        synchronized (this.mFieldData)
        {
          if (this.mFieldData != null)
          {
            Iterator localIterator = this.mFieldData.keySet().iterator();
            while (localIterator.hasNext())
            {
              AttachmentData.AttachmentDataKey localAttachmentDataKey = (AttachmentData.AttachmentDataKey)localIterator.next();
              int i = localAttachmentDataKey.getFieldID();
              if ((i < 3000000) || (i > 3999999))
              {
                AttachmentData localAttachmentData = (AttachmentData)this.mFieldData.get(localAttachmentDataKey);
                if (!localAttachmentData.isBusy())
                {
                  localIterator.remove();
                  if (localAttachmentData.mAttData != null)
                    AttachmentData.ATTACHMENT_DATA_KEY_INMEMORY_MAP.remove(localAttachmentDataKey);
                  localAttachmentData.dispose();
                }
              }
            }
          }
        }
        if ((this.mFieldData != null) && (this.mFieldData.isEmpty()))
        {
          this.mFieldData = null;
          ??? = (AttachmentDataCache)AttachmentData.MAttCaches.remove(AttachmentData.getAttachmentCacheID(this.mScreenName, paramSessionData.getID()));
          paramSessionData.removeEventListener(this);
          if ((!$assertionsDisabled) && (??? != this))
            throw new AssertionError();
        }
      }
      finally
      {
        AttachmentData.ATTACHMENT_CACHE_LOCK.unlock();
      }
    }

    private void removeAll(SessionData paramSessionData)
    {
      paramSessionData.removeEventListener(this);
      disposing(paramSessionData);
    }
  }

  public static class AttachmentDataKey
    implements Serializable
  {
    private static final long serialVersionUID = -2012768573675437320L;
    private transient String mServer;
    private transient String mSchema;
    private transient String mEntryID;
    private transient int mFieldID;
    private transient String mScreenName;
    private transient String mSessionID;
    private String mStrFormat;
    private long lastAccessed;

    public AttachmentDataKey(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5)
    {
      this.mServer = paramString1;
      this.mSchema = paramString2;
      this.mEntryID = paramString3;
      this.mFieldID = paramInt;
      this.mScreenName = paramString4;
      this.mSessionID = paramString5;
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.mScreenName == null)
        str = "0";
      else
        str = "1";
      localStringBuilder.append(str.length()).append('/').append(str);
      String str = this.mServer;
      localStringBuilder.append(str.length()).append('/').append(str);
      str = this.mSchema;
      localStringBuilder.append(str.length()).append('/').append(str);
      str = this.mEntryID != null ? this.mEntryID : "";
      localStringBuilder.append(str.length()).append('/').append(str);
      str = Integer.valueOf(this.mFieldID).toString();
      localStringBuilder.append(str.length()).append('/').append(str);
      if (this.mScreenName != null)
      {
        str = this.mScreenName;
        localStringBuilder.append(str.length()).append('/').append(str);
      }
      str = this.mSessionID;
      localStringBuilder.append(str.length()).append('/').append(str);
      this.mStrFormat = localStringBuilder.toString();
    }

    public AttachmentDataKey(String paramString)
      throws GoatException
    {
      initFromString(this, paramString);
    }

    public long getLastAccessed()
    {
      return this.lastAccessed;
    }

    public void setLastAccessed(long paramLong)
    {
      this.lastAccessed = paramLong;
    }

    public int hashCode()
    {
      return this.mStrFormat.hashCode();
    }

    public boolean equals(Object paramObject)
    {
      return (this == paramObject) || (((paramObject instanceof AttachmentDataKey)) && (this.mStrFormat.equals(((AttachmentDataKey)paramObject).mStrFormat)));
    }

    public String toString()
    {
      return this.mStrFormat;
    }

    public String toStringFormat()
    {
      return this.mStrFormat;
    }

    public String getServer()
    {
      return this.mServer;
    }

    public String getSchema()
    {
      return this.mSchema;
    }

    public String getEntryID()
    {
      return this.mEntryID;
    }

    public int getFieldID()
    {
      return this.mFieldID;
    }

    public String getScreenName()
    {
      return this.mScreenName;
    }

    public String getSessionID()
    {
      return this.mSessionID;
    }

    private static void initFromString(AttachmentDataKey paramAttachmentDataKey, String paramString)
      throws GoatException
    {
      NDXRequest.Parser localParser = new NDXRequest.Parser(paramString);
      String str1 = localParser.next();
      assert (("0".equals(str1)) || ("1".equals(str1)));
      paramAttachmentDataKey.mServer = localParser.next();
      paramAttachmentDataKey.mSchema = localParser.next();
      String str2 = localParser.next();
      if ((str2 != null) && (str2.length() > 0))
      {
        if (str2.indexOf('|') != -1)
          paramAttachmentDataKey.mEntryID = str2;
        else
          paramAttachmentDataKey.mEntryID = str2;
      }
      else
        paramAttachmentDataKey.mEntryID = null;
      try
      {
        paramAttachmentDataKey.mFieldID = (int)Long.parseLong(localParser.next());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new GoatException("The string form of AttachmentDataKey is incorrect " + paramString, localNumberFormatException);
      }
      if ("1".equals(str1))
        paramAttachmentDataKey.mScreenName = localParser.next();
      else
        paramAttachmentDataKey.mScreenName = null;
      paramAttachmentDataKey.mSessionID = localParser.next();
      paramAttachmentDataKey.mStrFormat = paramString;
    }

    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      try
      {
        initFromString(this, this.mStrFormat);
      }
      catch (GoatException localGoatException)
      {
        throw new IOException(localGoatException.getMessage());
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.AttachmentData
 * JD-Core Version:    0.6.1
 */