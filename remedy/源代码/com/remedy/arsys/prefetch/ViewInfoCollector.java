package com.remedy.arsys.prefetch;

import com.bmc.arsys.api.GroupInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ViewInfoCollector
{
  public static final String VIEWSTATS_FILENAME = "viewStats.dat";
  private static long MLastPrefetchModifiedTime = new Date().getTime();
  private static long MCurrentPrefetchModifiedTime = new Date().getTime();
  private static int MStatCeilingCount = getFormsCacheCeilingCount();
  private static Log MLog = Log.get(8);
  private static FrequencyOrderedSet MViewStatistics = new FrequencyOrderedSet();

  public static void add(Form paramForm, Form.ViewInfo paramViewInfo)
  {
    SessionData localSessionData = SessionData.get();
    String str1 = paramForm.getServerName();
    String str2 = paramForm.getName();
    String str3 = localSessionData.getUserName();
    String str4 = localSessionData.getLocale();
    String str5 = localSessionData.getTimeZone();
    String str6 = paramViewInfo.getName();
    PrefetchWorker.Item localItem = PrefetchManager.createItem(str1, str2, str3, str4, str5, str6);
    synchronized (MViewStatistics)
    {
      MViewStatistics.add(localItem);
    }
  }

  public static int getStatCeilingCount()
  {
    return MStatCeilingCount;
  }

  public static synchronized Set<PrefetchWorker.Item> getViewUsageSet()
  {
    return MViewStatistics.set();
  }

  public static synchronized void remove(PrefetchWorker.Item paramItem)
  {
    MViewStatistics.remove(paramItem);
  }

  public static synchronized void saveStatistics()
  {
    MViewStatistics.saveCurrentStats();
  }

  public static boolean isNewPrefetchFile()
  {
    return MCurrentPrefetchModifiedTime - MLastPrefetchModifiedTime > 0L;
  }

  private static int getHtmlCacheCeilingCount()
  {
    int i = Cache.getMaxElementsInMemory("Html");
    return i;
  }

  private static int getFormsCacheCeilingCount()
  {
    int i = Cache.getMaxElementsInMemory("Forms");
    return i;
  }

  static
  {
    MViewStatistics.loadStatsFromDisk();
  }

  static class WeightedItemImpl
    implements ViewInfoCollector.WeightedItem, Serializable
  {
    private static final long serialVersionUID = 1269745881999540746L;
    private final PrefetchWorker.Item mItem;
    private int mWeight = 1;
    private String mPermissionKeys = null;

    public WeightedItemImpl(PrefetchWorker.Item paramItem)
    {
      this.mItem = paramItem;
      this.mPermissionKeys = computeResolvedUserGroupComboCode();
    }

    public String getPermissionKeys()
    {
      return this.mPermissionKeys;
    }

    public int getWeight()
    {
      return this.mWeight;
    }

    public void incrementWeight()
    {
      this.mWeight += 1;
    }

    public String getLocale()
    {
      return this.mItem.getLocale();
    }

    public String getSchema()
    {
      return this.mItem.getSchema();
    }

    public String getServer()
    {
      return this.mItem.getServer();
    }

    public String getTimezone()
    {
      return this.mItem.getTimezone();
    }

    public String getUsername()
    {
      return this.mItem.getUsername();
    }

    public String getViewname()
    {
      return this.mItem.getViewname();
    }

    public String toString()
    {
      return this.mItem.toString() + "||weight:" + this.mWeight;
    }

    public boolean equals(Object paramObject)
    {
      ViewInfoCollector.WeightedItem localWeightedItem = null;
      try
      {
        localWeightedItem = (Serializable)paramObject;
      }
      catch (Exception localException)
      {
        return false;
      }
      if (getServer() != null)
      {
        if (!getServer().equalsIgnoreCase(localWeightedItem.getServer()))
          return false;
      }
      else if (localWeightedItem.getServer() != null)
        return false;
      if (getSchema() != null)
      {
        if (!getSchema().equalsIgnoreCase(localWeightedItem.getSchema()))
          return false;
      }
      else if (localWeightedItem.getSchema() != null)
        return false;
      if (getViewname() != null)
      {
        if (!getViewname().equalsIgnoreCase(localWeightedItem.getViewname()))
          return false;
      }
      else if (localWeightedItem.getViewname() != null)
        return false;
      if (getLocale() != null)
      {
        if (!getLocale().equalsIgnoreCase(localWeightedItem.getLocale()))
          return false;
      }
      else if (localWeightedItem.getLocale() != null)
        return false;
      if (getPermissionKeys() != null)
      {
        if (!getPermissionKeys().equalsIgnoreCase(localWeightedItem.getPermissionKeys()))
          return false;
      }
      else if (localWeightedItem.getPermissionKeys() != null)
        return false;
      return true;
    }

    private String computeResolvedUserGroupComboCode()
    {
      if (getServer() == null)
        return null;
      String str = null;
      GroupInfo[] arrayOfGroupInfo1 = null;
      ServerLogin localServerLogin = null;
      try
      {
        localServerLogin = ServerLogin.getAdmin(getServer());
        arrayOfGroupInfo1 = localServerLogin.getGroupInfo();
        GroupInfo[] arrayOfGroupInfo2 = (GroupInfo[])localServerLogin.getListGroup(getUsername(), null).toArray(new GroupInfo[0]);
        localServerLogin.setPermKey(arrayOfGroupInfo2);
        str = localServerLogin.getPermissionsKey();
      }
      catch (Exception localException)
      {
      }
      finally
      {
        if ((arrayOfGroupInfo1 != null) && (localServerLogin != null))
          localServerLogin.setPermKey(arrayOfGroupInfo1);
      }
      return str;
    }
  }

  static class ItemComparator
    implements Comparator<ViewInfoCollector.WeightedItem>
  {
    public int compare(ViewInfoCollector.WeightedItem paramWeightedItem1, ViewInfoCollector.WeightedItem paramWeightedItem2)
    {
      return paramWeightedItem1.getWeight() - paramWeightedItem2.getWeight();
    }
  }

  public static abstract interface WeightedItem extends PrefetchWorker.Item
  {
    public abstract int getWeight();

    public abstract void incrementWeight();

    public abstract String getPermissionKeys();
  }

  static class FrequencyOrderedSet
  {
    private ArrayList<ViewInfoCollector.WeightedItem> mList = new ArrayList();
    private final ViewInfoCollector.ItemComparator mComparator = new ViewInfoCollector.ItemComparator();
    private final ViewInfoCollector.WeightedItem mDummy = new ViewInfoCollector.WeightedItemImpl(PrefetchManager.createItem(null, null, null, null, null, null));

    public void add(PrefetchWorker.Item paramItem)
    {
      Object localObject = new ViewInfoCollector.WeightedItemImpl(paramItem);
      if (((ViewInfoCollector.WeightedItem)localObject).getPermissionKeys() == null)
        return;
      if (!this.mList.contains(localObject))
      {
        this.mList.add(localObject);
      }
      else
      {
        int i = this.mList.lastIndexOf(localObject);
        localObject = (ViewInfoCollector.WeightedItem)this.mList.get(i);
        ((ViewInfoCollector.WeightedItem)localObject).incrementWeight();
        if (i == 0)
          return;
        ViewInfoCollector.WeightedItem localWeightedItem = (ViewInfoCollector.WeightedItem)this.mList.get(i - 1);
        if (this.mComparator.compare(localWeightedItem, (ViewInfoCollector.WeightedItem)localObject) < 0)
        {
          this.mList.set(i, this.mDummy);
          this.mList.set(i - 1, localObject);
          this.mList.set(i, localWeightedItem);
        }
      }
    }

    public void remove(PrefetchWorker.Item paramItem)
    {
      this.mList.remove(new ViewInfoCollector.WeightedItemImpl(paramItem));
    }

    public Iterator<PrefetchWorker.Item> iterator()
    {
      LinkedHashSet localLinkedHashSet = new LinkedHashSet(this.mList);
      return localLinkedHashSet.iterator();
    }

    public Set<PrefetchWorker.Item> set()
    {
      if (this.mList.size() > 0)
        this.mList = validateStats(this.mList);
      return new LinkedHashSet(this.mList);
    }

    void loadStatsFromDisk()
    {
      URL localURL = getFileAsURL("config.properties");
      String str1 = localURL.getFile();
      try
      {
        str1 = URLDecoder.decode(str1, "UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        ViewInfoCollector.MLog.fine("View Statistics cannot open files: " + localUnsupportedEncodingException.toString());
      }
      str1 = str1.replace("config.properties", "viewStats.dat");
      String str2 = str1.replace("viewStats.dat", "prefetchConfig.xml");
      String str3 = str1 + ".bak";
      File localFile = new File(str1);
      ViewInfoCollector.access$102(getFileLastModifiedTime(str2));
      FileInputStream localFileInputStream = null;
      ObjectInputStream localObjectInputStream = null;
      if (!localFile.exists())
      {
        localFile = new File(str3);
        if (!localFile.exists())
          return;
      }
      try
      {
        localFileInputStream = new FileInputStream(localFile);
        localObjectInputStream = new ObjectInputStream(localFileInputStream);
        ViewInfoCollector.access$202(((Long)localObjectInputStream.readObject()).longValue());
        this.mList = ((ArrayList)localObjectInputStream.readObject());
      }
      catch (Exception localException1)
      {
        if (localFile.getName().equalsIgnoreCase("viewStats.dat"))
        {
          localFile = new File(str3);
          if (localFile.exists())
            try
            {
              localFileInputStream = new FileInputStream(localFile);
              localObjectInputStream = new ObjectInputStream(localFileInputStream);
              ViewInfoCollector.access$202(((Long)localObjectInputStream.readObject()).longValue());
              this.mList = ((ArrayList)localObjectInputStream.readObject());
            }
            catch (Exception localException2)
            {
              ViewInfoCollector.MLog.fine("View Statistics cannot open files: " + localException1.toString());
            }
        }
        ViewInfoCollector.MLog.fine("View Statistics cannot open files: " + localException1.toString());
      }
      finally
      {
        try
        {
          if (localObjectInputStream != null)
            localObjectInputStream.close();
          if (localFileInputStream != null)
            localFileInputStream.close();
        }
        catch (Exception localException3)
        {
        }
      }
    }

    void saveCurrentStats()
    {
      URL localURL = getFileAsURL("config.properties");
      String str1 = localURL.getFile();
      try
      {
        str1 = URLDecoder.decode(str1, "UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        ViewInfoCollector.MLog.fine("View Statistics cannot write files: " + localUnsupportedEncodingException.toString());
      }
      str1 = str1.replace("config.properties", "viewStats.dat");
      String str2 = str1 + ".bak";
      File localFile1 = new File(str1);
      File localFile2 = new File(str2);
      FileOutputStream localFileOutputStream = null;
      ObjectOutputStream localObjectOutputStream = null;
      try
      {
        if ((localFile1.exists()) && (localFile2.exists()))
        {
          if (localFile2.delete())
            localFile1.renameTo(new File(str2));
          else
            localFile1.delete();
        }
        else if ((localFile1.exists()) && (!localFile2.exists()))
          localFile1.renameTo(new File(str2));
        localFile1 = new File(str1);
        localFileOutputStream = new FileOutputStream(localFile1);
        localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
        localObjectOutputStream.writeObject(new Long(ViewInfoCollector.MCurrentPrefetchModifiedTime));
        localObjectOutputStream.writeObject(this.mList);
      }
      catch (Exception localException)
      {
        ViewInfoCollector.MLog.fine("View Statistics cannot close files: " + localException.toString());
      }
      finally
      {
        try
        {
          if (localObjectOutputStream != null)
            localObjectOutputStream.close();
          if (localFileOutputStream != null)
            localFileOutputStream.close();
        }
        catch (IOException localIOException)
        {
        }
      }
    }

    private URL getFileAsURL(String paramString)
    {
      URL localURL = getClass().getResource(paramString);
      if (localURL == null)
        localURL = Thread.currentThread().getContextClassLoader().getResource(paramString);
      if (localURL == null)
      {
        String str = getClass().getPackage().getName();
        str = str.replace('.', '/');
        str = str + "/" + paramString;
        localURL = Thread.currentThread().getContextClassLoader().getResource(str);
      }
      if (localURL == null)
        ViewInfoCollector.MLog.info("failed to find the file: " + paramString);
      return localURL;
    }

    private static long getFileLastModifiedTime(String paramString)
    {
      File localFile = new File(paramString);
      if (localFile.exists())
        return localFile.lastModified();
      return 0L;
    }

    private static ArrayList<ViewInfoCollector.WeightedItem> validateStats(ArrayList<ViewInfoCollector.WeightedItem> paramArrayList)
    {
      ArrayList localArrayList = new ArrayList();
      Configuration localConfiguration = Configuration.getInstance();
      List localList = localConfiguration.getServers();
      ViewInfoCollector.WeightedItem localWeightedItem = null;
      for (int i = 0; i < paramArrayList.size(); i++)
      {
        localWeightedItem = (ViewInfoCollector.WeightedItem)paramArrayList.get(i);
        if (!localList.contains(localWeightedItem.getServer()))
          localArrayList.add(localWeightedItem);
      }
      if (localArrayList.size() > 0)
        for (i = 0; i < localArrayList.size(); i++)
          paramArrayList.remove(localArrayList.get(i));
      return paramArrayList;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.ViewInfoCollector
 * JD-Core Version:    0.6.1
 */