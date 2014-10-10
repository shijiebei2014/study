package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatImageButton;
import com.remedy.arsys.goat.field.IntegerField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class IntegerFieldEmitter extends DataFieldEmitter
{
  private IntegerField integerField;

  public IntegerFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setIntegerField((IntegerField)paramGoatField);
  }

  private void setIntegerField(IntegerField paramIntegerField)
  {
    this.integerField = paramIntegerField;
  }

  private IntegerField getIntegerField()
  {
    return this.integerField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    Box localBox1 = getMDataARBox().toBox();
    paramHTMLWriter.openTag("div").attr("class", "integer" + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt " : "")).attr("style", localBox1.toCSS()).endTag();
    paramHTMLWriter.openTag("input");
    if (TextDirStyleContext.get().isRTL())
      paramHTMLWriter.attr("dir", "ltr");
    if ((FormContext.get().IsVoiceAccessibleUser()) && (getMAltText() != null))
      paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
    paramHTMLWriter.attr("id", "arid_WIN_0_" + getMFieldID()).attr("class", "text " + getMDataFont() + (isMTextOnly() ? " dat" : "")).attr("type", "text");
    if (getMMin() != -2147483647L)
      paramHTMLWriter.attr("armin", getMMin());
    if (getMMax() != 2147483647L)
      paramHTMLWriter.attr("armax", getMMax());
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    if (isMSpinner())
      paramHTMLWriter.attr("arspinner", 1);
    Box localBox2 = new Box(localBox1);
    localBox2.mX = 0;
    localBox2.mY = 0;
    localBox2.mW -= 2;
    if ((isMSpinner()) && (localBox2.mW > 16))
      localBox2.mW -= 16;
    paramHTMLWriter.attr("style", localBox2.toCSS());
    paramHTMLWriter.closeOpenTag();
    if ((isMSpinner()) && (!FormContext.get().IsVoiceAccessibleUser()))
    {
      localBox2.mX += localBox2.mW;
      localBox2.mW = 16;
      int i = localBox2.mH;
      localBox2.mH = ((localBox2.mH - 1) / 2);
      String str1 = getLocalizedDescriptionStringForWidget("increment");
      String str2 = getLocalizedDescriptionStringForWidget("decrement");
      IntegerField.mSpinnerUp.emitMarkup(paramHTMLWriter, localBox2, "spinnerup", str1);
      localBox2.mY += localBox2.mH;
      localBox2.mH = (i - localBox2.mH);
      IntegerField.mSpinnerDown.emitMarkup(paramHTMLWriter, localBox2, "spinnerdown", str2);
    }
    paramHTMLWriter.closeTag("div");
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    if (getMMin() != -2147483647L)
      paramJSWriter.property("min", getMMin());
    if (getMMax() != 2147483647L)
      paramJSWriter.property("max", getMMax());
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if (isMHaveDefault())
      paramJSWriter.property("d", "" + getMDefault());
  }

  public void emitTableProperties(JSWriter paramJSWriter)
  {
    super.emitTableProperties(paramJSWriter);
  }

  protected long getMMin()
  {
    return getIntegerField().getMMin();
  }

  protected long getMMax()
  {
    return getIntegerField().getMMax();
  }

  protected boolean isMSpinner()
  {
    return getIntegerField().isMSpinner();
  }

  protected boolean isMHaveDefault()
  {
    return getIntegerField().isMHaveDefault();
  }

  protected long getMDefault()
  {
    return getIntegerField().getMDefault();
  }

  protected String getMAltText()
  {
    return getIntegerField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.IntegerFieldEmitter
 * JD-Core Version:    0.6.1
 */