package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.stubs.SessionData;

public class ParseAndEncodeQualifierAgent extends NDXParseAndEncodeQualifier
{
  public ParseAndEncodeQualifierAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    FieldGraph localFieldGraph = FieldGraph.get(this.mServer, this.mForm, this.mView);
    ARQualifier localARQualifier = new ARQualifier(SessionData.get().getServerLogin(this.mServer), this.mQualifier, localFieldGraph.GetQualifierHelper());
    append("this.result=");
    String str = Qualifier.AREncodeARQualifierStruct(SessionData.get().getServerLogin(this.mServer), localARQualifier.getQualInfo());
    append(escapeString(str));
    append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.ParseAndEncodeQualifierAgent
 * JD-Core Version:    0.6.1
 */