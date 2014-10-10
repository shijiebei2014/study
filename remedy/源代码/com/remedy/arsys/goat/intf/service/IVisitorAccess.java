package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.field.FieldGraph.Node;

public abstract interface IVisitorAccess
{
  public abstract boolean canAccessBeforeChildrenVisitor(FieldGraph.Node paramNode);

  public abstract boolean canAccessAfterChildrenVisitor(FieldGraph.Node paramNode);

  public abstract boolean canAccessTreeTraversal(FieldGraph.Node paramNode);
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IVisitorAccess
 * JD-Core Version:    0.6.1
 */