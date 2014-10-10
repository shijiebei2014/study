package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.stubs.SessionData;
import java.io.OutputStream;

public class GetQBETableEntryListAgent extends NDXGetQBETableEntryList
{
  GetQBETableEntryListAgent(String paramString1, String paramString2, String paramString3, String paramString4, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, Form.ViewInfo paramViewInfo, long[] paramArrayOfLong)
  {
    this.mbCombinedCall = true;
    this.mServer = (this.mTableServer = paramString1);
    this.mSchema = (this.mTableSchema = paramString2);
    this.mAppName = paramString3;
    this.mOwQual = paramString4;
    this.mOwFieldIds = paramArrayOfInt1;
    this.mOwFieldValues = paramArrayOfString;
    this.mOwFieldTypes = paramArrayOfInt2;
    this.mStartRow = 0L;
    this.mNumRows = 0L;
    this.mEntryToLoad = "0";
    this.mTableFieldId = 1020;
    this.mVuiId = paramViewInfo.getID();
    this.mTableVuiName = paramViewInfo.getLabel();
    this.mSortOrder = paramArrayOfLong;
    this.mFieldIds = new int[0];
    this.mFieldValues = new String[0];
    this.mFieldTypes = new int[0];
  }

  GetQBETableEntryListAgent(String paramString)
  {
    super(paramString);
  }

  GetQBETableEntryListAgent(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  protected QualifierInfo getELCQualifier()
    throws GoatException
  {
    Form localForm = Form.get(this.mTableServer, this.mTableSchema);
    CachedFieldMap localCachedFieldMap = localForm.getCachedFieldMap(true);
    Entry localEntry1 = buildEntryItems(this.mTableServerUser, this.mTableSchema, this.mFieldIds, this.mFieldValues, this.mFieldTypes, false);
    ARQualifier localARQualifier1 = new ARQualifier(this.mTableServerUser, localEntry1, localCachedFieldMap, this.mVuiId);
    ARQualifier localARQualifier2 = null;
    QualifierInfo localQualifierInfo = null;
    if ((this.mEncodedQual != null) && (this.mEncodedQual.length() > 0))
    {
      localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(this.mServerUser, this.mEncodedQual);
      localARQualifier2 = new ARQualifier(localQualifierInfo);
    }
    ARQualifier localARQualifier3 = null;
    if ((this.mOwQual != null) && (this.mOwQual.length() > 0))
    {
      Entry localEntry2 = buildEntryItems(this.mServer, this.mOwFieldIds, this.mOwFieldValues, this.mOwFieldTypes);
      localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(SessionData.get().getServerLogin(this.mServer), this.mOwQual);
      localARQualifier3 = new ARQualifier(localQualifierInfo, localEntry2);
    }
    if ((localARQualifier1.getQualInfo() != null) && (localARQualifier1.getQualInfo().getOperation() != 0))
    {
      if ((localARQualifier2 != null) && (localARQualifier2.getQualInfo() != null) && (localARQualifier2.getQualInfo().getOperation() != 0))
        return new QualifierInfo(1, localARQualifier1.getQualInfo(), localARQualifier2.getQualInfo());
      if ((localARQualifier3 != null) && (localARQualifier3.getQualInfo() != null) && (localARQualifier3.getQualInfo().getOperation() != 0))
        return new QualifierInfo(1, localARQualifier1.getQualInfo(), localARQualifier3.getQualInfo());
      return localARQualifier1.getQualInfo();
    }
    if ((localARQualifier2 != null) && (localARQualifier2.getQualInfo() != null) && (localARQualifier2.getQualInfo().getOperation() != 0))
      return localARQualifier2.getQualInfo();
    if ((localARQualifier3 != null) && (localARQualifier3.getQualInfo() != null) && (localARQualifier3.getQualInfo().getOperation() != 0))
      return localARQualifier3.getQualInfo();
    return null;
  }

  protected boolean sendReturnQualification()
  {
    return true;
  }

  protected void process()
    throws GoatException
  {
    if (this.mAppName.length() == 0)
      this.mAppName = null;
    SessionData localSessionData = SessionData.get();
    if (((this.mOwQual == null) || (this.mOwQual.length() == 0)) && (this.mSearchLabel.length() > 0))
    {
      ARUserPreferences localARUserPreferences = localSessionData.getPreferences();
      assert (localARUserPreferences != null);
      int i = localARUserPreferences.getRecentUsedListSize();
      ARUserSearches localARUserSearches = ARUserSearches.getUserSearches(localSessionData.getUserName(), this.mSchema, this.mServer, false);
      if (localARUserSearches != null)
      {
        if (localARUserSearches.getRecentSize() >= i)
          localARUserSearches.deleteSearch(new String[0]);
        assert (this.mSearchFieldIds.length == this.mSearchFieldVals.length);
        StringBuilder localStringBuilder = new StringBuilder(50);
        int j = this.mSearchFieldIds.length;
        localStringBuilder.append(j);
        for (int k = 0; k < j; k++)
        {
          localStringBuilder.append('/');
          localStringBuilder.append(this.mSearchFieldIds[k]).append('/');
          localStringBuilder.append(this.mSearchFieldVals[k].length()).append('/').append(this.mSearchFieldVals[k]);
        }
        String str = localStringBuilder.toString();
        localARUserSearches.saveSearch(localSessionData, this.mSearchLabel, str, true, 0, 0);
      }
    }
    this.mTableServerUser = localSessionData.getServerLogin(this.mTableServer);
    this.mServerUser = localSessionData.getServerLogin(this.mServer);
    getAndEmitTable(this.mTableSchema, this.mTableVuiName, this.mTableFieldId, this.mSchema, this.mAppName, this.mStartRow, this.mNumRows, this.mSortOrder, new int[0], new String[0], new int[0], this.mEntryToLoad, this.mScreenName, this.mIRId, this.mCCId);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetQBETableEntryListAgent
 * JD-Core Version:    0.6.1
 */