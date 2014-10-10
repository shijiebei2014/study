package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;

public class WebWriter
{
  protected StringBuilder mBuffer;
  protected int mIndent;
  protected boolean mNeedIndent;
  protected boolean mNoComments;
  protected boolean mStripWhitespace;
  protected int mIndentDepth;

  protected WebWriter()
  {
    this.mBuffer = new StringBuilder(16384);
    setDefaults();
  }

  protected WebWriter(StringBuilder paramStringBuilder)
  {
    this.mBuffer = paramStringBuilder;
    setDefaults();
  }

  protected WebWriter(WebWriter paramWebWriter)
  {
    this.mBuffer = paramWebWriter.mBuffer;
    setDefaults();
  }

  protected WebWriter(int paramInt)
  {
    this.mBuffer = new StringBuilder(paramInt);
    setDefaults();
  }

  protected void getConfigDefaults()
  {
    Configuration localConfiguration = Configuration.getInstance();
    this.mNoComments = (!localConfiguration.getWebWriterComments());
    this.mStripWhitespace = (!localConfiguration.getWebWriterWhitespace());
    this.mIndentDepth = localConfiguration.getWebWriterIndent();
  }

  protected void clearIndentParms()
  {
    this.mIndent = 0;
    this.mNeedIndent = true;
  }

  protected void setDefaults()
  {
    getConfigDefaults();
    clearIndentParms();
  }

  public final void clear()
  {
    this.mBuffer.setLength(0);
    clearIndentParms();
  }

  public final void setParams(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    this.mNoComments = paramBoolean1;
    this.mStripWhitespace = paramBoolean2;
    this.mIndentDepth = paramInt;
  }

  public final void increaseIndent()
  {
    this.mIndent += 1;
  }

  public final void decreaseIndent()
  {
    this.mIndent -= 1;
    if (this.mIndent < 0)
      this.mIndent = 0;
  }

  protected void indent()
  {
    if ((this.mNeedIndent) && (!this.mStripWhitespace))
      for (int i = 0; i < this.mIndent * this.mIndentDepth; i++)
        this.mBuffer.append(' ');
    this.mNeedIndent = false;
  }

  public void eol()
  {
    this.mBuffer.append("\n");
    this.mNeedIndent = true;
  }

  public static final String escapeString(String paramString)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length() + 2);
    localStringBuilder.append('"');
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '\\':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\\");
        i = j + 1;
        break;
      case '"':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\"");
        i = j + 1;
        break;
      case '\n':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\n");
        i = j + 1;
        break;
      case '\r':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\r");
        i = j + 1;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    localStringBuilder.append('"');
    return localStringBuilder.toString();
  }

  public static final String escapeStringAMF(String paramString)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length() + 2);
    localStringBuilder.append('"');
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '\\':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\\\\\\");
        i = j + 1;
        break;
      case '"':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\\"");
        i = j + 1;
        break;
      case '\n':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\n");
        i = j + 1;
        break;
      case '\r':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\r");
        i = j + 1;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    localStringBuilder.append('"');
    return localStringBuilder.toString();
  }

  public WebWriter appendStr(String paramString)
  {
    this.mBuffer.append(paramString);
    return this;
  }

  public WebWriter appendStr(StringBuilder paramStringBuilder)
  {
    this.mBuffer.append(paramStringBuilder);
    return this;
  }

  public WebWriter append(WebWriter paramWebWriter)
  {
    this.mBuffer.append(paramWebWriter.mBuffer);
    return this;
  }

  public String toString()
  {
    return this.mBuffer.toString();
  }

  public int length()
  {
    return this.mBuffer.length();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.WebWriter
 * JD-Core Version:    0.6.1
 */