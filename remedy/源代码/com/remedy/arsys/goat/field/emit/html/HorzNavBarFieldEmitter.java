package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.HorzNavBarField;
import com.remedy.arsys.goat.field.NavBarItemField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class HorzNavBarFieldEmitter extends GoatFieldEmitter
{
  private HorzNavBarField horzNavBarField;

  public HorzNavBarFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setHorzNavBarField((HorzNavBarField)paramGoatField);
  }

  private void setHorzNavBarField(HorzNavBarField paramHorzNavBarField)
  {
    this.horzNavBarField = paramHorzNavBarField;
  }

  private HorzNavBarField getHorzNavBarField()
  {
    return this.horzNavBarField;
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    paramJSWriter.property("v", isMVisible());
    int i = paramNode.getEmitMode();
    if (i == 0)
      paramJSWriter.property("a", getMAccess());
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    Box localBox = getMARBox().toBox();
    StringBuilder localStringBuilder1 = new StringBuilder();
    if (getParentFillStyle(paramNode) == 2)
      localStringBuilder1.append("HNavBar ").append(getSelectorClassNames()).append(" FillWidth");
    else
      localStringBuilder1.append("HNavBar ").append(getSelectorClassNames());
    StringBuilder localStringBuilder2 = new StringBuilder();
    if (!isMVisible())
      localStringBuilder2.append("visibility:hidden; ");
    if (getMZOrder() != -1L)
      localStringBuilder2.append("z-index:" + getMZOrder() + "; ");
    TextDirStyleContext localTextDirStyleContext = TextDirStyleContext.get();
    localStringBuilder2.append(localBox.toCSS(getMAlignment()));
    paramHTMLWriter.openTag("dl").attr("arid", getMFieldID()).attr("artype", getMDataTypeString()).attr("ardbn", getMDBName());
    String str1 = emitFillAttsInMarkup(paramNode, paramHTMLWriter);
    String str2 = emitFlowAttsInMarkup(paramNode, paramHTMLWriter);
    if (getMInitialValue() > 0L)
      paramHTMLWriter.attr("initvalue", getMInitialValue());
    paramHTMLWriter.attr("workflowonselected", getMWorkflowOnSelected());
    paramHTMLWriter.attr("selectonclick", getMSelectOnClick());
    paramHTMLWriter.attr("class", localStringBuilder1.toString());
    if (str1 != null)
      localStringBuilder2.append(str1);
    if (str2 != null)
      localStringBuilder2.append(str2);
    paramHTMLWriter.attr("style", localStringBuilder2.toString()).endTag(true);
    paramHTMLWriter.openTag("dt").endTag(false).append("HNavBar").closeTag("dt");
    paramHTMLWriter.openTag("dd").attr("class", "HNavLeftGap").endTag(false).openTag("span").endTag(false).append(" ").closeTag("span", false).closeTag("dd");
    if (paramNode.getChildNodes() != null)
      emitChildrenMarkup(paramNode.getChildNodes(), paramHTMLWriter, localTextDirStyleContext);
  }

  private void emitChildrenMarkup(FieldGraph.Node[] paramArrayOfNode, HTMLWriter paramHTMLWriter, TextDirStyleContext paramTextDirStyleContext)
  {
    TreeSet localTreeSet = new TreeSet(new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        FieldGraph.Node localNode1 = (FieldGraph.Node)paramAnonymousObject1;
        FieldGraph.Node localNode2 = (FieldGraph.Node)paramAnonymousObject2;
        assert ((localNode1.mField != null) && ((localNode1.mField instanceof NavBarItemField)));
        NavBarItemField localNavBarItemField1 = (NavBarItemField)localNode1.mField;
        NavBarItemField localNavBarItemField2 = (NavBarItemField)localNode2.mField;
        return localNavBarItemField1.getMPosition() > localNavBarItemField2.getMPosition() ? 1 : -1;
      }
    });
    for (int i = 0; i < paramArrayOfNode.length; i++)
      localTreeSet.add(paramArrayOfNode[i]);
    Iterator localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
    {
      FieldGraph.Node localNode = (FieldGraph.Node)localIterator.next();
      NavBarItemField localNavBarItemField = (NavBarItemField)localNode.mField;
      if ((localNavBarItemField.getMItemType() == 0) || (localNavBarItemField.getMItemType() == 2))
      {
        int j = 0;
        if (localNavBarItemField.getMPosition() == 1)
          j++;
        if (!localIterator.hasNext())
          j += 2;
        emitChildMarkup(localNavBarItemField, paramHTMLWriter, j, paramTextDirStyleContext);
      }
    }
  }

  private void emitChildMarkup(NavBarItemField paramNavBarItemField, HTMLWriter paramHTMLWriter, int paramInt, TextDirStyleContext paramTextDirStyleContext)
  {
    int i = paramNavBarItemField.getMItemType() == 2 ? 1 : 0;
    StringBuilder localStringBuilder = new StringBuilder();
    if (i != 0)
      localStringBuilder.append("HNavSep ");
    else if (paramNavBarItemField.getMFieldID() == getMInitialValue())
      localStringBuilder.append("HNavSelected ");
    else
      localStringBuilder.append("HNavItem ");
    if (paramNavBarItemField.getMAccess() == 3)
      localStringBuilder.append("HNavDisabled ");
    localStringBuilder.append(paramNavBarItemField.getSelectorClassNames());
    paramHTMLWriter.openTag("dd").attr("arid", paramNavBarItemField.getMFieldID()).attr("artype", paramNavBarItemField.getMDataTypeString()).attr("ardbn", paramNavBarItemField.getMDBName());
    if (i != 0)
      paramHTMLWriter.attr("separator", "1");
    paramHTMLWriter.attr("type", "horz").attr("parentid", paramNavBarItemField.getMParentFieldID()).attr("class", localStringBuilder.toString());
    if (!paramNavBarItemField.isMVisible())
      paramHTMLWriter.attr("style", "display:none");
    paramHTMLWriter.endTag(false);
    String str;
    if (!paramTextDirStyleContext.isRTL())
      str = (paramInt & 0x1) == 1 ? "leftrnd" : "left";
    else
      str = (paramInt & 0x2) == 2 ? "leftrnd" : "left";
    paramHTMLWriter.openTag("span").attr("class", str).endTag(false).append(" ").closeTag("span", false);
    paramHTMLWriter.openTag("span").attr("class", "center").endTag(false);
    if (i == 0)
    {
      paramHTMLWriter.openTag("a").attr("href", "javascript:").attr("class", "btn");
      if (FormContext.get().IsVoiceAccessibleUser())
        if (paramNavBarItemField.getMAccess() == 3)
          paramHTMLWriter.attr("title", paramNavBarItemField.getLocalizedDescriptionStringForWidget("tab {0} Disabled"));
        else if (paramNavBarItemField.getMFieldID() == getMInitialValue())
          paramHTMLWriter.attr("title", paramNavBarItemField.getLocalizedDescriptionStringForWidget("tab {0} Selected"));
        else
          paramHTMLWriter.attr("title", paramNavBarItemField.getLocalizedDescriptionStringForWidget("tab {0}"));
      paramHTMLWriter.endTag(false);
    }
    paramHTMLWriter.cdata(i != 0 ? " " : paramNavBarItemField.getMLabel());
    if (i == 0)
      paramHTMLWriter.closeTag("a", false);
    paramHTMLWriter.closeTag("span", false);
    if (!paramTextDirStyleContext.isRTL())
      str = (paramInt & 0x2) == 2 ? "rightrnd" : "right";
    else
      str = (paramInt & 0x1) == 1 ? "rightrnd" : "right";
    paramHTMLWriter.openTag("span").attr("class", str).endTag(false).append(" ").closeTag("span", false);
    paramHTMLWriter.closeTag("dd", true);
  }

  public void emitCloseMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    assert (isMInView());
    paramHTMLWriter.closeTag("dl");
  }

  protected int getMAccess()
  {
    return getHorzNavBarField().getMAccess();
  }

  protected int getMWorkflowOnSelected()
  {
    return getHorzNavBarField().getMWorkflowOnSelected();
  }

  protected long getMInitialValue()
  {
    return getHorzNavBarField().getMInitialValue();
  }

  protected int getMSelectOnClick()
  {
    return getHorzNavBarField().getMSelectOnClick();
  }

  protected String getMAltText()
  {
    return getHorzNavBarField().getMAltText();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.HorzNavBarFieldEmitter
 * JD-Core Version:    0.6.1
 */