package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.GoatException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ServerLoginThreadManager extends ThreadLocal
{
  protected Object initialValue()
  {
    return new LinkedList();
  }

  ServerLogin get(String paramString, ServerLoginHost paramServerLoginHost)
    throws GoatException
  {
    LinkedList localLinkedList = (LinkedList)get();
    if (localLinkedList.size() > 0)
    {
      ListIterator localListIterator = localLinkedList.listIterator(0);
      while (localListIterator.hasNext())
      {
        ServerLogin localServerLogin = (ServerLogin)localListIterator.next();
        if ((localServerLogin.getServer().equals(paramString)) && (localServerLogin.getHost().customEquals(localServerLogin.getServer()) == paramServerLoginHost.customEquals(paramString)))
          return localServerLogin;
      }
    }
    return null;
  }

  Iterator grabAll(ServerLoginHost paramServerLoginHost)
  {
    LinkedList localLinkedList1 = new LinkedList();
    LinkedList localLinkedList2 = (LinkedList)get();
    ListIterator localListIterator = null;
    ServerLogin localServerLogin = null;
    if (localLinkedList2.size() > 0)
    {
      localListIterator = localLinkedList2.listIterator(0);
      while (localListIterator.hasNext())
      {
        localServerLogin = (ServerLogin)localListIterator.next();
        if (localServerLogin.getHost() == paramServerLoginHost)
          localLinkedList1.add(localServerLogin);
      }
    }
    if (localLinkedList1.size() > 0)
      return localLinkedList1.listIterator();
    return null;
  }

  void put(ServerLogin paramServerLogin)
  {
    if (ServerLogin.MDebug)
      System.out.println("SLTM> Login " + paramServerLogin.hashCode() + " moving to threadvar " + hashCode() + " cache");
    LinkedList localLinkedList = (LinkedList)get();
    assert (localLinkedList != null);
    localLinkedList.add(paramServerLogin);
    paramServerLogin.updateLastUsedTime();
  }

  void threadDepartingGoatSpace()
  {
    threadDepartingGoatSpace(false);
  }

  void threadDepartingGoatSpace(boolean paramBoolean)
  {
    LinkedList localLinkedList = (LinkedList)get();
    if (localLinkedList.size() == 0)
      return;
    if (ServerLogin.MDebug)
      System.out.println("SLTM> Threadvar " + hashCode() + " leaving GoatSpace:");
    ListIterator localListIterator = localLinkedList.listIterator(0);
    while (localListIterator.hasNext())
    {
      ServerLogin localServerLogin = (ServerLogin)localListIterator.next();
      if (paramBoolean)
        localServerLogin.logout();
      else
        localServerLogin.threadDeparting();
      if (ServerLogin.MDebug)
        if (localServerLogin.getHost() != null)
          System.out.println("SLTM> Returning login " + localServerLogin.hashCode() + " to host " + localServerLogin.getHost() + " (hash:" + localServerLogin.getHost().hashCode() + ")");
        else
          System.out.println("SLTM> Returning login " + localServerLogin.hashCode() + " to host " + localServerLogin.getHost() + " (hash:)");
      localListIterator.remove();
    }
    assert (localLinkedList.size() == 0);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ServerLoginThreadManager
 * JD-Core Version:    0.6.1
 */