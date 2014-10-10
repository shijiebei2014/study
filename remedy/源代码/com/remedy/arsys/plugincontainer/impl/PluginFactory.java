package com.remedy.arsys.plugincontainer.impl;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.plugincontainer.Plugin;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SharedObjectLoader;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import javax.servlet.ServletContext;

public class PluginFactory
{
  private static Object MMapLock = new Object();
  private static Map MPluginCache = Collections.synchronizedMap(new HashMap());
  private static final boolean DEBUG = false;
  private static Log MLog = Log.get(13);
  private static ServletContext MServletContext;
  private static String MServletName;
  private static final File DOWNLOADED_PLUGINS_DIR = new File(Configuration.getInstance().getRootPath() + "/PluginsCache");
  private static final File LOCAL_PLUGINS_DIR = new File(Configuration.getInstance().getRootPath() + "/LocalPlugins");
  private static final int PLUGIN_NAME_FIELD_ID = 41000;
  private static final int PLUGIN_VERSION_FIELD_ID = 41004;
  private static final int PLUGIN_CLASS_FIELD_ID = 41003;
  private static final int PLUGIN_ATTACH_FIELD_ID = 41006;
  private static final int[] PLUGIN_SCHEMA_KEY_IDS = { 41000, 41003 };
  private static final Value STATUS_ACTIVE = new Value(Long.valueOf(0L), DataType.ENUM);
  private static final ArithmeticOrRelationalOperand PLUGIN_NAME_OPERAND = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41000);
  private static final QualifierInfo STATUS_EQ_ACTIVE = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.Status.getFieldId()), new ArithmeticOrRelationalOperand(STATUS_ACTIVE)));
  private static final int[] FIELDS_TO_RETRIEVE = { 41000, 41004, 41003, 41006, CoreFieldId.ModifiedDate.getFieldId() };
  private static final int[] FIELDS_TO_RETRIEVE_WITH_VERSION = { 41000, 41004, 41003, 41006, CoreFieldId.ModifiedDate.getFieldId(), 41014, 41015, 41016 };
  private static final URL[] EMPTY_URL_ARR = new URL[0];
  private static final String LOCAL_DISK = "LocalDisk";
  private static final int CURRENT_API_VERSION = 81000;
  private static final int FLDS_WITH_VERSIONCTRL = 70500;
  private static final int CURRENT_API_MAJOR_VERSION = 8;
  private static final int CURRENT_API_MINOR_VERSION = 1;
  private static final int CURRENT_API_SUBMINOR_VERSION = 0;
  private static final int MINIMUM_API_VERSION = 70001;
  private static final QualifierInfo LASTSTEP_QUAL = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41015), new ArithmeticOrRelationalOperand(new Value(Integer.valueOf(1), DataType.INTEGER)))), new QualifierInfo(new RelationalOperationInfo(5, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41016), new ArithmeticOrRelationalOperand(new Value(Integer.valueOf(0), DataType.INTEGER)))));
  private static final QualifierInfo MINOR_LASTSTEP_QUAL = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(4, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41015), new ArithmeticOrRelationalOperand(new Value(Integer.valueOf(1), DataType.INTEGER)))), LASTSTEP_QUAL);
  private static final QualifierInfo MAJOR_LASTSTEP_QUAL = new QualifierInfo(1, new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41014), new ArithmeticOrRelationalOperand(new Value(Integer.valueOf(8), DataType.INTEGER)))), MINOR_LASTSTEP_QUAL);
  private static final QualifierInfo FINAL_VERSION_QUAL = new QualifierInfo(2, new QualifierInfo(new RelationalOperationInfo(4, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41014), new ArithmeticOrRelationalOperand(new Value(Integer.valueOf(8), DataType.INTEGER)))), MAJOR_LASTSTEP_QUAL);

  static void init(String paramString, ServletContext paramServletContext)
  {
    MServletName = paramString;
    MServletContext = paramServletContext;
  }

  public static Plugin getInstance(String paramString)
    throws GoatException
  {
    assert ((paramString != null) && (paramString.length() > 0));
    String str = "Plugin:" + paramString;
    List localList = Configuration.getInstance().getPluginServers();
    Iterator localIterator = localList.iterator();
    synchronized (str.intern())
    {
      Map localMap;
      synchronized (MMapLock)
      {
        localMap = MPluginCache;
      }
      Object localObject1 = (PluginInfo)localMap.get(str);
      int i = 0;
      while (i == 0)
      {
        int j;
        if (localIterator.hasNext())
        {
          ??? = (String)localIterator.next();
          j = (localObject1 == null) || (!((PluginInfo)localObject1).getServer().equals(???)) ? 1 : 0;
        }
        else
        {
          ??? = null;
          i = 1;
          j = (localObject1 == null) || (!"LocalDisk".equals(((PluginInfo)localObject1).getServer())) ? 1 : 0;
        }
        if (j != 0)
        {
          PluginInfo localPluginInfo;
          try
          {
            localPluginInfo = ??? == null ? new PluginInfo(paramString) : new PluginInfo(paramString, (String)???);
          }
          catch (GoatException localGoatException2)
          {
            MLog.log(Level.FINE, "Exception while trying to find plugin \"" + paramString + "\" in the server \"" + (String)??? + "\"", localGoatException2);
          }
          continue;
          synchronized (MMapLock)
          {
            localMap = MPluginCache;
          }
          localObject1 = (PluginInfo)localMap.put(str, localPluginInfo);
          if (localObject1 != null)
            ((PluginInfo)localObject1).destroy();
          localObject1 = localPluginInfo;
        }
        else
        {
          try
          {
            if (localObject1 != null)
              return ((PluginInfo)localObject1).getPlugin();
          }
          catch (GoatException localGoatException1)
          {
            MLog.log(Level.FINE, "Exception while trying to find module \"" + paramString + "\" in the server \"" + (String)??? + "\"", localGoatException1);
            ((PluginInfo)localObject1).destroy();
            localObject1 = null;
          }
        }
      }
    }
    throw new GoatException(9391, paramString, localList.toString());
  }

  public static void cleanup()
  {
    Map localMap;
    synchronized (MMapLock)
    {
      localMap = MPluginCache;
      MPluginCache = Collections.synchronizedMap(new HashMap());
    }
    ??? = localMap.values().iterator();
    while (((Iterator)???).hasNext())
    {
      PluginInfo localPluginInfo = (PluginInfo)((Iterator)???).next();
      assert (localPluginInfo != null);
      localPluginInfo.destroy();
      ((Iterator)???).remove();
    }
  }

  private static class MyURLClassLoader extends URLClassLoader
  {
    private MyURLClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
    }

    protected Class findClass(String paramString)
      throws ClassNotFoundException
    {
      PluginFactory.MLog.log(Level.INFO, "My classloader is called to find class " + paramString);
      return super.findClass(paramString);
    }

    public Class loadClass(String paramString)
      throws ClassNotFoundException
    {
      PluginFactory.MLog.log(Level.INFO, "My classloader is called to load class " + paramString);
      return super.loadClass(paramString);
    }

    protected synchronized Class loadClass(String paramString, boolean paramBoolean)
      throws ClassNotFoundException
    {
      PluginFactory.MLog.log(Level.INFO, "My mod classloader is called to load class " + paramString + " with the resolve boolean as " + paramBoolean);
      return super.loadClass(paramString, paramBoolean);
    }

    protected Package definePackage(String paramString, Manifest paramManifest, URL paramURL)
      throws IllegalArgumentException
    {
      PluginFactory.MLog.log(Level.INFO, "Define package has been called for package " + paramString + "  at URL " + paramURL);
      return super.definePackage(paramString, paramManifest, paramURL);
    }

    protected Package getPackage(String paramString)
    {
      PluginFactory.MLog.log(Level.INFO, "Get package has been called for package " + paramString);
      return super.getPackage(paramString);
    }

    protected Package[] getPackages()
    {
      PluginFactory.MLog.log(Level.INFO, "Get packages has been called ");
      return super.getPackages();
    }
  }

  private static class PluginInfo
  {
    private final boolean mFromAR;
    private String mVersion;
    private final String mPluginName;
    private final String mServer;
    private String mClassName;
    private Plugin mPlugin;
    private long mLastModified;
    private long mLastChecked;
    private File mInputFile;
    private String mEntryID;
    private AttachmentValue mAttachmentValue;
    private String mSchemaKey;
    private int mAPIVersion = 70001;
    private boolean forceDownload = false;

    PluginInfo(String paramString1, String paramString2)
      throws GoatException
    {
      assert ((paramString1 != null) && (paramString2 != null));
      this.mFromAR = true;
      this.mPluginName = paramString1;
      this.mServer = paramString2;
      updatePluginInfo();
    }

    PluginInfo(String paramString)
      throws GoatException
    {
      assert ((paramString != null) && (paramString.length() > 0));
      this.mFromAR = false;
      this.mPluginName = paramString;
      this.mServer = "LocalDisk";
      updatePluginInfo();
    }

    String getVersion()
    {
      return this.mVersion;
    }

    String getPluginSchemaKey()
      throws GoatException, ARException
    {
      return SchemaKeyFactory.getInstance().getSchemaKey(ServerLogin.getAdmin(this.mServer), PluginFactory.PLUGIN_SCHEMA_KEY_IDS);
    }

    String getServer()
    {
      return this.mServer;
    }

    int getAPIVersion()
    {
      return this.mAPIVersion;
    }

    void destroy()
    {
      if (this.mPlugin != null)
        this.mPlugin.cleanup();
      File localFile = getPluginDir();
      deleteDir(localFile);
    }

    private void deleteDir(File paramFile)
    {
      assert (paramFile.isDirectory());
      if (paramFile.exists())
      {
        File[] arrayOfFile = paramFile.listFiles();
        if (arrayOfFile != null)
          for (int i = arrayOfFile.length - 1; i >= 0; i--)
            if (arrayOfFile[i].isDirectory())
              deleteDir(arrayOfFile[i]);
            else
              arrayOfFile[i].delete();
      }
    }

    Plugin getPlugin()
      throws GoatException
    {
      long l = System.currentTimeMillis();
      if ((this.mPlugin == null) || (l - this.mLastChecked > Configuration.getInstance().getCacheUpdateInterval() * 1000))
      {
        this.mLastChecked = l;
        File localFile1 = getPluginDir();
        File localFile2 = new File(localFile1, "classes.jar");
        updatePluginInfo();
        boolean bool = (!localFile2.exists()) || (localFile2.lastModified() < this.mLastModified) || (this.forceDownload == true);
        if (bool)
        {
          this.forceDownload = false;
          PluginFactory.MLog.fine("Last modifed time for the plugin jar is " + localFile2.lastModified() + " entry last modified is " + this.mLastModified + "\n");
          if ((!localFile1.exists()) && (!localFile1.mkdirs()))
            throw new GoatException(9392, new String[] { localFile1.getAbsolutePath(), this.mPluginName, this.mServer });
          localFile2.delete();
          downloadJarFile(localFile2);
          localFile2 = new File(localFile1 = getPluginDir(), "classes.jar");
          if (!localFile2.setLastModified(this.mLastModified))
            PluginFactory.MLog.warning("Last modified time for the plugin Jar file " + localFile2 + " did not get set to the required value " + this.mLastModified + ". The files last modified is " + localFile2.lastModified() + "\n");
          this.mPlugin = null;
        }
        if (this.mPlugin == null)
        {
          URL[] arrayOfURL = null;
          try
          {
            arrayOfURL = getJarURLs(localFile2, bool);
          }
          catch (IOException localIOException)
          {
            throw new GoatException(9394, localIOException, new String[] { this.mPluginName, this.mServer });
          }
          Object localObject1 = SharedObjectLoader.getInstance(null);
          if (localObject1 == null)
            localObject1 = Thread.currentThread().getContextClassLoader();
          URLClassLoader localURLClassLoader = new URLClassLoader(arrayOfURL, (ClassLoader)localObject1);
          try
          {
            PluginFactory.MLog.fine("The plugin class is " + this.mClassName + "\n");
            DefinitionServiceImpl.pluginRemoved(this.mPluginName);
            Class localClass = localURLClassLoader.loadClass(this.mClassName);
            Object localObject2 = localClass.newInstance();
            if (localObject2.getClass().getClassLoader() != localURLClassLoader)
              PluginFactory.MLog.fine("The plugin class " + this.mClassName + " has been loaded by a parent classloader. The class is incorrectly present in the web apps classpath. It should only be present in the jar in the ar system plugin form.\n");
            if ((localObject2 instanceof Plugin))
            {
              this.mPlugin = ((Plugin)localObject2);
              PluginConfigImpl localPluginConfigImpl = new PluginConfigImpl(PluginFactory.MServletName, PluginFactory.MServletContext, this.mPluginName, this.mVersion, new Properties());
              this.mPlugin.init(localPluginConfigImpl);
            }
            else
            {
              throw new GoatException(9398, new String[] { this.mClassName, this.mPluginName, this.mServer });
            }
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            throw new GoatException(9399, localClassNotFoundException, new String[] { this.mClassName, this.mPluginName, this.mServer });
          }
          catch (InstantiationException localInstantiationException)
          {
            throw new GoatException(9400, localInstantiationException, new String[] { this.mClassName, this.mPluginName, this.mServer });
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new GoatException(9401, localIllegalAccessException, new String[] { this.mClassName, this.mPluginName, this.mServer });
          }
        }
        this.mLastChecked = System.currentTimeMillis();
      }
      return this.mPlugin;
    }

    private File getPluginDir()
    {
      try
      {
        File localFile1 = new File(PluginFactory.DOWNLOADED_PLUGINS_DIR, URLEncoder.encode(this.mServer, "UTF-8"));
        return new File(localFile1, URLEncoder.encode(this.mPluginName, "UTF-8"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        File localFile2 = new File(PluginFactory.DOWNLOADED_PLUGINS_DIR, this.mServer);
        return new File(localFile2, this.mPluginName);
      }
    }

    private void downloadJarFile(File paramFile)
      throws GoatException
    {
      if (this.mFromAR)
      {
        assert (this.mAttachmentValue != null);
        try
        {
          ServerLogin.getAdmin(this.mServer).getEntryBlob(this.mSchemaKey, this.mEntryID, 41006, paramFile.getPath());
          File localFile = new File(paramFile.getPath());
          if (!localFile.exists())
            throw new GoatException(9394, new String[] { this.mPluginName, this.mServer });
        }
        catch (ARException localARException)
        {
          throw new GoatException(9394, localARException, new String[] { this.mPluginName, this.mServer });
        }
        catch (GoatException localGoatException)
        {
          throw new GoatException(9394, localGoatException, new String[] { this.mPluginName, this.mServer });
        }
      }
      else
      {
        BufferedInputStream localBufferedInputStream = null;
        try
        {
          localBufferedInputStream = new BufferedInputStream(new FileInputStream(this.mInputFile));
          copyStreamDataToFile(localBufferedInputStream, paramFile);
        }
        catch (IOException localIOException1)
        {
          throw new GoatException(9394, localIOException1, new String[] { this.mPluginName, this.mServer });
        }
        finally
        {
          if (localBufferedInputStream != null)
            try
            {
              localBufferedInputStream.close();
            }
            catch (IOException localIOException2)
            {
            }
        }
      }
    }

    private void updatePluginInfo()
      throws GoatException
    {
      Object localObject1;
      if (this.mFromAR)
      {
        try
        {
          if (this.mSchemaKey == null)
            this.mSchemaKey = getPluginSchemaKey();
          Entry localEntry = getEntryForPlugin();
          assert (localEntry != null);
          this.mEntryID = localEntry.getEntryId();
          this.mVersion = ((Value)localEntry.get(Integer.valueOf(41004))).getValue().toString();
          this.mLastModified = (((Timestamp)((Value)localEntry.get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()))).getValue()).getValue() * 1000L);
          if (!(((Value)localEntry.get(Integer.valueOf(41006))).getValue() instanceof AttachmentValue))
            throw new GoatException(9393, new String[] { this.mPluginName, this.mServer });
          if (localEntry.get(Integer.valueOf(41006)) != null)
            this.mAttachmentValue = ((AttachmentValue)((Value)localEntry.get(Integer.valueOf(41006))).getValue());
          localObject1 = (Value)localEntry.get(Integer.valueOf(41003));
          if (localObject1 == null)
            throw new GoatException(9398, new String[] { this.mClassName, this.mPluginName, this.mServer });
          this.mClassName = ((Value)localObject1).toString();
        }
        catch (ARException localARException)
        {
          throw new GoatException(9394, localARException, new String[] { this.mPluginName, this.mServer });
        }
        catch (GoatException localGoatException)
        {
          throw new GoatException(9394, localGoatException, new String[] { this.mPluginName, this.mServer });
        }
      }
      else if ((this.mInputFile == null) || (this.mLastModified != this.mInputFile.lastModified()))
      {
        File localFile = new File(PluginFactory.LOCAL_PLUGINS_DIR, this.mPluginName);
        localObject1 = new File(localFile, "details.txt");
        if (!((File)localObject1).exists())
          throw new GoatException(9395, new String[] { this.mPluginName });
        Properties localProperties = new Properties();
        BufferedInputStream localBufferedInputStream = null;
        try
        {
          localBufferedInputStream = new BufferedInputStream(new FileInputStream((File)localObject1));
          localProperties.load(localBufferedInputStream);
        }
        catch (IOException localIOException1)
        {
          throw new GoatException(9396, localIOException1, new String[] { ((File)localObject1).getAbsolutePath(), this.mPluginName });
        }
        finally
        {
          if (localBufferedInputStream != null)
            try
            {
              localBufferedInputStream.close();
            }
            catch (IOException localIOException2)
            {
            }
        }
        this.mVersion = localProperties.getProperty("Version");
        this.mClassName = localProperties.getProperty("EntryClass");
        this.mInputFile = new File(localFile, this.mPluginName + ".jar");
        if (!this.mInputFile.exists())
          throw new GoatException(9397, new String[] { this.mInputFile.getAbsolutePath(), this.mPluginName });
        this.mLastModified = this.mInputFile.lastModified();
        int i = 7;
        int j = 0;
        int k = 1;
        String str = localProperties.getProperty("APIMajorVersion");
        if ((str != null) && (str.length() > 0))
          i = Integer.parseInt(str);
        str = localProperties.getProperty("APIMinorVersion");
        if ((str != null) && (str.length() > 0))
          j = Integer.parseInt(str);
        str = localProperties.getProperty("APISubMinorVersion");
        if ((str != null) && (str.length() > 0))
          k = Integer.parseInt(str);
        int m = 10000 * i + 100 * j + k;
        if (81000 < m)
          throw new GoatException(9391, new String[] { this.mPluginName, "LocalSystem" });
        this.mAPIVersion = m;
      }
    }

    private URL[] getJarURLs(File paramFile, boolean paramBoolean)
      throws IOException
    {
      JarFile localJarFile = new JarFile(paramFile);
      File localFile = paramFile.getParentFile();
      Manifest localManifest = localJarFile.getManifest();
      Attributes localAttributes = localManifest.getMainAttributes();
      ArrayList localArrayList = new ArrayList();
      PluginFactory.MLog.fine("The plugin url is " + paramFile.toURI().toURL() + "\n");
      localArrayList.add(paramFile.toURI().toURL());
      String str1 = localAttributes.getValue(Attributes.Name.CLASS_PATH);
      if (str1 != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str1, " ");
        while (localStringTokenizer.hasMoreTokens())
        {
          String str2 = localStringTokenizer.nextToken();
          URL localURL = getDependentJarURL(localJarFile, localFile, str2, paramBoolean);
          if (localURL != null)
            localArrayList.add(localURL);
        }
      }
      return (URL[])localArrayList.toArray(PluginFactory.EMPTY_URL_ARR);
    }

    private URL getDependentJarURL(JarFile paramJarFile, File paramFile, String paramString, boolean paramBoolean)
      throws IOException
    {
      ZipEntry localZipEntry = paramJarFile.getEntry(paramString);
      if (localZipEntry != null)
      {
        File localFile = new File(paramFile, paramString);
        if ((paramBoolean) || (!localFile.exists()))
        {
          localFile.getParentFile().mkdirs();
          InputStream localInputStream = paramJarFile.getInputStream(localZipEntry);
          try
          {
            copyStreamDataToFile(localInputStream, localFile);
          }
          finally
          {
            localInputStream.close();
          }
        }
        PluginFactory.MLog.fine("Dependent jar file URL is " + localFile.toURI().toURL() + "\n");
        return localFile.toURI().toURL();
      }
      return null;
    }

    private void copyStreamDataToFile(InputStream paramInputStream, File paramFile)
      throws IOException
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
      BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
      try
      {
        byte[] arrayOfByte = new byte[8192];
        int i;
        while ((i = paramInputStream.read(arrayOfByte)) != -1)
          localBufferedOutputStream.write(arrayOfByte, 0, i);
      }
      finally
      {
        localBufferedOutputStream.close();
      }
    }

    private Entry getAPICompatibleEntry(List<Entry> paramList)
    {
      int i = 7;
      int j = 0;
      int k = 1;
      int m = 70001;
      Object localObject = null;
      int n = 81000 - m;
      int i1 = this.mAPIVersion;
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if (localEntry != null)
        {
          Value localValue = (Value)localEntry.get(Integer.valueOf(41014));
          if ((localValue != null) && (DataType.INTEGER.equals(localValue.getDataType())))
            i = ((Integer)localValue.getValue()).intValue();
          localValue = (Value)localEntry.get(Integer.valueOf(41015));
          if ((localValue != null) && (DataType.INTEGER.equals(localValue.getDataType())))
            j = ((Integer)localValue.getValue()).intValue();
          localValue = (Value)localEntry.get(Integer.valueOf(41016));
          if ((localValue != null) && (DataType.INTEGER.equals(localValue.getDataType())))
            k = ((Integer)localValue.getValue()).intValue();
          m = 10000 * i + 100 * j + k;
          if (81000 >= m)
            if (81000 - m <= n)
            {
              n = 81000 - m;
              localObject = localEntry;
              i1 = m;
              if (n == 0)
                break;
            }
        }
      }
      if (i1 != this.mAPIVersion)
      {
        this.mAPIVersion = i1;
        this.forceDownload = true;
      }
      return localObject;
    }

    private Entry getEntryForPlugin()
      throws ARException, GoatException
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(this.mServer);
      QualifierInfo localQualifierInfo = createQual();
      ServerInfo localServerInfo = ServerInfo.get(this.mServer, true);
      int[] arrayOfInt = PluginFactory.FIELDS_TO_RETRIEVE;
      if (localServerInfo.getVersionAsNumber() >= 70500)
      {
        arrayOfInt = PluginFactory.FIELDS_TO_RETRIEVE_WITH_VERSION;
        localQualifierInfo = new QualifierInfo(1, localQualifierInfo, PluginFactory.FINAL_VERSION_QUAL);
      }
      List localList = localServerLogin.getListEntryObjects(this.mSchemaKey, localQualifierInfo, 0, 0, new ArrayList(), arrayOfInt, false, null);
      if ((localList == null) || (localList.size() < 1))
        throw new GoatException(9391, new String[] { this.mPluginName, this.mServer });
      Entry localEntry = getAPICompatibleEntry(localList);
      if (localEntry == null)
        throw new GoatException(9391, new String[] { this.mPluginName, this.mServer });
      return localEntry;
    }

    private QualifierInfo createQual()
    {
      assert (this.mPluginName != null);
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(new Value(this.mPluginName));
      return new QualifierInfo(1, PluginFactory.STATUS_EQ_ACTIVE, new QualifierInfo(new RelationalOperationInfo(1, PluginFactory.PLUGIN_NAME_OPERAND, localArithmeticOrRelationalOperand)));
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PluginFactory
 * JD-Core Version:    0.6.1
 */