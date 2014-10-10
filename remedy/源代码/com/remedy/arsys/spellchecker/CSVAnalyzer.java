package com.remedy.arsys.spellchecker;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public final class CSVAnalyzer extends Analyzer
{
  public TokenStream tokenStream(String paramString, Reader paramReader)
  {
    return new CSVTokenizer(paramReader);
  }

  public TokenStream reusableTokenStream(String paramString, Reader paramReader)
    throws IOException
  {
    Object localObject = (Tokenizer)getPreviousTokenStream();
    if (localObject == null)
    {
      localObject = new CSVTokenizer(paramReader);
      setPreviousTokenStream(localObject);
    }
    else
    {
      ((Tokenizer)localObject).reset(paramReader);
    }
    return localObject;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.spellchecker.CSVAnalyzer
 * JD-Core Version:    0.6.1
 */