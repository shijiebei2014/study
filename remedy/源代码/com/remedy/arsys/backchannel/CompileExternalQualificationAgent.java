package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARQualifierHelper;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.share.ARQualifierHelper2;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CompileExternalQualificationAgent extends NDXCompileExternalQualification
{
  public CompileExternalQualificationAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    Qualifier localQualifier = compileExternalQualification(this.mCurrentServer, this.mCurrentSchema, this.mCurrentVui, this.mRemoteServer, this.mRemoteSchema, this.mQualification, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
    append("this.result=").append(localQualifier.emitEncodedAsString());
  }

  private static final int[] getIntArray(List<Integer> paramList)
  {
    int i = paramList.size();
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++)
      arrayOfInt[j] = ((Integer)paramList.get(j)).intValue();
    return arrayOfInt;
  }

  public static Object[][] splitFields(int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, boolean[] paramArrayOfBoolean)
  {
    Object[][] arrayOfObject = new Object[2][3];
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList5 = new ArrayList();
    ArrayList localArrayList6 = new ArrayList();
    for (int i = 0; i < paramArrayOfInt1.length; i++)
      if (paramArrayOfBoolean[i] != 0)
      {
        localArrayList1.add(Integer.valueOf(paramArrayOfInt1[i]));
        localArrayList2.add(paramArrayOfString[i]);
        localArrayList3.add(Integer.valueOf(paramArrayOfInt2[i]));
      }
      else
      {
        localArrayList4.add(Integer.valueOf(paramArrayOfInt1[i]));
        localArrayList5.add(paramArrayOfString[i]);
        localArrayList6.add(Integer.valueOf(paramArrayOfInt2[i]));
      }
    int[] arrayOfInt1 = getIntArray(localArrayList1);
    String[] arrayOfString1 = new String[localArrayList2.size()];
    System.arraycopy(localArrayList2.toArray(new String[0]), 0, arrayOfString1, 0, localArrayList2.size());
    int[] arrayOfInt2 = getIntArray(localArrayList3);
    int[] arrayOfInt3 = getIntArray(localArrayList4);
    String[] arrayOfString2 = new String[localArrayList5.size()];
    System.arraycopy(localArrayList5.toArray(new String[0]), 0, arrayOfString2, 0, localArrayList5.size());
    int[] arrayOfInt4 = getIntArray(localArrayList6);
    arrayOfObject[0][0] = arrayOfInt1;
    arrayOfObject[0][1] = arrayOfString1;
    arrayOfObject[0][2] = arrayOfInt2;
    arrayOfObject[1][0] = arrayOfInt3;
    arrayOfObject[1][1] = arrayOfString2;
    arrayOfObject[1][2] = arrayOfInt4;
    return arrayOfObject;
  }

  public static Qualifier compileExternalQualification(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2)
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString4);
    CachedFieldMap localCachedFieldMap1 = Form.get(paramString4, paramString5).getCachedFieldMap(true);
    CachedFieldMap localCachedFieldMap2 = Form.get(paramString1, paramString2).getCachedFieldMap();
    if ((paramArrayOfInt1.length != paramArrayOfString.length) || (paramArrayOfInt1.length != paramArrayOfInt2.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    Entry localEntry = buildEntryItems(localServerLogin.getServer(), paramArrayOfInt1, paramArrayOfString, paramArrayOfInt2);
    HashMap localHashMap = new HashMap();
    Object localObject1 = localEntry.entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      localObject3 = ((Value)((Map.Entry)localObject2).getValue()).toString();
      if (ARQualifier.isEncodedQualStr((String)localObject3))
      {
        QualifierInfo localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(localServerLogin, (String)localObject3);
        localHashMap.put(Integer.valueOf(((Integer)((Map.Entry)localObject2).getKey()).intValue()), new ARQualifier(localQualifierInfo));
      }
      else
      {
        try
        {
          localHashMap.put(Integer.valueOf(((Integer)((Map.Entry)localObject2).getKey()).intValue()), new ARQualifier(localServerLogin, (String)localObject3, localCachedFieldMap1, localCachedFieldMap2, 1));
        }
        catch (GoatException localGoatException)
        {
          Form.ViewInfo localViewInfo1 = Form.get(paramString4, paramString5).getViewInfoByInference(null, false, true);
          if (localViewInfo1 == null)
            throw localGoatException;
          String str = localViewInfo1.getLabel() != null ? localViewInfo1.getLabel() : localViewInfo1.getName();
          FieldGraph localFieldGraph1 = FieldGraph.get(paramString4, paramString5, str);
          FieldGraph localFieldGraph2 = FieldGraph.get(paramString1, paramString2, paramString3);
          ARQualifierHelper2 localARQualifierHelper2 = new ARQualifierHelper2();
          ARQualifierHelper localARQualifierHelper1 = localFieldGraph1.GetQualifierHelper();
          ARQualifierHelper localARQualifierHelper3 = localFieldGraph2.GetQualifierHelper();
          localARQualifierHelper2.setLocalFieldIdMap(localARQualifierHelper3.getLocalFieldIdMap());
          Form.ViewInfo localViewInfo2 = Form.get(paramString1, paramString2).getViewInfoByInference(paramString3, false, false);
          localARQualifierHelper2.setLocalFieldLabelMap(localARQualifierHelper3.getLocalFieldLabelMap(), localViewInfo2.mID);
          localARQualifierHelper2.setRemoteFieldIdMap(localARQualifierHelper1.getLocalFieldIdMap());
          localARQualifierHelper2.setRemoteFieldNameMap(localARQualifierHelper1.getLocalFieldLabelMap());
          localHashMap.put(Integer.valueOf(((Integer)((Map.Entry)localObject2).getKey()).intValue()), new ARQualifier(localServerLogin, (String)localObject3, localARQualifierHelper2));
        }
      }
    }
    localObject1 = Qualifier.ARDecodeARQualifierStruct(localServerLogin, paramString6);
    Object localObject2 = new ARQualifier((QualifierInfo)localObject1);
    ((ARQualifier)localObject2).substituteExternal(localHashMap);
    Object localObject3 = new Qualifier(((ARQualifier)localObject2).getQualInfo(), localServerLogin.getServer());
    return localObject3;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.CompileExternalQualificationAgent
 * JD-Core Version:    0.6.1
 */