package com.remedy.arsys.goat.action;

import java.io.Serializable;

public abstract interface SimplifyState extends Serializable
{
  public abstract FieldQuery crossReferenceFieldQuery(FieldQuery paramFieldQuery);

  public abstract SQLQuery crossReferenceSQLQuery(SQLQuery paramSQLQuery);
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.SimplifyState
 * JD-Core Version:    0.6.1
 */