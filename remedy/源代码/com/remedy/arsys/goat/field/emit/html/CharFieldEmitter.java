package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.CharField;
import com.remedy.arsys.goat.field.ControlField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatImageButton;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class CharFieldEmitter extends DataFieldEmitter
{
  private CharField charField;

  public CharFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setCharField((CharField)paramGoatField);
  }

  private void setCharField(CharField paramCharField)
  {
    this.charField = paramCharField;
  }

  private CharField getCharField()
  {
    return this.charField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    TextDirStyleContext localTextDirStyleContext = TextDirStyleContext.get();
    String str1;
    String str2;
    String str3;
    if (getMDisplayType() == 2)
    {
      str1 = "input";
      str2 = "password";
      str3 = "text ";
      str3 = str3 + "pf ";
    }
    else if (isMRichTextField())
    {
      str1 = "div";
      str3 = getMRows() == 1L ? "text sr " : "text ";
      str3 = str3 + "rtf ";
      str2 = null;
    }
    else
    {
      str1 = "textarea";
      str2 = null;
      if (getMRows() == 1L)
      {
        str3 = "text sr ";
        if (getMDataFont().equals("f2"))
          str3 = str3 + " f2sr ";
      }
      else
      {
        str3 = "text ";
      }
    }
    paramHTMLWriter.openTag(str1).attr("class", str3 + getMDataFont() + (isMTextOnly() ? " dat" : "") + ((isChildOfFlowLayout(paramNode)) && (!localTextDirStyleContext.isRTL()) ? " fltxt" : ""));
    if (getMDisplayType() != 2)
      if (getMRows() == 1L)
        paramHTMLWriter.attr("wrap", "off");
      else
        paramHTMLWriter.attr("wrap", "soft");
    if ((isMRichTextField()) && (getMAttachmentFieldID() > 0))
    {
      paramHTMLWriter.attr("arpool", Integer.toString(getMAttachmentFieldID()));
      if ((getCharField() != null) && (getCharField().getMJoinIndex() >= 0))
        paramHTMLWriter.attr("arindx", Integer.toString(getCharField().getMJoinIndex()));
    }
    Object localObject3;
    if ((isMRichTextField()) && (getMExternalURLFieldID() > 0))
    {
      paramHTMLWriter.attr("arexturl", Integer.toString(getMExternalURLFieldID()));
      localObject1 = paramNode.getParentFieldGraph();
      localObject2 = null;
      try
      {
        localObject2 = ((FieldGraph)localObject1).getField(getMExternalURLFieldID());
      }
      catch (GoatException localGoatException)
      {
      }
      if ((localObject2 != null) && ((localObject2 instanceof ControlField)))
      {
        String str4 = ((ControlField)localObject2).getMButtonText();
        if ((str4 != null) && (str4.length() > 0) && (!str4.equalsIgnoreCase("Button")))
        {
          paramHTMLWriter.attr("arextlabel", ((ControlField)localObject2).getMButtonText());
          if (((GoatField)localObject2).getMARBox() != null)
          {
            localObject3 = ((GoatField)localObject2).getMARBox().toBox();
            if ((localObject3 != null) && (((Box)localObject3).mW > 0))
              paramHTMLWriter.attr("arextwidth", ((Box)localObject3).mW);
          }
        }
      }
    }
    paramHTMLWriter.attr("id", "arid_WIN_0_" + getMFieldID());
    if (str1.equals("textarea"))
      paramHTMLWriter.attr("cols", "20");
    if (isShowURL())
      paramHTMLWriter.attr("arshowurl", "0");
    if (getMDisplayType() == 5)
      paramHTMLWriter.attr("artf", "1");
    if ((isMRichTextField()) && (getMAutoSize() > 0) && (getParentFillStyle(paramNode) == 2))
    {
      paramHTMLWriter.attr("arautosize", getMAutoSize());
      if (getMMaxHeight() > 0L)
        paramHTMLWriter.attr("armaxheight", getMMaxHeight());
      if (getMMaxWidth() > 0L)
        paramHTMLWriter.attr("armaxwidth", getMMaxWidth());
    }
    paramHTMLWriter.attr("maxlen", getMMaxLength());
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      paramHTMLWriter.attr("artitlecode", getDisplayTitleCodeForField());
      if (getMAltText() != null)
        paramHTMLWriter.attr("title", getLocalizedTitleForField(getMAltText()));
      else
        paramHTMLWriter.attr("title", getLocalizedTitleForField(getDisplayTitleForField()));
    }
    if (str2 != null)
      paramHTMLWriter.attr("type", str2);
    Object localObject1 = getMDataARBox().toBox();
    if (getMDisplayType() == 1)
    {
      localObject1.mW -= ((Box)localObject1).mH - 2;
      if (((Box)localObject1).mW < 0)
        ((Box)localObject1).mW = 0;
    }
    Object localObject2 = ((Box)localObject1).toCSS();
    if (((getMLabelAlign() == 4) || (getMLabelAlign() == 1)) && (isChildOfFlowLayout(paramNode)) && (!localTextDirStyleContext.isRTL()))
      localObject2 = (String)localObject2 + "clear: left;";
    if ((getMRows() > 1L) && (isMRichTextField()))
      localObject2 = (String)localObject2 + ";overflow:hidden;overflow-x:hidden;overflow-y:hidden;padding-left:0px;padding-right:0px;padding-top:0px;padding-bottom:0px;";
    paramHTMLWriter.attr("style", (String)localObject2);
    if (getMCharMenu() != null)
    {
      paramHTMLWriter.attr("armenu", getMCharMenu());
      if (getMMenuStyle() == 2)
        paramHTMLWriter.attr("mstyle", getMMenuStyle());
    }
    if (getMAccess() == 3L)
      paramHTMLWriter.attr("disabled");
    if ((getMAccess() == 1L) || (isMTextOnly()) || (getMDisplayType() == 1))
      paramHTMLWriter.attr("readonly");
    if (getMDisplayType() == 1)
    {
      paramHTMLWriter.attr("mdd", 1);
      if (isMEnableClear())
        paramHTMLWriter.attr("searchOnly", 1);
    }
    if (getMAutoCompleteStyle() > 0)
      paramHTMLWriter.attr("arautoc", getMAutoCompleteStyle());
    int i = getMAutoCompleteMatchBy();
    if (getMAutoCompleteMatchBy() > 0)
      paramHTMLWriter.attr("arautocmb", "" + i);
    paramHTMLWriter.attr("arautocak", this.charField.getMAutoCompleteAfterKeyStrokes());
    paramHTMLWriter.attr("arautoctt", Configuration.getInstance().getAutoCompleteTypingTimeout());
    if (str2 == null)
    {
      paramHTMLWriter.attr("rows", getMRows());
      paramHTMLWriter.endTag(false).closeTag(str1);
    }
    else
    {
      assert (str1.equals("input"));
      paramHTMLWriter.closeOpenTag();
    }
    if (isShowURL())
    {
      paramHTMLWriter.openTag("div").attr("class", str3 + getMDataFont() + (isMTextOnly() ? " dat" : ""));
      paramHTMLWriter.attr("contentEditable", "false");
      localObject3 = new StringBuilder();
      ((StringBuilder)localObject3).append(((Box)localObject1).toCSS());
      ((StringBuilder)localObject3).append("visibility:hidden;");
      paramHTMLWriter.attr("style", ((StringBuilder)localObject3).toString());
      paramHTMLWriter.endTag(false).closeTag("div");
    }
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    if ((getMDefaultValue() != null) && (!getMDefaultValue().equals("")))
      paramJSWriter.property("d", getMDefaultValue());
    int i = paramNode.getEmitMode();
    if (i == 0)
      paramJSWriter.property("m", getMCharMenu());
  }

  public void emitTableProperties(JSWriter paramJSWriter)
  {
    super.emitTableProperties(paramJSWriter);
    paramJSWriter.property("m", getMCharMenu());
    paramJSWriter.property("dLen", getMMaxLength());
  }

  protected void emitMenuBox(HTMLWriter paramHTMLWriter)
  {
    if (getMCharMenu() != null)
    {
      Box localBox1 = getMMenuARBox().toBox();
      if (getMDisplayType() == 1)
      {
        Box localBox2 = getMDataARBox().toBox();
        localBox2.mW -= localBox2.mH - 2;
        if (localBox2.mW < 0)
          localBox2.mW = 0;
        localBox1.mX = (localBox2.mX + localBox2.mW - 2);
        localBox1.mY = localBox2.mY;
        localBox1.mH = (localBox1.mW = localBox2.mH);
      }
      super.emitMenuBox(paramHTMLWriter, localBox1, getMCharMenu().equals("$NULL$"));
    }
  }

  protected void emitExpandBox(HTMLWriter paramHTMLWriter)
  {
    if (getMDisplayType() == 3)
      emitFileUploadBox(paramHTMLWriter);
    else if ((isMRichTextField()) && (!FormContext.get().IsVoiceAccessibleUser()))
      emitRichTextEditorBox(paramHTMLWriter);
    else if (isShowURL())
      emitEditExpandBox(paramHTMLWriter);
    else
      super.emitExpandBox(paramHTMLWriter);
  }

  private void emitEditExpandBox(HTMLWriter paramHTMLWriter)
  {
    String str = getExpandBoxAltText();
    GoatImageButton localGoatImageButton = getEditBoxButton();
    if (localGoatImageButton != null)
      localGoatImageButton.emitMarkup(paramHTMLWriter, getMExpandARBox().toBox(), getEditBoxClassString(), str);
  }

  protected void emitFileUploadBox(HTMLWriter paramHTMLWriter)
  {
    String str = getExpandBoxAltText();
    GoatImageButton localGoatImageButton = getFileUploadButton();
    if (localGoatImageButton != null)
      localGoatImageButton.emitMarkup(paramHTMLWriter, getMExpandARBox().toBox(), getFileUploadClassString(), str);
  }

  protected void emitRichTextEditorBox(HTMLWriter paramHTMLWriter)
  {
    String str = getExpandBoxAltText();
    GoatImageButton localGoatImageButton = getRichTextEditorButton();
    if (localGoatImageButton != null)
      localGoatImageButton.emitMarkup(paramHTMLWriter, getMExpandARBox().toBox(), getRichTextEditorClassString(), str);
  }

  protected int getMMaxLength()
  {
    return getCharField().getMMaxLength();
  }

  protected int getMMenuStyle()
  {
    return getCharField().getMMenuStyle();
  }

  protected int getMAutoCompleteStyle()
  {
    return getCharField().getMAutoCompleteStyle();
  }

  protected boolean isMEnableClear()
  {
    return getCharField().isMEnableClear();
  }

  protected int getMAutoCompleteMatchBy()
  {
    return getCharField().getMAutoCompleteMatchBy();
  }

  protected int getAutoCompleteAfterKeyStrokes()
  {
    return getCharField().getMAutoCompleteAfterKeyStrokes();
  }

  protected boolean isMAutoCompleteHideMenuButton()
  {
    return getCharField().isMAutoCompleteHideMenuButton();
  }

  protected int getMQBEMatch()
  {
    return getCharField().getMQBEMatch();
  }

  protected String getMCharMenu()
  {
    return getCharField().getMCharMenu();
  }

  protected String getMCharPattern()
  {
    return getCharField().getMCharPattern();
  }

  protected int getMFullTextOption()
  {
    return getCharField().getMFullTextOption();
  }

  protected String getMDefaultValue()
  {
    return getCharField().getMDefaultValue();
  }

  protected int getMDisplayType()
  {
    return getCharField().getMDisplayType();
  }

  protected GoatImageButton getFileUploadButton()
  {
    return getCharField().getFileUploadButton();
  }

  protected GoatImageButton getEditBoxButton()
  {
    return getCharField().getEditBoxButton();
  }

  protected GoatImageButton getRichTextEditorButton()
  {
    return getCharField().getRichTextEditorButton();
  }

  protected String getFileUploadClassString()
  {
    return getCharField().getFileUploadClassString();
  }

  protected String getEditBoxClassString()
  {
    return getCharField().getEditBoxClassesString();
  }

  protected boolean isShowURL()
  {
    if (isMRichTextField())
      return false;
    return getCharField().isMShowURL();
  }

  protected int getMAttachmentFieldID()
  {
    return getCharField().getMAttachmentFieldID();
  }

  protected String getMAltText()
  {
    return getCharField().getMAltText();
  }

  protected String getRichTextEditorClassString()
  {
    return getCharField().getRichTextEditorClassString();
  }

  protected boolean isMRichTextField()
  {
    return getCharField().isMRichTextField();
  }

  protected int getMExternalURLFieldID()
  {
    return getCharField().getMExternalURLFieldID();
  }

  protected int getMAutoSize()
  {
    return getCharField().getMAutoSize();
  }

  protected long getMMaxHeight()
  {
    return getCharField().getMMaxHeight();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.CharFieldEmitter
 * JD-Core Version:    0.6.1
 */