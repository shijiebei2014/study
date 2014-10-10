package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.share.JSWriter;

public class JSFieldGraphVisitor extends BaseJSFieldGraphVisitor
{
  public JSFieldGraphVisitor(JSWriter paramJSWriter, IEmitterFactory paramIEmitterFactory)
  {
    super(paramJSWriter, paramIEmitterFactory);
  }

  public void visitBeforeChildren(FieldGraph.Node paramNode)
  {
    if (paramNode.mParent != null)
    {
      int i = paramNode.getEmitMode();
      if (i == 1)
      {
        IGoatFieldEmitter localIGoatFieldEmitter = this.emitterFactory.getEmitter(paramNode.mField);
        localIGoatFieldEmitter.emitScript(paramNode, this.jw);
      }
    }
  }

  public void visitAfterChildren(FieldGraph.Node paramNode)
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.JSFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */