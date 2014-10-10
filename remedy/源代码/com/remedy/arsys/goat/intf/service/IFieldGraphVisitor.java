package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;

public abstract interface IFieldGraphVisitor
{
  public abstract void visitBeforeChildren(FieldGraph.Node paramNode)
    throws GoatException;

  public abstract void visitAfterChildren(FieldGraph.Node paramNode);
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */