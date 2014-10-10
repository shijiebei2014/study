package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.RealField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class RealFieldEmitter extends DataFieldEmitter
{
  private RealField realField;

  public RealFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setRealField((RealField)paramGoatField);
  }

  private void setRealField(RealField paramRealField)
  {
    this.realField = paramRealField;
  }

  private RealField getRealField()
  {
    return this.realField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    boolean bool = TextDirStyleContext.get().isRTL();
    paramHTMLWriter.openTag("input");
    if (bool)
      paramHTMLWriter.attr("dir", "ltr");
    if ((FormContext.get().IsVoiceAccessibleUser()) && (getMAltText() != null))
      paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
    paramHTMLWriter.attr("id", "arid_WIN_0_" + getMFieldID());
    if (bool)
      paramHTMLWriter.attr("class", "realrtl text " + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : ""));
    else
      paramHTMLWriter.attr("class", "text " + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : ""));
    paramHTMLWriter.attr("type", "text").attr("style", getMDataARBox().toBox().toCSS());
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    paramHTMLWriter.attr("arprecision", getMPrecision());
    paramHTMLWriter.attr("armin", new Double(getMMin()).toString());
    paramHTMLWriter.attr("armax", new Double(getMMax()).toString());
    paramHTMLWriter.closeOpenTag();
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    paramJSWriter.property("p", getMPrecision());
    paramJSWriter.property("min", new Double(getMMin()).toString());
    paramJSWriter.property("max", new Double(getMMax()).toString());
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
      String str1 = Integer.toString(3);
      String str2 = "DatatypeFactories[" + str1 + "]('" + getMDefault().toString() + "')";
      paramJSWriter.property("de", str2);
    }
  }

  protected double getMMin()
  {
    return getRealField().getMMin();
  }

  protected double getMMax()
  {
    return getRealField().getMMax();
  }

  protected int getMPrecision()
  {
    return getRealField().getMPrecision();
  }

  protected Double getMDefault()
  {
    return getRealField().getMDefault();
  }

  protected boolean isMHaveDefault()
  {
    return getRealField().isMHaveDefault();
  }

  protected String getMAltText()
  {
    return getRealField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.RealFieldEmitter
 * JD-Core Version:    0.6.1
 */