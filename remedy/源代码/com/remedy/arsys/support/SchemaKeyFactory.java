package com.remedy.arsys.support;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.FormType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaKeyFactory
{
  private static final SchemaKeyFactory MInstance = new SchemaKeyFactory();
  private final Map<SchemaID, String> mServerSchemaIDsToSchemaKeyMap = Collections.synchronizedMap(new HashMap());

  public static SchemaKeyFactory getInstance()
  {
    return MInstance;
  }

  public String getSchemaKey(ARServerUser paramARServerUser, int[] paramArrayOfInt, int paramInt)
    throws ARException
  {
    String str1 = paramARServerUser.getServer();
    SchemaID localSchemaID = new SchemaID(str1, paramArrayOfInt, paramInt, null);
    String str2 = (String)this.mServerSchemaIDsToSchemaKeyMap.get(localSchemaID);
    if (str2 == null)
    {
      String str3 = ("SchemaKeyFactory:" + str1).intern();
      synchronized (str3)
      {
        str2 = findSchemaByFieldIDs(paramARServerUser, paramArrayOfInt, paramInt);
        this.mServerSchemaIDsToSchemaKeyMap.put(localSchemaID, str2);
      }
    }
    return str2;
  }

  public String getSchemaKey(ARServerUser paramARServerUser, int[] paramArrayOfInt)
    throws ARException
  {
    return getSchemaKey(paramARServerUser, paramArrayOfInt, FormType.ALL.toInt());
  }

  private synchronized String findSchemaByFieldIDs(ARServerUser paramARServerUser, int[] paramArrayOfInt, int paramInt)
    throws ARException
  {
    String str = null;
    List localList = paramARServerUser.getListForm(0L, paramInt | 0x400, null, paramArrayOfInt);
    if ((localList == null) || (localList.size() == 0) || ((localList.size() > 0) && (((String)localList.get(0)).equals(" "))))
      return null;
    str = (String)localList.get(0);
    return str;
  }

  private class SchemaID
  {
    private String mServer;
    private int[] mIDs;
    private int mHashCode;
    private int mFormType;

    private SchemaID(String paramArrayOfInt, int[] paramInt, int arg4)
    {
      assert ((paramArrayOfInt != null) && (paramInt != null));
      this.mServer = paramArrayOfInt;
      this.mIDs = paramInt;
      int i;
      this.mFormType = i;
      for (int j = paramInt.length - 1; j >= 0; j--)
        this.mHashCode += 31 * this.mHashCode + paramInt[j];
      this.mHashCode += 31 * this.mHashCode + this.mServer.hashCode();
      this.mHashCode = (31 * this.mHashCode + this.mFormType);
    }

    public boolean equals(Object paramObject)
    {
      if ((paramObject != null) && ((paramObject instanceof SchemaID)))
      {
        SchemaID localSchemaID = (SchemaID)paramObject;
        return (Arrays.equals(this.mIDs, localSchemaID.mIDs)) && (this.mServer.equals(localSchemaID.mServer)) && (this.mFormType == localSchemaID.mFormType);
      }
      return false;
    }

    public int hashCode()
    {
      return this.mHashCode;
    }

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(this.mServer + " ");
      for (int i = 0; i < this.mIDs.length; i++)
        localStringBuilder.append(this.mIDs[i] + " ");
      localStringBuilder.append(this.mFormType + " ");
      return localStringBuilder.toString();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.SchemaKeyFactory
 * JD-Core Version:    0.6.1
 */