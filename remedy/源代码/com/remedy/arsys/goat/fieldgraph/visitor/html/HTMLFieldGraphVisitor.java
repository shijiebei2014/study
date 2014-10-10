package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.bmc.arsys.api.DataType;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.PageField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.share.HTMLWriter;

public class HTMLFieldGraphVisitor extends BaseHTMLFieldGraphVisitor
{
  public HTMLFieldGraphVisitor(HTMLWriter paramHTMLWriter, IEmitterFactory paramIEmitterFactory)
  {
    super(paramHTMLWriter, paramIEmitterFactory);
  }

  public void visitBeforeChildren(FieldGraph.Node paramNode)
  {
    if (paramNode.mParent != null)
    {
      int i = paramNode.getEmitMode();
      if (i == 0)
      {
        IGoatFieldEmitter localIGoatFieldEmitter = this.emitterFactory.getEmitter(paramNode.mField);
        localIGoatFieldEmitter.emitOpenMarkup(paramNode, this.hw);
      }
    }
  }

  public void visitAfterChildren(FieldGraph.Node paramNode)
  {
    if ((paramNode.mParent != null) && (paramNode.getEmitMode() == 0))
    {
      IGoatFieldEmitter localIGoatFieldEmitter = this.emitterFactory.getEmitter(paramNode.mField);
      localIGoatFieldEmitter.emitCloseMarkup(paramNode, this.hw);
      if ((paramNode.mParent != null) && (paramNode.mParent.mField != null))
      {
        if (DataType.PAGE.equals(paramNode.mParent.mField.getMDataType()))
        {
          long l = 0L;
          PageField localPageField = (PageField)paramNode.mParent.mField;
          if (localPageField.getMVerSpace() > 0L)
          {
            l = localPageField.getMVerSpace();
            localIGoatFieldEmitter.emitFlowVertSpaceMarkup(paramNode, this.hw, l);
          }
        }
      }
      else if (paramNode.mParent != null)
      {
        FieldGraph localFieldGraph = paramNode.getParentFieldGraph();
        Form.ViewInfo localViewInfo = localFieldGraph.getViewInfo();
        int i = localViewInfo.getFillStyle() == 2 ? 1 : 0;
        if ((i != 0) && (localViewInfo.getMVerSpace() > 0L))
          localIGoatFieldEmitter.emitFlowVertSpaceMarkup(paramNode, this.hw, localViewInfo.getMVerSpace());
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.HTMLFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */