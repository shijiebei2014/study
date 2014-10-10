package com.remedy.arsys.goat.permissions;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.GroupInfo;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.Cachetable;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Group
  implements Serializable
{
  private static Cache MGroupCache = new Cachetable("Groups", 2, 1);
  private final long mGroupId;
  private final ArrayList mNamelist;
  private final boolean mImplicit;
  private final int mGroupCategory;
  private static CachedGroupMap groupMap;
  private static int GROUP_FORM = 490000000;
  private static final int[] GROUP_SCHEMA_KEY_IDS = { GROUP_FORM };

  private Group(long paramLong, GroupInfo paramGroupInfo)
    throws GoatException
  {
    this.mGroupId = paramLong;
    this.mImplicit = ((this.mGroupId == 3L) || (this.mGroupId == 4L) || (this.mGroupId == 7L));
    this.mGroupCategory = paramGroupInfo.getCategory();
    this.mNamelist = new ArrayList();
    this.mNamelist.add(paramGroupInfo.getName());
  }

  public ArrayList getGroupNameList()
  {
    return this.mNamelist;
  }

  public boolean isImplicit()
  {
    return this.mImplicit;
  }

  public boolean isDynamic()
  {
    return this.mGroupCategory == 1;
  }

  public boolean isComputed()
  {
    return this.mGroupCategory == 2;
  }

  private static GroupInfo[] getGroupsFromServer(String paramString)
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString);
    GroupInfo[] arrayOfGroupInfo;
    try
    {
      arrayOfGroupInfo = (GroupInfo[])localServerLogin.getListGroup(null, null).toArray(new GroupInfo[0]);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    Arrays.sort(arrayOfGroupInfo, new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        GroupInfo localGroupInfo1 = (GroupInfo)paramAnonymousObject1;
        GroupInfo localGroupInfo2 = (GroupInfo)paramAnonymousObject2;
        return localGroupInfo1.getName().compareToIgnoreCase(localGroupInfo2.getName());
      }

      public boolean equals(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        GroupInfo localGroupInfo1 = (GroupInfo)paramAnonymousObject1;
        GroupInfo localGroupInfo2 = (GroupInfo)paramAnonymousObject2;
        return localGroupInfo1.getName().equalsIgnoreCase(localGroupInfo2.getName());
      }
    });
    return arrayOfGroupInfo;
  }

  public static Group getInstance(String paramString, Long paramLong)
    throws GoatException
  {
    Map localMap = getAllInstances(paramString);
    return (Group)localMap.get(paramLong);
  }

  public static Map getAllInstances(String paramString)
    throws GoatException
  {
    String str = paramString.intern();
    synchronized (str)
    {
      groupMap = (CachedGroupMap)MGroupCache.get(str, CachedGroupMap.class);
      boolean bool = SessionData.get().isGroupModified();
      if ((groupMap == null) || (bool))
      {
        groupMap = new CachedGroupMap(paramString);
        groupMap.putAll(getGroupsFromServer(str));
        MGroupCache.put(str, groupMap);
      }
      return groupMap;
    }
  }

  public static boolean isGroupModified(String paramString)
  {
    try
    {
      if (groupMap != null)
      {
        int i = groupMap.getTimeStamp();
        ServerLogin localServerLogin = ServerLogin.getAdmin(paramString);
        String str = SchemaKeyFactory.getInstance().getSchemaKey(localServerLogin, GROUP_SCHEMA_KEY_IDS);
        QualifierInfo localQualifierInfo = getLastModifiedQualification(i);
        int[] arrayOfInt = { 1 };
        List localList = localServerLogin.getListEntryObjects(str, localQualifierInfo, 0, 1, null, arrayOfInt, false, null);
        return localList.size() > 0;
      }
      return false;
    }
    catch (Exception localException)
    {
    }
    return false;
  }

  private static QualifierInfo getLastModifiedQualification(int paramInt)
  {
    QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(2, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 6), new ArithmeticOrRelationalOperand(new Value(paramInt))));
    return localQualifierInfo;
  }

  public static boolean isGroupList(long paramLong)
  {
    return paramLong == 104L;
  }

  public static boolean isRowLevelAccess(long paramLong)
  {
    return paramLong == 112L;
  }

  public static boolean isRoleStateMapping(long paramLong)
  {
    return (paramLong >= 2000L) && (paramLong <= 2199L);
  }

  public static boolean isMultiAssignField(long paramLong)
  {
    return (isRowLevelAccess(paramLong)) || ((paramLong >= 60000L) && (paramLong <= 60999L));
  }

  public static boolean isGroupField(long paramLong)
  {
    return (isGroupList(paramLong)) || (isMultiAssignField(paramLong)) || (isRoleStateMapping(paramLong));
  }

  private static class CachedGroupMap extends LinkedHashMap
    implements Cache.Item
  {
    private static final long serialVersionUID = -3410586179182900150L;
    private final String mServer;
    private final int mTimeStamp;

    public CachedGroupMap(String paramString)
    {
      this.mServer = paramString;
      this.mTimeStamp = (int)(new Date().getTime() / 1000L);
    }

    public void putAll(GroupInfo[] paramArrayOfGroupInfo)
      throws GoatException
    {
      for (int i = 0; i < paramArrayOfGroupInfo.length; i++)
      {
        Long localLong = new Long(paramArrayOfGroupInfo[i].getId());
        put(localLong, new Group(localLong.longValue(), paramArrayOfGroupInfo[i], null));
      }
    }

    public int getSize()
    {
      return size();
    }

    public int getTimeStamp()
    {
      return this.mTimeStamp;
    }

    public String getServer()
    {
      return this.mServer;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.permissions.Group
 * JD-Core Version:    0.6.1
 */