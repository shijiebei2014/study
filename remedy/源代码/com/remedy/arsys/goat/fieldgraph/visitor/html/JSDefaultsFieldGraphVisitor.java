package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import com.remedy.arsys.share.JSWriter;

public class JSDefaultsFieldGraphVisitor extends BaseJSFieldGraphVisitor
{
  private boolean first = true;

  public JSDefaultsFieldGraphVisitor(JSWriter paramJSWriter, IEmitterFactory paramIEmitterFactory)
  {
    super(paramJSWriter, paramIEmitterFactory);
  }

  public void visitBeforeChildren(FieldGraph.Node paramNode)
  {
    if ((paramNode.mParent != null) && (paramNode.getEmitMode() != 2))
    {
      if (!this.first)
        this.jw.append(",");
      this.jw.append("" + paramNode.mField.getMFieldID());
      this.jw.append(":");
      this.jw.openObj();
      IGoatFieldEmitter localIGoatFieldEmitter = this.emitterFactory.getEmitter(paramNode.mField);
      localIGoatFieldEmitter.emitDefaults(paramNode, this.jw);
      this.jw.closeObj();
      this.first = false;
    }
  }

  public void visitAfterChildren(FieldGraph.Node paramNode)
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.JSDefaultsFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */