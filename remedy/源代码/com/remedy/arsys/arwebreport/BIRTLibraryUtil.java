package com.remedy.arsys.arwebreport;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.log.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BIRTLibraryUtil
{
  private static Log mLog = Log.get(0);
  private static final String AR_SYSTEM_RESOURCE_DEFINITIONS = "AR System Resource Definitions";
  private static final String BIRT_LIBRARY = "BIRT Library";
  private static final int TYPE_CONSTANT = 2;
  private static final int FIELD_ATTACHMENT = 41103;
  private static final int FIELD_NAME = 41100;
  private static final int FIELD_TYPE = 41105;
  private static final String TIMESTAMP_FILE = ".timestamp";

  public static void main(String[] paramArrayOfString)
  {
    BIRTLibraryUtil localBIRTLibraryUtil = new BIRTLibraryUtil();
    String str1 = "c:/temp/libraries/libtimestamp";
    String str2 = "c:/temp/libraries";
    ARServerUser localARServerUser = new ARServerUser("Demo", "", null, "localhost");
    try
    {
      updateLibraries(localARServerUser, str2);
    }
    catch (ARException localARException)
    {
      mLog.log(Level.SEVERE, localARException.getMessage(), localARException);
    }
  }

  public static void updateLibraries(ARServerUser paramARServerUser, String paramString)
    throws ARException
  {
    String str1 = paramString + "/" + paramARServerUser.getServer();
    File localFile = new File(str1);
    if (!localFile.exists())
      localFile.mkdirs();
    String str2 = str1 + "/" + paramARServerUser.getServer() + ".timestamp";
    Timestamp localTimestamp = getLastTimestamp(str2);
    List localList = getLibraryList(paramARServerUser, localTimestamp);
    if ((localList != null) && (!localList.isEmpty()))
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        getLibrary(paramARServerUser, str1, localEntry);
      }
    }
    updateLastTimestamp(str2);
  }

  private static void unpackLibrary(String paramString, byte[] paramArrayOfByte)
  {
    ZipInputStream localZipInputStream = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(paramArrayOfByte)));
    ZipEntry localZipEntry = null;
    try
    {
      while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
      {
        if (localZipEntry.isDirectory())
        {
          File localFile = new File(paramString + "/" + localZipEntry.getName());
          if (!localFile.exists())
            localFile.mkdir();
        }
        else
        {
          FileOutputStream localFileOutputStream = null;
          byte[] arrayOfByte = new byte[1024];
          String str1 = paramString + "/" + localZipEntry.getName();
          int j = str1.lastIndexOf("/");
          if (j > 0)
          {
            String str2 = str1.substring(0, j);
            new File(str2).mkdirs();
          }
          localFileOutputStream = new FileOutputStream(paramString + "/" + localZipEntry.getName());
          int i;
          while ((i = localZipInputStream.read(arrayOfByte, 0, 1024)) > -1)
            localFileOutputStream.write(arrayOfByte, 0, i);
          localFileOutputStream.close();
        }
        localZipInputStream.closeEntry();
      }
      localZipInputStream.close();
    }
    catch (IOException localIOException)
    {
      mLog.log(Level.SEVERE, localIOException.getMessage(), localIOException);
    }
  }

  private static void getLibrary(ARServerUser paramARServerUser, String paramString, Entry paramEntry)
    throws ARException
  {
    if (paramEntry != null)
    {
      Value localValue = (Value)paramEntry.get(Integer.valueOf(41103));
      if ((localValue != null) && (localValue.getValue() != null) && (DataType.ATTACHMENT.equals(localValue.getDataType())))
      {
        AttachmentValue localAttachmentValue = (AttachmentValue)localValue.getValue();
        if (localAttachmentValue != null)
        {
          localAttachmentValue.setValue(new byte[0]);
          byte[] arrayOfByte = paramARServerUser.getEntryBlob("AR System Resource Definitions", paramEntry.getEntryId(), 41103);
          if ((arrayOfByte != null) && (localAttachmentValue.getName() != null))
          {
            String str1 = localAttachmentValue.getName();
            if ((str1.contains("/")) || (str1.contains("\\")))
            {
              str1 = str1.replace("\\", "/");
              str1 = str1.substring(str1.lastIndexOf("/") + 1);
            }
            if ((str1.toLowerCase().endsWith(".rptlibrary")) || (str1.toLowerCase().endsWith(".rpttemplate")))
            {
              String str2 = paramString + "/" + str1;
              writeToFile(str2, arrayOfByte);
            }
            else if (str1.toLowerCase().endsWith(".zip"))
            {
              unpackLibrary(paramString, arrayOfByte);
            }
          }
        }
      }
    }
  }

  private static void writeToFile(String paramString, byte[] paramArrayOfByte)
  {
    FileOutputStream localFileOutputStream = null;
    try
    {
      localFileOutputStream = new FileOutputStream(paramString);
      localFileOutputStream.write(paramArrayOfByte);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      mLog.log(Level.SEVERE, localFileNotFoundException.getMessage(), localFileNotFoundException);
    }
    catch (IOException localIOException1)
    {
      mLog.log(Level.SEVERE, localIOException1.getMessage(), localIOException1);
    }
    finally
    {
      if (localFileOutputStream != null)
        try
        {
          localFileOutputStream.close();
        }
        catch (IOException localIOException2)
        {
          mLog.log(Level.SEVERE, localIOException2.getMessage(), localIOException2);
        }
    }
  }

  private static List<Entry> getLibraryList(ARServerUser paramARServerUser, Timestamp paramTimestamp)
    throws ARException
  {
    QualifierInfo localQualifierInfo = paramARServerUser.parseQualification("AR System Resource Definitions", "'Type' = \"BIRT Library\"  AND 'Status' = \"Active\" AND '6' > " + paramTimestamp.getValue());
    int[] arrayOfInt = { 41105, 41100, 41103 };
    return paramARServerUser.getListEntryObjects("AR System Resource Definitions", localQualifierInfo, 0, 0, null, arrayOfInt, false, null);
  }

  private static Timestamp getLastTimestamp(String paramString)
  {
    Timestamp localTimestamp = new Timestamp(0L);
    if (new File(paramString).exists())
    {
      DataInputStream localDataInputStream = null;
      try
      {
        localDataInputStream = new DataInputStream(new FileInputStream(paramString));
        long l = localDataInputStream.readLong();
        localTimestamp = new Timestamp(l);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        mLog.log(Level.SEVERE, localFileNotFoundException.getMessage(), localFileNotFoundException);
      }
      catch (IOException localIOException1)
      {
        mLog.log(Level.SEVERE, localIOException1.getMessage(), localIOException1);
      }
      finally
      {
        if (localDataInputStream != null)
          try
          {
            localDataInputStream.close();
          }
          catch (IOException localIOException2)
          {
            mLog.log(Level.SEVERE, localIOException2.getMessage(), localIOException2);
          }
      }
    }
    return localTimestamp;
  }

  private static void updateLastTimestamp(String paramString)
  {
    DataOutputStream localDataOutputStream = null;
    try
    {
      localDataOutputStream = new DataOutputStream(new FileOutputStream(paramString));
      localDataOutputStream.writeLong(new Timestamp(new Date()).getValue());
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      mLog.log(Level.SEVERE, localFileNotFoundException.getMessage(), localFileNotFoundException);
    }
    catch (IOException localIOException1)
    {
      mLog.log(Level.SEVERE, localIOException1.getMessage(), localIOException1);
    }
    finally
    {
      if (localDataOutputStream != null)
        try
        {
          localDataOutputStream.close();
        }
        catch (IOException localIOException2)
        {
          mLog.log(Level.SEVERE, localIOException2.getMessage(), localIOException2);
        }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.arwebreport.BIRTLibraryUtil
 * JD-Core Version:    0.6.1
 */