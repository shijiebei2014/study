package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Keyword;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.type.CharType;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.goat.field.type.NullType;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class GetFieldDefaultsAgent extends NDXGetFieldDefaults
{
  public GetFieldDefaultsAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    append("this.result=");
    openObj();
    ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
    FieldGraph localFieldGraph = FieldGraph.get(Form.get(this.mServer, this.mForm).getViewInfoByInference(null, false, false));
    CachedFieldMap localCachedFieldMap = localFieldGraph.getForm().getCachedFieldMap();
    JSWriter localJSWriter = new JSWriter();
    for (int i = 0; i < this.mFieldIds.length; i++)
    {
      int j = this.mFieldIds[i];
      Field localField = (Field)localCachedFieldMap.get(Integer.valueOf(j));
      if (i > 0)
        append(", ");
      localJSWriter.clear();
      localJSWriter.openObj();
      Object localObject;
      if (localField != null)
      {
        Value localValue = localField.getDefaultValue();
        if (localValue != null)
        {
          if (DataType.KEYWORD.equals(localValue.getDataType()))
            localObject = new CharType(((Keyword)localValue.getValue()).toString(), j);
          else
            localObject = GoatTypeFactory.create(localValue, j, null, localServerLogin, localFieldGraph.getNode(j));
        }
        else
          localObject = NullType.MNullType;
      }
      else
      {
        localObject = NullType.MNullType;
      }
      ((GoatType)localObject).emitPrimitive(localJSWriter);
      localJSWriter.closeObj();
      append(this.mFieldIds[i] + ":" + "Datatype_Factory(" + localJSWriter.toString() + ")");
    }
    closeObj();
    append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetFieldDefaultsAgent
 * JD-Core Version:    0.6.1
 */