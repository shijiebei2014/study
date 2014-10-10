package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.service.IVisitorAccess;

public class HTMLNonFloatFieldGraphVisitorAccess
  implements IVisitorAccess
{
  public boolean canAccessAfterChildrenVisitor(FieldGraph.Node paramNode)
  {
    return (paramNode.mField == null) || (paramNode.mField.getMFloat() <= 1);
  }

  public boolean canAccessBeforeChildrenVisitor(FieldGraph.Node paramNode)
  {
    return (paramNode.mField == null) || (paramNode.mField.getMFloat() <= 1);
  }

  public boolean canAccessTreeTraversal(FieldGraph.Node paramNode)
  {
    return (paramNode.mField == null) || (paramNode.mField.getMFloat() <= 1);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.HTMLNonFloatFieldGraphVisitorAccess
 * JD-Core Version:    0.6.1
 */