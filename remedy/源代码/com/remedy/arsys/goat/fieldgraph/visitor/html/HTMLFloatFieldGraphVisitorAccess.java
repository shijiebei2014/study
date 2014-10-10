package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.service.IVisitorAccess;

public class HTMLFloatFieldGraphVisitorAccess
  implements IVisitorAccess
{
  boolean mApplyChildren = false;

  public boolean canAccessAfterChildrenVisitor(FieldGraph.Node paramNode)
  {
    if (paramNode.mField == null)
      return true;
    if (paramNode.mField.getMFloat() > 1)
    {
      this.mApplyChildren = false;
      return true;
    }
    return this.mApplyChildren;
  }

  public boolean canAccessBeforeChildrenVisitor(FieldGraph.Node paramNode)
  {
    if (paramNode.mField == null)
      return true;
    if ((this.mApplyChildren) || ((paramNode.mField != null) && (paramNode.mField.getMFloat() > 1)))
    {
      this.mApplyChildren = true;
      return true;
    }
    return false;
  }

  public boolean canAccessTreeTraversal(FieldGraph.Node paramNode)
  {
    if (paramNode.mField == null)
      return true;
    return this.mApplyChildren;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.HTMLFloatFieldGraphVisitorAccess
 * JD-Core Version:    0.6.1
 */