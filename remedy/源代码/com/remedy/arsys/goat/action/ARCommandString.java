package com.remedy.arsys.goat.action;

import com.bmc.arsys.api.Field;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Keyword;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ARCommandString
  implements FormAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -8504931738385600648L;
  private static final Pattern KEYWORD_FIELD_REF_PAT = Pattern.compile("(\\$([^$\\s]+)\\$)");
  private static final Pattern NUMBER_PAT = Pattern.compile("[0-9-][0-9]*");
  private String cmdString;
  private List fieldRefs;
  private List keywordRefs;
  private boolean hasDBNames;

  public ARCommandString(String paramString)
  {
    this.cmdString = paramString;
    this.keywordRefs = new LinkedList();
    this.fieldRefs = new LinkedList();
    this.hasDBNames = false;
    Matcher localMatcher = KEYWORD_FIELD_REF_PAT.matcher(paramString);
    while (localMatcher.find())
    {
      String str1 = localMatcher.group(1);
      String str2 = localMatcher.group(2);
      if (NUMBER_PAT.matcher(str2).matches())
      {
        int i = (int)Long.parseLong(str2);
        if (i < 0)
          this.keywordRefs.add(Integer.valueOf(i));
        else
          this.fieldRefs.add(Integer.valueOf(i));
      }
      else
      {
        Keyword localKeyword = Keyword.getKeyword(str1);
        if (localKeyword != null)
        {
          this.keywordRefs.add(Integer.valueOf(localKeyword.getAsFieldID()));
        }
        else
        {
          this.hasDBNames = true;
          this.fieldRefs.add(localMatcher.group(2));
        }
      }
    }
    this.fieldRefs = Collections.unmodifiableList(this.fieldRefs);
    this.keywordRefs = Collections.unmodifiableList(this.keywordRefs);
  }

  public String getCommandString()
  {
    return this.cmdString;
  }

  public List getKeywordReferences()
  {
    return this.keywordRefs;
  }

  public void emitKeywordReferencesAsJSArray(JSWriter paramJSWriter)
  {
    emitAsJSArray(paramJSWriter, this.keywordRefs);
  }

  public String getKeywordReferencesAsJSArrayString()
  {
    JSWriter localJSWriter = new JSWriter();
    emitKeywordReferencesAsJSArray(localJSWriter);
    return localJSWriter.toString();
  }

  public List getFieldReferences()
  {
    return this.fieldRefs;
  }

  public void emitFieldReferencesAsJSArray(JSWriter paramJSWriter)
  {
    emitAsJSArray(paramJSWriter, this.fieldRefs);
  }

  public String getFieldReferencesAsJSArrayString()
  {
    JSWriter localJSWriter = new JSWriter();
    emitFieldReferencesAsJSArray(localJSWriter);
    return localJSWriter.toString();
  }

  private static void emitAsJSArray(JSWriter paramJSWriter, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    paramJSWriter.openList();
    while (localIterator.hasNext())
      paramJSWriter.listSep().append(localIterator.next().toString());
    paramJSWriter.closeList();
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    if (this.hasDBNames)
    {
      ArrayList localArrayList = new ArrayList(this.fieldRefs.size());
      Iterator localIterator = this.fieldRefs.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if ((localObject instanceof String))
        {
          String str = (String)localObject;
          Field localField = paramCachedFieldMap.getFieldByDBName(str);
          if (localField == null)
            throw new GoatException("Could not find the field reference in the form");
          localArrayList.add(Integer.valueOf(localField.getFieldID()));
        }
        else
        {
          assert ((localObject instanceof Integer));
          localArrayList.add(localObject);
        }
      }
      this.fieldRefs = Collections.unmodifiableList(localArrayList);
      this.hasDBNames = false;
    }
  }

  public Object clone()
  {
    try
    {
      ARCommandString localARCommandString = (ARCommandString)super.clone();
      if (this.hasDBNames)
      {
        localARCommandString.fieldRefs = new ArrayList(this.fieldRefs.size());
        localARCommandString.fieldRefs.addAll(this.fieldRefs);
        localARCommandString.fieldRefs = Collections.unmodifiableList(localARCommandString.fieldRefs);
      }
      return localARCommandString;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
    }
    return null;
  }

  public int hashCode()
  {
    return this.cmdString.hashCode();
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ARCommandString))
    {
      ARCommandString localARCommandString = (ARCommandString)paramObject;
      if (this.cmdString.equals(localARCommandString.cmdString))
        return this.fieldRefs.equals(localARCommandString.fieldRefs);
      return false;
    }
    return false;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.ARCommandString
 * JD-Core Version:    0.6.1
 */