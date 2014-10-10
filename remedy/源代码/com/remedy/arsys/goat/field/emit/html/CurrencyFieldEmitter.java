package com.remedy.arsys.goat.field.emit.html;

import com.bmc.arsys.api.CurrencyDetail;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.CurrencyField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import java.math.BigDecimal;

public class CurrencyFieldEmitter extends DataFieldEmitter
{
  private CurrencyField currencyField;

  public CurrencyFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setCurrencyField((CurrencyField)paramGoatField);
  }

  private void setCurrencyField(CurrencyField paramCurrencyField)
  {
    this.currencyField = paramCurrencyField;
  }

  private CurrencyField getCurrencyField()
  {
    return this.currencyField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    paramHTMLWriter.openTag("input");
    if (TextDirStyleContext.get().isRTL())
      paramHTMLWriter.attr("dir", "ltr");
    paramHTMLWriter.attr("id", "arid_WIN_0_" + getMFieldID()).attr("class", "currency " + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt " : "")).attr("type", "text").attr("style", getMDataARBox().toBox().toCSS());
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    paramHTMLWriter.attr("arprecision", getMPrecision());
    paramHTMLWriter.attr("armin", getMMin().toString());
    paramHTMLWriter.attr("armax", getMMax().toString());
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      paramHTMLWriter.attr("artitlecode", getDisplayTitleCodeForField());
      if (getMAltText() != null)
        paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
      else
        paramHTMLWriter.attr("title", getLocalizedTitleForField(getDisplayTitleForField()));
    }
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openList();
    for (int i = 0; i < getMAllowable().length; i++)
    {
      localJSWriter.listSep().openObj();
      String str = getMAllowable()[i].getCurrencyCode();
      localJSWriter.property("v", str);
      localJSWriter.property("p", getMAllowable()[i].getPrecision());
      if (str == getMDefaultCurrency())
        localJSWriter.property("d", "1");
      localJSWriter.closeObj();
    }
    localJSWriter.closeList();
    paramHTMLWriter.attr("arallowablecur", localJSWriter.toString());
    localJSWriter = new JSWriter();
    localJSWriter.openList();
    for (i = 0; i < getMFunctional().length; i++)
    {
      localJSWriter.listSep().openObj();
      localJSWriter.property("v", getMFunctional()[i].getCurrencyCode());
      localJSWriter.property("p", getMFunctional()[i].getPrecision());
      localJSWriter.closeObj();
    }
    localJSWriter.closeList();
    paramHTMLWriter.attr("arfunctionalcur", localJSWriter.toString());
    if ((getMInitialCode() != null) && (getMInitialCode().length() > 0))
      paramHTMLWriter.attr("ic", getMInitialCode());
    paramHTMLWriter.closeOpenTag();
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    paramJSWriter.property("p", getMPrecision());
    if (getMAllowable().length > 0)
    {
      localJSWriter = new JSWriter();
      localJSWriter.openObj();
      for (i = 0; i < getMAllowable().length; i++)
      {
        localJSWriter.property(getMAllowable()[i].getCurrencyCode(), getMAllowable()[i].getPrecision());
        if (i == 0)
          paramJSWriter.property("d", getMAllowable()[i].getCurrencyCode());
      }
      localJSWriter.closeObj();
      paramJSWriter.property("a", localJSWriter);
    }
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openObj();
    for (int i = 0; i < getMFunctional().length; i++)
      localJSWriter.property(getMFunctional()[i].getCurrencyCode(), getMFunctional()[i].getPrecision());
    localJSWriter.closeObj();
    paramJSWriter.property("f", localJSWriter);
    if ((getMInitialCode() != null) && (getMInitialCode().length() > 0))
      paramJSWriter.property("ic", getMInitialCode());
    paramJSWriter.property("min", getMMin().toString());
    paramJSWriter.property("max", getMMax().toString());
  }

  public void emitTableProperties(JSWriter paramJSWriter)
  {
    super.emitTableProperties(paramJSWriter);
    paramJSWriter.property("p", getMPrecision());
    if (getMAllowable().length > 0)
    {
      localJSWriter = new JSWriter();
      localJSWriter.openObj();
      for (i = 0; i < getMAllowable().length; i++)
      {
        localJSWriter.property(getMAllowable()[i].getCurrencyCode(), getMAllowable()[i].getPrecision());
        if (i == 0)
          paramJSWriter.property("d", getMAllowable()[i].getCurrencyCode());
      }
      localJSWriter.closeObj();
      paramJSWriter.property("a", localJSWriter);
    }
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openObj();
    for (int i = 0; i < getMFunctional().length; i++)
      localJSWriter.property(getMFunctional()[i].getCurrencyCode(), getMFunctional()[i].getPrecision());
    localJSWriter.closeObj();
    paramJSWriter.property("f", localJSWriter);
  }

  public void emitPushFieldProperties(JSWriter paramJSWriter)
  {
    super.emitPushFieldProperties(paramJSWriter);
    paramJSWriter.openObj().property("p", getMPrecision()).closeObj();
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if (isMHaveDefault())
    {
      paramJSWriter.property("d", getMDefault().toString() + " " + getMDefaultCode());
      String str1 = Integer.toString(12);
      String str2 = "DatatypeFactories[" + str1 + "]('" + getMDefault().toString() + " " + getMDefaultCode() + "')";
      paramJSWriter.property("de", str2);
    }
  }

  public Object getSearchBarValue()
    throws GoatException
  {
    String str = getSearchBarLabel().replaceAll("'", "''");
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openList();
    localJSWriter.openObj();
    localJSWriter.property("l", "FIELD");
    localJSWriter.property("v", "'" + str + "'");
    localJSWriter.closeObj();
    localJSWriter.listSep();
    localJSWriter.openObj();
    localJSWriter.property("l", "VALUE");
    localJSWriter.property("v", "'" + str + ".VALUE'");
    localJSWriter.closeObj();
    localJSWriter.listSep();
    localJSWriter.openObj();
    localJSWriter.property("l", "TYPE");
    localJSWriter.property("v", "'" + str + ".TYPE'");
    localJSWriter.closeObj();
    localJSWriter.listSep();
    localJSWriter.openObj();
    localJSWriter.property("l", "DATE");
    localJSWriter.property("v", "'" + str + ".DATE'");
    localJSWriter.closeObj();
    for (int i = 0; i < getMFunctional().length; i++)
    {
      localJSWriter.listSep();
      localJSWriter.openObj();
      localJSWriter.property("l", getMFunctional()[i].getCurrencyCode());
      localJSWriter.property("v", "'" + str + "." + getMFunctional()[i].getCurrencyCode() + "'");
      localJSWriter.closeObj();
    }
    localJSWriter.closeList();
    return localJSWriter;
  }

  protected int getMPrecision()
  {
    return getCurrencyField().getMPrecision();
  }

  protected BigDecimal getMMin()
  {
    return getCurrencyField().getMMin();
  }

  protected BigDecimal getMMax()
  {
    return getCurrencyField().getMMax();
  }

  protected CurrencyDetail[] getMAllowable()
  {
    return getCurrencyField().getMAllowable();
  }

  protected String getMDefaultCurrency()
  {
    return getCurrencyField().getMDefaultCurrency();
  }

  protected CurrencyDetail[] getMFunctional()
  {
    return getCurrencyField().getMFunctional();
  }

  protected String getMInitialCode()
  {
    return getCurrencyField().getMInitialCode();
  }

  protected BigDecimal getMDefault()
  {
    return getCurrencyField().getMDefault();
  }

  protected String getMDefaultCode()
  {
    return getCurrencyField().getMDefaultCode();
  }

  protected boolean isMHaveDefault()
  {
    return getCurrencyField().isMHaveDefault();
  }

  protected String getMAltText()
  {
    return getCurrencyField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.CurrencyFieldEmitter
 * JD-Core Version:    0.6.1
 */