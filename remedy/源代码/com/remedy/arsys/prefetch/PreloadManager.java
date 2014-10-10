package com.remedy.arsys.prefetch;

import com.bmc.arsys.api.ARException;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.ActiveLink;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.ActiveLinkCache;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Holder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class PreloadManager extends AbstractManager
{
  private long mStartTime = 0L;
  private static boolean doneLoadingAllActiveLinkMenus = true;

  public PreloadManager(Set<PrefetchWorker.Item> paramSet)
  {
    super(PrefetchManager.roundRobin(paramSet), true);
  }

  public PreloadManager(Set<PrefetchWorker.Item> paramSet, HashMap<String, List<String>> paramHashMap)
  {
    super(PrefetchManager.roundRobin(paramSet), true, paramHashMap);
  }

  public void start(boolean paramBoolean)
  {
    this.mStartTime = System.currentTimeMillis();
    MPerformanceLog.log(Level.WARNING, "START form preload; start time = " + this.mStartTime);
    super.start(paramBoolean);
  }

  public boolean threadDone()
  {
    if (!super.threadDone())
      return false;
    this.svrformToALMap = null;
    ActiveLinkCache localActiveLinkCache = (ActiveLinkCache)GoatCacheManager.getInstance().getGoatCache("Active links");
    if (localActiveLinkCache != null)
      synchronized (localActiveLinkCache)
      {
        ActiveLinkCache.resetActiveLinkCount();
        localActiveLinkCache.save();
      }
    GoatCacheManager.doSave(true);
    ??? = PrefetchTask.MAP_SERVER_PRELOAD_STATE.keySet().iterator();
    String str = null;
    while (((Iterator)???).hasNext())
    {
      str = (String)((Iterator)???).next();
      PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(str, Integer.valueOf(PrefetchTask.PRELOAD_COMPLETED));
    }
    long l = System.currentTimeMillis();
    MPerformanceLog.log(Level.WARNING, "END form preload; end time = " + l + "; TOTAL(ms) = " + (l - this.mStartTime));
    return true;
  }

  public PrefetchWorker createWorker(AbstractManager paramAbstractManager)
  {
    return new PrefetchWorkerImpl(paramAbstractManager, false, true);
  }

  protected static synchronized Set<PrefetchWorker.Item> loadAllActiveLinkMenus(Holder<HashMap<String, List<String>>> paramHolder)
  {
    doneLoadingAllActiveLinkMenus = false;
    HashSet localHashSet = new HashSet();
    HashMap localHashMap = new HashMap();
    long l1 = System.currentTimeMillis();
    MPerformanceLog.log(Level.WARNING, "START activelink/menu preload; start time = " + l1);
    List localList1 = Configuration.getInstance().getServers();
    ServerLogin localServerLogin = null;
    for (int i = 0; i < localList1.size(); i++)
    {
      String str1 = (String)localList1.get(i);
      if (Configuration.getInstance().getServerPreloadFlag(str1))
        try
        {
          PrefetchTask.MAP_SERVER_PRELOAD_STATE.put(str1, Integer.valueOf(PrefetchTask.PRELOAD_RUNNING));
          PrefetchTask.MAP_SERVER_PRELOADED_ITEMS_COUNT.put(str1, Integer.valueOf(0));
          localServerLogin = ServerLogin.getAdmin(str1);
          PrefetchWorkerImpl.PrefetchSessionData localPrefetchSessionData = new PrefetchWorkerImpl.PrefetchSessionData(localServerLogin.getUser(), localServerLogin, null, null);
          SessionData.set(localPrefetchSessionData);
          String str2 = localServerLogin.getServer();
          String[] arrayOfString = (String[])localServerLogin.getListActiveLink(0L).toArray(new String[0]);
          List localList2 = ActiveLink.bulkLoad(localServerLogin, null, arrayOfString, "NOT_VALID_FORM", "NOT_VALID_VIEW");
          ArrayList localArrayList = new ArrayList();
          if (localList2 != null)
          {
            localObject1 = localList2.iterator();
            Object localObject2;
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (ActiveLink)((Iterator)localObject1).next();
              List localList3 = ((ActiveLink)localObject2).getFormList();
              String str3 = ((ActiveLink)localObject2).getName();
              Iterator localIterator = localList3.iterator();
              while (localIterator.hasNext())
              {
                String str4 = (String)localIterator.next();
                if (!localArrayList.contains(str4))
                  localArrayList.add(str4);
                String str5 = (str2 + "_" + str4).intern();
                Object localObject3 = (List)localHashMap.get(str5);
                if (localObject3 == null)
                  localObject3 = new ArrayList();
                ((List)localObject3).add(str3);
                localHashMap.put(str5, localObject3);
              }
            }
            PrefetchTask.MAP_SERVER_PRELOAD_ITEMS_COUNT.put(str1, Integer.valueOf(localArrayList.size()));
            MPerformanceLog.info("------------------------------------------------------------------------------------------");
            MPerformanceLog.info("(Server , Count) - " + str1 + "," + localArrayList.size());
            MPerformanceLog.info("------------------------------------------------------------------------------------------");
            MPerformanceLog.log(Level.INFO, "Forms to be loaded for server: " + str1);
            localObject1 = localArrayList.iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (String)((Iterator)localObject1).next();
              localHashSet.add(PrefetchManager.createItem(str1, (String)localObject2, localServerLogin.getUser(), null, null, null));
              MPerformanceLog.log(Level.INFO, (String)localObject2);
            }
            MPerformanceLog.log(Level.INFO, "End of the formlist for server: " + str1);
          }
          Object localObject1 = new ArrayList(0);
          Menu.bulkLoad(str1, (List)localObject1, true);
          SessionData.set(null);
        }
        catch (ARException localARException)
        {
          MPerformanceLog.log(Level.WARNING, localARException.getMessage());
        }
        catch (GoatException localGoatException)
        {
          MPerformanceLog.log(Level.WARNING, localGoatException.getMessage());
        }
    }
    long l2 = System.currentTimeMillis();
    MPerformanceLog.log(Level.WARNING, "END Activelink/menu preload; end time = " + l2 + "; TOTAL(ms) = " + (l2 - l1));
    doneLoadingAllActiveLinkMenus = true;
    paramHolder.set(localHashMap);
    return localHashSet;
  }

  protected static boolean getDoneLoadingAllActiveLinkMenus()
  {
    return doneLoadingAllActiveLinkMenus;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.PreloadManager
 * JD-Core Version:    0.6.1
 */