package com.remedy.arsys.share;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ServerInfoMap;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerInfo
{
  private static final int[][] MTagLists = { { 50000 }, { 140, 139, 122, 158, 19, 27, 101, 1, 116, 51, 52, 53, 28 }, { 50100 }, { 175 }, { 60000 }, { 189, 54, 20 }, { 70602 }, { 336, 337, 333 }, { 70604 }, { 349 } };
  private static final long MServerInfoOutOfDateTime = 10000L;
  private static final Pattern MVersionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).*");
  private final String mServer;
  private final String mVersion;
  private String mExportVersion;
  private int mIntVersion = 0;
  private String mDBType;
  private String mDBVersion;
  private boolean mAmLocalized;
  private String mAlertSchema;
  private boolean mAmMultiAssignGroup;
  private long mCurrencyInterval;
  private int mFloatTimeout;
  private String mHomepageSchema;
  private String mDefaultWebPath;
  private boolean mServerCaseSensitive;
  private long mUserCacheTime;
  private long mGroupCacheTime;
  private long mStructCacheTime;
  private long mServerTime;
  private long mLastSuccessfulUpdateTime;
  private int mGetListECount;
  private int mMaxAttachSize;
  private static final Map MServerInfoMap = Collections.synchronizedMap(new HashMap());
  private String mDecorator = "";
  private boolean mbAllowUnqualQueries = false;
  private int mReqIdentifierLoc = 1;
  private int mPromptBarSystem = 2;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  private ServerInfo(ServerLogin paramServerLogin, String paramString)
    throws GoatException
  {
    this.mServer = paramString;
    int[] arrayOfInt = { 4 };
    ServerInfoMap localServerInfoMap;
    try
    {
      localServerInfoMap = paramServerLogin.getServerInfo(arrayOfInt);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    assert (localServerInfoMap != null);
    Value localValue = (Value)localServerInfoMap.get(Integer.valueOf(4));
    assert (localValue != null);
    this.mVersion = ((String)localValue.getValue());
    assert (this.mVersion != null);
    Matcher localMatcher = MVersionPattern.matcher(this.mVersion);
    int i = 0;
    if ((localMatcher.matches()) && (localMatcher.groupCount() == 3))
      try
      {
        this.mIntVersion += 10000 * Integer.parseInt(localMatcher.group(1)) + 100 * Integer.parseInt(localMatcher.group(2)) + Integer.parseInt(localMatcher.group(3));
        i = 1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
    assert (i != 0);
    if (this.mIntVersion < 50000)
      throw new GoatException(9303, "5.x");
    fetchAndPopulate(paramServerLogin);
    MServerInfoMap.put(this.mServer, this);
  }

  private void fetchAndPopulate(ServerLogin paramServerLogin)
    throws GoatException
  {
    this.lock.writeLock().lock();
    try
    {
      int i = 0;
      for (int j = 0; j < MTagLists.length; j += 2)
      {
        assert (MTagLists[j].length == 1);
        if (MTagLists[j][0] <= this.mIntVersion)
          i += MTagLists[(j + 1)].length;
      }
      int[] arrayOfInt = new int[i];
      int k = 0;
      for (int m = 0; m < MTagLists.length; m += 2)
        if (MTagLists[m][0] <= this.mIntVersion)
          for (int n = 0; n < MTagLists[(m + 1)].length; n++)
            arrayOfInt[(k++)] = MTagLists[(m + 1)][n];
      assert (k == arrayOfInt.length);
      ServerInfoMap localServerInfoMap;
      try
      {
        localServerInfoMap = paramServerLogin.getServerInfo(arrayOfInt);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
      assert (localServerInfoMap != null);
      Value localValue = (Value)localServerInfoMap.get(Integer.valueOf(101));
      if ((localValue != null) && (localValue.toString() != null))
        this.mExportVersion = localValue.toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(1));
      if ((localValue != null) && (localValue.toString() != null))
        this.mDBType = localValue.toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(27));
      if ((localValue != null) && (localValue.toString() != null))
        this.mDBVersion = localValue.toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(140));
      if ((localValue != null) && (localValue.toString() != null))
        this.mAmLocalized = (Integer.parseInt(localValue.getValue().toString()) == 1);
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(139));
      if ((localValue != null) && (localValue.toString() != null))
        this.mAlertSchema = localValue.toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(122));
      if ((localValue != null) && (localValue.toString() != null))
        this.mAmMultiAssignGroup = (Integer.parseInt(localValue.getValue().toString()) == 1);
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(175));
      if ((localValue != null) && (localValue.toString() != null))
        this.mCurrencyInterval = Long.parseLong(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(189));
      if ((localValue != null) && (localValue.toString() != null) && (!"".equals(localValue.toString())))
        this.mHomepageSchema = localValue.toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(158));
      if ((localValue != null) && (localValue.toString() != null))
        this.mDefaultWebPath = localValue.toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(19));
      if ((localValue != null) && (localValue.toString() != null))
        this.mFloatTimeout = Integer.parseInt(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(51));
      if ((localValue != null) && (localValue.toString() != null))
        this.mUserCacheTime = Long.parseLong(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(52));
      if ((localValue != null) && (localValue.toString() != null))
        this.mGroupCacheTime = Long.parseLong(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(53));
      if ((localValue != null) && (localValue.toString() != null))
        this.mStructCacheTime = Long.parseLong(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(116));
      if ((localValue != null) && (localValue.toString() != null))
        this.mServerTime = Long.parseLong(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(54));
      if ((localValue != null) && (localValue.toString() != null))
        this.mServerCaseSensitive = (Integer.parseInt(localValue.getValue().toString()) == 0);
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(28));
      if ((localValue != null) && (localValue.toString() != null))
        this.mGetListECount = Integer.parseInt(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(336));
      if ((localValue != null) && (localValue.toString() != null))
        this.mDecorator = localValue.getValue().toString();
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(337));
      if ((localValue != null) && (localValue.toString() != null))
        this.mReqIdentifierLoc = Integer.parseInt(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(333));
      if ((localValue != null) && (localValue.toString() != null))
        this.mMaxAttachSize = Integer.parseInt(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(349));
      if ((localValue != null) && (localValue.toString() != null))
        this.mPromptBarSystem = Integer.parseInt(localValue.getValue().toString());
      localValue = (Value)localServerInfoMap.get(Integer.valueOf(20));
      if ((localValue != null) && (localValue.toString() != null))
        this.mbAllowUnqualQueries = (Integer.parseInt(localValue.getValue().toString()) == 1);
      this.mLastSuccessfulUpdateTime = new Date().getTime();
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
  }

  public static ServerInfo get(String paramString, boolean paramBoolean)
    throws GoatException
  {
    assert ((paramString != null) && (paramString.length() > 0));
    paramString = paramString.toUpperCase();
    String str = "ServerInfo:" + paramString;
    int i = !paramBoolean ? 1 : 0;
    ServerInfo localServerInfo;
    synchronized (str.intern())
    {
      localServerInfo = (ServerInfo)MServerInfoMap.get(paramString);
      if (localServerInfo == null)
      {
        localServerInfo = new ServerInfo(ServerLogin.getAdmin(paramString), paramString);
        i = 0;
      }
    }
    if (i != 0)
      localServerInfo.checkRepopulate();
    return localServerInfo;
  }

  public static ServerInfo get(String paramString)
    throws GoatException
  {
    return get(paramString, false);
  }

  public static final ServerInfo get(ServerLogin paramServerLogin, boolean paramBoolean)
    throws GoatException
  {
    String str1 = paramServerLogin.getServer();
    assert ((str1 != null) && (str1.length() > 0));
    str1 = str1.toUpperCase();
    String str2 = "ServerInfo:" + str1;
    int i = !paramBoolean ? 1 : 0;
    ServerInfo localServerInfo;
    synchronized (str2.intern())
    {
      localServerInfo = (ServerInfo)MServerInfoMap.get(str1);
      if (localServerInfo == null)
      {
        localServerInfo = new ServerInfo(paramServerLogin, str1);
        i = 0;
      }
    }
    if (i != 0)
      localServerInfo.checkRepopulate();
    return localServerInfo;
  }

  public static final ServerInfo get(ServerLogin paramServerLogin)
    throws GoatException
  {
    return get(paramServerLogin, false);
  }

  public final void checkRepopulate(long paramLong)
  {
    this.lock.writeLock().lock();
    try
    {
      long l = new Date().getTime();
      if (l - this.mLastSuccessfulUpdateTime > paramLong)
        fetchAndPopulate(ServerLogin.getAdmin(this.mServer));
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
  }

  public final void checkRepopulate()
  {
    checkRepopulate(10000L);
  }

  public String getVersion()
  {
    return this.mVersion;
  }

  public int getVersionAsNumber()
  {
    return this.mIntVersion;
  }

  public String getExportVersion()
  {
    this.lock.readLock().lock();
    try
    {
      String str = this.mExportVersion;
      return str;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public String getDBType()
  {
    this.lock.readLock().lock();
    try
    {
      String str = this.mDBType;
      return str;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public String getDBVersion()
  {
    this.lock.readLock().lock();
    try
    {
      String str = this.mDBVersion;
      return str;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public boolean isLocalized()
  {
    this.lock.readLock().lock();
    try
    {
      boolean bool = this.mAmLocalized;
      return bool;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public String getAlertSchema()
  {
    this.lock.writeLock().lock();
    try
    {
      checkRepopulate(Configuration.getInstance().getServerInfoAlertSchemaCheckInterval() * 1000L);
      String str = this.mAlertSchema;
      return str;
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
  }

  public boolean isMultiAssignGroup()
  {
    this.lock.readLock().lock();
    try
    {
      boolean bool = this.mAmMultiAssignGroup;
      return bool;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public long getCurrencyInterval()
  {
    this.lock.readLock().lock();
    try
    {
      long l = this.mCurrencyInterval;
      return l;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public String getHomepageSchema()
  {
    this.lock.readLock().lock();
    try
    {
      String str = this.mHomepageSchema;
      return str;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public String getDefaultWebPath()
  {
    this.lock.readLock().lock();
    try
    {
      String str = this.mDefaultWebPath;
      return str;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public int getFloatTimeout()
  {
    this.lock.readLock().lock();
    try
    {
      int i = this.mFloatTimeout;
      return i;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public long getFloatTimeoutMillis()
  {
    this.lock.readLock().lock();
    try
    {
      long l = this.mFloatTimeout * 3600L * 1000L;
      return l;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public boolean isCaseSensitive()
  {
    this.lock.readLock().lock();
    try
    {
      boolean bool = this.mServerCaseSensitive;
      return bool;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public String getDecorator()
  {
    this.lock.readLock().lock();
    try
    {
      String str = this.mDecorator;
      return str;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public int getReqIdentifierLoc()
  {
    this.lock.readLock().lock();
    try
    {
      int i = this.mReqIdentifierLoc;
      return i;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public long getServerTime()
    throws GoatException
  {
    fetchAndPopulate(ServerLogin.getAdmin(this.mServer));
    this.lock.readLock().lock();
    try
    {
      long l = this.mServerTime;
      return l;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public long[] getCacheChangeTimes()
  {
    try
    {
      fetchAndPopulate(ServerLogin.getAdmin(this.mServer));
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
    }
    long[] arrayOfLong = new long[3];
    this.lock.readLock().lock();
    try
    {
      arrayOfLong[0] = this.mUserCacheTime;
      arrayOfLong[1] = this.mGroupCacheTime;
      arrayOfLong[2] = this.mStructCacheTime;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
    return arrayOfLong;
  }

  public int getGetListECount()
  {
    this.lock.readLock().lock();
    try
    {
      int i = this.mGetListECount;
      return i;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public int getMaxAttachSize()
  {
    this.lock.readLock().lock();
    try
    {
      int i = this.mMaxAttachSize;
      return i;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public int getSystemPromptBar()
  {
    this.lock.readLock().lock();
    try
    {
      int i = this.mPromptBarSystem;
      return i;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }

  public boolean getAllowUnqualQueries()
  {
    this.lock.readLock().lock();
    try
    {
      boolean bool = this.mbAllowUnqualQueries;
      return bool;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ServerInfo
 * JD-Core Version:    0.6.1
 */