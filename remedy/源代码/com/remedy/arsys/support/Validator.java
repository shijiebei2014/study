package com.remedy.arsys.support;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator
{
  private static final String CR = "%0[dD]";
  private static final String LF = "%0[aA]";

  public static String sanitizeCRandLF(String paramString)
  {
    if (paramString == null)
      return null;
    return paramString.replaceAll("%0[dD]", "").replaceAll("%0[aA]", "").replaceAll("\r", "").replaceAll("\n", "");
  }

  public static boolean isPathDepthLessOrEqual(String paramString, int paramInt)
  {
    boolean bool = true;
    if ((paramString == null) || (paramString.trim().equals("")))
      return bool;
    String[] arrayOfString = paramString.split("/");
    bool = arrayOfString.length <= paramInt;
    return bool;
  }

  public static boolean isStringLengthValid(String paramString, int paramInt)
  {
    boolean bool = true;
    if ((paramString == null) || (paramString.trim().equals("")))
      return bool;
    bool = paramString.trim().length() <= paramInt;
    return bool;
  }

  public static final String escape(String paramString)
  {
    if (paramString == null)
      return null;
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
      case '%':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&#37;");
        i = j + 1;
        break;
      case ';':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&#59;");
        i = j + 1;
        break;
      case '(':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&#40;");
        i = j + 1;
        break;
      case ')':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&#41;");
        i = j + 1;
        break;
      case '+':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&#43;");
        i = j + 1;
      case '#':
      case '$':
      case '\'':
      case '*':
      case ',':
      case '-':
      case '.':
      case '/':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case ':':
      case '=':
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }

  public static final String URLParamsEscape(String paramString)
  {
    if (paramString == null)
      return null;
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
      switch (arrayOfChar[j])
      {
      case '<':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&lt;");
        i = j + 1;
        break;
      case '>':
        localStringBuilder.append(arrayOfChar, i, j - i);
        localStringBuilder.append("&gt;");
        i = j + 1;
      }
    localStringBuilder.append(arrayOfChar, i, arrayOfChar.length - i);
    return localStringBuilder.toString();
  }

  public static final String StripOffScriptTag(String paramString)
  {
    if (paramString == null)
      return null;
    Pattern localPattern = Pattern.compile("(?i)<script>|(?i)</script>|(?i)%3Cscript%3E|(?i)%2Fscript%3E|(?i)<iframe|(?i)%3Ciframe|(?i)<IMG|(?i)<img|(?i)alert\\(|(?i)alert%28|prompt|onload");
    for (Matcher localMatcher = localPattern.matcher(paramString); localMatcher.find(); localMatcher = localPattern.matcher(paramString))
      paramString = localMatcher.replaceAll("");
    return paramString;
  }

  public static final boolean URLParamHasXSSTag(Map<String, String[]> paramMap)
  {
    boolean bool = false;
    if (paramMap == null)
      return bool;
    Set localSet = paramMap.keySet();
    String str1 = localSet.toString();
    String[] arrayOfString1 = str1.substring(str1.indexOf("[") + 1, str1.indexOf("]")).split("\\,");
    for (int i = 0; i <= arrayOfString1.length - 1; i++)
    {
      String str2 = arrayOfString1[i].trim();
      String str3 = str2.toLowerCase();
      if (str3 != null)
      {
        bool = (str3.indexOf("%3C%2Fscript".toLowerCase()) != -1) || (str3.indexOf("</script") != -1) || (str3.indexOf("<script") != -1) || (str3.indexOf("%3Cscript".toLowerCase()) != -1) || (str3.indexOf("<iframe>") != -1) || (str3.indexOf("<frame>") != -1) || (str3.indexOf("<img>") != -1) || (str3.indexOf("alert(") != -1) || (str3.indexOf("javascript:") != -1) || (str3.indexOf("alert%28") != -1);
        if (bool)
          return bool;
      }
      String[] arrayOfString2 = (String[])paramMap.get(str2);
      if (arrayOfString2 != null)
      {
        String str4 = arrayOfString2[0].toLowerCase();
        bool = (str4.indexOf("%3C%2Fscript".toLowerCase()) != -1) || (str4.indexOf("</script") != -1) || (str4.indexOf("<script") != -1) || (str4.indexOf("%3Cscript".toLowerCase()) != -1) || (str4.indexOf("<iframe>") != -1) || (str3.indexOf("<frame>") != -1) || (str3.indexOf("<img>") != -1) || (str4.indexOf("alert(") != -1) || (str4.indexOf("javascript:") != -1) || (str4.indexOf("alert%28") != -1);
        if (bool)
          return bool;
      }
    }
    return bool;
  }

  public static final boolean URLParamHasXSSTag_String(Map<String, String> paramMap)
  {
    boolean bool = false;
    if (paramMap == null)
      return bool;
    Set localSet = paramMap.keySet();
    String str1 = localSet.toString();
    String[] arrayOfString = str1.substring(str1.indexOf("[") + 1, str1.indexOf("]")).split("\\,");
    for (int i = 0; i <= arrayOfString.length - 1; i++)
    {
      String str2 = arrayOfString[i].trim();
      String str3 = str2.toLowerCase();
      if (str3 != null)
      {
        bool = (str3.indexOf("%3C%2Fscript".toLowerCase()) != -1) || (str3.indexOf("</script") != -1) || (str3.indexOf("<script") != -1) || (str3.indexOf("%3Cscript".toLowerCase()) != -1) || (str3.indexOf("<iframe>") != -1) || (str3.indexOf("<frame>") != -1) || (str3.indexOf("<img>") != -1) || (str3.indexOf("alert(") != -1) || (str3.indexOf("javascript:") != -1) || (str3.indexOf("alert%28") != -1);
        if (bool)
          return bool;
      }
      String str4 = (String)paramMap.get(str2);
      if (str4 != null)
      {
        String str5 = str4.toLowerCase();
        bool = (str5.indexOf("%3C%2Fscript".toLowerCase()) != -1) || (str5.indexOf("</script") != -1) || (str5.indexOf("<script") != -1) || (str5.indexOf("%3Cscript".toLowerCase()) != -1) || (str5.indexOf("<iframe>") != -1) || (str3.indexOf("<frame>") != -1) || (str3.indexOf("<img>") != -1) || (str5.indexOf("alert(") != -1) || (str5.indexOf("javascript:") != -1) || (str5.indexOf("alert%28") != -1);
        if (bool)
          return bool;
      }
    }
    return bool;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.Validator
 * JD-Core Version:    0.6.1
 */