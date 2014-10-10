package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.DataType;
import com.remedy.arsys.goat.ActiveLinkCollector;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.log.Log;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Action
  implements FormAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -442475259448514092L;
  protected static final transient Log mLog;
  private static Map mNonOptimizedFieldIds = Collections.unmodifiableMap(localHashMap);

  public abstract boolean canTerminate();

  public abstract boolean hasGoto();

  public abstract boolean isInterruptible();

  public abstract String toString();

  public abstract void emitJS(Emitter paramEmitter)
    throws GoatException;

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
  }

  public void simplify()
    throws GoatException
  {
  }

  public void guideMark(ActiveLinkCollector paramActiveLinkCollector, String paramString)
  {
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }

  protected void logAction(String paramString)
  {
    mLog.fine(getClass().getName() + ": " + paramString);
  }

  public static void log(String paramString)
  {
    mLog.warning(paramString);
  }

  protected boolean isNonOptimizedField(int paramInt)
  {
    return mNonOptimizedFieldIds.containsKey(Integer.valueOf(paramInt));
  }

  protected DataType getNonOptimizedFieldDataType(int paramInt)
  {
    assert (mNonOptimizedFieldIds.containsKey(Integer.valueOf(paramInt)));
    return (DataType)mNonOptimizedFieldIds.get(Integer.valueOf(paramInt));
  }

  static
  {
    mLog = Log.get(7);
    HashMap localHashMap = new HashMap();
    localHashMap.put(Integer.valueOf(1005), DataType.CHAR);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.Action
 * JD-Core Version:    0.6.1
 */