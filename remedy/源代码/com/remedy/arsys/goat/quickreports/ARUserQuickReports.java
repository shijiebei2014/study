package com.remedy.arsys.goat.quickreports;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.CharacterFieldLimit;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.CacheDirectiveController.TimeHashMap;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

public class ARUserQuickReports
{
  private final String mForm;
  private final String mUser;
  private final String mServer;
  private String mPrefForm;
  private String mPrefServer;
  private static long mPrefServerLoadedDate;
  private static int TYPE_FID = 51003;
  private static int FORM_FID = 51001;
  private static int QUAL_FID = 51000;
  private static int SERVER_FID = 51004;
  private static int DISABLE_FID = 51002;
  private static int NAME_FID = CoreFieldId.ShortDescription.getFieldId();
  private static int USER_FID = CoreFieldId.Submitter.getFieldId();
  public static final int TYPE_QUICKREPORTS = 3;
  private static final ArithmeticOrRelationalOperand USER_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, USER_FID);
  private static final ArithmeticOrRelationalOperand FORM_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, FORM_FID);
  private static final ArithmeticOrRelationalOperand SERVER_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SERVER_FID);
  private static final ArithmeticOrRelationalOperand TYPE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, TYPE_FID);
  private static final ArithmeticOrRelationalOperand NAME_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, NAME_FID);
  private static final RelationalOperationInfo typeOp = new RelationalOperationInfo(1, TYPE_OP, new ArithmeticOrRelationalOperand(new Value(3)));
  private static final int[] SEARCHES_SCHEMA_KEY = { QUAL_FID, FORM_FID, TYPE_FID, SERVER_FID };
  private static final int[] qrfieldIds = { NAME_FID, DISABLE_FID, QUAL_FID };
  private static transient Log MLog = Log.get(2);
  private transient SessionData sesdata = SessionData.get();
  private static int numReportsInCache = 0;

  public ARUserQuickReports(String paramString1, String paramString2, String paramString3)
  {
    this.mForm = paramString2;
    this.mServer = paramString3;
    this.mUser = paramString1;
    ARUserSearches localARUserSearches = this.sesdata.getUserSearches();
    if (localARUserSearches != null)
    {
      this.mPrefServer = localARUserSearches.getServer();
      this.mPrefForm = localARUserSearches.getPrefForm();
    }
  }

  private void checkPrefServerUpdates()
  {
    List localList = Configuration.getInstance().getPreferenceServers();
    long l = Configuration.getInstance().getPreferenceServerLastModifiedDate();
    if ((localList == null) || (localList.size() == 0))
    {
      this.mPrefServer = null;
    }
    else if (mPrefServerLoadedDate < l)
    {
      if (localList.size() > 0)
      {
        Iterator localIterator = localList.iterator();
        if (localIterator.hasNext())
          this.mPrefServer = ((String)localIterator.next());
      }
      mPrefServerLoadedDate = l;
    }
  }

  private ServerLogin getAdminContext()
    throws GoatException
  {
    checkPrefServerUpdates();
    if (this.mPrefServer != null)
      return ServerLogin.getAdmin(this.mPrefServer);
    return null;
  }

  private ServerLogin getUserContext(SessionData paramSessionData)
    throws GoatException
  {
    checkPrefServerUpdates();
    if (this.mPrefServer == null)
      return null;
    return paramSessionData.getServerLogin(this.mPrefServer);
  }

  public boolean saveQuickReport(SessionData paramSessionData, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    throws GoatException
  {
    ServerLogin localServerLogin = getUserContext(paramSessionData);
    if (localServerLogin == null)
      return false;
    try
    {
      if (paramBoolean1)
      {
        Entry localEntry = new Entry();
        localEntry.put(Integer.valueOf(NAME_FID), new Value(paramString1));
        localEntry.put(Integer.valueOf(SERVER_FID), new Value(this.mServer));
        localEntry.put(Integer.valueOf(FORM_FID), new Value(this.mForm));
        localEntry.put(Integer.valueOf(QUAL_FID), new Value(paramString2));
        localEntry.put(Integer.valueOf(USER_FID), new Value(this.mUser));
        localEntry.put(Integer.valueOf(TYPE_FID), new Value(Integer.valueOf(3), DataType.ENUM));
        localEntry.put(Integer.valueOf(DISABLE_FID), paramBoolean2 ? new Value(0) : new Value());
        localServerLogin.createEntry(this.mPrefForm, localEntry);
        HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
        Configuration.getInstance();
        String str1 = Configuration.getInstance().getProperty("arsystem.myreport.report_cache_limit", "" + 20);
        int i = new Integer(str1).intValue();
        if (localHashMap == null)
        {
          localHashMap = new HashMap();
          localHashMap.put(((Value)localEntry.get(Integer.valueOf(NAME_FID))).toString(), new QuickReportCache(paramBoolean2, paramString2));
          this.sesdata.getMQuickReports().put(getCacheKey(), localHashMap);
          numReportsInCache += 1;
        }
        else
        {
          String str2 = "";
          if (numReportsInCache < i)
          {
            str2 = paramString2;
            numReportsInCache += 1;
          }
          else
          {
            str2 = "";
          }
          localHashMap.put(((Value)localEntry.get(Integer.valueOf(NAME_FID))).toString(), new QuickReportCache(paramBoolean2, str2));
        }
      }
      else
      {
        updateExistingRecord(paramSessionData, new String[] { paramString1 }, new boolean[] { paramBoolean2 }, paramString2);
      }
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Unable to save report", localARException);
      return false;
    }
    return true;
  }

  public boolean updateExistingRecord(SessionData paramSessionData, String[] paramArrayOfString, boolean[] paramArrayOfBoolean, String paramString)
    throws GoatException
  {
    int i = paramArrayOfString.length;
    assert (this.mPrefServer != null);
    try
    {
      ServerLogin localServerLogin = getUserContext(paramSessionData);
      if (localServerLogin == null)
        return false;
      int[] arrayOfInt = { QUAL_FID };
      for (int j = 0; j < i; j++)
      {
        HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
        if (localHashMap != null)
        {
          QuickReportCache localQuickReportCache = (QuickReportCache)localHashMap.get(paramArrayOfString[j]);
          QualifierInfo localQualifierInfo = buildQualifierForReportDefinition(paramArrayOfString[j], null);
          List localList = localServerLogin.getListEntryObjects(this.mPrefForm, localQualifierInfo, 0, 0, new ArrayList(), arrayOfInt, false, null);
          if (localList.size() > 0)
          {
            Entry localEntry = (Entry)localList.get(0);
            assert (localEntry != null);
            localEntry.put(Integer.valueOf(DISABLE_FID), paramArrayOfBoolean[j] != 0 ? new Value(0) : new Value());
            if ((paramString != null) && (paramString.length() > 0))
            {
              localEntry.put(Integer.valueOf(QUAL_FID), new Value(paramString));
              localHashMap.put(paramArrayOfString[j], new QuickReportCache(paramArrayOfBoolean[j], paramString));
            }
            else if (localQuickReportCache != null)
            {
              paramString = localQuickReportCache.reportParam;
              localHashMap.put(paramArrayOfString[j], new QuickReportCache(paramArrayOfBoolean[j], paramString));
            }
            localServerLogin.setEntry(this.mPrefForm, localEntry.getEntryId(), localEntry, new Timestamp(), 1);
          }
        }
      }
      return true;
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Unable to update report", localARException);
    }
    return false;
  }

  private QualifierInfo buildQualifier()
  {
    RelationalOperationInfo localRelationalOperationInfo1 = new RelationalOperationInfo(1, USER_OP, new ArithmeticOrRelationalOperand(new Value(this.mUser)));
    RelationalOperationInfo localRelationalOperationInfo2 = new RelationalOperationInfo(1, FORM_OP, new ArithmeticOrRelationalOperand(new Value(this.mForm)));
    RelationalOperationInfo localRelationalOperationInfo3 = new RelationalOperationInfo(1, SERVER_OP, new ArithmeticOrRelationalOperand(new Value(this.mServer)));
    QualifierInfo localQualifierInfo = new QualifierInfo(1, new QualifierInfo(localRelationalOperationInfo1), new QualifierInfo(localRelationalOperationInfo2));
    return new QualifierInfo(1, localQualifierInfo, new QualifierInfo(1, new QualifierInfo(localRelationalOperationInfo3), new QualifierInfo(typeOp)));
  }

  private QualifierInfo buildQualifierForReportDefinition(String paramString, List<Value> paramList)
  {
    RelationalOperationInfo localRelationalOperationInfo1 = null;
    RelationalOperationInfo localRelationalOperationInfo2 = null;
    RelationalOperationInfo localRelationalOperationInfo3 = null;
    RelationalOperationInfo localRelationalOperationInfo4 = null;
    RelationalOperationInfo localRelationalOperationInfo5 = null;
    localRelationalOperationInfo1 = new RelationalOperationInfo(1, USER_OP, new ArithmeticOrRelationalOperand(new Value(this.mUser)));
    localRelationalOperationInfo2 = new RelationalOperationInfo(1, FORM_OP, new ArithmeticOrRelationalOperand(new Value(this.mForm)));
    localRelationalOperationInfo3 = new RelationalOperationInfo(1, SERVER_OP, new ArithmeticOrRelationalOperand(new Value(this.mServer)));
    localRelationalOperationInfo4 = new RelationalOperationInfo(1, TYPE_OP, new ArithmeticOrRelationalOperand(new Value(3)));
    QualifierInfo localQualifierInfo = null;
    if ((paramList == null) || (paramList.size() == 1))
    {
      localObject1 = paramString == null ? ((Value)paramList.get(0)).toString() : paramString;
      localRelationalOperationInfo5 = new RelationalOperationInfo(1, NAME_OP, new ArithmeticOrRelationalOperand(new Value((String)localObject1)));
    }
    else
    {
      localObject1 = new RelationalOperationInfo(1, NAME_OP, new ArithmeticOrRelationalOperand((Value)paramList.get(0)));
      for (int i = 1; i < paramList.size(); i++)
      {
        Value localValue = (Value)paramList.get(i);
        localObject2 = new RelationalOperationInfo(1, NAME_OP, new ArithmeticOrRelationalOperand(localValue));
        if (localQualifierInfo == null)
          localQualifierInfo = new QualifierInfo(2, new QualifierInfo((RelationalOperationInfo)localObject1), new QualifierInfo((RelationalOperationInfo)localObject2));
        else
          localQualifierInfo = new QualifierInfo(2, new QualifierInfo((RelationalOperationInfo)localObject2), localQualifierInfo);
      }
    }
    Object localObject1 = new QualifierInfo(1, new QualifierInfo(localRelationalOperationInfo1), new QualifierInfo(localRelationalOperationInfo2));
    Object localObject2 = new QualifierInfo(1, new QualifierInfo(localRelationalOperationInfo3), new QualifierInfo(localRelationalOperationInfo4));
    if (localRelationalOperationInfo5 != null)
      return new QualifierInfo(1, (QualifierInfo)localObject1, new QualifierInfo(1, new QualifierInfo(localRelationalOperationInfo5), new QualifierInfo(1, (QualifierInfo)localObject1, (QualifierInfo)localObject2)));
    return new QualifierInfo(1, (QualifierInfo)localObject1, new QualifierInfo(1, localQualifierInfo, new QualifierInfo(1, (QualifierInfo)localObject1, (QualifierInfo)localObject2)));
  }

  public boolean loadQuickReports()
  {
    HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
    if (localHashMap == null)
      return loadQuickReportNameFromServer();
    return localHashMap.size() > 0;
  }

  public boolean loadQuickReportNameFromServer()
  {
    try
    {
      List localList = null;
      ARUserSearches localARUserSearches = this.sesdata.getUserSearches();
      Object localObject;
      if (localARUserSearches != null)
      {
        localList = localARUserSearches.getQREntries();
      }
      else
      {
        localObject = getUserContext(this.sesdata);
        if (localObject == null)
          return false;
        QualifierInfo localQualifierInfo = buildQualifier();
        localList = ((ServerLogin)localObject).getListEntryObjects(this.mPrefForm, localQualifierInfo, 0, 0, new ArrayList(), qrfieldIds, false, null);
      }
      if ((localList != null) && (localList.size() > 0))
      {
        populateReportList((Entry[])localList.toArray(new Entry[0]));
      }
      else
      {
        localObject = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
        if (localObject != null)
          ((HashMap)localObject).clear();
      }
      return true;
    }
    catch (ARException localARException)
    {
      MLog.log(Level.WARNING, "Error loading quick reports from server " + this.mPrefServer, localARException);
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.WARNING, "Error loading quick reports from server " + this.mPrefServer, localGoatException);
    }
    return false;
  }

  public String retrieveDefinition(String paramString1, String paramString2)
  {
    QualifierInfo localQualifierInfo = buildQualifierForReportDefinition(paramString2, null);
    try
    {
      HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
      if (localHashMap != null)
      {
        localObject = (QuickReportCache)localHashMap.get(paramString2);
        if ((localObject != null) && (((QuickReportCache)localObject).reportParam.length() != 0))
          return ((QuickReportCache)localObject).reportParam;
      }
      Object localObject = getUserContext(this.sesdata);
      if (localObject == null)
        return null;
      int[] arrayOfInt = { QUAL_FID };
      List localList = ((ServerLogin)localObject).getListEntryObjects(this.mPrefForm, localQualifierInfo, 0, 0, new ArrayList(), arrayOfInt, false, null);
      if (localList.size() > 0)
      {
        Entry localEntry = (Entry)localList.get(0);
        String str = "";
        Iterator localIterator = localEntry.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry1 = (Map.Entry)localIterator.next();
          if (((Integer)localEntry1.getKey()).intValue() == QUAL_FID)
          {
            str = (String)((Value)localEntry1.getValue()).getValue();
            return str;
          }
        }
        return null;
      }
    }
    catch (ARException localARException)
    {
      MLog.log(Level.WARNING, "Error loading quick reports from server " + paramString1, localARException);
      return null;
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.WARNING, "Error loading quick reports from server " + paramString1, localGoatException);
      return null;
    }
    return null;
  }

  public boolean deleteQuickReport(SessionData paramSessionData, String[] paramArrayOfString)
    throws GoatException
  {
    ServerLogin localServerLogin1 = getUserContext(paramSessionData);
    ServerLogin localServerLogin2 = getAdminContext();
    if ((localServerLogin2 == null) || (localServerLogin1 == null))
      return false;
    int i = paramArrayOfString.length;
    ArrayList localArrayList = new ArrayList();
    HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
    for (int j = 0; j < i; j++)
      if (localHashMap != null)
      {
        localObject = (QuickReportCache)localHashMap.get(paramArrayOfString[j]);
        if ((localObject != null) && (((QuickReportCache)localObject).reportParam.length() > 0))
          numReportsInCache -= 1;
        localHashMap.remove(paramArrayOfString[j]);
        localArrayList.add(new Value(paramArrayOfString[j]));
      }
    QualifierInfo localQualifierInfo = buildQualifierForReportDefinition(null, localArrayList);
    Object localObject = { NAME_FID };
    try
    {
      List localList = localServerLogin1.getListEntryObjects(this.mPrefForm, localQualifierInfo, 0, 0, new ArrayList(), (int[])localObject, false, null);
      ListIterator localListIterator = localList.listIterator();
      while (localListIterator.hasNext())
      {
        Entry localEntry = (Entry)localListIterator.next();
        localServerLogin2.deleteEntry(this.mPrefForm, localEntry.getKey(), 0);
      }
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Error bulkentrytransaction to deleteReports ", localARException);
    }
    return true;
  }

  private void populateReportList(Entry[] paramArrayOfEntry)
  {
    HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
    if (localHashMap == null)
    {
      localHashMap = new HashMap();
      Configuration.getInstance();
      String str1 = Configuration.getInstance().getProperty("arsystem.myreport.report_cache_limit", "" + 20);
      int i = new Integer(str1).intValue();
      for (int j = 0; j < paramArrayOfEntry.length; j++)
      {
        boolean bool = false;
        Value localValue = (Value)paramArrayOfEntry[j].get(Integer.valueOf(DISABLE_FID));
        if (localValue != null)
          bool = localValue.getValue() != null;
        String str2 = j >= i ? "" : ((Value)paramArrayOfEntry[j].get(Integer.valueOf(QUAL_FID))).toString();
        if (str2.length() > 0)
          numReportsInCache += 1;
        localHashMap.put(((Value)paramArrayOfEntry[j].get(Integer.valueOf(NAME_FID))).toString(), new QuickReportCache(bool, str2));
      }
      this.sesdata.getMQuickReports().put(getCacheKey(), localHashMap);
    }
  }

  public void emitSavedJS(JSWriter paramJSWriter)
    throws GoatException
  {
    Configuration.getInstance();
    String str1 = Configuration.getInstance().getProperty("arsystem.myreport.report_cache_limit", "" + 20);
    int i = new Integer(str1).intValue();
    paramJSWriter.startStatement("this.maxQuickReportSize=" + i + ",");
    HashMap localHashMap = (HashMap)this.sesdata.getMQuickReports().get(getCacheKey());
    if ((localHashMap == null) || (localHashMap.size() == 0))
    {
      paramJSWriter.continueStatement("this.quickReportsExisting={},");
      paramJSWriter.continueStatement("this.curQuickReportSize=0");
      return;
    }
    paramJSWriter.continueStatement("this.quickReportsExisting=");
    paramJSWriter.openObj();
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str2 = (String)localEntry.getKey();
      QuickReportCache localQuickReportCache = (QuickReportCache)localEntry.getValue();
      paramJSWriter.listSep().append("\"" + JSWriter.escape(str2) + "\":");
      paramJSWriter.openObj();
      paramJSWriter.property("d", !localQuickReportCache.disabled ? 0 : 1);
      paramJSWriter.property("u", localQuickReportCache.reportParam);
      paramJSWriter.closeObj();
    }
    paramJSWriter.closeObj();
    paramJSWriter.comma();
    paramJSWriter.continueStatement("this.curQuickReportSize=" + numReportsInCache);
  }

  public String getCacheKey()
  {
    return (this.mServer + "/" + this.mForm + "/" + this.mUser).intern();
  }

  public String getQBEMatchValue(String[] paramArrayOfString)
  {
    String str = "";
    try
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        int j = new Integer(paramArrayOfString[i]).intValue();
        Field localField = localServerLogin.getField(this.mForm, j);
        if ((localField.getFieldType() == 1) && (localField.getDataType() == 4))
        {
          int k = ((CharacterFieldLimit)localField.getFieldLimit()).getQBEMatch();
          if ((i > 0) && (str.length() > 0))
            str = str + ",";
          str = str + j + ":" + k;
        }
      }
      return str;
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Error retrieving QBEMatch Info ", localGoatException);
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Error retrieving QBEMatch Info ", localARException);
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.quickreports.ARUserQuickReports
 * JD-Core Version:    0.6.1
 */