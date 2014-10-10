package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.ActiveLinkAction;
import com.bmc.arsys.api.CallGuideAction;
import com.bmc.arsys.api.ChangeFieldAction;
import com.bmc.arsys.api.CloseWindowAction;
import com.bmc.arsys.api.CommitChangesAction;
import com.bmc.arsys.api.DirectSqlAction;
import com.bmc.arsys.api.ExitGuideAction;
import com.bmc.arsys.api.GotoAction;
import com.bmc.arsys.api.GotoGuideLabelAction;
import com.bmc.arsys.api.MessageAction;
import com.bmc.arsys.api.OpenWindowAction;
import com.bmc.arsys.api.PushFieldsAction;
import com.bmc.arsys.api.RunProcessAction;
import com.bmc.arsys.api.ServiceAction;
import com.bmc.arsys.api.SetFieldsAction;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.ActiveLinkCollector;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionList
  implements FormAware, Cloneable, Serializable
{
  private static final long serialVersionUID = 6916300621276383385L;
  private static final int TRUE_ACTION_START_IDX = 0;
  private static final int FALSE_ACTION_START_IDX = 1000;
  private static final Pattern MDynamicPattern = Pattern.compile("\\$([0-9])+\\$");
  private List mActions = new ArrayList();
  private HashSet mGuideNames;

  public Object clone()
    throws CloneNotSupportedException
  {
    ActionList localActionList = (ActionList)super.clone();
    localActionList.mActions = new ArrayList(this.mActions.size());
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
      localActionList.mActions.add(((Action)localIterator.next()).clone());
    return localActionList;
  }

  public ActionList cloneForEPStartAL(String paramString1, String paramString2)
    throws CloneNotSupportedException
  {
    ActionList localActionList = (ActionList)super.clone();
    localActionList.mActions = new ArrayList();
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
    {
      Action localAction = (Action)localIterator.next();
      if ((localAction instanceof ActionOpenDialog))
        localActionList.mActions.add(((ActionOpenDialog)localAction).cloneForEPStartAL(paramString1, paramString2));
      else
        localActionList.mActions.add(localAction);
    }
    return localActionList;
  }

  public void guideMark(ActiveLinkCollector paramActiveLinkCollector, String paramString)
  {
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
      ((Action)localIterator.next()).guideMark(paramActiveLinkCollector, paramString);
  }

  public ActionList(ActiveLink paramActiveLink, List<ActiveLinkAction> paramList, boolean paramBoolean)
  {
    int i = paramBoolean ? 0 : 1000;
    if (paramList != null)
    {
      int j = 0;
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        ActiveLinkAction localActiveLinkAction = (ActiveLinkAction)localIterator.next();
        try
        {
          compileAction(paramActiveLink, localActiveLinkAction, j + i);
        }
        catch (GoatException localGoatException)
        {
          Action.log("Caught exception while building action list (action " + j + ") - dropping action : " + localGoatException.toString());
        }
        j++;
      }
    }
  }

  public void compileAction(ActiveLink paramActiveLink, ActiveLinkAction paramActiveLinkAction, int paramInt)
    throws GoatException
  {
    if ((paramActiveLinkAction instanceof CloseWindowAction))
    {
      this.mActions.add(new ActionCloseWindow(paramActiveLink, (CloseWindowAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof OpenWindowAction))
    {
      this.mActions.add(new ActionOpenDialog(paramActiveLink, (OpenWindowAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof GotoAction))
    {
      this.mActions.add(new ActionGoto(paramActiveLink, (GotoAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof MessageAction))
    {
      this.mActions.add(new ActionMessage(paramActiveLink, (MessageAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof SetFieldsAction))
    {
      this.mActions.add(new ActionSetFields(paramActiveLink, (SetFieldsAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof PushFieldsAction))
    {
      this.mActions.add(new ActionPushFields(paramActiveLink, (PushFieldsAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof ChangeFieldAction))
    {
      this.mActions.add(new ActionChangeFields(paramActiveLink, (ChangeFieldAction)paramActiveLinkAction, paramInt));
    }
    else if ((paramActiveLinkAction instanceof CommitChangesAction))
    {
      this.mActions.add(new ActionCommit(paramActiveLink, paramInt));
    }
    else
    {
      Object localObject;
      if ((paramActiveLinkAction instanceof RunProcessAction))
      {
        localObject = (RunProcessAction)paramActiveLinkAction;
        if (((RunProcessAction)localObject).getCommandLine().startsWith("PERFORM-ACTION-APPLY"))
          this.mActions.add(new ActionCommit(paramActiveLink, paramInt));
        else
          this.mActions.add(new ActionProcess(paramActiveLink, ((RunProcessAction)localObject).getCommandLine(), paramInt));
      }
      else if ((paramActiveLinkAction instanceof GotoGuideLabelAction))
      {
        this.mActions.add(new ActionGotoGuide(paramActiveLink, (GotoGuideLabelAction)paramActiveLinkAction, paramInt));
      }
      else if ((paramActiveLinkAction instanceof ExitGuideAction))
      {
        this.mActions.add(new ActionExitGuide(paramActiveLink, (ExitGuideAction)paramActiveLinkAction, paramInt));
      }
      else if ((paramActiveLinkAction instanceof CallGuideAction))
      {
        this.mActions.add(new ActionCallGuide(paramActiveLink, (CallGuideAction)paramActiveLinkAction, paramInt));
        if (this.mGuideNames == null)
          this.mGuideNames = new HashSet();
        localObject = ((CallGuideAction)paramActiveLinkAction).getGuideName();
        Matcher localMatcher = MDynamicPattern.matcher((CharSequence)localObject);
        if (!localMatcher.matches())
          this.mGuideNames.add(localObject);
      }
      else if ((paramActiveLinkAction instanceof DirectSqlAction))
      {
        this.mActions.add(new ActionSQL(paramActiveLink, (DirectSqlAction)paramActiveLinkAction, paramInt));
      }
      else if ((paramActiveLinkAction instanceof ServiceAction))
      {
        this.mActions.add(new ActionService(paramActiveLink, (ServiceAction)paramActiveLinkAction, paramInt));
      }
      else
      {
        Action.log("Unsupported link action " + paramActiveLinkAction.getClass());
      }
    }
  }

  public boolean isEmpty()
  {
    return this.mActions.size() == 0;
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
  {
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
      try
      {
        ((Action)localIterator.next()).bindToForm(paramCachedFieldMap);
      }
      catch (GoatException localGoatException)
      {
        if (localGoatException.amInternalError())
          Action.log("Caught exception while binding action list - dropping action : " + localGoatException.getMessage());
        else
          Action.log("Caught exception while binding action list - dropping action : " + localGoatException.toString());
        localIterator.remove();
      }
  }

  public void simplify()
  {
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
      try
      {
        ((Action)localIterator.next()).simplify();
      }
      catch (GoatException localGoatException)
      {
        Action.log("Caught exception while simplifying action list - dropping action : " + localGoatException.toString());
        localIterator.remove();
      }
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < this.mActions.size(); i++)
      localStringBuilder.append(((Action)this.mActions.get(i)).toString()).append(";");
    return localStringBuilder.toString();
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.startInterruptibleBlock();
    for (int i = 0; i < this.mActions.size(); i++)
    {
      Action localAction1 = (Action)this.mActions.get(i);
      localJSWriter.comment("flags = " + (localAction1.hasGoto() ? "G" : " ") + (localAction1.canTerminate() ? "T" : " ") + (localAction1.isInterruptible() ? "I" : " "));
      if (FormContext.get().getWorkflowLogging())
        localJSWriter.callFunction(false, "LogActionNum", new Object[] { "windowID", "\"action " + i + "\"" });
      int j = i;
      int k = 0;
      if ((tableRefreshBulkable(localAction1)) && (j + 1 < this.mActions.size()))
      {
        int m = 0;
        Action localAction2 = (Action)this.mActions.get(++j);
        ArrayList localArrayList = new ArrayList();
        while (tableRefreshBulkable(localAction2))
        {
          if (m == 0)
          {
            localActionChangeFields = (ActionChangeFields)localAction1;
            localArrayList.add("windowID");
            localArrayList.add("" + localActionChangeFields.getFieldIdInt());
            localActionChangeFields.setBulkTableRefresh(true);
          }
          m = 1;
          ActionChangeFields localActionChangeFields = (ActionChangeFields)localAction2;
          localArrayList.add("" + localActionChangeFields.getFieldIdInt());
          localActionChangeFields.setBulkTableRefresh(true);
          if (j + 1 < this.mActions.size())
            localAction2 = (Action)this.mActions.get(++j);
          else
            k = 1;
        }
        if (m != 0)
          localJSWriter.callFunction(true, "ARACTBulkTableRefresh", localArrayList.toArray());
      }
      localAction1.emitJS(paramEmitter);
    }
    localJSWriter.endInterruptibleBlock();
  }

  public boolean hasGoto()
  {
    boolean bool = false;
    for (int i = 0; i < this.mActions.size(); i++)
      bool = (bool) || (((Action)this.mActions.get(i)).hasGoto());
    return bool;
  }

  public boolean canTerminate()
  {
    boolean bool = false;
    for (int i = 0; i < this.mActions.size(); i++)
      bool = (bool) || (((Action)this.mActions.get(i)).canTerminate());
    return bool;
  }

  public boolean isInterruptible()
  {
    boolean bool = false;
    for (int i = 0; i < this.mActions.size(); i++)
      bool = (bool) || (((Action)this.mActions.get(i)).isInterruptible());
    return bool;
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes)
  {
    for (int i = 0; i < this.mActions.size(); i++)
      ((Action)this.mActions.get(i)).addToOutputNotes(paramOutputNotes);
  }

  public HashSet getGuideNames()
  {
    return this.mGuideNames;
  }

  private boolean tableRefreshBulkable(Action paramAction)
  {
    return ((paramAction instanceof ActionChangeFields)) && (((ActionChangeFields)paramAction).isTableRefresh()) && (((ActionChangeFields)paramAction).getFieldIdInt() != 706);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ActionList
 * JD-Core Version:    0.6.1
 */