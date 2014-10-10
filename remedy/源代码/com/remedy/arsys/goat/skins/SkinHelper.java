package com.remedy.arsys.goat.skins;

import com.bmc.arsys.api.Value;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkinHelper
{
  public static String replaceSkinProperties(String paramString1, String paramString2, SkinDefinitionMap paramSkinDefinitionMap, short paramShort)
  {
    Pattern localPattern = Pattern.compile("(SKIN_PROPERTY\\(\\s*([^,]+),\\s*([^\\)]+)\\))");
    Matcher localMatcher = localPattern.matcher(paramString1);
    StringBuffer localStringBuffer = new StringBuffer();
    String str1 = "";
    String str2 = "";
    String str3 = "";
    Value localValue = null;
    while (localMatcher.find())
    {
      str2 = localMatcher.group(2).trim();
      str3 = localMatcher.group(3);
      if ((paramSkinDefinitionMap != null) && (str2.length() > 0))
      {
        localValue = paramSkinDefinitionMap.getProperty(paramString2, str2, paramShort);
        if (localValue != null)
          str3 = localValue.toString();
      }
      localMatcher.appendReplacement(localStringBuffer, str3.trim());
    }
    localMatcher.appendTail(localStringBuffer);
    return localStringBuffer.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.skins.SkinHelper
 * JD-Core Version:    0.6.1
 */