package com.remedy.arsys.spellchecker;

import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.AttributeSource.AttributeFactory;

public class CSVTokenizer extends CharTokenizer
{
  private static final Character[] tokens = { Character.valueOf(','), Character.valueOf(';'), Character.valueOf('\n'), Character.valueOf('\r') };
  private static final Set<Character> tokenSet = new HashSet(Arrays.asList(tokens));

  public CSVTokenizer(AttributeSource.AttributeFactory paramAttributeFactory, Reader paramReader)
  {
    super(paramAttributeFactory, paramReader);
  }

  public CSVTokenizer(AttributeSource paramAttributeSource, Reader paramReader)
  {
    super(paramAttributeSource, paramReader);
  }

  public CSVTokenizer(Reader paramReader)
  {
    super(paramReader);
  }

  protected boolean isTokenChar(char paramChar)
  {
    return !tokenSet.contains(Character.valueOf(paramChar));
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.spellchecker.CSVTokenizer
 * JD-Core Version:    0.6.1
 */