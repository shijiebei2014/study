package com.remedy.arsys.goat.action;

import java.util.HashMap;
import java.util.Map;

public class RunProcessCommands
{
  protected static final int NONE = 0;
  protected static final int RUN_PROCESS = 1;
  protected static final int DOLLAR_PROCESS = 2;
  protected static final int INTERRUPTIBLE = 4;
  private static final Map mRunProcessHash = new HashMap();

  protected static final int getRunProcessType(String paramString)
  {
    Integer localInteger = (Integer)mRunProcessHash.get(paramString);
    if (localInteger != null)
      return localInteger.intValue();
    return 7;
  }

  static
  {
    mRunProcessHash.put("GET-CHANGE-FLAG", new Integer(2));
    mRunProcessHash.put("JAVASCRIPT", new Integer(7));
    mRunProcessHash.put("PERFORM-ACTION-ACTIVE-LINK", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-ADD-ATTACHMENT", new Integer(7));
    mRunProcessHash.put("PERFORM-ACTION-APPLY", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-DELETE-ATTACHMENT", new Integer(3));
    mRunProcessHash.put("PERFORM-ACTION-EXIT-APP", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-GET-FIELD-LABEL", new Integer(6));
    mRunProcessHash.put("PERFORM-ACTION-GET-PREFERENCE", new Integer(2));
    mRunProcessHash.put("PERFORM-ACTION-GO-HOME", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-HOME-FIELD-REFRESH", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-NEXT", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-OPEN-ATTACHMENT", new Integer(3));
    mRunProcessHash.put("PERFORM-ACTION-OPEN-URL", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-PREV", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-REFRESH-PREFERENCE", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-SAVE-ATTACHMENT", new Integer(3));
    mRunProcessHash.put("PERFORM-ACTION-SEND-EVENT", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-SET-PREFERENCE", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-SET-SCREEN-PREFERENCE", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-CLEAR", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-CLEAR-ROWCHANGED", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-DESELECTALL", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-NEXT-CHUNK", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-PREV-CHUNK", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-REFRESH", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-REPORT", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-SELECTALL", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-GET-SELECTED-COLUMN", new Integer(2));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-SELECT-NODE", new Integer(7));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-IS-LEAF-SELECTED", new Integer(6));
    mRunProcessHash.put("SET-CHANGE-FLAG", new Integer(1));
    mRunProcessHash.put("SET-RO-COLOR", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-NAV-FIELD-SET-SELECTED-ITEM", new Integer(1));
    mRunProcessHash.put("APPLICATION-COPY-FIELD-VALUE", new Integer(2));
    mRunProcessHash.put("PERFORM-ACTION-MAP-GROUPIDS-TO-NAMES", new Integer(6));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-SET-ROWSTATE", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-ADD-ROW", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-DELETE-ROW", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-LIST-APPEND", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-MAP-PUT", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-CLEAR-DELETED", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-CHANGE-ROW-COL-VISIBILITY", new Integer(1));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-GET-CURRENT-CHUNK", new Integer(2));
    mRunProcessHash.put("PERFORM-ACTION-GET-ENTRY", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-VALIDATE-NULL-REQUIRED-FIELDS", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-CLEAR-PROMPTBAR", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-CHANGE-MODE", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-SHOW-TOOLBAR", new Integer(5));
    mRunProcessHash.put("PERFORM-ACTION-TABLE-GET-CHUNKSIZE", new Integer(6));
    mRunProcessHash.put("PERFORM-ACTION-SET-QUERY-WIDGET", new Integer(3));
    mRunProcessHash.put("ENABLE-WAIT-CURSOR-ON-LONG-RUNNING-PROCESS", new Integer(1));
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.RunProcessCommands
 * JD-Core Version:    0.6.1
 */