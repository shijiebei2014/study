package com.remedy.arsys.goat;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.Configuration.ServerInformation;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Keyword extends KeywordBase
  implements TargetAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -3250177110847847436L;
  private static Map mKeywordHash;
  private int mIndex;
  private int mFieldID;
  private String mDefaultsObj;
  private int mDefaultFieldID;
  private static String[] MVUITypes = { "NONE", "WINDOWS", "WEB", "WEB(FIXED)", "WIRELESS", "WEB(AUTO)", "WEB(AUTO,FIXED)" };

  private Keyword(int paramInt)
  {
    this.mIndex = paramInt;
    this.mFieldID = 0;
  }

  public static Keyword getKeyword(String paramString)
  {
    Object localObject = mKeywordHash.get(paramString);
    if (localObject == null)
      return null;
    return (Keyword)localObject;
  }

  public static Operand getKeywordOperand(String paramString)
    throws GoatException
  {
    Keyword localKeyword1 = getKeyword(paramString);
    if (localKeyword1 != null)
    {
      Keyword localKeyword2 = new Keyword(localKeyword1.mIndex);
      localKeyword2.mFieldID = localKeyword1.mFieldID;
      return new Operand(localKeyword1.getType(), 14, localKeyword2);
    }
    throw new GoatException("Unrecognised keyword '" + paramString + "'");
  }

  public int getIndex()
  {
    return this.mIndex;
  }

  public String toString()
  {
    return "$" + MKeywordNames[this.mIndex] + "$";
  }

  private int getType()
  {
    return MKeywordTypes[this.mIndex];
  }

  public int getAsFieldID()
  {
    assert (this.mIndex != 0);
    return -this.mIndex + 1;
  }

  public boolean isTimeRelated()
  {
    return (MKeywordFlags[this.mIndex] & 0x10) != 0;
  }

  public void bindToTarget(int paramInt1, int paramInt2)
    throws GoatException
  {
    this.mFieldID = paramInt2;
  }

  public void emitJS(Emitter paramEmitter)
  {
    JSWriter localJSWriter = paramEmitter.js();
    if ((this.mIndex == 1) && (this.mDefaultFieldID != 0))
    {
      localJSWriter.append(this.mDefaultsObj + "[" + this.mDefaultFieldID + "]");
    }
    else
    {
      localJSWriter.append("ARKWGetByIdx(").append("windowID").comma().append(this.mIndex);
      if ((MKeywordFlags[this.mIndex] & 0x40) != 0)
        localJSWriter.append(", ").append(this.mFieldID);
      localJSWriter.append(")");
    }
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }

  public static String convertStringToNumericFormat(String paramString)
  {
    int i = paramString.indexOf("$");
    if (i < 0)
      return paramString;
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    while (i >= 0)
    {
      int j = localStringBuilder.indexOf("$", i + 1);
      if (j < 0)
        break;
      Keyword localKeyword = getKeyword(localStringBuilder.substring(i, j + 1));
      if (localKeyword != null)
      {
        String str = "-" + localKeyword.getIndex();
        localStringBuilder.replace(i + 1, j, str);
        j = i + str.length() + 2;
      }
      i = localStringBuilder.indexOf("$", j);
    }
    return localStringBuilder.toString();
  }

  public static void emitKeywords(JSWriter paramJSWriter, Form paramForm, Form.ViewInfo paramViewInfo, String paramString)
    throws GoatException
  {
    String str1 = paramForm.getName().toString();
    String str2 = paramViewInfo.getLabel();
    int i = paramViewInfo.getType();
    if ((i < 0) || (i > MVUITypes.length))
      i = 0;
    String str3 = FormContext.get().getApplication();
    String str4 = paramForm.getServerName();
    ServerInfo localServerInfo = ServerInfo.get(str4);
    int j = ServerLogin.getServerInformation(str4).getPort();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(localServerInfo.getDBType()).append(" ").append(localServerInfo.getDBVersion());
    paramJSWriter.startStatement("this.ARKeywords=");
    paramJSWriter.openList();
    paramJSWriter.append(7).listSep().append(JSWriter.escapeString(str4)).listSep();
    paramJSWriter.append(6).listSep().append(JSWriter.escapeString(str1)).listSep();
    paramJSWriter.append(17).listSep().append(JSWriter.escapeString(str2)).listSep();
    paramJSWriter.append(21).listSep().append(str3 == null ? "null" : JSWriter.escapeString(str3)).listSep();
    paramJSWriter.append(28).listSep().append(JSWriter.escapeString(MVUITypes[i])).listSep();
    paramJSWriter.append(23).listSep().append(9).listSep();
    paramJSWriter.append(16).listSep().append(JSWriter.escapeString(Configuration.getInstance().getClientVersion())).listSep();
    paramJSWriter.append(29).listSep().append(JSWriter.escape(Integer.toString(j))).listSep();
    paramJSWriter.append(13).listSep().append(JSWriter.escapeString(localStringBuilder.toString())).listSep();
    paramJSWriter.append(30).listSep().append(JSWriter.escapeString(paramString)).listSep();
    paramJSWriter.append(24).listSep().append("this.ARTitles.singleLong").listSep();
    paramJSWriter.append(22).listSep().append(JSWriter.escapeString(SessionData.get().getLocale())).listSep();
    paramJSWriter.append(36).listSep().append(0);
    paramJSWriter.closeList();
    paramJSWriter.endStatement();
  }

  public void bindDefaultKeywordToField(int paramInt, String paramString)
  {
    if (this.mIndex == 1)
    {
      this.mDefaultFieldID = paramInt;
      this.mDefaultsObj = paramString;
    }
  }

  public void collectDefaultKeywordFieldIds(Set paramSet)
  {
    if (this.mDefaultFieldID != 0)
      paramSet.add(Integer.valueOf(this.mDefaultFieldID));
  }

  static
  {
    mKeywordHash = new HashMap();
    for (int i = 0; i < MKeywordNames.length; i++)
      mKeywordHash.put("$" + MKeywordNames[i] + "$", new Keyword(i));
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.Keyword
 * JD-Core Version:    0.6.1
 */