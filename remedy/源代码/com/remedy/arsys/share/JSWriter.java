package com.remedy.arsys.share;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JSWriter extends WebWriter
{
  private static Log MLog = Log.get(11);
  public static final String[] EmptyString = new String[0];
  private boolean mNeedSemicolon;
  private boolean mEndStatement;
  private int mEatCharacters;
  private boolean mInterruptible;
  private ArrayList mStatements;
  private int mComments;
  private int mNumStatements;
  private static boolean mWorkflowVerifyImode = Configuration.getInstance().getWorkflowVerifyImode();
  private List mMultiBufferList;
  private boolean mMultiLineMode;
  protected OutputStream outStream = null;

  public JSWriter()
  {
  }

  public JSWriter(StringBuilder paramStringBuilder)
  {
    super(paramStringBuilder);
  }

  public JSWriter(HTMLWriter paramHTMLWriter)
  {
    super(paramHTMLWriter);
  }

  public JSWriter(int paramInt)
  {
    super(paramInt);
  }

  public JSWriter(OutputStream paramOutputStream)
  {
    this.outStream = paramOutputStream;
  }

  public JSWriter(boolean paramBoolean)
  {
    this.mMultiLineMode = paramBoolean;
    if (this.mMultiLineMode)
      this.mMultiBufferList = new LinkedList();
  }

  protected void setDefaults()
  {
    Configuration localConfiguration = Configuration.getInstance();
    this.mNoComments = (!localConfiguration.getJSWriterComments());
    this.mStripWhitespace = (!localConfiguration.getJSWriterWhitespace());
    this.mIndentDepth = localConfiguration.getJSWriterIndent();
    this.mIndent = 0;
    this.mNeedIndent = true;
    this.mEatCharacters = 0;
  }

  public final void eol()
  {
    if (this.mMultiLineMode)
    {
      if (this.mMultiBufferList != null)
        this.mMultiBufferList.add(this.mBuffer.toString());
      this.mBuffer = new StringBuilder();
    }
    else
    {
      this.mBuffer.append("\n");
    }
    this.mNeedIndent = true;
  }

  public String toString()
  {
    if (this.mMultiLineMode)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.mMultiBufferList != null)
      {
        Iterator localIterator = this.mMultiBufferList.iterator();
        while (localIterator.hasNext())
          localStringBuilder.append(localIterator.next());
      }
      localStringBuilder.append(this.mBuffer);
      return localStringBuilder.toString();
    }
    return this.mBuffer.toString();
  }

  public void startFunction(String paramString1, String paramString2)
  {
    indent();
    this.mBuffer.append("function ").append(paramString1).append('(').append(paramString2).append(')');
    if (!this.mStripWhitespace)
      eol();
    startBlock();
  }

  public void startThisFunction(String paramString1, String paramString2)
  {
    indent();
    this.mBuffer.append("this.").append(paramString1).append(" = function(").append(paramString2).append(')');
    if (!this.mStripWhitespace)
      eol();
    startBlock();
  }

  public void endFunction()
  {
    endBlock();
    eol();
  }

  public void endFunction(boolean paramBoolean)
  {
    endBlock();
    if (paramBoolean)
      eol();
  }

  public final JSWriter startStatement(String paramString)
  {
    this.mNeedSemicolon = true;
    this.mEndStatement = false;
    return append(paramString);
  }

  public final JSWriter startStatement(StringBuilder paramStringBuilder)
  {
    this.mNeedSemicolon = true;
    this.mEndStatement = false;
    return append(paramStringBuilder);
  }

  public final JSWriter continueStatement(String paramString)
  {
    this.mNeedSemicolon = true;
    return append(paramString);
  }

  public final JSWriter continueStatement(StringBuilder paramStringBuilder)
  {
    this.mNeedSemicolon = true;
    return append(paramStringBuilder);
  }

  public void endStatement()
  {
    if (this.mEndStatement)
      return;
    if (this.mNeedSemicolon)
    {
      this.mNeedSemicolon = false;
      appendln(";");
    }
    else if (!this.mStripWhitespace)
    {
      eol();
    }
    this.mEndStatement = true;
  }

  public void statement(String paramString)
  {
    if (this.mStatements != null)
    {
      this.mStatements.add(new Statement(paramString));
      this.mNumStatements += 1;
    }
    else
    {
      startStatement(paramString);
      endStatement();
    }
  }

  public void statement(StringBuilder paramStringBuilder)
  {
    if (this.mStatements != null)
    {
      this.mStatements.add(new Statement(paramStringBuilder.toString()));
      this.mNumStatements += 1;
    }
    else
    {
      startStatement(paramStringBuilder);
      endStatement();
    }
  }

  public final JSWriter property(long paramLong, String paramString)
  {
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramLong).append(":").append(escapeString(paramString));
    return this;
  }

  public final JSWriter property(String paramString, int paramInt)
  {
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString).append(":").append(paramInt);
    return this;
  }

  public final JSWriter property(String paramString, long paramLong)
  {
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString).append(":").append(paramLong);
    return this;
  }

  public final JSWriter property(String paramString, boolean paramBoolean)
  {
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString).append(":").append(paramBoolean ? "true" : "false");
    return this;
  }

  public final JSWriter property(String paramString, Number paramNumber)
  {
    assert (paramNumber != null);
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString).append(":").append(paramNumber);
    return this;
  }

  public final JSWriter property(String paramString1, String paramString2)
  {
    if (paramString2 == null)
      return property(paramString1);
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString1).append(":").append(escapeString(paramString2));
    return this;
  }

  public final JSWriter escapedProperty(String paramString1, String paramString2)
  {
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(escapeString(paramString1)).append(":").append(escapeString(paramString2));
    return this;
  }

  public final JSWriter escapedProperty(String paramString, int paramInt)
  {
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(escapeString(paramString)).append(":").append(paramInt);
    return this;
  }

  public final JSWriter property(String paramString, JSWriter paramJSWriter)
  {
    if (paramJSWriter == null)
      return property(paramString);
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString).append(":").append(paramJSWriter.mBuffer);
    return this;
  }

  public final JSWriter propertyDestinedForHTML(String paramString1, String paramString2)
  {
    assert (paramString2 != null);
    return property(paramString1, HTMLWriter.escape(paramString2));
  }

  public final JSWriter property(String paramString)
  {
    assert (paramString != null);
    if ((this.mBuffer.length() > 0) && (this.mBuffer.charAt(this.mBuffer.length() - 1) != '{'))
      this.mBuffer.append(",");
    this.mBuffer.append(paramString).append(":null");
    return this;
  }

  public static final String escape(String paramString)
  {
    return escape(paramString, false);
  }

  public static final String escape(String paramString, boolean paramBoolean)
  {
    assert (paramString != null);
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
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
        break;
      case '\'':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\'");
        i = j + 1;
        break;
      case '/':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("\\/");
        i = j + 1;
        break;
      case '<':
        if (paramBoolean)
        {
          localStringBuilder.append(arrayOfChar, i, j - i);
          localStringBuilder.append("\\x3c");
          i = j + 1;
        }
        break;
      case '>':
        if (paramBoolean)
        {
          localStringBuilder.append(arrayOfChar, i, j - i);
          localStringBuilder.append("\\x3e");
          i = j + 1;
        }
        break;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }

  public static final String genDynEmbedRsrcRef(String paramString1, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString, String paramString2, String paramString3, String paramString4)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramArrayOfString.length;
    for (int j = 0; j < i; j++)
    {
      if (j > 0)
        localStringBuilder.append(" ");
      localStringBuilder.append(paramArrayOfString[j]);
      String str = paramArrayOfString[(++j)];
      if (str != null)
        localStringBuilder.append("=\"" + str + "\"");
    }
    if (paramString3.indexOf("javascript") != -1)
    {
      paramString3 = paramString3.substring(0, paramString3.length() - 1);
      return "document.writeln(\"" + escape(new StringBuilder().append("<").append(paramString1).append(localStringBuilder.length() > 0 ? " " + localStringBuilder : "").append(" ").append(paramString2).append("=\"").append(paramString3).toString(), true) + "\"" + " + " + "ARMTGetBrowserVersionString()" + " + " + "\"" + escape("/8.1.00 201301251157/", true) + "\"" + " + " + "ARMTGetBrowserTypeString()" + " + " + "\"" + escape(new StringBuilder().append("/").append(paramString4).append("\"").append(paramBoolean1 ? "" : paramBoolean2 ? "/" : "").append(">").append(paramBoolean1 ? "</" + paramString1 + ">" : paramBoolean2 ? "" : "").toString(), true) + "\"" + ");";
    }
    return "document.writeln(\"" + escape(new StringBuilder().append("<").append(paramString1).append(localStringBuilder.length() > 0 ? " " + localStringBuilder : "").append(" ").append(paramString2).append("=\"").append(paramString3).append("8.1.00 201301251157").append("/").toString(), true) + "\"" + " + " + "ARMTGetBrowserTypeString()" + " + " + "\"" + escape(new StringBuilder().append("/").append(paramString4).append("\"").append(paramBoolean1 ? "" : paramBoolean2 ? "/" : "").append(">").append(paramBoolean1 ? "</" + paramString1 + ">" : paramBoolean2 ? "" : "").toString(), true) + "\"" + ");";
  }

  public static final String genDynEmbedRsrcString(String paramString1, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString, String paramString2, String paramString3, String paramString4)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramArrayOfString.length;
    for (int j = 0; j < i; j++)
    {
      if (j > 0)
        localStringBuilder.append(" ");
      localStringBuilder.append(paramArrayOfString[j]);
      String str = paramArrayOfString[(++j)];
      if (str != null)
        localStringBuilder.append("=\"" + str + "\"");
    }
    if (paramString3.indexOf("javascript") != -1)
    {
      paramString3 = paramString3.substring(0, paramString3.length() - 1);
      return escape(new StringBuilder().append("<").append(paramString1).append(localStringBuilder.length() > 0 ? " " + localStringBuilder : "").append(" ").append(paramString2).append("=\"").append(paramString3).toString(), true) + "\"" + " + " + "ARMTGetBrowserVersionString()" + " + " + "\"" + escape("/8.1.00 201301251157/", true) + "\"" + " + " + "ARMTGetBrowserTypeString()" + " + " + "\"" + escape(new StringBuilder().append("/").append(paramString4).append("\"").append(paramBoolean1 ? "" : paramBoolean2 ? "/" : "").append(">").append(paramBoolean1 ? "</" + paramString1 + ">" : paramBoolean2 ? "" : "").toString(), true);
    }
    return escape(new StringBuilder().append("<").append(paramString1).append(localStringBuilder.length() > 0 ? " " + localStringBuilder : "").append(" ").append(paramString2).append("=\"").append(paramString3).append("8.1.00 201301251157").append("/").toString(), true) + "\"" + " + " + "ARMTGetBrowserTypeString()" + " + " + "\"" + escape(new StringBuilder().append("/").append(paramString4).append("\"").append(paramBoolean1 ? "" : paramBoolean2 ? "/" : "").append(">").append(paramBoolean1 ? "</" + paramString1 + ">" : paramBoolean2 ? "" : "").toString(), true);
  }

  public final JSWriter openObj()
  {
    this.mBuffer.append("{");
    return this;
  }

  public final JSWriter closeObj()
  {
    this.mBuffer.append("}");
    return this;
  }

  public final JSWriter openList()
  {
    this.mBuffer.append("[");
    return this;
  }

  public final JSWriter listSep()
  {
    int i = this.mBuffer.charAt(this.mBuffer.length() - 1);
    if ((i != 91) && (i != 123))
      this.mBuffer.append(",");
    return this;
  }

  public final JSWriter closeList()
  {
    this.mBuffer.append("]");
    return this;
  }

  public final JSWriter startBlock()
  {
    if ((!this.mNeedIndent) && (!this.mStripWhitespace))
      this.mBuffer.append(' ');
    appendln("{");
    increaseIndent();
    this.mEndStatement = false;
    return this;
  }

  public final JSWriter endBlock()
  {
    decreaseIndent();
    append("}");
    this.mNeedSemicolon = false;
    this.mEndStatement = false;
    return this;
  }

  public void comment(String paramString)
  {
    if (!this.mNoComments)
      if (this.mStatements != null)
      {
        this.mStatements.add(paramString);
        this.mComments += 1;
      }
      else
      {
        appendln("/* " + paramString + " */");
      }
  }

  public JSWriter append(String paramString)
  {
    if (this.mEatCharacters == 0)
    {
      indent();
      this.mBuffer.append(paramString);
    }
    return this;
  }

  public JSWriter append(StringBuilder paramStringBuilder)
  {
    if (this.mEatCharacters == 0)
    {
      indent();
      this.mBuffer.append(paramStringBuilder);
    }
    return this;
  }

  public JSWriter appendln(String paramString)
  {
    if (this.mEatCharacters == 0)
    {
      indent();
      this.mBuffer.append(paramString);
      if (!this.mStripWhitespace)
        eol();
    }
    return this;
  }

  public final JSWriter append(JSWriter paramJSWriter)
  {
    if (this.mEatCharacters == 0)
    {
      Object localObject;
      if (paramJSWriter.mMultiLineMode)
      {
        if (paramJSWriter.mMultiBufferList != null)
        {
          localObject = paramJSWriter.mMultiBufferList.iterator();
          while (((Iterator)localObject).hasNext())
            appendln((String)((Iterator)localObject).next());
        }
        appendln(paramJSWriter.mBuffer.toString());
      }
      else
      {
        localObject = paramJSWriter.toString().split("\n");
        for (int i = 0; i < localObject.length; i++)
          appendln(localObject[i]);
        MLog.fine("The JSWRiter parameter is not set to mutiline mode. Performance might get affected due to this.");
      }
    }
    return this;
  }

  public final JSWriter append(int paramInt)
  {
    return append(Integer.toString(paramInt));
  }

  public final JSWriter append(long paramLong)
  {
    return append(Long.toString(paramLong));
  }

  public final JSWriter append(boolean paramBoolean)
  {
    return append(paramBoolean ? "1" : "0");
  }

  public final JSWriter comma()
  {
    append(",");
    if (!this.mStripWhitespace)
      append(" ");
    return this;
  }

  public final JSWriter appendqs(String paramString)
  {
    assert (paramString != null);
    return append(escapeString(paramString));
  }

  public final JSWriter appendqs(String paramString, boolean paramBoolean)
  {
    assert (paramString != null);
    return append("\"").append(escape(paramString, paramBoolean)).append("\"");
  }

  public final JSWriter appends(String paramString)
  {
    assert (paramString != null);
    paramString = paramString.replaceAll("\"", "\\\\\"");
    return append(paramString);
  }

  public static String makeJSFunctionName(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString1);
    localStringBuilder.append(paramString2);
    localStringBuilder.append(paramString3);
    for (int i = 0; i < localStringBuilder.length(); i++)
    {
      int j = localStringBuilder.charAt(i);
      if (((j < 65) || (j > 90)) && ((j < 97) || (j > 122)) && ((j < 48) || (j > 57)))
        localStringBuilder.replace(i, i + 1, "_" + j);
    }
    return localStringBuilder.toString();
  }

  public static String _urlencode(String paramString)
  {
    String str1 = "0123456789ABCDEF";
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      String str2 = paramString.substring(i, i + 1);
      int j = str2.charAt(0);
      if (j > 255)
      {
        localStringBuilder.append(str1.charAt((j & 0xF000) >> 12));
        localStringBuilder.append(str1.charAt((j & 0xF00) >> 8));
        localStringBuilder.append(str1.charAt((j & 0xF0) >> 4));
        localStringBuilder.append(str1.charAt(j & 0xF));
      }
      else
      {
        localStringBuilder.append(str1.charAt(j >> 4 & 0xF));
        localStringBuilder.append(str1.charAt(j & 0xF));
      }
    }
    return localStringBuilder.toString();
  }

  public void startInterruptibleBlock()
  {
    assert (this.mStatements == null);
    this.mStatements = new ArrayList();
    this.mInterruptible = false;
    this.mComments = 0;
  }

  public void callFunction(boolean paramBoolean, String paramString, Object[] paramArrayOfObject)
  {
    FunctionCall localFunctionCall = new FunctionCall(paramBoolean, null, paramString, paramArrayOfObject);
    this.mStatements.add(localFunctionCall);
    this.mInterruptible |= localFunctionCall.mInterruptible;
  }

  public void callFunction(boolean paramBoolean, String paramString1, String paramString2, Object[] paramArrayOfObject)
  {
    FunctionCall localFunctionCall = new FunctionCall(paramBoolean, paramString1, paramString2, paramArrayOfObject);
    this.mStatements.add(localFunctionCall);
    this.mInterruptible |= localFunctionCall.mInterruptible;
  }

  public final void endInterruptibleBlock()
    throws GoatException
  {
    JSWriter localJSWriter = new JSWriter(true);
    int i = this.mStatements.size();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      Object localObject = this.mStatements.get(k);
      if ((localObject instanceof String))
      {
        localJSWriter.comment((String)localObject);
        this.mComments -= 1;
      }
      else if ((localObject instanceof Statement))
      {
        localJSWriter.statement(((Statement)localObject).mStatement);
        this.mNumStatements -= 1;
      }
      else
      {
        FunctionCall localFunctionCall = (FunctionCall)localObject;
        int m;
        if ((localFunctionCall.mInterruptible) && ((k < i - 1 - this.mComments - this.mNumStatements) || (mWorkflowVerifyImode)))
        {
          localJSWriter.startStatement("return {c:{");
          if (localFunctionCall.mThis != null)
            localJSWriter.continueStatement("t:").append(localFunctionCall.mThis).append(",").append("f:").append(localFunctionCall.mThis).append(".").append(localFunctionCall.mFunction);
          else
            localJSWriter.continueStatement("f:").append(localFunctionCall.mFunction);
          if ((localFunctionCall.mArgs != null) && (localFunctionCall.mArgs.length > 0))
          {
            localJSWriter.continueStatement(", a:[");
            for (m = 0; m < localFunctionCall.mArgs.length; m++)
            {
              if (m != 0)
                localJSWriter.comma();
              assert (localFunctionCall.mArgs[m] != null);
              localJSWriter.continueStatement(localFunctionCall.mArgs[m].toString());
            }
            localJSWriter.continueStatement("]");
          }
          localJSWriter.continueStatement("}");
          if (k < i - 1 - this.mComments - this.mNumStatements)
          {
            localJSWriter.continueStatement(", n:");
            localJSWriter.startBlock();
            localJSWriter.continueStatement("f:");
            localJSWriter.startFunction("", "");
            j++;
          }
          else
          {
            localJSWriter.continueStatement("};");
          }
        }
        else
        {
          localJSWriter.startStatement("");
          if (localFunctionCall.mInterruptible)
            localJSWriter.continueStatement("return ");
          if (localFunctionCall.mThis != null)
            localJSWriter.continueStatement(localFunctionCall.mThis).append(".");
          localJSWriter.continueStatement(localFunctionCall.mFunction).append("(");
          if (localFunctionCall.mArgs != null)
            for (m = 0; m < localFunctionCall.mArgs.length; m++)
            {
              if (m != 0)
                localJSWriter.comma();
              assert (localFunctionCall.mArgs[m] != null);
              localJSWriter.continueStatement(localFunctionCall.mArgs[m].toString());
            }
          localJSWriter.continueStatement(")");
          localJSWriter.endStatement();
        }
      }
    }
    assert (this.mComments == 0);
    while (j-- > 0)
    {
      localJSWriter.endFunction();
      localJSWriter.endBlock();
      localJSWriter.continueStatement("}");
      localJSWriter.endStatement();
    }
    append(localJSWriter);
    this.mStatements = null;
  }

  protected static class Statement
  {
    protected String mStatement;

    protected Statement(String paramString)
    {
      this.mStatement = paramString;
    }
  }

  protected static class FunctionCall
  {
    protected boolean mInterruptible;
    protected String mThis;
    protected String mFunction;
    protected Object[] mArgs;

    protected FunctionCall(boolean paramBoolean, String paramString1, String paramString2, Object[] paramArrayOfObject)
    {
      this.mInterruptible = paramBoolean;
      this.mThis = paramString1;
      this.mFunction = paramString2;
      this.mArgs = paramArrayOfObject;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.JSWriter
 * JD-Core Version:    0.6.1
 */