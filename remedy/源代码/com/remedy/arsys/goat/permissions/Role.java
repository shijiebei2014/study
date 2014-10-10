package com.remedy.arsys.goat.permissions;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.RoleInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.Cachetable;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Role
  implements Serializable
{
  private static final long serialVersionUID = 991077559816270125L;
  private static Cache MRoleCache = new Cachetable("Roles", 2, 1);
  private final String name;

  private Role(RoleInfo paramRoleInfo)
    throws GoatException
  {
    this.name = paramRoleInfo.getName();
  }

  public String toString()
  {
    return this.name;
  }

  private static RoleInfo[] getRolesFromServer(String paramString1, String paramString2)
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
    RoleInfo[] arrayOfRoleInfo;
    try
    {
      arrayOfRoleInfo = (RoleInfo[])localServerLogin.getListRole(paramString2, null, null).toArray(new RoleInfo[0]);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    Arrays.sort(arrayOfRoleInfo, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        RoleInfo localRoleInfo1 = (RoleInfo)paramAnonymousObject1;
        RoleInfo localRoleInfo2 = (RoleInfo)paramAnonymousObject2;
        return localRoleInfo1.getName().compareToIgnoreCase(localRoleInfo2.getName());
      }

      public boolean equals(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        RoleInfo localRoleInfo1 = (RoleInfo)paramAnonymousObject1;
        RoleInfo localRoleInfo2 = (RoleInfo)paramAnonymousObject2;
        return localRoleInfo1.getName().equalsIgnoreCase(localRoleInfo2.getName());
      }
    });
    return arrayOfRoleInfo;
  }

  private static String buildKey(String paramString1, String paramString2)
  {
    return paramString1 + "/" + paramString2;
  }

  public static Role getInstance(String paramString1, String paramString2, Long paramLong)
    throws GoatException
  {
    Map localMap = getAllInstances(paramString1, paramString2);
    return (Role)localMap.get(paramLong);
  }

  public static Map getAllInstances(String paramString1, String paramString2)
    throws GoatException
  {
    String str = buildKey(paramString1, paramString2).intern();
    synchronized (str)
    {
      CachedRoleMap localCachedRoleMap = (CachedRoleMap)MRoleCache.get(str, CachedRoleMap.class);
      if (localCachedRoleMap == null)
      {
        localCachedRoleMap = new CachedRoleMap(paramString1);
        localCachedRoleMap.putAll(getRolesFromServer(paramString1, paramString2));
        MRoleCache.put(buildKey(paramString1, paramString2), localCachedRoleMap);
      }
      return localCachedRoleMap;
    }
  }

  private static class CachedRoleMap extends LinkedHashMap
    implements Cache.Item
  {
    private static final long serialVersionUID = 4700469874228167863L;
    private final String mServer;

    public CachedRoleMap(String paramString)
    {
      this.mServer = paramString;
    }

    public void putAll(RoleInfo[] paramArrayOfRoleInfo)
      throws GoatException
    {
      for (int i = 0; i < paramArrayOfRoleInfo.length; i++)
      {
        Long localLong = new Long(paramArrayOfRoleInfo[i].getId());
        put(localLong, new Role(paramArrayOfRoleInfo[i], null));
      }
    }

    public int getSize()
    {
      return size();
    }

    public String getServer()
    {
      return this.mServer;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.permissions.Role
 * JD-Core Version:    0.6.1
 */