package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.ServerInfo;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ServerLoginGrimReaper extends TimerTask
{
  private static Timer MReaperTimer;
  private static Map MReaperMap;

  public static void addToReaper(ServerLogin paramServerLogin)
  {
    assert (paramServerLogin.getHost() == null);
    String str = paramServerLogin.getServer();
    synchronized (MReaperMap)
    {
      LinkedList localLinkedList = (LinkedList)MReaperMap.get(str);
      if (localLinkedList == null)
      {
        localLinkedList = new LinkedList();
        MReaperMap.put(str, localLinkedList);
      }
      localLinkedList.add(paramServerLogin);
    }
  }

  public void run()
  {
    if (ServerLogin.MDebug)
      System.out.println("SLGR> Reaper launching");
    long l = new Date().getTime();
    HashSet localHashSet = new HashSet();
    synchronized (MReaperMap)
    {
      localIterator = MReaperMap.keySet().iterator();
      while (localIterator.hasNext())
        localHashSet.add((String)localIterator.next());
    }
    ??? = new HashMap();
    Iterator localIterator = localHashSet.iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (String)localIterator.next();
      Long localLong1;
      try
      {
        ServerInfo localServerInfo = ServerInfo.get((String)localObject2);
        localLong1 = new Long(localServerInfo.getFloatTimeoutMillis());
      }
      catch (GoatException localGoatException)
      {
        localLong1 = new Long(3600000L);
      }
      if (ServerLogin.MDebug)
        System.out.println("SLGR> Licence timeout for server " + (String)localObject2 + " is " + localLong1.longValue());
      ((Map)???).put(localObject2, localLong1);
    }
    Object localObject2 = new ArrayList();
    Object localObject3;
    synchronized (MReaperMap)
    {
      localObject3 = MReaperMap.values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        LinkedList localLinkedList = (LinkedList)((Iterator)localObject3).next();
        ListIterator localListIterator = localLinkedList.listIterator(0);
        while (localListIterator.hasNext())
        {
          ServerLogin localServerLogin = (ServerLogin)localListIterator.next();
          assert ((localServerLogin.getHost() == null) && (localServerLogin.getServer() != null));
          String str = localServerLogin.getServer();
          if (((Map)???).containsKey(str))
          {
            Long localLong2 = (Long)((Map)???).get(str);
            if (l - localServerLogin.mLastUsedTime >= localLong2.longValue())
            {
              if (ServerLogin.MDebug)
                System.out.println("SLGR> Reaping login  " + localServerLogin.hashCode() + " for user " + localServerLogin.getUser().toString());
              localListIterator.remove();
              ((ArrayList)localObject2).add(localServerLogin);
            }
            else if (ServerLogin.MDebug)
            {
              System.out.println("SLGR> Leaving login " + localServerLogin.hashCode() + " for user " + localServerLogin.getUser().toString() + " as is...");
            }
          }
          else if (ServerLogin.MDebug)
          {
            System.out.println("SLGR> Missing server information for " + str);
          }
        }
        if (localLinkedList.size() == 0)
          ((Iterator)localObject3).remove();
      }
    }
    for (int i = 0; i < ((ArrayList)localObject2).size(); i++)
    {
      localObject3 = (ServerLogin)((ArrayList)localObject2).get(i);
      if (ServerLogin.MDebug)
        System.out.println("SLGR> Logging out login " + ((ServerLogin)localObject3).hashCode() + " for user " + ((ServerLogin)localObject3).getUser().toString());
      ((ServerLogin)localObject3).logout();
    }
    ServerLogin.threadDepartingGoatSpace();
  }

  static
  {
    MReaperMap = new HashMap();
    MReaperTimer = new Timer(true);
    MReaperTimer.schedule(new ServerLoginGrimReaper(), 1000L, 300000L);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ServerLoginGrimReaper
 * JD-Core Version:    0.6.1
 */