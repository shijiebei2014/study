package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunProcessCommandInfo extends RunProcessCommands
  implements FormAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -6696142983698056588L;
  private static final Pattern INITIAL_RP_PAT = Pattern.compile("\\s*(@[^:]+:\\s*)?((\\$[^$]*(\\$)?)|([^$][^\\s]*))\\s*(.*)$");
  private static final Pattern RP_ACTIVE_LINK_ACTION_PAT = Pattern.compile("\\s*(PERFORM-ACTION-ACTIVE-LINK)\\s*([0-9]+)\\s*((\\$[^$]*\\$)|([0-9]+))");
  private static final String PERFORM_ACTIVE_LINK_ACTION_CMD = "PERFORM-ACTION-ACTIVE-LINK";
  private static transient Log MPerformanceLog = Log.get(8);
  private int commandType;
  private String cmdName;
  private boolean isDynamic;
  private boolean needGroupMapping;
  private ARCommandString cmdString;
  private int rpFieldID;

  public RunProcessCommandInfo(String paramString)
  {
    Matcher localMatcher = INITIAL_RP_PAT.matcher(paramString);
    this.cmdString = null;
    this.needGroupMapping = false;
    if (localMatcher.matches())
    {
      this.cmdName = localMatcher.group(2);
      int i = localMatcher.group(1) != null ? 1 : 0;
      if (i != 0)
      {
        this.commandType = 3;
        this.isDynamic = false;
      }
      else
      {
        if (this.cmdName != null)
          this.cmdName = this.cmdName.trim().toUpperCase();
        this.commandType = getRunProcessType(this.cmdName);
        this.isDynamic = ((localMatcher.group(3) != null) && (localMatcher.group(4) != null));
        if ((this.cmdName != null) && (this.cmdName.equals("PERFORM-ACTION-ACTIVE-LINK")))
        {
          localMatcher = RP_ACTIVE_LINK_ACTION_PAT.matcher(paramString);
          if (localMatcher.matches())
          {
            this.isDynamic = ((localMatcher.group(3) != null) && (localMatcher.group(4) != null));
            if (!this.isDynamic)
            {
              int j = (int)Long.parseLong(localMatcher.group(3));
              if (j > 0)
                this.rpFieldID = j;
            }
          }
        }
        else if ((this.cmdName != null) && (this.cmdName.equals("PERFORM-ACTION-MAP-GROUPIDS-TO-NAMES")))
        {
          this.needGroupMapping = true;
        }
      }
      if (i != 0)
        this.cmdString = new ARCommandString(paramString);
    }
    else
    {
      this.commandType = 7;
    }
  }

  public final boolean isInterruptible()
  {
    return (this.commandType & 0x4) != 0;
  }

  public final boolean isRunProcess()
  {
    return (this.commandType & 0x1) != 0;
  }

  public final boolean isDollarProcess()
  {
    return (this.commandType & 0x2) != 0;
  }

  public final boolean isServerSideCommand()
  {
    return this.cmdString != null;
  }

  public final boolean isDynamicCommand()
  {
    return this.isDynamic;
  }

  public String getKeywordReferencesAsJSArrayString()
  {
    return this.cmdString == null ? "[]" : this.cmdString.getKeywordReferencesAsJSArrayString();
  }

  public void emitKeywordReferencesAsJSArray(JSWriter paramJSWriter)
  {
    if (this.cmdString == null)
      paramJSWriter.openList().closeList();
    else
      this.cmdString.emitKeywordReferencesAsJSArray(paramJSWriter);
  }

  public void emitFieldReferencesAsJSArray(JSWriter paramJSWriter)
  {
    if (this.cmdString == null)
      paramJSWriter.openList().closeList();
    else
      this.cmdString.emitFieldReferencesAsJSArray(paramJSWriter);
  }

  public String getFieldReferencesAsJSArrayString()
  {
    return this.cmdString == null ? "[]" : this.cmdString.getFieldReferencesAsJSArrayString();
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    if (isDynamicCommand())
    {
      MPerformanceLog.fine("Dynamic run-process action detected - optimizations will be impacted");
      paramOutputNotes.setPerformActionDynamic();
    }
    else
    {
      paramOutputNotes.addRunProcessField(this.rpFieldID);
    }
    if (this.needGroupMapping)
      paramOutputNotes.addGroupMenuMapping("GLMENU");
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    if (this.cmdString != null)
      this.cmdString.bindToForm(paramCachedFieldMap);
  }

  public Object clone()
  {
    try
    {
      RunProcessCommandInfo localRunProcessCommandInfo = (RunProcessCommandInfo)super.clone();
      if (localRunProcessCommandInfo.cmdString != null)
        localRunProcessCommandInfo.cmdString = ((ARCommandString)this.cmdString.clone());
      return localRunProcessCommandInfo;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.RunProcessCommandInfo
 * JD-Core Version:    0.6.1
 */