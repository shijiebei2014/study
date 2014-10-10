package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARQualifierHelper;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.ServiceLocator;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;

public class CompileQualificationAgent extends NDXCompileQualification
{
  private ServerLogin mServerUser;
  private static final Field[] EMPTY_FIELD_ARR = new Field[0];

  public CompileQualificationAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    CachedFieldMap localCachedFieldMap = Form.get(this.mServer, this.mSchema).getCachedFieldMap();
    ArrayList localArrayList = new ArrayList(localCachedFieldMap.values());
    QualifierInfo localQualifierInfo;
    Object localObject;
    try
    {
      if (ARQualifier.isEncodedQualStr(this.mQualification))
        localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(this.mServerUser, this.mQualification);
      else
        localQualifierInfo = this.mServerUser.parseQualification(this.mQualification, localArrayList, localArrayList, 0);
    }
    catch (ARException localARException1)
    {
      localObject = Form.get(this.mServer, this.mSchema).getViewInfoByInference(this.mVui, false, false);
      FieldGraph localFieldGraph = FieldGraph.get(this.mServer, this.mSchema, this.mVui);
      try
      {
        localQualifierInfo = localFieldGraph.GetQualifierHelper().parseQualification(this.mServerUser.getLocale(), this.mQualification, this.mServerUser.getTimeZone());
      }
      catch (ARException localARException2)
      {
        throw new GoatException(localARException2);
      }
    }
    try
    {
      Qualifier localQualifier = new Qualifier(localQualifierInfo, this.mServer);
      localQualifier.bindToForm(localCachedFieldMap);
      append("this.result=");
      localObject = new Emitter(this, (IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
      localQualifier.emitJS((Emitter)localObject);
      append(";");
    }
    catch (GoatException localGoatException)
    {
      this.mBuffer = new StringBuilder();
      append("this.result=false;");
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.CompileQualificationAgent
 * JD-Core Version:    0.6.1
 */