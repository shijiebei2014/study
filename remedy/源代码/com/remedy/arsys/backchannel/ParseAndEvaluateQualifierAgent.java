package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class ParseAndEvaluateQualifierAgent extends NDXParseAndEvaluateQualifier
{
  private ServerLogin mServerUser;

  public ParseAndEvaluateQualifierAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    if ((this.mQualFieldIds.length != this.mQualFieldValues.length) || (this.mQualFieldIds.length != this.mQualFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    Entry localEntry = buildEntryItems(this.mServer, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
    QualifierInfo localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(SessionData.get().getServerLogin(this.mServer), this.mQualification);
    ARQualifier localARQualifier = new ARQualifier(localQualifierInfo, localEntry);
    append("this.result=");
    String str = Qualifier.AREncodeARQualifierStruct(this.mServerUser, localARQualifier.getQualInfo());
    append(escapeString(str));
    append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.ParseAndEvaluateQualifierAgent
 * JD-Core Version:    0.6.1
 */