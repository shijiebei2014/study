package com.remedy.arsys.goat.service;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldCriteria;
import com.bmc.arsys.api.ObjectBaseCriteria;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.intf.service.IFormFieldService;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormFieldService
  implements IFormFieldService
{
  private static transient Log MPerformanceLog = Log.get(8);
  private static final int OBJECT_SPECIFIC_CRITERIA_FLAGS_DEFAULT = ObjectBaseCriteria.NAME | ObjectBaseCriteria.PROPERTY_LIST | ObjectBaseCriteria.TIMESTAMP;
  private static final int OBJECT_SPECIFIC_CRITERIA_FLAGS_HELPTEXT = ObjectBaseCriteria.HELP_TEXT;
  private static final int FIELD_SPECIFIC_CRITERIA_FLAGS_DEFAULT = FieldCriteria.CREATE_MODE | FieldCriteria.DATATYPE | FieldCriteria.DEFAULT_VALUE | FieldCriteria.DISPLAY_INSTANCE_LIST | FieldCriteria.FIELD_MAP | FieldCriteria.LIMIT | FieldCriteria.OPTION | FieldCriteria.SETFIELD_OPTION;
  private static final FieldCriteria FIELD_CRITERIA_DEFAULT = new FieldCriteria();
  private static final FieldCriteria FIELD_CRITERIA_HELPTEXT;

  private final ServerLogin getServerLogin(String paramString)
    throws GoatException
  {
    return SessionData.get().getServerLogin(paramString);
  }

  public Field getField(String paramString1, String paramString2, int paramInt)
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(2);
    try
    {
      Field localField1 = getServerLogin(paramString1).getField(paramString2, paramInt, FIELD_CRITERIA_DEFAULT);
      localField2 = localField1;
      return localField2;
    }
    catch (ARException localARException)
    {
      localField2 = null;
      return localField2;
    }
    catch (GoatException localGoatException)
    {
      Field localField2 = null;
      return localField2;
    }
    finally
    {
      localMeasurement.end();
    }
  }

  public Field[] getFields(String paramString1, String paramString2, Integer[] paramArrayOfInteger)
    throws GoatException
  {
    Field[] arrayOfField1 = null;
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(2);
    try
    {
      int i = paramArrayOfInteger.length;
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < i; j++)
        arrayOfInt[j] = paramArrayOfInteger[j].intValue();
      arrayOfField1 = (Field[])getServerLogin(paramString1).getListFieldObjects(paramString2, arrayOfInt, FIELD_CRITERIA_DEFAULT).toArray(new Field[0]);
      Field[] arrayOfField2 = arrayOfField1;
      return arrayOfField2;
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
  }

  public Field[] getFieldsAsAdmin(String paramString1, String paramString2, int paramInt)
    throws GoatException
  {
    long l = new Date().getTime();
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(2);
    try
    {
      Field[] arrayOfField = (Field[])ServerLogin.getAdmin(paramString1).getListFieldObjects(paramString2, paramInt, 0L, FIELD_CRITERIA_DEFAULT).toArray(new Field[0]);
      return arrayOfField;
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      localMeasurement.end();
    }
  }

  public List<List<Integer>> categorizeLocalRemoteFields(String paramString1, String paramString2, List<Integer> paramList)
  {
    ArrayList localArrayList = new ArrayList(2);
    localArrayList.add(new ArrayList());
    localArrayList.add(paramList);
    return localArrayList;
  }

  public Map<Integer, String> getHelpText(String paramString1, String paramString2, Set<Integer> paramSet)
    throws GoatException
  {
    HashMap localHashMap = new HashMap();
    int i = paramSet.size();
    if (i == 0)
      return localHashMap;
    int[] arrayOfInt = new int[i];
    int j = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    int k = 1;
    Object localObject1 = paramSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      int m = ((Integer)((Iterator)localObject1).next()).intValue();
      arrayOfInt[(j++)] = m;
      if (k == 0)
        localStringBuilder.append(",");
      else
        k = 0;
      localStringBuilder.append(m);
    }
    localObject1 = new MeasureTime.Measurement(2);
    long l1 = new Date().getTime();
    Field[] arrayOfField = null;
    try
    {
      arrayOfField = (Field[])SessionData.get().getServerLogin(paramString1).getListFieldObjects(paramString2, arrayOfInt, FIELD_CRITERIA_HELPTEXT).toArray(new Field[0]);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
      ((MeasureTime.Measurement)localObject1).end();
    }
    for (j = 0; j < arrayOfField.length; j++)
    {
      Field localField = arrayOfField[j];
      Integer localInteger = Integer.valueOf(localField.getFieldID());
      String str = localField.getHelpText();
      localHashMap.put(localInteger, str);
    }
    long l2 = new Date().getTime();
    MPerformanceLog.fine("Form.CachedFieldMap: Key " + paramString1 + "/" + paramString2 + " missing field help text took " + (l2 - l1) + " [" + localStringBuilder + "]");
    return localHashMap;
  }

  static
  {
    FIELD_CRITERIA_DEFAULT.setPropertiesToRetrieve(OBJECT_SPECIFIC_CRITERIA_FLAGS_DEFAULT | FIELD_SPECIFIC_CRITERIA_FLAGS_DEFAULT);
    FIELD_CRITERIA_HELPTEXT = new FieldCriteria();
    FIELD_CRITERIA_HELPTEXT.setPropertiesToRetrieve(OBJECT_SPECIFIC_CRITERIA_FLAGS_HELPTEXT);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.FormFieldService
 * JD-Core Version:    0.6.1
 */