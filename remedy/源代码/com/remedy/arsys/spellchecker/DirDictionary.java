package com.remedy.arsys.spellchecker;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;

public class DirDictionary
  implements Dictionary
{
  static final String LUCENE_DICTIONARY_FIELD_NAME = "contents";
  private static final String INDEX_SUB_PATH = "index";
  private static final String DICTIONARY_SUB_PATH = "dictionary";
  private static final String DEFAULT_LOCALE_NAME = "default";
  private LuceneDictionary luceneDictionary;
  private String indexPath;
  private String dictionaryPath;

  public DirDictionary(String paramString1, String paramString2)
    throws IOException, Exception
  {
    assert ((paramString1 != null) && (paramString1.length() > 0));
    assert ((paramString2 != null) && (paramString2.length() > 0));
    this.dictionaryPath = buildDictionaryPath(paramString2, paramString1);
    if (!checkFolderExists(this.dictionaryPath))
    {
      paramString1 = "default";
      this.dictionaryPath = buildDictionaryPath(paramString2, paramString1);
    }
    this.indexPath = buildIndexPath(paramString2, paramString1);
    WordExtractor localWordExtractor = new WordExtractor(this.indexPath);
    IndexReader localIndexReader = localWordExtractor.extract(this.dictionaryPath);
    this.luceneDictionary = new LuceneDictionary(localIndexReader, "contents");
  }

  private boolean checkFolderExists(String paramString)
  {
    return new File(paramString).isDirectory();
  }

  private String buildIndexPath(String paramString1, String paramString2)
  {
    return paramString1 + File.separatorChar + "index" + File.separatorChar + paramString2;
  }

  private String buildDictionaryPath(String paramString1, String paramString2)
  {
    return paramString1 + File.separatorChar + "dictionary" + File.separatorChar + paramString2;
  }

  public Iterator<String> getWordsIterator()
  {
    return this.luceneDictionary.getWordsIterator();
  }

  public String getIndexPath()
  {
    return this.indexPath;
  }

  public String getDictionaryPath()
  {
    return this.dictionaryPath;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.spellchecker.DirDictionary
 * JD-Core Version:    0.6.1
 */