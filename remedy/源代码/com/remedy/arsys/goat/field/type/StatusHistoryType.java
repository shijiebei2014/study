package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.stubs.ServerLogin;

public class StatusHistoryType extends CharType
{
  public StatusHistoryType(Value paramValue, int paramInt)
    throws GoatException
  {
    super(paramValue, paramInt);
  }

  public StatusHistoryType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    this(paramValue, paramInt);
  }

  public StatusHistoryType(String paramString, int paramInt)
  {
    super(paramString, paramInt);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.StatusHistoryType
 * JD-Core Version:    0.6.1
 */