package com.remedy.arsys.share;

import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

public class UserActorCache extends BaseMappingDataCache
{
  private static final long serialVersionUID = 4483811824391803990L;
  private final String userName;
  private transient String primaryKey = null;

  private UserActorCache(String paramString, ServerLogin paramServerLogin)
    throws GoatException
  {
    super(String.format("%s%s", new Object[] { "UserActors", paramString }), true, false, false, true, paramServerLogin, findServerForm(ServerLogin.getAdmin(paramServerLogin.getServer()), new int[] { 45001 }));
    this.userName = paramString;
  }

  protected QualifierInfo getQualification()
  {
    QualifierInfo localQualifierInfo1 = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 101), new ArithmeticOrRelationalOperand(new Value())));
    QualifierInfo localQualifierInfo2 = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 101), new ArithmeticOrRelationalOperand(new Value(Keyword.AR_KEYWORD_USER))));
    return new QualifierInfo(2, localQualifierInfo1, localQualifierInfo2);
  }

  protected int[] getRetrieveFieldIds()
  {
    return Fields.ALL_FIELDS;
  }

  protected List<SortInfo> getSortList()
  {
    return null;
  }

  protected void addEntryToMap(Entry paramEntry)
  {
    if (paramEntry != null)
    {
      String str = getValueForFieldAsString(101, paramEntry);
      Integer localInteger = null;
      Value localValue = getValueForField(45005, paramEntry);
      if (localValue != null)
        localInteger = (Integer)localValue.getValue();
      addUserActor(getValueForFieldAsString(1700, paramEntry), str, localInteger, getValueForFieldAsString(45003, paramEntry));
    }
  }

  public static UserActorCache getInstance(SessionData paramSessionData, String paramString)
  {
    String str = paramSessionData.getUserName().intern();
    UserActorCache localUserActorCache = null;
    synchronized (str)
    {
      try
      {
        ServerLogin localServerLogin = paramSessionData.getServerLogin(paramString);
        localUserActorCache = new UserActorCache(paramSessionData.getUserName(), localServerLogin);
        localUserActorCache.checkCache();
      }
      catch (Exception localException)
      {
        logger.log(Level.WARNING, "Could not load User Actor Data from the previously loaded server " + paramString);
      }
    }
    return (UserActorCache)localUserActorCache;
  }

  protected void addUserActor(String paramString1, String paramString2, Integer paramInteger, String paramString3)
  {
    UserActorValueItem localUserActorValueItem = (UserActorValueItem)super.getMapping(new String[] { paramString1, paramString2 });
    if (localUserActorValueItem != null)
    {
      int i = localUserActorValueItem.getOrder() == null ? -1 : localUserActorValueItem.getOrder().intValue();
      int j = paramInteger == null ? -1 : paramInteger.intValue();
      if (j > i)
        super.replaceMapping(new String[] { paramString1, paramString2 }, new UserActorValueItem(paramInteger, paramString3));
    }
    else
    {
      super.addMapping(new String[] { paramString1, paramString2 }, new UserActorValueItem(paramInteger, paramString3));
    }
  }

  public String getActor(String paramString1, String paramString2)
  {
    String str = null;
    paramString1 = (paramString1 == null) || ("".equals(paramString1)) ? "$NULL$" : paramString1;
    paramString2 = (paramString2 == null) || ("".equals(paramString2)) ? "$NULL$" : paramString2;
    UserActorValueItem localUserActorValueItem = (UserActorValueItem)super.getMapping(new String[] { paramString1, paramString2 });
    if (localUserActorValueItem != null)
      str = localUserActorValueItem.getActor();
    return str;
  }

  protected String getPrimaryKey()
  {
    if (this.primaryKey == null)
      this.primaryKey = String.format("{%s}{%s}", new Object[] { super.getServer(), this.userName });
    return this.primaryKey;
  }

  public void release()
  {
    super.release();
  }

  protected int getMaximumRetrieve()
  {
    return 0;
  }

  private static abstract interface Fields
  {
    public static final int USER = 101;
    public static final int APPLICATION = 1700;
    public static final int ACTOR = 45003;
    public static final int ORDER = 45005;
    public static final int[] ALL_FIELDS = { 101, 1700, 45003, 45005 };
    public static final int IDENTIFY_FIELD = 45001;
  }

  public static class UserActorValueItem
    implements Serializable
  {
    private static final long serialVersionUID = 8356250820854838531L;
    private String actor;
    private Integer order;

    public UserActorValueItem()
    {
    }

    public UserActorValueItem(Integer paramInteger, String paramString)
    {
      this.order = paramInteger;
      this.actor = paramString;
    }

    protected String getActor()
    {
      return this.actor;
    }

    protected void setActor(String paramString)
    {
      this.actor = paramString;
    }

    protected Integer getOrder()
    {
      return this.order;
    }

    protected void setOrder(Integer paramInteger)
    {
      this.order = paramInteger;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.UserActorCache
 * JD-Core Version:    0.6.1
 */