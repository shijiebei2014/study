package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.PageField;
import com.remedy.arsys.goat.field.PageHolderField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class PageHolderFieldEmitter extends GoatFieldEmitter
{
  private PageHolderField pageHolderField;

  public PageHolderFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setPageHolderField((PageHolderField)paramGoatField);
  }

  private void setPageHolderField(PageHolderField paramPageHolderField)
  {
    this.pageHolderField = paramPageHolderField;
  }

  private PageHolderField getPageHolderField()
  {
    return this.pageHolderField;
  }

  protected void emitTabMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter, int paramInt)
  {
    FieldGraph.Node[] arrayOfNode = paramNode.getChildNodes();
    if (!isMTablessBorderless())
      paramHTMLWriter.openTag("div").attr("class", "TabChildMissingBorder").attr("style", "visibility:hidden;top:" + (paramInt - 1) + "px;width:" + getMARBox().toBox().mW + "px;").endTag().closeTag("div");
    int i = getMARBox().toBox().mW;
    paramHTMLWriter.openTag("div").attr("style", "visibility:hidden;z-index:3;height:" + paramInt + "px;width:" + i + "px;").attr("class", "OuterTabsDiv").endTag();
    if (!isMTablessBorderless())
      emitScrollingTabs(paramHTMLWriter, true);
    StringBuilder localStringBuilder = new StringBuilder();
    paramHTMLWriter.openTag("div").attr("class", "TabsViewPort");
    localStringBuilder.append("position:relative;overflow:hidden;width:5000px;height:" + paramInt + "px;");
    if (!TextDirStyleContext.get().isRTL())
      localStringBuilder.append("float:left;");
    paramHTMLWriter.attr("style", localStringBuilder.toString());
    paramHTMLWriter.endTag();
    paramHTMLWriter.openTag("div").attr("style", "overflow:visible;float:left;width:5000px;top:0px;left:0px").endTag();
    paramHTMLWriter.openTag("dl").attr("class", "OuterOuterTab").endTag();
    for (int j = 0; j < arrayOfNode.length; j++)
      if (((arrayOfNode[j].mField instanceof PageField)) && (arrayOfNode[j].getEmitMode() == 0))
      {
        PageFieldEmitter localPageFieldEmitter = (PageFieldEmitter)getEmitterFactory().getEmitter(arrayOfNode[j].mField);
        localPageFieldEmitter.emitTabMarkup(j, arrayOfNode[j], paramHTMLWriter);
      }
    paramHTMLWriter.closeTag("dl");
    paramHTMLWriter.closeTag("div");
    paramHTMLWriter.closeTag("div");
    if (!isMTablessBorderless())
      emitScrollingTabs(paramHTMLWriter, false);
    paramHTMLWriter.closeTag("div");
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    assert (isMInView());
    int i = 22;
    paramHTMLWriter.openTag("div").attr("id", "WIN_0_" + getMFieldID()).attr("arid", getMFieldID()).attr("artype", getMDataTypeString()).attr("armaxh", isMTablessBorderless() ? 0 : i);
    String str1 = getMDisplayType() == 0L ? " tb" : "";
    String str2 = (getParentFillStyle(paramNode) == 2) && (getMFloat() == 0) ? " FillWidth " : "";
    paramHTMLWriter.attr("class", "PageHolder " + getSelectorClassNames() + str1 + str2);
    String str3 = emitFillAttsInMarkup(paramNode, paramHTMLWriter);
    String str4 = emitFlowAttsInMarkup(paramNode, paramHTMLWriter);
    if (isMDropShadow())
      paramHTMLWriter.attr("arshadow", "1");
    paramHTMLWriter.attr("arphviewtype", getMPHViewType());
    paramHTMLWriter.attr("arorientation", getMOrientation() == 0L ? "horizontal" : "vertical");
    if (getMStackViewType().length() > 0)
    {
      paramHTMLWriter.attr("arstackviewtype", getMStackViewType());
      if (getMStackViewType().equals("resizable"))
        paramHTMLWriter.attr("splitter", getMSplitter());
    }
    if ((getMInitPage() != null) && (getMInitPage().length() > 0))
      paramHTMLWriter.attr("arinitpage", getMInitPage());
    if (getMFloat() > 0)
      paramHTMLWriter.attr("arfloattype", getMFloat());
    Box localBox = getMARBox().toBox();
    if (getMFloat() == 2)
    {
      localBox.mX = 0;
      localBox.mY = 0;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (getParentFillStyle(paramNode) != 3)
      localStringBuilder.append(localBox.toCSS(getMAlignment()));
    else
      localStringBuilder.append(localBox.toAutoHeightCSS(getMAlignment()));
    if (!isMVisible())
      localStringBuilder.append("visibility:hidden;");
    if (getMZOrder() != -1L)
      localStringBuilder.append("z-index:" + getMZOrder() + ";");
    if ((!isMTransparent()) && (getMDisplayType() != 0L))
    {
      String str5 = paramNode.getParentFieldGraph().getDetailColor();
      if ((str5 == null) || (str5.length() == 0))
        str5 = "#ffffff";
      localStringBuilder.append("background-color:" + str5 + ";");
    }
    if (getMDisplayType() > 0L)
    {
      localStringBuilder.append("margin:0px;");
      localStringBuilder.append("padding-left:" + getMMarginLeft() + "px;");
      localStringBuilder.append("padding-top:" + getMMarginTop() + "px;");
      localStringBuilder.append("padding-right:" + getMMarginRight() + "px;");
      localStringBuilder.append("padding-bottom:" + getMMarginBottom() + "px;");
      paramHTMLWriter.attr("armargin", getMMarginTop() + "," + getMMarginRight() + "," + getMMarginBottom() + "," + getMMarginLeft());
      if (getMOrientation() == 0L)
        localStringBuilder.append("overflow-x:auto;");
      if (!isMBorderless())
      {
        localStringBuilder.append("border:1px solid #717375;");
        paramHTMLWriter.attr("arborder", "1");
      }
    }
    if (str3 != null)
      localStringBuilder.append(str3);
    if (str4 != null)
      localStringBuilder.append(str4);
    paramHTMLWriter.attr("style", localStringBuilder.toString());
    paramHTMLWriter.endTag();
    if (getMStackViewType().length() > 0)
    {
      paramHTMLWriter.openTag("div").attr("class", "PageHolderStackViewResizable");
      if (!getMStackViewType().equals("accordion"))
        if (getParentFillStyle(paramNode) == 3)
          paramHTMLWriter.attr("style", "overflow:auto;");
        else
          paramHTMLWriter.attr("style", getMOrientation() == 0L ? "overflow-x:auto" : "overflow-y:auto");
      paramHTMLWriter.endTag();
      if (getMStackViewType().equals("fixed"))
      {
        paramHTMLWriter.openTag("div").attr("class", getMOrientation() == 0L ? "PageHolderStackViewFixedCH" : "PageHolderStackViewFixedCV");
        if (getParentFillStyle(paramNode) == 3)
          if (getMOrientation() == 0L)
            paramHTMLWriter.attr("style", "overflow-y:visible");
          else
            paramHTMLWriter.attr("style", "height:auto;position:relative;");
        paramHTMLWriter.endTag();
      }
    }
    if (getMDisplayType() == 0L)
      emitTabMarkup(paramNode, paramHTMLWriter, i);
  }

  private void emitScrollingTabs(HTMLWriter paramHTMLWriter, boolean paramBoolean)
  {
    TextDirStyleContext localTextDirStyleContext = TextDirStyleContext.get();
    String str1 = paramBoolean ? "<<" : ">>";
    String str2 = paramBoolean ? ">>" : "<<";
    paramHTMLWriter.openTag("div").attr("class", "ScrollingTab");
    if (str1.equals("<<"))
      paramHTMLWriter.attr("style", "position:relative;float:left;");
    else
      paramHTMLWriter.attr("style", "position:relative;float:right;");
    paramHTMLWriter.endTag();
    paramHTMLWriter.openTag("span").attr("class", "TabLeftRounded").endTag(false).append("&nbsp;").closeTag("span");
    paramHTMLWriter.openTag("span").attr("class", "Tab").endTag(false);
    paramHTMLWriter.openTag("a").attr("href", "javascript:").attr("class", "btn");
    if (paramBoolean)
    {
      paramHTMLWriter.attr("id", "prevTab");
      if (FormContext.get().IsVoiceAccessibleUser())
        paramHTMLWriter.attr("title", "Previous panel holder tab - disabled");
    }
    else
    {
      paramHTMLWriter.attr("id", "nextTab");
      if (FormContext.get().IsVoiceAccessibleUser())
        paramHTMLWriter.attr("title", "Next panel holder tab - disabled");
    }
    paramHTMLWriter.endTag(false);
    paramHTMLWriter.cdata(localTextDirStyleContext.isRTL() ? str2 : str1);
    paramHTMLWriter.closeTag("a");
    paramHTMLWriter.closeTag("span");
    paramHTMLWriter.openTag("span").attr("class", "TabRightRounded").endTag(false).append("&nbsp;").closeTag("span");
    paramHTMLWriter.closeTag("div");
  }

  private void createHeaderLabel(HTMLWriter paramHTMLWriter, String paramString1, String paramString2, String paramString3)
  {
    paramHTMLWriter.openTag("span");
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramString2 != null) && (paramString2.length() > 0))
      localStringBuilder.append("label " + paramString2);
    if ((getMStackViewType().equals("accordion")) || (getMStackViewType().equals("resizable")))
      localStringBuilder.append(" clickArea ");
    if ((getMStackViewType().equals("fixed")) || (getMStackViewType().equals("resizable")))
      localStringBuilder.append(" vlabel ");
    if ((paramString3 != null) && (paramString3.length() > 0))
      paramHTMLWriter.attr("style", "color:" + paramString3 + ";");
    if (getMOrientation() == 0L)
    {
      if (TextDirStyleContext.get().isRTL())
        localStringBuilder.append(" rotateHdr90 ");
      else
        localStringBuilder.append(" rotateHdr270 ");
      paramHTMLWriter.attr("vtext", paramString1);
      if ((paramString3 != null) && (paramString3.length() > 0))
        paramHTMLWriter.attr("tcolor", paramString3.substring(1));
      if ((paramString2 != null) && (paramString2.length() > 0))
        paramHTMLWriter.attr("tfont", paramString2);
      if (getMStackViewType().equals("fixed"))
        paramHTMLWriter.attr("otop", "20");
      if (TextDirStyleContext.get().isRTL())
        paramHTMLWriter.attr("rotate", "90");
    }
    paramHTMLWriter.attr("class", localStringBuilder.toString());
    paramHTMLWriter.endTag(false);
    if (getMOrientation() == 1L)
      paramHTMLWriter.append("&nbsp;" + paramString1);
    paramHTMLWriter.closeTag("span");
  }

  public void emitPageHeaderMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, long paramLong)
  {
    if (getMDisplayType() > 0L)
    {
      FormContext localFormContext = FormContext.get();
      String str = "PageHeader" + (getMOrientation() == 0L ? "Horizontal" : "Vertical");
      paramHTMLWriter.openTag("div");
      PageField localPageField = (PageField)paramNode.mField;
      if (localPageField.getMLineWidth() <= 0L)
        str = str + " PageHeaderNoBorder";
      paramHTMLWriter.attr("class", str);
      if ((paramLong == 0L) && (paramString2 != null) && (paramString2.length() > 0))
        if (getMOrientation() == 0L)
        {
          localStringBuilder = new StringBuilder();
          paramHTMLWriter.attr("arcolor", paramString2.substring(1));
          localStringBuilder.append("background: -moz-linear-gradient(center center," + paramString2 + "," + paramString2 + ");");
          localStringBuilder.append("background: -webkit-gradient(linear, center center, center center, from(" + paramString2 + "),to(" + paramString2 + "));");
          paramHTMLWriter.attr("style", localStringBuilder.toString());
        }
        else
        {
          paramHTMLWriter.attr("style", "background-color:" + paramString2 + ";");
          paramHTMLWriter.attr("arcolor", paramString2.substring(1));
        }
      if ((paramString5 != null) && (paramLong > 0L))
      {
        localStringBuilder = new StringBuilder();
        localObject = getGradientEffect(paramNode, paramString2, paramString5, paramLong);
        localStringBuilder.append(" background: -moz-linear-gradient(" + getPointMoz(paramString2, paramString5, paramLong) + ") ;");
        localStringBuilder.append("background: -webkit-gradient(linear," + getPointSafari(paramString2, paramString5, paramLong) + ");");
        paramHTMLWriter.attr("style", localStringBuilder.toString());
        paramHTMLWriter.attr("argcolor", (String)localObject);
      }
      paramHTMLWriter.endTag(false);
      StringBuilder localStringBuilder = new StringBuilder();
      paramHTMLWriter.openTag("a").attr("href", "javascript:");
      localStringBuilder.append("pagebtn ");
      if (getMStackViewType().equals("accordion"))
        localStringBuilder.append(" Accordion ");
      paramHTMLWriter.attr("class", localStringBuilder.toString());
      paramHTMLWriter.attr("title", paramString1);
      Object localObject = new StringBuilder();
      if ((!getMStackViewType().equals("accordion")) && (!getMStackViewType().equals("resizable")))
        ((StringBuilder)localObject).append("float:" + TextDirStyleContext.get().lleft + ";background:none;");
      paramHTMLWriter.attr("style", ((StringBuilder)localObject).toString()).endTag(false);
      if (getMStackViewType().equals("accordion"))
      {
        createHeaderLabel(paramHTMLWriter, paramString1, paramString3, paramString4);
        paramHTMLWriter.closeTag("a");
      }
      else if (getMStackViewType().equals("fixed"))
      {
        paramHTMLWriter.openTag("span");
        if (getMOrientation() == 1L)
          paramHTMLWriter.attr("class", "Twisty Tsize");
        else
          paramHTMLWriter.attr("class", "Twisty");
        if (TextDirStyleContext.get().isRTL())
          paramHTMLWriter.attr("dir", "ltr");
        if ((paramString4 != null) && (paramString4.length() > 0))
          paramHTMLWriter.attr("style", "color:" + paramString4 + ";");
        paramHTMLWriter.endTag(false);
        if (getMOrientation() == 0L)
        {
          if (TextDirStyleContext.get().isRTL())
            paramHTMLWriter.append("&nbsp;&#9668").closeTag("span");
          else
            paramHTMLWriter.append("&nbsp;&#9658").closeTag("span");
        }
        else
          paramHTMLWriter.append("&nbsp;&#9660").closeTag("span");
        paramHTMLWriter.closeTag("a");
        createHeaderLabel(paramHTMLWriter, paramString1, paramString3, paramString4);
      }
      else
      {
        createHeaderLabel(paramHTMLWriter, paramString1, paramString3, paramString4);
        paramHTMLWriter.closeTag("a");
      }
      paramHTMLWriter.closeTag("div");
    }
  }

  public void emitOpenInitialPageBodyMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter, long paramLong)
  {
    if (getMDisplayType() > 0L)
    {
      paramHTMLWriter.openTag("fieldset").attr("class", "PageBody" + (getMOrientation() == 0L ? "Horizontal" : "Vertical"));
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append((getMOrientation() == 0L ? "width:" : "height:") + paramLong + ";");
      paramHTMLWriter.attr("style", localStringBuilder.toString()).endTag();
    }
  }

  public void emitCloseInitialPageBodyMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    if (getMDisplayType() > 0L)
      paramHTMLWriter.closeTag("fieldset");
  }

  public void emitCloseMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    assert (isMInView());
    if (getMStackViewType().length() > 0)
    {
      if (getMStackViewType().equals("fixed"))
        paramHTMLWriter.closeTag("div");
      paramHTMLWriter.closeTag("div");
    }
    paramHTMLWriter.closeTag("div");
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    paramJSWriter.property("v", isMVisible());
    int i = paramNode.getEmitMode();
    if (i == 0)
      paramJSWriter.property("l", getMLabel() == null ? "" : getMLabel());
  }

  private String getGradientEffect(FieldGraph.Node paramNode, String paramString1, String paramString2, long paramLong)
  {
    if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L))
    {
      if (paramLong == 1L)
        str = "LH";
      else if (paramLong == 2L)
        str = "LV";
      else if (paramLong == 3L)
        str = "RH";
      else
        str = "RV";
      String str = str + paramString1.substring(1) + paramString2.substring(1);
      return str;
    }
    return null;
  }

  private String getRegPointSafari(String paramString1, String paramString2, long paramLong)
  {
    String str = "";
    if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L))
      if (paramLong == 1L)
        str = "center top, center bottom,from(" + paramString1 + "),to(" + paramString2 + ")";
      else if (paramLong == 2L)
        str = "left center, right center,from(" + paramString1 + "),to(" + paramString2 + ")";
      else if (paramLong == 3L)
        str = "center top, center bottom, from(" + paramString1 + "),  color-stop(50%," + paramString2 + "), to(" + paramString1 + ")";
      else
        str = "left center, right center, from(" + paramString1 + "),  color-stop(50%," + paramString2 + "), to(" + paramString1 + ")";
    return str;
  }

  private String getRegPointMoz(String paramString1, String paramString2, long paramLong)
  {
    String str = "";
    if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L))
      if (paramLong == 1L)
        str = "bottom," + paramString2 + "," + paramString1;
      else if (paramLong == 2L)
        str = "right ," + paramString2 + "," + paramString1;
      else if (paramLong == 3L)
        str = "top, " + paramString1 + ", " + paramString2 + " 50%, " + paramString1;
      else
        str = "left, " + paramString1 + ", " + paramString2 + " 50%, " + paramString1;
    return str;
  }

  private String getPointSafari(String paramString1, String paramString2, long paramLong)
  {
    String str = "";
    if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L) && (getMOrientation() == 0L))
    {
      if (paramLong == 1L)
        str = "center top, center bottom,from(" + paramString1 + "),to(" + paramString2 + ")";
      else if (paramLong == 2L)
        str = "left center, right center,from(" + paramString1 + "),to(" + paramString2 + ")";
      else if (paramLong == 3L)
        str = "center top, center bottom, from(" + paramString1 + "),  color-stop(50%," + paramString2 + "), to(" + paramString1 + ")";
      else
        str = "left center, right center, from(" + paramString1 + "),  color-stop(50%," + paramString2 + "), to(" + paramString1 + ")";
    }
    else if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L) && (getMOrientation() == 1L))
      if (paramLong == 1L)
        str = "center top, center bottom,from(" + paramString1 + "),to(" + paramString2 + ")";
      else if (paramLong == 2L)
        str = "left center, right center,from(" + paramString1 + "),to(" + paramString2 + ")";
      else if (paramLong == 3L)
        str = "center top, center bottom, from(" + paramString1 + "),  color-stop(50%," + paramString2 + "), to(" + paramString1 + ")";
      else
        str = "left center, right center, from(" + paramString1 + "),  color-stop(50%," + paramString2 + "), to(" + paramString1 + ")";
    return str;
  }

  private String getPointMoz(String paramString1, String paramString2, long paramLong)
  {
    String str = "";
    if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L) && (getMOrientation() == 0L))
    {
      if (paramLong == 1L)
        str = "bottom," + paramString2 + "," + paramString1;
      else if (paramLong == 2L)
        str = "right," + paramString2 + "," + paramString1;
      else if (paramLong == 3L)
        str = "top, " + paramString1 + ", " + paramString2 + " 50%, " + paramString1;
      else
        str = "left, " + paramString1 + ", " + paramString2 + " 50%, " + paramString1;
    }
    else if ((paramString1 != null) && (paramString2 != null) && (paramLong > 0L) && (getMOrientation() == 1L))
      if (paramLong == 1L)
        str = "top," + paramString1 + "," + paramString2;
      else if (paramLong == 2L)
        str = "left," + paramString1 + "," + paramString2;
      else if (paramLong == 3L)
        str = "top, " + paramString1 + ", " + paramString2 + " 50%, " + paramString1;
      else
        str = "left, " + paramString1 + ", " + paramString2 + " 50%, " + paramString1;
    return str;
  }

  protected boolean isMTablessBorderless()
  {
    return getPageHolderField().isMTablessBorderless();
  }

  protected boolean isMDropShadow()
  {
    return getPageHolderField().isMDropShadow();
  }

  protected String getMPHViewType()
  {
    return getPageHolderField().getMPHViewType();
  }

  protected long getMOrientation()
  {
    return getPageHolderField().getMOrientation();
  }

  protected String getMStackViewType()
  {
    return getPageHolderField().getMStackViewType();
  }

  protected int getMSplitter()
  {
    return getPageHolderField().getMSplitter();
  }

  protected String getMInitPage()
  {
    return getPageHolderField().getMInitPage();
  }

  protected boolean isMTransparent()
  {
    return getPageHolderField().isMTransparent();
  }

  protected long getMDisplayType()
  {
    return getPageHolderField().getMDisplayType();
  }

  protected long getMMarginLeft()
  {
    return getPageHolderField().getMMarginLeft();
  }

  protected long getMMarginTop()
  {
    return getPageHolderField().getMMarginTop();
  }

  protected long getMMarginRight()
  {
    return getPageHolderField().getMMarginRight();
  }

  protected long getMMarginBottom()
  {
    return getPageHolderField().getMMarginBottom();
  }

  protected boolean isMBorderless()
  {
    return getPageHolderField().isMBorderless();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.PageHolderFieldEmitter
 * JD-Core Version:    0.6.1
 */