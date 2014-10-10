package com.remedy.arsys.backchannel;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.service.XMLWriter;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.spellchecker.DirDictionary;
import com.remedy.arsys.stubs.SessionData;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.NIOFSDirectory;

public class SpellCheckerAgent extends NDXSpellChecker
{
  private static final String ELEMENT_WORD = "word";
  private static final String ELEMENT_WORDS = "words";
  private static final String ELEMENT_SUGGEST = "suggest";
  private static final String ATTRIBUTE_WORD = "word";
  private static final String SPELL_CHECKER_PATH = "SpellChecker";

  public SpellCheckerAgent(String paramString)
  {
    super(paramString);
  }

  protected void process(XMLWriter paramXMLWriter)
    throws GoatException
  {
    append("this.result={");
    SpellChecker localSpellChecker = null;
    try
    {
      String str1 = Configuration.getInstance().getRootPath() + File.separatorChar + "SpellChecker";
      append("locale: ").appendqs(getLocale());
      append(", xml: ");
      DirDictionary localDirDictionary = new DirDictionary(getLocale(), str1);
      NIOFSDirectory localNIOFSDirectory = new NIOFSDirectory(new File(localDirDictionary.getIndexPath()));
      localSpellChecker = new SpellChecker(localNIOFSDirectory, new JaroWinklerDistance());
      localSpellChecker.indexDictionary(localDirDictionary);
      if ((this.mWords != null) && (this.mWords.length > 0))
      {
        paramXMLWriter.openWholeTag("words");
        HashMap localHashMap = null;
        HashSet localHashSet = new HashSet(Arrays.asList(this.mWords));
        Iterator localIterator = localHashSet.iterator();
        while (localIterator.hasNext())
        {
          String str2 = ((String)localIterator.next()).trim();
          if ((str2 != null) && (str2.length() > 2) && (!StandardAnalyzer.STOP_WORDS_SET.contains(str2)) && (!localSpellChecker.exist(str2)) && (!localSpellChecker.exist(str2.toLowerCase())) && (!localSpellChecker.exist(str2.replaceAll("[’]", "'"))))
          {
            boolean bool = Character.isUpperCase(str2.charAt(0));
            localHashMap = new HashMap(1);
            localHashMap.put("word", str2);
            paramXMLWriter.openWholeTag("word", localHashMap);
            String[] arrayOfString = localSpellChecker.suggestSimilar(str2, 20);
            if ((arrayOfString != null) && (arrayOfString.length > 0))
              for (int i = 0; i < arrayOfString.length; i++)
              {
                String str3 = arrayOfString[i];
                if (str3.length() != 0)
                {
                  if (bool)
                    if (str3.length() > 1)
                    {
                      char c = Character.toUpperCase(str3.charAt(0));
                      str3 = c + str3.substring(1);
                    }
                    else
                    {
                      str3 = str3.toUpperCase();
                    }
                  localHashMap = new HashMap(1);
                  localHashMap.put("word", str3);
                  paramXMLWriter.openWholeTag("suggest", localHashMap, true, true);
                }
              }
            paramXMLWriter.closeWholeTag("word");
          }
        }
        paramXMLWriter.closeWholeTag("words");
        appendqs(paramXMLWriter.toString());
      }
      else
      {
        appendqs("");
      }
    }
    catch (Exception localException1)
    {
      MLog.fineAndConsole("Service: exception using Lucene in Spell Checker");
      throw new GoatException("Unable to execute the Spell Checker on the specified words.", localException1);
    }
    finally
    {
      if (localSpellChecker != null)
      {
        try
        {
          localSpellChecker.close();
        }
        catch (Exception localException2)
        {
        }
        localSpellChecker = null;
      }
    }
    append("};");
  }

  protected String getLocale()
  {
    if ((this.mLocale == null) || (this.mLocale.length() == 0))
      this.mLocale = SessionData.get().getLocale();
    return this.mLocale;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.SpellCheckerAgent
 * JD-Core Version:    0.6.1
 */