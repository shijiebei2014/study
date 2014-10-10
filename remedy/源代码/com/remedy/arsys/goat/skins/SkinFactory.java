package com.remedy.arsys.goat.skins;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FormType;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class SkinFactory
{
  private final ServerLogin mUserCtxt;
  private String mViewName = "";
  private String mViewLabel = "";
  private String mViewForm = "";
  private String mForm = "";
  private String mApp = "";
  private String mServer = "";
  private String mTag = "";
  private static String mSkinSchema;
  private static String mSkinPropSchema;
  private static int CREATE_DATE = 3;
  private static int SKIN_TYPE = 52000;
  private static int SKIN_TYPE_ID = 52001;
  private static int SKIN_STATE = 7;
  private static int SKIN_VIEW_FORM = 52002;
  private static int SKIN_GUID = 179;
  private static int SKIN_PROP_TYPE = 52101;
  private static int SKIN_PROP_TYPE_ID = 52102;
  private static int SKIN_PROP_PROPERTY_TAG = 52103;
  private static int SKIN_PROP_PROPERTY_VALUE = 52104;
  private static int SKIN_PROP_GUID = 52100;
  private static int SKIN_PRIORITY_TYPE = 50106;
  private static int DVM_SKIN_PROP_PROPERTY = 50107;
  private static int SKIN_PROP_ASCENDING_SORT_ORDER = 1;
  protected static int[] SKIN_SCHEMA_KEY = { SKIN_TYPE, SKIN_TYPE_ID };
  protected static int[] SKIN_PROP_SCHEMA_KEY = { SKIN_PROP_TYPE, SKIN_PROP_TYPE_ID, SKIN_PROP_PROPERTY_TAG, SKIN_PROP_PROPERTY_VALUE };
  private static final ArithmeticOrRelationalOperand SKIN_PROP_GUID_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_PROP_GUID);
  private static final ArithmeticOrRelationalOperand SKIN_PROP_TYPE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_PROP_TYPE);
  private static final ArithmeticOrRelationalOperand SKIN_PROP_TYPE_ID_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_PROP_TYPE_ID);
  private static final ArithmeticOrRelationalOperand SKIN_TYPE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_TYPE);
  private static final ArithmeticOrRelationalOperand SKIN_TYPE_ID_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_TYPE_ID);
  protected static final ArithmeticOrRelationalOperand SKIN_STATE_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_STATE);
  protected static final ArithmeticOrRelationalOperand SKIN_GUID_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_GUID);
  private static final ArithmeticOrRelationalOperand SKIN_VIEW_FORM_OP = new ArithmeticOrRelationalOperand(OperandType.FIELDID, SKIN_VIEW_FORM);
  private static transient Log MLog = Log.get(2);

  public SkinFactory(ServerLogin paramServerLogin, Form.ViewInfo paramViewInfo)
  {
    this.mUserCtxt = paramServerLogin;
    this.mViewName = paramViewInfo.mName;
    this.mViewLabel = paramViewInfo.mLabel;
    this.mViewForm = paramViewInfo.getForm().getName();
    this.mForm = paramViewInfo.getForm().getName();
    this.mApp = FormContext.get().getApplication();
    this.mTag = paramViewInfo.getForm().getTagName();
    this.mServer = paramViewInfo.getForm().getServer();
  }

  private synchronized QualifierInfo buildARSkinQual()
  {
    QualifierInfo localQualifierInfo1 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(0)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(this.mViewName)))));
    QualifierInfo localQualifierInfo2 = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(1, SKIN_VIEW_FORM_OP, new ArithmeticOrRelationalOperand(new Value(this.mForm)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_VIEW_FORM_OP, new ArithmeticOrRelationalOperand(new Value()))));
    QualifierInfo localQualifierInfo3 = new QualifierInfo(1, localQualifierInfo1, localQualifierInfo2);
    QualifierInfo localQualifierInfo4 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(1)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(this.mViewName)))));
    QualifierInfo localQualifierInfo5 = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(1, SKIN_VIEW_FORM_OP, new ArithmeticOrRelationalOperand(new Value(this.mForm)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_VIEW_FORM_OP, new ArithmeticOrRelationalOperand(new Value()))));
    QualifierInfo localQualifierInfo6 = new QualifierInfo(1, localQualifierInfo4, localQualifierInfo5);
    QualifierInfo localQualifierInfo7 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(2)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(this.mForm)))));
    QualifierInfo localQualifierInfo8 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(3)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(this.mTag)))));
    QualifierInfo localQualifierInfo9 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(4)))), new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(this.mApp)))));
    QualifierInfo localQualifierInfo10 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(5)))), new QualifierInfo(new RelationalOperationInfo(7, SKIN_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(Configuration.getInstance().getShortName(this.mServer) + "%")))));
    QualifierInfo localQualifierInfo11 = new QualifierInfo(2, localQualifierInfo3, localQualifierInfo6);
    QualifierInfo localQualifierInfo12 = new QualifierInfo(2, localQualifierInfo11, localQualifierInfo7);
    QualifierInfo localQualifierInfo13 = new QualifierInfo(2, localQualifierInfo12, localQualifierInfo8);
    QualifierInfo localQualifierInfo14 = new QualifierInfo(2, localQualifierInfo13, localQualifierInfo9);
    QualifierInfo localQualifierInfo15 = new QualifierInfo(2, localQualifierInfo14, localQualifierInfo10);
    QualifierInfo localQualifierInfo16 = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, SKIN_STATE_OP, new ArithmeticOrRelationalOperand(new Value(1)))), localQualifierInfo15);
    return localQualifierInfo16;
  }

  private synchronized QualifierInfo buildARSkinPropQual(ArrayList<String> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramArrayList == null)
      return null;
    int i = paramArrayList.size();
    QualifierInfo localQualifierInfo = null;
    if (i > 0)
    {
      for (int j = 0; j < i; j++)
        localArrayList.add(new RelationalOperationInfo(1, SKIN_PROP_GUID_OP, new ArithmeticOrRelationalOperand(new Value((String)paramArrayList.get(j)))));
      localQualifierInfo = new QualifierInfo((RelationalOperationInfo)localArrayList.get(0));
      for (j = 1; j < i; j++)
        localQualifierInfo = new QualifierInfo(2, localQualifierInfo, new QualifierInfo((RelationalOperationInfo)localArrayList.get(j)));
    }
    return localQualifierInfo;
  }

  public SkinDefinitionMap getSkinDefinitions()
  {
    SkinDefinitionList localSkinDefinitionList = getSkins();
    return getSkinProperties(localSkinDefinitionList, -1, "");
  }

  public SkinDefinitionMap getSkinProperties(SkinDefinitionList paramSkinDefinitionList, int paramInt, String paramString)
  {
    return getSkinProperties(paramSkinDefinitionList, paramInt, paramString, new HashSet());
  }

  public SkinDefinitionMap getSkinProperties(SkinDefinitionList paramSkinDefinitionList, int paramInt, String paramString, Set<String> paramSet)
  {
    SkinDefinitionMap localSkinDefinitionMap = new SkinDefinitionMap();
    try
    {
      QualifierInfo localQualifierInfo = null;
      ArrayList localArrayList = new ArrayList();
      if (paramInt == 3)
      {
        localQualifierInfo = buildARSystemSkinPropQual(paramSkinDefinitionList.getSkins(), 3);
        localArrayList.add(new SortInfo(SKIN_PRIORITY_TYPE, SKIN_PROP_ASCENDING_SORT_ORDER));
      }
      else if (paramInt == 4)
      {
        localQualifierInfo = buildDVMSkinPropQual(paramSkinDefinitionList.getSkins(), 4L, paramString);
        localArrayList.add(new SortInfo(SKIN_PRIORITY_TYPE, SKIN_PROP_ASCENDING_SORT_ORDER));
      }
      else if (paramInt == 5)
      {
        localQualifierInfo = buildTemplateSkinPropQual(paramSkinDefinitionList.getSkins(), paramInt, paramSet);
        localArrayList.add(new SortInfo(SKIN_PRIORITY_TYPE, SKIN_PROP_ASCENDING_SORT_ORDER));
      }
      else if (paramInt == 6)
      {
        localQualifierInfo = buildARSystemSkinPropQual(paramSkinDefinitionList.getSkins(), 6);
        localArrayList.add(new SortInfo(SKIN_PRIORITY_TYPE, SKIN_PROP_ASCENDING_SORT_ORDER));
      }
      else
      {
        localQualifierInfo = buildARSkinPropQual(paramSkinDefinitionList.getSkins());
        localArrayList.add(new SortInfo(SKIN_PROP_GUID, SKIN_PROP_ASCENDING_SORT_ORDER));
      }
      if (localQualifierInfo == null)
        return localSkinDefinitionMap;
      List localList = this.mUserCtxt.getListEntryObjects(mSkinPropSchema, localQualifierInfo, 0, 0, localArrayList, new int[] { SKIN_PROP_GUID, SKIN_PROP_TYPE, SKIN_PROP_TYPE_ID, SKIN_PROP_PROPERTY_TAG, SKIN_PROP_PROPERTY_VALUE, DVM_SKIN_PROP_PROPERTY }, false, null);
      if (localList != null)
      {
        Entry[] arrayOfEntry = (Entry[])localList.toArray(new Entry[0]);
        for (int i = 0; i < arrayOfEntry.length; i++)
        {
          String str = ((Value)arrayOfEntry[i].get(Integer.valueOf(SKIN_PROP_GUID))).toString();
          long l = paramSkinDefinitionList.getModifiedTime(str);
          Skin localSkin = localSkinDefinitionMap.getSkin(str);
          if (localSkin == null)
          {
            localSkin = new Skin();
            localSkin.setMGUID(str);
            localSkin.setLastModified(l);
            localSkin.setMServer(this.mServer);
            localSkin.setFormList(paramSkinDefinitionList.getFormList(str));
            localSkinDefinitionMap.addSkin(str, localSkin);
          }
          getSkinDetails(str, arrayOfEntry[i], l, localSkin);
        }
      }
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Unable to retrieve Skin Properties - " + localARException.getMessage(), localARException);
    }
    catch (Exception localException)
    {
      MLog.log(Level.SEVERE, "Unable to retrieve Skin Properties - " + localException.getMessage(), localException);
    }
    return localSkinDefinitionMap;
  }

  public SkinDefinitionList getSkins()
  {
    SkinDefinitionList localSkinDefinitionList = new SkinDefinitionList();
    boolean bool = initSchema();
    if (!bool)
      return localSkinDefinitionList;
    QualifierInfo localQualifierInfo = buildARSkinQual();
    int[] arrayOfInt = { SKIN_GUID, SKIN_TYPE, SKIN_TYPE_ID, CREATE_DATE };
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new SortInfo(SKIN_TYPE, 1));
    localArrayList.add(new SortInfo(SKIN_VIEW_FORM, 2));
    localArrayList.add(new SortInfo(CREATE_DATE, 2));
    try
    {
      List localList = this.mUserCtxt.getListEntryObjects(mSkinSchema, localQualifierInfo, 0, 0, localArrayList, arrayOfInt, false, null);
      if (localList == null)
        return localSkinDefinitionList;
      Entry[] arrayOfEntry = (Entry[])localList.toArray(new Entry[0]);
      for (int i = 0; i < arrayOfEntry.length; i++)
      {
        long l = 0L;
        if (arrayOfEntry[i].get(Integer.valueOf(CREATE_DATE)) != null)
          l = ((Timestamp)((Value)arrayOfEntry[i].get(Integer.valueOf(CREATE_DATE))).getValue()).getValue();
        String str = ((Value)arrayOfEntry[i].get(Integer.valueOf(SKIN_GUID))).toString();
        localSkinDefinitionList.addSkin(str, l);
        if (this.mForm != null)
          localSkinDefinitionList.addForm(str, this.mForm);
      }
      return localSkinDefinitionList;
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Unable to retrieve Skins List", localARException);
    }
    return localSkinDefinitionList;
  }

  public Skin getSkinDetails(String paramString, Entry paramEntry, long paramLong, Skin paramSkin)
  {
    int i = -1;
    Value localValue = (Value)paramEntry.get(Integer.valueOf(SKIN_PROP_TYPE));
    if (localValue != null)
      i = new Integer(localValue.getValue().toString()).intValue();
    localValue = (Value)paramEntry.get(Integer.valueOf(SKIN_PROP_TYPE_ID));
    String str1 = null;
    if (localValue != null)
      str1 = localValue.getValue().toString();
    String str2 = null;
    if (str1 != null)
      if ((i == 4) || (i == 5) || (i == 6))
      {
        localValue = (Value)paramEntry.get(Integer.valueOf(DVM_SKIN_PROP_PROPERTY));
        if (localValue != null)
          str2 = str1 + "/" + localValue.getValue().toString();
      }
      else
      {
        localValue = (Value)paramEntry.get(Integer.valueOf(SKIN_PROP_PROPERTY_TAG));
        if (localValue != null)
          str2 = str1 + "/" + localValue.getValue().toString();
      }
    localValue = (Value)paramEntry.get(Integer.valueOf(SKIN_PROP_PROPERTY_VALUE));
    String str3 = null;
    if (localValue != null)
      str3 = localValue.getValue().toString();
    if ((str2 != null) && (str3 != null))
      if (i == 0)
        paramSkin.putMPropIdMapEntry(str2, str3);
      else if (i == 1)
        paramSkin.putMPropStyleMapEntry(str2, str3);
      else if (i == 2)
        paramSkin.putMPropTypeMapEntry(str2, str3);
      else if (i == 3)
        paramSkin.putMSysPropMapEntry(str2, str3);
      else if (i == 4)
        paramSkin.putMDVMPropMapEntry(str2, str3);
      else if (i == 5)
        paramSkin.putMTemplatePropMapEntry(str2, str3);
      else if (i == 6)
        paramSkin.putMWebHeaderPropMapEntry(str2, str3);
    return paramSkin;
  }

  private boolean initSchema()
  {
    List localList = null;
    try
    {
      localList = ServerLogin.getAdmin(this.mServer).getListForm(0L, FormType.ALL.toInt() | 0x400, null, SKIN_SCHEMA_KEY);
      if ((localList != null) && (localList.size() > 0))
      {
        mSkinSchema = (String)localList.get(0);
      }
      else
      {
        MLog.log(Level.FINE, "\"AR System Skins\" form is not available in the server: " + this.mServer + ", schema: " + this.mForm + ", view: " + this.mViewLabel);
        return false;
      }
      localList = ServerLogin.getAdmin(this.mServer).getListForm(0L, FormType.ALL.toInt() | 0x400, null, SKIN_PROP_SCHEMA_KEY);
      if ((localList != null) && (localList.size() > 0))
      {
        mSkinPropSchema = (String)localList.get(0);
      }
      else
      {
        MLog.log(Level.FINE, "\"AR System Skins\" form is not available in the server: " + this.mServer + ", schema: " + this.mForm + ", view: " + this.mViewLabel);
        return false;
      }
    }
    catch (ARException localARException)
    {
      MLog.log(Level.SEVERE, "Unable to instantiate Skins Forms", localARException);
      return false;
    }
    catch (GoatException localGoatException)
    {
      MLog.log(Level.SEVERE, "Unable to instantiate Skins Forms", localGoatException);
      return false;
    }
    return true;
  }

  private synchronized QualifierInfo buildARSystemSkinPropQual(ArrayList<String> paramArrayList, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramArrayList == null)
      return null;
    int i = paramArrayList.size();
    QualifierInfo localQualifierInfo1 = null;
    if (i > 0)
    {
      for (int j = 0; j < i; j++)
        localArrayList.add(new RelationalOperationInfo(1, SKIN_PROP_GUID_OP, new ArithmeticOrRelationalOperand(new Value((String)paramArrayList.get(j)))));
      localQualifierInfo1 = new QualifierInfo((RelationalOperationInfo)localArrayList.get(0));
      for (j = 1; j < i; j++)
        localQualifierInfo1 = new QualifierInfo(2, localQualifierInfo1, new QualifierInfo((RelationalOperationInfo)localArrayList.get(j)));
    }
    RelationalOperationInfo localRelationalOperationInfo = new RelationalOperationInfo(1, SKIN_PROP_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(paramInt)));
    QualifierInfo localQualifierInfo2 = new QualifierInfo(localRelationalOperationInfo);
    QualifierInfo localQualifierInfo3 = new QualifierInfo(1, localQualifierInfo1, localQualifierInfo2);
    return localQualifierInfo3;
  }

  private synchronized QualifierInfo buildDVMSkinPropQual(ArrayList<String> paramArrayList, long paramLong, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramArrayList == null)
      return null;
    int i = paramArrayList.size();
    QualifierInfo localQualifierInfo1 = null;
    if (i > 0)
    {
      for (int j = 0; j < i; j++)
        localArrayList.add(new RelationalOperationInfo(1, SKIN_PROP_GUID_OP, new ArithmeticOrRelationalOperand(new Value((String)paramArrayList.get(j)))));
      localQualifierInfo1 = new QualifierInfo((RelationalOperationInfo)localArrayList.get(0));
      for (j = 1; j < i; j++)
        localQualifierInfo1 = new QualifierInfo(2, localQualifierInfo1, new QualifierInfo((RelationalOperationInfo)localArrayList.get(j)));
    }
    RelationalOperationInfo localRelationalOperationInfo1 = new RelationalOperationInfo(1, SKIN_PROP_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(4)));
    RelationalOperationInfo localRelationalOperationInfo2 = new RelationalOperationInfo(1, SKIN_PROP_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value(paramString)));
    QualifierInfo localQualifierInfo2 = new QualifierInfo(localRelationalOperationInfo1);
    QualifierInfo localQualifierInfo3 = new QualifierInfo(localRelationalOperationInfo2);
    QualifierInfo localQualifierInfo4 = new QualifierInfo(1, localQualifierInfo1, localQualifierInfo2);
    QualifierInfo localQualifierInfo5 = new QualifierInfo(1, localQualifierInfo4, localQualifierInfo3);
    return localQualifierInfo5;
  }

  private synchronized QualifierInfo buildTemplateSkinPropQual(ArrayList<String> paramArrayList, int paramInt, Set<String> paramSet)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    if (paramArrayList == null)
      return null;
    int i = paramArrayList.size();
    QualifierInfo localQualifierInfo1 = null;
    if (i > 0)
    {
      for (int j = 0; j < i; j++)
        localArrayList1.add(new RelationalOperationInfo(1, SKIN_PROP_GUID_OP, new ArithmeticOrRelationalOperand(new Value((String)paramArrayList.get(j)))));
      localQualifierInfo1 = new QualifierInfo((RelationalOperationInfo)localArrayList1.get(0));
      for (j = 1; j < i; j++)
        localQualifierInfo1 = new QualifierInfo(2, localQualifierInfo1, new QualifierInfo((RelationalOperationInfo)localArrayList1.get(j)));
    }
    RelationalOperationInfo localRelationalOperationInfo = new RelationalOperationInfo(1, SKIN_PROP_TYPE_OP, new ArithmeticOrRelationalOperand(new Value(paramInt)));
    int k = paramSet.size();
    QualifierInfo localQualifierInfo2 = null;
    if (k > 0)
    {
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        localArrayList2.add(new RelationalOperationInfo(1, SKIN_PROP_TYPE_ID_OP, new ArithmeticOrRelationalOperand(new Value((String)localObject))));
      }
      localQualifierInfo2 = new QualifierInfo((RelationalOperationInfo)localArrayList2.get(0));
      for (int m = 1; m < k; m++)
        localQualifierInfo2 = new QualifierInfo(2, localQualifierInfo2, new QualifierInfo((RelationalOperationInfo)localArrayList2.get(m)));
    }
    QualifierInfo localQualifierInfo3 = new QualifierInfo(localRelationalOperationInfo);
    Object localObject = new QualifierInfo(1, localQualifierInfo1, localQualifierInfo3);
    QualifierInfo localQualifierInfo4 = new QualifierInfo(1, (QualifierInfo)localObject, localQualifierInfo2);
    return localQualifierInfo4;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.skins.SkinFactory
 * JD-Core Version:    0.6.1
 */