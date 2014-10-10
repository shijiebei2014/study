package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.EnumField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatImageButton;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class EnumFieldEmitter extends DataFieldEmitter
{
  private EnumField enumField;

  public EnumFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setEnumField((EnumField)paramGoatField);
  }

  private void setEnumField(EnumField paramEnumField)
  {
    this.enumField = paramEnumField;
  }

  private EnumField getEnumField()
  {
    return this.enumField;
  }

  public void emitSelections(JSWriter paramJSWriter)
  {
    paramJSWriter.openList();
    for (int i = 0; i < getMEnumValues().length; i++)
    {
      if ((i > 0) && (i < getMEnumValues().length))
        paramJSWriter.comma();
      paramJSWriter.openObj();
      if (isMIsCustom())
        paramJSWriter.property("ci", getMEnumIds()[i]);
      paramJSWriter.propertyDestinedForHTML("v", getMEnumValues()[i]);
      if (!getMEnumValues()[i].equals(getMEnumLabels()[i]))
        paramJSWriter.propertyDestinedForHTML("l", getMEnumLabels()[i]);
      paramJSWriter.closeObj();
    }
    paramJSWriter.closeList();
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    Box localBox1 = getMDataARBox().toBox();
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    if (localBox1.mW <= 0)
      localBox1.mW = getMARBox().toBox().mW;
    if (localBox1.mH <= 0)
      localBox1.mH = getMARBox().toBox().mH;
    if ((getMEnumType() == 1) || (getMEnumType() == 2))
    {
      int k;
      String str2;
      if (getMRows() == 1L)
      {
        paramHTMLWriter.openTag("fieldset").attr("class", isChildOfFlowLayout(paramNode) ? " fld" : "").attr("style", localBox1.toCSS()).endTag();
        paramHTMLWriter.openTag("legend").attr("class", "hidden").endTag(false);
        if (FormContext.get().IsVoiceAccessibleUser())
        {
          if ((getMAltText() != null) && (getMAltText().trim().length() > 0))
            paramHTMLWriter.cdata(getMAltText());
          else if (getMLabel() != null)
            paramHTMLWriter.cdata(getMLabel());
        }
        else if (getMLabel() != null)
          paramHTMLWriter.cdata(getMLabel());
        paramHTMLWriter.closeTag("legend");
        paramHTMLWriter.openTag("div").attr("class", "radio " + getMDataFont());
        boolean bool = TextDirStyleContext.get().isRTL();
        Box localBox2 = localBox1.wholeChildBox();
        if (bool)
        {
          Box localBox4 = getMLabelARBox().toBox();
          localBox2.mX += localBox4.mW;
        }
        paramHTMLWriter.attr("style", localBox2.toCSS()).endTag();
        for (k = 0; k < getMEnumValues().length; k++)
        {
          paramHTMLWriter.openTag("span");
          if (bool)
            paramHTMLWriter.attr("dir", "rtl");
          if ((k > 0) && (getMEnumType() == 2))
            paramHTMLWriter.attr("style", "display:none");
          paramHTMLWriter.endTag(false);
          paramHTMLWriter.openTag("input").attr("type", getMEnumType() == 1 ? "radio" : "checkbox").attr("name", "WIN_0_RCGroup" + getMFieldID());
          if (getMAccess() != 2L)
            paramHTMLWriter.attr("disabled");
          paramHTMLWriter.attr("value", getMEnumIds()[k]);
          str2 = "WIN_0_rc" + k + "id" + getMFieldID();
          paramHTMLWriter.attr("id", str2);
          if (!getMEnumValues()[k].equals(getMEnumLabels()[k]))
            paramHTMLWriter.attr("arvalue", getMEnumValues()[k]);
          paramHTMLWriter.closeOpenTag(false);
          paramHTMLWriter.openTag("label").attr("for", str2).attr("class", getMDataFont()).endTag(false);
          paramHTMLWriter.cdataSpaceEncoded(getMEnumLabels()[k]);
          paramHTMLWriter.closeTag("label", false);
          paramHTMLWriter.closeTag("span", false);
        }
        paramHTMLWriter.closeTag("div");
        paramHTMLWriter.closeTag("fieldset");
      }
      else
      {
        assert ((getMRows() > 0L) && (getMRows() <= getMEnumValues().length));
        int i = (getMEnumValues().length + (int)getMRows() - 1) / (int)getMRows();
        paramHTMLWriter.openTag("fieldset").attr("style", localBox1.toCSS()).endTag(false);
        paramHTMLWriter.openTag("legend").attr("class", "hidden").endTag(false);
        paramHTMLWriter.cdata(getMLabel());
        paramHTMLWriter.closeTag("legend");
        paramHTMLWriter.openTag("table").attr("class", "radio " + getMDataFont()).attr("cellpadding", 0).attr("cellspacing", 0).attr("style", localBox1.wholeChildBox().toCSS()).endTag();
        k = 0;
        for (int j = 0; (j < getMRows()) && (k < getMEnumValues().length); j++)
        {
          paramHTMLWriter.openTag("tr").endTag(false);
          while (k < getMEnumValues().length)
          {
            paramHTMLWriter.openTag("td");
            if ((k > 0) && (getMEnumType() == 2))
              paramHTMLWriter.attr("style", "display:none");
            paramHTMLWriter.endTag(false);
            paramHTMLWriter.openTag("input").attr("type", getMEnumType() == 1 ? "radio" : "checkbox").attr("name", "WIN_0_RCGroup" + getMFieldID());
            if (getMAccess() != 2L)
              paramHTMLWriter.attr("disabled");
            paramHTMLWriter.attr("value", getMEnumIds()[k]);
            str2 = "WIN_0_rc" + k + "id" + getMFieldID();
            paramHTMLWriter.attr("id", str2);
            if (!getMEnumValues()[k].equals(getMEnumLabels()[k]))
              paramHTMLWriter.attr("arvalue", getMEnumValues()[k]);
            paramHTMLWriter.closeOpenTag(false);
            paramHTMLWriter.openTag("label").attr("for", str2).attr("class", getMDataFont()).endTag(false);
            paramHTMLWriter.cdataSpaceEncoded(getMEnumLabels()[k]);
            paramHTMLWriter.closeTag("label", false);
            paramHTMLWriter.closeTag("td", false);
            k++;
            if (k % i == 0)
              break;
          }
          if (j == 0)
          {
            paramHTMLWriter.openTag("td").attr("style", "width:100%;").endTag(false);
            paramHTMLWriter.closeTag("td", false);
          }
          paramHTMLWriter.closeTag("tr");
        }
        if (j < getMRows())
          paramHTMLWriter.openTag("tr").endTag(false).openTag("td").attr("style", "height:100%;").endTag(false).closeTag("td", true).closeTag("tr", true);
        paramHTMLWriter.closeTag("table");
        paramHTMLWriter.closeTag("fieldset");
      }
    }
    else
    {
      assert (getMEnumType() == 0);
      paramHTMLWriter.openTag("div").attr("class", "selection" + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : "")).attr("style", localBox1.toCSS());
      JSWriter localJSWriter = new JSWriter();
      emitSelections(localJSWriter);
      paramHTMLWriter.attr("arselmenu", localJSWriter.toString()).endTag();
      paramHTMLWriter.openTag("input").attr("id", "arid_WIN_0_" + getMFieldID()).attr("type", "text").attr("class", "text " + getMDataFont() + (isMTextOnly() ? " dat" : "")).attr("readonly");
      if (FormContext.get().IsVoiceAccessibleUser())
      {
        paramHTMLWriter.attr("artitlecode", getDisplayTitleCodeForField());
        if (getMAltText() != null)
          paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
        else
          paramHTMLWriter.attr("title", getLocalizedTitleForField(getDisplayTitleForField()));
      }
      Box localBox3 = new Box(localBox1);
      localBox3.mX = 0;
      localBox3.mY = 0;
      if (localBox3.mW > localBox3.mH)
        localBox3.mW -= localBox3.mH;
      paramHTMLWriter.attr("style", localBox3.toCSS()).closeOpenTag();
      localBox3.mX += localBox3.mW;
      localBox3.mW = localBox3.mH;
      String str1 = null;
      if (getMEnumType() == 0)
        if (FormContext.get().IsVoiceAccessibleUser())
        {
          if (getMAccess() == 3L)
            str1 = getLocalizedDescriptionStringForWidget("Selection menu for {0}, unavailable");
          else
            str1 = getLocalizedDescriptionStringForWidget("Selection menu for {0}");
        }
        else
          str1 = "";
      EnumField.mSelectionButton.emitMarkup(paramHTMLWriter, localBox3, "selectionbtn", str1);
      paramHTMLWriter.closeTag("div");
    }
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if (getMDefaultValue() != -1)
    {
      int i = idToIndex(getMDefaultValue());
      paramJSWriter.property("d", getMEnumLabels()[i]);
    }
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    paramJSWriter.append(",e:").openObj();
    for (int i = 0; i < getMEnumLabels().length; i++)
      paramJSWriter.property(getMEnumIds()[i], getMEnumLabels()[i]);
    paramJSWriter.closeObj();
    paramJSWriter.append(",vl:").openObj();
    for (i = 0; i < getMEnumValues().length; i++)
      paramJSWriter.property(getMEnumIds()[i], getMEnumValues()[i]);
    paramJSWriter.closeObj();
  }

  public void emitTableProperties(JSWriter paramJSWriter)
  {
    super.emitTableProperties(paramJSWriter);
    paramJSWriter.property("et", getMEnumType());
    paramJSWriter.append(",e:").openObj();
    for (int i = 0; i < getMEnumLabels().length; i++)
      paramJSWriter.property(getMEnumIds()[i], getMEnumLabels()[i]);
    paramJSWriter.closeObj();
    paramJSWriter.append(",vl:").openObj();
    for (i = 0; i < getMEnumValues().length; i++)
      paramJSWriter.property(getMEnumIds()[i], getMEnumValues()[i]);
    paramJSWriter.closeObj();
  }

  public void emitPushFieldProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.openObj();
    paramJSWriter.append("el:").openObj();
    for (int i = 0; i < getMEnumValues().length; i++)
      paramJSWriter.property(getMEnumIds()[i], getMEnumValues()[i]);
    paramJSWriter.closeObj();
    paramJSWriter.closeObj();
  }

  public void emitSearchBarMenu(JSWriter paramJSWriter)
  {
    paramJSWriter.openObj();
    if ((getMLabel() == null) || (getMLabel().length() == 0))
      paramJSWriter.propertyDestinedForHTML("l", getMDBName());
    else
      paramJSWriter.propertyDestinedForHTML("l", getMLabel());
    paramJSWriter.append(",v:");
    paramJSWriter.openList();
    for (int i = 0; i < getMEnumLabels().length; i++)
    {
      paramJSWriter.listSep().openObj();
      paramJSWriter.propertyDestinedForHTML("l", getMEnumLabels()[i]);
      paramJSWriter.propertyDestinedForHTML("v", "\"" + getMEnumLabels()[i] + "\"");
      paramJSWriter.closeObj();
    }
    paramJSWriter.closeList();
    paramJSWriter.closeObj();
  }

  public void emitStatusInfo(JSWriter paramJSWriter)
  {
    assert (getMFieldID() == 7);
    paramJSWriter.append("this.StatusEnumInfo=").openList();
    for (int i = 0; i < getMEnumValues().length; i++)
    {
      paramJSWriter.listSep();
      paramJSWriter.openObj();
      paramJSWriter.property("m", getMEnumValues()[i]);
      paramJSWriter.property("l", getMEnumLabels()[i]);
      paramJSWriter.closeObj();
    }
    paramJSWriter.closeList().append(";\n");
  }

  protected int getMEnumType()
  {
    return getEnumField().getMEnumType();
  }

  protected String[] getMEnumValues()
  {
    return getEnumField().getMEnumValues();
  }

  protected long[] getMEnumIds()
  {
    return getEnumField().getMEnumIds();
  }

  protected String[] getMEnumLabels()
  {
    return getEnumField().getMEnumLabels();
  }

  protected boolean isMIsCustom()
  {
    return getEnumField().isMIsCustom();
  }

  protected int getMDefaultValue()
  {
    return getEnumField().getMDefaultValue();
  }

  protected int idToIndex(long paramLong)
  {
    return getEnumField().idToIndex(paramLong);
  }

  protected String getMAltText()
  {
    return getEnumField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.EnumFieldEmitter
 * JD-Core Version:    0.6.1
 */