package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.util.ArrayList;

abstract class NDXSpellChecker extends XMLRequestBase
{
  protected String[] mWords;
  protected String mLocale;

  NDXSpellChecker(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> SpellChecker");
    if (paramArrayList.size() != 2)
      throw new GoatException("Wrong argument length, spoofed");
    this.mWords = argToStringArray((String)paramArrayList.get(0));
    StringBuilder localStringBuilder = new StringBuilder("mWords=");
    for (int i = 0; i < this.mWords.length; i++)
    {
      if (i > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mWords[i]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mLocale = ((String)paramArrayList.get(1));
    MLog.fine("mLocale=" + this.mLocale);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXSpellChecker
 * JD-Core Version:    0.6.1
 */