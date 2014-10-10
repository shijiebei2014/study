package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.ContainerCriteria;
import com.bmc.arsys.api.ContainerOwner;
import com.bmc.arsys.api.Reference;
import com.bmc.arsys.api.ReferenceType;
import com.bmc.arsys.api.Timestamp;
import com.remedy.arsys.goat.cache.sync.ServerSync;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.log.MeasureTime.Measurement;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.MiscCache;
import com.remedy.arsys.share.ObjectCreationCount;
import com.remedy.arsys.share.ObjectReleaseCount;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Guide
  implements Cache.Item
{
  private static final long serialVersionUID = 4389763105438935242L;
  private static Cache MCache = new MiscCache("Guides");
  private final String mServerName;
  private final String mSchemaName;
  private String mGuideName;
  private final String mGuideOriginalName;
  private final Reference[] mReferences;
  private boolean mIsEPGuide = false;
  private static transient Log cacheLog = Log.get(1);
  private final List<String> mFormList;
  private final long mLastUpdateTimeMs;
  private static ObjectReleaseCount delObjCnt = new ObjectReleaseCount();
  private static ObjectCreationCount createObjCnt = new ObjectCreationCount();

  private Guide(Key paramKey)
    throws GoatException
  {
    synchronized (createObjCnt)
    {
      ObjectCreationCount.inc(6);
    }
    ??? = new ContainerCriteria();
    ((ContainerCriteria)???).setPropertiesToRetrieve(ContainerCriteria.CONTAINER_OWNER | ContainerCriteria.REFERENCES);
    GoatException localGoatException = null;
    ServerLogin localServerLogin = paramKey.getContext();
    MeasureTime.Measurement localMeasurement = new MeasureTime.Measurement(14);
    Container localContainer;
    try
    {
      localContainer = localServerLogin.getContainer(paramKey.getContainerKey(), (ContainerCriteria)???);
    }
    catch (ARException localARException1)
    {
      localContainer = null;
      localGoatException = new GoatException(localARException1);
    }
    finally
    {
      localMeasurement.end();
    }
    if (localContainer == null)
    {
      localObject2 = EPGuideALCollection.getOriginalGuideNameFromEPGuideName(paramKey.getContainerKey());
      if (localObject2 != null)
      {
        Object localObject4 = localObject2;
        localMeasurement = new MeasureTime.Measurement(14);
        try
        {
          localContainer = paramKey.getContext().getContainer(localObject4, (ContainerCriteria)???);
        }
        catch (ARException localARException2)
        {
          throw new GoatException(localARException2);
        }
        finally
        {
          localMeasurement.end();
        }
      }
      else if (localGoatException != null)
      {
        throw localGoatException;
      }
    }
    if (localContainer == null)
      throw new GoatException(9370, paramKey.toString());
    Object localObject2 = localContainer.getContainerOwner();
    if (((List)localObject2).size() < 1)
      throw new GoatException(9370, paramKey.toString());
    this.mServerName = paramKey.getContext().getServer();
    this.mSchemaName = ((ContainerOwner)((List)localObject2).iterator().next()).getName();
    this.mGuideName = localContainer.getName();
    this.mGuideOriginalName = localContainer.getName();
    this.mReferences = ((Reference[])localContainer.getReferences().toArray(new Reference[0]));
    if (this.mReferences != null)
      for (int i = 0; i < this.mReferences.length; i++)
        if (this.mReferences[i].getReferenceType().equals(ReferenceType.ARREF_ENTRYPOINT_START_ACTLINK))
        {
          this.mGuideName = EPGuideALCollection.getEPGuideNameFromOriginal(this.mGuideName);
          this.mIsEPGuide = true;
          break;
        }
    this.mFormList = new ArrayList(((List)localObject2).size());
    Object localObject5 = ((List)localObject2).iterator();
    while (((Iterator)localObject5).hasNext())
    {
      ContainerOwner localContainerOwner = (ContainerOwner)((Iterator)localObject5).next();
      this.mFormList.add(localContainerOwner.getName());
    }
    localObject5 = localContainer.getLastUpdateTime();
    this.mLastUpdateTimeMs = ((Timestamp)localObject5).getValue();
  }

  public String getServer()
  {
    return this.mServerName;
  }

  public String getSchema()
  {
    return this.mSchemaName;
  }

  public static Guide get(Key paramKey)
    throws GoatException
  {
    String str = paramKey.getCacheKey().intern();
    Guide localGuide;
    synchronized (str)
    {
      localGuide = (Guide)MCache.get(str, Guide.class);
      if (localGuide == null)
      {
        localGuide = new Guide(paramKey);
        ServerSync.initContainerObjectTimesIfNeeded(localGuide.getServerName());
        MCache.put(str, localGuide);
      }
    }
    return localGuide;
  }

  public String getServerName()
  {
    return this.mServerName;
  }

  public String getGuideName()
  {
    return this.mGuideName;
  }

  public String getOriginalGuideName()
  {
    return this.mGuideOriginalName;
  }

  public Reference[] getReferences()
  {
    return this.mReferences;
  }

  public boolean isEntryPointGuide()
  {
    return this.mIsEPGuide;
  }

  public ServerLogin getServerLogin()
    throws GoatException
  {
    return SessionData.get().getServerLogin(this.mServerName);
  }

  public int getSize()
  {
    return 1;
  }

  public long getLastUpdateTimeMs()
  {
    return this.mLastUpdateTimeMs;
  }

  public List<String> getFormList()
  {
    return this.mFormList;
  }

  public static Cache getCache()
  {
    return MCache;
  }

  public void finalize()
    throws Throwable
  {
    try
    {
      synchronized (delObjCnt)
      {
        delObjCnt.inc(6);
      }
    }
    finally
    {
      super.finalize();
    }
  }

  public static class Key
  {
    private final String mServer;
    private final String mContainerKey;

    public Key(ServerLogin paramServerLogin, String paramString)
    {
      this.mServer = paramServerLogin.getServer().toLowerCase();
      this.mContainerKey = paramString;
    }

    public ServerLogin getContext()
      throws GoatException
    {
      return SessionData.get().getServerLogin(this.mServer);
    }

    public String getContainerKey()
    {
      return this.mContainerKey;
    }

    public String getCacheKey()
      throws GoatException
    {
      return "Guide:" + this.mServer.toLowerCase() + "/" + this.mContainerKey.toString();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Guide
 * JD-Core Version:    0.6.1
 */