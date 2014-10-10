package com.remedy.arsys.prefetch;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.GroupInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.support.Holder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PrefetchTask extends TimerTask
{
  public static final int PREFETCH_DELAY = 10;
  public static final String PREFETCHCONFIG_FILENAME = "prefetchConfig.xml";
  private static final String CONFIG_NS = "http://www.bmc.com/remedy/midtier/midtier";
  private static final String MIDTIER_PREFETCH_CONFIG = "midtier-prefetch-config";
  private static final String UTF_8_ENCODING = "UTF-8";
  private static final String TAG_PREFETCH_USER = "prefetch-user";
  private static final String TAG_USER_NAME = "user-name";
  private static final String TAG_PREFETCH_SERVER = "prefetch-server";
  private static final String TAG_SERVER_NAME = "server-name";
  private static final String TAG_LOCALE = "locale";
  private static final String TAG_TIMEZONE = "timezone";
  private static final String TAG_FORM_NAME = "form-name";
  private static final String TAG_PREFETCH_FORM = "prefetch-form";
  private static final String TAG_VIEW_NAME = "view-name";
  private static Log MPerformanceLog = Log.get(8);
  private static DocumentBuilder MDocumentBuilder;
  private static Transformer MXformer;
  private static Object MMutex = new Object();
  private volatile boolean mThreadDone = false;
  private static AbstractManager mWorkerManager;
  private static boolean mHasScheduledPrefetchTask = false;
  public static Map<String, Integer> MAP_SERVER_PRELOAD_STATE = new HashMap();
  public static Map<String, Integer> MAP_SERVER_PRELOAD_ITEMS_COUNT = new HashMap();
  public static Map<String, Integer> MAP_SERVER_PRELOADED_ITEMS_COUNT = new HashMap();
  public static int PRELOAD_DISABLED = 0;
  public static int PRELOAD_RUNNING = 1;
  public static int PRELOAD_COMPLETED = 2;
  public static int PRELOAD_NOT_RUNNING = 3;

  public void run()
  {
    synchronized (MMutex)
    {
      if (this.mThreadDone)
        return;
      mHasScheduledPrefetchTask = false;
      Cache.initCacheClasses();
      GoatCacheManager.doSave(true);
      mWorkerManager = createPrefetchAndViewBuildingManager();
      Set localSet = null;
      Holder localHolder = new Holder(null);
      localSet = PreloadManager.loadAllActiveLinkMenus(localHolder);
      mWorkerManager = createPreloadManager(localSet, mWorkerManager, (HashMap)localHolder.get());
      if (mWorkerManager != null)
        mWorkerManager.start(false);
    }
  }

  public AbstractManager createPrefetchAndViewBuildingManager()
  {
    AbstractManager localAbstractManager = null;
    FileInputStream localFileInputStream = loadPrefetchFromDisk();
    Set localSet1 = ViewInfoCollector.getViewUsageSet();
    if (localFileInputStream != null)
    {
      Set localSet2 = null;
      localSet2 = prefetchConfigToItemSet(localFileInputStream);
      if ((this.mThreadDone) || (((localSet2 == null) || (localSet2.isEmpty())) && ((localSet1 == null) || (localSet1.isEmpty()))))
      {
        localAbstractManager = null;
      }
      else
      {
        localAbstractManager = createManager(this, localSet2, localSet1);
        GoatCacheManager.addPrefetch(this);
        GoatCacheManager.setDirtyCheckEnable(false);
      }
    }
    else
    {
      if ((localSet1 != null) && (!localSet1.isEmpty()))
        localAbstractManager = createManager(this, null, localSet1);
      MPerformanceLog.fine("prefetch config file is not found.");
    }
    return localAbstractManager;
  }

  public static synchronized void schedule(long paramLong)
  {
    if ((mHasScheduledPrefetchTask) || (!PreloadManager.getDoneLoadingAllActiveLinkMenus()) || (!prefetchTaskDone()))
    {
      MPerformanceLog.warning("Prefetch task is still been executed, can not start up a new one.");
      return;
    }
    mHasScheduledPrefetchTask = true;
    Timer localTimer = new Timer(false);
    PrefetchTask localPrefetchTask = new PrefetchTask();
    localTimer.schedule(localPrefetchTask, 1000L * paramLong);
  }

  public void stop()
  {
    this.mThreadDone = true;
    if (mWorkerManager != null)
      mWorkerManager.stop();
  }

  private static AbstractManager createManager(PrefetchTask paramPrefetchTask, Set<PrefetchWorker.Item> paramSet1, Set<PrefetchWorker.Item> paramSet2)
  {
    Object localObject = null;
    if ((paramSet1 != null) && (paramSet1.size() != 0))
    {
      localObject = new PrefetchManager(paramPrefetchTask, paramSet1);
      ((PrefetchManager)localObject).setLoadActiveLink(true);
      if ((paramSet2 != null) && (paramSet2.size() != 0))
        ((AbstractManager)localObject).setSequentialManager(new ViewBuildingManager(paramSet2));
    }
    else
    {
      localObject = new ViewBuildingManager(paramSet2);
    }
    return localObject;
  }

  private static AbstractManager createPreloadManager(Set<PrefetchWorker.Item> paramSet, AbstractManager paramAbstractManager)
  {
    Object localObject = null;
    if ((paramSet != null) && (paramSet.size() != 0))
    {
      PreloadManager localPreloadManager = new PreloadManager(paramSet);
      if (paramAbstractManager != null)
      {
        localObject = paramAbstractManager.getSequentialManager() != null ? paramAbstractManager.getSequentialManager() : paramAbstractManager;
        ((AbstractManager)localObject).setSequentialManager(localPreloadManager);
        localObject = paramAbstractManager;
      }
      else
      {
        localObject = localPreloadManager;
      }
    }
    else
    {
      localObject = paramAbstractManager;
    }
    return localObject;
  }

  private static AbstractManager createPreloadManager(Set<PrefetchWorker.Item> paramSet, AbstractManager paramAbstractManager, HashMap<String, List<String>> paramHashMap)
  {
    Object localObject = null;
    if ((paramSet != null) && (paramSet.size() != 0))
    {
      PreloadManager localPreloadManager = new PreloadManager(paramSet, paramHashMap);
      if (paramAbstractManager != null)
      {
        if (paramAbstractManager.getSequentialManager() != null)
        {
          localPreloadManager.setSequentialManager(paramAbstractManager.getSequentialManager());
          paramAbstractManager.setSequentialManager(localPreloadManager);
          localObject = paramAbstractManager;
        }
        else if ((paramAbstractManager instanceof ViewBuildingManager))
        {
          localPreloadManager.setSequentialManager(paramAbstractManager);
          localObject = localPreloadManager;
        }
        else
        {
          paramAbstractManager.setSequentialManager(localPreloadManager);
          localObject = paramAbstractManager;
        }
      }
      else
        localObject = localPreloadManager;
    }
    else
    {
      localObject = paramAbstractManager;
    }
    return localObject;
  }

  public static boolean isPreloadTaskRunning()
  {
    synchronized (MMutex)
    {
      if ((mWorkerManager != null) && ((mWorkerManager instanceof PreloadManager)))
        return mWorkerManager.getThreadCount() != 0;
    }
    return false;
  }

  public static boolean prefetchTaskDone()
  {
    return getAllThreadCounts() <= 0;
  }

  private static int getAllThreadCounts()
  {
    int i = 0;
    if (mWorkerManager != null)
    {
      i = mWorkerManager.getThreadCount();
      for (AbstractManager localAbstractManager = mWorkerManager.getSequentialManager(); localAbstractManager != null; localAbstractManager = localAbstractManager.getSequentialManager())
        i += localAbstractManager.getThreadCount();
    }
    return i;
  }

  public static final String readPreFetchConfiguration()
  {
    String str1 = "";
    String str2 = Configuration.getInstance().getFilename("prefetchConfig.xml");
    if (str2 == null)
    {
      MPerformanceLog.fine("prefetch config file is not found.");
      return str1;
    }
    FileInputStream localFileInputStream = null;
    try
    {
      File localFile = new File(str2);
      localFileInputStream = new FileInputStream(localFile);
      if (localFileInputStream == null)
      {
        MPerformanceLog.fine("prefetch config file is not found.");
        localObject1 = str1;
        return localObject1;
      }
      Object localObject1 = new InputStreamReader(localFileInputStream, "UTF-8");
      BufferedReader localBufferedReader = new BufferedReader((Reader)localObject1);
      StringBuilder localStringBuilder = new StringBuilder();
      String str3 = null;
      while ((str3 = localBufferedReader.readLine()) != null)
      {
        localStringBuilder.append(str3);
        localStringBuilder.append("\n");
      }
      localBufferedReader.close();
      str1 = localStringBuilder.toString();
    }
    catch (Exception localException2)
    {
      MPerformanceLog.log(Level.WARNING, localException2.getMessage(), localException2);
      if ((localException2 instanceof RuntimeException))
        throw ((RuntimeException)localException2);
    }
    finally
    {
      try
      {
        if (localFileInputStream != null)
          localFileInputStream.close();
      }
      catch (Exception localException5)
      {
      }
    }
    return str1;
  }

  public static final String[] writePreFetchConfiguration(String paramString)
    throws IOException
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = null;
    arrayOfString[1] = paramString;
    if (paramString == null)
      return arrayOfString;
    FileOutputStream localFileOutputStream = null;
    try
    {
      Document localDocument = MDocumentBuilder.parse(new ByteArrayInputStream(paramString.getBytes("UTF-8")));
      Element localElement1 = localDocument.getDocumentElement();
      if (!isSyntaxRight(localElement1))
        arrayOfString[0] = ("Wrong midtier prefetch config root element. Expecting {http://www.bmc.com/remedy/midtier/midtier}midtier-prefetch-config;Got {" + localElement1.getNamespaceURI() + "}" + localElement1.getLocalName() + " (Hint -- make sure to save " + "prefetchConfig.xml" + " using Unicode editor)");
      NodeList localNodeList = localElement1.getElementsByTagNameNS("http://www.bmc.com/remedy/midtier/midtier", "prefetch-user");
      for (int i = 0; ; i++)
      {
        localObject1 = (Element)localNodeList.item(i);
        if (localObject1 == null)
          break;
        localObject2 = getChildValue((Element)localObject1, "user-name", true);
        localObject3 = ((Element)localObject1).getElementsByTagNameNS("http://www.bmc.com/remedy/midtier/midtier", "prefetch-server");
        for (int j = 0; ; j++)
        {
          Element localElement2 = (Element)((NodeList)localObject3).item(j);
          if (localElement2 == null)
            break;
          String str2 = getChildValue(localElement2, "server-name", true);
          isUserValid(str2, (String)localObject2);
        }
      }
      String str1 = Configuration.getInstance().getFilename("prefetchConfig.xml");
      Object localObject1 = new File(str1);
      localFileOutputStream = new FileOutputStream((File)localObject1);
      Object localObject2 = new OutputStreamWriter(localFileOutputStream, "UTF-8");
      Object localObject3 = new StreamResult((Writer)localObject2);
      MXformer.transform(new DOMSource(localDocument), (Result)localObject3);
    }
    catch (GoatException localGoatException)
    {
      MPerformanceLog.warning("GoatException:" + localGoatException.getMessage());
      arrayOfString[0] = localGoatException.toString();
    }
    catch (Exception localException3)
    {
      MPerformanceLog.log(Level.WARNING, localException3.getMessage(), localException3);
      arrayOfString[0] = localException3.toString();
    }
    finally
    {
      try
      {
        if (localFileOutputStream != null)
          localFileOutputStream.close();
      }
      catch (Exception localException5)
      {
      }
    }
    return arrayOfString;
  }

  private static void isUserValid(String paramString1, String paramString2)
    throws GoatException
  {
    ServerLogin localServerLogin = null;
    GroupInfo[] arrayOfGroupInfo1 = null;
    try
    {
      localServerLogin = ServerLogin.getAdmin(paramString1);
      ServerInfo localServerInfo = ServerInfo.get(localServerLogin, true);
      if (localServerInfo.getVersionAsNumber() < 70000)
      {
        MPerformanceLog.warning(paramString1 + " is pre-7.0 AR server, prefetch is ignored.");
        throw new GoatException(paramString1 + " is pre-7.0 AR server, prefetch is ignored.");
      }
      arrayOfGroupInfo1 = localServerLogin.getGroupInfo();
      GroupInfo[] arrayOfGroupInfo2 = (GroupInfo[])localServerLogin.getListGroup(paramString2, null).toArray(new GroupInfo[0]);
      localServerLogin.setPermKey(arrayOfGroupInfo2);
      localServerLogin.impersonateUser(paramString2);
    }
    catch (GoatException localGoatException)
    {
      throw localGoatException;
    }
    catch (ARException localARException2)
    {
      throw new GoatException(localARException2.getMessage());
    }
    finally
    {
      if (localServerLogin != null)
        try
        {
          if (arrayOfGroupInfo1 != null)
            localServerLogin.setPermKey(arrayOfGroupInfo1);
          localServerLogin.impersonateUser(null);
        }
        catch (ARException localARException3)
        {
          MPerformanceLog.warning("Prefetch failed to reset admin user back.");
        }
    }
  }

  private static boolean isSyntaxRight(Element paramElement)
  {
    boolean bool = false;
    if (paramElement == null)
      return bool;
    if (("http://www.bmc.com/remedy/midtier/midtier".equals(paramElement.getNamespaceURI())) && ("midtier-prefetch-config".equals(paramElement.getLocalName())))
      bool = true;
    return bool;
  }

  private Set<PrefetchWorker.Item> prefetchConfigToItemSet(InputStream paramInputStream)
  {
    HashSet localHashSet = null;
    try
    {
      if (this.mThreadDone)
      {
        localObject1 = null;
        return localObject1;
      }
      Object localObject1 = DocumentBuilderFactory.newInstance();
      ((DocumentBuilderFactory)localObject1).setNamespaceAware(true);
      DocumentBuilder localDocumentBuilder = ((DocumentBuilderFactory)localObject1).newDocumentBuilder();
      Document localDocument = localDocumentBuilder.parse(paramInputStream);
      Element localElement1 = localDocument.getDocumentElement();
      if (!isSyntaxRight(localElement1))
        throw new GoatException("Wrong midtier prefetch config root element. Expecting {http://www.bmc.com/remedy/midtier/midtier}midtier-prefetch-config;Got {" + localElement1.getNamespaceURI() + "}" + localElement1.getLocalName());
      NodeList localNodeList1 = localElement1.getElementsByTagNameNS("http://www.bmc.com/remedy/midtier/midtier", "prefetch-user");
      localHashSet = new HashSet();
      for (int i = 0; ; i++)
      {
        Element localElement2 = (Element)localNodeList1.item(i);
        if (localElement2 == null)
          break;
        String str1 = getChildValue(localElement2, "user-name", true);
        String str2 = getChildValue(localElement2, "locale", false);
        String str3 = getChildValue(localElement2, "timezone", false);
        NodeList localNodeList2 = localElement2.getElementsByTagNameNS("http://www.bmc.com/remedy/midtier/midtier", "prefetch-server");
        for (int j = 0; ; j++)
        {
          Element localElement3 = (Element)localNodeList2.item(j);
          if (localElement3 == null)
            break;
          String str4 = getChildValue(localElement3, "server-name", true);
          loadforms(localHashSet, null, localElement3, str4, str1, str2, str3);
        }
      }
    }
    catch (GoatException localGoatException)
    {
      MPerformanceLog.warning("GoatExceptin:" + localGoatException.getMessage());
    }
    catch (Exception localException)
    {
      MPerformanceLog.log(Level.WARNING, localException.getMessage(), localException);
      if ((localException instanceof RuntimeException))
        throw ((RuntimeException)localException);
    }
    finally
    {
      try
      {
        paramInputStream.close();
      }
      catch (IOException localIOException5)
      {
      }
    }
    return localHashSet;
  }

  private void loadforms(Set<PrefetchWorker.Item> paramSet, String paramString1, Element paramElement, String paramString2, String paramString3, String paramString4, String paramString5)
    throws GoatException
  {
    NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.bmc.com/remedy/midtier/midtier", "prefetch-form");
    for (int i = 0; ; i++)
    {
      Element localElement = (Element)localNodeList.item(i);
      if (localElement == null)
        break;
      String str1 = getChildValue(localElement, "form-name", true);
      String str2 = getChildValue(localElement, "view-name", false);
      paramSet.add(PrefetchManager.createItem(paramString2, str1, paramString3, paramString4, paramString5, str2));
    }
  }

  private static String getChildValue(Element paramElement, String paramString, boolean paramBoolean)
    throws GoatException
  {
    NodeList localNodeList1 = paramElement.getElementsByTagNameNS("http://www.bmc.com/remedy/midtier/midtier", paramString);
    if (localNodeList1.getLength() == 0)
    {
      if (paramBoolean)
        throw new GoatException("Missing requried child element {http://www.bmc.com/remedy/midtier/midtier}" + paramString + " of element {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName());
      return null;
    }
    if (localNodeList1.getLength() > 1)
      throw new GoatException("There can be only one child element {http://www.bmc.com/remedy/midtier/midtier}" + paramString + " of element {" + paramElement.getNamespaceURI() + "}" + paramElement.getLocalName());
    Element localElement = (Element)localNodeList1.item(0);
    NodeList localNodeList2 = localElement.getChildNodes();
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; ; i++)
    {
      Node localNode = localNodeList2.item(i);
      if (localNode == null)
      {
        if (localStringBuilder.length() <= 0)
          break;
        return localStringBuilder.toString();
      }
      if (3 == localNode.getNodeType())
      {
        if (localNode.getNodeValue() != null)
          localStringBuilder.append(localNode.getNodeValue());
      }
      else
        throw new GoatException("non text node found under element {" + localElement.getNamespaceURI() + "}" + localElement.getLocalName());
    }
    throw new GoatException("There is no text value for element{" + localElement.getNamespaceURI() + "}" + localElement.getLocalName());
  }

  private FileInputStream loadPrefetchFromDisk()
  {
    String str = Configuration.getInstance().getFilename("prefetchConfig.xml");
    if (str == null)
    {
      MPerformanceLog.fine("prefetch config file is not found.");
      return null;
    }
    FileInputStream localFileInputStream = null;
    try
    {
      File localFile = new File(str);
      localFileInputStream = new FileInputStream(localFile);
    }
    catch (Exception localException)
    {
      MPerformanceLog.fine("Prefetch process cannot open file: " + localException.toString());
    }
    return localFileInputStream;
  }

  public static Map getPreloadStatus()
  {
    return MAP_SERVER_PRELOAD_STATE;
  }

  static
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      MDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      MXformer = TransformerFactory.newInstance().newTransformer();
      MXformer.setOutputProperty("encoding", "UTF-8");
      MXformer.setOutputProperty("indent", "yes");
      MXformer.setOutputProperty("method", "xml");
      MXformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    }
    catch (Exception localException)
    {
      MPerformanceLog.log(Level.WARNING, localException.getMessage(), localException);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.prefetch.PrefetchTask
 * JD-Core Version:    0.6.1
 */