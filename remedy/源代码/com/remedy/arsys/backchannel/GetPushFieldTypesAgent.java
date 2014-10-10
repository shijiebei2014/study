package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.action.ActionPushFields;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServiceLocator;

public class GetPushFieldTypesAgent extends NDXGetPushFieldTypes
{
  private IEmitterFactory emitterFactory;

  public GetPushFieldTypesAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    setEmitterFactory((IEmitterFactory)ServiceLocator.getInstance().getService("emitterFactory"));
    Form localForm = Form.get(this.mServer, this.mForm);
    append("this.result={");
    for (int i = 0; i < this.mFieldIds.length; i++)
    {
      int j = this.mFieldIds[i];
      Field localField = (Field)localForm.getCachedFieldMap(true).get(Integer.valueOf(j));
      if (localField != null)
      {
        int k = this.mBuffer.charAt(this.mBuffer.length() - 1);
        if ((k != 123) && (k != 44))
          append(",");
        append(this.mFieldIds[i]).append(": {t:");
        append(ActionPushFields.getPushFieldsType(localField));
        String str = ActionPushFields.getPushFieldArgs(this.mServer, this.mForm, j, this.emitterFactory);
        if (str.length() > 0)
          append(", a:" + str);
        append("}");
      }
    }
    append("}");
  }

  public void setEmitterFactory(IEmitterFactory paramIEmitterFactory)
  {
    this.emitterFactory = paramIEmitterFactory;
  }

  public IEmitterFactory getEmitterFactory()
  {
    return this.emitterFactory;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetPushFieldTypesAgent
 * JD-Core Version:    0.6.1
 */