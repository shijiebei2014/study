package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;

public abstract interface IFieldGraphService
{
  public abstract FieldGraph get(Form.ViewInfo paramViewInfo)
    throws GoatException;

  public abstract FieldGraph get(String paramString1, String paramString2, String paramString3)
    throws GoatException;

  public abstract FieldGraph get(String paramString1, String paramString2, int paramInt)
    throws GoatException;

  public abstract void instantiateFields(FieldGraph paramFieldGraph)
    throws GoatException;

  public abstract void instantiateFieldsOnly(FieldGraph paramFieldGraph)
    throws GoatException;

  public abstract void traverseDepthFirst(FieldGraph paramFieldGraph, IFieldGraphVisitor paramIFieldGraphVisitor, IVisitorAccess paramIVisitorAccess)
    throws GoatException;

  public abstract void traverseDepthFirst(FieldGraph paramFieldGraph, FieldGraph.Node paramNode, IFieldGraphVisitor paramIFieldGraphVisitor, IVisitorAccess paramIVisitorAccess)
    throws GoatException;

  public abstract void traverseDepthFirst_takeTwo(FieldGraph paramFieldGraph, IFieldGraphVisitor paramIFieldGraphVisitor1, IVisitorAccess paramIVisitorAccess1, IFieldGraphVisitor paramIFieldGraphVisitor2, IVisitorAccess paramIVisitorAccess2)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IFieldGraphService
 * JD-Core Version:    0.6.1
 */