package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.intf.service.IVisitorAccess;

public class DummyFieldGraphVistorAccess
  implements IVisitorAccess
{
  public boolean canAccessAfterChildrenVisitor(FieldGraph.Node paramNode)
  {
    return true;
  }

  public boolean canAccessBeforeChildrenVisitor(FieldGraph.Node paramNode)
  {
    return true;
  }

  public boolean canAccessTreeTraversal(FieldGraph.Node paramNode)
  {
    return true;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.DummyFieldGraphVistorAccess
 * JD-Core Version:    0.6.1
 */