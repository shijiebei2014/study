package com.remedy.arsys.prefetch;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.FormCriteria;
import com.bmc.arsys.api.GroupInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ActiveLinkCache;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.share.ObjectCreationCount;
import com.remedy.arsys.share.ObjectReleaseCount;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PrefetchWorkerImpl
  implements PrefetchWorker
{
  private static Log MPerformanceLog = Log.get(8);
  private static ThreadLocal serverLoginTL = new ThreadLocal()
  {
    protected synchronized Object initialValue()
    {
      return null;
    }
  };
  private final FormCriteria MINIMAL_SCHEMA_PROPS = new FormCriteria();
  private volatile boolean mThreadDone = false;
  private final AbstractManager mManager;
  private final boolean mIsViewBuilding;
  private boolean mLoadActiveLink = true;

  PrefetchWorkerImpl(AbstractManager paramAbstractManager, boolean paramBoolean)
  {
    this(paramAbstractManager, paramBoolean, true);
  }

  PrefetchWorkerImpl(AbstractManager paramAbstractManager, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.MINIMAL_SCHEMA_PROPS.setPropertiesToRetrieve(FormCriteria.LAST_CHANGED);
    this.mManager = paramAbstractManager;
    this.mIsViewBuilding = paramBoolean1;
    this.mLoadActiveLink = paramBoolean2;
  }

  static PrefetchWorker.Item create(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return new FetchItem(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6);
  }

  public void start(boolean paramBoolean)
  {
    prefetchForm(paramBoolean);
  }

  private void prefetchForm(boolean paramBoolean)
  {
    PrefetchWorker.Item localItem = null;
    ServerLogin localServerLogin1 = null;
    GroupInfo[] arrayOfGroupInfo = null;
    try
    {
      while ((localItem = this.mManager.getNextItem()) != null)
      {
        if (!isValidState())
          return;
        try
        {
          localServerLogin1 = ServerLogin.getAdmin(localItem.getServer());
          ServerInfo localServerInfo1 = ServerInfo.get(localServerLogin1, true);
          if (localServerInfo1.getVersionAsNumber() < 70000)
          {
            MPerformanceLog.warning(localItem.getServer() + " is pre-7.0 AR server, prefetch is ignored.");
            break;
          }
          Object localObject1;
          if (this.mIsViewBuilding)
          {
            if (localItem.getUsername() == null)
            {
              MPerformanceLog.fine("Form/View: " + localItem.getSchema() + "/" + localItem.getViewname() + " has no User to build view.");
            }
            else
            {
              arrayOfGroupInfo = localServerLogin1.getGroupInfo();
              localObject1 = (GroupInfo[])localServerLogin1.getListGroup(localItem.getUsername(), null).toArray(new GroupInfo[0]);
              localServerLogin1.setPermKey((GroupInfo[])localObject1);
              localServerLogin1.impersonateUser(localItem.getUsername());
            }
          }
          else
          {
            serverLoginTL.set(localServerLogin1);
            localObject1 = new PrefetchSessionData(localItem.getUsername() != null ? localItem.getUsername() : localServerLogin1.getUserName(), localServerLogin1, localItem.getLocale(), localItem.getTimezone());
            SessionData.set((SessionData)localObject1);
            FormContext localFormContext = new FormContext(null, "../../../../", null, Configuration.getInstance().getRootPath());
            long l1 = new Date().getTime();
            long[] arrayOfLong1 = Form.getFormTimeStamp(localItem.getServer());
            ServerLogin localServerLogin2 = getServerLogin(localItem.getServer());
            ServerInfo localServerInfo2 = ServerInfo.get(localItem.getServer(), true);
            long[] arrayOfLong2 = localServerInfo2.getCacheChangeTimes();
            if (!isValidState())
              return;
            FieldGraph.preCache(localItem, this.mIsViewBuilding, this.mLoadActiveLink, this.mManager.getSvrFormToALMap());
            long l2 = new Date().getTime();
            long l3 = l2 - l1;
            MPerformanceLog.fine("successful prefetch - " + localItem + " took " + l3 + " milliseconds.");
          }
        }
        catch (GoatException localGoatException)
        {
          this.mManager.addToNAserverList(localItem.getServer());
          logException(localGoatException, localItem);
          ViewInfoCollector.remove(localItem);
          if (paramBoolean)
            break;
        }
        catch (Exception localException)
        {
          logException(localException, localItem);
          ViewInfoCollector.remove(localItem);
          if (paramBoolean)
            break;
        }
        finally
        {
          if (localItem != null)
          {
            Integer localInteger1 = (Integer)PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.get(localItem.getServer());
            if ((localInteger1 != null) && (localInteger1.intValue() > 0))
              synchronized (PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT)
              {
                Integer localInteger2 = (Integer)PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.get(localItem.getServer());
                if (localInteger2 == null)
                  localInteger2 = new Integer(0);
                localInteger2 = Integer.valueOf(localInteger2.intValue() + 1);
                MPerformanceLog.fine("(Server, Total , Current) - " + localItem.getServer() + "," + localInteger1.intValue() + "," + localInteger2.intValue());
                if (localInteger1.intValue() == localInteger2.intValue())
                {
                  PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.put(localItem.getServer(), Integer.valueOf(0));
                  PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(localItem.getServer(), Integer.valueOf(PrefetchTask.PRELOAD_COMPLETED));
                }
                PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.put(localItem.getServer(), localInteger2);
              }
          }
          if (localServerLogin1 != null)
            try
            {
              if (arrayOfGroupInfo != null)
                localServerLogin1.setPermKey(arrayOfGroupInfo);
              localServerLogin1.impersonateUser(null);
            }
            catch (ARException localARException)
            {
              MPerformanceLog.warning("Prefetch failed to reset admin user back.");
            }
        }
      }
    }
    finally
    {
      ActiveLinkCache localActiveLinkCache = (ActiveLinkCache)GoatCacheManager.getInstance().getGoatCache("Active links");
      if (localActiveLinkCache != null)
        synchronized (localActiveLinkCache)
        {
          ActiveLinkCache.resetActiveLinkCount();
          localActiveLinkCache.save();
        }
    }
    ObjectCreationCount.print("ACTIVE_LINKS", 1);
    ObjectReleaseCount.print("ACTIVE_LINKS", 1);
    ObjectCreationCount.print("DISPLAYED_FIELDS", 8);
    ObjectReleaseCount.print("DISPLAYED_FIELDS", 8);
  }

  public void stop()
  {
    this.mThreadDone = true;
  }

  private boolean isValidState()
  {
    return !this.mThreadDone;
  }

  private void logException(Exception paramException, Object paramObject)
  {
    if (!isValidState())
      return;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Failed to prefetch ").append(paramObject).append(" ");
    if ((paramException instanceof GoatException))
    {
      if (paramException.getMessage() != null)
        localStringBuilder.append(paramException.getMessage());
      localStringBuilder.append(paramException.toString());
      MPerformanceLog.log(Level.WARNING, localStringBuilder.toString());
    }
    else
    {
      if ((paramException instanceof RuntimeException))
        localStringBuilder.append("RuntimeException ");
      if (paramException.getMessage() != null)
        localStringBuilder.append(paramException.getMessage());
      if (MPerformanceLog != null)
        MPerformanceLog.log(Level.WARNING, localStringBuilder.toString(), paramException);
    }
  }

  private final ServerLogin getServerLogin(String paramString)
    throws GoatException
  {
    return SessionData.get().getServerLogin(paramString);
  }

  public static class FetchItem
    implements PrefetchWorker.Item, Serializable
  {
    private static final long serialVersionUID = 1094146809002213218L;
    private String server;
    private String schema;
    private String viewname;
    private String username;
    private String locale;
    private String timezone;

    FetchItem(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
    {
      if (paramString1 != null)
        this.server = paramString1.trim();
      if (paramString2 != null)
        this.schema = paramString2.trim();
      if (paramString3 != null)
        this.username = paramString3.trim();
      if (paramString4 != null)
        this.locale = paramString4.trim();
      if (paramString5 != null)
        this.timezone = paramString5.trim();
      if (paramString6 != null)
        this.viewname = paramString6.trim();
    }

    public String getServer()
    {
      return this.server;
    }

    public String getSchema()
    {
      return this.schema;
    }

    public String getUsername()
    {
      return this.username;
    }

    public void setUsername(String paramString)
    {
      this.username = paramString;
    }

    public String getViewname()
    {
      return this.viewname;
    }

    public String getLocale()
    {
      return this.locale;
    }

    public String getTimezone()
    {
      return this.timezone;
    }

    public String toString()
    {
      return "server:" + this.server + "||" + "form:" + this.schema + "||" + "username:" + this.username + "||" + "view:" + this.viewname + "||" + "locale:" + this.locale + "||" + "timezone:" + this.timezone;
    }
  }

  protected static class PrefetchSessionData extends SessionData
  {
    private final ServerLogin serverLogin;
    private static final String SESSION_ID = "fixed prefetch session id";

    PrefetchSessionData(String paramString1, ServerLogin paramServerLogin, String paramString2, String paramString3)
      throws GoatException
    {
      super(null, null, paramString2, paramString3, null, "fixed prefetch session id", true);
      this.serverLogin = paramServerLogin;
    }

    public ServerLogin getServerLogin(String paramString)
    {
      if ((this.serverLogin != null) && (this.serverLogin.getServer().equals(paramString)))
        return this.serverLogin;
      ServerLogin localServerLogin = (ServerLogin)PrefetchWorkerImpl.serverLoginTL.get();
      if ((localServerLogin != null) && (localServerLogin.getServer().equals(paramString)))
      {
        try
        {
          localServerLogin.impersonateUser(null);
        }
        catch (ARException localARException)
        {
          localARException.printStackTrace();
        }
        return localServerLogin;
      }
      try
      {
        localServerLogin = ServerLogin.getAdmin(paramString);
      }
      catch (GoatException localGoatException)
      {
        localServerLogin = null;
      }
      return localServerLogin;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.PrefetchWorkerImpl
 * JD-Core Version:    0.6.1
 */