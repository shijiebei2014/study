package com.remedy.arsys.share;

import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ActorViewCache extends BaseMappingDataCache
{
  private static transient Set<String> firstTimeSet = new HashSet(10);
  private static final long serialVersionUID = 6989521166486607463L;
  private static final transient QualifierInfo qualification = new QualifierInfo(new RelationalOperationInfo(6, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 45004), new ArithmeticOrRelationalOperand(new Value())));

  private ActorViewCache(ServerLogin paramServerLogin)
    throws GoatException
  {
    super("ActorViews", false, true, true, false, paramServerLogin, findServerForm(paramServerLogin, new int[] { 45002 }));
  }

  protected QualifierInfo getQualification()
  {
    return qualification;
  }

  protected int[] getRetrieveFieldIds()
  {
    return Fields.ALL_FIELDS;
  }

  protected List<SortInfo> getSortList()
  {
    return null;
  }

  public static ActorViewCache getInstance(String paramString)
  {
    String str = paramString.intern();
    ActorViewCache localActorViewCache = null;
    synchronized (str)
    {
      try
      {
        localActorViewCache = new ActorViewCache(ServerLogin.getAdmin(paramString));
        if (localActorViewCache.formName == null)
          return null;
        if (!firstTimeSet.contains(paramString))
        {
          localActorViewCache.checkCache();
          firstTimeSet.add(paramString);
        }
      }
      catch (Exception localException)
      {
        logger.log(Level.WARNING, "Could not load Actor View Data from the previously loaded server " + paramString);
      }
    }
    return (ActorViewCache)localActorViewCache;
  }

  protected void addActorView(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    super.addMapping(new String[] { paramString1, paramString2, paramString3 }, paramString4);
  }

  protected void addEntryToMap(Entry paramEntry)
  {
    if (paramEntry != null)
      addActorView(getValueForFieldAsString(1700, paramEntry), getValueForFieldAsString(2000010, paramEntry), getValueForFieldAsString(45003, paramEntry), getValueForFieldAsString(45004, paramEntry));
  }

  public String getViewLabel(String paramString1, String paramString2, String paramString3)
  {
    String str = null;
    paramString1 = (paramString1 == null) || ("".equals(paramString1)) ? "$NULL$" : paramString1;
    paramString2 = (paramString2 == null) || ("".equals(paramString2)) ? "$NULL$" : paramString2;
    paramString3 = (paramString3 == null) || ("".equals(paramString3)) ? "$NULL$" : paramString3;
    str = (String)super.getMapping(new String[] { paramString1, paramString2, paramString3 });
    return str;
  }

  protected int getMaximumRetrieve()
  {
    return 0;
  }

  private static abstract interface Fields
  {
    public static final int APPLICATION = 1700;
    public static final int ACTOR = 45003;
    public static final int FORM = 2000010;
    public static final int VIEW_LABEL = 45004;
    public static final int[] ALL_FIELDS = { 1700, 45003, 2000010, 45004 };
    public static final int IDENTIFY_FIELD = 45002;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ActorViewCache
 * JD-Core Version:    0.6.1
 */