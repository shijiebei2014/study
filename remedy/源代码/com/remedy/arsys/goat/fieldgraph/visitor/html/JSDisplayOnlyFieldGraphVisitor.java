package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;

public class JSDisplayOnlyFieldGraphVisitor extends BaseJSFieldGraphVisitor
{
  public JSDisplayOnlyFieldGraphVisitor(JSWriter paramJSWriter, IEmitterFactory paramIEmitterFactory)
  {
    super(paramJSWriter, paramIEmitterFactory);
  }

  public void visitBeforeChildren(FieldGraph.Node paramNode)
  {
    if ((paramNode.mParent != null) && (paramNode.getEmitMode() != 2) && (paramNode.mField.getMFieldOption() == 4))
      this.jw.property("" + paramNode.mField.getMFieldID());
  }

  public void visitAfterChildren(FieldGraph.Node paramNode)
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.JSDisplayOnlyFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */