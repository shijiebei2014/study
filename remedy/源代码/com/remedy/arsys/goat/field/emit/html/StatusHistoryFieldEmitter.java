package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.EnumField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatField.LightForm;
import com.remedy.arsys.goat.field.StatusHistoryField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;
import java.util.Map;

public class StatusHistoryFieldEmitter extends DataFieldEmitter
{
  private StatusHistoryField statusHistoryField;

  public StatusHistoryFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setStatusHistoryField((StatusHistoryField)paramGoatField);
  }

  private void setStatusHistoryField(StatusHistoryField paramStatusHistoryField)
  {
    this.statusHistoryField = paramStatusHistoryField;
  }

  private StatusHistoryField getStatusHistoryField()
  {
    return this.statusHistoryField;
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
  }

  public Object getSearchBarValue()
    throws GoatException
  {
    JSWriter localJSWriter1 = new JSWriter();
    String str = getSearchBarLabel().replaceAll("'", "''");
    localJSWriter1.openList();
    localJSWriter1.openObj();
    localJSWriter1.property("l", "TIME");
    JSWriter localJSWriter2 = new JSWriter();
    localJSWriter2.openList();
    Form localForm = Form.get(getMLForm().getServerName(), getMLForm().getFormName());
    EnumField localEnumField = (EnumField)GoatField.get(localForm.getViewInfo(getMView())).get(Integer.valueOf(7));
    String[] arrayOfString = new String[0];
    if (localEnumField != null)
      arrayOfString = localEnumField.getMEnumLabels();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      if (i > 0)
        localJSWriter2.listSep();
      localJSWriter2.openObj();
      localJSWriter2.property("l", arrayOfString[i]);
      localJSWriter2.property("v", "'" + str + "." + arrayOfString[i] + ".TIME'");
      localJSWriter2.closeObj();
    }
    localJSWriter2.closeList();
    localJSWriter1.property("v", localJSWriter2);
    localJSWriter1.closeObj();
    localJSWriter1.listSep();
    localJSWriter1.openObj();
    localJSWriter1.property("l", "USER");
    localJSWriter2 = new JSWriter();
    localJSWriter2.openList();
    for (i = 0; i < arrayOfString.length; i++)
    {
      if (i > 0)
        localJSWriter2.listSep();
      localJSWriter2.openObj();
      localJSWriter2.property("l", arrayOfString[i]);
      localJSWriter2.property("v", "'" + str + "." + arrayOfString[i] + ".USER'");
      localJSWriter2.closeObj();
    }
    localJSWriter2.closeList();
    localJSWriter1.property("v", localJSWriter2);
    localJSWriter1.closeObj();
    localJSWriter1.closeList();
    return localJSWriter1;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.StatusHistoryFieldEmitter
 * JD-Core Version:    0.6.1
 */