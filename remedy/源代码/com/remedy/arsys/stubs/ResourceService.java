package com.remedy.arsys.stubs;

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
import com.remedy.arsys.support.SchemaKeyFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceService
{
  private static ResourceService mInstance = null;
  private static Map<String, FileItem> MFileCache = Collections.synchronizedMap(new HashMap());
  private static Map<String, String> MServerFormCache = Collections.synchronizedMap(new HashMap());
  private static List<String> mServerList = new ArrayList();
  public static final int OSNUM = getAROSValue();
  private List<URL> mURLList = new ArrayList();
  private Timestamp mTimestamp = new Timestamp(0L);
  private static final String OS_WINDOWS = "Windows";
  private static final String OS_SOLARIS = "Solaris";
  private static final String OS_SOLARIS1 = "SunOS";
  private static final String OS_LINUX = "Linux";
  private static final String OS_AIX = "AIX";
  private static final String OS_HPUX = "HP";
  private static final String OS_HPUX1 = "UX";
  public static final int OS_WINNUM = 0;
  public static final int OS_SOLARISNUM = 1;
  public static final int OS_LINUXNUM = 2;
  public static final int OS_AIXNUM = 3;
  public static final int OS_HPUXNUM = 4;
  public static final int OS_ALL = 5;
  private static final Pattern MAJOR_VERSION_PAT = Pattern.compile("^([0-9]+)");
  private static final Pattern MINOR_VERSION_PAT = Pattern.compile("^[0-9]+.([0-9]+)");
  private static final Pattern SUBMINOR_VERSION_PAT = Pattern.compile("^[0-9]+.[0-9]+.([0-9]+)");
  private static final Pattern PATCH_VERSION_PAT = Pattern.compile("^[0-9]+.[0-9]+.[0-9]+.([0-9]+)");
  private static final int[] PLUGIN_SCHEMA_KEY_IDS = { 41020, 41021 };
  private static final int[] FIELDS_TO_RETRIEVE = { 41020, 41022, 41024, CoreFieldId.ModifiedDate.getFieldId() };
  private static final File MIDTIER_LIB_DIR = new File(Configuration.getInstance().getRootPath() + "/WEB-INF/lib");
  private static final File MIDTIER_JAR_DIR = new File(Configuration.getInstance().getRootPath() + "/ThirdPartyJars");
  private static final Value STATUS_ACTIVE = new Value(new Integer(0), DataType.ENUM);
  private static final Value PLATFORM_ALL = new Value(new Integer(5), DataType.ENUM);
  private static final QualifierInfo STATUS_EQ_ACTIVE = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, CoreFieldId.Status.getFieldId()), new ArithmeticOrRelationalOperand(STATUS_ACTIVE)));
  private static final QualifierInfo PLATFORM_ALL_QUAL = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41021), new ArithmeticOrRelationalOperand(PLATFORM_ALL)));
  private static final Value OS_VAL = new Value(new Integer(OSNUM), DataType.ENUM);
  private static final QualifierInfo OS_QUAL = new QualifierInfo(new RelationalOperationInfo(1, new ArithmeticOrRelationalOperand(OperandType.FIELDID, 41021), new ArithmeticOrRelationalOperand(OS_VAL)));
  private static final ArithmeticOrRelationalOperand MOD_DATE_OP = new ArithmeticOrRelationalOperand(CoreFieldId.ModifiedDate.getFieldId());
  private static final QualifierInfo SYSFILE_OS_ALL_QUAL = new QualifierInfo(2, PLATFORM_ALL_QUAL, OS_QUAL);
  private static final QualifierInfo SYSFILE_QUAL = new QualifierInfo(1, STATUS_EQ_ACTIVE, SYSFILE_OS_ALL_QUAL);
  protected Log mLog;

  private static int getAROSValue()
  {
    String str = System.getProperty("os.name");
    if (str == null)
      return 5;
    if (str.indexOf("Windows") != -1)
      return 0;
    if ((str.indexOf("Solaris") != -1) || (str.indexOf("SunOS") != -1))
      return 1;
    if (str.indexOf("Linux") != -1)
      return 2;
    if (str.indexOf("AIX") != -1)
      return 3;
    if ((str.indexOf("HP") != -1) && (str.indexOf("UX") != -1))
      return 4;
    return 5;
  }

  private static String getSchema(ServerLogin paramServerLogin)
    throws ARException
  {
    String str = null;
    str = (String)MServerFormCache.get(paramServerLogin.getServer());
    if (str == null)
    {
      str = SchemaKeyFactory.getInstance().getSchemaKey(paramServerLogin, PLUGIN_SCHEMA_KEY_IDS);
      if (str != null)
        MServerFormCache.put(paramServerLogin.getServer(), str);
    }
    return str;
  }

  private static QualifierInfo createTimestampQual(Timestamp paramTimestamp)
  {
    if ((paramTimestamp == null) || (paramTimestamp.getValue() == 0L))
      return null;
    Value localValue = new Value(paramTimestamp);
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand = new ArithmeticOrRelationalOperand(localValue);
    RelationalOperationInfo localRelationalOperationInfo = new RelationalOperationInfo(2, MOD_DATE_OP, localArithmeticOrRelationalOperand);
    return new QualifierInfo(localRelationalOperationInfo);
  }

  public synchronized void start(Log paramLog)
  {
    this.mLog = paramLog;
    buildFileItemList();
    downLoadFiles();
    listFiles();
    SharedObjectLoader.getInstance((URL[])this.mURLList.toArray(new URL[0]));
  }

  public static ResourceService getInstance()
  {
    if (mInstance == null)
      mInstance = new ResourceService();
    return mInstance;
  }

  int getIntValue(String paramString)
  {
    try
    {
      return Integer.parseInt(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    return 0;
  }

  private VersionInfo buildVersion(String paramString)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (paramString != null)
    {
      Matcher localMatcher1 = MAJOR_VERSION_PAT.matcher(paramString);
      Matcher localMatcher2 = MINOR_VERSION_PAT.matcher(paramString);
      Matcher localMatcher3 = SUBMINOR_VERSION_PAT.matcher(paramString);
      Matcher localMatcher4 = PATCH_VERSION_PAT.matcher(paramString);
      if (localMatcher1.find())
        i = getIntValue(localMatcher1.group(1));
      if (localMatcher2.find())
        j = getIntValue(localMatcher2.group(1));
      if (localMatcher3.find())
        k = getIntValue(localMatcher3.group(1));
      if (localMatcher4.find())
        m = getIntValue(localMatcher4.group(1));
    }
    return new VersionInfo(i, j, k, m);
  }

  private void buildFiles(String paramString, QualifierInfo paramQualifierInfo)
  {
    try
    {
      this.mLog.fine("Current OS is - " + System.getProperty("os.name"));
      ServerLogin localServerLogin = ServerLogin.getAdmin(paramString);
      String str1 = getSchema(localServerLogin);
      List localList = localServerLogin.getListEntryObjects(str1, paramQualifierInfo, 0, 0, new ArrayList(), FIELDS_TO_RETRIEVE, false, null);
      if ((localList == null) || (localList.size() <= 0))
      {
        this.mLog.warning("No File Entries were found in server - " + paramString);
        return;
      }
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        String str2 = (String)((Value)localEntry.get(Integer.valueOf(41020))).getValue();
        String str3 = (String)((Value)localEntry.get(Integer.valueOf(41022))).getValue();
        long l1 = ((Timestamp)((Value)localEntry.get(Integer.valueOf(CoreFieldId.ModifiedDate.getFieldId()))).getValue()).getValue() * 1000L;
        long l2 = ((Value)localEntry.get(Integer.valueOf(41024))).getValue() != null ? ((AttachmentValue)((Value)localEntry.get(Integer.valueOf(41024))).getValue()).getOriginalSize() : 0L;
        FileItem localFileItem = (FileItem)MFileCache.get(str2);
        if (localFileItem == null)
        {
          localFileItem = new FileItem(str2, paramString, str3, l1, l2, localEntry.getEntryId());
          MFileCache.put(str2, localFileItem);
        }
        else
        {
          VersionInfo localVersionInfo = buildVersion(str3);
          if ((localVersionInfo.compare(localFileItem.getVersionInfo()) > 0) || (l1 > localFileItem.getLastModified()) || (l2 > localFileItem.getOriginalSize()))
          {
            localFileItem.setOptions(paramString, str3, l1, l2, localEntry.getEntryId());
            localFileItem.setForceOverride(true);
            MFileCache.put(str2, localFileItem);
          }
        }
      }
    }
    catch (GoatException localGoatException)
    {
      this.mLog.warning(localGoatException.getLocalizedMessage());
    }
    catch (ARException localARException)
    {
      this.mLog.severe(localARException.getLocalizedMessage());
    }
  }

  private boolean isFileDownLoadable(File paramFile, FileItem paramFileItem)
  {
    return (!paramFile.exists()) || (paramFile.lastModified() < paramFileItem.getLastModified()) || (paramFile.length() < paramFileItem.getOriginalSize());
  }

  void downloadFile(File paramFile, FileItem paramFileItem)
  {
    String str1 = paramFileItem.getServer();
    try
    {
      ServerLogin localServerLogin = ServerLogin.getAdmin(str1);
      if (localServerLogin == null)
        return;
      String str2 = getSchema(localServerLogin);
      localServerLogin.getEntryBlob(str2, paramFileItem.getEntryID(), 41024, paramFile.getPath());
      File localFile = new File(paramFile.getPath());
      if (!localFile.exists())
      {
        this.mLog.warning("Error downloading the file - " + paramFileItem.getName());
        return;
      }
      if (OSNUM == 4)
        try
        {
          Runtime.getRuntime().exec("chmod 755 " + paramFile.getPath());
        }
        catch (IOException localIOException)
        {
          this.mLog.warning(localIOException.getMessage());
        }
      this.mLog.fine("Downloaded file - " + localFile.getName());
    }
    catch (GoatException localGoatException)
    {
      this.mLog.warning(localGoatException.getLocalizedMessage());
    }
    catch (ARException localARException)
    {
      this.mLog.severe(localARException.getLocalizedMessage());
    }
  }

  private void listFiles()
  {
    try
    {
      File[] arrayOfFile1 = MIDTIER_JAR_DIR.listFiles(new FileFilter()
      {
        public boolean accept(File paramAnonymousFile)
        {
          return (!paramAnonymousFile.isDirectory()) && (paramAnonymousFile.getName().endsWith(".jar"));
        }
      });
      if ((arrayOfFile1 == null) || (arrayOfFile1.length <= 0))
        return;
      for (File localFile : arrayOfFile1)
        if (localFile != null)
          this.mURLList.add(localFile.toURI().toURL());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      this.mLog.warning(localMalformedURLException.getLocalizedMessage());
    }
  }

  private void downLoadFiles()
  {
    if (!MIDTIER_JAR_DIR.exists())
      MIDTIER_JAR_DIR.mkdirs();
    Iterator localIterator = MFileCache.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((str != null) && (str.length() != 0))
      {
        FileItem localFileItem = (FileItem)MFileCache.get(str);
        if (localFileItem != null)
        {
          File localFile = null;
          if (str.endsWith(".jar") == true)
            localFile = new File(MIDTIER_JAR_DIR, str);
          else
            localFile = new File(MIDTIER_LIB_DIR, str);
          if (isFileDownLoadable(localFile, localFileItem))
          {
            if (localFile.exists())
              localFile.delete();
            downloadFile(localFile, localFileItem);
          }
        }
      }
    }
  }

  private boolean isServerAvailable(String paramString)
  {
    Iterator localIterator = mServerList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((str != null) && (str.length() != 0))
        if (str.equals(paramString))
          return true;
    }
    return false;
  }

  private void buildFileItemList()
  {
    List localList = Configuration.getInstance().getPluginServers();
    Iterator localIterator = localList.iterator();
    QualifierInfo localQualifierInfo1 = SYSFILE_QUAL;
    QualifierInfo localQualifierInfo2 = createTimestampQual(this.mTimestamp);
    if (localQualifierInfo2 != null)
      localQualifierInfo1 = new QualifierInfo(1, SYSFILE_QUAL, localQualifierInfo2);
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((str != null) && (str.length() != 0))
      {
        QualifierInfo localQualifierInfo3 = SYSFILE_QUAL;
        if (isServerAvailable(str))
          localQualifierInfo3 = localQualifierInfo1;
        else
          mServerList.add(str);
        buildFiles(str, localQualifierInfo3);
      }
    }
    this.mTimestamp.setValue(System.currentTimeMillis() / 1000L);
  }

  private class VersionInfo
  {
    private int mMajor;
    private int mMinor;
    private int mSubMinor;
    private int mPatch;
    private int mCode;

    public VersionInfo(int paramInt1, int paramInt2, int paramInt3, int arg5)
    {
      this.mMajor = paramInt1;
      this.mMinor = paramInt2;
      this.mSubMinor = paramInt3;
      int i;
      this.mPatch = i;
      this.mCode = (this.mMajor * 10000000 + this.mMinor * 100000 + this.mSubMinor * 1000 + this.mPatch);
    }

    public int getMajorVersion()
    {
      return this.mMajor;
    }

    public int getMinorVersion()
    {
      return this.mMinor;
    }

    public int getSubMinorversion()
    {
      return this.mSubMinor;
    }

    public int getPatch()
    {
      return this.mPatch;
    }

    public int getCode()
    {
      return this.mCode;
    }

    public int compare(VersionInfo paramVersionInfo)
    {
      return getCode() - paramVersionInfo.getCode();
    }
  }

  private class FileItem
  {
    String mVersion;
    String mName;
    long mLastModified;
    long mOriginalSize;
    String mServer;
    String mEntryID;
    ResourceService.VersionInfo mVersionInfo = null;
    boolean mForceOverride = false;

    FileItem(String paramString1, String paramString2, String paramLong1, long arg5, long arg7, String arg9)
    {
      this.mName = paramString1;
      this.mServer = paramString2;
      this.mVersion = paramLong1;
      this.mLastModified = ???;
      Object localObject1;
      this.mOriginalSize = localObject1;
      Object localObject2;
      this.mEntryID = localObject2;
      this.mVersionInfo = ResourceService.this.buildVersion(this.mVersion);
    }

    public void setOptions(String paramString1, String paramString2, long paramLong1, long paramLong2, String paramString3)
    {
      this.mServer = paramString1;
      this.mVersion = paramString2;
      this.mLastModified = paramLong1;
      this.mOriginalSize = paramLong2;
      this.mEntryID = paramString3;
      this.mVersionInfo = ResourceService.this.buildVersion(this.mVersion);
    }

    public String getEntryID()
    {
      return this.mEntryID;
    }

    public void setEntryID(String paramString)
    {
      this.mEntryID = paramString;
    }

    public String getName()
    {
      return this.mName;
    }

    public String getVersion()
    {
      return this.mVersion;
    }

    public void setVersion(String paramString)
    {
      this.mVersion = paramString;
      this.mVersionInfo = ResourceService.this.buildVersion(this.mVersion);
    }

    public String getServer()
    {
      return this.mServer;
    }

    public void setServer(String paramString)
    {
      this.mServer = paramString;
    }

    public long getLastModified()
    {
      return this.mLastModified;
    }

    public void setLastModified(long paramLong)
    {
      this.mLastModified = paramLong;
    }

    public long getOriginalSize()
    {
      return this.mOriginalSize;
    }

    public void setOriginalSize(long paramLong)
    {
      this.mOriginalSize = paramLong;
    }

    public ResourceService.VersionInfo getVersionInfo()
    {
      return this.mVersionInfo;
    }

    public boolean isForceOverridable()
    {
      return this.mForceOverride;
    }

    public void setForceOverride(boolean paramBoolean)
    {
      this.mForceOverride = paramBoolean;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ResourceService
 * JD-Core Version:    0.6.1
 */