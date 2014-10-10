package com.remedy.arsys.spellchecker;

import com.remedy.arsys.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

class WordExtractor
{
  private static final Log logger = Log.get(2);
  private static final String LAST_UPDATED_FILENAME = "lastupdated.properties";
  private static final Map<String, File> indexedFolders = new HashMap();
  private IndexWriter writer;
  private Directory ramDir;
  private String propertyFilename;

  public WordExtractor(String paramString)
    throws IOException
  {
    assert ((paramString != null) && (paramString.length() > 0));
    this.propertyFilename = (paramString + File.separatorChar + "lastupdated.properties");
    this.ramDir = new RAMDirectory();
    this.writer = new IndexWriter(this.ramDir, new CSVAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
  }

  private void close()
    throws IOException
  {
    this.writer.commit();
    this.writer.close();
  }

  public IndexReader extract(String paramString)
    throws Exception
  {
    synchronized (getFileForLocking(paramString))
    {
      Properties localProperties = new Properties();
      FileInputStream localFileInputStream = null;
      try
      {
        File localFile1 = new File(this.propertyFilename);
        if (localFile1.exists())
        {
          try
          {
            localFileInputStream = new FileInputStream(localFile1);
            localProperties.load(localFileInputStream);
          }
          catch (IOException localIOException1)
          {
            logger.log(Level.SEVERE, "Unable to open the Spell Checker property file '" + this.propertyFilename + "' for reading.");
            throw localIOException1;
          }
          finally
          {
            if (localFileInputStream != null)
              try
              {
                localFileInputStream.close();
              }
              catch (IOException localIOException3)
              {
              }
          }
        }
        else
        {
          localObject1 = localFile1.getParentFile();
          if ((localObject1 != null) && (!((File)localObject1).exists()))
            ((File)localObject1).mkdirs();
        }
        Object localObject1 = new File(paramString).listFiles();
        if (localObject1 != null)
          for (File localFile2 : localObject1)
            if ((!localFile2.isDirectory()) && (!localFile2.isHidden()) && (localFile2.exists()) && (localFile2.canRead()) && (toDo(localProperties, localFile2)))
              indexFile(localFile2);
        ??? = null;
        try
        {
          ??? = new FileOutputStream(localFile1);
          localProperties.store((OutputStream)???, null);
        }
        catch (IOException localIOException2)
        {
          logger.log(Level.SEVERE, "Unable to open the Spell Checker property file '" + this.propertyFilename + "' for writing.");
          throw localIOException2;
        }
        finally
        {
          if (??? != null)
            try
            {
              ((FileOutputStream)???).close();
            }
            catch (IOException localIOException4)
            {
            }
        }
      }
      finally
      {
        close();
      }
    }
    return IndexReader.open(this.ramDir, false);
  }

  private static File getFileForLocking(String paramString)
  {
    assert ((paramString != null) && (paramString.length() > 0));
    File localFile = null;
    synchronized (indexedFolders)
    {
      if (!indexedFolders.containsKey(paramString))
        indexedFolders.put(paramString, new File(paramString));
      localFile = (File)indexedFolders.get(paramString);
    }
    return localFile;
  }

  private boolean toDo(Properties paramProperties, File paramFile)
  {
    boolean bool = false;
    String str1 = paramFile.getName();
    String str2 = paramProperties.getProperty(str1);
    if (str2 == null)
    {
      bool = true;
      paramProperties.setProperty(str1, "" + paramFile.lastModified());
    }
    else if (!str2.equals("" + paramFile.lastModified()))
    {
      bool = true;
      paramProperties.setProperty(str1, "" + paramFile.lastModified());
    }
    return bool;
  }

  private void indexFile(File paramFile)
    throws Exception
  {
    Document localDocument = new Document();
    FileReader localFileReader = new FileReader(paramFile);
    try
    {
      localDocument.add(new Field("contents", localFileReader));
      this.writer.addDocument(localDocument);
    }
    finally
    {
      localFileReader.close();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.spellchecker.WordExtractor
 * JD-Core Version:    0.6.1
 */