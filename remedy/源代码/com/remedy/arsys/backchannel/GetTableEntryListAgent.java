package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.stubs.SessionData;
import java.io.OutputStream;

public class GetTableEntryListAgent extends NDXGetTableEntryList
{
  private Qualifier externQual;

  GetTableEntryListAgent(String paramString)
  {
    super(paramString);
  }

  GetTableEntryListAgent(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  protected QualifierInfo getELCQualifier()
    throws GoatException
  {
    int i = (this.externQual != null) && (this.externQual.getARQualifierInfo() != null) ? 1 : 0;
    int j = this.mQualification.length() > 0 ? 1 : 0;
    int k = (i != 0) || (j != 0) ? 1 : 0;
    Entry localEntry = null;
    if (k != 0)
      localEntry = buildEntryItems(this.mServer, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
    QualifierInfo localQualifierInfo = null;
    if (i != 0)
      localQualifierInfo = this.externQual.getARQualifierInfo();
    else if (j != 0)
      localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(SessionData.get().getServerLogin(this.mServer), this.mQualification);
    if ((k != 0) && (localQualifierInfo != null))
    {
      ARQualifier localARQualifier = new ARQualifier(localQualifierInfo, localEntry);
      return localARQualifier.getQualInfo();
    }
    return null;
  }

  protected void process()
    throws GoatException
  {
    if (this.mCompile)
    {
      Object[][] arrayOfObject = CompileExternalQualificationAgent.splitFields(this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes, this.mQualFieldExterns);
      int[] arrayOfInt1 = (int[])arrayOfObject[0][0];
      String[] arrayOfString = (String[])arrayOfObject[0][1];
      int[] arrayOfInt2 = (int[])arrayOfObject[0][2];
      this.mQualFieldIds = ((int[])arrayOfObject[1][0]);
      this.mQualFieldValues = ((String[])arrayOfObject[1][1]);
      this.mQualFieldTypes = ((int[])arrayOfObject[1][2]);
      this.externQual = CompileExternalQualificationAgent.compileExternalQualification(this.mTableServer, this.mTableSchema, this.mTableVuiName, this.mServer, this.mSchema, this.mQualification, arrayOfInt1, arrayOfString, arrayOfInt2);
    }
    if ((this.mQualFieldIds.length != this.mQualFieldValues.length) || (this.mQualFieldIds.length != this.mQualFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    if (this.mAppName.length() == 0)
      this.mAppName = null;
    this.mTableServerUser = SessionData.get().getServerLogin(this.mTableServer);
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    getAndEmitTable(this.mTableSchema, this.mTableVuiName, this.mTableFieldId, this.mSchema, this.mAppName, this.mStartRow, this.mNumRows, this.mSortOrder, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes, null, null, this.mIRId, this.mCCId);
  }

  protected boolean sendReturnQualification()
  {
    return true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetTableEntryListAgent
 * JD-Core Version:    0.6.1
 */