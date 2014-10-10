package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.util.Map;

public class StatusHistoryField extends DataField
{
  private static final long serialVersionUID = 1266062780647292443L;
  private String mQualifierFieldLabel;

  public StatusHistoryField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    setMEmitViewable(1);
    setMEmitOptimised(1);
    setMEmitViewless(1);
  }

  public boolean hasSearchBarMenu()
  {
    return true;
  }

  public String getQualifierFieldLabel()
  {
    if (this.mQualifierFieldLabel != null)
      return this.mQualifierFieldLabel;
    this.mQualifierFieldLabel = getSearchBarLabel();
    return this.mQualifierFieldLabel;
  }

  public String getSearchBarLabel()
  {
    if (getMLabel() != null)
      return getMLabel();
    try
    {
      Form localForm = Form.get(getMLForm().getServerName(), getMLForm().getFormName());
      FieldGraph localFieldGraph = FieldGraph.get(localForm.getViewInfo(getMView()));
      assert (localFieldGraph != null);
      if (localFieldGraph != null)
      {
        GoatField localGoatField = localFieldGraph.getField(7);
        assert (localGoatField != null);
        if (localGoatField != null)
        {
          String str = MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "-History");
          return localGoatField.getSearchBarLabel() + str;
        }
      }
    }
    catch (GoatException localGoatException)
    {
      return getMDBName();
    }
    return getMDBName();
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
 * Qualified Name:     com.remedy.arsys.goat.field.StatusHistoryField
 * JD-Core Version:    0.6.1
 */