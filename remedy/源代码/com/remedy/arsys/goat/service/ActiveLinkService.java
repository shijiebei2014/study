package com.remedy.arsys.goat.service;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ActiveLinkCriteria;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.cache.sync.ServerSync;
import com.remedy.arsys.goat.intf.service.IActiveLinkService;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class ActiveLinkService
  implements IActiveLinkService
{
  private static final Log MLog = Log.get(7);
  private static final ActiveLinkCriteria AL_CRITERIA = new ActiveLinkCriteria();

  public com.remedy.arsys.goat.ActiveLink get(String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(0);
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
    com.bmc.arsys.api.ActiveLink localActiveLink1;
    try
    {
      localActiveLink1 = localServerLogin.getActiveLink(paramString2, AL_CRITERIA);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    if (localActiveLink1 == null)
      throw new GoatException("ActiveLinkFactory.findByKey(" + paramString2 + ") failed");
    localMeasurement.end();
    com.remedy.arsys.goat.ActiveLink localActiveLink = new com.remedy.arsys.goat.ActiveLink(paramString1, localActiveLink1, paramString3, paramString4);
    ServerSync.initActiveLinkObjectTimesIfNeeded(paramString1);
    return localActiveLink;
  }

  public List<com.remedy.arsys.goat.ActiveLink> get(String paramString1, String paramString2, String[] paramArrayOfString, String paramString3, String paramString4)
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
    List localList1 = Arrays.asList(paramArrayOfString);
    List localList2;
    try
    {
      int i = localServerLogin.getTimeoutNormal();
      int j = localServerLogin.getTimeoutLong();
      int k = localServerLogin.getTimeoutXLong();
      localServerLogin.setTimeoutNormal(36000000);
      localServerLogin.setTimeoutLong(36000000);
      localServerLogin.setTimeoutXLong(36000000);
      localList2 = localServerLogin.getListActiveLinkObjects(localList1, AL_CRITERIA);
      localServerLogin.setTimeoutNormal(i);
      localServerLogin.setTimeoutLong(j);
      localServerLogin.setTimeoutXLong(k);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    finally
    {
    }
    if (localList2 == null)
      throw new GoatException("ActiveLinkFactory.findObjs() failed");
    List localList3 = getHelper(localList2, paramString1, paramString3, paramString4);
    return localList3;
  }

  private List<com.remedy.arsys.goat.ActiveLink> getHelper(List<com.bmc.arsys.api.ActiveLink> paramList, String paramString1, String paramString2, String paramString3)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      com.bmc.arsys.api.ActiveLink localActiveLink = (com.bmc.arsys.api.ActiveLink)localIterator.next();
      try
      {
        com.remedy.arsys.goat.ActiveLink localActiveLink1 = new com.remedy.arsys.goat.ActiveLink(paramString1, localActiveLink, paramString2, paramString3);
        ServerSync.initActiveLinkObjectTimesIfNeeded(paramString1);
        localArrayList.add(localActiveLink1);
      }
      catch (GoatException localGoatException)
      {
        MLog.log(Level.FINE, "Caught exception while bulk loading activelinks - dropping " + localActiveLink.toString(), localGoatException);
      }
    }
    return localArrayList;
  }

  public List<com.remedy.arsys.goat.ActiveLink> getAsAdmin(String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(paramString1);
      int i = localServerLogin.getTimeoutNormal();
      int j = localServerLogin.getTimeoutLong();
      int k = localServerLogin.getTimeoutXLong();
      localServerLogin.setTimeoutNormal(36000000);
      localServerLogin.setTimeoutLong(36000000);
      localServerLogin.setTimeoutXLong(36000000);
      List localList = localServerLogin.getListActiveLinkObjects(paramString2, 0L, AL_CRITERIA);
      localServerLogin.setTimeoutNormal(i);
      localServerLogin.setTimeoutLong(j);
      localServerLogin.setTimeoutXLong(k);
      if (localList == null)
        return new ArrayList();
      return getHelper(localList, paramString1, paramString3, paramString4);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
  }

  static
  {
    AL_CRITERIA.setPropertiesToRetrieve(ActiveLinkCriteria.ACTION_LIST | ActiveLinkCriteria.CONTROL_FIELD | ActiveLinkCriteria.ELSE_LIST | ActiveLinkCriteria.ENABLE | ActiveLinkCriteria.EXECUTE_MASK | ActiveLinkCriteria.FOCUS_FIELD | ActiveLinkCriteria.ORDER | ActiveLinkCriteria.QUERY | ActiveLinkCriteria.PROPERTY_LIST | ActiveLinkCriteria.NAME | ActiveLinkCriteria.TIMESTAMP | ActiveLinkCriteria.WORKFLOW_CONNECT);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.ActiveLinkService
 * JD-Core Version:    0.6.1
 */