package com.remedy.arsys.goat.field.emit.html;

import com.bmc.arsys.api.DataType;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.field.ColumnField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.GoatField.LightForm;
import com.remedy.arsys.goat.field.type.TypeMap;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.share.WebWriter;

public abstract class GoatFieldEmitter
  implements IGoatFieldEmitter
{
  private GoatField gf;
  private IEmitterFactory emitterFactory;

  public GoatFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    setGf(paramGoatField);
    setEmitterFactory(paramIEmitterFactory);
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    assert (isMInView());
  }

  protected String emitFillAttsInMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    if (paramNode.mField.getMFloat() == 2)
    {
      if (getMMinWidth() > 0L)
        paramHTMLWriter.attr("arminwidth", "" + getMMinWidth());
      if (getMMinHeight() > 0L)
        paramHTMLWriter.attr("arminheight", "" + getMMinHeight());
    }
    if ((paramNode.mParent == null) || (paramNode.mField.isMFloat()))
      return null;
    boolean bool = isChildOfFillLayout(paramNode);
    if (bool)
    {
      paramHTMLWriter.attr("arcontainerid", "" + getMParentFieldID());
      paramHTMLWriter.attr("arpercentwidth", "100");
      int i = paramNode.mParent.getNumChildren();
      paramHTMLWriter.attr("arpercentheight", "" + Math.round(100 / i));
      if (getMMinWidth() > 0L)
        paramHTMLWriter.attr("arminwidth", "" + getMMinWidth());
      if (getMMinHeight() > 0L)
        paramHTMLWriter.attr("arminheight", "" + getMMinHeight());
      if (getMMaxWidth() > 0L)
        paramHTMLWriter.attr("armaxwidth", "" + getMMaxWidth());
      if (getMMaxHeight() > 0L)
        paramHTMLWriter.attr("armaxheight", "" + getMMaxHeight());
      return getStyleAttributesForFill();
    }
    return null;
  }

  protected String emitFlowAttsInMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    if ((paramNode.mParent == null) || (paramNode.mField.isMFloat()))
      return null;
    boolean bool = isChildOfFlowLayout(paramNode);
    if (bool)
    {
      paramHTMLWriter.attr("arflowid", "" + getMParentFieldID());
      if (getMParentFieldID() == 0)
      {
        paramHTMLWriter.attr("arpercentwidth", "100");
        int i = paramNode.mParent.getNumChildren();
        paramHTMLWriter.attr("arpercentheight", "" + Math.round(100 / i));
        if (getMMinWidth() > 0L)
          paramHTMLWriter.attr("arminwidth", "" + getMMinWidth());
        if (getMMinHeight() > 0L)
          paramHTMLWriter.attr("arminheight", "" + getMMinHeight());
        if (getMMaxWidth() > 0L)
          paramHTMLWriter.attr("armaxwidth", "" + getMMaxWidth());
        if (getMMaxHeight() > 0L)
          paramHTMLWriter.attr("armaxheight", "" + getMMaxHeight());
      }
      return getStyleAttributesForFlow();
    }
    return null;
  }

  public void emitFlowVertSpaceMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter, long paramLong)
  {
    boolean bool = isChildOfFlowLayout(paramNode);
    if ((bool) && (paramLong > 0L))
    {
      paramHTMLWriter.openTag("div").attr("class", "flVspace");
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("height:" + paramLong + "px;");
      paramHTMLWriter.attr("style", localStringBuilder.toString());
      paramHTMLWriter.endTag();
      paramHTMLWriter.closeTag("div");
    }
  }

  protected int getParentFillStyle(FieldGraph.Node paramNode)
  {
    int i = 0;
    if (paramNode.mParent != null)
      if (paramNode.mParent.mField != null)
        i = paramNode.mParent.mField.getMFillStyle();
      else
        i = paramNode.getParentFieldGraph().getViewInfo().getFillStyle();
    return i;
  }

  public void emitCloseMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    assert (isMInView());
  }

  protected void emitOpenHelpBase(HTMLWriter paramHTMLWriter)
  {
    paramHTMLWriter.openTag("div").attr("class", "FieldContainer").endTag();
    paramHTMLWriter.openTag("span").attr("class", "FieldID").endTag().append("" + getMFieldID()).closeTag("span");
  }

  protected void emitCloseHelpBase(HTMLWriter paramHTMLWriter)
  {
    paramHTMLWriter.closeTag("div");
    paramHTMLWriter.openWholeTag("br");
  }

  public void emitOpenHelp(HTMLWriter paramHTMLWriter)
  {
    String str = getHelpText();
    if ((str == null) || (str.trim().length() == 0))
      return;
    emitOpenHelpBase(paramHTMLWriter);
    StringBuilder localStringBuilder1 = new StringBuilder();
    StringBuilder localStringBuilder2 = new StringBuilder();
    localStringBuilder1.append("FieldName ");
    paramHTMLWriter.openTag("span").attr("class", localStringBuilder1.toString()).attr("style", localStringBuilder2.toString()).endTag();
    paramHTMLWriter.cdata(getMDBName());
    paramHTMLWriter.closeTag("span");
    paramHTMLWriter.openTag("span").attr("class", "HelpText").endTag();
    paramHTMLWriter.append(str);
    paramHTMLWriter.closeTag("span");
  }

  public void emitCloseHelp(HTMLWriter paramHTMLWriter)
  {
    String str = getHelpText();
    if ((str == null) || (str.trim().length() == 0))
      return;
    emitCloseHelpBase(paramHTMLWriter);
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    paramJSWriter.property("dbn", getMDBName());
  }

  public void emitScript(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    paramJSWriter.startStatement("new J").appendStr(getMDataTypeString()).appendStr("(").appendStr("windowID").appendStr(",").appendStr("" + getMFieldID()).appendStr(",{");
    emitScriptProperties(paramNode, paramJSWriter);
    paramJSWriter.appendStr("})");
    paramJSWriter.endStatement();
  }

  public void emitTableProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", TypeMap.mapDataType(getMDataType(), getMFieldID()));
    paramJSWriter.property("dt", TypeMap.getARDataType(TypeMap.getOperandDataType(getMDataType())));
  }

  public static void emitEmptyTableProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", TypeMap.mapDataType(DataType.NULL, 0));
    paramJSWriter.property("dt", TypeMap.getARDataType(TypeMap.getOperandDataType(DataType.NULL)));
  }

  public static void emitWeightHackTableProperties(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", TypeMap.mapDataType(DataType.INTEGER, ColumnField.getMWeightFieldID()));
    paramJSWriter.property("dt", TypeMap.getARDataType(TypeMap.getOperandDataType(DataType.INTEGER)));
  }

  public void emitPushFieldProperties(JSWriter paramJSWriter)
  {
  }

  protected GoatField getGf()
  {
    return this.gf;
  }

  public void setGf(GoatField paramGoatField)
  {
    this.gf = paramGoatField;
  }

  protected boolean isMInView()
  {
    return getGf().isMInView();
  }

  protected int getMFieldID()
  {
    return getGf().getMFieldID();
  }

  protected int getMParentFieldID()
  {
    return getGf().getMParentFieldID();
  }

  protected boolean isChildOfFillLayout(FieldGraph.Node paramNode)
  {
    return getGf().isChildOfFillLayout(paramNode);
  }

  protected boolean isChildOfFlowLayout(FieldGraph.Node paramNode)
  {
    return getGf().isChildOfFlowLayout(paramNode);
  }

  protected long getMMinWidth()
  {
    return getGf().getMMinWidth();
  }

  protected long getMMinHeight()
  {
    return getGf().getMMinHeight();
  }

  protected long getMMaxWidth()
  {
    return getGf().getMMaxWidth();
  }

  protected long getMMaxHeight()
  {
    return getGf().getMMaxHeight();
  }

  protected String getMHelpText()
  {
    return getGf().getMHelpText();
  }

  protected String getMDBName()
  {
    return getGf().getMDBName();
  }

  protected String getMDataTypeString()
  {
    return getGf().getMDataTypeString();
  }

  protected DataType getMDataType()
  {
    return getGf().getMDataType();
  }

  protected final String getServer()
  {
    return getGf().getServer();
  }

  protected GoatField.LightForm getMLForm()
  {
    return getGf().getMLForm();
  }

  public ARBox getMARBox()
  {
    return getGf().getMARBox();
  }

  public int getMAlignment()
  {
    return getGf().getMAlignment();
  }

  public boolean isMVisible()
  {
    return getGf().isMVisible();
  }

  public int getMFieldOption()
  {
    return getGf().getMFieldOption();
  }

  public long getMZOrder()
  {
    return getGf().getMZOrder();
  }

  public String getMLabel()
  {
    return getGf().getMLabel();
  }

  public void setMInView(boolean paramBoolean)
  {
    getGf().setMInView(paramBoolean);
  }

  public boolean isMDraggable()
  {
    return getGf().isMDraggable();
  }

  public boolean isMDroppable()
  {
    return getGf().isMDroppable();
  }

  protected void setMLabel(String paramString)
  {
    getGf().setMLabel(paramString);
  }

  protected int getMFillStyle()
  {
    return getGf().getMFillStyle();
  }

  protected int getMView()
  {
    return getGf().getMView();
  }

  public int getMFloat()
  {
    return getGf().getMFloat();
  }

  protected void setMARBox(ARBox paramARBox)
  {
    getGf().setMARBox(paramARBox);
  }

  protected String getStyleAttributesForFill()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    long l = getMMinWidth();
    if (l == 0L)
      l = 20L;
    localStringBuilder.append("min-width:" + l + "px;");
    l = getMMaxWidth();
    if (l == 0L)
      l = 32767L;
    localStringBuilder.append("max-width:" + l + "px;");
    l = getMMinHeight();
    if (l == 0L)
      l = 20L;
    localStringBuilder.append("min-height:" + l + "px;");
    l = getMMaxHeight();
    if (l == 0L)
      l = 32767L;
    localStringBuilder.append("max-height:" + l + "px;");
    return localStringBuilder.toString();
  }

  protected String getStyleAttributesForFlow()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("position: static; clear: left;");
    return localStringBuilder.toString();
  }

  protected String getHelpText()
  {
    String str = MessageTranslation.getLocalizedFieldHelp(getServer(), getMLForm().getFormName().toString(), getMFieldID());
    if (str != null)
    {
      str = HTMLWriter.escape(str);
      str = str.replaceAll("\\n", "<br>");
      return str;
    }
    return getMHelpText();
  }

  public String getLocalizedDescriptionStringForWidget(String paramString)
  {
    return getGf().getLocalizedDescriptionStringForWidget(paramString);
  }

  public String getMCustomCSSStyle()
  {
    return getGf().getMCustomCSSStyle();
  }

  protected String getSelectorClassNames()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (getMCustomCSSStyle() != null)
      localStringBuilder.append(getMCustomCSSStyle()).append(" ");
    localStringBuilder.append("arfid");
    localStringBuilder.append(getMFieldID());
    localStringBuilder.append(" ardbn");
    String str = getMDBName();
    for (int i = 0; i < str.length(); i++)
      if (str.charAt(i) != ' ')
        localStringBuilder.append(str.charAt(i));
    return localStringBuilder.toString();
  }

  protected String getLocalizedTitleForField(String paramString)
  {
    return getGf().getLocalizedTitleForField(paramString);
  }

  public void setEmitterFactory(IEmitterFactory paramIEmitterFactory)
  {
    this.emitterFactory = paramIEmitterFactory;
  }

  protected IEmitterFactory getEmitterFactory()
  {
    return this.emitterFactory;
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    int i = paramNode.getEmitMode();
    if (i == 0)
    {
      paramJSWriter.property("drag", isMDraggable());
      paramJSWriter.property("drop", isMDroppable());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.GoatFieldEmitter
 * JD-Core Version:    0.6.1
 */