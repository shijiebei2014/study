package com.remedy.arsys.stubs;

import com.bmc.arsys.api.LoggingInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.ARServerLog;
import com.remedy.arsys.session.LoginServlet;
import com.remedy.arsys.support.MultiHashPool;
import com.remedy.arsys.support.MultiHashPool.IteratorCB;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class ServerLoginHost
{
  private MultiHashPool mHashPool = new MultiHashPool();
  private int mState = 0;
  private static final int STATE_ALIVE = 0;
  private static final int STATE_TIMED_OUT = 1;
  private static final int STATE_LOGGED_OUT = 2;
  public static final int OVERRIDE_UNSET = -1;
  public static final int OVERRIDE_NO = 0;
  public static final int OVERRIDE_YES = 1;

  public abstract String getUserName(String paramString)
    throws GoatException;

  public abstract String getPassword(String paramString)
    throws GoatException;

  public abstract String getAuthentication(String paramString)
    throws GoatException;

  public abstract int getPort(String paramString)
    throws GoatException;

  public abstract int getRPC(String paramString)
    throws GoatException;

  public abstract String getGUID(String paramString);

  public abstract int getOverride();

  public abstract String getLocale();

  public abstract String getTimezone();

  public abstract String getDateFormat();

  public abstract String getTimeFormat();

  public abstract Value getClientType();

  public abstract LoggingInfo getLoggingInfo();

  public abstract ARServerLog getServerLog();

  public synchronized ServerLogin get(String paramString)
    throws GoatException
  {
    if (this.mState != 0)
      throw new GoatException(9353);
    ServerLogin localServerLogin = (ServerLogin)this.mHashPool.iterate(paramString, true, new MultiHashPool.IteratorCB()
    {
      public boolean callback(Object paramAnonymousObject)
      {
        ServerLogin localServerLogin = (ServerLogin)paramAnonymousObject;
        return localServerLogin.getHost() == ServerLoginHost.this;
      }
    });
    return localServerLogin;
  }

  public synchronized Iterator grabAll()
  {
    Iterator localIterator1 = this.mHashPool.getKeySet();
    if (localIterator1 == null)
      return null;
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator2 = null;
    while (localIterator1.hasNext())
    {
      localIterator2 = this.mHashPool.getIterator(localIterator1.next());
      if (localIterator2 != null)
        while (localIterator2.hasNext())
          localLinkedList.add(localIterator2.next());
    }
    return localLinkedList.iterator();
  }

  public synchronized void threadDeparting(ServerLogin paramServerLogin)
  {
    if (this.mState == 0)
      this.mHashPool.addItem(paramServerLogin.getServer(), paramServerLogin);
    else if (this.mState == 1)
      paramServerLogin.moveToGrimReaper();
    else if (this.mState == 2)
      paramServerLogin.logout();
    else
      assert ("Invalid ServerLoginHost state" == null);
  }

  public synchronized void setHostDeparting()
  {
    if (this.mState != 0)
      return;
    this.mState = 1;
    ArrayList localArrayList = this.mHashPool.removeAll();
    if (ServerLogin.MDebug)
      System.err.println("SLH > Host " + hashCode() + " now departing " + localArrayList.size() + " items");
    for (int i = 0; i < localArrayList.size(); i++)
    {
      ServerLogin localServerLogin = (ServerLogin)localArrayList.get(i);
      localServerLogin.moveToGrimReaper();
    }
  }

  public synchronized void setHostLoggingOut()
  {
    if (this.mState != 0)
      return;
    this.mState = 2;
    ArrayList localArrayList = this.mHashPool.removeAll();
    if (ServerLogin.MDebug)
      System.err.println("SLH > Host " + hashCode() + " now logging out " + localArrayList.size() + " items");
    for (int i = 0; i < localArrayList.size(); i++)
    {
      assert ((localArrayList.get(i) instanceof ServerLogin));
      ServerLogin localServerLogin = (ServerLogin)localArrayList.get(i);
      localServerLogin.logout();
    }
  }

  public int customEquals(String paramString)
    throws GoatException
  {
    String str = getUserName(paramString) + getPassword(paramString) + getAuthentication(paramString) + getPort(paramString) + getRPC(paramString) + getGUID(paramString) + getOverride() + getLocale() + getTimezone() + getDateFormat() + getTimeFormat() + getClientType();
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = str.getBytes("UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    return LoginServlet.computeCRC32(arrayOfByte);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ServerLoginHost
 * JD-Core Version:    0.6.1
 */