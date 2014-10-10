package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSDragDropFieldGraphVisitor extends BaseJSFieldGraphVisitor
{
  private List<Integer> draggableFieldIDList = new ArrayList();
  private List<Integer> droppableFieldIDList = new ArrayList();

  public JSDragDropFieldGraphVisitor(JSWriter paramJSWriter, IEmitterFactory paramIEmitterFactory)
  {
    super(paramJSWriter, paramIEmitterFactory);
  }

  public void finish()
  {
    this.jw.append("this.dragFields=[");
    emitJSArray(this.draggableFieldIDList);
    this.jw.append("];");
    this.jw.append("this.dropFields=[");
    emitJSArray(this.droppableFieldIDList);
    this.jw.append("];");
  }

  private void emitJSArray(List<?> paramList)
  {
    int i = 1;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (i == 0)
        this.jw.append(",");
      else
        i = 0;
      this.jw.append(localObject.toString());
    }
  }

  public void visitBeforeChildren(FieldGraph.Node paramNode)
  {
    if ((paramNode.mParent != null) && (paramNode.getEmitMode() == 0))
    {
      GoatField localGoatField = paramNode.mField;
      boolean bool1 = localGoatField.isMDraggable();
      boolean bool2 = localGoatField.isMDroppable();
      if (bool1)
        this.draggableFieldIDList.add(Integer.valueOf(localGoatField.getMFieldID()));
      if (bool2)
        this.droppableFieldIDList.add(Integer.valueOf(localGoatField.getMFieldID()));
    }
  }

  public void visitAfterChildren(FieldGraph.Node paramNode)
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.JSDragDropFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */