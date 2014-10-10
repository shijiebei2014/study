package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.ContainerCriteria;
import com.bmc.arsys.api.ContainerType;
import com.bmc.arsys.api.PermissionInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Timestamp;
import com.remedy.arsys.goat.cache.sync.ServerSync;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MiscCache;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GoatContainer
  implements Cache.Item
{
  private static final long serialVersionUID = -674302569915560990L;
  private static Cache MCache;
  protected Container mContainer;
  private final String mServer;
  private final long mLastUpdateTimeMs;
  private final Set mGrantedKeys = Collections.synchronizedSet(new HashSet());
  private final Set mForbiddenKeys = Collections.synchronizedSet(new HashSet());
  private static final ContainerCriteria REQUIRED_PROPS;
  private static final ContainerCriteria MINIMAL_PROPS;

  public final int getSize()
  {
    return 1;
  }

  public static String getKey(String paramString1, String paramString2)
  {
    return (paramString1 + "/" + paramString2).intern();
  }

  protected GoatContainer(Container paramContainer, String paramString)
  {
    this.mContainer = paramContainer;
    this.mServer = paramString;
    this.mLastUpdateTimeMs = this.mContainer.getLastUpdateTime().getValue();
  }

  public long getLastUpdateTimeMs()
  {
    return this.mLastUpdateTimeMs;
  }

  public final String getServer()
  {
    return this.mServer;
  }

  public final Container getContainer()
  {
    return this.mContainer;
  }

  public static Cache getCache()
  {
    return MCache;
  }

  public static GoatContainer getContainer(ServerLogin paramServerLogin, String paramString)
    throws GoatException
  {
    String str1 = getKey(paramServerLogin.getServer(), paramString);
    synchronized (str1)
    {
      Object localObject1 = (GoatContainer)MCache.get(str1, GoatContainer.class);
      if (localObject1 == null)
      {
        String str2 = paramServerLogin.getServer();
        ServerLogin localServerLogin = ServerLogin.getAdmin(str2);
        try
        {
          Container localContainer = localServerLogin.getContainer(paramString, REQUIRED_PROPS);
          assert (localContainer != null);
          if (localContainer.getType() == ContainerType.APPLICATION.toInt())
            localObject1 = new GoatApplicationContainer(localContainer, str2);
          else
            localObject1 = new GoatContainer(localContainer, str2);
        }
        catch (ARException localARException)
        {
          throw new GoatException(localARException);
        }
        ServerSync.initContainerObjectTimesIfNeeded(str2);
        MCache.put(str1, (Cache.Item)localObject1);
      }
      assert (localObject1 != null);
      ((GoatContainer)localObject1).checkAccess(paramServerLogin);
      return localObject1;
    }
  }

  private void checkAccess(ServerLogin paramServerLogin)
    throws GoatException
  {
    assert (paramServerLogin != null);
    String str = paramServerLogin.getPermissionsKey();
    if (this.mGrantedKeys.contains(str))
      return;
    if (this.mForbiddenKeys.contains(str))
      throw new GoatException(9264, this.mContainer.getName());
    try
    {
      assert (this.mContainer != null);
      Container localContainer = paramServerLogin.getContainer(this.mContainer.getKey(), MINIMAL_PROPS);
      if (localContainer != null)
      {
        this.mGrantedKeys.add(str);
        return;
      }
    }
    catch (ARException localARException)
    {
      StatusInfo[] arrayOfStatusInfo = (StatusInfo[])localARException.getLastStatus().toArray(new StatusInfo[0]);
      for (int i = 0; (i < arrayOfStatusInfo.length) && (arrayOfStatusInfo[i].getMessageNum() != 8805L); i++);
      if (i == arrayOfStatusInfo.length)
        throw new GoatException(localARException);
    }
    this.mForbiddenKeys.add(str);
    throw new GoatException(9200, this.mContainer.getName());
  }

  public static String[] getKeys(ServerLogin paramServerLogin, ContainerType paramContainerType)
    throws GoatException
  {
    try
    {
      return (String[])paramServerLogin.getListContainer(0L, new int[] { paramContainerType.toInt() }, true, null, null).toArray(new String[0]);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
  }

  public boolean isPublic()
  {
    List localList = this.mContainer.getPermissions();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      PermissionInfo localPermissionInfo = (PermissionInfo)localIterator.next();
      if (localPermissionInfo.getGroupID() == 0L)
        return true;
    }
    return false;
  }

  static
  {
    MCache = new MiscCache("Containers");
    REQUIRED_PROPS = new ContainerCriteria();
    MINIMAL_PROPS = new ContainerCriteria();
    REQUIRED_PROPS.setPropertiesToRetrieve(ContainerCriteria.PERMISSIONS | ContainerCriteria.REFERENCES | ContainerCriteria.CONTAINER_TYPE | ContainerCriteria.LABEL | ContainerCriteria.DESCRIPTION);
    MINIMAL_PROPS.setPropertiesToRetrieve(ContainerCriteria.TIMESTAMP);
  }

  public static class ContainerList
    implements Cache.Item
  {
    private static final long serialVersionUID = 2106359570663044338L;
    private static Cache MCache = new MiscCache("ContainerList");
    private String[] mContainerList;
    private final String mServer;

    public final int getSize()
    {
      return 1;
    }

    private static String getKey(String paramString1, ContainerType paramContainerType, String paramString2)
    {
      return paramString1 + "/" + paramContainerType + "/" + paramString2;
    }

    private ContainerList(ServerLogin paramServerLogin, ContainerType paramContainerType)
      throws GoatException
    {
      this.mServer = paramServerLogin.getServer();
      try
      {
        List localList = paramServerLogin.getListContainer(0L, new int[] { paramContainerType.toInt() }, true, null, null);
        if (localList != null)
          this.mContainerList = ((String[])localList.toArray(new String[0]));
        else
          this.mContainerList = JSWriter.EmptyString;
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

    public static String[] get(ServerLogin paramServerLogin, ContainerType paramContainerType)
      throws GoatException
    {
      String str1 = paramServerLogin.getServer();
      String str2 = getKey(str1, paramContainerType, paramServerLogin.getPermissionsKey()).intern();
      synchronized (str2)
      {
        ContainerList localContainerList = (ContainerList)MCache.get(str2, ContainerList.class);
        if (localContainerList == null)
        {
          localContainerList = new ContainerList(paramServerLogin, paramContainerType);
          MCache.put(str2, localContainerList);
        }
        return localContainerList.mContainerList;
      }
    }

    public static Cache getCache()
    {
      return MCache;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.GoatContainer
 * JD-Core Version:    0.6.1
 */