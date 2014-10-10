package com.remedy.arsys.goat;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.action.ActionList;
import com.remedy.arsys.share.JSWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class ActiveLinkCollection
  implements Serializable
{
  private static final long serialVersionUID = -6188863605514532181L;
  protected transient Collection mActiveLinks;
  protected String mName;
  public boolean mMarked;
  private static boolean mWorkflowProfiling = Configuration.getInstance().getWorkflowProfiling();
  private static boolean mWorkflowVerifyImode = Configuration.getInstance().getWorkflowVerifyImode();
  protected String mServer;
  protected String mForm;
  protected String mView;
  protected int mAREvtID;
  protected int mEvtID;

  public void guideMark(ActiveLinkCollector paramActiveLinkCollector, String paramString)
  {
    if (!this.mMarked)
    {
      System.out.println("Marking guide " + this.mName);
      this.mMarked = true;
      Iterator localIterator = this.mActiveLinks.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if ((localObject instanceof ActiveLink))
        {
          ActiveLink localActiveLink = (ActiveLink)localObject;
          localActiveLink.getTrueActions().guideMark(paramActiveLinkCollector, paramString);
          localActiveLink.getFalseActions().guideMark(paramActiveLinkCollector, paramString);
        }
      }
    }
    else
    {
      System.out.println("Guide " + this.mName + " already marked");
    }
  }

  protected ActiveLinkCollection(String paramString)
  {
    this.mName = paramString;
    this.mMarked = false;
    constructCollection();
  }

  protected abstract void constructCollection();

  public void add(ActiveLinkInfo paramActiveLinkInfo)
  {
    this.mActiveLinks.add(paramActiveLinkInfo);
  }

  public String getName()
  {
    return this.mName;
  }

  public abstract String getJSFunctionName();

  protected void emitDebugStatements(JSWriter paramJSWriter)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.mActiveLinks.iterator();
    while (localIterator.hasNext())
    {
      ActiveLinkInfo localActiveLinkInfo = (ActiveLinkInfo)localIterator.next();
      localStringBuilder.append((localActiveLinkInfo instanceof ActiveLink) ? "A" : "L");
      localStringBuilder.append(localActiveLinkInfo.hasGoto() ? "G" : " ");
      localStringBuilder.append(localActiveLinkInfo.canTerminate() ? "T" : " ");
      localStringBuilder.append(localActiveLinkInfo.isInterruptible() ? "I" : " ");
    }
    paramJSWriter.comment(localStringBuilder.toString());
  }

  protected void emitFunctionBlock(JSWriter paramJSWriter, boolean paramBoolean)
  {
    String str = getRunFunction(paramBoolean);
    if (str == null)
      return;
    int i = -1;
    int j = -1;
    long l = -1L;
    if ((this instanceof EventALCollection))
    {
      i = this.mEvtID;
      j = this.mAREvtID;
      Object localObject1 = this.mActiveLinks.iterator();
      Object localObject2;
      Object localObject3;
      if (((Iterator)localObject1).hasNext())
      {
        localObject2 = ((Iterator)localObject1).next();
        if ((localObject2 instanceof ActiveLink))
        {
          localObject3 = (ActiveLink)localObject2;
          ExecutionEvent localExecutionEvent = ((ActiveLink)localObject3).getEvent();
          l = localExecutionEvent.getEvtParm(this.mAREvtID);
        }
      }
    }
    paramJSWriter.startStatement("return " + str + "(this, " + "windowID" + ",");
    if ((this instanceof EventALCollection))
      paramJSWriter.continueStatement(j + "," + l + ",");
    else
      paramJSWriter.continueStatement("-1,-1,");
    localObject1 = new StringBuffer();
    paramJSWriter.continueStatement(" [");
    localObject2 = this.mActiveLinks.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (ActiveLinkInfo)((Iterator)localObject2).next();
      if (paramBoolean)
      {
        if ((localObject3 instanceof ActiveLink))
        {
          paramJSWriter.append(((ActiveLinkInfo)localObject3).getExecutionOrder()).comma().append("this." + ((ActiveLinkInfo)localObject3).getJSFunctionName());
          ((StringBuffer)localObject1).append(JSWriter.escapeString(((ActiveLink)localObject3).getName()));
        }
        else
        {
          paramJSWriter.appendqs(((ActiveLinkInfo)localObject3).getJSFunctionName());
        }
      }
      else
      {
        paramJSWriter.continueStatement("this." + ((ActiveLinkInfo)localObject3).getJSFunctionName());
        if ((localObject3 instanceof ActiveLink))
          ((StringBuffer)localObject1).append(JSWriter.escapeString(((ActiveLink)localObject3).getName()));
      }
      if (((Iterator)localObject2).hasNext())
      {
        paramJSWriter.comma();
        if ((localObject3 instanceof ActiveLink))
          ((StringBuffer)localObject1).append(',');
      }
    }
    paramJSWriter.continueStatement("],");
    paramJSWriter.continueStatement(" [");
    paramJSWriter.append(((StringBuffer)localObject1).toString());
    paramJSWriter.continueStatement("])");
    paramJSWriter.endStatement();
  }

  protected abstract String getRunFunction(boolean paramBoolean);

  protected void emitInterruptibleBlock(JSWriter paramJSWriter)
    throws GoatException
  {
    paramJSWriter.startInterruptibleBlock();
    int i = -1;
    int j = -1;
    long l = -1L;
    if ((this instanceof EventALCollection))
    {
      i = this.mEvtID;
      j = this.mAREvtID;
      Iterator localIterator = this.mActiveLinks.iterator();
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (localIterator.hasNext())
      {
        localObject1 = localIterator.next();
        if ((localObject1 instanceof ActiveLink))
        {
          localObject2 = (ActiveLink)localObject1;
          localObject3 = ((ActiveLink)localObject2).getEvent();
          l = ((ExecutionEvent)localObject3).getEvtParm(this.mAREvtID);
        }
      }
    }
    if (FormContext.get().getWorkflowLogging())
      paramJSWriter.callFunction(false, "LogEvent", new Object[] { "windowID", Integer.valueOf(j), Long.valueOf(l) });
    localIterator = this.mActiveLinks.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (ActiveLinkInfo)localIterator.next();
      localObject2 = JSWriter.escapeString(((ActiveLink)localObject1).getName());
      if (((String)localObject2).startsWith("\""))
        localObject2 = ((String)localObject2).substring(1, ((String)localObject2).length());
      if (((String)localObject2).endsWith("\""))
        localObject2 = ((String)localObject2).substring(0, ((String)localObject2).length() - 1);
      localObject3 = "\"ActiveLink End:- " + (String)localObject2 + "\"";
      paramJSWriter.callFunction(((ActiveLinkInfo)localObject1).isInterruptible(), "this", ((ActiveLinkInfo)localObject1).getJSFunctionName(), new Object[] { "windowID" });
      paramJSWriter.callFunction(false, "ActiveLinkLogWriteEnd", new Object[] { "windowID", localObject3 });
    }
    if (FormContext.get().getWorkflowLogging())
      paramJSWriter.callFunction(false, "LogEndofEvent", new Object[] { "windowID", Integer.valueOf(j), Long.valueOf(l) });
    paramJSWriter.endInterruptibleBlock();
  }

  protected void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    JSWriter localJSWriter = paramEmitter.js();
    localJSWriter.startThisFunction(getJSFunctionName(), "windowID");
    if (mWorkflowProfiling)
    {
      localJSWriter.startStatement("var prof=Profile_Start(").appendqs(this.mName).continueStatement(", ").append((this instanceof GuideALCollection) ? 4 : 1).append(")").endStatement();
      localJSWriter.startStatement("try");
      localJSWriter.startBlock();
    }
    emitDebugStatements(localJSWriter);
    boolean bool = false;
    int i = 0;
    Iterator localIterator = this.mActiveLinks.iterator();
    while (localIterator.hasNext())
    {
      ActiveLinkInfo localActiveLinkInfo = (ActiveLinkInfo)localIterator.next();
      bool = (bool) || (localActiveLinkInfo.hasGoto());
      i = (i != 0) || (localActiveLinkInfo.isInterruptible()) ? 1 : 0;
    }
    if ((bool) || ((i != 0) && ((this.mActiveLinks.size() > 1) || (mWorkflowVerifyImode))))
      emitFunctionBlock(localJSWriter, bool);
    else
      emitInterruptibleBlock(localJSWriter);
    if (mWorkflowProfiling)
    {
      localJSWriter.endBlock();
      localJSWriter.startStatement("finally");
      localJSWriter.startBlock();
      localJSWriter.statement("Profile_Stop(prof)");
      localJSWriter.endBlock().endStatement();
    }
    localJSWriter.endFunction();
  }

  public void collectAllActivelinks(Map paramMap)
  {
    Iterator localIterator = this.mActiveLinks.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject instanceof ActiveLink))
      {
        ActiveLink localActiveLink = (ActiveLink)localObject;
        paramMap.put(localActiveLink.getJSFunctionName(), localActiveLink);
      }
    }
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (this.mActiveLinks == null)
    {
      paramObjectOutputStream.writeInt(0);
      return;
    }
    Object[] arrayOfObject = this.mActiveLinks.toArray();
    for (int i = 0; i < arrayOfObject.length; i++)
    {
      ActiveLink localActiveLink = (ActiveLink)arrayOfObject[i];
      paramObjectOutputStream.writeObject(localActiveLink.getName());
      paramObjectOutputStream.writeObject(localActiveLink.getServer());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.ActiveLinkCollection
 * JD-Core Version:    0.6.1
 */