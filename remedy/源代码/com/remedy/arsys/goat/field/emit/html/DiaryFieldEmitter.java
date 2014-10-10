package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.field.DiaryField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatImageButton;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class DiaryFieldEmitter extends DataFieldEmitter
{
  private DiaryField diaryField;

  public DiaryFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setDiaryField((DiaryField)paramGoatField);
  }

  private void setDiaryField(DiaryField paramDiaryField)
  {
    this.diaryField = paramDiaryField;
  }

  private DiaryField getDiaryField()
  {
    return this.diaryField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    String str1 = "textarea";
    String str2 = getMRows() == 1L ? "text sr " : "text ";
    paramHTMLWriter.openTag(str1).attr("id", "arid_WIN_0_" + getMFieldID()).attr("class", str2 + getMDataFont() + (isMTextOnly() ? " dat" : "") + (isChildOfFlowLayout(paramNode) ? " fltxt" : ""));
    if (getMRows() == 1L)
      paramHTMLWriter.attr("wrap", "off");
    else
      paramHTMLWriter.attr("wrap", "soft");
    paramHTMLWriter.attr("cols", "20");
    paramHTMLWriter.attr("style", getMDataARBox().toBox().toCSS());
    if (isShowURL())
      paramHTMLWriter.attr("arshowurl", "0");
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()))
      paramHTMLWriter.attr("readonly");
    paramHTMLWriter.attr("rows", getMRows());
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      paramHTMLWriter.attr("artitlecode", getDisplayTitleCodeForField());
      if (getMAltText() != null)
        paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
      else
        paramHTMLWriter.attr("title", getLocalizedTitleForField(getDisplayTitleForField()));
    }
    paramHTMLWriter.endTag(false).closeTag(str1);
    if (isShowURL())
    {
      paramHTMLWriter.openTag("div").attr("class", str2 + getMDataFont() + (isMTextOnly() ? " dat" : ""));
      paramHTMLWriter.attr("contentEditable", "false");
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(getMDataARBox().toBox().toCSS());
      localStringBuilder.append("visibility:hidden;");
      paramHTMLWriter.attr("style", localStringBuilder.toString());
      paramHTMLWriter.endTag(false).closeTag("div");
    }
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if ((getMDefaultValue() != null) && (!getMDefaultValue().equals("")))
      paramJSWriter.property("d", getMDefaultValue());
  }

  protected String getMDefaultValue()
  {
    return getDiaryField().getMDefaultValue();
  }

  protected boolean isShowURL()
  {
    return getDiaryField().isMShowURL();
  }

  protected void emitExpandBox(HTMLWriter paramHTMLWriter)
  {
    if (isShowURL())
      emitEditExpandBox(paramHTMLWriter);
    else
      super.emitExpandBox(paramHTMLWriter);
  }

  private void emitEditExpandBox(HTMLWriter paramHTMLWriter)
  {
    String str = getExpandBoxAltText();
    GoatImageButton localGoatImageButton = super.getExpandButton();
    if (localGoatImageButton != null)
      localGoatImageButton.emitMarkup(paramHTMLWriter, getMExpandARBox().toBox(), getExpandBoxClassesString(), str);
  }

  protected String getMAltText()
  {
    return getDiaryField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.DiaryFieldEmitter
 * JD-Core Version:    0.6.1
 */