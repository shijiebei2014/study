package com.remedy.arsys.share;

import com.remedy.arsys.support.Validator;
import java.util.Iterator;
import java.util.List;

public class HTMLWriter extends WebWriter
{
  private static final String STYLE = "style";
  private static final String CLASS = "class";

  public HTMLWriter()
  {
  }

  public HTMLWriter(StringBuilder paramStringBuilder)
  {
    super(paramStringBuilder);
  }

  public HTMLWriter(HTMLWriter paramHTMLWriter)
  {
    super(paramHTMLWriter);
  }

  public HTMLWriter(int paramInt)
  {
    super(paramInt);
  }

  public HTMLWriter createInstance()
  {
    return new HTMLWriter();
  }

  public final HTMLWriter openWholeTag(String paramString)
  {
    indent();
    this.mBuffer.append("<").append(paramString).append(">");
    eol();
    return this;
  }

  public final HTMLWriter openTag(String paramString)
  {
    indent();
    this.mBuffer.append("<" + paramString);
    increaseIndent();
    return this;
  }

  public HTMLWriter openTag(String paramString, int paramInt)
  {
    return openTag(paramString);
  }

  public final HTMLWriter attr(String paramString)
  {
    this.mBuffer.append(" ");
    this.mBuffer.append(paramString);
    return this;
  }

  public final HTMLWriter attr(String paramString1, String paramString2)
  {
    assert (paramString2 != null);
    this.mBuffer.append(" ").append(paramString1).append("=");
    this.mBuffer.append("\"").append(escape(paramString2)).append("\"");
    return this;
  }

  public final HTMLWriter attrnoesc(String paramString1, String paramString2)
  {
    assert (paramString2 != null);
    this.mBuffer.append(" ").append(paramString1).append("=");
    this.mBuffer.append("\"").append(paramString2).append("\"");
    return this;
  }

  public final HTMLWriter attr(String paramString, int paramInt)
  {
    this.mBuffer.append(" ").append(paramString).append("=").append("" + paramInt);
    return this;
  }

  public final HTMLWriter attr(String paramString, long paramLong)
  {
    this.mBuffer.append(" ").append(paramString).append("=").append("" + paramLong);
    return this;
  }

  public HTMLWriter attr(String paramString, Object paramObject)
  {
    return attr(paramString, (String)paramObject);
  }

  public final HTMLWriter attrStyle(List<String> paramList)
  {
    this.mBuffer.append(" ").append("style").append("='");
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((str != null) && (str.length() > 0))
        this.mBuffer.append(str).append("; ");
    }
    this.mBuffer.append("'");
    return this;
  }

  public final HTMLWriter attrClass(String paramString)
  {
    return attr("class", paramString);
  }

  public final HTMLWriter condAttr(boolean paramBoolean, String paramString, int paramInt1, int paramInt2)
  {
    if (paramBoolean)
      attr(paramString, paramInt1);
    else
      attr(paramString, paramInt2);
    return this;
  }

  public final HTMLWriter condAttr(boolean paramBoolean, String paramString1, String paramString2, String paramString3)
  {
    if (paramBoolean)
      attr(paramString1, paramString2);
    else
      attr(paramString1, paramString3);
    return this;
  }

  public final HTMLWriter comment(String paramString)
  {
    if (!this.mNoComments)
    {
      indent();
      this.mBuffer.append("<!-- " + paramString + "-->");
      eol();
    }
    return this;
  }

  public final HTMLWriter cdata(String paramString)
  {
    this.mBuffer.append(escape(paramString));
    return this;
  }

  public HTMLWriter cdata(Object paramObject)
  {
    return cdata((String)paramObject);
  }

  public HTMLWriter cdata(String paramString, Object paramObject)
  {
    openWholeTag(paramString);
    cdata(paramObject);
    closeTag(paramString);
    return this;
  }

  public final HTMLWriter cdataSpaceEncoded(String paramString)
  {
    this.mBuffer.append(escapeSpace(paramString));
    return this;
  }

  public HTMLWriter addChild(String paramString, HTMLWriter paramHTMLWriter)
  {
    return append(paramHTMLWriter.getChildData().toString());
  }

  public StringBuilder getChildData()
  {
    return this.mBuffer;
  }

  public final HTMLWriter endTag(boolean paramBoolean)
  {
    this.mBuffer.append(">");
    if (paramBoolean)
      eol();
    return this;
  }

  public final HTMLWriter endTag()
  {
    return endTag(true);
  }

  public final HTMLWriter closeTag(String paramString)
  {
    return closeTag(paramString, true);
  }

  public final HTMLWriter closeTag(String paramString, boolean paramBoolean)
  {
    decreaseIndent();
    indent();
    this.mBuffer.append("</" + paramString + ">");
    if (paramBoolean)
      eol();
    return this;
  }

  public final HTMLWriter append(String paramString)
  {
    this.mBuffer.append(paramString);
    return this;
  }

  public final HTMLWriter endCloseTag(String paramString)
  {
    endTag();
    closeTag(paramString);
    return this;
  }

  public final HTMLWriter endCloseTag(String paramString, boolean paramBoolean)
  {
    endTag();
    closeTag(paramString, paramBoolean);
    return this;
  }

  public final HTMLWriter closeOpenTag()
  {
    return closeOpenTag(true);
  }

  public final HTMLWriter closeOpenTag(boolean paramBoolean)
  {
    decreaseIndent();
    if (this.mBuffer.charAt(this.mBuffer.length() - 1) != ' ')
      this.mBuffer.append(" ");
    this.mBuffer.append("/>");
    if (paramBoolean)
      eol();
    return this;
  }

  public static final String escape(String paramString)
  {
    return Validator.escape(paramString);
  }

  public static final String escapeSpace(String paramString)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '&':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&amp;");
        i = j + 1;
        break;
      case '<':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&lt;");
        i = j + 1;
        break;
      case '>':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&gt;");
        i = j + 1;
        break;
      case '"':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&quot;");
        i = j + 1;
        break;
      case '\n':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("<br>");
        i = j + 1;
        break;
      case ' ':
        if ((j > 0) && (arrayOfChar[(j - 1)] == ' '))
        {
          localStringBuilder.append(arrayOfChar, i, j - i);
          localStringBuilder.append("&nbsp;");
          i = j + 1;
        }
        break;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.HTMLWriter
 * JD-Core Version:    0.6.1
 */