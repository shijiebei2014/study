package com.remedy.arsys.goat.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class XMLWriter
{
  protected Stack<StringBuffer> mChildStack;
  protected StringBuffer mChildData;
  protected StringBuffer mBuffer;

  public XMLWriter()
  {
    this(new StringBuffer(16384));
  }

  public XMLWriter(StringBuffer paramStringBuffer)
  {
    this.mBuffer = paramStringBuffer;
    this.mChildData = new StringBuffer();
    this.mChildStack = new Stack();
  }

  public StringBuffer pushChildBuffer()
  {
    this.mChildStack.push(new StringBuffer(this.mChildData.toString()));
    this.mChildData = new StringBuffer();
    return this.mChildData;
  }

  public StringBuffer popChildBuffer()
  {
    if (!this.mChildStack.isEmpty())
      this.mChildData = ((StringBuffer)this.mChildStack.pop());
    return this.mChildData;
  }

  public final XMLWriter openWholeTag(String paramString)
  {
    return openWholeTag(paramString, (XMLWriter)null);
  }

  public final XMLWriter openWholeTag(String paramString, XMLWriter paramXMLWriter)
  {
    openTag(paramString);
    if (paramXMLWriter != null)
      append(paramXMLWriter.toString(), false);
    return endTag();
  }

  public final XMLWriter openTag(String paramString)
  {
    return openTag(paramString, -1);
  }

  public final XMLWriter openTag(String paramString, int paramInt)
  {
    if (paramInt != -1)
      this.mBuffer.insert(0, "<" + paramString);
    else
      this.mBuffer.append("<" + paramString);
    return this;
  }

  public final XMLWriter endTag()
  {
    return endTag(true);
  }

  public final XMLWriter endTag(boolean paramBoolean)
  {
    return append(">", paramBoolean);
  }

  public final XMLWriter closeOpenTag()
  {
    return closeOpenTag(true);
  }

  public final XMLWriter closeOpenTag(boolean paramBoolean)
  {
    append("/>", paramBoolean);
    return this;
  }

  public final XMLWriter closeTag(String paramString)
  {
    return closeTag(paramString, true);
  }

  public final XMLWriter closeTag(String paramString, boolean paramBoolean)
  {
    return closeTag(paramString, Boolean.valueOf(false), paramBoolean);
  }

  public final XMLWriter closeTag(String paramString, Boolean paramBoolean, boolean paramBoolean1)
  {
    if (!paramBoolean.booleanValue())
      endTag(true);
    commitChildDdata();
    return closeWholeTag(paramString, paramBoolean1);
  }

  public final XMLWriter closeWholeTag(String paramString)
  {
    return closeWholeTag(paramString, true);
  }

  public final XMLWriter closeWholeTag(String paramString, boolean paramBoolean)
  {
    return append("</" + paramString + ">", paramBoolean);
  }

  public final XMLWriter attr(String paramString, Object paramObject)
  {
    if (paramObject != null)
    {
      this.mBuffer.append(" ");
      if (paramString != null)
        this.mBuffer.append(paramString).append("=").append("\"");
      this.mBuffer.append(escape(String.valueOf(paramObject)));
      if (paramString != null)
        this.mBuffer.append("\"");
    }
    return this;
  }

  public final XMLWriter attr(Object paramObject)
  {
    return attr(null, paramObject);
  }

  public final XMLWriter cdata(String paramString, Object paramObject)
  {
    if ((paramObject != null) && (paramObject != ""))
      appendChildDdata(paramString, "<![CDATA[" + paramObject + "]]>");
    return this;
  }

  public final XMLWriter cdata(Object paramObject)
  {
    return cdata(null, paramObject);
  }

  public final XMLWriter addChild(String paramString, XMLWriter paramXMLWriter)
  {
    if (paramXMLWriter != null)
    {
      paramXMLWriter.openTag(paramString, 0);
      if (paramXMLWriter.mChildData.length() > 0)
        paramXMLWriter.closeTag(paramString);
      else
        paramXMLWriter.closeOpenTag();
      if (paramXMLWriter.length() > 0)
        this.mChildData.append(paramXMLWriter.toString());
    }
    return this;
  }

  public final XMLWriter appendChildDdata(String paramString)
  {
    return appendChildDdata(paramString, null);
  }

  public final XMLWriter appendChildDdata(String paramString, Object paramObject)
  {
    return appendChildDdata(paramString, paramObject, null);
  }

  public final XMLWriter appendChildDdata(String paramString, Object paramObject, XMLWriter paramXMLWriter)
  {
    if ((paramObject != null) && (paramObject != ""))
    {
      if (paramString != null)
      {
        this.mChildData.append("<" + paramString);
        if (paramXMLWriter != null)
          this.mChildData.append(paramXMLWriter.toString());
        this.mChildData.append(">\n");
      }
      this.mChildData.append(paramObject);
      if (paramString != null)
        this.mChildData.append("\n</" + paramString + ">\n");
    }
    else
    {
      this.mChildData.append("<" + paramString + "/>\n");
    }
    return this;
  }

  public final XMLWriter commitChildDdata()
  {
    if (this.mChildData.length() > 0)
      append(this.mChildData.toString(), false);
    this.mChildData = new StringBuffer();
    return this;
  }

  public final XMLWriter append(String paramString)
  {
    return append(paramString, true);
  }

  public final XMLWriter append(String paramString, boolean paramBoolean)
  {
    this.mBuffer.append(paramString);
    if (paramBoolean)
      this.mBuffer.append("\n");
    return this;
  }

  public static final String escape(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString.length());
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '&':
        localStringBuffer.append(arrayOfChar, i, j - i);
        localStringBuffer.append("&amp;");
        i = j + 1;
        break;
      case '<':
        localStringBuffer.append(arrayOfChar, i, j - i);
        localStringBuffer.append("&lt;");
        i = j + 1;
        break;
      case '>':
        localStringBuffer.append(arrayOfChar, i, j - i);
        localStringBuffer.append("&gt;");
        i = j + 1;
        break;
      case '"':
        localStringBuffer.append(arrayOfChar, i, j - i);
        localStringBuffer.append("&quot;");
        i = j + 1;
      }
    localStringBuffer.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuffer.toString();
  }

  public int length()
  {
    return this.mBuffer.length() + this.mChildData.length();
  }

  public String toString()
  {
    return this.mBuffer.toString();
  }

  /** @deprecated */
  public final XMLWriter cdataSpaceEncoded(Object paramObject)
  {
    return cdata(paramObject);
  }

  /** @deprecated */
  public final XMLWriter property(Object paramObject1, Object paramObject2)
  {
    return cdata((String)paramObject1, paramObject2);
  }

  /** @deprecated */
  public final XMLWriter propertyDestinedForHTML(Object paramObject1, Object paramObject2)
  {
    return cdata((String)paramObject1, paramObject2);
  }

  /** @deprecated */
  public final XMLWriter openList()
  {
    return this;
  }

  /** @deprecated */
  public final XMLWriter closeList()
  {
    return this;
  }

  /** @deprecated */
  public final XMLWriter openObj()
  {
    return this;
  }

  /** @deprecated */
  public final XMLWriter closeObj()
  {
    return this;
  }

  /** @deprecated */
  public final XMLWriter listSep()
  {
    return this;
  }

  public final XMLWriter openWholeTag(String paramString, Map<String, Object> paramMap, boolean paramBoolean1, boolean paramBoolean2)
  {
    assert ((paramString != null) && (paramString.length() > 0));
    openTag(paramString);
    if (paramMap != null)
    {
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = paramMap.get(str);
        attr(str, localObject);
      }
    }
    if (paramBoolean1)
      this.mBuffer.append('/');
    endTag(paramBoolean2);
    return this;
  }

  public final XMLWriter openWholeTag(String paramString, Map<String, Object> paramMap)
  {
    return openWholeTag(paramString, paramMap, false, true);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.service.XMLWriter
 * JD-Core Version:    0.6.1
 */