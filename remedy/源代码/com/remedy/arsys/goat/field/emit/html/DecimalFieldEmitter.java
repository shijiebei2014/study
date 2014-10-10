package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.DecimalField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import java.math.BigDecimal;

public class DecimalFieldEmitter extends DataFieldEmitter
{
  private DecimalField decimalField;

  public DecimalFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setDecimalField((DecimalField)paramGoatField);
  }

  private void setDecimalField(DecimalField paramDecimalField)
  {
    this.decimalField = paramDecimalField;
  }

  private DecimalField getDecimalField()
  {
    return this.decimalField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    paramHTMLWriter.openTag("input");
    if (TextDirStyleContext.get().isRTL())
      paramHTMLWriter.attr("dir", "ltr");
    if ((FormContext.get().IsVoiceAccessibleUser()) && (getMAltText() != null))
      paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
    paramHTMLWriter.attr("id", "arid_WIN_0_" + getMFieldID()).attr("class", "decimal " + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : "")).attr("type", "text").attr("style", getMDataARBox().toBox().toCSS());
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    paramHTMLWriter.attr("arprecision", getMPrecision());
    paramHTMLWriter.attr("armin", getMMin().toString());
    paramHTMLWriter.attr("armax", getMMax().toString());
    paramHTMLWriter.closeOpenTag();
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    paramJSWriter.property("p", getMPrecision());
    paramJSWriter.property("min", getMMin().toString());
    paramJSWriter.property("max", getMMax().toString());
  }

  public void emitTableProperties(JSWriter paramJSWriter)
  {
    super.emitTableProperties(paramJSWriter);
    paramJSWriter.property("p", getMPrecision());
  }

  public void emitPushFieldProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.openObj().property("p", getMPrecision()).closeObj();
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if (isMHaveDefault())
    {
      paramJSWriter.property("d", getMDefault().toString());
      String str1 = Integer.toString(10);
      String str2 = "DatatypeFactories[" + str1 + "]('" + getMDefault().toString() + "')";
      paramJSWriter.property("de", str2);
    }
  }

  protected int getMPrecision()
  {
    return getDecimalField().getMPrecision();
  }

  protected BigDecimal getMMin()
  {
    return getDecimalField().getMMin();
  }

  protected BigDecimal getMMax()
  {
    return getDecimalField().getMMax();
  }

  protected boolean isMHaveDefault()
  {
    return getDecimalField().isMHaveDefault();
  }

  protected BigDecimal getMDefault()
  {
    return getDecimalField().getMDefault();
  }

  protected String getMAltText()
  {
    return getDecimalField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.DecimalFieldEmitter
 * JD-Core Version:    0.6.1
 */