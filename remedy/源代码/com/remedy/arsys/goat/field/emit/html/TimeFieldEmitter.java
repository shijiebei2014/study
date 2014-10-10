package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.TimeField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class TimeFieldEmitter extends DataFieldEmitter
{
  private TimeField timeField;

  public TimeFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setTimeField((TimeField)paramGoatField);
  }

  private void setTimeField(TimeField paramTimeField)
  {
    this.timeField = paramTimeField;
  }

  private TimeField getTimeField()
  {
    return this.timeField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    paramHTMLWriter.openTag("input");
    if (TextDirStyleContext.get().isRTL())
      paramHTMLWriter.attr("dir", "ltr");
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      String str1 = null;
      String str2 = null;
      String str3 = null;
      if (SessionData.get() != null)
      {
        str2 = SessionData.get().getDateFormatPattern();
        str3 = SessionData.get().getTimeFormatPattern();
      }
      if (getMAltText() != null)
        str1 = getMAltText() + " " + str2 + " " + str3;
      else
        str1 = getMLabel() + " " + str2 + " " + str3;
      paramHTMLWriter.attr("title", getLocalizedTitleForField(str1));
    }
    paramHTMLWriter.attr("id", "arid_WIN_0_" + getMFieldID()).attr("class", "text  realrtl" + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : "")).attr("type", "text").attr("style", getMDataARBox().toBox().toCSS());
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    paramHTMLWriter.attr("ds", getMDateTimePopup());
    paramHTMLWriter.closeOpenTag();
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if (isMHaveDefault())
      paramJSWriter.property("d", getMDefault());
  }

  protected int getMDateTimePopup()
  {
    return getTimeField().getMDateTimePopup();
  }

  protected boolean isMHaveDefault()
  {
    return getTimeField().isMHaveDefault();
  }

  protected String getMDefault()
  {
    return getTimeField().getMDefault();
  }

  protected String getMAltText()
  {
    return getTimeField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.TimeFieldEmitter
 * JD-Core Version:    0.6.1
 */