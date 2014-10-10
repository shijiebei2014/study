package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.field.DateField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class DateFieldEmitter extends DataFieldEmitter
{
  private DateField dateField;

  public DateFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setDateField((DateField)paramGoatField);
  }

  private void setDateField(DateField paramDateField)
  {
    this.dateField = paramDateField;
  }

  private DateField getDateField()
  {
    return this.dateField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    paramHTMLWriter.openTag("input").attr("id", "arid_WIN_0_" + getMFieldID()).attr("class", "text " + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : "")).attr("type", "text").attr("style", getMDataARBox().toBox().toCSS());
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      String str1 = null;
      String str2 = null;
      if (SessionData.get() != null)
        str2 = SessionData.get().getDateFormatPattern();
      if (getMAltText() != null)
        str1 = getMAltText() + " " + str2;
      else
        str1 = getMLabel() + " " + str2;
      paramHTMLWriter.attr("title", getLocalizedTitleForField(str1));
    }
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    paramHTMLWriter.closeOpenTag();
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if (isMHaveDefault())
      paramJSWriter.property("d", getMDefault());
  }

  protected boolean isMHaveDefault()
  {
    return getDateField().isMHaveDefault();
  }

  protected String getMDefault()
  {
    return getDateField().getMDefault();
  }

  protected String getMAltText()
  {
    return getDateField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.DateFieldEmitter
 * JD-Core Version:    0.6.1
 */