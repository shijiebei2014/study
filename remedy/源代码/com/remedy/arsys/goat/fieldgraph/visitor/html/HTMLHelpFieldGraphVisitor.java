package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.share.HTMLWriter;

public class HTMLHelpFieldGraphVisitor extends BaseHTMLFieldGraphVisitor
{
  public HTMLHelpFieldGraphVisitor(HTMLWriter paramHTMLWriter, IEmitterFactory paramIEmitterFactory)
  {
    super(paramHTMLWriter, paramIEmitterFactory);
  }

  public void visitBeforeChildren(FieldGraph.Node paramNode)
  {
    if ((paramNode.mParent != null) && (paramNode.mState == 1))
    {
      IGoatFieldEmitter localIGoatFieldEmitter = this.emitterFactory.getEmitter(paramNode.mField);
      localIGoatFieldEmitter.emitOpenHelp(this.hw);
    }
  }

  public void visitAfterChildren(FieldGraph.Node paramNode)
  {
    if ((paramNode.mParent != null) && (paramNode.mState == 1))
    {
      IGoatFieldEmitter localIGoatFieldEmitter = this.emitterFactory.getEmitter(paramNode.mField);
      localIGoatFieldEmitter.emitCloseHelp(this.hw);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.HTMLHelpFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */