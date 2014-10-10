package com.remedy.arsys.goat;

import java.io.Serializable;

public class DateTimeParser
  implements Serializable
{
  private static final long serialVersionUID = -1027299676780888781L;
  public static final String patternChars = "GyMdkHmsSEDFwWahKzYeugAZ";
  public static final String dateSeparators = "/-.";
  public static final int REGEXPPATTERN = 0;
  public static final int NORMALIZEDPATTERN = 1;

  public static String[] getParsePattern(String paramString)
  {
    int i = 0;
    String[] arrayOfString = new String[2];
    StringBuilder localStringBuilder1 = new StringBuilder(30);
    StringBuilder localStringBuilder2 = new StringBuilder(10);
    int j = 0;
    int k = 0;
    for (int m = 0; m < paramString.length(); m++)
    {
      char c = paramString.charAt(m);
      String str;
      if ((k == 0) && ("GyMdkHmsSEDFwWahKzYeugAZ".indexOf(c) != -1))
      {
        i = 1;
        while ((m + 1 < paramString.length()) && (paramString.charAt(m + 1) == c))
        {
          i++;
          m++;
        }
        str = getRegExpforChar(c, i);
        localStringBuilder1.append(str);
        localStringBuilder2.append(getNormalizedPatternforChar(c, i));
        if ((c == 'h') || (c == 'm') || (c == 'H') || (c == 'k'))
          j = 1;
        else
          j = 0;
      }
      else if ((k == 0) && ("/-.".indexOf(c) != -1) && (j == 0))
      {
        str = "[/\\\\.-]";
        localStringBuilder1.append(str);
      }
      else if (c == '.')
      {
        if (j != 0)
          localStringBuilder1.append("[:\\\\.]");
        else
          localStringBuilder1.append("\\\\.");
      }
      else if (c == '\'')
      {
        if ((m + 1 < paramString.length()) && (paramString.charAt(m + 1) == '\''))
        {
          localStringBuilder1.append('\'');
          m++;
        }
        else
        {
          k = k == 0 ? 1 : 0;
        }
      }
      else if (c == '"')
      {
        localStringBuilder1.append("\\\"");
      }
      else if ((c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '*') || (c == '+'))
      {
        localStringBuilder1.append("\\\\\\").append(c);
      }
      else
      {
        localStringBuilder1.append(c);
      }
    }
    arrayOfString[0] = localStringBuilder1.toString();
    arrayOfString[1] = localStringBuilder2.toString();
    return arrayOfString;
  }

  private static String getRegExpforChar(char paramChar, int paramInt)
  {
    String str = "";
    switch (paramChar)
    {
    case 'y':
      str = "(\\\\d{4}|\\\\d{3}|\\\\d{2}|\\\\d{1})([^\\\\d]*)";
      break;
    case 'M':
      if (paramInt < 3)
        str = "(\\\\d{2}|\\\\d{1})";
      else
        str = "([^\\\\d\\\\s]+)";
      break;
    case 'd':
      str = "(\\\\d{2}|\\\\d{1})";
      break;
    case 'E':
    case 'G':
    case 'a':
      str = "([^\\\\d\\\\s]+)";
      break;
    case 'z':
      str = "([a-zA-Z]{3}|gmt[+-]\\\\d{2}:\\\\d{2})";
      break;
    case 'H':
    case 'h':
    case 'm':
    case 's':
      str = "(\\\\d{2}|\\\\d{1})";
      break;
    default:
      str = paramChar + "";
    }
    return str;
  }

  private static char getNormalizedPatternforChar(char paramChar, int paramInt)
  {
    char c = ' ';
    switch (paramChar)
    {
    case 'y':
      c = 'y';
      break;
    case 'M':
      if (paramInt < 3)
        c = 'm';
      else
        c = 'M';
      break;
    case 'd':
      c = 'd';
      break;
    case 'E':
      c = 'E';
      break;
    case 'a':
      c = 'a';
      break;
    case 'z':
      c = 'z';
      break;
    case 'G':
      c = 'G';
      break;
    case 'H':
    case 'h':
      c = 'h';
      break;
    case 'm':
      c = 'u';
      break;
    case 's':
      c = 's';
      break;
    default:
      c = paramChar;
    }
    return c;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.DateTimeParser
 * JD-Core Version:    0.6.1
 */